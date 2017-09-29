package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.models.MarkModel;
import com.capstone.models.StudentMarkModel;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class StudentDetail {

    IStudentService service = new StudentServiceImpl();
    IMarksService service2 = new MarksServiceImpl();

    @RequestMapping("/studentDetail")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("StudentDetail");
        view.addObject("students", service.findAllStudents());
        return view;
    }

    @RequestMapping("/getStudentDetail")
    @ResponseBody
    public JsonObject GetStudentDetail(@RequestParam Map<String, String> params) {
        JsonObject data = new JsonObject();

        try {
            List<MarksEntity> list = service2.getStudentMarksById(Integer.parseInt(params.get("stuId")));

            Map<String, List<MarksEntity>> map = new HashMap<>();
            List<MarksEntity> set = new ArrayList<>();
            List<MarksEntity> set2 = new ArrayList<>();
            if (!list.isEmpty()) {
                List<MarksEntity> filtered = list.stream().filter(a -> a.getSubjectId() != null).collect(Collectors.toList());
                for (MarksEntity m : filtered) {
                    if (map.get(m.getSubjectId().getSubjectId()) == null) {
                        List<MarksEntity> tmp = new ArrayList<>();
                        tmp.add(m);
                        map.put(m.getSubjectId().getSubjectId(), Ultilities.SortMarkBySemester(tmp));
                    } else {
                        map.get(m.getSubjectId().getSubjectId()).add(m);
                        Ultilities.SortMarkBySemester(map.get(m.getSubjectId().getSubjectId()));
                    }
                }

                for (Map.Entry<String, List<MarksEntity>> cell : map.entrySet()) {
                    if (cell.getValue().get(cell.getValue().size() - 1).getStatus().equals("Fail")) {
                        set.add(cell.getValue().get(cell.getValue().size() - 1));
                    }
                }

                set.stream().filter(a -> a.getSubjectId() == null).forEach(c -> {
                    if (c.getStatus().equals("Fail")) set.add(c);
                });

                set2 = set.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
            }

            ArrayList<ArrayList<String>> parent = new ArrayList<>();
            if (!set2.isEmpty()) {
                set2.forEach(m -> {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(m.getStudentId().getRollNumber());
                    tmp.add(m.getStudentId().getFullName());
                    tmp.add(m.getSubjectId() == null ? "N/A" : m.getSubjectId().getSubjectId());
                    tmp.add(m.getCourseId() == null ? "N/A" : m.getCourseId().getClazz());
                    tmp.add(m.getSemesterId() == null ? "N/A" : m.getSemesterId().getSemester());
                    tmp.add(String.valueOf(m.getAverageMark()));
                    tmp.add(m.getStatus());
                    parent.add(tmp);
                });
            }

            JsonArray result = (JsonArray) new Gson().toJsonTree(parent, new TypeToken<List<MarksEntity>>() {
            }.getType());

            data.addProperty("iTotalRecords", set.size());
            data.addProperty("iTotalDisplayRecords", set.size());
            data.add("aaData", result);
            data.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }
}
