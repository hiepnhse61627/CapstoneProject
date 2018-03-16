package com.capstone.services;

import com.capstone.entities.CredentialsRolesEntity;
import com.capstone.jpa.exceptions.NonexistentEntityException;

import java.util.List;

public interface ICredentialsRolesService {
    List<CredentialsRolesEntity> getCredentialsRolesByCredentialsId(int credentialsId);
    void createCredentialRoles(CredentialsRolesEntity entity) throws Exception;
    void deleteCredentialRoles(CredentialsRolesEntity entity) throws NonexistentEntityException;
}
