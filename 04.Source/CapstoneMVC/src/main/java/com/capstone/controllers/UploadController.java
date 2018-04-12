package com.capstone.controllers;

import com.capstone.exporters.ExportConvert2StudentQuantityByClassAndSubject;
import com.capstone.exporters.IExportObject;
import com.capstone.jpa.GraduateDetailEntityJpaController;
import com.capstone.models.*;
import com.capstone.services.*;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import com.capstone.entities.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.capstone.models.Ultilities.distinctByKey;
import static com.capstone.models.Ultilities.sendNotification;


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
    private boolean isExcelRunning = true;

    @Autowired
    ServletContext context;

    @Autowired
    AndroidPushNotificationsService androidPushNotificationsService;

    ICourseStudentService courseStudentService = new CourseStudentServiceImpl();
    IScheduleService scheduleService = new ScheduleServiceImpl();
    IDaySlotService daySlotService = new DaySlotServiceImpl();
    ISlotService slotService = new SlotServiceImpl();
    IRoomService roomService = new RoomServiceImpl();
    IEmployeeService employeeService = new EmployeeServiceImpl();
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
    IDepartmentService departmentService = new DepartmentServiceImpl();
    IEmployeeCompetenceService employeeCompetenceService = new EmployeeCompetenceServiceImpl();

    /**
     * --------------STUDENTS------------
     **/
    @RequestMapping(value = "/goUploadStudentList")
    public ModelAndView goUploadStudentListPage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

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

    @RequestMapping(value = "/updateStatusForStudentsPage")
    public ModelAndView goUpdateStatusForStudentPage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView mav = new ModelAndView("updateStatusForStudents");
        mav.addObject("title", "Cập nhật trạng thái tốt nghiệp cho sinh viên");

        List<RealSemesterEntity> semesters = realSemesterService.getAllSemester();
        semesters = Ultilities.SortSemesters(semesters);
        semesters = semesters.stream().filter(s -> !s.getSemester().contains("N/A")).collect(Collectors.toList());
        semesters = Lists.reverse(semesters);

        mav.addObject("semesters", semesters);

        return mav;
    }

    @RequestMapping(value = "/importStudentCurriculumsPage")
    public ModelAndView goImportStudentCurriculumsPage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView mav = new ModelAndView("ImportStudentCurriculum");
        mav.addObject("title", "Nhập khung chương trình cho sinh viên");

        return mav;
    }

    @RequestMapping(value = "/uploadStudentCurriculumsPage")
    public ModelAndView goUploadStudentCurriculumsPage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView mav = new ModelAndView("uploadStudentCurriculums");
        List<RealSemesterEntity> semesters = realSemesterService.getAllSemester();
        semesters = Ultilities.SortSemesters(semesters);
        semesters = semesters.stream().filter(s -> !s.getSemester().contains("N/A")).collect(Collectors.toList());
        semesters = Lists.reverse(semesters);

        mav.addObject("semesters", semesters);
        mav.addObject("title", "Cập nhật khung chương trình cho sinh viên");

        return mav;
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
                    Ultilities.logUserAction("Upload student exist file - " + file);
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
                Ultilities.logUserAction("upload student list ");
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

    //fixed , tạo student Status nếu chưa có
    //dành cho cập nhật trạng thái của sinh viên tốt nghiệp
    @RequestMapping(value = "/updateStatusForStudents", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject updateStatusForStudents(@RequestParam("updateFile") MultipartFile file, @RequestParam("semesterId") Integer semesterId) {
        JsonObject jsonObject = new JsonObject();
        Ultilities.logUserAction("Update student status (usually for graduated Student)");
        try {
            InputStream is = file.getInputStream();

            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);

            XSSFRow row;
            int excelDataIndex = 3;
            int rollNumberColIndex = 1;
            //xếp loại
            int gradeColIndex = 5;
            //hình thức đào tạo
            int formColIndex = 6;
            //số hiệu văn vằng
            int diplomaCodeColIndex = 7;
            //vào sổ cấp văn bằng, chứng chỉ số
            int certificateCodeColIndex = 8;
            //số quyết định tốt nghiệp
            int graduateDecisionNumberColIndex = 9;
            //ngày quyết định
            int dateColIndex = 10;


            int lastRow = spreadsheet.getLastRowNum();
            this.totalLine = lastRow - startRowNumber + 1;
            GraduateDetailServiceImpl graduateDetailService = new GraduateDetailServiceImpl();

            RealSemesterEntity selectedSemester = realSemesterService.findSemesterById(semesterId);

            for (int rowIndex = excelDataIndex; rowIndex <= lastRow; rowIndex++) {
                System.out.println(rowIndex);
                row = spreadsheet.getRow(rowIndex);
                if (row != null) {
                    Cell rollNumberCell = row.getCell(rollNumberColIndex);
                    String rollNumber = rollNumberCell.getCellType() == Cell.CELL_TYPE_STRING ?
                            rollNumberCell.getStringCellValue().trim().toUpperCase() : Integer.toString((int) rollNumberCell.getNumericCellValue()).trim().toUpperCase();
                    if (rollNumberCell != null) {
                        StudentEntity studentEntity = studentService.findStudentByRollNumber(rollNumber);
                         int studentId = studentEntity.getId();
                        if (studentEntity != null) {
                            StudentStatusEntity studentStatusEntity = studentStatusService.getStudentStatusBySemesterIdAndStudentId(semesterId, studentEntity.getId());
                            if (studentStatusEntity != null) {
                                // update status
                                studentStatusEntity.setStatus(Enums.StudentStatus.Graduated.getValue());
                                studentStatusService.updateStudentStatus(studentStatusEntity);
                            } else {
                                //tạo mới student status cho những sinh viên tốt nghiệp chưa có status
                                //vd: sinh viên A đủ tín chỉ để cấp = tốt nghiệp sau khi passed đồ án vào cuối FALL2017
                                //  -> sinh viên A  sẽ được nhà trường xét duyệt tốt nghiệp 3 tuần sau đó
                                //  -> sẽ tính sinh viên A tốt nghiệp vào đầu kì SPRING2018

                                int previousSemesterId = Ultilities.GetSemesterIdBeforeThisId(semesterId);
                                StudentStatusEntity previousStatus = studentStatusService.
                                        getStudentStatusBySemesterIdAndStudentId(previousSemesterId, studentEntity.getId());

                                studentStatusEntity = new StudentStatusEntity();
                                studentStatusEntity.setStudentId(studentEntity);
                                studentStatusEntity.setStatus(Enums.StudentStatus.Graduated.getValue());
                                studentStatusEntity.setTerm(previousStatus.getTerm());
                                studentStatusEntity.setSemesterId(selectedSemester);

                                studentStatusService.createStudentStatus(studentStatusEntity);
                            }

                            String gradeValue = row.getCell(gradeColIndex).getStringCellValue();
                            String formValue = row.getCell(formColIndex).getStringCellValue();

                            String diplomaValue = "";
                            Cell diplomaCodeCell = row.getCell(diplomaCodeColIndex);
                            if (diplomaCodeCell.getCellTypeEnum() == CellType.STRING) {
                                diplomaValue = diplomaCodeCell.getStringCellValue();
                            } else if (diplomaCodeCell.getCellTypeEnum() == CellType.NUMERIC) {
                                diplomaValue = diplomaCodeCell.getNumericCellValue() + "";
                            }

                            String certificateValue = row.getCell(certificateCodeColIndex).getStringCellValue();
                            String graduateNumberValue = row.getCell(graduateDecisionNumberColIndex).getStringCellValue();
                            String dateValue = row.getCell(dateColIndex).getStringCellValue();

                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            Date date = sdf.parse(dateValue);
                            GraduateDetailEntity graduateDetailEntity = graduateDetailService.findGraduateDetailEntity(studentId);
                            if(graduateDetailEntity == null){
                                graduateDetailEntity = new GraduateDetailEntity();
                                graduateDetailEntity.setStudentId(studentId);
                                graduateDetailEntity.setDiplomaCode(diplomaValue);
                                graduateDetailEntity.setCertificateCode(certificateValue);
                                graduateDetailEntity.setGraduateDecisionNumber(graduateNumberValue);
                                graduateDetailEntity.setForm(formValue);
                                graduateDetailEntity.setDate(sdf.format(date));
                                graduateDetailEntity.setGraded(gradeValue);

                                graduateDetailService.create(graduateDetailEntity);
                            }else{
                                graduateDetailEntity.setStudentId(studentId);
                                graduateDetailEntity.setDiplomaCode(diplomaValue);
                                graduateDetailEntity.setCertificateCode(certificateValue);
                                graduateDetailEntity.setGraduateDecisionNumber(graduateNumberValue);
                                graduateDetailEntity.setForm(formValue);
                                graduateDetailEntity.setDate(sdf.format(date));
                                graduateDetailEntity.setGraded(gradeValue);

                                graduateDetailService.edit(graduateDetailEntity);
                            }
                        }
                    }
                }
            }

            this.currentLine = 0;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.writeLog(ex);
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", ex.getMessage());

            return jsonObject;
        }

        jsonObject.addProperty("success", true);
        jsonObject.addProperty("message", "Cập nhật trạng thái cho sinh viên tốt nghiệp thành công !");
        return jsonObject;
    }


    @RequestMapping(value = "/importStudentCurriculums", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject importStudentCurriculum(@RequestParam("file") MultipartFile file) {
        JsonObject jsonObject = new JsonObject();

        Ultilities.logUserAction("Import student curriculum");
        try {
            InputStream is = file.getInputStream();

            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);

            XSSFRow row;

            int excelDataIndex = 1;
            int lastRow = spreadsheet.getLastRowNum();

            int rollNumberIndex = 0;
            int curriculumIndex = 1;

            int count = 0;

            for (int index = excelDataIndex; index <= lastRow; index++) {
                row = spreadsheet.getRow(index);
                if (row != null) {
                    Cell rollNumberCell = row.getCell(rollNumberIndex);
                    if (rollNumberCell != null) {
                        String rollNumber = rollNumberCell.getCellType() == Cell.CELL_TYPE_STRING ?
                                rollNumberCell.getStringCellValue().trim().toUpperCase() :
                                Integer.toString((int) rollNumberCell.getNumericCellValue()).trim().toUpperCase();
                        StudentEntity studentEntity = studentService.findStudentByRollNumber(rollNumber);
                        if (studentEntity != null) {
                            Cell curriculumCell = row.getCell(curriculumIndex);
                            String curriculumCellValue = curriculumCell.getStringCellValue().trim().toUpperCase();

                            boolean found = false;
                            List<DocumentStudentEntity> documentStudentEntityList = studentEntity.getDocumentStudentEntityList();
                            for (DocumentStudentEntity documentStudentEntity : documentStudentEntityList) {
                                CurriculumEntity curriculumEntity = documentStudentEntity.getCurriculumId();

                                if (curriculumEntity.getName().trim().toUpperCase().equals(curriculumCellValue)) {
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) {
                                // Start create new
                                DocumentEntity documentEntity = documentService.getAllDocuments().get(0); // get document
                                CurriculumEntity curriculumEntity = curriculumService.getCurriculumByName(curriculumCellValue);
                                DocumentStudentEntity documentStudentEntity = new DocumentStudentEntity();
                                documentStudentEntity.setStudentId(studentEntity);
                                documentStudentEntity.setDocumentId(documentEntity);
                                documentStudentEntity.setCurriculumId(curriculumEntity);

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(new Date());
                                documentStudentEntity.setCreatedDate(calendar.getTime());

                                documentStudentService.createDocumentStudent(documentStudentEntity);
                                count++;
                            }
                        }
                    }
                }
            }
            System.out.println(count);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.writeLog(ex);
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", ex.getMessage());

            return jsonObject;
        }

        jsonObject.addProperty("success", true);
        jsonObject.addProperty("message", "Thêm khung chương trình cho sinh viên thành công !");
        return jsonObject;
    }

    //fix this (làm version 2 cho cái này)
    //làm update kì hiện tại cho sinh viên, sinh ra student status
    @RequestMapping(value = "/updateStudentCurriculums", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject updateStudentCurriculum(@RequestParam("file") MultipartFile file) {
        JsonObject jsonObject = new JsonObject();
        Ultilities.logUserAction("Update student curriculum");
        try {
            InputStream is = file.getInputStream();

            String originalFileName = file.getOriginalFilename();
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

            int excelDataIndexRow = 1;

            int rollNumberIndex = 0;
            int curriculumIndex1 = 13; // du bi
            int curriculumIndex2 = 14; // chuyen nganh
            int curriculumIndex3 = 15; // OJT
            int curriculumIndex4 = 16; // chuyen nganh hep

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            for (int rowIndex = excelDataIndexRow; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
                System.out.println(rowIndex + " - " + spreadsheet.getLastRowNum());
                row = spreadsheet.getRow(rowIndex);
                if (row != null) {
                    Cell rollNumberCell = row.getCell(rollNumberIndex);
                    Cell curriculumCell1 = row.getCell(curriculumIndex1);
                    Cell curriculumCell2 = row.getCell(curriculumIndex2);
                    Cell curriculumCell3 = row.getCell(curriculumIndex3);
                    Cell curriculumCell4 = row.getCell(curriculumIndex4);

                    if (rollNumberCell != null) {
                        String rollNumberValue = rollNumberCell.getStringCellValue().trim().toUpperCase();
                        StudentEntity studentEntity = studentService.findStudentByRollNumber(rollNumberValue);

                        List<DocumentStudentEntity> oldDocumentStudentEntites = studentEntity.getDocumentStudentEntityList();
                        for (DocumentStudentEntity old : oldDocumentStudentEntites) {
                            documentStudentService.deleteDocumentStudent(old.getId());
                        }

                        if (studentEntity != null) {
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
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.writeLog(ex);
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", ex.getMessage());

            return jsonObject;
        }

        jsonObject.addProperty("success", true);
        jsonObject.addProperty("message", "Cập nhật khung chương trình cho sinh viên thành công !");
        return jsonObject;
    }

    @RequestMapping(value = "/updateStudentCurriculumsVer2", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject updateStudentCurriculumVer2(@RequestParam("file") MultipartFile file
            , @RequestParam("semesterId") String semesterIdStr, HttpServletRequest request) {
        JsonObject jsonObject = new JsonObject();
        Ultilities.logUserAction("Update students curriculums");
        try {
            InputStream is = file.getInputStream();

            String originalFileName = file.getOriginalFilename();
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

            int excelDataIndexRow = 3;
            int excelDataHeader = 2;

            int rollNumberIndex = 1;
            int currentCurriculumIndex = 8; // Chuyen ngành hiện tại của kì học mới
            int termIndex = 10; // kì (vd: kì 1, kì 2, kì 3, ...) hiện tại của sinh viên trong kì học mới(vd: FALL2017, SPring2018, ..)
            int statusIndex = 13; // trạng thái sv kì mới (vd: HL - học lại, HD - Học đi,..)

//            check định dạng
//            row = spreadsheet.getRow(excelDataHeader);
//            Cell rollNumberHeader = row.getCell(rollNumberIndex);
//            Cell curriculumCell1 = row.getCell(currentCurriculumIndex);
//            Cell curriculumCell2 = row.getCell(termIndex);
//            Cell curriculumCell3 = row.getCell(statusIndex);

            int semesterId = Integer.parseInt(semesterIdStr);
            RealSemesterEntity selectedSemester = realSemesterService.findSemesterById(semesterId);

            //list chứa student lỗi hoặc không import được curriculum, status vì lý do nào đó
            HashMap<StudentEntity, String> cantImport = new HashMap<>();
//            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            List<StudentEntity> bulkImport = new ArrayList<>();
            int countStop = 0;
            dataLoop:
            for (int rowIndex = excelDataIndexRow; rowIndex <= spreadsheet.getLastRowNum(); rowIndex++) {
                System.out.println(rowIndex + " - " + spreadsheet.getLastRowNum());
                row = spreadsheet.getRow(rowIndex);
                if (row != null) {
                    Cell rollNumberCell = row.getCell(rollNumberIndex);
                    Cell curriculumCell = row.getCell(currentCurriculumIndex);
                    Cell termCell = row.getCell(termIndex);
                    Cell statusCell = row.getCell(statusIndex);

                    //nếu data trắng quá 2 dòng thì sẽ dừng vòng lặp
                    if (countStop > 2) {
                        break dataLoop;
                    }

                    if (rollNumberCell != null && rollNumberCell.getCellTypeEnum() == CellType.BLANK) {
                        countStop++;
                        continue dataLoop;
                    }

                    if (rollNumberCell != null && rollNumberCell.getCellTypeEnum() != CellType.BLANK) {
                        String rollNumberValue = "";
                        if (rollNumberCell.getCellTypeEnum() == CellType.NUMERIC) {
                            rollNumberValue = rollNumberCell.getNumericCellValue() + "";
                        } else if (rollNumberCell.getCellTypeEnum() == CellType.STRING) {
                            rollNumberValue = rollNumberCell.getStringCellValue().trim().toUpperCase();
                        }
                        StudentEntity studentEntity = studentService.findStudentByRollNumber(rollNumberValue);


                        if (studentEntity != null) {
                            // start save document student
                            DocumentEntity documentEntity = documentService.getAllDocuments().get(0);

                            if (curriculumCell != null && curriculumCell.getCellTypeEnum() != CellType.BLANK) {
                                String curriculumName = curriculumCell.getStringCellValue().trim();
                                List<DocumentStudentEntity> docsStudent = new ArrayList<>(studentEntity.getDocumentStudentEntityList());

                                CurriculumEntity exist = docsStudent.stream().filter(q -> q.getCurriculumId().getName().equalsIgnoreCase(curriculumName))
                                        .map(q -> q.getCurriculumId())
                                        .findFirst().orElse(null);
                                //tạo docs student mới cho sinh viên (sinh viên chuyển qua chuyên ngành khác thì cần khung chương trình tương ứng)
                                if (exist == null) {

                                    CurriculumEntity curriculumEntity = curriculumService.getCurriculumLikeName(curriculumName);
                                    if (curriculumEntity != null) {
                                        DocumentStudentEntity documentStudentEntity = new DocumentStudentEntity();
                                        documentStudentEntity.setStudentId(studentEntity);
                                        documentStudentEntity.setDocumentId(documentEntity);
                                        documentStudentEntity.setCurriculumId(curriculumEntity);
                                        documentStudentEntity.setCreatedDate(new Date());
                                        studentEntity.getDocumentStudentEntityList().add(documentStudentEntity);
                                    } else {
                                        cantImport.put(studentEntity, curriculumName + " curriculum not exist");
                                        continue dataLoop;
                                    }

                                }

                            }

                            if (termCell != null && termCell.getCellTypeEnum() != CellType.BLANK
                                    && statusCell != null && statusCell.getCellTypeEnum() != CellType.BLANK) {

                                String term = "";
                                if (termCell.getCellTypeEnum() == CellType.NUMERIC) {
                                    term = termCell.getNumericCellValue() + "";
                                } else if (termCell.getCellTypeEnum() == CellType.STRING) {
                                    term = termCell.getStringCellValue().trim();
                                }
                                String status = statusCell.getStringCellValue().trim().toUpperCase();

                                Integer studentTerm = null;
                                String statusTerm = null;
                                if (term.contains("+")) {
                                    //lấy số kì ra (vd: term = "9+")
                                    Pattern p = Pattern.compile("(\\d)");
                                    Matcher m = p.matcher(term);   // get a matcher object

                                    m.find();
                                    String token = m.group(0); //group 0 is always the entire match
                                    try {
                                        double tempTerm = Double.parseDouble(token);
                                        studentTerm = (int) tempTerm;

                                        //những ai có số kì là + thì đều + 0.1 để thành 9.1, 6.1(giả sử),
                                        statusTerm = (tempTerm + 0.1) + "";

                                    } catch (NumberFormatException ex) {
                                        System.out.println(ex.getMessage());
                                        cantImport.put(studentEntity, token + " term is not a number");
                                        continue dataLoop;
                                    }

                                } else {
                                    if (term.contains("ENG6")) {
                                        studentTerm = 0;
                                    } else if (term.contains("ENG5")) {
                                        studentTerm = -1;
                                    } else if (term.contains("ENG4")) {
                                        studentTerm = -2;
                                    } else if (term.contains("ENG3")) {
                                        studentTerm = -3;
                                    } else if (term.contains("ENG2")) {
                                        studentTerm = -4;
                                    }
                                    //dành cho những sinh viên đi ojt lâu hơn 1 kì -> giả sử là 6', 7'
                                    else if (term.contains("'")) {
                                        String[] splitList = term.split("'");
                                        try {
                                            Integer tempTerm = Integer.parseInt(splitList[0]);
                                            studentTerm = tempTerm;

                                        } catch (NumberFormatException ex) {
                                            System.out.printf(ex.getMessage());
                                            cantImport.put(studentEntity, term + " term is not a number");
                                        }
                                    }
                                    statusTerm = term;

                                    if (studentTerm == null) {
                                        try {
                                            double tempTerm = Double.parseDouble(term);
                                            studentTerm = (int) tempTerm;
                                            statusTerm = (int) tempTerm + "";
                                        } catch (NumberFormatException ex) {
                                            System.out.println(ex.getMessage());
                                            cantImport.put(studentEntity, term + " term is not a number");

                                            continue dataLoop;
                                        }
                                    }
                                }
                                studentEntity.setTerm(studentTerm);

                                //tạo student status mớicho sinh viên
                                List<StudentStatusEntity> statusList = new ArrayList<>(studentEntity.getStudentStatusEntityList());
                                boolean statusExist = statusList.stream().anyMatch(q -> q.getSemesterId().getId() == selectedSemester.getId());

                                if (!statusExist) {
                                    StudentStatusEntity studentStatusEntity = new StudentStatusEntity();
                                    studentStatusEntity.setStudentId(studentEntity);
                                    studentStatusEntity.setStatus(status);
                                    studentStatusEntity.setSemesterId(selectedSemester);
                                    studentStatusEntity.setTerm(statusTerm);
                                    studentEntity.getStudentStatusEntityList().add(studentStatusEntity);
                                }

                                bulkImport.add(studentEntity);
//                                studentService.myUpdateStudent(studentEntity);
                            }
                        }
                    }
                }
            }//end of dataLoop
            studentService.myBulkUpdateStudents(bulkImport);
            request.getSession().setAttribute("failImportStudentCurriculumNStatus", cantImport);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.writeLog(ex);
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", ex.getMessage());

            return jsonObject;
        }

        jsonObject.addProperty("success", true);
        jsonObject.addProperty("message", "Cập nhật khung chương trình cho sinh viên thành công !");
        return jsonObject;
    }

    @RequestMapping("/processFailImportCurriculums")
    @ResponseBody
    public JsonObject GetFailImportCurriculums(@RequestParam Map<String, String> params, HttpServletRequest request) {
        JsonObject obj = new JsonObject();

        //lấy ra danh sách những sinh viên không import, update được curriculum và status, term
        // <Student, Error>
        HashMap<StudentEntity, String> studentList = (HashMap<StudentEntity, String>) request.getSession().getAttribute("failImportStudentCurriculumNStatus");

//        final String sSearch = params.get("sSearch");

//        int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
//        int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
//        boolean isGraduate = Boolean.parseBoolean(params.get("boolean"));

        try {
            // RollNumber, FullName, Term
            List<List<String>> data = new ArrayList<>();
            if (studentList != null) {
                for (StudentEntity student :
                        studentList.keySet()) {
                    List<String> tempData = new ArrayList<>();
                    tempData.add(student.getRollNumber());
                    tempData.add(student.getFullName());
                    tempData.add(student.getTerm() + "");
                    String error = studentList.get(student);
                    tempData.add(error);
                    data.add(tempData);
                }
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(data);
            obj.add("aaData", aaData);
//            obj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
        }

        return obj;
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
    public ModelAndView goUploadStudentMarksPage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView view = new ModelAndView("uploadStudentMarks");
        view.addObject("title", "Nhập danh sách điểm");

        ReadAndSaveFileToServer read = new ReadAndSaveFileToServer();
        File[] list = read.readFiles(context, marksFolder);
        view.addObject("files", list);
        return view;
    }

    @RequestMapping(value = "/importStudyingStudentPage")
    public ModelAndView goImportStudyingStudentPage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView mav = new ModelAndView("importStudyingStudent");
        mav.addObject("title", "Nhập điểm sinh viên đang học");

        List<RealSemesterEntity> semesters = realSemesterService.getAllSemester();
        semesters = Ultilities.SortSemesters(semesters);
        semesters = semesters.stream().filter(s -> !s.getSemester().contains("N/A")).collect(Collectors.toList());
        semesters = Lists.reverse(semesters);

        mav.addObject("semesters", semesters);

        return mav;
    }

    @RequestMapping(value = "/updateMarkForStudyingStudentPage")
    public ModelAndView goUpdateMarkForStudyingStudentPage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView mav = new ModelAndView("updateMarkForStudyingStudent");
        mav.addObject("title", "Cập nhật điểm cho sinh viên đang học");

        List<RealSemesterEntity> semesters = realSemesterService.getAllSemester();
        semesters = Ultilities.SortSemesters(semesters);
        semesters = semesters.stream().filter(s -> !s.getSemester().contains("N/A")).collect(Collectors.toList());
        semesters = Lists.reverse(semesters);

        mav.addObject("semesters", semesters);

        return mav;
    }

    @RequestMapping(value = "/importEmployeesPage")
    public ModelAndView goImportEmployeesPage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView mav = new ModelAndView("importEmployees");
        mav.addObject("title", "Nhập danh sách giảng viên");

        return mav;
    }

    @RequestMapping(value = "/importEmployeeCompetencesPage")
    public ModelAndView goImportEmployeeCompetencesPage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());
        ModelAndView mav = new ModelAndView("importEmployeeCompetences");
        mav.addObject("title", "Nhập danh sách GV-Môn học");

        return mav;
    }

    @RequestMapping(value = "/importRoomsPage")
    public ModelAndView goImportRoomsPage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView mav = new ModelAndView("importRooms");
        mav.addObject("title", "Nhập danh sách phòng");

        return mav;
    }

    @RequestMapping(value = "/importDepartmentsPage")
    public ModelAndView goImportDepartmentsPage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView mav = new ModelAndView("importDepartments");
        mav.addObject("title", "Nhập danh sách bộ môn");

        return mav;
    }

    @RequestMapping(value = "/importSchedulesPage")
    public ModelAndView goImportSchedulesPage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView mav = new ModelAndView("importSchedules");
        mav.addObject("title", "Nhập danh sách lịch dạy của GV");
        List<RealSemesterEntity> semesters = realSemesterService.getAllSemester();
        semesters = Ultilities.SortSemesters(semesters);

        mav.addObject("semesters", semesters);
        return mav;
    }

    @RequestMapping(value = "/importCourseStudentsPage")
    public ModelAndView goImportCourseStudentPage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());

        ModelAndView mav = new ModelAndView("importCourseStudents");
        mav.addObject("title", "Nhập danh sách lớp của SV");
        List<RealSemesterEntity> semesters = realSemesterService.getAllSemester();
        semesters = Ultilities.SortSemesters(semesters);

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
        Ultilities.logUserAction("Upload exist mark file");
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

        Ultilities.logUserAction("Upload student mark");

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

    //fix thành import cả notStart, (làm version 2 )
    //import diem cho hoc sinh
    @RequestMapping(value = "/uploadStudyingStudent", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject importStudyingStudent(@RequestParam("file") MultipartFile file, @RequestParam("semesterId") String semesterIdStr) {
        JsonObject jsonObject = new JsonObject();
        List<MarksEntity> marksEntities = new ArrayList<MarksEntity>();

        Integer semesterId = Integer.parseInt(semesterIdStr.trim());
        RealSemesterEntity realSemesterEntity = realSemesterService.findSemesterById(semesterId);
        Ultilities.logUserAction("Upload studying student ( generate studying and does not generate notstart mark)");

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
                        Cell semesterNameCell = row.getCell(semesterNameIndex);
                        Cell subjectCodeCell = row.getCell(subjectCodeIndex);
                        Cell averageMarkCell = row.getCell(averageMarkIndex);
                        Cell statusCell = row.getCell(statusIndex);
                        Cell enabledCell = row.getCell(enabledIndex);

                        List<MarksEntity> studentMarks =
                                marksService.findMarksByStudentIdAndSubjectCdAndSemesterId(studentEntity.getId(), subjectCodeCell.getStringCellValue().trim().toUpperCase(), semesterId);

                        boolean found = false;
                        for (MarksEntity marksEntity : studentMarks) {
                            if (marksEntity.getStatus().equals("Studying")) {
                                found = true;
                                break;
                            }
                        }

                        String status = statusCell.getStringCellValue();
                        String semesterName = semesterNameCell.getStringCellValue().trim().toUpperCase().replaceAll(" ", "");
                        if ((!found || !status.equals("Studying")) && (realSemesterEntity.getSemester().equals(semesterName))) {
                            MarksEntity mark = new MarksEntity();
                            // set Student
                            mark.setStudentId(studentEntity);
                            // set semester
                            mark.setSemesterId(realSemesterEntity);
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
                                mark.setStatus(status);
                            }

                            // set isActivated
                            mark.setIsActivated(true);

                            // set Enabled
                            if (enabledCell != null) {
                                mark.setEnabled(enabledCell.getBooleanCellValue());
                            } else {
                                mark.setEnabled(true);
                            }

                            // add to list mark entities
                            marksEntities.add(mark);
                        }
                    }
                }
                this.currentLine++;
            }
            marksService.createMarks(marksEntities);
            jsonObject.addProperty("success", true);
            jsonObject.addProperty("message", "Import sinh viên đang học thành công !");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.writeLog(ex);
            jsonObject.addProperty("fail", false);
            jsonObject.addProperty("message", ex.getMessage());
        }


        return jsonObject;
    }

    //import điểm studying và notStart cho sinh viên đang học
    @RequestMapping(value = "/uploadStudyingStudentVer2", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject importStudyingStudentVer2(@RequestParam("file") MultipartFile file
            , @RequestParam("semesterId") String semesterIdStr, HttpServletRequest request) {
        JsonObject jsonObject = new JsonObject();
        List<MarksEntity> marksEntities = new ArrayList<MarksEntity>();

        Integer semesterId = Integer.parseInt(semesterIdStr.trim());

        HashMap<StudentEntity, List<MarkModelExcel>> dataExcel = new HashMap<>();
        RealSemesterEntity selectedSemester = realSemesterService.findSemesterById(semesterId);

        Ultilities.logUserAction("Upload studying student (generate studying and not start mark)");
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
            //Map<StudentRollNumber, error> chứa danh sách không sinh viên không import được ,hoặc gặp lỗi
            HashMap<String, String> errorList = new HashMap<>();
            List<SubjectEntity> allSubjects = subjectService.getAllSubjects();
            String semesterName = selectedSemester.getSemester();

            for (int rowIndex = excelDataIndex; rowIndex <= lastRow; rowIndex++) {
                row = spreadsheet.getRow(rowIndex);

                Cell rollNumberCell = row.getCell(rollNumberIndex);
                if (rollNumberCell != null && rollNumberCell.getCellTypeEnum() != CellType.BLANK) {
                    String rollNumber = "";
                    if (rollNumberCell.getCellTypeEnum() == CellType.NUMERIC) {
                        rollNumber = rollNumberCell.getNumericCellValue() + "";
                    } else if (rollNumberCell.getCellTypeEnum() == CellType.STRING) {
                        rollNumber = rollNumberCell.getStringCellValue().trim();
                    }
                    StudentEntity studentEntity = studentService.findStudentByRollNumber(rollNumber);
                    if (studentEntity != null) {
                        Cell semesterNameCell = row.getCell(semesterNameIndex);
                        Cell subjectCodeCell = row.getCell(subjectCodeIndex);
                        Cell averageMarkCell = row.getCell(averageMarkIndex);
                        Cell statusCell = row.getCell(statusIndex);
                        Cell enabledCell = row.getCell(enabledIndex);

                        String avgString = "";
//                        double avgMark = 0.0;
//                        if (averageMarkCell.getCellTypeEnum() == CellType.NUMERIC) {
//                            avgString = averageMarkCell.getNumericCellValue() + "";
//
//                        } else if (averageMarkCell.getCellTypeEnum() == CellType.STRING) {
//                            avgString = averageMarkCell.getStringCellValue().trim();
//                        }
//                        try {
//                            avgMark = Double.parseDouble(avgString);
//                        } catch (NumberFormatException e) {
//                            System.out.println(e);
//                        }


                        String subjectCode = subjectCodeCell.getStringCellValue().trim();
                        String status = Enums.MarkStatus.STUDYING.getValue();
                        MarkModelExcel tempModel = new MarkModelExcel(-1, semesterName, subjectCode, status);

                        if (dataExcel.containsKey(studentEntity)) {
                            List<MarkModelExcel> markList = dataExcel.get(studentEntity);
                            markList.add(tempModel);
                            dataExcel.put(studentEntity, markList);
                        } else {
                            List<MarkModelExcel> markList = new ArrayList<>();
                            markList.add(tempModel);
                            dataExcel.put(studentEntity, markList);
                        }
                    } else {
                        errorList.put(rollNumber, "Rollnumber not exist!");
                    }
                }
                System.out.println("Read " + (currentLine + 1) + " - " + lastRow);
                this.currentLine++;
            }


            int i = 1;
            for (StudentEntity studentEntity : dataExcel.keySet()) {
                System.out.println("process " + i + " - " + dataExcel.size());
//                if (studentEntity.getRollNumber().equalsIgnoreCase("SE62849")) {
////                    System.out.println("bug oi");
////                }
                List<SubjectCurriculumEntity> subjectCurriculumList = subjectCurriculumService.getSubjectCurriculumByStudent(studentEntity.getId());

                //lấy ra những môn học kì này sẽ học theo khung chương trình
                List<String> subjectList = subjectCurriculumList.stream()
                        .filter(q -> q.getTermNumber() == studentEntity.getTerm()).map(q -> q.getSubjectId().getId())
                        .collect(Collectors.toList());

                List<MarkModelExcel> excelMarks = dataExcel.get(studentEntity);

//                //chứa điểm học vượt, điểm trả nợ, chứa những điểm không có trong khung chương trình kì hiện tại của sinh viên
//                List<MarkModelExcel> markNotInCurriculums = excelMarks.stream()
//                        .filter(q -> !subjectList.contains(q.getSubjectId())).collect(Collectors.toList());

                //chứa những điểm học trong khung chương trình hiện tại theo kỳ hiện tại của sinh viên
                List<MarkModelExcel> markInCurriculums = excelMarks.stream()
                        .filter(q -> subjectList.contains(q.getSubjectId())).collect(Collectors.toList());

                //chứa những môn chậm tiến độ
                List<MarkModelExcel> notStartMarks = subjectList.stream()
                        .filter(q -> !markInCurriculums.stream().anyMatch(c -> c.getSubjectId().equalsIgnoreCase(q)))
                        .map(q -> new MarkModelExcel(-1.0, semesterName, q, Enums.MarkStatus.NOT_START.getValue()))
                        .collect(Collectors.toList());

                //xóa những môn notStart mà sinh viên đã học rồi(sử dụng trong trường hợp sinh viên đã học trước môn của kì tới)
                List<MarkModelExcel> removedMarks = new ArrayList<>(notStartMarks);
                List<MarksEntity> hasLearned = studentEntity.getMarksEntityList();
                for (int j = 0; j < removedMarks.size(); j++) {
                    boolean alreadyLearned = false;
                    MarkModelExcel mark = removedMarks.get(j);
                    for (MarksEntity mItem : hasLearned) {
                        String subId = mItem.getSubjectMarkComponentId().getSubjectId().getId();
                        if (subId.equalsIgnoreCase(mark.getSubjectId())) {
                            alreadyLearned = true;
                            break;
                        }
                    }
                    if (alreadyLearned)
                        notStartMarks.remove(mark);
                }

//                for (MarkModelExcel mark : removedMarks) {
//                    long exist = marksService.countMarksByStudentIdAndSubjectId(studentEntity.getId(), mark.getSubjectId());
//                    if (exist > 0) {
//                        notStartMarks.remove(mark);
//                    }
//                }

                List<MarkModelExcel> importedMark = new ArrayList<>();
//                importedMark.addAll(markNotInCurriculums);
//                importedMark.addAll(markInCurriculums);
                importedMark.addAll(excelMarks);
                importedMark.addAll(notStartMarks);

                importedMarkLoop:
                for (MarkModelExcel item : importedMark) {
                    SubjectEntity subjectEntity = allSubjects.stream()
                            .filter(q -> q.getId().equalsIgnoreCase(item.getSubjectId()))
                            .findFirst().orElse(null);

                    //kiểm tra xem subject có tồn tại không
                    if (subjectEntity == null) {
                        errorList.put(studentEntity.getRollNumber(), item.getSubjectId() + " - subject not exist!");
                        continue importedMarkLoop;
                    }
                    //tạo Mark mới
                    MarksEntity mark = new MarksEntity();

                    MarkComponentEntity markComponentEntity =
                            markComponentService.getMarkComponentByName(markComponentName);

                    String subjectMarkComponentName = subjectEntity.getId() + "_" + markComponentName;
                    SubjectMarkComponentEntity subjectMarkComponentEntity =
                            subjectMarkComponentService.findSubjectMarkComponentByNameAndSubjectCd(markComponentName, subjectEntity.getId());

                    //set subjectMarkComponent
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
                    //set student
                    mark.setStudentId(studentEntity);
                    //set Semester
                    mark.setSemesterId(selectedSemester);
                    //set Course, tạo Course nếu chưa tồn tại

                    CourseEntity courseEntity = courseService
                            .findCourseBySemesterAndSubjectCode(semesterName, subjectEntity.getId());
                    if (courseEntity != null) {
                        mark.setCourseId(courseEntity);
                    } else {
                        courseEntity = new CourseEntity();
                        courseEntity.setSemester(semesterName);
                        courseEntity.setSubjectCode(subjectEntity.getId());
                        courseEntity = courseService.createCourse(courseEntity);
                        mark.setCourseId(courseEntity);
                    }

                    //set mark, studying: hs chưa có điểm nên set vầy
                    mark.setAverageMark(-1.0);
                    mark.setStatus(item.getStatus());
                    mark.setIsActivated(true);
                    mark.setEnabled(true);

                    //bỏ vào list để import
                    marksEntities.add(mark);
                }
                i++;

            }

            //batch insert mark
            marksService.createMarks(marksEntities);
            //đẩy danh sách lỗi về cho user xem
            request.getSession().setAttribute("importMarksStudyingStudentVer2Error", errorList);
            jsonObject.addProperty("success", true);
            jsonObject.addProperty("message", "Import sinh viên đang học thành công !");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.writeLog(ex);
            jsonObject.addProperty("fail", false);
            jsonObject.addProperty("message", ex.getMessage());
        }


        return jsonObject;
    }

    //danh sách lỗi khi import điểm studying và notStart cho sinh viên đang học
    @RequestMapping("/getFailImportMark4StudyingStudent")
    @ResponseBody
    public JsonObject getFailImport4StudyingStudent(@RequestParam Map<String, String> params, HttpServletRequest request) {
        JsonObject obj = new JsonObject();

        //lấy ra danh sách những sinh viên không import, update được curriculum và status, term
        // <RollNumber, Error>
        HashMap<String, String> studentList = (HashMap<String, String>) request.getSession()
                .getAttribute("importMarksStudyingStudentVer2Error");
        String type = params.get("type");

//        final String sSearch = params.get("sSearch");

//        int iDisplayStart = Integer.parseInt(params.get("iDisplayStart"));
//        int iDisplayLength = Integer.parseInt(params.get("iDisplayLength"));
//        boolean isGraduate = Boolean.parseBoolean(params.get("boolean"));

        try {
            // RollNumber, FullName, Term
            List<List<String>> data = new ArrayList<>();
            if (studentList != null) {
                for (String rollNumber :
                        studentList.keySet()) {
                    List<String> tempData = new ArrayList<>();
                    tempData.add(rollNumber);
                    String error = studentList.get(rollNumber);
                    tempData.add(error);
                    data.add(tempData);
                }
            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(data);
            obj.add("aaData", aaData);
//            obj.addProperty("sEcho", params.get("sEcho"));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
        }

        return obj;
    }


    @RequestMapping(value = "/uploadEmployees", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject importEmployees(@RequestParam("file") MultipartFile file) {
        JsonObject jsonObject = new JsonObject();
        List<EmployeeEntity> employeeEntities = new ArrayList<EmployeeEntity>();

        Ultilities.logUserAction("Upload employee");
        try {
            InputStream is = file.getInputStream();

            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);

            XSSFRow row;
            int excelDataIndex = 1;
            int lastRow = spreadsheet.getLastRowNum();
            this.totalLine = lastRow - startRowNumber + 1;

            int codeIndex = 2;
            int fullNameIndex = 3;
            int positionIndex = 8;
            int emailEDUIndex = 11;
            int emailFEIndex = 12;
            int emailPersonalIndex = 10;
            int genderIndex = 4;
            int addressIndex = 6;
            int contractIndex = 7;
            int dobIndex = 5;
            int phoneIndex = 9;


            this.currentLine = 0;
            for (int rowIndex = excelDataIndex; rowIndex <= lastRow; rowIndex++) {
                row = spreadsheet.getRow(rowIndex);

                Cell fullNameCell = row.getCell(fullNameIndex);
                Cell emailEDUCell = row.getCell(emailEDUIndex);
                Cell emailFECell = row.getCell(emailFEIndex);

                if (fullNameCell != null && !fullNameCell.getStringCellValue().trim().equals("")) {
                    List<EmployeeEntity> employeeList = employeeService.findEmployeesByFullName(fullNameCell.getStringCellValue().trim());
                    if (employeeList.size() == 0) {

                        if (emailEDUCell != null && !emailEDUCell.getStringCellValue().trim().equals("")) {
                            employeeList = employeeService.findEmployeesByFullName(fullNameCell.getStringCellValue().trim());
                            if (employeeList.size() == 0) {

                                if (emailFECell != null && !emailFECell.getStringCellValue().trim().equals("")) {
                                    employeeList = employeeService.findEmployeesByFullName(fullNameCell.getStringCellValue().trim());
                                    if (employeeList.size() == 0) {
                                        EmployeeEntity employeeEntity = new EmployeeEntity();

                                        Cell codeCell = row.getCell(codeIndex);
                                        Cell positionCell = row.getCell(positionIndex);
                                        Cell emailPersonalCell = row.getCell(emailPersonalIndex);
                                        Cell genderCell = row.getCell(genderIndex);
                                        Cell addressCell = row.getCell(addressIndex);
                                        Cell dobCell = row.getCell(dobIndex);
                                        Cell phoneCell = row.getCell(phoneIndex);
                                        Cell contractCell = row.getCell(contractIndex);

                                        if (codeCell != null && !codeCell.getStringCellValue().trim().equals("")) {
                                            employeeEntity.setCode(codeCell.getStringCellValue());
                                        }

                                        employeeEntity.setFullName(fullNameCell.getStringCellValue());

                                        if (positionCell != null && !positionCell.getStringCellValue().trim().equals("")) {
                                            employeeEntity.setPosition(positionCell.getStringCellValue());
                                        }

                                        if (emailEDUCell != null && !emailEDUCell.getStringCellValue().trim().equals("")) {
                                            employeeEntity.setEmailEDU(emailEDUCell.getStringCellValue());
                                        }

                                        if (emailFECell != null && !emailFECell.getStringCellValue().trim().equals("")) {
                                            employeeEntity.setEmailFE(emailFECell.getStringCellValue());
                                        }

                                        if (emailPersonalCell != null && !emailPersonalCell.getStringCellValue().trim().equals("")) {
                                            employeeEntity.setPersonalEmail(emailPersonalCell.getStringCellValue());
                                        }

                                        boolean gender = genderCell.getStringCellValue().equals("Nam") ? true : false;
                                        employeeEntity.setGender(gender);

                                        if (addressCell != null && !addressCell.getStringCellValue().trim().equals("")) {
                                            employeeEntity.setAddress(addressCell.getStringCellValue());
                                        }

                                        String formattedDate = "";
                                        if (dobCell != null && !dobCell.toString().equals("")) {
                                            if (dobCell.getCellType() != Cell.CELL_TYPE_STRING) {
                                                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                                formattedDate = df.format(dobCell.getDateCellValue());
                                            } else {
                                                formattedDate = dobCell.getStringCellValue();
                                            }
                                        }

                                        employeeEntity.setDateOfBirth(formattedDate);

                                        if (phoneCell != null && !phoneCell.getStringCellValue().trim().equals("")) {
                                            employeeEntity.setPhone(phoneCell.getStringCellValue());
                                        }

                                        if (contractCell != null && !contractCell.getStringCellValue().trim().equals("")) {
                                            employeeEntity.setPhone(phoneCell.getStringCellValue());
                                        }

                                        employeeEntities.add(employeeEntity);
                                    }
                                }
                            }
                        }


                    }
                }
                this.currentLine++;
            }
            employeeService.createEmployeeList(employeeEntities);
            jsonObject.addProperty("success", true);
            jsonObject.addProperty("message", "Import giảng viên thành công !");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.writeLog(ex);
            jsonObject.addProperty("fail", false);
            jsonObject.addProperty("message", ex.getMessage());
        }


        return jsonObject;
    }

    @RequestMapping(value = "/uploadRooms", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject importRooms(@RequestParam("file") MultipartFile file) {
        JsonObject jsonObject = new JsonObject();
        List<RoomEntity> roomEntities = new ArrayList<RoomEntity>();

        Ultilities.logUserAction("Upload room");
        try {
            InputStream is = file.getInputStream();

            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);

            XSSFRow row;
            int excelDataIndex = 1;
            int lastRow = spreadsheet.getLastRowNum();
            this.totalLine = lastRow - startRowNumber + 1;

            int nameIndex = 1;
            int capacityIndex = 2;
            int noteIndex = 3;

            this.currentLine = 0;
            for (int rowIndex = excelDataIndex; rowIndex <= lastRow; rowIndex++) {
                row = spreadsheet.getRow(rowIndex);

                Cell nameCell = row.getCell(nameIndex);

                String name = "";

                if (nameCell.getCellType() != Cell.CELL_TYPE_STRING) {
                    name = String.valueOf((int) nameCell.getNumericCellValue());
                } else {
                    name = nameCell.getStringCellValue().trim();
                }

                if (nameCell != null && !name.equals("")) {
                    if (roomService.findRoomsByName(name).size() == 0) {
                        RoomEntity roomEntity = new RoomEntity();

                        Cell capacityCell = row.getCell(capacityIndex);
                        Cell noteCell = row.getCell(noteIndex);

                        roomEntity.setName(name);
                        roomEntity.setCapacity((int) capacityCell.getNumericCellValue());
                        roomEntity.setNote(noteCell.getStringCellValue());
                        roomEntity.setIsAvailable(true);

                        roomEntities.add(roomEntity);

                    }
                }
                this.currentLine++;
            }
            roomService.createRoomList(roomEntities);
            jsonObject.addProperty("success", true);
            jsonObject.addProperty("message", "Import phòng thành công !");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.writeLog(ex);
            jsonObject.addProperty("fail", false);
            jsonObject.addProperty("message", ex.getMessage());
        }

        return jsonObject;
    }


    @RequestMapping(value = "/uploadDepartments", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject importDepartments(@RequestParam("file") MultipartFile file) {
        JsonObject jsonObject = new JsonObject();
        Ultilities.logUserAction("Upload departments");
        try {
            InputStream is = file.getInputStream();

            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);

            XSSFRow row;
            int excelDataIndex = 1;
            int lastRow = spreadsheet.getLastRowNum();
            this.totalLine = lastRow - startRowNumber + 1;

            int subjectIndex = 0;
            int nameIndex = 1;

            this.currentLine = 0;
            for (int rowIndex = excelDataIndex; rowIndex <= lastRow; rowIndex++) {
                row = spreadsheet.getRow(rowIndex);
                Cell subjectCell = row.getCell(subjectIndex);
                Cell nameCell = row.getCell(nameIndex);

                String subjectCode = "";
                String name = "";

                subjectCode = subjectCell.getStringCellValue().trim();
                name = nameCell.getStringCellValue().trim();

                if (nameCell != null && !name.equals("")) {
                    if (departmentService.findDepartmentsByName(name).size() == 0) {
                        DepartmentEntity departmentEntity = new DepartmentEntity();
                        departmentEntity.setDeptName(name);
                        departmentService.createDepartment(departmentEntity);
                    }
                }

                if (subjectCode != null && !subjectCode.equals("")) {
                    List<DepartmentEntity> departmentList = departmentService.findDepartmentsByName(name);
                    if (departmentList.size() != 0) {
                        SubjectEntity subjectEntity = subjectService.findSubjectById(subjectCode);
                        DepartmentEntity departmentEntity = departmentList.get(0);
                        if (subjectEntity != null) {
                            if(subjectEntity.getDepartmentId()== null){
                                subjectEntity.setDepartmentId(departmentEntity);
                                EntityManagerFactory emf2 = Persistence.createEntityManagerFactory("CapstonePersistence");
                                EntityManager em = emf2.createEntityManager();
                                try {
                                    em.getTransaction().begin();
                                    em.merge(subjectEntity);
                                    em.getTransaction().commit();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                this.currentLine++;
            }

            jsonObject.addProperty("success", true);
            jsonObject.addProperty("message", "Import bộ môn thành công !");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.writeLog(ex);
            jsonObject.addProperty("fail", false);
            jsonObject.addProperty("message", ex.getMessage());
        }

        return jsonObject;
    }

    @RequestMapping(value = "/uploadEmployeeCompetences", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject importEmployeeCompetences(@RequestParam("file") MultipartFile file) {
        JsonObject jsonObject = new JsonObject();
        List<EmpCompetenceEntity> empCompetenceEntities = new ArrayList<>();
        Ultilities.logUserAction("importEmployeeCompetences");

        try {
            InputStream is = file.getInputStream();

            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);

            XSSFRow row;
            int excelDataIndex = 1;
            int lastRow = spreadsheet.getLastRowNum();
            this.totalLine = lastRow - startRowNumber + 1;

            int accountIndex = 0;
            int subjectIndex = 3;

            this.currentLine = 0;
            for (int rowIndex = excelDataIndex; rowIndex <= lastRow; rowIndex++) {
                row = spreadsheet.getRow(rowIndex);

                Cell accountCell = row.getCell(accountIndex);

                String account = "";

                account = accountCell.getStringCellValue().trim();

                EmployeeEntity employeeEntity = null;
                if (accountCell != null && !account.equals("")) {
                    employeeEntity = employeeService.findEmployeeByEmail(account + "@fpt.edu.vn");
                    if (employeeEntity == null) {
                        employeeEntity = employeeService.findEmployeeByEmail(account + "@fe.edu.vn");
                    }
                }

                if (employeeEntity != null) {
                    Cell subjectListCell = row.getCell(subjectIndex);
                    String subjectListStr = subjectListCell.getStringCellValue().trim();
                    if (subjectListCell != null && !subjectListStr.equals("")) {
                        List<String> subjectList = Arrays.asList(subjectListStr.split("\\s*,\\s*"));
                        for (String subject : subjectList) {
                            SubjectEntity aSubject = subjectService.findSubjectById(subject);

                            if (aSubject != null) {
                                EmpCompetenceEntity empCompetenceEntity = new EmpCompetenceEntity();
                                empCompetenceEntity.setEmployeeId(employeeEntity);
                                empCompetenceEntity.setSubjectId(aSubject);

                                empCompetenceEntities.add(empCompetenceEntity);
                            }
                        }
                    }
                }
                this.currentLine++;
            }
            employeeCompetenceService.createEmployeeCompetenceList(empCompetenceEntities);
            jsonObject.addProperty("success", true);
            jsonObject.addProperty("message", "Import GV-môn thành công !");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.writeLog(ex);
            jsonObject.addProperty("fail", false);
            jsonObject.addProperty("message", ex.getMessage());
        }

        return jsonObject;
    }


    public boolean isExcelRunning() {
        return isExcelRunning;
    }

    public void setExcelRunning(boolean excelRunning) {
        isExcelRunning = excelRunning;
    }

    @RequestMapping(value = "/uploadSchedules", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject importSchedules(@RequestParam("file") MultipartFile file, @RequestParam("semesterId") String semesterIdStr) {
        JsonObject jsonObject = new JsonObject();
        Ultilities.logUserAction("Upload schedules");

        List<DaySlotEntity> daySlotEntities = new ArrayList<DaySlotEntity>();
        List<ScheduleEntity> scheduleEntities = new ArrayList<ScheduleEntity>();
        List<SlotEntity> slots = null;
        List<RoomEntity> rooms = null;
        EmployeeEntity employee = null;
        CourseEntity course = null;

//        Map<EmployeeEntity, List<ScheduleEntity>> employeesMap = new HashMap<>();
//        Map<StudentEntity, List<ScheduleEntity>> studentsMap = new HashMap<>();

        try {
            InputStream is = file.getInputStream();

            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);
            XSSFRow row;
            int excelDataIndex = 1;
            int lastRow = spreadsheet.getLastRowNum();
            this.totalLine = lastRow;

            int groupNameIndex = 0;
            int courseIndex = 1;
            int dateIndex = 2;
            int slotNameIndex = 3;
            int roomNameIndex = 4;
            int employeeIndex = 5;

            this.currentLine = 0;

            this.isExcelRunning = true;

            for (int rowIndex = excelDataIndex; rowIndex <= lastRow; rowIndex++) {
                row = spreadsheet.getRow(rowIndex);
                if (row != null) {

                    Cell groupNameCell = row.getCell(groupNameIndex);
                    Cell courseCell = row.getCell(courseIndex);
                    Cell dateCell = row.getCell(dateIndex);
                    Cell slotNameCell = row.getCell(slotNameIndex);
                    Cell roomNameCell = row.getCell(roomNameIndex);
                    Cell employeeCell = row.getCell(employeeIndex);

                    if (dateCell != null && !dateCell.toString().equals("") && slotNameCell != null && !slotNameCell.toString().equals("")) {
                        String formattedDate = "";
                        if (dateCell.getCellType() != Cell.CELL_TYPE_STRING) {
                            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                            formattedDate = df.format(dateCell.getDateCellValue());
                        } else {
                            DateFormat df = new SimpleDateFormat("M/d/yyyy HH:mm:ss");
                            DateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");

                            formattedDate = df2.format(df.parse(dateCell.getStringCellValue()));
                        }

                        String slotName = "";
                        if (slotNameCell.getCellType() != Cell.CELL_TYPE_STRING) {
                            slotName = String.valueOf((int) slotNameCell.getNumericCellValue());
                        } else {
                            slotName = slotNameCell.getStringCellValue().trim();
                        }
                        if (!slotName.contains("Slot")) {
                            slotName = "Slot " + slotName;
                        }

                        slots = slotService.findSlotsByName(slotName);
                        if (slots.size() != 0) {
                            DaySlotEntity daySlotEntity = daySlotService.findDaySlotByDateAndSlot(formattedDate, slots.get(0));
                            //add DaySlot to DB
                            if (daySlotEntity == null) {
                                daySlotEntity = new DaySlotEntity();

                                daySlotEntity.setDate(formattedDate);
                                daySlotEntity.setSlotId(slots.get(0));
                                daySlotEntity = daySlotService.createDateSlot(daySlotEntity);
//                                daySlotEntities.add(daySlotEntity);
                            }


                            Integer semesterId = Integer.parseInt(semesterIdStr.trim());
                            RealSemesterEntity realSemesterEntity = realSemesterService.findSemesterById(semesterId);
                            course = courseService.findCourseBySemesterAndSubjectCode(realSemesterEntity.getSemester(), courseCell.getStringCellValue());
                            if (course == null) {
                                CourseEntity tmpCourse = new CourseEntity();
                                tmpCourse.setSubjectCode(courseCell.getStringCellValue());
                                tmpCourse.setSemester(realSemesterEntity.getSemester());
                                course = courseService.createCourse(tmpCourse);
                            }
                            String roomName = "";
                            if (roomNameCell.getCellType() != Cell.CELL_TYPE_STRING) {
                                roomName = String.valueOf((int) roomNameCell.getNumericCellValue());
                            } else {
                                roomName = roomNameCell.getStringCellValue().trim();
                            }
                            rooms = roomService.findRoomsByName(roomName);

                            if (course != null && rooms.size() > 0) {
//                                if (!course.getSubjectCode().contains("VOV") || !course.getSubjectCode().contains("LAB")) {
                                employee = employeeService.findEmployeeByShortName(employeeCell.getStringCellValue());
//                                }

//                                DaySlotEntity daySlot = daySlotService.findDaySlotByDateAndSlot(formattedDate, slots.get(0));
//                                if (scheduleService.findScheduleByDateSlotAndRoom(daySlotService.findDaySlotByDateAndSlot(formattedDate, slots.get(0)), rooms.get(0)) == null) {
                                ScheduleEntity aScheduleEntity = scheduleService.findScheduleByDateSlotAndGroupName(daySlotEntity, groupNameCell.getStringCellValue());
                                if (aScheduleEntity == null && scheduleService.findScheduleByDateSlotAndLecture(daySlotEntity, employee) == null) {
                                    ScheduleEntity scheduleEntity = new ScheduleEntity();

                                    scheduleEntity.setCourseId(course);
                                    scheduleEntity.setDateId(daySlotEntity);
                                    scheduleEntity.setRoomId(rooms.get(0));
                                    scheduleEntity.setGroupName(groupNameCell.getStringCellValue());
                                    scheduleEntity.setActive(true);
                                    if (employee != null) {
                                        scheduleEntity.setEmpId(employee);

//                                        List<ScheduleEntity> teacherSchedule = new ArrayList<>();
//                                        if (employeesMap.get(employee) == null) {
//                                            employeesMap.put(employee, new ArrayList<ScheduleEntity>());
//                                        }
//                                        teacherSchedule = employeesMap.get(employee);
//                                        teacherSchedule = new ArrayList<>(teacherSchedule);
//
//                                        ScheduleEntity tmp = teacherSchedule.stream().filter(q -> q.getRoomId().getId() == scheduleEntity.getRoomId().getId()
//                                                && q.getDateId().getId() == scheduleEntity.getDateId().getId()).findFirst().orElse(null);
//
//                                        if (tmp == null) {
//                                            teacherSchedule.add(scheduleEntity);
//                                        }
//
//                                        employeesMap.put(employee, teacherSchedule);
                                    }

                                    scheduleEntities.add(scheduleEntity);
                                    scheduleService.createSchedule(scheduleEntity);

//                                    List<CourseStudentEntity> courseStudentEntityList = courseStudentService.findCourseStudentByGroupNameAndCourse(groupNameCell.getStringCellValue(), course);
//                                    if (courseStudentEntityList != null) {
//                                        for (CourseStudentEntity courseStudentEntity : courseStudentEntityList) {
//                                            List<ScheduleEntity> studentSchedule = new ArrayList<>();
//                                            StudentEntity aStudent = courseStudentEntity.getStudentId();
//                                            if (studentsMap.get(aStudent) == null) {
//                                                studentsMap.put(aStudent, new ArrayList<ScheduleEntity>());
//                                            }
//                                            studentSchedule = studentsMap.get(aStudent);
//                                            studentSchedule = new ArrayList<>(studentSchedule);
//
//                                            ScheduleEntity tmp = studentSchedule.stream().filter(q -> q.getRoomId().getId() == scheduleEntity.getRoomId().getId()
//                                                    && q.getDateId().getId() == scheduleEntity.getDateId().getId()).findFirst().orElse(null);
//
//                                            if (tmp == null) {
//                                                studentSchedule.add(scheduleEntity);
//                                            }
//
//                                            studentsMap.put(aStudent, studentSchedule);
//                                        }
//                                    }
                                } else {
                                    //update schedule
                                    if (employee != null && aScheduleEntity != null) {
                                        if (aScheduleEntity.getRoomId().getId() != rooms.get(0).getId() || aScheduleEntity.getEmpId().getId() != employee.getId()) {
                                            aScheduleEntity.setEmpId(employee);

                                            aScheduleEntity.setRoomId(rooms.get(0));
                                            scheduleService.updateSchedule(aScheduleEntity);

//                                            List<ScheduleEntity> teacherSchedule = new ArrayList<>();
//                                            if (employeesMap.get(employee) == null) {
//                                                employeesMap.put(employee, new ArrayList<ScheduleEntity>());
//                                            }
//                                            teacherSchedule = employeesMap.get(employee);
//                                            teacherSchedule = new ArrayList<>(teacherSchedule);
//
//                                            ScheduleEntity tmp = teacherSchedule.stream().filter(q -> q.getRoomId().getId() == aScheduleEntity.getRoomId().getId()
//                                                    && q.getDateId().getId() == aScheduleEntity.getDateId().getId()).findFirst().orElse(null);
//
//                                            if (tmp == null) {
//                                                teacherSchedule.add(aScheduleEntity);
//                                            }
//                                            employeesMap.put(employee, teacherSchedule);
//
//
//                                            List<CourseStudentEntity> courseStudentEntityList = courseStudentService.findCourseStudentByGroupNameAndCourse(groupNameCell.getStringCellValue(), course);
//                                            if (courseStudentEntityList != null) {
//                                                for (CourseStudentEntity courseStudentEntity : courseStudentEntityList) {
//                                                    List<ScheduleEntity> studentSchedule = new ArrayList<>();
//                                                    StudentEntity aStudent = courseStudentEntity.getStudentId();
//                                                    if (studentsMap.get(aStudent) == null) {
//                                                        studentsMap.put(aStudent, new ArrayList<ScheduleEntity>());
//                                                    }
//                                                    studentSchedule = studentsMap.get(aStudent);
//                                                    studentSchedule = new ArrayList<>(studentSchedule);
//
//                                                    ScheduleEntity tmp2 = studentSchedule.stream().filter(q -> q.getRoomId().getId() == aScheduleEntity.getRoomId().getId()
//                                                            && q.getDateId().getId() == aScheduleEntity.getDateId().getId()).findFirst().orElse(null);
//
//                                                    if (tmp2 == null) {
//                                                        studentSchedule.add(aScheduleEntity);
//                                                    }
//
//                                                    studentsMap.put(aStudent, studentSchedule);
//                                                }
//                                            }
                                        }
                                    }

                                }
                            } else {
                                System.out.println("");
                            }
                        } else {
                            System.out.println("");
                        }
                    } else {
                        System.out.println("");
                    }
                    this.currentLine++;
                }
            }

//            scheduleService.createScheduleList(scheduleEntities);
            this.isExcelRunning = false;


//            String msg = "Your schedule has been changed. Click here to check update";
//
//            for (EmployeeEntity key : employeesMap.keySet()) {
//                sendNotification(msg, key.getEmailEDU().substring(0, key.getEmailEDU().indexOf("@")), employeesMap.get(key), androidPushNotificationsService,"edit");
//            }
//
//            for (StudentEntity key : studentsMap.keySet()) {
//                sendNotification(msg, key.getEmail().substring(0, key.getEmail().indexOf("@")), studentsMap.get(key), androidPushNotificationsService, "edit");
//            }

            jsonObject.addProperty("success", true);
            jsonObject.addProperty("message", "Import lịch học thành công !");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.writeLog(ex);
            jsonObject.addProperty("fail", false);
            jsonObject.addProperty("message", ex.getMessage());
        }
        return jsonObject;
    }


    @RequestMapping("/getExcelCurrentLineStatus")
    @ResponseBody
    public JsonObject getExcelCurrentLineStatus() {
        JsonObject obj = new JsonObject();
        obj.addProperty("excelCurrent", this.currentLine);
        obj.addProperty("excelTotal", this.totalLine);
        obj.addProperty("isExcelRunning", isExcelRunning());
        return obj;
    }


    @RequestMapping("/getLineScheduleStatus")
    @ResponseBody
    public JsonObject getLineScheduleStatus() {
        JsonObject obj = new JsonObject();
        obj.addProperty("current", scheduleService.getCurrentLine());
        obj.addProperty("total", scheduleService.getTotalLine());
        return obj;
    }


    @RequestMapping(value = "/uploadCourseStudents", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject importCourseStudents(@RequestParam("file") MultipartFile file, @RequestParam("semesterId") String semesterIdStr) {
        JsonObject jsonObject = new JsonObject();
        List<CourseStudentEntity> courseStudentEntities = new ArrayList<CourseStudentEntity>();
        StudentEntity student = null;
        CourseEntity course = null;

        Ultilities.logUserAction("Import course student");
//        Set<EmployeeEntity> employees = new HashSet<>();

        try {
            InputStream is = file.getInputStream();

            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);

            XSSFRow row;
            int excelDataIndex = 1;
            int lastRow = spreadsheet.getLastRowNum();
            this.totalLine = lastRow - startRowNumber + 1;

            int groupNameIndex = 2;
            int rollNumberIndex = 0;
            int courseIndex = 1;

            this.currentLine = 0;
            for (int rowIndex = excelDataIndex; rowIndex <= lastRow; rowIndex++) {
                row = spreadsheet.getRow(rowIndex);
                if (row != null) {

                    Cell groupNameCell = row.getCell(groupNameIndex);
                    Cell courseCell = row.getCell(courseIndex);
                    Cell rollNumberCell = row.getCell(rollNumberIndex);

                    if (rollNumberCell != null && !rollNumberCell.toString().equals("") && courseCell != null && !courseCell.toString().equals("")) {

                        String rollNumber = rollNumberCell.getStringCellValue().trim();
                        String courseName = courseCell.getStringCellValue().trim();

                        student = studentService.findStudentByRollNumber(rollNumber);

                        Integer semesterId = Integer.parseInt(semesterIdStr.trim());
                        RealSemesterEntity realSemesterEntity = realSemesterService.findSemesterById(semesterId);
                        course = courseService.findCourseBySemesterAndSubjectCode(realSemesterEntity.getSemester(), courseName);

                        if (course != null && student != null) {
                            if (courseStudentService.findCourseStudentByCourseAndStudent(course, student) == null) {
                                CourseStudentEntity courseStudentEntity = new CourseStudentEntity();
                                courseStudentEntity.setCourseId(course);
                                courseStudentEntity.setStudentId(student);
                                courseStudentEntity.setGroupName(groupNameCell.getStringCellValue());
                                courseStudentEntities.add(courseStudentEntity);
                            } else {
                                System.out.println(currentLine);
                            }
                        } else {
                            System.out.println(currentLine);
                        }

                    } else {
                        System.out.println(currentLine);
                    }
                    this.currentLine++;
                } else {
                    System.out.println(currentLine);
                }

            }
            courseStudentService.createCourseStudentList(courseStudentEntities);

            jsonObject.addProperty("success", true);
            jsonObject.addProperty("message", "Import lịch học thành công !");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.writeLog(ex);
            jsonObject.addProperty("fail", false);
            jsonObject.addProperty("message", ex.getMessage());
        }


        return jsonObject;
    }

    //cập nhật điểm cho sinh viên sau khi đã có điểm Final
    @RequestMapping(value = "/updateMarkForStudyingStudent", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject updateMarkForStudyingStudent(@RequestParam("updateFile") MultipartFile file, @RequestParam("semesterId") String semesterIdStr) {
        Ultilities.logUserAction("Update mark for studying student");
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

                    if (rollNumberCell.getStringCellValue().trim().equalsIgnoreCase("SE62824") || currentLine == 11443) {
                        System.out.println("hehe");
                    }
                    if ((semesterNameCell.getStringCellValue() == null)) {
                        break;
                    }

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
                                for (int i = 0; i < foundMarks.size(); i++) {
                                    MarksEntity foundMark = foundMarks.get(i);
                                    if (foundMark.getStatus().equalsIgnoreCase("Studying")) {
                                        foundMark.setAverageMark(averageMarkValue);
                                        foundMark.setStatus(statusValue);
                                        marksService.updateMark(foundMark);
                                        break;
                                    }
                                }

                            }
                        }
                    }
                }
                this.currentLine++;
            }
            jsonObject.addProperty("success", true);
            jsonObject.addProperty("message", "Cập nhật điểm sinh viên đang học thành công !");

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.writeLog(ex);
            jsonObject.addProperty("fail", false);
            jsonObject.addProperty("message", ex.getMessage());
        }

        return jsonObject;
    }

    @RequestMapping(value = "/updateStudentCredits", method = RequestMethod.POST)
    @ResponseBody
    public Callable<JsonObject> UpdateStudentCredits() {
        currentLine1 = 0;
        totalLine1 = 0;
        Ultilities.logUserAction("Update student credits");

        Callable<JsonObject> callable = () -> {
            JsonObject result = new JsonObject();

            try {
                List<StudentEntity> studentList = studentService.findAllStudents();
                totalLine1 = studentList.size();

                boolean demo = false;
                if (demo) {
                    studentList = studentList.stream().filter(c -> c.getRollNumber().equals("SE61073")).collect(Collectors.toList());
                }

                for (StudentEntity student : studentList) {
                    // Object[]: SubjectId, SubjectCredits, Mark, MarkStatus
//                    List<Object[]> markList = marksService.getLatestPassFailMarksAndCredits(student.getId());

                    int totalPassCredits = 0;
                    int totalPassFailCredits = 0;

                    double sumPassFailMark = 0;
                    double sumPassFailCredits = 0;

                    List<MarksEntity> marks = marksService.getStudentMarksById(student.getId());
                    marks = marks.stream().filter(c -> c.getIsActivated()).collect(Collectors.toList());
                    marks = Ultilities.SortSemestersByMarks(marks);

                    List<SubjectCurriculumEntity> stuSubs = Ultilities.StudentCurriculumSubjects(student);

//                    List<String> dacongList = new ArrayList<>();

                    for (SubjectCurriculumEntity sub : stuSubs) {
                        if (!sub.getSubjectId().getId().toLowerCase().contains("vov")) {

                            List<MarksEntity> subMarks = marks
                                    .stream()
                                    .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(sub.getSubjectId().getId()))
                                    .collect(Collectors.toList());
                            if (subMarks.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") ||
                                    c.getStatus().toLowerCase().contains("exempt"))) {

//                                if (!dacongList.stream().anyMatch(c -> c.equals(sub.getSubjectId().getId()))) {
//                                    totalPassCredits += sub.getSubjectCredits();
//                                    dacongList.add(sub.getSubjectId().getId());
//
//                                    System.out.println(totalPassCredits + " - " + sub.getSubjectId().getId() + " - " + sub.getSubjectCredits());
//                                }
                                totalPassCredits += sub.getSubjectCredits();

                            } else {
                                boolean dacong = false;
                                List<SubjectEntity> replacers = sub.getSubjectId().getSubjectEntityList();

                                for (SubjectEntity replacer : replacers) {
                                    List<MarksEntity> replaceMarks = marks
                                            .stream()
                                            .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(replacer.getId()))
                                            .collect(Collectors.toList());
                                    if (replaceMarks.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") ||
                                            c.getStatus().toLowerCase().contains("exempt"))) {

//                                        if (!dacongList.stream().anyMatch(c -> c.equals(replacer.getId()))) {
//                                            totalPassCredits += sub.getSubjectCredits();
//                                            dacongList.add(replacer.getId());
//
//                                            System.out.println(totalPassCredits + " - " + sub.getSubjectId().getId() + " - " + replacer.getId() + " - " + sub.getSubjectCredits());
//                                        }

                                        totalPassCredits += sub.getSubjectCredits();
                                        dacong = true;
                                        break;
                                    }
                                }
                                if (!dacong) {
                                    List<SubjectEntity> replacersFirst = sub.getSubjectId().getSubjectEntityList1();
                                    for (SubjectEntity repls : replacersFirst) {
                                        List<MarksEntity> replaceMarks = marks
                                                .stream()
                                                .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(repls.getId()))
                                                .collect(Collectors.toList());
                                        if (replaceMarks.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") ||
                                                c.getStatus().toLowerCase().contains("exempt"))) {

//                                            if (!dacongList.stream().anyMatch(c -> c.equals(repls.getId()))) {
//                                                totalPassCredits += sub.getSubjectCredits();
//                                                dacongList.add(repls.getId());
//
//                                                System.out.println(totalPassCredits + " - " + sub.getSubjectId().getId() + " - " + repls.getId() + " - " + sub.getSubjectCredits());
//                                            }
                                            totalPassCredits += sub.getSubjectCredits();

                                            break;
                                        } else {
                                            List<SubjectEntity> reps = repls.getSubjectEntityList();
                                            for (SubjectEntity replacer : reps) {
                                                List<MarksEntity> replaceMarks2 = marks
                                                        .stream()
                                                        .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(replacer.getId()))
                                                        .collect(Collectors.toList());
                                                if (replaceMarks2.stream().anyMatch(c -> c.getStatus().toLowerCase().contains("pass") ||
                                                        c.getStatus().toLowerCase().contains("exempt"))) {

//                                                    if (!dacongList.stream().anyMatch(c -> c.equals(replacer.getId()))) {
//                                                        totalPassCredits += sub.getSubjectCredits();
//                                                        dacongList.add(replacer.getId());
//
//                                                        System.out.println(totalPassCredits + " - " + sub.getSubjectId().getId() + " - " + replacer.getId() + " - " + sub.getSubjectCredits());
//                                                    }

                                                    totalPassCredits += sub.getSubjectCredits();

                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            List<MarksEntity> m = marks
                                    .stream()
                                    .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(sub.getSubjectId().getId()))
                                    .collect(Collectors.toList());
                            if (!m.isEmpty()) {
                                totalPassFailCredits += sub.getSubjectCredits();
                            }
                        }

                        // tính dtb
                        MarksEntity latestEntry = marks
                                .stream()
                                .filter(c -> !c.getSubjectMarkComponentId().getSubjectId().getId().toLowerCase().contains("lab") &&
                                        !c.getSubjectMarkComponentId().getSubjectId().getId().toLowerCase().contains("oj") &&
                                        !c.getSubjectMarkComponentId().getSubjectId().getId().toLowerCase().contains("syb"))
                                .filter(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(sub.getSubjectId().getId()))
                                .reduce((first, second) -> second)
                                .orElse(null);
                        if (latestEntry != null) {
                            sumPassFailMark += latestEntry.getAverageMark() * sub.getSubjectCredits();
                            sumPassFailCredits += sub.getSubjectCredits();
                        }
                    }

//
//                    for (Object[] m : markList) {
//                        String subjectCode = m[0].toString();
//                        int subjectCredits = (int) m[1];
//                        double mark = (double) m[2];
//                        String status = m[3].toString();
//
//                        if (!Ultilities.containsIgnoreCase(subjectCode, "VOV")) {
//                            if (!status.equals(Enums.MarkStatus.FAIL.getValue())) {
//                                totalPassCredits += subjectCredits;
//                            }
//                            totalPassFailCredits += subjectCredits;
//                        }
//
//                        if (!Ultilities.containsIgnoreCase(subjectCode, "LAB")
//                                && !Ultilities.containsIgnoreCase(subjectCode, "OJT")
//                                && !Ultilities.containsIgnoreCase(subjectCode, "SYB")) {
//                            sumPassFailMark += mark * subjectCredits;
//                            sumPassFailCredits += subjectCredits;
//                        }
//                    }

                    double passFailAverageMark = Math.round(sumPassFailMark / sumPassFailCredits * 100.0) / 100.0;

                    student.setPassCredits(totalPassCredits);
                    student.setPassFailCredits(totalPassFailCredits);
                    student.setPassFailAverageMark(passFailAverageMark);

                    studentService.updateStudent(student);
                    ++currentLine1;
                }

                result.addProperty("success", true);
            } catch (Exception e) {
                e.printStackTrace();
                result.addProperty("success", false);
                result.addProperty("message", e.getMessage());
            }

            return result;
        };
        return callable;
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
        Ultilities.logUserAction("Upload updated marks (use to update average marks)");
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
    @RequestMapping(value = "/convertToStudentQuantityPage")
    public ModelAndView convertToStudentQuantityPage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to " + request.getRequestURI());
        ModelAndView mav = new ModelAndView("Convert2StudentQuantityByClassAndSubject");
        mav.addObject("title", "Số lượng sinh viên theo lớp môn");


        return mav;
    }

    @RequestMapping(value = "/convertToStudentQuantity", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject goConvert2StudentQuantityByClassAndSubject(@RequestParam("file") MultipartFile file,
                                                                 HttpServletRequest request, HttpServletResponse response) {
        JsonObject jsonObject = new JsonObject();

        Ultilities.logUserAction("Convert student quantity (use for statistic)");

        try {
            InputStream is = file.getInputStream();

            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);

            XSSFRow row;
            int excelDataIndex = 1;
            int lastRow = spreadsheet.getLastRowNum();
            this.totalLine = lastRow - startRowNumber + 1;


            int rollNumberIndex = -1;
            int subjectCodeIndex = -1;
            int classNameIndex = -1;

            //dynamic search and assign index column
            for (Row r : spreadsheet) {
                for (Cell cell : r) {
                    if (cell.getCellTypeEnum() == CellType.STRING) {
                        String cellValue = cell.getStringCellValue().trim();
                        switch (cellValue) {
                            case "RollNumber":
                                rollNumberIndex = cell.getColumnIndex();
                                break;
                            case "SubjectCode":
                                subjectCodeIndex = cell.getColumnIndex();
                                break;
                            case "GroupName":
                                classNameIndex = cell.getColumnIndex();
                                break;
                        }

                    }
                }
            }
            if (rollNumberIndex != -1 && subjectCodeIndex != -1 && classNameIndex != -1) {

                //classList: contains Map<SubjectCode, Map<ClassName, StudentQuantity>>
                Map<String, Map<String, Integer>> combineList = new HashMap<>();

                //use for sorting, !!!!not need right now!!!!!
//                List<String> subjectCodeList = new ArrayList<>();


                this.currentLine = 1;
                String markComponentName = Enums.MarkComponent.AVERAGE.getValue();
                for (int rowIndex = excelDataIndex; rowIndex <= lastRow; rowIndex++) {
                    row = spreadsheet.getRow(rowIndex);

                    Cell rollNumberCell = row.getCell(rollNumberIndex);
                    Cell classNameCell = row.getCell(classNameIndex);
                    Cell subjectCodeCell = row.getCell(subjectCodeIndex);

                    //check if cell is empty or null to end the loop
                    if (rollNumberCell == null || rollNumberCell.getCellTypeEnum() == CellType.BLANK
                            || classNameCell == null || classNameCell.getCellTypeEnum() == CellType.BLANK
                            || subjectCodeCell == null || subjectCodeCell.getCellTypeEnum() == CellType.BLANK) {
                        break;
                    } else {
                        String rollNumberValue = rollNumberCell.getStringCellValue();
                        String classNameValue = classNameCell.getStringCellValue();
                        String subjectCodeValue = subjectCodeCell.getStringCellValue();

                        Map<String, Integer> classesInSubject = combineList.get(subjectCodeValue);
                        if (classesInSubject == null) {
                            combineList.put(subjectCodeValue, new HashMap<String, Integer>());
                        } else {
                            Integer studentQuantity = classesInSubject.get(classNameValue);
                            if (studentQuantity == null) {
                                //if not found any class, add class with initiate student quantity =1
                                classesInSubject.put(classNameValue, 1);
                            } else {
                                //if class is founded, increase number of student by 1
                                ++studentQuantity;
                                classesInSubject.put(classNameValue, studentQuantity);
                            }
                        }
                    }
                    System.out.println("convert" + currentLine);
                    this.currentLine++;
                }
                HttpSession session = request.getSession();
                session.setAttribute("studentQuantityConverList", combineList);


                jsonObject.addProperty("success", true);
                jsonObject.addProperty("message", "Convert thành công !");
//                jsonObject.addProperty("downloadPath", fileName);
            } else {
                jsonObject.addProperty("success", false);
                jsonObject.addProperty("message", "File không đúng định dạng !");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.writeLog(ex);
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", ex.getMessage());
        }


        return jsonObject;
    }


    @RequestMapping(value = "/convertToStudentQuantityData")
    @ResponseBody
    public JsonObject StudentQuantityPerClass(@RequestParam Map<String, String> params, HttpServletRequest request) {
        JsonObject jsonObject = new JsonObject();

        ModelAndView mav = new ModelAndView("Convert2StudentQuantityByClassAndSubject");
        mav.addObject("title", "Số lượng sinh viên theo lớp môn");
        try {
            HttpSession session = request.getSession();
            Map<String, Map<String, Integer>> combineList = (Map<String, Map<String, Integer>>)
                    session.getAttribute("studentQuantityConverList");


            List<List<String>> result = new ArrayList<>();
            for (Map.Entry<String, Map<String, Integer>> combine : combineList.entrySet()) {


                Map<String, Integer> classesAndStudentsQuantityList = combine.getValue();

                //Map<ClassName, StudentQuantity>
                for (Map.Entry<String, Integer> item :
                        classesAndStudentsQuantityList.entrySet()) {
                    List<String> classInfo = new ArrayList<>();

                    //Subject name
                    classInfo.add(combine.getKey());
                    //Class name
                    classInfo.add(item.getKey());
                    //Student Quantity
                    classInfo.add(item.getValue() + "");

                    result.add(classInfo);
                }

            }

            JsonArray aaData = (JsonArray) new Gson().toJsonTree(result);

            jsonObject.addProperty("iTotalRecords", result.size());
            jsonObject.addProperty("iTotalDisplayRecords", result.size());
            jsonObject.add("aaData", aaData);
            jsonObject.addProperty("sEcho", params.get("sEcho"));


        } catch (Exception e) {
            System.out.println(e.getMessage());
            Logger.writeLog(e);
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", e.getMessage());
        }

        return jsonObject;
    }


    //trang này chưa xong
    @RequestMapping(value = "/importStudentMarksFromAnotherAcademicPage")
    public ModelAndView ImportStudentMarksFromAnotherAcademicPage(HttpServletRequest request) {
        if (!Ultilities.checkUserAuthorize(request)) {
            return Ultilities.returnDeniedPage();
        }
        //logging user action
        Ultilities.logUserAction("go to /importStudentMarksFromAnotherAcademicPage");

        ModelAndView mav = new ModelAndView("ImportStudentMarksFromAnotherAcademic");
        mav.addObject("title", "Nhập điểm cho một sinh viên");


        return mav;
    }

    //hàm này chưa xong, template hiện tại mà hàm này đọc không phải template đúng (hãy request template đúng và làm lại)
    @RequestMapping(value = "/importStudentMarksFromAnotherAcademic", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject goImportStudentMarksFromAnotherAcademic(@RequestParam("file") MultipartFile file,
                                                              HttpServletRequest request, HttpServletResponse response) {
        JsonObject jsonObject = new JsonObject();

        try {
            InputStream is = file.getInputStream();

            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);

            XSSFRow row;

            int lastRow = spreadsheet.getLastRowNum();
            this.totalLine = lastRow - startRowNumber + 1;

            int excelDataIndex = -1;

            int rollNumberColumnIndex = -1;
            int rollNumberIndexRowIndex = -1;
            int subjectNameIndex = -1;
            int creditIndex = -1;
            int markIndex = -1;
            int termIndex = -1;

            //dynamic search and assign index column
            for (Row r : spreadsheet) {
                for (Cell cell : r) {
                    if (cell.getCellTypeEnum() == CellType.STRING) {
                        String cellValue = cell.getStringCellValue().trim();
                        switch (cellValue) {
                            case "MSSV:":
                                rollNumberColumnIndex = cell.getColumnIndex() + 1;
                                rollNumberIndexRowIndex = cell.getRowIndex();
                                break;
                            case "Subject":
                                subjectNameIndex = cell.getColumnIndex();
                                break;
                            case "Tín chỉ":
                                creditIndex = cell.getColumnIndex();
                                // excel data
                                excelDataIndex = cell.getRowIndex() + 1;
                                break;
                            case "Điểm":
                                markIndex = cell.getColumnIndex();
                                break;
                            case "Học kỳ":
                                termIndex = cell.getColumnIndex();
                                break;
                        }

                    }
                }
            }
            List<SubjectEntity> allSubjects = subjectService.getAllSubjects();

            List<MarksEntity> marksList = new ArrayList<>();
            List<RealSemesterEntity> semestersList = Global.getSortedList();

            if (rollNumberColumnIndex != -1 && subjectNameIndex != -1 && creditIndex != -1
                    && markIndex != -1 && termIndex != -1) {


                //get student and check if student exists
                row = spreadsheet.getRow(rollNumberIndexRowIndex);
                Cell rollNumberCell = row.getCell(rollNumberColumnIndex);
                String rollNumberValue = rollNumberCell.getStringCellValue();

                StudentEntity student = studentService.findStudentByRollNumber(rollNumberValue);
                if (student == null) {
                    jsonObject.addProperty("success", false);
                    jsonObject.addProperty("message", "Không tìm thấy học sinh !");
                    return jsonObject;
                }

//                List<DocumentStudentEntity> documentStudentList = new ArrayList<>(student.getDocumentStudentEntityList());
                List<StudentStatusEntity> studentStatusList = new ArrayList<>(student.getStudentStatusEntityList());

                List<SubjectCurriculumEntity> subjectCurriculumList =
                        subjectCurriculumService.getSubjectCurriculumByStudent(student.getId());

                this.currentLine = 1;

                //get mark component name for later use
                String markComponentName = Enums.MarkComponent.AVERAGE.getValue();

                for (int rowIndex = excelDataIndex; rowIndex <= lastRow; rowIndex++) {
                    row = spreadsheet.getRow(rowIndex);


                    Cell subjectNameCell = row.getCell(subjectNameIndex);
                    Cell creditCell = row.getCell(creditIndex);
                    Cell markCell = row.getCell(markIndex);
                    Cell termCell = row.getCell(termIndex);

                    //check if cell is empty or null to end the loop
                    if (rollNumberCell == null || rollNumberCell.getCellTypeEnum() == CellType.BLANK
                            || subjectNameCell == null || subjectNameCell.getCellTypeEnum() == CellType.BLANK
                            || creditCell == null || creditCell.getCellTypeEnum() == CellType.BLANK
                            || markCell == null || markCell.getCellTypeEnum() == CellType.BLANK
                            || termCell == null || termCell.getCellTypeEnum() == CellType.BLANK) {
                        break;
                    } else {

                        String subjectNameValue = subjectNameCell.getStringCellValue();
                        String creditValue = creditCell.getStringCellValue();
                        String markValue = markCell.getStringCellValue();
                        String termValue = termCell.getStringCellValue();

                        //get semester , return null if not find any
                        RealSemesterEntity semester = semestersList
                                .stream().filter(q -> q.getSemester().equalsIgnoreCase(termValue))
                                .findFirst().orElse(null);

                        //check student status có hay chưa, chưa có thì tạo
                        StudentStatusEntity studentStatus = studentStatusList.stream()
                                .filter(q -> q.getSemesterId().getSemester().equalsIgnoreCase(termValue))
                                .findFirst().orElse(null);


                        if (studentStatus == null) {
                            StudentStatusEntity newStudentStatus = new StudentStatusEntity();
                            newStudentStatus.setStudentId(student);
                            newStudentStatus.setSemesterId(semester);

                            //set status là học đi
                            newStudentStatus.setStatus("HD");

                            //chưa có dữ liệu để xét term
//                         newStudentStatus.setTerm();
                            studentStatusService.createStudentStatus(newStudentStatus);
                            studentStatus = newStudentStatus;
                        }

                        //set passed or Failed
                        Double avgMark = null;
                        try {
                            avgMark = Double.parseDouble(markValue);

                        } catch (NumberFormatException ex) {
                            System.out.println(ex.getMessage());
                        }
                        String markStatus = null;
                        if (avgMark >= 5.0) {
                            markStatus = Enums.MarkStatus.PASSED.getValue();
                        } else {
                            markStatus = Enums.MarkStatus.FAIL.getValue();
                        }

                        //get Subject mark component
                        SubjectEntity subject = subjectCurriculumList.stream()
                                .filter(q -> q.getSubjectId().getName().equalsIgnoreCase(subjectNameValue))
                                .map(SubjectCurriculumEntity::getSubjectId)
                                .findFirst().orElse(null);

                        //check replacement subject
                        if (subject == null) {
                            outerloop:
                            for (SubjectCurriculumEntity sc : subjectCurriculumList
                                    ) {
                                SubjectEntity s = sc.getSubjectId();
                                List<SubjectEntity> replacedSubjects = s.getSubjectEntityList();
                                for (SubjectEntity reSubject : replacedSubjects
                                        ) {
                                    reSubject.getName().equalsIgnoreCase(subjectNameValue);
                                    subject = reSubject;
                                    break outerloop;
                                }
                            }
                        }
                        String componentName = subject.getId() + "_" + markComponentName;

                        SubjectMarkComponentEntity subjectMarkComponent = subjectMarkComponentService
                                .findSubjectMarkComponentByNameAndSubjectCd(componentName, subject.getId());


                        //check if data is enough to create mark
                        if (subjectMarkComponent == null && avgMark == null
                                && semester == null) {
                            jsonObject.addProperty("success", false);
                            jsonObject.addProperty("message", "Xảy ra lỗi, ko tìm thấy môn," +
                                    " kì học hoặc điểm ko đúng định dạng");
                            return jsonObject;

                        }

                        //create marks entity
                        MarksEntity mark = new MarksEntity();
                        mark.setAverageMark(avgMark);   // mark
                        mark.setEnabled(true);          //enabled
                        mark.setIsActivated(true);      // active
                        mark.setStatus(markStatus);    //status
                        mark.setSemesterId(semester);  // semester
                        mark.setStudentId(student);     // student

                        mark.setSubjectMarkComponentId(subjectMarkComponent); //markcomponent
                        //học sinh chuyển từ cơ sở khác về thì không có course do trường quản lý
                        mark.setCourseId(null);

                        //add mark to list marks
//                        marksList.add(mark);

                    }
                    System.out.println("import" + currentLine);
                    this.currentLine++;
                }
                if (marksList.isEmpty()) {
                    jsonObject.addProperty("success", false);
                    jsonObject.addProperty("message", "Không phát hiện điểm mới để import !");
                    return jsonObject;
                }
//                marksService.createMarks(marksList);

                jsonObject.addProperty("success", true);
                jsonObject.addProperty("message", "Import điểm cho 1 sinh viên thành công !");
            } else {
                jsonObject.addProperty("success", false);
                jsonObject.addProperty("message", "File không đúng định dạng !");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.writeLog(ex);
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", ex.getMessage());
        }


        return jsonObject;
    }

    @RequestMapping(value = "/uploadThesisName", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject goUploadThesisName(@RequestParam("file") MultipartFile file,
                                         HttpServletRequest request, HttpServletResponse response) {
        JsonObject jsonObject = new JsonObject();
        Ultilities.logUserAction("Upload thesis name");
        try {
            InputStream is = file.getInputStream();

            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);

            XSSFRow row;

            int lastRow = spreadsheet.getLastRowNum();
            this.totalLine = lastRow - startRowNumber + 1;

            int excelDataIndex = -1;

            int rollNumberColIndex = -1;
            int vietnameseNameColIndex = -1;
            int englishNameColIndex = -1;


            //dynamic search and assign index column
            for (Row r : spreadsheet) {
                for (Cell cell : r) {
                    if (cell.getCellTypeEnum() == CellType.STRING) {
                        String cellValue = cell.getStringCellValue().trim();
                        switch (cellValue.toUpperCase()) {
                            case "MSSV":
                                rollNumberColIndex = cell.getColumnIndex();
                                excelDataIndex = cell.getRowIndex() + 1;
                                break;
                            case "TÊN LVTN":
                                vietnameseNameColIndex = cell.getColumnIndex();
                                break;
                            case "G. THESIS":
                                englishNameColIndex = cell.getColumnIndex();
                                // excel data
                                break;
                        }

                    }
                }
            }


            this.startRowNumber = excelDataIndex;
            if (rollNumberColIndex != -1 && vietnameseNameColIndex != -1 && englishNameColIndex != -1) {


                //get student and check if student exists
                row = spreadsheet.getRow(excelDataIndex);

                this.currentLine = 1;

                //get mark component name for later use
                String markComponentName = Enums.MarkComponent.AVERAGE.getValue();
                HashMap<String, List<String>> thesisName = new HashMap<>();
                for (int rowIndex = excelDataIndex; rowIndex <= lastRow; rowIndex++) {
                    row = spreadsheet.getRow(rowIndex);


                    Cell rollNumberCell = row.getCell(rollNumberColIndex);
                    Cell vietnameseNameCell = row.getCell(vietnameseNameColIndex);
                    Cell englishNameCell = row.getCell(englishNameColIndex);

                    //check if cell is empty or null to end the loop
                    if (rollNumberCell == null || rollNumberCell.getCellTypeEnum() == CellType.BLANK
                            || vietnameseNameCell == null || vietnameseNameCell.getCellTypeEnum() == CellType.BLANK
                            || englishNameCell == null || englishNameCell.getCellTypeEnum() == CellType.BLANK
                            ) {
//                        break;
                    } else {

                        String rollNumberValue = rollNumberCell.getStringCellValue().trim().toUpperCase();
                        String vietnameseNameValue = vietnameseNameCell.getStringCellValue().trim().toUpperCase();
                        String englishNameValue = englishNameCell.getStringCellValue().trim().toUpperCase();

                        //mảng gồm 2 item [0]: tên tiếng việt, [1]: tên tiếng anh
                        List<String> nameList = new ArrayList<>();
                        nameList.add(vietnameseNameValue);
                        nameList.add(englishNameValue);
                        thesisName.put(rollNumberValue, nameList);
                    }
                    System.out.println("upload" + currentLine);
                    this.currentLine++;
                }
                request.getSession().setAttribute(Enums.GraduateVariable.ThesisName_List.getValue(), thesisName);

                jsonObject.addProperty("success", true);
                jsonObject.addProperty("message", "Upload tên đề tài thành công !");
            } else {
                jsonObject.addProperty("success", false);
                jsonObject.addProperty("message", "File không đúng định dạng !");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.writeLog(ex);
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", ex.getMessage());
        }

        return jsonObject;
    }


    @RequestMapping(value = "/uploadRequiedDocuments", method = RequestMethod.POST)
    @ResponseBody
    public JsonObject goUploadRequiedDocuments(@RequestParam("file") MultipartFile file,
                                               HttpServletRequest request, HttpServletResponse response) {
        JsonObject jsonObject = new JsonObject();
        Ultilities.logUserAction("Upload student graduate requiredDocuments");
        try {
            InputStream is = file.getInputStream();

            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet spreadsheet = workbook.getSheetAt(0);

            XSSFRow row;

            int lastRow = spreadsheet.getLastRowNum();
            this.totalLine = lastRow - startRowNumber + 1;

            int excelDataIndex = 4;

            int rollNumberColIndex = 0;
            int highschoolGraduateIndex = 2;
            int idCardIndex = 3;
            int birthRecordsIndex = 4;
            this.startRowNumber = excelDataIndex;


            Row dueDateRow = spreadsheet.getRow(1);
            Row graduateTimeRow = spreadsheet.getRow(2);

            String dueDate = dueDateRow.getCell(1).getStringCellValue().trim();
            String graduateTime = graduateTimeRow.getCell(1).getStringCellValue().trim();

            HashMap<String, RequiredDocuments> requiredDocuments = new HashMap<>();
            int countStop = 0;
            this.currentLine = 1;

            for (int rowIndex = excelDataIndex; rowIndex <= lastRow; rowIndex++) {
                row = spreadsheet.getRow(rowIndex);


                Cell rollNumberCell = row.getCell(rollNumberColIndex);
                Cell highschoolGraduateCell = row.getCell(highschoolGraduateIndex);
                Cell idCardCell = row.getCell(idCardIndex);
                Cell birthRecordsCell = row.getCell(birthRecordsIndex);

                //check if cell is empty or null to end the loop
                if (rollNumberCell == null || rollNumberCell.getCellTypeEnum() == CellType.BLANK
                        || highschoolGraduateCell == null || highschoolGraduateCell.getCellTypeEnum() == CellType.BLANK
                        || idCardCell == null || idCardCell.getCellTypeEnum() == CellType.BLANK
                        || birthRecordsCell == null || birthRecordsCell.getCellTypeEnum() == CellType.BLANK
                        ) {
                    //nếu có 2 dòng trống thì ngừng đọc, tránh trường hợp lastRow quá lớn mà k có data
                    if (countStop > 2) {
                        break;
                    }
                    countStop++;
                } else {

                    String rollNumberValue = rollNumberCell.getStringCellValue().trim().toUpperCase();
                    String highschoolGraduateValue = highschoolGraduateCell.getStringCellValue().trim().toUpperCase();
                    String idCardValue = idCardCell.getStringCellValue().trim().toUpperCase();
                    String birthRecordsValue = birthRecordsCell.getStringCellValue().trim().toUpperCase();


                    RequiredDocuments rd = new RequiredDocuments(false, false, false, dueDate, graduateTime);
                    if (highschoolGraduateValue.equalsIgnoreCase("ĐÃ NỘP")) {
                        rd.setHighschoolGraduate(true);
                    }
                    if (idCardValue.equalsIgnoreCase("ĐÃ NỘP")) {
                        rd.setIdCard(true);
                    }
                    if (birthRecordsValue.equalsIgnoreCase("ĐÃ NỘP")) {
                        rd.setBirthRecords(true);
                    }
                    requiredDocuments.put(rollNumberValue, rd);
                }
                System.out.println("upload" + currentLine);
                this.currentLine++;
            }
            request.getSession().setAttribute(Enums.GraduateVariable.Required_Documents.getValue(), requiredDocuments);

            jsonObject.addProperty("success", true);
            jsonObject.addProperty("message", "Upload tên thông tin thành công !");

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Logger.writeLog(ex);
            jsonObject.addProperty("success", false);
            jsonObject.addProperty("message", ex.getMessage());
        }

        return jsonObject;
    }
}

