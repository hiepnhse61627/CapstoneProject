package com.capstone.controllers;

import com.capstone.entities.MarkComponentEntity;
import com.capstone.entities.MarksEntity;
import com.capstone.models.Enums;
import com.capstone.models.Logger;
import com.capstone.models.Ultilities;
import com.capstone.services.IMarkComponentService;
import com.capstone.services.IMarksService;
import com.capstone.services.MarkComponentServiceImpl;
import com.capstone.services.MarksServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MarkController {

    @RequestMapping("/markPage")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("MarkPage");
        view.addObject("title", "Quản lý điểm");

        return view;
    }

    @RequestMapping("/markPage/getMarkList")
    @ResponseBody
    public JsonObject GetMarkList(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();
        IMarksService markService = new MarksServiceImpl();

        int studentId = Integer.parseInt(params.get("studentId"));
        String sSearch = params.get("sSearch").trim();
        int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
        int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
        long iTotalRecords = 0;
        long iTotalDisplayRecords = 0;

        try {
            List<MarksEntity> markList = markService.getMarksForMarkPage(studentId);

            if (studentId > 0) {
                iTotalRecords = markList.size();
            } else {
                iTotalRecords = markService.countAllMarks();
            }

            markList = markList.stream().filter(m ->
                    Ultilities.containsIgnoreCase(m.getSubjectMarkComponentId().getSubjectId().getId(), sSearch)
                    || Ultilities.containsIgnoreCase(m.getSubjectMarkComponentId().getSubjectId().getName(), sSearch)
                    || Ultilities.containsIgnoreCase(m.getSemesterId().getSemester(), sSearch)
                    || Ultilities.containsIgnoreCase(m.getStatus(), sSearch))
                    .collect(Collectors.toList());

            iTotalDisplayRecords = markList.size();

            List<List<String>> result = new ArrayList<>();
            List<MarksEntity> displayList = markList.stream().skip(iDisplayStart)
                    .limit(iDisplayLength).collect(Collectors.toList());
            for (MarksEntity m : displayList) {
                List<String> row = new ArrayList<>();
                row.add(m.getStudentId().getRollNumber()); // Roll number
                row.add(m.getStudentId().getFullName()); // Full name
                row.add(m.getSubjectMarkComponentId().getSubjectId().getId()); // Subject code
                row.add(m.getSubjectMarkComponentId().getSubjectId().getName()); // Subject name
                row.add(m.getSemesterId().getSemester()); // Semester
                row.add(String.valueOf(m.getAverageMark())); // Mark
                row.add(m.getStatus()); // Status
                row.add(String.valueOf(m.getId())); // Mark id

                result.add(row);
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

            jsonObj.addProperty("iTotalRecords", iTotalRecords);
            jsonObj.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
            jsonObj.add("aaData", aaData);
            jsonObj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
        }

        return jsonObj;
    }

    @RequestMapping("/markPage/edit")
    @ResponseBody
    public JsonObject EditMark(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();
        IMarksService markService = new MarksServiceImpl();

        int markId = Integer.parseInt(params.get("markId"));
        double mark = Double.parseDouble(params.get("mark"));
        String status = params.get("status");

        try {
            MarksEntity marksEntity = markService.getMarkById(markId);
            marksEntity.setAverageMark(mark);
            marksEntity.setStatus(status);
            markService.updateMark(marksEntity);

            jsonObj.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
            jsonObj.addProperty("success", false);
        }

        return jsonObj;
    }

    @RequestMapping("/markPage/delete")
    @ResponseBody
    public JsonObject DeleteMark(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();
        IMarksService markService = new MarksServiceImpl();

        int markId = Integer.parseInt(params.get("markId"));

        try {
            markService.deleteMark(markId);

            jsonObj.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
            jsonObj.addProperty("success", false);
        }

        return jsonObj;
    }

}
