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
        studentStatusEntityJpaController.create(entity);
    }

    @Override
    public List<StudentStatusEntity> getStudentStatusForStudentArrangement(int semesterId, List<String> statusList) {
        return studentStatusEntityJpaController.getStudentStatusForStudentArrangement(semesterId, statusList);
    }
}