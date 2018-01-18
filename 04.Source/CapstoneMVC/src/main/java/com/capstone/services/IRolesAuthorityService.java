package com.capstone.services;

import com.capstone.entities.RolesAuthorityEntity;

public interface IRolesAuthorityService {
    RolesAuthorityEntity createRolesAuthority(RolesAuthorityEntity entity);
    boolean findRolesAuthorityByRoleIdAndMenuId(String roleId, int dynamicMenuId);
}
