package com.capstone.exporters;

import com.capstone.controllers.StudentController;
import com.capstone.entities.MarksEntity;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

        XSSFWorkbook workbook = new XSSFWorkbook(is);
        is.close();

        SXSSFWorkbook streamingWorkbook = new SXSSFWorkbook(workbook);
        SXSSFSheet streamingSheet = streamingWorkbook.getSheetAt(0);
        streamingSheet.setRandomAccessWindowSize(100);
        // build list title
        buildListTitle(streamingWorkbook, streamingSheet);
        // build table header
        buildTableHeader(streamingWorkbook, streamingSheet);
        // write data
        writeDataToTable(streamingWorkbook, streamingSheet, dataList);

        streamingWorkbook.write(os);
    }

    private void buildListTitle(SXSSFWorkbook workbook, SXSSFSheet spreadsheet) {
        Row row = spreadsheet.createRow(0);
        Cell cell = row.createCell(1);
        row.setHeight((short) 800);
        cell.setCellValue("Danh sách sinh viên nợ môn");
        // style
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        // font
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 18);
        font.setColor(HSSFColor.DARK_RED.index);
        // set style and font
        cellStyle.setFont(font);
        cell.setCellStyle(cellStyle);
        // merging cell
        CellRangeAddress range = new CellRangeAddress(0, 0, 1, 7);
        spreadsheet.addMergedRegion(range);
        RegionUtil.setBorderBottom(BorderStyle.THIN.getCode(), range, spreadsheet, workbook);
        RegionUtil.setBorderLeft(BorderStyle.THIN.getCode(), range, spreadsheet, workbook);
        RegionUtil.setBorderRight(BorderStyle.THIN.getCode(), range, spreadsheet, workbook);
        RegionUtil.setBorderTop(BorderStyle.THIN.getCode(), range, spreadsheet, workbook);
    }

    private void buildTableHeader(SXSSFWorkbook workbook, SXSSFSheet spreadsheet) {
        Row row = spreadsheet.createRow(2);
        // style
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        // font
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(HSSFColor.DARK_RED.index);
        // set font
        cellStyle.setFont(font);
        // create cell
        Cell rollNumberCell = row.createCell(0);
        rollNumberCell.setCellStyle(cellStyle);
        rollNumberCell.setCellValue("MSSV");

        Cell studentNameCell = row.createCell(1);
        studentNameCell.setCellStyle(cellStyle);
        studentNameCell.setCellValue("HỌ VÀ TÊN");
        CellRangeAddress range1 = new CellRangeAddress(2, 2, 1, 3);
        spreadsheet.addMergedRegion(range1);
        RegionUtil.setBorderBottom(BorderStyle.THIN.getCode(), range1, spreadsheet, workbook);
        RegionUtil.setBorderLeft(BorderStyle.THIN.getCode(), range1, spreadsheet, workbook);
        RegionUtil.setBorderRight(BorderStyle.THIN.getCode(), range1, spreadsheet, workbook);
        RegionUtil.setBorderTop(BorderStyle.THIN.getCode(), range1, spreadsheet, workbook);

        Cell SubjectCodeCell = row.createCell(4);
        SubjectCodeCell.setCellStyle(cellStyle);
        SubjectCodeCell.setCellValue("MÃ MÔN");
        CellRangeAddress range2 = new CellRangeAddress(2, 2, 4, 5);
        spreadsheet.addMergedRegion(range2);
        RegionUtil.setBorderBottom(BorderStyle.THIN.getCode(), range2, spreadsheet, workbook);
        RegionUtil.setBorderLeft(BorderStyle.THIN.getCode(), range2, spreadsheet, workbook);
        RegionUtil.setBorderRight(BorderStyle.THIN.getCode(), range2, spreadsheet, workbook);
        RegionUtil.setBorderTop(BorderStyle.THIN.getCode(), range2, spreadsheet, workbook);

        Cell classCell = row.createCell(6);
        classCell.setCellStyle(cellStyle);
        classCell.setCellValue("LỚP");

        Cell semesterCell = row.createCell(7);
        semesterCell.setCellStyle(cellStyle);
        semesterCell.setCellValue("HỌC KỲ");
        CellRangeAddress range3 = new CellRangeAddress(2, 2, 7, 8);
        spreadsheet.addMergedRegion(range3);
        RegionUtil.setBorderBottom(BorderStyle.THIN.getCode(), range3, spreadsheet, workbook);
        RegionUtil.setBorderLeft(BorderStyle.THIN.getCode(), range3, spreadsheet, workbook);
        RegionUtil.setBorderRight(BorderStyle.THIN.getCode(), range3, spreadsheet, workbook);
        RegionUtil.setBorderTop(BorderStyle.THIN.getCode(), range3, spreadsheet, workbook);
    }

    private void writeDataToTable(SXSSFWorkbook workbook, SXSSFSheet spreadsheet, List<MarksEntity> marks) throws IOException {
        // style
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);

        int rowIndex = 3;
        for (MarksEntity mark : marks) {
            Row row = spreadsheet.createRow(rowIndex);
            Cell rollNumberCell = row.createCell(0);
            rollNumberCell.setCellStyle(cellStyle);
            rollNumberCell.setCellValue(mark.getStudentId().getRollNumber());

            Cell studentNameCell = row.createCell(1);
            studentNameCell.setCellStyle(cellStyle);
            studentNameCell.setCellValue(mark.getStudentId().getFullName());
            CellRangeAddress range1 = new CellRangeAddress(rowIndex, rowIndex, 1, 3);
            spreadsheet.addMergedRegion(range1);
            RegionUtil.setBorderBottom(BorderStyle.THIN.getCode(), range1, spreadsheet, workbook);
            RegionUtil.setBorderLeft(BorderStyle.THIN.getCode(), range1, spreadsheet, workbook);
            RegionUtil.setBorderRight(BorderStyle.THIN.getCode(), range1, spreadsheet, workbook);
            RegionUtil.setBorderTop(BorderStyle.THIN.getCode(), range1, spreadsheet, workbook);

            Cell SubjectCodeCell = row.createCell(4);
            SubjectCodeCell.setCellStyle(cellStyle);
            SubjectCodeCell.setCellValue(mark.getSubjectId().getSubjectId());
            CellRangeAddress range2 = new CellRangeAddress(rowIndex, rowIndex, 4, 5);
            spreadsheet.addMergedRegion(range2);
            RegionUtil.setBorderBottom(BorderStyle.THIN.getCode(), range2, spreadsheet, workbook);
            RegionUtil.setBorderLeft(BorderStyle.THIN.getCode(), range2, spreadsheet, workbook);
            RegionUtil.setBorderRight(BorderStyle.THIN.getCode(), range2, spreadsheet, workbook);
            RegionUtil.setBorderTop(BorderStyle.THIN.getCode(), range2, spreadsheet, workbook);

            Cell classCell = row.createCell(6);
            classCell.setCellStyle(cellStyle);
            classCell.setCellValue(mark.getCourseId().getClass1());

            Cell semesterCell = row.createCell(7);
            semesterCell.setCellStyle(cellStyle);
            semesterCell.setCellValue(mark.getSemesterId().getSemester());
            CellRangeAddress range3 = new CellRangeAddress(rowIndex, rowIndex, 7, 8);
            spreadsheet.addMergedRegion(range3);
            RegionUtil.setBorderBottom(BorderStyle.THIN.getCode(), range3, spreadsheet, workbook);
            RegionUtil.setBorderLeft(BorderStyle.THIN.getCode(), range3, spreadsheet, workbook);
            RegionUtil.setBorderRight(BorderStyle.THIN.getCode(), range3, spreadsheet, workbook);
            RegionUtil.setBorderTop(BorderStyle.THIN.getCode(), range3, spreadsheet, workbook);

            rowIndex++;
        }
    }
}
