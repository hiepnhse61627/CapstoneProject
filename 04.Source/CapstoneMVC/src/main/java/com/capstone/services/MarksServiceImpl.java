package com.capstone.services;

import com.capstone.entities.MarksEntity;
import com.capstone.jpa.exJpa.ExMarksEntityJpaController;
import com.capstone.jpa.exJpa.ExRealSemesterEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class MarksServiceImpl implements IMarksService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExMarksEntityJpaController marksEntityJpaController = new ExMarksEntityJpaController(emf);

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
    public List<MarksEntity> getMarkByProgramAndSemester(int programId, int semesterId) {
        return marksEntityJpaController.getMarkByProgramAndSemester(programId, semesterId);
    }

    @Override
    public List<MarksEntity> getStudentMarksById(int stuId) {
        return marksEntityJpaController.getAllMarksByStudent(stuId);
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
    public List<MarksEntity> getAllMarksByStudentAndSubject(int studentId, String subjectId, String semesterId) {
        return marksEntityJpaController.getAllMarksByStudentAndSubject(studentId, subjectId, semesterId);
    }
}
