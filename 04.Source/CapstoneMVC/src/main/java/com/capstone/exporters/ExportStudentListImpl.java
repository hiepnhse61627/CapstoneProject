package com.capstone.exporters;

import com.capstone.entities.CurriculumEntity;
import com.capstone.entities.DocumentStudentEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.entities.SubjectCurriculumEntity;
import com.capstone.models.Enums;
import com.capstone.services.*;
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
    public void writeData(OutputStream os, Map<String, String> params) throws Exception {
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

    private void writeDataToTable(SXSSFWorkbook workbook, SXSSFSheet spreadsheet) throws Exception {
        // start data table row
        IStudentService studentService = new StudentServiceImpl();
        IDocumentStudentService documentStudentService = new DocumentStudentServiceImpl();

        List<StudentEntity> studentList = studentService.findAllStudents();
        List<DocumentStudentEntity> docStudentList = documentStudentService.getAllLatestDocumentStudent();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        if (studentList != null && !studentList.isEmpty()) {
            // style
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            int rowIndex = 6;
            for (StudentEntity student : studentList) {
                Row row = spreadsheet.createRow(rowIndex);
                Cell cell;

                cell = row.createCell(0);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(student.getRollNumber());

                cell = row.createCell(1);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(student.getFullName());

                cell = row.createCell(2);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(df.format(student.getDateOfBirth()));

                cell = row.createCell(3);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(student.getGender() == Enums.Gender.MALE.getValue()
                        ? Enums.Gender.MALE.getName() : Enums.Gender.FEMALE.getName());

                cell = row.createCell(4);
                cell.setCellStyle(cellStyle);
                if (student.getProgramId() != null) {
                    cell.setCellValue(student.getProgramId().getName());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(5);
                cell.setCellStyle(cellStyle);
                DocumentStudentEntity curDocStudent = null;
                for (DocumentStudentEntity docStudent : docStudentList) {
                    if (student.getId() == docStudent.getStudentId().getId()) {
                        curDocStudent = docStudent;
                        break;
                    }
                }
                if (curDocStudent != null) {
                    cell.setCellValue(curDocStudent.getCurriculumId().getProgramId().getName()
                            + "_" + curDocStudent.getCurriculumId().getName());
                } else {
                    cell.setCellValue("");
                }

                cell = row.createCell(6);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(student.getTerm());

                rowIndex++;
            }
        }
    }


}
