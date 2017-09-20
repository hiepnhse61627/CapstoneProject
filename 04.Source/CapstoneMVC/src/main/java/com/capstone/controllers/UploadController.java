package com.capstone.controllers;

import com.capstone.services.*;
import com.google.gson.JsonObject;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import com.capstone.entities.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class UploadController {

    IStudentService studentService = new StudentServiceImpl();
    ISubjectService subjectService = new SubjectServiceImpl();
    IRealSemesterService realSemesterService = new RealSemesterServiceImpl();
    ISubjectMarkComponentService subjectMarkComponentService = new SubjectMarkComponentServiceImpl();
    ICourseService courseService = new CourseServiceImpl();
    IMarksService marksService = new MarksServiceImpl();

    @RequestMapping(value = "/goUploadStudentList")
    public String goUploadStudentListPage() {
        return "uploadStudentList";
    }

    @RequestMapping("/getlinestatus")
    @ResponseBody
    public JsonObject getCurrentLine() {
        JsonObject obj = new JsonObject();
        obj.addProperty("current", studentService.getCurrentLine());
        obj.addProperty("total", studentService.getTotalLine());
        return obj;
    }

    @RequestMapping(value = "/uploadStudentList", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject uploadFile(@RequestParam("file") MultipartFile file){
        JsonObject obj = new JsonObject();

        try {
            InputStream is = file.getInputStream();

            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);

            XSSFRow row;
            int rollNumberIndex = 1;
            int studentNameIndex = 2;
            int excelDataIndex = 3;
            List<StudentEntity> students = new ArrayList<StudentEntity>();

            for (int rowIndex = excelDataIndex; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
                row = spreadsheet.getRow(rowIndex);
                if (row != null) {
                    StudentEntity student = new StudentEntity();
                    Cell rollNumberCell = row.getCell(rollNumberIndex);
                    Cell studentNameCell = row.getCell(studentNameIndex);
                    if (rollNumberCell != null) {
                        System.out.println(rollNumberCell.getStringCellValue() + " \t\t ");
                        student.setRollNumber(rollNumberCell.getStringCellValue());
                    }
                    if (studentNameCell != null) {
                        System.out.println(studentNameCell.getStringCellValue());
                        student.setFullName(studentNameCell.getStringCellValue());
                    }

                    if (student.getRollNumber() != null) {
                        students.add(student);
                    }
                }
            }
            studentService.createStudentList(students);
        } catch (Exception e) {
            obj.addProperty("success", false);
            obj.addProperty("message", e.getMessage());
            return obj;
        }

        obj.addProperty("success", true);
        return obj;
    }

    @RequestMapping(value = "/goUploadStudentMarks")
    public String goUploadStudentMarksPage() {return "uploadStudentMarks";}

    @RequestMapping(value = "/uploadStudentMarks")
    public void uploadStudentMarks(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream is = file.getInputStream();

        XSSFWorkbook workbook = new XSSFWorkbook(is);
        XSSFSheet spreadsheet = workbook.getSheetAt(0);

        XSSFRow row;
        int excelDataIndex = 1;
        int semesterNameIndex = 0;
        int rollNumberIndex = 1;
        int subjectCodeIndex = 2;
        int classNameIndex = 3;
        int averageMarkIndex = 4;
        int statusIndex = 5;

        int count = 0;

        List<MarksEntity> marksEntities = new ArrayList<MarksEntity>();

        for (int rowIndex = excelDataIndex; rowIndex < spreadsheet.getLastRowNum(); rowIndex++) {
            row = spreadsheet.getRow(rowIndex);

            Cell rollNumberCell = row.getCell(rollNumberIndex);
            if (rollNumberCell != null) {
                StudentEntity studentEntity = studentService.findStudentByRollNumber(rollNumberCell.getStringCellValue());
                if (studentEntity != null) {
                    MarksEntity marksEntity = new MarksEntity();
                    marksEntity.setStudentId(studentEntity);

                    Cell semesterNameCell = row.getCell(semesterNameIndex);
                    Cell subjectCodeCell = row.getCell(subjectCodeIndex);
                    Cell classNameCell = row.getCell(classNameIndex);
                    Cell averageMarkCell = row.getCell(averageMarkIndex);
                    Cell statusCell = row.getCell(statusIndex);

                    if (semesterNameCell != null) {
                        RealSemesterEntity realSemesterEntity = realSemesterService.findSemesterByName(semesterNameCell.getStringCellValue().toUpperCase());
                        if (realSemesterEntity != null) {
                            marksEntity.setSemesterId(realSemesterEntity);
                        } else {
                            realSemesterEntity = new RealSemesterEntity();
                            realSemesterEntity.setSemester(semesterNameCell.getStringCellValue().toUpperCase());
                            realSemesterEntity = realSemesterService.createRealSemester(realSemesterEntity);

                            marksEntity.setSemesterId(realSemesterEntity);
                        }
                    }

                    if (subjectCodeCell != null) {
                        SubjectMarkComponentEntity subjectMarkComponentEntity =
                                subjectMarkComponentService.findSubjectMarkComponentById(subjectCodeCell.getStringCellValue().toUpperCase());
                        if (subjectMarkComponentEntity != null) {
                            marksEntity.setSubjectId(subjectMarkComponentEntity);
                        }
                    }

                    if (classNameCell != null) {
                        String cla = classNameCell.getStringCellValue();
                        cla = cla.substring(0, cla.indexOf("_") < 0 ? cla.length() - 1 : cla.indexOf("_") - 1);
                        CourseEntity courseEntity = courseService.findCourseByClass(cla.toUpperCase());
                        if (courseEntity != null) {
                            marksEntity.setCourseId(courseEntity);
                        } else {
                            courseEntity = new CourseEntity();
                            courseEntity.setClass1(cla.toUpperCase());
                            courseEntity = courseService.createCourse(courseEntity);

                            marksEntity.setCourseId(courseEntity);
                        }
                    }

                    if (averageMarkCell != null) {
                        marksEntity.setAverageMark(averageMarkCell.getNumericCellValue());
                    }

                    if (statusCell != null) {
                        marksEntity.setStatus(statusCell.getStringCellValue());
                    }

                    marksEntities.add(marksEntity);
                }
            }
        }
        marksService.createMarks(marksEntities);
    }
}
