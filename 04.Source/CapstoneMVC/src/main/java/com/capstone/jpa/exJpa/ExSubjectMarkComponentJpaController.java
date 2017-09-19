package com.capstone.jpa.exJpa;

import com.capstone.jpa.SubjectMarkComponentEntityJpaController;

import javax.persistence.EntityManagerFactory;

public class ExSubjectMarkComponentJpaController extends SubjectMarkComponentEntityJpaController {
    public ExSubjectMarkComponentJpaController(EntityManagerFactory emf) {
        super(emf);
    }
}
