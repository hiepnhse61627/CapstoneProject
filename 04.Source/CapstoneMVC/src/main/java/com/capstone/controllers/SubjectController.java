package com.capstone.controllers;

import com.capstone.entities.SubjectEntity;
import com.capstone.services.SubjectServiceImpl;
import com.google.gson.JsonObject;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Controller
public class SubjectController {
    @RequestMapping("/subject")
    public String Index() {
        return "UploadSubject";
    }

    @RequestMapping(value = "/subject", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject Upload(@RequestParam("file") MultipartFile file) {
        List<SubjectEntity> columndata = null;
        Map<String, String> prerequisiteList = null;
        JsonObject obj = new JsonObject();

        try {
            prerequisiteList = new HashMap<>();
            columndata = new ArrayList<SubjectEntity>();

            InputStream is = file.getInputStream();
            HSSFWorkbook workbook = new HSSFWorkbook(is);
            HSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                SubjectEntity en = new SubjectEntity();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (row.getRowNum() > 3) { //To filter column headings
                        if (cell.getColumnIndex() == 1) { // Subject code
                            en.setId(cell.getStringCellValue().trim());
                        } else if (cell.getColumnIndex() == 2) { // Abbreviation
                            en.setAbbreviation(cell.getStringCellValue().trim());
                        } else if (cell.getColumnIndex() == 3) { // Subject name
                            en.setName(cell.getStringCellValue().trim());
                        } else if (cell.getColumnIndex() == 4) { // No. of credits
                            en.setCredits((int) cell.getNumericCellValue());
                        } else if (cell.getColumnIndex() == 5) { // Prerequisite
                            String preCode = cell.getStringCellValue().trim();
                            if (!preCode.isEmpty()) {
                                if (preCode.contains("/")) {
                                    preCode = preCode.split("/")[0];
                                }

                                prerequisiteList.put(en.getId(), preCode);
                            }
                        }
                    }
                }

                if (en.getName() != null && !en.getName().isEmpty() && !columndata.stream().anyMatch(c -> c.getId().equals(en.getId()))) {
                    columndata.add(en);
                }
            }
            is.close();

            SubjectServiceImpl service = new SubjectServiceImpl();
            service.insertSubjectList(columndata, prerequisiteList);
        } catch (Exception e) {
            e.printStackTrace();
            obj.addProperty("success", false);
            obj.addProperty("message", e.getMessage());
            return obj ;
        }

        obj.addProperty("success", true);
        return obj;
    }
}
