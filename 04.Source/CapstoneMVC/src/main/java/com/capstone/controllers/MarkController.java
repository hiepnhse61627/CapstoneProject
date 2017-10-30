package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.models.Logger;
import com.capstone.services.IMarksService;
import com.capstone.services.MarksServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class MarkController {

    @RequestMapping("/markPage")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("MarkPage");
        view.addObject("title", "Quản lý điểm");

        return view;
    }

    @RequestMapping("/markPage/getMarkList")
    @ResponseBody
    public JsonObject GetMarkList(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();
        IMarksService markService = new MarksServiceImpl();

        int studentId = Integer.parseInt(params.get("studentId"));
        String sSearch = params.get("sSearch").trim();
        int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
        int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
        long iTotalRecords = 0;
        long iTotalDisplayRecords = 0;

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            String queryStr, querySelect;
            String queryFrom = " FROM MarksEntity m" +
                    " INNER JOIN StudentEntity s ON m.studentId.id = s.id" +
                    " INNER JOIN SubjectMarkComponentEntity smc ON m.subjectMarkComponentId.id = smc.id" +
                    " INNER JOIN MarkComponentEntity mc ON smc.markComponentId.id = mc.id" +
                    " INNER JOIN SubjectEntity sub ON smc.subjectId.id = sub.id" +
                    " AND mc.name LIKE :markComponentName";
            String querySearchValue =
                    " AND sub.id IN (SELECT sub1.id FROM SubjectEntity sub1" +
                            " WHERE sub1.id LIKE :subjectId OR sub1.name LIKE :subjectName)";
            String querySearchStudent =
                    " AND s.id = :studentId";
            String queryOrderBy =
                    " ORDER BY m.studentId.id";

            // Count all
            querySelect = "SELECT COUNT(m)";
            queryStr = querySelect + queryFrom;
            if (!sSearch.isEmpty()) {
                queryStr += querySearchValue;
            }
            if (studentId > 0) {
                queryStr += querySearchStudent;
            }

            TypedQuery<Integer> queryCount = em.createQuery(queryStr, Integer.class);
            queryCount.setParameter("markComponentName", "%average%");
            if (!sSearch.isEmpty()) {
                queryCount.setParameter("subjectId", "%" + sSearch + "%");
                queryCount.setParameter("subjectName", "%" + sSearch + "%");
            }
            if (studentId > 0) {
                queryCount.setParameter("studentId", studentId);
            }
            iTotalDisplayRecords = ((Number) queryCount.getSingleResult()).intValue();
            iTotalRecords = markService.countAllMarks();

            // Query data
            querySelect = "SELECT s.rollNumber, s.fullName, sub.id, sub.name, m.semesterId.semester, m.averageMark, m.status, m.id";
            queryStr = querySelect + queryFrom;
            if (!sSearch.isEmpty()) {
                queryStr += querySearchValue;
            }
            if (studentId > 0) {
                queryStr += querySearchStudent;
            }
            queryStr += queryOrderBy;

            Query queryData = em.createQuery(queryStr);
            queryData.setParameter("markComponentName", "%average%");
            if (!sSearch.isEmpty()) {
                queryData.setParameter("subjectId", "%" + sSearch + "%");
                queryData.setParameter("subjectName", "%" + sSearch + "%");
            }
            if (studentId > 0) {
                queryData.setParameter("studentId", studentId);
            }
            queryData.setFirstResult(iDisplayStart);
            queryData.setMaxResults(iDisplayLength);
            List<Object[]> list = queryData.getResultList();

            List<List<String>> result = new ArrayList<>();
            for (Object[] mark : list) {
                List<String> row = new ArrayList<>();
                row.add(mark[0].toString()); // Roll number
                row.add(mark[1].toString()); // Full name
                row.add(mark[2].toString()); // Subject code
                row.add(mark[3].toString()); // Subject name
                row.add(mark[4].toString()); // Semester
                row.add(mark[5].toString()); // Mark
                row.add(mark[6].toString()); // Status
                row.add(mark[7].toString()); // Mark id

                result.add(row);
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

            jsonObj.addProperty("iTotalRecords", iTotalRecords);
            jsonObj.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
            jsonObj.add("aaData", aaData);
            jsonObj.addProperty("sEcho", params.get("sEcho"));
            em.close();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
        }

        return jsonObj;
    }

    @RequestMapping("/markPage/edit")
    @ResponseBody
    public JsonObject EditMark(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();
        IMarksService markService = new MarksServiceImpl();

        int markId = Integer.parseInt(params.get("markId"));
        double mark = Double.parseDouble(params.get("mark"));
        String status = params.get("status");

        try {
            MarksEntity marksEntity = markService.getMarkById(markId);
            marksEntity.setAverageMark(mark);
            marksEntity.setStatus(status);
            markService.updateMark(marksEntity);

            jsonObj.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
            jsonObj.addProperty("success", false);
        }

        return jsonObj;
    }

}
