package com.capstone.services;

import com.capstone.entities.MarksEntity;
import com.capstone.jpa.exJpa.ExMarksEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class MarksServiceImpl implements IMarksService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExMarksEntityJpaController marksEntityJpaController = new ExMarksEntityJpaController(emf);

    @Override
    public MarksEntity getMarkById(int id) {
        return marksEntityJpaController.findMarksEntity(id);
    }

    @Override
    public MarksEntity getMarkByAllFields(int studentId, String subjectCode, int semesterId, double mark, String status, int markComponentId) {
        return marksEntityJpaController.getMarkByAllFields(studentId, subjectCode, semesterId, mark, status, markComponentId);
    }

    @Override
    public void createMark(MarksEntity entity) {
        try {
            marksEntityJpaController.create(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createMarks(List<MarksEntity> marksEntities) {
        try {
            marksEntityJpaController.createMarks(marksEntities);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateMark(MarksEntity entity) throws Exception {
        marksEntityJpaController.edit(entity);
    }

    @Override
    public int getTotalExistMarks() {
        return marksEntityJpaController.getTotalExistMarks();
    }

    @Override
    public int getSuccessSavedMark() {
        return marksEntityJpaController.getSuccessSavedMark();
    }

    @Override
    public List<MarksEntity> getAllMarks() {
        return marksEntityJpaController.findMarksEntityEntities();
    }

    @Override
    public List<MarksEntity> getAllMarksByStudent(int studentId) {
        return  marksEntityJpaController.getAllMarksByStudent(studentId);
    }

    @Override
    public List<MarksEntity> getMarkByConditions(String semesterId, String subjectId, String searchKey) {
        return marksEntityJpaController.getMarksByConditions(semesterId, subjectId, searchKey);
    }

    @Override
    public List<List<String>> getMarksForGraduatedStudent(int programId, int semesterId, int limitTotalCredits, int limitTotalSCredits) {
        return marksEntityJpaController.getMarksForGraduatedStudent(programId, semesterId, limitTotalCredits, limitTotalSCredits);
    }

    @Override
    public List<Object[]> getLatestPassFailMarksAndCredits(int studentId) {
        return marksEntityJpaController.getLatestPassFailMarksAndCredits(studentId);
    }

    @Override
    public List<MarksEntity> findMarksBySemesterId(Integer semesterId) {
        return marksEntityJpaController.findMarksBySemesterId(semesterId);
    }

    @Override
    public List<MarksEntity> findMarksByStudentIdAndSubjectCdAndSemesterId(Integer studentId, String subjectCd, Integer semesterId) {
        return marksEntityJpaController.findMarksByStudentIdAndSubjectCdAndSemesterId(studentId, subjectCd, semesterId);
    }

    @Override
    public List<MarksEntity> getMarksForMarkPage(int studentId) {
        return marksEntityJpaController.getMarksForMarkPage(studentId);
    }

    @Override
    public List<MarksEntity> getStudentMarksById(int stuId) {
        return marksEntityJpaController.getAllMarksByStudent(stuId);
    }

    @Override
    public List<MarksEntity> getStudentMarksByStudentIdAndSortBySubjectName(int studentId) {
        return marksEntityJpaController.getStudentMarksByStudentIdAndSortBySubjectName(studentId);
    }

    @Override
    public List<MarksEntity> getStudyingStudents(String subjectId, String[] statuses) {
        return marksEntityJpaController.getStudyingStudents(subjectId, statuses);
    }

    @Override
    public int countMarksByCourseId(int courseId) {
        return marksEntityJpaController.countMarksByCourseId(courseId);
    }

    @Override
    public int countAllMarks() {
        return marksEntityJpaController.countAllMarks();
    }

    @Override
    public List<MarksEntity> getAllMarksByStudentAndSubject(int studentId, String subjectId, String semesterId) {
        return marksEntityJpaController.getAllMarksByStudentAndSubject(studentId, subjectId, semesterId);
    }

    @Override
    public List<MarksEntity> getMarksByStudentIdAndStatus(int studentId, String status) {
        return marksEntityJpaController.getMarksByStudentIdAndStatus(studentId, status);
    }

    @Override
    public List<MarksEntity> getMarksByStudentIdAndStatusAndSemester(int studentId, String status, List<String> semesters) {
        return marksEntityJpaController.getMarksByStudentIdAndStatusAndSemester(studentId, status, semesters);
    }

    @Override
    public List<MarksEntity> getListMarkToCurrentSemester(List<Integer> semesterIds, String[] statuses) {
        return marksEntityJpaController.getListMarkToCurrentSemester(semesterIds, statuses);
    }

    @Override
    public List<MarksEntity> getMarkByConditions(int semesterId, List<String> subjects, int studentId) {
        return marksEntityJpaController.getMarksByConditions(semesterId, subjects, studentId);
    }

    @Override
    public List<MarksEntity> getLatestMarksByStudentId(int studentId) {
        return marksEntityJpaController.getLatestMarksByStudentId(studentId);
    }


    // ------------ Manager function -------------

    @Override
    public List<Object[]> getTotalStudentsGroupBySemesterAndSubject(int semesterId) {
        return marksEntityJpaController.getTotalStudentsGroupBySemesterAndSubject(semesterId);
    }

    @Override
    public List<List<String>> getAverageSubjectLearnedByStudent(int programId) {
         return marksEntityJpaController.getAverageSubjectLearnedByStudent(programId);
    }
}
