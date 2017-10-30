package com.capstone.exporters;

import com.capstone.controllers.GoodStudentController;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ExportGoodStudentsImpl implements IExportObject {
    private String EXCEL_TEMPL = "/template/DSSV-Giỏi.xlsx";

    @Override
    public String getFileName() {
        return "GoodStudent_List.xlsx";
    }

    @Override
    public void writeData(OutputStream os, Map<String, String> params) throws IOException {
        GoodStudentController goodStudentController = new GoodStudentController();
        List<List<String>> studentList = goodStudentController.getGoodStudentList(params);

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(EXCEL_TEMPL);

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        is.close();

        // change to streaming working
        SXSSFWorkbook streamingWorkbook = new SXSSFWorkbook(xssfWorkbook);
        SXSSFSheet streamingSheet = streamingWorkbook.getSheetAt(0);
        streamingSheet.setRandomAccessWindowSize(100);

        writeDataToTable(streamingWorkbook, streamingSheet, studentList);

        streamingWorkbook.write(os);
    }

    private void writeDataToTable(SXSSFWorkbook workbook, SXSSFSheet spreadsheet, List<List<String>> studentList) {


        if (!studentList.isEmpty()) {
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            int rowIndex = 6;
            for (List<String> studentData : studentList) {
                Row row = spreadsheet.createRow(rowIndex);
                Cell cell;

                // MSSV, Tên sinh viên, Học kỳ, Khóa, Kỳ, Điểm trung bình
                for (int i = 0; i < studentData.size(); ++i) {
                    cell = row.createCell(i);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(studentData.get(i));
                }

                ++rowIndex;
            }

        }
    }
}
