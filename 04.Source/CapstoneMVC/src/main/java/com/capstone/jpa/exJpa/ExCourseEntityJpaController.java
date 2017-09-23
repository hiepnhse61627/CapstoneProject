package com.capstone.jpa.exJpa;

import com.capstone.entities.CourseEntity;
import com.capstone.jpa.CourseEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class ExCourseEntityJpaController extends CourseEntityJpaController {
    public ExCourseEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public CourseEntity findCourseByClass(String className) {
        EntityManager em = getEntityManager();
        CourseEntity courseEntity = new CourseEntity();
        try {
            String sqlString = "SELECT c FROM CourseEntity c WHERE c.class1 = :class1";
            Query query = em.createQuery(sqlString);
            query.setParameter("class1", className);

            courseEntity = (CourseEntity) query.getSingleResult();

            return  courseEntity;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public CourseEntity createCourse(CourseEntity entity) {
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