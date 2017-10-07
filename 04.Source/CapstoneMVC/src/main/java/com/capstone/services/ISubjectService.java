package com.capstone.services;

import com.capstone.entities.SubjectEntity;
import com.capstone.entities.SubjectMarkComponentEntity;

import java.util.List;

public interface ISubjectService {
    void insertSubjectList(List<SubjectEntity> list);
    List<SubjectEntity> getAllSubjects();
    List<SubjectEntity> getAllPrequisiteSubjects(String subId);
    List<SubjectEntity> getAlllPrequisite();
    SubjectEntity findSubjectById(String id);

    int getCurrentLine();
    int getTotalLine();
}
