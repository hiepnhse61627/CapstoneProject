package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.models.*;
import com.capstone.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.builder.CompareToBuilder;
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
    IMarksService service2 = new MarksServiceImpl();
    ISubjectService service3 = new SubjectServiceImpl();

    @RequestMapping("/studentDetail")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("StudentDetail");
        view.addObject("title", "Danh sách sinh viên nợ môn");
        view.addObject("students", studentService.findAllStudents());

        return view;
    }

    @RequestMapping("/getStudentList")
    @ResponseBody
    public JsonObject GetStudentList(@RequestParam String searchValue) {
        JsonObject jsonObj = new JsonObject();
        searchValue = searchValue == null ? "" : searchValue.trim();

        try {
            List<StudentEntity> studentList = studentService.findStudentsByValue(searchValue);
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
        IMarksService marksService = new MarksServiceImpl();

        int studentId = Integer.parseInt(params.get("stuId"));

        try {
            List<MarksEntity> list = marksService.getAllMarksByStudentAndSubject(studentId, "0", "0");

            List<MarksEntity> newlist = Ultilities.FilterStudentsOnlyPassAndFail(list);
            List<MarksEntity> resultList = Ultilities.FilterListFailStudent(newlist);

            //remove studying marks from fail list
            List<MarksEntity> studyingList = marksService.getMarksByStudentIdAndStatus(studentId, "studying");
            Iterator<MarksEntity> iterator = resultList.iterator();
            while(iterator.hasNext()) {
                MarksEntity current = iterator.next();
                if (studyingList.stream().anyMatch(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(current.getSubjectMarkComponentId().getSubjectId().getId()))) {
                    iterator.remove();
                }
            }

            List<MarksEntity> set2 = resultList.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());

            ArrayList<ArrayList<String>> parent = new ArrayList<>();
            if (!set2.isEmpty()) {
                set2.forEach(m -> {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(m.getSubjectMarkComponentId() == null ? "N/A" : m.getSubjectMarkComponentId().getSubjectId().getId());
                    tmp.add(m.getCourseId() == null ? "N/A" : m.getCourseId().getSemester());
                    tmp.add(m.getSemesterId() == null ? "N/A" : m.getSemesterId().getSemester());
                    tmp.add(String.valueOf(m.getAverageMark()));
                    tmp.add(m.getStatus());
                    parent.add(tmp);
                });
            }

            JsonArray result = (JsonArray) new Gson().toJsonTree(parent);

            data.addProperty("iTotalRecords", resultList.size());
            data.addProperty("iTotalDisplayRecords", resultList.size());
            data.add("aaData", result);
            data.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    @RequestMapping("/getStudentCurrentCourse")
    @ResponseBody
    public JsonObject GetStudentCurrentCourse(@RequestParam Map<String, String> params) {
        JsonObject jsonObject = new JsonObject();
        IMarksService marksService = new MarksServiceImpl();

        int stuId = Integer.parseInt(params.get("stuId"));

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            List<MarksEntity> list = marksService.getMarksByStudentIdAndStatus(stuId, "studying");

            List<MarksEntity> set2 = list.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());

            List<List<String>> displayList = new ArrayList<>();
            for (MarksEntity sc : set2) {
                List<String> row = new ArrayList<>();
                row.add(sc.getSubjectMarkComponentId().getSubjectId().getId());
                row.add(sc.getSubjectMarkComponentId().getSubjectId().getName());
                row.add(sc.getStatus());

                displayList.add(row);
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(displayList);

            jsonObject.addProperty("iTotalRecords", list.size());
            jsonObject.addProperty("iTotalDisplayRecords", list.size());
            jsonObject.add("aaData", aaData);
            jsonObject.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @RequestMapping("/getStudentNextCourse")
    @ResponseBody
    public JsonObject GetStudentNextCourse(@RequestParam Map<String, String> params) {
        IStudentService studentService = new StudentServiceImpl();
        JsonObject jsonObject = new JsonObject();

        int stuId = Integer.parseInt(params.get("stuId"));
        StudentEntity student = studentService.findStudentById(stuId);

        try {
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

            // Check students score if exist remove
            if (!list.isEmpty()) {
                List<String> curriculumSubjects = new ArrayList<>();
                list.forEach(c -> {
                    if (!curriculumSubjects.contains(c.getSubjectId().getId())) {
                        curriculumSubjects.add(c.getSubjectId().getId());
                    }
                });
                TypedQuery<MarksEntity> query2 = em.createQuery("SELECT a FROM MarksEntity a WHERE a.studentId.id = :id AND a.subjectMarkComponentId.subjectId.id IN :list", MarksEntity.class);
                query2.setParameter("id", stuId);
                query2.setParameter("list", curriculumSubjects);
                List<MarksEntity> existList = Ultilities.FilterStudentsOnlyPassAndFail(query2.getResultList());
                Iterator<SubjectCurriculumEntity> iterator = list.iterator();
                while(iterator.hasNext()) {
                    SubjectCurriculumEntity cur = iterator.next();
                    if (existList.stream().anyMatch(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(cur.getSubjectId().getId()))) {
                        iterator.remove();
                    }
                }
            }

            List<SubjectCurriculumEntity> set2 = list.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());

            List<List<String>> result = new ArrayList<>();
            for (SubjectCurriculumEntity sc : set2) {
                List<String> row = new ArrayList<>();
                row.add(sc.getSubjectId().getId());
                row.add(sc.getSubjectId().getName());

                result.add(row);
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

            jsonObject.addProperty("iTotalRecords", result.size());
            jsonObject.addProperty("iTotalDisplayRecords", result.size());
            jsonObject.add("aaData", aaData);
            jsonObject.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @RequestMapping("/getStudentNextCourseSuggestion")
    @ResponseBody
    public JsonObject GetStudentNextCourseSuggestion(@RequestParam Map<String, String> params) {
        JsonObject data = new JsonObject();
        IMarksService marksService = new MarksServiceImpl();

        IStudentService studentService = new StudentServiceImpl();

        int stuId = Integer.parseInt(params.get("stuId"));
        StudentEntity student = studentService.findStudentById(stuId);

        try {
            /*-----------------------------------Fail Course--------------------------------------------------*/
            List<MarksEntity> list = marksService.getAllMarksByStudentAndSubject(Integer.parseInt(params.get("stuId")), "0", "0");
            List<MarksEntity> newlist = Ultilities.FilterStudentsOnlyPassAndFail(list);
            List<MarksEntity> resultList = Ultilities.FilterListFailStudent(newlist);
            List<SubjectEntity> failSubjects = new ArrayList<>();
            resultList.forEach(c -> {
                if (!failSubjects.contains(c.getSubjectMarkComponentId().getSubjectId())) {
                    failSubjects.add(c.getSubjectMarkComponentId().getSubjectId());
                }
            });

            /*-------------------------------Get Next Course------------------------------------------------*/
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

            List<SubjectCurriculumEntity> listNextCurri = query.getResultList();
            List<SubjectEntity> nextSubjects = new ArrayList<>();
            listNextCurri.forEach(c -> {
                if (!nextSubjects.contains(c.getSubjectId())) {
                    nextSubjects.add(c.getSubjectId());
                }
            });

            /*-------------------------------Chậm tiến độ------------------------------------------------*/
            List<MarksEntity> slowList = marksService.getMarksByStudentIdAndStatus(stuId, "start");
            List<SubjectEntity> slowSubjects = new ArrayList<>();
            slowList.forEach(c -> {
                if (!slowSubjects.contains(c.getSubjectMarkComponentId().getSubjectId())) {
                    slowSubjects.add(c.getSubjectMarkComponentId().getSubjectId());
                }
            });

            // gộp chậm tiến độ và fail và sort theo semester
            List<SubjectEntity> combine = new ArrayList<>();
            List<SubjectEntity> finalCombine = combine;
            slowSubjects.forEach(c -> {
                if (!finalCombine.contains(c)) {
                    finalCombine.add(c);
                }
            });
            failSubjects.forEach(c -> {
                if (!finalCombine.contains(c)) {
                    finalCombine.add(c);
                }
            });
            combine = finalCombine;

            /*-----------------------------get Subject List--------------------------------------------------------*/
            ArrayList<ArrayList<String>> parent = new ArrayList<>();
            if (combine.size() >= 5) {
                combine = combine.stream().limit(7).collect(Collectors.toList());
                List<SubjectEntity> set2 = combine.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());

                if (!set2.isEmpty()) {
                    set2.forEach(m -> {
                        ArrayList<String> tmp = new ArrayList<>();
                        tmp.add(m.getId());
                        tmp.add(m.getName());
                        parent.add(tmp);

                    });
                }
            } else if (combine.size() < 5 && combine.size() > 0) {
                for (SubjectEntity subject : combine) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(subject.getId());
                    tmp.add(subject.getName());
                    parent.add(tmp);
                }
                if (nextSubjects.size() > (7 - parent.size())) {
                    for (int i = 0; i < (7 - parent.size()); i++) {
                        ArrayList<String> tmp = new ArrayList<>();
                        tmp.add(nextSubjects.get(i).getId());
                        tmp.add(nextSubjects.get(i).getName());
                        parent.add(tmp);
                    }
                } else {
                    for (int i = 0; i < nextSubjects.size(); i++) {
                        ArrayList<String> tmp = new ArrayList<>();
                        tmp.add(nextSubjects.get(i).getId());
                        tmp.add(nextSubjects.get(i).getName());
                        parent.add(tmp);
                    }
                }
            } else {
                for (int i = 0; i < nextSubjects.size(); i++) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(nextSubjects.get(i).getId());
                    tmp.add(nextSubjects.get(i).getName());
                    parent.add(tmp);
                }
            }
            /**/

            JsonArray finalResult = (JsonArray) new Gson().toJsonTree(parent);

            data.addProperty("iTotalRecords", resultList.size());
            data.addProperty("iTotalDisplayRecords", resultList.size());
            data.add("aaData", finalResult);
            data.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    @RequestMapping("/getStudentNotStart")
    @ResponseBody
    public JsonObject GetStudentNotStart(@RequestParam Map<String, String> params) {
        JsonObject jsonObject = new JsonObject();
        IMarksService marksService = new MarksServiceImpl();

        int stuId = Integer.parseInt(params.get("stuId"));

        try {
            List<MarksEntity> list = marksService.getMarksByStudentIdAndStatus(stuId, "start");

            List<MarksEntity> set2 = list.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());

            List<List<String>> displayList = new ArrayList<>();
            for (MarksEntity sc : set2) {
                List<String> row = new ArrayList<>();
                row.add(sc.getSubjectMarkComponentId().getSubjectId().getId());
                row.add(sc.getSubjectMarkComponentId().getSubjectId().getName());
                row.add(sc.getStatus());

                displayList.add(row);
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(displayList);

            jsonObject.addProperty("iTotalRecords", list.size());
            jsonObject.addProperty("iTotalDisplayRecords", list.size());
            jsonObject.add("aaData", aaData);
            jsonObject.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
