package com.capstone.controllers;

import com.capstone.entities.SubjectEntity;
import com.capstone.entities.SubjectMarkComponentEntity;
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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
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
    public List<SubjectEntity> Upload(@RequestParam("file") MultipartFile file) {
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
                            en.setName(cell.getStringCellValue());
                        } else if (cell.getColumnIndex() == 3) {
                            en.setAbbreviation(cell.getStringCellValue());
                        } else if (cell.getColumnIndex() == 4) {
                            en.setCredits((int) cell.getNumericCellValue());
                        } else if (cell.getColumnIndex() == 5) {
                            en.setPrequisiteId(cell.getStringCellValue());
                        }
                    }
                }
                if (en.getName() != null && !en.getName().isEmpty()) {
                    columndata.add(en);
                }
            }
            is.close();

            EntityManagerFactory fac = Persistence.createEntityManagerFactory("CapstoneProject");
            EntityManager manager = fac.createEntityManager();
            TypedQuery<SubjectEntity> query = manager.createQuery("SELECT c FROM SubjectEntity c", SubjectEntity.class);
            List<SubjectEntity> cur = query.getResultList();


            manager.getTransaction().begin();
            for (SubjectEntity en : columndata) {
                try {
                    if (!cur.contains(en)) {
//                        SubjectMarkComponentEntity entity = new SubjectMarkComponentEntity();
//                        entity.setSubjectId(en.getId());
//                        entity.setComponentPercent(0);
//
//                        entity.setSubjectBySubjectId(en);
//                        en.setSubjectMarkComponentById(entity);

                        manager.persist(en);
//                        manager.persist(entity);
//                        manager.flush();
//                        manager.clear();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            manager.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return columndata;
    }
}
