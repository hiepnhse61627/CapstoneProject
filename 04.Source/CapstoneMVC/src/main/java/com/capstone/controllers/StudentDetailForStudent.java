package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.entities.SubjectCurriculumEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.models.CustomUser;
import com.capstone.models.Logger;
import com.capstone.models.SelectItem;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
public class StudentDetailForStudent {

    IStudentService studentService = new StudentServiceImpl();
    IMarksService service2 = new MarksServiceImpl();
    ISubjectService service3 = new SubjectServiceImpl();

    StudentDetail detail = new StudentDetail();

    private CustomUser getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser user = (CustomUser) authentication.getPrincipal();
        return user;
    }

    @RequestMapping("/studentDetail")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("StudentDetailForStudent");
        view.addObject("title", "Thông tin chi tiết");
        CustomUser user = getPrincipal();
        String rollnum = user.getUser().getStudentRollNumber();
        StudentEntity student = studentService.findStudentByRollNumber(rollnum);
        view.addObject("studentId", student.getId());
        return view;
    }

    @RequestMapping("/getStudentDetail")
    @ResponseBody
    public JsonObject GetStudentFail(@RequestParam Map<String, String> params) {
        JsonObject data = new JsonObject();

        int studentId = Integer.parseInt(params.get("stuId"));

        try {
            List<List<String>> set2 = detail.processFailed(studentId);

            List<List<String>> resultList = set2.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());

            JsonArray result = (JsonArray) new Gson().toJsonTree(resultList);

            data.addProperty("iTotalRecords", set2.size());
            data.addProperty("iTotalDisplayRecords", set2.size());
            data.add("aaData", result);
            data.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }


    @RequestMapping("/getStudentCurrentCourse")
    @ResponseBody
    public JsonObject GetStudentCurrentCourse(@RequestParam Map<String, String> params) {
        JsonObject jsonObject = new JsonObject();

        int stuId = Integer.parseInt(params.get("stuId"));

        try {
            List<List<String>> result = detail.processCurrent(stuId);

            List<List<String>> set2 = result.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(set2);

            jsonObject.addProperty("iTotalRecords", result.size());
            jsonObject.addProperty("iTotalDisplayRecords", result.size());
            jsonObject.add("aaData", aaData);
            jsonObject.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @RequestMapping("/getStudentNextCourse")
    @ResponseBody
    public JsonObject GetStudentNextCourse(@RequestParam Map<String, String> params) {

        JsonObject jsonObject = new JsonObject();

        int stuId = Integer.parseInt(params.get("stuId"));

        try {
            List<List<String>> result = detail.processNext(stuId);

            List<List<String>> set2 = result.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(set2);

            jsonObject.addProperty("iTotalRecords", result.size());
            jsonObject.addProperty("iTotalDisplayRecords", result.size());
            jsonObject.add("aaData", aaData);
            jsonObject.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @RequestMapping("/getStudentNextCourseSuggestion")
    @ResponseBody
    public JsonObject GetStudentNextCourseSuggestion(@RequestParam Map<String, String> params) {
        JsonObject data = new JsonObject();

        int stuId = Integer.parseInt(params.get("stuId"));

        try {
            List<List<String>> result = detail.processSuggestion(stuId);

            List<List<String>> set2 = result.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(set2);

            data.addProperty("iTotalRecords", set2.size());
            data.addProperty("iTotalDisplayRecords", set2.size());
            data.add("aaData", aaData);
            data.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    @RequestMapping("/getStudentNotStart")
    @ResponseBody
    public JsonObject GetStudentNotStart(@RequestParam Map<String, String> params) {
        JsonObject jsonObject = new JsonObject();

        int stuId = Integer.parseInt(params.get("stuId"));

        try {
            List<List<String>> result = detail.processNotStart(stuId);

            List<List<String>> set2 = result.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(set2);

            jsonObject.addProperty("iTotalRecords", result.size());
            jsonObject.addProperty("iTotalDisplayRecords", result.size());
            jsonObject.add("aaData", aaData);
            jsonObject.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
