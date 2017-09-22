package com.capstone.controllers;

import com.capstone.models.ReadAndSaveFileToServer;
import com.capstone.services.*;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import com.capstone.entities.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class UploadController {

    private final String folder = "DSSV-StudentsList";
    private final String marksFolder = "Marks-StudentMarks";
    private int totalLine;
    private int currentLine;

    @Autowired
    ServletContext context;

    IStudentService studentService = new StudentServiceImpl();
    ISubjectService subjectService = new SubjectServiceImpl();
    IRealSemesterService realSemesterService = new RealSemesterServiceImpl();
    ISubjectMarkComponentService subjectMarkComponentService = new SubjectMarkComponentServiceImpl();
    ICourseService courseService = new CourseServiceImpl();
    IMarksService marksService = new MarksServiceImpl();

    /** --------------STUDENTS------------ **/
    @RequestMapping(value = "/goUploadStudentList")
    public ModelAndView goUploadStudentListPage() {
        ModelAndView view = new ModelAndView("uploadStudentList");

        ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
        File[] list = read.readFiles(context, folder);
        view.addObject("files", list);
        return view;
    }

    @RequestMapping("/getlinestatus")
    @ResponseBody
    public JsonObject getCurrentLine() {
        JsonObject obj = new JsonObject();
        obj.addProperty("current", studentService.getCurrentLine());
        obj.addProperty("total", studentService.getTotalLine());
        return obj;
    }

    @RequestMapping(value = "/uploadStudentExistFile", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject chooseExistFile(@RequestParam("file") String file){
        JsonObject obj;
        try {
            File f = new File(context.getRealPath("/") + "UploadedFiles/" + folder + "/" + file);
            obj = ReadFile(null, f, false);
        } catch (Exception e) {
            obj = new JsonObject();
            obj.addProperty("success", false);
            obj.addProperty("message", e.getMessage());
        }

        return obj;
    }

    @RequestMapping(value = "/uploadStudentList", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject uploadFile(@RequestParam("file") MultipartFile file){
        JsonObject obj = ReadFile(file, null, true);
        if (obj.get("success").getAsBoolean()) {
            ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
            read.saveFile(context, file, folder);
        }

        return obj;
    }

    private JsonObject ReadFile(MultipartFile file, File file2, boolean isNewFile) {
        JsonObject obj = new JsonObject();

        try {
            InputStream is;
            if (isNewFile) {
                is = file.getInputStream();
            }
            else {
                is = new FileInputStream(file2);
            }

            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);

            XSSFRow row;
            int rollNumberIndex = 1;
            int studentNameIndex = 2;
            int excelDataIndex = 3;
            List<StudentEntity> students = new ArrayList<>();

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

    /** --------------MARKS------------ **/
    @RequestMapping(value = "/goUploadStudentMarks")
    public ModelAndView goUploadStudentMarksPage() {
        ModelAndView view = new ModelAndView("uploadStudentMarks");

        ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
        File[] list = read.readFiles(context, marksFolder);
        view.addObject("files", list);
        return view;
    }

    @RequestMapping(value = "/upload-exist-marks-file", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject chooseExistMarkFile(@RequestParam("file") String file) {
        JsonObject jsonObject;
        try {
            File f = new File(context.getRealPath("/") + "UploadedFiles/" + marksFolder + "/" + file);
            jsonObject = readMarkFile(null, f, false);
        } catch (Exception ex) {
            jsonObject = new JsonObject();
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", ex.getMessage());
        }

        return jsonObject;
    }

    @RequestMapping(value = "/uploadStudentMarks", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject uploadStudentMarks(@RequestParam("file") MultipartFile file) throws IOException {
        JsonObject jsonObject = readMarkFile(file, null, true);
        if (jsonObject.get("success").getAsBoolean()) {
            ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
            read.saveFile(context, file, marksFolder);
        }

        return jsonObject;
    }

    private JsonObject readMarkFile(MultipartFile file, File file2, boolean isNewFile) {
        JsonObject jsonObject = new JsonObject();
        List<MarksEntity> marksEntities = new ArrayList<MarksEntity>();

        try {
            InputStream is;
            if (isNewFile) {
                is = file.getInputStream();
            }
            else {
                is = new FileInputStream(file2);
            }

            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);

            this.totalLine = spreadsheet.getLastRowNum();

            XSSFRow row;
            int excelDataIndex = 1;
            int semesterNameIndex = 0;
            int rollNumberIndex = 1;
            int subjectCodeIndex = 2;
            int classNameIndex = 3;
            int averageMarkIndex = 4;
            int statusIndex = 5;

            this.currentLine = excelDataIndex;
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
                            cla = cla.substring(0, cla.indexOf("_") < 0 ? cla.length() : cla.indexOf("_"));
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
                ++this.currentLine;
            }
            is.close();
            marksService.createMarks(marksEntities);
        } catch(Exception ex) {
            ex.printStackTrace();
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", ex.getMessage());
            return jsonObject;
        }

        jsonObject.addProperty("success", true);
        return jsonObject;
    }

    @RequestMapping("/marks/getStatus")
    @ResponseBody
    public JsonObject GetLineStatus() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("totalLine", this.totalLine);
        jsonObject.addProperty("currentLine", this.currentLine);
        jsonObject.addProperty("totalExistMarks", marksService.getTotalExistMarks());
        jsonObject.addProperty("successSavedMark", marksService.getSuccessSavedMark());
        return jsonObject;
    }
}
