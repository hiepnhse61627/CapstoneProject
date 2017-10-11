package com.capstone.jpa.exJpa;

import com.capstone.entities.CurriculumMappingEntity;
import com.capstone.jpa.CurriculumEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class ExCurriculumMappingEntityJpaController extends CurriculumEntityJpaController {
    public ExCurriculumMappingEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public CurriculumMappingEntity createCurriculumMapping(CurriculumMappingEntity entity) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(entity);
            em.flush();
            em.refresh(entity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return entity;
    }
}
