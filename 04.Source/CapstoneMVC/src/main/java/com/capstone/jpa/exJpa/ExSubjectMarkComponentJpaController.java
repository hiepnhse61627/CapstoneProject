package com.capstone.jpa.exJpa;

import com.capstone.entities.SubjectMarkComponentEntity;
import com.capstone.jpa.SubjectMarkComponentEntityJpaController;

import javax.persistence.*;

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

    public SubjectMarkComponentEntity findSubjectMarkComponentByNameAndSubjectCd(String name, String subjectCd) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            String sqlString = "SELECT s FROM SubjectMarkComponentEntity s WHERE s.name LIKE :name AND s.subjectId.id = :subjectCd";
            Query query = em.createQuery(sqlString);
            query.setParameter("name", "%" + name + "%");
            query.setParameter("subjectCd", subjectCd);

            SubjectMarkComponentEntity result = (SubjectMarkComponentEntity) query.getSingleResult();
            return result;
        } catch (NoResultException nrEx) {
            System.out.println("No records were found with: " + name + "AND" + subjectCd);
            return null;
        } catch (NonUniqueResultException nuEx) {
            System.out.println("Many records were found with: " + name + "AND" + subjectCd);
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
