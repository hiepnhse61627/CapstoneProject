package com.capstone.services;

import com.capstone.entities.CurriculumEntity;
import com.capstone.entities.MarksEntity;
import com.capstone.entities.SubjectCurriculumEntity;
import com.capstone.jpa.exJpa.ExMarksEntityJpaController;
import com.capstone.jpa.exJpa.ExSubjectCurriculumJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class SubjectCurriculumServiceImpl implements ISubjectCurriculumService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExSubjectCurriculumJpaController controller = new ExSubjectCurriculumJpaController(emf);

    @Override
    public List<SubjectCurriculumEntity> getAllSubjectCurriculums() {
        return controller.findSubjectCurriculumEntityEntities();
    }

    @Override
    public List<SubjectCurriculumEntity> getSubjectCurriculums(int curriculumId) {
        return controller.getSubjectCurriculums(curriculumId);
    }

    @Override
    public SubjectCurriculumEntity getCurriculumById(int curId) {
        return controller.findById(curId);
    }

    @Override
    public SubjectCurriculumEntity getCurriculumByName(String name) {
        return controller.findByName(name);
    }

    public void createCurriculumList(List<SubjectCurriculumEntity> curriculumEntityList) {
        controller.createCurriculumList(curriculumEntityList);
    }

    @Override
    public SubjectCurriculumEntity createCurriculum(SubjectCurriculumEntity entity) {
        return controller.createCurriculum(entity);
    }

    @Override
    public void updateCurriculum(SubjectCurriculumEntity entity) {
        controller.updateCurriculum(entity);
    }

    @Override
    public  void deleteCurriculum(int subjectCurriculumId) {
        controller.deleteCurriculum(subjectCurriculumId);
    }

    @Override
    public CurriculumEntity findCurriculum(String curName, String programName) {
        return controller.findCurriculum(curName, programName);
    }

    @Override
    public List<SubjectCurriculumEntity> getSubjectIds(List<Integer> curriculumIds, Integer currentTerm) {
        return controller.getSubjectIds(curriculumIds, currentTerm);
    }

    @Override
    public CurriculumEntity cleanCurriculum(CurriculumEntity cur) {
        return controller.cleanCurriculum(cur);
    }
}
