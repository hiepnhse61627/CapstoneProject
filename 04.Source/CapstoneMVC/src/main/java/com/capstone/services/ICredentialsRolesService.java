package com.capstone.services;

import com.capstone.entities.CredentialsRolesEntity;

import java.util.List;

public interface ICredentialsRolesService {
    List<CredentialsRolesEntity> getCredentialsRolesByCredentialsId(int credentialsId);

}
