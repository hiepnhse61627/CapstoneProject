package com.capstone.services;


import com.capstone.entities.CourseEntity;
import com.capstone.entities.CredentialsEntity;
import com.capstone.models.DatatableModel;

import java.util.List;

public interface ICredentialsService {
    CredentialsEntity findCredential(String username);
    CredentialsEntity findCredential(String username, String password);
}