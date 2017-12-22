package com.capstone.exporters;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.services.IStudentService;
import com.capstone.services.StudentServiceImpl;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ExportStudentsFailedMoreThanRequiredCreditsImpl implements IExportObject {

    private String EXCEL_TEMPLATE = "/template/DSSV-No-Tin-Chi.xlsx";

    private String fileName = "DSSV-no-tin-chi.xlsx";

    private IStudentService studentService = new StudentServiceImpl();

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
        InputStream inputStream = classLoader.getResourceAsStream(EXCEL_TEMPLATE);

        Integer credits = Integer.valueOf(params.get("credits"));
        List<StudentEntity> students = studentService.getStudentFailedMoreThanRequiredCredits(credits);

        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);

        writeDataToSheet(workbook, sheet, students);

        workbook.write(os);
    }

    private void writeDataToSheet(XSSFWorkbook workbook, XSSFSheet sheet, List<StudentEntity> students) {
        //Start data table row
        int rowIndex = 1;
        for (StudentEntity student : students) {
            // write roll number
            Row row = sheet.createRow(rowIndex);
            Cell rollNumberCell = row.createCell(0);
            rollNumberCell.setCellValue(student.getRollNumber());
            // write full name
            Cell studentNameCell = row.createCell(1);
            studentNameCell.setCellValue(student.getFullName());
            // write pass fail credits
            Cell passfailcreditscell = row.createCell(2);
            passfailcreditscell.setCellValue(student.getPassFailCredits() + "");
            // write pass credits
            Cell passcreditscell = row.createCell(3);
            passcreditscell.setCellValue(student.getPassCredits() + "");
            // write debt credits
            Cell debtCredits = row.createCell(4);
            debtCredits.setCellValue(student.getPassFailCredits() - student.getPassCredits() + "");

            rowIndex++;
        }
    }
}
