package com.capstone.jpa.exJpa;

import com.capstone.entities.RealSemesterEntity;
import com.capstone.jpa.RealSemesterEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class ExRealSemesterEntityJpaController extends RealSemesterEntityJpaController {
    public ExRealSemesterEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public RealSemesterEntity findRealSemesterByName(String semesterName) {
        EntityManager em = getEntityManager();
        RealSemesterEntity realSemesterEntity = new RealSemesterEntity();
        try {
            String sqlString = "SELECT r FROM RealSemesterEntity r WHERE r.semester = :semester";
            Query query = em.createQuery(sqlString);
            query.setParameter("semester", semesterName);

            realSemesterEntity = (RealSemesterEntity) query.getSingleResult();

            return realSemesterEntity;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public RealSemesterEntity createRealSemester(RealSemesterEntity entity) {
        EntityManager em = null;
        try {
            em= getEntityManager();
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
