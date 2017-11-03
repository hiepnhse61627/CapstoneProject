package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.models.*;
import com.capstone.services.*;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private String searchKey = "";

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

            String searchKey = params.get("sSearch");

            List<List<String>> result = processData(params);

            result = result.stream().filter(c -> c.get(0).contains(searchKey) ||
                    c.get(1).contains(searchKey)).collect(Collectors.toList());

            List<List<String>> display = new ArrayList<>();
            if (!result.isEmpty()) {
                display = result.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(display);

            data.addProperty("iTotalRecords", result.size());
            data.addProperty("iTotalDisplayRecords", result.size());
            data.add("aaData", aaData);
            data.addProperty("sEcho", params.get("sEcho"));

            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(value = "/getstudents/studentsDistinct", method = RequestMethod.GET)
    @ResponseBody
    public JsonObject getStudentDistinctNumber(@RequestParam("semesterId") String semesterId, @RequestParam("subjectId") String subjectId) {
        JsonObject jsonObject = new JsonObject();

        List<MarksEntity> dataList = this.GetStudentsList(semesterId, subjectId, this.searchKey);
        List<String> studentList = dataList.stream().map(d -> d.getStudentId().getRollNumber()).distinct().collect(Collectors.toList());

        jsonObject.addProperty("success", true);
        jsonObject.addProperty("studentSize", studentList.size());

        return jsonObject;
    }

    public List<List<String>> processData(Map<String, String> params) {
        this.searchKey = params.get("sSearch");
        String semesterId = params.get("semesterId");
        String subjectId = params.get("subjectId");
        String searchKey = params.get("sSearch");

        List<MarksEntity> dataList = this.GetStudentsList(semesterId, subjectId, searchKey);

        List<List<String>> result = new ArrayList<>();
        if (!dataList.isEmpty()) {
            List<List<String>> finalResult = result;
            dataList.forEach(m -> {
                ArrayList<String> tmp = new ArrayList<>();
                tmp.add(m.getStudentId().getRollNumber());
                tmp.add(m.getStudentId().getFullName());
                tmp.add(m.getSubjectMarkComponentId() == null ? "N/A" : m.getSubjectMarkComponentId().getSubjectId().getId());
                tmp.add(m.getSemesterId() == null ? "N/A" : m.getSemesterId().getSemester());
                tmp.add(String.valueOf(m.getAverageMark()));
                tmp.add(m.getStatus());
                tmp.add(m.getStudentId().getId() + "");
                finalResult.add(tmp);
            });
            result = finalResult;
        }

        return result;
    }

    public List<MarksEntity> GetStudentsList(String semesterId, String subjectId, String searchKey) {
        List<MarksEntity> markList = marksService.getMarkByConditions(semesterId, subjectId, searchKey);
        // result list
        List<MarksEntity> resultList = new ArrayList<>();
        Table<String, String, List<MarksEntity>> map = HashBasedTable.create();
        if (!markList.isEmpty()) {
            for (MarksEntity m : markList) {
                if (map.get(m.getStudentId().getRollNumber(), m.getSubjectMarkComponentId().getSubjectId().getId()) == null) {
                    List<MarksEntity> newMarkList = new ArrayList<>();
                    newMarkList.add(m);
                    map.put(m.getStudentId().getRollNumber(), m.getSubjectMarkComponentId().getSubjectId().getId(), newMarkList);
                } else {
                    map.get(m.getStudentId().getRollNumber(), m.getSubjectMarkComponentId().getSubjectId().getId()).add(m);
                }
            }

            Set<String> studentIds = map.rowKeySet();
            for (String studentId : studentIds) {
                Map<String, List<MarksEntity>> subject = map.row(studentId);
                for (Map.Entry<String, List<MarksEntity>> entry : subject.entrySet()) {
//                    if (!aReplace.stream().anyMatch(c -> c.getId().equals(entry.getKey()))) {
                    boolean isPass = false;

                    List<MarksEntity> g = Ultilities.FilterStudentsOnlyPassAndFail(entry.getValue().stream().filter(c -> !c.getStatus().toLowerCase().contains("studying")).collect(Collectors.toList()));

                    if (!g.isEmpty()) {
                        MarksEntity tmp = null;
                        for (MarksEntity k2 : g) {
                            tmp = k2;
                            if (k2.getStatus().toLowerCase().contains("pass") || k2.getStatus().toLowerCase().contains("exempt")) {
                                isPass = true;
                                break;
                            }
                        }

                        if (!isPass) {
                            SubjectEntity sub = tmp.getSubjectMarkComponentId().getSubjectId();

                            int totalFail = 0;
                            MarksEntity failedRow = tmp;

                            for (SubjectEntity replace : sub.getSubjectEntityList()) {
//                                    List<MarksEntity> replaced = marksService.getAllMarksByStudentAndSubject(tmp.getStudentId().getId(), replace.getId(), semesterId);
                                List<MarksEntity> replaced = subject.get(replace.getId());
                                if (replaced != null) {
                                    for (MarksEntity marks : replaced) {
                                        tmp = marks;
                                        if (marks.getStatus().toLowerCase().contains("pass") || marks.getStatus().toLowerCase().contains("exempt")) {
                                            isPass = true;
                                            break;
                                        }
                                    }
                                }

                                if (!isPass) {
                                    failedRow = tmp;
                                    totalFail++;
                                }
                            }

                            String studentRollNumber = failedRow.getStudentId().getRollNumber();
                            String subjectCd = failedRow.getSubjectMarkComponentId().getSubjectId().getId();

                            if (totalFail == sub.getSubjectEntityList().size()
                                    && !resultList.stream().anyMatch(r -> r.getStudentId().getRollNumber().equalsIgnoreCase(studentRollNumber)
                                                                     && r.getSubjectMarkComponentId().getSubjectId().getId().equalsIgnoreCase(subjectCd))) {
                                resultList.add(failedRow);
                            }
                        }
                    }
//                    }
                }
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
            mlist = Ultilities.FilterStudentsOnlyPassAndFail(mlist);

            boolean isFound;
            MarkModel curMark;
            List<MarkModel> dataList = new ArrayList<>();

            for (MarksEntity m : mlist) {
                String subjectCode = m.getSubjectMarkComponentId() != null ? m.getSubjectMarkComponentId().getSubjectId().getId() : "N/A";
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
//                    mark.setClass1(m.getCourseId().getClass1());
                    mark.setStatus(m.getStatus());
                    mark.setAverageMark(m.getAverageMark());
                    mark.setRepeatingNumber(1);

                    dataList.add(mark);
                } else {
                    curMark.setSemester(m.getSemesterId().getSemester());
//                    curMark.setClass1(m.getCourseId().getClass1());
                    curMark.setStatus(m.getStatus());
                    curMark.setAverageMark(m.getAverageMark());
                    curMark.setRepeatingNumber(curMark.getRepeatingNumber() + 1);
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


    // ------------------ Role_Student ----------------------
    @RequestMapping("/studentMarkHistory")
    public ModelAndView StudentMarkHistory() {
        ModelAndView view = new ModelAndView("StudentMarkHistory");
        view.addObject("title", "Lịch sử môn học");

        return view;
    }

    @RequestMapping("/studentMarkHistory/getMarkList")
    @ResponseBody
    public JsonObject StudentMarkHistoryDataTable() {
        JsonObject jsonObject = new JsonObject();
        IStudentService studentService = new StudentServiceImpl();
        IMarksService marksService = new MarksServiceImpl();
        List<List<String>> result = new ArrayList<>();

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            if (customUser.getUser().getStudentRollNumber() != null) {
                StudentEntity student = studentService.findStudentByRollNumber(customUser.getUser().getStudentRollNumber());
                List<MarksEntity> markList = marksService.getStudentMarksByStudentIdAndSortBySubjectName(student.getId());

                for (MarksEntity mark : markList) {
                    List<String> row = new ArrayList<>();

                    List<SubjectEntity> replacementSubjects = mark.getSubjectMarkComponentId()
                            .getSubjectId().getSubjectEntityList();
                    String tmp = "";
                    int count = 0;
                    for (SubjectEntity subject : replacementSubjects) {
                        tmp += subject.getId() + (count != replacementSubjects.size() - 1 ? ", " : "");
                        count++;
                    }

                    row.add(mark.getSubjectMarkComponentId().getSubjectId().getId());
                    row.add(mark.getSubjectMarkComponentId().getSubjectId().getName());
                    row.add(tmp);
                    row.add(mark.getSemesterId().getSemester());
                    row.add(mark.getAverageMark() + "");
                    row.add(mark.getStatus());

                    result.add(row);
                }
            }

            JsonArray jsonArray = (JsonArray) new Gson().toJsonTree(result);
            jsonObject.add("markList", jsonArray);
            jsonObject.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
            jsonObject.addProperty("success", false);
        }

        return jsonObject;
    }
}
