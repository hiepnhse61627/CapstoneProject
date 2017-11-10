package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.models.Logger;
import com.capstone.models.ReadAndSaveFileToServer;
import com.capstone.models.SubjectModel;
import com.capstone.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.security.auth.Subject;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Controller
public class SubjectCurriculumController {

    ISubjectService subjectService = new SubjectServiceImpl();
    ISubjectCurriculumService subjectCurriculumService = new SubjectCurriculumServiceImpl();

    @Autowired
    ServletContext context;

    private final String folder = "KhungChuongTrinh";

    @RequestMapping("/subcurriculum")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("SubjectCurriculum");
        view.addObject("title", "Các khung chương trình");

        ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
        File[] list = read.readFiles(context, folder);
        view.addObject("files", list);
        return view;
    }

    @RequestMapping("/createcurriculum")
    public ModelAndView Create() {
        ModelAndView view = new ModelAndView("CreateSubjectCurriculum");
        ISubjectService subjectService = new SubjectServiceImpl();
        IProgramService programService = new ProgramServiceImpl();

        List<SubjectEntity> subjectList = subjectService.getAllSubjects();
        view.addObject("subs", subjectList);

        List<ProgramEntity> programList = programService.getAllPrograms();
        view.addObject("programs", programList);

        return view;
    }

    @RequestMapping("/editcurriculum/{curId}")
    public ModelAndView Edit(@PathVariable("curId") int curId) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = emf.createEntityManager();

        ISubjectCurriculumService subjectCurriculumService = new SubjectCurriculumServiceImpl();
        ISubjectService subjectService = new SubjectServiceImpl();
        IProgramService programService = new ProgramServiceImpl();
        ICurriculumService curriculumService = new CurriculumServiceImpl();

        ModelAndView view = new ModelAndView("EditSubjectCurriculum");

        IRealSemesterService realSemesterService = new RealSemesterServiceImpl();
        List<RealSemesterEntity> semesters = realSemesterService.getAllSemester().stream().filter(s -> !s.getSemester().contains("N/A")).collect(Collectors.toList());
        view.addObject("effectionSemester", semesters);

        CurriculumEntity curriculum = curriculumService.getCurriculumById(curId);
        view.addObject("data", curriculum);

        List<SubjectCurriculumEntity> subjectCurriList = subjectCurriculumService.getSubjectCurriculums(curId);
        Map<String, List<SubjectCurriculumEntity>> displayList = new LinkedHashMap<>();
        for (SubjectCurriculumEntity item : subjectCurriList) {
            if (displayList.get("Học kỳ " + item.getTermNumber()) == null) {
                List<SubjectCurriculumEntity> tmp = new ArrayList<>();
                tmp.add(item);
                displayList.put("Học kỳ " + item.getTermNumber(), tmp);
            } else {
                displayList.get("Học kỳ " + item.getTermNumber()).add(item);
            }
        }
        view.addObject("displayList", displayList);

        List<SubjectEntity> subjectList = subjectService.getAllSubjects();
        view.addObject("subs", subjectList);

        List<ProgramEntity> programList = programService.getAllPrograms();
        view.addObject("programs", programList);

        return view;
    }

    @RequestMapping(value = "/createcurriculum", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject Create(@RequestParam List<String> data, @RequestParam String name, @RequestParam int programId) {
        JsonObject obj = new JsonObject();

        ICurriculumService curriculumService = new CurriculumServiceImpl();
        IProgramService programService = new ProgramServiceImpl();
        ISubjectService subjectService = new SubjectServiceImpl();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = emf.createEntityManager();

        try {
            // Create subject curriculum
            ProgramEntity program = programService.getProgramById(programId);

            CurriculumEntity curriculum = new CurriculumEntity();
            curriculum.setName(name);
            curriculum.setProgramId(program);
            List<SubjectCurriculumEntity> list = new ArrayList<>();
            curriculum.setSubjectCurriculumEntityList(list);
            curriculum = curriculumService.createCurriculum(curriculum);

            // Create mapping
            int term = -1;

            int order = 1;
            for (String str : data) {
                if (str.toLowerCase().contains("học kỳ")) {
                    term = Integer.parseInt(str.toLowerCase().split("học kỳ")[1].trim());
                } else {
                    SubjectEntity subject = subjectService.findSubjectById(str);
                    if (subject != null) {
                        SubjectCurriculumEntity s = new SubjectCurriculumEntity();
                        s.setTermNumber(term);
                        s.setOrdinalNumber(order++);
                        s.setCurriculumId(curriculum);
                        s.setSubjectId(subject);
                        curriculum.getSubjectCurriculumEntityList().add(s);
                    }
                }
            }

            em.getTransaction().begin();
            curriculum = em.merge(curriculum);
            em.flush();
            em.refresh(curriculum);
            em.getTransaction().commit();

            obj.addProperty("success", true);
        } catch (Exception e) {
            Logger.writeLog(e);
            obj.addProperty("success", false);
            obj.addProperty("message", e.getMessage());
        }

        return obj;
    }

    @RequestMapping(value = "/editcurriculum", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject Edit(@RequestParam List<String> data, @RequestParam int curriculumId) {
        JsonObject jsonObj = new JsonObject();
//        curriculumName = curriculumName.toUpperCase().trim();

        ICurriculumService curriculumService = new CurriculumServiceImpl();
        ISubjectCurriculumService subjectCurriculumService = new SubjectCurriculumServiceImpl();

        try {
            // Check curriculum is edited or not, if not updates
            CurriculumEntity currentCurriculum = curriculumService.getCurriculumById(curriculumId);
//            if (currentCurriculum.getProgramId().getId() != programId
//                    || !currentCurriculum.getName().equalsIgnoreCase(curriculumName)) {
//                CurriculumEntity entity = curriculumService.getCurriculumByNameAndProgramId(curriculumName, programId);
//                if (entity != null) {
//                    jsonObj.addProperty("success", false);
//                    jsonObj.addProperty("message", "Không thể cập nhật, khung chương trình này đã tồn tại.");
//                    return jsonObj;
//                } else {
//                    ProgramEntity programEntity = new ProgramEntity();
//                    programEntity.setId(programId);
//
//                    currentCurriculum.setName(curriculumName);
//                    currentCurriculum.setProgramId(programEntity);
//                    curriculumService.updateCurriculum(currentCurriculum);
//                }
//            }

            // Update subject curriculum
            List<SubjectCurriculumEntity> oldSubjectCurriList =
                    subjectCurriculumService.getSubjectCurriculums(curriculumId);

            int curTerm = 0;
            int orderinalNumber = 1;
            // "data" là 1 list, vd: Học kỳ 1, Môn 1, Môn 2, Môn 3, Học kỳ 2, Môn 4, Môn 5... theo thứ tự order
            for (String str : data) {
                if (str.contains("Học kỳ")) {
                    curTerm = Integer.parseInt(str.substring("Học kỳ".length() + 1));
                } else {
                    SubjectCurriculumEntity curSubCurriculum = null;
                    // Find existed subject curriculum
                    for (SubjectCurriculumEntity sc : oldSubjectCurriList) {
                        if (sc.getSubjectId().getId().equalsIgnoreCase(str)) {
                            curSubCurriculum = sc;
                            break;
                        }
                    }

                    // Update if subject curriculum is found, crete new record if not
                    if (curSubCurriculum != null) {
                        curSubCurriculum.setTermNumber(curTerm);
                        curSubCurriculum.setOrdinalNumber(orderinalNumber++);
                        subjectCurriculumService.updateCurriculum(curSubCurriculum);
                        oldSubjectCurriList.remove(curSubCurriculum);
                    } else {
                        SubjectEntity subjectEntity = new SubjectEntity();
                        subjectEntity.setId(str);

                        curSubCurriculum = new SubjectCurriculumEntity();
                        curSubCurriculum.setCurriculumId(currentCurriculum);
                        curSubCurriculum.setSubjectId(subjectEntity);
                        curSubCurriculum.setTermNumber(curTerm);
                        curSubCurriculum.setOrdinalNumber(orderinalNumber++);

                        subjectCurriculumService.createCurriculum(curSubCurriculum);
                    }
                }
            }

            // Remove all subject curriculum aren't using
            if (!oldSubjectCurriList.isEmpty()) {
                for (SubjectCurriculumEntity sc : oldSubjectCurriList) {
                    subjectCurriculumService.deleteCurriculum(sc.getId());
                }
            }

            jsonObj.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
            jsonObj.addProperty("success", false);
            jsonObj.addProperty("message", e.getMessage());
        }

        return jsonObj;
    }

    @RequestMapping(value = "/subcurriculum/getSubject", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject GetSubject(@RequestParam String subjectCurId) {
        JsonObject jsonObj = new JsonObject();
        ISubjectService subjectService = new SubjectServiceImpl();
        ISubjectCurriculumService subjectCurriculumService = new SubjectCurriculumServiceImpl();


        try {

            int curId = Integer.parseInt(subjectCurId);
            SubjectCurriculumEntity subjectCurriculumEntity = subjectCurriculumService.getCurriculumById(curId);
            String subjectId = subjectCurriculumEntity.getSubjectId().getId();
            SubjectEntity entity = subjectService.findSubjectById(subjectId);
            String replacementSubject = "";
            for (SubjectEntity list : entity.getSubjectEntityList()) {
                replacementSubject = replacementSubject + "," + list.getId();
            }
            SubjectModel subjectModel = new SubjectModel();
            subjectModel.setSubjectID(entity.getId());
            subjectModel.setSubjectName(entity.getName());
            if (entity.getPrequisiteEntity().getEffectionSemester() != null
                    && !entity.getPrequisiteEntity().getEffectionSemester().isEmpty()) {
                subjectModel.setEffectionSemester(entity.getPrequisiteEntity().getEffectionSemester());
                subjectModel.setPrerequisiteSubject(entity.getPrequisiteEntity().getNewPrequisiteSubs());
                subjectModel.setFailMark(entity.getPrequisiteEntity().getNewFailMark());
            } else {
                subjectModel.setEffectionSemester(null);
                subjectModel.setPrerequisiteSubject(entity.getPrequisiteEntity().getPrequisiteSubs());
                subjectModel.setFailMark(entity.getPrequisiteEntity().getFailMark());
            }

            subjectModel.setCredits(subjectCurriculumEntity.getSubjectCredits());
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

    @RequestMapping("/getsubcurriculum")
    @ResponseBody
    public JsonObject GetSubCurriculum(@RequestParam Map<String, String> params) {
        ICurriculumService curriculumService = new CurriculumServiceImpl();
        JsonObject jsonObj = new JsonObject();

        try {
            int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
            int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
            String searchValue = params.get("sSearch").trim();

            List<CurriculumEntity> curriculumList = curriculumService
                    .getCurriculums(iDisplayStart, iDisplayLength, searchValue);
            List<List<String>> dataList = new ArrayList<>();
            for (CurriculumEntity c : curriculumList) {
                List<String> row = new ArrayList<>();
                row.add(c.getProgramId().getName() + "_" + c.getName());
                row.add(c.getId().toString());

                dataList.add(row);
            }

            int iTotalRecords = curriculumService.countAllCurriculums();
            int iTotalDisplayRecords = curriculumService.countCurriculums(searchValue);
            JsonArray aaData = (JsonArray) new Gson()
                    .toJsonTree(dataList, new TypeToken<List<List<String>>>() {
                    }.getType());

            jsonObj.addProperty("iTotalRecords", iTotalRecords);
            jsonObj.addProperty("iTotalDisplayRecords", iTotalDisplayRecords);
            jsonObj.add("aaData", aaData);
            jsonObj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
        }

        return jsonObj;
    }

    @RequestMapping(value = "/subjectcur/edit")
    @ResponseBody
    public JsonObject EditSubject(@RequestParam("sSubjectId") String subjectId, @RequestParam("sSubjectName") String subjectName,
                                  @RequestParam("sCredits") String credits, @RequestParam("sReplacement") String replacement,
                                  @RequestParam("sPrerequisite") String prerequisite, @RequestParam("sEffectionSemester") String effectionSemester,
                                  @RequestParam("sFailMark") String failMark,@RequestParam("sCurId") String curId) {
        JsonObject jsonObj = new JsonObject();

        try {

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            int curriculumId = Integer.parseInt(curId);
            SubjectModel model = new SubjectModel();
            model.setSubjectID(subjectId);
            model.setSubjectName(subjectName);
            model.setCredits(Integer.parseInt(credits));
            model.setPrerequisiteSubject(prerequisite);
            model.setReplacementSubject(replacement);
            model.setEffectionSemester(effectionSemester);
            if (failMark.isEmpty()) {
                model.setFailMark(0);
            } else {
                model.setFailMark(Integer.parseInt(failMark));
            }


            SubjectModel result = subjectService.updateSubject(model);
            SubjectModel result2 = subjectCurriculumService.updateSubject(model, curriculumId);
            if (!result.isResult() && !result2.isResult()) {
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

    @RequestMapping(value = "/subcurriculum/choose", method = RequestMethod.POST)
    @ResponseBody
    public Callable<JsonObject> choose(@RequestParam("file") String file) {
        Callable<JsonObject> callable = new Callable<JsonObject>() {
            @Override
            public JsonObject call() throws Exception {
                JsonObject obj;
                try {
                    File f = new File(context.getRealPath("/") + "UploadedFiles/" + folder + "/" + file);
                    obj = ReadFile(null, f, false);
                } catch (Exception e) {
                    obj = new JsonObject();
                    obj.addProperty("success", false);
                    obj.addProperty("message", e.getMessage());
                }

                return obj;
            }
        };

        return callable;
    }

    @RequestMapping(value = "/subcurriculum/upload", method = RequestMethod.POST)
    @ResponseBody
    public Callable<JsonObject> upload(@RequestParam("file") MultipartFile file) {
        Callable<JsonObject> callable = new Callable<JsonObject>() {
            @Override
            public JsonObject call() throws Exception {
                JsonObject obj = ReadFile(file, null, true);
                if (obj.get("success").getAsBoolean()) {
                    ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
                    read.saveFile(context, file, folder);
                }

                return obj;
            }
        };

        return callable;
    }

    private JsonObject ReadFile(MultipartFile file, File file2, boolean isNewFile) {
        JsonObject obj = new JsonObject();

        try {
            InputStream is = isNewFile ? file.getInputStream() : new FileInputStream(file2);

            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);

            XSSFRow row;

            int termIndex = -1;
            int subjectIndex = -1;
            int curriculumIndex = -1;
            int programIndex = -1;
            int creditsIndex = -1;

            int rowIndex;
            boolean flag = false;

            for (rowIndex = 0; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
                row = spreadsheet.getRow(rowIndex);

                if (row != null) {
                    for (int cellIndex = row.getFirstCellNum(); cellIndex <= row.getLastCellNum(); cellIndex++) {
                        Cell cell = row.getCell(cellIndex);
                        if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().toLowerCase().contains("curriculumcode")) {
                            curriculumIndex = cellIndex;
                        } else if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().toLowerCase().contains("subjectcode")) {
                            subjectIndex = cellIndex;
                        } else if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().toLowerCase().contains("termno")) {
                            termIndex = cellIndex;
                        } else if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().toLowerCase().contains("program")) {
                            programIndex = cellIndex;
                        } else if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().toLowerCase().contains("credits")) {
                            creditsIndex = cellIndex;
                        }

                        if (termIndex != -1 && subjectIndex != -1 && curriculumIndex != -1 && programIndex != -1 && creditsIndex != -1) {
                            flag = true;
                            break;
                        }
                    }
                }

                if (flag) {
                    break;
                }
            }

            ISubjectService subjectService = new SubjectServiceImpl();
            ISubjectCurriculumService subjectCurriculumService = new SubjectCurriculumServiceImpl();
            ICurriculumService curriculumService = new CurriculumServiceImpl();
            IProgramService programService = new ProgramServiceImpl();

            Map<String, List<SubjectCurriculumEntity>> map = new LinkedHashMap<>();
            for (rowIndex = rowIndex + 1; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {

                System.out.println(rowIndex + " - " + spreadsheet.getLastRowNum());

                row = spreadsheet.getRow(rowIndex);
                if (row != null) {
                    String curriculumName = row.getCell(curriculumIndex).getStringCellValue().trim();
                    String subjectCode = row.getCell(subjectIndex).getStringCellValue().trim();
                    String programName = row.getCell(programIndex).getStringCellValue().trim();
                    Double termNo = row.getCell(termIndex).getNumericCellValue();
                    Double subjectCredits = row.getCell(creditsIndex).getNumericCellValue();

                    if (map.get(curriculumName) == null) {
                        SubjectEntity subjectEntity = subjectService.findSubjectById(subjectCode);
                        if (subjectEntity != null) {
                            ProgramEntity programEntity = programService.getProgramByName(programName);
                            if (programEntity != null) {
                                // create curriculum
                                CurriculumEntity curriculumEntity = curriculumService.getCurriculumByNameAndProgramId(curriculumName, programEntity.getId());
                                if (curriculumEntity != null) {
                                    // create Subject Curriculumn
                                    List<SubjectCurriculumEntity> subjectCurriculumEntityList = new ArrayList<>();
                                    SubjectCurriculumEntity subjectCurriculumEntity = new SubjectCurriculumEntity();
                                    subjectCurriculumEntity.setCurriculumId(curriculumEntity);
                                    subjectCurriculumEntity.setSubjectId(subjectEntity);
                                    subjectCurriculumEntity.setOrdinalNumber(1);
                                    subjectCurriculumEntity.setTermNumber(termNo.intValue());
                                    subjectCurriculumEntity.setSubjectCredits(subjectCredits.intValue());
                                    subjectCurriculumEntityList.add(subjectCurriculumEntity);

                                    map.put(curriculumName, subjectCurriculumEntityList);
                                } else { // curriculum null
                                    curriculumEntity = new CurriculumEntity();
                                    curriculumEntity.setProgramId(programEntity);
                                    curriculumEntity.setName(curriculumName);
                                    curriculumEntity = curriculumService.createCurriculum(curriculumEntity);
                                    // create Subject Curriculumn
                                    List<SubjectCurriculumEntity> subjectCurriculumEntityList = new ArrayList<>();
                                    SubjectCurriculumEntity subjectCurriculumEntity = new SubjectCurriculumEntity();
                                    subjectCurriculumEntity.setCurriculumId(curriculumEntity);
                                    subjectCurriculumEntity.setSubjectId(subjectEntity);
                                    subjectCurriculumEntity.setOrdinalNumber(1);
                                    subjectCurriculumEntity.setTermNumber(termNo.intValue());
                                    subjectCurriculumEntity.setSubjectCredits(subjectCredits.intValue());
                                    subjectCurriculumEntityList.add(subjectCurriculumEntity);

                                    map.put(curriculumName, subjectCurriculumEntityList);
                                }
                            }
                        }
                    } else {
                        SubjectEntity subjectEntity = subjectService.findSubjectById(subjectCode);
                        if (subjectEntity != null) {
                            ProgramEntity programEntity = programService.getProgramByName(programName);
                            if (programEntity != null) {
                                // create curriculum
                                CurriculumEntity curriculumEntity = curriculumService.getCurriculumByNameAndProgramId(curriculumName, programEntity.getId());
                                if (curriculumEntity != null) {
                                    // create Subject Curriculumn
                                    SubjectCurriculumEntity subjectCurriculumEntity = new SubjectCurriculumEntity();
                                    subjectCurriculumEntity.setCurriculumId(curriculumEntity);
                                    subjectCurriculumEntity.setSubjectId(subjectEntity);
                                    subjectCurriculumEntity.setOrdinalNumber(map.get(curriculumName).size() + 1);
                                    subjectCurriculumEntity.setTermNumber(termNo.intValue());
                                    subjectCurriculumEntity.setSubjectCredits(subjectCredits.intValue());

                                    map.get(curriculumName).add(subjectCurriculumEntity);
                                } else { // curriculum null
                                    curriculumEntity = new CurriculumEntity();
                                    curriculumEntity.setProgramId(programEntity);
                                    curriculumEntity.setName(curriculumName);
                                    curriculumEntity = curriculumService.createCurriculum(curriculumEntity);
                                    // create Subject Curriculumn
                                    SubjectCurriculumEntity subjectCurriculumEntity = new SubjectCurriculumEntity();
                                    subjectCurriculumEntity.setCurriculumId(curriculumEntity);
                                    subjectCurriculumEntity.setSubjectId(subjectEntity);
                                    subjectCurriculumEntity.setOrdinalNumber(map.get(curriculumName).size() + 1);
                                    subjectCurriculumEntity.setTermNumber(termNo.intValue());
                                    subjectCurriculumEntity.setSubjectCredits(subjectCredits.intValue());

                                    map.get(curriculumName).add(subjectCurriculumEntity);
                                }
                            }
                        }
                    }
                }
            }
//
            for (Map.Entry<String, List<SubjectCurriculumEntity>> entry : map.entrySet()) {
                entry.getValue().forEach(c -> subjectCurriculumService.createCurriculum(c));
            }

            obj.addProperty("success", true);
        } catch (Exception e) {
            Logger.writeLog(e);
            obj.addProperty("success", false);
            obj.addProperty("message", e.getMessage());
            return obj;
        }

        return obj;
    }

    @RequestMapping(value = "/deletesubcurriculum", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject delete(@RequestParam int curId) {
        JsonObject obj = new JsonObject();
        ICurriculumService service = new CurriculumServiceImpl();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = emf.createEntityManager();
        try {
            CurriculumEntity ent = service.getCurriculumById(curId);
            em.getTransaction().begin();
            ent = em.merge(ent);
            em.remove(ent);
            em.flush();
            em.getTransaction().commit();

            obj.addProperty("success", true);
        } catch (Exception e) {
            obj.addProperty("success", false);
            obj.addProperty("message", e.getMessage());
        }

        return obj;
    }
}
