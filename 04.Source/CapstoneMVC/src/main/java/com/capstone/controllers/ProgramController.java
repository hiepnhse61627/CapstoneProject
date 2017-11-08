package com.capstone.controllers;


import com.capstone.entities.ProgramEntity;
import com.capstone.models.Logger;
import com.capstone.models.ProgramModel;
import com.capstone.services.IProgramService;
import com.capstone.services.ProgramServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
public class ProgramController {
    IProgramService programService = new ProgramServiceImpl();

    @RequestMapping("/programDetail")
    public ModelAndView subjectCurriculumDetail() {
        ModelAndView view = new ModelAndView("Program");
        view.addObject("title", "Danh sách sinh viên chuyển ngành");

        return view;
    }

    @RequestMapping(value = "/loadProgramList")
    @ResponseBody
    public JsonObject LoadStudentListAll(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            String sSearch = params.get("sSearch");
            int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
            int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
            int iTotalRecords = 0;
            int iTotalDisplayRecords = 0;

            String queryStr;
            // Đếm số lượng program
            queryStr = "SELECT COUNT(p) FROM ProgramEntity p";
            TypedQuery<Integer> queryCounting = em.createQuery(queryStr, Integer.class);
            iTotalRecords = ((Number) queryCounting.getSingleResult()).intValue();

            // Query danh sách program
            queryStr = "SELECT p FROM ProgramEntity p";
            if (!sSearch.isEmpty()) {
                queryStr += " WHERE s.name LIKE :sName OR s.fullName LIKE :sFullName";
            }

            TypedQuery<ProgramEntity> query = em.createQuery(queryStr, ProgramEntity.class);
//                    .setFirstResult(iDisplayStart)
//                    .setMaxResults(iDisplayLength);

            if (!sSearch.isEmpty()) {
                query.setParameter("sName", "%" + sSearch + "%");
                query.setParameter("sFullName", "%" + sSearch + "%");
            }

            List<ProgramEntity> programEntityList = query.getResultList();
            iTotalDisplayRecords = programEntityList.size();
            List<List<String>> result = new ArrayList<>();
            programEntityList = programEntityList.stream().skip(iDisplayStart).limit(iDisplayLength).collect(Collectors.toList());

            for (ProgramEntity std : programEntityList) {
                List<String> dataList = new ArrayList<String>() {{
                    add(std.getName());
                    add(std.getFullName());
                }};
                result.add(dataList);
            }

            JsonArray aaData = (JsonArray) new Gson()
                    .toJsonTree(result, new TypeToken<List<List<String>>>() {
                    }.getType());

            jsonObj.addProperty("iTotalRecords", iTotalRecords);
//            jsonObj.addProperty("iTotalDisplayRecords", iTotalRecords);
            jsonObj.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
            jsonObj.add("aaData", aaData);
            jsonObj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
    }

    @RequestMapping(value = "/getProgram", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject GetSubject(@RequestParam String name) {
        JsonObject jsonObj = new JsonObject();


        try {
            ProgramEntity entity = programService.getProgramByName(name);
            ProgramEntity program = new ProgramEntity();
            program.setId(entity.getId());
            program.setName(entity.getName());
            program.setFullName(entity.getFullName());
            program.setOjt(entity.getOjt());
            program.setCapstone(entity.getCapstone());
            program.setGraduate(entity.getGraduate());

            String json = new Gson().toJson(program);

            jsonObj.addProperty("success", true);
            jsonObj.addProperty("program", json);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
            jsonObj.addProperty("success", false);
        }

        return jsonObj;
    }

    @RequestMapping(value = "/program/edit")
    @ResponseBody
    public JsonObject EditSubject(@RequestParam("sName") String name, @RequestParam("sFullName") String fullName,
                                  @RequestParam("sOJT") String ojt, @RequestParam("sCapstone") String capstone,
                                  @RequestParam("sGraduate") String graduate,@RequestParam("sId") String id) {
        JsonObject jsonObj = new JsonObject();

        try {

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            ProgramModel model = new ProgramModel();
            model.setId(Integer.parseInt(id));
            model.setName(name);
            model.setFullName(fullName);
            if (ojt.isEmpty()){
                model.setOjt(0);
            }else{
                model.setOjt(Integer.parseInt(ojt));
            }
            if (capstone.isEmpty()){
                model.setCapstone(0);
            }else{
                model.setCapstone(Integer.parseInt(capstone));
            }
            if (graduate.isEmpty()){
                model.setGraduate(0);
            }else{
                model.setGraduate(Integer.parseInt(graduate));
            }

            ProgramModel result = programService.updateProgram(model);
            if (!result.isResult()) {
                jsonObj.addProperty("success", false);
                jsonObj.addProperty("message", result.getErrorMessage());
            } else {
                jsonObj.addProperty("success", true);
            }

        } catch (Exception e) {
            Logger.writeLog(e);
            jsonObj.addProperty("false", false);
            jsonObj.addProperty("message", e.getMessage());
        }

        return jsonObj;
    }
}
