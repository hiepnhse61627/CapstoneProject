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

    private CellStyle titleStyle;
    private CellStyle tableHeaderStyle;
    private CellStyle tableCellStyle;
    private CellStyle tableCellAlignmentLeftStyle;

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

        titleStyle = workbook.createCellStyle();
        titleStyle.setFont(fontBold);

        tableHeaderStyle = workbook.createCellStyle();
        tableHeaderStyle.setBorderBottom(BorderStyle.THIN);
        tableHeaderStyle.setBorderLeft(BorderStyle.THIN);
        tableHeaderStyle.setBorderRight(BorderStyle.THIN);
        tableHeaderStyle.setBorderTop(BorderStyle.THIN);
        tableHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
        tableHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        tableHeaderStyle.setFont(fontBold);

        tableCellStyle = workbook.createCellStyle();
        tableCellStyle.setBorderBottom(BorderStyle.THIN);
        tableCellStyle.setBorderLeft(BorderStyle.THIN);
        tableCellStyle.setBorderRight(BorderStyle.THIN);
        tableCellStyle.setBorderTop(BorderStyle.THIN);
        tableCellStyle.setAlignment(HorizontalAlignment.CENTER);
        tableCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        tableCellAlignmentLeftStyle = workbook.createCellStyle();
        tableCellAlignmentLeftStyle.setBorderBottom(BorderStyle.THIN);
        tableCellAlignmentLeftStyle.setBorderLeft(BorderStyle.THIN);
        tableCellAlignmentLeftStyle.setBorderRight(BorderStyle.THIN);
        tableCellAlignmentLeftStyle.setBorderTop(BorderStyle.THIN);
        tableCellAlignmentLeftStyle.setAlignment(HorizontalAlignment.LEFT);
        tableCellAlignmentLeftStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        this.createStatisticSheet(workbook, studentList);

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
                cell.setCellStyle(tableCellStyle);
                cell.setCellValue(ordinalNumber++);

                cell = row.createCell(1);
                cell.setCellStyle(tableCellStyle);
                cell.setCellValue(rollNumber);

                cell = row.createCell(2);
                cell.setCellStyle(tableCellAlignmentLeftStyle);
                cell.setCellValue(studentName);

                previousSubject = currentSubject;
                previousShift = currentShift;
                previousClass = currentClass;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createStatisticSheet(SXSSFWorkbook workbook, List<List<String>> studentList) {
        try {
            SXSSFSheet spreadsheet = workbook.createSheet("Thống kê");
            this.createTitleForStatisticSheet(spreadsheet);

            spreadsheet.setColumnWidth(2, 4200);
            spreadsheet.setColumnWidth(3, 4200);
            spreadsheet.setColumnWidth(4, 3000);
            spreadsheet.setColumnWidth(5, 4100);
            spreadsheet.setColumnWidth(6, 4200);
            spreadsheet.setColumnWidth(7, 3000);
            spreadsheet.setColumnWidth(8, 7200);

            int ordinalNumber = 1;
            int currentRow = 3;

            int countClassAM = 0;
            int countClassPM = 0;
            int countStudentAM = 0;
            int countStudentPM = 0;

            String previousSubject = "";
            String previousShift = "";
            String previousClass = "";

            boolean isChangeSubject = false;

            Cell cell;
            Row row;
            for (List<String> data : studentList) {
                if (countStudentAM == 86) {
                    System.out.println();
                }

                if (countStudentPM == 88) {
                    System.out.println();
                }

                String rollNumber = data.get(2);
                String studentName = data.get(3);

                String currentSubject = data.get(0);
                String currentClass = data.get(4);
                String currentShift = data.get(5);

                if (previousSubject.isEmpty() || currentSubject.equals(previousSubject)) {
                    if (currentShift.equals("AM")) {
                        ++countStudentAM;
                    } else {
                        ++countStudentPM;
                    }

                    if (!currentClass.equals(previousClass)) {
                        if (currentShift.equals("AM")) {
                            ++countClassAM;
                        } else {
                            ++countClassPM;
                        }
                    }
                } else {
                    // Change subject, write data
                    row = spreadsheet.createRow(currentRow++);

                    cell = row.createCell(0);
                    cell.setCellValue(ordinalNumber++);
                    cell.setCellStyle(tableCellStyle);

                    cell = row.createCell(1);
                    cell.setCellValue(previousSubject);
                    cell.setCellStyle(tableCellStyle);

                    cell = row.createCell(2);
                    cell.setCellValue(countClassAM);
                    cell.setCellStyle(tableCellStyle);

                    cell = row.createCell(3);
                    cell.setCellValue(countClassPM);
                    cell.setCellStyle(tableCellStyle);

                    cell = row.createCell(4);
                    cell.setCellValue(countClassAM + countClassPM);
                    cell.setCellStyle(tableCellStyle);

                    cell = row.createCell(5);
                    cell.setCellValue(countStudentAM);
                    cell.setCellStyle(tableCellStyle);

                    cell = row.createCell(6);
                    cell.setCellValue(countStudentPM);
                    cell.setCellStyle(tableCellStyle);

                    cell = row.createCell(7);
                    cell.setCellValue(countStudentAM + countStudentPM);
                    cell.setCellStyle(tableCellStyle);

                    countStudentAM = 0;
                    countStudentPM = 0;
                    countClassAM = 0;
                    countClassPM = 0;
                    if (currentShift.equals("AM")) {
                        ++countClassAM;
                        ++countStudentAM;
                    } else {
                        ++countClassPM;
                        ++countStudentPM;
                    }
                }

                previousSubject = currentSubject;
                previousShift = currentShift;
                previousClass = currentClass;
            }

            // Create last record
            row = spreadsheet.createRow(currentRow);

            cell = row.createCell(0);
            cell.setCellValue(ordinalNumber++);
            cell.setCellStyle(tableCellStyle);

            cell = row.createCell(1);
            cell.setCellValue(previousSubject);
            cell.setCellStyle(tableCellStyle);

            cell = row.createCell(2);
            cell.setCellValue(countClassAM);
            cell.setCellStyle(tableCellStyle);

            cell = row.createCell(3);
            cell.setCellValue(countClassPM);
            cell.setCellStyle(tableCellStyle);

            cell = row.createCell(4);
            cell.setCellValue(countClassAM + countClassPM);
            cell.setCellStyle(tableCellStyle);

            cell = row.createCell(5);
            cell.setCellValue(countStudentAM);
            cell.setCellStyle(tableCellStyle);

            cell = row.createCell(6);
            cell.setCellValue(countStudentPM);
            cell.setCellStyle(tableCellStyle);

            cell = row.createCell(7);
            cell.setCellValue(countStudentAM + countStudentPM);
            cell.setCellStyle(tableCellStyle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTitleForStatisticSheet(SXSSFSheet spreadsheet){
        Row row = spreadsheet.createRow(0);

        Cell titleCell = row.createCell(0);
        titleCell.setCellValue("SỐ LƯỢNG LỚP VÀ SINH VIÊN");
        titleCell.setCellStyle(titleStyle);

        row = spreadsheet.createRow(2);

        Cell tableCell = row.createCell(0);
        tableCell.setCellValue("STT");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(1);
        tableCell.setCellValue("Môn");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(2);
        tableCell.setCellValue("Số lớp buổi sáng");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(3);
        tableCell.setCellValue("Số lớp buổi chiều");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(4);
        tableCell.setCellValue("Tổng số lớp");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(5);
        tableCell.setCellValue("Số SV buổi sáng");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(6);
        tableCell.setCellValue("Số SV buổi chiều");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(7);
        tableCell.setCellValue("Tổng số SV");
        tableCell.setCellStyle(tableHeaderStyle);
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
