package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.models.MarkModel;
import com.capstone.models.StudentMarkModel;
import com.capstone.models.Ultilities;
import com.capstone.services.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContext;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class StudentController {

    @Autowired
    ServletContext context;

    IMarksService marksService = new MarksServiceImpl();

    @RequestMapping("/create")
    public String Index() {
        return "CreateNewStudent";
    }

    @RequestMapping("/display")
    public ModelAndView Display() {
        ModelAndView view = new ModelAndView("DisplayStudentPassFail");
        view.addObject("title", "Danh sách sinh viên học lại");

        IRealSemesterService service = new RealSemesterServiceImpl();
        view.addObject("semesters", Ultilities.SortSemesters(service.getAllSemester()));
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

            List<MarksEntity> dataList = this.GetStudentsList(semesterId, subjectId, searchKey);
            List<MarksEntity> displayList = new ArrayList<>();
            if (!dataList.isEmpty()) {
                displayList = dataList.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
            }

            ArrayList<ArrayList<String>> result = new ArrayList<>();
            if (!displayList.isEmpty()) {
                displayList.forEach(m -> {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(m.getStudentId().getRollNumber());
                    tmp.add(m.getStudentId().getFullName());
                    tmp.add(m.getSubjectId() == null ? "N/A" : m.getSubjectId().getSubjectId());
                    tmp.add(m.getCourseId() == null ? "N/A" : m.getCourseId().getClass1());
                    tmp.add(m.getSemesterId() == null ? "N/A" : m.getSemesterId().getSemester());
                    tmp.add(String.valueOf(m.getAverageMark()));
                    tmp.add(m.getStatus());
                    tmp.add(m.getStudentId().getId() + "");
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

    public List<MarksEntity> GetStudentsList(String semesterId, String subjectId, String searchKey) {
        List<MarksEntity> markList = marksService.getMarkByConditions(semesterId, subjectId, searchKey);
        // result list
        List<MarksEntity> resultList = new ArrayList<>();
        // compared list
        List<MarksEntity> comparedList = new ArrayList<>();
        // Init students passed and failed
        List<MarksEntity> listPassed = markList.stream().filter(p -> p.getStatus().contains("Passed")).collect(Collectors.toList());
        List<MarksEntity> listFailed = markList.stream().filter(f -> !f.getStatus().contains("Passed")).collect(Collectors.toList());
        // make comparator
        Comparator<MarksEntity> comparator = new Comparator<MarksEntity>() {
            @Override
            public int compare(MarksEntity o1, MarksEntity o2) {
                return new CompareToBuilder()
                        .append(o1.getSubjectId() == null ? "" : o1.getSubjectId().getSubjectId().toUpperCase(), o2.getSubjectId() == null ? "" : o2.getSubjectId().getSubjectId().toUpperCase())
                        .append(o1.getStudentId().getRollNumber().toUpperCase(), o2.getStudentId().getRollNumber().toUpperCase())
                        .toComparison();
            }
        };
        Collections.sort(listPassed, comparator);
        // start compare failed list to passed list
        for (int i = 0; i < listFailed.size(); i++) {
            MarksEntity keySearch = listFailed.get(i);
            int index = Collections.binarySearch(listPassed, keySearch, comparator);
            if (index < 0) {
                comparedList.add(keySearch);
            }
        }
        // remove duplicate
        
        for (MarksEntity marksEntity : comparedList) {
            if (marksEntity.getSubjectId() != null && !resultList.stream().anyMatch(r -> r.getSubjectId().getSubjectId().toUpperCase().equals(marksEntity.getSubjectId().getSubjectId().toUpperCase())
                                                && r.getStudentId().getRollNumber().toUpperCase().equals(marksEntity.getStudentId().getRollNumber().toUpperCase()))) {
                resultList.add(marksEntity);
            }
        }

        return resultList;
    }

    @RequestMapping(value = "/student/getAllLatestMarks", method = RequestMethod.POST)
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

                for (MarkModel data : dataList) {
                    if (data.getSubject().equals(subjectCode)) {
                        curMark = data;
                        break;
                    }
                }

                if (curMark == null) {
                    MarkModel mark = new MarkModel();
                    mark.setSemester(m.getSemesterId().getSemester());
                    mark.setSubject(subjectCode);
                    mark.setClass1(m.getCourseId().getClass1());
                    mark.setStatus(m.getStatus());
                    mark.setAverageMark(m.getAverageMark());
                    mark.setRepeatingNumber(1);
                    mark.setStartDate(m.getCourseId().getStartDate());
                    mark.setEndDate(m.getCourseId().getEndDate());

                    dataList.add(mark);
                } else {
                    curMark.setSemester(m.getSemesterId().getSemester());
                    curMark.setClass1(m.getCourseId().getClass1());
                    curMark.setStatus(m.getStatus());
                    curMark.setAverageMark(m.getAverageMark());
                    curMark.setRepeatingNumber(curMark.getRepeatingNumber() + 1);
                    curMark.setStartDate(curMark.getStartDate());
                    curMark.setEndDate(curMark.getEndDate());
                }
            }
//            dataList = Ultilities.SortMarkModelBySemester(dataList);

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
