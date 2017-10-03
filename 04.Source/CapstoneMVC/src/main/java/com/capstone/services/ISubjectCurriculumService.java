package com.capstone.services;

import com.capstone.entities.SubjectCurriculumEntity;

import java.util.List;

public interface ISubjectCurriculumService {
    List<SubjectCurriculumEntity> getAllSubjectCurriculum();
    SubjectCurriculumEntity getCurriculumById(int curId);
    void createCurriculumList(List<SubjectCurriculumEntity> courseEntityList);
    SubjectCurriculumEntity createCurriculum(SubjectCurriculumEntity entity);
}
