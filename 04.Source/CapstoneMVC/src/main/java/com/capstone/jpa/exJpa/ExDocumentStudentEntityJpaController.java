package com.capstone.jpa.exJpa;

import com.capstone.entities.DocumentStudentEntity;
import com.capstone.jpa.DocumentStudentEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class ExDocumentStudentEntityJpaController extends DocumentStudentEntityJpaController {
    public ExDocumentStudentEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public DocumentStudentEntity createDocumentStudent(DocumentStudentEntity entity) {
        EntityManager em = null;

        try {
            em = getEntityManager();

            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return entity;
    }
}
