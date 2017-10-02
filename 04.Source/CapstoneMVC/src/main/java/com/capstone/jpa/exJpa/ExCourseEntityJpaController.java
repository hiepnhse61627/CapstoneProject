package com.capstone.jpa.exJpa;

import com.capstone.entities.CourseEntity;
import com.capstone.jpa.CourseEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

public class ExCourseEntityJpaController extends CourseEntityJpaController {
    public ExCourseEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public CourseEntity findCourseByClassAndSubjectCode(String className, String subjectCode) {
        EntityManager em = getEntityManager();
        try {
            String sqlString = "SELECT c FROM CourseEntity c WHERE lower(c.clazz) = :clazz AND lower(c.subjectCode) = :subjectCode";
            Query query = em.createQuery(sqlString);
            query.setParameter("clazz", className);
            query.setParameter("subjectCode", subjectCode);
            query.setMaxResults(1);

            CourseEntity courseEntity = (CourseEntity) query.getSingleResult();

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

    public void createCourseList(List<CourseEntity> courseEntityList) {
        EntityManager em = getEntityManager();
        for (CourseEntity courseEntity: courseEntityList) {
            em.getTransaction().begin();
            em.persist(courseEntity);
            em.getTransaction().commit();
        }
    }
}
