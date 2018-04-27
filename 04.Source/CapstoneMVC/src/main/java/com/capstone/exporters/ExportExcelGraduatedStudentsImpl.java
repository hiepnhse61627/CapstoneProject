package com.capstone.exporters;

import com.capstone.entities.*;
import com.capstone.enums.SubjectTypeEnum;
import com.capstone.models.*;
import com.capstone.services.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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

public class ExportExcelGraduatedStudentsImpl implements IExportObject {
    private String EXCEL_TEMPLATE = "template/DSSV-TN.xlsx";
    private IMarksService marksService = new MarksServiceImpl();
    private ISubjectService subjectService = new SubjectServiceImpl();
    private IRealSemesterService semesterService = new RealSemesterServiceImpl();
    private ISubjectCurriculumService subjectCurriculumService = new SubjectCurriculumServiceImpl();
    IStudentService studentService = new StudentServiceImpl();
    StudentStatusServiceImpl studentStatusService = new StudentStatusServiceImpl();
    GraduationConditionServiceImpl graduationConditionService = new GraduationConditionServiceImpl();
    GraduateDetailServiceImpl graduateDetailService = new GraduateDetailServiceImpl();

    private String fileName = "Graduated-Students.xlsx";

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

        HashMap<String, List<String>> thesisNames = (HashMap<String, List<String>>)
                request.getSession().getAttribute(Enums.GraduateVariable.ThesisName_List.getValue());

        ExportStatusReport.StatusStudentDetailExport = "Đang xử lý";
        ExportStatusReport.StatusExportStudentDetailRunning = true;
        ExportStatusReport.StopExporting = false;

        XSSFWorkbook workbook = new XSSFWorkbook(is);
        // close input stream
//        is.close();
        XSSFSheet spreadsheet = workbook.getSheetAt(0);

        writeDataToTable(workbook, spreadsheet, params, thesisNames, request);

        workbook.write(os);
    }

    private void writeDataToTable(XSSFWorkbook workbook, XSSFSheet sheet, Map<String, String> params, HashMap<String, List<String>> thesisNames, HttpServletRequest request) throws Exception {

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat monthfirstFromat = new SimpleDateFormat("MMMM dd, yyyy");
            // style
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);


//        Map<StudentEntity, List<MarkCreditTermModel>> dataMap = processData2(params);
            ExportStatusReport.StatusStudentDetailExport = "Đang tìm danh sách sinh viên tốt nghiệp";

            List<StudentAndMark> dataMap = (List<StudentAndMark>)
                    request.getSession().getAttribute(Enums.GraduateVariable.GRADUATE_LIST.getValue());
            int requestProgramId = Integer.parseInt(params.get("programId"));
            int requestSemesterId = Integer.parseInt(params.get("semesterId"));
            Integer currentProgramId = (Integer) request.getSession()
                    .getAttribute(Enums.GraduateVariable.PROGRAM_ID.getValue());
            Integer currentSemesterId = (Integer) request.getSession()
                    .getAttribute(Enums.GraduateVariable.SEMESTER_ID.getValue());

            if (dataMap == null || currentProgramId == null || currentSemesterId == null
                    || currentProgramId != requestProgramId
                    || currentSemesterId != requestSemesterId) {
                dataMap = processData2(params);

                //set lên session nếu chưa có
                request.getSession()
                        .setAttribute(Enums.GraduateVariable.PROGRAM_ID.getValue(), requestProgramId);
                request.getSession()
                        .setAttribute(Enums.GraduateVariable.SEMESTER_ID.getValue(), requestSemesterId);
                request.getSession()
                        .setAttribute(Enums.GraduateVariable.GRADUATE_LIST.getValue(), dataMap);
            }


            request.getSession().setAttribute(Enums.GraduateVariable.GRADUATE_LIST.getValue(), dataMap);


            ExportStatusReport.StatusStudentDetailExport = "Đang khởi tạo file";
            int myIndex = 1;
            for (StudentAndMark entry : dataMap) {
                ExportStatusReport.StatusStudentDetailExport = "Đang khởi tạo file " + myIndex + " - " + dataMap.size();
                StudentEntity student = entry.getStudent();
                sheet = workbook.cloneSheet(0, student.getRollNumber());
                XSSFRow row = sheet.getRow(11);
                row.getCell(2).setCellValue(student.getFullName());
                row.getCell(6).setCellValue(student.getRollNumber());

                //Date of birth
                Date dateOfBirth = student.getDateOfBirth();
                row = sheet.getRow(13);
                row.getCell(2).setCellValue(sdf.format(dateOfBirth));
                row = sheet.getRow(14);
                row.getCell(2).setCellValue(monthfirstFromat.format(dateOfBirth));

                //Diploma Code
                GraduateDetailEntity graduateDetailEntity = graduateDetailService.findGraduateDetailEntity(student.getId());
                if (graduateDetailEntity != null) {
                    try {
                        String diplomaCode = graduateDetailEntity.getDiplomaCode();
                        String notformatGraduateDate = graduateDetailEntity.getDate();


                        SimpleDateFormat gradateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String graduateDate = monthfirstFromat.format(gradateFormat.parse(notformatGraduateDate));
                        row = sheet.getRow(8);
                        row.getCell(1).setCellValue("(Kèm theo văn bằng tốt nghiệp số "
                                + diplomaCode + ", cấp ngày " + graduateDate + ")");
                        row = sheet.getRow(9);
                        row.getCell(1)
                                .setCellValue("(Attached to diploma No." + diplomaCode + ", issued on " + graduateDate + ")");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //ngành , chuyên ngành
                row = sheet.getRow(15);
                row.getCell(2).setCellValue(student.getProgramId().getFullName());
//            DocumentStudentEntity documentStudentEntity = Ultilities.getStudentLatestDocument(entry.getKey());
//            row.getCell(7).setCellValue(documentStudentEntity.getCurriculumId().getName());
                row.getCell(7).setCellValue(student.getProgramId().getFullName());

                //in tên đề tài
                if (thesisNames != null) {
                    //thesisName<MSSV, List<Tên đồ án tiếng việt, tên đồ án tiếng anh>>
                    List<String> names = thesisNames.get(student.getRollNumber());
                    //tên đồ án = tiếng việt
                    row = sheet.getRow(22);
                    row.getCell(1).setCellValue(names.get(0));
                    //tên đồ án = tiếng anh
                    row = sheet.getRow(24);
                    row.getCell(2).setCellValue(names.get(1));
                }


                //điểm trung bình
                row = sheet.getRow(26);
                double average = entry.getAverage();
                row.getCell(8).setCellValue(average);

                //loại tốt nghiệp
                String rankingVn = "";
                String rankingEng = "";
                if (average >= 9) {
                    rankingVn = "Xuất sắc";
                    rankingEng = "Excellent";
                } else if (average >= 8) {
                    rankingVn = "Giỏi";
                    rankingEng = "Very good";
                } else if (average >= 7) {
                    rankingVn = "Khá";
                    rankingEng = "Good";
                } else if (average >= 6) {
                    rankingVn = "Trung bình khá";
                    rankingEng = "Fairly Good";
                } else if (average >= 5) {
                    rankingVn = "Trung bình";
                    rankingEng = "Ordinary";
                }
                row = sheet.getRow(30);
                row.getCell(7).setCellValue(rankingVn);
                row = sheet.getRow(31);
                row.getCell(7).setCellValue(rankingEng);

                int ordinalNumber = 1;
                int rowIndex = 20;
                List<MarkCreditTermModel> marksList = entry.getMarkList();
                int markSize = marksList.size();
                Collections.reverse(marksList);
                for (MarkCreditTermModel item : marksList) {

                    MarksEntity marksEntity = item.getMark();

                    row = sheet.createRow(rowIndex);
                    // ordinal number
                    XSSFCell ordinalNumberCell = row.createCell(0);
                    ordinalNumberCell.setCellStyle(cellStyle);
                    ordinalNumberCell.setCellValue("" + (markSize - ordinalNumber + 1));

                    // Subject code
                    SubjectEntity subjectEntity = subjectService.findSubjectById(marksEntity.getSubjectMarkComponentId().getSubjectId().getId());
                    XSSFCell subjectCodeCell = row.createCell(1);
                    subjectCodeCell.setCellValue(subjectEntity.getName());
                    CellRangeAddress range1 = new CellRangeAddress(rowIndex, rowIndex, 1, 2);
                    sheet.addMergedRegion(range1);
                    RegionUtil.setBorderBottom(BorderStyle.THIN, range1, sheet);
                    RegionUtil.setBorderLeft(BorderStyle.THIN, range1, sheet);
                    RegionUtil.setBorderRight(BorderStyle.THIN, range1, sheet);
                    RegionUtil.setBorderTop(BorderStyle.THIN, range1, sheet);
                    // subject name
                    XSSFCell subjectNameCell = row.createCell(3);
                    subjectNameCell.setCellValue(subjectEntity.getVnName());
                    CellRangeAddress range2 = new CellRangeAddress(rowIndex, rowIndex, 3, 5);
                    sheet.addMergedRegion(range2);
                    RegionUtil.setBorderBottom(BorderStyle.THIN, range2, sheet);
                    RegionUtil.setBorderLeft(BorderStyle.THIN, range2, sheet);
                    RegionUtil.setBorderRight(BorderStyle.THIN, range2, sheet);
                    RegionUtil.setBorderTop(BorderStyle.THIN, range2, sheet);
                    // credit
                    XSSFCell creditCell = row.createCell(6);
                    creditCell.setCellStyle(cellStyle);
//                Map<SubjectEntity, Integer> subjectsCredits = processCreditsForSubject(entry.getKey().getDocumentStudentEntityList());
//                creditCell.setCellValue(subjectsCredits.get(marksEntity.getSubjectMarkComponentId().getSubjectId()) + "");
                    creditCell.setCellValue(item.getCredit());

                    // mark
                    XSSFCell markCell = row.createCell(7);
                    markCell.setCellStyle(cellStyle);
                    //làm tròn 2 chữ số
                    double round2Decimal = Math.round(marksEntity.getAverageMark() * 100.0) / 100.0;
                    if (round2Decimal == 0) {
                        markCell.setCellValue("Pass");
                    } else {
                        markCell.setCellValue(round2Decimal);
                    }
//                    markCell.setCellValue(round2Decimal == 0 ? "Pass" : round2Decimal + "");
                    // grade
                    XSSFCell gradeCell = row.createCell(8);
                    gradeCell.setCellStyle(cellStyle);
                    Double averageMark = marksEntity.getAverageMark();
                    if (averageMark >= 9) {
                        gradeCell.setCellValue("A+");
                    } else if (averageMark >= 8.5) {
                        gradeCell.setCellValue("A");
                    } else if (averageMark >= 8) {
                        gradeCell.setCellValue("A-");
                    } else if (averageMark >= 7.5) {
                        gradeCell.setCellValue("B+");
                    } else if (averageMark >= 7) {
                        gradeCell.setCellValue("B");
                    } else if (averageMark >= 6.5) {
                        gradeCell.setCellValue("B-");
                    } else if (averageMark >= 6) {
                        gradeCell.setCellValue("C+");
                    } else if (averageMark >= 5.5) {
                        gradeCell.setCellValue("C");
                    } else if (averageMark >= 5) {
                        gradeCell.setCellValue("C-");
                    } else if (averageMark == 0 && marksEntity.getStatus()
                            .equalsIgnoreCase(Enums.MarkStatus.PASSED.getValue())) {
                        gradeCell.setCellValue("Pass");
                    } else {
                        gradeCell.setCellValue("F");
                    }

                    if (ordinalNumber < markSize) {
                        sheet.shiftRows(rowIndex, sheet.getLastRowNum(), 1);
                    }
                    ordinalNumber++;
                }

                //vị trí dòng ngay trên 2 tên đề tài
                int thesisRow = rowIndex + ordinalNumber;
                //set lại row height cho tên đồ án, vì sau khi shift column (npoi sẽ auto size lại cell, row = default height)
                sheet.getRow(thesisRow).setHeightInPoints(49);
                sheet.getRow(thesisRow + 2).setHeightInPoints(49);
//            sheet.shiftRows(21, sheet.getLastRowNum(), -1);
                System.out.println("Done " + myIndex + " - " + dataMap.size());
                myIndex++;
            }
            //remove first template
            if (!dataMap.isEmpty())
                workbook.removeSheetAt(0);
            ExportStatusReport.StatusStudentDetailExport = "Hoàn tất tạo file";
//            ExportStatusReport.StatusExportStudentDetailRunning = false;
            ExportStatusReport.StopExporting = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private List<StudentAndMark> processData2(Map<String, String> params) {
        int programId = Integer.parseInt(params.get("programId"));
        int semesterId = Integer.parseInt(params.get("semesterId"));

        List<StudentAndMark> resultMap = new ArrayList<>();
        int previousSemesterId = Ultilities.GetSemesterIdBeforeThisId(semesterId);
        List<StudentEntity> studentEntityList;
        if (programId < 0) {
            studentEntityList = studentService.findAllStudents();
        } else {
            studentEntityList = studentService.getStudentBySemesterIdAndProgram(previousSemesterId, programId);
        }


//
        List<StudentEntity> filteredStudents = new ArrayList<>();
        List<StudentStatusEntity> allStatus = studentStatusService.getStudentStatusBySemesterId(semesterId);
        List<GraduationConditionEntity> graduationConditions = graduationConditionService.findAllGraduationCondition();

        studentEntityList = studentEntityList.stream().filter(s -> isCapstone(s, previousSemesterId)).collect(Collectors.toList());


        //loại những học sinh đã tốt nghiệp ra, chỉ add những sinh viên chưa tốt nghiệp tại kì đang xét
        for (StudentEntity studentEntity : studentEntityList) {
            List<StudentStatusEntity> studentStatusEntities = new ArrayList<>(studentEntity.getStudentStatusEntityList());
            //lấy ra status của sinh viên
            List<StudentStatusEntity> tempStatus = studentStatusEntities.stream()
                    .filter(q -> q.getSemesterId().getId() == previousSemesterId && q.getStatus().equalsIgnoreCase("G"))
                    .collect(Collectors.toList());

            //nếu như học sinh này chưa tốt nghiệp ở kì chỉ định thì add vào mảng để mốt xét
            if (tempStatus.isEmpty()) {
                filteredStudents.add(studentEntity);
            }

        }


        //lay danh sach status roi stream filter

        //use 4 test
//        List<StudentEntity> a = studentService.findAllStudents();
//        List<StudentEntity> temp = a.stream().filter(q -> q.getRollNumber().equalsIgnoreCase("SE61822")
//                || q.getRollNumber().equalsIgnoreCase("SE62094")
//                || q.getRollNumber().equalsIgnoreCase("SE62137")
//        ).collect(Collectors.toList());
//        filteredStudents.addAll(temp);

        System.out.println(filteredStudents.size() + " students");
        int i = 1;
        for (StudentEntity student : filteredStudents) {
            boolean failFlag = false;
            List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
            List<MarksEntity> allMarks = new ArrayList<>(student.getMarksEntityList());
            List<MarkCreditTermModel> finalMarks = new ArrayList<>();
            List<SubjectCurriculumEntity> subjectCurriculumList = new ArrayList<>();

            String startCourse = "";
            for (DocumentStudentEntity docStudent : docs) {
                CurriculumEntity curriculum = docStudent.getCurriculumId();
                String curriculumName = curriculum.getName();
                if (startCourse.isEmpty() && !curriculumName.toLowerCase().contains("pc")) {
                    int firstToken = curriculumName.indexOf("_", 0);
                    int secondToken = curriculumName.indexOf("_", firstToken + 1);

                    if (secondToken > -1) {
                        startCourse = curriculumName.substring(firstToken + 1, secondToken);
                    } else {
                        startCourse = curriculumName.substring(firstToken + 1, curriculumName.length());
                    }
                }
                subjectCurriculumList.addAll(curriculum.getSubjectCurriculumEntityList());
            }
            // tính tín chỉ tích lũy của sv
            int studentCredits = 0;
            int ojtCredits = 0;
            for (SubjectCurriculumEntity subjectCurriculum : subjectCurriculumList) {
                SubjectEntity subject = subjectCurriculum.getSubjectId();
                if (subject.getType() == Enums.SubjectType.OJT.getValue()) {
                    ojtCredits = subjectCurriculum.getSubjectCredits();
                }

                //mảng này chứa tất cả môn thay thế và môn chính
                List<SubjectEntity> checkSubjects = Ultilities.findBackAndForwardReplacementSubject(subject);
                List<MarksEntity> filteredMarks = allMarks.stream().filter(q -> checkSubjects.stream()
                        .anyMatch(c -> c.getId()
                                .equalsIgnoreCase(q.getSubjectMarkComponentId().getSubjectId().getId())))
                        .collect(Collectors.toList());
                List<MarksEntity> sortedMarks = Ultilities.SortSemestersByMarks(filteredMarks);

                //get latest mark
                if (!sortedMarks.isEmpty()) {
                    MarksEntity latestMark = sortedMarks.get(sortedMarks.size() - 1);
                    RealSemesterEntity tmpSemester = latestMark.getSemesterId();

                    //check xem trong một kì có học môn đó 2 lần không (trả nợ ngay trong kì)
                    List<MarksEntity> reLearnInSameSemester = sortedMarks.stream()
                            .filter(q -> q.getSemesterId().getId() == tmpSemester.getId())
                            .collect(Collectors.toList());

                    //nếu trong kì có 2 record, pass, fail --> hs đó pass (không được học cải thiện ngay trong kì)
                    // nếu có 2 fail --> fail; nếu có 1 pass, 1 fail -> pass
                    MarksEntity passMark = reLearnInSameSemester.stream()
                            .filter(q -> q.getStatus().equalsIgnoreCase(Enums.MarkStatus.PASSED.getValue()))
                            .findFirst().orElse(null);

                    if (passMark != null) {
                        finalMarks.add(new MarkCreditTermModel(passMark,
                                subjectCurriculum.getSubjectCredits(),
                                subjectCurriculum.getTermNumber() * 1.0));
                        studentCredits += subjectCurriculum.getSubjectCredits();
                        failFlag = false;
                    } else if (passMark == null && subjectCurriculum.isRequired()) {
                        //loại ra khỏi danh sách có thể tốt nghiệp
                        failFlag = true;
                        break;
                    }
                }

            } //end of subjectCurriculum loop

            //trừ tín chỉ ojt
            studentCredits -= ojtCredits;

            int specializedCredits = 0;
            String tmpStartCourse = startCourse;
            GraduationConditionEntity gc = graduationConditions.stream().filter(q -> q.getProgramId().getId() == student.getProgramId().getId()
                    && q.getStartCourse().equalsIgnoreCase(tmpStartCourse)).findFirst().orElse(null);
            if (gc != null) {
                specializedCredits = gc.getGraduateCredits();
            } else {
                specializedCredits = student.getProgramId().getSpecializedCredits();
            }

            if (!failFlag && studentCredits >= specializedCredits) {
                Collections.sort(finalMarks, new MarkCreditTermModelComparator());
                resultMap.add(new StudentAndMark(finalMarks, student));
            }
            System.out.println(i + " - " + filteredStudents.size());
            i++;
        }
        return resultMap;
    }

    // finda all atudents match Capstone term
    private boolean isCapstone(StudentEntity student, int previousSemesterId) {
        int capstone = Enums.SpecialTerm.CAPSTONETERM.getValue();
        List<DocumentStudentEntity> docs = student.getDocumentStudentEntityList();
        for (DocumentStudentEntity doc : docs) {
            if (doc.getCurriculumId() != null && !doc.getCurriculumId().getProgramId().getName().toLowerCase().contains("pc")) {
                List<SubjectCurriculumEntity> list = doc.getCurriculumId().getSubjectCurriculumEntityList();
                for (SubjectCurriculumEntity s : list) {
                    if (s.getSubjectId().getType() == SubjectTypeEnum.Capstone.getId()) {
                        capstone = s.getTermNumber();
                        break;
                    }
                }
            }
        }

        //convert to double 4 comparison
        double require = capstone * 1.0;
        StudentStatusEntity studentStatus = null;
        List<StudentStatusEntity> statusList = student.getStudentStatusEntityList();

        //get student status ở kì được chọn
        for (StudentStatusEntity status : statusList) {
            if (status.getSemesterId().getId() == previousSemesterId) {
                studentStatus = status;
            }
        }

        double studentTerm;
        // kiểm tra xem kì được chọn của học sinh có record hay không, tránh parse null
        try {
            if (studentStatus != null)
                studentTerm = Double.parseDouble(studentStatus.getTerm());
            else
                studentTerm = -1;

        } catch (Exception ex) {
            studentTerm = -1;
        }

        //studentTerm + 1  == selectedSemester : chọn danh sách sinh viên đi trong hk Spring2018
        // --> sinh viên đó sẽ được đi vào Spring2018 --> lấy trạng thái kì trước đó là Fall2017 để check
        if (studentTerm + 1 >= require) {
            return true;
        } else {
            return false;
        }
    }


}
