package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.models.Logger;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.jws.WebParam;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/managerrole")
public class ManagerController {

    @RequestMapping("/changecurriculum")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("ChangeCurriculum");
        view.addObject("title", "Đổi ngành");
        IStudentService studentService = new StudentServiceImpl();
        List<StudentEntity> list = studentService.findAllStudents();
        view.addObject("students", list);
        ICurriculumService curriculumService = new CurriculumServiceImpl();
        List<CurriculumEntity> list2 = curriculumService.getAllCurriculums();
        view.addObject("curs", list2);
        return view;
    }

    @RequestMapping("/getinfo")
    @ResponseBody
    public JsonObject GetInfo(@RequestParam(value = "stuId") int stuId) {
        JsonObject result = new JsonObject();

        try {
            IDocumentStudentService documentStudentService = new DocumentStudentServiceImpl();
            List<Integer> list = new ArrayList<>();
            list.add(stuId);
            List<DocumentStudentEntity> docs = documentStudentService.getDocumentStudentByIdList(list);
            docs.sort(Comparator.comparingLong(c -> {
                DocumentStudentEntity doc = (DocumentStudentEntity) c;
                if (doc.getCreatedDate() == null) return 0;
                else return doc.getCreatedDate().getTime();
            }));
            DocumentStudentEntity d = docs.get(docs.size() - 1);
            String data = d.getCurriculumId().getProgramId().getName() + "_" + d.getCurriculumId().getName();

            result.addProperty("info", data);
            result.addProperty("curriculum", d.getCurriculumId().getId());
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }

        return result;
    }

    @RequestMapping("/getdocuments")
    @ResponseBody
    public JsonObject GetDocuments(@RequestParam Map<String, String> params) {
        int stuId = Integer.parseInt(params.get("stuId"));
        JsonObject result = new JsonObject();

        try {
            IDocumentStudentService documentStudentService = new DocumentStudentServiceImpl();
            List<Integer> list = new ArrayList<>();
            list.add(stuId);
            List<DocumentStudentEntity> docs = documentStudentService.getDocumentStudentByIdList(list);
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

            List<List<String>> parent = new ArrayList<>();
            if (!docs.isEmpty()) {
                docs.forEach(c -> {
                    List<String> tmp = new ArrayList<>();
                    tmp.add(c.getCurriculumId().getProgramId().getName() + "_" + c.getCurriculumId().getName());
                    tmp.add(c.getDocumentId().getCode() + " - " + c.getDocumentId().getDocTypeId().getName());
                    tmp.add(df.format(c.getCreatedDate()));
                    parent.add(tmp);
                });
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(parent);

            result.addProperty("iTotalRecords", parent.size());
            result.addProperty("iTotalDisplayRecords", parent.size());
            result.add("aaData", aaData);
            result.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }

        return result;
    }

    public List<SubjectEntity> GetCurrentCurriculumSubjects(int curId) {
        List<SubjectEntity> result = new ArrayList<>();
        try {
            ICurriculumService curriculumService = new CurriculumServiceImpl();
            CurriculumEntity cur = curriculumService.getCurriculumById(curId);
            if (cur != null) {
                List<SubjectCurriculumEntity> list = cur.getSubjectCurriculumEntityList();
                for (SubjectCurriculumEntity en : list) {
                    if (!result.contains(en.getSubjectId())) {
                        result.add(en.getSubjectId());
                    }
                }
            }
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }

        return result;
    }

    @RequestMapping("/getcurrent")
    @ResponseBody
    public JsonObject GetCurrent(@RequestParam int curId, @RequestParam int newId) {
        JsonObject result = new JsonObject();

        try {
            List<SubjectEntity> subs = this.GetCurrentCurriculumSubjects(curId);
            List<SubjectEntity> newSubs = this.GetCurrentCurriculumSubjects(newId);

            List<SubjectEntity> common = new ArrayList<>();
            for (SubjectEntity s : subs) {
                if (newSubs.stream().anyMatch(c -> c.getId().equals(s.getId()))) {
                    common.add(s);
                }
            }

            List<List<String>> parent = new ArrayList<>();
            if (!common.isEmpty()) {
                common.forEach(c -> {
                    List<String> tmp = new ArrayList<>();
                    tmp.add(c.getId());
                    tmp.add(c.getName());
                    parent.add(tmp);
                });
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(parent);

            result.add("data", aaData);
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }

        return result;
    }

    @RequestMapping("/getnew")
    @ResponseBody
    public JsonObject GetNew(@RequestParam int curId, @RequestParam int newId) {
        JsonObject result = new JsonObject();

        try {
            List<SubjectEntity> subs = this.GetCurrentCurriculumSubjects(curId);
            List<SubjectEntity> newSubs = this.GetCurrentCurriculumSubjects(newId);

            List<SubjectEntity> common = new ArrayList<>();
            for (SubjectEntity s : subs) {
                if (!newSubs.stream().anyMatch(c -> c.getId().equals(s.getId()))) {
                    common.add(s);
                }
            }

            List<List<String>> parent = new ArrayList<>();
            if (!common.isEmpty()) {
                common.forEach(c -> {
                    List<String> tmp = new ArrayList<>();
                    tmp.add(c.getId());
                    tmp.add(c.getName());
                    parent.add(tmp);
                });
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(parent);

            result.add("data", aaData);
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }

        return result;
    }
}
