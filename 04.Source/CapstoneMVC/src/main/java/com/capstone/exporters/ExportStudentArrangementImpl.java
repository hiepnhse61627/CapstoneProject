package com.capstone.exporters;

import com.capstone.controllers.PercentFailController;
import com.capstone.entities.RealSemesterEntity;
import com.capstone.services.IRealSemesterService;
import com.capstone.services.RealSemesterServiceImpl;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ExportStudentArrangementImpl implements IExportObject {
    private String EXCEL_TEMPLATE = "/template/Empty_Excel.xlsx";
    private String FILE_NAME = "DSSV theo lop mon.xlsx";

    @Override
    public String getFileName() {
        return FILE_NAME;
    }

    @Override
    public void writeData(OutputStream os, Map<String, String> params, HttpServletRequest request) throws Exception {
        // Mã môn, Tên môn, MSSV, Tên sinh viên, Lớp, Buổi
        List<List<String>> studentList = (List<List<String>>) request.getSession().getAttribute("STUDENT_ARRANGEMENT_LIST");
        if (studentList == null) {
            return;
        }
        studentList = new ArrayList<>(studentList);
        this.sortStudentList(studentList);

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();

        // change to streaming working
        SXSSFWorkbook streamingWorkbook = new SXSSFWorkbook(xssfWorkbook);
        writeDataToTable(streamingWorkbook, studentList);

        streamingWorkbook.write(os);
    }

    private void writeDataToTable(SXSSFWorkbook workbook, List<List<String>> studentList) throws Exception {
        Font fontBold = workbook.createFont();
        fontBold.setBold(true);

        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setFont(fontBold);

        CellStyle tableHeaderStyle = workbook.createCellStyle();
        tableHeaderStyle.setBorderBottom(BorderStyle.THIN);
        tableHeaderStyle.setBorderLeft(BorderStyle.THIN);
        tableHeaderStyle.setBorderRight(BorderStyle.THIN);
        tableHeaderStyle.setBorderTop(BorderStyle.THIN);
        tableHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
        tableHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        tableHeaderStyle.setFont(fontBold);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle cellAlignmentLeftStyle = workbook.createCellStyle();
        cellAlignmentLeftStyle.setBorderBottom(BorderStyle.THIN);
        cellAlignmentLeftStyle.setBorderLeft(BorderStyle.THIN);
        cellAlignmentLeftStyle.setBorderRight(BorderStyle.THIN);
        cellAlignmentLeftStyle.setBorderTop(BorderStyle.THIN);
        cellAlignmentLeftStyle.setAlignment(HorizontalAlignment.LEFT);
        cellAlignmentLeftStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        int ordinalNumber = 1;
        int currentRow = 3;
        int classNumber = 1;
        String previousSubject = "";
        String previousShift = "";
        String previousClass = "";
        SXSSFSheet spreadsheet = null;

        Row row;
        Cell cell;
        try {
            for (List<String> data : studentList) {
                String rollNumber = data.get(2);
                String studentName = data.get(3);

                String currentSubject = data.get(0);
                String currentClass = data.get(4);
                String currentShift = data.get(5);

                boolean isCreateNewSheet = false;
                if (!currentSubject.equals(previousSubject) || !currentShift.equals(previousShift)) {
                    classNumber = 1;
                    isCreateNewSheet = true;
                } else if (!currentClass.equals(previousClass)) {
                    classNumber++;
                    isCreateNewSheet = true;
                }

                if (isCreateNewSheet) {
                    ordinalNumber = 1;
                    currentRow = 3;

                    String className = currentSubject + "_" + currentShift + "_" + classNumber;
                    spreadsheet = workbook.createSheet(className);
                    spreadsheet.setColumnWidth(2, 8000);
                    this.createSpreadSheetTitle(spreadsheet, titleStyle, tableHeaderStyle, className);
                }

                row = spreadsheet.createRow(currentRow++);

                cell = row.createCell(0);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(ordinalNumber++);

                cell = row.createCell(1);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(rollNumber);

                cell = row.createCell(2);
                cell.setCellStyle(cellAlignmentLeftStyle);
                cell.setCellValue(studentName);

                previousSubject = currentSubject;
                previousShift = currentShift;
                previousClass = currentClass;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createSpreadSheetTitle(SXSSFSheet spreadsheet, CellStyle titleStyle, CellStyle tableHeaderStyle, String className){
        Row row = spreadsheet.createRow(0);

        Cell titleCell = row.createCell(0);
        titleCell.setCellValue("DANH SÁCH SINH VIÊN LỚP " + className);
        titleCell.setCellStyle(titleStyle);

        row = spreadsheet.createRow(2);

        Cell tableCell = row.createCell(0);
        tableCell.setCellValue("STT");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(1);
        tableCell.setCellValue("MSSV");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(2);
        tableCell.setCellValue("Họ Tên");
        tableCell.setCellStyle(tableHeaderStyle);
    }

    private void sortStudentList(List<List<String>> studentList) {
        // Sort: Mã môn > Buổi > Lớp > MSSV
        studentList.sort(new Comparator<List<String>>() {
            @Override
            public int compare(List<String> o1, List<String> o2) {
                return o1.get(0).compareTo(o2.get(0));
            }
        }.thenComparing(new Comparator<List<String>>() {
            @Override
            public int compare(List<String> o1, List<String> o2) {
                return o1.get(5).compareTo(o2.get(5));
            }
        }).thenComparing(new Comparator<List<String>>() {
            @Override
            public int compare(List<String> o1, List<String> o2) {
                return o1.get(4).compareTo(o2.get(4));
            }
        }).thenComparing(new Comparator<List<String>>() {
            @Override
            public int compare(List<String> o1, List<String> o2) {
                return o1.get(3).compareTo(o2.get(3));
            }
        }));
    }
}
