package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.SubjectCurriculumEntity;
import com.capstone.services.ISubjectCurriculumService;
import com.capstone.services.SubjectCurriculumServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class SubjectCurriculumController {

    private ISubjectCurriculumService service = new SubjectCurriculumServiceImpl();

    @RequestMapping("/subcurriculum")
    public String Index() {
        return "SubjectCurriculum";
    }

    @RequestMapping("/getsubcurriculum")
    @ResponseBody
    public JsonObject GetSubCurriculum(@RequestParam Map<String, String> params) {
        try {
            JsonObject data = new JsonObject();

            List<SubjectCurriculumEntity> dataList = service.getAllSubjectCurriculum();

            List<SubjectCurriculumEntity> displayList = new ArrayList<>();
            if (!dataList.isEmpty()) {
                displayList = dataList.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
            }

            ArrayList<ArrayList<String>> result = new ArrayList<>();
            if (!displayList.isEmpty()) {
                displayList.forEach(m -> {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(m.getName());
                    tmp.add(m.getDescription());
                    tmp.add(m.getId().toString());
                    result.add(tmp);
                });
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result, new TypeToken<List<MarksEntity>>() {
            }.getType());

            data.addProperty("iTotalRecords", dataList.size());
            data.addProperty("iTotalDisplayRecords", dataList.size());
            data.add("aaData", aaData);
            data.addProperty("sEcho", params.get("sEcho"));

            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
