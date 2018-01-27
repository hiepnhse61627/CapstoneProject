package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.models.*;
import com.capstone.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.*;
import javax.security.auth.Subject;
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

        int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
        int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));

        try {
            String searchKey = params.get("sSearch").toLowerCase();
            List<List<String>> studentList = this.getGoodStudentList(params);
            studentList = studentList.stream().filter(c ->
                    c.get(0).toLowerCase().contains(searchKey) ||
                            c.get(1).toLowerCase().contains(searchKey) ||
                            c.get(3).toLowerCase().contains(searchKey) ||
                            c.get(4).toLowerCase().contains(searchKey)).collect(Collectors.toList());

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
    public List<List<String>> getGoodStudentList(Map<String, String> params) {
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

        Map<Integer, Map<Integer, List<GoodStudentMarkModel>>> studentList = new HashMap<>();
        Map<Integer, List<DocumentStudentEntity>> docStudentMap = new HashMap<>();

        // Get student's marks in current curriculum
        String queryStr = "SELECT m.StudentId, m.SemesterId, smc.SubjectId, sc.SubjectCredits," +
                " m.AverageMark, m.Status, s.Term, ds.CurriculumId" +
                " FROM Marks m" +
                " INNER JOIN Student s ON m.StudentId = s.Id" +
                " INNER JOIN Subject_MarkComponent smc ON m.SubjectMarkComponentId = smc.Id" +
                " INNER JOIN Subject sub ON smc.SubjectId = sub.Id" +
                " INNER JOIN Document_Student ds ON m.StudentId = ds.StudentId" +
                " INNER JOIN Curriculum c ON ds.CurriculumId = c.Id" +
                " INNER JOIN Program p ON c.ProgramId = p.Id" +
                " INNER JOIN Subject_Curriculum sc ON c.Id = sc.CurriculumId" +
                " AND smc.MarkComponentId = ? AND sub.Id = sc.SubjectId" +
                " AND c.ProgramId = s.ProgramId AND p.Name != 'PC'" +
                " AND ds.CurriculumId IS NOT NULL" +
                " AND m.IsActivated = 1" +
                ((semesterId != 0) ? " AND m.SemesterId = ?" : "");

        Query query = em.createNativeQuery(queryStr);
        query.setParameter(1, markComponent.getId());
        if (semesterId != 0) {
            query.setParameter(2, semesterId);
        }

        List<Object[]> searchList = query.getResultList();

        if (!searchList.isEmpty()) {
            // Set values of searchList to GoodStudentMarkModel, depend on semester create HashMap for each model
            // Map[Key: StudentId, Value: Map[Key: SemesterId, Value: GoodStudentMarkModel]]
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
                markModel.setCurriculumId((int) m[7]);

                semesterMarkList.get(semesId).add(markModel);
            }

            // Get DocumentStudentEntity list
            String idStr = "(" + Ultilities.parseIntegerListToString(studentMarkList.keySet()) + ")";
            queryStr = "SELECT ds.* FROM Document_Student ds" +
                    " INNER JOIN Student s ON ds.StudentId = s.Id" +
                    " INNER JOIN Curriculum c ON ds.CurriculumId = c.Id" +
                    " INNER JOIN Program p ON c.ProgramId = p.Id" +
                    " AND c.ProgramId = s.ProgramId AND p.Name != 'PC'" +
                    " AND ds.StudentId IN " + idStr +
                    " AND ds.CurriculumId IS NOT NULL";
            Query queryDocStudent = em.createNativeQuery(queryStr, DocumentStudentEntity.class);
            List<DocumentStudentEntity> docStudentList = queryDocStudent.getResultList();

            // Depend on StudentId, create Map[Key: StudentId, Value: List<DocumentStudent>]
            List<Integer> curriculumList = new ArrayList<>();
            docStudentMap = new HashMap<>();
            for (DocumentStudentEntity docStudent : docStudentList) {
                List<DocumentStudentEntity> curDocList = docStudentMap.get(docStudent.getStudentId().getId());
                if (curDocList == null) {
                    curDocList = new ArrayList<>();
                    docStudentMap.put(docStudent.getStudentId().getId(), curDocList);
                }
                curDocList.add(docStudent);
                curriculumList.add(docStudent.getCurriculumId().getId());
            }

            // Validate studentMarkList
            for (Integer studentId : studentMarkList.keySet()) {
                // Get all student's subjects in curriculum, except OJT Curriculum
                List<SubjectCurriculumEntity> allStudentSubjects = new ArrayList<>();
                for (DocumentStudentEntity docStudent : docStudentMap.get(studentId)) {
                    if (docStudent.getCurriculumId() != null
                            && !docStudent.getCurriculumId().getName().contains("OJT")) {
                        List<SubjectCurriculumEntity> subjectCurriculumList = docStudent.getCurriculumId()
                                .getSubjectCurriculumEntityList();
                        if (subjectCurriculumList != null && !subjectCurriculumList.isEmpty()) {
                            allStudentSubjects.addAll(subjectCurriculumList);
                        }
                    }
                }

                Map<Integer, List<GoodStudentMarkModel>> semesterMarkList = studentMarkList.get(studentId);

                for (Integer semesId : semesterMarkList.keySet()) {
                    List<GoodStudentMarkModel> markList = semesterMarkList.get(semesId);

                    // Validation
                    if (/*checkSubjectsAreLearnedAgain(semesterPositionMap, semesterMarkList, semesId) &&*/
                            validateMarkList(studentId, markList, allStudentSubjects, marksService, semesId)) {
                        if (studentList.get(studentId) == null) {
                            studentList.put(studentId, new HashMap<>());
                        }

                        studentList.get(studentId).put(semesId, markList);
                    }
                }
            }
        }


        List<List<String>> result = new ArrayList<>();
        for (int studentId : studentList.keySet()) {
            Map<Integer, List<GoodStudentMarkModel>> semesterMarkList = studentList.get(studentId);
            StudentEntity studentEntity = studentService.findStudentById(studentId);
            List<DocumentStudentEntity> documentStudentList = docStudentMap.get(studentId);

            for (int semesId : semesterMarkList.keySet()) {
                List<GoodStudentMarkModel> markList = semesterMarkList.get(semesId);
                int curriculumId = markList.get(0).getCurriculumId();

                List<String> row = new ArrayList<>();
                row.add(studentEntity.getRollNumber()); // RollNumber
                row.add(studentEntity.getFullName()); // FullName
                for (RealSemesterEntity semester : semesterList) {
                    if (semester.getId() == semesId) {
                        row.add(semester.getSemester()); // Semester
                        break;
                    }
                }
                for (DocumentStudentEntity docStudent : documentStudentList) {
                    if (docStudent.getCurriculumId().getId() == curriculumId) {
                        row.add(docStudent.getCurriculumId().getProgramId().getName() + "_"
                                + docStudent.getCurriculumId().getName()); // Curriculum
                        break;
                    }
                }
                row.add("Học kỳ " + markList.get(0).getTerm()); // Term
                row.add(this.calculateAverageMark(markList) + ""); // AverageMark

                result.add(row);
            }
        }


        Collections.sort(result, new Comparator<List<String>>() {
            public int compare(List<String> o1, List<String> o2) {
                int compareRollNumber = o1.get(0).compareTo(o2.get(0));

                if (compareRollNumber != 0) {
                    return compareRollNumber;
                } else {
                    int compareTerm = o1.get(4).compareTo(o2.get(4));
                    return compareTerm;
                }
            }
        });

        return result;
    }

    private boolean checkSubjectsAreLearnedAgain(Map<Integer, Integer> semesterPositionMap,
                                                 Map<Integer, List<GoodStudentMarkModel>> semesterMarkList, int curSemesterId) {
        boolean isValidate = true;

        int curSemesterPosition = semesterPositionMap.get(curSemesterId);
        List<GoodStudentMarkModel> curMarkList = semesterMarkList.get(curSemesterId);
        for (Integer semesterId : semesterMarkList.keySet()) {
            if (semesterPositionMap.get(semesterId) < curSemesterPosition && isValidate) {
                List<GoodStudentMarkModel> markList = semesterMarkList.get(semesterId);
                for (GoodStudentMarkModel curMark : curMarkList) {
                    for (GoodStudentMarkModel mark : markList) {
                        if (curMark.getSubjectId().equalsIgnoreCase(mark.getSubjectId())) {
                            isValidate = false;
                            break;
                        }
                    }
                }
            }
        }

        return isValidate;
    }

    private boolean validateMarkList(int studentId, List<GoodStudentMarkModel> markList,
                                     List<SubjectCurriculumEntity> subCurricumlumList, IMarksService marksService,
                                     int semesterId) {
        boolean isValidate = true;

        List<GoodStudentMarkModel> curriculumSubjectMarks = new ArrayList<>();

        // Get subjects in curriculum, have the same Term as marks in markList
        List<SubjectCurriculumEntity> subjectInCurrentTerm = new ArrayList<>();
        GoodStudentMarkModel m = markList.get(markList.size() - 1);
        int currentTerm = m.getTerm();

        if (currentTerm <= 0) {
            isValidate = false;
        }

        for (SubjectCurriculumEntity sc : subCurricumlumList) {
            if (sc.getTermNumber() == currentTerm) {
                subjectInCurrentTerm.add(sc);
            }
        }

        //Fix this
        // Check if student's learned subjects is more or less than subjects in curriculum
//        if (markList.size() != subjectInCurrentTerm.size()) {
//            isValidate = false;
//        }

        // Check subjects exist in curriculum
        if (isValidate) {
            for (GoodStudentMarkModel mark : markList) {
                int isFound = -1;
                for (SubjectCurriculumEntity sc : subjectInCurrentTerm) {
                    if (mark.getSubjectId().equals(sc.getSubjectId().getId())) {
                        curriculumSubjectMarks.add(mark);
                        isFound = subjectInCurrentTerm.indexOf(sc);
                        break;
                    }
                }

                if (isFound != -1) {
                    subjectInCurrentTerm.remove(isFound);
                }
            }
        }

        if(isValidate && !subjectInCurrentTerm.isEmpty()) {
            List<MarksEntity> semesterMarks = marksService.findMarksByProperties(semesterId, studentId);
            List<SubjectEntity> subjectEntityInCurrentTerm = subjectInCurrentTerm.stream().map(q -> q.getSubjectId()).collect(Collectors.toList());
            for (SubjectEntity entity : subjectEntityInCurrentTerm) {
                for (SubjectEntity subject : entity.getSubjectEntityList()) {
                    outLoop:
                    for (MarksEntity mark : semesterMarks) {
                        if(mark.getCourseId().getSubjectCode().equalsIgnoreCase(subject.getId())) {
                            GoodStudentMarkModel model = new GoodStudentMarkModel();
                            SubjectCurriculumEntity temp = subjectInCurrentTerm.stream().filter(q -> q.getSubjectId().getId().equalsIgnoreCase(entity.getId())).findFirst().get();

                            model.setCredits(temp.getSubjectCredits());
                            model.setCurriculumId(temp.getCurriculumId().getId());
                            model.setMark(mark.getAverageMark());
                            model.setStatus(mark.getStatus());
                            model.setSubjectId(subject.getId());
                            model.setTerm(currentTerm);
                            model.setMarkId(mark.getId());

                            curriculumSubjectMarks.add(model);
                            subjectInCurrentTerm.remove(temp);

                            break outLoop;
                        }
                    }
                }
            }

            isValidate &= subjectInCurrentTerm.isEmpty();
        }

        // Check marks is not fail
        if (isValidate) {
            for (GoodStudentMarkModel mark : curriculumSubjectMarks) {
                if (!Ultilities.containsIgnoreCase(mark.getStatus(), "pass")) {
                    isValidate = false;
                    break;
                }
            }
        }

        // Average mark >= 8
        if (isValidate) {
            isValidate = this.calculateAverageMark(curriculumSubjectMarks) >= 8;
            markList.clear();
            markList.addAll(curriculumSubjectMarks);
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

    private class GoodStudentMarkModel {
        private int markId;
        private String subjectId;
        private double mark;
        private int term;
        private int credits;
        private String status;
        private int curriculumId;

        public GoodStudentMarkModel() {
        }

        public int getMarkId() {
            return markId;
        }

        public void setMarkId(int markId) {
            this.markId = markId;
        }

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }

        public double getMark() {
            return mark;
        }

        public void setMark(double mark) {
            this.mark = mark;
        }

        public int getTerm() {
            return term;
        }

        public void setTerm(int term) {
            this.term = term;
        }

        public int getCredits() {
            return credits;
        }

        public void setCredits(int credits) {
            this.credits = credits;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getCurriculumId() {
            return curriculumId;
        }

        public void setCurriculumId(int curriculumId) {
            this.curriculumId = curriculumId;
        }
    }

}
