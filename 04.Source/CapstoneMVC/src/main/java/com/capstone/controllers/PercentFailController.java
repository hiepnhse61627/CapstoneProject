package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.RealSemesterEntity;
import com.capstone.models.FailPrequisiteModel;
import com.capstone.models.Global;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/percent")
public class PercentFailController {

    @RequestMapping("/index")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("ByClassPercent");
        view.addObject("title", "Tỉ lệ môn đạt");

        ISubjectService service1 = new SubjectServiceImpl();
        ICourseService service2 = new CourseServiceImpl();
        view.addObject("subjects", service1.getAllSubjects());
        view.addObject("classes", service2.getAllCourse());
        return view;
    }

    @RequestMapping("/query")
    @ResponseBody
    public JsonObject GetStudentFailPercent(@RequestParam Map<String, String> params) {
        JsonObject data = new JsonObject();

        List<List<String>> parent = processFailPercent(params);
        String searchKey = params.get("sSearch").toLowerCase();
        parent = parent.stream().filter(c -> c.get(0).toLowerCase().contains(searchKey) ||
                c.get(1).toLowerCase().contains(searchKey)).collect(Collectors.toList());

        JsonArray output = (JsonArray) new Gson().toJsonTree(parent.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList()));

        data.addProperty("iTotalRecords", parent.size());
        data.addProperty("iTotalDisplayRecords", parent.size());
        data.add("aaData", output);
        data.addProperty("sEcho", params.get("sEcho"));

        return data;
    }

    public List<List<String>> processFailPercent(Map<String, String> params) {
        String subjectId = params.get("subject");
        String courseId = params.get("course");

        EntityManagerFactory fac = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager manager = fac.createEntityManager();

        List<List<String>> parent = new ArrayList<>();

        ICourseService service = new CourseServiceImpl();

        try {
            String queryStr = "SELECT a FROM MarksEntity a WHERE a.isActivated = TRUE";
            if (!subjectId.equals("0")) {
                queryStr += " AND a.subjectMarkComponentId.subjectId.id = :subject";
            }
            if (!courseId.equals("0")) {
                queryStr += " AND a.courseId.id = :course";
            }

            TypedQuery<MarksEntity> query = manager.createQuery(queryStr, MarksEntity.class);
            if (!subjectId.equals("0")) {
                query.setParameter("subject", subjectId);
            }
            if (!courseId.equals("0")) {
                query.setParameter("course", Integer.parseInt(courseId));
            }

            List<MarksEntity> list = query.getResultList();
            list = Global.TransformMarksList(list);

            if (!list.isEmpty()) {
                Table<Integer, String, List<MarksEntity>> filtered = FillterPassFailList(list);

                String sub = "";
                String class1 = "";
                int percent = 0;

                Set<Integer> courses = filtered.rowKeySet();
                for (Integer course : courses) {

                    class1 = service.findCourseById(course).getSemester();

                    Map<String, List<MarksEntity>> subjects = filtered.row(course);
                    for (Map.Entry<String, List<MarksEntity>> subject : subjects.entrySet()) {

                        sub = subject.getKey();

                        float total = (float) subject.getValue().stream().count();
                        float failed = (float) subject.getValue().stream().filter(c -> !c.getStatus().toLowerCase().contains("pass")).count();
                        percent = Math.round((failed / total) * 100f);

                        ArrayList<String> tmp = new ArrayList<>();
                        tmp.add(sub);
                        tmp.add(class1);
                        tmp.add(String.valueOf(100 - percent) + "% passed");
                        parent.add(tmp);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return parent;
    }

    private Table<Integer, String, List<MarksEntity>> FillterPassFailList(List<MarksEntity> list) {
        List<MarksEntity> removed = list.stream().filter(c -> c.getSubjectMarkComponentId() != null).collect(Collectors.toList());

        Table<Integer, String, List<MarksEntity>> map = HashBasedTable.create();
        for (MarksEntity m : removed) {
            if (map.get(m.getCourseId().getId(), m.getSubjectMarkComponentId().getSubjectId().getId()) == null) {
                List<MarksEntity> tmp = new ArrayList<>();
                tmp.add(m);
                map.put(m.getCourseId().getId(), m.getSubjectMarkComponentId().getSubjectId().getId(), tmp);
            } else {
                map.get(m.getCourseId().getId(), m.getSubjectMarkComponentId().getSubjectId().getId()).add(m);
            }
        }

        return map;
    }
}
