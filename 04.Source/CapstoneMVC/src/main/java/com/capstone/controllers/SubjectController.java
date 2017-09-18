package com.capstone.controllers;

import com.capstone.entities.SubjectEntity;
import com.capstone.services.SubjectServiceImpl;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class SubjectController {
    @RequestMapping("/subject")
    public String Index() {
        return "UploadSubject";
    }

    @RequestMapping(value = "/subject", method = RequestMethod.POST)
    @ResponseBody
    public String Upload(@RequestParam("file") MultipartFile file) {
        List<SubjectEntity> columndata = null;
        try {
            InputStream is = file.getInputStream();
            HSSFWorkbook workbook = new HSSFWorkbook(is);
            HSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            columndata = new ArrayList<SubjectEntity>();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                SubjectEntity en = new SubjectEntity();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (row.getRowNum() > 3) { //To filter column headings
                        if (cell.getColumnIndex() == 1) {// To match column index
                            en.setId(cell.getStringCellValue());
                        } else if (cell.getColumnIndex() == 2) {
                            en.setName(cell.getStringCellValue().trim());
                        } else if (cell.getColumnIndex() == 3) {
                            en.setAbbreviation(cell.getStringCellValue().trim());
                        } else if (cell.getColumnIndex() == 4) {
                            en.setCredits((int) cell.getNumericCellValue());
                        } else if (cell.getColumnIndex() == 5) {
//                            String tmp = cell.getStringCellValue().trim();
//                            if (tmp != null && !tmp.isEmpty()) {
//                                tmp = tmp.split("/")[0];
//                            } else {
//                                tmp = null;
//                            }

//                             INSERT WITH PREQUISITE ERRORS A LOT
                            en.setPrequisiteId(null);
                        }
                    }
                }

                if (en.getName() != null && !en.getName().isEmpty() && !columndata.stream().anyMatch(c -> c.getId().equals(en.getId()))) {
                    columndata.add(en);
                }
            }
            is.close();

            SubjectServiceImpl service = new SubjectServiceImpl();
            service.insertSubjectList(columndata);
        } catch (Exception e) {
            e.printStackTrace();
            return "{ \"success\" : \"false\", \"message\" : \"" + e.getMessage() + "\" }";
        }

        return "{ \"success\" : \"true\" }";
    }
}
