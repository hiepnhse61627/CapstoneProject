package com.capstone.controllers;

import com.capstone.entities.PrequisiteEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.models.ReadAndSaveFileToServer;
import com.capstone.models.ReplacementSubject;
import com.capstone.services.ISubjectService;
import com.capstone.services.SubjectServiceImpl;
import com.google.gson.JsonObject;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.Replace;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.*;

@Controller
public class SubjectController {
    private final String folder = "UploadedSubjectTemplate";
    ISubjectService subjectService = new SubjectServiceImpl();

    @Autowired
    ServletContext context;

    @RequestMapping("/subject")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("UploadSubject");
        view.addObject("title", "Nhập môn học");

        File dir = new File(context.getRealPath("/") + "UploadedFiles/UploadedSubjectTemplate/");
        System.out.println(context.getRealPath("/"));
        if (dir.isDirectory()) {
            File[] listOfFiles = dir.listFiles();
            view.addObject("files", listOfFiles);
        }

        return view;
    }

    @RequestMapping(value = "/subject/upload-exist-file", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject UploadExistFile(@RequestParam("file") String fileName) {
        JsonObject result;
        try {
            File file = new File(context.getRealPath("/") + "UploadedFiles/" + folder + "/" + fileName);
            result = this.ReadFile(null, file, false);
        } catch (Exception e) {
            result = new JsonObject();
            result.addProperty("success", false);
        }

        return result;
    }

    @RequestMapping(value = "/subject/upload", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject Upload(@RequestParam("file") MultipartFile file) {
        JsonObject result = this.ReadFile(file, null, true);
        if (result.get("success").getAsBoolean()) {
            ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
            read.saveFile(context, file, folder);
        }

        return result;
    }

    private JsonObject ReadFile(MultipartFile file1, File file2, boolean isNewFile) {
        List<SubjectEntity> columndata = new ArrayList<SubjectEntity>();
        ;
        JsonObject obj = new JsonObject();
        InputStream is = null;

        try {
            if (isNewFile) {
                is = file1.getInputStream();
            } else {
                is = new FileInputStream(file2);
            }

            XSSFWorkbook workbook = new XSSFWorkbook(is);

//            List<ReplacementSubject> replace = new ArrayList<>();

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                Iterator<Row> rowIterator = sheet.iterator();

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();
                    SubjectEntity en = new SubjectEntity();
                    en.setIsSpecialized(false);
//                    ReplacementSubject re = new ReplacementSubject();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        if (row.getRowNum() > 3) { //To filter column headings
                            System.out.println(row.getRowNum());
                            if (cell.getColumnIndex() == 0) { // Subject code
                                en.setId(cell.getStringCellValue().trim());
                                // set sub code
//                                re.setSubCode(en.getId());
                            } else if (cell.getColumnIndex() == 1) { // Abbreviation
                                en.setAbbreviation(cell.getStringCellValue().trim());
                            } else if (cell.getColumnIndex() == 2) { // Subject name
                                en.setName(cell.getStringCellValue().trim());
                            } else if (cell.getColumnIndex() == 3) { // No. of credits
                                try {
                                    en.setCredits((int)cell.getNumericCellValue());
                                } catch (Exception e) {
                                    en.setCredits(null);
                                }
                            } else if (cell.getColumnIndex() == 4) { // Prerequisite
                                String prequisite = cell.getStringCellValue().trim();
                                PrequisiteEntity prequisiteEntity = new PrequisiteEntity();
                                prequisiteEntity.setSubjectId(en.getId());
                                if (prequisite != null && !prequisite.isEmpty()) {
                                    prequisiteEntity.setFailMark(4);
                                    prequisiteEntity.setPrequisiteSubs(prequisite);
                                }
                                en.setPrequisiteEntity(prequisiteEntity);
                            } else if (cell.getColumnIndex() == 5) { // Replacement
                                String replacers = cell.getStringCellValue().trim();
//                                re.setReplaceCode(replacers);
                                if (replacers != null && !replacers.isEmpty()) {
                                    String processed = "";
                                    String[] data = replacers.split("OR");
                                    for (String d : data) {
                                        processed += d.trim() + ",";
                                    }
                                    if (processed.lastIndexOf(",") == processed.length() - 1) {
                                        processed = processed.substring(0, processed.lastIndexOf(","));
                                    }
//                                    en.setReplacementId(processed.trim());
                                }
                            }
                        }
                    }

                    if (en.getName() != null && !en.getName().isEmpty() && !columndata.stream().anyMatch(c -> c.getId().equals(en.getId()))) {
                        columndata.add(en);
//                        replace.add(re);
                    }
                }
            }

            is.close();

            subjectService.insertSubjectList(columndata);
//            subjectService.insertReplacementList(replace);
        } catch (Exception e) {
            e.printStackTrace();
            obj.addProperty("success", false);
            obj.addProperty("message", e.getMessage());
            return obj;
        }

        obj.addProperty("success", true);
        return obj;
    }

    @RequestMapping("/subject/getlinestatus")
    @ResponseBody
    public JsonObject GetLineStatus() {
        JsonObject obj = new JsonObject();
        obj.addProperty("currentLine", subjectService.getCurrentLine());
        obj.addProperty("totalLine", subjectService.getTotalLine());
        return obj;
    }

//    public void SaveFileToServer(MultipartFile file) {
//        if (!file.isEmpty()) {
//            try {
//                byte[] bytes = file.getBytes();
//
//                File dir = new File(context.getRealPath("/") + "UploadedFiles/UploadedSubjectTemplate/");
//                if (!dir.exists()) {
//                    dir.mkdirs();
//                }
//
//                File serverFile = new File(dir.getAbsolutePath()
//                        + File.separator + file.getOriginalFilename());
//                if (serverFile.exists()) {
//                    SimpleDateFormat df = new SimpleDateFormat("_yyyy-MM-dd-HH-mm-ss");
//                    String suffix = df.format(Calendar.getInstance().getTime());
//                    serverFile = new File(dir.getAbsolutePath() + File.separator + file.getOriginalFilename() + suffix);
//                }
//
//                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
//                stream.write(bytes);
//                stream.close();
//
//                System.out.println(("Server File Location = " + serverFile.getAbsolutePath()));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
