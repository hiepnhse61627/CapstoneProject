package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.models.Enums;
import com.capstone.models.Logger;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class BestStudentController {
    @RequestMapping(value = "/bestStudent")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("BestStudent");
        IRealSemesterService semesterService = new RealSemesterServiceImpl();

        List<RealSemesterEntity> semesterList = semesterService.getAllSemester();
        semesterList = Ultilities.SortSemesters(semesterList);
        view.addObject("semesterList", semesterList);

        view.addObject("title", "Danh sách sinh viên giỏi nhất môn");

        return view;
    }

    @RequestMapping("/bestStudent/getStudentList")
    @ResponseBody
    public JsonObject GetBestStudentDataTable(@RequestParam Map<String, String> params) {
        JsonObject jsonObject = new JsonObject();

        int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
        int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));

        try {
            String searchKey = params.get("sSearch").toLowerCase();
            List<List<String>> studentList = this.getBestStudentList(params);
            studentList = studentList.stream().filter(c ->
                    c.get(0).toLowerCase().contains(searchKey) ||
                            c.get(1).toLowerCase().contains(searchKey) ||
                            c.get(3).toLowerCase().contains(searchKey) ||
                            c.get(4).toLowerCase().contains(searchKey) ||
                            c.get(5).toLowerCase().contains(searchKey) ||
                            c.get(6).toLowerCase().contains(searchKey)).collect(Collectors.toList());

            List<List<String>> result = studentList.stream().skip(iDisplayStart).limit(iDisplayLength).collect(Collectors.toList());
            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

            jsonObject.addProperty("iTotalRecords", studentList.size());
            jsonObject.addProperty("iTotalDisplayRecords", studentList.size());
            jsonObject.add("aaData", aaData);
            jsonObject.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
        }

        return jsonObject;
    }

    // Return [RollNumber, FullName, Semester, Curriculum, Term, AverageMark]
    public List<List<String>> getBestStudentList(Map<String, String> params) {
        IMarksService marksService = new MarksServiceImpl();
        IStudentService studentService = new StudentServiceImpl();
        IRealSemesterService semesterService = new RealSemesterServiceImpl();
        IMarkComponentService markComponentService = new MarkComponentServiceImpl();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = emf.createEntityManager();

        int semesterId = Integer.parseInt(params.get("semesterId"));

        // Get MarkComponentEntity with name is AVERAGE
        MarkComponentEntity markComponent = markComponentService.getMarkComponentByName(
                Enums.MarkComponent.AVERAGE.getValue());

        // Get all semesters
        List<RealSemesterEntity> semesterList = semesterService.getAllSemester();
        semesterList = Ultilities.SortSemesters(semesterList);

        // Put semesters into map [Key: SemesterId, Value: OrdinalNumber]
        Map<Integer, Integer> semesterPositionMap = new HashMap<>();
        for (int i = 0; i < semesterList.size(); i++) {
            semesterPositionMap.put(semesterList.get(i).getId(), i);
        }

        Map<Integer, Map<Integer, List<BestStudentController.BestStudentMarkModel>>> studentList = new HashMap<>();
        Map<Integer, List<DocumentStudentEntity>> docStudentMap = new HashMap<>();

        String queryStr = "SELECT m.StudentId, s.RollNumber, s.FullName, m.SemesterId, co.Semester, c.Name," +
                " sc.TermNumber, smc.SubjectId, co.SubjectCode, sub.Name, m.CourseId, m.AverageMark, m.Status" +
                " FROM Marks m" +
                " INNER JOIN Course co ON m.CourseId = co.Id" +
                " INNER JOIN" +
                " (SELECT mm.StudentId, cc.SubjectCode FROM Marks mm" +
                " INNER JOIN Course cc ON mm.CourseId = cc.Id" +
                " GROUP BY mm.StudentId, cc.SubjectCode" +
                " HAVING Count(*) = 1) AS subQ" +
                " ON m.StudentId = subQ.StudentId AND co.SubjectCode = subQ.SubjectCode" +
                " INNER JOIN" +
                " (SELECT SubjectCode, MAX(AverageMark) AS AverageMark FROM Marks mm" +
                " INNER JOIN Course cc" +
                " ON mm.CourseId = cc.Id" +
                ((semesterId != 0) ? " AND mm.SemesterId = ?" : "") +
                " GROUP BY cc.SubjectCode) n" +
                " ON co.SubjectCode = n.SubjectCode" +
                " AND m.AverageMark = n.AverageMark" +
                " INNER JOIN Student s ON m.StudentId = s.Id" +
                " INNER JOIN Subject_MarkComponent smc ON m.SubjectMarkComponentId = smc.Id" +
                " INNER JOIN Subject sub ON smc.SubjectId = sub.Id" +
                " INNER JOIN Document_Student ds ON m.StudentId = ds.StudentId" +
                " INNER JOIN Curriculum c ON ds.CurriculumId = c.Id" +
                " INNER JOIN Program p ON c.ProgramId = p.Id" +
                " INNER JOIN Subject_Curriculum sc ON c.Id = sc.CurriculumId" +
                " WHERE m.IsActivated = 1" +
                " AND smc.MarkComponentId = ? AND sub.Id = sc.SubjectId" +
                " AND c.ProgramId = s.ProgramId AND p.Name != 'PC'" +
                " AND ds.CurriculumId IS NOT NULL" +
                " AND m.Status NOT IN ('NotStart', 'Studying', 'Fail')" +
                " AND co.SubjectCode NOT LIKE N'LAB%'" +
                " AND s.Term = sc.TermNumber" +
                ((semesterId != 0) ? " AND m.SemesterId = ?" : "");

        Query query = em.createNativeQuery(queryStr);
        if (semesterId != 0) {
            query.setParameter(1, semesterId);
            query.setParameter(2, markComponent.getId());
            query.setParameter(3, semesterId);
        } else {
            query.setParameter(1, markComponent.getId());
        }

        List<Object[]> searchList = query.getResultList();

        List<List<String>> result = new ArrayList<>();

        if (!searchList.isEmpty()) {
            for (Object[] m : searchList) {
                int studentId = (int) m[0];

                ArrayList<String> row = new ArrayList<>();
                row.add(m[1].toString());
                row.add(m[2].toString());
                row.add(m[4].toString());
                row.add(m[5].toString());
                row.add("Học kỳ " + m[6].toString());
                row.add(m[8].toString());
                row.add(m[9].toString());
                row.add(m[11].toString());

//                studentMarkList.put(studentId, markModel);
                result.add(row);
            }
        }

        Collections.sort(result, new Comparator<List<String>>() {
            public int compare(List<String> o1, List<String> o2) {
                int comparedSubjectCode = o1.get(5).compareTo(o2.get(5));

                if (comparedSubjectCode != 0) {
                    return comparedSubjectCode;
                } else {
                    int comparedRollNumber = o1.get(0).compareTo(o2.get(0));
                    return comparedRollNumber;
                }
            }
        });

        return result;
    }

//    private boolean checkSubjectsAreLearnedAgain(Map<Integer, Integer> semesterPositionMap,
//                                                 Map<Integer, List<BestStudentController.BestStudentMarkModel>> semesterMarkList, int curSemesterId) {
//        boolean isValidate = true;
//
//        int curSemesterPosition = semesterPositionMap.get(curSemesterId);
//        List<BestStudentController.BestStudentMarkModel> curMarkList = semesterMarkList.get(curSemesterId);
//        for (Integer semesterId : semesterMarkList.keySet()) {
//            if (semesterPositionMap.get(semesterId) < curSemesterPosition && isValidate) {
//                List<BestStudentController.BestStudentMarkModel> markList = semesterMarkList.get(semesterId);
//                for (BestStudentController.BestStudentMarkModel curMark : curMarkList) {
//                    for (BestStudentController.BestStudentMarkModel mark : markList) {
//                        if (curMark.getSubjectId().equalsIgnoreCase(mark.getSubjectId())) {
//                            isValidate = false;
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//
//        return isValidate;
//    }

//    private boolean validateMarkList(List<BestStudentController.BestStudentMarkModel> markList, List<SubjectCurriculumEntity> subCurricumlumList) {
//        boolean isValidate = true;
//
//        // Get subjects in curriculum, have the same Term as marks in markList
//        List<SubjectCurriculumEntity> subjectInCurrentTerm = new ArrayList<>();
//        BestStudentController.BestStudentMarkModel m = markList.get(markList.size() - 1);
//        int currentTerm = m.getTerm();
//
//        if (currentTerm <= 0) {
//            isValidate = false;
//        }
//
////        for (SubjectCurriculumEntity sc : subCurricumlumList) {
////            if (sc.getTermNumber() == currentTerm) {
////                subjectInCurrentTerm.add(sc);
////            }
////        }
////
////        // Check if student's learned subjects is more or less than subjects in curriculum
////        if (markList.size() != subjectInCurrentTerm.size()) {
////            isValidate = false;
////        }
//
//        // Check marks is not fail
//        if (isValidate) {
//            for (BestStudentController.BestStudentMarkModel mark : markList) {
//                if (!Ultilities.containsIgnoreCase(mark.getStatus(), "pass")) {
//                    isValidate = false;
//                    break;
//                }
//            }
//        }
//
//        // Check subjects exist in curriculum
//        if (isValidate) {
//            for (BestStudentController.BestStudentMarkModel mark : markList) {
//                boolean isFound = false;
//                for (SubjectCurriculumEntity sc : subjectInCurrentTerm) {
//                    if (mark.getSubjectId().equals(sc.getSubjectId().getId())) {
//                        isFound = true;
//                        break;
//                    }
//                }
//
//                if (!isFound) {
//                    isValidate = false;
//                    break;
//                }
//            }
//        }
//
//        // Average mark >= 8
//        if (isValidate) {
//            isValidate = this.calculateAverageMark(markList) >= 8;
//        }
//
//        return isValidate;
//    }

    private class BestStudentMarkModel {
        private String rollNumber;
        private String name;
        private String semester;
        private String curriculum;
        private int term;
        private String subjectCode;
        private String subjectName;
        private double average;

        public BestStudentMarkModel() {
        }

        public BestStudentMarkModel(String rollNumber, String name, String semester, String curriculum, int term, String subjectCode, String subjectName, double average) {
            this.rollNumber = rollNumber;
            this.name = name;
            this.semester = semester;
            this.curriculum = curriculum;
            this.term = term;
            this.subjectCode = subjectCode;
            this.subjectName = subjectName;
            this.average = average;
        }

        public String getRollNumber() {
            return rollNumber;
        }

        public void setRollNumber(String rollNumber) {
            this.rollNumber = rollNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSemester() {
            return semester;
        }

        public void setSemester(String semester) {
            this.semester = semester;
        }

        public String getCurriculum() {
            return curriculum;
        }

        public void setCurriculum(String curriculum) {
            this.curriculum = curriculum;
        }

        public int getTerm() {
            return term;
        }

        public void setTerm(int term) {
            this.term = term;
        }

        public String getSubjectCode() {
            return subjectCode;
        }

        public void setSubjectCode(String subjectCode) {
            this.subjectCode = subjectCode;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public double getAverage() {
            return average;
        }

        public void setAverage(double average) {
            this.average = average;
        }
    }
}
