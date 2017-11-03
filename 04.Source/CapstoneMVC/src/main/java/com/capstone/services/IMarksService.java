package com.capstone.services;

import com.capstone.entities.MarksEntity;

import java.util.List;

public interface IMarksService {
    MarksEntity getMarkById(int id);
    void createMark(MarksEntity entity);
    void createMarks(List<MarksEntity> marksEntities);
    void updateMark(MarksEntity entity) throws Exception;
    int getTotalExistMarks();
    int getSuccessSavedMark();
    List<MarksEntity> getAllMarks();
    List<MarksEntity> getAllMarksByStudent(int studentId);
    List<MarksEntity> getMarkByConditions(String semesterId, String subjectId, String searchKey);
    List<MarksEntity> getMarkByProgramAndSemester(int programId, int semesterId);
    List<MarksEntity> getStudentMarksById(int stuId);
    List<MarksEntity> getStudentMarksByStudentIdAndSortBySubjectName(int studentId);
    List<MarksEntity> getStudyingStudents(String subjectId, String[] statuses);
    int countMarksByCourseId(int courseId);
    int countAllMarks();
    List<MarksEntity> getAllMarksByStudentAndSubject(int studentId, String subjectId, String semesterId);
    List<MarksEntity> getMarksByStudentIdAndStatus(int studentId, String status);
    List<MarksEntity> getListMarkToCurrentSemester(List<Integer> semesterIds, String[] statuses);

    // ----- Manager function ----
    List<Object[]> getTotalStudentsGroupBySemesterAndSubject(int semesterId);
    void getAverageSubjectLearnedByStudent(int programId);
}
