package com.capstone.services;

import com.capstone.entities.SubjectEntity;

import java.util.List;
import java.util.Map;

public interface ISubjectService {
    void insertSubjectList(List<SubjectEntity> list, Map<String, String> prerequisiteList);
}
