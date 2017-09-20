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

    @RequestMapping("/display")
    public ModelAndView Display() {
        ModelAndView view = new ModelAndView("DisplayStudentPassFail");

        IRealSemesterService service = new RealSemesterServiceImpl();
        view.addObject("semesters", service.getAllSemester());
        ISubjectService service2 = new SubjectServiceImpl();
        view.addObject("subjects", service2.getAllSubjects());

        return view;
    }

    @RequestMapping(value = "/getstudents")
    @ResponseBody
    public JsonObject GetStudents(@RequestParam Map<String,String> params) {
        try {
            JsonObject data  = new JsonObject();

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager manager = emf.createEntityManager();

            String search = params.get("sSearch");
            TypedQuery<MarksEntity> query = null;
            if (search != null && !search.isEmpty()) {
                String cid = params.get("semesterId");
                String sid = params.get("subjectId");
                if (cid.equals("0") && !sid.equals("0")) {
                    query = manager.createQuery("SELECT c FROM MarksEntity c WHERE c.subjectId.subjectId = :sid AND LOWER(c.status) LIKE '%fail%' AND (LOWER(c.studentId.fullName) LIKE :s OR LOWER(c.studentId.rollNumber) LIKE :s OR LOWER(c.courseId.class1) LIKE :s)", MarksEntity.class);
                    query.setParameter("sid", sid);
                    query.setParameter("s", "%" + search + "%");
                } else if (sid.equals("0") && !cid.equals("0")) {
                    query = manager.createQuery("SELECT c FROM MarksEntity c WHERE c.semesterId.id = :cid AND LOWER(c.status) LIKE '%fail%' AND (LOWER(c.studentId.fullName) LIKE :s OR LOWER(c.studentId.rollNumber) LIKE :s OR LOWER(c.courseId.class1) LIKE :s)", MarksEntity.class);
                    query.setParameter("cid", Integer.parseInt(cid));
                    query.setParameter("s", "%" + search + "%");
                } else if (sid.equals("0") && cid.equals("0")) {
                    query = manager.createQuery("SELECT c FROM MarksEntity c WHERE LOWER(c.status) LIKE '%fail%' AND (LOWER(c.studentId.fullName) LIKE :s OR LOWER(c.studentId.rollNumber) LIKE :s OR LOWER(c.courseId.class1) LIKE :s)", MarksEntity.class);
                    query.setParameter("s", "%" + search + "%");
                } else if (!sid.equals("0") && !cid.equals("0")) {
                    query = manager.createQuery("SELECT c FROM MarksEntity c WHERE c.subjectId.subjectId = :sid AND c.semesterId.id = :cid AND LOWER(c.status) LIKE '%fail%' AND (LOWER(c.studentId.fullName) LIKE :s OR LOWER(c.studentId.rollNumber) LIKE :s OR LOWER(c.courseId.class1) LIKE :s)", MarksEntity.class);
                    query.setParameter("cid", Integer.parseInt(cid));
                    query.setParameter("sid", sid);
                    query.setParameter("s", "%" + search + "%");
                }
            } else {
                String cid = params.get("semesterId");
                String sid = params.get("subjectId");
                if (cid.equals("0") && !sid.equals("0")) {
                    query = manager.createQuery("SELECT c FROM MarksEntity c WHERE c.subjectId.subjectId = :sid AND LOWER(c.status) LIKE '%fail%'", MarksEntity.class);
                    query.setParameter("sid", sid);
                } else if (sid.equals("0") && !cid.equals("0")) {
                    query = manager.createQuery("SELECT c FROM MarksEntity c WHERE c.semesterId.id = :cid AND LOWER(c.status) LIKE '%fail%'", MarksEntity.class);
                    query.setParameter("cid", Integer.parseInt(cid));
                } else if (sid.equals("0") && cid.equals("0")) {
                    query = manager.createQuery("SELECT c FROM MarksEntity c WHERE LOWER(c.status) LIKE '%fail%'", MarksEntity.class);
                } else if (!cid.equals("0") && !sid.equals("0")) {
                    query = manager.createQuery("SELECT c FROM MarksEntity c WHERE c.subjectId.subjectId = :sid AND c.semesterId.id = :cid AND LOWER(c.status) LIKE '%fail%'", MarksEntity.class);
                    query.setParameter("sid", sid);
                    query.setParameter("cid", Integer.parseInt(cid));
                }
            }

//            query.setFirstResult(Integer.parseInt(params.get("iDisplayStart")));
            List<MarksEntity> set = query.getResultList();

            List<MarksEntity> set2 = new ArrayList<>();
            if (!set.isEmpty()) {
                set2 = set.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
            }

            ArrayList<ArrayList<String>> parent = new ArrayList<>();
            if (!set2.isEmpty()) {
                set2.forEach(m -> {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(m.getStudentId().getRollNumber());
                    tmp.add(m.getStudentId().getFullName());
                    tmp.add(m.getSubjectId() == null ? "N/A" : m.getSubjectId().getSubjectId());
                    tmp.add(m.getCourseId() == null ? "N/A" : m.getCourseId().getClass1());
                    tmp.add(String.valueOf(m.getAverageMark()));
                    tmp.add(m.getStatus());
                    parent.add(tmp);
                });
            }

            JsonArray result = (JsonArray) new Gson().toJsonTree(parent, new TypeToken<List<MarksEntity>>(){}.getType());

            data.addProperty("iTotalRecords", set.size());
            data.addProperty("iTotalDisplayRecords",  set.size());
            data.add("aaData", result);
            data.addProperty("sEcho", params.get("sEcho"));

            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(value = "/createnew", method = RequestMethod.POST)
    @ResponseBody
    public StudentEntity CreateNewStudent(@RequestBody StudentEntity student) {
        System.out.println(student.getFullName());
        System.out.println(student.getRollNumber());
        return student;
    }
}
