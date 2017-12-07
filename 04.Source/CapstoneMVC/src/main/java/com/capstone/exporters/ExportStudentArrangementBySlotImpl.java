package com.capstone.exporters;

import com.capstone.models.Ultilities;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ExportStudentArrangementBySlotImpl implements IExportObject {
    private String FILE_NAME = "DSSV lop mon theo slot.xlsx";

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
        List<List<String>> studentList = (List<List<String>>) request.getSession().getAttribute("STUDENT_ARRANGEMENT_BY_SLOT_LIST");
        if (studentList == null) {
            return;
        }
        studentList = new ArrayList<>(studentList);

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
                if (!currentSubject.equals(previousSubject) || !currentShift.equals(previousShift)
                        || !currentClass.equals(previousClass)) {
                    isCreateNewSheet = true;
                }

                if (isCreateNewSheet) {
                    ordinalNumber = 1;
                    currentRow = 3;

                    spreadsheet = workbook.createSheet(currentClass);
                    spreadsheet.setColumnWidth(2, 8000);
                    this.createSpreadSheetTitle(spreadsheet, titleStyle, tableHeaderStyle, currentClass);
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
            spreadsheet.setColumnWidth(8, 4200);
            spreadsheet.setColumnWidth(14, 4100);
            spreadsheet.setColumnWidth(15, 4200);
            spreadsheet.setColumnWidth(21, 4200);
            spreadsheet.setColumnWidth(27, 4200);

            List<String> slotList = new ArrayList<>();
            slotList.add("S21");
            slotList.add("S22");
            slotList.add("S23");
            slotList.add("S31");
            slotList.add("S32");

            int ordinalNumber = 1;
            int currentRow = 3;

            int totalClassAM = 0;
            int totalClassPM = 0;
            int totalStudentAM = 0;
            int totalStudentPM = 0;
            
            int[] countClassAM = new int[5];
            int[] countClassPM = new int[5];
            int[] countStudentAM = new int[5];
            int[] countStudentPM = new int[5];

            String previousSubject = "";
            String previousShift = "";
            String previousClass = "";

            boolean isChangeSubject = false;

            Cell cell;
            Row row;
            for (List<String> data : studentList) {
                String rollNumber = data.get(2);
                String studentName = data.get(3);

                String currentSubject = data.get(0);
                String currentClass = data.get(4);
                String currentShift = data.get(5);

                int pos = currentClass.indexOf("_S");
                String slotName = "";
                if (pos != -1) {
                    slotName = currentClass.substring(pos + 1);
                }

                if (previousSubject.isEmpty() || currentSubject.equals(previousSubject)) {
                    if (currentShift.equals("AM")) {
                        ++totalStudentAM;

                        if (!slotName.isEmpty()) {
                            countStudentAM[slotList.indexOf(slotName)]++;
                        }
                    } else {
                        ++totalStudentPM;

                        if (!slotName.isEmpty()) {
                            countStudentPM[slotList.indexOf(slotName)]++;
                        }
                    }

                    if (!currentClass.equals(previousClass)) {
                        if (currentShift.equals("AM")) {
                            ++totalClassAM;

                            if (!slotName.isEmpty()) {
                                countClassAM[slotList.indexOf(slotName)]++;
                            }
                        } else {
                            ++totalClassPM;

                            if (!slotName.isEmpty()) {
                                countClassPM[slotList.indexOf(slotName)]++;
                            }
                        }
                    }
                } else {
                    // Change subject, write data
                    row = spreadsheet.createRow(currentRow++);

                    int colNum = 0;
                    boolean isLabSubject = Ultilities.containsIgnoreCase(previousSubject, "LAB");

                    cell = row.createCell(colNum++);
                    cell.setCellValue(ordinalNumber++);
                    cell.setCellStyle(tableCellStyle);

                    cell = row.createCell(colNum++);
                    cell.setCellValue(previousSubject);
                    cell.setCellStyle(tableCellStyle);

                    cell = row.createCell(colNum++);
                    cell.setCellValue(totalClassAM);
                    cell.setCellStyle(tableCellStyle);

                    for (int i = 0; i < slotList.size(); i++) {
                        cell = row.createCell(colNum++);
                        cell.setCellValue(isLabSubject ? "N/A" : String.valueOf(countClassAM[i]));
                        cell.setCellStyle(tableCellStyle);
                    }

                    cell = row.createCell(colNum++);
                    cell.setCellValue(totalClassPM);
                    cell.setCellStyle(tableCellStyle);

                    for (int i = 0; i < slotList.size(); i++) {
                        cell = row.createCell(colNum++);
                        cell.setCellValue(isLabSubject ? "N/A" : String.valueOf(countClassPM[i]));
                        cell.setCellStyle(tableCellStyle);
                    }

                    cell = row.createCell(colNum++);
                    cell.setCellValue(totalClassAM + totalClassPM);
                    cell.setCellStyle(tableCellStyle);

                    cell = row.createCell(colNum++);
                    cell.setCellValue(totalStudentAM);
                    cell.setCellStyle(tableCellStyle);

                    for (int i = 0; i < slotList.size(); i++) {
                        cell = row.createCell(colNum++);
                        cell.setCellValue(isLabSubject ? "N/A" : String.valueOf(countStudentAM[i]));
                        cell.setCellStyle(tableCellStyle);
                    }

                    cell = row.createCell(colNum++);
                    cell.setCellValue(totalStudentPM);
                    cell.setCellStyle(tableCellStyle);

                    for (int i = 0; i < slotList.size(); i++) {
                        cell = row.createCell(colNum++);
                        cell.setCellValue(isLabSubject ? "N/A" : String.valueOf(countStudentPM[i]));
                        cell.setCellStyle(tableCellStyle);
                    }

                    cell = row.createCell(colNum++);
                    cell.setCellValue(totalStudentAM + totalStudentPM);
                    cell.setCellStyle(tableCellStyle);

                    totalStudentAM = 0;
                    totalStudentPM = 0;
                    totalClassAM = 0;
                    totalClassPM = 0;

                    countClassAM = new int[5];
                    countClassPM = new int[5];
                    countStudentAM = new int[5];
                    countStudentPM = new int[5];

                    if (currentShift.equals("AM")) {
                        ++totalClassAM;
                        ++totalStudentAM;
                        if (!slotName.isEmpty()) {
                            countClassAM[slotList.indexOf(slotName)]++;
                            countStudentAM[slotList.indexOf(slotName)]++;
                        }
                    } else {
                        ++totalClassPM;
                        ++totalStudentPM;
                        if (!slotName.isEmpty()) {
                            countClassPM[slotList.indexOf(slotName)]++;
                            countStudentPM[slotList.indexOf(slotName)]++;
                        }
                    }
                }

                previousSubject = currentSubject;
                previousShift = currentShift;
                previousClass = currentClass;
            }

            // Create last record
            row = spreadsheet.createRow(currentRow);
            boolean isLabSubject = Ultilities.containsIgnoreCase(previousSubject, "LAB");

            int colNum = 0;
            cell = row.createCell(colNum++);
            cell.setCellValue(ordinalNumber++);
            cell.setCellStyle(tableCellStyle);

            cell = row.createCell(colNum++);
            cell.setCellValue(previousSubject);
            cell.setCellStyle(tableCellStyle);

            cell = row.createCell(colNum++);
            cell.setCellValue(totalClassAM);
            cell.setCellStyle(tableCellStyle);

            for (int i = 0; i < slotList.size(); i++) {
                cell = row.createCell(colNum++);
                cell.setCellValue(isLabSubject ? "N/A" : String.valueOf(countClassAM[i]));
                cell.setCellStyle(tableCellStyle);
            }

            cell = row.createCell(colNum++);
            cell.setCellValue(totalClassPM);
            cell.setCellStyle(tableCellStyle);

            for (int i = 0; i < slotList.size(); i++) {
                cell = row.createCell(colNum++);
                cell.setCellValue(isLabSubject ? "N/A" : String.valueOf(countClassPM[i]));
                cell.setCellStyle(tableCellStyle);
            }

            cell = row.createCell(colNum++);
            cell.setCellValue(totalClassAM + totalClassPM);
            cell.setCellStyle(tableCellStyle);

            cell = row.createCell(colNum++);
            cell.setCellValue(totalStudentAM);
            cell.setCellStyle(tableCellStyle);

            for (int i = 0; i < slotList.size(); i++) {
                cell = row.createCell(colNum++);
                cell.setCellValue(isLabSubject ? "N/A" : String.valueOf(countStudentAM[i]));
                cell.setCellStyle(tableCellStyle);
            }

            cell = row.createCell(colNum++);
            cell.setCellValue(totalStudentPM);
            cell.setCellStyle(tableCellStyle);

            for (int i = 0; i < slotList.size(); i++) {
                cell = row.createCell(colNum++);
                cell.setCellValue(isLabSubject ? "N/A" : String.valueOf(countStudentPM[i]));
                cell.setCellStyle(tableCellStyle);
            }

            cell = row.createCell(colNum++);
            cell.setCellValue(totalStudentAM + totalStudentPM);
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
        tableCell.setCellValue("S21");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(4);
        tableCell.setCellValue("S22");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(5);
        tableCell.setCellValue("S23");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(6);
        tableCell.setCellValue("S31");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(7);
        tableCell.setCellValue("S32");
        tableCell.setCellStyle(tableHeaderStyle);
        
        tableCell = row.createCell(8);
        tableCell.setCellValue("Số lớp buổi chiều");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(9);
        tableCell.setCellValue("S21");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(10);
        tableCell.setCellValue("S22");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(11);
        tableCell.setCellValue("S23");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(12);
        tableCell.setCellValue("S31");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(13);
        tableCell.setCellValue("S32");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(14);
        tableCell.setCellValue("Tổng số lớp");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(15);
        tableCell.setCellValue("Số SV buổi sáng");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(16);
        tableCell.setCellValue("S21");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(17);
        tableCell.setCellValue("S22");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(18);
        tableCell.setCellValue("S23");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(19);
        tableCell.setCellValue("S31");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(20);
        tableCell.setCellValue("S32");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(21);
        tableCell.setCellValue("Số SV buổi chiều");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(22);
        tableCell.setCellValue("S21");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(23);
        tableCell.setCellValue("S22");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(24);
        tableCell.setCellValue("S23");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(25);
        tableCell.setCellValue("S31");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(26);
        tableCell.setCellValue("S32");
        tableCell.setCellStyle(tableHeaderStyle);

        tableCell = row.createCell(27);
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
}
