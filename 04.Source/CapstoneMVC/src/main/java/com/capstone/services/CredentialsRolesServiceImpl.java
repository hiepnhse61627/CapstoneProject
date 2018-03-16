package com.capstone.services;

import com.capstone.entities.CredentialsEntity;
import com.capstone.entities.CredentialsRolesEntity;
import com.capstone.jpa.exJpa.ExCredentialsRolesEntityJpaController;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class CredentialsRolesServiceImpl implements ICredentialsRolesService{
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExCredentialsRolesEntityJpaController controller = new ExCredentialsRolesEntityJpaController(emf);


    @Override
    public List<CredentialsRolesEntity> getCredentialsRolesByCredentialsId(int credentialsId) {
        return controller.getCredentialsRolesByCredentialsId(credentialsId);
    }

    @Override
    public void createCredentialRoles(CredentialsRolesEntity entity) throws Exception {
        controller.create(entity);
    }

    @Override
    public void deleteCredentialRoles(CredentialsRolesEntity entity) throws NonexistentEntityException {
        controller.destroy(entity.getId());
    }
}
