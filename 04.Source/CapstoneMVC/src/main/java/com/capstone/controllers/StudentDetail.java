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
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class StudentDetail {

    IStudentService studentService = new StudentServiceImpl();

    @RequestMapping("/studentDetail")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("StudentDetail");
        view.addObject("title", "Danh sách sinh viên nợ môn");
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
                students = studentService.findAllStudents();
            } else {
                students = new ArrayList<>();
                students.add(studentService.findStudentByRollNumber(principal.getUser().getStudentRollNumber()));
            }

            final String finalSearchValue = searchValue;
            List<StudentEntity> studentList = students.stream()
                    .filter(c -> Ultilities.containsIgnoreCase(c.getRollNumber(), finalSearchValue)
                            || Ultilities.containsIgnoreCase(c.getFullName(), finalSearchValue))
                    .collect(Collectors.toList());

            List<SelectItem> itemList = new ArrayList<>();
            for (StudentEntity student : studentList) {
                SelectItem item = new SelectItem();
                item.setValue(student.getId() + "");
                item.setText(student.getRollNumber() + " - " + student.getFullName());

                itemList.add(item);
            }

            JsonArray result = (JsonArray) new Gson().toJsonTree(itemList, new TypeToken<List<SelectItem>>() {
            }.getType());

            jsonObj.addProperty("success", true);
            jsonObj.add("items", result);
        } catch (Exception e) {
            Logger.writeLog(e);
            jsonObj.addProperty("success", false);
        }

        return jsonObj;
    }

    @RequestMapping("/getStudentDetail")
    @ResponseBody
    public JsonObject GetStudentFail(@RequestParam Map<String, String> params) {
        JsonObject data = new JsonObject();

        int studentId = Integer.parseInt(params.get("stuId"));

        try {
            List<List<String>> set2 = processFailed(studentId);

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

    public List<List<String>> processFailed(int studentId) {
        IMarksService marksService = new MarksServiceImpl();

        List<MarksEntity> list = marksService.getAllMarksByStudentAndSubject(studentId, "0", "0");

        List<MarksEntity> newlist = Ultilities.FilterStudentsOnlyPassAndFail(list);
        List<MarksEntity> resultList = Ultilities.FilterListFailStudent(newlist);

        // subjects in student curriculum
        StudentEntity stu = studentService.findStudentById(studentId);
        List<String> l = Ultilities.getStudentCurriculumSubjects(stu);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = emf.createEntityManager();

        //remove studying marks from fail list
        List<MarksEntity> studyingList = marksService.getMarksByStudentIdAndStatus(studentId, "studying");
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
                    for (SubjectEntity s : replacers) {
                        TypedQuery<MarksEntity> queryCheckPass = em.createQuery("SELECT a FROM MarksEntity a WHERE a.active = TRUE AND a.studentId.id = :id AND a.subjectMarkComponentId.subjectId.id = :sub AND (LOWER(a.status) LIKE '%pass%' OR LOWER(a.status) LIKE 'exempt')", MarksEntity.class);
                        queryCheckPass.setParameter("id", studentId);
                        queryCheckPass.setParameter("sub", s.getId());
                        List<MarksEntity> rep = queryCheckPass.getResultList();
                        if (!rep.isEmpty()) {
                            iterator.remove();
                            break;
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

    @RequestMapping("/getStudentCurrentCourse")
    @ResponseBody
    public JsonObject GetStudentCurrentCourse(@RequestParam Map<String, String> params) {
        JsonObject jsonObject = new JsonObject();

        int stuId = Integer.parseInt(params.get("stuId"));

        try {
            List<List<String>> result = processCurrent(stuId);

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

    public List<List<String>> processCurrent(int stuId) {
        IMarksService marksService = new MarksServiceImpl();

//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
//        EntityManager em = emf.createEntityManager();

        List<MarksEntity> list = marksService.getMarksByStudentIdAndStatus(stuId, "studying");

        // Check students score if exist remove
//        if (!list.isEmpty()) {
//                StudentEntity stu = studentService.findStudentById(stuId);
//                List<String> l = Ultilities.getStudentCurriculumSubjects(stu);

//            List<String> curriculumSubjects = new ArrayList<>();
//            list.forEach(c -> {
//                if (!curriculumSubjects.contains(c.getSubjectMarkComponentId().getSubjectId().getId())) {
//                    curriculumSubjects.add(c.getSubjectMarkComponentId().getSubjectId().getId());
//                }
//            });
//                TypedQuery<MarksEntity> query2 = em.createQuery("SELECT a FROM MarksEntity a WHERE a.studentId.id = :id AND a.subjectMarkComponentId.subjectId.id IN :list", MarksEntity.class);
//                query2.setParameter("id", stuId);
//                query2.setParameter("list", curriculumSubjects);
//                List<MarksEntity> existList = Ultilities.FilterStudentsOnlyPassAndFail(query2.getResultList());
//                Iterator<MarksEntity> iterator = list.iterator();
//                while (iterator.hasNext()) {
//                    MarksEntity cur = iterator.next();
//                    if (existList.stream().anyMatch(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(cur.getSubjectMarkComponentId().getSubjectId().getId()))) {
//                        iterator.remove();
//                    }  else {
//                        SubjectEntity su = cur.getSubjectMarkComponentId().getSubjectId();
//                        if (!l.stream().anyMatch(a -> a.equals(su.getId()))) {
//                            iterator.remove();
//                        }
//                    }
//                }
//        }

        List<List<String>> displayList = new ArrayList<>();
        for (MarksEntity sc : list) {
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

        try {
            List<List<String>> result = processNext(stuId, false, false);

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

        try {
            List<List<String>> result = processNext(stuId, true, true);

            Suggestion suggestion = processSuggestion(stuId);
            List<List<String>> result2 = suggestion.getData();

            List<String> brea = new ArrayList<>();
            brea.add("break");
            brea.add("");

            int index = result2.indexOf(brea);
            if (index > -1) {
                if (suggestion.isDuchitieu()) {
                    result2 = result2.subList(index + 1, result2.size());
                } else {
                    result2 = result2.subList(0, index);
                }
            }

            for (List<String> r : result2) {
                result.add(r);
            }

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

    public List<List<String>> processNext(int stuId, boolean checkPrequisite, boolean getFailPrequisiteList) {
        IStudentService studentService = new StudentServiceImpl();
        ISubjectService subjectService = new SubjectServiceImpl();

        StudentEntity student = studentService.findStudentById(stuId);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = emf.createEntityManager();

        String queryStr = "SELECT sc FROM DocumentStudentEntity ds, SubjectCurriculumEntity sc" +
                " WHERE ds.studentId.id = :studentId AND ds.createdDate =" +
                " (SELECT MAX(ds1.createdDate) FROM DocumentStudentEntity ds1 WHERE ds1.studentId.id = :studentId) " +
                " AND ds.curriculumId.id = sc.curriculumId.id" +
                " AND sc.termNumber = :term";
        TypedQuery<SubjectCurriculumEntity> query = em.createQuery(queryStr, SubjectCurriculumEntity.class);
        query.setParameter("studentId", stuId);
        query.setParameter("term", student.getTerm() + 1);

        List<SubjectCurriculumEntity> list = query.getResultList();

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
                TypedQuery<MarksEntity> query2 = em.createQuery("SELECT a FROM MarksEntity a WHERE a.active = true and a.studentId.id = :id AND a.subjectMarkComponentId.subjectId.id IN :list", MarksEntity.class);
                query2.setParameter("id", stuId);
                query2.setParameter("list", curriculumSubjects);
                List<MarksEntity> existList = Ultilities.FilterStudentsOnlyPassAndFail(query2.getResultList());
                Iterator<SubjectCurriculumEntity> iterator = list.iterator();
                while (iterator.hasNext()) {
                    SubjectCurriculumEntity cur = iterator.next();
                    if (existList.stream().anyMatch(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(cur.getSubjectId().getId()))) {
                        iterator.remove();
                    } else {
                        boolean failed = false;
                        if (checkPrequisite) {
//                                     check prequisite
                            List<String> processedData = new ArrayList<>();
                            SubjectEntity entity = cur.getSubjectId();
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
                            if (!processedData.isEmpty()) {
                                String str = "SELECT p FROM MarksEntity p WHERE p.active = true and p.studentId.id = :id and p.subjectMarkComponentId.subjectId.id IN :sList";
                                TypedQuery<MarksEntity> prequisiteQuery;
                                prequisiteQuery = em.createQuery(str, MarksEntity.class);
                                prequisiteQuery.setParameter("sList", processedData);
                                prequisiteQuery.setParameter("id", stuId);

                                List<MarksEntity> list2 = prequisiteQuery.getResultList();
                                failed = Ultilities.HasFailedPrequisitesOfOneStudent(list2, cur.getSubjectId().getPrequisiteEntity());
                            }
                        }

                        if (failed) {
                            failedPrequisiteList.add(cur.getSubjectId());
                            iterator.remove();
                        } else {
                            List<SubjectEntity> replacers = cur.getSubjectId().getSubjectEntityList();
                            for (SubjectEntity s : replacers) {
                                TypedQuery<MarksEntity> queryCheckPass = em.createQuery("SELECT a FROM MarksEntity a WHERE a.active = true AND a.studentId.id = :id AND a.subjectMarkComponentId.subjectId.id = :sub AND (LOWER(a.status) LIKE '%pass%' OR LOWER(a.status) LIKE 'exempt')", MarksEntity.class);
                                queryCheckPass.setParameter("id", stuId);
                                queryCheckPass.setParameter("sub", s.getId());
                                List<MarksEntity> rep = queryCheckPass.getResultList();
                                if (!rep.isEmpty()) {
                                    iterator.remove();
                                    break;
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

    @RequestMapping("/getStudentNextCourseSuggestion")
    @ResponseBody
    public JsonObject GetStudentNextCourseSuggestion(@RequestParam Map<String, String> params) {
        JsonObject data = new JsonObject();

        int stuId = Integer.parseInt(params.get("stuId"));

        try {
            Suggestion suggestion = processSuggestion(stuId);
            List<List<String>> result = suggestion.getData();

            List<String> brea = new ArrayList<>();
            brea.add("break");
            brea.add("");

            int index = result.indexOf(brea);
            if (index > -1) {
                if (suggestion.isDuchitieu()) {
                    result = result.subList(0, index);
                } else {
                    result = result.subList(index + 1, result.size());
                }
            }

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

    public Suggestion processSuggestion(int stuId) {
        Suggestion suggestion = new Suggestion();

        IMarksService marksService = new MarksServiceImpl();

        IStudentService studentService = new StudentServiceImpl();
        ISubjectService subjectService = new SubjectServiceImpl();

        StudentEntity student = studentService.findStudentById(stuId);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = emf.createEntityManager();

        /*-----------------------------------Fail Course--------------------------------------------------*/
        List<String> l2 = Ultilities.getStudentCurriculumSubjects(student);

        List<MarksEntity> list = marksService.getAllMarksByStudentAndSubject(stuId, "0", "0");
        List<MarksEntity> newlist = Ultilities.FilterStudentsOnlyPassAndFail(list);
        List<MarksEntity> resultList = Ultilities.FilterListFailStudent(newlist);
        List<SubjectEntity> failSubjects = new ArrayList<>();

        List<MarksEntity> list2 = marksService.getMarksByStudentIdAndStatus(stuId, "studying");
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
                    for (SubjectEntity s : replacers) {
                        TypedQuery<MarksEntity> queryCheckPass = em.createQuery("SELECT a FROM MarksEntity a WHERE a.active = true and a.studentId.id = :id AND a.subjectMarkComponentId.subjectId.id = :sub AND (LOWER(a.status) LIKE '%pass%' OR LOWER(a.status) LIKE 'exempt')", MarksEntity.class);
                        queryCheckPass.setParameter("id", stuId);
                        queryCheckPass.setParameter("sub", s.getId());
                        List<MarksEntity> rep = queryCheckPass.getResultList();
                        if (!rep.isEmpty()) {
                            iterator.remove();
                            break;
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
        String queryStr = "SELECT sc FROM DocumentStudentEntity ds, SubjectCurriculumEntity sc" +
                " WHERE ds.studentId.id = :studentId AND ds.createdDate =" +
                " (SELECT MAX(ds1.createdDate) FROM DocumentStudentEntity ds1 WHERE ds1.studentId.id = :studentId) " +
                " AND ds.curriculumId.id = sc.curriculumId.id" +
                " AND sc.termNumber = :term";
        TypedQuery<SubjectCurriculumEntity> query = em.createQuery(queryStr, SubjectCurriculumEntity.class);
        query.setParameter("studentId", stuId);
        query.setParameter("term", student.getTerm() + 1);

        List<SubjectCurriculumEntity> listNextCurri = query.getResultList();
        List<SubjectEntity> nextSubjects = new ArrayList<>();
        listNextCurri.forEach(c -> {
            if (!nextSubjects.contains(c.getSubjectId())) {
                nextSubjects.add(c.getSubjectId());
            }
        });

        // check ojt and syb pass or not
        DocumentStudentEntity doc = Ultilities.getStudentLatestDocument(student);
        if (doc != null) {
            ProgramEntity program = doc.getCurriculumId().getProgramId();
            for (SubjectEntity subject : nextSubjects) {
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
                    List<SubjectCurriculumEntity> curSubs = doc.getCurriculumId().getSubjectCurriculumEntityList();
                    int total = 0; // total total tin chi
                    for (SubjectCurriculumEntity m : curSubs) {
                        Integer num = m.getSubjectId().getCredits();
                        total += (num == null ? 0 : num);
                    }
                    List<MarksEntity> stuSubs = student.getMarksEntityList();
                    int tongtinchi = 0;
                    for (MarksEntity mark : stuSubs) {
                        if (mark.getIsActivated() && (mark.getStatus().toLowerCase().contains("pass") || mark.getStatus().toLowerCase().contains("exempt"))) {
                            Integer tmp = mark.getSubjectMarkComponentId().getSubjectId().getCredits();
                            tongtinchi += (tmp == null ? 0 : tmp);
                        }
                    }
                    // tính tổng tín chỉ
                    List<List<String>> parent = new ArrayList<>();
                    float required = (float) ((total * 1.0) * (percent * 1.0) / 100);
                    if (tongtinchi > required) {
                        suggestion.setDuchitieu(true);
                    } else {
                        suggestion.setDuchitieu(false);
                    }

                    if (subject.getType() == SubjectTypeEnum.OJT.getId()) {
                        for (SubjectEntity s : nextSubjects) {
                            List<String> tmp = new ArrayList<>();
                            if (s.getType() == SubjectTypeEnum.OJT.getId() || s.getId().toLowerCase().contains("syb")) {
                                tmp.add(s.getId());
                                tmp.add(s.getName());
                                parent.add(tmp);
                            }
                        }
                    } else if (subject.getType() == SubjectTypeEnum.Capstone.getId()) {
                        for (SubjectEntity s : nextSubjects) {
                            List<String> tmp = new ArrayList<>();
                            if (s.getType() == SubjectTypeEnum.Capstone.getId()) {
                                tmp.add(s.getId());
                                tmp.add(s.getName());
                                parent.add(tmp);
                            }
                        }
                    }

                    List<String> tmp = new ArrayList<>();
                    tmp.add("break");
                    tmp.add("");
                    parent.add(tmp);

                    suggestion.setData(parent);
                }
            }
        } else {
            System.out.println("Sinh viên " + student.getRollNumber() + " không có document!");
        }

        // ------------------------------------------------------------------------------------------

        List<String> curriculumSubjects = new ArrayList<>();
        for (SubjectEntity next : nextSubjects) {
            if (!curriculumSubjects.contains(next.getId())) curriculumSubjects.add(next.getId());
        }
        if (!curriculumSubjects.isEmpty()) {
            TypedQuery<MarksEntity> query2 = em.createQuery("SELECT a FROM MarksEntity a WHERE a.active = true and a.studentId.id = :id AND a.subjectMarkComponentId.subjectId.id IN :list", MarksEntity.class);
            query2.setParameter("id", stuId);
            query2.setParameter("list", curriculumSubjects);
            List<MarksEntity> existList = Ultilities.FilterStudentsOnlyPassAndFail(query2.getResultList());
            Iterator<SubjectEntity> iterator3 = nextSubjects.iterator();
            while (iterator3.hasNext()) {
                SubjectEntity entity = iterator3.next();
                if (existList.stream().anyMatch(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(entity.getId()))) {
                    iterator3.remove();
                } else {
                    // check prequisite
                    List<String> processedData = new ArrayList<>();
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
                    }
                    boolean failed = false;
                    if (!processedData.isEmpty()) {
                        String str = "SELECT p FROM MarksEntity p WHERE p.active = true and p.studentId.id = :id and p.subjectMarkComponentId.subjectId.id IN :sList";
                        TypedQuery<MarksEntity> prequisiteQuery;
                        prequisiteQuery = em.createQuery(str, MarksEntity.class);
                        prequisiteQuery.setParameter("sList", processedData);
                        prequisiteQuery.setParameter("id", stuId);

                        List<MarksEntity> list3 = prequisiteQuery.getResultList();
                        failed = Ultilities.HasFailedPrequisitesOfOneStudent(list3, entity.getPrequisiteEntity());
                    }

                    if (failed) {
                        iterator3.remove();
                    } else {
                        List<SubjectEntity> replacers = entity.getSubjectEntityList();
                        for (SubjectEntity s : replacers) {
                            TypedQuery<MarksEntity> queryCheckPass = em.createQuery("SELECT a FROM MarksEntity a WHERE a.active = true and a.studentId.id = :id AND a.subjectMarkComponentId.subjectId.id = :sub AND (LOWER(a.status) LIKE '%pass%' OR LOWER(a.status) LIKE 'exempt')", MarksEntity.class);
                            queryCheckPass.setParameter("id", stuId);
                            queryCheckPass.setParameter("sub", s.getId());
                            List<MarksEntity> rep = queryCheckPass.getResultList();
                            if (!rep.isEmpty()) {
                                iterator3.remove();
                                break;
                            }
                        }
                    }
                }
            }
        }

            /*-------------------------------Chậm tiến độ------------------------------------------------*/
        List<MarksEntity> slowList = marksService.getMarksByStudentIdAndStatus(stuId, "start");
        List<SubjectEntity> slowSubjects = new ArrayList<>();
        Iterator<MarksEntity> iterator2 = slowList.iterator();
        while (iterator2.hasNext()) {
            boolean hasRemoved = false;
            MarksEntity mark = iterator2.next();
            List<SubjectEntity> replacers = mark.getSubjectMarkComponentId().getSubjectId().getSubjectEntityList();
            List<SubjectEntity> subs = mark.getSubjectMarkComponentId().getSubjectId().getSubjectEntityList1();
            for (SubjectEntity s : replacers) {
                TypedQuery<MarksEntity> queryCheckPass = em.createQuery("SELECT a FROM MarksEntity a WHERE a.active = true and a.studentId.id = :id AND a.subjectMarkComponentId.subjectId.id = :sub AND (LOWER(a.status) LIKE '%pass%' OR LOWER(a.status) LIKE 'exempt')", MarksEntity.class);
                queryCheckPass.setParameter("id", stuId);
                queryCheckPass.setParameter("sub", s.getId());
                List<MarksEntity> rep = queryCheckPass.getResultList();
                if (!rep.isEmpty()) {
                    if (!hasRemoved) {
                        hasRemoved = true;
                        iterator2.remove();
                        break;
                    }
                }
            }
            for (SubjectEntity s : subs) {
                TypedQuery<MarksEntity> queryCheckPass = em.createQuery("SELECT a FROM MarksEntity a WHERE a.active = true and a.studentId.id = :id AND a.subjectMarkComponentId.subjectId.id = :sub AND (LOWER(a.status) LIKE '%pass%' OR LOWER(a.status) LIKE '%exempt%' OR LOWER(a.status) LIKE '%fail%' OR LOWER(a.status) LIKE '%suspend%' OR LOWER(a.status) LIKE '%attend%')", MarksEntity.class);
                queryCheckPass.setParameter("id", stuId);
                queryCheckPass.setParameter("sub", s.getId());
                List<MarksEntity> rep = queryCheckPass.getResultList();
                if (!rep.isEmpty()) {
                    if (!hasRemoved) {
                        hasRemoved = true;
                        iterator2.remove();
                        break;
                    }
                } else {
                    List<SubjectEntity> replaceOfS = s.getSubjectEntityList();
                    for (SubjectEntity r : replaceOfS) {
                        TypedQuery<MarksEntity> queryCheckPass2 = em.createQuery("SELECT a FROM MarksEntity a WHERE a.active = true and a.studentId.id = :id AND a.subjectMarkComponentId.subjectId.id = :sub AND (LOWER(a.status) LIKE '%pass%' OR LOWER(a.status) LIKE '%exempt%' OR LOWER(a.status) LIKE '%fail%' OR LOWER(a.status) LIKE '%suspend%' OR LOWER(a.status) LIKE '%attend%')", MarksEntity.class);
                        queryCheckPass2.setParameter("id", stuId);
                        queryCheckPass2.setParameter("sub", r.getId());
                        List<MarksEntity> result = queryCheckPass2.getResultList();
                        if (!result.isEmpty()) {
                            if (!hasRemoved) {
                                hasRemoved = true;
                                iterator2.remove();
                                break;
                            }
                        }
                    }
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
//            sorted = sorted.stream().limit(7).collect(Collectors.toList());

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
            if (nextSubjects.size() > (7 - others.size())) {
                for (int i = 0; i < (7 - others.size()); i++) {
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

        if (suggestion.getData() == null) suggestion.setData(new ArrayList<>());
        for (List<String> d : others) {
            suggestion.getData().add(d);
        }

        return suggestion;
    }

    @RequestMapping("/getStudentNotStart")
    @ResponseBody
    public JsonObject GetStudentNotStart(@RequestParam Map<String, String> params) {
        JsonObject jsonObject = new JsonObject();

        int stuId = Integer.parseInt(params.get("stuId"));

        try {
            List<List<String>> result = processNotStart(stuId);

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

    public List<List<String>> processNotStart(int stuId) {
        IMarksService marksService = new MarksServiceImpl();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = emf.createEntityManager();

        List<MarksEntity> list = marksService.getMarksByStudentIdAndStatus(stuId, "start");
        Iterator<MarksEntity> iterator = list.iterator();
        while (iterator.hasNext()) {
            boolean hasRemoved = false;
            MarksEntity mark = iterator.next();
            List<SubjectEntity> replacers = mark.getSubjectMarkComponentId().getSubjectId().getSubjectEntityList();
            List<SubjectEntity> subs = mark.getSubjectMarkComponentId().getSubjectId().getSubjectEntityList1();
            for (SubjectEntity s : replacers) {
                TypedQuery<MarksEntity> queryCheckPass = em.createQuery("SELECT a FROM MarksEntity a WHERE a.active = true and a.studentId.id = :id AND a.subjectMarkComponentId.subjectId.id = :sub AND (LOWER(a.status) LIKE '%pass%' OR LOWER(a.status) LIKE 'exempt')", MarksEntity.class);
                queryCheckPass.setParameter("id", stuId);
                queryCheckPass.setParameter("sub", s.getId());
                List<MarksEntity> rep = queryCheckPass.getResultList();
                if (!rep.isEmpty()) {
                    if (!hasRemoved) {
                        hasRemoved = true;
                        iterator.remove();
                        break;
                    }
                }
            }
            for (SubjectEntity s : subs) {
                TypedQuery<MarksEntity> queryCheckPass = em.createQuery("SELECT a FROM MarksEntity a WHERE a.active = true and a.studentId.id = :id AND a.subjectMarkComponentId.subjectId.id = :sub AND (LOWER(a.status) LIKE '%pass%' OR LOWER(a.status) LIKE '%exempt%' OR LOWER(a.status) LIKE '%fail%' OR LOWER(a.status) LIKE '%suspend%' OR LOWER(a.status) LIKE '%attend%')", MarksEntity.class);
                queryCheckPass.setParameter("id", stuId);
                queryCheckPass.setParameter("sub", s.getId());
                List<MarksEntity> rep = queryCheckPass.getResultList();
                if (!rep.isEmpty()) {
                    if (!hasRemoved) {
                        hasRemoved = true;
                        iterator.remove();
                        break;
                    }
                } else {
                    List<SubjectEntity> replaceOfS = s.getSubjectEntityList();
                    for (SubjectEntity r : replaceOfS) {
                        TypedQuery<MarksEntity> queryCheckPass2 = em.createQuery("SELECT a FROM MarksEntity a WHERE a.active = true and a.studentId.id = :id AND a.subjectMarkComponentId.subjectId.id = :sub AND (LOWER(a.status) LIKE '%pass%' OR LOWER(a.status) LIKE '%exempt%' OR LOWER(a.status) LIKE '%fail%' OR LOWER(a.status) LIKE '%suspend%' OR LOWER(a.status) LIKE '%attend%')", MarksEntity.class);
                        queryCheckPass2.setParameter("id", stuId);
                        queryCheckPass2.setParameter("sub", r.getId());
                        List<MarksEntity> result = queryCheckPass2.getResultList();
                        if (!result.isEmpty()) {
                            if (!hasRemoved) {
                                hasRemoved = true;
                                iterator.remove();
                                break;
                            }
                        }
                    }
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

        return displayList;
    }
}
