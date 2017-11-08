package com.capstone.controllers;

import com.capstone.entities.DocTypeEntity;
import com.capstone.entities.DocumentStudentEntity;
import com.capstone.entities.MarksEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.models.Enums;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class StudentStatusController {

    @RequestMapping("/studentstatus")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("StudentStatus");
        IDocTypeService docTypeService = new DocTypeServiceImpl();
        List<DocTypeEntity> list = docTypeService.getAllDocTypes();
        view.addObject("list", list);
        return view;
    }

    @RequestMapping("/studentstatus/get")
    @ResponseBody
    public JsonObject GetStudents(@RequestParam Map<String, String> params) {
        try {
            JsonObject data = new JsonObject();
            IStudentService studentService = new StudentServiceImpl();

            int docTypeId = Integer.parseInt(params.get("id"));

            List<StudentEntity> list = studentService.getStudentByDocType(docTypeId);

            list = list.stream().filter(c -> Ultilities.containsIgnoreCase(c.getRollNumber(), params.get("sSearch")) ||
                    Ultilities.containsIgnoreCase(c.getFullName(), params.get("sSearch"))).collect(Collectors.toList());

            List<StudentEntity> displayList = new ArrayList<>();
            if (!list.isEmpty()) {
                displayList = list.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
            }

            List<List<String>> result = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            IDocumentStudentService documentStudentService = new DocumentStudentServiceImpl();
            if (!displayList.isEmpty()) {
                displayList.forEach(m -> {
                    List<String> dataList = new ArrayList<>();
                    dataList.add(m.getRollNumber());
                    dataList.add(m.getFullName());
                    dataList.add(sdf.format(m.getDateOfBirth()));
                    dataList.add(m.getGender() == Enums.Gender.MALE.getValue() ? Enums.Gender.MALE.getName() : Enums.Gender.FEMALE.getName());

                    List<Integer> tmp = new ArrayList<>();
                    tmp.add(m.getId());
                    List<DocumentStudentEntity> l = documentStudentService.getDocumentStudentByByStudentId(tmp);

                    dataList.add(l.get(0).getCurriculumId() != null
                            ? l.get(0).getCurriculumId().getProgramId().getName() + "_" + l.get(0).getCurriculumId().getName()
                            : "N/A");
//                    dataList.add(m.getId() + "");

                    dataList.add(l.get(0).getDocumentId() != null
                            ? l.get(0).getDocumentId().getDocTypeId().getName()
                            : "N/A");

                    result.add(dataList);
                });
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

            data.addProperty("iTotalRecords", list.size());
            data.addProperty("iTotalDisplayRecords", list.size());
            data.add("aaData", aaData);
            data.addProperty("sEcho", params.get("sEcho"));

            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
