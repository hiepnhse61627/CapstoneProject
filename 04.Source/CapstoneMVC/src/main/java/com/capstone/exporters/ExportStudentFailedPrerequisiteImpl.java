package com.capstone.exporters;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.PrequisiteEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.models.FailPrequisiteModel;
import com.capstone.models.Ultilities;
import com.capstone.services.ISubjectService;
import com.capstone.services.SubjectServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
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
import java.util.*;
import java.util.stream.Collectors;

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
        // process list
        List<FailPrequisiteModel> models = processFailPrerequisite(params);
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
                prerequisiteCell.setCellValue(model.getMark().getSubjectMarkComponentId() == null ? "N/A" : model.getMark().getSubjectMarkComponentId().getSubjectId().getId());

                rowIndex++;
            }
        }
    }

    private List<FailPrequisiteModel> processFailPrerequisite(Map<String, String> params) {
        ISubjectService subjectService = new SubjectServiceImpl();

        String subjectId = params.get("subId").trim();
        String prequisiteStr = params.get("prequisiteId").trim();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = emf.createEntityManager();

        List<FailPrequisiteModel> result = new ArrayList<>();

        Map<String, List<String>> map = new HashMap<>();
        List<String> prequisiteRow = Arrays.asList(prequisiteStr.split(";"));
        if (prequisiteRow.get(0) != null && !prequisiteRow.get(0).isEmpty()) {
            for (String s : prequisiteRow) {
                String[] a = s.split("_");
                if (map.get(a[0]) == null) {
                    List<String> l = new ArrayList<>();
                    l.add(a[1]);
                    map.put(a[0], l);
                } else {
                    map.get(a[0]).add(a[1]);
                }
            }
        }

        String queryStr = "SELECT p FROM MarksEntity p WHERE p.subjectMarkComponentId.subjectId.id IN :sList OR p.subjectMarkComponentId.subjectId.id = :sub";
        TypedQuery<MarksEntity> prequisiteQuery;
        if (!subjectId.equals("0") && !subjectId.isEmpty()) {
            SubjectEntity entity = subjectService.findSubjectById(subjectId);
            PrequisiteEntity prequisite = entity.getPrequisiteEntity();

            prequisiteQuery = em.createQuery(queryStr, MarksEntity.class);
            prequisiteQuery.setParameter("sub", subjectId);

            List<String> l = map.get(subjectId);
            List<String> processedData = new ArrayList<>();
            for (String data : l) {
                String[] s = data.trim().split(",");
                for (String ss : s) {
                    processedData.add(ss.trim());
                }
            }
            prequisiteQuery.setParameter("sList", processedData);

            List<MarksEntity> list = prequisiteQuery.getResultList();
            Ultilities.FilterStudentPassedSubFailPrequisite(list, subjectId, l, prequisite.getFailMark()).forEach(c -> {
                if (!result.contains(c)) {
                    result.add(c);
                }
            });
        } else {
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                SubjectEntity entity = subjectService.findSubjectById(entry.getKey());
                PrequisiteEntity prequisite = entity.getPrequisiteEntity();

                prequisiteQuery = em.createQuery(queryStr, MarksEntity.class);
                prequisiteQuery.setParameter("sub", entry.getKey());

                List<String> processedData = new ArrayList<>();
                for (String data : entry.getValue()) {
                    String[] s = data.trim().split(",");
                    for (String ss : s) {
                        processedData.add(ss.trim());
                    }
                }
                prequisiteQuery.setParameter("sList", processedData);

                List<MarksEntity> list = prequisiteQuery.getResultList();
                Ultilities.FilterStudentPassedSubFailPrequisite(list, entry.getKey(), entry.getValue(), prequisite.getFailMark()).forEach(c -> {
                    if (!result.contains(c)) {
                        result.add(c);
                    }
                });
            }
        }

        return result;
    }
}
