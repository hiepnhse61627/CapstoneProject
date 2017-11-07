package com.capstone.services;

import com.capstone.entities.CurriculumEntity;
import com.capstone.entities.SubjectCurriculumEntity;

import java.util.List;

public interface ISubjectCurriculumService {
    List<SubjectCurriculumEntity> getAllSubjectCurriculums();
    List<SubjectCurriculumEntity> getSubjectCurriculums(int curriculumId);
    SubjectCurriculumEntity getCurriculumById(int curId);
    SubjectCurriculumEntity getCurriculumByName(String name);
    void createCurriculumList(List<SubjectCurriculumEntity> courseEntityList);
    SubjectCurriculumEntity createCurriculum(SubjectCurriculumEntity entity);
    void updateCurriculum(SubjectCurriculumEntity entity);
    void deleteCurriculum(int subjectCurriculumId);
    CurriculumEntity findCurriculum(String curName, String programName);
    List<SubjectCurriculumEntity> getSubjectIds(Integer studentId, Integer currentTerm);
    CurriculumEntity cleanCurriculum(CurriculumEntity cur);
}
