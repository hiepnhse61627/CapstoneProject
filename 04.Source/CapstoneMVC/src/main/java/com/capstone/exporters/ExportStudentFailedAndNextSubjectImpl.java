package com.capstone.exporters;

import com.capstone.entities.*;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestParam;

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
//        StudentEntity stu = studentService.findStudentById(Integer.parseInt(params.get("studentId")));
//        students.add(stu);
        writeDataToTable(streamingWorkbook, streamingSheet, students.subList(0, 40));

        streamingWorkbook.write(os);
    }

    private void writeDataToTable(SXSSFWorkbook workbook, SXSSFSheet spreadsheet, List<StudentEntity> students) {
        // start data table row
        if (students != null && !students.isEmpty()) {
            // style
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            int rowIndex = 6;
            int count = 1;
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
                        failedSubject += mark.getSubjectMarkComponentId().getSubjectId().getId();
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
                List<List<String>> nextSubjects = processNextSubject(student);
                if (nextSubjects != null && !nextSubjects.isEmpty()) {
                    String next = "";
                    for (List<String> subjects : nextSubjects) {
                        String subjectId = subjects.get(0);
                        next += subjectId;
                        next += ",";
                    }

                    if (next.equals("")) {
                        next = "N/A";
                    }

                    Cell nextSubjectCell = row.createCell(3);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue(next);
                } else {
                    Cell nextSubjectCell = row.createCell(3);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue("N/A");
                }

                // current subject
                List<List<String>> currentSubject = processCurrentSubject(student.getId());
                if (currentSubject != null && !currentSubject.isEmpty()) {
                    String next = "";
                    for (List<String> subjects : currentSubject) {
                        String subjectId = subjects.get(0);
                        next += subjectId;
                        next += ",";
                    }

                    if (next.equals("")) {
                        next = "N/A";
                    }

                    Cell nextSubjectCell = row.createCell(4);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue(next);
                } else {
                    Cell nextSubjectCell = row.createCell(4);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue("N/A");
                }

                // current subject
                List<List<String>> slowSubject = processNotStart(student.getId());
                if (slowSubject != null && !slowSubject.isEmpty()) {
                    String next = "";
                    for (List<String> subjects : slowSubject) {
                        String subjectId = subjects.get(0);
                        next += subjectId;
                        next += ",";
                    }

                    if (next.equals("")) {
                        next = "N/A";
                    }

                    Cell nextSubjectCell = row.createCell(5);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue(next);
                } else {
                    Cell nextSubjectCell = row.createCell(5);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue("N/A");
                }

                // current subject
                List<List<String>> suggestSubjects = processSuggestion(student.getId());
                if (suggestSubjects != null && !suggestSubjects.isEmpty()) {
                    String next = "";
                    for (List<String> subjects : suggestSubjects) {
                        String subjectId = subjects.get(0);
                        next += subjectId;
                        next += ",";
                    }

                    if (next.equals("")) {
                        next = "N/A";
                    }

                    Cell nextSubjectCell = row.createCell(6);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue(next);
                } else {
                    Cell nextSubjectCell = row.createCell(6);
                    nextSubjectCell.setCellStyle(cellStyle);
                    nextSubjectCell.setCellValue("N/A");
                }

                ExportStatusReport.StatusExport = "Exporting " + (count++) + " of " + students.size();
                rowIndex++;
            }
        }
    }

    private List<MarksEntity> processFailedSubject(StudentEntity student) {
        List<MarksEntity> list = marksService.getAllMarksByStudentAndSubject(student.getId(), "0", "0");

        List<MarksEntity> newlist = Ultilities.FilterStudentsOnlyPassAndFail(list);
        List<MarksEntity> resultList = Ultilities.FilterListFailStudent(newlist);

        //remove studying marks from fail list
        List<MarksEntity> studyingList = marksService.getMarksByStudentIdAndStatus(student.getId(), "studying");
        Iterator<MarksEntity> iterator = resultList.iterator();
        while(iterator.hasNext()) {
            MarksEntity current = iterator.next();
            if (studyingList.stream().anyMatch(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(current.getSubjectMarkComponentId().getSubjectId().getId()))) {
                iterator.remove();
            }
        }

        return resultList;
    }

    private List<List<String>> processNextSubject(StudentEntity student) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
        EntityManager em = emf.createEntityManager();

        String queryStr = "SELECT sc FROM DocumentStudentEntity ds, SubjectCurriculumEntity sc" +
                " WHERE ds.studentId.id = :studentId AND ds.createdDate =" +
                " (SELECT MAX(ds1.createdDate) FROM DocumentStudentEntity ds1 WHERE ds1.studentId.id = :studentId) " +
                " AND ds.curriculumId.id = sc.curriculumId.id" +
                " AND sc.termNumber = :term";
        TypedQuery<SubjectCurriculumEntity> query = em.createQuery(queryStr, SubjectCurriculumEntity.class);
        query.setParameter("studentId", student.getId());
        query.setParameter("term", student.getTerm() + 1);

        List<SubjectCurriculumEntity> list = query.getResultList();

        // Check students score if exist remove
        if (!list.isEmpty()) {
            List<String> curriculumSubjects = new ArrayList<>();
            list.forEach(c -> {
                if (!curriculumSubjects.contains(c.getSubjectId().getId())) {
                    curriculumSubjects.add(c.getSubjectId().getId());
                }
            });
            TypedQuery<MarksEntity> query2 = em.createQuery("SELECT a FROM MarksEntity a WHERE a.studentId.id = :id AND a.subjectMarkComponentId.subjectId.id IN :list", MarksEntity.class);
            query2.setParameter("id", student.getId());
            query2.setParameter("list", curriculumSubjects);
            List<MarksEntity> existList = Ultilities.FilterStudentsOnlyPassAndFail(query2.getResultList());
            Iterator<SubjectCurriculumEntity> iterator = list.iterator();
            while(iterator.hasNext()) {
                SubjectCurriculumEntity cur = iterator.next();
                if (existList.stream().anyMatch(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(cur.getSubjectId().getId()))) {
                    iterator.remove();
                }
            }
        }

        List<List<String>> result = new ArrayList<>();
        for (SubjectCurriculumEntity sc : list) {
            List<String> row = new ArrayList<>();
            row.add(sc.getSubjectId().getId());
            row.add(sc.getSubjectId().getName());

            result.add(row);
        }

        return result;
    }

    public List<List<String>> processCurrentSubject(int stuId) {
        List<List<String>> displayList = new ArrayList<>();

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            List<MarksEntity> list = marksService.getMarksByStudentIdAndStatus(stuId, "studying");

            // Check students score if exist remove
            if (!list.isEmpty()) {
                List<String> curriculumSubjects = new ArrayList<>();
                list.forEach(c -> {
                    if (!curriculumSubjects.contains(c.getSubjectMarkComponentId().getSubjectId().getId())) {
                        curriculumSubjects.add(c.getSubjectMarkComponentId().getSubjectId().getId());
                    }
                });
                TypedQuery<MarksEntity> query2 = em.createQuery("SELECT a FROM MarksEntity a WHERE a.studentId.id = :id AND a.subjectMarkComponentId.subjectId.id IN :list", MarksEntity.class);
                query2.setParameter("id", stuId);
                query2.setParameter("list", curriculumSubjects);
                List<MarksEntity> existList = Ultilities.FilterStudentsOnlyPassAndFail(query2.getResultList());
                Iterator<MarksEntity> iterator = list.iterator();
                while(iterator.hasNext()) {
                    MarksEntity cur = iterator.next();
                    if (existList.stream().anyMatch(c -> c.getSubjectMarkComponentId().getSubjectId().getId().equals(cur.getSubjectMarkComponentId().getSubjectId().getId()))) {
                        iterator.remove();
                    }
                }
            }

            for (MarksEntity sc : list) {
                List<String> row = new ArrayList<>();
                row.add(sc.getSubjectMarkComponentId().getSubjectId().getId());
                row.add(sc.getSubjectMarkComponentId().getSubjectId().getName());
                row.add(sc.getStatus());

                displayList.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return displayList;
    }

    public List<List<String>> processNotStart(int stuId) {
        IMarksService marksService = new MarksServiceImpl();
        List<List<String>> displayList = new ArrayList<>();

        try {
            List<MarksEntity> list = marksService.getMarksByStudentIdAndStatus(stuId, "start");

            for (MarksEntity sc : list) {
                List<String> row = new ArrayList<>();
                row.add(sc.getSubjectMarkComponentId().getSubjectId().getId());
                row.add(sc.getSubjectMarkComponentId().getSubjectId().getName());
                row.add(sc.getStatus());

                displayList.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return displayList;
    }

    public List<List<String>> processSuggestion(int stuId) {
        IMarksService marksService = new MarksServiceImpl();

        IStudentService studentService = new StudentServiceImpl();

        StudentEntity student = studentService.findStudentById(stuId);

        List<List<String>> parent = new ArrayList<>();
        try {
            /*-----------------------------------Fail Course--------------------------------------------------*/
            List<MarksEntity> list = marksService.getAllMarksByStudentAndSubject(stuId, "0", "0");
            List<MarksEntity> newlist = Ultilities.FilterStudentsOnlyPassAndFail(list);
            List<MarksEntity> resultList = Ultilities.FilterListFailStudent(newlist);
            List<SubjectEntity> failSubjects = new ArrayList<>();
            resultList.forEach(c -> {
                if (!failSubjects.contains(c.getSubjectMarkComponentId().getSubjectId())) {
                    failSubjects.add(c.getSubjectMarkComponentId().getSubjectId());
                }
            });

            /*-------------------------------Get Next Course------------------------------------------------*/
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
            EntityManager em = emf.createEntityManager();

            String queryStr = "SELECT sc FROM DocumentStudentEntity ds, SubjectCurriculumEntity sc" +
                    " WHERE ds.studentId.id = :studentId AND ds.createdDate =" +
                    " (SELECT MAX(ds1.createdDate) FROM DocumentStudentEntity ds1 WHERE ds1.studentId.id = :studentId) " +
                    " AND ds.curriculumId.id = sc.curriculumId.id" +
                    " AND sc.termNumber = :term";
            TypedQuery<SubjectCurriculumEntity> query = em.createQuery(queryStr, SubjectCurriculumEntity.class);
            query.setParameter("studentId", stuId);
            query.setParameter("term", student.getTerm() + 1);

            List<SubjectCurriculumEntity> listNextCurri = query.getResultList();
            List<SubjectEntity> nextSubjects = new ArrayList<>();
            listNextCurri.forEach(c -> {
                if (!nextSubjects.contains(c.getSubjectId())) {
                    nextSubjects.add(c.getSubjectId());
                }
            });

            /*-------------------------------Chậm tiến độ------------------------------------------------*/
            List<MarksEntity> slowList = marksService.getMarksByStudentIdAndStatus(stuId, "start");
            List<SubjectEntity> slowSubjects = new ArrayList<>();
            slowList.forEach(c -> {
                if (!slowSubjects.contains(c.getSubjectMarkComponentId().getSubjectId())) {
                    slowSubjects.add(c.getSubjectMarkComponentId().getSubjectId());
                }
            });

            // gộp chậm tiến độ và fail và sort theo semester
            List<SubjectEntity> combine = new ArrayList<>();
            List<SubjectEntity> finalCombine = combine;
            failSubjects.forEach(c -> {
                if (!finalCombine.contains(c)) {
                    finalCombine.add(c);
                }
            });
            slowSubjects.forEach(c -> {
                if (!finalCombine.contains(c)) {
                    finalCombine.add(c);
                }
            });
            combine = finalCombine;
            List<SubjectEntity> sortiedCombine = Ultilities.SortSubjectsByOrdering(combine, stuId);

            /*-----------------------------get Subject List--------------------------------------------------------*/
            if (sortiedCombine.size() >= 5) {
                sortiedCombine = sortiedCombine.stream().limit(7).collect(Collectors.toList());

                if (!sortiedCombine.isEmpty()) {
                    sortiedCombine.forEach(m -> {
                        ArrayList<String> tmp = new ArrayList<>();
                        tmp.add(m.getId());
                        tmp.add(m.getName());
                        parent.add(tmp);

                    });
                }
            } else if (sortiedCombine.size() > 0) {
                for (SubjectEntity subject : sortiedCombine) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(subject.getId());
                    tmp.add(subject.getName());
                    parent.add(tmp);
                }
                if (nextSubjects.size() > (7 - parent.size())) {
                    for (int i = 0; i < (7 - parent.size()); i++) {
                        ArrayList<String> tmp = new ArrayList<>();
                        tmp.add(nextSubjects.get(i).getId());
                        tmp.add(nextSubjects.get(i).getName());
                        parent.add(tmp);
                    }
                } else {
                    for (SubjectEntity nextSubject : nextSubjects) {
                        ArrayList<String> tmp = new ArrayList<>();
                        tmp.add(nextSubject.getId());
                        tmp.add(nextSubject.getName());
                        parent.add(tmp);
                    }
                }
            } else {
                for (SubjectEntity nextSubject : nextSubjects) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(nextSubject.getId());
                    tmp.add(nextSubject.getName());
                    parent.add(tmp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return parent;
    }
}
