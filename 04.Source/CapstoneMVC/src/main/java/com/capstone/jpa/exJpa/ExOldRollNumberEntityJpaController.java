package com.capstone.jpa.exJpa;

import com.capstone.entities.OldRollNumberEntity;
import com.capstone.jpa.OldRollNumberEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class ExOldRollNumberEntityJpaController extends OldRollNumberEntityJpaController {
    public ExOldRollNumberEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public OldRollNumberEntity createOldRollNumber(OldRollNumberEntity entity) {
        EntityManager em = null;

        try {
            em = getEntityManager();

            em.getTransaction().begin();
            em.persist(entity);
            em.flush();
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return entity;
    }
}
