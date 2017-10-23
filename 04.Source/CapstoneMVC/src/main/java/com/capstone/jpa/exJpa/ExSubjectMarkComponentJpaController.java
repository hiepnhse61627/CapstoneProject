package com.capstone.jpa.exJpa;

import com.capstone.entities.SubjectMarkComponentEntity;
import com.capstone.jpa.SubjectMarkComponentEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class ExSubjectMarkComponentJpaController extends SubjectMarkComponentEntityJpaController {
    public ExSubjectMarkComponentJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public SubjectMarkComponentEntity createSubjectMarkComponent (SubjectMarkComponentEntity entity) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(entity);
            em.flush();
            em.getTransaction().commit();

            return entity;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
