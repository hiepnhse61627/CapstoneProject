package com.capstone.exporters;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.capstone.entities.MarksEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.services.IMarksService;
import com.capstone.services.ISubjectService;
import com.capstone.services.MarksServiceImpl;
import com.capstone.services.SubjectServiceImpl;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExportPDFGraduatedStudentsImpl implements IExportObject {

    private String EXCEL_TEMPLATE = "template/DSSV-TN.xlsx";
    private IMarksService marksService = new MarksServiceImpl();
    private ISubjectService subjectService = new SubjectServiceImpl();

    @Override
    public String getFileName() {
        return "Graduated-Students.pdf";
    }

    @Override
    public void writeData(OutputStream os, Map<String, String> params, HttpServletRequest request) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(EXCEL_TEMPLATE);

        XSSFWorkbook workbook = new XSSFWorkbook(is);
        // close input stream
        is.close();
        XSSFSheet spreadsheet = workbook.getSheetAt(0);

        writeDataToTable(workbook, spreadsheet, params);

        String realPath = ExportPDFGraduatedStudentsImpl.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String excelPath = realPath.substring(0, realPath.indexOf("WEB-INF")) + "output.xlsx";
        OutputStream outputFile = new FileOutputStream(excelPath);
        workbook.write(outputFile);
        // close output stream
        try {
            Workbook asposeWorkbook = new Workbook(excelPath);
            // remove file
            File file = new File(excelPath);
            file.delete();
            file = null;
            asposeWorkbook.save(os, SaveFormat.PDF);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeDataToTable(XSSFWorkbook workbook, XSSFSheet sheet, Map<String, String> params) throws Exception {
        // style
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Map<StudentEntity, List<MarksEntity>> dataMap = processData(params);
        for (Map.Entry<StudentEntity, List<MarksEntity>> entry : dataMap.entrySet()) {
            sheet = workbook.cloneSheet(0, entry.getKey().getRollNumber());
            XSSFRow row = sheet.getRow(11);
            row.getCell(2).setCellValue(entry.getKey().getFullName());
            row.getCell(6).setCellValue(entry.getKey().getRollNumber());

            int ordinalNumber = 1;
            int rowIndex = 20;
            int markSize = entry.getValue().size();
            for (MarksEntity marksEntity : entry.getValue()) {
                row = sheet.createRow(rowIndex);
                // ordinal number
                XSSFCell ordinalNumberCell = row.createCell(0);
                ordinalNumberCell.setCellStyle(cellStyle);
                ordinalNumberCell.setCellValue("" + (markSize - ordinalNumber + 1));
                // Subject code
                SubjectEntity subjectEntity = subjectService.findSubjectById(marksEntity.getSubjectMarkComponentId().getSubjectId().getId());
                XSSFCell subjectCodeCell = row.createCell(1);
                subjectCodeCell.setCellValue(subjectEntity.getId());
                CellRangeAddress range1 = new CellRangeAddress(rowIndex, rowIndex, 1, 2);
                sheet.addMergedRegion(range1);
                RegionUtil.setBorderBottom(BorderStyle.THIN, range1, sheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, range1, sheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, range1, sheet);
                RegionUtil.setBorderTop(BorderStyle.THIN, range1, sheet);
                // subject name
                XSSFCell subjectNameCell = row.createCell(3);
                subjectNameCell.setCellValue(subjectEntity.getName());
                CellRangeAddress range2 = new CellRangeAddress(rowIndex, rowIndex, 3, 5);
                sheet.addMergedRegion(range2);
                RegionUtil.setBorderBottom(BorderStyle.THIN, range2, sheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, range2, sheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, range2, sheet);
                RegionUtil.setBorderTop(BorderStyle.THIN, range2, sheet);
                // credit
                XSSFCell creditCell = row.createCell(6);
                creditCell.setCellStyle(cellStyle);
//                creditCell.setCellValue(subjectEntity.getCredits() + "");
                // mark
                XSSFCell markCell = row.createCell(7);
                markCell.setCellStyle(cellStyle);
                markCell.setCellValue(marksEntity.getAverageMark() + "");
                // grade
                XSSFCell gradeCell = row.createCell(8);
                gradeCell.setCellStyle(cellStyle);
                gradeCell.setCellValue(marksEntity.getAverageMark() + "");

                if (ordinalNumber < markSize) {
                    sheet.shiftRows(rowIndex, sheet.getLastRowNum(), 1);
                }
                ordinalNumber++;
            }
        }
        workbook.removeSheetAt(0);
    }

    private Map<StudentEntity, List<MarksEntity>> processData(Map<String, String> params) {
        int totalCredit = Integer.parseInt(params.get("credit").isEmpty() ? "0" : params.get("credit"));
        int sCredit = Integer.parseInt(params.get("sCredit").isEmpty() ? "0" : params.get("sCredit"));
        int programId = Integer.parseInt(params.get("programId"));
        int semesterId = Integer.parseInt(params.get("semesterId"));

        Map<StudentEntity, List<MarksEntity>> map = new HashMap<>();
//        List<MarksEntity> marks = marksService.getMarksForGraduatedStudent(programId, semesterId);
//        for (MarksEntity mark : marks) {
//            if (map.get(mark.getStudentId()) != null) {
//                map.get(mark.getStudentId()).add(mark);
//            } else {
//                List<MarksEntity> tmp = new ArrayList<>();
//                tmp.add(mark);
//                map.put(mark.getStudentId(), tmp);
//            }
//        }
//
        Map<StudentEntity, List<MarksEntity>> resultMap = new HashMap<>();

//        for (Map.Entry<StudentEntity, List<MarksEntity>> entry : map.entrySet()) {
//            int credits = 0;
//            int specializedCredits = 0;
//            for (MarksEntity c : entry.getValue()) {
//                if (c.getStatus().toLowerCase().contains("pass") && c.getSubjectId() != null) {
//                    System.out.println(c.getSubjectId().getSubjectId());
//                    int curCredit = c.getSubjectId().getSubjectEntity().getCredits();
//                    credits += curCredit;
//                    if (c.getSubjectId().getSubjectEntity().getIsSpecialized()) {
//                        specializedCredits += curCredit;
//                    }
//                }
//            }
//
//            if (credits >= totalCredit && specializedCredits >= sCredit) {
//                resultMap.put(entry.getKey(),
//                        entry.getValue().stream().filter(m -> m.getStatus().toLowerCase().contains("pass")
//                                && m.getSubjectId() != null
//                                && m.getSubjectId().getSubjectEntity().getIsSpecialized()).collect(Collectors.toList()));
//            }
//        }

        return resultMap;
    }
}
