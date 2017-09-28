package com.capstone.services;

import com.capstone.entities.SubjectEntity;
import com.capstone.entities.SubjectMarkComponentEntity;

import java.util.List;

public interface ISubjectService {
    void insertSubjectList(List<SubjectEntity> list);
    SubjectEntity findSubjectbyId(String id);
    List<SubjectEntity> getAllSubjects();
    List<SubjectEntity> getAllPrequisiteSubjects(String subId);

    int getCurrentLine();
    int getTotalLine();
}
