package com.capstone.services;

import com.capstone.entities.SubjectEntity;
import com.capstone.jpa.exJpa.ExSubjectEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Map;

public class SubjectServiceImpl implements ISubjectService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExSubjectEntityJpaController controller = new ExSubjectEntityJpaController(emf);

    @Override
    public void insertSubjectList(List<SubjectEntity> list, Map<String, String> prerequisiteList) {
        controller.insertSubjectList(list, prerequisiteList);
    }

    @Override
    public List<SubjectEntity> getAllSubjects() {
        return controller.findSubjectEntityEntities();
    }
}
