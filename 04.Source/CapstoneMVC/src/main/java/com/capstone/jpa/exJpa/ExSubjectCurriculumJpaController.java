package com.capstone.jpa.exJpa;

import com.capstone.jpa.SubjectCurriculumEntityJpaController;

import javax.persistence.EntityManagerFactory;

public class ExSubjectCurriculumJpaController extends SubjectCurriculumEntityJpaController {

    public ExSubjectCurriculumJpaController(EntityManagerFactory emf) {
        super(emf);
    }
}
