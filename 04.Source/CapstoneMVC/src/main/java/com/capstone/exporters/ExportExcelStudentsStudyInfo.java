package com.capstone.exporters;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.services.StudentServiceImpl;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExportExcelStudentsStudyInfo implements IExportObject {
    private String EXCEL_TEMPLATE = "template/My-template-list-student-Study-info.xlsx";
    private StudentServiceImpl studentService = new StudentServiceImpl();

    private String fileName = "ListStudent_info.xlsx";

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void setFileName(String name) {
        this.fileName = name;
    }

    @Override
    public void writeData(OutputStream os, Map<String, String> params, HttpServletRequest request) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(EXCEL_TEMPLATE);

        XSSFWorkbook workbook = new XSSFWorkbook(is);
        // close input stream
        is.close();
        XSSFSheet spreadsheet = workbook.getSheetAt(0);

        writeDataToTable(workbook, spreadsheet, params);

        workbook.write(os);
    }

    private void writeDataToTable(XSSFWorkbook workbook, XSSFSheet sheet, Map<String, String> params) throws Exception {
        //use for birthDate format
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        // style
        CellStyle cellStyle = workbook.createCellStyle();
//        cellStyle.setBorderBottom(BorderStyle.THIN);
//        cellStyle.setBorderLeft(BorderStyle.THIN);
//        cellStyle.setBorderRight(BorderStyle.THIN);
//        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        int semesterId = Integer.parseInt(params.get("semesterId"));
        int programId = Integer.parseInt(params.get("programId"));
        List<StudentEntity>  listStudents = studentService.findStudentsBySemesterId(semesterId);

        if(programId > -1){
            listStudents = listStudents.stream().filter(q -> q.getProgramId().getId() == programId)
                    .collect(Collectors.toList());
        }

        //use lambda sort all students by program then by rollnumber
        listStudents.sort((p1, p2) -> {
            if (p2.getProgramId().getName().compareTo(p1.getProgramId().getName()) == 0) {
                return p2.getRollNumber().compareTo(p1.getRollNumber());
            } else {
                return p2.getProgramId().getName().compareTo(p1.getProgramId().getName());
            }

        });

        sheet = workbook.getSheetAt(0);
        int rowIndex = 1;
        int markSize = listStudents.size();
        XSSFRow row = null;

        for (StudentEntity student : listStudents) {
            ExportStatusReport.StatusStudentDetailExport = "Đang lấy thông tin " + rowIndex + " - " + (listStudents.size()+1);
            row = sheet.createRow(rowIndex);
            // ordinal number
            XSSFCell ordinalNumberCell = row.createCell(0);
            ordinalNumberCell.setCellStyle(cellStyle);
            ordinalNumberCell.setCellValue(rowIndex + "");

            XSSFCell programCell = row.createCell(1);
            programCell.setCellStyle(cellStyle);
            programCell.setCellValue(student.getProgramId().getName());

            XSSFCell rollNumberCell = row.createCell(2);
            rollNumberCell.setCellStyle(cellStyle);
            rollNumberCell.setCellValue(student.getRollNumber());

            XSSFCell fullNameCell = row.createCell(3);
            fullNameCell.setCellStyle(cellStyle);
            fullNameCell.setCellValue(student.getFullName());
            ++rowIndex;
            System.out.println(rowIndex+ " sv");
        }
    }
}
