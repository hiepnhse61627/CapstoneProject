package com.capstone.services;

import com.capstone.entities.RolesEntity;
import com.capstone.jpa.exJpa.ExRolesEntityJpaController;
import com.capstone.jpa.exceptions.PreexistingEntityException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class RolesServiceImpl implements IRolesService {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExRolesEntityJpaController controller = new ExRolesEntityJpaController(emf);

    @Override
    public RolesEntity findRolesEntity(Integer id) {
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

    @Override
    public List<RolesEntity> getAllRoles() {
        return controller.getAllRoles();
    }

    @Override
    public boolean createNewRole(RolesEntity newRole) {
        return controller.createNewRole(newRole);
    }

    @Override
    public List<RolesEntity> getRolesByName(String role) {
        return controller.getRolesByName(role);
    }


}
