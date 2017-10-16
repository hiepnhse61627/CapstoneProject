package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.StudentEntity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class StudentsOJTController {
    IProgramService programService = new ProgramServiceImpl();
    IStudentService studentService = new StudentServiceImpl();
    ICurriculumMappingService curriculumMappingService = new CurriculumMappingServiceImpl();
    ISubjectService subjectService = new SubjectServiceImpl();

    @RequestMapping("/studentsOJT")
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("StudentsOJT");
        mav.addObject("title", "DSSV đủ điều kiện OJT");
        mav.addObject("programs", programService.getAllPrograms());

        return mav;
    }

    @RequestMapping("/getStudentsOJT")
    @ResponseBody
    public JsonObject getStudentsOJT(@RequestParam Map<String, String> params) {
        JsonObject jsonObject = new JsonObject();
        int programId = Integer.parseInt(params.get("programId").trim());
        String programName = programService.getProgramById(programId).getName();
        try {
            List<StudentEntity> students = studentService.findStudentsByProgramName(programName);
            List<StudentEntity> resultList = new ArrayList<>();
            int qualifiedCredits = programName.equals("SE") ? 69 : 68;
            if (students != null && !students.isEmpty()) {
                for (StudentEntity student : students) {
                    String semesterTerm = curriculumMappingService.getSemesterTermByStudentIdAndProgramId(student.getId(), programId);
                    if (semesterTerm != null) {
                        int currentTermNumber = Integer.parseInt(semesterTerm.replaceAll("[^0-9]", ""));
                        if (currentTermNumber == 6) { // semester term 6
                            int totalCredits = subjectService.countStudentCredits(student.getId());
                            if (totalCredits >= qualifiedCredits) {
                                resultList.add(student);
                            }
                        }
                    }
                }
            }

            List<StudentEntity> set = resultList.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());

            ArrayList<ArrayList<String>> parent = new ArrayList<>();
            if (!set.isEmpty()) {
                set.forEach(s -> {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(s.getRollNumber());
                    tmp.add(s.getFullName());
                    parent.add(tmp);
                });
            }

            JsonArray output = (JsonArray) new Gson().toJsonTree(parent, new TypeToken<List<MarksEntity>>() {}.getType());

            jsonObject.addProperty("iTotalRecords", resultList.size());
            jsonObject.addProperty("iTotalDisplayRecords", resultList.size());
            jsonObject.add("aaData", output);
            jsonObject.addProperty("sEcho", params.get("sEcho"));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return jsonObject;
    }
}
