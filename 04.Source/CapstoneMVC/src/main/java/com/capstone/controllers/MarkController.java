package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.entities.fapEntities.StudentAvgMarks;
import com.capstone.entities.fapEntities.StudentStudyingMarks;
import com.capstone.models.*;
import com.capstone.services.*;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class MarkController {

    @RequestMapping("/markPage")
    public ModelAndView Index(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to /markPage");

        ModelAndView view = new ModelAndView("MarkPage");
        view.addObject("title", "Quản lý điểm");

        return view;
    }

    // get all mark records available in database
    @RequestMapping("/markPage/getMarkList")
    @ResponseBody
    public JsonObject GetMarkList(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();
        IMarksService markService = new MarksServiceImpl();

        int studentId = Integer.parseInt(params.get("studentId"));
        String sSearch = params.get("sSearch").trim();
        int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
        int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
        long iTotalRecords = 0;
        long iTotalDisplayRecords = 0;

        try {
            List<MarksEntity> markList = markService.getMarksForMarkPage(studentId);

            if (studentId > 0) {
                iTotalRecords = markList.size();
            } else {
                iTotalRecords = markService.countAllMarks();
            }

            markList = markList.stream().filter(m ->
                    Ultilities.containsIgnoreCase(m.getSubjectMarkComponentId().getSubjectId().getId(), sSearch)
                            || Ultilities.containsIgnoreCase(m.getSubjectMarkComponentId().getSubjectId().getName(), sSearch)
                            || Ultilities.containsIgnoreCase(m.getSemesterId().getSemester(), sSearch)
                            || Ultilities.containsIgnoreCase(m.getStatus(), sSearch))
                    .collect(Collectors.toList());

            iTotalDisplayRecords = markList.size();

            List<List<String>> result = new ArrayList<>();
            List<MarksEntity> displayList = markList.stream().skip(iDisplayStart)
                    .limit(iDisplayLength).collect(Collectors.toList());
            for (MarksEntity m : displayList) {
                List<String> row = new ArrayList<>();
                row.add(m.getStudentId().getRollNumber()); // Roll number
                row.add(m.getStudentId().getFullName()); // Full name
                row.add(m.getSubjectMarkComponentId().getSubjectId().getId()); // Subject code
                row.add(m.getSubjectMarkComponentId().getSubjectId().getName()); // Subject name
                row.add(m.getSemesterId().getSemester()); // Semester
                row.add(String.valueOf(m.getAverageMark())); // Mark
                row.add(m.getStatus()); // Status
                row.add(String.valueOf(m.getId())); // Mark id

                result.add(row);
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

            jsonObj.addProperty("iTotalRecords", iTotalRecords);
            jsonObj.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
            jsonObj.add("aaData", aaData);
            jsonObj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
        }

        return jsonObj;
    }

    // edit mark's details
    @RequestMapping("/markPage/edit")
    @ResponseBody
    public JsonObject EditMark(@RequestParam Map<String, String> params, HttpServletRequest request) {
        JsonObject jsonObj = new JsonObject();

        if (!Ultilities.checkUserAuthorize2(request, "/markPage")) {
            jsonObj.addProperty("success", false);
            jsonObj.addProperty("message", "Không đủ quyền hạn để thực hiện");
            return jsonObj;
        }


        IMarksService markService = new MarksServiceImpl();

        int markId = Integer.parseInt(params.get("markId"));
        double mark = Double.parseDouble(params.get("mark"));
        String status = params.get("status");

        try {
            MarksEntity marksEntity = markService.getMarkById(markId);
            marksEntity.setAverageMark(mark);
            marksEntity.setStatus(status);
            markService.updateMark(marksEntity);

            //logging user action
            Ultilities.logUserAction("Edit " + marksEntity.getStudentId().getRollNumber() +
                    " - " + marksEntity.getSubjectMarkComponentId().getSubjectId().getId() + " mark");

            jsonObj.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
            jsonObj.addProperty("success", false);
        }

        return jsonObj;
    }

    // delete a record
    @RequestMapping("/markPage/delete")
    @ResponseBody
    public JsonObject DeleteMark(@RequestParam Map<String, String> params, HttpServletRequest request) {
        JsonObject jsonObj = new JsonObject();

        if (!Ultilities.checkUserAuthorize2(request, "/markPage")) {
            jsonObj.addProperty("success", false);
            jsonObj.addProperty("message", "Không đủ quyền hạn để thực hiện");
            return jsonObj;
        }

        IMarksService markService = new MarksServiceImpl();

        int markId = Integer.parseInt(params.get("markId"));

        try {
            MarksEntity marksEntity = markService.getMarkById(markId);
            if (marksEntity != null) {
                //logging User Action
                Ultilities.logUserAction("Delete " + marksEntity.getStudentId().getRollNumber() +
                        " - " + marksEntity.getSubjectMarkComponentId().getSubjectId().getId() + " mark");

                markService.deleteMark(markId);

                jsonObj.addProperty("success", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
            jsonObj.addProperty("success", false);
        }

        return jsonObj;
    }

    @RequestMapping(value = "/importMarkFromFAPPage")
    public ModelAndView goImportStudyingStudentPage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView mav = new ModelAndView("importMarkFromFAP");
        mav.addObject("title", "Đồng bộ hóa điểm từ FAP");
        RealSemesterServiceImpl semesterService = new RealSemesterServiceImpl();

        List<RealSemesterEntity> semesters = semesterService.getAllSemester();
        semesters = Ultilities.SortSemesters(semesters);
        semesters = semesters.stream().filter(s -> !s.getSemester().contains("N/A")).collect(Collectors.toList());
        semesters = Lists.reverse(semesters);

        mav.addObject("semesters", semesters);

        return mav;
    }

    @RequestMapping("/importSynchronizeMarkFromFAP")
    @ResponseBody
    public JsonObject goSynchornizeMarksDataFromFapBySemester(@RequestParam Map<String, String> params, HttpServletRequest request,
                                                              @RequestParam("backup") boolean backup,
                                                              @RequestParam("semesterId") int semesterId) {
        JsonObject jsonObject = new JsonObject();

//        if (!Ultilities.checkUserAuthorize2(request, "/markPage")) {
//            jsonObj.addProperty("success", false);
//            jsonObj.addProperty("message", "Không đủ quyền hạn để thực hiện");
//            return jsonObj;
//        }

        MarksServiceImpl marksService = new MarksServiceImpl();
        Ultilities2ServiceImpl ult2 = new Ultilities2ServiceImpl();
        StudentServiceImpl studentService = new StudentServiceImpl();
        SubjectServiceImpl subjectService = new SubjectServiceImpl();
        RealSemesterServiceImpl semesterService = new RealSemesterServiceImpl();
        MarkComponentServiceImpl markComponentService = new MarkComponentServiceImpl();
        SubjectCurriculumServiceImpl subjectCurriculumService = new SubjectCurriculumServiceImpl();
        SubjectMarkComponentServiceImpl subjectMarkComponentService = new SubjectMarkComponentServiceImpl();
        CourseServiceImpl courseService = new CourseServiceImpl();


        //backup
//        if (backup) {
//            ult2.backupCapstoneDB();
//        }

        HashMap<String, String> error = new HashMap<>();
        HashMap<String, List<StudentAvgMarks>> tempMap = new HashMap<>();
        HashMap<StudentEntity, List<StudentAvgMarks>> map = new HashMap<>();
        RealSemesterEntity semesterEntity = semesterService.findSemesterById(semesterId);
        String semesterName = semesterEntity.getSemester();

        List<StudentAvgMarks> fapMarks = ult2.getFAPMarksBySemester(semesterName);
        List<SubjectEntity> allSubjects = subjectService.getAllSubjects();
        String markComponentName = Enums.MarkComponent.AVERAGE.getValue();
        List<String> subjectCodes = ult2.getFAPSubjectCodesHaveMarks(semesterName);


        List<MarksEntity> marksEntities = new ArrayList<MarksEntity>();


        if (fapMarks == null) {
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", "Đã xảy ra lỗi khi lấy dữ liệu từ FAP!");
            return jsonObject;
        } else if (fapMarks.isEmpty()) {
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", "Điểm của " + semesterName + " bên FAP chưa có điểm");
            return jsonObject;
        } else if (subjectCodes == null) {
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", "Đã xảy ra lỗi khi lấy dữ liệu từ FAP");
            return jsonObject;
        }


        try {
            //delete all student marks by semesterId
            marksService.deleteMarksBySemesterAndSubjectCodesAndStudentId(semesterId, subjectCodes, -1);
            List<StudentEntity> students = studentService.findStudentsBySemesterId(semesterId);
            //group by student rollnumber
            for (StudentAvgMarks item : fapMarks) {
                if (tempMap.containsKey(item.getRollNumber().toUpperCase())) {
                    tempMap.get(item.getRollNumber().toUpperCase()).add(item);
                } else {
                    List<StudentAvgMarks> marks = new ArrayList<>();
                    marks.add(item);
                    tempMap.put(item.getRollNumber().toUpperCase(), marks);
                }
            }

            //turn to map<StudentEntity, List<Marks>> for easier to process
            for (String rollNumber : tempMap.keySet()) {
                StudentEntity student = students.stream().filter(q -> q.getRollNumber().equalsIgnoreCase(rollNumber))
                        .findFirst().orElse(null);
                //spring cache lại data mỗi khi lấy lên
//                studentService.refresh(student);
                if (student != null) {
                    List<StudentAvgMarks> marks = tempMap.get(rollNumber);
                    map.put(student, marks);
                } else {
                    error.put(rollNumber, "student rollNumber not exist");
                }
            }
            List<RealSemesterEntity> allSemester = Global.getSortedList();
            List<Integer> semestersToPreCurrentSelected = new ArrayList<>();
            for (RealSemesterEntity r : allSemester) {
                if (r.getId() == semesterId)
                    break;
                else
                    semestersToPreCurrentSelected.add(r.getId());
            }

            int i = 1;

            dataLoop:
            for (StudentEntity studentEntity : map.keySet()) {
                try {
                    System.out.println("process " + i + " - " + map.size());
                    if (studentEntity.getRollNumber().equalsIgnoreCase("SE61428")) {
                        System.out.println("bug oi");
                    }
                    String rollNumber = studentEntity.getRollNumber();
                    int studentId = studentEntity.getId();
                    List<StudentStatusEntity> statuses = new ArrayList<>(studentEntity.getStudentStatusEntityList());

                    StudentStatusEntity status = statuses.stream().filter(q -> q.getSemesterId().getId() == semesterId).findFirst().orElse(null);
                    if (status == null) {
                        error.put(rollNumber, semesterName + " status not exist!");
                        continue dataLoop;
                    }
                    String term = status.getTerm();
                    Integer studentTerm = null;
                    if (term.contains("+")) {
                        //lấy số kì ra (vd: term = "9+")
                        Pattern p = Pattern.compile("(\\d)");
                        Matcher m = p.matcher(term);   // get a matcher object

                        m.find();
                        String token = m.group(0); //group 0 is always the entire match
                        try {
                            double tempTerm = Double.parseDouble(token);
                            studentTerm = (int) tempTerm;

                        } catch (NumberFormatException ex) {
                            System.out.println(ex.getMessage());
                            error.put(rollNumber, token + " term is not a number");
                            continue dataLoop;
                        }
                    } else {
                        if (term.contains("ENG6")) {
                            studentTerm = 0;
                        } else if (term.contains("ENG5")) {
                            studentTerm = -1;
                        } else if (term.contains("ENG4")) {
                            studentTerm = -2;
                        } else if (term.contains("ENG3")) {
                            studentTerm = -3;
                        } else if (term.contains("ENG2")) {
                            studentTerm = -4;
                        }
                        //dành cho những sinh viên đi ojt lâu hơn 1 kì -> giả sử là 6', 7'
                        else if (term.contains("'")) {
                            String[] splitList = term.split("'");
                            try {
                                Integer tempTerm = Integer.parseInt(splitList[0]);
                                studentTerm = tempTerm;
                            } catch (NumberFormatException ex) {
                                System.out.printf(ex.getMessage());
                                error.put(rollNumber, term + " term is not a number");
                            }
                        }
                        if (studentTerm == null) {
                            try {
                                double tempTerm = Double.parseDouble(term);
                                studentTerm = (int) tempTerm;
                            } catch (NumberFormatException ex) {
                                System.out.println(ex.getMessage());
                                error.put(rollNumber, term + " term is not a number");
                                continue dataLoop;
                            }
                        }
                    }

                    //lấy ra những môn học sẽ học theo khung chương trình của kì được chọn
                    List<SubjectCurriculumEntity> subjectCurList = subjectCurriculumService
                            .getSubjectCurriculumByStudentByTerm(studentId, studentTerm);

                    List<String> subjCodeList = subjectCurList.stream().map(q -> q.getSubjectId().getId())
                            .collect(Collectors.toList());

                    List<StudentAvgMarks> studentFAPMarks = map.get(studentEntity);
                    List<MarkModelExcel> markFAPThisSemester = studentFAPMarks.stream()
                            .map(q -> new MarkModelExcel(q.getAvgMark(), q.getSemesterName(), q.getSubjectCode(),
                                    q.isPassed() == true ? Enums.MarkStatus.PASSED.getValue() : Enums.MarkStatus.FAIL.getValue()))
                            .collect(Collectors.toList());


//                    List<MarksEntity> hasLearned = marksService.getStudentMarksById(studentId);
//                    List<MarksEntity> hasLearnedThisSemester = hasLearned.stream()
//                            .filter(q -> q.getSemesterId().getId() == semesterId)
//                            .collect(Collectors.toList());


                    List<MarkModelExcel> notStartMarks = getNotStartMarks(studentEntity, studentId, marksService, semestersToPreCurrentSelected,
                            semesterName, semesterId, studentFAPMarks, subjCodeList);

                    List<MarkModelExcel> importedMark = new ArrayList<>();
                    importedMark.addAll(markFAPThisSemester);
                    importedMark.addAll(notStartMarks);

                    importedMarkLoop:
                    for (MarkModelExcel item : importedMark) {
                        SubjectEntity subjectEntity = allSubjects.stream()
                                .filter(q -> q.getId().equalsIgnoreCase(item.getSubjectId()))
                                .findFirst().orElse(null);

                        //kiểm tra xem subject có tồn tại không
                        if (subjectEntity == null) {
                            error.put(rollNumber, item.getSubjectId() + " - subject not exist!");
                            continue importedMarkLoop;
                        }
                        //tạo Mark mới
                        MarksEntity mark = new MarksEntity();

                        MarkComponentEntity markComponentEntity =
                                markComponentService.getMarkComponentByName(markComponentName);

                        String subjectMarkComponentName = subjectEntity.getId() + "_" + markComponentName;
                        SubjectMarkComponentEntity subjectMarkComponentEntity =
                                subjectMarkComponentService.findSubjectMarkComponentByNameAndSubjectCd(markComponentName, subjectEntity.getId());

                        //set subjectMarkComponent
                        if (subjectMarkComponentEntity != null) {
                            mark.setSubjectMarkComponentId(subjectMarkComponentEntity);
                        } else {
                            subjectMarkComponentEntity = new SubjectMarkComponentEntity();
                            subjectMarkComponentEntity.setMarkComponentId(markComponentEntity);
                            subjectMarkComponentEntity.setName(subjectMarkComponentName);
                            subjectMarkComponentEntity.setPercentWeight(0.0);
                            subjectMarkComponentEntity.setSubjectId(subjectEntity);
                            subjectMarkComponentEntity = subjectMarkComponentService.createSubjectMarkComponent(subjectMarkComponentEntity);
                            mark.setSubjectMarkComponentId(subjectMarkComponentEntity);
                        }
                        //set student
                        mark.setStudentId(studentEntity);
                        //set Semester
                        mark.setSemesterId(semesterEntity);
                        //set Course, tạo Course nếu chưa tồn tại

                        CourseEntity courseEntity = courseService
                                .findCourseBySemesterAndSubjectCode(semesterName, subjectEntity.getId());
                        if (courseEntity != null) {
                            mark.setCourseId(courseEntity);
                        } else {
                            courseEntity = new CourseEntity();
                            courseEntity.setSemester(semesterName);
                            courseEntity.setSubjectCode(subjectEntity.getId());
                            courseEntity = courseService.createCourse(courseEntity);
                            mark.setCourseId(courseEntity);
                        }

                        //set mark
                        mark.setAverageMark(item.getAverage());
                        mark.setStatus(item.getStatus());
                        mark.setIsActivated(true);
                        mark.setEnabled(true);

                        //bỏ vào list để import
                        marksEntities.add(mark);
                    }
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.writeLog(e);
                    error.put(studentEntity.getRollNumber(), e.getMessage());
                }
            }

            //batch insert mark
            marksService.createMarks(marksEntities);
            request.getSession().setAttribute(Enums.SynchronizeFAP.ERROR_lIST.getValue(), error);
            jsonObject.addProperty("success", true);
            jsonObject.addProperty("message", "Đồng bộ hóa điểm sinh viên thành công !");

        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", e.getMessage());
        }

        return jsonObject;
    }

    public List<MarkModelExcel> getNotStartMarks(StudentEntity studentEntity, int studentId, MarksServiceImpl marksService, List<Integer> semestersToPreCurrentSelected,
                                                 String semesterName, int semesterId, List<StudentAvgMarks> studentFAPMarks,
                                                 List<String> subjCodeList) {
//        List<MarksEntity> hasLearned = new ArrayList<>(studentEntity.getMarksEntityList());
        List<MarksEntity> hasLearned = marksService.getAllMarksByStudent(studentId);
        List<MarksEntity> hasLearnedThisSemester = hasLearned.stream()
                .filter(q -> q.getSemesterId().getId() == semesterId)
                .collect(Collectors.toList());

        //chứa những môn chậm tiến độ
        List<MarkModelExcel> notStartMarks = subjCodeList.stream()
                .filter(q -> !studentFAPMarks.stream().anyMatch(c -> c.getSubjectCode().equalsIgnoreCase(q))
                        && !hasLearnedThisSemester.stream().anyMatch(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equalsIgnoreCase(q)))
                .map(q -> new MarkModelExcel(-1.0, semesterName, q, Enums.MarkStatus.NOT_START.getValue()))
                .collect(Collectors.toList());

        //xóa những môn notStart mà sinh viên đã học rồi(sử dụng trong trường hợp sinh viên đã học trước môn của kì tới)
        List<MarkModelExcel> removedMarks = new ArrayList<>(notStartMarks);
//                    List<MarksEntity> hasLearned = marksService.getMarkByConditions(semesterId, null,studentId);

        List<String> hasLearnedToPreSemester = hasLearned.stream()
                .filter(q -> semestersToPreCurrentSelected.contains(q.getSemesterId().getId()))
                .map(q -> q.getSubjectMarkComponentId().getSubjectId().getId())
                .collect(Collectors.toList());
        if (studentEntity.getRollNumber().equalsIgnoreCase("SE61525")) {
            System.out.println("bug");
        }
        for (int j = 0; j < removedMarks.size(); j++) {
            boolean alreadyLearned = false;
            MarkModelExcel mark = removedMarks.get(j);
            for (String subId : hasLearnedToPreSemester) {
                if (subId.equalsIgnoreCase(mark.getSubjectId())) {
                    alreadyLearned = true;
                    break;
                }
            }
            if (alreadyLearned)
                notStartMarks.remove(mark);
        }
        return notStartMarks;
    }

    //danh sách lỗi khi import điểm studying và notStart cho sinh viên đang học
    @RequestMapping("/getFailImportSynchronizeMarkFromFAP")
    @ResponseBody
    public JsonObject getFailImport4StudyingStudent(@RequestParam Map<String, String> params, HttpServletRequest request) {
        JsonObject obj = new JsonObject();

        //lấy ra danh sách những sinh viên bị lỗi khi đẩy điểm từ bên FAP qua
        // <RollNumber, Error>
        HashMap<String, String> studentList = (HashMap<String, String>) request.getSession()
                .getAttribute(Enums.SynchronizeFAP.ERROR_lIST.getValue());
        String type = params.get("type");

//        final String sSearch = params.get("sSearch");

//        int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
//        int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
//        boolean isGraduate = Boolean.parseBoolean(params.get("boolean"));

        try {
            // RollNumber
            List<List<String>> data = new ArrayList<>();
            if (studentList != null) {
                for (String rollNumber :
                        studentList.keySet()) {
                    List<String> tempData = new ArrayList<>();
                    tempData.add(rollNumber);
                    String error = studentList.get(rollNumber);
                    tempData.add(error);
                    data.add(tempData);
                }
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(data);
            obj.add("aaData", aaData);
//            obj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
        }

        return obj;
    }


    //đồng bộ điểm từ FAP cho 1 sinh viên (không tạo điểm notStart)
    @RequestMapping("/importSynchronizeStudentMarksFromFAP")
    @ResponseBody
    public JsonObject getFAPStudentMarksSynchronize(@RequestParam Map<String, String> params, HttpServletRequest request,
                                                    @RequestParam("studentId") int studentId) {
        JsonObject jsonObject = new JsonObject();

//        if (!Ultilities.checkUserAuthorize2(request, "/markPage")) {
//            jsonObj.addProperty("success", false);
//            jsonObj.addProperty("message", "Không đủ quyền hạn để thực hiện");
//            return jsonObj;
//        }

        MarksServiceImpl marksService = new MarksServiceImpl();
        Ultilities2ServiceImpl ult2 = new Ultilities2ServiceImpl();
        StudentServiceImpl studentService = new StudentServiceImpl();
        SubjectServiceImpl subjectService = new SubjectServiceImpl();
        RealSemesterServiceImpl semesterService = new RealSemesterServiceImpl();
        MarkComponentServiceImpl markComponentService = new MarkComponentServiceImpl();
        SubjectCurriculumServiceImpl subjectCurriculumService = new SubjectCurriculumServiceImpl();
        SubjectMarkComponentServiceImpl subjectMarkComponentService = new SubjectMarkComponentServiceImpl();
        CourseServiceImpl courseService = new CourseServiceImpl();


        //backup
//        if (backup) {
//            ult2.backupCapstoneDB();
//        }

        StudentEntity studentEntity = studentService.findStudentById(studentId);
        if (studentEntity == null) {
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", "Sinh viên này không tồn tại!");
            return jsonObject;
        }
        String studentRollNumber = studentEntity.getRollNumber();

        HashMap<String, String> error = new HashMap<>();
        HashMap<String, List<StudentAvgMarks>> tempMap = new HashMap<>();
        HashMap<StudentEntity, List<StudentAvgMarks>> map = new HashMap<>();

        List<StudentAvgMarks> fapMarks = ult2.getFAPMarksByStudentRollNumber(studentRollNumber);
        List<SubjectEntity> allSubjects = subjectService.getAllSubjects();
        String markComponentName = Enums.MarkComponent.AVERAGE.getValue();


        List<MarksEntity> marksEntities = new ArrayList<MarksEntity>();


        if (fapMarks == null) {
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", "Đã xảy ra lỗi khi lấy dữ liệu từ FAP!");
            return jsonObject;
        } else if (fapMarks.isEmpty()) {
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", "Điểm của " + studentRollNumber + " bên FAP không có điểm");
            return jsonObject;
        }

        try {

            //group by student rollnumber
            for (StudentAvgMarks item : fapMarks) {
                if (tempMap.containsKey(item.getRollNumber().toUpperCase())) {
                    tempMap.get(item.getRollNumber().toUpperCase()).add(item);
                } else {
                    List<StudentAvgMarks> marks = new ArrayList<>();
                    marks.add(item);
                    tempMap.put(item.getRollNumber().toUpperCase(), marks);
                }
            }

            //group mark by semestername
            HashBasedTable<String, String, List<StudentAvgMarks>> table = HashBasedTable.create();

            for (String itemRollNumber : tempMap.keySet()) {
                if (itemRollNumber.equalsIgnoreCase(studentRollNumber)) {
                    List<StudentAvgMarks> marks = tempMap.get(itemRollNumber);

                    for (StudentAvgMarks mark : marks) {
                        String semesterName = mark.getSemesterName();
                        if (table.get(itemRollNumber, semesterName) == null) {
                            List<StudentAvgMarks> newMarkList = new ArrayList<>();
                            newMarkList.add(mark);

                            table.put(itemRollNumber, semesterName, newMarkList);
                        } else {
                            table.get(itemRollNumber, semesterName).add(mark);
                        }
                    }
                }
            }

            List<RealSemesterEntity> allSemester = Global.getSortedList();
////            List<Integer> semestersToPreCurrentSelected = new ArrayList<>();
////            for (RealSemesterEntity r : allSemester) {
////                if (r.getId() == semesterId)
////                    break;
////                else
////                    semestersToPreCurrentSelected.add(r.getId());
////            }
            int i = 1;
            List<StudentAvgMarks> importedMark = new ArrayList<>();
            for (String stuRollNumber : table.rowKeySet()) {
                try {
                    System.out.println(i++ + " - " + table.size() + " semester");
                    Map<String, List<StudentAvgMarks>> semesterGroups = table.row(stuRollNumber);
                    for (String iSemester : semesterGroups.keySet()) {
                        RealSemesterEntity semester = allSemester.stream().filter(q -> q.getSemester().equalsIgnoreCase(iSemester))
                                .findFirst().orElse(null);
                        if (semester == null) {
                            if (error.containsKey(stuRollNumber)) {
                                String errorMsg = error.get(stuRollNumber);
                                errorMsg += "\n " + iSemester + " semester not exist";
                            } else {
                                error.put(stuRollNumber, iSemester + " semester not exist");
                            }
                        }
                        int semesterId = semester.getId();
                        List<StudentAvgMarks> semesterMark = semesterGroups.get(iSemester);
                        List<String> subjCodes = semesterMark.stream().map(q -> q.getSubjectCode())
                                .collect(Collectors.toList());

                        //delete
                        marksService.deleteMarksBySemesterAndSubjectCodesAndStudentId(semesterId, subjCodes, studentId);
                        //import new mark
                        importedMark.addAll(semesterMark);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.writeLog(e);
                    error.put(stuRollNumber, e.getMessage());
                }
            }


            i = 1;
            importedMarkLoop:
            for (StudentAvgMarks item : importedMark) {
                try {
                    System.out.println(i++ + " - " + importedMark.size() + " mark");
                    SubjectEntity subjectEntity = allSubjects.stream()
                            .filter(q -> q.getId().equalsIgnoreCase(item.getSubjectCode()))
                            .findFirst().orElse(null);
                    RealSemesterEntity semesterEntity = allSemester.stream()
                            .filter(q -> q.getSemester().equalsIgnoreCase(item.getSemesterName()))
                            .findFirst().orElse(null);

                    if (semesterEntity == null) {
                        if (error.containsKey(studentRollNumber)) {
                            String errorMsg = error.get(studentRollNumber);
                            errorMsg += "\n " + item.getSubjectCode() + " - subject not exist!";
                        } else {
                            error.put(studentRollNumber, item.getSubjectCode() + " - subject not exist!");
                        }
                        continue importedMarkLoop;
                    }

                    String semesterName = semesterEntity.getSemester();

                    //kiểm tra xem subject có tồn tại không
                    if (subjectEntity == null) {
                        if (error.containsKey(studentRollNumber)) {
                            String errorMsg = error.get(studentRollNumber);
                            errorMsg += "\n " + item.getSubjectCode() + " - subject not exist!";
                        } else {
                            error.put(studentRollNumber, item.getSubjectCode() + " - subject not exist!");
                        }
                        continue importedMarkLoop;
                    }
                    //tạo Mark mới
                    MarksEntity mark = new MarksEntity();

                    MarkComponentEntity markComponentEntity =
                            markComponentService.getMarkComponentByName(markComponentName);

                    String subjectMarkComponentName = subjectEntity.getId() + "_" + markComponentName;
                    SubjectMarkComponentEntity subjectMarkComponentEntity =
                            subjectMarkComponentService.findSubjectMarkComponentByNameAndSubjectCd(markComponentName, subjectEntity.getId());

                    //set subjectMarkComponent
                    if (subjectMarkComponentEntity != null) {
                        mark.setSubjectMarkComponentId(subjectMarkComponentEntity);
                    } else {
                        subjectMarkComponentEntity = new SubjectMarkComponentEntity();
                        subjectMarkComponentEntity.setMarkComponentId(markComponentEntity);
                        subjectMarkComponentEntity.setName(subjectMarkComponentName);
                        subjectMarkComponentEntity.setPercentWeight(0.0);
                        subjectMarkComponentEntity.setSubjectId(subjectEntity);
                        subjectMarkComponentEntity = subjectMarkComponentService.createSubjectMarkComponent(subjectMarkComponentEntity);
                        mark.setSubjectMarkComponentId(subjectMarkComponentEntity);
                    }
                    //set student
                    mark.setStudentId(studentEntity);
                    //set Semester
                    mark.setSemesterId(semesterEntity);
                    //set Course, tạo Course nếu chưa tồn tại

                    CourseEntity courseEntity = courseService
                            .findCourseBySemesterAndSubjectCode(semesterName, subjectEntity.getId());
                    if (courseEntity != null) {
                        mark.setCourseId(courseEntity);
                    } else {
                        courseEntity = new CourseEntity();
                        courseEntity.setSemester(semesterName);
                        courseEntity.setSubjectCode(subjectEntity.getId());
                        courseEntity = courseService.createCourse(courseEntity);
                        mark.setCourseId(courseEntity);
                    }

                    //set mark
                    String passOrFail = item.isPassed() == true ?
                            Enums.MarkStatus.PASSED.getValue() : Enums.MarkStatus.FAIL.getValue();
                    mark.setAverageMark(item.getAvgMark());
                    mark.setStatus(passOrFail);
                    mark.setIsActivated(true);
                    mark.setEnabled(true);

                    //bỏ vào list để import
                    marksEntities.add(mark);
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.writeLog(e);
                }
            }


            //batch insert mark
            marksService.createMarks(marksEntities);
            request.getSession().setAttribute(Enums.SynchronizeFAP.SINGLE_STUDENT_ERROR_lIST.getValue(), error);
            jsonObject.addProperty("success", true);
            jsonObject.addProperty("message", "Đồng bộ hóa điểm sinh viên thành công !");

        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", e.getMessage());
        }

        return jsonObject;
    }


    @RequestMapping(value = "/synchronizeMarkSingleStudentFromFAPPage")
    public ModelAndView goSyncMarkSingleStudentFromFAPPage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView mav = new ModelAndView("importMarks1StudentFromFAP");
        mav.addObject("title", "Đồng bộ điểm từ FAP cho 1 sinh viên");

        return mav;
    }


    //danh sách lỗi khi đồng bộ điểm từ FAP cho 1 sinh viên
    @RequestMapping("/getFailSynchronizeMarksOfSingleStudentFromFAP")
    @ResponseBody
    public JsonObject getFailSynMarksSingleStudentFromFAP(@RequestParam Map<String, String> params, HttpServletRequest request) {
        JsonObject obj = new JsonObject();

        //lấy ra danh sách những sinh viên bị lỗi khi đẩy điểm từ bên FAP qua
        // <RollNumber, Error>
        HashMap<String, String> studentList = (HashMap<String, String>) request.getSession()
                .getAttribute(Enums.SynchronizeFAP.SINGLE_STUDENT_ERROR_lIST.getValue());
        String type = params.get("type");

//        final String sSearch = params.get("sSearch");

//        int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
//        int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
//        boolean isGraduate = Boolean.parseBoolean(params.get("boolean"));

        try {
            // RollNumber
            List<List<String>> data = new ArrayList<>();
            if (studentList != null) {
                for (String rollNumber :
                        studentList.keySet()) {
                    List<String> tempData = new ArrayList<>();
                    tempData.add(rollNumber);
                    String error = studentList.get(rollNumber);
                    tempData.add(error);
                    data.add(tempData);
                }
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(data);
            obj.add("aaData", aaData);
//            obj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
        }

        return obj;
    }

    //đồng bộ điểm studying (trạng thái là đang học) từ FAP
    @RequestMapping("/importSynchronizeStudyingMarksFromFAP")
    @ResponseBody
    public JsonObject getFAPStudyingMarksSynchronize(@RequestParam Map<String, String> params, HttpServletRequest request,
                                                     @RequestParam("semesterId") int semesterId) {
        JsonObject jsonObject = new JsonObject();

//        if (!Ultilities.checkUserAuthorize2(request, "/markPage")) {
//            jsonObj.addProperty("success", false);
//            jsonObj.addProperty("message", "Không đủ quyền hạn để thực hiện");
//            return jsonObj;
//        }


        MarksServiceImpl marksService = new MarksServiceImpl();
        Ultilities2ServiceImpl ult2 = new Ultilities2ServiceImpl();
        StudentServiceImpl studentService = new StudentServiceImpl();
        SubjectServiceImpl subjectService = new SubjectServiceImpl();
        RealSemesterServiceImpl semesterService = new RealSemesterServiceImpl();
        MarkComponentServiceImpl markComponentService = new MarkComponentServiceImpl();
        SubjectCurriculumServiceImpl subjectCurriculumService = new SubjectCurriculumServiceImpl();
        SubjectMarkComponentServiceImpl subjectMarkComponentService = new SubjectMarkComponentServiceImpl();
        CourseServiceImpl courseService = new CourseServiceImpl();


        //backup
//        if (backup) {
//            ult2.backupCapstoneDB();
//        }

        HashMap<String, String> error = new HashMap<>();
        HashMap<String, List<StudentStudyingMarks>> tempMap = new HashMap<>();
        HashMap<StudentEntity, List<StudentStudyingMarks>> map = new HashMap<>();
        RealSemesterEntity semesterEntity = semesterService.findSemesterById(semesterId);
        String semesterName = semesterEntity.getSemester();

        List<StudentStudyingMarks> fapstudyingMarks = ult2.getFAPStudyingMarkBySemester(semesterName);
        List<SubjectEntity> allSubjects = subjectService.getAllSubjects();
        String markComponentName = Enums.MarkComponent.AVERAGE.getValue();
        List<MarksEntity> updateList = new ArrayList<>();


        List<MarksEntity> marksEntities = new ArrayList<MarksEntity>();


        if (fapstudyingMarks == null) {
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", "Đã xảy ra lỗi khi lấy dữ liệu từ FAP!");
            return jsonObject;
        } else if (fapstudyingMarks.isEmpty()) {
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", "Điểm của " + semesterName + " bên FAP chưa có điểm");
            return jsonObject;
        }


        try {
            List<StudentEntity> students = studentService.findStudentsBySemesterId(semesterId);

            //group by student rollnumber
            for (StudentStudyingMarks item : fapstudyingMarks) {
                if (tempMap.containsKey(item.getRollNumber())) {
                    tempMap.get(item.getRollNumber()).add(item);
                } else {
                    List<StudentStudyingMarks> marks = new ArrayList<>();
                    marks.add(item);
                    tempMap.put(item.getRollNumber(), marks);
                }
            }

            //turn to map<StudentEntity, List<Marks>> for easier to process
            for (String rollNumber : tempMap.keySet()) {
                StudentEntity student = students.stream().filter(q -> q.getRollNumber().equalsIgnoreCase(rollNumber))
                        .findFirst().orElse(null);
                //spring cache lại data mỗi khi lấy lên
//                studentService.refresh(student);
                if (student != null) {
                    List<StudentStudyingMarks> marks = tempMap.get(rollNumber);
                    map.put(student, marks);
                } else {
                    error.put(rollNumber, "student rollNumber not exist");
                }
            }

            List<RealSemesterEntity> allSemester = Global.getSortedList();
            List<Integer> semestersToPreCurrentSelected = new ArrayList<>();
            for (RealSemesterEntity r : allSemester) {
                if (r.getId() == semesterId)
                    break;
                else
                    semestersToPreCurrentSelected.add(r.getId());
            }

            int i = 1;

            dataLoop:
            for (StudentEntity studentEntity : map.keySet()) {
                try {
                    System.out.println("process " + i + " - " + map.size());
                    if (studentEntity.getRollNumber().equalsIgnoreCase("SE61428")) {
                        System.out.println("bug oi");
                    }
                    String rollNumber = studentEntity.getRollNumber();
                    int studentId = studentEntity.getId();
                    List<StudentStatusEntity> statuses = new ArrayList<>(studentEntity.getStudentStatusEntityList());

                    StudentStatusEntity status = statuses.stream().filter(q -> q.getSemesterId().getId() == semesterId).findFirst().orElse(null);
                    if (status == null) {
                        error.put(rollNumber, semesterName + " status not exist!");
                        continue dataLoop;
                    }
                    String term = status.getTerm();
                    Integer studentTerm = null;
                    if (term.contains("+")) {
                        //lấy số kì ra (vd: term = "9+")
                        Pattern p = Pattern.compile("(\\d)");
                        Matcher m = p.matcher(term);   // get a matcher object

                        m.find();
                        String token = m.group(0); //group 0 is always the entire match
                        try {
                            double tempTerm = Double.parseDouble(token);
                            studentTerm = (int) tempTerm;

                        } catch (NumberFormatException ex) {
                            System.out.println(ex.getMessage());
                            error.put(rollNumber, token + " term is not a number");
                            continue dataLoop;
                        }
                    } else {
                        if (term.contains("ENG6")) {
                            studentTerm = 0;
                        } else if (term.contains("ENG5")) {
                            studentTerm = -1;
                        } else if (term.contains("ENG4")) {
                            studentTerm = -2;
                        } else if (term.contains("ENG3")) {
                            studentTerm = -3;
                        } else if (term.contains("ENG2")) {
                            studentTerm = -4;
                        }
                        //dành cho những sinh viên đi ojt lâu hơn 1 kì -> giả sử là 6', 7'
                        else if (term.contains("'")) {
                            String[] splitList = term.split("'");
                            try {
                                Integer tempTerm = Integer.parseInt(splitList[0]);
                                studentTerm = tempTerm;
                            } catch (NumberFormatException ex) {
                                System.out.printf(ex.getMessage());
                                error.put(rollNumber, term + " term is not a number");
                            }
                        }
                        if (studentTerm == null) {
                            try {
                                double tempTerm = Double.parseDouble(term);
                                studentTerm = (int) tempTerm;
                            } catch (NumberFormatException ex) {
                                System.out.println(ex.getMessage());
                                error.put(rollNumber, term + " term is not a number");
                                continue dataLoop;
                            }
                        }
                    }

                    //lấy ra những môn học sẽ học theo khung chương trình của kì được chọn
                    List<SubjectCurriculumEntity> subjectCurList = subjectCurriculumService
                            .getSubjectCurriculumByStudentByTerm(studentId, studentTerm);

                    List<String> subjCodeList = subjectCurList.stream().map(q -> q.getSubjectId().getId())
                            .collect(Collectors.toList());

                    List<StudentStudyingMarks> studyingFAP = map.get(studentEntity);
                    List<MarksEntity> hasLearned = marksService.getAllMarksByStudent(studentId);
                    List<MarksEntity> hasLearnedThisSemester = hasLearned.stream()
                            .filter(q -> q.getSemesterId().getId() == semesterId).collect(Collectors.toList());

                    List<StudentAvgMarks> studyingMarkFAPThisSemester = studyingFAP.stream()
                            .map(q -> new StudentAvgMarks(q.getRollNumber(), -1.0,
                                    false, q.getSubjectCode(), q.getSemesterName()))
                            .collect(Collectors.toList());


//                    List<MarksEntity> hasLearned = marksService.getStudentMarksById(studentId);
//                    List<MarksEntity> hasLearnedThisSemester = hasLearned.stream()
//                            .filter(q -> q.getSemesterId().getId() == semesterId)
//                            .collect(Collectors.toList());


                    List<MarkModelExcel> notStartMarks = getNotStartMarks(studentEntity, studentId, marksService, semestersToPreCurrentSelected,
                            semesterName, semesterId, studyingMarkFAPThisSemester, subjCodeList);

                    List<MarkModelExcel> markFAPThisSemester = new ArrayList<>();

                    for (StudentStudyingMarks item : studyingFAP) {

                        String subjectCode = item.getSubjectCode();
                        MarksEntity m = hasLearnedThisSemester.stream()
                                .filter(q -> q.getSubjectMarkComponentId().getSubjectId().getId().equalsIgnoreCase(subjectCode))
                                .findFirst().orElse(null);
                        if(m != null){
                            if(m.getStatus().equalsIgnoreCase(Enums.MarkStatus.NOT_START.getValue())){
                                m.setStatus(Enums.MarkStatus.STUDYING.getValue());
                                updateList.add(m);
                                marksService.updateMark(m);

                                hasLearnedThisSemester.remove(m);
                            }
                        }else{
                            //nếu kiếm không thấy thì import là studying
                            MarkModelExcel markModelExcel = new MarkModelExcel(-1, semesterName, subjectCode, Enums.MarkStatus.STUDYING.getValue());
                            markFAPThisSemester.add(markModelExcel);
                        }
                    }


                    List<MarkModelExcel> importedMark = new ArrayList<>();
                    importedMark.addAll(markFAPThisSemester);
                    importedMark.addAll(notStartMarks);

                    importedMarkLoop:
                    for (MarkModelExcel item : importedMark) {
                        SubjectEntity subjectEntity = allSubjects.stream()
                                .filter(q -> q.getId().equalsIgnoreCase(item.getSubjectId()))
                                .findFirst().orElse(null);

                        //kiểm tra xem subject có tồn tại không
                        if (subjectEntity == null) {
                            error.put(rollNumber, item.getSubjectId() + " - subject not exist!");
                            continue importedMarkLoop;
                        }
                        //tạo Mark mới
                        MarksEntity mark = new MarksEntity();

                        MarkComponentEntity markComponentEntity =
                                markComponentService.getMarkComponentByName(markComponentName);

                        String subjectMarkComponentName = subjectEntity.getId() + "_" + markComponentName;
                        SubjectMarkComponentEntity subjectMarkComponentEntity =
                                subjectMarkComponentService.findSubjectMarkComponentByNameAndSubjectCd(markComponentName, subjectEntity.getId());

                        //set subjectMarkComponent
                        if (subjectMarkComponentEntity != null) {
                            mark.setSubjectMarkComponentId(subjectMarkComponentEntity);
                        } else {
                            subjectMarkComponentEntity = new SubjectMarkComponentEntity();
                            subjectMarkComponentEntity.setMarkComponentId(markComponentEntity);
                            subjectMarkComponentEntity.setName(subjectMarkComponentName);
                            subjectMarkComponentEntity.setPercentWeight(0.0);
                            subjectMarkComponentEntity.setSubjectId(subjectEntity);
                            subjectMarkComponentEntity = subjectMarkComponentService.createSubjectMarkComponent(subjectMarkComponentEntity);
                            mark.setSubjectMarkComponentId(subjectMarkComponentEntity);
                        }
                        //set student
                        mark.setStudentId(studentEntity);
                        //set Semester
                        mark.setSemesterId(semesterEntity);
                        //set Course, tạo Course nếu chưa tồn tại

                        CourseEntity courseEntity = courseService
                                .findCourseBySemesterAndSubjectCode(semesterName, subjectEntity.getId());
                        if (courseEntity != null) {
                            mark.setCourseId(courseEntity);
                        } else {
                            courseEntity = new CourseEntity();
                            courseEntity.setSemester(semesterName);
                            courseEntity.setSubjectCode(subjectEntity.getId());
                            courseEntity = courseService.createCourse(courseEntity);
                            mark.setCourseId(courseEntity);
                        }

                        //set mark
                        mark.setAverageMark(item.getAverage());
                        mark.setStatus(item.getStatus());
                        mark.setIsActivated(true);
                        mark.setEnabled(true);

                        //bỏ vào list để import
                        marksEntities.add(mark);
                    }
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.writeLog(e);
                    error.put(studentEntity.getRollNumber(), e.getMessage());
                }
            }

            //batch insert mark
            marksService.createMarks(marksEntities);
            request.getSession().setAttribute(Enums.SynchronizeFAP.ERROR_STUDYING_lIST.getValue(), error);
            jsonObject.addProperty("success", true);
            jsonObject.addProperty("message", "Đồng bộ hóa điểm sinh viên thành công !");

        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", e.getMessage());
        }

        return jsonObject;
    }

    @RequestMapping(value = "/synchronizeStudyingMarksFromFAPPage")
    public ModelAndView goSynStudyingMarksFromFAPPage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView mav = new ModelAndView("importStudyingMarkFromFAP");
        mav.addObject("title", "Đồng bộ môn đang học cho sinh viên");

        RealSemesterServiceImpl semesterService = new RealSemesterServiceImpl();

        List<RealSemesterEntity> semesters = semesterService.getAllSemester();
        semesters = Ultilities.SortSemesters(semesters);
        semesters = semesters.stream().filter(s -> !s.getSemester().contains("N/A")).collect(Collectors.toList());
        semesters = Lists.reverse(semesters);

        mav.addObject("semesters", semesters);

        return mav;
    }


    //danh sách lỗi khi đồng bộ điểm từ FAP cho 1 sinh viên
    @RequestMapping("/getFailSynStudyingMarksFromFAP")
    @ResponseBody
    public JsonObject getFailSynStudyingMarksFromFAP(@RequestParam Map<String, String> params, HttpServletRequest request) {
        JsonObject obj = new JsonObject();

        //lấy ra danh sách những sinh viên bị lỗi khi đẩy điểm từ bên FAP qua
        // <RollNumber, Error>
        HashMap<String, String> studentList = (HashMap<String, String>) request.getSession()
                .getAttribute(Enums.SynchronizeFAP.ERROR_STUDYING_lIST.getValue());
        String type = params.get("type");

//        final String sSearch = params.get("sSearch");

//        int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
//        int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
//        boolean isGraduate = Boolean.parseBoolean(params.get("boolean"));

        try {
            // RollNumber
            List<List<String>> data = new ArrayList<>();
            if (studentList != null) {
                for (String rollNumber :
                        studentList.keySet()) {
                    List<String> tempData = new ArrayList<>();
                    tempData.add(rollNumber);
                    String error = studentList.get(rollNumber);
                    tempData.add(error);
                    data.add(tempData);
                }
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(data);
            obj.add("aaData", aaData);
//            obj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
        }

        return obj;
    }


}
