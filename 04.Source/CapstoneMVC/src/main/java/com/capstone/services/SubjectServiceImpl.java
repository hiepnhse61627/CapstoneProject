package com.capstone.services;

import com.capstone.entities.SubjectEntity;
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
}
