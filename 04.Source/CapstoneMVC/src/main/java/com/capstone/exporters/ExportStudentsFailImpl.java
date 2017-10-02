package com.capstone.exporters;

import com.capstone.controllers.StudentController;
import com.capstone.entities.MarksEntity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExportStudentsFailImpl implements IExportObject {

    private String STUDENTS_FAIL_EXCEL_TEMPL = "/template/DSSV-học -lại.xlsx";

    @Override
    public String getFileName() {
        return "Students-Fail.xlsx";
    }

    @Override
    public void writeData(OutputStream os, Map<String, String> params) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(STUDENTS_FAIL_EXCEL_TEMPL);

        String semesterId = params.get("semesterId");
        String subjectId = params.get("subjectId");
        String sSearch = params.get("sSearch");

        StudentController stdController = new StudentController();
        List<MarksEntity> dataList = stdController.GetStudentsList(semesterId, subjectId, sSearch);

        List<String> headerList = new ArrayList<String>(){{
            add("MSSV");
            add("Tên sinh viên");
            add("Môn học");
            add("Lớp");
            add("Học kỳ");
            add("Điểm trung bình");
            add("Trạng thái");
        }};

        Workbook wb = new XSSFWorkbook(is);
        CreationHelper creationHelper = wb.getCreationHelper();
        Sheet sheet;
        if (wb.getNumberOfSheets() == 0) {
            sheet = wb.createSheet("Sheet1");
        } else {
            sheet = wb.getSheetAt(0);
        }

//        Font defaultFont= wb.createFont();
//        defaultFont.setFontHeightInPoints((short)10);
//        defaultFont.setFontName("Arial");
//        defaultFont.setColor(IndexedColors.BLACK.getIndex());
//        defaultFont.setBold(false);
//        defaultFont.setItalic(false);
//
//        Font headerFont= wb.createFont();
//        headerFont.setFontHeightInPoints((short)10);
//        headerFont.setFontName("Arial");
//        headerFont.setColor(IndexedColors.WHITE.getIndex());
//        headerFont.setBold(true);
//        headerFont.setItalic(false);

        int curRow = 0;

        // Create header
        Row row = sheet.createRow(curRow++);
        for (int i = 0; i < headerList.size(); ++i) {
            row.createCell(i).setCellValue(headerList.get(i));
        }
//        row.getRowStyle().setFont(headerFont);

        int count;
        for (MarksEntity m : dataList) {
            row = sheet.createRow(curRow++);
            count = 0;

            row.createCell(count++).setCellValue(m.getStudentId().getRollNumber());
            row.createCell(count++).setCellValue(m.getStudentId().getFullName());
            row.createCell(count++).setCellValue(m.getSubjectId() == null ? "N/A" : m.getSubjectId().getSubjectId());
            row.createCell(count++).setCellValue(m.getCourseId() == null ? "N/A" : m.getCourseId().getClass1());
            row.createCell(count++).setCellValue(m.getSemesterId() == null ? "N/A" : m.getSemesterId().getSemester());
            row.createCell(count++).setCellValue(String.valueOf(m.getAverageMark()));
            row.createCell(count).setCellValue(m.getStatus());
        }

        wb.write(os);
    }
}
