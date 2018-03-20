package com.capstone.services;

import com.capstone.entities.DynamicMenuEntity;
import com.capstone.entities.RolesAuthorityEntity;

import java.util.List;

public interface IRolesAuthorityService {
    RolesAuthorityEntity createRolesAuthority(RolesAuthorityEntity entity);
    boolean findRolesAuthorityByRoleIdAndMenuId(String roleId, int dynamicMenuId);
    List<RolesAuthorityEntity> findRolesAuthorityByMenuId(int dynamicMenuId);
    boolean deleteRolesAuthorityByIdList(List<RolesAuthorityEntity> roleAuthorIds);
    List<DynamicMenuEntity> findMenuByRoleId(int roleId);
    List<RolesAuthorityEntity> findRolesAuthorityByRoleId(int roleId);
    List<RolesAuthorityEntity> findRolesAuthorityByRoleIdByUrl(int roleId, String url);
    List<RolesAuthorityEntity> findRolesAuthorityByRoleIdByMenuId(int roleId, int dynamicMenuId);
}
