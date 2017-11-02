package com.capstone.exporters;

import com.capstone.controllers.StudentDetail;
import com.capstone.entities.*;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.persistence.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ExportStudentFailedAndNextSubjectImpl implements IExportObject {

    private IStudentService studentService = new StudentServiceImpl();
    private IMarksService marksService = new MarksServiceImpl();
    private ISubjectService subjectService = new SubjectServiceImpl();

    private String EXCEL_TEMPL = "/template/DSSV_HL_MTT.xlsx";

    @Override
    public String getFileName() {
        return "DSSV_HL_MTT.xlsx";
    }

    @Override
    public void writeData(OutputStream os, Map<String, String> params) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(EXCEL_TEMPL);

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        is.close();
        // change to streaming working
        SXSSFWorkbook streamingWorkbook = new SXSSFWorkbook(xssfWorkbook);
        SXSSFSheet streamingSheet = streamingWorkbook.getSheetAt(0);
        streamingSheet.setRandomAccessWindowSize(100);
        // student list
        List<StudentEntity> students = studentService.findAllStudents();
//        StudentEntity stu = studentService.findStudentById(Integer.parseInt(params.get("studentId")));
//        students.add(stu);
        writeDataToTable(streamingWorkbook, streamingSheet, students);

        streamingWorkbook.write(os);
    }

    private void writeDataToTable(SXSSFWorkbook workbook, SXSSFSheet spreadsheet, List<StudentEntity> students) {
        // start data table row
        if (students != null && !students.isEmpty()) {
            // style
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            int rowIndex = 6;
            int count = 1;
            for (StudentEntity student : students) {
                Row row = spreadsheet.createRow(rowIndex);
                Cell rollNumberCell = row.createCell(0);
                rollNumberCell.setCellStyle(cellStyle);
                rollNumberCell.setCellValue(student.getRollNumber());

                Cell studentNameCell = row.createCell(1);
                studentNameCell.setCellStyle(cellStyle);
                studentNameCell.setCellValue(student.getFullName());
                // failed subject
                List<List<String>> marks = processFailedSubject(student);
                if (marks != null && !marks.isEmpty()) {
                    String failedSubject = "";
                    for (int i = 0; i < marks.size(); i++) {
                        failedSubject += marks.get(i).get(0);
                        failedSubject += ",";
                    }
                    failedSubject = Character.toString(failedSubject.charAt(failedSubject.length() - 1)).equals(",") ? failedSubject.substring(0, failedSubject.length() - 1) : failedSubject;
                    Cell failedSubjectCell = row.createCell(2);
                    failedSubjectCell.setCellStyle(cellStyle);
                    failedSubjectCell.setCellValue(failedSubject);
                } else {
                    Cell failedSubjectCell = row.createCell(2);
                    failedSubjectCell.setCellStyle(cellStyle);
                    failedSubjectCell.setCellValue("N/A");
                }

                // next subject
                List<List<String>> nextSubjects = processNextSubject(student);
                if (nextSubjects != null && !nextSubjects.isEmpty()) {
                    String next = "";
                    for (List<String> subjects : nextSubjects) {
                        String subjectId = subjects.get(0);
                        next += subjectId;
                        next += ",";
                    }

                    if (next.equals("")) {
                        next = "N/A";
                    }

                    Cell nextSubjectCell = row.createCell(3);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue(next);
                } else {
                    Cell nextSubjectCell = row.createCell(3);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue("N/A");
                }

                // current subject
                List<List<String>> currentSubject = processCurrentSubject(student.getId());
                if (currentSubject != null && !currentSubject.isEmpty()) {
                    String next = "";
                    for (List<String> subjects : currentSubject) {
                        String subjectId = subjects.get(0);
                        next += subjectId;
                        next += ",";
                    }

                    if (next.equals("")) {
                        next = "N/A";
                    }

                    Cell nextSubjectCell = row.createCell(4);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue(next);
                } else {
                    Cell nextSubjectCell = row.createCell(4);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue("N/A");
                }

                // current subject
                List<List<String>> slowSubject = processNotStart(student.getId());
                if (slowSubject != null && !slowSubject.isEmpty()) {
                    String next = "";
                    for (List<String> subjects : slowSubject) {
                        String subjectId = subjects.get(0);
                        next += subjectId;
                        next += ",";
                    }

                    if (next.equals("")) {
                        next = "N/A";
                    }

                    Cell nextSubjectCell = row.createCell(5);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue(next);
                } else {
                    Cell nextSubjectCell = row.createCell(5);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue("N/A");
                }

                // current subject
                List<List<String>> suggestSubjects = processSuggestion(student.getId());
                if (suggestSubjects != null && !suggestSubjects.isEmpty()) {
                    String next = "";
                    for (List<String> subjects : suggestSubjects) {
                        String subjectId = subjects.get(0);
                        next += subjectId;
                        next += ",";
                    }

                    if (next.equals("")) {
                        next = "N/A";
                    }

                    Cell nextSubjectCell = row.createCell(6);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue(next);
                } else {
                    Cell nextSubjectCell = row.createCell(6);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue("N/A");
                }

                ExportStatusReport.StatusStudentDetailExport = "Exporting " + (count++) + " of " + students.size();
                rowIndex++;
            }
        }
    }

    private List<List<String>> processFailedSubject(StudentEntity student) {
        StudentDetail detail = new StudentDetail();
        return detail.processFailed(student.getId());
    }

    private List<List<String>> processNextSubject(StudentEntity student) {
        StudentDetail detail = new StudentDetail();
        return detail.processNext(student.getId());
    }

    public List<List<String>> processCurrentSubject(int stuId) {
        StudentDetail detail = new StudentDetail();
        return detail.processCurrent(stuId);
    }

    public List<List<String>> processNotStart(int stuId) {
        StudentDetail detail = new StudentDetail();
        return detail.processNotStart(stuId);
    }

    public List<List<String>> processSuggestion(int stuId) {
        StudentDetail detail = new StudentDetail();
        return detail.processSuggestion(stuId);
    }
}
