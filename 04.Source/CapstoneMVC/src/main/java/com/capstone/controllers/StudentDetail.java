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

        try {
            List<MarksEntity> list = service2.getStudentMarksById(Integer.parseInt(params.get("stuId")));
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
            List<MarksEntity> resultList = new ArrayList<>();
            // remove duplicate
            for (MarksEntity marksEntity : comparedList) {
                if (marksEntity.getSubjectMarkComponentId() != null && !resultList.stream().anyMatch(r -> r.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase().equals(marksEntity.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase())
                        && r.getStudentId().getRollNumber().toUpperCase().equals(marksEntity.getStudentId().getRollNumber().toUpperCase()))) {
                    resultList.add(marksEntity);
                }
            }
            List<MarksEntity> set2 = new ArrayList<>();
            set2 = resultList.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());

            ArrayList<ArrayList<String>> parent = new ArrayList<>();
            if (!set2.isEmpty()) {
                set2.forEach(m -> {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(m.getSubjectMarkComponentId() == null ? "N/A" : m.getSubjectMarkComponentId().getSubjectId().getId());
//                    tmp.add(m.getCourseId() == null ? "N/A" : m.getCourseId().getClass1());
                    tmp.add(m.getSemesterId() == null ? "N/A" : m.getSemesterId().getSemester());
                    tmp.add(String.valueOf(m.getAverageMark()));
                    tmp.add(m.getStatus());
                    parent.add(tmp);
                });
            }

            JsonArray result = (JsonArray) new Gson().toJsonTree(parent, new TypeToken<List<MarksEntity>>() {
            }.getType());

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
//        IStudentService studentService = new StudentServiceImpl();
//        IProgramService programService = new ProgramServiceImpl();
//
//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
//        EntityManager em = emf.createEntityManager();
//        JsonObject jsonObject = new JsonObject();
//        int currentTermNumber = 0;
//        List<SubjectEntity> objects = new ArrayList<>();
//        int stuId = Integer.parseInt(params.get("stuId"));
//        Gson gson = new Gson();
//
//        try {
//            StudentEntity student = studentService.findStudentById(stuId);
//            String programName = "SE";
//            if (student.getRollNumber().matches("\\D+\\d+")) {
//                String[] data = student.getRollNumber().split("(?<=\\D)(?=\\d)");
//                if (data[0] != null && (data[0] = data[0].trim()).length() > 0) {
//                    programName = data[0];
//                }
//            }
//            ProgramEntity program = programService.getProgramByName(programName);
//
////            String sqlString = "SELECT distinct Curriculum_Mapping.term FROM Student " +
////                    "INNER JOIN Marks on student.ID = Marks.StudentId AND Student.ID = ?" +
////                    " INNER JOIN Curriculum_Mapping ON Marks.SubjectId = Curriculum_Mapping.SubId " +
////                    " INNER JOIN Subject_Curriculum ON Subject_Curriculum.Id = Curriculum_Mapping.CurId" +
////                    " AND Subject_Curriculum.ProgramId = ?" +
////                    " ORDER BY Curriculum_Mapping.Term desc";
//            String sqlString = "SELECT Curriculum_Mapping.Term, COUNT(*) FROM Student " +
//                    "INNER JOIN Marks on student.ID = Marks.StudentId AND Student.ID = ?" +
//                    " INNER JOIN Curriculum_Mapping ON Marks.SubjectId = Curriculum_Mapping.SubId " +
//                    " INNER JOIN Subject_Curriculum ON Subject_Curriculum.Id = Curriculum_Mapping.CurId" +
//                    " AND Subject_Curriculum.ProgramId = ?" +
//                    " GROUP BY Curriculum_Mapping.Term" +
//                    " ORDER BY Curriculum_Mapping.Term desc";
//            Query query = em.createNativeQuery(sqlString);
//
//            query.setParameter(1, stuId);
//            query.setParameter(2, program.getId());
//            List<Object[]> studentSubCurri = query.getResultList();
//
//
//            if (studentSubCurri.size() > 0) {
//                Object[] lastestData = studentSubCurri.get(0);
//                String lastestTerm = lastestData[0].toString();
//                int numOfStudyingSubjects = (int) lastestData[1];
//                currentTermNumber = Integer.parseInt(lastestTerm.replaceAll("[^0-9]", ""));
//
//                sqlString = "SELECT COUNT(*) FROM Subject_Curriculum" +
//                        " INNER JOIN Curriculum_Mapping ON Subject_Curriculum.Id = Curriculum_Mapping.CurId" +
//                        " AND Subject_Curriculum.ProgramId = ? AND Curriculum_Mapping.Term LIKE ?";
//                query = em.createNativeQuery(sqlString);
//                query.setParameter(1, program.getId());
//                query.setParameter(2, "%" + currentTermNumber);
//
//                int maxNumOfSubjectsInTerm = (int) query.getSingleResult();
//                if (numOfStudyingSubjects == maxNumOfSubjectsInTerm) {
//                    currentTermNumber++;
//                }
//            }

//            query = em.createQuery("SELECT s FROM CurriculumMappingEntity c, SubjectEntity s WHERE c.term LIKE '%" + currentTermNumber + "' AND c.subjectEntity.id = s.id", SubjectEntity.class);
//            objects = (List<SubjectEntity>) query.getResultList();
//
//            List<SubjectEntity> objects2 = objects.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
//
//            ArrayList<ArrayList<String>> parent = new ArrayList<>();
//            if (!objects2.isEmpty()) {
//                objects2.forEach(m -> {
//                    ArrayList<String> tmp = new ArrayList<>();
//                    tmp.add(m.getId());
//                    tmp.add(m.getName());
//
//                    SubjectEntity cur = service3.findSubjectById(m.getId());
//                    boolean exist = false;
//                    //                    for (PrequisiteEntity s : cur.getPrequisiteEntityList()) {
////                        TypedQuery<MarksEntity> q = em.createQuery("SELECT c FROM MarksEntity c WHERE c.studentId.id = :id AND c.subjectId.subjectId = :sub", MarksEntity.class);
////                        List<MarksEntity> l = q.setParameter("sub", s.getSubjectEntity().getId()).setParameter("id", stuId).getResultList();
////                        exist = Ultilities.CheckStudentSubjectFailOrPass(l);
////                    }
//
//                    if (exist) {
//                        tmp.add("1");
//                    } else {
//                        tmp.add("0");
//                    }
//
//                    parent.add(tmp);
//                });
//            }
//
//            sqlString = "SELECT c.SubId, s.Name" +
//                    " FROM Curriculum_Mapping c, Subject s, Subject_Curriculum sc" +
//                    " WHERE c.term LIKE ? AND c.SubId = s.Id" +
//                    " AND sc.Id = c.CurId AND sc.ProgramId = ?";
//            query = em.createNativeQuery(sqlString);
//            query.setParameter(1, "%" + currentTermNumber);
//            query.setParameter(2, program.getId());
//            objects = query.getResultList();
//
//            JsonArray aaData = (JsonArray) gson.toJsonTree(parent);
//
//            jsonObject.addProperty("iTotalRecords", objects.size());
//            jsonObject.addProperty("iTotalDisplayRecords", objects.size());
//            jsonObject.add("aaData", aaData);
//            jsonObject.addProperty("sEcho", params.get("sEcho"));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//
//        return jsonObject;
        return null;
    }

    @RequestMapping("/getStudentNextCourse")
    @ResponseBody
    public JsonObject GetStudentNextCourse(@RequestParam Map<String, String> params) {
//        IStudentService studentService = new StudentServiceImpl();
//        IProgramService programService = new ProgramServiceImpl();
//
//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
//        EntityManager em = emf.createEntityManager();
//        JsonObject jsonObject = new JsonObject();
//        String[] currentTerm = {"0"};
//        int nextTermNumber = 0;
//        int currentTermNumber = 0;
//        List<SubjectEntity> objects = new ArrayList<>();
//        int stuId = Integer.parseInt(params.get("stuId"));
//        Gson gson = new Gson();
//
//        try {
//            StudentEntity student = studentService.findStudentById(stuId);
//            String programName = "SE";
//            if (student.getRollNumber().matches("\\D+\\d+")) {
//                String[] data = student.getRollNumber().split("(?<=\\D)(?=\\d)");
//                if (data[0] != null && (data[0] = data[0].trim()).length() > 0) {
//                    programName = data[0];
//                }
//            }
//            ProgramEntity program = programService.getProgramByName(programName);
//
//            // Query lấy list các "Học kỳ" và đếm số "Môn học" mà sv học trong kỳ đó
//            String sqlString = "SELECT Curriculum_Mapping.Term, COUNT(*) FROM Student " +
//                    "INNER JOIN Marks on student.ID = Marks.StudentId AND Student.ID = ?" +
//                    " INNER JOIN Curriculum_Mapping ON Marks.SubjectId = Curriculum_Mapping.SubId " +
//                    " INNER JOIN Subject_Curriculum ON Subject_Curriculum.Id = Curriculum_Mapping.CurId" +
//                    " AND Subject_Curriculum.ProgramId = ?" +
//                    " GROUP BY Curriculum_Mapping.Term" +
//                    " ORDER BY Curriculum_Mapping.Term desc";
//            Query query = em.createNativeQuery(sqlString);
//
//            query.setParameter(1, stuId);
//            query.setParameter(2, program.getId());
//            List<Object[]> studentSubCurri = query.getResultList();
//
//
//            if (studentSubCurri.size() > 0) {
//                Object[] lastestData = studentSubCurri.get(0);
//                String lastestTerm = lastestData[0].toString();
//                int numOfStudyingSubjects = (int) lastestData[1];
//                currentTermNumber = Integer.parseInt(lastestTerm.replaceAll("[^0-9]", ""));
//                nextTermNumber = currentTermNumber + 1;
//
//                // Query đếm tổng số môn trong 1 kỳ
//                sqlString = "SELECT COUNT(*) FROM Subject_Curriculum" +
//                        " INNER JOIN Curriculum_Mapping ON Subject_Curriculum.Id = Curriculum_Mapping.CurId" +
//                        " AND Subject_Curriculum.ProgramId = ? AND Curriculum_Mapping.Term LIKE ?";
//                query = em.createNativeQuery(sqlString);
//                query.setParameter(1, program.getId());
//                query.setParameter(2, "%" + currentTermNumber);
//
//                int maxNumOfSubjectsInTerm = (int) query.getSingleResult();
//                if (numOfStudyingSubjects == maxNumOfSubjectsInTerm) {
//                    currentTermNumber++;
//                    nextTermNumber++;
//                }
//            }
//
//            query = em.createQuery("SELECT s FROM CurriculumMappingEntity c, SubjectEntity s WHERE c.term LIKE '%" + nextTermNumber + "' AND c.subjectEntity.id = s.id", SubjectEntity.class);
//            objects = (List<SubjectEntity>) query.getResultList();
//
//            List<SubjectEntity> objects2 = objects.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
//
//            ArrayList<ArrayList<String>> parent = new ArrayList<>();
//            if (!objects2.isEmpty()) {
//                objects2.forEach(m -> {
//                    ArrayList<String> tmp = new ArrayList<>();
//                    tmp.add(m.getId());
//                    tmp.add(m.getName());
//
//                    SubjectEntity cur = service3.findSubjectById(m.getId());
//                    boolean exist = false;
////                    for (PrequisiteEntity s : cur.getPrequisiteEntityList()) {
////                        TypedQuery<MarksEntity> q = em.createQuery("SELECT c FROM MarksEntity c WHERE c.studentId.id = :id AND c.subjectId.subjectId = :sub", MarksEntity.class);
////                        List<MarksEntity> l = q.setParameter("sub", s.getId()).setParameter("id", stuId).getResultList();
////                        exist = Ultilities.CheckStudentSubjectFailOrPass(l);
////                    }
//
//                    if (exist) {
//                        tmp.add("1");
//                    } else {
//                        tmp.add("0");
//                    }
//
//                    parent.add(tmp);
//                });
//            }
//
//            sqlString = "SELECT c.SubId, s.Name" +
//                    " FROM Curriculum_Mapping c, Subject s, Subject_Curriculum sc" +
//                    " WHERE c.term LIKE ? AND c.SubId = s.Id" +
//                    " AND sc.Id = c.CurId AND sc.ProgramId = ?";
//            query = em.createNativeQuery(sqlString);
//            query.setParameter(1, "%" + nextTermNumber);
//            query.setParameter(2, program.getId());
//            objects = query.getResultList();
//
//            JsonArray aaData = (JsonArray) gson.toJsonTree(parent);
//
//            jsonObject.addProperty("iTotalRecords", objects.size());
//            jsonObject.addProperty("iTotalDisplayRecords", objects.size());
//            jsonObject.add("aaData", aaData);
//            jsonObject.addProperty("sEcho", params.get("sEcho"));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//
//        return jsonObject;
        return null;
    }
}
