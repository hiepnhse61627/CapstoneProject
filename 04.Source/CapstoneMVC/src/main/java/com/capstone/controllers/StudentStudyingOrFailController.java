package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.models.Logger;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
public class StudentStudyingOrFailController {

    @RequestMapping("/studyingorfail")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("StudentStudyingOrFail");
        ICurriculumService curriculumService = new CurriculumServiceImpl();
        IRealSemesterService realSemesterService = new RealSemesterServiceImpl();
        view.addObject("cur", curriculumService.getAllCurriculums());
        view.addObject("semester", Ultilities.SortSemesters(realSemesterService.getAllSemester()));
        return view;
    }

    @RequestMapping("/getcurriculumtermlist")
    @ResponseBody
    public JsonObject GetTerms(@RequestParam String term) {
        JsonObject data = new JsonObject();

        ICurriculumService curriculumService = new CurriculumServiceImpl();

        EntityManagerFactory fac = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = fac.createEntityManager();

        TypedQuery<String> query = em.createQuery("SELECT DISTINCT a.termNumber FROM SubjectCurriculumEntity a WHERE a.curriculumId.id = :id", String.class);
        query.setParameter("id", Integer.parseInt(term));
        List<String> list = query.getResultList();
        JsonArray aaData = (JsonArray) new Gson().toJsonTree(list);

        data.addProperty("success", true);
        data.add("data", aaData);

        return data;
    }

    @RequestMapping("/getstudyingorfail")
    @ResponseBody
    public JsonObject GetStudentFail(@RequestParam Map<String, String> params) {
        JsonObject data = new JsonObject();

        String curId = params.get("curId");
        String term = params.get("term");
        String semester = params.get("semester");

        try {
            ICurriculumService curriculumService = new CurriculumServiceImpl();
            IRealSemesterService semesterService = new RealSemesterServiceImpl();

            EntityManagerFactory fac = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = fac.createEntityManager();

            CurriculumEntity cur = curriculumService.getCurriculumById(Integer.parseInt(curId));
            if (cur != null) {
                List<RealSemesterEntity> realSemesters = Ultilities.SortSemesters(semesterService.getAllSemester());

                List<String> subject = new ArrayList<>();
                List<SubjectCurriculumEntity> listCur = cur.getSubjectCurriculumEntityList();
                for (SubjectCurriculumEntity c : listCur) {
                    if (c.getTermNumber() == Integer.parseInt(term)) {
                        if (!subject.contains(c.getSubjectId().getId())) {
                            subject.add(c.getSubjectId().getId());
                        }
                    }
                }

                int row = -1;
                for (RealSemesterEntity r : realSemesters) {
                    if (r.getId() == Integer.parseInt(semester)) {
                        row = realSemesters.indexOf(r);
                    }
                }

                List<MarksEntity> list = new ArrayList<>();
                if (row < 0) {
                    TypedQuery<MarksEntity> query = em.createQuery("SELECT a FROM MarksEntity a WHERE a.subjectMarkComponentId.subjectId.id IN :list", MarksEntity.class);
                    query.setParameter("list", subject);
                    list = Ultilities.FilterListFailStudent(query.getResultList());
                } else {
                    for (int i = 0; i < row + 1; i++) {
                        TypedQuery<MarksEntity> query = em.createQuery("SELECT a FROM MarksEntity a WHERE a.subjectMarkComponentId.subjectId.id IN :list AND a.semesterId.id = :semester", MarksEntity.class);
                        query.setParameter("list", subject);
                        query.setParameter("semester", realSemesters.get(i).getId());
                        List<MarksEntity> finalList = list;
                        Ultilities.FilterListFailStudent(query.getResultList()).forEach(c -> {
                            if (!finalList.contains(c)) {
                                finalList.add(c);
                            }
                        });
                        list = finalList;
                    }
                }

                List<MarksEntity> displayList = new ArrayList<>();
                if (!list.isEmpty()) {
                    displayList = list.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
                }

                ArrayList<ArrayList<String>> result = new ArrayList<>();
                if (!displayList.isEmpty()) {
                    displayList.forEach(m -> {
                        ArrayList<String> tmp = new ArrayList<>();
                        tmp.add(m.getStudentId().getRollNumber());
                        tmp.add(m.getStudentId().getFullName());
                        tmp.add(m.getSubjectMarkComponentId() == null ? "N/A" : m.getSubjectMarkComponentId().getSubjectId().getId());
                        tmp.add(m.getSemesterId() == null ? "N/A" : m.getSemesterId().getSemester());
                        tmp.add(String.valueOf(m.getAverageMark()));
                        tmp.add(m.getStatus());
                        tmp.add(m.getStudentId().getId() + "");
                        result.add(tmp);
                    });
                }

                JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

                data.addProperty("iTotalRecords", list.size());
                data.addProperty("iTotalDisplayRecords", list.size());
                data.add("aaData", aaData);
                data.addProperty("sEcho", params.get("sEcho"));
            }
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }

        return data;
    }
}
