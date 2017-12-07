package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.enums.SubjectTypeEnum;
import com.capstone.models.*;
import com.capstone.services.*;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sun.org.apache.xpath.internal.operations.Bool;
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
        semesterList = Lists.reverse(semesterList);

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

//        final String sSearch = params.get("sSearch");

//        int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
//        int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
//        boolean isGraduate = Boolean.parseBoolean(params.get("boolean"));

        try {
            // RollNumber, FullName, TotalCredits, TotalSpecializedCredits
            List<List<String>> studentList;
            if (type.equals("Graduate")) {
                studentList = processGraduate(params);
            } else if (type.equals("OJT")) {
                studentList = proccessOJT(params);
            } else {
                studentList = processCapstone(params);
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(studentList);
            obj.add("aaData", aaData);
//            obj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
        }

        return obj;
    }

    public List<List<String>> processGraduate(Map<String, String> params) {
        List<List<String>> data = new ArrayList<>();

        int programId = Integer.parseInt(params.get("programId"));
        int semesterId = Integer.parseInt(params.get("semesterId"));
        boolean isGraduate = Boolean.parseBoolean(params.get("boolean"));

        // get list semester to current semesterId
        List<RealSemesterEntity> semesters = getToCurrentSemester(semesterId);
        Set<Integer> semesterIds = semesters.stream().map(s -> s.getId()).collect(Collectors.toSet());

        List<StudentEntity> studentEntityList;
        if (programId < 0) {
            studentEntityList = studentService.findAllStudents();
        } else {
            studentEntityList = studentService.findStudentByProgramId(programId);
        }
        // filter student in term 9
        studentEntityList = studentEntityList.stream().filter(s -> s.getTerm() == 9).collect(Collectors.toList());
        List<StudentEntity> filteredList = new ArrayList<>();
        for (StudentEntity studentEntity : studentEntityList) {
            List<StudentStatusEntity> studentStatusEntities = studentEntity.getStudentStatusEntityList();
            for (StudentStatusEntity studentStatusEntity : studentStatusEntities) {
                if (studentStatusEntity.getSemesterId().getId() == semesterId && !studentStatusEntities.get(0).getStatus().equals("G")) {
                    filteredList.add(studentEntity);
                }
            }
        }

        for (StudentEntity student : filteredList) {
            List<DocumentStudentEntity> documentStudentEntityList = student.getDocumentStudentEntityList();
            Map<SubjectEntity, Integer> subjectsCredits = processCreditsForSubject(documentStudentEntityList);
            // get mark list from student
            List<MarksEntity> marksEntityList = student.getMarksEntityList();
            // filter passed marks
            List<MarksEntity> passedMarks = new ArrayList<>();
            for (MarksEntity marksEntity : marksEntityList) {
                if ((marksEntity.getStatus().toLowerCase().contains("pass") || marksEntity.getStatus().toLowerCase().contains("exempt"))
                        && (semesterIds.contains(marksEntity.getSemesterId().getId()))) {
                    passedMarks.add(marksEntity);
                }
            }
            // distinct passed Marks
            List<MarksEntity> distinctMarks = new ArrayList<>();
            for (MarksEntity mark : passedMarks) {
                if (!distinctMarks.stream().anyMatch(d -> d.getStudentId().getRollNumber().equalsIgnoreCase(mark.getStudentId().getRollNumber())
                        && d.getSubjectMarkComponentId().getSubjectId().getId().equalsIgnoreCase(mark.getSubjectMarkComponentId().getSubjectId().getId()))) {
                    distinctMarks.add(mark);
                }
            }
            // calculate student credits if SYB was passed
            int studentCredits = 0;
            boolean passedFlag = false; // flag shows that a student have a subject that has 0 credits and not passed
            if (!subjectsCredits.containsValue(0)) {
                passedFlag = true;
            }
            for (MarksEntity marksEntity : distinctMarks) {
                SubjectEntity subject = marksEntity.getSubjectMarkComponentId().getSubjectId();
                if (!passedFlag) {
                    if ((subjectsCredits.get(subject) != null) && (subjectsCredits.get(subject) == 0)) {
                        passedFlag = true; // passed
                    }
                }
                studentCredits += subjectsCredits.get(subject) != null && subject.getType() != SubjectTypeEnum.OJT.getId() ? subjectsCredits.get(subject) : 0;

                if (subjectsCredits.get(subject) != null && student.getId() == 69667) {
                    System.out.println(subject.getId() + "_" + subjectsCredits.get(subject) + "_" + studentCredits);
                }
            }

            int specializedCredits = student.getProgramId().getSpecializedCredits();
            if (isGraduate) {
                if ((studentCredits >= specializedCredits) && (passedFlag)) {
                    List<String> t = new ArrayList<>();
                    t.add(student.getRollNumber());
                    t.add(student.getFullName());
                    t.add(String.valueOf(studentCredits));
//                    t.add(String.valueOf(studentCredits > creditsInCurriculum ? (studentCredits - creditsInCurriculum) : 0));
                    t.add(String.valueOf(specializedCredits));
                    t.add(String.valueOf(student.getId()));
                    data.add(t);
                }
            } else {
                if ((studentCredits < specializedCredits) || (!passedFlag)) {
                    List<String> t = new ArrayList<>();
                    t.add(student.getRollNumber());
                    t.add(student.getFullName());
                    t.add(String.valueOf(studentCredits));
//                    t.add(String.valueOf(studentCredits > creditsInCurriculum ? (studentCredits - creditsInCurriculum) : 0));
                    t.add(String.valueOf(specializedCredits));
                    t.add(String.valueOf(student.getId()));
                    data.add(t);
                }
            }
        }

        return data;
    }

//    private List<SubjectEntity> getSubjectsInCurriculumns(List<DocumentStudentEntity> documentStudentEntityList) {
//        List<SubjectEntity> subjectEntityList = new ArrayList<>();
//        if (documentStudentEntityList != null && !documentStudentEntityList.isEmpty()) {
//            for (DocumentStudentEntity documentStudentEntity : documentStudentEntityList) {
//                CurriculumEntity curriculumEntity = documentStudentEntity.getCurriculumId();
//                List<SubjectCurriculumEntity> subjectCurriculumEntityList = subjectCurriculumService.getSubjectCurriculums(curriculumEntity.getId());
//                List<SubjectEntity> subjects = subjectCurriculumEntityList.stream().filter(s -> s.getTermNumber() > 0).map(s -> s.getSubjectId()).collect(Collectors.toList());
//
//                subjectEntityList.addAll(subjects);
//            }
//        }
//        return subjectEntityList;
//    }

//    private Integer countCreditsInCurriculumn(List<DocumentStudentEntity> documentStudentEntityList) {
//        int credits = 0;
//        if (documentStudentEntityList != null && !documentStudentEntityList.isEmpty()) {
//            for (DocumentStudentEntity documentStudentEntity : documentStudentEntityList) {
//                CurriculumEntity curriculumEntity = documentStudentEntity.getCurriculumId();
//                List<SubjectCurriculumEntity> subjectCurriculumEntityList = subjectCurriculumService.getSubjectCurriculums(curriculumEntity.getId());
//                subjectCurriculumEntityList = subjectCurriculumEntityList.stream().filter(s -> s.getTermNumber() >= 0).collect(Collectors.toList());
//
//                for (SubjectCurriculumEntity subjectCurriculumEntity : subjectCurriculumEntityList) {
//                    credits += subjectCurriculumEntity.getSubjectCredits();
//                }
//            }
//        }
//        return credits;
//    }

    /**
     * [This method processes (sort all semesters then iterate over the list, add semester to result list until reaching the current semester)
     * and returns list semesters from the beginning to current semester]
     *
     * @param currentSemesterId
     * @return listResult
     * @author HiepNH
     * @DateCreated 28/10/2017
     **/
    private List<RealSemesterEntity> getToCurrentSemester(Integer currentSemesterId) {
        List<RealSemesterEntity> semesters = semesterService.getAllSemester();
        semesters = Ultilities.SortSemesters(semesters);
        List<RealSemesterEntity> listResult = new ArrayList<>();
        for (RealSemesterEntity semester : semesters) {
            listResult.add(semester);
            if (semester.getId() == currentSemesterId) {
                break;
            }
        }
        return listResult;
    }

    public Map<SubjectEntity, Integer> processCreditsForSubject(List<DocumentStudentEntity> documentStudentEntityList) {
        Map<SubjectEntity, Integer> map = new HashMap<>();
        if (documentStudentEntityList != null && !documentStudentEntityList.isEmpty()) {
            for (DocumentStudentEntity documentStudentEntity : documentStudentEntityList) {
                CurriculumEntity curriculumEntity = documentStudentEntity.getCurriculumId();
                List<SubjectCurriculumEntity> subjectCurriculumEntityList = subjectCurriculumService.getSubjectCurriculums(curriculumEntity.getId());
                subjectCurriculumEntityList = subjectCurriculumEntityList.stream().filter(s -> s.getTermNumber() >= 0).collect(Collectors.toList());

                for (SubjectCurriculumEntity subjectCurriculumEntity : subjectCurriculumEntityList) {
                    SubjectEntity subjectEntity = subjectCurriculumEntity.getSubjectId();
                    Integer subjectCredits = subjectCurriculumEntity.getSubjectCredits();
                    map.put(subjectEntity, subjectCredits);
                    List<SubjectEntity> replacesInTheRight = subjectEntity.getSubjectEntityList();
                    if (replacesInTheRight != null && !replacesInTheRight.isEmpty()) {
                        for (SubjectEntity rightReplace : replacesInTheRight) {
                            map.put(rightReplace, subjectCredits);
                        }
                    }
                    List<SubjectEntity> replacesInTheLeft = subjectEntity.getSubjectEntityList1();
                    if (replacesInTheLeft != null && !replacesInTheLeft.isEmpty()) {
                        for (SubjectEntity leftReplace : replacesInTheLeft) {
                            map.put(leftReplace, subjectCredits);

                            for (SubjectEntity rightOfLeftReplace : leftReplace.getSubjectEntityList()) {
                                map.put(rightOfLeftReplace, subjectCredits);
                            }
                        }
                    }
                }
            }
        }

        return map;
    }

    public List<List<String>> proccessOJT(Map<String, String> params) {
        List<List<String>> data = new ArrayList<>();

        int programId = Integer.parseInt(params.get("programId"));
        int semesterId = Integer.parseInt(params.get("semesterId"));

        IRealSemesterService service = new RealSemesterServiceImpl();
        RealSemesterEntity semester = service.findSemesterById(semesterId);

        boolean isGraduate = Boolean.parseBoolean(params.get("boolean"));

        List<StudentEntity> students;
        if (programId < 0) {
            students = studentService.findAllStudents();
        } else {
            students = studentService.getStudentByProgram(programId);
        }

        EntityManagerFactory fac = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = fac.createEntityManager();

//        IMarksService marksService = new MarksServiceImpl();

        students = students.stream().filter(c -> isOJT(c, semester)).collect(Collectors.toList());
//        students = students.stream().filter(c -> c.getTerm() >= 5).collect(Collectors.toList());

       List<MarksEntity> map = em.createQuery("SELECT a FROM MarksEntity a WHERE a.isActivated = true AND a.subjectMarkComponentId.subjectId.type = 1 AND (LOWER(a.status) LIKE '%studying%' OR LOWER(a.status) LIKE '%pass%')", MarksEntity.class)
                .getResultList()
                .stream()
                .filter(Ultilities.distinctByKey(c -> c.getStudentId().getId()))
                .collect(Collectors.toList());

       IDocumentStudentService documentStudentService = new DocumentStudentServiceImpl();

        int i = 1;
        for (StudentEntity student : students) {

            System.out.println(i + " - " + students.size());

            if (student.getRollNumber().equals("SE61107")) {
                System.out.println();
            }

//            List<SubjectCurriculumEntity> subjects = new ArrayList<>();
            List<SubjectCurriculumEntity> processedSub = new ArrayList<>();


            int ojt = 1;
//            List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
            List<DocumentStudentEntity> docs = documentStudentService.getDocumentStudentListByStudentId(student.getId());
//            List<String> tmp = new ArrayList<>();
            for (DocumentStudentEntity doc : docs) {
                if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
                    List<SubjectCurriculumEntity> list = doc.getCurriculumId().getSubjectCurriculumEntityList();
                    for (SubjectCurriculumEntity s : list) {
                        if (!processedSub.contains(s)) {
                            processedSub.add(s);
                            if (s.getSubjectId().getType() == SubjectTypeEnum.OJT.getId()) {
//                                ojt = s.getTermNumber();
                                break;
//                                tmp.add(s.getSubjectId().getId());
                            }
                        }
                    }
                }
            }

//            List<MarksEntity> marks = marksService.getMarkByConditions(Ultilities.GetSemesterIdBeforeThisId(semester.getId()), tmp, student.getId());
//            marks = marks.stream().filter(c -> c.getIsActivated()).collect(Collectors.toList());
//            marks = Ultilities.SortSemestersByMarks(marks);

            boolean req = false;
            if (map.stream().anyMatch(c -> c.getStudentId().getId() == student.getId())) {
                req = true;
            }
//            for (MarksEntity m : marks) {
//                if (m.getStatus().toLowerCase().contains("pass") || m.getStatus().toLowerCase().contains("studying")) {
//                    req = true;
//                    break;
//                }
//            }

            if (!req) {
//                List<SubjectCurriculumEntity> processedSub = new ArrayList<>();
//                for (SubjectCurriculumEntity c : subjects) {
//                    if (c.getTermNumber() >= 1 && c.getTermNumber() < ojt) {
//                        processedSub.add(c);
//                    }
//                }

                int required = 0;

                for (SubjectCurriculumEntity s : processedSub) {
                    if (s.getSubjectCredits() != null) {
                        required += s.getSubjectCredits();
                    }
                }

                int percent = student.getProgramId().getOjt();

                int tongtinchi = student.getPassCredits();

                List<String> t = new ArrayList<>();
                t.add(student.getRollNumber());
                t.add(student.getFullName());
                t.add(String.valueOf(student.getTerm()));
                t.add(String.valueOf(tongtinchi));
                t.add(String.valueOf((int) ((required * percent * 1.0) / 100)));
                t.add(String.valueOf(student.getId()));

                if (isGraduate) {
                    if (tongtinchi >= (int) ((required * percent * 1.0) / 100)) {
                        data.add(t);
                    }
                } else {
                    if (tongtinchi < (int) ((required * percent * 1.0) / 100)) {
                        data.add(t);
                    }
                }
            } else {
                System.out.println("dang hoc hoac da pass");
            }

            i++;
        }

        return data;
    }

    private boolean isOJT(StudentEntity student, RealSemesterEntity semester) {
        int ojt = 6;
        List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
        for (DocumentStudentEntity doc : docs) {
            if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
                List<SubjectCurriculumEntity> list = doc.getCurriculumId().getSubjectCurriculumEntityList();
                for (SubjectCurriculumEntity s : list) {
                    if (s.getSubjectId().getType() == SubjectTypeEnum.OJT.getId()) {
                        ojt = s.getTermNumber();
                        break;
                    }
                }
            }
        }

        int require = ojt - 1 - Global.CompareSemesterGap(semester);
        if (student.getTerm() >= require) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isCapstone(StudentEntity student, RealSemesterEntity r1) {
        int ojt = 9;
        List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
        for (DocumentStudentEntity doc : docs) {
            if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
                List<SubjectCurriculumEntity> list = doc.getCurriculumId().getSubjectCurriculumEntityList();
                for (SubjectCurriculumEntity s : list) {
                    if (s.getSubjectId().getType() == SubjectTypeEnum.Capstone.getId()) {
                        ojt = s.getTermNumber();
                        break;
                    }
                }
            }
        }

        int require = ojt - 1 - Global.CompareSemesterGap(r1);
        if (student.getTerm() >= require) {
            return true;
        } else {
            return false;
        }
    }

    public List<List<String>> processCapstone(Map<String, String> params) {
        List<List<String>> data = new ArrayList<>();

        int programId = Integer.parseInt(params.get("programId"));
        int semesterId = Integer.parseInt(params.get("semesterId"));

        IRealSemesterService service = new RealSemesterServiceImpl();
        RealSemesterEntity semester = service.findSemesterById(semesterId);

        boolean isGraduate = Boolean.parseBoolean(params.get("boolean"));

        List<StudentEntity> students;
        if (programId < 0) {
            students = studentService.findAllStudents();
        } else {
            students = studentService.getStudentByProgram(programId);
        }

        EntityManagerFactory fac = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = fac.createEntityManager();

//        IMarksService marksService = new MarksServiceImpl();

        students = students.stream().filter(c -> isCapstone(c, semester)).collect(Collectors.toList());
//        students = students.stream().filter(c -> c.getTerm() >= 5).collect(Collectors.toList());

        List<MarksEntity> map = em.createQuery("SELECT a FROM MarksEntity a WHERE a.isActivated = true AND a.subjectMarkComponentId.subjectId.type = 2 AND (LOWER(a.status) LIKE '%studying%' OR LOWER(a.status) LIKE '%pass%')", MarksEntity.class)
                .getResultList()
                .stream()
                .filter(Ultilities.distinctByKey(c -> c.getStudentId().getId()))
                .collect(Collectors.toList());

        IDocumentStudentService documentStudentService = new DocumentStudentServiceImpl();

        int i = 1;
        for (StudentEntity student : students) {

            System.out.println(i + " - " + students.size());

            if (student.getRollNumber().equals("SE61107")) {
                System.out.println();
            }

//            List<SubjectCurriculumEntity> subjects = new ArrayList<>();
            List<SubjectCurriculumEntity> processedSub = new ArrayList<>();


            int ojt = 1;
//            List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
            List<DocumentStudentEntity> docs = documentStudentService.getDocumentStudentListByStudentId(student.getId());
//            List<String> tmp = new ArrayList<>();
            for (DocumentStudentEntity doc : docs) {
                if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
                    List<SubjectCurriculumEntity> list = doc.getCurriculumId().getSubjectCurriculumEntityList();
                    for (SubjectCurriculumEntity s : list) {
                        if (!processedSub.contains(s)) {
                            processedSub.add(s);
                            if (s.getSubjectId().getType() == SubjectTypeEnum.Capstone.getId()) {
//                                ojt = s.getTermNumber();
                                break;
//                                tmp.add(s.getSubjectId().getId());
                            }
                        }
                    }
                }
            }

//            List<MarksEntity> marks = marksService.getMarkByConditions(Ultilities.GetSemesterIdBeforeThisId(semester.getId()), tmp, student.getId());
//            marks = marks.stream().filter(c -> c.getIsActivated()).collect(Collectors.toList());
//            marks = Ultilities.SortSemestersByMarks(marks);

            boolean req = false;
            if (map.stream().anyMatch(c -> c.getStudentId().getId() == student.getId())) {
                req = true;
            }
//            for (MarksEntity m : marks) {
//                if (m.getStatus().toLowerCase().contains("pass") || m.getStatus().toLowerCase().contains("studying")) {
//                    req = true;
//                    break;
//                }
//            }

            if (!req) {
//                List<SubjectCurriculumEntity> processedSub = new ArrayList<>();
//                for (SubjectCurriculumEntity c : subjects) {
//                    if (c.getTermNumber() >= 1 && c.getTermNumber() < ojt) {
//                        processedSub.add(c);
//                    }
//                }

                int required = 0;

                for (SubjectCurriculumEntity s : processedSub) {
                    if (s.getSubjectCredits() != null) {
                        required += s.getSubjectCredits();
                    }
                }

                int percent = student.getProgramId().getCapstone();

                int tongtinchi = student.getPassCredits();

                List<String> t = new ArrayList<>();
                t.add(student.getRollNumber());
                t.add(student.getFullName());
                t.add(String.valueOf(student.getTerm()));
                t.add(String.valueOf(tongtinchi));
                t.add(String.valueOf((int) ((required * percent * 1.0) / 100)));
                t.add(String.valueOf(student.getId()));

                if (isGraduate) {
                    if (tongtinchi >= (int) ((required * percent * 1.0) / 100)) {
                        data.add(t);
                    }
                } else {
                    if (tongtinchi < (int) ((required * percent * 1.0) / 100)) {
                        data.add(t);
                    }
                }
            } else {
                System.out.println("dang hoc hoac da pass");
            }

            i++;
        }

        return data;
    }
}


