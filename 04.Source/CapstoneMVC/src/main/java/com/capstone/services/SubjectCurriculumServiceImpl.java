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
    public List<SubjectCurriculumEntity> getAllSubjectCurriculum() {
        return controller.findSubjectCurriculumEntityEntities();
    }

    @Override
    public SubjectCurriculumEntity getCurriculumById(int curId) {
        return controller.findById(curId);
    }

    public void createCurriculumList(List<SubjectCurriculumEntity> curriculumEntityList) {
//        controller.createCurriculumList(curriculumEntityList);
    }

    @Override
    public SubjectCurriculumEntity createCurriculum(SubjectCurriculumEntity entity) {
//        return controller.createCurriculum(entity);
        return null;
    }
}
