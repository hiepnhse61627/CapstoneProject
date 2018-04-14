package com.capstone.services;

import com.capstone.entities.StudentStatusEntity;
import com.capstone.jpa.exJpa.ExStudentStatusEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class StudentStatusServiceImpl implements IStudentStatusService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    private ExStudentStatusEntityJpaController studentStatusEntityJpaController = new ExStudentStatusEntityJpaController(emf);

    @Override
    public void createStudentStatus(StudentStatusEntity entity) {
        try {
            studentStatusEntityJpaController.create(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<StudentStatusEntity> getStudentStatusForStudentArrangement(int semesterId, List<String> statusList) {
        return studentStatusEntityJpaController.getStudentStatusForStudentArrangement(semesterId, statusList);
    }

    @Override
    public StudentStatusEntity getStudentStatusBySemesterIdAndStudentId(Integer semesterId, Integer studentId) {
        return studentStatusEntityJpaController.getStudentStatusBySemesterIdAndStudentId(semesterId, studentId);
    }

    @Override
    public void updateStudentStatus(StudentStatusEntity entity) {
        try {
            studentStatusEntityJpaController.edit(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<StudentStatusEntity> getStudentStatusBySemesterId(Integer semesterId) {
        return studentStatusEntityJpaController.getStudentStatusBySemesterId(semesterId);
    }

    @Override
    public List<StudentStatusEntity> getStudentStatusesByStudentId(int studentId) {
        return studentStatusEntityJpaController.getStudentStatusesByStudentId(studentId);
    }
}
