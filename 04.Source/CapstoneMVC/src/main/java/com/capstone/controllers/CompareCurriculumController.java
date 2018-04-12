package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.models.*;
import com.capstone.services.*;
import com.google.common.collect.Lists;
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
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/managerrole")
public class CompareCurriculumController {

    //home page
    @RequestMapping("/curriculumcompare")
    public ModelAndView Index(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView view = new ModelAndView("CompareCurriculum");
        view.addObject("title", "So sánh khung");
        ICurriculumService curriculumService = new CurriculumServiceImpl();
        List<CurriculumEntity> list2 = curriculumService.getAllCurriculums();

        // sort using Java 8 lambda mode
        // below works as sorting array by curriculum name in default ascending way
        // Collections.reverse or Lists.reverse to reverse the list to get descending array
        list2.sort(Comparator.comparing(c -> c.getName()));
        view.addObject("curs", list2);
        return view;
    }

    // get all subjects inside a curricuulum
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

    // return json: compare both curriculum to get both side subjects
    @RequestMapping("/curriculumcompare/getcurrent")
    @ResponseBody
    public JsonObject GetCurrent(@RequestParam int curId, @RequestParam int newId) {
        JsonObject result = new JsonObject();

        try {
            List<SubjectEntity> subs = this.GetCurrentCurriculumSubjects(curId);
            List<SubjectEntity> newSubs = this.GetCurrentCurriculumSubjects(newId);

            List<SubjectEntity> common = new ArrayList<>();

            for (SubjectEntity s : subs) {
                if (newSubs.stream().anyMatch(c -> c.getId().equals(s.getId()))) {
                    if (!common.contains(s)) common.add(s);
                }
            }

            for (SubjectEntity s : newSubs) {
                if (subs.stream().anyMatch(c -> c.getId().equals(s.getId()))) {
                    if (!common.contains(s)) common.add(s);
                }
            }

            List<SubjectModel> data = common.stream().map(c -> new SubjectModel(c.getId(), c.getName())).collect(Collectors.toList());
            JsonArray aaData = (JsonArray) new Gson().toJsonTree(data);

            result.add("data", aaData);
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }

        return result;
    }

    // return json: compare both curriculum to get uniquesubjects
    @RequestMapping("/curriculumcompare/getnew")
    @ResponseBody
    public JsonObject GetNew(@RequestParam int curId, @RequestParam int newId) {
        JsonObject result = new JsonObject();

        try {
            List<SubjectEntity> subs = this.GetCurrentCurriculumSubjects(curId);
            List<SubjectEntity> newSubs = this.GetCurrentCurriculumSubjects(newId);

            List<SubjectEntity> notcommon = new ArrayList<>();
            for (SubjectEntity s : subs) {
                if (!newSubs.stream().anyMatch(c -> c.getId().equals(s.getId()))) {
                    if (!notcommon.contains(s)) notcommon.add(s);
                }
            }

            for (SubjectEntity s : newSubs) {
                if (!subs.stream().anyMatch(c -> c.getId().equals(s.getId()))) {
                    if (!notcommon.contains(s)) notcommon.add(s);
                }
            }

            List<SubjectModel> data = notcommon.stream().map(c -> new SubjectModel(c.getId(), c.getName())).collect(Collectors.toList());
            JsonArray aaData = (JsonArray) new Gson().toJsonTree(data);

            result.add("data", aaData);
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }

        return result;
    }
}
