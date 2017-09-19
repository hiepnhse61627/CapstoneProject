package com.capstone.jpa.exJpa;

import com.capstone.jpa.MarksEntityJpaController;

import javax.persistence.EntityManagerFactory;

public class ExMarksEntityJpaController extends MarksEntityJpaController {
    public ExMarksEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }
}
