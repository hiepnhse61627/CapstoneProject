package com.capstone.controllers;

import com.capstone.models.ImportedMarkObject;
import com.capstone.models.Logger;
import com.capstone.models.ReadAndSaveFileToServer;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.capstone.entities.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.swing.text.Document;
import java.io.*;

import java.text.SimpleDateFormat;
import java.util.*;

import java.util.concurrent.Callable;
import java.util.stream.Collectors;


@Controller
public class UploadController {

    private boolean isCancel;
    private boolean isPause;

    private final String xlsExcelExtension = "xls";
    private final String xlsxExcelExtension = "xlsx";
    private final String folder = "DSSV-StudentsList";
    private final String marksFolder = "Marks-StudentMarks";
    private int totalLine;
    private int currentLine;
    private int startRowNumber = -1;
    private int endRowNumber = -1;

    @Autowired
    ServletContext context;

    IStudentService studentService = new StudentServiceImpl();
    ISubjectService subjectService = new SubjectServiceImpl();
    IRealSemesterService realSemesterService = new RealSemesterServiceImpl();
    ISubjectMarkComponentService subjectMarkComponentService = new SubjectMarkComponentServiceImpl();
    ICourseService courseService = new CourseServiceImpl();
    ISubjectCurriculumService subjectCurriculumService = new SubjectCurriculumServiceImpl();
    IMarksService marksService = new MarksServiceImpl();
    IProgramService programService = new ProgramServiceImpl();
    ICurriculumService curriculumService = new CurriculumServiceImpl();
    IDocumentService documentService = new DocumentServiceImpl();
    IDocTypeService docTypeService = new DocTypeServiceImpl();
    IMarkComponentService markComponentService = new MarkComponentServiceImpl();

    /**
     * --------------STUDENTS------------
     **/
    @RequestMapping(value = "/goUploadStudentList")
    public ModelAndView goUploadStudentListPage() {
        ModelAndView view = new ModelAndView("uploadStudentList");
        view.addObject("title", "Nhập danh sách sinh viên");

        ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
        File[] list = read.readFiles(context, folder);
        view.addObject("files", list);
        return view;
    }

    @RequestMapping("/getlinestatus")
    @ResponseBody
    public JsonObject getCurrentLine() {
        JsonObject obj = new JsonObject();
        obj.addProperty("current", studentService.getCurrentLine());
        obj.addProperty("total", studentService.getTotalLine());
        return obj;
    }

    @RequestMapping(value = "/uploadStudentExistFile", method = RequestMethod.POST)
    @ResponseBody
    public Callable<JsonObject> chooseExistFile(@RequestParam("file") String file) {
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

    @RequestMapping(value = "/uploadStudentList", method = RequestMethod.POST)
    @ResponseBody
    public Callable<JsonObject> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam boolean update) {
        Callable<JsonObject> callable = new Callable<JsonObject>() {
            @Override
            public JsonObject call() throws Exception {
                JsonObject obj;
                if (update) {
                    obj = UpdateFile(file, null, true);
                } else {
                    obj = ReadFile(file, null, true);
                }

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

            String originalFileName = isNewFile ? file.getOriginalFilename() : file2.getName();
            String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1, originalFileName.length());

            List<DocumentStudentEntity> studentList = new ArrayList<>();
            List<ProgramEntity> programList = programService.getAllPrograms();
            List<CurriculumEntity> curriculumList = curriculumService.getAllCurriculums();

            Date now = Calendar.getInstance().getTime();
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.DATE, -30);
            Date dateBefore30Days = cal.getTime();

            // Get template document
            List<DocumentEntity> docList = documentService.getAllDocuments();
            DocumentEntity templateDoc = null;
            if (!docList.isEmpty()) {
                templateDoc = docList.get(0);
            } else {
                List<DocTypeEntity> docTypeList = docTypeService.getAllDocTypes();
                DocTypeEntity docType = null;
                if (!docTypeList.isEmpty()) {
                    docType = docTypeList.get(0);
                } else {
                    docType = new DocTypeEntity();
                    docType.setName("Đang học");
                    docTypeService.createDocType(docType);
                }
                templateDoc = new DocumentEntity();
                templateDoc.setDocTypeId(docType);
                templateDoc.setCode("000000");

                documentService.createDocument(templateDoc);
            }

            DocumentEntity docChangingCurriculum = documentService.getDocumentById(2);

            Workbook workbook = null;
            Sheet spreadsheet = null;
            Row row = null;
            if (extension.equals(xlsExcelExtension)) {
                workbook = new HSSFWorkbook(is);
                spreadsheet = workbook.getSheetAt(0);
            } else if (extension.equals(xlsxExcelExtension)) {
                workbook = new XSSFWorkbook(is);
                spreadsheet = workbook.getSheetAt(0);
            } else {
                obj.addProperty("success", false);
                obj.addProperty("message", "Chỉ chấp nhận file excel");
                return obj;
            }

            int excelDataIndex = 1;

            int rollNumberIndex = 0;
            int oldRollNumberIndex = 1;
            int studentNameIndex = 2;
            int dateOfBirthIndex = 3;
            int genderIndex = 4;
            int programNameIndex = 5;
            int oldProgramNameIndex = 6;
            int curriculumIndex = 13;
            int termIndex = 14;
            int email = 26;

            int mainClass = 15;

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            for (int rowIndex = excelDataIndex; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
                row = spreadsheet.getRow(rowIndex);
                if (row != null) {
                    StudentEntity student = new StudentEntity();

                    Cell rollNumberCell = row.getCell(rollNumberIndex);
                    Cell oldRollNumberCell = row.getCell(oldRollNumberIndex);
                    Cell studentNameCell = row.getCell(studentNameIndex);
                    Cell dateOfBirthCell = row.getCell(dateOfBirthIndex);
                    Cell genderCell = row.getCell(genderIndex);
                    Cell programNameCell = row.getCell(programNameIndex);
                    Cell oldProgramNameCell = row.getCell(oldProgramNameIndex);
                    Cell curriculumCell = row.getCell(curriculumIndex);
                    Cell emailcell = row.getCell(email);

                    Cell termCell = row.getCell(termIndex);

                    // Get Student Info
                    if (rollNumberCell != null) {
                        String rollNumber = rollNumberCell.getCellType() == Cell.CELL_TYPE_STRING ?
                                rollNumberCell.getStringCellValue() : (rollNumberCell.getNumericCellValue() == 0 ?
                                "" : Integer.toString((int) rollNumberCell.getNumericCellValue()));
                        if (!rollNumber.isEmpty()) {
                            student.setRollNumber(rollNumber);
                        }
                    }

                    if (emailcell != null) {
                        String e = emailcell.getCellType() == Cell.CELL_TYPE_STRING ?
                                emailcell.getStringCellValue() : (emailcell.getNumericCellValue() == 0 ?
                                "" : Integer.toString((int) emailcell.getNumericCellValue()));
                        if (!e.isEmpty()) {
                            student.setEmail(e);
                        }
                    }

                    String studentFullName;
                    if (studentNameCell != null &&
                            !(studentFullName = studentNameCell.getStringCellValue().trim()).isEmpty()) {
                        student.setFullName(studentFullName);
                    }

                    String dateOfBirth;
                    if (dateOfBirthCell != null &&
                            !(dateOfBirth = dateOfBirthCell.getStringCellValue().trim()).isEmpty()) {
                        if (dateOfBirth.matches("\\d{1,2}-\\d{1,2}-\\d{4}")) {
                            student.setDateOfBirth(sdf.parse(dateOfBirth));
                        }
                    }

                    String gender;
                    if (genderCell != null &&
                            !(gender = genderCell.getStringCellValue().trim()).isEmpty()) {
                        student.setGender(gender.equalsIgnoreCase("Nam"));
                    }

                    ProgramEntity studentProgram;
                    String programName;
                    if (programNameCell != null &&
                            !(programName = programNameCell.getStringCellValue().trim()).isEmpty()) {
                        studentProgram = this.findOrCreateProgram(programList, programName);
                        student.setProgramId(studentProgram);
                    }

                    CurriculumEntity currentCurriculum = null;
                    String curriculumStr;
                    if (curriculumCell != null && !(curriculumStr = curriculumCell.getStringCellValue().trim()).isEmpty()) {
                        int pos = curriculumStr.indexOf("_");
                        if (pos != -1) {
                            String curPogramName = curriculumStr.substring(0, pos);
                            String curCurriName = curriculumStr.substring(pos + 1);

                            currentCurriculum = findOrCreateCurriculum(programList, curriculumList, curPogramName, curCurriName);
                        }
                    }

                    if (termCell != null) {
                        double term = termCell.getNumericCellValue();
                        student.setTerm(((Number) term).intValue());
                    }

                    if (oldRollNumberCell != null) {
                        OldRollNumberEntity old = new OldRollNumberEntity();
                        old.setStudentId(student);
                        old.setChangedCurriculumDate(new Date(19, 8, 1996));
                        if (oldRollNumberCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                            old.setOldRollNumber(String.valueOf(oldRollNumberCell.getNumericCellValue()));
                        } else {
                            old.setOldRollNumber(oldRollNumberCell.getStringCellValue());
                        }
                        student.setOldRollNumberEntityList(new ArrayList<>());
                        student.getOldRollNumberEntityList().add(old);
                    }

                    if (student.getRollNumber() != null) {
                        DocumentStudentEntity docStd = new DocumentStudentEntity();
                        docStd.setStudentId(student);
                        docStd.setCurriculumId(currentCurriculum);
                        docStd.setDocumentId(templateDoc);
                        docStd.setCreatedDate(now);
                        studentList.add(docStd);

                        if (oldRollNumberCell != null && oldProgramNameCell != null &&
                                !oldProgramNameCell.getStringCellValue().trim().isEmpty()) {
                            DocumentStudentEntity oldDoc = new DocumentStudentEntity();
                            oldDoc.setStudentId(student);
                            oldDoc.setCurriculumId(null);
                            oldDoc.setDocumentId(docChangingCurriculum);
                            oldDoc.setCreatedDate(new Date(19, 8, 1996));
                            studentList.add(oldDoc);
                        }

                        if (currentCurriculum != null && student.getProgramId() != null) {
                            if (!currentCurriculum.getProgramId().getName().equals("PC")
                                    && !currentCurriculum.getProgramId().getName().equals(student.getProgramId().getName())) {
                                CurriculumEntity parentCurriculum = findOrCreateCurriculum(
                                        programList, curriculumList, student.getProgramId().getName(), currentCurriculum.getName());

                                DocumentStudentEntity parentDocStudent = new DocumentStudentEntity();
                                parentDocStudent.setStudentId(student);
                                parentDocStudent.setCurriculumId(parentCurriculum);
                                parentDocStudent.setDocumentId(templateDoc);
                                parentDocStudent.setCreatedDate(dateBefore30Days);

                                studentList.add(parentDocStudent);
                            }
                        }
                    }
                }
            }

            studentService.createStudentList(studentList);
        } catch (Exception e) {
            obj.addProperty("success", false);
            obj.addProperty("message", e.getMessage());
            e.printStackTrace();
            return obj;
        }

        obj.addProperty("success", true);
        return obj;
    }

    private JsonObject UpdateFile(MultipartFile file, File file2, boolean isNewFile) {
        JsonObject obj = new JsonObject();

        try {
            InputStream is = isNewFile ? file.getInputStream() : new FileInputStream(file2);

            String originalFileName = isNewFile ? file.getOriginalFilename() : file2.getName();
            String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1, originalFileName.length());

//            List<DocumentStudentEntity> studentList = new ArrayList<>();
            List<ProgramEntity> programList = programService.getAllPrograms();
            List<CurriculumEntity> curriculumList = curriculumService.getAllCurriculums();
            Date now = new Date();

            // Get template document
            List<DocumentEntity> docList = documentService.getAllDocuments();
//            if (!docList.isEmpty()) {
//                templateDoc = docList.get(0);
//            } else {
//                List<DocTypeEntity> docTypeList = docTypeService.getAllDocTypes();
//                DocTypeEntity docType = null;
//                if (!docTypeList.isEmpty()) {
//                    docType = docTypeList.get(0);
//                } else {
//                    docType = new DocTypeEntity();
//                    docType.setName("Đang học");
//                    docTypeService.createDocType(docType);
//                }
//                templateDoc = new DocumentEntity();
//                templateDoc.setDocTypeId(docType);
//                templateDoc.setCode("000000");
//
//                documentService.createDocument(templateDoc);
//            }

            Workbook workbook = null;
            Sheet spreadsheet = null;
            Row row = null;
            if (extension.equals(xlsExcelExtension)) {
                workbook = new HSSFWorkbook(is);
                spreadsheet = workbook.getSheetAt(0);

            } else if (extension.equals(xlsxExcelExtension)) {
                workbook = new XSSFWorkbook(is);
                spreadsheet = workbook.getSheetAt(0);
            } else {
                obj.addProperty("success", false);
                obj.addProperty("message", "Chỉ chấp nhận file excel");
                return obj;
            }

            int excelDataIndex = 1;

            int rollNumberIndex = 0;
            int oldRollNumberIndex = 1;
            int studentNameIndex = 2;
            int dateOfBirthIndex = 3;
            int genderIndex = 4;
            int programNameIndex = 5;
//            int curriculumIndex = 13;
            int termIndex = 16;
            int email = 28;
            int statusIndex = 19;

            int changeCurIndex = 6;
            int cur1 = 13;
            int cur2 = 14;
            int cur3 = 15;

            int mainClass = 15;

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            for (int rowIndex = excelDataIndex; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
                System.out.println(rowIndex + " - " + spreadsheet.getLastRowNum());
                row = spreadsheet.getRow(rowIndex);
                if (row != null) {
//                    StudentEntity student = new StudentEntity();

                    Cell rollNumberCell = row.getCell(rollNumberIndex);
                    Cell studentNameCell = row.getCell(studentNameIndex);
                    Cell dateOfBirthCell = row.getCell(dateOfBirthIndex);
                    Cell genderCell = row.getCell(genderIndex);
                    Cell programNameCell = row.getCell(programNameIndex);
//                    Cell curriculumCell = row.getCell(curriculumIndex);
                    Cell emailcell = row.getCell(email);
                    Cell changeCurCell = row.getCell(changeCurIndex);
                    Cell oldRollNumCell = row.getCell(oldRollNumberIndex);
                    Cell statusCell = row.getCell(statusIndex);

                    Cell cur1Cell = row.getCell(cur1);
                    Cell cur2Cell = row.getCell(cur2);
                    Cell cur3Cell = row.getCell(cur3);

                            Cell termCell = row.getCell(termIndex);

                    // Get Student Info
                    if (rollNumberCell != null) {
                        String rollNumber = rollNumberCell.getCellType() == Cell.CELL_TYPE_STRING ?
                                rollNumberCell.getStringCellValue() : (rollNumberCell.getNumericCellValue() == 0 ?
                                "" : Integer.toString((int) rollNumberCell.getNumericCellValue()));
                        if (!rollNumber.isEmpty()) {
                            StudentEntity student = studentService.findStudentByRollNumber(rollNumber);
                            if (student != null) {
                                student.setRollNumber(rollNumber);
                                student = studentService.cleanDocumentAndOldRollNumber(student);

                                // update student
                                if (emailcell != null) {
                                    String e = emailcell.getCellType() == Cell.CELL_TYPE_STRING ?
                                            emailcell.getStringCellValue() : (emailcell.getNumericCellValue() == 0 ?
                                            "" : Integer.toString((int) emailcell.getNumericCellValue()));
                                    if (!e.isEmpty()) {
                                        student.setEmail(e);
                                    }
                                }

                                String studentFullName;
                                if (studentNameCell != null &&
                                        !(studentFullName = studentNameCell.getStringCellValue().trim()).isEmpty()) {
                                    student.setFullName(studentFullName);
                                }

                                String dateOfBirth;
                                if (dateOfBirthCell != null &&
                                        !(dateOfBirth = dateOfBirthCell.getStringCellValue().trim()).isEmpty()) {
                                    if (dateOfBirth.matches("\\d{1,2}-\\d{1,2}-\\d{4}")) {
                                        student.setDateOfBirth(sdf.parse(dateOfBirth));
                                    }
                                }

                                String gender;
                                if (genderCell != null &&
                                        !(gender = genderCell.getStringCellValue().trim()).isEmpty()) {
                                    student.setGender(gender.equalsIgnoreCase("Nam"));
                                }

                                ProgramEntity studentProgram;
                                String programName;
                                if (programNameCell != null &&
                                        !(programName = programNameCell.getStringCellValue().trim()).isEmpty()) {
                                    studentProgram = this.findOrCreateProgram(programList, programName);
                                    student.setProgramId(studentProgram);
                                }

                                if (termCell != null) {
                                    double term = termCell.getNumericCellValue();
                                    student.setTerm(((Number) term).intValue());
                                }

                                if (student.getRollNumber() != null) {
//                                    List<DocTypeEntity> docTypeList = docTypeService.getAllDocTypes();

                                    DocTypeEntity docType = docTypeService.findDocType(statusCell.getStringCellValue());
                                    if (docType == null) {
                                        docType = new DocTypeEntity();
                                        docType.setName(statusCell.getStringCellValue());
                                        docTypeService.createDocType(docType);
                                    }
                                    DocumentEntity templateDoc = documentService.getDocumentByDocTypeId(docType.getId());
                                    if (templateDoc == null) {
                                        templateDoc = new DocumentEntity();
                                        templateDoc.setDocTypeId(docType);
                                        templateDoc.setCode(RandomStringUtils.randomAlphanumeric(10).toUpperCase());
                                        documentService.createDocument(templateDoc);
                                    }

                                    CurriculumEntity cur = null;
                                    String curriculumStr;
                                    Calendar cal = Calendar.getInstance();
                                    int j = 0;
                                    for (int i = cur1; i < cur3 + 1; i++) {
                                        cal.add(Calendar.YEAR, j++);
                                        if (row.getCell(i) != null && !(curriculumStr = row.getCell(i).getStringCellValue().trim()).isEmpty()) {
                                            int pos = curriculumStr.indexOf("_");
                                            if (pos != -1) {
                                                String curPogramName = curriculumStr.substring(0, pos);
                                                String curCurriName = curriculumStr.substring(pos + 1);

                                                cur = findOrCreateCurriculum(programList, curriculumList, curPogramName, curCurriName);

                                                DocumentStudentEntity docStd = new DocumentStudentEntity();
                                                docStd.setStudentId(student);
                                                docStd.setCurriculumId(cur);
                                                docStd.setDocumentId(templateDoc);
                                                docStd.setCreatedDate(cal.getTime());
                                                student.getDocumentStudentEntityList().add(docStd);
                                            }
                                        }
                                    }
//                                    if (curriculumCell != null && !(curriculumStr = curriculumCell.getStringCellValue().trim()).isEmpty()) {
//                                        int pos = curriculumStr.indexOf("_");
//                                        if (pos != -1) {
//                                            String beforeCur;
//                                            String curPogramName = curriculumStr.substring(0, pos);
//
//                                            if (beforeCurCell != null) {
//                                                beforeCur = beforeCurCell.getStringCellValue();
//                                            } else {
//                                                beforeCur = curPogramName;
//                                            }
//
//                                            String curCurriName = curriculumStr.substring(pos + 1);
//
//                                            currentCurriculum = findOrCreateCurriculum(programList, curriculumList, curPogramName, curCurriName);
//                                            if (!curPogramName.equals(beforeCur)) {
//                                                beforeCurriculum = findOrCreateCurriculum(programList, curriculumList, beforeCur, curCurriName);
//                                            }
//                                        }
//                                    }

//                                    if (beforeCurriculum != null) {
//                                        DocumentStudentEntity docStd = new DocumentStudentEntity();
//                                        docStd.setStudentId(student);
//                                        docStd.setCurriculumId(beforeCurriculum);
//                                        docStd.setDocumentId(templateDoc);
//                                        docStd.setCreatedDate(new Date(2010, 1, 1));
//                                        student.getDocumentStudentEntityList().add(docStd);
//                                    }
//
//                                    DocumentStudentEntity docStd = new DocumentStudentEntity();
//                                    docStd.setStudentId(student);
//                                    docStd.setCurriculumId(currentCurriculum);
//                                    docStd.setDocumentId(templateDoc);
//                                    docStd.setCreatedDate(now);
//                                    student.getDocumentStudentEntityList().add(docStd);

                                    cal.add(Calendar.YEAR, -10);
                                    if (changeCurCell != null) {
                                        DocumentStudentEntity oldDoc = new DocumentStudentEntity();
                                        oldDoc.setStudentId(student);
                                        oldDoc.setCurriculumId(null);
                                        oldDoc.setDocumentId(documentService.getDocumentById(2));
                                        oldDoc.setCreatedDate(cal.getTime());
                                        student.getDocumentStudentEntityList().add(oldDoc);
                                    }

                                    if (oldRollNumCell != null) {
                                        OldRollNumberEntity old = new OldRollNumberEntity();
                                        old.setStudentId(student);
                                        old.setChangedCurriculumDate(cal.getTime());
                                        if (oldRollNumCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                            old.setOldRollNumber(String.valueOf(oldRollNumCell.getNumericCellValue()));
                                        } else {
                                            old.setOldRollNumber(oldRollNumCell.getStringCellValue());
                                        }
                                        student.getOldRollNumberEntityList().add(old);
                                    }
                                }

                                studentService.saveStudent(student);
                            }
                        }
                    }
                }
            }

//            studentService.createStudentList(studentList);
        } catch (Exception e) {
            obj.addProperty("success", false);
            obj.addProperty("message", e.getMessage());
            e.printStackTrace();
            return obj;
        }

        obj.addProperty("success", true);
        return obj;
    }

    private ProgramEntity findOrCreateProgram(List<ProgramEntity> list, String programName) {
        ProgramEntity program = null;

        for (ProgramEntity p : list) {
            if (p.getName().equals(programName)) {
                program = p;
                break;
            }
        }

        if (program == null) {
            program = new ProgramEntity();
            program.setName(programName);
            program.setOjt(90);
            program.setCapstone(80);
            program.setGraduate(70);
            programService.createProgram(program);
            list.add(program);
        }

        return program;
    }

    private CurriculumEntity findOrCreateCurriculum(List<ProgramEntity> programList, List<CurriculumEntity> curriList,
                                                    String programName, String curriName) {
        CurriculumEntity curriculum = null;

        for (CurriculumEntity c : curriList) {
            if (c.getProgramId().getName().equals(programName)
                    && c.getName().equals(curriName)) {
                curriculum = c;
                break;
            }
        }

        if (curriculum == null) {
            curriculum = new CurriculumEntity();
            curriculum.setProgramId(this.findOrCreateProgram(programList, programName));
            curriculum.setName(curriName);
            curriculumService.createCurriculum(curriculum);
            curriList.add(curriculum);
        }

        return curriculum;
    }

    // Old file
//    private JsonObject ReadFile(MultipartFile file, File file2, boolean isNewFile) {
//        IProgramService programService = new ProgramServiceImpl();
//        ICurriculumService curriculumService = new CurriculumServiceImpl();
//        IDocumentService documentService = new DocumentServiceImpl();
//        IDocTypeService docTypeService = new DocTypeServiceImpl();
//        JsonObject obj = new JsonObject();
//
//        try {
//            InputStream is = isNewFile ? file.getInputStream() : new FileInputStream(file2);
//
//            String originalFileName = isNewFile ? file.getOriginalFilename() : file2.getName();
//            String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1, originalFileName.length());
//
//            List<DocumentStudentEntity> studentList = new ArrayList<>();
//            List<ProgramEntity> programList = programService.getAllPrograms();
//            List<CurriculumEntity> curriculumList = curriculumService.getAllCurriculums();
//
//            // Get template document
//            List<DocumentEntity> docList = documentService.getAllDocuments();
//            DocumentEntity templateDoc = null;
//            if (!docList.isEmpty()) {
//                templateDoc = docList.get(0);
//            } else {
//                List<DocTypeEntity> docTypeList = docTypeService.getAllDocTypes();
//                DocTypeEntity docType = null;
//                if (!docTypeList.isEmpty()) {
//                    docType = docTypeList.get(0);
//                } else {
//                    docType = new DocTypeEntity();
//                    docType.setName("Đang học");
//                    docTypeService.createDocType(docType);
//                }
//                templateDoc = new DocumentEntity();
//                templateDoc.setDocTypeId(docType);
//                templateDoc.setCode("000000");
//
//                documentService.createDocument(templateDoc);
//            }
//
//            Workbook workbook = null;
//            Sheet spreadsheet = null;
//            Row row = null;
//            if (extension.equals(xlsExcelExtension)) {
//                workbook = new HSSFWorkbook(is);
//                spreadsheet = workbook.getSheetAt(0);
//
//            } else if (extension.equals(xlsxExcelExtension)) {
//                workbook = new XSSFWorkbook(is);
//                spreadsheet = workbook.getSheetAt(0);
//            } else {
//                obj.addProperty("success", false);
//                obj.addProperty("message", "Chỉ chấp nhận file excel");
//                return obj;
//            }
//
//            int rollNumberIndex = 1;
//            int studentNameIndex = 2;
//            int excelDataIndex = 3;
//            int programFullNameIndex = 3;
//            int programNameIndex = 4;
//            int curriculumIndex = 7;
//
//            for (int rowIndex = excelDataIndex; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
//                row = spreadsheet.getRow(rowIndex);
//                if (row != null) {
//                    StudentEntity student = new StudentEntity();
//
//                    Cell rollNumberCell = row.getCell(rollNumberIndex);
//                    Cell studentNameCell = row.getCell(studentNameIndex);
//                    Cell programNameCell = row.getCell(programNameIndex);
//                    Cell programFullNameCell = row.getCell(programFullNameIndex);
//                    Cell curriculumCell = row.getCell(curriculumIndex);
//
//                    // Get Student Info
//                    if (rollNumberCell != null) {
//                        String rollNumber = rollNumberCell.getCellType() == Cell.CELL_TYPE_STRING ?
//                                rollNumberCell.getStringCellValue() : (rollNumberCell.getNumericCellValue() == 0 ?
//                                "" : Integer.toString((int) rollNumberCell.getNumericCellValue()));
//                        if (!rollNumber.isEmpty()) {
//                            student.setRollNumber(rollNumber);
//                        }
//                    }
//
//                    String studentFullName;
//                    if (studentNameCell != null &&
//                            !(studentFullName = studentNameCell.getStringCellValue().trim()).isEmpty()) {
//                        student.setFullName(studentFullName);
//                    }
//
//                    // Get Program Info
//                    ProgramEntity curProgram = null;
//                    boolean isProgramExist = false;
//                    String programName;
//                    if (programNameCell != null &&
//                            !(programName = programNameCell.getStringCellValue().trim()).isEmpty()) {
//
//                        boolean isFound = false;
//                        for (ProgramEntity program : programList) {
//                            if (!isFound && program.getName().equals(programName)) {
//                                isFound = true;
//                                isProgramExist = true;
//                                curProgram = program;
//                                break;
//                            }
//                        }
//
//                        if (!isFound) {
//                            curProgram = new ProgramEntity();
//                            curProgram.setName(programName);
//                        }
//                    }
//                    String programFullName;
//                    if (!isProgramExist && programFullNameCell != null &&
//                            !(programFullName = programFullNameCell.getStringCellValue().trim()).isEmpty()) {
//                        curProgram.setFullName(programFullName);
//                    }
//                    if (!isProgramExist && curProgram != null) {
//                        programService.createProgram(curProgram);
//                        programList.add(curProgram);
//                    }
//
//                    // Get Curriculum Info
//                    CurriculumEntity currentCurriculum = null;
//                    if (curProgram != null) {
//                        String curriculumName;
//                        if (curriculumCell != null &&
//                                !(curriculumName = curriculumCell.getStringCellValue().trim()).isEmpty()) {
//
//                            int pos = curriculumName.indexOf("_");
//                            if (pos != -1) {
//                                curriculumName = curriculumName.substring(pos + 1);
//                            }
//
//                            boolean isFound = false;
//                            for (CurriculumEntity curriculum : curriculumList) {
//                                if (!isFound && curriculum.getName().equals(curriculumName)
//                                        && curriculum.getProgramId().getName().equals(curProgram.getName())) {
//                                    isFound = true;
//                                    currentCurriculum = curriculum;
//                                    break;
//                                }
//                            }
//                            if (!isFound) {
//                                currentCurriculum = new CurriculumEntity();
//                                currentCurriculum.setName(curriculumName);
//                                currentCurriculum.setProgramId(curProgram);
//                                curriculumService.createCurriculum(currentCurriculum);
//
//                                curriculumList.add(currentCurriculum);
//                            }
//                        }
//                    }
//
//                    if (student.getRollNumber() != null) {
//                        DocumentStudentEntity docStd = new DocumentStudentEntity();
//                        docStd.setStudentId(student);
//                        docStd.setCurriculumId(currentCurriculum);
//                        docStd.setDocumentId(templateDoc);
//
//                        studentList.add(docStd);
//                    }
//                }
//            }
//
//            studentService.createStudentList(studentList);
//        } catch (Exception e) {
//            obj.addProperty("success", false);
//            obj.addProperty("message", e.getMessage());
//            e.printStackTrace();
//            return obj;
//        }
//
//        obj.addProperty("success", true);
//        return obj;
//    }

    /**
     * --------------MARKS------------
     **/
    @RequestMapping(value = "/goUploadStudentMarks")
    public ModelAndView goUploadStudentMarksPage() {
        ModelAndView view = new ModelAndView("uploadStudentMarks");
        view.addObject("title", "Nhập danh sách điểm");

        ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
        File[] list = read.readFiles(context, marksFolder);
        view.addObject("files", list);
        return view;
    }

//    @RequestMapping(value = "/threadmili", method = RequestMethod.POST)
//    @ResponseBody
//    public JsonObject Threadmili(@RequestParam boolean thread) {
//        JsonObject obj = new JsonObject();
//	}
//
//    public JsonObject chooseExistMarkFile(@RequestParam("file") String file, @RequestParam("startRow") int startRow, @RequestParam("endRow") int endRow) {
//        this.totalLine = 0;
//        this.currentLine = 0;
//        startRowNumber = startRow;
//        endRowNumber = endRow;
//        JsonObject jsonObject;
//        try {
//            if (thread) {
//                isPause = true;
//            } else {
//                isPause = false;
//            }
//            obj.addProperty("success", true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return obj;
//    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject Cancel(@RequestParam boolean exit) {
        isCancel = exit;
        JsonObject obj = new JsonObject();
        obj.addProperty("success", true);
        return obj;
    }

    @RequestMapping(value = "/upload-exist-marks-file", method = RequestMethod.POST)
    @ResponseBody
    public Callable<JsonObject> chooseExistMarkFile(@RequestParam("file") String file, @RequestParam("startRow") int startRow, @RequestParam("endRow") int endRow) {
        isCancel = false;
        isPause = false;
        System.out.println("Cancel is " + String.valueOf(isCancel));

        Callable<JsonObject> callable = () -> {
            this.totalLine = 0;
            this.currentLine = 0;
            startRowNumber = startRow;
            endRowNumber = endRow;
            JsonObject jsonObject;
            try {
                File f = new File(context.getRealPath("/") + "UploadedFiles/" + marksFolder + "/" + file);
                jsonObject = readMarkFile(null, f, false);
            } catch (Exception ex) {
                jsonObject = new JsonObject();
                jsonObject.addProperty("success", false);
                jsonObject.addProperty("message", ex.getMessage());
            }

            return jsonObject;
        };

        return callable;
    }

    @RequestMapping(value = "/uploadStudentMarks", method = RequestMethod.POST)
    @ResponseBody
    public Callable<JsonObject> uploadStudentMarks(@RequestParam("file") MultipartFile file, @RequestParam("startRow") int startRow, @RequestParam("endRow") int endRow) throws IOException {
        isCancel = false;
        isPause = false;
        System.out.println("Cancel is " + String.valueOf(isCancel));

        Callable<JsonObject> callable = () -> {
            this.totalLine = 0;
            this.currentLine = 0;
            startRowNumber = startRow;
            endRowNumber = endRow;
            JsonObject jsonObject = readMarkFile(file, null, true);
            if (jsonObject.get("success").getAsBoolean()) {
                ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
                read.saveFile(context, file, marksFolder);
            }

            return jsonObject;
        };

        return callable;
    }

    private JsonObject readMarkFile(MultipartFile file, File file2, boolean isNewFile) {
        JsonObject jsonObject = new JsonObject();
        List<MarksEntity> marksEntities = new ArrayList<MarksEntity>();

        try {
            InputStream is;
            if (isNewFile) {
                is = file.getInputStream();
            } else {
                is = new FileInputStream(file2);
            }

            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);

            XSSFRow row;
            int excelDataIndex = startRowNumber < 0 ? 0 : startRowNumber;
            int lastRow = endRowNumber < 1 ? spreadsheet.getLastRowNum() : endRowNumber;
            this.totalLine = lastRow - startRowNumber + 1;

            int semesterNameIndex = 0;
            int rollNumberIndex = 1;
            int subjectCodeIndex = 2;
            int averageMarkIndex = 4;
            int statusIndex = 5;

            this.currentLine = 0;
            String markComponentName = "AVERAGE";
            Map<StudentEntity, List<ImportedMarkObject>> studentMarksMap = new HashMap<>();
            for (int rowIndex = excelDataIndex; rowIndex <= lastRow; rowIndex++) {
                if (!isCancel) {
                    while (isPause) {
                        if (isCancel) break;
                        System.out.println("is Pausing");
                        Thread.sleep(1000);
                    }

                    if (isCancel) break;

                    row = spreadsheet.getRow(rowIndex);

                    Cell rollNumberCell = row.getCell(rollNumberIndex);
                    if (rollNumberCell != null) {
                        StudentEntity studentEntity = studentService.findStudentByRollNumber(rollNumberCell.getStringCellValue().trim());
                        if (studentEntity != null) {
                            if (studentMarksMap.get(studentEntity) != null) {
                                ImportedMarkObject importedMarkObject = new ImportedMarkObject();
                                importedMarkObject.setStudentEntity(studentEntity);

                                Cell semesterNameCell = row.getCell(semesterNameIndex);
                                Cell subjectCodeCell = row.getCell(subjectCodeIndex);
                                Cell averageMarkCell = row.getCell(averageMarkIndex);
                                Cell statusCell = row.getCell(statusIndex);

                                String semesterName = "";
                                if (semesterNameCell != null) {
                                    semesterName = semesterNameCell.getStringCellValue().trim().toUpperCase().replaceAll(" ", "");
                                    if (semesterName.contains("_H2")) {
                                        semesterName = semesterName.substring(0, semesterName.indexOf("_"));
                                    }
                                    importedMarkObject.setSemesterName(semesterName.toUpperCase());
                                }

                                if (subjectCodeCell != null) {
                                    String subjectCode = subjectCodeCell.getStringCellValue().trim().toUpperCase();
                                    // find subject code
                                    SubjectEntity subjectEntity = subjectService.findSubjectById(subjectCode);
                                    importedMarkObject.setSubjectCode(subjectEntity);
                                }

                                if (averageMarkCell != null) {
                                    importedMarkObject.setAverageMark(averageMarkCell.getNumericCellValue());
                                }

                                if (statusCell != null) {
                                    importedMarkObject.setStatus(statusCell.getStringCellValue());
                                }

                                studentMarksMap.get(studentEntity).add(importedMarkObject);
                            } else {
                                ImportedMarkObject importedMarkObject = new ImportedMarkObject();
                                importedMarkObject.setStudentEntity(studentEntity);

                                Cell semesterNameCell = row.getCell(semesterNameIndex);
                                Cell subjectCodeCell = row.getCell(subjectCodeIndex);
                                Cell averageMarkCell = row.getCell(averageMarkIndex);
                                Cell statusCell = row.getCell(statusIndex);

                                String semesterName = "";
                                if (semesterNameCell != null) {
                                    semesterName = semesterNameCell.getStringCellValue().trim().toUpperCase().replaceAll(" ", "");
                                    if (semesterName.contains("_H2")) {
                                        semesterName = semesterName.substring(0, semesterName.indexOf("_"));
                                    }
                                    importedMarkObject.setSemesterName(semesterName.toUpperCase());
                                }

                                if (subjectCodeCell != null) {
                                    String subjectCode = subjectCodeCell.getStringCellValue().trim().toUpperCase();
                                    // find subject code
                                    SubjectEntity subjectEntity = subjectService.findSubjectById(subjectCode);
                                    importedMarkObject.setSubjectCode(subjectEntity);
                                }

                                if (averageMarkCell != null) {
                                    importedMarkObject.setAverageMark(averageMarkCell.getNumericCellValue());
                                }

                                if (statusCell != null) {
                                    importedMarkObject.setStatus(statusCell.getStringCellValue());
                                }
                                List<ImportedMarkObject> listImported = new ArrayList<>();
                                listImported.add(importedMarkObject);

                                studentMarksMap.put(studentEntity, listImported);
                            }
                        }
                    }
                    this.currentLine++;
                } else {
                    break;
                }
            }
            is.close();
            // processing
            List<RealSemesterEntity> allSemester = Ultilities.SortSemesters(realSemesterService.getAllSemester());
            for (Map.Entry<StudentEntity, List<ImportedMarkObject>> entry : studentMarksMap.entrySet()) {
                Integer studentId = entry.getKey().getId();
                Integer termNo = entry.getKey().getTerm();
                List<ImportedMarkObject> importMarks = entry.getValue().stream().filter(f -> f.getSubjectCode() != null).collect(Collectors.toList());
                List<SubjectCurriculumEntity> subjectsInCurriculum = subjectCurriculumService.getSubjectIds(studentId, termNo);
                Set<String> importedSubjectIdsSet = importMarks.stream().map(f -> f.getSubjectCode().getId()).collect(Collectors.toSet());
                List<SubjectCurriculumEntity> subjectNotInMarkList = subjectsInCurriculum.stream().filter(s -> !importedSubjectIdsSet.contains(s.getSubjectId().getId())).collect(Collectors.toList());
                List<ImportedMarkObject> processedList = new ArrayList<>();
                for (SubjectCurriculumEntity subjectCd : subjectNotInMarkList) {
                    ImportedMarkObject importedMarkObject = new ImportedMarkObject();
                    importedMarkObject.setStudentEntity(entry.getKey());
                    importedMarkObject.setAverageMark(-1.0);
                    importedMarkObject.setSubjectCode(subjectCd.getSubjectId());
                    // process semester
                    List<SubjectCurriculumEntity> subjectsInCurrentTerm =
                            subjectsInCurriculum.stream().filter(s -> s.getTermNumber() == subjectCd.getTermNumber()).collect(Collectors.toList());
                    Set<String> subjectCdsInCurrentTerm =
                            subjectsInCurrentTerm.stream().map(s -> s.getSubjectId().getId()).collect(Collectors.toSet());
                    List<ImportedMarkObject> subjectMarkInCurrentTerm =
                            importMarks.stream().filter(i -> subjectCdsInCurrentTerm.contains(i.getSubjectCode().getId())).collect(Collectors.toList());
                    List<String> semesters = subjectMarkInCurrentTerm.stream().map(s -> s.getSemesterName()).collect(Collectors.toList());
                    if (subjectCd.getTermNumber() == termNo && semesters.size() != 0) {
                        importedMarkObject.setStatus("NotStart");
                        String semester = Ultilities.SortSemestersString(semesters).get(0);
                        importedMarkObject.setSemesterName(semester);
                    } else if (subjectCd.getTermNumber() == termNo && semesters.size() == 0) {
                        importedMarkObject.setStatus("NotStart");
                        RealSemesterEntity semester = allSemester.get(allSemester.size() - 1);
                        importedMarkObject.setSemesterName(semester.getSemester());
                    } else if (subjectCd.getTermNumber() < termNo && semesters.size() == 0) {
                        importedMarkObject.setStatus("NotStart");
                        importedMarkObject.setSemesterName("N/A");
                    } else {
                        importedMarkObject.setStatus("NotStart");
                        String semester = Ultilities.SortSemestersString(semesters).get(0);
                        importedMarkObject.setSemesterName(semester);
                    }

                    processedList.add(importedMarkObject);
                }
                importMarks.addAll(processedList);
                // convert to Mark Entity
                for (ImportedMarkObject object : importMarks) {
                    MarksEntity marksEntity = new MarksEntity();
                    // set student
                    marksEntity.setStudentId(object.getStudentEntity());
                    // set average mark
                    marksEntity.setAverageMark(object.getAverageMark());
                    // set status
                    marksEntity.setStatus(object.getStatus());
                    // set isActivated
                    marksEntity.setIsActivated(true);
                    // set semester
                    if (object.getSemesterName() != null) {
                        RealSemesterEntity realSemesterEntity = realSemesterService.findSemesterByName(object.getSemesterName());
                        if (realSemesterEntity != null) {
                            marksEntity.setSemesterId(realSemesterEntity);
                        } else {
                            // create new semester
                            realSemesterEntity = new RealSemesterEntity();
                            realSemesterEntity.setSemester(object.getSemesterName());
                            realSemesterEntity = realSemesterService.createRealSemester(realSemesterEntity);
                            marksEntity.setSemesterId(realSemesterEntity);
                        }
                    }
                    // set course
                    if (object.getSemesterName() != null && object.getSubjectCode() != null) {
                        CourseEntity courseEntity = courseService.findCourseBySemesterAndSubjectCode(object.getSemesterName(), object.getSubjectCode().getId());
                        if (courseEntity != null) {
                            marksEntity.setCourseId(courseEntity);
                        } else {
                            courseEntity = new CourseEntity();
                            courseEntity.setSemester(object.getSemesterName());
                            courseEntity.setSubjectCode(object.getSubjectCode().getId());
                            courseEntity = courseService.createCourse(courseEntity);
                            marksEntity.setCourseId(courseEntity);
                        }
                    }
                    // set subject mark component
                    MarkComponentEntity markComponentEntity = markComponentService.getMarkComponentByName(markComponentName);
                    String subjectMarkComponentName = object.getSubjectCode().getId() + "_" + markComponentName;
                    SubjectMarkComponentEntity subjectMarkComponentEntity =
                            subjectMarkComponentService.findSubjectMarkComponentByNameAndSubjectCd(markComponentName, object.getSubjectCode().getId());
                    if (subjectMarkComponentEntity != null) {
                        marksEntity.setSubjectMarkComponentId(subjectMarkComponentEntity);
                    } else {
                        subjectMarkComponentEntity = new SubjectMarkComponentEntity();
                        subjectMarkComponentEntity.setMarkComponentId(markComponentEntity);
                        subjectMarkComponentEntity.setName(subjectMarkComponentName);
                        subjectMarkComponentEntity.setPercentWeight(0.0);
                        subjectMarkComponentEntity.setSubjectId(object.getSubjectCode());
                        subjectMarkComponentEntity = subjectMarkComponentService.createSubjectMarkComponent(subjectMarkComponentEntity);
                        marksEntity.setSubjectMarkComponentId(subjectMarkComponentEntity);
                    }
                    // add to list mark entities
                    marksEntities.add(marksEntity);
                }
            }
            marksService.createMarks(marksEntities);

            if (!isCancel) {
                jsonObject.addProperty("success", true);
            } else {
                jsonObject.addProperty("success", false);
                jsonObject.addProperty("message", "Đã hủy tiến trình!");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.writeLog(ex);

            jsonObject = new JsonObject();
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", ex.getMessage());
        }

        Ultilities.notexist = new ArrayList<>();
        return jsonObject;
    }

    @RequestMapping("/marks/getStatus")
    @ResponseBody
    public JsonObject GetLineStatus() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("totalLine", this.totalLine);
        jsonObject.addProperty("currentLine", this.currentLine);
        jsonObject.addProperty("totalExistMarks", marksService.getTotalExistMarks());
        jsonObject.addProperty("successSavedMark", marksService.getSuccessSavedMark());
        return jsonObject;
    }

    /**
     * --------------COURSE------------
     **/
//    @RequestMapping(value = "/goUploadCoursePage")
//    public ModelAndView goUploadCoursePage() {
//        ModelAndView view = new ModelAndView("uploadCourse");
//        view.addObject("title", "Nhập danh sách khóa học");
//
//        ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
//        File[] list = read.readFiles(context, folder);
//        view.addObject("files", list);
//        return view;
//    }
//
//    @RequestMapping("/getCourseStatus")
//    @ResponseBody
//    public JsonObject getCourseCurrentLine() {
//        JsonObject obj = new JsonObject();
//        obj.addProperty("current", studentService.getCurrentLine());
//        obj.addProperty("total", studentService.getTotalLine());
//        return obj;
//    }
//
//    @RequestMapping(value = "/uploadCourseExistFile", method = RequestMethod.POST)
//    @ResponseBody
//    public JsonObject chooseExistCourseFile(@RequestParam("file") String file) {
//        JsonObject obj;
//        try {
//            File f = new File(context.getRealPath("/") + "UploadedFiles/" + folder + "/" + file);
//            obj = ReadCourseFile(null, f, false);
//        } catch (Exception e) {
//            obj = new JsonObject();
//            obj.addProperty("success", false);
//            obj.addProperty("message", e.getMessage());
//        }
//
//        return obj;
//    }
//
//    @RequestMapping(value = "/uploadCourse", method = RequestMethod.POST)
//    @ResponseBody
//    public JsonObject uploadCourseFile(@RequestParam("file") MultipartFile files) {
//        JsonObject obj = ReadCourseFile(files, null, true);
//        if (obj.get("success").getAsBoolean()) {
//            ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
//            read.saveFile(context, files, folder);
//        }
//
//        return obj;
//    }
//
//    private static int findRow(XSSFSheet sheet, String cellContent) {
//        for (Row row : sheet) {
//            for (Cell cell : row) {
//                if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
//                    if (cell.getRichStringCellValue().getString().trim().equals(cellContent)) {
//                        return row.getRowNum();
//                    }
//                }
//            }
//        }
//        return 0;
//    }
//
//    private static boolean checkCell(XSSFCell c) {
//        if (c.getCellType() == Cell.CELL_TYPE_NUMERIC) {
//            return false;
//        }
//        return true;
//    }
//
//
//    private JsonObject ReadCourseFile(MultipartFile file, File file2, boolean isNewFile) {
//        JsonObject obj = new JsonObject();
//
//        try {
//            InputStream is;
//            if (isNewFile) {
//                is = file.getInputStream();
//            } else {
//                is = new FileInputStream(file2);
//            }
//
//            XSSFWorkbook workbook = new XSSFWorkbook(is);
//            XSSFSheet spreadsheet = workbook.getSheetAt(0);
//
//            XSSFRow row;
//            int classIndex = 0;
//            int startDateIndex = 0;
//            int endDateIndex = 0;
//            int subjectCodeIndex = 0;
//            int excelDataIndex = 0;
//            int checkIndex = 0;
//            int dataStartIndex = 0;
//
//            List<CourseEntity> courses = new ArrayList<>();
//            List<CourseEntity> uniqueCourses = new ArrayList<>();
//            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
//
//            //get header data row index
//            excelDataIndex = findRow(spreadsheet, "Lớp");
//            row = spreadsheet.getRow(excelDataIndex);
//
//            //get data start index
//            for (dataStartIndex = excelDataIndex; dataStartIndex <= spreadsheet.getLastRowNum(); dataStartIndex++) {
//                boolean flag = false;
//                for (int curCellIndex = 0; curCellIndex <= row.getLastCellNum(); curCellIndex++) {
//                    if (row.getCell(curCellIndex).getStringCellValue().toString().equals("Lớp")) {
//                        checkIndex = curCellIndex;
//                        flag = true;
//                        break;
//                    }
//                }
//                if (flag) {
//                    break;
//                }
//            }
//
//            for (int conRowIndex = excelDataIndex + 1; conRowIndex <= spreadsheet.getLastRowNum(); conRowIndex++) {
//                if (checkCell(spreadsheet.getRow(conRowIndex).getCell(checkIndex)) == true) {
//                    dataStartIndex = conRowIndex;
//                    break;
//                }
//            }
//
//            row = spreadsheet.getRow(excelDataIndex);
//            for (int cellIndex = 0; cellIndex <= row.getLastCellNum(); cellIndex++) {
//                if (row.getCell(cellIndex).getStringCellValue().toString().equals("Lớp")) {
//                    classIndex = cellIndex;
//                }
//                if (row.getCell(cellIndex).getStringCellValue().toString().equals("Ngày \n" + "bắt đầu")) {
//                    startDateIndex = cellIndex;
//                }
//                if (row.getCell(cellIndex).getStringCellValue().toString().equals("Ngày \n" + "kết thúc")) {
//                    endDateIndex = cellIndex;
//                }
//                if (row.getCell(cellIndex).getStringCellValue().toString().equals("Mã môn")) {
//                    subjectCodeIndex = cellIndex;
//                }
//                if (classIndex != 0 && startDateIndex != 0 && endDateIndex != 0 && subjectCodeIndex != 0) break;
//            }
//            if (classIndex == 0 && startDateIndex == 0 && endDateIndex == 0 && subjectCodeIndex == 0) {
//
//            } else {
//
//                for (int rowIndex = dataStartIndex; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
//                    row = spreadsheet.getRow(rowIndex);
//
//                    if (row != null) {
//                        CourseEntity course = new CourseEntity();
//                        Cell classCell = row.getCell(classIndex);
//                        Cell startDateCell = row.getCell(startDateIndex);
//                        Cell endDateCell = row.getCell(endDateIndex);
//                        Cell subjectCell = row.getCell(subjectCodeIndex);
//                        if (classCell != null && startDateCell != null && endDateCell != null && subjectCell != null) {
//                            if (classCell != null) {
//                                if (classCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
//                                    System.out.println("Class Num ---- " + classCell.getNumericCellValue());
//                                    course.setClass1(String.valueOf(classCell.getNumericCellValue()));
//                                } else {
//                                    System.out.println("Class String ----" + classCell.getStringCellValue());
//                                    course.setClass1(classCell.getStringCellValue());
//                                }
//                            }
//
//                            if (course.getClass1() != null) {
//                                courses.add(course);
//                            }
//                        }
//                    }
//                }
//
//                System.out.println("All Course Added");
////                for (CourseEntity element : courses) {
////                    if (!uniqueCourses.stream().anyMatch(c -> c.getClass1().equals(element.getClass1())
////                            && c.getSubjectCode().equals(element.getSubjectCode())
////                            && c.getStartDate() == element.getStartDate()
////                            && c.getEndDate() == element.getEndDate())) {
////                        uniqueCourses.add(element);
////                    }
////                }
//
//                courseService.createCourseList(uniqueCourses);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            obj.addProperty("success", false);
//            obj.addProperty("message", e.getMessage());
//            return obj;
//        }
//
//        obj.addProperty("success", true);
//        return obj;
//    }
//
//    /**
//     * --------------CURRICULUM------------
//     **/
//    @RequestMapping(value = "/goUploadCurriculumPage")
//    public ModelAndView goUploadCurriculumPage() {
//        ModelAndView view = new ModelAndView("uploadCurriculum");
//        view.addObject("title", "Nhập chương trình đào tạo");
//
//        ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
//        File[] list = read.readFiles(context, folder);
//        view.addObject("files", list);
//        return view;
//    }
//
//    @RequestMapping("/getCurriculumStatus")
//    @ResponseBody
//    public JsonObject getCurriculumCurrentLine() {
//        JsonObject obj = new JsonObject();
//        obj.addProperty("current", studentService.getCurrentLine());
//        obj.addProperty("total", studentService.getTotalLine());
//        return obj;
//    }
//
//    @RequestMapping(value = "/uploadCurriculumExistFile", method = RequestMethod.POST)
//    @ResponseBody
//    public JsonObject chooseExistCurriculumFile(@RequestParam("file") String file) {
//        JsonObject obj;
//        try {
//            File f = new File(context.getRealPath("/") + "UploadedFiles/" + folder + "/" + file);
//            obj = ReadCurriculumFile(null, f, false);
//        } catch (Exception e) {
//            obj = new JsonObject();
//            obj.addProperty("success", false);
//            obj.addProperty("message", e.getMessage());
//        }
//
//        return obj;
//    }
//
//    @RequestMapping(value = "/uploadCurriculum", method = RequestMethod.POST)
//    @ResponseBody
//    public JsonObject uploadCurriculumFile(@RequestParam("file") MultipartFile files) {
//        JsonObject obj = ReadCurriculumFile(files, null, true);
//        if (obj.get("success").getAsBoolean()) {
//            ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
//            read.saveFile(context, files, folder);
//        }
//
//        return obj;
//    }
//
//    private static int findRowCurriculum(XSSFSheet sheet, String cellContent) {
//        for (Row row : sheet) {
//            for (Cell cell : row) {
//                if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
//                    if (cell.getRichStringCellValue().getString().trim().equals(cellContent)) {
//                        return row.getRowNum();
//                    }
//                }
//            }
//        }
//        return 0;
//    }
//
//
//    private JsonObject ReadCurriculumFile(MultipartFile file, File file2, boolean isNewFile) {
//        JsonObject obj = new JsonObject();
//
////        try {
////            InputStream is;
////            if (isNewFile) {
////                is = file.getInputStream();
////            } else {
////                is = new FileInputStream(file2);
////            }
////
////            XSSFWorkbook workbook = new XSSFWorkbook(is);
////            XSSFSheet spreadsheet = workbook.getSheetAt(1);
////
////            XSSFRow row;
////            int excelDataIndex = 0;
////
////            String curriculumName = spreadsheet.getSheetName();
////
////            List<CurriculumMappingEntity> curriculums = new ArrayList<>();
////            List<CurriculumMappingEntity> uniqueCurriculum = new ArrayList<>();
//////            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
////
////            //get header data row index
////            excelDataIndex = findRowCurriculum(spreadsheet, "Học kỳ 1");
////
////            for (int rowIndex = excelDataIndex; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
////                XSSFRow termRow = spreadsheet.getRow(rowIndex);
////                String term = "";
////                if (termRow.getCell(1).getCellType() == Cell.CELL_TYPE_NUMERIC) {
////                    if (termRow.getCell(2).getStringCellValue() == ""
////                            && termRow.getCell(3).getStringCellValue() != "") {
////                        term = termRow.getCell(3).getStringCellValue();
////                        rowIndex++;
////                    }
////                }
////
////                row = spreadsheet.getRow(rowIndex);
////                if (row != null) {
////                    CurriculumMappingEntity curriculum = new CurriculumMappingEntity();
////                    Cell subjectCell = row.getCell(2);
////                    if (subjectCell != null) {
////                        curriculum.setCurriculumMappingEntityPK(subjectCell.getRichStringCellValue().getString());
////                        curriculum.setTerm(term);
////                        curriculums.add(curriculum);
////
////                    }
////                }
////            }
////
////            System.out.println("All Curriculum Added");
////            for (CurriculumMappingEntity element : curriculums) {
////                if (!uniqueCurriculum.stream().anyMatch(c -> c.getTerm().equals(element.getTerm())
////                        && c.getCurriculumMappingEntityPK().equals(element.getCurriculumMappingEntityPK()))) {
////                    uniqueCurriculum.add(element);
////                }
////            }
////
////            curriculumsService.createCurriculumList(curriculums);
////
////        } catch (Exception e) {
////            e.printStackTrace();
////            obj.addProperty("success", false);
////            obj.addProperty("message", e.getMessage());
////            return obj;
////        }
//
//        obj.addProperty("success", true);
//        return obj;
//    }


}

