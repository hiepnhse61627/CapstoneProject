package com.capstone.controllers;

import com.capstone.entities.RoomEntity;
import com.capstone.entities.SubjectDepartmentEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.models.Ultilities;
import com.capstone.services.ISubjectDepartmentService;
import com.capstone.services.ISubjectService;
import com.capstone.services.SubjectDepartmentServiceImpl;
import com.capstone.services.SubjectServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class SubjectDepartmentList {

    ISubjectDepartmentService subjectDepartmentService = new SubjectDepartmentServiceImpl();
    ISubjectService subjectService = new SubjectServiceImpl();

    @RequestMapping("/subjectDepartmentList")
    public ModelAndView SubjectDepartmentListAll(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " +request.getRequestURI());
        ModelAndView view = new ModelAndView("SubjectDepartmentList");
        view.addObject("title", "Danh sách môn thuộc bộ môn");

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
                dataList.add(subjectDepartment.getDepartmentId().getDeptName());
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

}


