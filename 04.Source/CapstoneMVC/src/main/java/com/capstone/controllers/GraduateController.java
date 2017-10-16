package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.models.Logger;
import com.capstone.services.IMarksService;
import com.capstone.services.IStudentService;
import com.capstone.services.MarksServiceImpl;
import com.capstone.services.StudentServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class GraduateController {

    @RequestMapping("/graduate")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("StudentGraduate");
        view.addObject("title", "Danh sách xét tốt nghiệp");
        return view;
    }

    @RequestMapping("/processgraduate")
    @ResponseBody
    public JsonObject GetGraduateStudents(@RequestParam Map<String, String> params) {
        JsonObject obj = new JsonObject();

        IMarksService service = new MarksServiceImpl();

        try {
            Map<StudentEntity, List<MarksEntity>> map = new HashMap<>();
            List<MarksEntity> marks = service.getAllMarks();
            for (MarksEntity mark: marks) {
                if (map.get(mark.getStudentId()) != null) {
                    map.get(mark.getStudentId()).add(mark);
                } else {
                    List<MarksEntity> tmp = new ArrayList<>();
                    tmp.add(mark);
                    map.put(mark.getStudentId(), tmp);
                }
            }

            ArrayList<ArrayList<String>> parent = new ArrayList<>();
            System.out.println("Credit: " + params.get("creditPass"));
            for (Map.Entry<StudentEntity, List<MarksEntity>> entry : map.entrySet()) {
                int credits = 0;
                for (MarksEntity c : entry.getValue()) {
                    if (c.getStatus().toLowerCase().contains("pass")) credits += c.getSubjectId() == null ? 0 : c.getSubjectId().getSubjectEntity().getCredits();
                }

                if (credits >= Integer.parseInt(params.get("creditPass").isEmpty() ? "0" : params.get("creditPass"))) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(entry.getKey().getRollNumber());
                    tmp.add(entry.getKey().getFullName());
                    tmp.add(String.valueOf(credits));
                    parent.add(tmp);
                }
            }

            List<ArrayList<String>> result = parent.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
            int size = parent.size();

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result, new TypeToken<List<ArrayList<String>>>() {}.getType());

            obj.addProperty("iTotalRecords", size);
            obj.addProperty("iTotalDisplayRecords", size);
            obj.add("aaData", aaData);
            obj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Logger.writeLog(e);
        }

        return obj;
    }
}
