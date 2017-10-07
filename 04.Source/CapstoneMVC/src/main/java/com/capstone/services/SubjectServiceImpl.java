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
    public void insertSubjectList(List<SubjectEntity> list) {
        controller.insertSubjectList(list);
    }

    @Override
    public List<SubjectEntity> getAllSubjects() {
        return controller.findSubjectEntityEntities();
    }

    @Override
    public List<SubjectEntity> getAllPrequisiteSubjects(String subId) {
        return controller.getAllPrequisiteSubjects(subId);
    }

    @Override
    public List<SubjectEntity> getAlllPrequisite() {
        return controller.getAllPrequisite();
    }

    @Override
    public SubjectEntity findSubjectById(String id) {
        return controller.findSubjectEntity(id);
    }

    @Override
    public int getCurrentLine() { return controller.getCurrentLine(); }

    @Override
    public int getTotalLine() { return controller.getTotalLine(); }
}
