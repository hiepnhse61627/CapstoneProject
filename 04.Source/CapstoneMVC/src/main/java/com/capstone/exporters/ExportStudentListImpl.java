package com.capstone.exporters;

import com.capstone.entities.CurriculumEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.entities.SubjectCurriculumEntity;
import com.capstone.services.CurriculumServiceImpl;
import com.capstone.services.ICurriculumService;
import com.capstone.services.IStudentService;
import com.capstone.services.StudentServiceImpl;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class ExportStudentListImpl implements IExportObject {

    private String EXCEL_TEMPL = "/template/DSSV.xlsx";

    @Override
    public String getFileName() {
        return "StudentList.xlsx";
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

        writeDataToTable(streamingWorkbook, streamingSheet);

        streamingWorkbook.write(os);
    }

    private void writeDataToTable(SXSSFWorkbook workbook, SXSSFSheet spreadsheet) {
        // start data table row
        IStudentService service = new StudentServiceImpl();
        List<StudentEntity> entities = service.findAllStudents();

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
            for (StudentEntity entity : entities) {
                Row row = spreadsheet.createRow(rowIndex);
                Cell cell;

                cell = row.createCell(0);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(entity.getRollNumber());

                cell = row.createCell(1);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(entity.getFullName());

                cell = row.createCell(2);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(entity.getGender() == true ? "Nữ" : "Nam");

                cell = row.createCell(3);
                cell.setCellStyle(cellStyle);
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                cell.setCellValue(df.format(entity.getDateOfBirth()));

                cell = row.createCell(4);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(entity.getProgramId().getName() + "_" + entity.getProgramId().getCurriculumEntityList().get(entity.getProgramId().getCurriculumEntityList().size() - 1).getName());

                cell = row.createCell(5);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(entity.getTerm());

                rowIndex++;
            }
        }
    }
}
