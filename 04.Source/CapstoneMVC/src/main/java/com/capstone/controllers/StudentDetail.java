package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.models.*;
import com.capstone.services.*;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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
    public JsonObject GetStudentDetail(@RequestParam Map<String, String> params) {
        JsonObject data = new JsonObject();
        IMarksService marksService = new MarksServiceImpl();

        try {
            List<MarksEntity> list = marksService.getAllMarksByStudentAndSubject(Integer.parseInt(params.get("stuId")), "0", "0");

            // result list
            List<MarksEntity> resultList = new ArrayList<>();

            Map<String, List<MarksEntity>> map = new HashMap<>();
            for (MarksEntity m : list) {
                if (map.get(m.getSubjectMarkComponentId().getSubjectId().getId()) == null) {
                    List<MarksEntity> newMarkList = new ArrayList<>();
                    newMarkList.add(m);
                    map.put(m.getSubjectMarkComponentId().getSubjectId().getId(), newMarkList);
                } else {
                    map.get(m.getSubjectMarkComponentId().getSubjectId().getId()).add(m);
                }
            }

            for (Map.Entry<String, List<MarksEntity>> entry : map.entrySet()) {
                boolean isPass = false;

                List<MarksEntity> g = Ultilities.SortMarkBySemester(entry.getValue().stream().filter(c -> !c.getStatus().toLowerCase().contains("studying")).collect(Collectors.toList()));
                if (!g.isEmpty()) {
                    MarksEntity tmp = null;
                    for (MarksEntity k2 : g) {
                        tmp = k2;
                        if (k2.getStatus().toLowerCase().contains("pass") || k2.getStatus().toLowerCase().contains("exempt")) {
                            isPass = true;
                            break;
                        }
                    }

                    if (!isPass) {
                        SubjectEntity sub = tmp.getSubjectMarkComponentId().getSubjectId();

                        int totalFail = 0;
                        MarksEntity failedRow = tmp;

                        for (SubjectEntity replace : sub.getSubjectEntityList()) {
                            List<MarksEntity> replaced = marksService.getAllMarksByStudentAndSubject(tmp.getStudentId().getId(), replace.getId(), "0");
                            for (MarksEntity marks : replaced) {
                                tmp = marks;
                                if (marks.getStatus().toLowerCase().contains("pass") || marks.getStatus().toLowerCase().contains("exempt")) {
                                    isPass = true;
                                    break;
                                }
                            }

                            if (!isPass) {
                                failedRow = tmp;
                                totalFail++;
                            }
                        }

                        if (totalFail == sub.getSubjectEntityList().size()) {
                            resultList.add(failedRow);
                        }
                    }
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
        IStudentService studentService = new StudentServiceImpl();
        ISubjectService subjectService = new SubjectServiceImpl();
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
            query.setParameter("term", student.getTerm());

            List<SubjectCurriculumEntity> currentTermSubjectCurriList = query.getResultList();


            List<MarksEntity> list = service2.getStudentMarksById(stuId);
            // Init students passed and failed
            List<MarksEntity> listPassed = list.stream().filter(p -> p.getStatus().contains("Passed") || p.getStatus().contains("Exempt")).collect(Collectors.toList());
            List<MarksEntity> listFailed = list.stream().filter(f -> !f.getStatus().contains("Passed") || !f.getStatus().contains("Exempt")).collect(Collectors.toList());
            // compared list
            List<MarksEntity> comparedList = new ArrayList<>();
            // make comparator
            Comparator<MarksEntity> comparator = new Comparator<MarksEntity>() {
                @Override
                public int compare(MarksEntity o1, MarksEntity o2) {
                    return new CompareToBuilder()
                            .append(o1.getSubjectMarkComponentId() == null ? "" : o1.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase(), o2.getSubjectMarkComponentId() == null ? "" : o2.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase())
                            .append(o1.getStudentId().getRollNumber().toUpperCase(), o2.getStudentId().getRollNumber().toUpperCase())
                            .toComparison();
                }
            };
            Collections.sort(listPassed, comparator);
            // start compare failed list to passed list
            for (int i = 0; i < listFailed.size(); i++) {
                MarksEntity keySearch = listFailed.get(i);
                int index = Collections.binarySearch(listPassed, keySearch, comparator);
                if (index < 0) {
                    comparedList.add(keySearch);
                }
            }
            // result list
            List<MarksEntity> failList = new ArrayList<>();
            // remove duplicate
            for (MarksEntity marksEntity : comparedList) {
                if (marksEntity.getSubjectMarkComponentId() != null && !failList.stream().anyMatch(r -> r.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase().equals(marksEntity.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase())
                        && r.getStudentId().getRollNumber().toUpperCase().equals(marksEntity.getStudentId().getRollNumber().toUpperCase()))) {
                    failList.add(marksEntity);
                }
            }


            List<SubjectCurriculumEntity> result = new ArrayList<>();
            for (SubjectCurriculumEntity sc : currentTermSubjectCurriList) {
                List<List<SubjectEntity>> preList = subjectService.getAllPrequisiteSubjects(sc.getSubjectId().getId());

                boolean isFound = false;
                // Find if fail subject in preList
                for (MarksEntity marksEntity : failList) {
                    for (List<SubjectEntity> sList : preList) {
                        for (SubjectEntity s : sList) {
                            if (s.getId().equals(marksEntity.getSubjectMarkComponentId().getSubjectId().getId())) {
                                isFound = true;
                                break;
                            }
                        }
                    }
                }

                if (!isFound) {
                    result.add(sc);
                }
            }


            List<List<String>> displayList = new ArrayList<>();
            for (SubjectCurriculumEntity sc : result) {
                List<String> row = new ArrayList<>();
                row.add(sc.getSubjectId().getId());
                row.add(sc.getSubjectId().getName());

                displayList.add(row);
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(displayList);

            jsonObject.addProperty("iTotalRecords", result.size());
            jsonObject.addProperty("iTotalDisplayRecords", result.size());
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
            List<List<String>> result = new ArrayList<>();
            for (SubjectCurriculumEntity sc : list) {
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
        JsonObject jsonObject = new JsonObject();

        int stuId = Integer.parseInt(params.get("stuId"));
        StudentEntity student = studentService.findStudentById(stuId);

        try {
            /*-----------------------------------Fail Course--------------------------------------------------*/
            List<MarksEntity> list = marksService.getAllMarksByStudentAndSubject(Integer.parseInt(params.get("stuId")), "0", "0");

            // result list
            List<MarksEntity> resultList = new ArrayList<>();

            Map<String, List<MarksEntity>> map = new HashMap<>();
            for (MarksEntity m : list) {
                if (map.get(m.getSubjectMarkComponentId().getSubjectId().getId()) == null) {
                    List<MarksEntity> newMarkList = new ArrayList<>();
                    newMarkList.add(m);
                    map.put(m.getSubjectMarkComponentId().getSubjectId().getId(), newMarkList);
                } else {
                    map.get(m.getSubjectMarkComponentId().getSubjectId().getId()).add(m);
                }
            }

            for (Map.Entry<String, List<MarksEntity>> entry : map.entrySet()) {
                boolean isPass = false;

                List<MarksEntity> g = Ultilities.SortMarkBySemester(entry.getValue().stream().filter(c -> !c.getStatus().toLowerCase().contains("studying")).collect(Collectors.toList()));
                if (!g.isEmpty()) {
                    MarksEntity tmp = null;
                    for (MarksEntity k2 : g) {
                        tmp = k2;
                        if (k2.getStatus().toLowerCase().contains("pass") || k2.getStatus().toLowerCase().contains("exempt")) {
                            isPass = true;
                            break;
                        }
                    }

                    if (!isPass) {
                        SubjectEntity sub = tmp.getSubjectMarkComponentId().getSubjectId();

                        int totalFail = 0;
                        MarksEntity failedRow = tmp;

                        for (SubjectEntity replace : sub.getSubjectEntityList()) {
                            List<MarksEntity> replaced = marksService.getAllMarksByStudentAndSubject(tmp.getStudentId().getId(), replace.getId(), "0");
                            for (MarksEntity marks : replaced) {
                                tmp = marks;
                                if (marks.getStatus().toLowerCase().contains("pass") || marks.getStatus().toLowerCase().contains("exempt")) {
                                    isPass = true;
                                    break;
                                }
                            }

                            if (!isPass) {
                                failedRow = tmp;
                                totalFail++;
                            }
                        }

                        if (totalFail == sub.getSubjectEntityList().size()) {
                            resultList.add(failedRow);
                        }
                    }
                }
            }



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
            ArrayList<ArrayList<String>> result = new ArrayList<>();
            for (SubjectCurriculumEntity sc : listNextCurri) {
                ArrayList<String> row = new ArrayList<>();
                row.add(sc.getSubjectId().getId());
                row.add(sc.getSubjectId().getName());

                result.add(row);
            }
            /*-----------------------------get Subject List--------------------------------------------------------*/
            if (resultList.size() >= 5) {
                resultList = resultList.stream().limit(7).collect(Collectors.toList());
                List<MarksEntity> set2 = resultList.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
                ArrayList<ArrayList<String>> parent = new ArrayList<>();
                if (!set2.isEmpty()) {

                    set2.forEach(m -> {

                        ArrayList<String> tmp = new ArrayList<>();
                        tmp.add(m.getSubjectMarkComponentId() == null ? "N/A" : m.getSubjectMarkComponentId().getSubjectId().getId());
                        tmp.add(m.getSubjectMarkComponentId() == null ? "N/A" : m.getSubjectMarkComponentId().getSubjectId().getName());
                        tmp.add(m.getCourseId() == null ? "N/A" : m.getCourseId().getSemester());
                        tmp.add(m.getSemesterId() == null ? "N/A" : m.getSemesterId().getSemester());
                        tmp.add(String.valueOf(m.getAverageMark()));
                        tmp.add(m.getStatus());
                        parent.add(tmp);

                    });
                    JsonArray finalResult = (JsonArray) new Gson().toJsonTree(parent);

                    data.addProperty("iTotalRecords", resultList.size());
                    data.addProperty("iTotalDisplayRecords", resultList.size());
                    data.add("aaData", finalResult);
                    data.addProperty("sEcho", params.get("sEcho"));
                }
            } else if (resultList.size() < 5 && resultList.size() > 0) {
                ArrayList<ArrayList<String>> parent = new ArrayList<>();
                for (MarksEntity subject : resultList) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(subject.getSubjectMarkComponentId() == null ? "N/A" : subject.getSubjectMarkComponentId().getSubjectId().getId());
                    tmp.add(subject.getSubjectMarkComponentId() == null ? "N/A" : subject.getSubjectMarkComponentId().getSubjectId().getName());
                    tmp.add(subject.getCourseId() == null ? "N/A" : subject.getCourseId().getSemester());
                    tmp.add(subject.getSemesterId() == null ? "N/A" : subject.getSemesterId().getSemester());
                    tmp.add(String.valueOf(subject.getAverageMark()));
                    tmp.add(subject.getStatus());
                    parent.add(tmp);
                }
                if (result.size() > (7 - parent.size())) {
                    for (int i = 0; i < (7 - parent.size()); i++) {
                        parent.add(result.get(i));
                    }
                } else {
                    for (int i = 0; i < result.size(); i++) {
                        parent.add(result.get(i));
                    }
                }


                JsonArray finalResult = (JsonArray) new Gson().toJsonTree(parent);

                data.addProperty("iTotalRecords", resultList.size());
                data.addProperty("iTotalDisplayRecords", resultList.size());
                data.add("aaData", finalResult);
                data.addProperty("sEcho", params.get("sEcho"));
            } else {
                ArrayList<ArrayList<String>> parent = new ArrayList<>();
                for (int i = 0; i < result.size(); i++) {
                    parent.add(result.get(i));
                }

                JsonArray finalResult = (JsonArray) new Gson().toJsonTree(parent);

                data.addProperty("iTotalRecords", resultList.size());
                data.addProperty("iTotalDisplayRecords", resultList.size());
                data.add("aaData", finalResult);
                data.addProperty("sEcho", params.get("sEcho"));
            }
            /**/

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }
}
