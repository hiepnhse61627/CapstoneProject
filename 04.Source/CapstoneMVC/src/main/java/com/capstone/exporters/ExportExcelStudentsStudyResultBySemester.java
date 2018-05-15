package com.capstone.exporters;

import com.capstone.entities.*;
import com.capstone.enums.SubjectTypeEnum;
import com.capstone.models.Logger;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import org.apache.commons.lang.ArrayUtils;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExportExcelStudentsStudyResultBySemester implements IExportObject {
    private String EXCEL_TEMPLATE = "template/My-Template-Student-Study-Result.xlsx";

    private StudentServiceImpl studentService = new StudentServiceImpl();
    private MarksServiceImpl marksService = new MarksServiceImpl();
    private RealSemesterServiceImpl realSemesterService = new RealSemesterServiceImpl();
    private SubjectServiceImpl subjectService = new SubjectServiceImpl();
    private SubjectCurriculumServiceImpl subjectCurriculumService = new SubjectCurriculumServiceImpl();

    private String fileName = "Students-StudyResult.xlsx";

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void setFileName(String name) {
        this.fileName = name;
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

        workbook.write(os);
    }

    private void writeDataToTable(XSSFWorkbook workbook, XSSFSheet sheet, Map<String, String> params) throws Exception {
        try {


            //use for birthDate format
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            // Cell style
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            int semesterId = Integer.parseInt(params.get("semesterId"));
            int programId = Integer.parseInt(params.get("programId"));
            RealSemesterEntity semester = realSemesterService.findSemesterById(semesterId);
            String semesterName = semester.getSemester().toUpperCase();
            Map<StudentEntity, List<StudyInfoMarkModel>> dataHashMap = new HashMap<>();
            List<StudentEntity> listStudents = studentService.findStudentsBySemesterId(semesterId);
            Map<String, Map<String, Integer>> studentSubjCredits = new HashMap<>();

            if(programId > -1){
                listStudents = listStudents.stream().filter(q -> q.getProgramId().getId() == programId)
                        .collect(Collectors.toList());
            }

            //use lambda sort all students by program then by roll number with descending order
            listStudents.sort((p1, p2) -> {
                if (p2.getProgramId().getName().compareTo(p1.getProgramId().getName()) == 0) {
                    return p2.getRollNumber().compareTo(p1.getRollNumber());
                } else {
                    return p2.getProgramId().getName().compareTo(p1.getProgramId().getName());
                }

            });

            ExportStatusReport.StatusStudentDetailExport = "Đang tìm và lấy thông tin danh sách sinh viên của học kỳ";
            int check2 = 1;
            for (StudentEntity student : listStudents) {
                System.out.println("find " + check2 + " - " + (listStudents.size()+1));
                ExportStatusReport.StatusStudentDetailExport = "Đang lấy thông tin " + check2 + " - " + (listStudents.size()+1);
                //get only marks from given semester
                String rollNumber = student.getRollNumber();
                List<MarksEntity> stuMarksList = marksService.getMarksByStudentIdAndSemester(student.getId(), semesterId);
                //Map<SubjectCode, MarksEntity> , get final mark of Subject in Semester
                Map<String, MarksEntity> marksList = new HashMap<>();
                for (MarksEntity mark : stuMarksList) {
                    String subjectId = mark.getSubjectMarkComponentId().getSubjectId().getId();

                    marksList.put(subjectId, mark);
                }
                //contains only final marks of subject
                stuMarksList = new ArrayList<MarksEntity>(marksList.values());
                List<StudyInfoMarkModel> dataList = new ArrayList<>();
                for (MarksEntity mItem : stuMarksList) {
                    SubjectEntity subjectEntity = mItem.getSubjectMarkComponentId().getSubjectId();
                    String subjectId = subjectEntity.getId();
                    Double avgMark = mItem.getAverageMark();
                    String status = mItem.getStatus();
                    dataList.add(new StudyInfoMarkModel(subjectEntity, status, avgMark, subjectId));
                }
                dataHashMap.put(student, dataList);
                // Map<SubjectId, Credits>
                Map<String, Integer> subjCredits = processCreditsForSubjects(student.getDocumentStudentEntityList());
                studentSubjCredits.put(rollNumber, subjCredits);
                check2++;
            }
            ExportStatusReport.StatusStudentDetailExport = "Lấy điểm từng sinh viên";
            int check = 1;
            for (StudentEntity student : listStudents) {
                ExportStatusReport.StatusStudentDetailExport = "Đang khởi tạo file " + check + " - " + (listStudents.size()+1);
                String rollNumber = student.getRollNumber();
                String fullName = student.getFullName();
                Date dateOfBirth = student.getDateOfBirth();
                Map<String, Integer> subjCredits = studentSubjCredits.get(rollNumber);

                sheet = workbook.cloneSheet(0, rollNumber);
                //rows start from 0 index
                XSSFRow row = sheet.getRow(9);
                //Cells of row start from 0
                row.getCell(3).setCellValue(fullName);
                row.getCell(5).setCellValue(rollNumber);
                row.getCell(8).setCellValue(sdf.format(dateOfBirth));


                row = sheet.getRow(11);
                //Program name
                row.getCell(3).setCellValue(student.getProgramId().getFullName());

                //Semester name
                row.getCell(8).setCellValue(semesterName);

                row = sheet.getRow(12);
                row.getCell(8).setCellValue(semesterName);


                int ordinalNumber = 1;
                int rowIndex = 18;
                int markSize = dataHashMap.get(student).size();
                for (StudyInfoMarkModel marksEntity : dataHashMap.get(student)) {
                    row = sheet.createRow(rowIndex);
                    // ordinal number
                    XSSFCell ordinalNumberCell = row.createCell(1);
                    ordinalNumberCell.setCellStyle(cellStyle);
                    ordinalNumberCell.setCellValue("" + (markSize - ordinalNumber + 1));
                    // Subject code
                    SubjectEntity subjectEntity = marksEntity.getSubjectEntity();
                    String subjId = marksEntity.getSubjectId();
                    XSSFCell subjectCodeCell = row.createCell(2);
                    subjectCodeCell.setCellValue(subjId);
                    CellRangeAddress range1 = new CellRangeAddress(rowIndex, rowIndex, 2, 3);
                    sheet.addMergedRegion(range1);
                    RegionUtil.setBorderBottom(BorderStyle.THIN, range1, sheet);
                    RegionUtil.setBorderLeft(BorderStyle.THIN, range1, sheet);
                    RegionUtil.setBorderRight(BorderStyle.THIN, range1, sheet);
                    RegionUtil.setBorderTop(BorderStyle.THIN, range1, sheet);

                    // subject name
                    XSSFCell subjectNameCell = row.createCell(4);
                    subjectNameCell.setCellValue(subjectEntity.getName());
                    subjectNameCell.setCellStyle(cellStyle);

                    // credit
                    XSSFCell creditCell = row.createCell(5);
                    creditCell.setCellStyle(cellStyle);
//                Map<SubjectEntity, Integer> subjectsCredits = processCreditsForSubject(entry.getKey().getDocumentStudentEntityList());
//                String sujCredits = subjectsCredits.get(subjectEntity) != null ? subjectsCredits.get(subjectEntity).toString() : "";
                    Integer subjectCredit = subjCredits.get(subjId);
                    if (subjectCredit != null)
                        creditCell.setCellValue(subjectCredit);

                    // mark
                    XSSFCell markCell = row.createCell(6);
                    markCell.setCellStyle(cellStyle);
                    markCell.setCellValue(marksEntity.getAvg() + "");

                    //number of learned time
                    XSSFCell learnedTime = row.createCell(7);
                    learnedTime.setCellStyle(cellStyle);
                    int count = checkSubjectLearnedTime(student.getId(), subjectEntity);

                    learnedTime.setCellValue(count + "");

                    // status
                    XSSFCell gradeCell = row.createCell(8);
                    gradeCell.setCellStyle(cellStyle);
                    String status = marksEntity.getMarkStatus().toLowerCase();
                    switch (status) {
                        case "passed":
                            gradeCell.setCellValue("Đạt");
                            break;
                        case "fail":
                            gradeCell.setCellValue("Rớt");
                            break;
                        case "notstart":
                            gradeCell.setCellValue("Chậm tiến độ");
                            break;
                        default:
                            gradeCell.setCellValue("Đang học");
                            break;
                    }


                    if (ordinalNumber < markSize) {
                        sheet.shiftRows(rowIndex, sheet.getLastRowNum(), 1);
                    }
                    ordinalNumber++;
                }
                ++check;
                System.out.println(check + " - " + (listStudents.size()+1));
            }
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }
        ExportStatusReport.StatusStudentDetailExport = "Hoàn tất tạo file";
        //delete clone Sheet
        workbook.removeSheetAt(0);
    }


//    public SubjectCurriculumEntity processCreditsForSubject(List<DocumentStudentEntity> documentStudentEntityList, String subjectId) {
//        SubjectCurriculumEntity resultEntity = null;
//        if (documentStudentEntityList != null && !documentStudentEntityList.isEmpty()) {
//            for (DocumentStudentEntity documentStudentEntity : documentStudentEntityList) {
//                CurriculumEntity curriculumEntity = documentStudentEntity.getCurriculumId();
//                List<SubjectCurriculumEntity> subjectCurriculumEntityList = subjectCurriculumService.getSubjectCurriculums(curriculumEntity.getId());
//                subjectCurriculumEntityList = subjectCurriculumEntityList.stream()
//                        .filter(q -> q.getSubjectId().getId().equals(subjectId)).collect(Collectors.toList());
//
//                //only 1 subject in curriculum
//                if (subjectCurriculumEntityList.size() != 0) {
//                    resultEntity = subjectCurriculumEntityList.get(0);
//                    break;
//                }
//            }
//        }
////        System.out.println("Done credit");
//        return resultEntity;
//    }

    public Map<String, Integer> processCreditsForSubjects(List<DocumentStudentEntity> documentStudentEntityList) {
        Map<String, Integer> mapSubjCredits = new HashMap<>();
        if (documentStudentEntityList != null && !documentStudentEntityList.isEmpty()) {
            for (DocumentStudentEntity documentStudentEntity : documentStudentEntityList) {
                CurriculumEntity curriculumEntity = documentStudentEntity.getCurriculumId();
                List<SubjectCurriculumEntity> subjectCurriculumEntityList = curriculumEntity.getSubjectCurriculumEntityList();
                for (SubjectCurriculumEntity subjCurr : subjectCurriculumEntityList) {
                    SubjectEntity subEntity = subjCurr.getSubjectId();
                    int subCredit = subjCurr.getSubjectCredits();
                    List<SubjectEntity> allReplacements = Ultilities.findBackAndForwardReplacementSubject(subEntity);
                    for (SubjectEntity subItem : allReplacements) {
                        String subId = subItem.getId();
                        mapSubjCredits.put(subId, subCredit);
                    }
                }
            }
        }
//        System.out.println("Done credit");
        return mapSubjCredits;
    }

    public int checkSubjectLearnedTime(int studentId, SubjectEntity subjectEntity) {

        Map<String, SubjectEntity> map = new HashMap<>();
        //put subject into map
        map.put(subjectEntity.getId(), subjectEntity);

        //put any replacement subject in map, check all replacement subject
        List<SubjectEntity> replacesInTheRight = subjectEntity.getSubjectEntityList();
        if (replacesInTheRight != null && !replacesInTheRight.isEmpty()) {
            for (SubjectEntity rightReplace : replacesInTheRight) {
                map.put(rightReplace.getId(), rightReplace);
            }
        }


        //get all SubjectId
        List<String> finalSubjList = new ArrayList<>(map.keySet());

        //check number of times learned given subject or its replacementSubjects
        List<MarksEntity> list = marksService.getMarksByStudentAndSubjectIdList(studentId, finalSubjList);

        return list.size();
    }


//    public Map<SubjectEntity, Integer> processCreditsForSubject(List<DocumentStudentEntity> documentStudentEntityList) {
//        Map<SubjectEntity, Integer> map = new HashMap<>();
//        if (documentStudentEntityList != null && !documentStudentEntityList.isEmpty()) {
//            for (DocumentStudentEntity documentStudentEntity : documentStudentEntityList) {
//                CurriculumEntity curriculumEntity = documentStudentEntity.getCurriculumId();
//                List<SubjectCurriculumEntity> subjectCurriculumEntityList = subjectCurriculumService.getSubjectCurriculums(curriculumEntity.getId());
//                subjectCurriculumEntityList = subjectCurriculumEntityList.stream().filter(s -> s.getTermNumber() >= 0).collect(Collectors.toList());
//
//                for (SubjectCurriculumEntity subjectCurriculumEntity : subjectCurriculumEntityList) {
//                    SubjectEntity subjectEntity = subjectCurriculumEntity.getSubjectId();
//                    Integer subjectCredits = subjectCurriculumEntity.getSubjectCredits();
//                    map.put(subjectEntity, subjectCredits);
//                    List<SubjectEntity> replacesInTheRight = subjectEntity.getSubjectEntityList();
//                    if (replacesInTheRight != null && !replacesInTheRight.isEmpty()) {
//                        for (SubjectEntity rightReplace : replacesInTheRight) {
//                            map.put(rightReplace, subjectCredits);
//                        }
//                    }
//                    List<SubjectEntity> replacesInTheLeft = subjectEntity.getSubjectEntityList1();
//                    if (replacesInTheLeft != null && !replacesInTheLeft.isEmpty()) {
//                        for (SubjectEntity leftReplace : replacesInTheLeft) {
//                            map.put(leftReplace, subjectCredits);
//
//                            for (SubjectEntity rightOfLeftReplace : leftReplace.getSubjectEntityList()) {
//                                map.put(rightOfLeftReplace, subjectCredits);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        return map;
//    }

    public class StudyInfoMarkModel {
        private SubjectEntity subjectEntity;
        private String markStatus;
        private double avg;
        private String subjectId;

        public StudyInfoMarkModel(SubjectEntity subjectEntity, String markStatus, double avg, String subjectId) {
            this.subjectEntity = subjectEntity;
            this.markStatus = markStatus;
            this.avg = avg;
            this.subjectId = subjectId;
        }

        public StudyInfoMarkModel() {
        }

        public SubjectEntity getSubjectEntity() {
            return subjectEntity;
        }

        public void setSubjectEntity(SubjectEntity subjectEntity) {
            this.subjectEntity = subjectEntity;
        }

        public String getMarkStatus() {
            return markStatus;
        }

        public void setMarkStatus(String markStatus) {
            this.markStatus = markStatus;
        }

        public double getAvg() {
            return avg;
        }

        public void setAvg(double avg) {
            this.avg = avg;
        }

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }
    }

}
