package com.capstone.exporters;

import com.capstone.entities.GraduateDetailEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.services.GraduateDetailServiceImpl;
import com.capstone.services.StudentServiceImpl;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ExportGraduatedConfirmation implements IExportObject {

    private String EXCEL_TEMPLATE = "template/Xacnhan_TotNghiep.xlsx";
    private String fileName = "Xac-nhan-tot-Nghiep.xlsx";
    StudentServiceImpl studentService = new StudentServiceImpl();

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public void setFileName(String name) {
        this.fileName = name;
    }


    public ExportGraduatedConfirmation() {
    }

    @Override
    public void writeData(OutputStream os, Map<String, String> params, HttpServletRequest request) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(EXCEL_TEMPLATE);

        HttpSession session = request.getSession();

        XSSFWorkbook workbook = new XSSFWorkbook(is);
        // close input stream
        is.close();
        XSSFSheet spreadsheet = workbook.getSheetAt(0);

        writeDataToTable(workbook, spreadsheet, params);

        workbook.write(os);

        os.close();
    }

    private void writeDataToTable(XSSFWorkbook workbook, XSSFSheet sheet, Map<String, String> params) throws Exception {
        try {


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

            String studentIdParam = params.get("studentId");
            int studentId = Integer.parseInt(studentIdParam);
            StudentEntity student = studentService.findStudentById(studentId);
            GraduateDetailServiceImpl graduateDetailService = new GraduateDetailServiceImpl();
            GraduateDetailEntity gd = graduateDetailService.findGraduateDetailEntity(student.getId());
//            this.setFileName("Xac-nhan-tot-Nghiep"+student.getRollNumber()+".xlsx");

            int studentNameIndex = 8;
            int studentNameCol = 2;
            int studentBirthDateIndex = 10;
            int studentBirthDateCol = 2;
            int studentRollNumberIndex = 12;
            int studentRollNumberCol = 2;
            int studentProgramIndex = 14;
            int studentProgramCol = 2;
            int studentFormIndex = 16;
            int studentFormCol = 2;

            int graduateYearIndex = 18;
            int graduateYearCol = 4;
            int diplomaCodeIndex = 20;
            int diplomaCodeCol = 4;
            int certificateCodeIndex = 22;
            int certificateCodeCol = 4;
            int decisionNumberIndex = 24;
            int decisionNumberCol = 4;

            Date birthDate = student.getDateOfBirth();
            String bd = "";
            if (birthDate != null) {

                bd = sdf.format(birthDate);
            }

            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

            sheet = workbook.getSheetAt(0);
            //Name
            XSSFRow row = sheet.getRow(studentNameIndex);
            Cell nameCell = row.createCell(studentNameCol);
            nameCell.setCellValue(student.getFullName());

            //Birthdate
            row = sheet.getRow(studentBirthDateIndex);
            Cell birthDateCell = row.createCell(studentBirthDateCol);
            birthDateCell.setCellValue(bd);

            //rollNumber
            row = sheet.getRow(studentRollNumberIndex);
            Cell rollNumberCell = row.createCell(studentRollNumberCol);
            rollNumberCell.setCellValue(student.getRollNumber());

            //program
            row = sheet.getRow(studentProgramIndex);
            Cell programCell = row.createCell(studentProgramCol);
            programCell.setCellValue(student.getProgramId().getFullName());

            //Form
            String form = gd.getForm();
            if (form != null) {
                row = sheet.getRow(studentFormIndex);
                Cell formCell = row.createCell(studentFormCol);
                formCell.setCellValue(form);
            }

            //Graduate Year
            String graduateYear = gd.getDate();
            if(graduateYear != null && !graduateYear.isEmpty()){
                row = sheet.getRow(graduateYearIndex);
                SimpleDateFormat convert = new SimpleDateFormat("yyyy-MM-dd");
                Date graduateDate = convert.parse(graduateYear);
                Cell graduateCell = row.createCell(graduateYearCol);
                graduateCell.setCellValue(yearFormat.format(graduateDate));
            }


            //diploma code
            String diplomaCode = gd.getDiplomaCode();
            if(diplomaCode != null){
                row = sheet.getRow(diplomaCodeIndex);
                Cell diplomaCell = row.createCell(diplomaCodeCol);
                diplomaCell.setCellValue(diplomaCode);
            }


            //certificate number
            String certificateCode = gd.getCertificateCode();
            if(certificateCode != null){
                row = sheet.getRow(certificateCodeIndex);
                Cell certificateCell = row.createCell(certificateCodeCol);
                certificateCell.setCellValue(certificateCode);
            }


            //decision number
            String decisionNumber= gd.getGraduateDecisionNumber();
            if(decisionNumber != null){
                row = sheet.getRow(decisionNumberIndex);
                Cell decisionCell = row.createCell(decisionNumberCol);
                decisionCell.setCellValue(decisionNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
