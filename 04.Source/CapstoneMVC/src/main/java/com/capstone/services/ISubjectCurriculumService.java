package com.capstone.services;

import com.capstone.entities.CurriculumEntity;
import com.capstone.entities.SubjectCurriculumEntity;
import com.capstone.models.SubjectModel;

import java.util.List;

public interface ISubjectCurriculumService {
    SubjectModel updateSubject(SubjectModel subject, int curriculumId);
    List<SubjectCurriculumEntity> getAllSubjectCurriculums();
    List<SubjectCurriculumEntity> getSubjectCurriculums(int curriculumId);
    SubjectCurriculumEntity getCurriculumById(int curId);
    SubjectCurriculumEntity getCurriculumByName(String name);
    void createCurriculumList(List<SubjectCurriculumEntity> courseEntityList);
    SubjectCurriculumEntity createCurriculum(SubjectCurriculumEntity entity);
    void updateCurriculum(SubjectCurriculumEntity entity);
    void deleteCurriculum(int subjectCurriculumId);
    CurriculumEntity findCurriculum(String curName, String programName);
    List<SubjectCurriculumEntity> getSubjectIds(List<Integer> curriculumIds, Integer currentTerm);
    CurriculumEntity cleanCurriculum(CurriculumEntity cur);
    List<SubjectCurriculumEntity> getSubjectCurriculumByStudent(int studentId);
    List<SubjectCurriculumEntity> getSubjectCurriculumByStudentByTerm(int studentId, int term);
}
