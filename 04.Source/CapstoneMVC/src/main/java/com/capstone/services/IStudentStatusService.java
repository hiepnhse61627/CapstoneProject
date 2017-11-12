package com.capstone.services;

import com.capstone.entities.StudentStatusEntity;

import java.util.List;

public interface IStudentStatusService {
    void createStudentStatus(StudentStatusEntity entity);
    List<StudentStatusEntity> getStudentStatusForStudentArrangement(int semesterId, List<String> statusList);
}
