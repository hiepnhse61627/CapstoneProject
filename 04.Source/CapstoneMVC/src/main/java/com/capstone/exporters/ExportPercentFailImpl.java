package com.capstone.exporters;

import com.capstone.controllers.PercentFailController;
import com.capstone.entities.CurriculumEntity;
import com.capstone.entities.SubjectCurriculumEntity;
import com.capstone.services.CurriculumServiceImpl;
import com.capstone.services.ICurriculumService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ExportPercentFailImpl implements IExportObject {

    private String EXCEL_TEMPL = "/template/PercentFail.xlsx";

    @Override
    public String getFileName() {
        return "PercentFail.xlsx";
    }

    @Override
    public void writeData(OutputStream os, Map<String, String> params, HttpServletRequest request) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(EXCEL_TEMPL);

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        is.close();

        // change to streaming working
        SXSSFWorkbook streamingWorkbook = new SXSSFWorkbook(xssfWorkbook);
        SXSSFSheet streamingSheet = streamingWorkbook.getSheetAt(0);
        streamingSheet.setRandomAccessWindowSize(100);

        PercentFailController controller = new PercentFailController();
        List<List<String>> entities = controller.processFailPercent(params);
        writeDataToTable(streamingWorkbook, streamingSheet, entities);

        streamingWorkbook.write(os);
    }

    private void writeDataToTable(SXSSFWorkbook workbook, SXSSFSheet spreadsheet, List<List<String>> entities) throws Exception {
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
            for (List<String> entity : entities) {
                Row row = spreadsheet.createRow(rowIndex);
                Cell cell;

                for (int i = 0; i < entity.size(); i++) {
                    cell = row.createCell(i);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(entity.get(i));
                }

                rowIndex++;
            }
        }
    }
}
