package com.capstone.services;

import com.capstone.entities.RolesEntity;
import com.capstone.jpa.exceptions.PreexistingEntityException;

import java.util.List;

public interface IRolesService {
    RolesEntity findRolesEntity(String id);
    public void create(RolesEntity rolesEntity) throws PreexistingEntityException, Exception;
    List<RolesEntity> getAllRoles();
    boolean createNewRole(RolesEntity newRole);
    List<RolesEntity> getRolesById(String role);
}
