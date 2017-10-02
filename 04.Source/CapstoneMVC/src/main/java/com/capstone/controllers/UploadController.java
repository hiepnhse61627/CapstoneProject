package com.capstone.controllers;

import com.capstone.models.ReadAndSaveFileToServer;
import com.capstone.services.*;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
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

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class UploadController {

    private final String xlsExcelExtension = "xls";
    private final String xlsxExcelExtension = "xlsx";
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

    /**
     * --------------STUDENTS------------
     **/
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
    public JsonObject chooseExistFile(@RequestParam("file") String file) {
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
    public JsonObject uploadFile(@RequestParam("file") MultipartFile file) {
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
            InputStream is = isNewFile ? file.getInputStream() : new FileInputStream(file2);

            String originalFileName = isNewFile ? file.getOriginalFilename() : file2.getName();
            String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1, originalFileName.length());

            List<StudentEntity> students = new ArrayList<>();

            if (extension.equals(xlsExcelExtension)) {
                HSSFWorkbook workbook = new HSSFWorkbook(is);
                HSSFSheet spreadsheet = workbook.getSheetAt(0);

                HSSFRow row;
                int excelDataIndex = 0;
                int rollNumberIndex = 0;
                int studentNameIndex = 0;
                boolean rollNumberFlag = false;
                boolean studentNameFlag = false;

                for (int rowIndex = excelDataIndex; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
                    row = spreadsheet.getRow(rowIndex);
                    if (row != null) {
                        if (rollNumberFlag == false || studentNameFlag == false) {
                            for (int cellIndex = row.getFirstCellNum(); cellIndex <= row.getLastCellNum(); cellIndex++) {
                                Cell cell = row.getCell(cellIndex);
                                if (cell != null && cell.getStringCellValue().contains("MSSV")) {
                                    rollNumberIndex = cellIndex;
                                    rollNumberFlag = true;
                                } else if (cell != null && cell.getStringCellValue().contains("tên")) {
                                    studentNameIndex = cellIndex;
                                    studentNameFlag = true;
                                }
                            }
                            if (rollNumberFlag == true && studentNameFlag == true) {
                                rowIndex++;
                                row = spreadsheet.getRow(rowIndex);
                            }
                        }
                        System.out.println(rowIndex);
                        if (rollNumberFlag == true && studentNameFlag == true) {
                            StudentEntity student = new StudentEntity();
                            Cell rollNumberCell = row.getCell(rollNumberIndex);
                            Cell studentNameCell = row.getCell(studentNameIndex);
                            if (rollNumberCell != null) {
                                String rollNumber = rollNumberCell.getCellType() == Cell.CELL_TYPE_STRING ?
                                        rollNumberCell.getStringCellValue() : (rollNumberCell.getNumericCellValue() == 0 ?
                                        "" : Integer.toString((int)rollNumberCell.getNumericCellValue()));
                                if (!rollNumber.isEmpty()) {
                                    student.setRollNumber(rollNumber);
                                }
                            }
                            if (studentNameCell != null && !studentNameCell.getStringCellValue().isEmpty()) {
                                student.setFullName(studentNameCell.getStringCellValue());
                            }

                            if (student.getRollNumber() != null) {
                                students.add(student);
                            }
                        }
                    }
                }
            } else if (extension.equals(xlsxExcelExtension)) {
                XSSFWorkbook workbook = new XSSFWorkbook(is);
                XSSFSheet spreadsheet = workbook.getSheetAt(0);

                XSSFRow row;

                int rollNumberIndex = 1;
                int studentNameIndex = 2;
                int excelDataIndex = 3;

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
            } else {
                obj.addProperty("success", false);
                obj.addProperty("message", "Chỉ chấp nhận file excel");
                return obj;
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

    /**
     * --------------MARKS------------
     **/
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
            } else {
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
                    StudentEntity studentEntity = studentService.findStudentByRollNumber(rollNumberCell.getStringCellValue().trim());
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
                            }
                        }

                        if (subjectCodeCell != null) {
                            SubjectMarkComponentEntity subjectMarkComponentEntity =
                                    subjectMarkComponentService.findSubjectMarkComponentById(subjectCodeCell.getStringCellValue().toUpperCase());
                            if (subjectMarkComponentEntity != null) {
                                marksEntity.setSubjectId(subjectMarkComponentEntity);
                            }
                        }

                        if (classNameCell != null && subjectCodeCell != null) {
                            String cla = classNameCell.getStringCellValue();
                            String subjectCd = subjectCodeCell.getStringCellValue();
                            CourseEntity courseEntity = courseService.findCourseByClassAndSubjectCode(cla.toLowerCase(), subjectCd.toLowerCase());
                            if (courseEntity != null) {
                                marksEntity.setCourseId(courseEntity);
                            }
                        }

                        if (averageMarkCell != null) {
                            marksEntity.setAverageMark(averageMarkCell.getNumericCellValue());
                        }

                        if (statusCell != null) {
                            marksEntity.setStatus(statusCell.getStringCellValue());
                        }
                        // Add mark entity to list
                        marksEntities.add(marksEntity);
                    }
                }
                ++this.currentLine;
            }
            is.close();
            // Check student same semester, same subject but in different class
            for (int i = 0; i < marksEntities.size(); i++) {
                MarksEntity current = marksEntities.get(i);
                for (int j = i + 1; j < marksEntities.size(); j++) {
                    MarksEntity next = marksEntities.get(j);
                    if (current.getSubjectId() != null && next.getSubjectId() != null) {
                        if ((current.getSemesterId().getSemester().toUpperCase().equals(next.getSemesterId().getSemester().toUpperCase()))
                                && (current.getStudentId().getRollNumber().toUpperCase().equals(next.getStudentId().getRollNumber().toUpperCase()))
                                && (current.getSubjectId().getSubjectId().toUpperCase().equals(next.getSubjectId().getSubjectId().toUpperCase()))
                                && (current.getAverageMark().toString().toUpperCase().equals(next.getAverageMark().toString().toUpperCase()))) { // found
                            System.out.println("SEMESTER: " + current.getSemesterId().getSemester().toUpperCase() + "\t\t" + next.getSemesterId().getSemester().toUpperCase());
                            System.out.println("SUBJECT_ID: " + current.getSubjectId().getSubjectId().toUpperCase() + "\t\t" + next.getSubjectId().getSubjectId().toUpperCase());
                            System.out.println("AVERAGE_MARK: " + current.getAverageMark() + "\t\t"  + next.getAverageMark());
                            System.out.println("-------------------------------------------------------------------------------------------------------------------------");
                            if (current.getCourseId() != null && next.getCourseId() != null) {
                                if (current.getCourseId().getClass1().toUpperCase().contains("_SPRING")
                                        || current.getCourseId().getClass1().toUpperCase().contains("_FALL")
                                        || current.getCourseId().getClass1().toUpperCase().contains("_SUMMER")) {
                                    marksEntities.remove(i);
                                } else if (next.getCourseId().getClass1().toUpperCase().contains("_SPRING")
                                        || next.getCourseId().getClass1().toUpperCase().contains("_FALL")
                                        || next.getCourseId().getClass1().toUpperCase().contains("_SUMMER")) {
                                    marksEntities.remove(j);
                                }
                            }
                        }
                    }
                }
            }
            marksService.createMarks(marksEntities);
        } catch (Exception ex) {
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

    /**
     * --------------COURSE------------
     **/
    @RequestMapping(value = "/goUploadCoursePage")
    public ModelAndView goUploadCoursePage() {
        ModelAndView view = new ModelAndView("uploadCourse");

        ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
        File[] list = read.readFiles(context, folder);
        view.addObject("files", list);
        return view;
    }

    @RequestMapping("/getCourseStatus")
    @ResponseBody
    public JsonObject getCourseCurrentLine() {
        JsonObject obj = new JsonObject();
        obj.addProperty("current", studentService.getCurrentLine());
        obj.addProperty("total", studentService.getTotalLine());
        return obj;
    }

    @RequestMapping(value = "/uploadCourseExistFile", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject chooseExistCourseFile(@RequestParam("file") String file) {
        JsonObject obj;
        try {
            File f = new File(context.getRealPath("/") + "UploadedFiles/" + folder + "/" + file);
            obj = ReadCourseFile(null, f, false);
        } catch (Exception e) {
            obj = new JsonObject();
            obj.addProperty("success", false);
            obj.addProperty("message", e.getMessage());
        }

        return obj;
    }

    @RequestMapping(value = "/uploadCourse", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject uploadCourseFile(@RequestParam("file") MultipartFile files) {
        JsonObject obj = ReadCourseFile(files, null, true);
        if (obj.get("success").getAsBoolean()) {
            ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
            read.saveFile(context, files, folder);
        }

        return obj;
    }

    private static int findRow(XSSFSheet sheet, String cellContent) {
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                    if (cell.getRichStringCellValue().getString().trim().equals(cellContent)) {
                        return row.getRowNum();
                    }
                }
            }
        }
        return 0;
    }

    private static boolean checkCell(XSSFCell c) {
        if (c.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return false;
        }
        return true;
    }


    private JsonObject ReadCourseFile(MultipartFile file, File file2, boolean isNewFile) {
        JsonObject obj = new JsonObject();

        try {
            InputStream is;
            if (isNewFile) {
                is = file.getInputStream();
            } else {
                is = new FileInputStream(file2);
            }

            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);

            XSSFRow row;
            int classIndex = 0;
            int startDateIndex = 0;
            int endDateIndex = 0;
            int subjectCodeIndex = 0;
            int excelDataIndex = 0;
            int checkIndex = 0;
            int dataStartIndex = 0;

            List<CourseEntity> courses = new ArrayList<>();
            List<CourseEntity> uniqueCourses = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);

            //get header data row index
            excelDataIndex = findRow(spreadsheet, "Lớp");
            row = spreadsheet.getRow(excelDataIndex);

            //get data start index
            for (dataStartIndex = excelDataIndex; dataStartIndex <= spreadsheet.getLastRowNum(); dataStartIndex++) {
                boolean flag = false;
                for (int curCellIndex = 0; curCellIndex <= row.getLastCellNum(); curCellIndex++) {
                    if (row.getCell(curCellIndex).getStringCellValue().toString().equals("Lớp")) {
                        checkIndex = curCellIndex;
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    break;
                }
            }

            for (int conRowIndex = excelDataIndex + 1; conRowIndex <= spreadsheet.getLastRowNum(); conRowIndex++) {
                if (checkCell(spreadsheet.getRow(conRowIndex).getCell(checkIndex)) == true) {
                    dataStartIndex = conRowIndex;
                    break;
                }
            }

            row = spreadsheet.getRow(excelDataIndex);
            for (int cellIndex = 0; cellIndex <= row.getLastCellNum(); cellIndex++) {
                if (row.getCell(cellIndex).getStringCellValue().toString().equals("Lớp")) {
                    classIndex = cellIndex;
                }
                if (row.getCell(cellIndex).getStringCellValue().toString().equals("Ngày \n" + "bắt đầu")) {
                    startDateIndex = cellIndex;
                }
                if (row.getCell(cellIndex).getStringCellValue().toString().equals("Ngày \n" + "kết thúc")) {
                    endDateIndex = cellIndex;
                }
                if (row.getCell(cellIndex).getStringCellValue().toString().equals("Mã môn")) {
                    subjectCodeIndex = cellIndex;
                }
                if (classIndex != 0 && startDateIndex != 0 && endDateIndex != 0 && subjectCodeIndex != 0) break;
            }
            if (classIndex == 0 && startDateIndex == 0 && endDateIndex == 0 && subjectCodeIndex == 0) {

            } else {

                for (int rowIndex = dataStartIndex; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
                    row = spreadsheet.getRow(rowIndex);

                    if (row != null) {
                        CourseEntity course = new CourseEntity();
                        Cell classCell = row.getCell(classIndex);
                        Cell startDateCell = row.getCell(startDateIndex);
                        Cell endDateCell = row.getCell(endDateIndex);
                        Cell subjectCell = row.getCell(subjectCodeIndex);
                        if (classCell != null && startDateCell != null && endDateCell != null && subjectCell != null) {
                            if (classCell != null) {
                                if (classCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                                    System.out.println("Class Num ---- " + classCell.getNumericCellValue());
                                    course.setClass1(String.valueOf(classCell.getNumericCellValue()));
                                }else{
                                    System.out.println("Class String ----" + classCell.getStringCellValue());
                                    course.setClass1(classCell.getStringCellValue());
                                }
                            }
                            if (startDateCell != null) {
                                course.setStartDate(sdf.parse(String.valueOf(startDateCell.getDateCellValue())));
                            }
                            if (endDateCell != null) {
                                course.setEndDate(sdf.parse(String.valueOf(endDateCell.getDateCellValue())));
                            }
                            if (subjectCell != null) {
                                if (subjectCell.getCellType() == Cell.CELL_TYPE_NUMERIC){
                                    System.out.println("Subject Num ------" + subjectCell.getNumericCellValue());
                                    course.setSubjectCode(String.valueOf(subjectCell.getNumericCellValue()));
                                }else{
                                    System.out.println("Subject String ----" + subjectCell.getStringCellValue());
                                    course.setSubjectCode(subjectCell.getStringCellValue());
                                }

                            }

                            if (course.getClass1() != null) {
                                courses.add(course);
                            }
                        }
                    }
                }

                System.out.println("All Course Added");
                for (CourseEntity element : courses) {
                    if (!uniqueCourses.contains(element)) {
                        uniqueCourses.add(element);
                    }
                }

                courseService.createCourseList(uniqueCourses);
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj.addProperty("success", false);
            obj.addProperty("message", e.getMessage());
            return obj;
        }

        obj.addProperty("success", true);
        return obj;
    }
}

