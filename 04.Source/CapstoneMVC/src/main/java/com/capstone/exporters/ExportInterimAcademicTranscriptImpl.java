package com.capstone.exporters;

import com.capstone.entities.*;
import com.capstone.models.Ultilities;
import com.capstone.services.IStudentService;
import com.capstone.services.StudentServiceImpl;
import com.google.common.collect.Table;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class ExportInterimAcademicTranscriptImpl implements IExportObject {

    private String EXCEL_TEMPLATE = "template/InterimAcademicTranscript.xlsx";

    private String fileName = "InterimAcademicTranscript.xlsx";

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
        InputStream is = classLoader.getResourceAsStream(EXCEL_TEMPLATE);

        XSSFWorkbook workbook = new XSSFWorkbook(is);
        // close inputStream
        is.close();
        XSSFSheet spreadsheet = workbook.getSheetAt(0);
        // write data to table
        writeDataToTable(workbook, spreadsheet, params);
        // write to os
        workbook.write(os);
    }

    private void writeDataToTable(XSSFWorkbook workbook, XSSFSheet sheet, Map<String, String> params) {
        Integer studentId = Integer.valueOf(params.get("studentId"));
        StudentEntity studentEntity = studentService.findStudentById(studentId);
        Map<String, List<List<String>>> studentData = processData(studentEntity);
        Map<String, List<List<String>>> reverseMap = new TreeMap<>(Collections.reverseOrder());
        reverseMap.putAll(studentData);

        // write student's information
        XSSFRow row = sheet.getRow(9);
        row.getCell(2).setCellValue(studentEntity.getFullName());
        row.getCell(4).setCellValue(studentEntity.getRollNumber());
        row.getCell(7).setCellValue(studentEntity.getDateOfBirth());

        row = sheet.getRow(10);
        row.getCell(2).setCellValue(studentEntity.getFullName());
        row.getCell(4).setCellValue(studentEntity.getRollNumber());
        row.getCell(7).setCellValue(studentEntity.getDateOfBirth());

        row = sheet.getRow(11);
        row.getCell(2).setCellValue(studentEntity.getProgramId().getFullName());
        // write footer
        row = sheet.getRow(20);
        row.getCell(0).setCellValue(studentEntity.getPassFailCredits() - studentEntity.getPassCredits());
        row.getCell(3).setCellValue(studentEntity.getPassFailCredits());
        row.getCell(5).setCellValue(studentEntity.getPassFailAverageMark());
        // write student's Marks
        int rowIndex = 16;
        // get ordinal number
        int markSize = 0;
        for (Map.Entry<String, List<List<String>>> entry : reverseMap.entrySet()) {
            List<List<String>> subjectMarks = entry.getValue();
            markSize += subjectMarks.size();
        }
        // write
        int ordinalNumber = 1;
        for (Map.Entry<String, List<List<String>>> entry : reverseMap.entrySet()) {
            // data in term
            List<List<String>> subjectMarks = entry.getValue();
            for (List<String> subjectMark : subjectMarks) {
                row = sheet.createRow(rowIndex);

                XSSFCell ordinalNumberCell = row.createCell(0);
                ordinalNumberCell.setCellValue(markSize - ordinalNumber + 1);
                ordinalNumberCell.setCellStyle(style1(workbook));

                XSSFCell subjectCell1 = row.createCell(1);
                subjectCell1.setCellValue(subjectMark.get(0));
                subjectCell1.setCellStyle(style2(workbook));
                CellRangeAddress range1 = new CellRangeAddress(rowIndex, rowIndex, 1, 2);
                sheet.addMergedRegion(range1);
                RegionUtil.setBorderBottom(BorderStyle.THIN, range1, sheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, range1, sheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, range1, sheet);
                RegionUtil.setBorderTop(BorderStyle.THIN, range1, sheet);

                XSSFCell subjectCell2 = row.createCell(3);
                subjectCell2.setCellValue(subjectMark.get(0));
                subjectCell2.setCellStyle(style2(workbook));
                CellRangeAddress range2 = new CellRangeAddress(rowIndex, rowIndex, 3, 4);
                sheet.addMergedRegion(range2);
                RegionUtil.setBorderBottom(BorderStyle.THIN, range2, sheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, range2, sheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, range2, sheet);
                RegionUtil.setBorderTop(BorderStyle.THIN, range2, sheet);

                XSSFCell creditsCell = row.createCell(5);
                creditsCell.setCellValue(subjectMark.get(2));
                creditsCell.setCellStyle(style1(workbook));

                XSSFCell markCell = row.createCell(6);
                markCell.setCellValue(subjectMark.get(1));
                markCell.setCellStyle(style2(workbook));
                CellRangeAddress range3 = new CellRangeAddress(rowIndex, rowIndex, 6, 7);
                sheet.addMergedRegion(range3);
                RegionUtil.setBorderBottom(BorderStyle.THIN, range3, sheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, range3, sheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, range3, sheet);
                RegionUtil.setBorderTop(BorderStyle.THIN, range3, sheet);

                XSSFCell termCell = row.createCell(8);
                termCell.setCellValue(subjectMark.get(3));
                termCell.setCellStyle(style1(workbook));

                if (ordinalNumber < markSize) {
                    sheet.shiftRows(rowIndex, sheet.getLastRowNum(), 1);
                }
                ordinalNumber++;
            }
        }
    }

    private Map<String, List<List<String>>> processData(StudentEntity studentEntity) {
        Map<String, List<List<String>>> subjectMap = new TreeMap<>();
        List<DocumentStudentEntity>  docs = studentEntity.getDocumentStudentEntityList();

        if (docs != null && !docs.isEmpty()) {
            for (DocumentStudentEntity doc : docs) {
                CurriculumEntity cur = doc.getCurriculumId();
                if (cur != null) {
                    List<SubjectCurriculumEntity> curSubjects = cur.getSubjectCurriculumEntityList();
                    List<String> subjects = new ArrayList<>();
                    for (SubjectCurriculumEntity s : curSubjects) {
                        if (!subjects.contains(s.getSubjectId().getId())) subjects.add(s.getSubjectId().getId());
                    }

                    EntityManagerFactory fac = Persistence.createEntityManagerFactory("CapstonePersistence");
                    EntityManager man = fac.createEntityManager();

                    TypedQuery<MarksEntity> query = man.createQuery("SELECT a FROM MarksEntity a WHERE a.isActivated = true and a.studentId.id = :id AND a.subjectMarkComponentId.subjectId.id IN :list", MarksEntity.class);
                    query.setParameter("id", studentEntity.getId());
                    query.setParameter("list", subjects);
                    List<MarksEntity> marks = query.getResultList();

                    if (!marks.isEmpty()) {
                        Table<String, String, List<MarksEntity>> map = Ultilities.StudentSubjectHashmap(marks);
                        Set<String> students = map.rowKeySet();
                        for (String stu : students) {
                            Map<String, List<MarksEntity>> subs = map.row(stu);
                            for (Map.Entry<String, List<MarksEntity>> mark : subs.entrySet()) {

                                List<String> result = new ArrayList<>();
                                MarksEntity r = null;
                                for (MarksEntity m : mark.getValue()) {
                                    r = m;
                                    if (m.getStatus().toLowerCase().contains("pass") || m.getStatus().toLowerCase().contains("exempt")) {
                                        break;
                                    }
                                }
                                if (r != null) {
                                    result.add(r.getSubjectMarkComponentId().getSubjectId().getName());
                                    result.add(r.getAverageMark().toString());
                                }

                                SubjectCurriculumEntity c = null;
                                for (SubjectCurriculumEntity s : curSubjects) {
                                    if (s.getSubjectId().getId().equals(mark.getKey())) {
                                        result.add(s.getSubjectCredits().toString());
                                        result.add(s.getTermNumber().toString());
                                        c = s;
                                    }
                                }

                                String term;
                                if (c != null) {
                                    term = "Học kỳ " + c.getTermNumber();
                                } else {
                                    term = "Khác";
                                }

                                if (subjectMap.get(term) == null) {
                                    List<List<String>> o = new ArrayList<>();
                                    o.add(result);
                                    subjectMap.put(term, o);
                                } else {
                                    subjectMap.get(term).add(result);
                                }
                            }
                        }
                    }
                }
            }
        }

        return subjectMap;
    }

    private CellStyle style1(XSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = workbook.createFont();
        font.setFontName("Cambria");
        cellStyle.setFont(font);

        return cellStyle;
    }

    private CellStyle style2(XSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = workbook.createFont();
        font.setFontName("Cambria");
        cellStyle.setFont(font);

        return cellStyle;
    }
}
