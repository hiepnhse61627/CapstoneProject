package com.capstone.exporters;

import com.capstone.controllers.StudentFailPrequisite;
import com.capstone.entities.MarksEntity;
import com.capstone.entities.PrequisiteEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.models.FailPrequisiteModel;
import com.capstone.models.Ultilities;
import com.capstone.services.ISubjectService;
import com.capstone.services.SubjectServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ExportStudentFailedPrerequisiteImpl implements IExportObject {

    private String EXCEL_TEMPLATE = "/template/DSSV_Fail_Prequisite.xlsx";

    private ISubjectService service = new SubjectServiceImpl();

    @Override
    public String getFileName() {
        return "DSSV_Fail_Prequisite.xlsx";
    }

    @Override
    public void writeData(OutputStream os, Map<String, String> params) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(EXCEL_TEMPLATE);

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        is.close();
        // change to streaming working
        SXSSFWorkbook streamingWorkbook = new SXSSFWorkbook(xssfWorkbook);
        SXSSFSheet streamingSheet = streamingWorkbook.getSheetAt(0);
        streamingSheet.setRandomAccessWindowSize(100);
        // write data to table
        writeDataToTable(streamingWorkbook, streamingSheet, params);
        // write os
        streamingWorkbook.write(os);
    }

    private void writeDataToTable(SXSSFWorkbook workbook, SXSSFSheet spreadsheet, Map<String, String> params) {
        // process list
        StudentFailPrequisite controller = new StudentFailPrequisite();
        List<List<String>> models = controller.processData(params);
        if (models != null && !models.isEmpty()) {
            // style
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            // write data
            int rowIndex = 6;
            for (List<String> model : models) {
                Row row = spreadsheet.createRow(rowIndex);

                for (int i = 0; i < 4; i++) {
                    Cell rollNumberCell = row.createCell(i);
                    rollNumberCell.setCellStyle(cellStyle);
                    rollNumberCell.setCellValue(model.get(i));
                }

                rowIndex++;
            }
        }
    }
}
