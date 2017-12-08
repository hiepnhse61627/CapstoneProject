package com.capstone.exporters;

import com.capstone.controllers.GraduateController;
import com.capstone.controllers.StudentDetail;
import com.capstone.entities.*;
import com.capstone.enums.SubjectTypeEnum;
import com.capstone.models.Suggestion;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ExportStudentFailedAndNextSubjectImpl implements IExportObject {

    private IStudentService studentService = new StudentServiceImpl();

    private String EXCEL_TEMPL = "/template/Kehoachhocdihoclai.xlsx";

    private String fileName = "Kehoachhocdihoclai.xlsx";

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void setFileName(String name) {
        fileName = name;
    }

    @Override
    public void writeData(OutputStream os, Map<String, String> params, HttpServletRequest request) throws Exception {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream is = classLoader.getResourceAsStream(EXCEL_TEMPL);

            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
            is.close();
            // change to streaming working
            SXSSFWorkbook streamingWorkbook = new SXSSFWorkbook(xssfWorkbook);
            SXSSFSheet streamingSheet = streamingWorkbook.getSheetAt(0);
            streamingSheet.setRandomAccessWindowSize(100);
            // student list
            List<StudentEntity> students;

            int stu = Integer.parseInt(params.get("studentId"));
            String semester = params.get("semesterId");

            if (stu < 0) {
                students = studentService.findAllStudents();
            } else {
                students = new ArrayList<>();
                StudentEntity student = studentService.findStudentById(stu);
                students.add(student);
            }

            writeDataToTable(streamingWorkbook, streamingSheet, students, semester);

            streamingWorkbook.write(os);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void writeDataToTable(SXSSFWorkbook workbook, SXSSFSheet spreadsheet, List<StudentEntity> students, String semester) throws Exception {
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
                if (ExportStatusReport.StopExporting) {
                    System.out.println("stopped exporting!");
                    break;
                }

                Row row = spreadsheet.createRow(rowIndex);
                Cell rollNumberCell = row.createCell(0);
                rollNumberCell.setCellStyle(cellStyle);
                rollNumberCell.setCellValue(student.getRollNumber());

                Cell studentNameCell = row.createCell(1);
                studentNameCell.setCellStyle(cellStyle);
                studentNameCell.setCellValue(student.getFullName());

                Cell studentEmail = row.createCell(2);
                studentEmail.setCellStyle(cellStyle);
                studentEmail.setCellValue(student.getEmail() == null ? "N/A" : student.getEmail());

                Cell tinchi = row.createCell(3);
                tinchi.setCellStyle(cellStyle);
                tinchi.setCellValue(student.getPassCredits());

                // failed subject
                List<List<String>> marks = processFailedSubject(student, semester);
                if (marks != null && !marks.isEmpty()) {
                    String failedSubject = "";
                    for (int i = 0; i < marks.size(); i++) {
                        failedSubject += marks.get(i).get(0);
                        failedSubject += ",";
                    }
                    failedSubject = Character.toString(failedSubject.charAt(failedSubject.length() - 1)).equals(",") ? failedSubject.substring(0, failedSubject.length() - 1) : failedSubject;
                    Cell failedSubjectCell = row.createCell(4);
                    failedSubjectCell.setCellStyle(cellStyle);
                    failedSubjectCell.setCellValue(failedSubject);
                } else {
                    Cell failedSubjectCell = row.createCell(4);
                    failedSubjectCell.setCellStyle(cellStyle);
                    failedSubjectCell.setCellValue("N/A");
                }

                // next subject
                List<List<String>> nextSubjects = processNextSubject(student, semester);
                if (nextSubjects != null && !nextSubjects.isEmpty()) {
                    String next = "";
                    for (List<String> subjects2 : nextSubjects) {
                        String subjectId = subjects2.get(0);
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
                List<List<String>> currentSubject = processCurrentSubject(student.getId(), semester);
                if (currentSubject != null && !currentSubject.isEmpty()) {
                    String next = "";
                    for (List<String> subjects2 : currentSubject) {
                        String subjectId = subjects2.get(0);
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

                // current subject
                List<List<String>> slowSubject = processNotStart(student.getId(), semester);
                if (slowSubject != null && !slowSubject.isEmpty()) {
                    String next = "";
                    for (List<String> subjects2 : slowSubject) {
                        String subjectId = subjects2.get(0);
                        next += subjectId;
                        next += ",";
                    }

                    if (next.equals("")) {
                        next = "N/A";
                    }

                    Cell nextSubjectCell = row.createCell(7);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue(next);
                } else {
                    Cell nextSubjectCell = row.createCell(7);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue("N/A");
                }

                // current subject
                List<List<String>> suggestSubjects = processSuggestion(student.getId(), semester);
                if (suggestSubjects != null && !suggestSubjects.isEmpty()) {
                    String next = "";
                    for (List<String> subjects2 : suggestSubjects) {
                        String subjectId = subjects2.get(0);
                        next += subjectId;
                        next += ",";
                    }

                    if (next.equals("")) {
                        next = "N/A";
                    }

                    Cell nextSubjectCell = row.createCell(8);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue(next);
                } else {
                    Cell nextSubjectCell = row.createCell(8);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue("N/A");
                }

                ExportStatusReport.StatusStudentDetailExport = "Exporting " + (count++) + " of " + students.size();
                rowIndex++;
            }
        }
    }

    private List<List<String>> processFailedSubject(StudentEntity student, String semester) {
        StudentDetail detail = new StudentDetail();
        return detail.processFailed(student.getId(), semester);
    }

    private List<List<String>> processNextSubject(StudentEntity student, String semester) {
        StudentDetail detail = new StudentDetail();
        return detail.processNext(student.getId(), semester, true, false);
    }

    public List<List<String>> processCurrentSubject(int stuId, String semester) {
        StudentDetail detail = new StudentDetail();
        return detail.processCurrent(stuId, semester);
    }

    public List<List<String>> processNotStart(int stuId, String semester) {
        StudentDetail detail = new StudentDetail();
        return detail.processNotStart(stuId, semester);
    }

    public List<List<String>> processSuggestion(int stuId, String semester) {
        StudentDetail detail = new StudentDetail();
        Suggestion suggestion = detail.processSuggestion(stuId, semester);
        List<List<String>> result = suggestion.getData();

        List<String> brea = new ArrayList<>();
        brea.add("break");
        brea.add("");

        int index = result.indexOf(brea);
        if (index > -1) {
            if (suggestion.isDuchitieu()) {
                result = result.subList(0, index);
            } else {
                result = result.subList(index + 1, result.size());
            }
        }
        return result;
    }
}
