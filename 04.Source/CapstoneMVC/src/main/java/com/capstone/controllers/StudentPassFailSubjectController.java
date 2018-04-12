package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.RealSemesterEntity;
import com.capstone.models.Global;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class StudentPassFailSubjectController {

    @RequestMapping("/passfail")
    public ModelAndView Index(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView view = new ModelAndView("StudentPassFailSubject");
        view.addObject("title", "DSSV đậu rớt");
        IRealSemesterService realSemesterService = new RealSemesterServiceImpl();
        List<RealSemesterEntity> reals = Ultilities.SortSemesters(realSemesterService.getAllSemester());
        view.addObject("semesters", Lists.reverse(reals));
        ISubjectService subjectService = new SubjectServiceImpl();
        view.addObject("subjects", subjectService.getAllSubjects());
        return view;
    }

    @RequestMapping("/passfail/get")
    @ResponseBody
    public JsonObject GetData(@RequestParam Map<String, String> params) {
//        List<RealSemesterEntity> list = Global.getSortedList();
//        List<RealSemesterEntity> processedSemesters = list.stream().filter(c -> list.indexOf(c) <= list.indexOf(list.stream().filter(a -> a.getId() == Integer.parseInt(semesterId)).findFirst().get())).collect(Collectors.toList());
//        List<String> tmp = processedSemesters.stream().map(c -> c.getSemester()).collect(Collectors.toList());

        JsonObject jsonObj = new JsonObject();

        try {
            int semesterId = Integer.parseInt(params.get("semesterId"));
            String subjectId = params.get("subjectId");
            int passfail =  Integer.parseInt(params.get("passfail"));

            IMarksService service = new MarksServiceImpl();
            List<MarksEntity> marks = service.findMarksByStudentIdAndSubjectCdAndSemesterId(0, subjectId, semesterId);
            marks = marks.stream().filter(c -> c.getIsActivated() && c.getEnabled() != null && c.getEnabled()).collect(Collectors.toList());
            marks = Ultilities.SortSemestersByMarks(marks);
            Map<String, List<MarksEntity>> map = marks
                    .stream()
                    .collect(Collectors.groupingBy(c -> c.getStudentId().getRollNumber()));

            List<List<String>> data = new ArrayList<>();
            map.entrySet().forEach(c -> {
                List<MarksEntity> stuMarks = c.getValue();
                stuMarks.forEach(a -> {
                    List<String> tmp = new ArrayList<>();

                    if (passfail == 1) {
                        if (a.getStatus().toLowerCase().contains("pass") || a.getStatus().toLowerCase().contains("exempt")) {
                            tmp.add(a.getStudentId().getRollNumber());
                            tmp.add(a.getStudentId().getFullName());
                            tmp.add(a.getSemesterId().getSemester());
                            tmp.add(String.valueOf(a.getAverageMark()));
                            tmp.add(a.getStatus());
                            data.add(tmp);
                        }
                    } else {
                        if (a.getStatus().toLowerCase().contains("fail")) {
                            tmp.add(a.getStudentId().getRollNumber());
                            tmp.add(a.getStudentId().getFullName());
                            tmp.add(a.getSemesterId().getSemester());
                            tmp.add(String.valueOf(a.getAverageMark()));
                            tmp.add(a.getStatus());
                            data.add(tmp);
                        }
                    }
                });
            });

            Gson gson = new Gson();
            JsonArray array = (JsonArray) gson.toJsonTree(data);

//        jsonObj.addProperty("iTotalRecords", array.size());
//        jsonObj.addProperty("iTotalDisplayRecords", array.size());
            jsonObj.add("aaData", array);
//        jsonObj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
    }
}
