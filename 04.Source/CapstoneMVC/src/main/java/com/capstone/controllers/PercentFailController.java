package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.RealSemesterEntity;
import com.capstone.services.*;
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
        ISubjectService service1 = new SubjectServiceImpl();
        ICourseService service2 = new CourseServiceImpl();
        ModelAndView view = new ModelAndView("ByClassPercent");
        view.addObject("subjects", service1.getAllSubjects());
        view.addObject("classes", service2.getAllCourseToString());
        return view;
    }

    @RequestMapping("/query")
    @ResponseBody
    public JsonObject GetStudentFailPercent(@RequestParam Map<String, String> params) {
        JsonObject data = new JsonObject();

        String subjectId = params.get("subject");
        String courseId = params.get("course");

        EntityManagerFactory fac = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager manager = fac.createEntityManager();

        Comparator<MarksEntity> comparator = new Comparator<MarksEntity>() {
            @Override
            public int compare(MarksEntity o1, MarksEntity o2) {
                return new CompareToBuilder()
                        .append(o1.getSubjectId().getSubjectId().toUpperCase(), o2.getSubjectId().getSubjectId().toUpperCase())
                        .append(o1.getStudentId().getRollNumber().toUpperCase(), o2.getStudentId().getRollNumber().toUpperCase())
                        .toComparison();
            }
        };

        ArrayList<ArrayList<String>> parent = new ArrayList<>();

        IRealSemesterService service1 = new RealSemesterServiceImpl();
        for (RealSemesterEntity r : service1.getAllSemester()) {
            TypedQuery<MarksEntity> query = manager.createQuery("SELECT m FROM MarksEntity m WHERE " +
                    "m.subjectId.subjectId = :sub " +
                    "AND m.courseId.class1 LIKE :course " +
                    "AND m.semesterId.id = :semester", MarksEntity.class);
            query.setParameter("sub", subjectId);
            query.setParameter("course", "%" + courseId + "%");
            query.setParameter("semester", r.getId());
            List<MarksEntity> list = query.getResultList();
            if (!list.isEmpty()) {
                List<MarksEntity> filtered = FillterPassFailList(list, comparator);
                long failed = filtered.stream().filter(c -> !c.getStatus().toLowerCase().contains("pass")).count();
                ArrayList<String> tmp = new ArrayList<>();
                tmp.add(subjectId);
                tmp.add(r.getSemester());
                tmp.add(courseId);

                float percent = ((float)failed / (float)filtered.stream().count()) * 100f;
                tmp.add(String.valueOf(Math.round(percent) + "%"));
                parent.add(tmp);
            }
        }

        JsonArray output = (JsonArray) new Gson().toJsonTree(parent.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList()));

        data.addProperty("iTotalRecords", parent.size());
        data.addProperty("iTotalDisplayRecords", parent.size());
        data.add("aaData", output);
        data.addProperty("sEcho", params.get("sEcho"));

        return data;
    }

    public List<MarksEntity> FillterPassFailList(List<MarksEntity> list, Comparator comparator) {
        Map<String, List<MarksEntity>> map = new HashMap<>();
        for (MarksEntity m : list) {
            if (map.get(m.getStudentId().getRollNumber()) != null)  {
                map.get(m.getStudentId().getRollNumber()).add(m);
            } else {
                List<MarksEntity> tmp = new ArrayList<>();
                tmp.add(m);
                map.put(m.getStudentId().getRollNumber(), tmp);
            }
        }

        List<MarksEntity> result = new ArrayList<>();
        for (Map.Entry<String, List<MarksEntity>> entry : map.entrySet()) {
            MarksEntity tmp = null;
            for (MarksEntity e: entry.getValue()) {
                tmp = e;
                if (e.getStatus().contains("passs")) {
                    break;
                }
            }

            result.add(tmp);
        }

        return result;
    }
}
