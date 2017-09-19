package com.capstone.services;

import com.capstone.entities.SubjectEntity;

import java.util.List;

public interface ISubjectService {
    void insertSubjectList(List<SubjectEntity> list);
    List<SubjectEntity> getAllSubjects();
}
