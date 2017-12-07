package com.capstone.exporters;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.capstone.entities.*;
import com.capstone.enums.SubjectTypeEnum;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ExportPDFGraduatedStudentsImpl implements IExportObject {

    private String EXCEL_TEMPLATE = "template/DSSV-TN.xlsx";
    private IMarksService marksService = new MarksServiceImpl();
    private ISubjectService subjectService = new SubjectServiceImpl();
    private IRealSemesterService semesterService = new RealSemesterServiceImpl();
    private ISubjectCurriculumService subjectCurriculumService = new SubjectCurriculumServiceImpl();
    IStudentService studentService = new StudentServiceImpl();

    private String fileName = "Graduated-Students.pdf";

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void setFileName(String name) {
        fileName = name;
    }

    @Override
    public void writeData(OutputStream os, Map<String, String> params, HttpServletRequest request) throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(EXCEL_TEMPLATE);

        XSSFWorkbook workbook = new XSSFWorkbook(is);
        // close input stream
        is.close();
        XSSFSheet spreadsheet = workbook.getSheetAt(0);

        writeDataToTable(workbook, spreadsheet, params);

        String realPath = ExportPDFGraduatedStudentsImpl.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String excelPath = realPath.substring(0, realPath.indexOf("WEB-INF")) + "output.xlsx";
        OutputStream outputFile = new FileOutputStream(excelPath);
        workbook.write(outputFile);
        // close output stream
        try {
            Workbook asposeWorkbook = new Workbook(excelPath);
            // remove file
            File file = new File(excelPath);
            file.delete();
            file = null;
            asposeWorkbook.save(os, SaveFormat.PDF);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeDataToTable(XSSFWorkbook workbook, XSSFSheet sheet, Map<String, String> params) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        // style
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Map<StudentEntity, List<MarksEntity>> dataMap = processData(params);
        for (Map.Entry<StudentEntity, List<MarksEntity>> entry : dataMap.entrySet()) {
            sheet = workbook.cloneSheet(0, entry.getKey().getRollNumber());
            XSSFRow row = sheet.getRow(11);
            row.getCell(2).setCellValue(entry.getKey().getFullName());
            row.getCell(6).setCellValue(entry.getKey().getRollNumber());
            row = sheet.getRow(13);
            row.getCell(2).setCellValue(sdf.format(entry.getKey().getDateOfBirth()));
            row = sheet.getRow(15);
            row.getCell(2).setCellValue(entry.getKey().getProgramId().getFullName());
            DocumentStudentEntity documentStudentEntity = Ultilities.getStudentLatestDocument(entry.getKey());
            row.getCell(7).setCellValue(documentStudentEntity.getCurriculumId().getName());

            int ordinalNumber = 1;
            int rowIndex = 20;
            int markSize = entry.getValue().size();
            for (MarksEntity marksEntity : entry.getValue()) {
                row = sheet.createRow(rowIndex);
                // ordinal number
                XSSFCell ordinalNumberCell = row.createCell(0);
                ordinalNumberCell.setCellStyle(cellStyle);
                ordinalNumberCell.setCellValue("" + (markSize - ordinalNumber + 1));
                // Subject code
                SubjectEntity subjectEntity = subjectService.findSubjectById(marksEntity.getSubjectMarkComponentId().getSubjectId().getId());
                XSSFCell subjectCodeCell = row.createCell(1);
                subjectCodeCell.setCellValue(subjectEntity.getId());
                CellRangeAddress range1 = new CellRangeAddress(rowIndex, rowIndex, 1, 2);
                sheet.addMergedRegion(range1);
                RegionUtil.setBorderBottom(BorderStyle.THIN, range1, sheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, range1, sheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, range1, sheet);
                RegionUtil.setBorderTop(BorderStyle.THIN, range1, sheet);
                // subject name
                XSSFCell subjectNameCell = row.createCell(3);
                subjectNameCell.setCellValue(subjectEntity.getName());
                CellRangeAddress range2 = new CellRangeAddress(rowIndex, rowIndex, 3, 5);
                sheet.addMergedRegion(range2);
                RegionUtil.setBorderBottom(BorderStyle.THIN, range2, sheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, range2, sheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, range2, sheet);
                RegionUtil.setBorderTop(BorderStyle.THIN, range2, sheet);
                // credit
                XSSFCell creditCell = row.createCell(6);
                creditCell.setCellStyle(cellStyle);
                Map<SubjectEntity, Integer> subjectsCredits = processCreditsForSubject(entry.getKey().getDocumentStudentEntityList());
                creditCell.setCellValue(subjectsCredits.get(marksEntity.getSubjectMarkComponentId().getSubjectId()) + "");
                // mark
                XSSFCell markCell = row.createCell(7);
                markCell.setCellStyle(cellStyle);
                markCell.setCellValue(marksEntity.getAverageMark() + "");
                // grade
                XSSFCell gradeCell = row.createCell(8);
                gradeCell.setCellStyle(cellStyle);
                if (marksEntity.getAverageMark().intValue() >= 9) {
                    gradeCell.setCellValue("A+");
                } else if (marksEntity.getAverageMark().intValue() >= 8 && marksEntity.getAverageMark().intValue() <= 9) {
                    gradeCell.setCellValue("A");
                } else if (marksEntity.getAverageMark().intValue() >= 7 && marksEntity.getAverageMark().intValue() <= 8) {
                    gradeCell.setCellValue("B");
                } else if (marksEntity.getAverageMark().intValue() >= 6 && marksEntity.getAverageMark().intValue() <= 7) {
                    gradeCell.setCellValue("C+");
                } else if (marksEntity.getAverageMark().intValue() >= 5 && marksEntity.getAverageMark().intValue() <= 6) {
                    gradeCell.setCellValue("C-");
                } else {
                    gradeCell.setCellValue("");
                }

                if (ordinalNumber < markSize) {
                    sheet.shiftRows(rowIndex, sheet.getLastRowNum(), 1);
                }
                ordinalNumber++;
            }
        }
        workbook.removeSheetAt(0);
    }

    private Map<StudentEntity, List<MarksEntity>> processData(Map<String, String> params) {
        Map<StudentEntity, List<MarksEntity>> resultMap = new HashMap<>();

        List<List<String>> data = new ArrayList<>();

        int programId = Integer.parseInt(params.get("programId"));
        int semesterId = Integer.parseInt(params.get("semesterId"));

        // get list semester to current semesterId
        List<RealSemesterEntity> semesters = getToCurrentSemester(semesterId);
        Set<Integer> semesterIds = semesters.stream().map(s -> s.getId()).collect(Collectors.toSet());

        List<StudentEntity> studentEntityList;
        if (programId < 0) {
            studentEntityList = studentService.findAllStudents();
        } else {
            studentEntityList = studentService.findStudentByProgramId(programId);
        }
        // filter student in term 9
        studentEntityList = studentEntityList.stream().filter(s -> s.getTerm() == 9).collect(Collectors.toList());
        List<StudentEntity> filteredList = new ArrayList<>();
        for (StudentEntity studentEntity : studentEntityList) {
            List<StudentStatusEntity> studentStatusEntities = studentEntity.getStudentStatusEntityList();
            for (StudentStatusEntity studentStatusEntity : studentStatusEntities) {
                if (studentStatusEntity.getSemesterId().getId() == semesterId && !studentStatusEntities.get(0).getStatus().equals("G")) {
                    filteredList.add(studentEntity);
                }
            }
        }

        for (StudentEntity student : filteredList) {
            List<DocumentStudentEntity> documentStudentEntityList = student.getDocumentStudentEntityList();
            Map<SubjectEntity, Integer> subjectsCredits = processCreditsForSubject(documentStudentEntityList);
            // get mark list from student
            List<MarksEntity> marksEntityList = student.getMarksEntityList();
            // filter passed marks
            List<MarksEntity> passedMarks = new ArrayList<>();
            for (MarksEntity marksEntity : marksEntityList) {
                if ((marksEntity.getStatus().toLowerCase().contains("pass") || marksEntity.getStatus().toLowerCase().contains("exempt"))
                        && (semesterIds.contains(marksEntity.getSemesterId().getId()))) {
                    passedMarks.add(marksEntity);
                }
            }
            // distinct passed Marks
            List<MarksEntity> distinctMarks = new ArrayList<>();
            for (MarksEntity mark : passedMarks) {
                if (!distinctMarks.stream().anyMatch(d -> d.getStudentId().getRollNumber().equalsIgnoreCase(mark.getStudentId().getRollNumber())
                        && d.getSubjectMarkComponentId().getSubjectId().getId().equalsIgnoreCase(mark.getSubjectMarkComponentId().getSubjectId().getId()))) {
                    distinctMarks.add(mark);
                }
            }
            // calculate student credits if SYB was passed
            int studentCredits = 0;
            boolean passedFlag = false; // flag shows that a student have a subject that has 0 credits and not passed
            if (!subjectsCredits.containsValue(0)) {
                passedFlag = true;
            }

            List<MarksEntity> savedMarks = new ArrayList<>();
            for (MarksEntity marksEntity : distinctMarks) {
                SubjectEntity subject = marksEntity.getSubjectMarkComponentId().getSubjectId();
                if (!passedFlag) {
                    if ((subjectsCredits.get(subject) != null) && (subjectsCredits.get(subject) == 0)) {
                        passedFlag = true; // passed
                    }
                }

                if (subjectsCredits.get(subject) != null && subject.getType() != SubjectTypeEnum.OJT.getId()) {
                    studentCredits += subjectsCredits.get(subject);
                    savedMarks.add(marksEntity);
                }
            }

            int specializedCredits = student.getProgramId().getSpecializedCredits();
            if ((studentCredits >= specializedCredits) && (passedFlag)) {
                resultMap.put(student, savedMarks);
            }
        }

        return resultMap;
    }

    /**
        * [This method processes (sort all semesters then iterate over the list, add semester to result list until reaching the current semester)
        * and returns list semesters from the beginning to current semester]
        *
        * @param currentSemesterId
        * @return listResult
        * @author HiepNH
        * @DateCreated 28/10/2017
     **/
    private List<RealSemesterEntity> getToCurrentSemester(Integer currentSemesterId) {
        List<RealSemesterEntity> semesters = semesterService.getAllSemester();
        semesters = Ultilities.SortSemesters(semesters);
        List<RealSemesterEntity> listResult = new ArrayList<>();
        for (RealSemesterEntity semester : semesters) {
            listResult.add(semester);
            if (semester.getId() == currentSemesterId) {
                break;
            }
        }
        return listResult;
    }

    public Map<SubjectEntity, Integer> processCreditsForSubject(List<DocumentStudentEntity> documentStudentEntityList) {
        Map<SubjectEntity, Integer> map = new HashMap<>();
        if (documentStudentEntityList != null && !documentStudentEntityList.isEmpty()) {
            for (DocumentStudentEntity documentStudentEntity : documentStudentEntityList) {
                CurriculumEntity curriculumEntity = documentStudentEntity.getCurriculumId();
                List<SubjectCurriculumEntity> subjectCurriculumEntityList = subjectCurriculumService.getSubjectCurriculums(curriculumEntity.getId());
                subjectCurriculumEntityList = subjectCurriculumEntityList.stream().filter(s -> s.getTermNumber() >= 0).collect(Collectors.toList());

                for (SubjectCurriculumEntity subjectCurriculumEntity : subjectCurriculumEntityList) {
                    SubjectEntity subjectEntity = subjectCurriculumEntity.getSubjectId();
                    Integer subjectCredits = subjectCurriculumEntity.getSubjectCredits();
                    map.put(subjectEntity, subjectCredits);
                    List<SubjectEntity> replacesInTheRight = subjectEntity.getSubjectEntityList();
                    if (replacesInTheRight != null && !replacesInTheRight.isEmpty()) {
                        for (SubjectEntity rightReplace : replacesInTheRight) {
                            map.put(rightReplace, subjectCredits);
                        }
                    }
                    List<SubjectEntity> replacesInTheLeft = subjectEntity.getSubjectEntityList1();
                    if (replacesInTheLeft != null && !replacesInTheLeft.isEmpty()) {
                        for (SubjectEntity leftReplace : replacesInTheLeft) {
                            map.put(leftReplace, subjectCredits);

                            for (SubjectEntity rightOfLeftReplace : leftReplace.getSubjectEntityList()) {
                                map.put(rightOfLeftReplace, subjectCredits);
                            }
                        }
                    }
                }
            }
        }

        return map;
    }
}
