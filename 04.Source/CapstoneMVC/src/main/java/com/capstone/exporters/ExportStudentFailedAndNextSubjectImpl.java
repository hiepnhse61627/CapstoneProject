package com.capstone.exporters;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.PrequisiteEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.persistence.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ExportStudentFailedAndNextSubjectImpl implements IExportObject {

    private IStudentService studentService = new StudentServiceImpl();
    private IMarksService marksService = new MarksServiceImpl();
    private ISubjectService subjectService = new SubjectServiceImpl();

    private String EXCEL_TEMPL = "/template/DSSV_HL_MTT.xlsx";

    @Override
    public String getFileName() {
        return "DSSV_HL_MTT.xlsx";
    }

    @Override
    public void writeData(OutputStream os, Map<String, String> params) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(EXCEL_TEMPL);

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        is.close();
        // change to streaming working
        SXSSFWorkbook streamingWorkbook = new SXSSFWorkbook(xssfWorkbook);
        SXSSFSheet streamingSheet = streamingWorkbook.getSheetAt(0);
        streamingSheet.setRandomAccessWindowSize(100);
        // student list
        List<StudentEntity> students = studentService.findAllStudents();
        writeDataToTable(streamingWorkbook, streamingSheet, students);

        streamingWorkbook.write(os);
    }

    private void writeDataToTable(SXSSFWorkbook workbook, SXSSFSheet spreadsheet, List<StudentEntity> students) {
        // start data table row
        if (students != null && !students.isEmpty()) {
            // style
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
            cellStyle.setAlignment(XSSFCellStyle.ALIGN_LEFT);
            cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);

            int rowIndex = 6;
            for (StudentEntity student : students) {
                Row row = spreadsheet.createRow(rowIndex);
                Cell rollNumberCell = row.createCell(0);
                rollNumberCell.setCellStyle(cellStyle);
                rollNumberCell.setCellValue(student.getRollNumber());

                Cell studentNameCell = row.createCell(1);
                studentNameCell.setCellStyle(cellStyle);
                studentNameCell.setCellValue(student.getFullName());
                // failed subject
                List<MarksEntity> marks = processFailedSubject(student);
                if (marks != null && !marks.isEmpty()) {
                    String failedSubject = "";
                    for (MarksEntity mark : marks) {
                        failedSubject += mark.getSubjectId().getSubjectId();
                        failedSubject += ",";
                    }
                    failedSubject = Character.toString(failedSubject.charAt(failedSubject.length() - 1)).equals(",") ? failedSubject.substring(0, failedSubject.length() - 1) : failedSubject;
                    Cell failedSubjectCell = row.createCell(2);
                    failedSubjectCell.setCellStyle(cellStyle);
                    failedSubjectCell.setCellValue(failedSubject);
                } else {
                    Cell failedSubjectCell = row.createCell(2);
                    failedSubjectCell.setCellStyle(cellStyle);
                    failedSubjectCell.setCellValue("N/A");
                }
                // next subject
                ArrayList<ArrayList<String>> nextSubjects = processNextSubject(student);
                if (nextSubjects != null && !nextSubjects.isEmpty()) {
                    String next = "";
                    String notQualified = "";
                    for (List<String> subjects : nextSubjects) {
                        String subjectId = subjects.get(0);
                        String subjectType = subjects.get(2);
                        if (subjectType.equals("0")) {
                            next += subjectId;
                            next += ",";
                        } else if (subjectType.equals("1")) {
                            notQualified += subjectId;
                            notQualified += ",";
                        }
                    }

                    if (next.equals("")) {
                        next = "N/A";
                    }

                    if (notQualified.equals("")) {
                        notQualified = "N/A";
                    }

                    next = Character.toString(next.charAt(next.length() - 1)).equals(",") ? next.substring(0, next.length() - 1) : next;
                    notQualified = Character.toString(notQualified.charAt(notQualified.length() - 1)).equals(",") ? notQualified.substring(0, notQualified.length() - 1) : notQualified;

                    Cell nextSubjectCell = row.createCell(3);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue(next);

                    Cell notQualifiedNextSubjectCell = row.createCell(4);
                    notQualifiedNextSubjectCell.setCellStyle(cellStyle);
                    notQualifiedNextSubjectCell.setCellValue(notQualified);
                } else {
                    Cell nextSubjectCell = row.createCell(3);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue("N/A");

                    Cell notQualifiedNextSubjectCell = row.createCell(4);
                    notQualifiedNextSubjectCell.setCellStyle(cellStyle);
                    notQualifiedNextSubjectCell.setCellValue("N/A");
                }
                rowIndex++;
            }
        }
    }

    private List<MarksEntity> processFailedSubject(StudentEntity student) {
        List<MarksEntity> marks = marksService.getStudentMarksById(student.getId());
        // Init students passed and failed
        List<MarksEntity> listPassed = marks.stream().filter(p -> p.getStatus().contains("Passed") || p.getStatus().contains("Exempt")).collect(Collectors.toList());
        List<MarksEntity> listFailed = marks.stream().filter(f -> !f.getStatus().contains("Passed") || !f.getStatus().contains("Exempt")).collect(Collectors.toList());
        // compared list
        List<MarksEntity> comparedList = new ArrayList<>();
        // make comparator
        Comparator<MarksEntity> comparator = new Comparator<MarksEntity>() {
            @Override
            public int compare(MarksEntity o1, MarksEntity o2) {
                return new CompareToBuilder()
                        .append(o1.getSubjectId() == null ? "" : o1.getSubjectId().getSubjectId().toUpperCase(), o2.getSubjectId() == null ? "" : o2.getSubjectId().getSubjectId().toUpperCase())
                        .append(o1.getStudentId().getRollNumber().toUpperCase(), o2.getStudentId().getRollNumber().toUpperCase())
                        .toComparison();
            }
        };
        Collections.sort(listPassed, comparator);
        // start compare failed list to passed list
        for (int i = 0; i < listFailed.size(); i++) {
            MarksEntity keySearch = listFailed.get(i);
            int index = Collections.binarySearch(listPassed, keySearch, comparator);
            if (index < 0) {
                comparedList.add(keySearch);
            }
        }
        // result list
        List<MarksEntity> resultList = new ArrayList<>();
        // remove duplicate
        for (MarksEntity marksEntity : comparedList) {
            if (marksEntity.getSubjectId() != null && !resultList.stream().anyMatch(r -> r.getSubjectId().getSubjectId().toUpperCase().equals(marksEntity.getSubjectId().getSubjectId().toUpperCase())
                    && r.getStudentId().getRollNumber().toUpperCase().equals(marksEntity.getStudentId().getRollNumber().toUpperCase()))) {
                resultList.add(marksEntity);
            }
        }

        return resultList;
    }

    private ArrayList<ArrayList<String>> processNextSubject(StudentEntity student) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = emf.createEntityManager();

        List<SubjectEntity> subjects = new ArrayList<>();
        int studentId = student.getId();
        String[] currentTerm = {"0"};
        int nextTermNumber = 0;

        String sqlString = "SELECT distinct Curriculum_Mapping.term FROM Student " +
                "INNER JOIN Marks on student.ID = Marks.StudentId and Student.ID =" + studentId +
                " INNER JOIN Curriculum_Mapping on Marks.SubjectId = Curriculum_Mapping.SubId " +
                "order by Curriculum_Mapping.Term desc";
        Query query = em.createNativeQuery(sqlString);
        List<String> list = query.getResultList();
        list.stream().findFirst().ifPresent(c -> currentTerm[0] = list.stream().findFirst().get());

        if (!currentTerm[0].equals("0")) {
            int currentTermNumber = Integer.parseInt(currentTerm[0].replaceAll("[^0-9]", ""));
            nextTermNumber = currentTermNumber + 1;
        }

        query = em.createQuery("SELECT s FROM CurriculumMappingEntity c, SubjectEntity s " +
                                 "WHERE c.term LIKE '%" + nextTermNumber + "' AND c.subjectEntity.id = s.id", SubjectEntity.class);
        subjects = (List<SubjectEntity>) query.getResultList();

        ArrayList<ArrayList<String>> parent = new ArrayList<>();
        if (!subjects.isEmpty()) {
            subjects.forEach(m -> {
                ArrayList<String> tmp = new ArrayList<>();
                tmp.add(m.getId());
                tmp.add(m.getName());

                SubjectEntity cur = subjectService.findSubjectById(m.getId());
                boolean exist = false;
//                for (PrequisiteEntity s : cur.getPrequisiteEntityList()) {
//                    TypedQuery<MarksEntity> q = em.createQuery("SELECT c FROM MarksEntity c WHERE c.studentId.id = :id AND c.subjectId.subjectId = :sub", MarksEntity.class);
//                    List<MarksEntity> l = q.setParameter("sub", s.getId()).setParameter("id", studentId).getResultList();
//                    exist = Ultilities.CheckStudentSubjectFailOrPass(l);
//                }

                if (exist) {
                    tmp.add("1");
                } else {
                    tmp.add("0");
                }

                parent.add(tmp);
            });
        }
        return parent;
    }
}
