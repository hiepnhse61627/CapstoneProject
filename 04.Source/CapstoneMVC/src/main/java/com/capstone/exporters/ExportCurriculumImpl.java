package com.capstone.exporters;

import com.capstone.entities.*;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExportCurriculumImpl implements IExportObject {

    private String EXCEL_TEMPL = "/template/Curriculum.xlsx";

    @Override
    public String getFileName() {
        return "Curriculum.xlsx";
    }

    @Override
    public void writeData(OutputStream os, Map<String, String> params) throws IOException {
        ICurriculumService curriculumService = new CurriculumServiceImpl();

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(EXCEL_TEMPL);

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        is.close();

        // change to streaming working
        SXSSFWorkbook streamingWorkbook = new SXSSFWorkbook(xssfWorkbook);
        SXSSFSheet streamingSheet = streamingWorkbook.getSheetAt(0);
        streamingSheet.setRandomAccessWindowSize(100);

        CurriculumEntity stu = curriculumService.getCurriculumById(Integer.parseInt(params.get("curId")));

        writeDataToTable(streamingWorkbook, streamingSheet, stu.getSubjectCurriculumEntityList());

        streamingWorkbook.write(os);
    }

    private void writeDataToTable(SXSSFWorkbook workbook, SXSSFSheet spreadsheet, List<SubjectCurriculumEntity> entities) {
        // start data table row
        if (entities != null && !entities.isEmpty()) {
            // style
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            int rowIndex = 6;
            for (SubjectCurriculumEntity entity : entities) {
                Row row = spreadsheet.createRow(rowIndex);
                Cell cell;

                cell = row.createCell(0);
                cell.setCellStyle(cellStyle);
                cell.setCellValue("Học kỳ " + entity.getTermNumber());

                cell = row.createCell(1);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(entity.getSubjectId().getId());

                cell = row.createCell(2);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(entity.getSubjectId().getAbbreviation());

                cell = row.createCell(3);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(entity.getSubjectId().getName());

                cell = row.createCell(4);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(entity.getSubjectId().getCredits());

                rowIndex++;
            }
        }
    }
}
