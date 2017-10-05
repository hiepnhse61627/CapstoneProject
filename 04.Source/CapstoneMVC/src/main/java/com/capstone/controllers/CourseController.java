package com.capstone.controllers;

import com.capstone.entities.CourseEntity;
import com.capstone.entities.MarksEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.services.CourseServiceImpl;
import com.capstone.services.ICourseService;
import com.capstone.services.ISubjectService;
import com.capstone.services.SubjectServiceImpl;
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
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class CourseController {

    ICourseService courseService = new CourseServiceImpl();

    @RequestMapping("/course")
    public ModelAndView CoursePage() {
        ModelAndView view = new ModelAndView("CoursePage");

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

            String sSearch = params.get("sSearch");
            int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
            int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
            int iTotalRecords = 0;
            int iTotalDisplayRecords = 0;

            TypedQuery<Integer> queryCount;

            // Đếm số khóa học
            queryCount = em.createQuery("SELECT COUNT(c) FROM CourseEntity c", Integer.class);
            iTotalRecords = ((Number) queryCount.getSingleResult()).intValue();

            // Đếm số khóa học sau khi filter
            if (sSearch.isEmpty()) {
                iTotalDisplayRecords = iTotalRecords;
            } else {
                queryCount = em.createQuery("SELECT COUNT(c) FROM CourseEntity c " +
                        "WHERE c.subjectCode LIKE :subCode OR c.class1 LIKE :class", Integer.class);
                queryCount.setParameter("subCode", "%" + sSearch + "%");
                queryCount.setParameter("class", "%" + sSearch + "%");
                iTotalDisplayRecords = ((Number) queryCount.getSingleResult()).intValue();
            }

            // Danh sách khóa học
            String queryStr = "SELECT c FROM CourseEntity c";
            if (!sSearch.isEmpty()) {
                queryStr += " WHERE c.subjectCode LIKE :subCode OR c.class1 LIKE :class";
            }

            TypedQuery<CourseEntity> query = em.createQuery(queryStr, CourseEntity.class)
                    .setFirstResult(iDisplayStart)
                    .setMaxResults(iDisplayLength);

            if (!sSearch.isEmpty()) {
                query.setParameter("subCode", "%" + sSearch + "%");
                query.setParameter("class", "%" + sSearch + "%");
            }

            List<CourseEntity> courseList = query.getResultList();
            List<List<String>> result = new ArrayList<>();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (CourseEntity course : courseList) {
                List<String> dataList = new ArrayList<String>() {{
                    add(course.getClass1());
                    add(course.getSubjectCode());
                    add(sdf.format(course.getStartDate()));
                    add(sdf.format(course.getEndDate()));
                    add(course.getId() + "");
                }};
                result.add(dataList);
            }

            JsonArray aaData = (JsonArray) new Gson()
                    .toJsonTree(result, new TypeToken<List<List<String>>>() {
                    }.getType());

            jsonObj.addProperty("iTotalRecords", iTotalRecords);
            jsonObj.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
            jsonObj.add("aaData", aaData);
            jsonObj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
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

//            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
//            Date startDate = sdf.parse(String.valueOf(new Date(params.get("sStartDate"))));
//            Date endDate = sdf.parse(String.valueOf(new Date(params.get("sEndDate"))));

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate = sdf.parse(params.get("sStartDate"));
            Date endDate = sdf.parse(params.get("sEndDate"));

            CourseEntity course = new CourseEntity();
            course.setClass1(params.get("clazz"));
            course.setSubjectCode(params.get("subjectCode"));
            course.setStartDate(startDate);
            course.setEndDate(endDate);

            courseService.createCourse(course);

            jsonObj.addProperty("success", true);
        } catch (Exception e) {
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

            int courseId = Integer.parseInt(params.get("courseId"));

            String queryStr = "SELECT c FROM CourseEntity c WHERE c.id = :courseId";
            TypedQuery<CourseEntity> query = em.createQuery(queryStr, CourseEntity.class);
            query.setParameter("courseId", courseId);
            CourseEntity course = query.getSingleResult();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate = sdf.parse(params.get("sStartDate"));
            Date endDate = sdf.parse(params.get("sEndDate"));

            course.setClass1(params.get("clazz"));
            course.setSubjectCode(params.get("subjectCode"));
            course.setStartDate(startDate);
            course.setEndDate(endDate);

            em.getTransaction().begin();
            em.merge(course);
            em.getTransaction().commit();

            jsonObj.addProperty("success", true);
        } catch (Exception e) {
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

            String queryStr = "SELECT COUNT(m) FROM MarksEntity m WHERE m.courseId.id = :courseId";
            TypedQuery<Integer> queryCountMarks = em.createQuery(queryStr, Integer.class);
            queryCountMarks.setParameter("courseId", courseId);
            int countMarks = ((Number) queryCountMarks.getSingleResult()).intValue();

            if (countMarks > 0) {
                jsonObj.addProperty("success", false);
                jsonObj.addProperty("message", "Không thể xóa, khóa học này đã được dùng.");
            } else {
                queryStr = "SELECT c FROM CourseEntity c WHERE c.id = :courseId";
                TypedQuery<CourseEntity> query = em.createQuery(queryStr, CourseEntity.class);
                query.setParameter("courseId", courseId);
                CourseEntity course = query.getSingleResult();

                em.getTransaction().begin();
                em.remove(course);
                em.getTransaction().commit();

                jsonObj.addProperty("success", true);
            }
        } catch (Exception e) {
            jsonObj.addProperty("success", false);
            jsonObj.addProperty("message", e.getMessage());
        }

        return jsonObj;
    }

}
