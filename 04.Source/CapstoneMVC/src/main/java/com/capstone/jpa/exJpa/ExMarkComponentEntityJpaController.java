package com.capstone.jpa.exJpa;

import com.capstone.entities.MarkComponentEntity;
import com.capstone.jpa.MarkComponentEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class ExMarkComponentEntityJpaController extends MarkComponentEntityJpaController {

    public ExMarkComponentEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public MarkComponentEntity getMarkComponentByName(String name) {
        EntityManager em = getEntityManager();
        try {
            String sqlString = "SELECT m FROM MarkComponentEntity m WHERE m.name = :name";
            Query query = em.createQuery(sqlString);
            query.setParameter("name", name);

            MarkComponentEntity markComponentEntity = (MarkComponentEntity) query.getSingleResult();

            return markComponentEntity;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
