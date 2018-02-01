package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Controller
public class StudentArrangementController {
    @Autowired
    ServletContext context;

    private final String xlsExcelExtension = "xls";
    private final String xlsxExcelExtension = "xlsx";

    private int totalStudents;
    private int countStudents;
    private boolean file1Done;
    private boolean file2Done;
    private boolean file3Done;
    private boolean process1;
    private boolean process2;

    @RequestMapping("/studentArrangement")
    public ModelAndView StudentArrangementIndex() {
        ModelAndView view = new ModelAndView("StudentArrangement");
        view.addObject("title", "Dự kiến xếp lớp");

        IRealSemesterService semesterService = new RealSemesterServiceImpl();
        List<RealSemesterEntity> semesters = semesterService.getAllSemester();
        semesters = Ultilities.SortSemesters(semesters);
        view.addObject("semesters", semesters);

        return view;
    }

    @RequestMapping("/studentArrangementBySlot")
    public ModelAndView StudentArrangementBySlotIndex() {
        ModelAndView view = new ModelAndView("StudentArrangementBySlot");
        view.addObject("title", "Danh sách sinh viên lớp môn theo slot");

        IRealSemesterService semesterService = new RealSemesterServiceImpl();
        List<RealSemesterEntity> semesters = semesterService.getAllSemester();
        semesters = Ultilities.SortSemesters(semesters);
        view.addObject("semesters", semesters);

        return view;
    }

    @RequestMapping("/studentArrangement/loadTable")
    @ResponseBody
    public JsonObject LoadStudentArrangementTable(@RequestParam Map<String, String> params, HttpServletRequest request) {
        JsonObject jsonObj = new JsonObject();

        int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
        int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
        String sSearch = params.get("sSearch").trim();
        String shiftType = params.get("shiftType").trim();

        try {
            List<List<String>> studentList = (List<List<String>>) request.getSession().getAttribute("STUDENT_ARRANGEMENT_LIST");
            if (studentList == null) {
                studentList = new ArrayList<>();
            }
            List<List<String>> searchList = studentList.stream().filter(s ->
                    Ultilities.containsIgnoreCase(s.get(0), sSearch)
                            || Ultilities.containsIgnoreCase(s.get(1), sSearch)
                            || Ultilities.containsIgnoreCase(s.get(2), sSearch)
                            || Ultilities.containsIgnoreCase(s.get(3), sSearch)).collect(Collectors.toList());
            if (!shiftType.equalsIgnoreCase("All")) {
                searchList = searchList.stream().filter(s ->
                        Ultilities.containsIgnoreCase(s.get(5), shiftType)).collect(Collectors.toList());
            }

            List<List<String>> result = searchList.stream().skip(iDisplayStart).limit(iDisplayLength).collect(Collectors.toList());
            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

            jsonObj.addProperty("iTotalRecords", studentList.size());
            jsonObj.addProperty("iTotalDisplayRecords", searchList.size());
            jsonObj.add("aaData", aaData);
            jsonObj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
    }

    @RequestMapping("/studentArrangementBySlot/loadTable")
    @ResponseBody
    public JsonObject LoadStudentArrangementBySlotTable(@RequestParam Map<String, String> params, HttpServletRequest request) {
        JsonObject jsonObj = new JsonObject();

        int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
        int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
        String sSearch = params.get("sSearch").trim();
        String shiftType = params.get("shiftType").trim();

        try {
            List<List<String>> studentList = (List<List<String>>) request.getSession().getAttribute("STUDENT_ARRANGEMENT_BY_SLOT_LIST");
            if (studentList == null) {
                studentList = new ArrayList<>();
            }
            List<List<String>> searchList = studentList.stream().filter(s ->
                    Ultilities.containsIgnoreCase(s.get(0), sSearch)
                            || Ultilities.containsIgnoreCase(s.get(1), sSearch)
                            || Ultilities.containsIgnoreCase(s.get(2), sSearch)
                            || Ultilities.containsIgnoreCase(s.get(3), sSearch)
                            || Ultilities.containsIgnoreCase(s.get(4), sSearch))
                    .collect(Collectors.toList());
            if (!shiftType.equalsIgnoreCase("All")) {
                searchList = searchList.stream().filter(s ->
                        Ultilities.containsIgnoreCase(s.get(5), shiftType)).collect(Collectors.toList());
            }

            List<List<String>> result = searchList.stream().skip(iDisplayStart).limit(iDisplayLength).collect(Collectors.toList());
            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

            jsonObj.addProperty("iTotalRecords", studentList.size());
            jsonObj.addProperty("iTotalDisplayRecords", searchList.size());
            jsonObj.add("aaData", aaData);
            jsonObj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
    }

    @RequestMapping(value = "/studentArrangement/import1", method = RequestMethod.POST)
    @ResponseBody
    public Callable<JsonObject> importFile1(
            @RequestParam("file-suggestion") MultipartFile fileSuggestion, HttpServletRequest request) {
        Callable<JsonObject> callable = new Callable<JsonObject>() {
            @Override
            public JsonObject call() throws Exception {
                JsonObject obj = ReadFile1(fileSuggestion, request);
                return obj;
            }
        };

        return callable;
    }

    @RequestMapping(value = "/studentArrangement/import2", method = RequestMethod.POST)
    @ResponseBody
    public Callable<JsonObject> importFile2(
            @RequestParam("file-suggestion") MultipartFile fileSuggestion,
            @RequestParam("file-going") MultipartFile fileGoing,
            @RequestParam("file-relearn") MultipartFile fileRelearn,
            @RequestParam("semesterId") int semesterId, HttpServletRequest request) {
        Callable<JsonObject> callable = new Callable<JsonObject>() {
            @Override
            public JsonObject call() throws Exception {
                JsonObject obj = ReadFile2(fileSuggestion, fileGoing, fileRelearn, semesterId, request);
                return obj;
            }
        };

        return callable;
    }

    private JsonObject ReadFile1(MultipartFile fileSuggestion, HttpServletRequest request) {
        JsonObject jsonObj = new JsonObject();
        IStudentService studentService = new StudentServiceImpl();
        ISubjectService subjectService = new SubjectServiceImpl();

        this.totalStudents = 0;
        this.countStudents = 0;
        this.file1Done = false;

        try {
            // Get all students and put into Map[Key: RollNumber, Value: StudentEntity]
            List<StudentEntity> students = studentService.findAllStudents();
            Map<String, StudentEntity> studentMap = new HashMap<>();
            for (StudentEntity s : students) {
                studentMap.put(s.getRollNumber(), s);
            }

            // Get all subjects and put into Map[Key: SubjectId, Value: SubjectEntity]
            List<SubjectEntity> subjects = subjectService.getAllSubjects();
            Map<String, SubjectEntity> allSubjectsMap = new HashMap<>();
            for (SubjectEntity subject : subjects) {
                allSubjectsMap.put(subject.getId(), subject);
            }

            // Read fileSuggestion
            Map<String, SubjectList> studentSubjectSuggestion = this.getSubjectSuggestionList(fileSuggestion, allSubjectsMap);
            totalStudents = studentSubjectSuggestion.keySet().size();

            Map<String, Map<String, List<StudentEntity>>> shiftMap = new HashMap<>();
            shiftMap.put("AM", new HashMap<>());
            shiftMap.put("PM", new HashMap<>());

            int count = 0;
            for (String rollNumber : studentSubjectSuggestion.keySet()) {
                StudentEntity curStudent = studentMap.get(rollNumber);
                SubjectList subjectList = studentSubjectSuggestion.get(rollNumber);

                // Create course classes base on suggestionList
                // If the number of subjects is more than 6
                // the remaining subjects will be created in course classes of other shift
                if (!subjectList.suggestionList.isEmpty()) {
                    count = 1;

                    Map<String, List<StudentEntity>> subjectMap = shiftMap.get(curStudent.getShift());
                    for (String subjectCode : subjectList.suggestionList) {
                        if (count > 6) {
                            subjectMap = shiftMap.get(curStudent.getShift().equals("AM") ? "PM" : "AM");
                        }

                        List<StudentEntity> studentList = subjectMap.get(subjectCode);
                        if (studentList == null) {
                            studentList = new ArrayList<>();
                            subjectMap.put(subjectCode, studentList);
                        }
                        studentList.add(curStudent);

                        ++count;
                    }
                }

                countStudents++;
            }

            // Create list for display
            int classNumber = 0;
            List<List<String>> result = new ArrayList<>();
            for (String shift : shiftMap.keySet()) {
                Map<String, List<StudentEntity>> subjectMap = shiftMap.get(shift);
                for (String subjectCode : subjectMap.keySet()) {
                    count = 0;
                    classNumber++;

                    List<StudentEntity> studentList = subjectMap.get(subjectCode);
                    Collections.sort(studentList, new RollNumberComparator());

                    SubjectEntity subject = allSubjectsMap.get(subjectCode);

                    List<String> dataRow = new ArrayList<>();
                    for (StudentEntity student : studentList) {
                        if (count == 25) {
                            classNumber++;
                            count = 0;
                        }

                        dataRow = new ArrayList<>();
                        dataRow.add(subject.getId());
                        dataRow.add(subject.getName());
                        dataRow.add(student.getRollNumber());
                        dataRow.add(student.getFullName());
                        dataRow.add(classNumber + "");
                        dataRow.add(shift);
                        result.add(dataRow);

                        ++count;
                    }
                }
            }

            request.getSession().setAttribute("STUDENT_ARRANGEMENT_LIST", result);
            jsonObj.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObj.addProperty("success", false);
            jsonObj.addProperty("message", e.getMessage());
        }


        return jsonObj;
    }

    private JsonObject ReadFile2(MultipartFile fileSuggestion, MultipartFile fileGoing,
                                MultipartFile fileRelearn, int semesterId, HttpServletRequest request) {
        JsonObject jsonObj = new JsonObject();
        IStudentStatusService studentStatusService = new StudentStatusServiceImpl();
        ISubjectService subjectService = new SubjectServiceImpl();

        this.totalStudents = 0;
        this.countStudents = 0;
        this.file1Done = false;
        this.file2Done = false;
        this.file3Done = false;

        try {
            // Get all subjects and put into Map[Key: SubjectId, Value: SubjectEntity]
            List<SubjectEntity> allSubjects = subjectService.getAllSubjects();
            Map<String, SubjectEntity> allSubjectsMap = new HashMap<>();
            for (SubjectEntity subject : allSubjects) {
                allSubjectsMap.put(subject.getId(), subject);
            }

            // Read files
            Map<String, SubjectList> studentSubjectSuggestion = this.getSubjectSuggestionList(fileSuggestion, allSubjectsMap);
            List<String> goingList = this.getGoingListAlreadyPaying(fileGoing, allSubjectsMap);
            Map<String, List<String>> relearnList = this.getRelearnListAlreadyPaying(fileRelearn, allSubjectsMap);

            List<String> statusList = new ArrayList<>();
            statusList.add("HD");
            statusList.add("HL");
            List<StudentStatusEntity> studentStatusList = studentStatusService
                    .getStudentStatusForStudentArrangement(semesterId, statusList);

            // Get students that have rollNumber in goingList and relearnList
            List<StudentStatusEntity> studentList = new ArrayList<>();
            for (StudentStatusEntity studentStatus : studentStatusList) {
                String curStudentRollNumber = studentStatus.getStudentId().getRollNumber();
                String curStudentStatus = studentStatus.getStatus();

                if (curStudentStatus.equals("HD")) {
                    for (String rollNumber : goingList) {
                        if (curStudentRollNumber.equals(rollNumber)) {
                            studentList.add(studentStatus);
                            break;
                        }
                    }
                } else if (curStudentStatus.equals("HL")) {
                    for (String rollNumber : relearnList.keySet()) {
                        if (curStudentRollNumber.equals(rollNumber)) {
                            studentList.add(studentStatus);
                            break;
                        }
                    }
                }
            }
            this.totalStudents = studentList.size();

            // Map[Key: Shift, Value: Map[Key: SubjectCode, Value: StudentList]]
            Map<String, Map<String, StudentList>> shiftMap = new HashMap<>();

            // Create course classes
            // Based on student's status (HD, HL), first create student's going list, then student's relearned list after that
            // and subjects in relearned list must be in suggestion list
            for (StudentStatusEntity studentStatus : studentList) {
                String curStudentRollNumber = studentStatus.getStudentId().getRollNumber();
                String curStudentStatus = studentStatus.getStatus();
                String curStudentShift = studentStatus.getStudentId().getShift();

                Map<String, StudentList> subjectMap = shiftMap.get(curStudentShift);
                if (subjectMap == null) {
                    subjectMap = new HashMap<>();
                    shiftMap.put(curStudentShift, subjectMap);
                }

                SubjectList subjectSuggestionList = studentSubjectSuggestion.get(curStudentRollNumber);
                List<String> subjects = null;
                if (curStudentStatus.equals("HD")) {
                    subjects = subjectSuggestionList.nextCourseList;
                } else if (curStudentStatus.equals("HL")) {
                    List<String> suggestionList = subjectSuggestionList.suggestionList;
                    List<String> relearnSubjects = relearnList.get(curStudentRollNumber);
                    subjects = this.removeRelearnedSubjectsNotInSuggestionList(relearnSubjects, suggestionList);
                }
                addStudentToSubjectMap(subjectMap, subjects, studentStatus.getStudentId(), curStudentStatus);

                // Check if student has HD status want to relearn
                if (curStudentStatus.equals("HD")) {
                    boolean isRelearn = false;
                    for (String rollNumber : relearnList.keySet()) {
                        if (curStudentRollNumber.equals(rollNumber)) {
                            isRelearn = true;
                            break;
                        }
                    }

                    if (isRelearn) {
                        String otherShift = curStudentShift.equals("AM") ? "PM" : "AM";
                        subjectMap = shiftMap.get(otherShift);
                        if (subjectMap == null) {
                            subjectMap = new HashMap<>();
                            shiftMap.put(otherShift, subjectMap);
                        }

                        List<String> suggestionList = subjectSuggestionList.suggestionList;
                        List<String> relearnSubjects = relearnList.get(curStudentRollNumber);
                        subjects = this.removeRelearnedSubjectsNotInSuggestionList(relearnSubjects, suggestionList);
                        addStudentToSubjectMap(subjectMap, subjects, studentStatus.getStudentId(), "HL");
                    }
                }

                ++this.countStudents;
            }

            // Create list for display
            List<List<String>> result = new ArrayList<>();
            int classNumber = 0;
            int count;
            for (String shift : shiftMap.keySet()) {
                Map<String, StudentList> subjectMap = shiftMap.get(shift);
                for (String subjectCode : subjectMap.keySet()) {
                    count = 0;
                    classNumber++;

                    SubjectEntity subject = subjectService.findSubjectById(subjectCode);

                    StudentList list = subjectMap.get(subjectCode);
                    Collections.sort(list.goingList, new RollNumberComparator());
                    Collections.sort(list.relearnList, new RollNumberComparator());

                    List<String> dataRow;
                    for (StudentEntity student : list.goingList) {
                        if (count == 25) {
                            classNumber++;
                            count = 0;
                        }

                        dataRow = new ArrayList<>();
                        dataRow.add(subject.getId());
                        dataRow.add(subject.getName());
                        dataRow.add(student.getRollNumber());
                        dataRow.add(student.getFullName());
                        dataRow.add(classNumber + "");
                        dataRow.add(shift);
                        result.add(dataRow);

                        ++count;
                    }

                    for (StudentEntity student : list.relearnList) {
                        if (count == 25) {
                            classNumber++;
                            count = 0;
                        }

                        dataRow = new ArrayList<>();
                        dataRow.add(subject.getId());
                        dataRow.add(subject.getName());
                        dataRow.add(student.getRollNumber());
                        dataRow.add(student.getFullName());
                        dataRow.add(classNumber + "");
                        dataRow.add(shift);
                        result.add(dataRow);

                        ++count;
                    }
                }
            }

            request.getSession().setAttribute("STUDENT_ARRANGEMENT_LIST", result);
            request.getSession().setAttribute("STUDENT_ARRANGEMENT_SEMESTER_ID", semesterId);

            jsonObj.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObj.addProperty("success", false);
            jsonObj.addProperty("message", e.getMessage());
        }

        return jsonObj;
    }

    private JsonObject ReadFile3(MultipartFile fileSuggestion, HttpServletRequest request) {
        List<List<String>> displayList = new ArrayList<>();
        ISubjectService subjectService = new SubjectServiceImpl();
        JsonObject jsonObj = new JsonObject();

        IStudentService studentService = new StudentServiceImpl();

        this.totalStudents = 0;
        this.countStudents = 0;
        this.file1Done = false;
        this.process1 = false;
        this.process2 = false;

        try {
            // Get all students and put into Map[Key: RollNumber, Value: StudentEntity]
            List<StudentEntity> students = studentService.findAllStudents();
            Map<String, StudentEntity> studentMap = new HashMap<>();
            for (StudentEntity student : students) {
                studentMap.put(student.getRollNumber(), student);
            }

            // Get all subjects and put into Map[Key: SubjectId, Value: SubjectEntity]
            List<SubjectEntity> subjects = subjectService.getAllSubjects();
            Map<String, SubjectEntity> subjectMap = new HashMap<>();
            for (SubjectEntity subject : subjects) {
                subjectMap.put(subject.getId(), subject);
            }

            // Read fileSuggestion
            Map<String, SubjectList> studentSubjectSuggestion = this.getSubjectSuggestionList(fileSuggestion, subjectMap);
            totalStudents = studentSubjectSuggestion.keySet().size();

            // Create course classes for LAB
            // All subjects will be created course classes into slots, except OJT, Capstone, Vovinam
            // LAB is special won't be created into slots, if student's shift is AM, LAB will be learned in PM
            // LAB class name: LAB_Shift_OrdinalNumber
            // Other subjects: SubjectCode_Shift_OrdinalNumber_Slot
            // [Slot]: S21, S22, S23, S31, S32, S33
            // S21: Monday, Wednesday, Friday - Slot 1...
            // S31: Tuesday, Thursday - Slot 1...
            // Subjects will be created in order as subject's OrdinalNumber in curriculum
            Map<String, Map<String, List<StudentEntity>>> shiftMapForLAB = new HashMap<>();
            shiftMapForLAB.put("AM", new HashMap<>());
            shiftMapForLAB.put("PM", new HashMap<>());

            List<StudentArrangementModel> studentList = new ArrayList<>();
            for (String rollNumber : studentSubjectSuggestion.keySet()) {
                SubjectList subjectList = studentSubjectSuggestion.get(rollNumber);

                if (!subjectList.nextCourseList.isEmpty()) {
                    StudentEntity student = studentMap.get(rollNumber);
                    StudentArrangementModel std = new StudentArrangementModel();
                    std.student = studentMap.get(rollNumber);
                    std.numOfSubjects = subjectList.nextCourseList.size();

                    List<SubjectCurriculumEntity> subjectCurriculumList = this.getSubjectCurriculumList(student);
                    for (String subjectCode : subjectList.nextCourseList) {
                        if (Ultilities.containsIgnoreCase(subjectCode, "LAB")) {
                            String otherShift = student.getShift().equals("AM") ? "PM" : "AM";
                            Map<String, List<StudentEntity>> subjectMapForLAB = shiftMapForLAB.get(otherShift);
                            List<StudentEntity> stdList = subjectMapForLAB.get(subjectCode);
                            if (stdList == null) {
                                stdList = new ArrayList<>();
                                subjectMapForLAB.put(subjectCode, stdList);
                            }
                            stdList.add(student);
                            --std.numOfSubjects;
                        } else {
                            int pos = -1;
                            for (int i = 0; i < subjectCurriculumList.size(); ++i) {
                                if (subjectCurriculumList.get(i).getSubjectId().getId().equals(subjectCode)) {
                                    pos = i;
                                    break;
                                }
                            }

                            if (pos >= 0 && pos <= 5) {
                                std.subjects[pos] = subjectCode;
                            }
                        }
                    }

                    studentList.add(std);
                }
            }
            this.process1 = true;

            // Create class for LAB
            int count;
            int classNumber;
            for (String shift : shiftMapForLAB.keySet()) {
                Map<String, List<StudentEntity>> subjectMapForLAB = shiftMapForLAB.get(shift);
                for (String subjectCode : subjectMapForLAB.keySet()) {
                    count = 0;
                    classNumber = 1;

                    SubjectEntity subject = subjectMap.get(subjectCode);

                    List<StudentEntity> list = subjectMapForLAB.get(subjectCode);
                    List<String> dataRow;
                    for (StudentEntity student : list) {
                        if (count == 25) {
                            classNumber++;
                            count = 0;
                        }

                        dataRow = new ArrayList<>();
                        dataRow.add(subject.getId());
                        dataRow.add(subject.getName());
                        dataRow.add(student.getRollNumber());
                        dataRow.add(student.getFullName());
                        dataRow.add(subjectCode + "_" + shift + "_" + classNumber);
                        dataRow.add(shift);
                        displayList.add(dataRow);

                        ++count;
                    }
                }
            }

            // Shift, Subject, Slot, Ordinal number
            Map<String, Map<String, Map<String, Integer>>> shiftOrdinalNumberMap = new HashMap<>();
            shiftOrdinalNumberMap.put("AM", new HashMap<>());
            shiftOrdinalNumberMap.put("PM", new HashMap<>());

            // Create course classes for other subjects
            for (int i = 0; i <= 5; i++) {
                // Group by StudentKey
                Map<StudentKey, List<StudentArrangementModel>> groupStudentsMap = new HashMap<>();
                for (StudentArrangementModel student : studentList) {
                    StudentKey key = new StudentKey();
                    key.numOfSubjects = student.numOfSubjects;
                    key.termNumber = student.student.getTerm();
                    key.shift = student.student.getShift();
                    key.subject = student.subjects[i];

                    List<StudentArrangementModel> list = groupStudentsMap.get(key);
                    if (list == null) {
                        list = new ArrayList<>();
                        groupStudentsMap.put(key, list);
                    }
                    list.add(student);
                }

                // Sort key
                List<StudentKey> keyList = new ArrayList<>(groupStudentsMap.keySet());
                keyList.sort(new Comparator<StudentKey>() {
                    @Override
                    public int compare(StudentKey k1, StudentKey k2) {
                        return Integer.compare(k1.termNumber, k2.termNumber);
                    }
                }.thenComparing(new Comparator<StudentKey>() {
                    @Override
                    public int compare(StudentKey k1, StudentKey k2) {
                        return Integer.compare(k2.numOfSubjects, k1.numOfSubjects);
                    }
                }));

                // Create class
                for (StudentKey key : keyList) {
                    List<StudentArrangementModel> allList = groupStudentsMap.get(key);
                    List<StudentArrangementModel> amList = new ArrayList<>();
                    List<StudentArrangementModel> pmList = new ArrayList<>();

                    for (StudentArrangementModel std : allList) {
                        if (std.student.getShift().equals("AM")) {
                            amList.add(std);
                        } else {
                            pmList.add(std);
                        }
                    }

                    String subjectCode = key.subject;
                    if (!subjectCode.isEmpty()) {
                        this.arrangeStudentIntoSlot(displayList, amList, shiftOrdinalNumberMap.get("AM"),
                                subjectMap, "AM", subjectCode);
                        this.arrangeStudentIntoSlot(displayList, pmList, shiftOrdinalNumberMap.get("PM"),
                                subjectMap, "PM", subjectCode);
                    }
                }

            }
            this.process2 = true;

            displayList.sort(new Comparator<List<String>>() {
                @Override
                public int compare(List<String> l1, List<String> l2) {
                    return l1.get(0).compareTo(l2.get(0));
                }
            }.thenComparing(new Comparator<List<String>>() {
                @Override
                public int compare(List<String> l1, List<String> l2) {
                    return l1.get(4).compareTo(l2.get(4));
                }
            }));

            request.getSession().setAttribute("STUDENT_ARRANGEMENT_BY_SLOT_LIST", displayList);
            jsonObj.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObj.addProperty("success", false);
            jsonObj.addProperty("message", e.getMessage());
        }

        return jsonObj;
    }

    private void arrangeStudentIntoSlot(List<List<String>> displayList, List<StudentArrangementModel> studentList,
                                        Map<String, Map<String, Integer>> subjectOrdinalNumberMap,
                                        Map<String, SubjectEntity> subjectMap,
                                        String shift, String subjectCode) {
        if (studentList.isEmpty()) {
            return;
        }

        Map<String, Integer> ordinalNumberMap = subjectOrdinalNumberMap.get(subjectCode);
        if (ordinalNumberMap == null) {
            ordinalNumberMap = new HashMap<>();
            subjectOrdinalNumberMap.put(subjectCode, ordinalNumberMap);
        }

        String[] slotName = new String[]{"S21", "S22", "S23", "S31", "S32", "S33"};

        Integer ordinalNumber;
        // Create 6 slots
        List<List<StudentArrangementModel>> studentsInSlot = new ArrayList<>();
        for (int i = 1; i <= 6; ++i) {
            studentsInSlot.add(new ArrayList<>());
        }

        for (StudentArrangementModel student : studentList) {
            for (int i = 0; i <= 5; i++) {
                if (student.slots[i] == null || student.slots[i] == false) {
                    studentsInSlot.get(i).add(student);
                }
            }
        }

        // 3 phần tử đầu của list là 0,1,2 đại diện cho slot 1,2,3 của T2,T4,T6
        // 2 phần tử cuối của list là 3,4,5 đại diện cho slot 1,2 của T3,T5
        // Lấy số sv lớn nhất (của ngày chẵn và ngày lẻ) và vị trí có số sv nhiều nhất trong list
        int maxStudentsOfEvenDays = 0;
        int maxPositionOfEvenDays = 0;
        for (int i = 0; i < 3; i++) {
            if (maxStudentsOfEvenDays < studentsInSlot.get(i).size()) {
                maxStudentsOfEvenDays = studentsInSlot.get(i).size();
                maxPositionOfEvenDays = i;
            }
        }

        int maxStudentsOfOddDays = 0;
        int maxPositionOfOddDays = 0;
        for (int i = 3; i < 6; i++) {
            if (maxStudentsOfOddDays < studentsInSlot.get(i).size()) {
                maxStudentsOfOddDays = studentsInSlot.get(i).size();
                maxPositionOfOddDays = i;
            }
        }

        // Nếu mà số sv lớn nhất của ngày chẵn lẻ bằng nhau -> so sánh slot, chọn vị trí có số slot nhỏ nhất
        // Còn không thì chọn vị trí mà số sv là lớn nhất
        int finalPosition = 0;
        if (maxStudentsOfEvenDays == maxStudentsOfOddDays) {
            if (maxPositionOfEvenDays <= maxPositionOfOddDays - 3) {
                finalPosition = maxPositionOfEvenDays;
            } else {
                finalPosition = maxPositionOfOddDays;
            }
        } else {
            if (maxStudentsOfEvenDays < maxStudentsOfOddDays) {
                finalPosition = maxPositionOfOddDays;
            } else {
                finalPosition = maxPositionOfEvenDays;
            }
        }

        ordinalNumber = ordinalNumberMap.get(slotName[finalPosition]);
        if (ordinalNumber == null) {
            ordinalNumber = 1;
            ordinalNumberMap.put(slotName[finalPosition], ordinalNumber);
        } else {
            ordinalNumber++;
            ordinalNumberMap.put(slotName[finalPosition], ordinalNumber);
        }

        // Chọn ra 25 sinh viên đầu danh sách để xếp lớp
        SubjectEntity subjectEntity = subjectMap.get(subjectCode);
        List<StudentArrangementModel> finalStudentList = studentsInSlot.get(finalPosition)
                .stream().skip(0).limit(25).collect(Collectors.toList());
        String currentClass = subjectCode + "_" + shift + "_" + ordinalNumber + "_" + slotName[finalPosition];
        for (StudentArrangementModel std : finalStudentList) {
            std.slots[finalPosition] = true;
            List<String> row = new ArrayList<>();
            row.add(subjectEntity.getId());
            row.add(subjectEntity.getName());
            row.add(std.student.getRollNumber());
            row.add(std.student.getFullName());
            row.add(currentClass);
            row.add(shift);

            displayList.add(row);
        }

        studentList.removeAll(finalStudentList);
        this.arrangeStudentIntoSlot(displayList, studentList, subjectOrdinalNumberMap, subjectMap, shift, subjectCode);
    }

    private List<SubjectCurriculumEntity> getSubjectCurriculumList(StudentEntity student) {
        List<SubjectCurriculumEntity> result = new ArrayList<>();
        int nextTerm = student.getTerm() + 1;

        if (student.getDocumentStudentEntityList() != null) {
            for (DocumentStudentEntity docStudent : student.getDocumentStudentEntityList()) {
                if (docStudent.getCurriculumId() != null
                        && docStudent.getCurriculumId().getSubjectCurriculumEntityList() != null) {
                    for (SubjectCurriculumEntity sc : docStudent.getCurriculumId().getSubjectCurriculumEntityList()) {
                        if (sc.getTermNumber() == nextTerm) {
                            result.add(sc);
                        }
                    }
                }
            }
        }

        Collections.sort(result, new Comparator<SubjectCurriculumEntity>() {
            @Override
            public int compare(SubjectCurriculumEntity o1, SubjectCurriculumEntity o2) {
                return o1.getOrdinalNumber().compareTo(o2.getOrdinalNumber());
            }
        });

        return result;
    }

    private void addStudentToSubjectMap(Map<String, StudentList> subjectMap,
                                        List<String> subjects, StudentEntity student, String status) {
        for (String subjectCode : subjects) {
            StudentList studentList = subjectMap.get(subjectCode);
            if (studentList == null) {
                studentList = new StudentList();
                subjectMap.put(subjectCode, studentList);
            }

            if (status.equals("HD")) {
                studentList.goingList.add(student);
            } else if (status.equals("HL")) {
                studentList.relearnList.add(student);
            }
        }
    }

    private Map<String, SubjectList> getSubjectSuggestionList(MultipartFile file, Map<String, SubjectEntity> subjectMap) {
        Map<String, SubjectList> result = new HashMap<>();

        try {
            InputStream is = file.getInputStream();

            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1, originalFileName.length());

            Workbook workbook = null;
            Sheet spreadsheet = null;
            Row row = null;
            if (extension.equals(xlsExcelExtension)) {
                workbook = new HSSFWorkbook(is);
                spreadsheet = workbook.getSheetAt(0);
            } else if (extension.equals(xlsxExcelExtension)) {
                workbook = new XSSFWorkbook(is);
                spreadsheet = workbook.getSheetAt(0);
            }

            int excelDataIndexRow = 6;
            int rollNumberIndex = 0;
            int subjectsInNextCourseIndex = 5;
            int subjectsSuggestionIndex = 8;

            for (int rowIndex = excelDataIndexRow; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
                row = spreadsheet.getRow(rowIndex);
                if (row != null) {
                    Cell rollNumberCell = row.getCell(rollNumberIndex);
                    Cell subjectsInNextCourseCell = row.getCell(subjectsInNextCourseIndex);
                    Cell subjectsSuggestionCell = row.getCell(subjectsSuggestionIndex);

                    String rollNumber = null;
                    if (rollNumberCell != null) {
                        rollNumber = rollNumberCell.getCellType() == Cell.CELL_TYPE_STRING ?
                                rollNumberCell.getStringCellValue() : (rollNumberCell.getNumericCellValue() == 0 ?
                                "" : Integer.toString((int) rollNumberCell.getNumericCellValue()));
                    }

                    String subjectsInNextCourseStr = null;
                    if (subjectsInNextCourseCell != null) {
                        subjectsInNextCourseStr = subjectsInNextCourseCell.getStringCellValue().trim();
                    }

                    String subjectsSuggestionStr = null;
                    if (subjectsSuggestionCell != null) {
                        subjectsSuggestionStr = subjectsSuggestionCell.getStringCellValue().trim();
                    }

                    if (rollNumber != null && subjectsInNextCourseStr != null && subjectsSuggestionStr != null) {
                        List<String> nextCourseList = this.changeSubjectStringToList(subjectsInNextCourseStr);
                        if (!nextCourseList.isEmpty()) {
                            nextCourseList = this.removeVOVAndOJTAndCapstoneInSubjectList(nextCourseList, subjectMap);
                        }

                        List<String> suggestionList = this.changeSubjectStringToList(subjectsSuggestionStr);
                        if (!suggestionList.isEmpty()) {
                            suggestionList = this.removeVOVAndOJTAndCapstoneInSubjectList(suggestionList, subjectMap);
                        }

                        SubjectList subjectList = new SubjectList();
                        subjectList.nextCourseList = nextCourseList;
                        subjectList.suggestionList = suggestionList;

                        result.put(rollNumber, subjectList);
                    }
                }
            }

            this.file1Done = true;
            workbook.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private List<String> getGoingListAlreadyPaying(MultipartFile file, Map<String, SubjectEntity> subjectMap) {
        List<String> result = new ArrayList<>();

        try {
            InputStream is = file.getInputStream();

            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1, originalFileName.length());

            Workbook workbook = null;
            Sheet spreadsheet = null;
            Row row = null;
            if (extension.equals(xlsExcelExtension)) {
                workbook = new HSSFWorkbook(is);
                spreadsheet = workbook.getSheetAt(0);
            } else if (extension.equals(xlsxExcelExtension)) {
                workbook = new XSSFWorkbook(is);
                spreadsheet = workbook.getSheetAt(0);
            }

            int excelDataIndexRow = 10;
            int rollNumberIndex = 4;

            for (int rowIndex = excelDataIndexRow; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
                row = spreadsheet.getRow(rowIndex);
                if (row != null) {
                    Cell rollNumberCell = row.getCell(rollNumberIndex);

                    if (rollNumberCell != null) {
                        String rollNumber = rollNumberCell.getCellType() == Cell.CELL_TYPE_STRING ?
                                rollNumberCell.getStringCellValue() : (rollNumberCell.getNumericCellValue() == 0 ?
                                "" : Integer.toString((int) rollNumberCell.getNumericCellValue()));
                        if (!rollNumber.isEmpty()) {
                            result.add(rollNumber);
                        }
                    }
                }
            }

            this.file2Done = true;
            workbook.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private Map<String, List<String>> getRelearnListAlreadyPaying(MultipartFile file, Map<String, SubjectEntity> subjectMap) {
        Map<String, List<String>> result = new HashMap<>();

        try {
            InputStream is = file.getInputStream();

            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1, originalFileName.length());

            Workbook workbook = null;
            Sheet spreadsheet = null;
            Row row = null;
            if (extension.equals(xlsExcelExtension)) {
                workbook = new HSSFWorkbook(is);
                spreadsheet = workbook.getSheetAt(0);
            } else if (extension.equals(xlsxExcelExtension)) {
                workbook = new XSSFWorkbook(is);
                spreadsheet = workbook.getSheetAt(0);
            }

            int excelDataIndexRow = 1;
            int rollNumberIndex = 1;
            int subjectsIndex = 3;

            for (int rowIndex = excelDataIndexRow; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
                row = spreadsheet.getRow(rowIndex);
                if (row != null) {
                    Cell rollNumberCell = row.getCell(rollNumberIndex);
                    Cell subjectsCell = row.getCell(subjectsIndex);

                    String rollNumber = null;
                    if (rollNumberCell != null) {
                        rollNumber = rollNumberCell.getCellType() == Cell.CELL_TYPE_STRING ?
                                rollNumberCell.getStringCellValue().trim() : (rollNumberCell.getNumericCellValue() == 0 ?
                                "" : Integer.toString((int) rollNumberCell.getNumericCellValue()));
                    }

                    String subjectsStr = null;
                    if (subjectsCell != null) {
                        subjectsStr = subjectsCell.getStringCellValue().trim();
                    }

                    if (rollNumber != null && subjectsStr != null) {
                        List<String> subjectList = this.changeSubjectStringToList(subjectsStr);
                        if (!subjectList.isEmpty()) {
                            subjectList = this.removeVOVAndOJTAndCapstoneInSubjectList(subjectList, subjectMap);
                        }

                        result.put(rollNumber, subjectList);
                    }
                }
            }

            this.file3Done = true;
            workbook.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private List<String> changeSubjectStringToList(String subjectStr) {
        if (subjectStr.isEmpty() || subjectStr.equals("N/A")) {
            return new ArrayList<>();
        }

        String[] subjectArr;
        if (subjectStr.contains(",")) {
            subjectArr = subjectStr.split(",");
        } else {
            subjectArr = new String[1];
            subjectArr[0] = subjectStr;
        }

        return new LinkedList<String>(Arrays.asList(subjectArr));
    }

    private List<String> removeVOVAndOJTAndCapstoneInSubjectList(List<String> subjectList, Map<String, SubjectEntity> subjectMap) {
        int pos;
        do {
            pos = -1;
            for (int i = 0; i < subjectList.size(); ++i) {
                String subjectCode = subjectList.get(i);
                SubjectEntity subjectEntity = subjectMap.get(subjectCode);
                if (Ultilities.containsIgnoreCase(subjectCode, "VOV")
                        || subjectEntity == null || subjectEntity.getType() == null
                        || subjectMap.get(subjectCode).getType() != 0) {
                    pos = i;
                    break;
                }
            }

            if (pos >= 0) subjectList.remove(pos);
        } while (pos >= 0 && !subjectList.isEmpty());

        return subjectList;
    }

    private List<String> removeRelearnedSubjectsNotInSuggestionList(List<String> relearnList, List<String> suggestionList) {
        List<String> result = new ArrayList<>();
        for (String relearnSubject : relearnList) {
            for (String suggestionSubject : suggestionList) {
                if (relearnSubject.equals(suggestionSubject)) {
                    result.add(relearnSubject);
                    break;
                }
            }
        }

        return result;
    }

    @RequestMapping(value = "/studentArrangementBySlot/import", method = RequestMethod.POST)
    @ResponseBody
    public Callable<JsonObject> importFileBySlot(
            @RequestParam("file-suggestion") MultipartFile fileSuggestion, HttpServletRequest request) {
        Callable<JsonObject> callable = new Callable<JsonObject>() {
            @Override
            public JsonObject call() throws Exception {
                JsonObject obj = ReadFile3(fileSuggestion, request);

                return obj;
            }
        };

        return callable;
    }

    @RequestMapping(value = "/studentArrangement/updateProgress")
    @ResponseBody
    public JsonObject UpdateProgress() {
        JsonObject jsonObj = new JsonObject();

        jsonObj.addProperty("total", this.totalStudents);
        jsonObj.addProperty("count", this.countStudents);
        jsonObj.addProperty("file1Done", this.file1Done);
        jsonObj.addProperty("file2Done", this.file2Done);
        jsonObj.addProperty("file3Done", this.file3Done);
        jsonObj.addProperty("process1", this.process1);
        jsonObj.addProperty("process2", this.process2);

        return jsonObj;
    }

    private class RollNumberComparator implements Comparator<StudentEntity> {
        @Override
        public int compare(StudentEntity o1, StudentEntity o2) {
            return o1.getRollNumber().compareTo(o2.getRollNumber());
        }
    }

    private class StudentList {
        public List<StudentEntity> goingList;
        public List<StudentEntity> relearnList;

        public StudentList() {
            this.goingList = new ArrayList<>();
            this.relearnList = new ArrayList<>();
        }
    }

    private class SubjectList {
        public List<String> nextCourseList;
        public List<String> suggestionList;
    }

    private class StudentArrangementModel {
        public StudentEntity student;
        public int numOfSubjects; // In suggestion list
        public String[] subjects; // Based on subject's OrdinalNumber in curriculum

        // [0, 1, 2]: Slot 1,2,3 of Monday, Wednesday, Friday
        // [3, 4, 5]: Slot 1,2,3 of Tuesday, Thursday
        public Boolean[] slots;

        public StudentArrangementModel() {
            this.subjects = new String[6];
            this.slots = new Boolean[6];

            for (int i = 0; i < 6; i++) {
                this.subjects[i] = "";
            }
        }
    }

    private class StudentKey {
        public int termNumber;
        public int numOfSubjects;
        public String shift;
        public String subject;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StudentKey that = (StudentKey) o;

            if (termNumber != that.termNumber) return false;
            if (numOfSubjects != that.numOfSubjects) return false;
            if (!shift.equals(that.shift)) return false;
            return subject.equals(that.subject);
        }

        @Override
        public int hashCode() {
            int result = termNumber;
            result = 31 * result + numOfSubjects;
            result = 31 * result + shift.hashCode();
            result = 31 * result + subject.hashCode();
            return result;
        }
    }

}
