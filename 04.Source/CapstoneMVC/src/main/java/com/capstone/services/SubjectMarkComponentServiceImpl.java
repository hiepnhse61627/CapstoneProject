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
}
