package com.capstone.services;

import com.capstone.entities.SubjectMarkComponentEntity;
import com.capstone.jpa.exJpa.ExSubjectMarkComponentJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class SubjectMarkComponentServiceImpl implements ISubjectMarkComponentService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExSubjectMarkComponentJpaController subjectMarkComponentJpaController = new ExSubjectMarkComponentJpaController(emf);

    @Override
    public SubjectMarkComponentEntity findSubjectMarkComponentById(Integer id) {
        return subjectMarkComponentJpaController.findSubjectMarkComponentEntity(id);
    }

    @Override
    public SubjectMarkComponentEntity createSubjectMarkComponent(SubjectMarkComponentEntity entity) {
        return subjectMarkComponentJpaController.createSubjectMarkComponent(entity);
    }

    @Override
    public SubjectMarkComponentEntity findSubjectMarkComponentByNameAndSubjectCd(String name, String subjectCd) {
        return subjectMarkComponentJpaController.findSubjectMarkComponentByNameAndSubjectCd(name, subjectCd);
    }
}
