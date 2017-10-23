package com.capstone.services;

import com.capstone.entities.MarkComponentEntity;
import com.capstone.jpa.exJpa.ExMarkComponentEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class MarkComponentServiceImpl implements IMarkComponentService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExMarkComponentEntityJpaController markComponentEntityJpaController = new ExMarkComponentEntityJpaController(emf);

    @Override
    public MarkComponentEntity getMarkComponentByName(String name) {
        return markComponentEntityJpaController.getMarkComponentByName(name);
    }
}
