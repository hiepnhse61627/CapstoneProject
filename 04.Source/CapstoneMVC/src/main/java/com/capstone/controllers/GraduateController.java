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

        String type = params.get("type");

        IStudentService studentService = new StudentServiceImpl();
        IMarksService marksService = new MarksServiceImpl();

        List<StudentEntity> students;
        if (programId < 0) {
            students = studentService.findAllStudents();
        } else {
            students = studentService.getStudentByProgram(programId);
        }

//        if (type.equals("Graduate")) {
//            students = students.stream().filter(c -> c.getTerm() >= 9).collect(Collectors.toList());
//        } else
//        if (type.equals("OJT")) {
//
//        } else if (type.equals("SWP")) {
//            students = students.stream().filter(c -> c.getTerm() >= 9).collect(Collectors.toList());
//        }
        if (programService.getProgramById(programId).getName().contains("SE")) {
            students = students.stream().filter(c -> isOJT(c, semester)).collect(Collectors.toList());
        } else if (programService.getProgramById(programId).getName().contains("BA")) {
            students = students.stream().filter(c -> c.getTerm() == 5).collect(Collectors.toList());
        } else {
            students = students.stream().filter(c -> c.getTerm() == 6).collect(Collectors.toList());
        }

        int i = 1;
//        StudentDetail detail = new StudentDetail();
        for (StudentEntity student : students) {

            List<SubjectCurriculumEntity> subjects = new ArrayList<>();

            int ojt = 1;
            List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
            for (DocumentStudentEntity doc : docs) {
                if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
                    List<SubjectCurriculumEntity> list = doc.getCurriculumId().getSubjectCurriculumEntityList();
                    for (SubjectCurriculumEntity s : list) {
                        if (!subjects.contains(s)) {
                            subjects.add(s);
                            if (s.getSubjectId().getType() == SubjectTypeEnum.OJT.getId()) {
                                ojt = s.getTermNumber();
                            }
                        }
                    }
                }
            }

//            if (type.equals("OJT")) {
//
//            } else if (type.equals("SWP")) {
//                subjects = subjects.stream().distinct().collect(Collectors.toList());
//            }

//            subjects = subjects.stream().filter(c -> c.getSubjectId().getType() != SubjectTypeEnum.OJT.getId()).distinct().collect(Collectors.toList());

//                boolean aye = false;

            // CHECK OJT DIEU KIEN DU THU KHAC NHAU
//                Suggestion suggestion = detail.processSuggestion(student.getId(), semester.getSemester());
//                List<List<String>> result2 = suggestion.getData();
//                List<String> brea = new ArrayList<>();
//                brea.add("break");
//                brea.add("");
//                int index = result2.indexOf(brea);
//                if (index > -1) {
//                    if (suggestion.isDuchitieu()) {
//                        result2 = result2.subList(index + 1, result2.size());
//                    } else {
//                        result2 = result2.subList(0, index);
//                    }
//                }
//                for (List<String> r : result2) {
//                    SubjectCurriculumEntity s = subjects.stream().filter(c -> c.getSubjectId().getId().equals(r.get(0))).findFirst().get();
//                    if (type.equals("OJT")) {
//                        if (s.getSubjectId().getType() == SubjectTypeEnum.OJT.getId()) {
//                            aye = true;
//                            break;
//                        }
//                    } else if (type.equals("SWP")) {
//                        if (s.getSubjectId().getType() == SubjectTypeEnum.Capstone.getId()) {
//                            aye = true;
//                            break;
//                        }
//                    }
//                }

//                if (aye) {

            System.out.println(i + " - " + students.size());

//            List<SubjectCurriculumEntity> processedSub = subjects;
            List<SubjectCurriculumEntity> processedSub = new ArrayList<>();
            for (SubjectCurriculumEntity c : subjects) {
                if (c.getTermNumber() >= 1 && c.getTermNumber() < ojt) {
                    processedSub.add(c);
                }
            }

            List<String> tmp = new ArrayList<>();
            for (SubjectCurriculumEntity s : processedSub) {
                if (!tmp.contains(s.getSubjectId().getId())) tmp.add(s.getSubjectId().getId());
            }

            List<MarksEntity> marks = marksService.getMarkByConditions(Ultilities.GetSemesterIdBeforeThisId(semesterId), tmp, student.getId());
//            List<MarksEntity> marks = marksService.getMarkByConditions(semesterId, tmp, student.getId());
            marks = marks.stream().filter(c -> c.getIsActivated() && c.getEnabled() != null && c.getEnabled()).collect(Collectors.toList());
            marks = Ultilities.SortSemestersByMarks(marks);

            List<MarksEntity> finalMarks = marks;
            tmp.stream().filter(c -> !finalMarks.stream().anyMatch(a -> a
                    .getSubjectMarkComponentId()
                    .getSubjectId()
                    .getId()
                    .equals(c))).forEach(c -> System.out.println("môn" + c + " không có điểm"));

            //            Map<String, List<MarksEntity>> map = marks.stream()
//                    .collect(Collectors.groupingBy(c -> c.getSubjectMarkComponentId().getSubjectId().getId()));
//            for (MarksEntity m : marks) {
//                if (map.get(m.getSubjectMarkComponentId().getSubjectId().getId()) == null) {
//                    List<MarksEntity> l = new ArrayList<>();
//                    l.add(m);
//                    map.put(m.getSubjectMarkComponentId().getSubjectId().getId(), l);
//                } else {
//                    map.get(m.getSubjectMarkComponentId().getSubjectId().getId()).add(m);
//                }
//            }

            int required = 0;
//            for (SubjectCurriculumEntity s : processedSub) {
//                required += s.getSubjectCredits();
//            }

//            if (type.equals("OJT")) {
//
//            } else if (type.equals("SWP")) {
//                required = student.getProgramId().getSpecializedCredits();
//            }
            for (SubjectCurriculumEntity s : processedSub) {
                if (s.getSubjectCredits() != null) {
                    required += s.getSubjectCredits();
                }
            }

            int percent = 0;
//            if (type.equals("Graduate")) {
//                percent = student.getProgramId().getGraduate();
//            } else
//            if (type.equals("OJT")) {
//                percent = student.getProgramId().getOjt();
//            } else if (type.equals("SWP")) {
//                percent = student.getProgramId().getCapstone();
//            }

            percent = student.getProgramId().getOjt();

            int tongtinchi = student.getPassCredits();
//            int tongtinchi = 0;

//                    List<MarksEntity> curriculumMarks = marks
//                            .stream()
//                            .filter(c -> stuSubs.stream().anyMatch(a -> a.getSubjectId().getId().equals(c.getSubjectMarkComponentId().getSubjectId().getId())))
//                            .collect(Collectors.toList());

//            for (SubjectCurriculumEntity sub : processedSub) {
//                if (!sub.getSubjectId().getId().toLowerCase().contains("vov")) {
//                    List<MarksEntity> subMarks = marks
//                            .stream()
//                            .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(sub.getSubjectId().getId()))
//                            .collect(Collectors.toList());
//                    if (subMarks.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") ||
//                            c.getStatus().toLowerCase().contains("exempt"))) {
//                        tongtinchi += sub.getSubjectCredits();
//                    } else {
//                        System.out.println("môn" + sub.getSubjectId().getId() + " không đạt - " + student.getRollNumber());
//
//                        boolean dacong = false;
//                        List<SubjectEntity> replacers = sub.getSubjectId().getSubjectEntityList();
//                        for (SubjectEntity replacer : replacers) {
//                            List<MarksEntity> replaceMarks = marks
//                                    .stream()
//                                    .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(replacer.getId()))
//                                    .collect(Collectors.toList());
//                            if (replaceMarks.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") ||
//                                    c.getStatus().toLowerCase().contains("exempt"))) {
//                                tongtinchi += sub.getSubjectCredits();
//                                dacong = true;
//
//                                System.out.println("đã cộng môn thay thế");
//                            }
//                        }
//                        if (!dacong) {
//                            List<SubjectEntity> replacersFirst = sub.getSubjectId().getSubjectEntityList1();
//                            for (SubjectEntity repls : replacersFirst) {
//                                List<SubjectEntity> reps = repls.getSubjectEntityList();
//                                for (SubjectEntity replacer : reps) {
//                                    List<MarksEntity> replaceMarks = marks
//                                            .stream()
//                                            .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(replacer.getId()))
//                                            .collect(Collectors.toList());
//                                    if (replaceMarks.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") ||
//                                            c.getStatus().toLowerCase().contains("exempt"))) {
//                                        tongtinchi += sub.getSubjectCredits();
//
//                                        System.out.println("đã cộng môn thay thế");
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            List<String> datontai = new ArrayList<>();
//            for (SubjectCurriculumEntity s : processedSub) {
//                if (map.get(s.getSubjectId().getId()) != null) {
//                    List<MarksEntity> list = map.get(s.getSubjectId().getId());
//                    if (list.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt"))) {
//                        tongtinchi += s.getSubjectCredits();
//                    } else {
//                        boolean dacong = false;
//                        for (SubjectEntity ss : s.getSubjectId().getSubjectEntityList()) {
//                            List<MarksEntity> t = student.getMarksEntityList().stream().filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equalsIgnoreCase(ss.getId())).collect(Collectors.toList());
//                            if (t.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt"))) {
//                                tongtinchi += s.getSubjectCredits();
//                                dacong = true;
//                                break;
//                            }
//                        }
//                        if (!dacong) {
//                            for (SubjectEntity ss : s.getSubjectId().getSubjectEntityList1()) {
//                                for (SubjectEntity sss : ss.getSubjectEntityList()) {
//                                    List<MarksEntity> t = student.getMarksEntityList().stream().filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equalsIgnoreCase(sss.getId())).collect(Collectors.toList());
//                                    if (t.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt"))) {
//                                        tongtinchi += s.getSubjectCredits();
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            for (MarksEntity mark : marks) {
//                if (mark.getStatus().toLowerCase().contains("pass") || mark.getStatus().toLowerCase().contains("exempt")) {
//                    if (!datontai.contains(mark.getSubjectMarkComponentId().getSubjectId().getId())) {
//                        SubjectCurriculumEntity s = processedSub.stream().filter(c -> c.getSubjectId().getId().equals(mark.getSubjectMarkComponentId().getSubjectId().getId())).findFirst().get();
//                        tongtinchi += s.getSubjectCredits();
//                        datontai.add(mark.getSubjectMarkComponentId().getSubjectId().getId());
//                    }
//                }
//            }

            List<String> t = new ArrayList<>();
            t.add(student.getRollNumber());
            t.add(student.getFullName());
            t.add(String.valueOf(tongtinchi));
            t.add(String.valueOf((int)((required * percent * 1.0) / 100)));
            t.add(String.valueOf(student.getId()));

            if (isGraduate) {
                if (tongtinchi >= (int)((required * percent * 1.0) / 100)) {
                    data.add(t);
                }
            } else {
                if (tongtinchi < (int)((required * percent * 1.0) / 100)) {
                    data.add(t);
                }
            }

            i++;
        }
        return data;
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

//    /**
//     * [This method processes (sort all semesters then iterate over the list, add semester to result list until reaching the current semester)
//     *              and returns list semesters from the beginning to current semester]
//     * @param currentSemesterId
//     * @return listResult
//     * @author HiepNH
//     * @DateCreated 28/10/2017
//     **/
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

    private boolean isOJT(StudentEntity student, RealSemesterEntity semester) {
        int ojt = 9999;
        List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
//        String subjectName = "";
        List<String> tmp = new ArrayList<>();
        for (DocumentStudentEntity doc : docs) {
            if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
                List<SubjectCurriculumEntity> list = doc.getCurriculumId().getSubjectCurriculumEntityList();
                for (SubjectCurriculumEntity s : list) {
                    if (s.getSubjectId().getType() == SubjectTypeEnum.OJT.getId()) {
                        ojt = s.getTermNumber();
//                        subjectName = s.getSubjectId().getId();
                        tmp.add(s.getSubjectId().getId());
                        break;
                    }
                }
            }
        }

        IMarksService service = new MarksServiceImpl();
//        List<MarksEntity> marks = service.getStudentMarksById(student.getId());
//        String finalSubjectName = subjectName;
//        List<MarksEntity> processedMarks = marks.stream()
//                .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(finalSubjectName))
//                .collect(Collectors.toList());
        List<MarksEntity> marks = service.getMarkByConditions(Ultilities.GetSemesterIdBeforeThisId(semester.getId()), tmp, student.getId());
        marks = marks.stream().filter(c -> c.getIsActivated() && c.getEnabled() != null && c.getEnabled()).collect(Collectors.toList());
        marks = Ultilities.SortSemestersByMarks(marks);
        int require = ojt - 1 + Global.CompareSemesterGap(semester);
//        if (student.getTerm() >= require) {
        if ((student.getTerm() >= require) && (marks.size() == 0 || !marks.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("studying")))) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isCapstone(StudentEntity student, RealSemesterEntity r1) {
        IMarksService service = new MarksServiceImpl();

        int capstone = 9999;
        List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
        List<String> tmp = new ArrayList<>();
//        String subjectName = "";
        for (DocumentStudentEntity doc : docs) {
            if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
                List<SubjectCurriculumEntity> list = doc.getCurriculumId().getSubjectCurriculumEntityList();
                for (SubjectCurriculumEntity s : list) {
                    if (s.getSubjectId().getType() == SubjectTypeEnum.Capstone.getId()) {
                        capstone = s.getTermNumber();
//                        subjectName = s.getSubjectId().getId();
                        tmp.add(s.getSubjectId().getId());
                        break;
                    }
                }
            }
        }

//        List<MarksEntity> marks = service.getStudentMarksById(student.getId());
//        String finalSubjectName = subjectName;
//        List<MarksEntity> processedMarks = marks.stream()
//                .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(finalSubjectName))
//                .collect(Collectors.toList());
        List<MarksEntity> marks = service.getMarkByConditions(Ultilities.GetSemesterIdBeforeThisId(r1.getId()), tmp, student.getId());
        marks = marks.stream().filter(c -> c.getIsActivated() && c.getEnabled() != null && c.getEnabled()).collect(Collectors.toList());
        marks = Ultilities.SortSemestersByMarks(marks);
        int require = capstone - 1 + Global.CompareSemesterGap(r1);
//        if (student.getTerm() == capstone - 1) {
        if ((student.getTerm() >= require) && (marks.size() == 0 || !marks.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("studying")))) {
            return true;
        } else {
            return false;
        }
    }

    public List<List<String>> processCapstone(Map<String, String> params) {
        List<List<String>> data = new ArrayList<>();

        int programId = Integer.parseInt(params.get("programId"));
        int semesterId = Integer.parseInt(params.get("semesterId"));
        boolean isGraduate = Boolean.parseBoolean(params.get("boolean"));

        String type = params.get("type");

        IStudentService studentService = new StudentServiceImpl();
        IMarksService marksService = new MarksServiceImpl();

        List<StudentEntity> students;
        if (programId < 0) {
            students = studentService.findAllStudents();
        } else {
            students = studentService.getStudentByProgram(programId);
        }

//        if (type.equals("Graduate")) {
//            students = students.stream().filter(c -> c.getTerm() >= 9).collect(Collectors.toList());
//        } else
//        if (type.equals("OJT")) {
//
//        } else if (type.equals("SWP")) {
//            students = students.stream().filter(c -> c.getTerm() >= 9).collect(Collectors.toList());
//        }
//        if (programService.getProgramById(programId).getName().contains("SE")) {
//        } else if (programService.getProgramById(programId).getName().contains("BA")) {
//            students = students.stream().filter(c -> c.getTerm() == 5).collect(Collectors.toList());
//        } else {
//            students = students.stream().filter(c -> c.getTerm() == 6).collect(Collectors.toList());
//        }

        IRealSemesterService service = new RealSemesterServiceImpl();
        RealSemesterEntity r1 = service.findSemesterById(semesterId);

        students = students.stream().filter(c -> isCapstone(c, r1)).collect(Collectors.toList());

        int i = 1;
//        StudentDetail detail = new StudentDetail();
        for (StudentEntity student : students) {

            List<SubjectCurriculumEntity> subjects = new ArrayList<>();

            int capstone = 1;
            List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
            for (DocumentStudentEntity doc : docs) {
                if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
                    List<SubjectCurriculumEntity> list = doc.getCurriculumId().getSubjectCurriculumEntityList();
                    for (SubjectCurriculumEntity s : list) {
                        if (!subjects.contains(s)) {
                            subjects.add(s);
                            if (s.getSubjectId().getType() == SubjectTypeEnum.Capstone.getId()) {
                                capstone = s.getTermNumber();
                            }
                        }
                    }
                }
            }

//            if (type.equals("OJT")) {
//
//            } else if (type.equals("SWP")) {
//                subjects = subjects.stream().distinct().collect(Collectors.toList());
//            }

//            subjects = subjects.stream().filter(c -> c.getSubjectId().getType() != SubjectTypeEnum.OJT.getId()).distinct().collect(Collectors.toList());

//                boolean aye = false;

            // CHECK OJT DIEU KIEN DU THU KHAC NHAU
//                Suggestion suggestion = detail.processSuggestion(student.getId(), semester.getSemester());
//                List<List<String>> result2 = suggestion.getData();
//                List<String> brea = new ArrayList<>();
//                brea.add("break");
//                brea.add("");
//                int index = result2.indexOf(brea);
//                if (index > -1) {
//                    if (suggestion.isDuchitieu()) {
//                        result2 = result2.subList(index + 1, result2.size());
//                    } else {
//                        result2 = result2.subList(0, index);
//                    }
//                }
//                for (List<String> r : result2) {
//                    SubjectCurriculumEntity s = subjects.stream().filter(c -> c.getSubjectId().getId().equals(r.get(0))).findFirst().get();
//                    if (type.equals("OJT")) {
//                        if (s.getSubjectId().getType() == SubjectTypeEnum.OJT.getId()) {
//                            aye = true;
//                            break;
//                        }
//                    } else if (type.equals("SWP")) {
//                        if (s.getSubjectId().getType() == SubjectTypeEnum.Capstone.getId()) {
//                            aye = true;
//                            break;
//                        }
//                    }
//                }

//                if (aye) {

            System.out.println(i + " - " + students.size());

//            List<SubjectCurriculumEntity> processedSub = subjects;
            List<SubjectCurriculumEntity> processedSub = new ArrayList<>();
            for (SubjectCurriculumEntity c : subjects) {
                if (c.getTermNumber() >= 1 && c.getTermNumber() < capstone) {
                    processedSub.add(c);
                }
            }

            List<String> tmp = new ArrayList<>();
            for (SubjectCurriculumEntity s : processedSub) {
                if (!tmp.contains(s.getSubjectId().getId())) tmp.add(s.getSubjectId().getId());
            }

            List<MarksEntity> marks = marksService.getMarkByConditions(Ultilities.GetSemesterIdBeforeThisId(semesterId), tmp, student.getId());
//            List<MarksEntity> marks = marksService.getMarkByConditions(semesterId, tmp, student.getId());
            marks = marks.stream().filter(c -> c.getIsActivated() && c.getEnabled() != null && c.getEnabled()).collect(Collectors.toList());
            marks = Ultilities.SortSemestersByMarks(marks);

            List<MarksEntity> finalMarks = marks;
            tmp.stream().filter(c -> !finalMarks.stream().anyMatch(a -> a
                    .getSubjectMarkComponentId()
                    .getSubjectId()
                    .getId()
                    .equals(c))).forEach(c -> System.out.println("môn" + c + " không có điểm"));

            //            Map<String, List<MarksEntity>> map = marks.stream()
//                    .collect(Collectors.groupingBy(c -> c.getSubjectMarkComponentId().getSubjectId().getId()));
//            for (MarksEntity m : marks) {
//                if (map.get(m.getSubjectMarkComponentId().getSubjectId().getId()) == null) {
//                    List<MarksEntity> l = new ArrayList<>();
//                    l.add(m);
//                    map.put(m.getSubjectMarkComponentId().getSubjectId().getId(), l);
//                } else {
//                    map.get(m.getSubjectMarkComponentId().getSubjectId().getId()).add(m);
//                }
//            }

//            int required = 0;
            int required = student.getProgramId().getGraduateCredits();
//            for (SubjectCurriculumEntity s : processedSub) {
//                required += s.getSubjectCredits();
//            }

//            if (type.equals("OJT")) {
//
//            } else if (type.equals("SWP")) {
//                required = student.getProgramId().getSpecializedCredits();
//            }
//            for (SubjectCurriculumEntity s : processedSub) {
//                if (s.getSubjectCredits() != null) {
//                    required += s.getSubjectCredits();
//                }
//            }

            int percent = 0;
//            if (type.equals("Graduate")) {
//                percent = student.getProgramId().getGraduate();
//            } else
//            if (type.equals("OJT")) {
//                percent = student.getProgramId().getOjt();
//            } else if (type.equals("SWP")) {
//                percent = student.getProgramId().getCapstone();
//            }

            percent = student.getProgramId().getCapstone();

            int tongtinchi = student.getPassCredits();
//            int tongtinchi = 0;

//                    List<MarksEntity> curriculumMarks = marks
//                            .stream()
//                            .filter(c -> stuSubs.stream().anyMatch(a -> a.getSubjectId().getId().equals(c.getSubjectMarkComponentId().getSubjectId().getId())))
//                            .collect(Collectors.toList());

//            for (SubjectCurriculumEntity sub : processedSub) {
//                if (!sub.getSubjectId().getId().toLowerCase().contains("vov")) {
//                    List<MarksEntity> subMarks = marks
//                            .stream()
//                            .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(sub.getSubjectId().getId()))
//                            .collect(Collectors.toList());
//                    if (subMarks.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") ||
//                            c.getStatus().toLowerCase().contains("exempt"))) {
//                        tongtinchi += sub.getSubjectCredits();
//                    } else {
//                        System.out.println("môn" + sub.getSubjectId().getId() + " không đạt - " + student.getRollNumber());
//
//                        boolean dacong = false;
//                        List<SubjectEntity> replacers = sub.getSubjectId().getSubjectEntityList();
//                        for (SubjectEntity replacer : replacers) {
//                            List<MarksEntity> replaceMarks = marks
//                                    .stream()
//                                    .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(replacer.getId()))
//                                    .collect(Collectors.toList());
//                            if (replaceMarks.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") ||
//                                    c.getStatus().toLowerCase().contains("exempt"))) {
//                                tongtinchi += sub.getSubjectCredits();
//                                dacong = true;
//
//                                System.out.println("đã cộng môn thay thế");
//                            }
//                        }
//                        if (!dacong) {
//                            List<SubjectEntity> replacersFirst = sub.getSubjectId().getSubjectEntityList1();
//                            for (SubjectEntity repls : replacersFirst) {
//                                List<SubjectEntity> reps = repls.getSubjectEntityList();
//                                for (SubjectEntity replacer : reps) {
//                                    List<MarksEntity> replaceMarks = marks
//                                            .stream()
//                                            .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(replacer.getId()))
//                                            .collect(Collectors.toList());
//                                    if (replaceMarks.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") ||
//                                            c.getStatus().toLowerCase().contains("exempt"))) {
//                                        tongtinchi += sub.getSubjectCredits();
//
//                                        System.out.println("đã cộng môn thay thế");
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            List<String> datontai = new ArrayList<>();
//            for (SubjectCurriculumEntity s : processedSub) {
//                if (map.get(s.getSubjectId().getId()) != null) {
//                    List<MarksEntity> list = map.get(s.getSubjectId().getId());
//                    if (list.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt"))) {
//                        tongtinchi += s.getSubjectCredits();
//                    } else {
//                        boolean dacong = false;
//                        for (SubjectEntity ss : s.getSubjectId().getSubjectEntityList()) {
//                            List<MarksEntity> t = student.getMarksEntityList().stream().filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equalsIgnoreCase(ss.getId())).collect(Collectors.toList());
//                            if (t.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt"))) {
//                                tongtinchi += s.getSubjectCredits();
//                                dacong = true;
//                                break;
//                            }
//                        }
//                        if (!dacong) {
//                            for (SubjectEntity ss : s.getSubjectId().getSubjectEntityList1()) {
//                                for (SubjectEntity sss : ss.getSubjectEntityList()) {
//                                    List<MarksEntity> t = student.getMarksEntityList().stream().filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equalsIgnoreCase(sss.getId())).collect(Collectors.toList());
//                                    if (t.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") || c.getStatus().toLowerCase().contains("exempt"))) {
//                                        tongtinchi += s.getSubjectCredits();
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            for (MarksEntity mark : marks) {
//                if (mark.getStatus().toLowerCase().contains("pass") || mark.getStatus().toLowerCase().contains("exempt")) {
//                    if (!datontai.contains(mark.getSubjectMarkComponentId().getSubjectId().getId())) {
//                        SubjectCurriculumEntity s = processedSub.stream().filter(c -> c.getSubjectId().getId().equals(mark.getSubjectMarkComponentId().getSubjectId().getId())).findFirst().get();
//                        tongtinchi += s.getSubjectCredits();
//                        datontai.add(mark.getSubjectMarkComponentId().getSubjectId().getId());
//                    }
//                }
//            }

            List<String> t = new ArrayList<>();
            t.add(student.getRollNumber());
            t.add(student.getFullName());
            t.add(String.valueOf(tongtinchi));
            t.add(String.valueOf((int)((required * percent * 1.0) / 100)));
            t.add(String.valueOf(student.getId()));

            if (isGraduate) {
                if (tongtinchi >= (int)((required * percent * 1.0) / 100)) {
                    data.add(t);
                }
            } else {
                if (tongtinchi < (int)((required * percent * 1.0) / 100)) {
                    data.add(t);
                }
            }

            i++;
        }
        return data;
    }
}


