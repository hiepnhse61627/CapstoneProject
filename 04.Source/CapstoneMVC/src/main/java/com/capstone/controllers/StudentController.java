package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.models.*;
import com.capstone.services.*;

import com.capstone.services.customSecurity.MySecurity;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    IStudentService studentService = new StudentServiceImpl();

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
            List<List<String>> result = processData(params);
            String searchKey = params.get("sSearch").toLowerCase();
            result = result.stream().filter(c -> c.get(0).toLowerCase().contains(searchKey) ||
                    c.get(2).toLowerCase().contains(searchKey) ||
                    c.get(3).toLowerCase().contains(searchKey) ||
                    c.get(5).toLowerCase().contains(searchKey)).collect(Collectors.toList());

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
                                    for (SubjectEntity rep : replace.getSubjectEntityList1()) {
                                        List<MarksEntity> repd = subject.get(rep.getId());
                                        if (repd != null) {
                                            for (MarksEntity marks : repd) {
                                                tmp = marks;
                                                if (marks.getStatus().toLowerCase().contains("pass") || marks.getStatus().toLowerCase().contains("exempt")) {
                                                    isPass = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                    if (!isPass) {
                                        for (SubjectEntity rep : replace.getSubjectEntityList1()) {
                                            for (SubjectEntity r : rep.getSubjectEntityList()) {
                                                List<MarksEntity> repd = subject.get(r.getId());
                                                if (repd != null) {
                                                    for (MarksEntity marks : repd) {
                                                        tmp = marks;
                                                        if (marks.getStatus().toLowerCase().contains("pass") || marks.getStatus().toLowerCase().contains("exempt")) {
                                                            isPass = true;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (!isPass) {
                                            failedRow = tmp;
                                            totalFail++;
                                        }
                                    }
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
                    "SELECT m FROM MarksEntity m WHERE m.isActivated = true and m.studentId.id = :sid", MarksEntity.class);
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


    @RequestMapping("/studentsFailedCreditsPage")
    public ModelAndView goStudentsFailedCreditsPage() {
//        if (!MySecurity.hasPermission("studentsFailedCreditsPage")) {
//            ModelAndView errPage = new ModelAndView("PermissionError");
//            errPage.addObject("title", "Lỗi");
//            return errPage;
//        }
        ModelAndView mav = new ModelAndView("studentsFailedCredits");
        mav.addObject("title", "Danh sách sinh viên nợ tín chỉ");

        return mav;
    }

    @RequestMapping("/studentFailCreditsPage")
    public ModelAndView goStudentFailCreditPage() {
        ModelAndView mav = new ModelAndView("StudentFailCredit");
        mav.addObject("title","Danh sách sinh viên đang nợ tín chỉ 2");
        return mav;
    }

    @RequestMapping("/studentFailCredits")
    @ResponseBody
    public JsonObject studentFailCredits(@RequestParam Map<String, String> params) {
        JsonObject jsonObject = new JsonObject();

        Integer numbersOfCredit = Integer.valueOf(params.get("numOfCredits"));
        List<StudentFailedSubject> students = studentService.getStudentFailCreditsByCredits(numbersOfCredit);

        List<List<String>> results = new ArrayList<>();
        for (StudentFailedSubject student : students) {
            List<String> studentInfo = new ArrayList<>();
            studentInfo.add(student.getStudentCode());
            studentInfo.add(student.getStudentName());
            studentInfo.add(student.getSubjectFailed());
            studentInfo.add(student.getSubjectRelearned());
            studentInfo.add(String.valueOf(student.getFailedCredit()));
            results.add(studentInfo);
        }

        List<List<String>> displayList = new ArrayList<>();
        if (!results.isEmpty()) {
//            displayList = results.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
            displayList = results.stream().collect(Collectors.toList());
        }

        JsonArray aaData = (JsonArray) new Gson().toJsonTree(displayList);

        jsonObject.addProperty("iTotalRecords", results.size());
        jsonObject.addProperty("iTotalDisplayRecords", results.size());
        jsonObject.add("aaData", aaData);
        jsonObject.addProperty("sEcho", params.get("sEcho"));

        return jsonObject;
    }


    @RequestMapping("/studentsFailedCredits")
    @ResponseBody
    public JsonObject studentFailedCredits(@RequestParam Map<String, String> params) {
        JsonObject jsonObject = new JsonObject();

        Integer credits = Integer.valueOf(params.get("credits"));
        List<StudentEntity> students = studentService.getStudentFailedMoreThanRequiredCredits(credits);

        List<List<String>> results = new ArrayList<>();

        for (StudentEntity student : students) {
            List<String> studentInfo = new ArrayList<>();
            studentInfo.add(student.getRollNumber());
            studentInfo.add(student.getFullName());
            studentInfo.add(String.valueOf(student.getPassFailCredits()));
            studentInfo.add(String.valueOf(student.getPassCredits()));
            studentInfo.add(String.valueOf(student.getPassFailCredits() - student.getPassCredits()));

            results.add(studentInfo);
        }

        List<List<String>> displayList = new ArrayList<>();
        if (!results.isEmpty()) {
            displayList = results.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
        }

        JsonArray aaData = (JsonArray) new Gson().toJsonTree(displayList);

        jsonObject.addProperty("iTotalRecords", results.size());
        jsonObject.addProperty("iTotalDisplayRecords",  results.size());
        jsonObject.add("aaData", aaData);
        jsonObject.addProperty("sEcho", params.get("sEcho"));

        return jsonObject;
    }

    @RequestMapping("/subjectsTryingToPassPage")
    public ModelAndView goSubjectsTryingToPassPage(){
        ModelAndView mav = new ModelAndView("SubjectsTryingToPass");
        mav.addObject("title","Danh sách môn sinh viên cố gắng vượt qua");
        IRealSemesterService service = new RealSemesterServiceImpl();
        mav.addObject("semesters", Ultilities.SortSemesters(service.getAllSemester()));
        return mav;
    }

    @RequestMapping("/subjectsTryingToPass")
    @ResponseBody
    public JsonObject subjectsTryingToPass(@RequestParam Map<String, String> params){
        JsonObject jsonObject = new JsonObject();

        Integer semester = Integer.valueOf(params.get("semesterId"));
        List<StudentFailedSubject> subjects = studentService.getSubjectsSlotsFailedBySemester(semester);

        List<List<String>> results = new ArrayList<>();
        for (StudentFailedSubject subject : subjects) {
            List<String> displayInfo = new ArrayList<>();
            displayInfo.add(subject.getStudentCode());
            displayInfo.add(subject.getStudentName());
            displayInfo.add(String.valueOf(subject.getFailedCredit()));
            displayInfo.add(subject.getSubjectFailed());
            results.add(displayInfo);
        }

        List<List<String>> displayList = new ArrayList<>();
        if (!results.isEmpty()) {
            //displayList = results.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
            displayList = results.stream().collect(Collectors.toList());
        }

        JsonArray aaData = (JsonArray) new Gson().toJsonTree(displayList);

        jsonObject.addProperty("iTotalRecords", results.size());
        jsonObject.addProperty("iTotalDisplayRecords",  results.size());
        jsonObject.add("aaData", aaData);
        jsonObject.addProperty("sEcho", params.get("sEcho"));

        return jsonObject;
    }

    @RequestMapping("/subjectsSlotsTryingToPassPage")
    public ModelAndView goSubjectsSlotsTryingToPassPage(){
        ModelAndView mav = new ModelAndView("SubjectsSlotsTryingToPass");
        mav.addObject("title","Danh sách lượt môn sinh viên cố gắng vượt qua");
        IRealSemesterService service = new RealSemesterServiceImpl();
        mav.addObject("semesters", Ultilities.SortSemesters(service.getAllSemester()));
        return mav;
    }

    @RequestMapping("/subjectsSlotsTryingToPass")
    @ResponseBody
    public JsonObject subjectsSlotsTryingToPass(@RequestParam Map<String, String> params){
        JsonObject jsonObject = new JsonObject();

        Integer semester = Integer.valueOf(params.get("semesterId"));
        List<StudentFailedSubject> subjects = studentService.getSubjectsSlotsFailedBySemester(semester);

        List<List<String>> results = new ArrayList<>();
        for (StudentFailedSubject subject : subjects) {
            List<String> displayInfo = new ArrayList<>();
            displayInfo.add(subject.getStudentCode());
            displayInfo.add(subject.getStudentName());
            displayInfo.add(String.valueOf(subject.getFailedCredit()));
            displayInfo.add(subject.getSubjectFailed());
            results.add(displayInfo);
        }

        List<List<String>> displayList = new ArrayList<>();
        if (!results.isEmpty()) {
            //displayList = results.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
            displayList = results.stream().collect(Collectors.toList());
        }

        JsonArray aaData = (JsonArray) new Gson().toJsonTree(displayList);

        jsonObject.addProperty("iTotalRecords", results.size());
        jsonObject.addProperty("iTotalDisplayRecords", results.size());
        jsonObject.add("aaData", aaData);
        jsonObject.addProperty("sEcho", params.get("sEcho"));

        return jsonObject;
    }

    @RequestMapping("/studentsStudyResults")
    public ModelAndView studentsStudyInformation() {
        ModelAndView view = new ModelAndView("StudentsStudyInformations");
        view.addObject("title", "Thông tin kết quả học tập theo kì");

        IRealSemesterService semesterService = new RealSemesterServiceImpl();
        List<RealSemesterEntity> semesterList = semesterService.getAllSemester();
        semesterList = Ultilities.SortSemesters(semesterList);
        semesterList = Lists.reverse(semesterList);

        view.addObject("semesterList", semesterList);

        return view;
    }

    //Sinh viên không được xếp lớp
    @RequestMapping("/studentsNotBeingArrangePage")
    public ModelAndView studentsNotBeingArrangeClass() {
        ModelAndView view = new ModelAndView("StudentsAreNotBeingArrangedClass");
        view.addObject("title", "Danh sách sinh viên không được xếp lớp");

        IRealSemesterService semesterService = new RealSemesterServiceImpl();
        List<RealSemesterEntity> semesterList = semesterService.getAllSemester();
        semesterList = Ultilities.SortSemesters(semesterList);
        semesterList = Lists.reverse(semesterList);

        view.addObject("semesterList", semesterList);

        return view;
    }

    @RequestMapping("/studentsNotBeingArrangeData")
    @ResponseBody
    public JsonObject getStudentsNotBeingArrangeClassData(@RequestParam Map<String, String> params) {

        JsonObject jsonObject = new JsonObject();

        Integer semesterId = Integer.valueOf(params.get("semesterId"));
        StudentStatusServiceImpl statusService = new StudentStatusServiceImpl();
        List<String> statusList = new ArrayList<>();
        statusList.add("HD");
        statusList.add("HL");

        // [A] get students with status ['HD' : Học đi, 'HL': Học lại] from status table
        List<StudentEntity> studentsFromStatus =
                studentService.getStudentBySemesterIdAndStatus(semesterId, statusList);

        //[B] get students From Marks table
        List<StudentEntity> studentsFromMarks = studentService.getStudentsFromMarksBySemester(semesterId);

        //list of students are not being arranged class,
        // use [A] left outer join [B], filter all rows of A that not match B
        List<StudentEntity> notArrangedClass = studentsFromStatus.stream().filter(q ->
                !studentsFromMarks.stream().anyMatch(a -> a.getId() == q.getId())).collect(Collectors.toList());


        List<List<String>> results = new ArrayList<>();
        for (StudentEntity student : notArrangedClass) {
            List<String> studentInfo = new ArrayList<>();
            //MSSV
            studentInfo.add(student.getRollNumber());
            //Tên
            studentInfo.add(student.getFullName());
            //Ngành
            studentInfo.add(student.getProgramId().getName());
            //Kì học
            studentInfo.add(student.getTerm() + "");
            //Trạng thái
            StudentStatusEntity studentStatus = statusService.getStudentStatusBySemesterIdAndStudentId(semesterId, student.getId());
            studentInfo.add(studentStatus.getStatus());

            studentInfo.add("-");
            results.add(studentInfo);
        }

        List<List<String>> displayList = new ArrayList<>();
        if (!results.isEmpty()) {
//            displayList = results.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
            displayList = results;
        }

        JsonArray aaData = (JsonArray) new Gson().toJsonTree(displayList);

        jsonObject.addProperty("iTotalRecords", displayList.size());
        jsonObject.addProperty("iTotalDisplayRecords", displayList.size());
        jsonObject.add("aaData", aaData);
        jsonObject.addProperty("sEcho", params.get("sEcho"));

        return jsonObject;
    }

    @RequestMapping("/studentsBySemesterAndProgramPage")
    public ModelAndView StudentsBySemesterPage() {
        ModelAndView view = new ModelAndView("StudentBySemester");
        view.addObject("title", "Danh sách thông tin sinh viên theo kỳ và ngành");

        IRealSemesterService semesterService = new RealSemesterServiceImpl();
        ProgramServiceImpl programService = new ProgramServiceImpl();
        List<ProgramEntity> programList = programService.getAllPrograms();

        List<RealSemesterEntity> semesterList = semesterService.getAllSemester();
        semesterList = Ultilities.SortSemesters(semesterList);
        semesterList = Lists.reverse(semesterList);

        view.addObject("programList", programList);
        view.addObject("semesterList", semesterList);

        return view;
    }


    @RequestMapping("/studentsBySemesterAndProgramData")
    @ResponseBody
    public JsonObject getstudentsBySemesterAndProgramData(@RequestParam Map<String, String> params) {

        JsonObject jsonObject = new JsonObject();

        Integer semesterId = Integer.parseInt(params.get("semesterId"));
        Integer programId = Integer.parseInt(params.get("programId"));
        StudentStatusServiceImpl statusService = new StudentStatusServiceImpl();
        StudentServiceImpl studentService = new StudentServiceImpl();
        List<StudentEntity> studentList = studentService
                .getStudentBySemesterIdAndProgram(semesterId, programId);


        List<List<String>> results = new ArrayList<>();
        for (StudentEntity student : studentList) {
            List<String> studentInfo = new ArrayList<>();
            //MSSV
            studentInfo.add(student.getRollNumber());
            //Tên
            studentInfo.add(student.getFullName());
            //Ngành
            studentInfo.add(student.getProgramId().getName());
            //Kì học
            studentInfo.add(student.getTerm() + "");
            //Trạng thái
            StudentStatusEntity studentStatus = statusService.getStudentStatusBySemesterIdAndStudentId(semesterId, student.getId());
            studentInfo.add(studentStatus.getStatus());

            results.add(studentInfo);
        }

        List<List<String>> displayList = new ArrayList<>();

        //comment saving for serverSide DataTable
        if (!results.isEmpty()) {
//            displayList = results.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
            displayList = results;
        }

        JsonArray aaData = (JsonArray) new Gson().toJsonTree(displayList);

        jsonObject.addProperty("iTotalRecords", displayList.size());
        jsonObject.addProperty("iTotalDisplayRecords", displayList.size());
        jsonObject.add("aaData", aaData);
        jsonObject.addProperty("sEcho", params.get("sEcho"));

        return jsonObject;
    }

}
