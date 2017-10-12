package com.capstone.services;

import com.capstone.entities.CourseEntity;
import com.capstone.entities.CredentialsEntity;
import com.capstone.jpa.exJpa.ExCourseEntityJpaController;
import com.capstone.jpa.exJpa.ExCredentialsEntityJpaController;
import com.capstone.models.DatatableModel;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class CredentialsServiceImpl implements ICredentialsService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExCredentialsEntityJpaController controller = new ExCredentialsEntityJpaController(emf);


    @Override
    public CredentialsEntity findCredential(String username) {
        return controller.findCredential(username);
    }

    @Override
    public CredentialsEntity findCredential(String username, String password) {
        return controller.findCredential(username, password);
    }
}
