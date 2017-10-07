package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.models.FailPrequisiteModel;
import com.capstone.models.Ultilities;
import com.capstone.services.ISubjectService;
import com.capstone.services.SubjectServiceImpl;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            List<SubjectEntity> pres;
            if (subId.equals("0")) {
                pres = service.getAlllPrequisite();
            } else {
                pres = service.getAllPrequisiteSubjects(subId);
            }

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

    @RequestMapping(value = "/getFailStudents")
    @ResponseBody
    public JsonObject GetStudents(@RequestParam Map<String, String> params) {
        try {
            String pres = params.get("prequisiteId").trim();
            System.out.println(pres);

            JsonObject data = new JsonObject();

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager manager = emf.createEntityManager();

            String sub = params.get("subId").trim();

            String[] p = pres.split(",");

            List<FailPrequisiteModel> result;
            if (sub.equals("0")) {
                ISubjectService service = new SubjectServiceImpl();
                result = new ArrayList<>();
                if (p.length > 0) {
                    for (String i : p) {
                        SubjectEntity pre = service.findSubjectById(i);
                        if (pre != null) {
                            for (SubjectEntity s: pre.getSubjectEntityList1()) {
                                TypedQuery<MarksEntity> query = manager.createQuery("SELECT c FROM MarksEntity c WHERE c.subjectId.subjectId = :sub OR c.subjectId.subjectId IN :sList", MarksEntity.class);
                                List<MarksEntity> list = query.setParameter("sub", s.getId()).setParameter("sList", Arrays.asList(p)).getResultList();
                                Ultilities.FilterStudentPassedSubFailPrequisite(list, s.getId(), p).forEach(c -> result.add(c));
                            }
                        }
                    }
                }
            } else {
                TypedQuery<MarksEntity> query = manager.createQuery("SELECT c FROM MarksEntity c WHERE c.subjectId.subjectId = :sub OR c.subjectId.subjectId IN :sList", MarksEntity.class);
                List<MarksEntity> list = query.setParameter("sub", sub).setParameter("sList", Arrays.asList(p)).getResultList();
                result = Ultilities.FilterStudentPassedSubFailPrequisite(list, sub, p);
            }

            List<FailPrequisiteModel> displayList = new ArrayList<>();
            if (!result.isEmpty()) {
                displayList = result.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
            }

            ArrayList<ArrayList<String>> parent = new ArrayList<>();
            if (!displayList.isEmpty()) {
                displayList.forEach(m -> {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(m.getMark().getStudentId().getRollNumber());
                    tmp.add(m.getMark().getStudentId().getFullName());
                    tmp.add(m.getSubjectWhichPrequisiteFail() == null ? "N/A" : m.getSubjectWhichPrequisiteFail());
                    tmp.add(m.getMark().getSubjectId() == null ? "N/A" : m.getMark().getSubjectId().getSubjectId());
                    tmp.add(m.getMark().getCourseId() == null ? "N/A" : m.getMark().getCourseId().getClass1());
                    tmp.add(m.getMark().getSemesterId() == null ? "N/A" : m.getMark().getSemesterId().getSemester());
                    tmp.add(String.valueOf(m.getMark().getAverageMark()));
                    tmp.add(m.getMark().getStatus());
                    parent.add(tmp);
                });
            }

            JsonArray output = (JsonArray) new Gson().toJsonTree(parent, new TypeToken<List<MarksEntity>>() {
            }.getType());

            data.addProperty("iTotalRecords", result.size());
            data.addProperty("iTotalDisplayRecords", result.size());
            data.add("aaData", output);
            data.addProperty("sEcho", params.get("sEcho"));

            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
