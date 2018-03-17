package com.capstone.exporters;

import com.capstone.controllers.EmployeeList;
import com.capstone.controllers.ScheduleList;
import com.capstone.entities.DepartmentEntity;
import com.capstone.entities.EmployeeEntity;
import com.capstone.entities.SubjectDepartmentEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.services.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import static com.capstone.services.DateUtil.formatDate;

public class ExportFreeScheduleImpl implements IExportObject {
    private String EXCEL_TEMPL = "/template/FreeScheduleTemplate.xlsx";

    private String fileName = "Lich_trong_GV.xlsx";

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void setFileName(String name) {
        fileName = name;
    }

    @Override
    public void writeData(OutputStream os, Map<String, String> params, HttpServletRequest request) throws Exception {
        EmployeeList employeeListController = new EmployeeList();
        List<List<String>> freeScheduleList = employeeListController.LoadEmployeeFreeScheduleAllImpl(params);

        Integer lectureId = null;
        if (!params.get("lecture").equals("") && !params.get("lecture").equals("-1")) {
            lectureId = Integer.parseInt(params.get("lecture"));
        }
        String employeeCompetenceStr = employeeListController.findEmployeCompetence(lectureId);

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(EXCEL_TEMPL);

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        is.close();

        XSSFSheet streamingSheet = xssfWorkbook.getSheetAt(0);

        writeDataToTable(xssfWorkbook, streamingSheet, freeScheduleList, employeeCompetenceStr, params);

        xssfWorkbook.write(os);
    }

    private void writeDataToTable(XSSFWorkbook workbook, XSSFSheet spreadsheet, List<List<String>> changedScheduleList, String employeeCompetenceStr, Map<String, String> params) throws Exception {
        try {
            String lecture = "";
            Integer lectureId = null;
            IEmployeeService employeeService = new EmployeeServiceImpl();

            if (!params.get("lecture").equals("") && !params.get("lecture").equals("-1")) {
                lectureId = Integer.parseInt(params.get("lecture"));
                EmployeeEntity aLecture = employeeService.findEmployeeById(lectureId);
                lecture = aLecture.getFullName();
            }

            XSSFRow infoRow = spreadsheet.getRow(11);
            infoRow.getCell(1).setCellValue(lecture);
            infoRow.getCell(4).setCellValue(employeeCompetenceStr);

            String startDate = params.get("startDate");
            String endDate = params.get("endDate");

            XSSFRow dateRow = spreadsheet.getRow(9);
            dateRow.getCell(1).setCellValue(startDate);
            dateRow.getCell(4).setCellValue(endDate);
            Date today = new Date();
            dateRow.getCell(6).setCellValue(formatDate(today));

            if (!changedScheduleList.isEmpty()) {
//                Map<String, List<List<String>>> departmentTotal = new HashMap<>();

                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setBorderBottom(BorderStyle.THIN);
                cellStyle.setBorderLeft(BorderStyle.THIN);
                cellStyle.setBorderRight(BorderStyle.THIN);
                cellStyle.setBorderTop(BorderStyle.THIN);
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);





                int rowIndex = 15;
                for (List<String> changedSchedule : changedScheduleList) {
                    List<String> data = changedSchedule;
                    Cell cell;
                    XSSFRow row = spreadsheet.createRow(rowIndex);
                    for (int i = 0; i < data.size(); ++i) {
                        cell = row.createCell(i);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(data.get(i));
                    }
                    ++rowIndex;
                }

//                ExportStatusReport.StatusExportStudentDetailRunning = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
