package com.capstone.services;

import com.capstone.entities.SubjectEntity;
import com.capstone.entities.SubjectMarkComponentEntity;

import java.util.List;

public interface ISubjectService {
    void createSubjects(List<SubjectEntity> subjectEntities);
    void insertSubjectList(List<SubjectEntity> list);
    SubjectEntity findSubjectbyId(String id);
}
