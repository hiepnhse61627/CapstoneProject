package com.capstone.services;

import com.capstone.entities.MarksEntity;

import java.util.List;

public interface IMarksService {
    void createMark(MarksEntity entity);
    void createMarks(List<MarksEntity> marksEntities);
    int getTotalExistMarks();
    int getSuccessSavedMark();
    List<MarksEntity> getAllMarks();
    List<MarksEntity> getAllMarksByStudent(int studentId);
    List<MarksEntity> getMarkByConditions(String semesterId, String subjectId, String searchKey);
    List<MarksEntity> getMarkByProgramAndSemester(int programId, int semesterId);
    List<MarksEntity> getStudentMarksById(int stuId);
    List<MarksEntity> getStudyingStudents(String subjectId, String[] statuses);
    int countMarksByCourseId(int courseId);
    List<MarksEntity> getAllMarksByStudentAndSubject(int studentId, String subjectId, String semesterId);
}
