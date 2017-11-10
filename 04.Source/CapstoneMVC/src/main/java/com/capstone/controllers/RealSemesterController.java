package com.capstone.controllers;

import com.capstone.entities.RealSemesterEntity;
import com.capstone.services.IRealSemesterService;
import com.capstone.services.RealSemesterServiceImpl;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/managerrole")
public class RealSemesterController {

    @RequestMapping("/semester")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("RealSemesterList");
        IRealSemesterService realSemesterService = new RealSemesterServiceImpl();
        List<RealSemesterEntity> real =  realSemesterService.getAllSemester();
        view.addObject("semesters", real);
        return view;
    }

    @RequestMapping("/semester/edit")
    @ResponseBody
    public JsonObject TurnOnOff(@RequestParam int semesterId, @RequestParam boolean onoff) {
        JsonObject obj = new JsonObject();
        try {
            IRealSemesterService service = new RealSemesterServiceImpl();
            RealSemesterEntity semester = service.findSemesterById(semesterId);
            semester.setActive(onoff);
            service.update(semester);
            obj.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            obj.addProperty("success", false);
            obj.addProperty("msg", e.getMessage());
        }

        return obj;
    }

    @RequestMapping("/semester/create")
    @ResponseBody
    public JsonObject Create(@RequestParam String name) {
        JsonObject obj = new JsonObject();
        try {
            IRealSemesterService service = new RealSemesterServiceImpl();
            RealSemesterEntity semester = new RealSemesterEntity();
            semester.setActive(true);
            semester.setSemester(name);
            service.createRealSemester(semester);
            obj.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            obj.addProperty("success", false);
            obj.addProperty("msg", e.getMessage());
        }

        return obj;
    }
}
