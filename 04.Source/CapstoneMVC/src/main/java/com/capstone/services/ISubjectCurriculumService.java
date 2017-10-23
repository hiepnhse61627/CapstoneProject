package com.capstone.services;

import com.capstone.entities.CurriculumEntity;
import com.capstone.entities.SubjectCurriculumEntity;

import java.util.List;

public interface ISubjectCurriculumService {
    List<SubjectCurriculumEntity> getAllSubjectCurriculums();
    SubjectCurriculumEntity getCurriculumById(int curId);
    SubjectCurriculumEntity getCurriculumByName(String name);
    void createCurriculumList(List<SubjectCurriculumEntity> courseEntityList);
    SubjectCurriculumEntity createCurriculum(SubjectCurriculumEntity entity);
    void updateCurriculum(SubjectCurriculumEntity entity);
    CurriculumEntity findCurriculum(String curName, String programName);
}
