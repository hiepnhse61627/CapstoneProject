package com.capstone.services;

import com.capstone.entities.DynamicMenuEntity;
import com.capstone.entities.RolesAuthorityEntity;
import com.capstone.jpa.exJpa.ExCourseEntityJpaController;
import com.capstone.jpa.exJpa.ExRolesAuthorityEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

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

    @Override
    public List<RolesAuthorityEntity> findRolesAuthorityByMenuId(int dynamicMenuId) {
        return rolesAuthorityEntityJpaController.findRolesAuthorityByMenuId(dynamicMenuId);
    }

    @Override
    public boolean deleteRolesAuthorityByIdList(List<RolesAuthorityEntity> roleAuthorIds) {
        return rolesAuthorityEntityJpaController.deleteRolesAuthorityByIdList(roleAuthorIds);
    }

    @Override
    public List<DynamicMenuEntity> findMenuByRoleId(int roleId) {
        return rolesAuthorityEntityJpaController.findMenuByRoleId(roleId);
    }

    @Override
    public List<RolesAuthorityEntity> findRolesAuthorityByRoleId(int roleId) {
        return rolesAuthorityEntityJpaController.findRolesAuthorityByRoleId(roleId);
    }
}
