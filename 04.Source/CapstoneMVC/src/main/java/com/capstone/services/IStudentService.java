package com.capstone.services;

import com.capstone.entities.StudentEntity;

import java.util.List;

public interface IStudentService {
    int getCurrentLine();
    int getTotalLine();
    void createStudentList(List<StudentEntity> studentEntityList);
    StudentEntity findStudentById(int id);
    StudentEntity findStudentByRollNumber(String rollNumber);
    List<StudentEntity> findAllStudents();
}
