package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.enums.SubjectTypeEnum;
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
    IStudentService studentService = new StudentServiceImpl();
    ISubjectCurriculumService subjectCurriculumService = new SubjectCurriculumServiceImpl();

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
        List<List<String>> data = new ArrayList<>();

        String type = params.get("type");

        final String sSearch = params.get("sSearch");

        int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
        int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));

        try {
            int programId = Integer.parseInt(params.get("programId"));
            int semesterId = Integer.parseInt(params.get("semesterId"));

            IStudentService studentService = new StudentServiceImpl();
            IMarksService marksService = new MarksServiceImpl();

            List<StudentEntity> students = studentService.getStudentByProgram(programId);

            if (type.equals("Graduate")) {
                students = students.stream().filter(c -> c.getTerm() >= 9).collect(Collectors.toList());
            } else if (type.equals("OJT")) {
                students = students.stream().filter(c -> c.getTerm() == 6).collect(Collectors.toList());
            } else if (type.equals("SWP")) {
                students = students.stream().filter(c -> c.getTerm() == 9).collect(Collectors.toList());
            }

            int i = 1;
            for (StudentEntity student : students) {
                System.out.println((i++) + " - " + students.size());

                List<SubjectCurriculumEntity> subjects = new ArrayList<>();

                int ojt = 1;
                List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
                for (DocumentStudentEntity doc : docs) {
                    if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
                        List<SubjectCurriculumEntity> list = doc.getCurriculumId().getSubjectCurriculumEntityList();
                        for (SubjectCurriculumEntity s : list) {
                            if (!subjects.contains(s)) {
                                subjects.add(s);
                                if (s.getSubjectId().getType() == SubjectTypeEnum.OJT.getId()) ojt = s.getTermNumber();
                            }
                        }
                    }
                }

                List<SubjectCurriculumEntity> processedSub = new ArrayList<>();
                for (SubjectCurriculumEntity c : subjects) {
                    if (type.equals("OJT")) {
                        if (c.getTermNumber() >= 1 && c.getTermNumber() <= (ojt - 1)) {
                            processedSub.add(c);
                        }
                    } else {
                        if (c.getTermNumber() >= 1) {
                            processedSub.add(c);
                        }
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

                int percent = 9999;
                if (type.equals("Graduate")) {
                    percent = student.getProgramId().getGraduate();
                } else if (type.equals("OJT")) {
                    percent = student.getProgramId().getOjt();
                } else if (type.equals("SWP")) {
                    percent = student.getProgramId().getCapstone();
                }

                int tongtinchi = 0;
                List<String> datontai = new ArrayList<>();
                for (MarksEntity mark : marks) {
                    if (mark.getStatus().toLowerCase().contains("pass") || mark.getStatus().toLowerCase().contains("exempt")) {
                        if (!datontai.contains(mark.getSubjectMarkComponentId().getSubjectId().getId())) {
                            tongtinchi += mark.getSubjectMarkComponentId().getSubjectId().getCredits();
                            datontai.add(mark.getSubjectMarkComponentId().getSubjectId().getId());
                        }
                    }
                }

                if (tongtinchi >= ((required * percent * 1.0) / 100)) {
                    List<String> t = new ArrayList<>();
                    t.add(student.getRollNumber());
                    t.add(student.getFullName());
                    t.add(String.valueOf(tongtinchi));
                    t.add(String.valueOf((tongtinchi > required) ? (tongtinchi - required) : 0));
                    data.add(t);
                }
            }

            List<List<String>> searchList = data.stream().filter(s ->
                    Ultilities.containsIgnoreCase(s.get(0), sSearch)
                            || Ultilities.containsIgnoreCase(s.get(1), sSearch)).collect(Collectors.toList());
            List<List<String>> result = searchList.stream()
                    .skip(iDisplayStart).limit(iDisplayLength)
                    .collect(Collectors.toList());

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

            obj.addProperty("iTotalRecords", data.size());
            obj.addProperty("iTotalDisplayRecords", searchList.size());
            obj.add("aaData", aaData);
            obj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
        }

        return obj;
    }

//    private List<List<String>> processGraduate(Map<String, String> params) {
//        List<List<String>> data = new ArrayList<>();
//
//        int programId = Integer.parseInt(params.get("programId"));
//        int semesterId = Integer.parseInt(params.get("semesterId"));
//
//        // get list semester to current semesterId
//        List<RealSemesterEntity> semesters = getToCurrentSemester(semesterId);
//        Set<Integer> semesterIds = semesters.stream().map(s -> s.getId()).collect(Collectors.toSet());
//
//        List<StudentEntity> studentEntityList = new ArrayList<>();
//        studentEntityList = studentService.findStudentByProgramId(programId);
//        studentEntityList = studentEntityList.stream().filter(s -> s.getTerm() == 9).collect(Collectors.toList());
//
//        for (StudentEntity student : studentEntityList) {
//            List<SubjectEntity> subjectEntityList = getSubjectsInCurriculumns(student.getDocumentStudentEntityList());
//            List<SubjectEntity> comparedList = new ArrayList<>();
//            comparedList.addAll(subjectEntityList);
//            for (SubjectEntity subjectEntity : subjectEntityList) {
//                List<SubjectEntity> replaces = subjectEntity.getSubjectEntityList();
//                if (replaces != null && !replaces.isEmpty()) {
//                    comparedList.addAll(replaces);
//                }
//                List<SubjectEntity> replaces2 = subjectEntity.getSubjectEntityList1();
//                if (replaces2 != null && !replaces2.isEmpty()) {
//                    comparedList.addAll(replaces2);
//                }
//            }
//            Set<String> subjectCdsInCurriculum = comparedList.stream().map(s -> s.getId()).collect(Collectors.toSet());
//            // calculate credits in curriculum
//            int creditsInCurriculum = countCreditsInCurriculumn(subjectEntityList);
//            // get mark list from student
//            List<MarksEntity> marksEntityList = student.getMarksEntityList();
//            // filter passed marks
//            List<MarksEntity> passedMarks = new ArrayList<>();
//            for (MarksEntity marksEntity : marksEntityList) {
//                if ((marksEntity.getStatus().toLowerCase().contains("pass") || marksEntity.getStatus().toLowerCase().contains("exempt"))
//                        && (semesterIds.contains(marksEntity.getSemesterId().getId()))) {
//                    passedMarks.add(marksEntity);
//                }
//            }
//            // distinct passed Marks
//            List<MarksEntity> distinctMarks = new ArrayList<>();
//            for (MarksEntity mark : passedMarks) {
//                if (!distinctMarks.stream().anyMatch(d -> d.getStudentId().getRollNumber().equalsIgnoreCase(mark.getStudentId().getRollNumber())
//                        && d.getSubjectMarkComponentId().getSubjectId().getId().equalsIgnoreCase(mark.getSubjectMarkComponentId().getSubjectId().getId()))) {
//                    distinctMarks.add(mark);
//                }
//            }
//            // calculate student credits
//            int studentCredits = 0;
//            for (MarksEntity marksEntity : distinctMarks) {
//                if (subjectCdsInCurriculum.contains(marksEntity.getSubjectMarkComponentId().getSubjectId().getId())) {
//                    studentCredits += marksEntity.getSubjectMarkComponentId().getSubjectId().getCredits();
//                }
//            }
//
//            int percent = student.getProgramId().getGraduate();
//            if (studentCredits >= ((creditsInCurriculum * percent * 1.0) / 100)) {
//                System.out.println(studentCredits + "_____" + creditsInCurriculum);
//                List<String> t = new ArrayList<>();
//                t.add(student.getRollNumber());
//                t.add(student.getFullName());
//                t.add(String.valueOf(studentCredits));
//                t.add(String.valueOf(studentCredits > creditsInCurriculum ? (studentCredits - creditsInCurriculum) : 0));
//                data.add(t);
//            }
//        }
//
//        return data;
//    }

//    private List<SubjectEntity> getSubjectsInCurriculumns(List<DocumentStudentEntity> documentStudentEntityList) {
//        List<SubjectEntity> subjectEntityList = new ArrayList<>();
//        if (documentStudentEntityList != null && !documentStudentEntityList.isEmpty()) {
//            for (DocumentStudentEntity documentStudentEntity : documentStudentEntityList) {
//                CurriculumEntity curriculumEntity = documentStudentEntity.getCurriculumId();
//                List<SubjectCurriculumEntity> subjectCurriculumEntityList = subjectCurriculumService.getSubjectCurriculums(curriculumEntity.getId());
//                List<SubjectEntity> subjects = subjectCurriculumEntityList.stream().filter(s -> s.getTermNumber() != 0).map(s -> s.getSubjectId()).collect(Collectors.toList());
//
//                subjectEntityList.addAll(subjects);
//            }
//        }
//        return subjectEntityList;
//    }

//    private Integer countCreditsInCurriculumn(List<SubjectEntity> subjectEntityList) {
//        int credits = 0;
//        if (subjectEntityList != null && !subjectEntityList.isEmpty()) {
//            for (SubjectEntity subjectEntity : subjectEntityList) {
//                credits += subjectEntity.getCredits();
//            }
//        }
//        return credits;
//	}

    /**
     * [This method processes (sort all semesters then iterate over the list, add semester to result list until reaching the current semester)
     *              and returns list semesters from the beginning to current semester]
     * @param currentSemesterId
     * @return listResult
     * @author HiepNH
     * @DateCreated 28/10/2017
     **/
//    private List<RealSemesterEntity> getToCurrentSemester (Integer currentSemesterId) {
//        List<RealSemesterEntity> semesters = semesterService.getAllSemester();
//        semesters = Ultilities.SortSemesters(semesters);
//        List<RealSemesterEntity> listResult = new ArrayList<>();
//        for (RealSemesterEntity semester : semesters) {
//            listResult.add(semester);
//            if (semester.getId() == currentSemesterId) {
//                break;
//            }
//        }
//        return listResult;
//    }

//    private List<List<String>> proccessOJT(Map<String, String> params) {
//        List<List<String>> data = new ArrayList<>();
//
//        try {
//            int programId = Integer.parseInt(params.get("programId"));
//            int semesterId = Integer.parseInt(params.get("semesterId"));
//
//            IStudentService studentService = new StudentServiceImpl();
//            IMarksService marksService = new MarksServiceImpl();
//
//            List<StudentEntity> students = studentService.getStudentByProgram(programId);
//            students = students.stream().filter(c -> c.getTerm() == 5).collect(Collectors.toList());
//            int i = 1;
//            for (StudentEntity student : students) {
//                System.out.println((i++) + " - " + students.size());
//
//                List<SubjectCurriculumEntity> subjects = new ArrayList<>();
//
//                List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
//                for (DocumentStudentEntity doc : docs) {
//                    if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
//                        List<SubjectCurriculumEntity> list = doc.getCurriculumId().getSubjectCurriculumEntityList();
//                        for (SubjectCurriculumEntity s : list ) {
//                            if (!subjects.contains(s)) subjects.add(s);
//                        }
//                    }
//                }
//
//                List<SubjectCurriculumEntity> processedSub = new ArrayList<>();
//                for (SubjectCurriculumEntity c : subjects) {
//                    if (c.getTermNumber() >= 1 && c.getTermNumber() <= 5) {
//                        processedSub.add(c);
//                    }
//                }
//
//                List<String> tmp = new ArrayList<>();
//                for (SubjectCurriculumEntity s : processedSub) {
//                    if (!tmp.contains(s.getSubjectId().getId())) tmp.add(s.getSubjectId().getId());
//                }
//
//                List<MarksEntity> marks = marksService.getMarkByConditions(semesterId, tmp, student.getId());
//
//                int required = 0;
//                for (SubjectCurriculumEntity s : processedSub) {
//                    required += s.getSubjectId().getCredits();
//                }
//                int percent = student.getProgramId().getOjt();
//                int tongtinchi = 0;
//                List<String> datontai = new ArrayList<>();
//                for (MarksEntity mark : marks) {
//                    if (mark.getStatus().toLowerCase().contains("pass") || mark.getStatus().toLowerCase().contains("exempt")) {
//                        if (!datontai.contains(mark.getSubjectMarkComponentId().getSubjectId().getId())) {
//                            tongtinchi += mark.getSubjectMarkComponentId().getSubjectId().getCredits();
//                            datontai.add(mark.getSubjectMarkComponentId().getSubjectId().getId());
//                        }
//                    }
//                }
//
//                if (tongtinchi >= ((required * percent * 1.0) / 100)) {
//                    List<String> t = new ArrayList<>();
//                    t.add(student.getRollNumber());
//                    t.add(student.getFullName());
//                    t.add(String.valueOf(tongtinchi));
//                    t.add(String.valueOf((tongtinchi > required) ? (tongtinchi - required) : 0));
//                    data.add(t);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return data;
//    }

//    private List<List<String>> processCapstone(Map<String, String> params) {
//        List<List<String>> result = new ArrayList<>();
//        IMarkComponentService markComponentService = new MarkComponentServiceImpl();
//        IRealSemesterService semesterService = new RealSemesterServiceImpl();
//        MarkComponentEntity markComponent = markComponentService.getMarkComponentByName(Enums.MarkComponent.AVERAGE.getValue());
//
//        int programId = Integer.parseInt(params.get("programId"));
//        int semesterId = Integer.parseInt(params.get("semesterId"));
//
//        String strSemesterIds = "";
//        if (semesterId > 0) {
//            List<Integer> semesterIds = new ArrayList<>();
//            List<RealSemesterEntity> semesterList = Ultilities.SortSemesters(semesterService.getAllSemester());
//            for (RealSemesterEntity semester : semesterList) {
//                semesterIds.add(semester.getId());
//                if (semester.getId() == semesterId) {
//                    break;
//                }
//            }
//            strSemesterIds = Ultilities.parseIntegerListToString(semesterIds);
//        }
//
//
//        try {
//            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
//            EntityManager em = emf.createEntityManager();
//
//            // Get students have more than 1 document_student
//            String queryStr = "SELECT ds.StudentId" +
//                    " FROM Document_Student ds" +
//                    " INNER JOIN Curriculum c ON ds.CurriculumId = c.Id" +
//                    " INNER JOIN Program p ON c.ProgramId = p.Id" +
//                    " AND CurriculumId IS NOT NULL" +
//                    " AND p.Name != 'PC'" +
//                    ((programId > 0) ? " AND p.Id = ?" : "") +
//                    " GROUP BY ds.StudentId" +
//                    " HAVING COUNT(ds.CurriculumId) > 1";
//            Query queryStudentIds = em.createNativeQuery(queryStr);
//            if (programId > 0) queryStudentIds.setParameter(1, programId);
//            List<Object> studentIds = queryStudentIds.getResultList();
//            String strStudentIds = "";
//            int count = 0;
//            for (Object stId : studentIds) {
//                strStudentIds += ((int) stId) + (count != studentIds.size() - 1 ? "," : "");
//                count++;
//            }
//
//            // Get subjects and credits that students learned
//            queryStr = "SELECT m.StudentId, s.RollNumber, s.FullName, sub.Id, sub.Credits, sc.CurriculumId, p.Name, p.Capstone" +
//                    " FROM Marks m" +
//                    " INNER JOIN Subject_MarkComponent smc ON m.SubjectMarkComponentId = smc.Id" +
//                    " INNER JOIN Subject sub ON smc.SubjectId = sub.Id" +
//                    " INNER JOIN Document_Student ds ON m.StudentId = ds.StudentId" +
//                    " INNER JOIN Student s ON ds.StudentId = s.Id" +
//                    " INNER JOIN Curriculum c ON ds.CurriculumId = c.Id" +
//                    " INNER JOIN Program p ON c.ProgramId = p.Id" +
//                    " INNER JOIN Subject_Curriculum sc ON c.Id = sc.CurriculumId" +
//                    " AND sub.Id = sc.SubjectId" +
//                    " AND m.IsActivated = 1 AND (m.Status = 'Passed' OR m.Status = 'IsExempt')" +
//                    " AND smc.MarkComponentId = ?" +
//                    " AND ds.StudentId IN (" + strStudentIds + ")" +
//                    (semesterId > 0 ? " AND m.SemesterId IN (" + strSemesterIds + ")" : "") +
//                    " GROUP BY m.StudentId, s.RollNumber, s.FullName, sub.Id, sub.Credits, sc.CurriculumId, p.Name, p.Capstone" +
//                    " ORDER BY m.StudentId";
//            Query query = em.createNativeQuery(queryStr);
//            query.setParameter(1, markComponent.getId());
//            List<Object[]> searchList = query.getResultList();
//
//            Map<Integer, Credit_StudentModel> studentMap = new HashMap<>();
//            for (Object[] data : searchList) {
//                int studentId = (int) data[0];
//                int subjectCredit = (int) data[4];
//                int subjectCurriculumId = (int) data[5];
//
//                Credit_StudentModel student = studentMap.get(studentId);
//                if (student == null) {
//                    student = new Credit_StudentModel();
//                    student.studentId = studentId;
//                    student.rollNumber =  data[1].toString();
//                    student.fullName = data[2].toString();
//                    student.totalCredits = 0;
//                    student.programName = data[6].toString();
//                    student.capstonePercent = (int) data[7];
//                    student.curriculumIds = new ArrayList<>();
//                    studentMap.put(studentId, student);
//                }
//
//                student.totalCredits += subjectCredit;
//                boolean isExisted = false;
//                for (Integer curriculumId : student.curriculumIds) {
//                    if (curriculumId == subjectCurriculumId) {
//                        isExisted = true;
//                        break;
//                    }
//                }
//                if (!isExisted) student.curriculumIds.add(subjectCurriculumId);
//            }
//
//            // Get max credits for each student
//            queryStr = "SELECT ds.StudentId, SUM(sub.Credits)" +
//                    " FROM Document_Student ds" +
//                    " INNER JOIN Curriculum c ON ds.CurriculumId = c.Id" +
//                    " INNER JOIN Subject_Curriculum sc ON c.Id = sc.CurriculumId" +
//                    " INNER JOIN Subject sub ON sc.SubjectId = sub.Id" +
//                    " AND ds.StudentId IN (" + strStudentIds + ")" +
//                    " GROUP BY ds.StudentId";
//            query = em.createNativeQuery(queryStr);
//            List<Object[]> studentMaxCredits = query.getResultList();
//
//            for (Object[] studentMaxCredit : studentMaxCredits) {
//                int curStudentId = (int) studentMaxCredit[0];
//                int maxCredits = (int) studentMaxCredit[1];
//
//                Credit_StudentModel student = studentMap.get(curStudentId);
//                if (student != null) {
//                    int capstoneCredits = Math.round(student.capstonePercent * maxCredits / 100);
//                    if (student.totalCredits >= capstoneCredits) {
//                        List<String> row = new ArrayList<>();
//                        row.add(student.rollNumber);
//                        row.add(student.fullName);
//                        row.add(student.totalCredits + "");
//                        row.add(student.totalCredits > capstoneCredits ? (student.totalCredits - capstoneCredits) + "" : "0");
//                        result.add(row);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return result;
//    }

//    private class Credit_StudentModel {
//        public int studentId;
//        public String rollNumber;
//        public String fullName;
//        public int totalCredits;
//        public String programName;
//        public int capstonePercent;
//        public List<Integer> curriculumIds;
//    }
}


