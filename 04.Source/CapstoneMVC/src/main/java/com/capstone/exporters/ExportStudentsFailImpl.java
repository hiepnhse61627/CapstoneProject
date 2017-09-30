package com.capstone.exporters;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ExportStudentsFailImpl implements IExportObject {

    private String STUDENTS_FAIL_EXCEL_TEMPL = "/template/DSSV-học -lại.xlsx";

    @Override
    public String getFileName() {
        return "Students-Fail.xlsx";
    }

    @Override
    public void writeData(OutputStream os) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(STUDENTS_FAIL_EXCEL_TEMPL);
        Workbook workbook = new XSSFWorkbook(is);

        workbook.write(os);
    }
}
