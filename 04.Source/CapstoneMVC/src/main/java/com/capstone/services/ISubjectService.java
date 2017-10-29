package com.capstone.services;

import com.capstone.entities.SubjectEntity;
import com.capstone.models.ReplacementSubject;
import com.capstone.models.SubjectModel;

import java.util.List;

public interface ISubjectService {
    boolean updateSubject(SubjectModel subject);
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
