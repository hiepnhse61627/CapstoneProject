package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.services.ISubjectService;
import com.capstone.services.SubjectServiceImpl;
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

@Controller
public class StudentFailPrequisite {

    @RequestMapping("/checkPrequisite")
    public ModelAndView Index() {
        ISubjectService service = new SubjectServiceImpl();

        ModelAndView view = new ModelAndView("StudentFailPrequisite");
        view.addObject("subs", service.getAllSubjects());

        return view;
    }

    @RequestMapping("/getAllPrequisites")
    @ResponseBody
    public JsonObject getPrequisites(@RequestParam String subId) {
        JsonObject obj = new JsonObject();

        ISubjectService service = new SubjectServiceImpl();
        try {
            List<SubjectEntity> pres = service.getAllPrequisiteSubjects(subId);
            JsonArray o1 = new JsonArray();
            for (SubjectEntity p : pres) {
                JsonObject o2 = new JsonObject();
                o2.addProperty("value", p.getId());
                o2.addProperty("name", p.getId() + " - " + p.getName() + " - " + p.getAbbreviation());
                o1.add(o2);
            }
            obj.addProperty("success", true);
            obj.add("data", o1);
        } catch (Exception e) {
            e.printStackTrace();
            obj.addProperty("success", false);
            return obj;
        }

        return obj;
    }
}
