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
import org.apache.poi.ss.util.CellAddress;
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
    private int selectedRowNumber;

    @Autowired
    ServletContext context;

    IStudentService studentService = new StudentServiceImpl();
    ISubjectService subjectService = new SubjectServiceImpl();
    IRealSemesterService realSemesterService = new RealSemesterServiceImpl();
    ISubjectMarkComponentService subjectMarkComponentService = new SubjectMarkComponentServiceImpl();
    ICourseService courseService = new CourseServiceImpl();
    ISubjectCurriculumService curriculumsService = new SubjectCurriculumServiceImpl();
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
                                        "" : Integer.toString((int) rollNumberCell.getNumericCellValue()));
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
    public JsonObject uploadStudentMarks(@RequestParam("file") MultipartFile file, @RequestParam("row") int row) throws IOException {
        selectedRowNumber = row;
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

            XSSFRow row;
            int excelDataIndex = selectedRowNumber;
            this.totalLine = spreadsheet.getLastRowNum() - excelDataIndex;

            int semesterNameIndex = 0;
            int rollNumberIndex = 1;
            int subjectCodeIndex = 2;
            int classNameIndex = 3;
            int averageMarkIndex = 4;
            int statusIndex = 5;

            this.currentLine = 1;
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

                        String semesterName = "";
                        if (semesterNameCell != null) {
                            semesterName = semesterNameCell.getStringCellValue().trim().toUpperCase();
                            RealSemesterEntity realSemesterEntity = realSemesterService.findSemesterByName(semesterNameCell.getStringCellValue().toUpperCase());
                            if (realSemesterEntity != null) {
                                marksEntity.setSemesterId(realSemesterEntity);
                            }
                        }

                        if (classNameCell != null && subjectCodeCell != null) {
                            String cla = classNameCell.getStringCellValue().trim();
                            String subjectCd = subjectCodeCell.getStringCellValue().trim();
                            // find subject mark component
                            SubjectMarkComponentEntity subjectMarkComponentEntity =
                                    subjectMarkComponentService.findSubjectMarkComponentById(subjectCd.toUpperCase());

                            if (subjectMarkComponentEntity != null) {
                                marksEntity.setSubjectId(subjectMarkComponentEntity);
                            }
                            // find course
                            CourseEntity courseEntity = courseService.findCourseByClassAndSubjectCode(cla.toUpperCase(), subjectCd.toUpperCase());
                            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                            if (courseEntity != null) {
                                marksEntity.setCourseId(courseEntity);
                            } else { // create new course entity
                                String semesterYear;
                                CourseEntity newCourse = new CourseEntity();
                                if (semesterName.contains("_H2")) {
                                    semesterYear = semesterName.substring(0, semesterName.indexOf("_")).replaceAll("[^0-9]", "");
                                    newCourse.setClass1(cla.toUpperCase());
                                    newCourse.setSubjectCode(subjectCd.toUpperCase());
                                    if (semesterName.contains("SPRING")) {
                                        newCourse.setStartDate(sdf.parse(String.valueOf(new Date("03/01/" + semesterYear))));
                                        newCourse.setEndDate(sdf.parse(String.valueOf(new Date("03/30/" + semesterYear))));
                                    } else if (semesterName.contains("SUMMER")) {
                                        newCourse.setStartDate(sdf.parse(String.valueOf(new Date("07/01/" + semesterYear))));
                                        newCourse.setEndDate(sdf.parse(String.valueOf(new Date("07/30/" + semesterYear))));
                                    } else if (semesterName.contains("FALL")) {
                                        newCourse.setStartDate(sdf.parse(String.valueOf(new Date("11/05/" + semesterYear))));
                                        newCourse.setEndDate(sdf.parse(String.valueOf(new Date("11/30/" + semesterYear))));
                                    }
                                } else {
                                    semesterYear = semesterName.replaceAll("[^0-9]", "");
                                    newCourse.setClass1(cla.toUpperCase());
                                    newCourse.setSubjectCode(subjectCd.toUpperCase());
                                    if (semesterName.contains("SPRING")) {
                                        newCourse.setStartDate(sdf.parse(String.valueOf(new Date("01/01/" + semesterYear))));
                                        newCourse.setEndDate(sdf.parse(String.valueOf(new Date("01/30/" + semesterYear))));
                                    } else if (semesterName.contains("SUMMER")) {
                                        newCourse.setStartDate(sdf.parse(String.valueOf(new Date("05/01/" + semesterYear))));
                                        newCourse.setEndDate(sdf.parse(String.valueOf(new Date("05/30/" + semesterYear))));
                                    } else if (semesterName.contains("FALL")) {
                                        newCourse.setStartDate(sdf.parse(String.valueOf(new Date("09/05/" + semesterYear))));
                                        newCourse.setEndDate(sdf.parse(String.valueOf(new Date("09/30/" + semesterYear))));
                                    }
                                }
                                newCourse = courseService.createCourse(newCourse);
                                marksEntity.setCourseId(newCourse);
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
                    if (current.getSubjectId() != null && current.getSemesterId() != null && next.getSemesterId() != null && next.getSubjectId() != null) {
                        if ((current.getSemesterId().getSemester().toUpperCase().equals(next.getSemesterId().getSemester().toUpperCase()))
                                && (current.getStudentId().getRollNumber().toUpperCase().equals(next.getStudentId().getRollNumber().toUpperCase()))
                                && (current.getSubjectId().getSubjectId().toUpperCase().equals(next.getSubjectId().getSubjectId().toUpperCase()))
                                && (current.getAverageMark().toString().toUpperCase().equals(next.getAverageMark().toString().toUpperCase()))) { // found
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
                                if (classCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    System.out.println("Class Num ---- " + classCell.getNumericCellValue());
                                    course.setClass1(String.valueOf(classCell.getNumericCellValue()));
                                } else {
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
                                if (subjectCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                    System.out.println("Subject Num ------" + subjectCell.getNumericCellValue());
                                    course.setSubjectCode(String.valueOf(subjectCell.getNumericCellValue()));
                                } else {
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
                    if (!uniqueCourses.stream().anyMatch(c -> c.getClass1().equals(element.getClass1())
                            && c.getSubjectCode().equals(element.getSubjectCode())
                            && c.getStartDate() == element.getStartDate()
                            && c.getEndDate() == element.getEndDate())) {
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

    /**
     * --------------CURRICULUM------------
     **/
    @RequestMapping(value = "/goUploadCurriculumPage")
    public ModelAndView goUploadCurriculumPage() {
        ModelAndView view = new ModelAndView("uploadCurriculum");

        ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
        File[] list = read.readFiles(context, folder);
        view.addObject("files", list);
        return view;
    }

    @RequestMapping("/getCurriculumStatus")
    @ResponseBody
    public JsonObject getCurriculumCurrentLine() {
        JsonObject obj = new JsonObject();
        obj.addProperty("current", studentService.getCurrentLine());
        obj.addProperty("total", studentService.getTotalLine());
        return obj;
    }

    @RequestMapping(value = "/uploadCurriculumExistFile", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject chooseExistCurriculumFile(@RequestParam("file") String file) {
        JsonObject obj;
        try {
            File f = new File(context.getRealPath("/") + "UploadedFiles/" + folder + "/" + file);
            obj = ReadCurriculumFile(null, f, false);
        } catch (Exception e) {
            obj = new JsonObject();
            obj.addProperty("success", false);
            obj.addProperty("message", e.getMessage());
        }

        return obj;
    }

    @RequestMapping(value = "/uploadCurriculum", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject uploadCurriculumFile(@RequestParam("file") MultipartFile files) {
        JsonObject obj = ReadCurriculumFile(files, null, true);
        if (obj.get("success").getAsBoolean()) {
            ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
            read.saveFile(context, files, folder);
        }

        return obj;
    }

    private static int findRowCurriculum(XSSFSheet sheet, String cellContent) {
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


    private JsonObject ReadCurriculumFile(MultipartFile file, File file2, boolean isNewFile) {
        JsonObject obj = new JsonObject();

//        try {
//            InputStream is;
//            if (isNewFile) {
//                is = file.getInputStream();
//            } else {
//                is = new FileInputStream(file2);
//            }
//
//            XSSFWorkbook workbook = new XSSFWorkbook(is);
//            XSSFSheet spreadsheet = workbook.getSheetAt(1);
//
//            XSSFRow row;
//            int excelDataIndex = 0;
//
//            String curriculumName = spreadsheet.getSheetName();
//
//            List<CurriculumMappingEntity> curriculums = new ArrayList<>();
//            List<CurriculumMappingEntity> uniqueCurriculum = new ArrayList<>();
////            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
//
//            //get header data row index
//            excelDataIndex = findRowCurriculum(spreadsheet, "Học kỳ 1");
//
//            for (int rowIndex = excelDataIndex; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
//                XSSFRow termRow = spreadsheet.getRow(rowIndex);
//                String term = "";
//                if (termRow.getCell(1).getCellType() == Cell.CELL_TYPE_NUMERIC) {
//                    if (termRow.getCell(2).getStringCellValue() == ""
//                            && termRow.getCell(3).getStringCellValue() != "") {
//                        term = termRow.getCell(3).getStringCellValue();
//                        rowIndex++;
//                    }
//                }
//
//                row = spreadsheet.getRow(rowIndex);
//                if (row != null) {
//                    CurriculumMappingEntity curriculum = new CurriculumMappingEntity();
//                    Cell subjectCell = row.getCell(2);
//                    if (subjectCell != null) {
//                        curriculum.setCurriculumMappingEntityPK(subjectCell.getRichStringCellValue().getString());
//                        curriculum.setTerm(term);
//                        curriculums.add(curriculum);
//
//                    }
//                }
//            }
//
//            System.out.println("All Curriculum Added");
//            for (CurriculumMappingEntity element : curriculums) {
//                if (!uniqueCurriculum.stream().anyMatch(c -> c.getTerm().equals(element.getTerm())
//                        && c.getCurriculumMappingEntityPK().equals(element.getCurriculumMappingEntityPK()))) {
//                    uniqueCurriculum.add(element);
//                }
//            }
//
//            curriculumsService.createCurriculumList(curriculums);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            obj.addProperty("success", false);
//            obj.addProperty("message", e.getMessage());
//            return obj;
//        }

        obj.addProperty("success", true);
        return obj;
    }

}

