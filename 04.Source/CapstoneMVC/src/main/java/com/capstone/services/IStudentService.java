package com.capstone.services;

import com.capstone.entities.DocumentStudentEntity;
import com.capstone.entities.StudentEntity;

import java.util.List;

public interface IStudentService {
    int getCurrentLine();
    int getTotalLine();
    void createStudentList(List<DocumentStudentEntity> studentEntityList);
    StudentEntity findStudentById(int id);
    StudentEntity findStudentByRollNumber(String rollNumber);
    List<StudentEntity> findStudentsByValue(String value);
    List<StudentEntity> findAllStudents();
    List<StudentEntity> findAllStudentsWithoutCurChange();
    List<StudentEntity> findStudentsByProgramName(String programName);
    void saveStudent(StudentEntity stu) throws Exception;
    StudentEntity cleanDocumentAndOldRollNumber(StudentEntity stu);
    List<StudentEntity> getStudentByDocType(int type);
}
