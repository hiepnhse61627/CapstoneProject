package com.capstone.services;


import com.capstone.entities.CourseEntity;
import com.capstone.entities.CredentialsEntity;
import com.capstone.models.DatatableModel;

import java.util.List;

public interface ICredentialsService {
    CredentialsEntity findCredential(String username);
    CredentialsEntity findCredentialById(int id);
    CredentialsEntity findCredentialByEmail(String email);
    CredentialsEntity findCredential(String username, String password);
    void CreateCredentiall(CredentialsEntity entity);
    void SaveCredential(CredentialsEntity entity, boolean persist);
}
