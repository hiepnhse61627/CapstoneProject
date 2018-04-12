package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.exporters.IExportObject;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Controller
public class StudentArrangementController {
    @Autowired
    ServletContext context;

    private final String xlsExcelExtension = "xls";
    private final String xlsxExcelExtension = "xlsx";
    private final String arrangementSaveDirectory = "./Arrangement/";

    private int totalStudents;
    private int countStudents;
    private boolean file1Done;
    private boolean file2Done;
    private boolean file3Done;
    private boolean process1;
    private boolean process2;

    @RequestMapping("/studentArrangement")
    public ModelAndView StudentArrangementIndex(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView view = new ModelAndView("StudentArrangement");
        view.addObject("title", "Dự kiến xếp lớp");

        IRealSemesterService semesterService = new RealSemesterServiceImpl();
        List<RealSemesterEntity> semesters = semesterService.getAllSemester();
        semesters = Ultilities.SortSemesters(semesters);
        view.addObject("semesters", semesters);

        return view;
    }

    @RequestMapping("/studentArrangementBySlot")
    public ModelAndView StudentArrangementBySlotIndex(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());
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

//    private JsonObject ReadFile3(MultipartFile fileSuggestion, HttpServletRequest request,
//                                 boolean isRelearn, boolean isAddition) {
//        List<List<String>> displayList = new ArrayList<>();
//        ISubjectService subjectService = new SubjectServiceImpl();
//        JsonObject jsonObj = new JsonObject();
//
//        IStudentService studentService = new StudentServiceImpl();
//
//        this.totalStudents = 0;
//        this.countStudents = 0;
//        this.file1Done = false;
//        this.process1 = false;
//        this.process2 = false;
//
//        try {
//            // Get all students and put into Map[Key: RollNumber, Value: StudentEntity]
//            List<StudentEntity> students = studentService.findAllStudents();
//            Map<String, StudentEntity> studentMap = new HashMap<>();
//            for (StudentEntity student : students) {
//                studentMap.put(student.getRollNumber(), student);
//            }
//
//            // Get all subjects and put into Map[Key: SubjectId, Value: SubjectEntity]
//            List<SubjectEntity> subjects = subjectService.getAllSubjects();
//            Map<String, SubjectEntity> subjectMap = new HashMap<>();
//            for (SubjectEntity subject : subjects) {
//                subjectMap.put(subject.getId(), subject);
//            }
//
//            // Read fileSuggestion
////            Map<String, SubjectList> studentSubjectSuggestion = this.getSubjectSuggestionList(fileSuggestion, subjectMap);
//            Map<String, SubjectList> studentSubjectSuggestion = this.getSubjectSuggestionListForSummer2018(fileSuggestion, subjectMap);
//            totalStudents = studentSubjectSuggestion.keySet().size();
//
//            // Create course classes for LAB
//            // All subjects will be created course classes into slots, except OJT, Capstone, Vovinam
//            // LAB is special won't be created into slots, if student's shift is AM, LAB will be learned in PM
//            // LAB class name: LAB_Shift_OrdinalNumber
//            // Other subjects: SubjectCode_Shift_OrdinalNumber_Slot
//            // [Slot]: S21, S22, S23, S31, S32, S33
//            // S21: Monday, Wednesday, Friday - Slot 1...
//            // S31: Tuesday, Thursday - Slot 1...
//            // Subjects will be created in order as subject's OrdinalNumber in curriculum
//            Map<String, Map<String, List<StudentEntity>>> shiftMapForLAB = new HashMap<>();
//            shiftMapForLAB.put("AM", new HashMap<>());
//            shiftMapForLAB.put("PM", new HashMap<>());
//
//            List<StudentArrangementModel> studentList = new ArrayList<>();
//            for (String rollNumber : studentSubjectSuggestion.keySet()) {
//                SubjectList subjectList = studentSubjectSuggestion.get(rollNumber);
//
//                if (subjectList.nextCourseList != null) {
//                    StudentEntity student = studentMap.get(rollNumber);
//
//                    if(student != null) {
//                        StudentArrangementModel std = new StudentArrangementModel();
//                        std.student = student;
//                        std.numOfSubjects = subjectList.nextCourseList.size();
//
//                        List<SubjectCurriculumEntity> subjectCurriculumList = this.getSubjectCurriculumList(student);
//
//                        //To pre arrange SUMMER 2018
//                        subjectList.nextCourseList = subjectCurriculumList.stream().map(q -> q.getSubjectId().getId())
//                                .collect(Collectors.toList());
//                        for (String subjectCode : subjectList.nextCourseList) {
//                            if (Ultilities.containsIgnoreCase(subjectCode, "LAB")) {
//                                String otherShift = student.getShift().equals("AM") ? "PM" : "AM";
//                                Map<String, List<StudentEntity>> subjectMapForLAB = shiftMapForLAB.get(otherShift);
//                                List<StudentEntity> stdList = subjectMapForLAB.get(subjectCode);
//                                if (stdList == null) {
//                                    stdList = new ArrayList<>();
//                                    subjectMapForLAB.put(subjectCode, stdList);
//                                }
//                                stdList.add(student);
//                                --std.numOfSubjects;
//                            } else {
//                                int pos = -1;
//                                for (int i = 0; i < subjectCurriculumList.size(); ++i) {
//                                    if (subjectCurriculumList.get(i).getSubjectId().getId().equals(subjectCode)) {
//                                        pos = i;
//                                        break;
//                                    }
//                                }
//
//                                if (pos >= 0 && pos <= 5) {
//                                    std.subjects[pos] = subjectCode;
//                                }
//                            }
//                        }
//
//                        studentList.add(std);
//                    }
//                }
//            }
//            this.process1 = true;
//
//            // Create class for LAB
//            int count;
//            int classNumber;
//            int classCount = 0;
//            for (String shift : shiftMapForLAB.keySet()) {
//                Map<String, List<StudentEntity>> subjectMapForLAB = shiftMapForLAB.get(shift);
//                for (String subjectCode : subjectMapForLAB.keySet()) {
//                    count = 0;
//                    classNumber = 1;
//                    classCount++;
//
//                    SubjectEntity subject = subjectMap.get(subjectCode);
//
//                    List<StudentEntity> list = subjectMapForLAB.get(subjectCode);
//                    List<String> dataRow;
//                    for (StudentEntity student : list) {
//                        StudentArrangementModel currentStudentModel = studentList.stream()
//                                .filter(q -> q.student.getRollNumber().equals(student.getRollNumber()))
//                                .collect(Collectors.toList()).get(0);
//
//                        if (count == 25) {
//                            classCount++;
//                            classNumber++;
//                            count = 0;
//                        }
//
//                        dataRow = new ArrayList<>();
//                        dataRow.add(subject.getId());
//                        dataRow.add(subject.getName());
//                        dataRow.add(student.getRollNumber());
//                        dataRow.add(student.getFullName());
//                        dataRow.add(subjectCode + "_" + shift + "_" + classNumber + "_T" + ((classCount % 6) + 2));
//                        dataRow.add(shift);
//                        displayList.add(dataRow);
//
//                        currentStudentModel.setLabDay("T" + ((classCount % 6) + 2));
//
//                        ++count;
//                    }
//                }
//            }
//
//            // Shift, Subject, Slot, Ordinal number
//            Map<String, Map<String, Map<String, Integer>>> shiftOrdinalNumberMap = new HashMap<>();
//            shiftOrdinalNumberMap.put("AM", new HashMap<>());
//            shiftOrdinalNumberMap.put("PM", new HashMap<>());
//
//            // Group Student by subject, slot and shift
//            Map<ClassKey, List<StudentArrangementModel>> groupClassList = new HashMap<>();
//
//            // Create course classes for other subjects
//            for (int i = 0; i <= 5; i++) {
//                // Group by StudentKey
//                Map<StudentKey, List<StudentArrangementModel>> groupStudentsMap = new HashMap<>();
//                for (StudentArrangementModel student : studentList) {
//                    if(student.student.getTerm() != null && student.student.getShift() != null) {
//                        StudentKey key = new StudentKey();
//                        key.numOfSubjects = student.numOfSubjects;
//                        key.termNumber = student.student.getTerm();
//                        key.shift = student.student.getShift();
//                        key.subject = student.subjects[i];
//
//                        List<StudentArrangementModel> list = groupStudentsMap.get(key);
//                        if (list == null) {
//                            list = new ArrayList<>();
//                            groupStudentsMap.put(key, list);
//                        }
//                        list.add(student);
//                    } else {
//                        System.out.println(student.student.getRollNumber() + " is null");
//                    }
//                }
//
//                // Sort key
//                List<StudentKey> keyList = new ArrayList<>(groupStudentsMap.keySet());
//                keyList.sort(new Comparator<StudentKey>() {
//                    @Override
//                    public int compare(StudentKey k1, StudentKey k2) {
//                        return Integer.compare(k1.termNumber, k2.termNumber);
//                    }
//                }.thenComparing(new Comparator<StudentKey>() {
//                    @Override
//                    public int compare(StudentKey k1, StudentKey k2) {
//                        return Integer.compare(k2.numOfSubjects, k1.numOfSubjects);
//                    }
//                }));
//
//                // Create class
//                for (StudentKey key : keyList) {
//                    List<StudentArrangementModel> allList = groupStudentsMap.get(key);
//                    List<StudentArrangementModel> amList = new ArrayList<>();
//                    List<StudentArrangementModel> pmList = new ArrayList<>();
//
//                    for (StudentArrangementModel std : allList) {
//                        if (std.student.getShift().equals("AM")) {
//                            amList.add(std);
//                        } else {
//                            pmList.add(std);
//                        }
//                    }
//
//                    String subjectCode = key.subject;
//                    if (!subjectCode.isEmpty()) {
//                        this.arrangeStudentIntoSlot(groupClassList, displayList, amList, shiftOrdinalNumberMap.get("AM"),
//                                subjectMap, "AM", subjectCode);
//                        this.arrangeStudentIntoSlot(groupClassList, displayList, pmList, shiftOrdinalNumberMap.get("PM"),
//                                subjectMap, "PM", subjectCode);
//                    }
//                }
//
//            }
//
////            List<String> evenSlotName = Arrays.asList((new String[] {"S21", "S22", "S23"}));
////            List<String> oddSlotName = Arrays.asList((new String[] {"S31", "S32", "S33"}));
//
//            // Check if a group of students can't make up a class and move them to their opposite shift's classes
////            for(ClassKey classKey : groupClassList.keySet()) {
////                List<StudentArrangementModel> currentShiftList = groupClassList.get(classKey);
////
////                if(currentShiftList.size() < 15) {
////                    String oppositeShift = classKey.shift.equalsIgnoreCase("AM") ? "PM" : "AM";
////                    List<ClassKey> oppositeShiftClassKeyList = groupClassList.keySet().stream().filter(
////                            q -> q.subjectCode.equals(classKey.subjectCode)
////                            && q.shift.equalsIgnoreCase(oppositeShift)).collect(Collectors.toList());
////
////                    int lowestEven = Integer.MIN_VALUE;
////                    int lowestOdd = Integer.MIN_VALUE;
////                    int posEven = -1;
////                    int posOdd = -1;
////
////                    for (ClassKey key : oppositeShiftClassKeyList) {
////                        if (evenSlotName.contains(key.slotName)) {
////                            if(groupClassList.get(key).size() < lowestEven) {
////                                posEven = oppositeShiftClassKeyList.indexOf(key);
////                                lowestEven = groupClassList.get(key).size();
////                            }
////                        } else {
////                            if(groupClassList.get(key).size() < lowestOdd) {
////                                posOdd = oppositeShiftClassKeyList.indexOf(key);
////                                lowestOdd = groupClassList.get(key).size();
////                            }
////                        }
////                    }
////
////                    ClassKey optimizedEvenSlotClassKey, optimizedOddSlotClassKey;
////                    if(posEven != -1) {
////                        optimizedEvenSlotClassKey = oppositeShiftClassKeyList.get(posEven);
////                    }
////
////                    if(posOdd != -1) {
////                        optimizedOddSlotClassKey = oppositeShiftClassKeyList.get(posOdd);
////                    }
////
////                    for(StudentArrangementModel model : currentShiftList) {
////                        if(Arrays.asList(new String[] {"T2", "T4", "T6"}).contains(model.labDay) && posOdd != -1) {
////
////                        } else if (Arrays.asList(new String[] {"T3", "T5"}).contains(model.labDay) && posEven != 1) {
////
////                        } else if(posEven != 1 || posOdd != -1) {
////
////                        }
////                    }
////                }
////            }
//
//            for(ClassKey classKey : groupClassList.keySet()) {
//                List<StudentArrangementModel> tempStudentList = groupClassList.get(classKey);
//                int ordinalNumber = 1;
//                SubjectEntity subjectEntity = subjectMap.get(classKey.subjectCode);
//
//                while(tempStudentList.size() >= 55) {
//                    List<StudentArrangementModel> finalStudentList = tempStudentList.stream().skip(0).limit(25).collect(Collectors.toList());
//                    String currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
//                            + classKey.slotName;
//                    for (StudentArrangementModel std : finalStudentList) {
//                        List<String> row = new ArrayList<>();
//                        row.add(subjectEntity.getId());
//                        row.add(subjectEntity.getName());
//                        row.add(std.student.getRollNumber());
//                        row.add(std.student.getFullName());
//                        row.add(currentClass);
//                        row.add(classKey.shift);
//
//                        displayList.add(row);
//                    }
//
//                    tempStudentList.removeAll(finalStudentList);
//                    ordinalNumber++;
//                }
//
//                if (tempStudentList.size() <= 54 && tempStudentList.size() >= 51) {
//                    List<StudentArrangementModel> finalStudentList = tempStudentList.stream().skip(0).limit(tempStudentList.size() - 30).collect(Collectors.toList());
//                    String currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
//                            + classKey.slotName;
//                    for (StudentArrangementModel std : finalStudentList) {
//                        List<String> row = new ArrayList<>();
//                        row.add(subjectEntity.getId());
//                        row.add(subjectEntity.getName());
//                        row.add(std.student.getRollNumber());
//                        row.add(std.student.getFullName());
//                        row.add(currentClass);
//                        row.add(classKey.shift);
//
//                        displayList.add(row);
//                    }
//
//                    tempStudentList.removeAll(finalStudentList);
//                    ordinalNumber++;
//                }
//
//                if(tempStudentList.size() <= 50 & tempStudentList.size() >= 40) {
//                    List<StudentArrangementModel> finalStudentList = tempStudentList.stream().skip(0).limit(25).collect(Collectors.toList());
//                    String currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
//                            + classKey.slotName;
//                    for (StudentArrangementModel std : finalStudentList) {
//                        List<String> row = new ArrayList<>();
//                        row.add(subjectEntity.getId());
//                        row.add(subjectEntity.getName());
//                        row.add(std.student.getRollNumber());
//                        row.add(std.student.getFullName());
//                        row.add(currentClass);
//                        row.add(classKey.shift);
//
//                        displayList.add(row);
//                    }
//
//                    tempStudentList.removeAll(finalStudentList);
//                    ordinalNumber++;
//
//                    finalStudentList = tempStudentList.stream().skip(0).collect(Collectors.toList());
//                    currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
//                            + classKey.slotName;
//                    for (StudentArrangementModel std : finalStudentList) {
//                        List<String> row = new ArrayList<>();
//                        row.add(subjectEntity.getId());
//                        row.add(subjectEntity.getName());
//                        row.add(std.student.getRollNumber());
//                        row.add(std.student.getFullName());
//                        row.add(currentClass);
//                        row.add(classKey.shift);
//
//                        displayList.add(row);
//                    }
//
//                    tempStudentList.removeAll(finalStudentList);
//                    ordinalNumber++;
//                }
//
//                if(tempStudentList.size() <= 39 & tempStudentList.size() >= 30) {
//                    List<StudentArrangementModel> finalStudentList = tempStudentList.stream().skip(0).limit(tempStudentList.size() - 15).collect(Collectors.toList());
//                    String currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
//                            + classKey.slotName;
//                    for (StudentArrangementModel std : finalStudentList) {
//                        List<String> row = new ArrayList<>();
//                        row.add(subjectEntity.getId());
//                        row.add(subjectEntity.getName());
//                        row.add(std.student.getRollNumber());
//                        row.add(std.student.getFullName());
//                        row.add(currentClass);
//                        row.add(classKey.shift);
//
//                        displayList.add(row);
//                    }
//
//                    tempStudentList.removeAll(finalStudentList);
//                    ordinalNumber++;
//
//                    finalStudentList = tempStudentList.stream().skip(0).limit(15).collect(Collectors.toList());
//                    currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
//                            + classKey.slotName;
//                    for (StudentArrangementModel std : finalStudentList) {
//                        List<String> row = new ArrayList<>();
//                        row.add(subjectEntity.getId());
//                        row.add(subjectEntity.getName());
//                        row.add(std.student.getRollNumber());
//                        row.add(std.student.getFullName());
//                        row.add(currentClass);
//                        row.add(classKey.shift);
//
//                        displayList.add(row);
//                    }
//
//                    tempStudentList.removeAll(finalStudentList);
//                    ordinalNumber++;
//                }
//
//                if(!tempStudentList.isEmpty()) {
//                    List<StudentArrangementModel> finalStudentList = tempStudentList.stream().skip(0).collect(Collectors.toList());
//                    String currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
//                            + classKey.slotName;
//                    for (StudentArrangementModel std : finalStudentList) {
//                        List<String> row = new ArrayList<>();
//                        row.add(subjectEntity.getId());
//                        row.add(subjectEntity.getName());
//                        row.add(std.student.getRollNumber());
//                        row.add(std.student.getFullName());
//                        row.add(currentClass);
//                        row.add(classKey.shift);
//
//                        displayList.add(row);
//                    }
//
//                    tempStudentList.removeAll(finalStudentList);
//                    ordinalNumber++;
//                }
//            }
//
//            this.process2 = true;
//
//            displayList.sort(new Comparator<List<String>>() {
//                @Override
//                public int compare(List<String> l1, List<String> l2) {
//                    return l1.get(0).compareTo(l2.get(0));
//                }
//            }.thenComparing(new Comparator<List<String>>() {
//                @Override
//                public int compare(List<String> l1, List<String> l2) {
//                    return l1.get(4).compareTo(l2.get(4));
//                }
//            }));
//
//            request.getSession().setAttribute("STUDENT_ARRANGEMENT_BY_SLOT_LIST", displayList);
//            jsonObj.addProperty("success", true);
//
//            if(!isRelearn && !isAddition) {
//                IExportObject exportObject =(IExportObject)
//                        Class.forName("com.capstone.exporters.ExportStudentArrangementBySlotImpl").newInstance();
//
//                // get output stream of the response
//                OutputStream os;
//                try {
//                    // set headers for the response
//
//                    String fileName = exportObject.getFileName();
//
//                    File directory = new File(arrangementSaveDirectory);
//                    if(!directory.exists()) {
//                        directory.mkdirs();
//                    }
//
//                    File file = new File(arrangementSaveDirectory + fileName);
//
//                    if(!file.exists()) {
//                        file.createNewFile();
//                    }
//
//                    // write data
//                    os = new FileOutputStream(file);
//                    exportObject.writeData(os, null, request);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } if (isAddition) {
//
//            } if (isRelearn) {
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            jsonObj.addProperty("success", false);
//            jsonObj.addProperty("message", e.getMessage());
//        }
//
//        return jsonObj;
//    }

    //Customized for summer 2018
    private JsonObject ReadFile3(MultipartFile fileSuggestion, HttpServletRequest request,
                                 boolean isRelearn, boolean isAddition) {
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
//            Map<String, SubjectList> studentSubjectSuggestion = this.getSubjectSuggestionList(fileSuggestion, subjectMap);
            Map<String, SubjectList> studentSubjectSuggestion = this.getSubjectSuggestionListForSummer2018(fileSuggestion, subjectMap);
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

                if (subjectList.nextCourseList != null && subjectList.currentTerm != -5) {
                    StudentEntity student = studentMap.get(rollNumber);

                    if(student != null) {
                        StudentArrangementModel std = new StudentArrangementModel();
                        std.student = student;
                        std.numOfSubjects = subjectList.nextCourseList.size();

                        List<SubjectCurriculumEntity> subjectCurriculumList =
                                this.getSubjectCurriculumListForSummer2018(student, subjectList.currentTerm);

                        //To pre arrange SUMMER 2018
                        subjectList.nextCourseList = subjectCurriculumList.stream()
                                .filter(q -> q.getTermNumber() == subjectList.currentTerm)
                                .map(q -> q.getSubjectId().getId()).collect(Collectors.toList());
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
            }
            this.process1 = true;

            // Create class for LAB
            int count;
            int classNumber;
            int classCount = 0;
            for (String shift : shiftMapForLAB.keySet()) {
                Map<String, List<StudentEntity>> subjectMapForLAB = shiftMapForLAB.get(shift);
                for (String subjectCode : subjectMapForLAB.keySet()) {
                    count = 0;
                    classNumber = 1;
                    classCount++;

                    SubjectEntity subject = subjectMap.get(subjectCode);

                    List<StudentEntity> list = subjectMapForLAB.get(subjectCode);
                    List<String> dataRow;
                    for (StudentEntity student : list) {
                        StudentArrangementModel currentStudentModel = studentList.stream()
                                .filter(q -> q.student.getRollNumber().equals(student.getRollNumber()))
                                .collect(Collectors.toList()).get(0);

                        if (count == 25) {
                            classCount++;
                            classNumber++;
                            count = 0;
                        }

                        dataRow = new ArrayList<>();
                        dataRow.add(subject.getId());
                        dataRow.add(subject.getName());
                        dataRow.add(student.getRollNumber());
                        dataRow.add(student.getFullName());
                        dataRow.add(subjectCode + "_" + shift + "_" + classNumber + "_T" + ((classCount % 6) + 2));
                        dataRow.add(shift);
                        displayList.add(dataRow);

                        currentStudentModel.setLabDay("T" + ((classCount % 6) + 2));

                        ++count;
                    }
                }
            }

            // Shift, Subject, Slot, Ordinal number
            Map<String, Map<String, Map<String, Integer>>> shiftOrdinalNumberMap = new HashMap<>();
            shiftOrdinalNumberMap.put("AM", new HashMap<>());
            shiftOrdinalNumberMap.put("PM", new HashMap<>());

            // Group Student by subject, slot and shift
            Map<ClassKey, List<StudentArrangementModel>> groupClassList = new HashMap<>();

            // Create course classes for other subjects
            for (int i = 0; i <= 5; i++) {
                // Group by StudentKey
                Map<StudentKey, List<StudentArrangementModel>> groupStudentsMap = new HashMap<>();
                for (StudentArrangementModel student : studentList) {
                    if(student.student.getTerm() != null && student.student.getShift() != null) {
                        StudentKey key = new StudentKey();
                        //same number of subject problem
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
                    } else {
                        System.out.println(student.student.getRollNumber() + " is null");
                    }
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
                        this.arrangeStudentIntoSlot(groupClassList, displayList, amList, shiftOrdinalNumberMap.get("AM"),
                                subjectMap, "AM", subjectCode);
                        this.arrangeStudentIntoSlot(groupClassList, displayList, pmList, shiftOrdinalNumberMap.get("PM"),
                                subjectMap, "PM", subjectCode);
                    }
                }

            }

//            List<String> evenSlotName = Arrays.asList((new String[] {"S21", "S22", "S23"}));
//            List<String> oddSlotName = Arrays.asList((new String[] {"S31", "S32", "S33"}));

            // Check if a group of students can't make up a class and move them to their opposite shift's classes
//            for(ClassKey classKey : groupClassList.keySet()) {
//                List<StudentArrangementModel> currentShiftList = groupClassList.get(classKey);
//
//                if(currentShiftList.size() < 15) {
//                    String oppositeShift = classKey.shift.equalsIgnoreCase("AM") ? "PM" : "AM";
//                    List<ClassKey> oppositeShiftClassKeyList = groupClassList.keySet().stream().filter(
//                            q -> q.subjectCode.equals(classKey.subjectCode)
//                            && q.shift.equalsIgnoreCase(oppositeShift)).collect(Collectors.toList());
//
//                    int lowestEven = Integer.MIN_VALUE;
//                    int lowestOdd = Integer.MIN_VALUE;
//                    int posEven = -1;
//                    int posOdd = -1;
//
//                    for (ClassKey key : oppositeShiftClassKeyList) {
//                        if (evenSlotName.contains(key.slotName)) {
//                            if(groupClassList.get(key).size() < lowestEven) {
//                                posEven = oppositeShiftClassKeyList.indexOf(key);
//                                lowestEven = groupClassList.get(key).size();
//                            }
//                        } else {
//                            if(groupClassList.get(key).size() < lowestOdd) {
//                                posOdd = oppositeShiftClassKeyList.indexOf(key);
//                                lowestOdd = groupClassList.get(key).size();
//                            }
//                        }
//                    }
//
//                    ClassKey optimizedEvenSlotClassKey, optimizedOddSlotClassKey;
//                    if(posEven != -1) {
//                        optimizedEvenSlotClassKey = oppositeShiftClassKeyList.get(posEven);
//                    }
//
//                    if(posOdd != -1) {
//                        optimizedOddSlotClassKey = oppositeShiftClassKeyList.get(posOdd);
//                    }
//
//                    for(StudentArrangementModel model : currentShiftList) {
//                        if(Arrays.asList(new String[] {"T2", "T4", "T6"}).contains(model.labDay) && posOdd != -1) {
//
//                        } else if (Arrays.asList(new String[] {"T3", "T5"}).contains(model.labDay) && posEven != 1) {
//
//                        } else if(posEven != 1 || posOdd != -1) {
//
//                        }
//                    }
//                }
//            }

            for(ClassKey classKey : groupClassList.keySet()) {
                List<StudentArrangementModel> tempStudentList = groupClassList.get(classKey);
                int ordinalNumber = 1;
                SubjectEntity subjectEntity = subjectMap.get(classKey.subjectCode);

                while(tempStudentList.size() >= 55) {
                    List<StudentArrangementModel> finalStudentList = tempStudentList.stream().skip(0).limit(25).collect(Collectors.toList());
                    String currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
                            + classKey.slotName;
                    for (StudentArrangementModel std : finalStudentList) {
                        List<String> row = new ArrayList<>();
                        row.add(subjectEntity.getId());
                        row.add(subjectEntity.getName());
                        row.add(std.student.getRollNumber());
                        row.add(std.student.getFullName());
                        row.add(currentClass);
                        row.add(classKey.shift);

                        displayList.add(row);
                    }

                    tempStudentList.removeAll(finalStudentList);
                    ordinalNumber++;
                }

                if (tempStudentList.size() <= 54 && tempStudentList.size() >= 51) {
                    List<StudentArrangementModel> finalStudentList = tempStudentList.stream().skip(0).limit(tempStudentList.size() - 30).collect(Collectors.toList());
                    String currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
                            + classKey.slotName;
                    for (StudentArrangementModel std : finalStudentList) {
                        List<String> row = new ArrayList<>();
                        row.add(subjectEntity.getId());
                        row.add(subjectEntity.getName());
                        row.add(std.student.getRollNumber());
                        row.add(std.student.getFullName());
                        row.add(currentClass);
                        row.add(classKey.shift);

                        displayList.add(row);
                    }

                    tempStudentList.removeAll(finalStudentList);
                    ordinalNumber++;
                }

                if(tempStudentList.size() <= 50 & tempStudentList.size() >= 40) {
                    List<StudentArrangementModel> finalStudentList = tempStudentList.stream().skip(0).limit(25).collect(Collectors.toList());
                    String currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
                            + classKey.slotName;
                    for (StudentArrangementModel std : finalStudentList) {
                        List<String> row = new ArrayList<>();
                        row.add(subjectEntity.getId());
                        row.add(subjectEntity.getName());
                        row.add(std.student.getRollNumber());
                        row.add(std.student.getFullName());
                        row.add(currentClass);
                        row.add(classKey.shift);

                        displayList.add(row);
                    }

                    tempStudentList.removeAll(finalStudentList);
                    ordinalNumber++;

                    finalStudentList = tempStudentList.stream().skip(0).collect(Collectors.toList());
                    currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
                            + classKey.slotName;
                    for (StudentArrangementModel std : finalStudentList) {
                        List<String> row = new ArrayList<>();
                        row.add(subjectEntity.getId());
                        row.add(subjectEntity.getName());
                        row.add(std.student.getRollNumber());
                        row.add(std.student.getFullName());
                        row.add(currentClass);
                        row.add(classKey.shift);

                        displayList.add(row);
                    }

                    tempStudentList.removeAll(finalStudentList);
                    ordinalNumber++;
                }

                if(tempStudentList.size() <= 39 & tempStudentList.size() >= 30) {
                    List<StudentArrangementModel> finalStudentList = tempStudentList.stream().skip(0).limit(tempStudentList.size() - 15).collect(Collectors.toList());
                    String currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
                            + classKey.slotName;
                    for (StudentArrangementModel std : finalStudentList) {
                        List<String> row = new ArrayList<>();
                        row.add(subjectEntity.getId());
                        row.add(subjectEntity.getName());
                        row.add(std.student.getRollNumber());
                        row.add(std.student.getFullName());
                        row.add(currentClass);
                        row.add(classKey.shift);

                        displayList.add(row);
                    }

                    tempStudentList.removeAll(finalStudentList);
                    ordinalNumber++;

                    finalStudentList = tempStudentList.stream().skip(0).limit(15).collect(Collectors.toList());
                    currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
                            + classKey.slotName;
                    for (StudentArrangementModel std : finalStudentList) {
                        List<String> row = new ArrayList<>();
                        row.add(subjectEntity.getId());
                        row.add(subjectEntity.getName());
                        row.add(std.student.getRollNumber());
                        row.add(std.student.getFullName());
                        row.add(currentClass);
                        row.add(classKey.shift);

                        displayList.add(row);
                    }

                    tempStudentList.removeAll(finalStudentList);
                    ordinalNumber++;
                }

                if(!tempStudentList.isEmpty()) {
                    List<StudentArrangementModel> finalStudentList = tempStudentList.stream().skip(0).collect(Collectors.toList());
                    String currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
                            + classKey.slotName;
                    for (StudentArrangementModel std : finalStudentList) {
                        List<String> row = new ArrayList<>();
                        row.add(subjectEntity.getId());
                        row.add(subjectEntity.getName());
                        row.add(std.student.getRollNumber());
                        row.add(std.student.getFullName());
                        row.add(currentClass);
                        row.add(classKey.shift);

                        displayList.add(row);
                    }

                    tempStudentList.removeAll(finalStudentList);
                    ordinalNumber++;
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

            if(!isRelearn && !isAddition) {
                IExportObject exportObject =(IExportObject)
                        Class.forName("com.capstone.exporters.ExportStudentArrangementBySlotImpl").newInstance();

                // get output stream of the response
                OutputStream os;
                try {
                    // set headers for the response

                    String fileName = exportObject.getFileName();

                    File directory = new File(arrangementSaveDirectory);
                    if(!directory.exists()) {
                        directory.mkdirs();
                    }

                    File file = new File(arrangementSaveDirectory + fileName);

                    if(!file.exists()) {
                        file.createNewFile();
                    }

                    // write data
                    os = new FileOutputStream(file);
                    exportObject.writeData(os, null, request);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } if (isAddition) {

            } if (isRelearn) {

            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonObj.addProperty("success", false);
            jsonObj.addProperty("message", e.getMessage());
        }

        return jsonObj;
    }

    private void arrangeStudentIntoSlot(Map<ClassKey, List<StudentArrangementModel>> groupClassList,
                                        List<List<String>> displayList, List<StudentArrangementModel> studentList,
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

//            displayList.add(row);
        }

        ClassKey classKey = new ClassKey(slotName[finalPosition], shift, subjectCode);
        List<StudentArrangementModel> classList = groupClassList.get(classKey);

        if(classList == null) {
            classList = new ArrayList<>();
            groupClassList.put(classKey, classList);
        }

        classList.addAll(finalStudentList);
        studentList.removeAll(finalStudentList);
        this.arrangeStudentIntoSlot(groupClassList, displayList, studentList, subjectOrdinalNumberMap, subjectMap, shift, subjectCode);
    }

    private List<SubjectCurriculumEntity> getSubjectCurriculumList(StudentEntity student) {
        List<SubjectCurriculumEntity> result = new ArrayList<>();
        if(student.getShift() != null && student.getTerm() != null) {
            int nextTerm = student.getTerm() < 0 ? student.getTerm() + 2 : student.getTerm() + 1;
            nextTerm = nextTerm < 0 ? nextTerm + 2 : nextTerm + 1;

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
        }

        Collections.sort(result, new Comparator<SubjectCurriculumEntity>() {
            @Override
            public int compare(SubjectCurriculumEntity o1, SubjectCurriculumEntity o2) {
                return o1.getOrdinalNumber().compareTo(o2.getOrdinalNumber());
            }
        });

        return result;
    }

    private List<SubjectCurriculumEntity> getSubjectCurriculumListForSummer2018(StudentEntity student, int currentTerm) {
        List<SubjectCurriculumEntity> result = new ArrayList<>();
        if(student.getShift() != null && student.getTerm() != null) {
            if (student.getDocumentStudentEntityList() != null) {
                for (DocumentStudentEntity docStudent : student.getDocumentStudentEntityList()) {
                    if (docStudent.getCurriculumId() != null
                            && docStudent.getCurriculumId().getSubjectCurriculumEntityList() != null) {
                        for (SubjectCurriculumEntity sc : docStudent.getCurriculumId().getSubjectCurriculumEntityList()) {
                            if (sc.getTermNumber() == currentTerm) {
                                result.add(sc);
                            }
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

            Cell firstCell = spreadsheet.getRow(0).getCell(0);
            String firstCellValue = firstCell.getCellTypeEnum() == CellType.STRING ?
                    firstCell.getStringCellValue() : "";
            if(firstCellValue.contains("RollNumber")) {
                int excelDataIndexRow = 1;
                int rollNumberIndex = 0;
                int subjectsInNextCourseIndex = 1;

                for (int rowIndex = excelDataIndexRow; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
                    row = spreadsheet.getRow(rowIndex);
                    if (row != null) {
                        Cell rollNumberCell = row.getCell(rollNumberIndex);
                        Cell subjectInNextCourseCell = row.getCell(subjectsInNextCourseIndex);

                        String rollNumber = null;
                        if (rollNumberCell != null) {
                            rollNumber = rollNumberCell.getCellTypeEnum() == CellType.STRING ?
                                    rollNumberCell.getStringCellValue() : (rollNumberCell.getNumericCellValue() == 0 ?
                                    "" : Integer.toString((int) rollNumberCell.getNumericCellValue()));
                            rollNumber = rollNumber.toUpperCase().trim();
                        }

                        String subjectInNextCourseStr = null;
                        if (subjectInNextCourseCell != null) {
                            subjectInNextCourseStr = subjectInNextCourseCell.getStringCellValue().toUpperCase().trim();
                        }

                        if (rollNumber != null && subjectInNextCourseStr != null) {
                            SubjectList subjectList = result.get(rollNumber);
                            if(subjectList == null) {
                                subjectList = new SubjectList();
                                result.put(rollNumber, subjectList);
                            }

                            List<String> nextCourseList = subjectList.nextCourseList;

                            if(nextCourseList == null) {
                                nextCourseList = new ArrayList<>();
                            }

                            nextCourseList.add(subjectInNextCourseStr);

                            if (!nextCourseList.isEmpty()) {
                                nextCourseList = this.removeVOVAndOJTAndCapstoneInSubjectList(nextCourseList, subjectMap);
                            }

                            subjectList.nextCourseList = nextCourseList;
                        }
                    }
                }
            } else {
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
            }

            this.file1Done = true;
            workbook.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private Map<String, SubjectList> getSubjectSuggestionListForSummer2018(MultipartFile file, Map<String, SubjectEntity> subjectMap) {
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

            Cell firstCell = spreadsheet.getRow(0).getCell(0);
            String firstCellValue = firstCell.getCellTypeEnum() == CellType.STRING ?
                    firstCell.getStringCellValue() : "";
            if(firstCellValue.contains("RollNumber")) {
                int excelDataIndexRow = 1;
                int rollNumberIndex = 0;
                int subjectsInNextCourseIndex = 1;

                for (int rowIndex = excelDataIndexRow; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
                    row = spreadsheet.getRow(rowIndex);
                    if (row != null) {
                        Cell rollNumberCell = row.getCell(rollNumberIndex);
                        Cell subjectInNextCourseCell = row.getCell(subjectsInNextCourseIndex);

                        String rollNumber = null;
                        if (rollNumberCell != null) {
                            rollNumber = rollNumberCell.getCellTypeEnum() == CellType.STRING ?
                                    rollNumberCell.getStringCellValue() : (rollNumberCell.getNumericCellValue() == 0 ?
                                    "" : Integer.toString((int) rollNumberCell.getNumericCellValue()));
                            rollNumber = rollNumber.toUpperCase().trim();
                        }

                        String subjectInNextCourseStr = null;
                        if (subjectInNextCourseCell != null) {
                            subjectInNextCourseStr = subjectInNextCourseCell.getStringCellValue().toUpperCase().trim();
                        }

                        if (rollNumber != null && subjectInNextCourseStr != null) {
                            SubjectList subjectList = result.get(rollNumber);
                            if(subjectList == null) {
                                subjectList = new SubjectList();
                                result.put(rollNumber, subjectList);
                            }

                            List<String> nextCourseList = subjectList.nextCourseList;

                            if(nextCourseList == null) {
                                nextCourseList = new ArrayList<>();
                            }

                            nextCourseList.add(subjectInNextCourseStr);

                            if (!nextCourseList.isEmpty()) {
                                nextCourseList = this.removeVOVAndOJTAndCapstoneInSubjectList(nextCourseList, subjectMap);
                            }

                            subjectList.nextCourseList = nextCourseList;
                        }
                    }
                }
            } else {
                int excelDataIndexRow = 3;
                int rollNumberIndex = 1;
                int currentTermIndex = 12;

                for (int rowIndex = excelDataIndexRow; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
                    row = spreadsheet.getRow(rowIndex);
                    if (row != null) {
                        Cell rollNumberCell = row.getCell(rollNumberIndex);
                        String rollNumber = null;
                        if (rollNumberCell != null) {
                            rollNumber = rollNumberCell.getCellTypeEnum() == CellType.STRING ?
                                    rollNumberCell.getStringCellValue() : (rollNumberCell.getNumericCellValue() == 0 ?
                                    "" : Integer.toString((int) rollNumberCell.getNumericCellValue()));
                        }

                        if (rollNumber != null) {
                            SubjectList subjectList = new SubjectList();
                            Cell currentTermCell = row.getCell(currentTermIndex);

                            if(currentTermCell != null) {
                                double currentTermDouble = currentTermCell.getCellTypeEnum() == CellType.NUMERIC ?
                                        currentTermCell.getNumericCellValue() : -5;

                                try {
                                    if(currentTermDouble == -5) {
                                        subjectList.currentTerm = Integer.parseInt(currentTermCell.toString()
                                                .replace("+", "").trim());
                                    } else {
                                        subjectList.currentTerm = (int) currentTermDouble;
                                    }
                                } catch (NumberFormatException exception) {
                                    subjectList.currentTerm = -5;
                                }
                            } else {
                                subjectList.currentTerm = -5;
                            }

                            List<String> nextCourseList = new ArrayList<>();
                            List<String> suggestionList = new ArrayList<>();

                            subjectList.nextCourseList = nextCourseList;
                            subjectList.suggestionList = suggestionList;

                            result.put(rollNumber, subjectList);
                        }
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

    private void restoreClassArrangementFromFile(String path) {
        File file = new File(path);
        if(file.exists()) {
            XSSFWorkbook xssfWorkbook = null;
            try {
                xssfWorkbook = new XSSFWorkbook(file);

                int excelDataIndexRow = 3;
                int rollNumberIndex = 0;
                Row row = null;
                Map<ClassKey, List<StudentArrangementModel>> groupClassList = new HashMap<>();

                for(int i = 1; i < xssfWorkbook.getNumberOfSheets(); i++) {
                    Sheet spreadsheet = xssfWorkbook.getSheetAt(i);
                    String[] sheetNameParts = spreadsheet.getSheetName().split("_");
                    String subjectCode = sheetNameParts[0];
                    String shift = sheetNameParts[1];
                    String slotName = sheetNameParts[3];
                    for (int rowIndex = excelDataIndexRow; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
                        row = spreadsheet.getRow(rowIndex);
                        if (row != null) {
                            Cell rollNumberCell = row.getCell(rollNumberIndex);

                            String rollNumber = null;
                            if (rollNumberCell != null) {
                                rollNumber = rollNumberCell.getCellTypeEnum() == CellType.STRING ?
                                        rollNumberCell.getStringCellValue() : (rollNumberCell.getNumericCellValue() == 0 ?
                                        "" : Integer.toString((int) rollNumberCell.getNumericCellValue()));
                            }

                            if (rollNumber != null) {

                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidFormatException e) {
                e.printStackTrace();
            }
        }
    }

    //Class arrangement 6/6 + 10
    private JsonObject ReadFile4(MultipartFile fileSuggestion, HttpServletRequest request) {
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
//            Map<String, SubjectList> studentSubjectSuggestion = this.getSubjectSuggestionList(fileSuggestion, subjectMap);
            Map<String, SubjectList> studentSubjectSuggestion = this.getSubjectSuggestionListForSummer2018(fileSuggestion, subjectMap);
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

                if (subjectList.nextCourseList != null && subjectList.currentTerm != -5) {
                    StudentEntity student = studentMap.get(rollNumber);

                    if(student != null) {
                        StudentArrangementModel std = new StudentArrangementModel();
                        std.student = student;

                        List<SubjectCurriculumEntity> subjectCurriculumList =
                                this.getSubjectCurriculumListForSummer2018(student, subjectList.currentTerm);

                        //To pre arrange SUMMER 2018
                        subjectList.nextCourseList = subjectCurriculumList.stream()
                                .filter(q -> q.getTermNumber() == subjectList.currentTerm)
                                .map(q -> q.getSubjectId().getId()).collect(Collectors.toList());
                        std.numOfSubjects = subjectList.nextCourseList.size();

                        int ordinalCount = 1;
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
                                    std.subjects[pos] = subjectCode + "_" + ordinalCount;
                                }
                            }

                            ordinalCount++;
                        }

                        studentList.add(std);
                    }
                }
            }
            this.process1 = true;

            // Create class for LAB
            int count;
            int classNumber;
            int classCount = 0;
            for (String shift : shiftMapForLAB.keySet()) {
                Map<String, List<StudentEntity>> subjectMapForLAB = shiftMapForLAB.get(shift);
                for (String subjectCode : subjectMapForLAB.keySet()) {
                    count = 0;
                    classNumber = 1;
                    classCount++;

                    SubjectEntity subject = subjectMap.get(subjectCode);

                    List<StudentEntity> list = subjectMapForLAB.get(subjectCode);
                    List<String> dataRow;
                    for (StudentEntity student : list) {
                        StudentArrangementModel currentStudentModel = studentList.stream()
                                .filter(q -> q.student.getRollNumber().equals(student.getRollNumber()))
                                .collect(Collectors.toList()).get(0);

                        if (count == 25) {
                            classCount++;
                            classNumber++;
                            count = 0;
                        }

                        dataRow = new ArrayList<>();
                        dataRow.add(subject.getId());
                        dataRow.add(subject.getName());
                        dataRow.add(student.getRollNumber());
                        dataRow.add(student.getFullName());
                        dataRow.add(subjectCode + "_" + shift + "_" + classNumber + "_T" + ((classCount % 6) + 2));
                        dataRow.add(shift);
                        displayList.add(dataRow);

                        currentStudentModel.setLabDay("T" + ((classCount % 6) + 2));

                        ++count;
                    }
                }
            }

            // Shift, Subject, Slot, Ordinal number
            Map<String, Map<String, Map<String, Integer>>> shiftOrdinalNumberMap = new HashMap<>();
            shiftOrdinalNumberMap.put("AM", new HashMap<>());
            shiftOrdinalNumberMap.put("PM", new HashMap<>());

            // Group Student by subject, slot and shift
            Map<ClassKey, List<StudentArrangementModel>> groupClassList = new HashMap<>();

            // Create course classes for other subjects
            for (int i = 0; i <= 5; i++) {
                // Group by StudentKey
                Map<StudentKeyWithOrdinal, List<StudentArrangementModel>> groupStudentsMap = new HashMap<>();
                for (StudentArrangementModel student : studentList) {
                    if(student.student.getTerm() != null && student.student.getShift() != null) {
                        StudentKeyWithOrdinal key = new StudentKeyWithOrdinal();
                        key.numOfSubjects = student.numOfSubjects;
                        key.termNumber = student.student.getTerm();
                        key.shift = student.student.getShift();
                        key.subject = student.subjects[i];
                        if(key.subject.split("_").length > 1) {
                            key.subjectOrdinalNumber = Integer.parseInt(key.subject.split("_")[1]);
                        } else {
                            System.out.println(key.subject);
                        }

                        List<StudentArrangementModel> list = groupStudentsMap.get(key);
                        if (list == null) {
                            list = new ArrayList<>();
                            groupStudentsMap.put(key, list);
                        }
                        list.add(student);
                    } else {
                        System.out.println(student.student.getRollNumber() + " is null");
                    }
                }

                // Sort key
                List<StudentKeyWithOrdinal> keyList = new ArrayList<>(groupStudentsMap.keySet());
                keyList.sort(new Comparator<StudentKeyWithOrdinal>() {
                    @Override
                    public int compare(StudentKeyWithOrdinal k1, StudentKeyWithOrdinal k2) {
                        return Integer.compare(k1.termNumber, k2.termNumber);
                    }
                }.thenComparing(new Comparator<StudentKeyWithOrdinal>() {
                    @Override
                    public int compare(StudentKeyWithOrdinal k1, StudentKeyWithOrdinal k2) {
                        return Integer.compare(k2.numOfSubjects, k1.numOfSubjects);
                    }
                }));

                // Create class
                for (StudentKeyWithOrdinal key : keyList) {
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

                    String subjectCode = key.subject.split("_")[0];
                    int ordinalNumber = -1;

                    if(key.subject.split("_").length > 1) {
                        ordinalNumber = Integer.parseInt(key.subject.split("_")[1]);
                    }

                    if (!subjectCode.isEmpty() && ordinalNumber != -1) {
                        this.arrangeStudentIntoSlotSixTen(groupClassList, amList, shiftOrdinalNumberMap.get("AM"),
                                subjectMap, "AM", subjectCode, ordinalNumber, 0);
                        this.arrangeStudentIntoSlotSixTen(groupClassList, pmList, shiftOrdinalNumberMap.get("PM"),
                                subjectMap, "PM", subjectCode, ordinalNumber, 0);
                    }
                }

            }

            for(ClassKey classKey : groupClassList.keySet()) {
                List<StudentArrangementModel> tempStudentList = groupClassList.get(classKey);
                int ordinalNumber = 1;
                SubjectEntity subjectEntity = subjectMap.get(classKey.subjectCode);

                while(tempStudentList.size() >= 55) {
                    List<StudentArrangementModel> finalStudentList = tempStudentList.stream().skip(0).limit(25).collect(Collectors.toList());
                    String currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
                            + classKey.slotName;
                    for (StudentArrangementModel std : finalStudentList) {
                        List<String> row = new ArrayList<>();
                        row.add(subjectEntity.getId());
                        row.add(subjectEntity.getName());
                        row.add(std.student.getRollNumber());
                        row.add(std.student.getFullName());
                        row.add(currentClass);
                        row.add(classKey.shift);

                        displayList.add(row);
                    }

                    tempStudentList.removeAll(finalStudentList);
                    ordinalNumber++;
                }

                if (tempStudentList.size() <= 54 && tempStudentList.size() >= 51) {
                    List<StudentArrangementModel> finalStudentList = tempStudentList.stream().skip(0).limit(tempStudentList.size() - 30).collect(Collectors.toList());
                    String currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
                            + classKey.slotName;
                    for (StudentArrangementModel std : finalStudentList) {
                        List<String> row = new ArrayList<>();
                        row.add(subjectEntity.getId());
                        row.add(subjectEntity.getName());
                        row.add(std.student.getRollNumber());
                        row.add(std.student.getFullName());
                        row.add(currentClass);
                        row.add(classKey.shift);

                        displayList.add(row);
                    }

                    tempStudentList.removeAll(finalStudentList);
                    ordinalNumber++;
                }

                if(tempStudentList.size() <= 50 & tempStudentList.size() >= 40) {
                    List<StudentArrangementModel> finalStudentList = tempStudentList.stream().skip(0).limit(25).collect(Collectors.toList());
                    String currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
                            + classKey.slotName;
                    for (StudentArrangementModel std : finalStudentList) {
                        List<String> row = new ArrayList<>();
                        row.add(subjectEntity.getId());
                        row.add(subjectEntity.getName());
                        row.add(std.student.getRollNumber());
                        row.add(std.student.getFullName());
                        row.add(currentClass);
                        row.add(classKey.shift);

                        displayList.add(row);
                    }

                    tempStudentList.removeAll(finalStudentList);
                    ordinalNumber++;

                    finalStudentList = tempStudentList.stream().skip(0).collect(Collectors.toList());
                    currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
                            + classKey.slotName;
                    for (StudentArrangementModel std : finalStudentList) {
                        List<String> row = new ArrayList<>();
                        row.add(subjectEntity.getId());
                        row.add(subjectEntity.getName());
                        row.add(std.student.getRollNumber());
                        row.add(std.student.getFullName());
                        row.add(currentClass);
                        row.add(classKey.shift);

                        displayList.add(row);
                    }

                    tempStudentList.removeAll(finalStudentList);
                    ordinalNumber++;
                }

                if(tempStudentList.size() <= 39 & tempStudentList.size() >= 30) {
                    List<StudentArrangementModel> finalStudentList = tempStudentList.stream().skip(0).limit(tempStudentList.size() - 15).collect(Collectors.toList());
                    String currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
                            + classKey.slotName;
                    for (StudentArrangementModel std : finalStudentList) {
                        List<String> row = new ArrayList<>();
                        row.add(subjectEntity.getId());
                        row.add(subjectEntity.getName());
                        row.add(std.student.getRollNumber());
                        row.add(std.student.getFullName());
                        row.add(currentClass);
                        row.add(classKey.shift);

                        displayList.add(row);
                    }

                    tempStudentList.removeAll(finalStudentList);
                    ordinalNumber++;

                    finalStudentList = tempStudentList.stream().skip(0).limit(15).collect(Collectors.toList());
                    currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
                            + classKey.slotName;
                    for (StudentArrangementModel std : finalStudentList) {
                        List<String> row = new ArrayList<>();
                        row.add(subjectEntity.getId());
                        row.add(subjectEntity.getName());
                        row.add(std.student.getRollNumber());
                        row.add(std.student.getFullName());
                        row.add(currentClass);
                        row.add(classKey.shift);

                        displayList.add(row);
                    }

                    tempStudentList.removeAll(finalStudentList);
                    ordinalNumber++;
                }

                if(!tempStudentList.isEmpty()) {
                    List<StudentArrangementModel> finalStudentList = tempStudentList.stream().skip(0).collect(Collectors.toList());
                    String currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
                            + classKey.slotName;
                    for (StudentArrangementModel std : finalStudentList) {
                        List<String> row = new ArrayList<>();
                        row.add(subjectEntity.getId());
                        row.add(subjectEntity.getName());
                        row.add(std.student.getRollNumber());
                        row.add(std.student.getFullName());
                        row.add(currentClass);
                        row.add(classKey.shift);

                        displayList.add(row);
                    }

                    tempStudentList.removeAll(finalStudentList);
                    ordinalNumber++;
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

    //New algorithm
    private JsonObject ReadFile5(MultipartFile fileSuggestion, HttpServletRequest request) {
        List<List<String>> displayList = new ArrayList<>();
        List<List<String>> displayListFail = new ArrayList<>();
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
//            Map<String, SubjectList> studentSubjectSuggestion = this.getSubjectSuggestionList(fileSuggestion, subjectMap);
            Map<String, SubjectList> studentSubjectSuggestion = this.getSubjectSuggestionListForSummer2018(fileSuggestion, subjectMap);
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

            int countCSI101 = 0;
            int countCSI102 = 0;
            List<StudentArrangementModel> studentList = new ArrayList<>();
            for (String rollNumber : studentSubjectSuggestion.keySet()) {
                SubjectList subjectList = studentSubjectSuggestion.get(rollNumber);

                if (subjectList.nextCourseList != null && subjectList.currentTerm != -5) {
                    StudentEntity student = studentMap.get(rollNumber);

                    if(student != null) {
                        StudentArrangementModel std = new StudentArrangementModel();
                        std.student = student;

                        List<SubjectCurriculumEntity> subjectCurriculumList =
                                this.getSubjectCurriculumListForSummer2018(student, subjectList.currentTerm);

                        //To pre arrange SUMMER 2018
                        subjectList.nextCourseList = subjectCurriculumList.stream()
                                .filter(q -> q.getTermNumber() == subjectList.currentTerm
                                        && student.getProgramId() != null && student.getProgramId().getId() == q.getCurriculumId().getProgramId().getId())
                                .map(q -> q.getSubjectId().getId()).collect(Collectors.toList());
                        std.numOfSubjects = subjectList.nextCourseList.size();

                        int ordinalCount = 1;
                        for (String subjectCode : subjectList.nextCourseList) {
                            if(subjectCode.equals("CSI101")) {
                                countCSI101++;
                            }

                            if(subjectCode.equals("CSI102")) {
                                countCSI102++;
                            }

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
                                    SubjectCurriculumEntity entity = subjectCurriculumList.stream().filter(q -> q.getTermNumber() == subjectList.currentTerm
                                            && q.getSubjectId().getId().equalsIgnoreCase(subjectCode)).findFirst().get();

                                    std.subjects[pos] = subjectCode + "_" + ordinalCount;
                                }
                            }

                            ordinalCount++;
                        }

                        studentList.add(std);
                    } else {
                        System.out.println(rollNumber + " doesn't exist in the current database");
                    }
                }
            }
            this.process1 = true;

            System.out.println("CSI101: " + countCSI101);
            System.out.println("CSI102: " + countCSI102);

            // Create class for LAB
            int count;
            int classNumber;
            int classCount = 0;
            for (String shift : shiftMapForLAB.keySet()) {
                Map<String, List<StudentEntity>> subjectMapForLAB = shiftMapForLAB.get(shift);
                for (String subjectCode : subjectMapForLAB.keySet()) {
                    count = 0;
                    classNumber = 1;
                    classCount++;

                    SubjectEntity subject = subjectMap.get(subjectCode);

                    List<StudentEntity> list = subjectMapForLAB.get(subjectCode);
                    List<String> dataRow;
                    for (StudentEntity student : list) {
                        StudentArrangementModel currentStudentModel = studentList.stream()
                                .filter(q -> q.student.getRollNumber().equals(student.getRollNumber()))
                                .collect(Collectors.toList()).get(0);

                        if (count == 25) {
                            classCount++;
                            classNumber++;
                            count = 0;
                        }

                        dataRow = new ArrayList<>();
                        dataRow.add(subject.getId());
                        dataRow.add(subject.getName());
                        dataRow.add(student.getRollNumber());
                        dataRow.add(student.getFullName());
                        dataRow.add(subjectCode + "_" + shift + "_" + classNumber + "_T" + ((classCount % 6) + 2));
                        dataRow.add(shift);
                        displayList.add(dataRow);

                        currentStudentModel.setLabDay("T" + ((classCount % 6) + 2));

                        ++count;
                    }
                }
            }

            // Shift, Subject, Slot, Ordinal number
            Map<String, Map<String, Map<String, Integer>>> shiftOrdinalNumberMap = new HashMap<>();
            shiftOrdinalNumberMap.put("AM", new HashMap<>());
            shiftOrdinalNumberMap.put("PM", new HashMap<>());

            // Group Student by subject, slot and shift
            Map<ClassKey, List<StudentArrangementModel>> groupClassList = new HashMap<>();

            // Create course classes for other subjects
            for (int i = 0; i <= 5; i++) {
                // Group by StudentKey
                Map<StudentKeyWithOrdinal, List<StudentArrangementModel>> groupStudentsMap = new HashMap<>();
                for (StudentArrangementModel student : studentList) {
                    if(student.student.getTerm() != null && student.student.getShift() != null) {
                        StudentKeyWithOrdinal key = new StudentKeyWithOrdinal();
                        key.numOfSubjects = student.numOfSubjects;
                        key.termNumber = student.student.getTerm();
                        key.shift = student.student.getShift();
                        key.subject = student.subjects[i];
                        if(key.subject.trim() != "") {
                            key.subjectOrdinalNumber = Integer.parseInt(key.subject.split("_")[1]);
                        }

                        List<StudentArrangementModel> list = groupStudentsMap.get(key);
                        if (list == null) {
                            list = new ArrayList<>();
                            groupStudentsMap.put(key, list);
                        }
                        list.add(student);
                    } else {
                        //System.out.println(student.student.getRollNumber() + " is null");
                    }
                }

                // Sort key
                List<StudentKeyWithOrdinal> keyList = new ArrayList<>(groupStudentsMap.keySet());
                keyList.sort(new Comparator<StudentKeyWithOrdinal>() {
                    @Override
                    public int compare(StudentKeyWithOrdinal k1, StudentKeyWithOrdinal k2) {
                        return Integer.compare(k2.numOfSubjects, k1.numOfSubjects);
                    }
                }.thenComparing(new Comparator<StudentKeyWithOrdinal>() {
                    @Override
                    public int compare(StudentKeyWithOrdinal k1, StudentKeyWithOrdinal k2) {
                        return Integer.compare(k1.termNumber, k2.termNumber);
                    }
                }));

                // Create class
                for (StudentKeyWithOrdinal key : keyList) {
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

                    String subjectCode = key.subject.split("_")[0];
                    int ordinalNumber = -1;

                    if(key.subject.split("_").length > 1) {
                        ordinalNumber = Integer.parseInt(key.subject.split("_")[1]);
                    }

                    if (!subjectCode.isEmpty() && ordinalNumber != -1) {
                        this.arrangeStudentIntoSlotSixTen(groupClassList, amList, shiftOrdinalNumberMap.get("AM"),
                                subjectMap, "AM", subjectCode, ordinalNumber, 0);
                        this.arrangeStudentIntoSlotSixTen(groupClassList, pmList, shiftOrdinalNumberMap.get("PM"),
                                subjectMap, "PM", subjectCode, ordinalNumber, 0);
                    }
                }

            }

            HashMap<ClassKey, List<AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>>>>
                    groupClass25List = new HashMap<>();

            for(ClassKey classKey : groupClassList.keySet()) {
                List<StudentArrangementModel> tempStudentList = groupClassList.get(classKey);
                int ordinalNumber = 1;
                SubjectEntity subjectEntity = subjectMap.get(classKey.subjectCode);

                while(tempStudentList.size() >= 55) {
                    List<StudentArrangementModel> finalStudentList = tempStudentList.stream().skip(0).limit(25).collect(Collectors.toList());
//                    String currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
//                            + classKey.slotName;
//                    for (StudentArrangementModel std : finalStudentList) {
//                        List<String> row = new ArrayList<>();
//                        row.add(subjectEntity.getId());
//                        row.add(subjectEntity.getName());
//                        row.add(std.student.getRollNumber());
//                        row.add(std.student.getFullName());
//                        row.add(currentClass);
//                        row.add(classKey.shift);
//
//                        displayList.add(row);
//                    }
                    ClassKeyWithOrdinal classKeyOrdinal = new ClassKeyWithOrdinal(classKey, ordinalNumber);
                    AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>> realClass =
                            new AbstractMap.SimpleEntry<>(classKeyOrdinal, finalStudentList);

                    ClassKey noSlotKey = new ClassKey("", classKey.shift, classKey.subjectCode, classKey.is10Week);

                    List<AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>>> realClassList =
                            groupClass25List.get(noSlotKey);

                    if(realClassList == null) {
                        realClassList = new ArrayList();
                        groupClass25List.put(noSlotKey, realClassList);
                    }

                    realClassList.add(realClass);

                    tempStudentList.removeAll(finalStudentList);
                    ordinalNumber++;
                }

                if (tempStudentList.size() <= 54 && tempStudentList.size() >= 51) {
                    List<StudentArrangementModel> finalStudentList = tempStudentList.stream().skip(0).limit(tempStudentList.size() - 30).collect(Collectors.toList());
//                    String currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
//                            + classKey.slotName;
//                    for (StudentArrangementModel std : finalStudentList) {
//                        List<String> row = new ArrayList<>();
//                        row.add(subjectEntity.getId());
//                        row.add(subjectEntity.getName());
//                        row.add(std.student.getRollNumber());
//                        row.add(std.student.getFullName());
//                        row.add(currentClass);
//                        row.add(classKey.shift);
//
//                        displayList.add(row);
//                    }

                    ClassKeyWithOrdinal classKeyOrdinal = new ClassKeyWithOrdinal(classKey, ordinalNumber);
                    AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>> realClass =
                            new AbstractMap.SimpleEntry<>(classKeyOrdinal, finalStudentList);

                    ClassKey noSlotKey = new ClassKey("", classKey.shift, classKey.subjectCode, classKey.is10Week);

                    List<AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>>> realClassList =
                            groupClass25List.get(noSlotKey);

                    if(realClassList == null) {
                        realClassList = new ArrayList();
                        groupClass25List.put(noSlotKey, realClassList);
                    }

                    realClassList.add(realClass);

                    tempStudentList.removeAll(finalStudentList);
                    ordinalNumber++;
                }

                if(tempStudentList.size() <= 50 & tempStudentList.size() >= 40) {
                    List<StudentArrangementModel> finalStudentList = tempStudentList.stream().skip(0).limit(25).collect(Collectors.toList());
//                    String currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
//                            + classKey.slotName;
//                    for (StudentArrangementModel std : finalStudentList) {
//                        List<String> row = new ArrayList<>();
//                        row.add(subjectEntity.getId());
//                        row.add(subjectEntity.getName());
//                        row.add(std.student.getRollNumber());
//                        row.add(std.student.getFullName());
//                        row.add(currentClass);
//                        row.add(classKey.shift);
//
//                        displayList.add(row);
//                    }

                    ClassKeyWithOrdinal classKeyOrdinal = new ClassKeyWithOrdinal(classKey, ordinalNumber);
                    AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>> realClass =
                            new AbstractMap.SimpleEntry<>(classKeyOrdinal, finalStudentList);

                    ClassKey noSlotKey = new ClassKey("", classKey.shift, classKey.subjectCode, classKey.is10Week);

                    List<AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>>> realClassList =
                            groupClass25List.get(noSlotKey);

                    if(realClassList == null) {
                        realClassList = new ArrayList();
                        groupClass25List.put(noSlotKey, realClassList);
                    }

                    realClassList.add(realClass);

                    tempStudentList.removeAll(finalStudentList);
                    ordinalNumber++;

                    finalStudentList = tempStudentList.stream().skip(0).collect(Collectors.toList());
//                    currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
//                            + classKey.slotName;
//                    for (StudentArrangementModel std : finalStudentList) {
//                        List<String> row = new ArrayList<>();
//                        row.add(subjectEntity.getId());
//                        row.add(subjectEntity.getName());
//                        row.add(std.student.getRollNumber());
//                        row.add(std.student.getFullName());
//                        row.add(currentClass);
//                        row.add(classKey.shift);
//
//                        displayList.add(row);
//                    }

                    classKeyOrdinal = new ClassKeyWithOrdinal(classKey, ordinalNumber);
                    realClass = new AbstractMap.SimpleEntry<>(classKeyOrdinal, finalStudentList);

                    noSlotKey = new ClassKey("", classKey.shift, classKey.subjectCode, classKey.is10Week);

                    realClassList = groupClass25List.get(noSlotKey);

                    if(realClassList == null) {
                        realClassList = new ArrayList();
                        groupClass25List.put(noSlotKey, realClassList);
                    }

                    realClassList.add(realClass);

                    tempStudentList.removeAll(finalStudentList);
                    ordinalNumber++;
                }

                if(tempStudentList.size() <= 39 & tempStudentList.size() >= 30) {
                    List<StudentArrangementModel> finalStudentList = tempStudentList.stream().skip(0).limit(tempStudentList.size() - 15).collect(Collectors.toList());
//                    String currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
//                            + classKey.slotName;
//                    for (StudentArrangementModel std : finalStudentList) {
//                        List<String> row = new ArrayList<>();
//                        row.add(subjectEntity.getId());
//                        row.add(subjectEntity.getName());
//                        row.add(std.student.getRollNumber());
//                        row.add(std.student.getFullName());
//                        row.add(currentClass);
//                        row.add(classKey.shift);
//
//                        displayList.add(row);
//                    }

                    ClassKeyWithOrdinal classKeyOrdinal = new ClassKeyWithOrdinal(classKey, ordinalNumber);
                    AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>> realClass =
                            new AbstractMap.SimpleEntry<>(classKeyOrdinal, finalStudentList);

                    ClassKey noSlotKey = new ClassKey("", classKey.shift, classKey.subjectCode, classKey.is10Week);

                    List<AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>>> realClassList =
                            groupClass25List.get(noSlotKey);

                    if(realClassList == null) {
                        realClassList = new ArrayList();
                        groupClass25List.put(noSlotKey, realClassList);
                    }

                    realClassList.add(realClass);

                    tempStudentList.removeAll(finalStudentList);
                    ordinalNumber++;

                    finalStudentList = tempStudentList.stream().skip(0).limit(15).collect(Collectors.toList());
//                    currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
//                            + classKey.slotName;
//                    for (StudentArrangementModel std : finalStudentList) {
//                        List<String> row = new ArrayList<>();
//                        row.add(subjectEntity.getId());
//                        row.add(subjectEntity.getName());
//                        row.add(std.student.getRollNumber());
//                        row.add(std.student.getFullName());
//                        row.add(currentClass);
//                        row.add(classKey.shift);
//
//                        displayList.add(row);
//                    }

                    classKeyOrdinal = new ClassKeyWithOrdinal(classKey, ordinalNumber);
                    realClass = new AbstractMap.SimpleEntry<>(classKeyOrdinal, finalStudentList);

                    noSlotKey = new ClassKey("", classKey.shift, classKey.subjectCode, classKey.is10Week);

                    realClassList = groupClass25List.get(noSlotKey);

                    if(realClassList == null) {
                        realClassList = new ArrayList();
                        groupClass25List.put(noSlotKey, realClassList);
                    }

                    realClassList.add(realClass);

                    tempStudentList.removeAll(finalStudentList);
                    ordinalNumber++;
                }

                if(!tempStudentList.isEmpty()) {
                    List<StudentArrangementModel> finalStudentList = tempStudentList.stream().skip(0).collect(Collectors.toList());
//                    String currentClass = classKey.subjectCode + "_" + classKey.shift + "_" + ordinalNumber + "_"
//                            + classKey.slotName;
//                    for (StudentArrangementModel std : finalStudentList) {
//                        List<String> row = new ArrayList<>();
//                        row.add(subjectEntity.getId());
//                        row.add(subjectEntity.getName());
//                        row.add(std.student.getRollNumber());
//                        row.add(std.student.getFullName());
//                        row.add(currentClass);
//                        row.add(classKey.shift);
//
//                        displayList.add(row);
//                    }

                    ClassKeyWithOrdinal classKeyOrdinal = new ClassKeyWithOrdinal(classKey, ordinalNumber);
                    AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>> realClass =
                            new AbstractMap.SimpleEntry<>(classKeyOrdinal, finalStudentList);

                    ClassKey noSlotKey = new ClassKey("", classKey.shift, classKey.subjectCode, classKey.is10Week);

                    List<AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>>> realClassList =
                            groupClass25List.get(noSlotKey);

                    if(realClassList == null) {
                        realClassList = new ArrayList();
                        groupClass25List.put(noSlotKey, realClassList);
                    }

                    realClassList.add(realClass);

                    tempStudentList.removeAll(finalStudentList);
                    ordinalNumber++;
                }
            }

            String[] slotName = new String[]{"S11", "S12", "S13", "S21", "S22", "S23"};

            List<ClassKey> invalidClassList = new ArrayList<>();

            //PAINNNNNNNNNNNNNNNNNNNNNNN
            //optimized for same shift
            for (ClassKey classKey : groupClass25List.keySet()) {
                List<AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>>> realClassList
                        = groupClass25List.get(classKey);

                int totalClass = realClassList.size();
                int totalStudent = realClassList.stream().mapToInt(q -> q.getValue().size()).sum();

                if(totalClass > 0 && totalStudent/totalClass < 20) {
                    if(totalStudent < 15) {
                        invalidClassList.add(classKey);
                    } else {
                        for (AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>> realClassEntry : realClassList) {
                            ClassKeyWithOrdinal realClassKey = realClassEntry.getKey();
                            List<StudentArrangementModel> realClass = realClassEntry.getValue();

                            int failCount = -1;
                            if(realClass.size() < 15) {
                                failCount = 0;

                                for (StudentArrangementModel studentModel : realClass) {
                                    int position = 0;
                                    Boolean hasClass = false;

                                    innerLoop:
                                    for(Boolean isNotFree : studentModel.slots) {
                                        if(isNotFree == null || !isNotFree) {
                                            final int pos = position;
                                            if(realClassList.stream().filter(q -> q.getKey().slotName.contains(slotName[pos])).collect(Collectors.toList()).size() == 0) {
                                                hasClass = true;
                                                break innerLoop;
                                            }
                                        }

                                        position++;
                                    }

                                    if(!hasClass) {
                                        failCount++;
                                    }
                                }

                                if(failCount < 5) {
                                    List<StudentArrangementModel> removeList = new ArrayList<>();
                                    for (StudentArrangementModel studentModel : realClass) {
                                        int position = 0;

                                        innerLoop:
                                        for(Boolean isNotFree : studentModel.slots) {
                                            if(isNotFree == null || !isNotFree) {
                                                final int pos = position;
                                                if(realClassList.stream().filter(q -> q.getKey().slotName.contains(slotName[pos])).collect(Collectors.toList()).size() > 0) {
                                                    if(realClassKey.is10Week == null || realClassKey.is10Week == false || (realClassKey.is10Week == true && pos < 3 && (studentModel.slots[position + 3] == null || !studentModel.slots[position + 3]))) {
                                                        studentModel.slots[pos] = true;
                                                        String firstHalfSlot = realClassKey.slotName.substring(0, 3);
                                                        studentModel.slots[Arrays.asList(slotName).indexOf(firstHalfSlot)] = false;

                                                        if(realClassKey.is10Week != null && realClassKey.is10Week) {
                                                            studentModel.slots[pos + 3] = true;
                                                            studentModel.slots[Arrays.asList(slotName).indexOf(firstHalfSlot) + 3] = false;
                                                        }

                                                        removeList.add(studentModel);
                                                        AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>> newClassEntry = realClassList.stream().filter(q -> q.getKey().slotName.contains(slotName[pos])).collect(Collectors.toList()).get(0);
                                                        List<StudentArrangementModel> newClass = newClassEntry.getValue();
                                                        newClass.add(studentModel);
                                                        newClassEntry.setValue(newClass);

                                                        break innerLoop;
                                                    }
                                                }
                                            }

                                            position++;
                                        }
                                    }

                                    realClass.removeAll(removeList);
                                    realClassEntry.setValue(realClass);
                                }
                            }
                        }
                    }
                }
            }

            //PAINNNNNNNNNNNNNNNNNNNNNNN
            //Optimized for opposite shift
            for (ClassKey classKey : groupClass25List.keySet()) {
                List<AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>>> realClassList
                        = groupClass25List.get(classKey);

                String oppositeShift = classKey.shift.equalsIgnoreCase("AM") ? "PM" : "AM";

                ClassKey oppositeClassKey = new ClassKey(classKey.slotName, oppositeShift, classKey.subjectCode, classKey.is10Week);

                List<AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>>> realOppositeClassList
                        = groupClass25List.get(oppositeClassKey);

                if(realOppositeClassList != null) {
                    int totalStudent = realClassList.stream().mapToInt(q -> q.getValue().size()).sum();
                    for (AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>> realClassEntry : realClassList) {
                        //NHO REMOVE SINH VIEN DA~ CHUYEN LOPPPPPPPPPPPPPPPPPPPPPPPPP
                        List<StudentArrangementModel> realClass = realClassEntry.getValue();
                        boolean isRealClass10Weeks = realClassEntry.getKey().is10Week != null && realClassEntry.getKey().is10Week;
                        boolean doneArrangement = false;

                        if(realClass.size() < 15) {
                            //check slot and flip this up

                            int[] freeSlotCount = new int[6];
                            for (StudentArrangementModel student : realClass) {
                                if(student.oppositeSlots == null) {
                                    student.oppositeSlots = new Boolean[6];
                                }

                                for(int i = 0; i < 6; i++) {
                                    if(student.oppositeSlots[i] == null || !student.oppositeSlots[i]) {
                                        if(student.oppositeSlots[i] == null) {
                                            student.oppositeSlots[i] = false;
                                        }

                                        freeSlotCount[i]++;
                                    }
                                }
                            }

                            for (int i = 0; i < freeSlotCount.length && !doneArrangement; i++) {
                                if(freeSlotCount[i] == realClass.size()) {
                                    final int j = i;
                                    List<AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>>> availableClassList =
                                            realOppositeClassList.stream().filter(q -> q.getKey().slotName.contains(slotName[j])).collect(Collectors.toList());

                                    availableClassList.sort(Comparator.comparingInt(o -> o.getValue().size()));

                                    if(availableClassList.size() > 0) {
                                        for (AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>> selectedClassEntry : availableClassList) {
                                            boolean isValidForThisClass = true;
                                            boolean isSelectedClass10Weeks = selectedClassEntry.getKey().is10Week != null && selectedClassEntry.getKey().is10Week;
                                            if(isSelectedClass10Weeks) {
                                                isValidForThisClass &= i < 3;

                                                if(isValidForThisClass) {
                                                    for (StudentArrangementModel studentModel : realClass) {
                                                        if(studentModel.oppositeSlots[j] == null || studentModel.oppositeSlots[j] == false) {
                                                            isValidForThisClass &= studentModel.oppositeSlots != null
                                                                    && studentModel.oppositeSlots[j + 3] != null
                                                                    && studentModel.oppositeSlots[j + 3];
                                                        }
                                                    }
                                                }
                                            }

                                            if(isValidForThisClass) {
                                                List<StudentArrangementModel> selectedClass = selectedClassEntry.getValue();

                                                List<StudentArrangementModel> removeList = new ArrayList<>();

                                                for (StudentArrangementModel studentModel : realClass) {
                                                    String currentSlotName = realClassEntry.getKey().slotName;

                                                    if(isRealClass10Weeks) {
                                                        currentSlotName = currentSlotName.substring(0, 3);
                                                    }

                                                    int position = Arrays.asList(slotName).indexOf(currentSlotName);

                                                    studentModel.slots[position] = false;
                                                    studentModel.oppositeSlots[i] = true;

                                                    if(isSelectedClass10Weeks) {
                                                        studentModel.oppositeSlots[i + 3] = true;
                                                    }

                                                    if(isRealClass10Weeks) {
                                                        studentModel.slots[position + 3] = false;
                                                    }

                                                    removeList.add(studentModel);
                                                    selectedClass.add(studentModel);
                                                }

                                                realClass.removeAll(removeList);
                                                realClassEntry.setValue(realClass);
                                                selectedClassEntry.setValue(selectedClass);
                                                realOppositeClassList.set(0, selectedClassEntry);
                                                doneArrangement = true;
                                            }
                                        }
                                    }
                                }
                            }

                            if(!doneArrangement) {
                                List<StudentArrangementModel> removeList = new ArrayList<>();
                                for (StudentArrangementModel student : realClass) {
                                    for (AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>> realOppositeClassEntry : realOppositeClassList) {
                                        String oppositeClassSlotName = realOppositeClassEntry.getKey().slotName;
                                        boolean isOppositeClass10Weeks = realOppositeClassEntry.getKey().is10Week != null && realOppositeClassEntry.getKey().is10Week;

                                        if(isOppositeClass10Weeks) {
                                            oppositeClassSlotName = oppositeClassSlotName.substring(0, 3);
                                        }

                                        int position = Arrays.asList(slotName).indexOf(oppositeClassSlotName);

                                        if(student.oppositeSlots == null) {
                                            student.oppositeSlots = new Boolean[6];
                                        }

                                        if(student.oppositeSlots[position] == null || !student.oppositeSlots[position]) {
                                            boolean isAvailableInAllCases = true;
                                            if(isOppositeClass10Weeks) {
                                                if(position > 3 || (student.oppositeSlots[position] != null && student.oppositeSlots[position])) {
                                                    isAvailableInAllCases = false;
                                                }
                                            }

                                            if(isAvailableInAllCases) {
                                                String realClassSlotName = realClassEntry.getKey().slotName;
                                                if(isRealClass10Weeks) {
                                                    realClassSlotName = realClassSlotName.substring(0, 3);
                                                }

                                                List<StudentArrangementModel> selectedClass = realOppositeClassEntry.getValue();

                                                student.oppositeSlots[position] = true;
                                                student.slots[Arrays.asList(slotName).indexOf(realClassSlotName)] = false;

                                                if(isOppositeClass10Weeks) {
                                                    student.oppositeSlots[position + 3] = true;
                                                }

                                                if(isRealClass10Weeks) {
                                                    student.slots[Arrays.asList(slotName).indexOf(realClassSlotName) + 3] = false;
                                                }

                                                removeList.add(student);
                                                selectedClass.add(student);
                                                realOppositeClassEntry.setValue(selectedClass);
                                            }
                                        }
                                    }
                                }

                                realClass.removeAll(removeList);
                                realClassEntry.setValue(realClass);
                            }
                        }
                    }
                }
            }

            for (ClassKey classKey : groupClass25List.keySet()) {
                List<AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>>> realClassList
                        = groupClass25List.get(classKey);
                int totalStudent = realClassList.stream().mapToInt(q -> q.getValue().size()).sum();
                SubjectEntity subjectEntity = subjectMap.get(classKey.subjectCode);

                if(totalStudent > 10) {
                    for (AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>> realClassEntry : realClassList) {
                        String currentClass = classKey.subjectCode + "_" + classKey.shift + "_"
                                + realClassEntry.getKey().ordinalNumber + "_" + realClassEntry.getKey().slotName;
                        for (StudentArrangementModel std : realClassEntry.getValue()) {
                            List<String> row = new ArrayList<>();
                            row.add(subjectEntity.getId());
                            row.add(subjectEntity.getName());
                            row.add(std.student.getRollNumber());
                            row.add(std.student.getFullName());
                            row.add(currentClass);
                            row.add(realClassEntry.getKey().shift);

                            displayList.add(row);
                        }
                    }
                } else {
                    for (AbstractMap.SimpleEntry<ClassKeyWithOrdinal, List<StudentArrangementModel>> realClassEntry : realClassList) {
                        String currentClass = classKey.subjectCode + "_" + classKey.shift + "_"
                                + realClassEntry.getKey().ordinalNumber + "_" + realClassEntry.getKey().slotName;
                        for (StudentArrangementModel std : realClassEntry.getValue()) {
                            List<String> row = new ArrayList<>();
                            row.add(subjectEntity.getId());
                            row.add(subjectEntity.getName());
                            row.add(std.student.getRollNumber());
                            row.add(std.student.getFullName());
                            row.add(currentClass);
                            row.add(realClassEntry.getKey().shift);

                            displayListFail.add(row);
                        }
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

            displayListFail.sort(new Comparator<List<String>>() {
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
            request.getSession().setAttribute("STUDENT_ARRANGEMENT_BY_SLOT_LIST_FAILED", displayListFail);
            jsonObj.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObj.addProperty("success", false);
            jsonObj.addProperty("message", e.getMessage());
        }

        return jsonObj;
    }

    private void arrangeStudentIntoSlotSixTen(Map<ClassKey, List<StudentArrangementModel>> groupClassList,
                                              List<StudentArrangementModel> studentList,
                                              Map<String, Map<String, Integer>> subjectOrdinalNumberMap,
                                              Map<String, SubjectEntity> subjectMap,
                                              String shift, String subjectCode, int subjectOrdinalNumber, int lastCount) {
        if (studentList.isEmpty()) {
            return;
        }

        if(lastCount == studentList.size() && lastCount != 0) {
            //System.out.println("WTF " + subjectCode + " with ordinal: " + subjectOrdinalNumber);
            return;
        } else {
            lastCount = studentList.size();
        }

        Map<String, Integer> ordinalNumberMap = subjectOrdinalNumberMap.get(subjectCode);
        if (ordinalNumberMap == null) {
            ordinalNumberMap = new HashMap<>();
            subjectOrdinalNumberMap.put(subjectCode, ordinalNumberMap);
        }

        String[] slotName = new String[]{"S11", "S12", "S13", "S21", "S22", "S23"};

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

        int finalPosition = 0;

        if(subjectOrdinalNumber < 3) {
            int maxStudent = 0;
            for (int i = 0; i < 3; i++) {
                if (maxStudent < studentsInSlot.get(i).size()) {
                    maxStudent = studentsInSlot.get(i).size();
                    finalPosition = i;
                }
            }
        } else if (subjectOrdinalNumber > 3) {
            int maxStudent = 0;
            for (int i = 3; i < 6; i++) {
                if (maxStudent < studentsInSlot.get(i).size()) {
                    maxStudent = studentsInSlot.get(i).size();
                    finalPosition = i;
                }
            }
        } else {
            int maxStudent = 0;
            for (int i = 0; i < 3; i++) {
                if (maxStudent < studentsInSlot.get(i).size()
                        && studentsInSlot.get(i).size() == studentsInSlot.get(i + 3).size()) {
                    maxStudent = studentsInSlot.get(i).size();
                    finalPosition = i;
                }
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
            if(subjectOrdinalNumber != 3) {
                std.slots[finalPosition] = true;
            } else {
                std.slots[finalPosition] = true;
                std.slots[finalPosition + 3] = true;
            }
        }

        String currentSlotName = "";

        if(subjectOrdinalNumber != 3) {
            currentSlotName = slotName[finalPosition];
        } else {
            currentSlotName = slotName[finalPosition] + slotName[finalPosition + 3];
        }

        boolean is10Week = false;

        if(subjectOrdinalNumber == 3) {
            is10Week = true;
        }

        ClassKey classKey = new ClassKey(currentSlotName, shift, subjectCode, is10Week);
        List<StudentArrangementModel> classList = groupClassList.get(classKey);

        if(classList == null) {
            classList = new ArrayList<>();
            groupClassList.put(classKey, classList);
        }

        classList.addAll(finalStudentList);
        studentList.removeAll(finalStudentList);
        this.arrangeStudentIntoSlotSixTen(groupClassList, studentList, subjectOrdinalNumberMap, subjectMap,
                shift, subjectCode, subjectOrdinalNumber, lastCount);
    }

    @RequestMapping(value = "/studentArrangementBySlot/import", method = RequestMethod.POST)
    @ResponseBody
    public Callable<JsonObject> importFileBySlot(
            @RequestParam("file-suggestion") MultipartFile fileSuggestion,
            @RequestParam(value = "is-relearn", required = false, defaultValue = "false") Boolean isRelearn,
            @RequestParam(value = "is-addition", required = false, defaultValue = "false") Boolean isAddition,
            HttpServletRequest request) {
        Callable<JsonObject> callable = new Callable<JsonObject>() {
            @Override
            public JsonObject call() throws Exception {
                boolean isRelearnValue = isRelearn == null ? false : isRelearn.booleanValue();
                boolean isAdditionValue = isAddition == null ? false : isAddition.booleanValue();
//                JsonObject obj = ReadFile3(fileSuggestion, request, isRelearnValue, isAdditionValue);
                JsonObject obj = ReadFile5(fileSuggestion, request);

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
        public int currentTerm;
    }

    private class StudentArrangementModel {
        public StudentEntity student;
        public int numOfSubjects; // In suggestion list
        public String[] subjects; // Based on subject's OrdinalNumber in curriculum

        public String getLabDay() {
            return labDay;
        }

        public void setLabDay(String labDay) {
            this.labDay = labDay;
        }

        public String labDay;

        // [0, 1, 2]: Slot 1,2,3 of Monday, Wednesday, Friday
        // [3, 4, 5]: Slot 1,2,3 of Tuesday, Thursday
        public Boolean[] slots;

        public Boolean[] oppositeSlots;

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

    private class StudentKeyWithOrdinal {
        public int termNumber;
        public int numOfSubjects;
        public String shift;
        public String subject;
        public int subjectOrdinalNumber;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StudentKeyWithOrdinal that = (StudentKeyWithOrdinal) o;

            if (termNumber != that.termNumber) return false;
            if (numOfSubjects != that.numOfSubjects) return false;
            if (subjectOrdinalNumber != that.subjectOrdinalNumber) return false;
            if (!shift.equals(that.shift)) return false;
            return subject.equals(that.subject);
        }

        @Override
        public int hashCode() {
            int result = termNumber;
            result = 31 * result + numOfSubjects;
            result = 31 * result + shift.hashCode();
            result = 31 * result + subject.hashCode();
            result = 31 * result + subjectOrdinalNumber;
            return result;
        }
    }

    private class ClassKey {
        public String slotName;
        public String shift;
        public String subjectCode;
        public Boolean is10Week;

        public ClassKey(String slotName, String shift, String subjectCode) {
            this.slotName = slotName;
            this.shift = shift;
            this.subjectCode = subjectCode;
        }

        public ClassKey(String slotName, String shift, String subjectCode, Boolean is10Week) {
            this.slotName = slotName;
            this.shift = shift;
            this.subjectCode = subjectCode;
            this.is10Week = is10Week;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ClassKey that = (ClassKey) o;

            if (!slotName.equalsIgnoreCase(that.slotName)) return false;
            if (!shift.equalsIgnoreCase(that.shift)) return false;
            if (is10Week != that.is10Week) return false;
            return subjectCode.equalsIgnoreCase(that.subjectCode);
        }

        @Override
        public int hashCode() {
            int result = slotName.hashCode();
            result = 31 * result + shift.hashCode();
            result = 31 * result + subjectCode.hashCode();
            if(is10Week != null) {
                result = 31 * result + is10Week.hashCode();
            }
            return result;
        }
    }

    private class ClassKeyWithOrdinal {
        public String slotName;
        public String shift;
        public String subjectCode;
        public int ordinalNumber;
        public Boolean is10Week;

        public ClassKeyWithOrdinal(String slotName, String shift, String subjectCode, int ordinalNumber, boolean is10Week) {
            this.slotName = slotName;
            this.shift = shift;
            this.subjectCode = subjectCode;
            this.ordinalNumber = ordinalNumber;
            this.is10Week = is10Week;
        }

        public ClassKeyWithOrdinal(ClassKey classKey, int ordinalNumber) {
            this.slotName = classKey.slotName;
            this.shift = classKey.shift;
            this.subjectCode = classKey.subjectCode;
            this.ordinalNumber = ordinalNumber;
            this.is10Week = classKey.is10Week;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ClassKeyWithOrdinal that = (ClassKeyWithOrdinal) o;

            if (!slotName.equalsIgnoreCase(that.slotName)) return false;
            if (!shift.equalsIgnoreCase(that.shift)) return false;
            if (ordinalNumber != that.ordinalNumber) return false;
            if (is10Week != that.is10Week) return false;
            return subjectCode.equalsIgnoreCase(that.subjectCode);
        }

        @Override
        public int hashCode() {
            int result = slotName.hashCode();
            result = 31 * result + shift.hashCode();
            result = 31 * result + subjectCode.hashCode();
            result = 31 * result + is10Week.hashCode();
            result = 31 * result + ordinalNumber;
            return result;
        }
    }
}