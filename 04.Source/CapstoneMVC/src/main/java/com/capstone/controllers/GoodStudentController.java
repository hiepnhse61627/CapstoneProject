package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.models.*;
import com.capstone.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class GoodStudentController {

    @RequestMapping("/goodStudent")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("GoodStudent");
        IRealSemesterService semesterService = new RealSemesterServiceImpl();

        List<RealSemesterEntity> semesterList = semesterService.getAllSemester();
        semesterList = Ultilities.SortSemesters(semesterList);
        view.addObject("semesterList", semesterList);

        view.addObject("title", "Danh sách sinh viên giỏi");

        return view;
    }

    @RequestMapping("/goodStudent/getStudentList")
    @ResponseBody
    public JsonObject GetGoodStudentDataTable(@RequestParam Map<String, String> params) {
        JsonObject jsonObject = new JsonObject();
        IMarksService marksService = new MarksServiceImpl();
        IStudentService studentService = new StudentServiceImpl();
        IRealSemesterService semesterService = new RealSemesterServiceImpl();

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            int semesterId = Integer.parseInt(params.get("semesterId"));
            int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
            int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
            String sSearch = params.get("sSearch").trim();

            Map<Integer, Map<Integer, List<GoodStudentMarkModel>>> studentList = new HashMap<>();
            Map<Integer, DocumentStudentEntity> docStudentMap = new HashMap<>();

            String queryStr = "SELECT m.StudentId, m.SemesterId, smc.SubjectId, sub.Credits," +
                    " m.AverageMark, m.Status, sc.TermNumber" +
                    " FROM Marks m" +
                    " INNER JOIN Student s ON m.StudentId = s.Id" +
                    " INNER JOIN Subject_MarkComponent smc ON m.SubjectMarkComponentId = smc.Id" +
                    " INNER JOIN Subject sub ON smc.SubjectId = sub.Id" +
                    " INNER JOIN MarkComponent mc ON smc.MarkComponentId = mc.Id" +
                    " INNER JOIN Document_Student ds ON m.StudentId = ds.StudentId" +
                    " INNER JOIN Subject_Curriculum sc ON ds.CurriculumId = sc.CurriculumId" +
                    " AND mc.Name LIKE '%average%' AND smc.SubjectId = sc.SubjectId" +
                    " AND ds.CreatedDate = (SELECT MAX(CreatedDate) FROM Document_Student WHERE Id = ds.Id)" +
                    ((semesterId != 0) ? " AND m.SemesterId = ?" : "") +
                    (!sSearch.isEmpty() ? "AND m.StudentId IN (SELECT Id FROM Student WHERE RollNumber LIKE ? OR FullName LIKE ?)" : "") +
                    " ORDER BY m.StudentId, sc.TermNumber";
            Query query = em.createNativeQuery(queryStr);
            int count = 1;
            if (semesterId != 0) {
                query.setParameter(count++, semesterId);
            }
            if (!sSearch.isEmpty()) {
                query.setParameter(count++, "%" + sSearch + "%");
                query.setParameter(count, "%" + sSearch + "%");
            }

            List<Object[]> searchList = query.getResultList();

            if (!searchList.isEmpty()) {
                Map<Integer, Map<Integer, List<GoodStudentMarkModel>>> studentMarkList = new HashMap<>();
                for (Object[] m : searchList) {
                    int studentId = (int) m[0];
                    int semesId = (int) m[1];

                    if (studentMarkList.get(studentId) == null) {
                        studentMarkList.put(studentId, new HashMap<>());
                    }

                    Map<Integer, List<GoodStudentMarkModel>> semesterMarkList = studentMarkList.get(studentId);
                    if (semesterMarkList.get(semesId) == null) {
                        semesterMarkList.put(semesId, new ArrayList<>());
                    }

                    GoodStudentMarkModel markModel = new GoodStudentMarkModel();
                    markModel.setSubjectId(m[2].toString());
                    markModel.setCredits((int) m[3]);
                    markModel.setMark((double) m[4]);
                    markModel.setStatus(m[5].toString());
                    markModel.setTerm((int) m[6]);

                    semesterMarkList.get(semesId).add(markModel);
                }

                // Get DocumentStudent List
                String idStr = "(" + Ultilities.parseIntegerListToString(studentMarkList.keySet()) + ")";
                queryStr = "SELECT ds.* FROM Document_Student ds WHERE ds.StudentId IN " + idStr +
                        " AND ds.createdDate = (SELECT MAX(CreatedDate) FROM Document_Student WHERE Id = ds.Id)";
                Query queryDocStudent = em.createNativeQuery(queryStr, DocumentStudentEntity.class);
                List<DocumentStudentEntity> docStudentList = queryDocStudent.getResultList();

                List<Integer> curriculumList = new ArrayList<>();
                docStudentMap = new HashMap<>();
                for (DocumentStudentEntity docStudent : docStudentList) {
                    docStudentMap.put(docStudent.getStudentId().getId(), docStudent);
                    curriculumList.add(docStudent.getCurriculumId().getId());
                }

                idStr = "(" + Ultilities.parseIntegerListToString(curriculumList) + ")";
                queryStr = "SELECT sc.* FROM Subject_Curriculum sc WHERE sc.CurriculumId IN " + idStr;
                Query querySubjectCurriculum =
                        em.createNativeQuery(queryStr, SubjectCurriculumEntity.class);
                List<SubjectCurriculumEntity> subjectCurriculumList = querySubjectCurriculum.getResultList();

                // Change SubjectCurriculum List to HashMap
                Map<Integer, List<SubjectCurriculumEntity>> subjectCurriculumMap = new HashMap<>();
                for (SubjectCurriculumEntity sc: subjectCurriculumList) {
                    int currentCurriId = sc.getCurriculumId().getId();
                    if (subjectCurriculumMap.get(currentCurriId) != null) {
                        subjectCurriculumMap.get(currentCurriId).add(sc);
                    } else {
                        List<SubjectCurriculumEntity> newList = new ArrayList<>();
                        newList.add(sc);
                        subjectCurriculumMap.put(currentCurriId, newList);
                    }
                }

                // Validate student mark list
                for (Integer studentId : studentMarkList.keySet()) {
                    int curriculumId = docStudentMap.get(studentId).getCurriculumId().getId();

                    Map<Integer, List<GoodStudentMarkModel>>
                            semesterMarkList = studentMarkList.get(studentId);

                    for (Integer semesId : semesterMarkList.keySet()) {
                        List<GoodStudentMarkModel> markList = semesterMarkList.get(semesId);
                        boolean isValidate = validateMarkList(markList, subjectCurriculumMap.get(curriculumId));
                        if (isValidate) {
                            if (studentList.get(studentId) == null) {
                                studentList.put(studentId, new HashMap<>());
                            }

                            studentList.get(studentId).put(semesId, markList);
                        }
                    }
                }
            }

            List<RealSemesterEntity> semesterList = semesterService.getAllSemester();
            List<List<String>> result = new ArrayList<>();
            for (int studentId : studentList.keySet()) {
                Map<Integer, List<GoodStudentMarkModel>> semesterMarkList = studentList.get(studentId);
                StudentEntity studentEntity = studentService.findStudentById(studentId);
                DocumentStudentEntity documentStudentEntity = docStudentMap.get(studentId);

                for (int semesId : semesterMarkList.keySet()) {
                    List<GoodStudentMarkModel> markList = semesterMarkList.get(semesId);

                    List<String> row = new ArrayList<>();
                    row.add(studentEntity.getRollNumber()); // RollNumber
                    row.add(studentEntity.getFullName()); // FullName
                    for (RealSemesterEntity semester : semesterList) {
                        if (semester.getId() == semesId) {
                            row.add(semester.getSemester()); // Semester
                            break;
                        }
                    }
                    row.add(documentStudentEntity.getCurriculumId().getProgramId().getName() + "_"
                            + documentStudentEntity.getCurriculumId().getName()); // Curriculum
                    row.add("Học kỳ " + markList.get(0).getTerm()); // Term
                    row.add(this.calculateAverageMark(markList) + ""); // AverageMark

                    result.add(row);
                }
            }

            result = result.stream().skip(iDisplayStart).limit(iDisplayLength).collect(Collectors.toList());
            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

            jsonObject.addProperty("iTotalRecords", studentList.keySet().size());
            jsonObject.addProperty("iTotalDisplayRecords", studentList.keySet().size());
            jsonObject.add("aaData", aaData);
            jsonObject.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
        }

        return jsonObject;
    }

    private boolean validateMarkList(List<GoodStudentMarkModel> markList, List<SubjectCurriculumEntity> subCurricumlumList) {
        boolean isValidate = true;

        List<SubjectCurriculumEntity> subjectInCurrentTerm = new ArrayList<>();
        if (!markList.isEmpty()) {
            GoodStudentMarkModel mark = markList.get(markList.size() - 1);
            int currentTerm = mark.getTerm();

            if (currentTerm <= 0) {
                isValidate = false;
            }

            for (SubjectCurriculumEntity sc : subCurricumlumList) {
                if (sc.getTermNumber() == currentTerm) {
                    subjectInCurrentTerm.add(sc);
                }
            }
        }

        if (markList.size() != subjectInCurrentTerm.size()) {
            isValidate = false;
        }

        // Check Mark is not fail, and Subject is not OJT
        if (isValidate) {
            for (GoodStudentMarkModel mark : markList) {
                if (!Ultilities.containsIgnoreCase(mark.getStatus(), "pass")
                        || Ultilities.containsIgnoreCase(mark.getSubjectId(), "OJ")) {
                    isValidate = false;
                    break;
                }
            }
        }

        // Check Subject in SubjectCurriculum
        if (isValidate) {
            for (GoodStudentMarkModel mark : markList) {
                boolean isFound = false;
                for (SubjectCurriculumEntity sc : subjectInCurrentTerm) {
                    if (mark.getSubjectId().equals(sc.getSubjectId().getId())) {
                        isFound = true;
                        break;
                    }
                }

                if (!isFound) {
                    isValidate = false;
                    break;
                }
            }
        }

        // Average mark >= 8
        if (isValidate) {
            isValidate = this.calculateAverageMark(markList) >= 8;
        }

        return isValidate;
    }

    private double calculateAverageMark(List<GoodStudentMarkModel> markList) {
        double sum = 0;
        int totalCredits = 0;
        for (GoodStudentMarkModel mark : markList) {
            if (!mark.getSubjectId().contains("LAB")) {
                sum += mark.getMark() * mark.getCredits();
                totalCredits += mark.getCredits();
            }
        }

        return Math.round(sum / totalCredits * 10.0) / 10.0;
    }

}
