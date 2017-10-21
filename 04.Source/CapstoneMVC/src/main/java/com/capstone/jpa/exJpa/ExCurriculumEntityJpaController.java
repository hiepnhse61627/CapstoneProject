package com.capstone.jpa.exJpa;

import com.capstone.entities.CurriculumEntity;
import com.capstone.jpa.CurriculumEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

public class ExCurriculumEntityJpaController extends CurriculumEntityJpaController {
    public ExCurriculumEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public List<CurriculumEntity> getAllCurriculums() {
        return this.findCurriculumEntityEntities();
    }

    public CurriculumEntity getCurriculumById(int id) {
        return this.findCurriculumEntity(id);
    }

    public CurriculumEntity getCurriculumByName(String name) {
        CurriculumEntity entity = null;
        EntityManager em = null;

        try {
            em = getEntityManager();
            TypedQuery<CurriculumEntity> query = em.createQuery(
                    "SELECT c FROM CurriculumEntity c WHERE c.name LIKE :name", CurriculumEntity.class);
            query.setParameter("name", name);

            entity = query.getSingleResult();
        } finally {
            em.close();
        }

        return entity;
    }

    public CurriculumEntity createCurriculum(CurriculumEntity entity) {
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
