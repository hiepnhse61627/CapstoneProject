package com.capstone.controllers;

import com.capstone.entities.CurriculumMappingEntity;
import com.capstone.entities.MarksEntity;
import com.capstone.entities.SubjectCurriculumEntity;
import com.capstone.services.ISubjectCurriculumService;
import com.capstone.services.SubjectCurriculumServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class SubjectCurriculumController {

    private ISubjectCurriculumService service = new SubjectCurriculumServiceImpl();

    @RequestMapping("/subcurriculum")
    public String Index() {
        return "SubjectCurriculum";
    }

    @RequestMapping("/editcurriculum/{curId}")
    public ModelAndView Edit(@PathVariable("curId") int curId) {
        ModelAndView view = new ModelAndView("EditSubjectCurriculum");
        SubjectCurriculumEntity ent = service.getCurriculumById(curId);
        ent.getCurriculumMappingEntityList().sort(Comparator.comparingInt(c -> c.getOrdering()));
        view.addObject("data", ent);

        Map<String, List<CurriculumMappingEntity>> map = new HashMap<>();
        for (CurriculumMappingEntity en : ent.getCurriculumMappingEntityList()) {
            if (map.get(en.getTerm()) == null) {
                List<CurriculumMappingEntity> tmp = new ArrayList<>();
                tmp.add(en);
                map.put(en.getTerm(), tmp);
            } else {
                map.get(en.getTerm()).add(en);
            }
        }
        view.addObject("list", map);

        return view;
    }

    @RequestMapping(value = "/editcurriculum", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject Edit(@RequestParam() List<String> data, @RequestParam int id) {
        JsonObject obj = new JsonObject();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = emf.createEntityManager();

        try {
            SubjectCurriculumEntity ent = service.getCurriculumById(id);
            if (ent != null) {
                System.out.println(ent.getName() + " - " + ent.getDescription());

                String term = "";
                int order = 1;
                for (String s: data) {
                    if (s.toLowerCase().contains("học kỳ")) {
                        term = s;
                    } else {
                        List<CurriculumMappingEntity> l = ent.getCurriculumMappingEntityList();
                        for (CurriculumMappingEntity c : l) {
                            if (c.getSubjectEntity().getId().equals(s)) {
                                c.setTerm(term);
                                c.setOrdering(order++);
                                em.getTransaction().begin();
                                em.merge(c);
                                em.getTransaction().commit();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
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
