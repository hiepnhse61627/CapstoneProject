package com.capstone.exporters;

import com.capstone.entities.CurriculumEntity;
import com.capstone.entities.SubjectCurriculumEntity;
import com.capstone.services.CurriculumServiceImpl;
import com.capstone.services.ICurriculumService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ExportAllCurriculumImpl implements IExportObject {

    private String EXCEL_TEMPL = "/template/Curriculum.xlsx";

    @Override
    public String getFileName() {
        return "Curriculum.xlsx";
    }

    @Override
    public void writeData(OutputStream os, Map<String, String> params, HttpServletRequest request) throws Exception {
        ICurriculumService curriculumService = new CurriculumServiceImpl();

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(EXCEL_TEMPL);

        XSSFWorkbook streamingWorkbook = new XSSFWorkbook(is);
        is.close();

        // change to streaming working
//        XSSFWorkbook streamingWorkbook = new XSSFWorkbook(xssfWorkbook);
//        SXSSFSheet streamingSheet = streamingWorkbook.getSheetAt(0);
//        streamingSheet.setRandomAccessWindowSize(100);

        List<CurriculumEntity> stu = curriculumService.getAllCurriculums();

        writeDataToTable(streamingWorkbook, stu);

        streamingWorkbook.write(os);
    }

    private void writeDataToTable(XSSFWorkbook workbook, List<CurriculumEntity> stu) throws Exception {
        // start data table row
        if (stu != null && !stu.isEmpty()) {
            // style
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            for (CurriculumEntity cur : stu) {
                Sheet spreadsheet = workbook.cloneSheet(0);
                workbook.setSheetName(workbook.getSheetIndex(spreadsheet), cur.getName());
//                SXSSFSheet spreadsheet = workbook.createSheet(cur.getName());

                int rowIndex = 6;

                for (SubjectCurriculumEntity entity : cur.getSubjectCurriculumEntityList()) {
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
                    cell.setCellValue(entity.getSubjectCredits());

                    rowIndex++;
                }
            }
        }

        workbook.removeSheetAt(0);
    }
}
