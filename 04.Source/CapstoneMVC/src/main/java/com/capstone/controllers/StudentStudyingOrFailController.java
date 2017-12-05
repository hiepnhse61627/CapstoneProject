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

        EntityManagerFactory fac = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = fac.createEntityManager();

        JsonArray aaData = new JsonArray();
        if (!term.equals("0")) {
            TypedQuery<String> query = em.createQuery("SELECT DISTINCT a.termNumber FROM SubjectCurriculumEntity a WHERE a.curriculumId.id = :id", String.class);
            query.setParameter("id", Integer.parseInt(term));
            List<String> list = query.getResultList();
            aaData = (JsonArray) new Gson().toJsonTree(list);
        }

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

            List<CurriculumEntity> curs = new ArrayList<>();
            if (curId.equals("0")) {
                curs = curriculumService.getAllCurriculums();
            } else {
                CurriculumEntity curr = curriculumService.getCurriculumById(Integer.parseInt(curId));
                curs.add(curr);
            }

            List<RealSemesterEntity> realSemesters = Ultilities.SortSemesters(semesterService.getAllSemester());

            ArrayList<ArrayList<String>> result = new ArrayList<>();
            List<MarksEntity> list = new ArrayList<>();
            int j = 1;
            for (CurriculumEntity cur : curs) {
                System.out.println(j++ + " - " + curs.size());

                List<String> subject = new ArrayList<>();
                List<SubjectCurriculumEntity> listCur = cur.getSubjectCurriculumEntityList();
                if (term.equals("all")) {
                    for (SubjectCurriculumEntity c : listCur) {
                        if (!subject.contains(c.getSubjectId().getId())) {
                            subject.add(c.getSubjectId().getId());
                        }
                    }
                } else {
                    for (SubjectCurriculumEntity c : listCur) {
                        if (c.getTermNumber() == Integer.parseInt(term)) {
                            if (!subject.contains(c.getSubjectId().getId())) {
                                subject.add(c.getSubjectId().getId());
                            }
                        }
                    }
                }

                List<String> students = new ArrayList<>();
                List<DocumentStudentEntity> stuDoc = cur.getDocumentStudentEntityList();
                for (DocumentStudentEntity doc : stuDoc) {
                    if (doc.getStudentId() != null) {
                        if (!students.contains(doc.getStudentId().getRollNumber())) {
                            students.add(doc.getStudentId().getRollNumber());
                        }
                    }
                }

                if (!subject.isEmpty() && !students.isEmpty()) {
                    int row = -1;
                    for (RealSemesterEntity r : realSemesters) {
                        if (r.getId() == Integer.parseInt(semester)) {
                            row = realSemesters.indexOf(r);
                        }
                    }

                    String queryStr = "SELECT a FROM MarksEntity a WHERE a.subjectMarkComponentId.subjectId.id IN :list AND a.isActivated = TRUE";
                    if (!students.isEmpty()) {
                        queryStr += " AND a.studentId.rollNumber IN :stus";
                    } else {
                        queryStr += " AND a.studentId.rollNumber = 0";
                    }

                    if (row < 0) {
                        TypedQuery<MarksEntity> query = em.createQuery(queryStr, MarksEntity.class);
                        query.setParameter("list", subject);
                        if (!students.isEmpty()) {
                            query.setParameter("stus", students);
                        }
                        list = Ultilities.FilterListFailStudent(query.getResultList());
                    } else {
                        for (int i = 0; i < row + 1; i++) {
                            TypedQuery<MarksEntity> query = em.createQuery(queryStr + " AND a.semesterId.id = :semester", MarksEntity.class);
                            query.setParameter("list", subject);
                            query.setParameter("semester", realSemesters.get(i).getId());
                            if (!students.isEmpty()) {
                                query.setParameter("stus", students);
                            }
                            List<MarksEntity> finalList = list;
                            Ultilities.FilterListFailStudent(query.getResultList()).forEach(c -> {
                                if (!finalList.contains(c)) {
                                    finalList.add(c);
                                }
                            });
                            list = finalList;
                        }
                    }
                }
            }

            if (!list.isEmpty()) {
                list.forEach(m -> {
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

//            data.addProperty("iTotalRecords", list.size());
//            data.addProperty("iTotalDisplayRecords", list.size());
            data.add("aaData", aaData);
            data.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }

        return data;
    }

    @RequestMapping("/getstudyingorfail2")
    @ResponseBody
    public JsonObject GetStudentStudying(@RequestParam Map<String, String> params) {
        JsonObject data = new JsonObject();

        String curId = params.get("curId");
        String term = params.get("term");
        String semester = params.get("semester");

        try {
            ICurriculumService curriculumService = new CurriculumServiceImpl();
            IRealSemesterService semesterService = new RealSemesterServiceImpl();

            EntityManagerFactory fac = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = fac.createEntityManager();

            List<CurriculumEntity> curs = new ArrayList<>();
            if (curId.equals("0")) {
                curs = curriculumService.getAllCurriculums();
            } else {
                CurriculumEntity curr = curriculumService.getCurriculumById(Integer.parseInt(curId));
                curs.add(curr);
            }

            ArrayList<ArrayList<String>> result = new ArrayList<>();
            List<MarksEntity> list = new ArrayList<>();
            for (CurriculumEntity cur : curs) {
                List<RealSemesterEntity> realSemesters = Ultilities.SortSemesters(semesterService.getAllSemester());

                List<String> subject = new ArrayList<>();
                List<SubjectCurriculumEntity> listCur = cur.getSubjectCurriculumEntityList();
                if (term.equals("all")) {
                    for (SubjectCurriculumEntity c : listCur) {
                        if (!subject.contains(c.getSubjectId().getId())) {
                            subject.add(c.getSubjectId().getId());
                        }
                    }
                } else {
                    for (SubjectCurriculumEntity c : listCur) {
                        if (c.getTermNumber() == Integer.parseInt(term)) {
                            if (!subject.contains(c.getSubjectId().getId())) {
                                subject.add(c.getSubjectId().getId());
                            }
                        }
                    }
                }

                List<String> students = new ArrayList<>();
                List<DocumentStudentEntity> stuDoc = cur.getDocumentStudentEntityList();
                for (DocumentStudentEntity doc : stuDoc) {
                    if (doc.getStudentId() != null) {
                        if (!students.contains(doc.getStudentId().getRollNumber())) {
                            students.add(doc.getStudentId().getRollNumber());
                        }
                    }
                }

                if (!subject.isEmpty() && !students.isEmpty()) {
                    int row = -1;
                    for (RealSemesterEntity r : realSemesters) {
                        if (r.getId() == Integer.parseInt(semester)) {
                            row = realSemesters.indexOf(r);
                        }
                    }

                    String queryStr = "SELECT a FROM MarksEntity a WHERE a.isActivated = TRUE AND a.subjectMarkComponentId.subjectId.id IN :list AND a.status LIKE :stat";
                    if (!students.isEmpty()) {
                        queryStr += " AND a.studentId.rollNumber IN :stus";
                    } else {
                        queryStr += " AND a.studentId.rollNumber = 0";
                    }

                    if (row < 0) {
                        TypedQuery<MarksEntity> query = em.createQuery(queryStr, MarksEntity.class);
                        query.setParameter("list", subject);
                        if (!students.isEmpty()) {
                            query.setParameter("stus", students);
                        }
                        query.setParameter("stat", "%studying%");
                        list = query.getResultList();
                    } else {
                        for (int i = 0; i < row + 1; i++) {
                            TypedQuery<MarksEntity> query = em.createQuery(queryStr + " AND a.semesterId.id = :semester", MarksEntity.class);
                            query.setParameter("list", subject);
                            query.setParameter("semester", realSemesters.get(i).getId());
                            query.setParameter("stat", "%studying%");
                            if (!students.isEmpty()) {
                                query.setParameter("stus", students);
                            }
                            List<MarksEntity> finalList = list;
                            query.getResultList().forEach(c -> {
                                if (!finalList.contains(c)) {
                                    finalList.add(c);
                                }
                            });
                            list = finalList;
                        }
                    }
                }
            }

//            List<MarksEntity> displayList = new ArrayList<>();
//            if (!list.isEmpty()) {
//                displayList = list.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
//            }

            if (!list.isEmpty()) {
                list.forEach(m -> {
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

//            data.addProperty("iTotalRecords", list.size());
//            data.addProperty("iTotalDisplayRecords", list.size());
            data.add("aaData", aaData);
            data.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }

        return data;
    }
}
