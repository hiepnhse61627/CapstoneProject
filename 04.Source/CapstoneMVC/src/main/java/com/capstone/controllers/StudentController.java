package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.models.MarkModel;
import com.capstone.models.StudentMarkModel;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class StudentController {

    IMarksService marksService = new MarksServiceImpl();

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
    public JsonObject GetStudents(@RequestParam Map<String, String> params) {
        try {
            JsonObject data = new JsonObject();

            String semesterId = params.get("semesterId");
            String subjectId = params.get("subjectId");
            String searchKey = params.get("sSearch");

            List<MarksEntity> set = marksService.getMarkByConditions(semesterId, subjectId, searchKey);
            List<MarksEntity> set2 = new ArrayList<>();
            List<MarksEntity> set3 = new ArrayList<>();
            Table<String, String, List<MarksEntity>> map = HashBasedTable.create();

            if (!set.isEmpty()) {
                List<MarksEntity> filtered = set.stream().filter(a -> a.getSubjectId() != null).collect(Collectors.toList());
                for (MarksEntity m : filtered) {
                    if (map.get(m.getStudentId().getRollNumber(), m.getSubjectId().getSubjectId()) == null) {
                        List<MarksEntity> list = new ArrayList<>();
                        list.add(m);
                        map.put(m.getStudentId().getRollNumber(), m.getSubjectId().getSubjectId(), Ultilities.SortMarkBySemester(list));
                    } else {
                        map.get(m.getStudentId().getRollNumber(), m.getSubjectId().getSubjectId()).add(m);
                        Ultilities.SortMarkBySemester(map.get(m.getStudentId().getRollNumber(), m.getSubjectId().getSubjectId()));
                    }
                }

                for (Table.Cell<String, String, List<MarksEntity>> cell : map.cellSet()) {
                    if (cell.getValue().get(cell.getValue().size() - 1).getStatus().equals("Fail")) {
                        set2.add(cell.getValue().get(cell.getValue().size() - 1));
                    }
                }

                set.stream().filter(a -> a.getSubjectId() == null).forEach(c -> {
                    if (c.getStatus().equals("Fail"))  set2.add(c);
                });

                set3 = set2.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
            }

            ArrayList<ArrayList<String>> parent = new ArrayList<>();
            if (!set3.isEmpty()) {
                set3.forEach(m -> {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(m.getStudentId().getRollNumber());
                    tmp.add(m.getStudentId().getFullName());
                    tmp.add(m.getSubjectId() == null ? "N/A" : m.getSubjectId().getSubjectId());
                    tmp.add(m.getCourseId() == null ? "N/A" : m.getCourseId().getClazz());
                    tmp.add(m.getSemesterId() == null ? "N/A" : m.getSemesterId().getSemester());
                    tmp.add(String.valueOf(m.getAverageMark()));
                    tmp.add(m.getStatus());
                    tmp.add(m.getStudentId().getId() + "");
                    parent.add(tmp);
                });
            }

            JsonArray result = (JsonArray) new Gson().toJsonTree(parent, new TypeToken<List<MarksEntity>>() {
            }.getType());

            data.addProperty("iTotalRecords", set2.size());
            data.addProperty("iTotalDisplayRecords", set2.size());
            data.add("aaData", result);
            data.addProperty("sEcho", params.get("sEcho"));

            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(value = "/student/getAllMarks", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject GetAllStudentMarks(int studentId) {
        JsonObject result = new JsonObject();

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            TypedQuery<MarksEntity> query = em.createQuery(
                    "SELECT m FROM MarksEntity m WHERE m.studentId.id = :sid", MarksEntity.class);
            query.setParameter("sid", studentId);
            List<MarksEntity> mlist = query.getResultList();
            mlist = Ultilities.SortMarkBySemester(mlist);

            boolean isFound;
            MarkModel curMark;
            List<MarkModel> dataList = new ArrayList<>();

            for (MarksEntity m : mlist) {
                String subjectCode = m.getSubjectId() != null ? m.getSubjectId().getSubjectId() : "N/A";
                curMark = null;
                isFound = false;

                for (MarkModel data : dataList) {
                    if (data.getSubject().equals(subjectCode)) {
                        isFound = true;
                        curMark = data;
                        break;
                    }
                }

                if (!isFound) {
                    MarkModel mark = new MarkModel();
                    mark.setSemester(m.getSemesterId().getSemester());
                    mark.setSubject(subjectCode);
                    mark.setClass1(m.getCourseId().getClazz());
                    mark.setStatus(m.getStatus());
                    mark.setAverageMark(m.getAverageMark());
                    mark.setRepeatingNumber(1);

                    dataList.add(mark);
                } else {
                    curMark.setSemester(m.getSemesterId().getSemester());
                    curMark.setClass1(m.getCourseId().getClazz());
                    curMark.setStatus(m.getStatus());
                    curMark.setAverageMark(m.getAverageMark());
                    curMark.setRepeatingNumber(curMark.getRepeatingNumber() + 1);
                }
            }
            dataList = Ultilities.SortMarkModelBySemester(dataList);

            StudentMarkModel model = new StudentMarkModel();
            MarksEntity firstRecord = mlist.get(0);
            model.setStudentId(studentId);
            model.setStudentName(firstRecord.getStudentId().getFullName());
            model.setRollNumber(firstRecord.getStudentId().getRollNumber());
            model.setMarkList(dataList);

            String data = new Gson().toJson(model);

            result.addProperty("success", true);
            result.addProperty("studentMarkDetail", data);
        } catch (Exception e) {
            result.addProperty("success", false);
            result.addProperty("error", e.getMessage());
        }

        return result;
    }

    @RequestMapping(value = "/createnew", method = RequestMethod.POST)
    @ResponseBody
    public StudentEntity CreateNewStudent(@RequestBody StudentEntity student) {
        System.out.println(student.getFullName());
        System.out.println(student.getRollNumber());
        return student;
    }
}
