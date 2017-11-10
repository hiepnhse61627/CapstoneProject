package com.capstone.controllers;

import com.capstone.entities.StudentEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.models.ReadAndSaveFileToServer;
import com.capstone.models.Suggestion;
import com.capstone.services.IStudentService;
import com.capstone.services.ISubjectService;
import com.capstone.services.StudentServiceImpl;
import com.capstone.services.SubjectServiceImpl;
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

@Controller
public class StudentArrangementController {
    @Autowired
    ServletContext context;

    private final String xlsExcelExtension = "xls";
    private final String xlsxExcelExtension = "xlsx";
    private final String folder = "DSSV-StudentsList";

    @RequestMapping("/studentArrangement")
    public ModelAndView StudentArrangementIndex() {
        ModelAndView view = new ModelAndView("StudentArrangement");
        view.addObject("title", "Danh sách sinh viên theo lớp môn");

        return view;
    }

    @RequestMapping("/studentArrangement/loadTable")
    @ResponseBody
    public JsonObject LoadStudentArrangementTable(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();

        try {
            List<List<String>> result = new ArrayList<>();
            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

            jsonObj.addProperty("iTotalRecords", result.size());
            jsonObj.addProperty("iTotalDisplayRecords", result.size());
            jsonObj.add("aaData", aaData);
            jsonObj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
    }

    @RequestMapping(value = "/studentArrangement/import", method = RequestMethod.POST)
    @ResponseBody
    public Callable<JsonObject> upload(@RequestParam("file") MultipartFile file) {
        Callable<JsonObject> callable = new Callable<JsonObject>() {
            @Override
            public JsonObject call() throws Exception {
                JsonObject obj = ReadFile(file, null, true);
                if (obj.get("success").getAsBoolean()) {
                    ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
                    read.saveFile(context, file, folder);
                }

                return obj;
            }
        };

        return callable;
    }

    private JsonObject ReadFile(MultipartFile file, File file2, boolean isNewFile) {
        JsonObject jsonObj = new JsonObject();
        IStudentService studentService = new StudentServiceImpl();
        ISubjectService subjectService = new SubjectServiceImpl();
        StudentDetail studentDetailController = new StudentDetail();

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

            Pattern pattern = Pattern.compile("\\d+");

            // Map<Shift, Map<SubjectCode, StudentList>>
            Map<String, Map<String, StudentList>> shiftMap = new HashMap<>();
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
                        StudentEntity student = studentService.findStudentByRollNumber(rollNumber);
                        if (student != null) {
                            List<List<String>> subjectList = null;
                            if (status.equals("HD")) {
                                subjectList = studentDetailController.processNext(student.getId(), false, false);
                            } else if (status.equals("HL")) {
                                Suggestion suggestion = studentDetailController.processSuggestion(student.getId());
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
                            }

                            Matcher matcher = pattern.matcher(clazz);
                            matcher.find();
                            int classNumber = Integer.parseInt(clazz.substring(matcher.start(1), clazz.length()));
                            String shift = classNumber % 2 == 0 ? "PM" : "AM";

                            Map<String, StudentList> subjectMap = shiftMap.get(shift);
                            if (subjectMap == null) {
                                shiftMap.put(shift, new HashMap<>());
                            }

                            for (List<String> subject : subjectList) {
                                String subjectCode = subject.get(0);
                                StudentList studentList = subjectMap.get(subjectCode);
                                if (studentList == null) {
                                    subjectMap.put(subjectCode, new StudentList());
                                }

                                if (status.equals("HD")) {
                                    studentList.goingList.add(student);
                                } else if (status.equals("HL")) {
                                    studentList.relearnList.add(student);
                                }
                            }
                        }
                    }
                }
            }

            List<List<String>> result = new ArrayList<>();
            int classNumber = 1;
            int count = 0;
            for (String shift : shiftMap.keySet()) {
                Map<String, StudentList> subjectMap = shiftMap.get(shift);
                for (String subjectCode : subjectMap.keySet()) {
                    StudentList studentList = subjectMap.get(subjectCode);
                    SubjectEntity subject = subjectService.findSubjectById(subjectCode);

                    List<String> dataRow;
                    for (StudentEntity student : studentList.goingList) {
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

                    for (StudentEntity student : studentList.relearnList) {
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

            jsonObj.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
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
