package com.capstone.controllers;

import com.capstone.models.*;
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

import javax.persistence.criteria.CriteriaBuilder;
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
    private int totalLine1;
    private int currentLine1;
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
    IDocumentStudentService documentStudentService = new DocumentStudentServiceImpl();
    IOldRollNumberService oldRollNumberService = new OldRollNumberServiceImpl();
    IStudentStatusService studentStatusService = new StudentStatusServiceImpl();

    /**
     * --------------STUDENTS------------
     **/
    @RequestMapping(value = "/goUploadStudentList")
    public ModelAndView goUploadStudentListPage() {
        ModelAndView view = new ModelAndView("uploadStudentList");
        view.addObject("title", "Nhập danh sách sinh viên");

        ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
        List<RealSemesterEntity> semesters = realSemesterService.getAllSemester();
        semesters = Ultilities.SortSemesters(semesters);
        semesters = semesters.stream().filter(s -> !s.getSemester().contains("N/A")).collect(Collectors.toList());
        File[] list = read.readFiles(context, folder);
        view.addObject("files", list);
        view.addObject("semesters", semesters);
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
    public Callable<JsonObject> chooseExistFile(@RequestParam("file") String file, @RequestParam String semesterId) {
        Callable<JsonObject> callable = new Callable<JsonObject>() {
            @Override
            public JsonObject call() throws Exception {
                JsonObject obj;

                try {
                    File f = new File(context.getRealPath("/") + "UploadedFiles/" + folder + "/" + file);
                    obj = ReadFile(null, f, false, semesterId);
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
    public Callable<JsonObject> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam boolean update, @RequestParam String semesterId) {
        Callable<JsonObject> callable = new Callable<JsonObject>() {
            @Override
            public JsonObject call() throws Exception {
                JsonObject obj;
                if (update) {
                    obj = UpdateFile(file, null, true, semesterId);
                } else {
                    obj = ReadFile(file, null, true, semesterId);
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

    private JsonObject ReadFile(MultipartFile file, File file2, boolean isNewFile, String semesterId) {

        JsonObject obj = new JsonObject();

        try {
            InputStream is = isNewFile ? file.getInputStream() : new FileInputStream(file2);

            String originalFileName = isNewFile ? file.getOriginalFilename() : file2.getName();
            String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1, originalFileName.length());

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

            int excelDataIndexRow = 1;

            int rollNumberIndex = 0;
            int oldRollNumberIndex = 1;
            int fullNameIndex = 2;
            int dateOfBirthIndex = 3;
            int genderIndex = 4;
            int currentProgramIndex = 5;
            int oldProgramIndex = 6;
            int curriculumIndex1 = 13; // du bi
            int curriculumIndex2 = 14; // chuyen nganh
            int curriculumIndex3 = 15; // OJT
            int curriculumIndex4 = 16; // chuyen nganh hep
            int termNoIndex = 17; // hoc ky hien tai
            int statusIndex = 20; // trang thai cua sinh vien
            int emailIndex = 29;

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            for (int rowIndex = excelDataIndexRow; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
                System.out.println(rowIndex + " - " + spreadsheet.getLastRowNum());
                row = spreadsheet.getRow(rowIndex);
                if (row != null) {
                    Cell rollNumberCell = row.getCell(rollNumberIndex);
                    Cell oldRollNumberCell = row.getCell(oldRollNumberIndex);
                    Cell fullNameCell = row.getCell(fullNameIndex);
                    Cell dateOfBirthCell = row.getCell(dateOfBirthIndex);
                    Cell genderCell = row.getCell(genderIndex);
                    Cell currentProgramCell = row.getCell(currentProgramIndex);
                    Cell oldProgramCell = row.getCell(oldProgramIndex);
                    Cell curriculumCell1 = row.getCell(curriculumIndex1);
                    Cell curriculumCell2 = row.getCell(curriculumIndex2);
                    Cell curriculumCell3 = row.getCell(curriculumIndex3);
                    Cell curriculumCell4 = row.getCell(curriculumIndex4);
                    Cell termNoCell = row.getCell(termNoIndex);
                    Cell statusCell = row.getCell(statusIndex);
                    Cell emailCell = row.getCell(emailIndex);

                    StudentEntity studentEntity = new StudentEntity();
                    if (rollNumberCell != null) { // set roll number
                        String rollNumber = rollNumberCell.getCellType() == Cell.CELL_TYPE_STRING ?
                                rollNumberCell.getStringCellValue() : (rollNumberCell.getNumericCellValue() == 0 ?
                                "" : Integer.toString((int) rollNumberCell.getNumericCellValue()));
                        studentEntity.setRollNumber(rollNumber);
                    }

                    if (fullNameCell != null) { // set full name
                        studentEntity.setFullName(fullNameCell.getStringCellValue().trim());
                    }

                    if (dateOfBirthCell != null) {// set date of birth
                        studentEntity.setDateOfBirth(sdf.parse(dateOfBirthCell.getStringCellValue().trim()));
                    }

                    if (genderCell != null) { // set gender
                        studentEntity.setGender(genderCell.getStringCellValue().toLowerCase().contains("nam") ? Enums.Gender.MALE.getValue() : Enums.Gender.FEMALE.getValue());
                    }

                    if (currentProgramCell != null) { // set program
                        ProgramEntity programEntity = programService.getProgramByName(currentProgramCell.getStringCellValue().trim());
                        if (programEntity != null) {
                            studentEntity.setProgramId(programEntity);
                        }
                    }

                    if (termNoCell != null) { // set term number
                        Double termNo = termNoCell.getNumericCellValue();
                        studentEntity.setTerm(termNo.intValue());
                    }

                    if (emailCell != null) {
                        studentEntity.setEmail(emailCell.getStringCellValue().trim());
                    }
                    // save student
                    studentEntity = studentService.createStudent(studentEntity);
                    // save status
                    if (statusCell != null) {
                        RealSemesterEntity realSemesterEntity = realSemesterService.findSemesterById(Integer.parseInt(semesterId));
                        StudentStatusEntity studentStatusEntity = new StudentStatusEntity();
                        studentStatusEntity.setSemesterId(realSemesterEntity);
                        studentStatusEntity.setStudentId(studentEntity);
                        studentStatusEntity.setStatus(statusCell.getStringCellValue().trim());

                        studentStatusService.createStudentStatus(studentStatusEntity);
                    }
                    // start save document student
                    DocumentEntity documentEntity = documentService.getAllDocuments().get(0);
                    List<DocumentStudentEntity> documentStudentEntityList = new ArrayList<>();

                    if (curriculumCell4 != null) {
                        if (!curriculumCell4.getStringCellValue().isEmpty()) {
                            System.out.println(curriculumCell4.getStringCellValue().trim());
                            CurriculumEntity curriculumEntity = curriculumService.getCurriculumByName(curriculumCell4.getStringCellValue().trim());
                            DocumentStudentEntity documentStudentEntity = new DocumentStudentEntity();
                            documentStudentEntity.setStudentId(studentEntity);
                            documentStudentEntity.setDocumentId(documentEntity);
                            documentStudentEntity.setCurriculumId(curriculumEntity);

                            documentStudentEntityList.add(documentStudentEntity);
                        }
                    }

                    if (curriculumCell3 != null) {
                        if (!curriculumCell3.getStringCellValue().isEmpty()) {
                            System.out.println(curriculumCell3.getStringCellValue().trim());
                            CurriculumEntity curriculumEntity = curriculumService.getCurriculumByName(curriculumCell3.getStringCellValue().trim());
                            DocumentStudentEntity documentStudentEntity = new DocumentStudentEntity();
                            documentStudentEntity.setStudentId(studentEntity);
                            documentStudentEntity.setDocumentId(documentEntity);
                            documentStudentEntity.setCurriculumId(curriculumEntity);

                            documentStudentEntityList.add(documentStudentEntity);
                        }
                    }

                    if (curriculumCell2 != null) {
                        if (!curriculumCell2.getStringCellValue().isEmpty()) {
                            System.out.println(curriculumCell2.getStringCellValue().trim());
                            CurriculumEntity curriculumEntity = curriculumService.getCurriculumByName(curriculumCell2.getStringCellValue().trim());
                            DocumentStudentEntity documentStudentEntity = new DocumentStudentEntity();
                            documentStudentEntity.setStudentId(studentEntity);
                            documentStudentEntity.setDocumentId(documentEntity);
                            documentStudentEntity.setCurriculumId(curriculumEntity);

                            documentStudentEntityList.add(documentStudentEntity);
                        }
                    }

                    if (curriculumCell1 != null) {
                        if (!curriculumCell1.getStringCellValue().isEmpty()) {
                            System.out.println(curriculumCell1.getStringCellValue().trim());
                            CurriculumEntity curriculumEntity = curriculumService.getCurriculumByName(curriculumCell1.getStringCellValue().trim());
                            DocumentStudentEntity documentStudentEntity = new DocumentStudentEntity();
                            documentStudentEntity.setStudentId(studentEntity);
                            documentStudentEntity.setDocumentId(documentEntity);
                            documentStudentEntity.setCurriculumId(curriculumEntity);

                            documentStudentEntityList.add(documentStudentEntity);
                        }
                    }
                    // start save old roll number
                    if (oldRollNumberCell != null && oldProgramCell != null) {
                        OldRollNumberEntity oldRollNumberEntity = new OldRollNumberEntity();
                        oldRollNumberEntity.setStudentId(studentEntity);
                        oldRollNumberEntity.setOldRollNumber(oldRollNumberCell.getStringCellValue().trim());
                        ProgramEntity programEntity = programService.getProgramByName(oldProgramCell.getStringCellValue().trim());
                        if (programEntity != null) {
                            oldRollNumberEntity.setProgramId(programEntity);
                        }
                        oldRollNumberEntity = oldRollNumberService.createOldRollNumber(oldRollNumberEntity);
                        // create document student
                        DocumentStudentEntity documentStudentEntity = new DocumentStudentEntity();
                        documentStudentEntity.setOldStudentId(oldRollNumberEntity);
                        documentStudentEntity.setDocumentId(documentEntity);

                        documentStudentEntityList.add(documentStudentEntity);
                    }

                    // save document List
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    for (DocumentStudentEntity documentStudentEntity : documentStudentEntityList) {
                        documentStudentEntity.setCreatedDate(calendar.getTime());
                        calendar.add(Calendar.MONTH, -4);

                        documentStudentService.createDocumentStudent(documentStudentEntity);
                    }
                }
            }
        } catch (Exception e) {
            obj.addProperty("success", false);
            obj.addProperty("message", e.getMessage());
            e.printStackTrace();
            return obj;
        }

        obj.addProperty("success", true);
        return obj;
    }

    private JsonObject UpdateFile(MultipartFile file, File file2, boolean isNewFile, String semesterId) {
        JsonObject obj = new JsonObject();

        try {
            InputStream is = isNewFile ? file.getInputStream() : new FileInputStream(file2);

            String originalFileName = isNewFile ? file.getOriginalFilename() : file2.getName();
            String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1, originalFileName.length());

            List<ProgramEntity> programList = programService.getAllPrograms();
            List<CurriculumEntity> curriculumList = curriculumService.getAllCurriculums();

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

            int excelDataIndexRow = 1;

            int rollNumberIndex = 0;
            int payRollClassIndex = 18;

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            for (int rowIndex = excelDataIndexRow; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
                System.out.println(rowIndex + " - " + spreadsheet.getLastRowNum());
                row = spreadsheet.getRow(rowIndex);
                if (row != null) {
                    Cell rollNumberCell = row.getCell(rollNumberIndex);
                    Cell payRollClassCell = row.getCell(payRollClassIndex);

                    if (rollNumberCell != null) {
                        StudentEntity student = studentService.findStudentByRollNumber(rollNumberCell.getStringCellValue().trim());
                        if (student != null) {
                            if (payRollClassCell != null) {
                                String payRollClass = payRollClassCell.getStringCellValue().trim();
                                if (!payRollClass.isEmpty() && payRollClass.matches("^[a-zA-Z]+\\d+$")) {
                                    student.setPayRollClass(payRollClass);
                                    Integer classNumber = Integer.parseInt(payRollClass.replaceAll("[^0-9]", ""));
                                    student.setShift(classNumber % 2 == 0 ? "PM" : "AM");
                                }
                            }
                        }
                        studentService.updateStudent(student);
                    }
                }
            }
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

    private RealSemesterEntity findOrCreateSemester(List<RealSemesterEntity> list, String semesterName) {
        RealSemesterEntity semester = null;

        for (RealSemesterEntity p : list) {
            if (p.getSemester().equals(semesterName)) {
                semester = p;
                break;
            }
        }

        if (semester == null) {
            semester = new RealSemesterEntity();
            semester.setSemester(semesterName);
            realSemesterService.createRealSemester(semester);
            list.add(semester);
        }

        return semester;
    }

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

    @RequestMapping(value = "/manageStudyingStudent")
    public ModelAndView goManageStudyingStudentPage() {
        ModelAndView mav = new ModelAndView("manageStudyingStudent");
        mav.addObject("title", "Quản lý điểm cho sinh viên đang học");

        List<RealSemesterEntity> semesters = realSemesterService.getAllSemester();
        semesters = Ultilities.SortSemesters(semesters);
//        semesters = semesters.stream().filter(s -> !s.getSemester().contains("N/A")).collect(Collectors.toList());

        mav.addObject("semesters", semesters);

        return mav;
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
            this.totalLine1 = 0;
            this.currentLine1 = 0;
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
            int enabledIndex = 6;

            this.currentLine = 0;
            String markComponentName = Enums.MarkComponent.AVERAGE.getValue();
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
                            Cell semesterNameCell = row.getCell(semesterNameIndex);
                            Cell subjectCodeCell = row.getCell(subjectCodeIndex);
                            Cell averageMarkCell = row.getCell(averageMarkIndex);
                            Cell statusCell = row.getCell(statusIndex);
                            Cell enabledCell = row.getCell(enabledIndex);

                            if (studentMarksMap.get(studentEntity) != null) {
                                ImportedMarkObject importedMarkObject = new ImportedMarkObject();
                                importedMarkObject.setStudentEntity(studentEntity);

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

                                if (enabledCell != null) {
                                    importedMarkObject.setEnabled(enabledCell.getBooleanCellValue());
                                }

                                studentMarksMap.get(studentEntity).add(importedMarkObject);
                            } else {
                                ImportedMarkObject importedMarkObject = new ImportedMarkObject();
                                importedMarkObject.setStudentEntity(studentEntity);

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

                                if (enabledCell != null) {
                                    importedMarkObject.setEnabled(enabledCell.getBooleanCellValue());
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
                List<DocumentStudentEntity> documentStudentEntityList = documentStudentService.getDocumentStudentListByStudentId(studentId);
                List<Integer> curriculumIds = documentStudentEntityList.stream().filter(d -> d.getCurriculumId() != null).map(d -> d.getCurriculumId().getId()).collect(Collectors.toList());
                List<SubjectCurriculumEntity> subjectsInCurriculum = subjectCurriculumService.getSubjectIds(curriculumIds, termNo);
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
                    // set enabled
                    marksEntity.setEnabled(object.getEnabled());
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
            UpdateStudentCredits(studentMarksMap.keySet());

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

    @RequestMapping(value = "/uploadStudyingStudent",  method = RequestMethod.POST)
    @ResponseBody
    public JsonObject importStudyingStudent(@RequestParam("file") MultipartFile file) {
        JsonObject jsonObject = new JsonObject();
        List<MarksEntity> marksEntities = new ArrayList<MarksEntity>();

        try {
            InputStream is = file.getInputStream();

            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);

            XSSFRow row;
            int excelDataIndex = 1;
            int lastRow = spreadsheet.getLastRowNum();
            this.totalLine = lastRow - startRowNumber + 1;

            int semesterNameIndex = 0;
            int rollNumberIndex = 1;
            int subjectCodeIndex = 2;
            int averageMarkIndex = 4;
            int statusIndex = 5;
            int enabledIndex = 6;

            this.currentLine = 0;
            String markComponentName = Enums.MarkComponent.AVERAGE.getValue();
            for (int rowIndex = excelDataIndex; rowIndex <= lastRow; rowIndex++) {
                row = spreadsheet.getRow(rowIndex);

                Cell rollNumberCell = row.getCell(rollNumberIndex);
                if (rollNumberCell != null) {
                    StudentEntity studentEntity = studentService.findStudentByRollNumber(rollNumberCell.getStringCellValue().trim());
                    if (studentEntity != null) {
                        MarksEntity mark = new MarksEntity();
                        // set Student
                        mark.setStudentId(studentEntity);

                        Cell semesterNameCell = row.getCell(semesterNameIndex);
                        Cell subjectCodeCell = row.getCell(subjectCodeIndex);
                        Cell averageMarkCell = row.getCell(averageMarkIndex);
                        Cell statusCell = row.getCell(statusIndex);
                        Cell enabledCell = row.getCell(enabledIndex);

                        // set semester
                        if (semesterNameCell != null) {
                            String semesterName = semesterNameCell.getStringCellValue().trim().toUpperCase().replaceAll(" ", "");
                            RealSemesterEntity realSemesterEntity = realSemesterService.findSemesterByName(semesterName);
                            if (realSemesterEntity != null) {
                                mark.setSemesterId(realSemesterEntity);
                            } else {
                                // create new semester
                                realSemesterEntity = new RealSemesterEntity();
                                realSemesterEntity.setSemester(semesterName);
                                realSemesterEntity = realSemesterService.createRealSemester(realSemesterEntity);
                                mark.setSemesterId(realSemesterEntity);
                            }
                        }

                        // set subject mark component
                        if (subjectCodeCell != null) {
                            String subjectCode = subjectCodeCell.getStringCellValue().trim().toUpperCase();
                            // find subject code
                            SubjectEntity subjectEntity = subjectService.findSubjectById(subjectCode);
                            if (subjectEntity != null) {
                                MarkComponentEntity markComponentEntity = markComponentService.getMarkComponentByName(markComponentName);
                                String subjectMarkComponentName = subjectEntity.getId() + "_" + markComponentName;
                                SubjectMarkComponentEntity subjectMarkComponentEntity =
                                        subjectMarkComponentService.findSubjectMarkComponentByNameAndSubjectCd(markComponentName, subjectEntity.getId());
                                if (subjectMarkComponentEntity != null) {
                                    mark.setSubjectMarkComponentId(subjectMarkComponentEntity);
                                } else {
                                    subjectMarkComponentEntity = new SubjectMarkComponentEntity();
                                    subjectMarkComponentEntity.setMarkComponentId(markComponentEntity);
                                    subjectMarkComponentEntity.setName(subjectMarkComponentName);
                                    subjectMarkComponentEntity.setPercentWeight(0.0);
                                    subjectMarkComponentEntity.setSubjectId(subjectEntity);
                                    subjectMarkComponentEntity = subjectMarkComponentService.createSubjectMarkComponent(subjectMarkComponentEntity);
                                    mark.setSubjectMarkComponentId(subjectMarkComponentEntity);
                                }
                            }
                        }

                        // set course
                        if (semesterNameCell != null && subjectCodeCell != null) {
                            String semesterName = semesterNameCell.getStringCellValue().trim().toUpperCase().replaceAll(" ", "");
                            String subjectCode = subjectCodeCell.getStringCellValue().trim().toUpperCase();
                            CourseEntity courseEntity = courseService.findCourseBySemesterAndSubjectCode(semesterName, subjectCode);
                            if (courseEntity != null) {
                                mark.setCourseId(courseEntity);
                            } else {
                                courseEntity = new CourseEntity();
                                courseEntity.setSemester(semesterName);
                                courseEntity.setSubjectCode(subjectCode);
                                courseEntity = courseService.createCourse(courseEntity);
                                mark.setCourseId(courseEntity);
                            }
                        }

                        // set average Mark
                        if (averageMarkCell != null) {
                            mark.setAverageMark(averageMarkCell.getNumericCellValue());
                        }

                        // set status
                        if (statusCell != null) {
                            mark.setStatus(statusCell.getStringCellValue());
                        }

                        // set isActivated
                        mark.setIsActivated(true);

                        // set Enabled
                        if (enabledCell != null) {
                            mark.setEnabled(enabledCell.getBooleanCellValue());
                        }

                        // add to list mark entities
                        marksEntities.add(mark);
                    }
                }
                this.currentLine++;
            }
            marksService.createMarks(marksEntities);
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            Logger.writeLog(ex);
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", ex.getMessage());
        }

        jsonObject.addProperty("success", true);
        jsonObject.addProperty("message", "Import sinh viên đang học thành công !");
        return jsonObject;
    }

    @RequestMapping(value = "/updateMarkForStudyingStudent", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject updateMarkForStudyingStudent(@RequestParam("updateFile") MultipartFile file, @RequestParam("semesterId") String semesterIdStr) {
        JsonObject jsonObject = new JsonObject();
        Integer semesterId = Integer.parseInt(semesterIdStr.trim());
        RealSemesterEntity realSemesterEntity = realSemesterService.findSemesterById(semesterId);
        String semesterName = realSemesterEntity.getSemester().toLowerCase();
        List<MarksEntity> marksEntities = marksService.findMarksBySemesterId(semesterId);
        if (marksEntities == null || marksEntities.isEmpty()) {
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", "Không có sinh viên nào có trạng thái đang học trong học kỳ này");

            return jsonObject;
        }

        try {
            InputStream is = file.getInputStream();

            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);

            XSSFRow row;
            int excelDataIndex = 1;
            int lastRow = spreadsheet.getLastRowNum();
            this.totalLine = lastRow - startRowNumber + 1;

            int semesterNameIndex = 0;
            int rollNumberIndex = 1;
            int subjectCodeIndex = 2;
            int averageMarkIndex = 4;
            int statusIndex = 5;

            this.currentLine = 0;
            for (int rowIndex = excelDataIndex; rowIndex <= lastRow; rowIndex++) {
                row = spreadsheet.getRow(rowIndex);
                if (row != null) {
                    Cell semesterNameCell = row.getCell(semesterNameIndex);
                    Cell rollNumberCell = row.getCell(rollNumberIndex);
                    Cell subjectCodeCell = row.getCell(subjectCodeIndex);
                    Cell averageMarkCell = row.getCell(averageMarkIndex);
                    Cell statusCell = row.getCell(statusIndex);

                    if ((semesterNameCell != null) && (rollNumberCell != null)
                            && (subjectCodeCell != null) && (averageMarkCell != null) && (statusCell != null)) {
                        String semesterValue = semesterNameCell.getStringCellValue().trim().toLowerCase();
                        if (semesterValue.equals(semesterName)) { // check semester value in cell equal user's chosen semester
                            String rollNumberValue = rollNumberCell.getStringCellValue().trim().toLowerCase();
                            String subjectCodeValue = subjectCodeCell.getStringCellValue().trim().toLowerCase();
                            Double averageMarkValue = averageMarkCell.getNumericCellValue();
                            String statusValue = statusCell.getStringCellValue();

                            List<MarksEntity> foundMarks = marksEntities.stream()
                                    .filter(m -> m.getStudentId().getRollNumber().toLowerCase().equals(rollNumberValue)
                                              && m.getSubjectMarkComponentId().getSubjectId().getId().toLowerCase().equals(subjectCodeValue)).collect(Collectors.toList());

                            if (foundMarks != null && !foundMarks.isEmpty()) {
                                MarksEntity foundMark = foundMarks.get(0);
                                foundMark.setAverageMark(averageMarkValue);
                                foundMark.setStatus(statusValue);

                                marksService.updateMark(foundMark);
                            }
                        }
                    }
                }
                this.currentLine++;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.writeLog(ex);
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", ex.getMessage());
        }

        jsonObject.addProperty("success", true);
        jsonObject.addProperty("message", "Cập nhật điểm sinh viên đang học thành công !");
        return jsonObject;
    }

    private void UpdateStudentCredits(Collection<StudentEntity> studentList) {
        this.totalLine1 = studentList.size();
        this.currentLine1 = 0;

        for (StudentEntity student : studentList) {
            // Object[]: SubjectId, SubjectCredits, Mark, MarkStatus
            List<Object[]> markList = marksService.getLatestPassFailMarksAndCredits(student.getId());
            int totalPassCredits = 0;
            int totalPassFailCredits = 0;
            double passFailAverageMark = 0;

            double sumPassFailMark = 0;
            double sumPassFailCredits = 0;

            for (Object[] m : markList) {
                String subjectCode = m[0].toString();
                int subjectCredits = (int) m[1];
                double mark = (double) m[2];
                String status = m[3].toString();

                if (!Ultilities.containsIgnoreCase(subjectCode, "VOV")) {
                    if (!status.equals(Enums.MarkStatus.FAIL.getValue())) {
                        totalPassCredits += subjectCredits;
                    }
                    totalPassFailCredits += subjectCredits;
                }

                if (!Ultilities.containsIgnoreCase(subjectCode, "LAB")
                        && !Ultilities.containsIgnoreCase(subjectCode, "OJT")
                        && !Ultilities.containsIgnoreCase(subjectCode, "SYB")) {
                    sumPassFailMark += mark * subjectCredits;
                    sumPassFailCredits += subjectCredits;
                }
            }
            passFailAverageMark = Math.round(sumPassFailMark / sumPassFailCredits * 100.0) / 100.0;

            student.setPassCredits(totalPassCredits);
            student.setPassFailCredits(totalPassFailCredits);
            student.setPassFailAverageMark(passFailAverageMark);

            studentService.updateStudent(student);
            ++this.currentLine1;
        }
    }

    @RequestMapping("/marks/getStatus")
    @ResponseBody
    public JsonObject GetLineStatus() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("totalLine", this.totalLine);
        jsonObject.addProperty("currentLine", this.currentLine);
        jsonObject.addProperty("updateStudentTotalLine", this.totalLine1);
        jsonObject.addProperty("updateStudentCurrentLine", this.currentLine1);
        jsonObject.addProperty("totalExistMarks", marksService.getTotalExistMarks());
        jsonObject.addProperty("successSavedMark", marksService.getSuccessSavedMark());
        return jsonObject;
    }

    @RequestMapping(value = "/uploadUpdatedMarks", method = RequestMethod.POST)
    @ResponseBody
    public Callable<JsonObject> uploadUpdatedMarks(@RequestParam("file") MultipartFile file) throws IOException {
        Callable<JsonObject> callable = () -> {
            this.totalLine = 0;
            this.currentLine = 0;
            this.totalLine1 = 0;
            this.currentLine1 = 0;
            JsonObject jsonObject = updateMarkFile(file, null, true);
//            if (jsonObject.get("success").getAsBoolean()) {
//                ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
//                read.saveFile(context, file, marksFolder);
//            }

            return jsonObject;
        };

        return callable;
    }

    private JsonObject updateMarkFile(MultipartFile file, File file2, boolean isNewFile) {
        JsonObject jsonObject = new JsonObject();

        try {
            InputStream is = isNewFile ? file.getInputStream() : new FileInputStream(file2);

            String originalFileName = isNewFile ? file.getOriginalFilename() : file2.getName();
            String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1, originalFileName.length());

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
                jsonObject.addProperty("success", false);
                jsonObject.addProperty("message", "Chỉ chấp nhận file excel");
                return jsonObject;
            }

            MarkComponentEntity markComponent = markComponentService.getMarkComponentByName(
                    Enums.MarkComponent.AVERAGE.getValue());
            List<RealSemesterEntity> semesterList = realSemesterService.getAllSemester();

            int excelDataIndexRow = 1;

            int rollNumberIndex = 0;
            int subjectCodeIndex = 2;
            int oldSemesterIndex = 3;
            int newSemesterIndex = 4;
            int oldMarkIndex = 5;
            int newMarkIndex = 6;
            int oldStatusIndex = 7;
            int newStatusIndex = 8;

            List<UpdatedMarkObject> updateList = new ArrayList<>();
            for (int rowIndex = excelDataIndexRow; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
                row = spreadsheet.getRow(rowIndex);
                if (row != null) {
                    Cell rollNumberCell = row.getCell(rollNumberIndex);
                    Cell subjectCodeCell = row.getCell(subjectCodeIndex);
                    Cell oldSemesterCell = row.getCell(oldSemesterIndex);
                    Cell newSemesterCell = row.getCell(newSemesterIndex);
                    Cell oldMarkCell = row.getCell(oldMarkIndex);
                    Cell newMarkCell = row.getCell(newMarkIndex);
                    Cell oldStatusCell = row.getCell(oldStatusIndex);
                    Cell newStatusCell = row.getCell(newStatusIndex);

                    String rollNumber = null;
                    if (rollNumberCell != null) {
                        rollNumber = rollNumberCell.getCellType() == Cell.CELL_TYPE_STRING ?
                                rollNumberCell.getStringCellValue().trim() : (rollNumberCell.getNumericCellValue() == 0 ?
                                "" : Integer.toString((int) rollNumberCell.getNumericCellValue()));
                    }

                    String subjectCode = null;
                    if (subjectCodeCell != null) {
                        subjectCode = subjectCodeCell.getStringCellValue().trim();
                    }

                    String oldSemester = null;
                    if (oldSemesterCell != null) {
                        oldSemester = oldSemesterCell.getStringCellValue().trim();
                    }

                    String newSemester = null;
                    if (newSemesterCell != null) {
                        newSemester = newSemesterCell.getStringCellValue().trim();
                    }

                    Double oldMark = null;
                    if (oldMarkCell != null) {
                        oldMark = oldMarkCell.getNumericCellValue();
                    }

                    Double newMark = null;
                    if (newMarkCell != null) {
                        newMark = newMarkCell.getNumericCellValue();
                    }

                    String oldStatus = null;
                    if (oldStatusCell != null) {
                        oldStatus = oldStatusCell.getStringCellValue().trim();
                    }

                    String newStatus = null;
                    if (newStatusCell != null) {
                        newStatus = newStatusCell.getStringCellValue().trim();
                    }

                    if (rollNumber != null && subjectCode != null && oldSemester != null && oldMark != null && oldStatus != null) {
                        if (newSemester != null && newMark != null && newStatus != null) {
                            UpdatedMarkObject markObj = new UpdatedMarkObject();
                            markObj.setRollNumber(rollNumber);
                            markObj.setSubjectCode(subjectCode);
                            markObj.setOldSemester(oldSemester);
                            markObj.setNewSemester(newSemester);
                            markObj.setOldMark(oldMark);
                            markObj.setNewMark(newMark);
                            markObj.setOldStatus(oldStatus);
                            markObj.setNewStatus(newStatus);

                            updateList.add(markObj);
                        }
                    }
                }
            }

            this.totalLine = updateList.size();

            Set<StudentEntity> studentSet = new HashSet<>();
            for (UpdatedMarkObject markObj : updateList) {
                StudentEntity student = studentService.findStudentByRollNumber(markObj.getRollNumber());
                studentSet.add(student);
                RealSemesterEntity oldSemesterEntity = findOrCreateSemester(semesterList, markObj.getOldSemester());
                MarksEntity marksEntity = marksService.getMarkByAllFields(student.getId(),
                        markObj.getSubjectCode(), oldSemesterEntity.getId(), markObj.getOldMark(),
                        markObj.getOldStatus(), markComponent.getId());
                if (marksEntity != null) {
                    if (!markObj.getOldSemester().equalsIgnoreCase(markObj.getNewSemester())) {
                        RealSemesterEntity newSemesterEntity = findOrCreateSemester(semesterList, markObj.getNewSemester());
                        marksEntity.setSemesterId(newSemesterEntity);

                        CourseEntity courseEntity = courseService.findCourseBySemesterAndSubjectCode(markObj.getNewSemester(), markObj.getSubjectCode());
                        if (courseEntity == null) {
                            courseEntity = new CourseEntity();
                            courseEntity.setSubjectCode(markObj.getSubjectCode());
                            courseEntity.setSemester(markObj.getNewSemester());

                            courseService.createCourse(courseEntity);
                        }
                        marksEntity.setCourseId(courseEntity);
                    }

                    if (markObj.getOldMark() != markObj.getNewMark()) {
                        marksEntity.setAverageMark(markObj.getNewMark());
                    }

                    if (!markObj.getOldStatus().equalsIgnoreCase(markObj.getNewStatus())) {
                        marksEntity.setStatus(markObj.getNewStatus());
                    }

                    marksService.updateMark(marksEntity);
                }
                ++this.currentLine;
                System.out.println(currentLine + " - " + totalLine);
            }
            this.UpdateStudentCredits(studentSet);

            workbook.close();
            is.close();
            jsonObject.addProperty("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", e.getMessage());
        }

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

