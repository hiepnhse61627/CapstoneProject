package com.capstone.services;

import com.capstone.entities.DocumentStudentEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.jpa.exJpa.ExStudentEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class StudentServiceImpl implements IStudentService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExStudentEntityJpaController studentEntityJpaController = new ExStudentEntityJpaController(emf);

    @Override
    public void createStudentList(List<DocumentStudentEntity> studentEntityList) {
        studentEntityJpaController.createStudentList(studentEntityList);
    }

    @Override
    public StudentEntity findStudentById(int id) {
        return studentEntityJpaController.findStudentEntity(id);
    }

    @Override
    public StudentEntity findStudentByRollNumber(String rollNumber) {
        return studentEntityJpaController.findStudentByRollNumber(rollNumber);
    }

    @Override
    public List<StudentEntity> findStudentsByValue(String value) {
        return studentEntityJpaController.findStudentsByValue(value);
    }

    public List<StudentEntity> findAllStudents() {
        return studentEntityJpaController.findStudentEntityEntities();
    }

    @Override
    public List<StudentEntity> findAllStudentsWithoutCurChange() {
        return studentEntityJpaController.findStudentByIsActivated();
    }

    @Override
    public List<StudentEntity> findStudentsByProgramName(String programName) {
        return studentEntityJpaController.findStudentByProgramName(programName);
    }

    @Override
    public void saveStudent(StudentEntity stu) throws Exception {
        studentEntityJpaController.saveStudent(stu);
    }

    @Override
    public int getCurrentLine() {
        return studentEntityJpaController.getCurrentLine();
    }

    @Override
    public int getTotalLine() {
        return studentEntityJpaController.getTotalLine();
    }
}
