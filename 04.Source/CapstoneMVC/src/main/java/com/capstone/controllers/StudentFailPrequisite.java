package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.SubjectEntity;
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

    @RequestMapping(value = "/getFailStudents")
    @ResponseBody
    public JsonObject GetStudents(@RequestParam Map<String, String> params) {
        try {
            JsonObject data = new JsonObject();

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager manager = emf.createEntityManager();

            String sub = params.get("subId").trim();
            String pre = params.get("prequisiteId").trim();

            TypedQuery<MarksEntity> query = manager.createQuery("SELECT c FROM MarksEntity c WHERE c.subjectId.subjectId = :sub OR c.subjectId.subjectId = :pre", MarksEntity.class);
            List<MarksEntity> list = query.setParameter("sub", sub).setParameter("pre", pre).getResultList();

            List<MarksEntity> result = Ultilities.FilterStudentPassedSubFailPrequisite(list, sub, pre);

            ArrayList<ArrayList<String>> parent = new ArrayList<>();
            if (!result.isEmpty()) {
                result.forEach(m -> {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(m.getStudentId().getRollNumber());
                    tmp.add(m.getStudentId().getFullName());
                    tmp.add(m.getSubjectId() == null ? "N/A" : m.getSubjectId().getSubjectId());
                    tmp.add(m.getCourseId() == null ? "N/A" : m.getCourseId().getClass1());
                    tmp.add(m.getSemesterId() == null ? "N/A" : m.getSemesterId().getSemester());
                    tmp.add(String.valueOf(m.getAverageMark()));
                    tmp.add(m.getStatus());
                    tmp.add(m.getStudentId().getId() + "");
                    parent.add(tmp);
                });
            }

            JsonArray output = (JsonArray) new Gson().toJsonTree(parent, new TypeToken<List<MarksEntity>>() {}.getType());

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
