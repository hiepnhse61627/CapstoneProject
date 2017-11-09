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
        } else if(type.equals("OJT")) {
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
            int i = 1;
            for (StudentEntity student : students) {
                System.out.println((i++) + " - " + students.size());

                List<SubjectCurriculumEntity> subjects = new ArrayList<>();

                List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
                for (DocumentStudentEntity doc : docs) {
                    if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
                        List<SubjectCurriculumEntity> list = doc.getCurriculumId().getSubjectCurriculumEntityList();
                        for (SubjectCurriculumEntity s : list ) {
                            if (!subjects.contains(s)) subjects.add(s);
                        }
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
                    t.add(String.valueOf((tongtinchi > required) ? (tongtinchi - required) : 0));
                    data.add(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }
}


