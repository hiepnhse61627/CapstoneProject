package com.capstone.controllers;

import com.capstone.entities.*;
import com.capstone.models.ReadAndSaveFileToServer;
import com.capstone.services.ISubjectCurriculumService;
import com.capstone.services.ISubjectService;
import com.capstone.services.SubjectCurriculumServiceImpl;
import com.capstone.services.SubjectServiceImpl;
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
import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class SubjectCurriculumController {

    @Autowired
    ServletContext context;

    private final String folder = "KhungChuongTrinh";

    @RequestMapping("/subcurriculum")
    public ModelAndView Index() {
        ModelAndView view = new ModelAndView("SubjectCurriculum");
        ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
        File[] list = read.readFiles(context, folder);
        view.addObject("files", list);
        return view;
    }

    @RequestMapping("/editcurriculum/{curId}")
    public ModelAndView Edit(@PathVariable("curId") int curId) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = emf.createEntityManager();

        ISubjectCurriculumService service = new SubjectCurriculumServiceImpl();
        ISubjectService service2 = new SubjectServiceImpl();

        ModelAndView view = new ModelAndView("EditSubjectCurriculum");
        SubjectCurriculumEntity ent = service.getCurriculumById(curId);
        List<CurriculumMappingEntity> sortedList = ent.getCurriculumMappingEntityList();
        sortedList.sort(Comparator.comparing(c -> c.getOrdering()));
        System.out.println();
        sortedList.forEach(c -> System.out.print(c.getOrdering() + ", "));
        view.addObject("data", ent);

        Map<String, List<CurriculumMappingEntity>> unsortedmap = new HashMap<>();
        Map<String, List<CurriculumMappingEntity>> map = new TreeMap<>(unsortedmap);
        for (CurriculumMappingEntity en : sortedList) {
            if (map.get(en.getTerm()) == null) {
                List<CurriculumMappingEntity> tmp = new ArrayList<>();
                tmp.add(en);
                map.put(en.getTerm(), tmp);
            } else {
                map.get(en.getTerm()).add(en);
            }
        }
        view.addObject("list", map);

        List<SubjectEntity> l2 = service2.getAllSubjects();
        view.addObject("subs", l2);

        return view;
    }

    @RequestMapping(value = "/editcurriculum", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject Edit(@RequestParam() List<String> data, @RequestParam int id, @RequestParam String name, @RequestParam String des) {
        JsonObject obj = new JsonObject();
        ISubjectCurriculumService service = new SubjectCurriculumServiceImpl();
        ISubjectService service2 = new SubjectServiceImpl();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = emf.createEntityManager();

        try {
            SubjectCurriculumEntity ent = service.getCurriculumById(id);

            if (ent != null) {
                if (name != null && !name.isEmpty()) ent.setName(name);
                if (des != null && !des.isEmpty()) ent.setDescription(des);

                System.out.println(ent.getName() + " - " + ent.getDescription());

                List<CurriculumMappingEntity> l = ent.getCurriculumMappingEntityList();
                List<CurriculumMappingEntity> notin = new ArrayList<>();
                for (CurriculumMappingEntity m : l) {
                    boolean flag = false;
                    for (String s : data) {
                        if (!s.toLowerCase().contains("học kỳ") && m.getSubjectEntity().getId().equals(s)) flag = true;
                    }
                    if (!flag) {
                        notin.add(m);
                    }
                }

                if (!notin.isEmpty()) {
                    for (CurriculumMappingEntity m : notin) {
                        em.getTransaction().begin();
                        CurriculumMappingEntity tmp5 = em.merge(m);
                        em.remove(tmp5);
                        em.flush();
                        l.remove(m);
                        ent.setCurriculumMappingEntityList(l);
                        SubjectCurriculumEntity tmp4 = em.merge(ent);
                        em.flush();
                        em.refresh(tmp4);
                        em.getTransaction().commit();

                        ent = tmp4;
                    }
                }

                String term = "";
                int order = 1;
                for (String s : data) {
                    if (s.toLowerCase().contains("học kỳ")) {
                        term = s;
                    } else {
                        l = ent.getCurriculumMappingEntityList();

                        for (CurriculumMappingEntity c : l) {
                            if (c.getSubjectEntity().getId().equals(s)) {
                                int i = l.indexOf(c);
                                c.setTerm(term);
                                c.setOrdering(order++);
                                em.getTransaction().begin();
                                CurriculumMappingEntity tmp = em.merge(c);
                                em.flush();
                                em.refresh(tmp);
                                l.set(i, tmp);
                                ent.setCurriculumMappingEntityList(l);
                                SubjectCurriculumEntity tmp3 = em.merge(ent);
                                em.flush();
                                em.refresh(tmp3);
                                em.getTransaction().commit();
                                break;
                            }
                        }

                        boolean flag = false;
                        for (CurriculumMappingEntity c : l) {
                            if (c.getSubjectEntity().getId().equals(s)) {
                                flag = true;
                                break;
                            }
                        }

                        if (!flag) {
                            if (em.find(SubjectEntity.class, s) != null) {
                                em.getTransaction().begin();

                                CurriculumMappingEntity c = new CurriculumMappingEntity();
                                c.setOrdering(order++);
                                c.setTerm(term);

                                CurriculumMappingEntityPK pk = new CurriculumMappingEntityPK();
                                pk.setSubId(s);
                                pk.setCurId(ent.getId());
                                c.setCurriculumMappingEntityPK(pk);

                                em.persist(c);
                                em.flush();
                                em.refresh(c);

                                List<CurriculumMappingEntity> tmp = ent.getCurriculumMappingEntityList();
                                tmp.add(c);
                                ent.setCurriculumMappingEntityList(tmp);
                                SubjectCurriculumEntity tmp2 = em.merge(ent);
                                em.flush();
                                em.refresh(tmp2);

                                em.getTransaction().commit();
                            }
                        }
                    }
                }
            }
            obj.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            obj.addProperty("success", false);
            obj.addProperty("message", e.getMessage());
        }

        return obj;
    }

    @RequestMapping("/getsubcurriculum")
    @ResponseBody
    public JsonObject GetSubCurriculum(@RequestParam Map<String, String> params) {
        ISubjectCurriculumService service = new SubjectCurriculumServiceImpl();
        ISubjectService service2 = new SubjectServiceImpl();

        try {
            JsonObject data = new JsonObject();

            List<SubjectCurriculumEntity> dataList = service.getAllSubjectCurriculum();

            List<SubjectCurriculumEntity> displayList = new ArrayList<>();
            if (!dataList.isEmpty()) {
                displayList = dataList.stream().skip(Integer.parseInt(params.get("iDisplayStart"))).limit(Integer.parseInt(params.get("iDisplayLength"))).collect(Collectors.toList());
            }

            ArrayList<ArrayList<String>> result = new ArrayList<>();
            if (!displayList.isEmpty()) {
                displayList.forEach(m -> {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(m.getName());
                    tmp.add(m.getDescription());
                    tmp.add(m.getId().toString());
                    result.add(tmp);
                });
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result, new TypeToken<List<MarksEntity>>() {
            }.getType());

            data.addProperty("iTotalRecords", dataList.size());
            data.addProperty("iTotalDisplayRecords", dataList.size());
            data.add("aaData", aaData);
            data.addProperty("sEcho", params.get("sEcho"));

            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(value = "/subcurriculum/choose", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject choose(@RequestParam("file") String file) {
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

    @RequestMapping(value = "/subcurriculum/upload", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject upload(@RequestParam("file") MultipartFile file) {
        JsonObject obj = ReadFile(file, null, true);
        if (obj.get("success").getAsBoolean()) {
            ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
            read.saveFile(context, file, folder);
        }

        return obj;
    }

    @RequestMapping(value = "/deletesubcurriculum", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject delete(@RequestParam int curId) {
        JsonObject obj = new JsonObject();
        ISubjectCurriculumService service = new SubjectCurriculumServiceImpl();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = emf.createEntityManager();
        try {
            SubjectCurriculumEntity ent = service.getCurriculumById(curId);

            em.getTransaction().begin();

            SubjectCurriculumEntity b = em.merge(ent);
            em.remove(b);
            em.flush();

            em.getTransaction().commit();

            obj.addProperty("success", true);
        } catch (Exception e) {
            obj.addProperty("success", false);
            obj.addProperty("message", e.getMessage());
        }

        return obj;
    }

    private JsonObject ReadFile(MultipartFile file, File file2, boolean isNewFile) {
        JsonObject obj = new JsonObject();

        try {
            InputStream is = isNewFile ? file.getInputStream() : new FileInputStream(file2);

            HSSFWorkbook workbook = new HSSFWorkbook(is);
            HSSFSheet spreadsheet = workbook.getSheetAt(1);

            HSSFRow row;
            int termIndex = 0;
            int subjectIndex = 0;
            int rowIndex = 0;
            boolean flag = false;

            String name = "";
            String description = "";

            for (rowIndex = termIndex; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
                row = spreadsheet.getRow(rowIndex);

                if (row != null) {
                    for (int cellIndex = row.getFirstCellNum(); cellIndex <= row.getLastCellNum(); cellIndex++) {
                        Cell cell = row.getCell(cellIndex);
                        if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().toLowerCase().contains("học kỳ")) {
                            termIndex = cellIndex;
                            subjectIndex = cellIndex - 1;
                            flag = true;
                            break;
                        } else if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().toLowerCase().contains("khung")) {
                            description = cell.getStringCellValue();
                        } else if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().toLowerCase().contains("ngành")) {
                            name = cell.getStringCellValue();
                        }
                    }
                }

                if (flag) {
                    break;
                }
            }

            List<String> data = new ArrayList<>();

            int found = 0;
            while ((found = findRow(spreadsheet, found, "học kỳ")) != -1) {

                row = spreadsheet.getRow(found);
                Cell term = row.getCell(termIndex);
                String termString = term.getStringCellValue();
                data.add(termString);

                for (rowIndex = found + 1; rowIndex < findRow(spreadsheet, row.getRowNum() + 1, "học kỳ"); rowIndex++) {
                    row = spreadsheet.getRow(rowIndex);
                    Cell subject = row.getCell(subjectIndex);
                    String subString = subject.getStringCellValue();
                    if (subString != null && !subString.isEmpty()) data.add(subString);
                }

                rowIndex = row.getRowNum() + 1;
                found = row.getRowNum() + 1;
            }

            for (rowIndex = rowIndex; rowIndex < spreadsheet.getLastRowNum(); rowIndex++) {
                row = spreadsheet.getRow(rowIndex);
                Cell subject = row.getCell(subjectIndex);
                String subString = subject.getStringCellValue();
                if (subString != null && !subString.isEmpty()) data.add(subString);
            }

            obj = Create(data, name, description);
        } catch (Exception e) {
            obj.addProperty("success", false);
            obj.addProperty("message", e.getMessage());
            return obj;
        }

        return obj;
    }

    public JsonObject Create(List<String> data, String name, String des) {
        JsonObject obj = new JsonObject();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = emf.createEntityManager();

        try {
            SubjectCurriculumEntity ent = new SubjectCurriculumEntity();
            if (name != null && !name.isEmpty()) ent.setName(name);
            if (des != null && !des.isEmpty()) ent.setDescription(des);

            em.getTransaction().begin();
            em.persist(ent);
            em.flush();
            em.refresh(ent);
            em.getTransaction().commit();

            System.out.println(ent.getName() + " - " + ent.getDescription());

            List<CurriculumMappingEntity> l = new ArrayList<>();

            String term = "";
            int order = 1;
            for (String s : data) {
                if (s.toLowerCase().contains("học kỳ")) {
                    term = s;
                } else {
                    String[] split = s.split("/");
                    if (split.length == 0) {
                        if (em.find(SubjectEntity.class, s) != null) {
                            em.getTransaction().begin();

                            CurriculumMappingEntity c = new CurriculumMappingEntity();
                            c.setOrdering(order++);
                            c.setTerm(term);

                            CurriculumMappingEntityPK pk = new CurriculumMappingEntityPK();
                            pk.setSubId(s);
                            pk.setCurId(ent.getId());
                            c.setCurriculumMappingEntityPK(pk);

                            em.persist(c);
                            em.flush();
                            em.refresh(c);

                            l.add(c);

                            em.getTransaction().commit();
                        }
                    } else {
                        for (String s1 : split) {
                            if (em.find(SubjectEntity.class, s1) != null) {
                                em.getTransaction().begin();

                                CurriculumMappingEntity c = new CurriculumMappingEntity();
                                c.setOrdering(order++);
                                c.setTerm(term);

                                CurriculumMappingEntityPK pk = new CurriculumMappingEntityPK();
                                pk.setSubId(s1);
                                pk.setCurId(ent.getId());
                                c.setCurriculumMappingEntityPK(pk);

                                em.persist(c);
                                em.flush();
                                em.refresh(c);

                                l.add(c);

                                em.getTransaction().commit();

                                break;
                            }
                        }
                    }
                }
            }

            em.getTransaction().begin();

            ent.setCurriculumMappingEntityList(l);

            l.forEach(c -> System.out.print(c.getOrdering() + ", "));

            SubjectCurriculumEntity tmp2 = em.merge(ent);
            em.flush();
            em.refresh(tmp2);

            em.getTransaction().commit();

            obj.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();

            obj.addProperty("success", false);
            obj.addProperty("message", e.getMessage());
        }

        return obj;
    }

    private int findRow(HSSFSheet sheet, int currentRow, String cellContent) {
        for (int rowIndex = currentRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                for (int cellIndex = row.getFirstCellNum(); cellIndex <= row.getLastCellNum(); cellIndex++) {
                    Cell cell = row.getCell(cellIndex);
                    if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().toLowerCase().contains(cellContent)) {
                        return row.getRowNum();
                    }
                }
            }
        }
        return -1;
    }
}
