package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.models.Enums;
import com.capstone.models.Logger;
import com.capstone.models.Ultilities;
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
import javax.security.auth.Subject;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class GraduateController {
    IProgramService programService = new ProgramServiceImpl();
    IRealSemesterService semesterService = new RealSemesterServiceImpl();
    IMarksService markService = new MarksServiceImpl();

    @RequestMapping("/graduate")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("StudentGraduate");
        view.addObject("title", "Danh sách xét tốt nghiệp");

        List<ProgramEntity> programList = programService.getAllPrograms();
        List<RealSemesterEntity> semesterList = semesterService.getAllSemester();
        semesterList = Ultilities.SortSemesters(semesterList);

        view.addObject("programList", programList);
        view.addObject("semesterList", semesterList);

        return view;
    }

    @RequestMapping("/processgraduate")
    @ResponseBody
    public JsonObject GetGraduateStudents(@RequestParam Map<String, String> params) {
        JsonObject obj = new JsonObject();
        List<List<String>> studentList = new ArrayList<>();

        String type = params.get("type");
        if (type.equals("Graduate")) {
            studentList = processGraduate(params);
        } else if (type.equals("OJT")) {
            studentList = proccessOJT(params);
        } else if (type.equals("SWP")) {
            studentList = processCapstone(params);
        }
//        int totalCredit = Integer.parseInt(params.get("credit").isEmpty() ? "0" : params.get("credit"));
//        int sCredit = Integer.parseInt(params.get("sCredit").isEmpty() ? "0" : params.get("sCredit"));
//        int programId = Integer.parseInt(params.get("programId"));
//        int semesterId = Integer.parseInt(params.get("semesterId"));
        final String sSearch = params.get("sSearch");

        int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
        int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));

        try {
            // RollNumber, FullName, TotalCredits, TotalSpecializedCredits
//            List<List<String>> studentList = markService.getMarksForGraduatedStudent(
//                    programId, semesterId, totalCredit, sCredit);
            List<List<String>> searchList = studentList.stream().filter(s ->
                    Ultilities.containsIgnoreCase(s.get(0), sSearch)
                    || Ultilities.containsIgnoreCase(s.get(1), sSearch)).collect(Collectors.toList());
            List<List<String>> result = searchList.stream()
                    .skip(iDisplayStart).limit(iDisplayLength)
                    .collect(Collectors.toList());

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

            obj.addProperty("iTotalRecords", studentList.size());
            obj.addProperty("iTotalDisplayRecords", searchList.size());
            obj.add("aaData", aaData);
            obj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
        }

        return obj;
    }

    private List<List<String>> processGraduate(Map<String, String> params) {
        return null;
    }

    private List<List<String>> proccessOJT(Map<String, String> params) {
        List<List<String>> data = new ArrayList<>();

        try {
            int programId = Integer.parseInt(params.get("programId"));
            int semesterId = Integer.parseInt(params.get("semesterId"));

            IStudentService studentService = new StudentServiceImpl();
            IMarksService marksService = new MarksServiceImpl();

            List<StudentEntity> students = studentService.getStudentByProgram(programId);
            for (StudentEntity student : students) {
                List<SubjectCurriculumEntity> subjects = new ArrayList<>();

                List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
                for (DocumentStudentEntity doc : docs) {
                    if (doc.getCurriculumId() != null) {
                        doc.getCurriculumId().getSubjectCurriculumEntityList().forEach(c -> subjects.add(c));
                    }
                }

                List<SubjectCurriculumEntity> processedSub = new ArrayList<>();
                for (SubjectCurriculumEntity c : subjects) {
                    if (c.getTermNumber() >= 1 && c.getTermNumber() <= 5) {
                        processedSub.add(c);
                    }
                }

                List<String> tmp = new ArrayList<>();
                for (SubjectCurriculumEntity s : processedSub) {
                    if (!tmp.contains(s.getSubjectId().getId())) tmp.add(s.getSubjectId().getId());
                }

                List<MarksEntity> marks = marksService.getMarkByConditions(semesterId, tmp, student.getId());

                int required = 0;
                for (SubjectCurriculumEntity s : processedSub) {
                    required += s.getSubjectId().getCredits();
                }
                int percent = student.getProgramId().getOjt();
                int tongtinchi = 0;
                for (MarksEntity mark : marks) {
                    if (mark.getStatus().toLowerCase().contains("pass") || mark.getStatus().toLowerCase().contains("exempt")) {
                        tongtinchi += mark.getSubjectMarkComponentId().getSubjectId().getCredits();
                    }
                }

                if (tongtinchi >= ((required * percent * 1.0) / 100)) {
                    List<String> t = new ArrayList<>();
                    t.add(student.getRollNumber());
                    t.add(student.getFullName());
                    t.add(String.valueOf(tongtinchi));
                    t.add(String.valueOf(tongtinchi > required ? (tongtinchi - required) : 0));
                    data.add(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    private List<List<String>> processCapstone(Map<String, String> params) {
        List<List<String>> result = new ArrayList<>();
        IMarkComponentService markComponentService = new MarkComponentServiceImpl();
        MarkComponentEntity markComponent = markComponentService.getMarkComponentByName(Enums.MarkComponent.AVERAGE.getValue());

        int programId = Integer.parseInt(params.get("programId"));
        int semesterId = Integer.parseInt(params.get("semesterId"));

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            // Get students have more than 1 document_student
            String queryStr = "SELECT ds.StudentId" +
                    " FROM Document_Student ds" +
                    " INNER JOIN Curriculum c ON ds.CurriculumId = c.Id" +
                    " INNER JOIN Program p ON c.ProgramId = p.Id" +
                    " AND CurriculumId IS NOT NULL" +
                    " AND p.Name != 'PC'" +
                    " GROUP BY ds.StudentId" +
                    " HAVING COUNT(ds.CurriculumId) > 1";
            Query queryStudentIds = em.createNativeQuery(queryStr);
            List<Object> studentIds = queryStudentIds.getResultList();
            String strStudentIds = "";
            int count = 0;
            for (Object stId : studentIds) {
                strStudentIds += ((int) stId) + (count != studentIds.size() - 1 ? "," : "");
                count++;
            }

            // Get subjects and credits that students learned
            queryStr = "SELECT m.StudentId, s.RollNumber, s.FullName, sub.Id, sub.Credits, sc.CurriculumId, p.Name, p.Capstone" +
                    " FROM Marks m" +
                    " INNER JOIN Subject_MarkComponent smc ON m.SubjectMarkComponentId = smc.Id" +
                    " INNER JOIN Subject sub ON smc.SubjectId = sub.Id" +
                    " INNER JOIN Document_Student ds ON m.StudentId = ds.StudentId" +
                    " INNER JOIN Student s ON ds.StudentId = s.Id" +
                    " INNER JOIN Curriculum c ON ds.CurriculumId = c.Id" +
                    " INNER JOIN Program p ON c.ProgramId = p.Id" +
                    " INNER JOIN Subject_Curriculum sc ON c.Id = sc.CurriculumId" +
                    " AND sub.Id = sc.SubjectId" +
                    " AND m.IsActivated = 1 AND m.Status = 'Passed'" +
                    " AND smc.MarkComponentId = ?" +
                    " AND ds.StudentId IN (" + strStudentIds + ")" +
                    " GROUP BY m.StudentId, s.RollNumber, s.FullName, sub.Id, sub.Credits, sc.CurriculumId, p.Name, p.Capstone" +
                    " ORDER BY m.StudentId";
            Query query = em.createNativeQuery(queryStr);
            query.setParameter(1, markComponent.getId());
            List<Object[]> searchList = query.getResultList();

            Map<Integer, Credit_StudentModel> studentMap = new HashMap<>();
            for (Object[] data : searchList) {
                int studentId = (int) data[0];
                int subjectCredit = (int) data[4];
                int subjectCurriculumId = (int) data[5];

                Credit_StudentModel student = studentMap.get(studentId);
                if (student == null) {
                    student = new Credit_StudentModel();
                    student.studentId = studentId;
                    student.rollNumber =  data[1].toString();
                    student.fullName = data[2].toString();
                    student.totalCredits = 0;
                    student.programName = data[6].toString();
                    student.capstonePercent = (int) data[7];
                    student.curriculumIds = new ArrayList<>();
                    studentMap.put(studentId, student);
                }

                student.totalCredits += subjectCredit;
                boolean isExisted = false;
                for (Integer curriculumId : student.curriculumIds) {
                    if (curriculumId == subjectCurriculumId) {
                        isExisted = true;
                        break;
                    }
                }
                if (!isExisted) student.curriculumIds.add(subjectCurriculumId);
            }

            // Get max credits for each student
            queryStr = "SELECT ds.StudentId, SUM(sub.Credits)" +
                    " FROM Document_Student ds" +
                    " INNER JOIN Curriculum c ON ds.CurriculumId = c.Id" +
                    " INNER JOIN Subject_Curriculum sc ON c.Id = sc.CurriculumId" +
                    " INNER JOIN Subject sub ON sc.SubjectId = sub.Id" +
                    " AND ds.StudentId IN (" + strStudentIds + ")" +
                    " GROUP BY ds.StudentId";
            query = em.createNativeQuery(queryStr);
            List<Object[]> studentMaxCredits = query.getResultList();

            for (Object[] studentMaxCredit : studentMaxCredits) {
                int curStudentId = (int) studentMaxCredit[0];
                int maxCredits = (int) studentMaxCredit[1];

                Credit_StudentModel student = studentMap.get(curStudentId);
                if (student != null) {
                    int capstoneCredits = Math.round(student.capstonePercent * maxCredits / 100);
                    if (student.totalCredits >= capstoneCredits) {
                        List<String> row = new ArrayList<>();
                        row.add(student.rollNumber);
                        row.add(student.fullName);
                        row.add(student.totalCredits + "");
                        row.add(student.totalCredits > capstoneCredits ? (student.totalCredits - capstoneCredits) + "" : "0");
                        result.add(row);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private class Credit_StudentModel {
        public int studentId;
        public String rollNumber;
        public String fullName;
        public int totalCredits;
        public String programName;
        public int capstonePercent;
        public List<Integer> curriculumIds;
    }
}


