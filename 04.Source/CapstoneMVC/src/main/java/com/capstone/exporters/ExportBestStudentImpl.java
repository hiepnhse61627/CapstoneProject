package com.capstone.exporters;

import com.capstone.controllers.BestStudentController;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ExportBestStudentImpl implements IExportObject {
    private String EXCEL_TEMPL = "/template/DSSV-Giỏi-nhất-môn.xlsx";

    private String fileName = "BestStudent_List.xlsx";

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
        BestStudentController bestStudentController = new BestStudentController();
        List<List<String>> studentList = bestStudentController.getBestStudentList(params);

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

    private void writeDataToTable(SXSSFWorkbook workbook, SXSSFSheet spreadsheet, List<List<String>> studentList) throws Exception {


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

                // MSSV, Tên sinh viên, Học kỳ, Khóa, Kỳ, Mã môn, Tên môn, Điểm trung bình
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
