package com.capstone.exporters;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.PrequisiteEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.models.FailPrequisiteModel;
import com.capstone.models.Ultilities;
import com.capstone.services.ISubjectService;
import com.capstone.services.SubjectServiceImpl;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ExportStudentFailedPrerequisiteImpl implements IExportObject {

    private String EXCEL_TEMPLATE = "/template/DSSV_Fail_Prequisite.xlsx";

    private ISubjectService service = new SubjectServiceImpl();

    @Override
    public String getFileName() {
        return "DSSV_Fail_Prequisite.xlsx";
    }

    @Override
    public void writeData(OutputStream os, Map<String, String> params) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(EXCEL_TEMPLATE);

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        is.close();
        // change to streaming working
        SXSSFWorkbook streamingWorkbook = new SXSSFWorkbook(xssfWorkbook);
        SXSSFSheet streamingSheet = streamingWorkbook.getSheetAt(0);
        streamingSheet.setRandomAccessWindowSize(100);
        // write data to table
        writeDataToTable(streamingWorkbook, streamingSheet, params);
        // write os
        streamingWorkbook.write(os);
    }

    private void writeDataToTable(SXSSFWorkbook workbook, SXSSFSheet spreadsheet, Map<String, String> params) {
        // get params
        String prerequisiteIds = params.get("prequisiteId").trim();
        String subjectId = params.get("subId").trim();
        String[] prerequisiteIdArr = prerequisiteIds.split(",");
        // process list
        List<FailPrequisiteModel> models = processFailPrerequisite(subjectId, prerequisiteIdArr);
        if (models != null && !models.isEmpty()) {
            // style
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            // write data
            int rowIndex = 6;
            for (FailPrequisiteModel model : models) {
                Row row = spreadsheet.createRow(rowIndex);

                Cell rollNumberCell = row.createCell(0);
                rollNumberCell.setCellStyle(cellStyle);
                rollNumberCell.setCellValue(model.getMark().getStudentId().getRollNumber());

                Cell studentNameCell = row.createCell(1);
                studentNameCell.setCellStyle(cellStyle);
                studentNameCell.setCellValue(model.getMark().getStudentId().getFullName());

                Cell subjectWhichPrerequisiteFailCell = row.createCell(2);
                subjectWhichPrerequisiteFailCell.setCellStyle(cellStyle);
                subjectWhichPrerequisiteFailCell.setCellValue(model.getSubjectWhichPrequisiteFail() == null ? "N/A" : model.getSubjectWhichPrequisiteFail());

                Cell prerequisiteCell = row.createCell(3);
                prerequisiteCell.setCellStyle(cellStyle);
                prerequisiteCell.setCellValue(model.getMark().getSubjectId() == null ? "N/A" : model.getMark().getSubjectId().getSubjectId());

                rowIndex++;
            }
        }
    }

    private List<FailPrequisiteModel> processFailPrerequisite(String sub, String[] p) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager manager = emf.createEntityManager();

        List<FailPrequisiteModel> result = new ArrayList<>();
        if (sub.equals("0")) {
            if (p.length > 0) {
                for (String i : p) {
                    SubjectEntity pre = service.findSubjectById(i);
                    if (pre != null) {
                        for (PrequisiteEntity s: pre.getSubOfPrequisiteList()) {
                            TypedQuery<MarksEntity> query = manager.createQuery("SELECT c FROM MarksEntity c WHERE c.subjectId.subjectId = :sub OR c.subjectId.subjectId IN :sList", MarksEntity.class);
                            List<MarksEntity> list = query.setParameter("sub", s.getSubjectEntity().getId()).setParameter("sList", Arrays.asList(p)).getResultList();
                            Ultilities.FilterStudentPassedSubFailPrequisite(list, s.getSubjectEntity().getId(), i, s.getFailMark()).forEach(c -> {
                                if(!result.contains(c)) {
                                    result.add(c);
                                }
                            });
                        }
                    }
                }
            }
        } else {
            if (p.length > 0) {
                for (String i : p) {
                    SubjectEntity pre = service.findSubjectById(i);
                    if (pre != null) {
                        for (PrequisiteEntity s: pre.getSubOfPrequisiteList()) {
                            TypedQuery<MarksEntity> query = manager.createQuery("SELECT c FROM MarksEntity c WHERE c.subjectId.subjectId = :sub OR c.subjectId.subjectId IN :sList", MarksEntity.class);
                            List<MarksEntity> list = query.setParameter("sub", s.getSubjectEntity().getId()).setParameter("sList", Arrays.asList(p)).getResultList();
                            Ultilities.FilterStudentPassedSubFailPrequisite(list, s.getSubjectEntity().getId(), i, s.getFailMark()).forEach(c -> {
                                if(!result.contains(c)) {
                                    result.add(c);
                                }
                            });
                        }
                    }
                }
            }
        }

        return result;
    }
}
