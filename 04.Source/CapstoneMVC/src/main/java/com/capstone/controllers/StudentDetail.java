package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.enums.SubjectTypeEnum;
import com.capstone.models.*;
import com.capstone.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.*;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Controller
public class StudentDetail {

    IStudentService studentService = new StudentServiceImpl();

    private List<RealSemesterEntity> sortedSemester;

    public StudentDetail() {
        IRealSemesterService service = new RealSemesterServiceImpl();
        List<RealSemesterEntity> list = service.getAllSemester();
        sortedSemester = Ultilities.SortSemesters(list);
    }

    @RequestMapping("/studentDetail")
    public ModelAndView Index(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to /studentDetail");

        ModelAndView view = new ModelAndView("StudentDetail");
        view.addObject("title", "Thông tin chi tiết sinh viên");
        IRealSemesterService service = new RealSemesterServiceImpl();
//        List<RealSemesterEntity> list = service.getAllSemester();
//        sortedSemester = Ultilities.SortSemesters(list);
//        view.addObject("semesters", sortedSemester);
        return view;
    }

    @RequestMapping("/getStudentList")
    @ResponseBody
    public JsonObject GetStudentList(@RequestParam String searchValue) {
        JsonObject jsonObj = new JsonObject();
        searchValue = searchValue == null ? "" : searchValue.trim();

        try {
            List<StudentEntity> students;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUser principal = (CustomUser) authentication.getPrincipal();
            if (principal.getUser().getRole().contains("ADMIN") || principal.getUser().getRole().contains("STAFF") || principal.getUser().getRole().contains("MANAGER")) {
                students = studentService.findStudentsByFullNameOrRollNumber(searchValue);
            } else {
                students = new ArrayList<>();
                students.add(studentService.findStudentByRollNumber(principal.getUser().getStudentRollNumber()));
            }

//            final String finalSearchValue = searchValue;
//            List<StudentEntity> studentList = students.stream()
//                    .filter(c -> Ultilities.containsIgnoreCase(c.getRollNumber(), finalSearchValue)
//                            || Ultilities.containsIgnoreCase(c.getFullName(), finalSearchValue))
//                    .collect(Collectors.toList());

//            List<StudentEntity> studentList = students.stream()
//                    .filter(c -> c.getRollNumber().toLowerCase().contains(finalSearchValue.toLowerCase())
//                            || c.getFullName().toLowerCase().contains(finalSearchValue.toLowerCase()))
//                    .collect(Collectors.toList());

            List<SelectItem> itemList = new ArrayList<>();
            for (StudentEntity student : students) {
                SelectItem item = new SelectItem();
                item.setValue(student.getId() + "");
                item.setText(student.getRollNumber() + " - " + student.getFullName());

                itemList.add(item);
            }

            JsonArray result = (JsonArray) new Gson().toJsonTree(itemList);

            jsonObj.add("items", result);
            jsonObj.addProperty("success", true);
        } catch (Exception e) {
            Logger.writeLog(e);
            jsonObj.add("items", (JsonArray) new Gson().toJsonTree(new ArrayList<SelectItem>()));
            jsonObj.addProperty("success", false);
        }


        return jsonObj;
    }

    @RequestMapping("/getStudentDetail")
    @ResponseBody
    public JsonObject GetStudentFail(@RequestParam Map<String, String> params) {
        JsonObject data = new JsonObject();

        int studentId = Integer.parseInt(params.get("stuId"));
        String semester = Global.getTemporarySemester().getSemester();

        try {
            List<List<String>> set2 = processFailed2(studentId, semester);

            List<List<String>> resultList = set2.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());

            JsonArray result = (JsonArray) new Gson().toJsonTree(resultList);

            data.addProperty("iTotalRecords", set2.size());
            data.addProperty("iTotalDisplayRecords", set2.size());
            data.add("aaData", result);
            data.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public List<List<String>> processFailed(int studentId, String semester) {
//        int ss = 0;
//        List<String> t = new ArrayList<>();
//        for (RealSemesterEntity s : sortedSemester) {
//            t.add(s.getSemester());
//            if (s.getSemester().equals(semester)) {
//                ss = s.getId();
//                break;
//            }
//        }

        IMarksService marksService = new MarksServiceImpl();

//        List<MarksEntity> list = marksService.getAllMarksByStudentAndSubject(studentId, "0", String.valueOf(ss));
        List<MarksEntity> list = marksService.getStudentMarksById(studentId);
        list = Global.TransformMarksList(list);

        List<MarksEntity> newlist = Ultilities.FilterStudentsOnlyPassAndFail(list);
        List<MarksEntity> resultList = Ultilities.FilterListFailStudent(newlist);

        // subjects in student curriculum
        StudentEntity stu = studentService.findStudentById(studentId);
        List<String> l = Ultilities.getStudentCurriculumSubjects(stu);

//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
//        EntityManager em = emf.createEntityManager();

        //remove studying marks from fail list
//        List<MarksEntity> studyingList = marksService.getMarksByStudentIdAndStatusAndSemester(studentId, "studying", t);
        List<MarksEntity> studyingList = list.stream().filter(c -> c.getStatus().toLowerCase().contains("studying")).collect(Collectors.toList());
        Iterator<MarksEntity> iterator = resultList.iterator();
        while (iterator.hasNext()) {
            MarksEntity current = iterator.next();
            if (studyingList.stream().anyMatch(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(current.getSubjectMarkComponentId().getSubjectId().getId()))) {
                iterator.remove();
            } else {
                SubjectEntity su = current.getSubjectMarkComponentId().getSubjectId();
                if (!l.stream().anyMatch(a -> a.equals(su.getId()))) {
                    iterator.remove();
                } else {
                    List<SubjectEntity> replacers = su.getSubjectEntityList();
                    boolean deleted = false;
                    for (SubjectEntity s : replacers) {
                        List<MarksEntity> rep = list
                                .stream()
                                .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s.getId()))
                                .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
                        if (!rep.isEmpty()) {
                            iterator.remove();
                            deleted = true;
                            break;
                        }
                    }
                    if (!deleted) {
                        List<SubjectEntity> replacers2 = su.getSubjectEntityList1();
                        for (SubjectEntity s : replacers2) {
                            List<MarksEntity> rep = list
                                    .stream()
                                    .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s.getId()))
                                    .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
                            if (!rep.isEmpty()) {
                                iterator.remove();
                                deleted = true;
                                break;
                            }
                        }
                        if (!deleted) {
                            for (SubjectEntity s : replacers2) {
                                for (SubjectEntity ss : s.getSubjectEntityList()) {
                                    List<MarksEntity> rep = list
                                            .stream()
                                            .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(ss.getId()))
                                            .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
                                    if (!rep.isEmpty()) {
                                        iterator.remove();
                                        deleted = true;
                                        break;
                                    }
                                }
                                if (deleted) break;
                            }
                        }
                    }
                }
            }
        }

        List<List<String>> parent = new ArrayList<>();
        if (!resultList.isEmpty()) {
            resultList.forEach(m -> {
                ArrayList<String> tmp = new ArrayList<>();
                tmp.add(m.getSubjectMarkComponentId() == null ? "N/A" : m.getSubjectMarkComponentId().getSubjectId().getId());
                tmp.add(m.getCourseId() == null ? "N/A" : m.getCourseId().getSemester());
                tmp.add(m.getSemesterId() == null ? "N/A" : m.getSemesterId().getSemester());
                tmp.add(String.valueOf(m.getAverageMark()));
                tmp.add("FAILED");
                parent.add(tmp);
            });
        }

        return parent;
    }

    //Not done
    public List<List<String>> processFailed2(int studentId, String semester) {

        IMarksService marksService = new MarksServiceImpl();

//        List<MarksEntity> list = marksService.getAllMarksByStudentAndSubject(studentId, "0", String.valueOf(ss));
        List<MarksEntity> list = marksService.getStudentMarksById(studentId);
        list = Ultilities.SortSemestersByMarks(list);

        List<MarksEntity> failList = list.stream()
                .filter(q -> q.getStatus().equalsIgnoreCase(Enums.MarkStatus.FAIL.getValue()))
                .collect(Collectors.toList());


        //môn bị fail
        List<MarksEntity> resultList = new ArrayList<>();
        //môn thay thế bị fail, Map<Môn bị thay thế, Môn thay thế>
        Map<String, MarksEntity> replacementList = new HashMap<>();

        myFailList:
        for (MarksEntity currentMark : failList) {


            boolean isFailed = false;
            boolean replacementFailed = false;
            MarksEntity failMark = null;
            MarksEntity replacementFailMark = null;
            SubjectEntity subject = currentMark.getSubjectMarkComponentId().getSubjectId();

            //check xem môn đã được xét chưa, nếu đã tồn tại thì duyệt item tiếp theo
            MarksEntity exist = resultList.stream().filter(q -> q.getSubjectMarkComponentId().getSubjectId().getId()
                    .equalsIgnoreCase(subject.getId())).findFirst().orElse(null);
            if (exist != null) {
                continue myFailList;
            } else {
                //lấy môn bị thay thế, ex: B thay thế A -> lấy A
                List<SubjectEntity> tempReplacementSubjList = subject.getSubjectEntityList1();
                for (SubjectEntity subj : tempReplacementSubjList) {
                    boolean checkExist = replacementList.containsKey(subj);
                    if (checkExist) {
                        continue myFailList;
                    }
                }
            }


            //lấy điểm mới nhất của môn được xét
            List<MarksEntity> subjectMarks = list.stream()
                    .filter(q -> q.getSubjectMarkComponentId().getSubjectId().getId().equalsIgnoreCase(subject.getId()))
                    .collect(Collectors.toList());
            if (!subjectMarks.isEmpty()) {

                List<MarksEntity> sortedList = Ultilities.SortSemestersByMarks(subjectMarks);
                MarksEntity latestMark = null;
                latestMark = sortedList.get(sortedList.size() - 1);

                //dùng làm tham số để check xem môn có fail hay ko
                LatestFailMark failModel = new LatestFailMark();
                Ultilities.isLatestMarkFailOrNot(latestMark, sortedList, failModel);

                isFailed = failModel.isFailed();
                failMark = failModel.getLatestFailedMark();

            }


            //check xem điểm môn thay thế có tồn tại không
            List<SubjectEntity> replacementSubject = Ultilities.findBackAndForwardReplacementSubject(subject);
            //loại subject chính ra khỏi mảng môn thay thế
            List<SubjectEntity> replacementSubject2 = new ArrayList<>(replacementSubject);
            for (SubjectEntity subj :
                    replacementSubject2) {
                if (subj.getId().equalsIgnoreCase(subject.getId())) {
                    int index = replacementSubject2.indexOf(subj);
                    replacementSubject.remove(index);
                }
            }
            List<MarksEntity> replacementSubjectMarks = list.stream()
                    .filter(q -> replacementSubject.stream()
                            .anyMatch(a -> a.getId()
                                    .equalsIgnoreCase(q.getSubjectMarkComponentId().getSubjectId().getId()))
                    )
                    .collect(Collectors.toList());
            if (!replacementSubjectMarks.isEmpty()) {
                List<MarksEntity> sortedList = Ultilities.SortSemestersByMarks(replacementSubjectMarks);
                MarksEntity latestReplacementMark = null;
                latestReplacementMark = sortedList.get(sortedList.size() - 1);

                LatestFailMark replacementFailModel = new LatestFailMark();

                //check xem môn này có đang fail hay không
                Ultilities.isLatestMarkFailOrNot(latestReplacementMark, sortedList, replacementFailModel);


                replacementFailed = replacementFailModel.isFailed();
                replacementFailMark = replacementFailModel.getLatestFailedMark();

            }

            //(nếu fail đang được xét và fail cả môn thay thế) hoặc
            //(in ra môn thay thế bị failed, với môn bị thay thế là môn đang xét)
            if (isFailed && replacementFailed) {
                resultList.add(failMark);
                replacementList.put(subject.getId(), replacementFailMark);
            }
            //(nếu không fail môn được xét mà chỉ fail môn thay thế)
            else if (!isFailed && replacementFailed) {
                resultList.add(currentMark);
                replacementList.put(subject.getId(), replacementFailMark);

            }
            //nếu chỉ có môn đang xét bị fail
            // in ra môn đang xét bị fail
            else if (isFailed && !replacementFailed) {
                resultList.add(failMark);
            }


        }

        List<List<String>> resultFailList = new ArrayList<>();
        for (
                MarksEntity itemMark : resultList)

        {
            MarksEntity replacementMark = replacementList.get(
                    itemMark.getSubjectMarkComponentId().getSubjectId().getId());
            if (replacementMark != null) {
                ArrayList<String> tmp = new ArrayList<>();
                tmp.add(replacementMark.getSubjectMarkComponentId() == null ? "-" : replacementMark
                        .getSubjectMarkComponentId().getSubjectId().getId());
                tmp.add(replacementMark.getCourseId() == null ? "-" : replacementMark.getCourseId().getSemester());
                tmp.add(replacementMark.getSemesterId() == null ? "-" : replacementMark.getSemesterId().getSemester());
                tmp.add(String.valueOf(replacementMark.getAverageMark()));
                tmp.add("FAIL");
                tmp.add(itemMark.getSubjectMarkComponentId().getSubjectId().getId());
                resultFailList.add(tmp);
            } else {
                ArrayList<String> tmp = new ArrayList<>();
                tmp.add(itemMark.getSubjectMarkComponentId() == null ? "-" : itemMark.getSubjectMarkComponentId().getSubjectId().getId());
                tmp.add(itemMark.getCourseId() == null ? "-" : itemMark.getCourseId().getSemester());
                tmp.add(itemMark.getSemesterId() == null ? "-" : itemMark.getSemesterId().getSemester());
                tmp.add(String.valueOf(itemMark.getAverageMark()));
                tmp.add("FAIL");
                tmp.add("-");
                resultFailList.add(tmp);
            }
        }

        return resultFailList;
    }

    @RequestMapping("/getStudentCurrentCourse")
    @ResponseBody
    public JsonObject GetStudentCurrentCourse(@RequestParam Map<String, String> params) {
        JsonObject jsonObject = new JsonObject();

        int stuId = Integer.parseInt(params.get("stuId"));
        String semester = Global.getTemporarySemester().getSemester();

        try {
            List<List<String>> result = processCurrent(stuId, semester);

            List<List<String>> set2 = result.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(set2);

            jsonObject.addProperty("iTotalRecords", result.size());
            jsonObject.addProperty("iTotalDisplayRecords", result.size());
            jsonObject.add("aaData", aaData);
            jsonObject.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public List<List<String>> processCurrent(int stuId, String semester) {
        IMarksService marksService = new MarksServiceImpl();

        List<MarksEntity> list = marksService.getStudentMarksById(stuId);
        List<MarksEntity> sortedList = Global.TransformAllMarksList(list);
        List<MarksEntity> filterList = sortedList
                .stream()
                .filter(c -> c.getStatus().toLowerCase().contains("studying"))
                .filter(Ultilities.distinctByKey(c -> c.getSubjectMarkComponentId().getSubjectId().getId()))
                .collect(Collectors.toList());

        List<List<String>> displayList = new ArrayList<>();
        for (MarksEntity sc : filterList) {
            List<String> row = new ArrayList<>();
            row.add(sc.getSubjectMarkComponentId().getSubjectId().getId());
            row.add(sc.getSubjectMarkComponentId().getSubjectId().getName());
            row.add(sc.getStatus());

            displayList.add(row);
        }

        return displayList;
    }

    @RequestMapping("/getStudentNextCourse")
    @ResponseBody
    public JsonObject GetStudentNextCourse(@RequestParam Map<String, String> params) {

        JsonObject jsonObject = new JsonObject();

        int stuId = Integer.parseInt(params.get("stuId"));
        String semester = Global.getTemporarySemester().getSemester();

        try {
//            List<List<String>> result = processNext(stuId, semester, false, false);

            List<List<String>> result = processNext2(stuId, semester);

            List<List<String>> set2 = result.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(set2);

            jsonObject.addProperty("iTotalRecords", result.size());
            jsonObject.addProperty("iTotalDisplayRecords", result.size());
            jsonObject.add("aaData", aaData);
            jsonObject.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @RequestMapping("/getStudentNotNextCourse")
    @ResponseBody
    public JsonObject GetStudentCantStudy(@RequestParam Map<String, String> params) {

        JsonObject jsonObject = new JsonObject();

        int stuId = Integer.parseInt(params.get("stuId"));
        String semester = Global.getTemporarySemester().getSemester();
        int total = 7;
        if (params.get("total") != null) {
            total = Integer.parseInt(params.get("total"));
            if (total < 1) {
                total = 7;
            }
        }

        try {
//            List<List<String>> result = processNext(stuId, semester, true, true);
//
//            Suggestion suggestion = processSuggestion(stuId, semester, total);
//            List<List<String>> result2 = suggestion.getData();
//
//            List<String> brea = new ArrayList<>();
//            brea.add("break");
//            brea.add("");
//
//            int index = result2.indexOf(brea);
//            if (index > -1) {
//                if (suggestion.isDuchitieu()) {
//                    result2 = result2.subList(index + 1, result2.size());
//                } else {
//                    result2 = result2.subList(0, index);
//                }
//
//                for (List<String> r : result2) {
//                    result.add(r);
//                }
//            }
            List<List<String>> result = processSuggestion2(stuId, semester, total, false, null, null, null);

            List<List<String>> set2 = result.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(set2);

            jsonObject.addProperty("iTotalRecords", result.size());
            jsonObject.addProperty("iTotalDisplayRecords", result.size());
            jsonObject.add("aaData", aaData);
            jsonObject.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public List<List<String>> processNext(int stuId, String semester, boolean checkPrequisite, boolean getFailPrequisiteList) {
        IStudentService studentService = new StudentServiceImpl();
        ISubjectService subjectService = new SubjectServiceImpl();

        StudentEntity student = studentService.findStudentById(stuId);
        List<MarksEntity> marks = student.getMarksEntityList();
        List<MarksEntity> mfkList = new ArrayList<>(marks);
        marks = mfkList.stream().filter(q -> q.getIsActivated()).collect(Collectors.toList());
        marks = Global.TransformMarksList(marks);

        List<SubjectCurriculumEntity> list = new ArrayList<>();
        List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
        if (!docs.isEmpty()) {
            for (DocumentStudentEntity doc : docs) {
                if (doc.getCurriculumId() != null) {
                    List<SubjectCurriculumEntity> cursubs = doc.getCurriculumId().getSubjectCurriculumEntityList();
                    for (SubjectCurriculumEntity s : cursubs) {
                        int gap = Global.SemesterGap();
                        if (s.getTermNumber() == (student.getTerm() + 1 - gap)) {
                            if (!list.contains(s)) list.add(s);
                        }
                    }
                }
            }
        }

        if (student.getTerm() - Global.SemesterGap() == 5) {
            ICurriculumService curriculumService = new CurriculumServiceImpl();
            String prefix = student.getProgramId().getName();
            String studentClass = docs.get(0).getCurriculumId().getName().split("_")[1];
            String nextCur = prefix + "_" + studentClass + "_OJT";
            CurriculumEntity nCur = curriculumService.getCurriculumByName(nextCur);
            if (nCur != null) {
                List<SubjectCurriculumEntity> cursubs = nCur.getSubjectCurriculumEntityList();
                for (SubjectCurriculumEntity s : cursubs) {
                    if (!list.contains(s)) list.add(s);
                }
            }
        }

        List<SubjectEntity> failedPrequisiteList = new ArrayList<>();

        // Check students score if exist remove
        if (!list.isEmpty()) {
            List<String> curriculumSubjects = new ArrayList<>();
            list.forEach(c -> {
                if (!curriculumSubjects.contains(c.getSubjectId().getId())) {
                    curriculumSubjects.add(c.getSubjectId().getId());
                }
            });
            if (!curriculumSubjects.isEmpty()) {
                List<MarksEntity> existList = marks
                        .stream()
                        .filter(c -> curriculumSubjects.stream().anyMatch(b -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(b)))
                        .collect(Collectors.toList());
                existList = Ultilities.FilterStudentsOnlyPassAndFailAndStudying(existList);
                Iterator<SubjectCurriculumEntity> iterator = list.iterator();
                while (iterator.hasNext()) {
                    SubjectCurriculumEntity cur = iterator.next();
                    if (existList.stream().anyMatch(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(cur.getSubjectId().getId()))) {
                        iterator.remove();
                    } else {
                        boolean failed = false;
                        if (checkPrequisite) {
//               check prequisite
                            List<String> processedData = new ArrayList<>();
                            SubjectEntity entity = cur.getSubjectId();
                            if (entity != null && entity.getPrequisiteEntity() != null) {
                                String preSubs = entity.getPrequisiteEntity().getPrequisiteSubs();
                                String[] rows = preSubs == null ? (entity.getPrequisiteEntity().getNewPrequisiteSubs() == null ? new String[0] : entity.getPrequisiteEntity().getNewPrequisiteSubs().split("OR")) : preSubs.split("OR");
                                for (String row : rows) {
                                    row = row.replaceAll("\\(", "").replaceAll("\\)", "");
                                    String[] cells = row.split(",");
                                    for (String cell : cells) {
                                        cell = cell.trim();
                                        SubjectEntity c = subjectService.findSubjectById(cell);
                                        if (c != null) processedData.add(cell);
                                    }
                                }
                                if (!entity.getSubjectEntityList().isEmpty()) {
                                    for (SubjectEntity replaces : entity.getSubjectEntityList()) {
                                        processedData.add(replaces.getId());
                                    }
                                }
                                if (!entity.getSubjectEntityList1().isEmpty()) {
                                    for (SubjectEntity replaces : entity.getSubjectEntityList1()) {
                                        processedData.add(replaces.getId());
                                        for (SubjectEntity rep : replaces.getSubjectEntityList()) {
                                            processedData.add(rep.getId());
                                        }
                                    }
                                }
                                if (!processedData.isEmpty()) {
                                    List<MarksEntity> list2 = marks
                                            .stream()
                                            .filter(c -> processedData.stream().anyMatch(b -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(b)))
                                            .collect(Collectors.toList());
                                    failed = Ultilities.HasFailedPrequisitesOfOneStudent(list2, cur.getSubjectId().getPrequisiteEntity());
                                }
                            }
                        }

                        if (failed) {
                            failedPrequisiteList.add(cur.getSubjectId());
                            iterator.remove();
                        } else {
                            List<SubjectEntity> replacers = cur.getSubjectId().getSubjectEntityList();
                            boolean deleted = false;
                            for (SubjectEntity s : replacers) {
                                List<MarksEntity> rep = marks
                                        .stream()
                                        .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s.getId()))
                                        .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
                                if (!rep.isEmpty()) {
                                    iterator.remove();
                                    deleted = true;
                                    break;
                                }
                            }
                            if (!deleted) {
                                List<SubjectEntity> replacers2 = cur.getSubjectId().getSubjectEntityList1();
                                for (SubjectEntity s : replacers2) {
                                    List<MarksEntity> rep = marks
                                            .stream()
                                            .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s.getId()))
                                            .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
                                    if (!rep.isEmpty()) {
                                        iterator.remove();
                                        deleted = true;
                                        break;
                                    }
                                }
                                if (!deleted) {
                                    for (SubjectEntity s : replacers2) {
                                        for (SubjectEntity ss : s.getSubjectEntityList()) {
                                            List<MarksEntity> rep = marks
                                                    .stream()
                                                    .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(ss.getId()))
                                                    .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
                                            if (!rep.isEmpty()) {
                                                iterator.remove();
                                                deleted = true;
                                                break;
                                            }
                                        }
                                        if (deleted) break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        List<List<String>> result = new ArrayList<>();
        if (!getFailPrequisiteList) {
            for (SubjectCurriculumEntity sc : list) {
                List<String> row = new ArrayList<>();
                row.add(sc.getSubjectId().getId());
                row.add(sc.getSubjectId().getName());

                result.add(row);
            }
        } else {
            for (SubjectEntity sc : failedPrequisiteList) {
                List<String> row = new ArrayList<>();
                row.add(sc.getId());
                row.add(sc.getName());

                result.add(row);
            }
        }


        return result;
    }

    public List<List<String>> processNext2(int stuId, String semester) {
        IStudentService studentService = new StudentServiceImpl();
        ISubjectService subjectService = new SubjectServiceImpl();

        StudentEntity student = studentService.findStudentById(stuId);
        List<MarksEntity> marks = new ArrayList<>(student.getMarksEntityList());

        // loại notStart, sắp xếp điểm theo kỳ
        marks = Global.TransformMarksList(marks);

        //get all subjectCurriculum of student for next semester
        List<SubjectCurriculumEntity> list = new ArrayList<>();
        List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
        if (!docs.isEmpty()) {
            for (DocumentStudentEntity doc : docs) {
                if (doc.getCurriculumId() != null) {
                    List<SubjectCurriculumEntity> subjectCurriculumList
                            = doc.getCurriculumId().getSubjectCurriculumEntityList();
                    for (SubjectCurriculumEntity s : subjectCurriculumList) {
                        //khoảng cách giữa học kì hiện hành và học kì giả lập
                        int gap = Global.SemesterGap();
                        int studentNextTerm = student.getTerm() + 1 - gap;
                        //check để kì học sinh không vượt quá 9
//                        studentNextTerm = studentNextTerm > 9 ? 9 : student.getTerm() + 1 - gap;
                        if (s.getTermNumber() == studentNextTerm) {
                            if (!list.contains(s)) list.add(s);
                        }
                    }
                }
            }
        }

        //process những môn sinh viên chuẩn bi đi ojt, (ojt của BA trong tương lai có thể sẽ là kì 7)
        if ((student.getTerm() - Global.SemesterGap() == Enums.SpecialTerm.OJTTERM.getValue() && list.isEmpty())
                || (student.getTerm() - Global.SemesterGap() == Enums.SpecialTerm.OJTTERM2.getValue()
                && student.getProgramId().getName().equalsIgnoreCase("BA")
                && list.isEmpty())) {
            ICurriculumService curriculumService = new CurriculumServiceImpl();
            String prefix = student.getProgramId().getName();
            String studentClass = docs.get(0).getCurriculumId().getName().split("_")[1];
            String nextCur = prefix + "_" + studentClass + "_OJT";
            CurriculumEntity nCur = curriculumService.getCurriculumByName(nextCur);
            if (nCur != null) {
                List<SubjectCurriculumEntity> cursubs = nCur.getSubjectCurriculumEntityList();
                for (SubjectCurriculumEntity s : cursubs) {
                    if (!list.contains(s)) list.add(s);
                }
            }
        }

//        List<SubjectEntity> failedPrerequisiteList = new ArrayList<>();

        // Check students score if exist remove
        if (!list.isEmpty()) {
            // get unique subject id
            List<String> curriculumSubjects = list.stream()
                    .map(q -> q.getSubjectId().getId()).distinct().collect(Collectors.toList());

            if (!curriculumSubjects.isEmpty()) {
                List<MarksEntity> existList = marks
                        .stream()
                        .filter(c -> curriculumSubjects.stream().anyMatch(b -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(b)))
                        .collect(Collectors.toList());

                Iterator<SubjectCurriculumEntity> iterator = list.iterator();
                while (iterator.hasNext()) {
                    SubjectCurriculumEntity current = iterator.next();
                    SubjectEntity subject = current.getSubjectId();
                    List<MarksEntity> subjectMarkList = existList.stream()
                            .filter(q -> q.getSubjectMarkComponentId().getSubjectId().getId()
                                    .equalsIgnoreCase(subject.getId())
                            ).collect(Collectors.toList());
                    //sort marks list by semester
                    List<MarksEntity> sortedMarkList = Ultilities.SortSemestersByMarks(subjectMarkList);

                    MarksEntity latestMark = null;
                    if (!sortedMarkList.isEmpty()) {
                        //check xem có môn trả nợ trong kì hay không
                        latestMark = sortedMarkList.get(subjectMarkList.size() - 1);
                    }

                    // chỉ cần trước đó đã học môn này (bao gồm cả học sớm hơn tiến độ sẽ đều bị loại ra)
                    if (latestMark != null) {
                        iterator.remove();
                    }

                    //check môn thay thế
                }
            }
        }

        List<List<String>> result = new ArrayList<>();
        for (SubjectCurriculumEntity sc : list) {

            List<String> row = new ArrayList<>();
            row.add(sc.getSubjectId().getId());
            row.add(sc.getSubjectId().getName());

            result.add(row);
        }


        return result;
    }


//    private int findSubjectCredit(StudentEntity student, String subjectId) {
//        List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
//        if (!docs.isEmpty()) {
//            for (DocumentStudentEntity doc : docs) {
//                if (doc.getCurriculumId() != null) {
//                    List<SubjectCurriculumEntity> cursubs = doc.getCurriculumId().getSubjectCurriculumEntityList();
//                    for (SubjectCurriculumEntity s : cursubs) {
//                        if (s.getSubjectId().getId().equals(subjectId)) {
//                            return s.getSubjectCredits();
//                        }
//                    }
//                }
//            }
//        }
//
//        return 0;
//    }

    @RequestMapping("/getStudentNextCourseSuggestion")
    @ResponseBody
    public JsonObject GetStudentNextCourseSuggestion(@RequestParam Map<String, String> params) {
        JsonObject data = new JsonObject();

        int stuId = Integer.parseInt(params.get("stuId"));
        String semester = Global.getTemporarySemester().getSemester();
        int total = 7;
        if (params.get("total") != null) {
            total = Integer.parseInt(params.get("total"));
            if (total < 1) {
                total = 7;
            }
        }

        try {
//            Suggestion suggestion = processSuggestion(stuId, semester, total);
//            List<List<String>> result = suggestion.getData();
//
//            List<String> brea = new ArrayList<>();
//            brea.add("break");
//            brea.add("");
//
//            int index = result.indexOf(brea);
//            if (index > -1) {
//                if (suggestion.isDuchitieu()) {
//                    result = result.subList(0, index);
//                } else {
//                    result = result.subList(index + 1, result.size());
//                }
//            }
            List<List<String>> result = processSuggestion2(stuId, semester, total,
                    true, null, null, null);

            List<List<String>> set2 = result.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(set2);

            data.addProperty("iTotalRecords", set2.size());
            data.addProperty("iTotalDisplayRecords", set2.size());
            data.add("aaData", aaData);
            data.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public Suggestion processSuggestion(int stuId, String semester, int totalDisplay) {
        Suggestion suggestion = new Suggestion();

        IMarksService marksService = new MarksServiceImpl();

        IStudentService studentService = new StudentServiceImpl();
        ISubjectService subjectService = new SubjectServiceImpl();

        StudentEntity student = studentService.findStudentById(stuId);

        List<SubjectCurriculumEntity> studentSubs = new ArrayList<>();
        List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
        if (!docs.isEmpty()) {
            for (DocumentStudentEntity doc : docs) {
                if (doc.getCurriculumId() != null) {
                    List<SubjectCurriculumEntity> cursubs = doc.getCurriculumId().getSubjectCurriculumEntityList();
                    for (SubjectCurriculumEntity s : cursubs) {
                        studentSubs.add(s);
                    }
                }
            }
        }

        List<MarksEntity> marks = marksService.getStudentMarksById(stuId);
        marks = Global.TransformAllMarksList(marks);

        /*-----------------------------------Fail Course--------------------------------------------------*/
        List<String> l2 = Ultilities.getStudentCurriculumSubjects(student);

        List<MarksEntity> newlist = Ultilities.FilterStudentsOnlyPassAndFail(marks);
        List<MarksEntity> resultList = Ultilities.FilterListFailStudent(newlist);
        List<SubjectEntity> failSubjects = new ArrayList<>();

        List<MarksEntity> list2 = marks.stream().filter(c -> c.getStatus().toLowerCase().contains("studying")).collect(Collectors.toList());
        Iterator<MarksEntity> iterator = resultList.iterator();
        while (iterator.hasNext()) {
            MarksEntity cur = iterator.next();
            if (list2.stream().anyMatch(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(cur.getSubjectMarkComponentId().getSubjectId().getId()))) {
                iterator.remove();
            } else {
                SubjectEntity su = cur.getSubjectMarkComponentId().getSubjectId();
                if (!l2.stream().anyMatch(a -> a.equals(su.getId()))) {
                    iterator.remove();
                } else {
                    List<SubjectEntity> replacers = su.getSubjectEntityList();
                    boolean deleted = false;
                    for (SubjectEntity s : replacers) {
                        List<MarksEntity> rep = marks
                                .stream()
                                .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s.getId()))
                                .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
                        if (!rep.isEmpty()) {
                            iterator.remove();
                            deleted = true;
                            break;
                        }
                    }
                    if (!deleted) {
                        List<SubjectEntity> replacers2 = su.getSubjectEntityList1();
                        for (SubjectEntity s : replacers2) {
                            List<MarksEntity> rep = marks
                                    .stream()
                                    .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s.getId()))
                                    .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
                            if (!rep.isEmpty()) {
                                iterator.remove();
                                deleted = true;
                                break;
                            }
                        }
                        if (!deleted) {
                            for (SubjectEntity s : replacers2) {
                                for (SubjectEntity ss : s.getSubjectEntityList()) {
                                    List<MarksEntity> rep = marks
                                            .stream()
                                            .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(ss.getId()))
                                            .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
                                    if (!rep.isEmpty()) {
                                        iterator.remove();
                                        deleted = true;
                                        break;
                                    }
                                }
                                if (deleted) break;
                            }
                        }
                    }
                }
            }
        }

        resultList.forEach(c -> {
            if (!failSubjects.contains(c.getSubjectMarkComponentId().getSubjectId())) {
                failSubjects.add(c.getSubjectMarkComponentId().getSubjectId());
            }
        });

        /*-------------------------------Get Next Course------------------------------------------------*/
        List<SubjectCurriculumEntity> listNextCurri = new ArrayList<>();
        for (SubjectCurriculumEntity s : studentSubs) {
            if (s.getTermNumber() == (student.getTerm() + 1 - Global.SemesterGap())) {
                if (!listNextCurri.contains(s)) listNextCurri.add(s);
            }
        }

        if (student.getTerm() - Global.SemesterGap() == 5) {
            ICurriculumService curriculumService = new CurriculumServiceImpl();
            String prefix = student.getProgramId().getName();
            String studentClass = docs.get(0).getCurriculumId().getName().split("_")[1];
            String nextCur = prefix + "_" + studentClass + "_OJT";
            CurriculumEntity nCur = curriculumService.getCurriculumByName(nextCur);
            if (nCur != null) {
                List<SubjectCurriculumEntity> cursubs = nCur.getSubjectCurriculumEntityList();
                for (SubjectCurriculumEntity s : cursubs) {
                    if (!listNextCurri.contains(s)) listNextCurri.add(s);
                }
            }
        }

        List<String> tt = listNextCurri.stream().map(c -> c.getSubjectId().getId()).distinct().collect(Collectors.toList());
        if (!listNextCurri.isEmpty()) {
            List<MarksEntity> existList = marks
                    .stream()
                    .filter(c -> tt.stream().anyMatch(b -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(b)))
                    .collect(Collectors.toList());
            existList = Ultilities.FilterStudentsOnlyPassAndFailAndStudying(existList);
            Iterator<SubjectCurriculumEntity> iteratorPre1quisite = listNextCurri.iterator();
            while (iteratorPre1quisite.hasNext()) {
                SubjectCurriculumEntity cur = iteratorPre1quisite.next();
                if (existList.stream().anyMatch(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(cur.getSubjectId().getId()))) {
                    iteratorPre1quisite.remove();
                } else {
                    boolean failed = false;
//                                     check prequisite
                    List<String> processedData = new ArrayList<>();
                    SubjectEntity entity = cur.getSubjectId();
                    if (entity != null && entity.getPrequisiteEntity() != null) {
                        String preSubs = entity.getPrequisiteEntity().getPrequisiteSubs();
                        String[] rows = preSubs == null ? (entity.getPrequisiteEntity().getNewPrequisiteSubs() == null ? new String[0] : entity.getPrequisiteEntity().getNewPrequisiteSubs().split("OR")) : preSubs.split("OR");
                        for (String row : rows) {
                            row = row.replaceAll("\\(", "").replaceAll("\\)", "");
                            String[] cells = row.split(",");
                            for (String cell : cells) {
                                cell = cell.trim();
                                SubjectEntity c = subjectService.findSubjectById(cell);
                                if (c != null) processedData.add(cell);
                            }
                        }
                        if (!entity.getSubjectEntityList().isEmpty()) {
                            for (SubjectEntity replaces : entity.getSubjectEntityList()) {
                                processedData.add(replaces.getId());
                            }
                        }
                        if (!entity.getSubjectEntityList1().isEmpty()) {
                            for (SubjectEntity replaces : entity.getSubjectEntityList1()) {
                                processedData.add(replaces.getId());

                                for (SubjectEntity r : replaces.getSubjectEntityList()) {
                                    processedData.add(r.getId());
                                }
                            }
                        }
                        if (!processedData.isEmpty()) {
                            List<MarksEntity> list29 = marks
                                    .stream()
                                    .filter(c -> processedData.stream().anyMatch(b -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(b)))
                                    .collect(Collectors.toList());
                            failed = Ultilities.HasFailedPrequisitesOfOneStudent(list29, cur.getSubjectId().getPrequisiteEntity());
                        }

                        if (failed) {
                            iteratorPre1quisite.remove();
                        } else {
                            List<SubjectEntity> replacers = cur.getSubjectId().getSubjectEntityList();
                            boolean deleted = false;
                            for (SubjectEntity s : replacers) {
                                List<MarksEntity> rep = marks
                                        .stream()
                                        .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s.getId()))
                                        .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
                                if (!rep.isEmpty()) {
                                    iteratorPre1quisite.remove();
                                    deleted = true;
                                    break;
                                }
                            }
                            if (!deleted) {
                                List<SubjectEntity> replacers2 = cur.getSubjectId().getSubjectEntityList1();
                                for (SubjectEntity s : replacers2) {
                                    List<MarksEntity> rep = marks
                                            .stream()
                                            .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s.getId()))
                                            .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
                                    if (!rep.isEmpty()) {
                                        iteratorPre1quisite.remove();
                                        deleted = true;
                                        break;
                                    }
                                }
                                if (!deleted) {
                                    for (SubjectEntity s : replacers2) {
                                        for (SubjectEntity ss : s.getSubjectEntityList()) {
                                            List<MarksEntity> rep = marks
                                                    .stream()
                                                    .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(ss.getId()))
                                                    .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
                                            if (!rep.isEmpty()) {
                                                iteratorPre1quisite.remove();
                                                deleted = true;
                                                break;
                                            }
                                        }
                                        if (deleted) break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        List<SubjectEntity> nextSubjects = new ArrayList<>();
        listNextCurri.forEach(c -> {
            if (!nextSubjects.contains(c.getSubjectId())) {
                nextSubjects.add(c.getSubjectId());
            }
        });

        // ------------------------------------------------------------------------------------------

        List<String> curriculumSubjects = new ArrayList<>();
        for (SubjectEntity next : nextSubjects) {
            if (!curriculumSubjects.contains(next.getId())) curriculumSubjects.add(next.getId());
        }
        if (!curriculumSubjects.isEmpty()) {
            List<MarksEntity> existList = marks
                    .stream()
                    .filter(c -> curriculumSubjects.stream().anyMatch(b -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(b)))
                    .collect(Collectors.toList());
            existList = Ultilities.FilterStudentsOnlyPassAndFailAndStudying(existList);
            Iterator<SubjectEntity> iterator3 = nextSubjects.iterator();
            while (iterator3.hasNext()) {
                SubjectEntity entity = iterator3.next();
                if (existList.stream().anyMatch(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(entity.getId()))) {
                    iterator3.remove();
                } else {
                    // check prequisite
                    List<String> processedData = new ArrayList<>();
                    if (entity.getPrequisiteEntity() != null) {
                        String preSubs = entity.getPrequisiteEntity().getPrequisiteSubs();
                        String[] rows = preSubs == null ? (entity.getPrequisiteEntity().getNewPrequisiteSubs() == null ? new String[0] : entity.getPrequisiteEntity().getNewPrequisiteSubs().split("OR")) : preSubs.split("OR");
                        for (String row : rows) {
                            row = row.replaceAll("\\(", "").replaceAll("\\)", "");
                            String[] cells = row.split(",");
                            for (String cell : cells) {
                                cell = cell.trim();
                                SubjectEntity c = subjectService.findSubjectById(cell);
                                if (c != null) processedData.add(cell);
                            }
                            if (!entity.getSubjectEntityList().isEmpty()) {
                                for (SubjectEntity replaces : entity.getSubjectEntityList()) {
                                    processedData.add(replaces.getId());
                                }
                            }
                            if (!entity.getSubjectEntityList1().isEmpty()) {
                                for (SubjectEntity replaces : entity.getSubjectEntityList1()) {
                                    processedData.add(replaces.getId());

                                    for (SubjectEntity r : replaces.getSubjectEntityList()) {
                                        processedData.add(r.getId());
                                    }
                                }
                            }
                        }
                        boolean failed = false;
                        if (!processedData.isEmpty()) {
                            List<MarksEntity> list3 = marks
                                    .stream()
                                    .filter(c -> processedData.stream().anyMatch(b -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(b)))
                                    .collect(Collectors.toList());

                            failed = Ultilities.HasFailedPrequisitesOfOneStudent(list3, entity.getPrequisiteEntity());
                        }

                        if (failed) {
                            iterator3.remove();
                        } else {
                            List<SubjectEntity> replacers = entity.getSubjectEntityList();
                            boolean deleted = false;
                            for (SubjectEntity s : replacers) {
                                List<MarksEntity> rep = marks
                                        .stream()
                                        .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s.getId()))
                                        .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
                                if (!rep.isEmpty()) {
                                    iterator3.remove();
                                    deleted = true;
                                    break;
                                }
                            }
                            if (!deleted) {
                                List<SubjectEntity> replacers2 = entity.getSubjectEntityList1();
                                for (SubjectEntity s : replacers2) {
                                    List<MarksEntity> rep = marks
                                            .stream()
                                            .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s.getId()))
                                            .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
                                    if (!rep.isEmpty()) {
                                        iterator3.remove();
                                        deleted = true;
                                        break;
                                    }
                                }
                                if (!deleted) {
                                    for (SubjectEntity s : replacers2) {
                                        for (SubjectEntity ss : s.getSubjectEntityList()) {
                                            List<MarksEntity> rep = marks
                                                    .stream()
                                                    .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(ss.getId()))
                                                    .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
                                            if (!rep.isEmpty()) {
                                                iterator3.remove();
                                                deleted = true;
                                                break;
                                            }
                                        }
                                        if (deleted) break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        /*-------------------------------Chậm tiến độ------------------------------------------------*/
        List<MarksEntity> slowList = marks.stream().filter(c -> c.getStatus().toLowerCase().contains("start")).collect(Collectors.toList());

        List<SubjectEntity> slowSubjects = new ArrayList<>();
        Iterator<MarksEntity> iterator2 = slowList.iterator();
        while (iterator2.hasNext()) {
            // check prequisite
            MarksEntity mark = iterator2.next();
            SubjectEntity entity = mark.getSubjectMarkComponentId().getSubjectId();
            List<String> processedData = new ArrayList<>();
            if (entity.getPrequisiteEntity() != null) {
                String preSubs = entity.getPrequisiteEntity().getPrequisiteSubs();
                String[] rows = preSubs == null ? (entity.getPrequisiteEntity().getNewPrequisiteSubs() == null ? new String[0] : entity.getPrequisiteEntity().getNewPrequisiteSubs().split("OR")) : preSubs.split("OR");
                for (String row : rows) {
                    row = row.replaceAll("\\(", "").replaceAll("\\)", "");
                    String[] cells = row.split(",");
                    for (String cell : cells) {
                        cell = cell.trim();
                        SubjectEntity c = subjectService.findSubjectById(cell);
                        if (c != null) processedData.add(cell);
                    }
                    if (!entity.getSubjectEntityList().isEmpty()) {
                        for (SubjectEntity replaces : entity.getSubjectEntityList()) {
                            processedData.add(replaces.getId());
                        }
                    }
                    if (!entity.getSubjectEntityList1().isEmpty()) {
                        for (SubjectEntity replaces : entity.getSubjectEntityList1()) {
                            processedData.add(replaces.getId());

                            for (SubjectEntity r : replaces.getSubjectEntityList()) {
                                processedData.add(r.getId());
                            }
                        }
                    }
                }
                boolean failed = false;
                if (!processedData.isEmpty()) {
                    List<MarksEntity> list3 = marks
                            .stream()
                            .filter(c -> processedData.stream().anyMatch(b -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(b)))
                            .collect(Collectors.toList());
                    failed = Ultilities.HasFailedPrequisitesOfOneStudent(list3, entity.getPrequisiteEntity());
                }

                if (failed) {
                    iterator2.remove();
                } else {
                    SubjectEntity su = mark.getSubjectMarkComponentId().getSubjectId();
                    List<SubjectEntity> replacers = su.getSubjectEntityList();
                    boolean deleted = false;
                    for (SubjectEntity s : replacers) {
                        List<MarksEntity> rep = marks
                                .stream()
                                .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s.getId()))
                                .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
                        if (!rep.isEmpty()) {
                            iterator2.remove();
                            deleted = true;
                            break;
                        }
                    }
                    if (!deleted) {
                        List<SubjectEntity> replacers2 = su.getSubjectEntityList1();
                        for (SubjectEntity s : replacers2) {
                            List<MarksEntity> rep = marks
                                    .stream()
                                    .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s.getId()))
                                    .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
                            if (!rep.isEmpty()) {
                                iterator2.remove();
                                deleted = true;
                                break;
                            }
                        }
                        if (!deleted) {
                            for (SubjectEntity s : replacers2) {
                                for (SubjectEntity ss : s.getSubjectEntityList()) {
                                    List<MarksEntity> rep = marks
                                            .stream()
                                            .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(ss.getId()))
                                            .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
                                    if (!rep.isEmpty()) {
                                        iterator2.remove();
                                        deleted = true;
                                        break;
                                    }
                                }
                                if (deleted) break;
                            }
                        }
                    }
//                    boolean hasRemoved = false;
//                    List<SubjectEntity> replacers = mark.getSubjectMarkComponentId().getSubjectId().getSubjectEntityList();
//                    List<SubjectEntity> subs = mark.getSubjectMarkComponentId().getSubjectId().getSubjectEntityList1();
//                    for (SubjectEntity s : replacers) {
//                        List<MarksEntity> rep = marks
//                                .stream()
//                                .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s.getId()))
//                                .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt"))
//                                .collect(Collectors.toList());
//                        if (!rep.isEmpty()) {
//                            if (!hasRemoved) {
//                                hasRemoved = true;
//                                iterator2.remove();
//                                break;
//                            }
//                        } else {
//                            List<SubjectEntity> replaceOfS = s.getSubjectEntityList();
//                            for (SubjectEntity r : replaceOfS) {
//                                List<MarksEntity> result = marks
//                                        .stream()
//                                        .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(r.getId()))
//                                        .filter(c -> c.getStatus().toLowerCase().contains("pass") ||
//                                                c.getStatus().toLowerCase().contains("exempt") ||
//                                                c.getStatus().toLowerCase().contains("fall") ||
//                                                c.getStatus().toLowerCase().contains("suspend") ||
//                                                c.getStatus().toLowerCase().contains("attend"))
//                                        .collect(Collectors.toList());
//                                if (!result.isEmpty()) {
//                                    if (!hasRemoved) {
//                                        hasRemoved = true;
//                                        iterator2.remove();
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    for (SubjectEntity s : subs) {
//                        List<MarksEntity> rep = marks
//                                .stream()
//                                .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s.getId()))
//                                .filter(c -> c.getStatus().toLowerCase().contains("pass") ||
//                                        c.getStatus().toLowerCase().contains("exempt") ||
//                                        c.getStatus().toLowerCase().contains("fall") ||
//                                        c.getStatus().toLowerCase().contains("suspend") ||
//                                        c.getStatus().toLowerCase().contains("attend"))
//                                .collect(Collectors.toList());
//                        if (!rep.isEmpty()) {
//                            if (!hasRemoved) {
//                                hasRemoved = true;
//                                iterator2.remove();
//                                break;
//                            }
//                        } else {
//                            List<SubjectEntity> replaceOfS = s.getSubjectEntityList();
//                            for (SubjectEntity r : replaceOfS) {
//                                List<MarksEntity> result = marks
//                                        .stream()
//                                        .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(r.getId()))
//                                        .filter(c -> c.getStatus().toLowerCase().contains("pass") ||
//                                                c.getStatus().toLowerCase().contains("exempt") ||
//                                                c.getStatus().toLowerCase().contains("fall") ||
//                                                c.getStatus().toLowerCase().contains("suspend") ||
//                                                c.getStatus().toLowerCase().contains("attend"))
//                                        .collect(Collectors.toList());
//                                if (!result.isEmpty()) {
//                                    if (!hasRemoved) {
//                                        hasRemoved = true;
//                                        iterator2.remove();
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                    }
                }
            }
        }
        slowList.forEach(c -> {
            if (!slowSubjects.contains(c.getSubjectMarkComponentId().getSubjectId())) {
                slowSubjects.add(c.getSubjectMarkComponentId().getSubjectId());
            }
        });

        // gộp chậm tiến độ và fail và sort theo semester
        List<SubjectEntity> combine = new ArrayList<>();
        List<SubjectEntity> finalCombine = combine;
        failSubjects.forEach(c -> {
            if (!finalCombine.contains(c)) {
                finalCombine.add(c);
            }
        });
        slowSubjects.forEach(c -> {
            if (!finalCombine.contains(c)) {
                finalCombine.add(c);
            }
        });
        combine = finalCombine;
        List<SubjectEntity> sorted = Ultilities.SortSubjectsByOrdering(combine, stuId);

        /*-----------------------------get Subject List--------------------------------------------------------*/
        List<List<String>> others = new ArrayList<>();
        if (sorted.size() >= 5) {
//            sorted = sorted.stream().limit(totalDisplay).collect(Collectors.toList());

            if (!sorted.isEmpty()) {
                sorted.forEach(m -> {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(m.getId());
                    tmp.add(m.getName());
                    others.add(tmp);
                });
            }
        } else if (sorted.size() > 0) {
            for (SubjectEntity subject : sorted) {
                ArrayList<String> tmp = new ArrayList<>();
                tmp.add(subject.getId());
                tmp.add(subject.getName());
                others.add(tmp);
            }
            if (nextSubjects.size() > (totalDisplay - others.size())) {
                for (int i = 0; i < nextSubjects.size(); i++) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(nextSubjects.get(i).getId());
                    tmp.add(nextSubjects.get(i).getName());
                    others.add(tmp);
                }
            } else {
                for (SubjectEntity nextSubject : nextSubjects) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(nextSubject.getId());
                    tmp.add(nextSubject.getName());
                    others.add(tmp);
                }
            }
        } else {
            for (SubjectEntity nextSubject : nextSubjects) {
                ArrayList<String> tmp = new ArrayList<>();
                tmp.add(nextSubject.getId());
                tmp.add(nextSubject.getName());
                others.add(tmp);
            }
        }
        List<List<String>> others2 = others.stream().limit(totalDisplay).collect(Collectors.toList());

        // check ojt and syb pass or not
        docs = student.getDocumentStudentEntityList();
        if (!docs.isEmpty()) {
            ProgramEntity program = student.getProgramId();
            Iterator<List<String>> itr = others2.iterator();

            List<List<String>> tmp2 = others2.subList(0, others2.size());
            for (List<String> str : tmp2) {
                SubjectEntity subject = subjectService.findSubjectById(str.get(0));

                boolean exist = false;
                int percent = 0;
                if (subject.getType() == SubjectTypeEnum.OJT.getId()) {
                    percent = program.getOjt(); // phan tram
                    exist = true;
                } else if (subject.getType() == SubjectTypeEnum.Capstone.getId()) {
                    percent = program.getCapstone();
                    exist = true;
                }

                if (exist) {
//                    List<SubjectCurriculumEntity> curSubs = new ArrayList<>();
//                    for (DocumentStudentEntity doc : docs) {
//                        if (doc.getCurriculumId() != null) {
//                            curSubs.addAll(doc.getCurriculumId().getSubjectCurriculumEntityList());
//                        }
//                    }
//                    int total = 0; // total total tin chi
                    int total = 0;

                    studentSubs = studentSubs.stream().filter(Ultilities.distinctByKey(c -> c.getSubjectId().getId())).collect(Collectors.toList());
                    studentSubs.sort(Comparator.comparingInt(c -> ((SubjectCurriculumEntity) c).getTermNumber()).thenComparingInt(a -> ((SubjectCurriculumEntity) a).getOrdinalNumber()));
                    for (SubjectCurriculumEntity m : studentSubs) {
                        if (m.getSubjectId().getType() == SubjectTypeEnum.OJT.getId() || m.getSubjectId().getType() == SubjectTypeEnum.Capstone.getId()) {
                            break;
                        }
                        Integer num = m.getSubjectCredits();
                        total += (num == null ? 0 : num);
                    }

                    if (subject.getType() == SubjectTypeEnum.OJT.getId()) {
                        total = total;
                    } else if (subject.getType() == SubjectTypeEnum.Capstone.getId()) {
                        total = student.getProgramId().getSpecializedCredits();
                    }

                    // tính tổng tín chỉ
                    List<List<String>> parent = new ArrayList<>();
                    float required = 0;
                    if (subject.getType() == SubjectTypeEnum.OJT.getId()) {
                        required = (float) ((total * 1.0) * (percent * 1.0) / 100);
                    } else if (subject.getType() == SubjectTypeEnum.Capstone.getId()) {
                        required = (float) ((total * 1.0) * (percent * 1.0) / 100);
                    }

                    if (student.getPassCredits() >= required) {
                        List<String> processedData = new ArrayList<>();
                        if (subject.getPrequisiteEntity() != null) {
                            String preSubs = subject.getPrequisiteEntity().getPrequisiteSubs();
                            String[] rows = preSubs == null ? (subject.getPrequisiteEntity().getNewPrequisiteSubs() == null ? new String[0] : subject.getPrequisiteEntity().getNewPrequisiteSubs().split("OR")) : preSubs.split("OR");
                            for (String row : rows) {
                                row = row.replaceAll("\\(", "").replaceAll("\\)", "");
                                String[] cells = row.split(",");
                                for (String cell : cells) {
                                    cell = cell.trim();
                                    SubjectEntity c = subjectService.findSubjectById(cell);
                                    if (c != null) processedData.add(cell);
                                }
                                if (!subject.getSubjectEntityList().isEmpty()) {
                                    for (SubjectEntity replaces : subject.getSubjectEntityList()) {
                                        processedData.add(replaces.getId());
                                    }
                                }
                                if (!subject.getSubjectEntityList1().isEmpty()) {
                                    for (SubjectEntity replaces : subject.getSubjectEntityList1()) {
                                        processedData.add(replaces.getId());

                                        for (SubjectEntity r : replaces.getSubjectEntityList()) {
                                            processedData.add(r.getId());
                                        }
                                    }
                                }
                            }
                        }
                        boolean failed = false;
                        if (!processedData.isEmpty()) {
                            List<MarksEntity> list3 = marks
                                    .stream()
                                    .filter(c -> processedData.stream().anyMatch(b -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(b)))
                                    .collect(Collectors.toList());
                            failed = Ultilities.IsFailedSpecial(list3, subject.getPrequisiteEntity());
                        }

                        if (failed) {
                            suggestion.setDuchitieu(false);
                        } else {
                            suggestion.setDuchitieu(true);
                        }
                    } else {
                        suggestion.setDuchitieu(false);
                    }

                    if (subject.getType() == SubjectTypeEnum.OJT.getId()) {
                        while (itr.hasNext()) {
                            List<String> sss = itr.next();
                            SubjectEntity s = subjectService.findSubjectById(sss.get(0));
                            if (s.getType() == SubjectTypeEnum.OJT.getId() || s.getId().toLowerCase().contains("syb")) {
                                parent.add(sss);
                                itr.remove();
                            }
                        }
                    } else if (subject.getType() == SubjectTypeEnum.Capstone.getId()) {
                        while (itr.hasNext()) {
                            List<String> sss = itr.next();
                            SubjectEntity s = subjectService.findSubjectById(sss.get(0));
                            if (!suggestion.isDuchitieu()) {
                                if (s.getType() == SubjectTypeEnum.Capstone.getId()) {
                                    parent.add(sss);
                                    itr.remove();
                                }
                            } else {
                                parent.add(sss);
                                itr.remove();
                            }
                        }
                    }

                    List<String> tmp = new ArrayList<>();
                    tmp.add("break");
                    tmp.add("");
                    parent.add(tmp);

                    suggestion.setData(parent);

                    break;
                }
            }
        } else {
            System.out.println("Sinh viên " + student.getRollNumber() + " không có document!");
        }

        if (suggestion.getData() == null) suggestion.setData(new ArrayList<>());
        List<List<String>> trim = others2;
//        if (Global.SemesterGap() > 0) {
//            trim = others2;
//        } else {
//            trim = others2.stream().limit(totalDisplay).collect(Collectors.toList());
//        }
        for (List<String> o : trim) {
            suggestion.getData().add(o);
        }

        return suggestion;
    }

    public List<List<String>> processSuggestion2(int studentId, String semester, int totalDisplay
            , boolean isSuggest, List<List<String>> failList, List<List<String>> notStartList
            , List<List<String>> nextList) {
        ISubjectService subjectService = new SubjectServiceImpl();
        IPrerequisiteService prerequisiteService = new PrerequisiteServiceImpl();
        IMarksService marksService = new MarksServiceImpl();

        RealSemesterServiceImpl semesterService = new RealSemesterServiceImpl();
        RealSemesterEntity selectedSemester = semesterService.findSemesterByName(semester);

        List<PrequisiteEntity> allPrerequisites = prerequisiteService.getAllPrerequisite();

        //all marks have been sorted
        if (failList == null) {
            failList = processFailed2(studentId, semester);
        }
        if (notStartList == null) {
            notStartList = processNotStart2(studentId, semester);
        }
        if (nextList == null) {
            nextList = processNext2(studentId, semester);
        }

        StudentEntity studentEntity = studentService.findStudentById(studentId);



        //get all subjectCode, all subjectCode stat in index 0
        List<String> failCode = failList.stream().map(q -> q.get(0)).collect(Collectors.toList());
        List<String> notStartCode = notStartList.stream().map(q -> q.get(0)).collect(Collectors.toList());
        List<String> nextCode = nextList.stream().map(q -> q.get(0)).collect(Collectors.toList());

        //add fall subjectCode into this List for later check, -> this list will be ordered: fail -> notStart -> next
        List<String> subjectList = new ArrayList<>();
        subjectList.addAll(failCode);
        subjectList.addAll(notStartCode);
        subjectList.addAll(nextCode);


        List<SubjectCurriculumEntity> sorted = new ArrayList<>();
        List<DocumentStudentEntity> docs = studentEntity.getDocumentStudentEntityList();
        for (DocumentStudentEntity doc: docs) {
            List<SubjectCurriculumEntity> subjCurList = doc.getCurriculumId().getSubjectCurriculumEntityList();
            for (SubjectCurriculumEntity item: subjCurList) {
               SubjectEntity subjectEntity = item.getSubjectId();
               boolean exist =  subjectList.contains(subjectEntity.getId());
                if(exist){
                    sorted.add(item);
                }
            }
        }

        Collections.sort(sorted, new Comparator<SubjectCurriculumEntity>() {
            @Override
            public int compare(SubjectCurriculumEntity o1, SubjectCurriculumEntity o2) {
                return o1.getTermNumber().compareTo(o2.getTermNumber());
            }
        });

        subjectList = sorted.stream().map(q -> q.getSubjectId().getId()).collect(Collectors.toList());


        subjectList = subjectList.stream().distinct().collect(Collectors.toList());

        List<SubjectEntity> allSubjects = subjectService.getAllSubjects();
        //old code
//        List<PrequisiteEntity> allPrerequisites = prerequisiteService.getAllPrerequisite();
        List<MarksEntity> allMarks = marksService.getStudentMarksById(studentId);

        //suggest subjectList
        List<SubjectEntity> resultList = new ArrayList<>();
        //
        List<SubjectEntity> cantStudyList = new ArrayList<>();

        for (int i = 0; i < subjectList.size(); i++) {
            String subjectCode = subjectList.get(i);
            SubjectEntity subject = allSubjects.stream()
                    .filter(q -> q.getId().equalsIgnoreCase(subjectCode)).findFirst().orElse(null);
            //không cần xét duyệt tiên quyết cho môn fail, bỏ qua số lượng fail
            try {


                if (i < failCode.size() && i < totalDisplay) {

                    PrequisiteEntity prerequisite = subject.getPrequisiteEntity();

                    //----check prerequisite start here------
                    if (prerequisite != null) {
                        String oldPrerequisite = prerequisite.getPrequisiteSubs();
                        String newPrerequisite = prerequisite.getNewPrequisiteSubs();
                        if (oldPrerequisite == null && newPrerequisite == null) {
                            resultList.add(subject);
                        } else {

                            boolean isAbleToLearn = false;
                            //check year for newPrerequisite
                            // if currentSemester < effectionSemester -> we can't check prerequesite
                            boolean able4newPrerequisite = true;
                            List<String> rows = new ArrayList<>();
                            if (oldPrerequisite != null && newPrerequisite == null) {
                                rows = Arrays.asList(oldPrerequisite.split("OR"));
                            } else if (oldPrerequisite == null && newPrerequisite != null) {
                                RealSemesterEntity currentSemester = Global.getCurrentSemester();
                                String effectSemester = prerequisite.getEffectionSemester();
                                RealSemesterEntity effectionSemester = sortedSemester.stream()
                                        .filter(q -> q.getSemester().equalsIgnoreCase(effectSemester))
                                        .findFirst().orElse(null);

                                int currentIndex = sortedSemester.indexOf(currentSemester);
                                int effectIndex = sortedSemester.indexOf(effectionSemester);

                                if (currentIndex < effectIndex) {
                                    able4newPrerequisite = false;
                                }

                                rows = Arrays.asList(newPrerequisite.split("OR"));
                            } else if (oldPrerequisite != null && newPrerequisite != null) {

                                //kiểm tra cả 2 cũ lẫn mới
                                String[] oldSubj = oldPrerequisite.split("OR");
                                String[] newSubj = newPrerequisite.split("OR");
                                rows.addAll(Arrays.asList(oldSubj));
                                rows.addAll(Arrays.asList(newSubj));
                            }


                            if (able4newPrerequisite) {
                                //check prerequisite of subject
                                rowLoop:
                                for (String row : rows) {
                                    row = row.replaceAll("\\(", "")
                                            .replaceAll("\\)", "").trim();
                                    String[] cells = row.split(",");

                                    //count if student learned enough prerequisite subject
                                    int numNeedtoPass = cells.length;
                                    int countPass = 0;

                                    for (String cell : cells) {
                                        String subjCode = cell.trim();
                                        SubjectEntity cellSubject = allSubjects.stream()
                                                .filter(q -> q.getId().equalsIgnoreCase(subjCode)).findFirst()
                                                .orElse(null);
                                        if (cellSubject != null) {


                                            ////mảng này chứa tất cả môn thay thế và môn chính
                                            List<SubjectEntity> checkList = Ultilities.findBackAndForwardReplacementSubject(cellSubject);
                                            List<String> mainList = checkList.stream().map(q -> q.getId()).collect(Collectors.toList());


                                            // kiểm tra môn chính, môn thay thế và môn bị thay thế
                                            List<MarksEntity> marksList = allMarks.stream()
                                                    .filter(q -> mainList.stream()
                                                            .anyMatch(a -> a.equalsIgnoreCase(
                                                                    q.getSubjectMarkComponentId().getSubjectId().getId())
                                                            )
                                                    ).collect(Collectors.toList());

                                            marksList = Ultilities.SortSemestersByMarks(marksList);

                                            if (!marksList.isEmpty()) {

                                                MarksEntity latestMark = marksList.get(marksList.size() - 1);

                                                RealSemesterEntity tmpSemester = latestMark.getSemesterId();

                                                //lấy tất cả điểm trong cùng học kỳ mới nhất,
                                                //  tránh trường hợp học 1 môn 2 lần trong 1 học kỳ mà khi hàm sắp xếp chạy thì
                                                //  2 môn k biết môn nào học trước học sau để lấy điểm cuối cùng
                                                List<MarksEntity> reLearnInSameSemester = marksList.stream()
                                                        .filter(q -> q.getSemesterId().getId() == tmpSemester.getId())
                                                        .collect(Collectors.toList());


                                                RealSemesterEntity summer2017 = sortedSemester.stream()
                                                        .filter(q -> q.getSemester().equalsIgnoreCase("SUMMER2017"))
                                                        .findFirst().orElse(null);
                                                int selectedSemesterIndex = sortedSemester.indexOf(selectedSemester);
                                                int summer2017Index = sortedSemester.indexOf(summer2017);

                                                //check xem có điểm nào thỏa dk
                                                MarksEntity isPass = null;

                                                //nếu có 2 môn cùng được học trong 1 kì,
                                                //  nếu có record nào pass hoặc thỏa dk điểm tiên quyết thì môn đó
                                                //  được xem là pass tiên quyết (ko phải pass môn học đó- chỉ là đủ dk để học môn tiên quyết),
                                                //  vì học sinh không được dk học cải thiện cùng 1 môn trong 1 học kỳ
                                                //  -> trường hợp pass sau đó học cải thiện rồi bị fail trong cùng 1 kỳ là ko xuất hiện,
                                                //   chỉ có học fail sau đó trả nợ ngay trong kì
                                                if (!reLearnInSameSemester.isEmpty()) {

                                                    //1.nếu như học kỳ được chọn <= Summer2017,
                                                    //    xét dk qua môn tiên quyết theo trạng thái điểm.
                                                    //2.nếu như học kỳ được chọn > Summer2017,
                                                    //     xét dk qua  môn tiên quyết lớn hơn hoặc = điểm được quy định trong
                                                    //     bảng Prequsite.
                                                    if (selectedSemesterIndex <= summer2017Index) {
                                                        //nếu trong kì có 2 record, pass, fail --> hs đó pass (không được học cải thiện ngay trong kì)
                                                        // nếu có 2 fail --> fail
                                                        isPass = reLearnInSameSemester.stream()
                                                                .filter(q -> q.getStatus().equalsIgnoreCase(Enums.MarkStatus.PASSED.getValue())
                                                                        || q.getStatus().equalsIgnoreCase(Enums.MarkStatus.STUDYING.getValue()))
                                                                .findFirst().orElse(null);

                                                    } else {
                                                        PrequisiteEntity prerequisite1 = allPrerequisites.stream()
                                                                .filter(q -> q.getSubjectId().equalsIgnoreCase(cellSubject.getId()))
                                                                .findFirst().orElse(null);

                                                        String tempSemester = prerequisite1.getEffectionSemester();

                                                        Double tmpPassMark;
                                                        //xét xem môn này có tồn tại học kỳ áp dụng khác nhau ko
                                                        if (tempSemester == null || tempSemester.isEmpty()) {
                                                            tmpPassMark = prerequisite1.getFailMark() * 1.0;
                                                        } else {
                                                            //check xem học kỳ được chọn thỏa dk áp dụng theo học kỳ nào của môn dang xét
                                                            RealSemesterEntity affectionSemester = sortedSemester.stream()
                                                                    .filter(q -> q.getSemester().equalsIgnoreCase(tempSemester))
                                                                    .findFirst().orElse(null);
                                                            int affectIndex = sortedSemester.indexOf(affectionSemester);

                                                            if (selectedSemesterIndex <= affectIndex) {
                                                                tmpPassMark = prerequisite1.getFailMark() * 1.0;
                                                            } else {
                                                                tmpPassMark = prerequisite1.getNewFailMark() * 1.0;
                                                            }
                                                        }

                                                        isPass = reLearnInSameSemester.stream()
                                                                .filter(q -> q.getAverageMark() >= tmpPassMark
                                                                        || q.getStatus().equalsIgnoreCase(Enums.MarkStatus.PASSED.getValue())
                                                                        || q.getStatus().equalsIgnoreCase(Enums.MarkStatus.STUDYING.getValue()))
                                                                .findFirst().orElse(null);
                                                    }


                                                }
                                                if (isPass != null) {
                                                    ++countPass;
                                                }

                                            }


                                        }
                                    }
                                    if (countPass == numNeedtoPass) {
                                        isAbleToLearn = true;
                                        break rowLoop;
                                    }
                                }

                                //check if this subject is able to learn
                                if (isAbleToLearn) {
                                    resultList.add(subject);
                                } else {
                                    //add vào danh sách môn không được học
                                    cantStudyList.add(subject);
                                }
                            }
                        }
                    } //end of check prerequisite null
                } else {
                    if (subject != null) {
                        resultList.add(subject);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<List<String>> myResultList = new ArrayList<>();
        //return suggestList if isSuggest = true, return cantStudy if isSuggest = false
        if (isSuggest) {
            int count = 1;
            for (SubjectEntity item : resultList
                    ) {
                List<String> tmpList = new ArrayList<>();
                tmpList.add(item.getId());
                tmpList.add(item.getName());

                //set limited for total display
                if (count >= totalDisplay) {
                    break;
                }
                myResultList.add(tmpList);
                count++;

            }
        } else {
            for (SubjectEntity item : cantStudyList
                    ) {
                List<String> tmpList = new ArrayList<>();
                tmpList.add(item.getId());
                tmpList.add(item.getName());

                myResultList.add(tmpList);
            }
        }

        return myResultList;
    }


    @RequestMapping("/getStudentNotStart")
    @ResponseBody
    public JsonObject GetStudentNotStart(@RequestParam Map<String, String> params) {
        JsonObject jsonObject = new JsonObject();

        int stuId = Integer.parseInt(params.get("stuId"));
        String semester = Global.getTemporarySemester().getSemester();

        try {
            List<List<String>> result = processNotStart2(stuId, semester);

            List<List<String>> set2 = result.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(set2);

            jsonObject.addProperty("iTotalRecords", result.size());
            jsonObject.addProperty("iTotalDisplayRecords", result.size());
            jsonObject.add("aaData", aaData);
            jsonObject.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public List<List<String>> processNotStart(int stuId, String semester) {
        List<String> t = new ArrayList<>();
        for (RealSemesterEntity s : sortedSemester) {
            t.add(s.getSemester());
            if (s.getSemester().equals(semester)) break;
        }

        IMarksService marksService = new MarksServiceImpl();

        List<MarksEntity> marks = marksService.getStudentMarksById(stuId);
        //Fix Here
//        marks = Global.TransformMarksList(marks);
        marks = Global.TransformAllMarksList(marks);

        List<MarksEntity> list = marks
                .stream()
                .filter(c -> c.getStatus().toLowerCase().contains("start"))
                .filter(Ultilities.distinctByKey(c -> c.getSubjectMarkComponentId().getSubjectId().getId()))
                .collect(Collectors.toList());

        Iterator<MarksEntity> iterator = list.iterator();
        ISubjectService subjectService = new SubjectServiceImpl();
        while (iterator.hasNext()) {
            // check prequisite
            MarksEntity mark = iterator.next();
            SubjectEntity entity = mark.getSubjectMarkComponentId().getSubjectId();
            List<String> processedData = new ArrayList<>();
            if (entity.getPrequisiteEntity() != null) {
                String preSubs = entity.getPrequisiteEntity().getPrequisiteSubs();
                String[] rows = preSubs == null ?
                        (entity.getPrequisiteEntity().getNewPrequisiteSubs() == null ?
                                new String[0] : entity.getPrequisiteEntity().getNewPrequisiteSubs().split("OR")) : preSubs.split("OR");
                for (String row : rows) {
                    row = row.replaceAll("\\(", "").replaceAll("\\)", "");
                    String[] cells = row.split(",");
                    for (String cell : cells) {
                        cell = cell.trim();
                        SubjectEntity c = subjectService.findSubjectById(cell);
                        if (c != null) processedData.add(cell);
                    }
                    if (!entity.getSubjectEntityList().isEmpty()) {
                        for (SubjectEntity replaces : entity.getSubjectEntityList()) {
                            processedData.add(replaces.getId());
                        }
                    }
                    if (!entity.getSubjectEntityList1().isEmpty()) {
                        for (SubjectEntity replaces : entity.getSubjectEntityList1()) {
                            processedData.add(replaces.getId());

                            for (SubjectEntity r : replaces.getSubjectEntityList()) {
                                processedData.add(r.getId());
                            }
                        }
                    }
                }
                boolean failed = false;
                if (!processedData.isEmpty()) {
                    List<MarksEntity> list3 = marks
                            .stream()
                            .filter(c -> processedData.stream().anyMatch(b -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(b)))
                            .collect(Collectors.toList());
                    failed = Ultilities.HasFailedPrequisitesOfOneStudent(list3, entity.getPrequisiteEntity());
                }

                if (failed) {
                    iterator.remove();
                } else {
                    SubjectEntity su = mark.getSubjectMarkComponentId().getSubjectId();
                    List<SubjectEntity> replacers = su.getSubjectEntityList();
                    boolean deleted = false;
                    for (SubjectEntity s : replacers) {
                        List<MarksEntity> rep = marks
                                .stream()
                                .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s.getId()))
                                .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
                        if (!rep.isEmpty()) {
                            iterator.remove();
                            deleted = true;
                            break;
                        }
                    }
                    if (!deleted) {
                        List<SubjectEntity> replacers2 = su.getSubjectEntityList1();
                        for (SubjectEntity s : replacers2) {
                            List<MarksEntity> rep = marks
                                    .stream()
                                    .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s.getId()))
                                    .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
                            if (!rep.isEmpty()) {
                                iterator.remove();
                                deleted = true;
                                break;
                            }
                        }
                        if (!deleted) {
                            for (SubjectEntity s : replacers2) {
                                for (SubjectEntity ss : s.getSubjectEntityList()) {
                                    List<MarksEntity> rep = marks
                                            .stream()
                                            .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(ss.getId()))
                                            .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt")).collect(Collectors.toList());
                                    if (!rep.isEmpty()) {
                                        iterator.remove();
                                        deleted = true;
                                        break;
                                    }
                                }
                                if (deleted) break;
                            }
                        }
                    }
//                    boolean hasRemoved = false;
//                    List<SubjectEntity> replacers = mark.getSubjectMarkComponentId().getSubjectId().getSubjectEntityList();
//                    List<SubjectEntity> subs = mark.getSubjectMarkComponentId().getSubjectId().getSubjectEntityList1();
//                    for (SubjectEntity s : replacers) {
//                        List<MarksEntity> rep = marks
//                                .stream()
//                                .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s.getId()))
//                                .filter(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt"))
//                                .collect(Collectors.toList());
//                        if (!rep.isEmpty()) {
//                            if (!hasRemoved) {
//                                hasRemoved = true;
//                                iterator.remove();
//                                break;
//                            }
//                        } else {
//                            List<SubjectEntity> replaceOfS = s.getSubjectEntityList();
//                            for (SubjectEntity r : replaceOfS) {
//                                List<MarksEntity> result = marks
//                                        .stream()
//                                        .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(r.getId()))
//                                        .filter(c -> c.getStatus().toLowerCase().contains("pass") ||
//                                                c.getStatus().toLowerCase().contains("exempt") ||
//                                                c.getStatus().toLowerCase().contains("fall") ||
//                                                c.getStatus().toLowerCase().contains("suspend") ||
//                                                c.getStatus().toLowerCase().contains("attend"))
//                                        .collect(Collectors.toList());
//                                if (!result.isEmpty()) {
//                                    if (!hasRemoved) {
//                                        hasRemoved = true;
//                                        iterator.remove();
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    for (SubjectEntity s : subs) {
//                        List<MarksEntity> rep = marks
//                                .stream()
//                                .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(s.getId()))
//                                .filter(c -> c.getStatus().toLowerCase().contains("pass") ||
//                                        c.getStatus().toLowerCase().contains("exempt") ||
//                                        c.getStatus().toLowerCase().contains("fall") ||
//                                        c.getStatus().toLowerCase().contains("suspend") ||
//                                        c.getStatus().toLowerCase().contains("attend"))
//                                .collect(Collectors.toList());
//                        if (!rep.isEmpty()) {
//                            if (!hasRemoved) {
//                                hasRemoved = true;
//                                iterator.remove();
//                                break;
//                            }
//                        } else {
//                            List<SubjectEntity> replaceOfS = s.getSubjectEntityList();
//                            for (SubjectEntity r : replaceOfS) {
//                                List<MarksEntity> result = marks
//                                        .stream()
//                                        .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(r.getId()))
//                                        .filter(c -> c.getStatus().toLowerCase().contains("pass") ||
//                                                c.getStatus().toLowerCase().contains("exempt") ||
//                                                c.getStatus().toLowerCase().contains("fall") ||
//                                                c.getStatus().toLowerCase().contains("suspend") ||
//                                                c.getStatus().toLowerCase().contains("attend"))
//                                        .collect(Collectors.toList());
//                                if (!result.isEmpty()) {
//                                    if (!hasRemoved) {
//                                        hasRemoved = true;
//                                        iterator.remove();
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                    }
                }
            }
        }

        List<List<String>> displayList = new ArrayList<>();
        for (MarksEntity sc : list) {
            List<String> row = new ArrayList<>();
            row.add(sc.getSubjectMarkComponentId().getSubjectId().getId());
            row.add(sc.getSubjectMarkComponentId().getSubjectId().getName());
            row.add(sc.getStatus());

            displayList.add(row);
        }

        displayList = displayList.stream().distinct().collect(Collectors.toList());

        return displayList;
    }

    public List<List<String>> processNotStart2(int stuId, String semester) {
        IMarksService marksService = new MarksServiceImpl();

        List<MarksEntity> marks = marksService.getStudentMarksById(stuId);
        //Fix Here
//        marks = Global.TransformMarksList(marks);
//        marks = Global.TransformAllMarksList(marks);

        //get not start mark
        List<MarksEntity> list = marks
                .stream()
                .filter(c -> c.getStatus().toLowerCase().contains("start"))
                .filter(Ultilities.distinctByKey(c -> c.getSubjectMarkComponentId().getSubjectId().getId()))
                .collect(Collectors.toList());

        Iterator<MarksEntity> iterator = list.iterator();

        markLoop:
        while (iterator.hasNext()) {
            boolean hasPassed = false;

            // check prequisite
            MarksEntity mark = iterator.next();
            SubjectEntity subject = mark.getSubjectMarkComponentId().getSubjectId();

            //mảng này chứa tất cả môn thay thế và môn chính
            List<SubjectEntity> checkSubjects = Ultilities.findBackAndForwardReplacementSubject(subject);

            List<MarksEntity> tempMarkList = marks.stream().filter(q -> checkSubjects.stream()
                    .anyMatch(c -> c.getId().equalsIgnoreCase(q.getSubjectMarkComponentId().getSubjectId().getId()))
            ).collect(Collectors.toList());
            if (!tempMarkList.isEmpty()) {
                List<MarksEntity> sortedMarkList = Ultilities.SortSemestersByMarks(tempMarkList);
                MarksEntity latestMark = null;

                latestMark = sortedMarkList.get(sortedMarkList.size() - 1);
                boolean isFail = Ultilities.isLatestMarkFailOrNotVer2(latestMark, sortedMarkList);

                if (!isFail) {
                    hasPassed = true;
                } else {
                    hasPassed = false;
                }

            }


            if (hasPassed) {
                iterator.remove();
                continue markLoop;
            }


        }

        List<List<String>> displayList = new ArrayList<>();
        for (MarksEntity sc : list) {
            List<String> row = new ArrayList<>();
            row.add(sc.getSubjectMarkComponentId().getSubjectId().getId());
            row.add(sc.getSubjectMarkComponentId().getSubjectId().getName());
            row.add(sc.getStatus());

            displayList.add(row);
        }

        displayList = displayList.stream().distinct().collect(Collectors.toList());

        return displayList;
    }


}
