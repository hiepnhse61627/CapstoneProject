package com.capstone.controllers;

import com.capstone.entities.PrequisiteEntity;
import com.capstone.entities.PrequisiteEntityPK;
import com.capstone.entities.SubjectEntity;
import com.capstone.models.ReadAndSaveFileToServer;
import com.capstone.services.ISubjectService;
import com.capstone.services.SubjectServiceImpl;
import com.google.gson.JsonObject;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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

            HSSFWorkbook workbook = new HSSFWorkbook(is);

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                HSSFSheet sheet = workbook.getSheetAt(i);
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
                                try {
                                    en.setCredits((int)cell.getNumericCellValue());
                                } catch (Exception e) {
                                    en.setCredits(null);
                                }
                            } else if (cell.getColumnIndex() == 5) { // Prerequisite
                                String preCode = cell.getStringCellValue().trim();
                                if (!preCode.isEmpty()) {
                                    if (preCode.contains("/")) {
                                        preCode = preCode.split("/")[0];
                                    }

                                    if (preCode.contains(",")) {
                                        List<PrequisiteEntity> prequisites = new ArrayList<>();
                                        PrequisiteEntity pre = new PrequisiteEntity();

                                        String[] code = preCode.split(",");
                                        for (String c: code) {
                                            if (c != null && !c.isEmpty()) {
                                                pre = new PrequisiteEntity();

                                                PrequisiteEntityPK pk = new PrequisiteEntityPK();
                                                pk.setSubId(en.getId());
                                                pk.setPrequisiteSubId(c.trim());
                                                pre.setPrequisiteEntityPK(pk);
                                                pre.setFailMark(4);

                                                prequisites.add(pre);
                                            }
                                        }
                                        en.setSubOfPrequisiteList(prequisites);

//                                        prequisites = new ArrayList<>();
//                                        pre = new SubjectEntity();
//                                        pre.setId(en.getId());
//                                        prequisites.add(pre);
//                                        en.setPrequisiteEntityList(prequisites);
                                    } else {
                                        List<PrequisiteEntity> prequisites = new ArrayList<>();
                                        PrequisiteEntity pre = new PrequisiteEntity();

                                        PrequisiteEntityPK pk = new PrequisiteEntityPK();
                                        pk.setSubId(en.getId());
                                        pk.setPrequisiteSubId(preCode);
                                        pre.setPrequisiteEntityPK(pk);
                                        pre.setFailMark(4);

                                        prequisites.add(pre);
                                        en.setSubOfPrequisiteList(prequisites);
                                    }
                                }
                            }
                        }
                    }

                    if (en.getName() != null && !en.getName().isEmpty() && !columndata.stream().anyMatch(c -> c.getId().equals(en.getId()))) {
                        columndata.add(en);
                    }
                }
            }

            is.close();

            subjectService.insertSubjectList(columndata);
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
