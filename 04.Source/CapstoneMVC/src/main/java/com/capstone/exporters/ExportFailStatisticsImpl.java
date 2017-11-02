package com.capstone.exporters;

import com.capstone.controllers.FailStatisticsController;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;

public class ExportFailStatisticsImpl implements IExportObject {

    private String EXCEL_TEMPLATE = "/template/Fail_Statistics_Template.xlsx";

    @Override
    public String getFileName() {
        return "Fail_Statistics.xlsx";
    }

    @Override
    public void writeData(OutputStream os, Map<String, String> params) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(EXCEL_TEMPLATE);

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        XSSFSheet spreadsheet = xssfWorkbook.getSheetAt(0);
        // write data to table
        String semester = params.get("semesterId");
        writeDataToTable(xssfWorkbook, spreadsheet, semester);
        // write os
        xssfWorkbook.write(os);
    }

    private void writeDataToTable(XSSFWorkbook workbook, XSSFSheet spreadsheet, String semester) {
        // process list
        FailStatisticsController failStatisticsController = new FailStatisticsController();
        ArrayList<ArrayList<String >> result = failStatisticsController.processData(semester);
        if (result != null && !result.isEmpty()) {
            // style
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setBorderBottom(BorderStyle.THIN);
            headerCellStyle.setBorderLeft(BorderStyle.THIN);
            headerCellStyle.setBorderRight(BorderStyle.THIN);
            headerCellStyle.setBorderTop(BorderStyle.THIN);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(Font.COLOR_RED);
            headerCellStyle.setFont(font);
            // write data
            for (ArrayList<String> record : result) {
                int rowIndex = 6;
                Row row = spreadsheet.getRow(rowIndex);

                Cell headerCell = row.createCell(5);
                headerCell.setCellValue(semester);
                headerCell.setCellStyle(headerCellStyle);
                CellRangeAddress range1 = new CellRangeAddress(rowIndex, rowIndex, 5, 6);
                spreadsheet.addMergedRegion(range1);
                RegionUtil.setBorderBottom(BorderStyle.THIN, range1, spreadsheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, range1, spreadsheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, range1, spreadsheet);
                RegionUtil.setBorderTop(BorderStyle.THIN, range1, spreadsheet);

                rowIndex = rowIndex + 1;
                row = spreadsheet.getRow(rowIndex);
                Cell failedCell = row.createCell(5);
                failedCell.setCellValue(record.get(0));
                failedCell.setCellStyle(cellStyle);
                range1 = new CellRangeAddress(rowIndex, rowIndex, 5, 6);
                spreadsheet.addMergedRegion(range1);
                RegionUtil.setBorderBottom(BorderStyle.THIN, range1, spreadsheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, range1, spreadsheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, range1, spreadsheet);
                RegionUtil.setBorderTop(BorderStyle.THIN, range1, spreadsheet);

                rowIndex = rowIndex + 1;
                row = spreadsheet.getRow(rowIndex);
                Cell paidCell = row.createCell(5);
                paidCell.setCellValue(record.get(1));
                paidCell.setCellStyle(cellStyle);
                range1 = new CellRangeAddress(rowIndex, rowIndex, 5, 6);
                spreadsheet.addMergedRegion(range1);
                RegionUtil.setBorderBottom(BorderStyle.THIN, range1, spreadsheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, range1, spreadsheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, range1, spreadsheet);
                RegionUtil.setBorderTop(BorderStyle.THIN, range1, spreadsheet);

                rowIndex = rowIndex + 1;
                row = spreadsheet.getRow(rowIndex);
                Cell failedInCurrentCell = row.createCell(5);
                failedInCurrentCell.setCellValue(record.get(2));
                failedInCurrentCell.setCellStyle(cellStyle);
                range1 = new CellRangeAddress(rowIndex, rowIndex, 5, 6);
                spreadsheet.addMergedRegion(range1);
                RegionUtil.setBorderBottom(BorderStyle.THIN, range1, spreadsheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, range1, spreadsheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, range1, spreadsheet);
                RegionUtil.setBorderTop(BorderStyle.THIN, range1, spreadsheet);

                rowIndex = rowIndex + 1;
                row = spreadsheet.getRow(rowIndex);
                Cell failedInTheEndSemesterCell = row.createCell(5);
                failedInTheEndSemesterCell.setCellValue(record.get(3));
                failedInTheEndSemesterCell.setCellStyle(cellStyle);
                range1 = new CellRangeAddress(rowIndex, rowIndex, 5, 6);
                spreadsheet.addMergedRegion(range1);
                RegionUtil.setBorderBottom(BorderStyle.THIN, range1, spreadsheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, range1, spreadsheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, range1, spreadsheet);
                RegionUtil.setBorderTop(BorderStyle.THIN, range1, spreadsheet);
            }
        }
    }
}
