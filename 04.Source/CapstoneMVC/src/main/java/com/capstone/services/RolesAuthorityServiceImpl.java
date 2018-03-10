package com.capstone.services;

import com.capstone.entities.RolesAuthorityEntity;
import com.capstone.jpa.exJpa.ExCourseEntityJpaController;
import com.capstone.jpa.exJpa.ExRolesAuthorityEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class RolesAuthorityServiceImpl implements IRolesAuthorityService{
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExRolesAuthorityEntityJpaController
            rolesAuthorityEntityJpaController = new ExRolesAuthorityEntityJpaController(emf);


    @Override
    public RolesAuthorityEntity createRolesAuthority(RolesAuthorityEntity entity) {
        return rolesAuthorityEntityJpaController.createRolesAuthority(entity);
    }

    @Override
    public boolean findRolesAuthorityByRoleIdAndMenuId(String roleId, int dynamicMenuId) {
        return rolesAuthorityEntityJpaController.findRolesAuthorityByRoleIdAndMenuId(roleId, dynamicMenuId);
    }
}
