package com.capstone.services;

import com.capstone.entities.StudentStatusEntity;

import java.util.List;

public interface IStudentStatusService {
    void createStudentStatus(StudentStatusEntity entity);
    List<StudentStatusEntity> getStudentStatusForStudentArrangement(int semesterId, List<String> statusList);
    StudentStatusEntity getStudentStatusBySemesterIdAndStudentId(Integer semesterId, Integer studentId);
    void updateStudentStatus(StudentStatusEntity entity);
    public List<StudentStatusEntity> getStudentStatusBySemesterId(Integer semesterId);
    List<StudentStatusEntity> getStudentStatusesByStudentId(int studentId);
}
