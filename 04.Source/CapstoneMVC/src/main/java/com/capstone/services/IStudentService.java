package com.capstone.services;

import com.capstone.entities.StudentEntity;

import java.util.List;

public interface IStudentService {
    void createStudent(StudentEntity studentEntity);
    void createStudentList(List<StudentEntity> studentEntityList);
}
