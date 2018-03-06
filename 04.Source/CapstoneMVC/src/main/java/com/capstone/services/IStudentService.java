package com.capstone.services;

import com.capstone.entities.DocumentStudentEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.models.StudentAndSubject;
import com.capstone.models.StudentFailedSubject;
import com.capstone.models.SubjectModel;

import java.util.List;
import java.util.Map;

public interface IStudentService {
    int getCurrentLine();
    int getTotalLine();
    void createStudentList(List<StudentEntity> studentEntityList);
    StudentEntity findStudentById(int id);
    StudentEntity findStudentByRollNumber(String rollNumber);
    StudentEntity findStudentByEmail(String email);
    List<StudentEntity> findStudentsByFullNameOrRollNumber(String searchValue);
    List<StudentEntity> findAllStudents();
    List<StudentEntity> findAllStudentsWithoutCurChange();
    List<StudentEntity> findStudentsByProgramName(String programName);
    void saveStudent(StudentEntity stu) throws Exception;
    StudentEntity cleanDocumentAndOldRollNumber(StudentEntity stu);
    List<StudentEntity> getStudentByDocType(int type);
    List<StudentEntity> getStudentByProgram(int programId);
    StudentEntity createStudent(StudentEntity studentEntity);
    List<StudentEntity> findStudentByProgramId(Integer programId);
    void updateStudent(StudentEntity entity);
    List<StudentEntity> getStudentFailedMoreThanRequiredCredits(Integer credits);
    List<StudentEntity> findStudentsBySemesterId(int semesterId);
    List<StudentFailedSubject> getStudentFailCreditsByCredits(int numOfCredit);
    List<StudentEntity> getStudentsFromMarksBySemester(int semesterId);
    List<StudentEntity> getStudentBySemesterIdAndStatus(int semesterId, List<String> statusList);
    List<StudentEntity> getStudentBySemesterIdAndProgram(int semesterId, int programId);
    List<StudentFailedSubject> getSubjectsFailedBySemester(Integer selectedSemester);
    List<StudentFailedSubject> getSubjectsSlotsFailedBySemester(Integer selectedSemester);
    Map<String,StudentAndSubject> getSubjectsStudentsStudyInSemester(Integer selectedSemester);
    void myUpdateStudent(StudentEntity student);
}
