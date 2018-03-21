package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.models.Ultilities;
import com.capstone.services.IMarksService;
import com.capstone.services.ISubjectService;
import com.capstone.services.MarksServiceImpl;
import com.capstone.services.SubjectServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class StudentStudyingController {

    @RequestMapping("/studying")
    public ModelAndView Index(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView view = new ModelAndView("Studying");
        String[] statuses = {"Studying"};
        ISubjectService subjectService = new SubjectServiceImpl();
        view.addObject("list", subjectService.getSubjectsByMarkStatus(statuses));
        return view;
    }

    @RequestMapping(value = "/getStudyingStudents")
    @ResponseBody
    public JsonObject GetStudents(@RequestParam Map<String, String> params) {
        try {
            JsonObject data = new JsonObject();
            IMarksService marksService = new MarksServiceImpl();

            String subId = params.get("subId");

            String[] statuses = {"Studying"};
            List<MarksEntity> dataList;
            if (subId.equals("0")) {
                dataList = marksService.getStudyingStudents(null, statuses);
            } else {
                dataList = marksService.getStudyingStudents(subId, statuses);
            }

            List<MarksEntity> displayList = new ArrayList<>();
            if (!dataList.isEmpty()) {
                displayList = dataList.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
            }

            ArrayList<ArrayList<String>> result = new ArrayList<>();
            if (!displayList.isEmpty()) {
                displayList.forEach(m -> {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(m.getStudentId().getRollNumber());
                    tmp.add(m.getStudentId().getFullName());
                    tmp.add(m.getSubjectMarkComponentId() == null ? "N/A" : m.getSubjectMarkComponentId().getSubjectId().getId());
                    tmp.add(m.getSemesterId() == null ? "N/A" : m.getSemesterId().getSemester());
                    tmp.add(String.valueOf(m.getAverageMark()));
                    tmp.add(m.getStatus());
                    tmp.add(m.getStudentId().getId() + "");
                    result.add(tmp);
                });
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

            data.addProperty("iTotalRecords", dataList.size());
            data.addProperty("iTotalDisplayRecords", dataList.size());
            data.add("aaData", aaData);
            data.addProperty("sEcho", params.get("sEcho"));

            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
