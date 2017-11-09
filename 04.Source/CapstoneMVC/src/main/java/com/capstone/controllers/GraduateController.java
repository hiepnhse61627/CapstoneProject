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
        List<List<String>> studentList = new ArrayList<>();

        String type = params.get("type");
        if (type.equals("Graduate")) {
            studentList = processGraduate(params);
        } else if (type.equals("OJT")) {
            studentList = proccessOJT(params);
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
        List<List<String>> data = new ArrayList<>();

        int programId = Integer.parseInt(params.get("programId"));
        List<StudentEntity> studentEntityList = new ArrayList<>();
        if (programId != 0) {
            studentEntityList = studentService.findStudentByProgramId(programId);
            studentEntityList = studentEntityList.stream().filter(s -> s.getTerm() > 0).collect(Collectors.toList());
        }

        for (StudentEntity student : studentEntityList) {
            List<SubjectEntity> subjectEntityList = getSubjectsInCurriculumns(student.getDocumentStudentEntityList());
            List<SubjectEntity> comparedList = new ArrayList<>();
            comparedList.addAll(subjectEntityList);
            for (SubjectEntity subjectEntity : subjectEntityList) {
                List<SubjectEntity> replaces = subjectEntity.getSubjectEntityList();
                if (replaces != null && !replaces.isEmpty()) {
                    comparedList.addAll(replaces);
                }
                List<SubjectEntity> replaces2 = subjectEntity.getSubjectEntityList1();
                if (replaces2 != null && !replaces2.isEmpty()) {
                    comparedList.addAll(replaces2);
                }
            }
            Set<String> subjectCdsInCurriculum = comparedList.stream().map(s -> s.getId()).collect(Collectors.toSet());
            // calculate credits in curriculum
            int creditsInCurriculum = countCreditsInCurriculumn(subjectEntityList);
            // get mark list from student
            List<MarksEntity> marksEntityList = student.getMarksEntityList();
            // filter passed marks
            List<MarksEntity> passedMarks = new ArrayList<>();
            for (MarksEntity marksEntity : marksEntityList) {
                if (marksEntity.getStatus().toLowerCase().contains("pass") || marksEntity.getStatus().toLowerCase().contains("exempt")) {
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
            // calculate student credits
            int studentCredits = 0;
            for (MarksEntity marksEntity : distinctMarks) {
                if (subjectCdsInCurriculum.contains(marksEntity.getSubjectMarkComponentId().getSubjectId().getId())) {
                    if (student.getId() == 31180) {
                        System.out.println(marksEntity.getSubjectMarkComponentId().getSubjectId().getId() + "_" + marksEntity.getSubjectMarkComponentId().getSubjectId().getCredits());
                    }
                    studentCredits += marksEntity.getSubjectMarkComponentId().getSubjectId().getCredits();
                }
            }

            int percent = student.getProgramId().getGraduate();
            if (studentCredits >= ((creditsInCurriculum * percent * 1.0) / 100)) {
                System.out.println(studentCredits + "_____" + creditsInCurriculum);
                List<String> t = new ArrayList<>();
                t.add(student.getRollNumber());
                t.add(student.getFullName());
                t.add(String.valueOf(studentCredits));
                t.add(String.valueOf(studentCredits > creditsInCurriculum ? (studentCredits - creditsInCurriculum) : 0));
                data.add(t);
            }
        }

        return data;
    }

    private List<SubjectEntity> getSubjectsInCurriculumns(List<DocumentStudentEntity> documentStudentEntityList) {
        List<SubjectEntity> subjectEntityList = new ArrayList<>();
        if (documentStudentEntityList != null && !documentStudentEntityList.isEmpty()) {
            for (DocumentStudentEntity documentStudentEntity : documentStudentEntityList) {
                CurriculumEntity curriculumEntity = documentStudentEntity.getCurriculumId();
                List<SubjectCurriculumEntity> subjectCurriculumEntityList = subjectCurriculumService.getSubjectCurriculums(curriculumEntity.getId());
                List<SubjectEntity> subjects = subjectCurriculumEntityList.stream().filter(s -> s.getTermNumber() != 0).map(s -> s.getSubjectId()).collect(Collectors.toList());

                subjectEntityList.addAll(subjects);
            }
        }
        return subjectEntityList;
    }

    private Integer countCreditsInCurriculumn(List<SubjectEntity> subjectEntityList) {
        int credits = 0;
        if (subjectEntityList != null && !subjectEntityList.isEmpty()) {
            for (SubjectEntity subjectEntity : subjectEntityList) {
                credits += subjectEntity.getCredits();
            }
        }
        return credits;
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
}


