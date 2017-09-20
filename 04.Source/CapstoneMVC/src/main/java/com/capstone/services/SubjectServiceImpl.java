package com.capstone.services;

import com.capstone.entities.SubjectEntity;
import com.capstone.entities.SubjectMarkComponentEntity;
import com.capstone.jpa.exJpa.ExSubjectEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class SubjectServiceImpl implements ISubjectService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExSubjectEntityJpaController controller = new ExSubjectEntityJpaController(emf);

    @Override
    public void createSubjects(List<SubjectEntity> subjectEntities) {
        controller.createSubjects(subjectEntities);
    }

    @Override
    public void insertSubjectList(List<SubjectEntity> list) {
        controller.insertSubjectList(list);
    }

    @Override
    public SubjectEntity findSubjectbyId(String id) {
        return controller.findSubjectEntity(id);
    }

    @Override
    public List<SubjectEntity> getAllSubjects() {
        return controller.findSubjectEntityEntities();
    }
}
