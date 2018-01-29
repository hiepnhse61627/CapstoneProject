package com.capstone.services;

import com.capstone.entities.RolesEntity;
import com.capstone.jpa.exceptions.PreexistingEntityException;

public interface IRolesService {
    RolesEntity findRolesEntity(String id);
    public void create(RolesEntity rolesEntity) throws PreexistingEntityException, Exception;
}
