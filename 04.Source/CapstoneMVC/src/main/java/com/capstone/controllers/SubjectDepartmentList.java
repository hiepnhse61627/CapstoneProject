package com.capstone.controllers;

import com.capstone.entities.DepartmentEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.models.Ultilities;
import com.capstone.services.DepartmentServiceImpl;
import com.capstone.services.IDepartmentService;
import com.capstone.services.ISubjectService;
import com.capstone.services.SubjectServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class SubjectDepartmentList {

    ISubjectService subjectService = new SubjectServiceImpl();
    IDepartmentService departmentService = new DepartmentServiceImpl();

    @RequestMapping("/subjectDepartmentList")
    public ModelAndView SubjectDepartmentListAll(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());
        ModelAndView view = new ModelAndView("SubjectDepartmentList");
        view.addObject("title", "Danh sách môn thuộc bộ môn");
        List<SubjectEntity> subjects = subjectService.getAllSubjects();
        List<SubjectEntity> subjectsDoesntHaveDept = new ArrayList<>();
        for (SubjectEntity aSubject : subjects) {
            if (aSubject.getDepartmentId() == null) {
                subjectsDoesntHaveDept.add(aSubject);
            }
        }

        view.addObject("subjects", subjectsDoesntHaveDept);

        List<DepartmentEntity> departmentEntities = departmentService.findAllDepartments();
        view.addObject("departments", departmentEntities);
        return view;
    }

    @RequestMapping(value = "/loadSubjectDepartmentList")
    @ResponseBody
    public JsonObject LoadSubjectDepartmentList(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();

        try {
            List<SubjectEntity> subjectDepartmentEntityList = subjectService.getAllSubjects();

            List<List<String>> result = new ArrayList<>();
            for (SubjectEntity subjectDepartment : subjectDepartmentEntityList) {
                List<String> dataList = new ArrayList<String>();
                dataList.add(subjectDepartment.getId());
                if (subjectDepartment.getDepartmentId() != null) {
                    dataList.add(subjectDepartment.getDepartmentId().getDeptName());

                } else {
                    dataList.add("");
                }
                result.add(dataList);
            }

            Gson gson = new Gson();
            JsonArray array = (JsonArray) gson.toJsonTree(result);

            jsonObj.add("aaData", array);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
    }


    @RequestMapping(value = "/subjectDepartment/edit")
    @ResponseBody
    public JsonObject EditSubjectDepartmentList(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();

        try {
            String subjectCode = "";
            if (!params.get("subjectCode").equals("") && !params.get("subjectCode").equals("-1")) {
                subjectCode = params.get("subjectCode");
            }

            String departmentName = "";
            if (!params.get("departmentName").equals("") && !params.get("departmentName").equals("-1")) {
                departmentName = params.get("departmentName");
            }

            List<DepartmentEntity> departmentEntities = departmentService.findDepartmentsByName(departmentName);

            if (departmentEntities != null && departmentEntities.size() > 0) {
                SubjectEntity subject = subjectService.findSubjectById(subjectCode);
                subject.setDepartmentId(departmentEntities.get(0));

                EntityManagerFactory emf2 = Persistence.createEntityManagerFactory("CapstonePersistence");
                EntityManager em = emf2.createEntityManager();
                try {
                    em.getTransaction().begin();
                    em.merge(subject);
                    em.getTransaction().commit();
                    jsonObj.addProperty("success", true);
                } catch (Exception e) {
                    jsonObj.addProperty("fail", true);
                    jsonObj.addProperty("message", e.getMessage());
                    e.printStackTrace();
                }
            }else{
                jsonObj.addProperty("fail", true);
            }
        } catch (Exception e) {
            jsonObj.addProperty("fail", true);
            jsonObj.addProperty("message", e.getMessage());
            e.printStackTrace();
        }

        return jsonObj;
    }

}


