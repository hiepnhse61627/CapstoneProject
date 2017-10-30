package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.PrequisiteEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.models.FailPrequisiteModel;
import com.capstone.models.Ultilities;
import com.capstone.services.ISubjectService;
import com.capstone.services.SubjectServiceImpl;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
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
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class StudentFailPrequisite {

    @RequestMapping("/checkPrequisite")
    public ModelAndView Index() {
        ISubjectService service = new SubjectServiceImpl();

        ModelAndView view = new ModelAndView("StudentFailPrequisite");
        view.addObject("title", "Danh sách sinh viên rớt môn tiên quyết");
        view.addObject("subs", service.getAllSubjects());

        return view;
    }

    @RequestMapping("/getAllPrequisites")
    @ResponseBody
    public JsonObject getPrequisites(@RequestParam String subId) {
        JsonObject obj = new JsonObject();
        JsonArray o1 = new JsonArray();

        ISubjectService service = new SubjectServiceImpl();
        try {
            List<List<SubjectEntity>> pres = new ArrayList<>();
            if (subId.equals("0")) {
                for (SubjectEntity subs : service.getAllSubjects()) {
                    pres = service.getAllPrequisiteSubjects(subs.getId());
                    for (List<SubjectEntity> p : pres) {
                        JsonObject o2 = new JsonObject();
                        String value = subs.getId() + "_";
                        String name = subs.getId() + "_";
                        int i = 1;
                        for (SubjectEntity s : p) {
                            value += s.getId();
                            name += s.getId();
                            if (i != p.size()) {
                                value += ",";
                                name += " AND ";
                            }
                            i++;
                        }
                        o2.addProperty("value", value);
                        o2.addProperty("name", name);
                        o1.add(o2);
                    }
                }
            } else {
                pres = service.getAllPrequisiteSubjects(subId);
                for (List<SubjectEntity> p : pres) {
                    JsonObject o2 = new JsonObject();
                    String value = subId + "_";
                    String name = "";
                    int i = 1;
                    for (SubjectEntity s : p) {
                        value += s.getId();
                        name += s.getId();
                        if (i != p.size()) {
                            value += ",";
                            name += " AND ";
                        }
                        i++;
                    }
                    o2.addProperty("value", value);
                    o2.addProperty("name", name);
                    o1.add(o2);
                }
            }

            obj.addProperty("success", true);
            obj.add("data", o1);
        } catch (Exception e) {
            e.printStackTrace();
            obj.addProperty("success", false);
            return obj;
        }

        return obj;
    }

    @RequestMapping(value = "/getFailStudents")
    @ResponseBody
    public JsonObject GetStudentsFailPrequisite(@RequestParam Map<String, String> params) {
        try {
            JsonObject jsonObj = new JsonObject();
            ISubjectService subjectService = new SubjectServiceImpl();

            String subjectId = params.get("subId").trim();
//            String prequisiteStr = params.get("prequisiteId").trim();

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            List<FailPrequisiteModel> result = new ArrayList<>();

//            Map<String, List<String>> map = new HashMap<>();
//            List<String> prequisiteRow = Arrays.asList(prequisiteStr.split(";"));
//            if (prequisiteRow.get(0) != null && !prequisiteRow.get(0).isEmpty()) {
//                for (String s : prequisiteRow) {
//                    String[] a = s.split("_");
//                    if (map.get(a[0]) == null) {
//                        List<String> l = new ArrayList<>();
//                        l.add(a[1]);
//                        map.put(a[0], l);
//                    } else {
//                        map.get(a[0]).add(a[1]);
//                    }
//                }
//            }

            String queryStr = "SELECT p FROM MarksEntity p WHERE p.subjectMarkComponentId.subjectId.id IN :sList";
            TypedQuery<MarksEntity> prequisiteQuery;
            if (!subjectId.equals("0") && !subjectId.isEmpty()) {
                List<String> processedData = new ArrayList<>();

                SubjectEntity entity = subjectService.findSubjectById(subjectId);
                processedData.add(entity.getId());

                String preSubs = entity.getPrequisiteEntity().getPrequisiteSubs();
                String[] rows = preSubs.split("OR");
                for (String row : rows) {
                    row = row.replaceAll("\\(", "").replaceAll("\\)", "");
                    String[] cells = row.split(",");
                    for (String cell : cells) {
                        SubjectEntity c = subjectService.findSubjectById(cell);
                        if (c != null) processedData.add(cell);
                    }
                }

                prequisiteQuery = em.createQuery(queryStr, MarksEntity.class);
                prequisiteQuery.setParameter("sList", processedData);

                List<MarksEntity> list = prequisiteQuery.getResultList();
//                Ultilities.FilterStudentPassedSubFailPrequisite(list, subjectId, entity.getPrequisiteEntity().getFailMark()).forEach(c -> {
//                    if (!result.contains(c)) {
//                        result.add(c);
//                    }
//                });
            } else {
//                for (Map.Entry<String, List<String>> entry : map.entrySet()) {
//                    SubjectEntity entity = subjectService.findSubjectById(entry.getKey());
//                    PrequisiteEntity prequisite = entity.getPrequisiteEntity();
//
//                    prequisiteQuery = em.createQuery(queryStr, MarksEntity.class);
//                    prequisiteQuery.setParameter("sub", entry.getKey());
//
//                    List<String> processedData = new ArrayList<>();
//                    for (String data : entry.getValue()) {
//                        String[] s = data.trim().split(",");
//                        for (String ss : s) {
//                            processedData.add(ss.trim());
//                        }
//                    }
//                    prequisiteQuery.setParameter("sList", processedData);
//
//                    List<MarksEntity> list = prequisiteQuery.getResultList();
//                    Ultilities.FilterStudentPassedSubFailPrequisite(list, entry.getKey(), entry.getValue(), prequisite.getFailMark()).forEach(c -> {
//                        if (!result.contains(c)) {
//                            result.add(c);
//                        }
//                    });
//                }
            }

            List<FailPrequisiteModel> displayList = new ArrayList<>();
            if (!result.isEmpty()) {
                displayList = result.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
            }

            ArrayList<ArrayList<String>> parent = new ArrayList<>();
            if (!displayList.isEmpty()) {
                displayList.forEach(m -> {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(m.getMark().getStudentId().getRollNumber());
                    tmp.add(m.getMark().getStudentId().getFullName());
                    tmp.add(m.getSubjectWhichPrequisiteFail() == null ? "N/A" : m.getSubjectWhichPrequisiteFail());
                    tmp.add(m.getMark().getSubjectMarkComponentId() == null ? "N/A" : m.getMark().getSubjectMarkComponentId().getSubjectId().getId());
                    tmp.add(m.getMark().getCourseId() == null ? "N/A" : m.getMark().getCourseId().getSemester());
                    tmp.add(m.getMark().getSemesterId() == null ? "N/A" : m.getMark().getSemesterId().getSemester());
                    tmp.add(String.valueOf(m.getMark().getAverageMark()));
                    tmp.add(m.getMark().getStatus());
                    tmp.add(m.getMark().getStudentId().getId() + "");
                    parent.add(tmp);
                });
            }

            JsonArray output = (JsonArray) new Gson().toJsonTree(parent, new TypeToken<List<MarksEntity>>() {
            }.getType());

            jsonObj.addProperty("iTotalRecords", result.size());
            jsonObj.addProperty("iTotalDisplayRecords", result.size());
            jsonObj.add("aaData", output);
            jsonObj.addProperty("sEcho", params.get("sEcho"));

            return jsonObj;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
