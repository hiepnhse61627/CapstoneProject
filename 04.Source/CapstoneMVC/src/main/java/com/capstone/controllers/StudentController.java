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

    public List<List<String>> processData(Map<String, String> params) {
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
//        // compared list
//        List<MarksEntity> comparedList = new ArrayList<>();
//        // Init students passed and failed
//        List<MarksEntity> listPassed = markList.stream().filter(p -> p.getStatus().contains("Passed") || p.getStatus().contains("Exempt")).collect(Collectors.toList());
//        List<MarksEntity> listFailed = markList.stream().filter(f -> !f.getStatus().contains("Passed") || !f.getStatus().contains("Exempt")).collect(Collectors.toList());
//        // make comparator
//        Comparator<MarksEntity> comparator = new Comparator<MarksEntity>() {
//            @Override
//            public int compare(MarksEntity o1, MarksEntity o2) {
//                return new CompareToBuilder()
//                        .append(o1.getSubjectMarkComponentId() == null ? "" : o1.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase(), o2.getSubjectMarkComponentId() == null ? "" : o2.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase())
//                        .append(o1.getStudentId().getRollNumber().toUpperCase(), o2.getStudentId().getRollNumber().toUpperCase())
//                        .toComparison();
//            }
//        };
//        Collections.sort(listPassed, comparator);
//        // start compare failed list to passed list
//        for (int i = 0; i < listFailed.size(); i++) {
//            MarksEntity keySearch = listFailed.get(i);
//            int index = Collections.binarySearch(listPassed, keySearch, comparator);
//            if (index < 0) {
//                comparedList.add(keySearch);
//            }
//        }
//        // remove duplicate
//
//        for (MarksEntity marksEntity : comparedList) {
//            if (marksEntity.getSubjectMarkComponentId() != null && !resultList.stream().anyMatch(r -> r.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase().equals(marksEntity.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase())
//                                                && r.getStudentId().getRollNumber().toUpperCase().equals(marksEntity.getStudentId().getRollNumber().toUpperCase()))) {
//                resultList.add(marksEntity);
//            }
//        }

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

//            ISubjectService subjectService = new SubjectServiceImpl();
//            SubjectEntity aSub = subjectService.findSubjectById(subjectId);
//            List<SubjectEntity> aReplace;
//            if (aSub != null) {
//                aReplace = aSub.getSubjectEntityList();
//            } else {
//                aReplace = new ArrayList<>();
//            }

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

                            if (totalFail == sub.getSubjectEntityList().size()) {
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
