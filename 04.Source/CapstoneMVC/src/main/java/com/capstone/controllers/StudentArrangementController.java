package com.capstone.controllers;

import com.capstone.entities.RealSemesterEntity;
import com.capstone.entities.StudentEntity;
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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @RequestMapping("/studentArrangement")
    public ModelAndView StudentArrangementIndex() {
        ModelAndView view = new ModelAndView("StudentArrangement");
        view.addObject("title", "Danh sách sinh viên theo lớp môn");

        return view;
    }

    @RequestMapping("/studentArrangement/loadTable")
    @ResponseBody
    public JsonObject LoadStudentArrangementTable(@RequestParam Map<String, String> params, HttpServletRequest request) {
        JsonObject jsonObj = new JsonObject();

        int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
        int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));

        try {
            List<List<String>> studentList = (List<List<String>>) request.getSession().getAttribute("studentArrangementList");
            if (studentList == null) {
                studentList = new ArrayList<>();
            }
            List<List<String>> result = studentList.stream().skip(iDisplayStart).limit(iDisplayLength).collect(Collectors.toList());
            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

            jsonObj.addProperty("iTotalRecords", studentList.size());
            jsonObj.addProperty("iTotalDisplayRecords", studentList.size());
            jsonObj.add("aaData", aaData);
            jsonObj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
    }

    @RequestMapping(value = "/studentArrangement/import", method = RequestMethod.POST)
    @ResponseBody
    public Callable<JsonObject> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Callable<JsonObject> callable = new Callable<JsonObject>() {
            @Override
            public JsonObject call() throws Exception {
                JsonObject obj = ReadFile(file, null, true, request);
                if (obj.get("success").getAsBoolean()) {
                    ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
                    read.saveFile(context, file, folder);
                }

                return obj;
            }
        };

        return callable;
    }

    private JsonObject ReadFile(MultipartFile file, File file2, boolean isNewFile, HttpServletRequest request) {
        JsonObject jsonObj = new JsonObject();
        IStudentService studentService = new StudentServiceImpl();
        ISubjectService subjectService = new SubjectServiceImpl();
        IRealSemesterService semesterService = new RealSemesterServiceImpl();
        StudentDetail studentDetailController = new StudentDetail();

        this.totalStudents = 0;
        this.countStudents = 0;

        List<RealSemesterEntity> sortedSemester = Ultilities.SortSemesters(semesterService.getAllSemester());
        String lastestSemester = sortedSemester.get(sortedSemester.size() - 1).getSemester();

        try {
            InputStream is = isNewFile ? file.getInputStream() : new FileInputStream(file2);

            String originalFileName = isNewFile ? file.getOriginalFilename() : file2.getName();
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
            } else {
                jsonObj.addProperty("success", false);
                jsonObj.addProperty("message", "Chỉ chấp nhận file excel");
                return jsonObj;
            }

            int excelDataIndexRow = 1;

            int rollNumberIndex = 0;
            int termIndex = 16;
            int classIndex = 17;
            int statusIndex = 19;

            List<StudentModel> studentList = new ArrayList<>();
            for (int rowIndex = excelDataIndexRow; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
                row = spreadsheet.getRow(rowIndex);
                if (row != null) {
                    Cell rollNumberCell = row.getCell(rollNumberIndex);
                    Cell classCell = row.getCell(classIndex);
                    Cell statusCell = row.getCell(statusIndex);

                    String rollNumber = "";
                    if (rollNumberCell != null) {
                        rollNumber = rollNumberCell.getCellType() == Cell.CELL_TYPE_STRING ?
                                rollNumberCell.getStringCellValue() : (rollNumberCell.getNumericCellValue() == 0 ?
                                "" : Integer.toString((int) rollNumberCell.getNumericCellValue()));
                    }

                    String clazz = "";
                    if (classCell != null) {
                        clazz = classCell.getStringCellValue().trim();
                    }

                    String status = "";
                    if (statusCell != null) {
                        status = statusCell.getStringCellValue().trim();
                    }

                    if (!rollNumber.isEmpty()
                            && (status.equals("HD") || status.equals("HL"))
                            && !clazz.isEmpty() && clazz.matches("^[a-zA-Z]+\\d+$")) {
                        StudentModel student = new StudentModel();
                        student.rollNumber = rollNumber;
                        student.clazz = clazz;
                        student.status = status;

                        studentList.add(student);
                    }
                }
            }

            this.totalStudents = studentList.size();

            Pattern pattern = Pattern.compile("\\d+");
            // Map<Shift, Map<SubjectCode, StudentList>>
            Map<String, Map<String, StudentList>> shiftMap = new HashMap<>();
            for (StudentModel std : studentList) {
                StudentEntity student = studentService.findStudentByRollNumber(std.rollNumber);
                if (student != null) {
                    List<List<String>> subjectList = null;
                    Suggestion suggestion = studentDetailController.processSuggestion(student.getId(),
                            lastestSemester);
                    subjectList = suggestion.getData();
                    List<String> brea = new ArrayList<>();
                    brea.add("break");
                    brea.add("");

                    int index = subjectList.indexOf(brea);
                    if (index > -1) {
                        if (suggestion.isDuchitieu()) {
                            subjectList = subjectList.subList(0, index);
                        } else {
                            subjectList = subjectList.subList(index + 1, subjectList.size());
                        }
                    }

                    Matcher matcher = pattern.matcher(std.clazz);
                    matcher.find();
                    int classNumber = Integer.parseInt(std.clazz.substring(matcher.start(), std.clazz.length()));
                    String shift = classNumber % 2 == 0 ? "PM" : "AM";

                    Map<String, StudentList> subjectMap = shiftMap.get(shift);
                    if (subjectMap == null) {
                        subjectMap = new HashMap<>();
                        shiftMap.put(shift, subjectMap);
                    }

                    for (List<String> subject : subjectList) {
                        String subjectCode = subject.get(0);
                        StudentList list = subjectMap.get(subjectCode);
                        if (list == null) {
                            list = new StudentList();
                            subjectMap.put(subjectCode, list);
                        }

                        if (std.status.equals("HD")) {
                            list.goingList.add(student);
                        } else if (std.status.equals("HL")) {
                            list.relearnList.add(student);
                        }
                    }
                }
                ++this.countStudents;
                System.out.println(countStudents);
            }

            List<List<String>> result = new ArrayList<>();
            int classNumber = 0;
            int count;
            for (String shift : shiftMap.keySet()) {
                Map<String, StudentList> subjectMap = shiftMap.get(shift);
                for (String subjectCode : subjectMap.keySet()) {
                    count = 0;
                    classNumber++;
                    StudentList list = subjectMap.get(subjectCode);
                    SubjectEntity subject = subjectService.findSubjectById(subjectCode);

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

                        count++;
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

                        count++;
                    }
                }
            }

            request.getSession().setAttribute("studentArrangementList", result);

            jsonObj.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
    }

    @RequestMapping(value = "/studentArrangement/updateProgress")
    @ResponseBody
    public JsonObject UpdateProgress() {
        JsonObject jsonObj = new JsonObject();

        jsonObj.addProperty("total", this.totalStudents);
        jsonObj.addProperty("count", this.countStudents);

        return jsonObj;
    }

    private class StudentModel {
        public String rollNumber;
        public String clazz;
        public String status;

    }

    private class StudentList {
        public List<StudentEntity> goingList;
        public List<StudentEntity> relearnList;

        public StudentList() {
            goingList = new ArrayList<>();
            relearnList = new ArrayList<>();
        }
    }

}
