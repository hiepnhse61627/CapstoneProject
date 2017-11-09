package com.capstone.services;

import com.capstone.entities.OldRollNumberEntity;
import com.capstone.jpa.exJpa.ExOldRollNumberEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class OldRollNumberServiceImpl implements IOldRollNumberService {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    private ExOldRollNumberEntityJpaController oldRollNumberEntityJpaController = new ExOldRollNumberEntityJpaController(emf);

    @Override
    public OldRollNumberEntity createOldRollNumber(OldRollNumberEntity entity) {
        return oldRollNumberEntityJpaController.createOldRollNumber(entity);
    }
}
