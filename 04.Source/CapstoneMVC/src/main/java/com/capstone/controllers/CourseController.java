package com.capstone.controllers;

import com.capstone.entities.CourseEntity;
import com.capstone.models.DatatableModel;
import com.capstone.models.Logger;
import com.capstone.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class CourseController {

    @Autowired
    ServletContext context;

    ICourseService courseService = new CourseServiceImpl();
    IMarksService markService = new MarksServiceImpl();

    @RequestMapping("/course")
    public ModelAndView CoursePage() {
        ModelAndView view = new ModelAndView("CoursePage");
        view.addObject("title", "Danh sách khóa học");

        ISubjectService subjectService = new SubjectServiceImpl();
        view.addObject("subjects", subjectService.getAllSubjects());

        return view;
    }

    @RequestMapping(value = "/course/loadTable")
    @ResponseBody
    public JsonObject LoadCourseTable(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();


            DatatableModel model = new DatatableModel();
            model.sSearch = params.get("sSearch");
            model.iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
            model.iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
            model.iTotalRecords = 0;
            model.iTotalDisplayRecords = 0;

            List<CourseEntity> courseList = courseService.getCourseListForDatatable(model);
            List<List<String>> result = new ArrayList<>();

            for (CourseEntity course : courseList) {
                List<String> dataList = new ArrayList<String>() {{
//                    add(course.getClass1());
                    add(course.getId() + "");
                }};
                result.add(dataList);
            }

            JsonArray aaData = (JsonArray) new Gson()
                    .toJsonTree(result, new TypeToken<List<List<String>>>() {
                    }.getType());

            jsonObj.addProperty("iTotalRecords", model.iTotalRecords);
            jsonObj.addProperty("iTotalDisplayRecords", model.iTotalDisplayRecords);
            jsonObj.add("aaData", aaData);
            jsonObj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            Logger.writeLog(e);
        }

        return jsonObj;
    }

    @RequestMapping(value = "/course/create")
    @ResponseBody
    public JsonObject CreateCourse(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            CourseEntity course = new CourseEntity();
//            course.setClass1(params.get("clazz"));

            courseService.createCourse(course);

            jsonObj.addProperty("success", true);
        } catch (Exception e) {
            Logger.writeLog(e);
            jsonObj.addProperty("false", false);
            jsonObj.addProperty("message", e.getMessage());
        }

        return jsonObj;
    }

    @RequestMapping(value = "/course/edit")
    @ResponseBody
    public JsonObject EditCourse(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            CourseEntity model = new CourseEntity();
            model.setId(Integer.parseInt(params.get("courseId")));
//            model.setClass1(params.get("clazz"));

            courseService.updateCourse(model);
            jsonObj.addProperty("success", true);
        } catch (Exception e) {
            Logger.writeLog(e);
            jsonObj.addProperty("false", false);
            jsonObj.addProperty("message", e.getMessage());
        }

        return jsonObj;
    }

    @RequestMapping(value = "/course/delete")
    @ResponseBody
    public JsonObject DeleteCourse(int courseId) {
        JsonObject jsonObj = new JsonObject();

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            int countMarks = markService.countMarksByCourseId(courseId);

            if (countMarks > 0) {
                jsonObj.addProperty("success", false);
                jsonObj.addProperty("message", "Không thể xóa, khóa học này đã được dùng.");
            } else {
                courseService.deleteCourse(courseId);
                jsonObj.addProperty("success", true);
            }
        } catch (Exception e) {
            Logger.writeLog(e);
            jsonObj.addProperty("success", false);
            jsonObj.addProperty("message", e.getMessage());
        }

        return jsonObj;
    }

}
