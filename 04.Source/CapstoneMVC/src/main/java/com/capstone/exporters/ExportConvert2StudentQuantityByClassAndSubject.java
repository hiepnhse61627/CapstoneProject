package com.capstone.exporters;

import com.capstone.entities.StudentEntity;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class ExportConvert2StudentQuantityByClassAndSubject implements IExportObject {

    private String EXCEL_TEMPLATE = "template/Convert-Students-Quantity-BySubjectsAndClasses.xlsx";
    private String fileName = "StudentQuantity-by-Subject-and-Class.xlsx";
    private Map<String, Map<String, Integer>> combineList;

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public void setFileName(String name) {
        this.fileName = name;
    }

    public Map<String, Map<String, Integer>> getCombineList() {
        return combineList;
    }

    public void setCombineList(Map<String, Map<String, Integer>> combineList) {
        this.combineList = combineList;
    }

    public ExportConvert2StudentQuantityByClassAndSubject() {
    }

    @Override
    public void writeData(OutputStream os, Map<String, String> params, HttpServletRequest request) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(EXCEL_TEMPLATE);

        HttpSession session = request.getSession();
        Map<String, Map<String, Integer>> cbList = (Map<String, Map<String, Integer>>) session.getAttribute("studentQuantityConverList");
        this.combineList = cbList;

        XSSFWorkbook workbook = new XSSFWorkbook(is);
        // close input stream
        is.close();
        XSSFSheet spreadsheet = workbook.getSheetAt(0);

        writeDataToTable(workbook, spreadsheet, params);

        workbook.write(os);

        os.close();
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


        sheet = workbook.getSheetAt(0);
        int rowIndex = 1;
        if (combineList != null) {


            int markSize = this.combineList.size();
            XSSFRow row = null;
            for (Map.Entry<String, Map<String, Integer>> combine : this.combineList.entrySet()) {

                //index for total student quantity per subject
                int totalStudentsIndex = rowIndex;
                row = sheet.createRow(totalStudentsIndex);

                //set Subject Code
                XSSFCell subjectCodeCell = row.createCell(0);
                subjectCodeCell.setCellStyle(cellStyle);
                subjectCodeCell.setCellValue(combine.getKey());


                Map<String, Integer> classesAndStudentsQuantityList = combine.getValue();

                int totalStudents = 0;
                //Map<ClassName, StudentQuantity>
                for (Map.Entry<String, Integer> item :
                        classesAndStudentsQuantityList.entrySet()) {

                    //increase row
                    ++rowIndex;
                    row = sheet.createRow(rowIndex);

                    //set Class Name
                    XSSFCell classNameCell = row.createCell(1);
                    classNameCell.setCellStyle(cellStyle);
                    classNameCell.setCellValue(item.getKey());

                    XSSFCell quantityCell = row.createCell(2);
                    quantityCell.setCellStyle(cellStyle);
                    int studentQuantity = item.getValue();
                    totalStudents += studentQuantity;
                    quantityCell.setCellValue(studentQuantity);
                }

                //increase row to write a new subject
                ++rowIndex;

                //set total student of subject
                row = sheet.getRow(totalStudentsIndex);
                XSSFCell subjectStudentQuantityCell = row.createCell(2);
                subjectStudentQuantityCell.setCellStyle(cellStyle);
                subjectStudentQuantityCell.setCellValue(totalStudents);

                // ordinal number
                System.out.println(rowIndex);
            }
        }
    }
}
