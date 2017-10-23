package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.ProgramEntity;
import com.capstone.entities.RealSemesterEntity;
import com.capstone.entities.StudentEntity;
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

        int totalCredit = Integer.parseInt(params.get("credit").isEmpty() ? "0" : params.get("credit"));
        int sCredit = Integer.parseInt(params.get("sCredit").isEmpty() ? "0" : params.get("sCredit"));
        int programId = Integer.parseInt(params.get("programId"));
        int semesterId = Integer.parseInt(params.get("semesterId"));

        try {
            Map<StudentEntity, List<MarksEntity>> map = new HashMap<>();
            List<MarksEntity> marks = markService.getMarkByProgramAndSemester(programId, semesterId);
            for (MarksEntity mark : marks) {
                if (map.get(mark.getStudentId()) != null) {
                    map.get(mark.getStudentId()).add(mark);
                } else {
                    List<MarksEntity> tmp = new ArrayList<>();
                    tmp.add(mark);
                    map.put(mark.getStudentId(), tmp);
                }
            }

            ArrayList<ArrayList<String>> parent = new ArrayList<>();
            for (Map.Entry<StudentEntity, List<MarksEntity>> entry : map.entrySet()) {
                int credits = 0;
                int specializedCredits = 0;
                for (MarksEntity c : entry.getValue()) {
//                    if (c.getStatus().toLowerCase().contains("pass") && c.getSubjectId() != null) {
//                        System.out.println(c.getSubjectId().getSubjectId());
//                        int curCredit = c.getSubjectId().getSubjectEntity().getCredits();
//                        credits += curCredit;
//                        if (c.getSubjectId().getSubjectEntity().getIsSpecialized()) {
//                            specializedCredits += curCredit;
//                        }
//                    }
                }

                if (credits >= totalCredit && specializedCredits >= sCredit) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(entry.getKey().getRollNumber());
                    tmp.add(entry.getKey().getFullName());
                    tmp.add(String.valueOf(credits));
                    tmp.add(String.valueOf(specializedCredits));
                    parent.add(tmp);
                }
            }

            List<ArrayList<String>> result = parent.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
            int size = parent.size();

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result, new TypeToken<List<ArrayList<String>>>() {
            }.getType());

            obj.addProperty("iTotalRecords", size);
            obj.addProperty("iTotalDisplayRecords", size);
            obj.add("aaData", aaData);
            obj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
        }

        return obj;
    }
}


