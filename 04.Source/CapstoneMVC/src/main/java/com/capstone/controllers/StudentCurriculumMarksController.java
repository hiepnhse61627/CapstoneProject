package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.models.CustomUser;
import com.capstone.models.Ultilities;
import com.capstone.services.DocumentStudentServiceImpl;
import com.capstone.services.IDocumentStudentService;
import com.capstone.services.IStudentService;
import com.capstone.services.StudentServiceImpl;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/studentcurriculum")
public class StudentCurriculumMarksController {

    private CustomUser getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser user = (CustomUser) authentication.getPrincipal();
        return user;
    }

    @RequestMapping("/index")
    public ModelAndView Index(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to /studentcurriculum/index");
        ModelAndView view = new ModelAndView("StudentCurriculum");
        view.addObject("title", "Bảng điểm");
        return view;
    }

    @RequestMapping("/getmarks")
    @ResponseBody
    public JsonObject GetMarks(@RequestParam Map<String, String> params) {
        JsonObject data = new JsonObject();

        IStudentService studentService = new StudentServiceImpl();

        try {
            StudentEntity student = studentService.findStudentByEmail(getPrincipal().getUser().getEmail());
            if (student != null) {
                IDocumentStudentService documentStudentService = new DocumentStudentServiceImpl();
                List<Integer> tmp = new ArrayList<>();
                tmp.add(student.getId());
                List<DocumentStudentEntity> docs = documentStudentService.getDocumentStudentByByStudentId(tmp);

                Map<String, List<List<String>>> ma = new TreeMap<>();
                if (!docs.isEmpty()) {
                    for (DocumentStudentEntity doc : docs) {
                        CurriculumEntity cur = doc.getCurriculumId();
                        if (cur != null) {
                            List<SubjectCurriculumEntity> curSubjects = cur.getSubjectCurriculumEntityList();
                            List<String> subjects = new ArrayList<>();
                            for (SubjectCurriculumEntity s : curSubjects) {
                                if (!subjects.contains(s.getSubjectId().getId())) subjects.add(s.getSubjectId().getId());
                            }

                            EntityManagerFactory fac = Persistence.createEntityManagerFactory("CapstonePersistence");
                            EntityManager man = fac.createEntityManager();

                            TypedQuery<MarksEntity> query = man.createQuery("SELECT a FROM MarksEntity a WHERE a.isActivated = true and a.studentId.id = :id AND a.subjectMarkComponentId.subjectId.id IN :list", MarksEntity.class);
                            query.setParameter("id", student.getId());
                            query.setParameter("list", subjects);
                            List<MarksEntity> marks = query.getResultList();

                            if (!marks.isEmpty()) {
                                Table<String, String, List<MarksEntity>> map = Ultilities.StudentSubjectHashmap(marks);
                                Set<String> students = map.rowKeySet();
                                for (String stu : students) {
                                    Map<String, List<MarksEntity>> subs = map.row(stu);
                                    for (Map.Entry<String, List<MarksEntity>> mark : subs.entrySet()) {

                                        List<String> result = new ArrayList<>();
                                        MarksEntity r = null;
                                        for (MarksEntity m : mark.getValue()) {
                                            r = m;
                                            if (m.getStatus().toLowerCase().contains("pass") || m.getStatus().toLowerCase().contains("exempt")) {
                                                break;
                                            }
                                        }
                                        if (r != null) {
                                            result.add(mark.getKey());
                                            result.add(r.getSubjectMarkComponentId().getSubjectId().getName());
                                            result.add(r.getAverageMark().toString());
                                            result.add(r.getStatus());
                                        }

                                        SubjectCurriculumEntity c = null;
                                        for (SubjectCurriculumEntity s : curSubjects) {
                                            if (s.getSubjectId().getId().equals(mark.getKey())) c = s;
                                        }

                                        String term;
                                        if (c != null) {
                                            term = "Học kỳ " + c.getTermNumber();
                                        } else {
                                            term = "Khác";
                                        }

                                        if (ma.get(term) == null) {
                                            List<List<String>> o = new ArrayList<>();
                                            o.add(result);
                                            ma.put(term, o);
                                        } else {
                                            ma.get(term).add(result);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                JsonObject detailList = (JsonObject) new Gson().toJsonTree(ma);
                data.add("data", detailList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }
}
