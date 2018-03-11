package com.capstone.services;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.jpa.exceptions.NonexistentEntityException;

import java.util.List;

public interface IMarksService {
    MarksEntity getMarkById(int id);
    MarksEntity getMarkByAllFields(int studentId, String subjectCode, int semesterId, double mark, String status, int markComponentId);
    void createMark(MarksEntity entity);
    void createMarks(List<MarksEntity> marksEntities);
    void updateMark(MarksEntity entity) throws Exception;
    void deleteMark(int markId) throws Exception;
    int getTotalExistMarks();
    int getSuccessSavedMark();
    List<MarksEntity> getAllMarks();
    List<MarksEntity> getAllMarksByStudent(int studentId);
    List<MarksEntity> getMarkByConditions(String semesterId, String subjectId, String searchKey);
    List<MarksEntity> getStudentMarksById(int stuId);
    List<MarksEntity> getStudentMarksByStudentIdAndSortBySubjectName(int studentId);
    List<MarksEntity> getStudyingStudents(String subjectId, String[] statuses);
    int countMarksByCourseId(int courseId);
    int countAllMarks();
    List<MarksEntity> getAllMarksByStudentAndSubject(int studentId, String subjectId, String semesterId);
    List<MarksEntity> getMarksByStudentIdAndStatus(int studentId, String status);
    List<MarksEntity> getMarksByStudentIdAndStatusAndSemester(int studentId, String status, List<String> semesters);
    List<MarksEntity> getListMarkToCurrentSemester(List<Integer> semesterIds, String[] statuses);
    List<MarksEntity> getMarkByConditions(int semesterId, List<String> subjects, int studentId);
    List<MarksEntity> getLatestMarksByStudentId(int studentId);

    List<List<String>> getMarksForGraduatedStudent(int programId, int semesterId, int limitTotalCredits, int limitTotalSCredits);

    List<Object[]> getLatestPassFailMarksAndCredits(int studentId);
    List<MarksEntity> findMarksBySemesterId(Integer semesterId);
    List<MarksEntity> findMarksByStudentIdAndSubjectCdAndSemesterId(Integer studentId, String subjectCd, Integer semesterId);
    List<MarksEntity> findMarksByProperties(int semesterId, int studentId);

    List<MarksEntity> getMarksForMarkPage(int studentId);

    // ----- Manager function ----
    List<Object[]> getTotalStudentsGroupBySemesterAndSubject(int semesterId);
    List<List<String>> getAverageSubjectLearnedByStudent(int programId);
    List<MarksEntity> getMarksByStudentIdAndSemester(int studentId, int semesterId);
    List<MarksEntity> getMarksByStudentAndSubjectIdList(int studentId, List<String> subjIdList);
    List<StudentEntity> getOjtStudentsFromSelectedSemesterAndBeforeFromMarks(int semesterId);
    List<StudentEntity> getOjtStudentsBeforeSelectedSemesterFromMarks(int semesterId);
    List<StudentEntity> getCapstoneStudentsBeforeSelectedSemesterFromMarks(int semesterId);
    List<MarksEntity> getStudentMarkBeforeSelectedSemesterFromMarks(int semesterId, int studentId);
    List<MarksEntity> getStudentMarkFromAndBeforeSelectedSemesterFromMarks(int semesterId, int studentId);
    List<MarksEntity> getMarksBySelectedStudentsFromAndBeforeSelectedSemester(int semesterId, List<Integer> studentIds);
}
