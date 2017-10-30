package com.capstone.controllers;

import com.capstone.entities.PrequisiteEntity;
import com.capstone.entities.SubjectCurriculumEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.models.*;
import com.capstone.services.*;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

import javax.persistence.*;
import javax.security.auth.Subject;
import javax.servlet.ServletContext;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

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

//        List<SubjectCurriculumEntity> semesterList = termNumberService.findAllTermNumber();

//        view.addObject("semesterList", semesterList);
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

    @RequestMapping("/subjectList")
    public ModelAndView StudentListAll() {
        ModelAndView view = new ModelAndView("SubjectPage");
        view.addObject("title", "Danh sách môn học");

        return view;
    }

    @RequestMapping(value = "/loadSubjectList")
    @ResponseBody
    public JsonObject LoadStudentListAll(@RequestParam Map<String, String> params) {
        JsonObject jsonObj = new JsonObject();

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            String sSearch = params.get("sSearch");
            int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
            int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
            int iTotalRecords = 0;
            int iTotalDisplayRecords = 0;

            String queryStr;
            // Đếm số lượng subject
            queryStr = "SELECT COUNT(s) FROM SubjectEntity s";
            TypedQuery<Integer> queryCounting = em.createQuery(queryStr, Integer.class);
            iTotalRecords = ((Number) queryCounting.getSingleResult()).intValue();

            // Query danh sách subject
            queryStr = "SELECT s FROM SubjectEntity s";
            if (!sSearch.isEmpty()) {
                queryStr += " WHERE s.id LIKE :sId OR s.name LIKE :sName";
            }

            TypedQuery<SubjectEntity> query = em.createQuery(queryStr, SubjectEntity.class);
//                    .setFirstResult(iDisplayStart)
//                    .setMaxResults(iDisplayLength);

            if (!sSearch.isEmpty()) {
                query.setParameter("sId", "%" + sSearch + "%");
                query.setParameter("sName", "%" + sSearch + "%");
            }

            List<SubjectEntity> subjectList = query.getResultList();
            iTotalDisplayRecords = subjectList.size();
            List<List<String>> result = new ArrayList<>();
            subjectList = subjectList.stream().skip(iDisplayStart).limit(iDisplayLength).collect(Collectors.toList());

            for (SubjectEntity std : subjectList) {
                List<String> dataList = new ArrayList<String>() {{
                    add(std.getId());
                    add(std.getName());
                }};
                result.add(dataList);
            }

            JsonArray aaData = (JsonArray) new Gson()
                    .toJsonTree(result, new TypeToken<List<List<String>>>() {
                    }.getType());

            jsonObj.addProperty("iTotalRecords", iTotalRecords);
//            jsonObj.addProperty("iTotalDisplayRecords", iTotalRecords);
            jsonObj.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
            jsonObj.add("aaData", aaData);
            jsonObj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
    }

    @RequestMapping(value = "/getSubject", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject GetSubject(@RequestParam String subjectId) {
        JsonObject jsonObj = new JsonObject();
        ISubjectService subjectService = new SubjectServiceImpl();


        try {
            SubjectEntity entity = subjectService.findSubjectById(subjectId);
            String replacementSubject = "";
            for (SubjectEntity list : entity.getSubjectEntityList()) {
                replacementSubject = replacementSubject + "," + list.getId();
            }
            SubjectModel subjectModel = new SubjectModel();
            subjectModel.setSubjectID(entity.getId());
            subjectModel.setSubjectName(entity.getName());
            subjectModel.setPrerequisiteSubject(entity.getPrequisiteEntity().getPrequisiteSubs());
            subjectModel.setCredits(entity.getCredits());
            subjectModel.setPrerequisiteEffectStart(entity.getPrequisiteEntity().getPrerequisiteEffectStart());
            subjectModel.setPrerequisiteEffectEnd(entity.getPrequisiteEntity().getPrerequisiteEffectEnd());
            if (!replacementSubject.equals("")) {
                subjectModel.setReplacementSubject(replacementSubject.substring(1));
            } else {
                subjectModel.setReplacementSubject(replacementSubject);
            }


            String json = new Gson().toJson(subjectModel);

            jsonObj.addProperty("success", true);
            jsonObj.addProperty("subject", json);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
            jsonObj.addProperty("success", false);
        }

        return jsonObj;
    }


    @RequestMapping(value = "/subjectList/getDetail", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject GetAllStudentMarks(String subjectId) {
        JsonObject jsonObj = new JsonObject();

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            // Lấy thông tin chi tiết môn
            String queryStr = "select s.id, s.name, s.credits from SubjectEntity s where s.id = :sId";
            TypedQuery<SubjectEntity> querySubject = em.createQuery(queryStr, SubjectEntity.class);
            querySubject.setParameter("sId", subjectId);
            SubjectEntity subject = querySubject.getSingleResult();

            SubjectModel model = new SubjectModel();
            model.setSubjectID(subjectId);
            model.setSubjectName(subject.getName());
            model.setCredits(subject.getCredits());

            // Lấy môn tiên quyết
            queryStr = "select p.prequisiteSubs, p.prerequisiteEffectStart ,p.prerequisiteEffectEnd from PrequisiteEntity p where p.subjectId = :sId";
            TypedQuery<PrequisiteEntity> query = em.createQuery(queryStr, PrequisiteEntity.class);
            query.setParameter("sId", subjectId);

            PrequisiteEntity prequisiteSubs = query.getSingleResult();
            model.setPrerequisiteSubject(prequisiteSubs.getPrequisiteSubs());
            model.setPrerequisiteEffectStart(prequisiteSubs.getPrerequisiteEffectStart());
            model.setPrerequisiteEffectEnd(prequisiteSubs.getPrerequisiteEffectEnd());

            String result = new Gson().toJson(model);

            jsonObj.addProperty("success", true);
            jsonObj.addProperty("sSubjectDetail", result);
        } catch (Exception e) {
            jsonObj.addProperty("success", false);
            jsonObj.addProperty("error", e.getMessage());
        }

        return jsonObj;
    }

    private JsonObject ReadFile(MultipartFile file1, File file2, boolean isNewFile) {
        List<SubjectEntity> columndata = new ArrayList<>();
        JsonObject obj = new JsonObject();
        InputStream is = null;

        try {
            if (isNewFile) {
                is = file1.getInputStream();
            } else {
                is = new FileInputStream(file2);
            }

            XSSFWorkbook workbook = new XSSFWorkbook(is);

            List<ReplacementSubject> replace = new ArrayList<>();

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                Iterator<Row> rowIterator = sheet.iterator();

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();
                    SubjectEntity en = new SubjectEntity();
                    en.setIsSpecialized(false);
                    ReplacementSubject re = new ReplacementSubject();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        if (row.getRowNum() > 3) { //To filter column headings
                            System.out.println(row.getRowNum());
                            if (cell.getColumnIndex() == 0) { // Subject code
                                en.setId(cell.getStringCellValue().trim());
                                // set sub code
                                re.setSubCode(en.getId());
                            } else if (cell.getColumnIndex() == 1) { // Abbreviation
                                en.setAbbreviation(cell.getStringCellValue().trim());
                            } else if (cell.getColumnIndex() == 2) { // Subject name
                                en.setName(cell.getStringCellValue().trim());
                            } else if (cell.getColumnIndex() == 3) { // No. of credits
                                try {
                                    en.setCredits((int) cell.getNumericCellValue());
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
                                re.setReplaceCode(replacers);
//                                if (replacers != null && !replacers.isEmpty()) {
//                                    String processed = "";
//                                    String[] data = replacers.split("OR");
//                                    for (String d : data) {
//                                        processed += d.trim() + ",";
//                                    }
//                                    if (processed.lastIndexOf(",") == processed.length() - 1) {
//                                        processed = processed.substring(0, processed.lastIndexOf(","));
//                                    }
//                                    en.setReplacementId(processed.trim());
//                                }
                            }
                        }
                    }

                    if (en.getName() != null && !en.getName().isEmpty() && !columndata.stream().anyMatch(c -> c.getId().equals(en.getId()))) {
                        columndata.add(en);
                        replace.add(re);
                    }
                }
            }

            is.close();

            subjectService.insertSubjectList(columndata);
            subjectService.insertReplacementList(replace);
        } catch (Exception e) {
            e.printStackTrace();
            obj.addProperty("success", false);
            obj.addProperty("message", e.getMessage());
            return obj;
        }

        obj.addProperty("success", true);
        return obj;
    }

    @RequestMapping(value = "/subject/edit")
    @ResponseBody
    public JsonObject EditSubject(@RequestParam("sSubjectId") String subjectId, @RequestParam("sSubjectName") String subjectName,
                                  @RequestParam("sCredits") String credits, @RequestParam("sReplacement") String replacement,
                                  @RequestParam("sPrerequisite") String prerequisite, @RequestParam("sPreEffectStart") String preEffectStart,
                                  @RequestParam("sPreEffectEnd") String preEffectEnd) {
        JsonObject jsonObj = new JsonObject();

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            SubjectModel model = new SubjectModel();
            model.setSubjectID(subjectId);
            model.setSubjectName(subjectName);
            model.setCredits(Integer.parseInt(credits));
            model.setPrerequisiteSubject(prerequisite);
            model.setReplacementSubject(replacement);
            model.setPrerequisiteEffectEnd(preEffectEnd);
            model.setPrerequisiteEffectStart(preEffectStart);

            SubjectModel result = subjectService.updateSubject(model);
            if (!result.isResult()) {
                jsonObj.addProperty("success", false);
                jsonObj.addProperty("message", result.getErrorMessage());
            } else {
                jsonObj.addProperty("success", true);
            }

        } catch (Exception e) {
            Logger.writeLog(e);
            jsonObj.addProperty("false", false);
            jsonObj.addProperty("message", e.getMessage());
        }

        return jsonObj;
    }

    @RequestMapping(value = "/subject/create")
    @ResponseBody
    public JsonObject CreateNewSubject(@RequestParam("sNewSubjectId") String subjectId, @RequestParam("sNewSubjectName") String subjectName,
                                       @RequestParam("sNewCredits") String credits, @RequestParam("sNewReplacement") String replacement,
                                       @RequestParam("sNewPrerequisite") String prerequisite, @RequestParam("sNewPreEffectStart") String preEffectStart,
                                       @RequestParam("sNewPreEffectEnd") String preEffectEnd) {
        JsonObject jsonObj = new JsonObject();

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            SubjectModel model = new SubjectModel();
            model.setSubjectID(subjectId);
            model.setSubjectName(subjectName);
            model.setCredits(Integer.parseInt(credits));
            model.setPrerequisiteSubject(prerequisite);
            model.setReplacementSubject(replacement);
            model.setPrerequisiteEffectEnd(preEffectEnd);
            model.setPrerequisiteEffectStart(preEffectStart);

            SubjectModel result = subjectService.createSubject(model);
            if (!result.isResult()) {
                jsonObj.addProperty("success", false);
                jsonObj.addProperty("message", result.getErrorMessage());
            } else {
                jsonObj.addProperty("success", true);
            }

        } catch (Exception e) {
            Logger.writeLog(e);
            jsonObj.addProperty("false", false);
            jsonObj.addProperty("message", e.getMessage());
        }

        return jsonObj;
    }

    @RequestMapping("/subject/getlinestatus")
    @ResponseBody
    public JsonObject GetLineStatus() {
        JsonObject obj = new JsonObject();
        obj.addProperty("currentLine", subjectService.getCurrentLine());
        obj.addProperty("totalLine", subjectService.getTotalLine());
        return obj;
    }

}
