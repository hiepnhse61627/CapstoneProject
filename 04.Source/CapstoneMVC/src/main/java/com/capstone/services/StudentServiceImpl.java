package com.capstone.services;

import com.capstone.entities.StudentEntity;
import com.capstone.jpa.exJpa.ExStudentEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class StudentServiceImpl implements IStudentService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExStudentEntityJpaController studentEntityJpaController = new ExStudentEntityJpaController(emf);

    public void createStudent(StudentEntity studentEntity) {
        try {
            studentEntityJpaController.create(studentEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createStudentList(List<StudentEntity> studentEntityList) {
        studentEntityJpaController.createStudentList(studentEntityList);
    }

    @Override
    public StudentEntity findStudentByRollNumber(String rollNumber) {
        return studentEntityJpaController.findStudentByRollNumber(rollNumber);
    }
}
