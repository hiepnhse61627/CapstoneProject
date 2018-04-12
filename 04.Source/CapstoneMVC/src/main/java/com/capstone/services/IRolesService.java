package com.capstone.services;

import com.capstone.entities.RolesEntity;
import com.capstone.jpa.exceptions.PreexistingEntityException;

import java.util.List;

public interface IRolesService {
    RolesEntity findRolesEntity(Integer id);
    public void create(RolesEntity rolesEntity) throws PreexistingEntityException, Exception;
    List<RolesEntity> getAllRoles();
    boolean createNewRole(RolesEntity newRole);
    List<RolesEntity> getRolesByName(String role);
    boolean updateRole(RolesEntity currentRole);
}
