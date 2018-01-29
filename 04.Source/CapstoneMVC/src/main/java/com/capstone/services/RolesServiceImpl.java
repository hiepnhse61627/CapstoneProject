package com.capstone.services;

import com.capstone.entities.RolesEntity;
import com.capstone.jpa.exJpa.ExRolesEntityJpaController;
import com.capstone.jpa.exceptions.PreexistingEntityException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class RolesServiceImpl implements IRolesService {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExRolesEntityJpaController controller = new ExRolesEntityJpaController(emf);

    @Override
    public RolesEntity findRolesEntity(String id) {
        return controller.findRolesEntity(id);
    }

    @Override
    public void create(RolesEntity rolesEntity) throws Exception {
        try {

            controller.create(rolesEntity);
        } catch (PreexistingEntityException e) {
            System.out.println(e);
        }
    }


}
