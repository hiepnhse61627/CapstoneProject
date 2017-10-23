package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.RealSemesterEntity;
import com.capstone.models.FailPrequisiteModel;
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
        view.addObject("title", "Tỉ lệ sinh viên rớt môn");

        ISubjectService service1 = new SubjectServiceImpl();
        ICourseService service2 = new CourseServiceImpl();
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

        Comparator<MarksEntity> comparator = (o1, o2) -> new CompareToBuilder()
                .append(o1.getSubjectMarkComponentId() == null ? "" : o1.getSubjectMarkComponentId().getSubjectId().toUpperCase(), o2.getSubjectMarkComponentId() == null ? "" : o2.getSubjectMarkComponentId().getSubjectId().toUpperCase())
                .append(o1.getStudentId().getRollNumber().toUpperCase(), o2.getStudentId().getRollNumber().toUpperCase())
                .toComparison();

        EntityManagerFactory fac = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager manager = fac.createEntityManager();

        ArrayList<ArrayList<String>> parent = new ArrayList<>();

        try {
            IRealSemesterService service1 = new RealSemesterServiceImpl();
            for (RealSemesterEntity r : Ultilities.SortSemesters(service1.getAllSemester())) {

                TypedQuery<MarksEntity> query;

                if (subjectId.equals("0") && courseId.equals("0")) {
                    query = manager.createQuery("SELECT m FROM MarksEntity m WHERE " +
                            "m.semesterId.id = :semester", MarksEntity.class);
                    query.setParameter("semester", r.getId());
                } else if (!subjectId.equals("0") && courseId.equals("0")) {
                    query = manager.createQuery("SELECT m FROM MarksEntity m WHERE " +
                            "m.subjectMarkComponentId.subjectId = :sub " +
                            "AND m.semesterId.id = :semester", MarksEntity.class);
                    query.setParameter("sub", subjectId);
                    query.setParameter("semester", r.getId());
                } else if (subjectId.equals("0") && !courseId.equals("0")) {
                    query = manager.createQuery("SELECT m FROM MarksEntity m WHERE " +
                            "m.courseId.class1 LIKE :course " +
                            "AND m.semesterId.id = :semester", MarksEntity.class);
                    query.setParameter("course", "%" + courseId + "%");
                    query.setParameter("semester", r.getId());
                } else {
                    query = manager.createQuery("SELECT m FROM MarksEntity m WHERE " +
                            "m.subjectMarkComponentId.subjectId = :sub " +
                            "AND m.courseId.class1 LIKE :course " +
                            "AND m.semesterId.id = :semester", MarksEntity.class);
                    query.setParameter("sub", subjectId);
                    query.setParameter("course", "%" + courseId + "%");
                    query.setParameter("semester", r.getId());
                }

                List<MarksEntity> list = query.getResultList();
                if (!list.isEmpty()) {
                    Table<String, String, List<MarksEntity>> filtered = FillterPassFailList(list);

                    String sem = "";
                    String sub = "";
                    String class1 = "";
                    int percent = 0;

                    Set<String> l = filtered.rowKeySet();
                    for (String semester : l) {
                        sem = semester;
                        Map<String, List<MarksEntity>> subject = filtered.row(semester);
                        for (Map.Entry<String, List<MarksEntity>> m : subject.entrySet()) {
                            sub = m.getKey();

                            Map<String, List<String>> map = new HashMap<>();
                            for (MarksEntity mark : m.getValue()) {
                                if (map.get(mark.getCourseId().getClass1().trim()) != null) {
                                    map.get(mark.getCourseId().getClass1().trim()).add(mark.getStatus());
                                } else {
                                    List<String> tmp2 = new ArrayList<>();
                                    tmp2.add(mark.getStatus());
                                    map.put(mark.getCourseId().getClass1().trim(), tmp2);
                                }
                            }

                            for (Map.Entry<String, List<String>> last : map.entrySet()) {
                                class1 = last.getKey();

                                float total = (float)last.getValue().stream().count();
                                float failed = (float)last.getValue().stream().filter(c -> !c.toLowerCase().contains("pass")).count();
                                percent = Math.round((failed / total) * 100f);

                                ArrayList<String> tmp = new ArrayList<>();
                                tmp.add(sem);
                                tmp.add(sub);
                                tmp.add(class1);
                                tmp.add(String.valueOf(percent) + "% failed");
                                parent.add(tmp);
                            }
                        }
                    }

//                long failed = filtered.stream().filter(c -> !c.getStatus().toLowerCase().contains("pass")).count();
//                ArrayList<String> tmp = new ArrayList<>();
//                tmp.add(subjectId);
//                tmp.add(r.getSemester());
//                tmp.add(courseId);
//
//                float percent = ((float) failed / (float) filtered.stream().count()) * 100f;
//                tmp.add(String.valueOf(Math.round(percent) + "%"));
//                parent.add(tmp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonArray output = (JsonArray) new Gson().toJsonTree(parent.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList()));

        data.addProperty("iTotalRecords", parent.size());
        data.addProperty("iTotalDisplayRecords", parent.size());
        data.add("aaData", output);
        data.addProperty("sEcho", params.get("sEcho"));

        return data;
    }

    public Table<String, String, List<MarksEntity>> FillterPassFailList(List<MarksEntity> list) {
        List<MarksEntity> removed = list.stream().filter(c -> c.getSubjectMarkComponentId() != null).collect(Collectors.toList());
        Table<String, String, List<MarksEntity>> map = HashBasedTable.create();
        for (MarksEntity m : removed) {
            if (map.get(m.getSemesterId().getSemester(), m.getSubjectMarkComponentId().getSubjectId()) == null) {
                List<MarksEntity> tmp = new ArrayList<>();
                tmp.add(m);
                map.put(m.getSemesterId().getSemester(), m.getSubjectMarkComponentId().getSubjectId(), tmp);
            } else {
                map.get(m.getSemesterId().getSemester(), m.getSubjectMarkComponentId().getSubjectId()).add(m);
            }
        }

        return map;
    }
}
