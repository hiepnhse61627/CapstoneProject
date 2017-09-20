package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.services.IRealSemesterService;
import com.capstone.services.ISubjectService;
import com.capstone.services.RealSemesterServiceImpl;
import com.capstone.services.SubjectServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
public class StudentController {

    @RequestMapping("/create")
    public String Index() {
        return "CreateNewStudent";
    }

//    @RequestMapping("/display")
//    public ModelAndView Display() {
//        ModelAndView view = new ModelAndView("DisplayStudentPassFail");
//
//        IRealSemesterService service = new RealSemesterServiceImpl();
//        view.addObject("semesters", service.getAllSemester());
//        ISubjectService service2 = new SubjectServiceImpl();
//        view.addObject("subjects", service2.getAllSubjects());
//
//        return view;
//    }

//    @RequestMapping(value = "/getstudents")
//    @ResponseBody
//    public JsonObject GetStudents(@RequestParam Map<String,String> params) {
//        try {
//            JsonObject data  = new JsonObject();
//
//            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
//            EntityManager manager = emf.createEntityManager();
//
//            TypedQuery<MarksEntity> query = manager.createQuery("SELECT c FROM MarksEntity c WHERE c.semesterId = :cid AND c.subjectId = :sid", MarksEntity.class);
//            query.setParameter("cid", Integer.parseInt(params.get("semesterId")));
//            query.setParameter("sid", params.get("subjectId"));
//            query.setFirstResult(Integer.parseInt(params.get("iDisplayStart")));
//            query.setMaxResults(Integer.parseInt(params.get("iDisplayLength")));
//            List<MarksEntity> set = query.getResultList();
//
//            String search = params.get("sSearch");
//            if (search != null && !search.isEmpty()) {
//                set = set.stream().filter(c ->
//                        c.getStudentByStudentId().getFullName().toLowerCase().contains(search) ||
//                        c.getStudentByStudentId().getRollNumber().toLowerCase().contains(search) ||
//                        c.getCourseByCourseId().getClazz().toLowerCase().contains(search))
//                .collect(Collectors.toList());
//            };
//
//            ArrayList<ArrayList<String>> parent = new ArrayList<>();
//            if (!set.isEmpty()) {
//                set.forEach(m -> {
//                    ArrayList<String> tmp = new ArrayList<>();
//                    tmp.add(m.getStudentByStudentId().getRollNumber());
//                    tmp.add(m.getStudentByStudentId().getFullName());
//                    tmp.add(String.valueOf(m.getAverageMark()));
//                    tmp.add(m.getStatus());
//                    parent.add(tmp);
//                });
//            }
//
//            JsonArray result = (JsonArray) new Gson().toJsonTree(parent, new TypeToken<List<MarksEntity>>(){}.getType());
//
//            data.addProperty("iTotalRecords", parent.size());
//            data.addProperty("iTotalDisplayRecords",  parent.size());
//            data.add("aaData", result);
//            data.addProperty("sEcho", params.get("sEcho"));
//
//            return data;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

    @RequestMapping(value = "/createnew", method = RequestMethod.POST)
    @ResponseBody
    public StudentEntity CreateNewStudent(@RequestBody StudentEntity student) {
        System.out.println(student.getFullName());
        System.out.println(student.getRollNumber());
        return student;
    }
}
