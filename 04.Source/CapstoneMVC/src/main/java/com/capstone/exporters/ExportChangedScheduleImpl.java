package com.capstone.exporters;

import com.capstone.controllers.BestStudentController;
import com.capstone.controllers.ScheduleList;
import com.capstone.entities.*;
import com.capstone.services.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.capstone.services.DateUtil.formatDate;

public class ExportChangedScheduleImpl implements IExportObject {
    private String EXCEL_TEMPL = "/template/ChangedScheduleTemplate.xlsx";

    private String fileName = "Lich_day_thay_doi_GV.xlsx";

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
        ScheduleList scheduleListController = new ScheduleList();
        List<List<String>> changedScheduleList = scheduleListController.loadScheduleChangeAllImpl(params);

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(EXCEL_TEMPL);

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        is.close();

        XSSFSheet streamingSheet = xssfWorkbook.getSheetAt(0);

        writeDataToTable(xssfWorkbook, streamingSheet, changedScheduleList, params);

        xssfWorkbook.write(os);
    }

    private void writeDataToTable(XSSFWorkbook workbook, XSSFSheet spreadsheet, List<List<String>> changedScheduleList, Map<String, String> params) throws Exception {
        try {
            String startDate = params.get("startDate");
            String endDate = params.get("endDate");
            String department = "";
            String lecture = "";

            Integer lectureId = null;
            Integer departmentId = null;

            IEmployeeService employeeService = new EmployeeServiceImpl();
            IDepartmentService departmentService = new DepartmentServiceImpl();
            ISubjectDepartmentService subjectDepartmentService = new SubjectDepartmentServiceImpl();
            ISubjectService subjectService = new SubjectServiceImpl();

            if (!params.get("lecture").equals("") && !params.get("lecture").equals("-1")) {
                lectureId = Integer.parseInt(params.get("lecture"));
                EmployeeEntity aLecture = employeeService.findEmployeeById(lectureId);
                lecture = aLecture.getFullName();
            }

            if (!params.get("department").equals("") && !params.get("department").equals("-1")) {
                departmentId = Integer.parseInt(params.get("department"));
                DepartmentEntity aDepartment = departmentService.findDepartmentById(departmentId);
                department = aDepartment.getDeptName();
            }

            XSSFRow dateRow = spreadsheet.getRow(9);
            dateRow.getCell(1).setCellValue(startDate);
            dateRow.getCell(4).setCellValue(endDate);
            Date today = new Date();
            dateRow.getCell(6).setCellValue(formatDate(today));

            XSSFRow infoRow = spreadsheet.getRow(11);
            infoRow.getCell(1).setCellValue(lecture);
            infoRow.getCell(4).setCellValue(department);


            if (!changedScheduleList.isEmpty()) {
                Map<String, List<List<String>>> departmentTotal = new HashMap<>();

                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setBorderBottom(BorderStyle.THIN);
                cellStyle.setBorderLeft(BorderStyle.THIN);
                cellStyle.setBorderRight(BorderStyle.THIN);
                cellStyle.setBorderTop(BorderStyle.THIN);
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);


                for (List<String> changedSchedule : changedScheduleList) {
                    String subjectCode = changedSchedule.get(1);
                    SubjectEntity subjectEntity = subjectService.findSubjectById(subjectCode);
                    List<SubjectDepartmentEntity> subDeptEntity = subjectDepartmentService.findSubjectDepartmentsBySubject(subjectEntity);

                    if (subDeptEntity != null && subDeptEntity.size() > 0) {
                        List<List<String>> listOfChanges = departmentTotal.get(subDeptEntity.get(0).getDeptId().getDeptName());
                        if (listOfChanges == null) {
                            departmentTotal.put(subDeptEntity.get(0).getDeptId().getDeptName(), new ArrayList<>());
                        } else {
                            List<String> data = changedSchedule.subList(1, changedSchedule.size());
                            listOfChanges.add(data);
                            departmentTotal.put(subDeptEntity.get(0).getDeptId().getDeptName(), listOfChanges);
                        }
                    }

//                    List<String> data = changedSchedule.subList(1, changedSchedule.size());
//
//                    for (int i = 0; i < 2; ++i) {
//                        cell = row.createCell(i);
//                        cell.setCellStyle(cellStyle);
//                        cell.setCellValue(data.get(i));
//                    }


                }

                int rowIndex = 15;
//                int departmentSheetIndex = 1;
                Cell cell;
                for (String key : departmentTotal.keySet()) {
                    XSSFRow row = spreadsheet.createRow(rowIndex);
                    cell = row.createCell(0);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(key);

                    cell = row.createCell(1);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(departmentTotal.get(key).size());

                    ++rowIndex;

                    XSSFSheet aSheet = workbook.createSheet(key);
                    XSSFRow headerRow = aSheet.createRow(0);

                    cell = headerRow.createCell(0);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue("Mã môn");

                    cell = headerRow.createCell(1);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue("Lớp");

                    cell = headerRow.createCell(2);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue("Ngày thực dạy");

                    cell = headerRow.createCell(3);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue("Slot");

                    cell = headerRow.createCell(4);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue("Phòng");

                    cell = headerRow.createCell(5);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue("GV đứng lớp");

                    cell = headerRow.createCell(6);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue("Slot ban đầu");

                    List<List<String>> dataListOfDepartment = departmentTotal.get(key);

                    int dataRowIndex = 1;
                    for (List<String> lineData : dataListOfDepartment) {
                        XSSFRow dataRow = aSheet.createRow(dataRowIndex);
                        for (int i = 0; i < lineData.size(); ++i) {
                            cell = dataRow.createCell(i);
                            cell.setCellStyle(cellStyle);
                            cell.setCellValue(lineData.get(i));
                        }
                        ++dataRowIndex;
                    }

                    aSheet.autoSizeColumn(0);
                    aSheet.autoSizeColumn(1);
                    aSheet.autoSizeColumn(2);
                    aSheet.autoSizeColumn(3);
                    aSheet.autoSizeColumn(4);
                    aSheet.autoSizeColumn(5);
                    aSheet.autoSizeColumn(6);
                }


                ExportStatusReport.StatusExportStudentDetailRunning = false;
                System.out.println(departmentTotal.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
