package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.services.ISubjectCurriculumService;
import com.capstone.services.ISubjectService;
import com.capstone.services.SubjectCurriculumServiceImpl;
import com.capstone.services.SubjectServiceImpl;
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

    @RequestMapping("/subcurriculum")
    public String Index() {
        return "SubjectCurriculum";
    }

    @RequestMapping("/editcurriculum/{curId}")
    public ModelAndView Edit(@PathVariable("curId") int curId) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = emf.createEntityManager();

        ISubjectCurriculumService service = new SubjectCurriculumServiceImpl();
        ISubjectService service2 = new SubjectServiceImpl();

        ModelAndView view = new ModelAndView("EditSubjectCurriculum");
        SubjectCurriculumEntity ent = service.getCurriculumById(curId);
        ent.getCurriculumMappingEntityList().sort(Comparator.comparingInt(c -> c.getOrdering()));
        view.addObject("data", ent);

        Map<String, List<CurriculumMappingEntity>> unsortedmap = new HashMap<>();
        Map<String, List<CurriculumMappingEntity>> map = new TreeMap<>(unsortedmap);
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

        List<SubjectEntity> l2 = service2.getAllSubjects();
        view.addObject("subs", l2);

        return view;
    }

    @RequestMapping(value = "/editcurriculum", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject Edit(@RequestParam() List<String> data, @RequestParam int id, @RequestParam String name, @RequestParam String des) {
        JsonObject obj = new JsonObject();
        ISubjectCurriculumService service = new SubjectCurriculumServiceImpl();
        ISubjectService service2 = new SubjectServiceImpl();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = emf.createEntityManager();

        try {
            SubjectCurriculumEntity ent = service.getCurriculumById(id);

            if (ent != null) {
                if (name != null && !name.isEmpty()) ent.setName(name);
                if (des != null && !des.isEmpty()) ent.setDescription(des);

                System.out.println(ent.getName() + " - " + ent.getDescription());

                List<CurriculumMappingEntity> l = ent.getCurriculumMappingEntityList();
                List<CurriculumMappingEntity> notin = new ArrayList<>();
                for (CurriculumMappingEntity m : l) {
                    boolean flag = false;
                    for (String s : data) {
                        if (!s.toLowerCase().contains("học kỳ") && m.getSubjectEntity().getId().equals(s)) flag = true;
                    }
                    if (!flag) {
                        notin.add(m);
                    }
                }

                if (!notin.isEmpty()) {
                    for (CurriculumMappingEntity m : notin) {
                        em.getTransaction().begin();
                        CurriculumMappingEntity tmp5 = em.merge(m);
                        em.remove(tmp5);
                        em.flush();
                        l.remove(m);
                        ent.setCurriculumMappingEntityList(l);
                        SubjectCurriculumEntity tmp4 = em.merge(ent);
                        em.flush();
                        em.refresh(tmp4);
                        em.getTransaction().commit();

                        ent = tmp4;
                    }
                }

                String term = "";
                int order = 1;
                for (String s : data) {
                    if (s.toLowerCase().contains("học kỳ")) {
                        term = s;
                    } else {
                        l = ent.getCurriculumMappingEntityList();

                        for (CurriculumMappingEntity c : l) {
                            if (c.getSubjectEntity().getId().equals(s)) {
                                int i = l.indexOf(c);
                                c.setTerm(term);
                                c.setOrdering(order++);
                                em.getTransaction().begin();
                                CurriculumMappingEntity tmp = em.merge(c);
                                em.flush();
                                em.refresh(tmp);
                                l.set(i, tmp);
                                ent.setCurriculumMappingEntityList(l);
                                SubjectCurriculumEntity tmp3 = em.merge(ent);
                                em.flush();
                                em.refresh(tmp3);
                                em.getTransaction().commit();
                                break;
                            }
                        }

                        boolean flag = false;
                        for (CurriculumMappingEntity c : l) {
                            if (c.getSubjectEntity().getId().equals(s)) {
                                flag = true;
                                break;
                            }
                        }

                        if (!flag) {
                            em.getTransaction().begin();

                            CurriculumMappingEntity c = new CurriculumMappingEntity();
                            c.setOrdering(order++);
                            c.setTerm(term);

                            CurriculumMappingEntityPK pk = new CurriculumMappingEntityPK();
                            pk.setSubId(s);
                            pk.setCurId(ent.getId());
                            c.setCurriculumMappingEntityPK(pk);

                            em.persist(c);
                            em.flush();
//                            em.merge(c);
                            em.refresh(c);

                            List<CurriculumMappingEntity> tmp = ent.getCurriculumMappingEntityList();
                            tmp.add(c);
                            ent.setCurriculumMappingEntityList(tmp);
                            SubjectCurriculumEntity tmp2 = em.merge(ent);
                            em.flush();
                            em.refresh(tmp2);

                            em.getTransaction().commit();
                        }
                    }
                }
            }
            obj.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            obj.addProperty("success", false);
            obj.addProperty("message", e.getMessage());
        }

        return obj;
    }

    @RequestMapping("/getsubcurriculum")
    @ResponseBody
    public JsonObject GetSubCurriculum(@RequestParam Map<String, String> params) {
        ISubjectCurriculumService service = new SubjectCurriculumServiceImpl();
        ISubjectService service2 = new SubjectServiceImpl();

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
