package com.capstone.jpa.exJpa;

import com.capstone.jpa.StudentStatusEntityJpaController;

import javax.persistence.EntityManagerFactory;

public class ExStudentStatusEntityJpaController extends StudentStatusEntityJpaController {
    public ExStudentStatusEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }
}
