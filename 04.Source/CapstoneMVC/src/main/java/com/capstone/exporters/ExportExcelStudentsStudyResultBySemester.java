package com.capstone.exporters;

import com.capstone.entities.*;
import com.capstone.enums.SubjectTypeEnum;
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
        Map<StudentEntity, List<MarksEntity>> dataHashMap = new HashMap<>();
        List<StudentEntity> listStudents = studentService.findStudentsBySemesterId(semesterId);

        //use lambda sort all students by program then by roll number with descending order
        listStudents.sort((p1, p2) -> {
            if (p2.getProgramId().getName().compareTo(p1.getProgramId().getName()) == 0) {
                return p2.getRollNumber().compareTo(p1.getRollNumber());
            } else {
                return p2.getProgramId().getName().compareTo(p1.getProgramId().getName());
            }

        });


        for (StudentEntity student : listStudents) {
            //get only marks from given semester

            List<MarksEntity> stuMarksList = marksService.getMarksByStudentIdAndSemester(student.getId(), semesterId);
            //Map<SubjectCode, MarksEntity> , get final mark of Subject in Semester
            Map<String, MarksEntity> marksList = new HashMap<>();
            for (MarksEntity mark : stuMarksList) {
                String subjectId = mark.getSubjectMarkComponentId().getSubjectId().getId();
                marksList.put(subjectId, mark);
            }
            //contains only final marks of subject
            stuMarksList = new ArrayList<MarksEntity>(marksList.values());
            dataHashMap.put(student, stuMarksList);

        }

        int check = 1;
        for (StudentEntity student : listStudents) {

            sheet = workbook.cloneSheet(0, student.getRollNumber());
            //rows start from 0 index
            XSSFRow row = sheet.getRow(9);
            //Cells of row start from 0
            row.getCell(3).setCellValue(student.getFullName());
            row.getCell(5).setCellValue(student.getRollNumber());
            row.getCell(8).setCellValue(sdf.format(student.getDateOfBirth()));


            row = sheet.getRow(11);
            //Program name
            row.getCell(3).setCellValue(student.getProgramId().getFullName());
            RealSemesterEntity semester = realSemesterService.findSemesterById(semesterId);
            //Semester name
            row.getCell(8).setCellValue(semester.getSemester().toString());

            row = sheet.getRow(12);
            row.getCell(8).setCellValue(semester.getSemester().toString());


            int ordinalNumber = 1;
            int rowIndex = 18;
            int markSize = dataHashMap.get(student).size();
            for (MarksEntity marksEntity : dataHashMap.get(student)) {
                row = sheet.createRow(rowIndex);
                // ordinal number
                XSSFCell ordinalNumberCell = row.createCell(1);
                ordinalNumberCell.setCellStyle(cellStyle);
                ordinalNumberCell.setCellValue("" + (markSize - ordinalNumber + 1));
                // Subject code
                SubjectEntity subjectEntity = marksEntity.getSubjectMarkComponentId().getSubjectId();
                XSSFCell subjectCodeCell = row.createCell(2);
                subjectCodeCell.setCellValue(subjectEntity.getId());
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
                SubjectCurriculumEntity sujCredits = processCreditsForSubject(student.getDocumentStudentEntityList(),
                        subjectEntity.getId());
                if (sujCredits != null)
                    creditCell.setCellValue(sujCredits.getSubjectCredits() + "");

                // mark
                XSSFCell markCell = row.createCell(6);
                markCell.setCellStyle(cellStyle);
                markCell.setCellValue(marksEntity.getAverageMark() + "");

                //number of learned time
                XSSFCell learnedTime = row.createCell(7);
                learnedTime.setCellStyle(cellStyle);
                int count = checkSubjectLearnedTime(subjectEntity.getId(), student.getId(), subjectEntity);

                learnedTime.setCellValue(count + "");

                // status
                XSSFCell gradeCell = row.createCell(8);
                gradeCell.setCellStyle(cellStyle);
                String status = marksEntity.getStatus().toLowerCase();
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
            System.out.println(check);
        }
        //delete clone Sheet
        workbook.removeSheetAt(0);
    }


    public SubjectCurriculumEntity processCreditsForSubject(List<DocumentStudentEntity> documentStudentEntityList, String subjectId) {
        SubjectCurriculumEntity resultEntity = null;
        if (documentStudentEntityList != null && !documentStudentEntityList.isEmpty()) {
            for (DocumentStudentEntity documentStudentEntity : documentStudentEntityList) {
                CurriculumEntity curriculumEntity = documentStudentEntity.getCurriculumId();
                List<SubjectCurriculumEntity> subjectCurriculumEntityList = subjectCurriculumService.getSubjectCurriculums(curriculumEntity.getId());
                subjectCurriculumEntityList = subjectCurriculumEntityList.stream()
                        .filter(q -> q.getSubjectId().getId().equals(subjectId)).collect(Collectors.toList());

                //only 1 subject in curriculum
                if (subjectCurriculumEntityList.size() != 0) {
                    resultEntity = subjectCurriculumEntityList.get(0);
                    break;
                }
            }
        }
//        System.out.println("Done credit");
        return resultEntity;
    }

    public int checkSubjectLearnedTime(String subjectId, int studentId, SubjectEntity subjectEntity) {

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


}
