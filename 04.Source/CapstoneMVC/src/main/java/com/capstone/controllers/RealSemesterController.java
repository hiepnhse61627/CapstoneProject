package com.capstone.controllers;

import com.capstone.entities.RealSemesterEntity;
import com.capstone.models.CustomRealSemesterEntity;
import com.capstone.models.Jobs;
import com.capstone.models.Logger;
import com.capstone.models.Ultilities;
import com.capstone.services.IRealSemesterService;
import com.capstone.services.RealSemesterServiceImpl;
import com.google.common.collect.Lists;
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
import java.util.Map;

@Controller
@RequestMapping("/managerrole")
public class RealSemesterController {

    @RequestMapping("/semester")
    public ModelAndView Index(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView view = new ModelAndView("RealSemesterList");
        IRealSemesterService realSemesterService = new RealSemesterServiceImpl();
        List<RealSemesterEntity> real = Lists.reverse(Ultilities.SortSemesters(realSemesterService.getAllSemester()));
        List<CustomRealSemesterEntity> r2 = new ArrayList<>();
        for (RealSemesterEntity r : real) {
            CustomRealSemesterEntity custom = new CustomRealSemesterEntity();
            custom.setEntity(r);

            String loggerLocation = Logger.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String path = loggerLocation.substring(0, loggerLocation.indexOf("WEB-INF")) + "CloseSemester/";
            String realPath = path + r.getSemester() + "/";
            File dir = new File(realPath);

            if (r.getSemester().equals("SUMMER2017")) {
                System.out.println("");
            }

            if (Jobs.getJob(r.getSemester()) == null && !dir.exists()) {
                custom.setLink("");
                custom.setFinished(false);
            } else if (Jobs.getJob(r.getSemester()) != null && Jobs.getJob(r.getSemester()).equals("0")) {
                custom.setLink("Đang xử lý, xin đợi giây lát");
                custom.setFinished(false);
            } else if (Jobs.getJob(r.getSemester()) != null && Jobs.getJob(r.getSemester()).equals("1")) {
                custom.setLink("/managerrole/get/" + r.getSemester());
                custom.setFinished(true);
            } else if (dir.exists()) {
                custom.setLink("/managerrole/get/" + r.getSemester());
                custom.setFinished(true);
            } else {
                custom.setLink("");
                custom.setFinished(false);
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
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    IRealSemesterService service = new RealSemesterServiceImpl();
                    RealSemesterEntity semester = service.findSemesterById(semesterId);
                    //logging user action
                    Ultilities.logUserAction("Turn " + (onoff == true ? "on" : "off") + semester.getSemester());
                    semester.setActive(onoff);
                    service.update(semester);
                    System.out.println("Turned off " + semester.getSemester());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        obj.addProperty("thread", t.getId());
        return obj;
    }

    @RequestMapping("/semester/create")
    @ResponseBody
    public JsonObject Create(@RequestParam Map<String, String> params) {
        JsonObject obj = new JsonObject();
        try {
            String name = params.get("name");
            if (name.isEmpty()) {
                obj.addProperty("success", false);
                obj.addProperty("msg", "Không được để trống!");
                return obj;
            }
            Ultilities.logUserAction("Create semester" + name);

            String startDate = params.get("startDate");
            String endDate = params.get("endDate");

            IRealSemesterService service = new RealSemesterServiceImpl();
            RealSemesterEntity semester = new RealSemesterEntity();
            semester.setActive(true);
            semester.setSemester(name);
            semester.setStartDate(startDate);
            semester.setEndDate(endDate);
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
