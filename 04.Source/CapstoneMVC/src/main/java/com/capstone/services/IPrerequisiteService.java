package com.capstone.services;

import com.capstone.entities.PrequisiteEntity;

import java.util.List;

public interface IPrerequisiteService {
    public PrequisiteEntity getPrerequisiteBySubjectId(int subjectId);
    List<PrequisiteEntity> getAllPrerequisite();
}
