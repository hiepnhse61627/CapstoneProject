package com.capstone.services;

import com.capstone.entities.SubjectEntity;
import com.capstone.models.ReplacementSubject;

import java.util.List;

public interface ISubjectService {
    void insertSubjectList(List<SubjectEntity> list);
    void insertReplacementList(List<ReplacementSubject> list);
    List<SubjectEntity> getAllSubjects();
    List<List<SubjectEntity>> getAllPrequisiteSubjects(String subId);
    List<List<SubjectEntity>> getAlllPrequisite();
    SubjectEntity findSubjectById(String id);
    int getCurrentLine();
    int getTotalLine();
    int countStudentCredits(int studentId);
    List<SubjectEntity> getSubjectsByMarkStatus(String[] statuses);
}
