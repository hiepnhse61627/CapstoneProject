package com.capstone.exporters;

import com.capstone.controllers.StudentController;
import com.capstone.entities.MarksEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.entities.SubjectEntity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExportStudentsFailImpl implements IExportObject {

    private String STUDENTS_FAIL_EXCEL_TEMPL = "/template/DSSV-học -lại.xlsx";

    @Override
    public String getFileName() {
        return "Students-Fail.xlsx";
    }

    @Override
    public void writeData(OutputStream os, Map<String, String> params, HttpServletRequest request) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(STUDENTS_FAIL_EXCEL_TEMPL);

        String semesterId = params.get("semesterId");
        String subjectId = params.get("subjectId") == null ? "0" : params.get("subjectId");
        String sSearch = params.get("sSearch") == null ? "" : params.get("sSearch");

        StudentController stdController = new StudentController();
        List<MarksEntity> dataList = stdController.GetStudentsList(semesterId, subjectId, sSearch);

        XSSFWorkbook workbook = new XSSFWorkbook(is);
        is.close();

        SXSSFWorkbook streamingWorkbook = new SXSSFWorkbook(workbook);
        for (int sheetNumb = 0; sheetNumb < streamingWorkbook.getNumberOfSheets(); sheetNumb++) {
            SXSSFSheet streamingSheet = streamingWorkbook.getSheetAt(sheetNumb);
            streamingSheet.setRandomAccessWindowSize(100);
            // write data
            if (sheetNumb == 0) {
                writeSheet1(streamingWorkbook, streamingSheet, dataList);
            } else {
                writeSheet2(streamingWorkbook, streamingSheet, dataList);
            }
        }
        streamingWorkbook.write(os);
    }

    private void writeSheet1(SXSSFWorkbook workbook, SXSSFSheet spreadsheet, List<MarksEntity> marks) throws Exception {
        Map<StudentEntity, List<MarksEntity>> map = convertMarksListToMap(marks);
        // style
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        //Start data table row
        int rowIndex = 5;
        for (Map.Entry<StudentEntity, List<MarksEntity>> entry : map.entrySet()) {
            StudentEntity student = entry.getKey();
            // write roll number
            Row row = spreadsheet.createRow(rowIndex);
            Cell rollNumberCell = row.createCell(0);
            rollNumberCell.setCellStyle(cellStyle);
            rollNumberCell.setCellValue(student.getRollNumber());
            // write full name
            Cell studentNameCell = row.createCell(1);
            studentNameCell.setCellStyle(cellStyle);
            studentNameCell.setCellValue(student.getFullName());
            // write Email
            Cell emailCell = row.createCell(2);
            emailCell.setCellStyle(cellStyle);
            emailCell.setCellValue(student.getEmail());
            // write student's failed subject
            Cell subjectsCell = row.createCell(3);
            subjectsCell.setCellStyle(cellStyle);
            List<MarksEntity> markList = entry.getValue();
            String subjectList = "";
            int count = 0;
            for (MarksEntity mark : markList) {
                String subjectCode = mark.getSubjectMarkComponentId().getSubjectId().getId();
                subjectList += subjectCode;
                if (count < markList.size() - 1) {
                    subjectList += ",";
                    count++;
                }
            }
            subjectsCell.setCellValue(subjectList);

            rowIndex++;
        }
    }

    private void writeSheet2(SXSSFWorkbook workbook, SXSSFSheet spreadsheet, List<MarksEntity> marks) throws Exception {
        Map<SubjectEntity, List<StudentEntity>> map = convertMarksListToMap1(marks);
        // style
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        //Start data table row
        int rowIndex = 5;
        for (Map.Entry<SubjectEntity, List<StudentEntity>> entry : map.entrySet()) {
            SubjectEntity subject = entry.getKey();
            int size = entry.getValue().size();
            // write subject code
            Row row = spreadsheet.createRow(rowIndex);
            Cell subjectCodeCell = row.createCell(0);
            subjectCodeCell.setCellStyle(cellStyle);
            subjectCodeCell.setCellValue(subject.getId());
            // write number of students failed
            Cell failedStudentsCell = row.createCell(2);
            failedStudentsCell.setCellStyle(cellStyle);
            failedStudentsCell.setCellValue(size);
            // write number of classes should be opened
            Cell openingClassCell = row.createCell(3);
            openingClassCell.setCellStyle(cellStyle);
            openingClassCell.setCellValue(size / 25);

            rowIndex++;
        }
    }

    private Map<StudentEntity, List<MarksEntity>> convertMarksListToMap(List<MarksEntity> marks) {
        Map<StudentEntity, List<MarksEntity>> map = new HashMap<>();

        if (marks != null && !marks.isEmpty()) {
            for (MarksEntity mark : marks) {
                StudentEntity student = mark.getStudentId();
                if (map.get(student) != null) {
                    map.get(student).add(mark);
                } else {
                    List<MarksEntity> newMarks = new ArrayList<>();
                    newMarks.add(mark);
                    map.put(student, newMarks);
                }
            }
        }

        return map;
    }

    private Map<SubjectEntity, List<StudentEntity>> convertMarksListToMap1(List<MarksEntity> marks) {
        Map<SubjectEntity, List<StudentEntity>> map = new HashMap<>();

        if (marks != null && !marks.isEmpty()) {
            for (MarksEntity mark : marks) {
                SubjectEntity subject = mark.getSubjectMarkComponentId().getSubjectId();
                if (map.get(subject) != null) {
                    map.get(subject).add(mark.getStudentId());
                } else {
                    List<StudentEntity> newStudents = new ArrayList<>();
                    newStudents.add(mark.getStudentId());
                    map.put(subject, newStudents);
                }
            }
        }

        return map;
    }
}
