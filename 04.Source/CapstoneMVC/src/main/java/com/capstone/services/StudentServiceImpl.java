package com.capstone.services;

import com.capstone.entities.DocumentStudentEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.entities.StudentStatusEntity;
import com.capstone.jpa.exJpa.ExStudentEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;

public class StudentServiceImpl implements IStudentService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExStudentEntityJpaController studentEntityJpaController = new ExStudentEntityJpaController(emf);

    @Override
    public void createStudentList(List<StudentEntity> studentEntityList) {
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
    public StudentEntity findStudentByEmail(String email) {
        return studentEntityJpaController.findStudentByEmail(email);
    }

    @Override
    public List<StudentEntity> findStudentsByFullNameOrRollNumber(String searchValue) {
        return studentEntityJpaController.findStudentsByFullNameOrRollNumber(searchValue);
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
    public StudentEntity cleanDocumentAndOldRollNumber(StudentEntity stu) {
        return studentEntityJpaController.cleanDocumentAndOldRollNumber(stu);
    }

    @Override
    public List<StudentEntity> getStudentByDocType(int type) {
        return studentEntityJpaController.getStudentByDocType(type);
    }

    @Override
    public List<StudentEntity> getStudentByProgram(int programId) {
        return studentEntityJpaController.getStudentByProgram(programId);
    }

    @Override
    public StudentEntity createStudent(StudentEntity studentEntity) {
        return studentEntityJpaController.createStudent(studentEntity);
    }

    @Override
    public List<StudentEntity> findStudentByProgramId(Integer programId) {
        return studentEntityJpaController.findStudentByProgramId(programId);
    }

    @Override
    public void updateStudent(StudentEntity entity) {
        try {
            studentEntityJpaController.edit(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<StudentEntity> getStudentFailedMoreThanRequiredCredits(Integer credits) {
        List<StudentEntity> students = findAllStudents();
        List<StudentEntity> onGoingStudents = new ArrayList<>();
        List<StudentEntity> resultList = new ArrayList<>();

        if (students != null && !students.isEmpty()) {
            for (StudentEntity student : students) {
                List<StudentStatusEntity> studentStatusList = student.getStudentStatusEntityList();
                if (studentStatusList != null && !studentStatusList.isEmpty()) {
                    for (StudentStatusEntity studentStatus : studentStatusList) {
                        if (!studentStatus.getStatus().equals("G")) {
                            onGoingStudents.add(student);
                            break;
                        }
                    }
                }
            }
        }

        if (!onGoingStudents.isEmpty()) {
            for (StudentEntity student : onGoingStudents) {
                Integer passFailCredits = student.getPassFailCredits();
                Integer passCredits = student.getPassCredits();
                if (passFailCredits - passCredits >= credits) {
                    resultList.add(student);
                }
            }
        }

        return resultList;
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
