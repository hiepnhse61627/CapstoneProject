package com.capstone.exporters;

import com.capstone.controllers.GraduateController;
import com.capstone.entities.MarksEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.services.IMarksService;
import com.capstone.services.ISubjectService;
import com.capstone.services.MarksServiceImpl;
import com.capstone.services.SubjectServiceImpl;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ExportGraduatedStudentsImpl implements IExportObject {

    private String EXCEL_TEMPLATE = "template/DSSV-dudieukien.xlsx";
    private IMarksService marksService = new MarksServiceImpl();
    private ISubjectService subjectService = new SubjectServiceImpl();

    @Override
    public String getFileName() {
        return "Graduated-Students.xlsx";
    }

    @Override
    public void writeData(OutputStream os, Map<String, String> params) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(EXCEL_TEMPLATE);

        XSSFWorkbook workbook = new XSSFWorkbook(is);
        XSSFSheet spreadsheet = workbook.getSheetAt(0);

        writeDataToTable(workbook, spreadsheet, params);

        workbook.write(os);
    }

    private void writeDataToTable(XSSFWorkbook workbook, XSSFSheet sheet, Map<String, String> params) throws Exception {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        int rowIndex = 6;
        for (List<String> entity : processData(params)) {
            Row row = sheet.createRow(rowIndex);
            Cell cell;

            for (int i = 0; i < entity.size(); i++) {
                cell = row.createCell(i);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(entity.get(i));
            }

            rowIndex++;
        }
    }

    private List<List<String>> processData(Map<String, String> params) {
        String type = params.get("type");
        List<List<String>> studentList;
        GraduateController graduateController = new GraduateController();
        if (type.equals("Graduate")) {
            studentList = graduateController.processGraduate(params);
        } else {
            studentList = graduateController.proccessOJT(params);
        }

        return studentList;
    }
}
