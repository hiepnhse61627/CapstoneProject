package com.capstone.services;

import com.capstone.entities.fapEntities.StudentAvgMarks;
import com.capstone.entities.fapEntities.StudentStudyingMarks;

import java.util.List;

public interface IUltilities2Service {
    List<StudentStudyingMarks> getFAPStudyingMarkBySemester(String semesterName);
    List<StudentAvgMarks> getFAPMarksByStudentRollNumber(String studentRollNumber);
    List<StudentAvgMarks> getFAPMarksBySemester(String semesterName);
}
