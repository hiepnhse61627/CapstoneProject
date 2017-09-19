package com.capstone.controllers;

import com.capstone.services.*;
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

    @RequestMapping(value = "/uploadStudentList", method = RequestMethod.POST)
    public void uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
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
    }

    @RequestMapping(value = "/goUploadSubjects")
    public String goUploadSubjectsPage() {
        return "uploadSubjects";
    }

    @RequestMapping(value = "/uploadSubjects", method = RequestMethod.POST)
    public void uploadSubject(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream is = file.getInputStream();

        HSSFWorkbook workbook = new HSSFWorkbook(is);
        HSSFSheet spreadsheet = workbook.getSheetAt(0);

        HSSFRow row;
        int excelDataIndex = 4;
        int subjectCodeIndex = 1;
        int abbreviationIndex = 2;
        int subjectNameIndex = 3;
        int numberOfCreditsIndex = 4;
        int prerequisiteIndex = 5;

        List<SubjectEntity> subjects = new ArrayList<SubjectEntity>();
        List<SubjectMarkComponentEntity> subjectMarkComponents = new ArrayList<SubjectMarkComponentEntity>();

        for (int rowIndex = excelDataIndex; rowIndex < spreadsheet.getLastRowNum(); rowIndex++) {
            row = spreadsheet.getRow(rowIndex);
            if (row != null) {
                SubjectEntity subject = new SubjectEntity();

                Cell subjectCodeCell = row.getCell(subjectCodeIndex);
                Cell abbreviationCell = row.getCell(abbreviationIndex);
                Cell subjectNameCell = row.getCell(subjectNameIndex);
                Cell numberOfCreditsCell = row.getCell(numberOfCreditsIndex);
                Cell prerequisiteCell = row.getCell(prerequisiteIndex);

                if (subjectCodeCell != null) {
                    subject.setId(subjectCodeCell.getStringCellValue());
                }

                if (abbreviationCell != null) {
                    subject.setAbbreviation(abbreviationCell.getStringCellValue());
                }

                if (subjectNameCell != null) {
                    subject.setName(subjectNameCell.getStringCellValue());
                }

                if (numberOfCreditsCell != null) {
                    subject.setCredits((int) numberOfCreditsCell.getNumericCellValue());
                }

                if (prerequisiteCell != null && !prerequisiteCell.getStringCellValue().isEmpty()) {
                    if (prerequisiteCell.getStringCellValue().contains("/")) {
                        String prerequisite = prerequisiteCell.getStringCellValue().split("/")[0];
                        SubjectEntity prerequisiteSubject = new SubjectEntity();
                        prerequisiteSubject.setId(prerequisite);

                        subject.setPrequisiteId(prerequisiteSubject);
                    } else {
                        SubjectEntity prerequisiteSubject = new SubjectEntity();
                        prerequisiteSubject.setId(prerequisiteCell.getStringCellValue());

                        subject.setPrequisiteId(prerequisiteSubject);
                    }
                } else {
                    subject.setPrequisiteId(null);
                }

                if (subject.getId() != null && !subject.getId().isEmpty()) {
                    subjects.add(subject);
                }
            }
        }
        subjectService.createSubjects(subjects);
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
                        CourseEntity courseEntity = courseService.findCourseByClass(classNameCell.getStringCellValue().toUpperCase());
                        if (courseEntity != null) {
                            marksEntity.setCourseId(courseEntity);
                        } else {
                            courseEntity = new CourseEntity();
                            courseEntity.setClass1(classNameCell.getStringCellValue().toUpperCase());
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
