package com.capstone.controllers;

import com.capstone.entities.RealSemesterEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.entities.StudentStatusEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.models.ReadAndSaveFileToServer;
import com.capstone.models.Suggestion;
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

import javax.security.auth.Subject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class StudentArrangementController {
    @Autowired
    ServletContext context;

    private final String xlsExcelExtension = "xls";
    private final String xlsxExcelExtension = "xlsx";
    private final String folder = "DSSV-XepLop";

    private int totalStudents;
    private int countStudents;
    private boolean file1Done;
    private boolean file2Done;
    private boolean file3Done;

    @RequestMapping("/studentArrangement")
    public ModelAndView StudentArrangementIndex() {
        ModelAndView view = new ModelAndView("StudentArrangement");
        view.addObject("title", "Danh sách sinh viên theo lớp môn");

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

    @RequestMapping(value = "/studentArrangement/import1", method = RequestMethod.POST)
    @ResponseBody
    public Callable<JsonObject> importFile1(
            @RequestParam("file-suggestion") MultipartFile fileSuggestion, HttpServletRequest request) {
        Callable<JsonObject> callable = new Callable<JsonObject>() {
            @Override
            public JsonObject call() throws Exception {
                JsonObject obj = ReadFile(fileSuggestion, request);;

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
                JsonObject obj = ReadFile(fileSuggestion, fileGoing, fileRelearn, semesterId, request);;

                return obj;
            }
        };

        return callable;
    }

    private JsonObject ReadFile(MultipartFile fileSuggestion, HttpServletRequest request) {
        JsonObject jsonObj = new JsonObject();
        IStudentService studentService = new StudentServiceImpl();
        ISubjectService subjectService = new SubjectServiceImpl();

        this.totalStudents = 0;
        this.countStudents = 0;
        this.file1Done = false;

        try {
            List<StudentEntity> students = studentService.findAllStudents();
            Map<String, StudentEntity> studentMap = new HashMap<>();
            for (StudentEntity s : students) {
                studentMap.put(s.getRollNumber(), s);
            }

            Map<String, SubjectList> studentSubjectSuggestion = this.getSubjectSuggestionList(fileSuggestion);

            totalStudents = studentSubjectSuggestion.keySet().size();

            Map<String, Map<String, List<StudentEntity>>> shiftMap = new HashMap<>();
            shiftMap.put("AM", new HashMap<>());
            shiftMap.put("PM", new HashMap<>());

            int count = 0;
            for (String rollNumber : studentSubjectSuggestion.keySet()) {
                StudentEntity curStudent = studentMap.get(rollNumber);
                if (curStudent != null) {
                    SubjectList subjectList = studentSubjectSuggestion.get(rollNumber);
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
                }
                countStudents++;
            }

            int classNumber = 0;
            List<List<String>> result = new ArrayList<>();
            for (String shift : shiftMap.keySet()) {
                Map<String, List<StudentEntity>> subjectMap = shiftMap.get(shift);
                for (String subjectCode : subjectMap.keySet()) {
                    count = 0;
                    classNumber++;

                    List<StudentEntity> studentList = subjectMap.get(subjectCode);
                    Collections.sort(studentList, new RollNumberComparator());

                    SubjectEntity subject = subjectService.findSubjectById(subjectCode);

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

    private JsonObject ReadFile(MultipartFile fileSuggestion, MultipartFile fileGoing,
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
            Map<String, SubjectList> studentSubjectSuggestion = this.getSubjectSuggestionList(fileSuggestion);
            List<String> goingList = this.getGoingListAlreadyPaying(fileGoing);
            Map<String, List<String>> relearnList = this.getRelearnListAlreadyPaying(fileRelearn);

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

            Map<String, Map<String, StudentList>> shiftMap = new HashMap<>();
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

    private Map<String, SubjectList> getSubjectSuggestionList(MultipartFile file) {
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
                            nextCourseList = this.removeVOVInSubjectList(nextCourseList);
                        }

                        List<String> suggestionList = this.changeSubjectStringToList(subjectsSuggestionStr);
                        if (!suggestionList.isEmpty()) {
                            suggestionList = this.removeVOVInSubjectList(suggestionList);
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

    private List<String> getGoingListAlreadyPaying(MultipartFile file) {
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

    private Map<String, List<String>> getRelearnListAlreadyPaying(MultipartFile file) {
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
                            subjectList = this.removeVOVInSubjectList(subjectList);
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

    private List<String> removeVOVInSubjectList(List<String> subjectList) {
        int pos;
        do {
            pos = -1;
            for (int i = 0; i < subjectList.size(); ++i) {
                if (subjectList.get(i).contains("VOV")) {
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

    @RequestMapping(value = "/studentArrangement/updateProgress")
    @ResponseBody
    public JsonObject UpdateProgress() {
        JsonObject jsonObj = new JsonObject();

        jsonObj.addProperty("total", this.totalStudents);
        jsonObj.addProperty("count", this.countStudents);
        jsonObj.addProperty("file1Done", this.file1Done);
        jsonObj.addProperty("file2Done", this.file2Done);
        jsonObj.addProperty("file3Done", this.file3Done);

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

}
