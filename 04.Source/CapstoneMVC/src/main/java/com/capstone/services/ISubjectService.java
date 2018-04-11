package com.capstone.services;

import com.capstone.entities.DepartmentEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.models.ReplacementSubject;
import com.capstone.models.SubjectModel;

import javax.security.auth.Subject;
import java.util.List;

public interface ISubjectService {
    SubjectModel createSubject(SubjectModel subject);
    SubjectModel updateSubject(SubjectModel subject);
    void insertSubjectList(List<SubjectEntity> list);
    void insertReplacementList(List<ReplacementSubject> list);
    List<SubjectEntity> getAllSubjects();
    List<List<SubjectEntity>> getAllPrequisiteSubjects(String subId);
    List<List<SubjectEntity>> getAlllPrequisite();
    SubjectEntity findSubjectById(String id);
    List<SubjectEntity> findSubjectByDepartment(DepartmentEntity dept);
    int getCurrentLine();
    int getTotalLine();
    int countStudentCredits(int studentId);
    void cleanReplacers();
    List<SubjectEntity> getSubjectsByMarkStatus(String[] statuses);
    boolean bulkUpdateSubjects(List<SubjectEntity> subjectList);
}
