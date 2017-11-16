package com.capstone.controllers;

import com.capstone.entities.RealSemesterEntity;
import com.capstone.models.CustomRealSemesterEntity;
import com.capstone.services.IRealSemesterService;
import com.capstone.services.RealSemesterServiceImpl;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/managerrole")
public class RealSemesterController {

    @RequestMapping("/semester")
    public ModelAndView Index(HttpServletRequest request) {
        ModelAndView view = new ModelAndView("RealSemesterList");
        IRealSemesterService realSemesterService = new RealSemesterServiceImpl();
        List<RealSemesterEntity> real = realSemesterService.getAllSemester();
        List<CustomRealSemesterEntity> r2 = new ArrayList<>();
        for (RealSemesterEntity r : real) {
            CustomRealSemesterEntity custom = new CustomRealSemesterEntity();
            custom.setEntity(r);
            try {
                String realPath = request.getServletContext().getRealPath("/") + "CloseSemester/" + r.getSemester() + "/";
                File file = new File(realPath);
                if (file.exists() && file.isDirectory()) {
                    custom.setLink("/managerrole/get/" + r.getSemester());
                } else {
                    custom.setLink("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            r2.add(custom);
        }
        view.addObject("semesters", r2);
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
