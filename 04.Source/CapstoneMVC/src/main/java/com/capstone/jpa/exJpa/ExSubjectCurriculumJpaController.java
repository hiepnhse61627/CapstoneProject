package com.capstone.jpa.exJpa;

import com.capstone.entities.SubjectCurriculumEntity;
import com.capstone.jpa.SubjectCurriculumEntityJpaController;
import org.apache.commons.lang3.reflect.Typed;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class ExSubjectCurriculumJpaController extends SubjectCurriculumEntityJpaController {

    public ExSubjectCurriculumJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public SubjectCurriculumEntity findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SubjectCurriculumEntity.class, id);
        } finally {
            em.close();
        }
    }

    public SubjectCurriculumEntity findByName(String name) {
//        EntityManager em = getEntityManager();
//        try {
//            TypedQuery<SubjectCurriculumEntity> query = em.createQuery("SELECT a FROM SubjectCurriculumEntity a WHERE a.name = :name", SubjectCurriculumEntity.class);
//            query.setParameter("name", name);
//            return query.getSingleResult();
//        } catch (Exception e) {
//            return null;
//        } finally {
//            em.close();
//        }
        return null;
    }

    public SubjectCurriculumEntity createCurriculum(SubjectCurriculumEntity entity) {
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

    public void createCurriculumList(List<SubjectCurriculumEntity> subjectCurriculumEntityList) {
//        EntityManager em = getEntityManager();
//        for (SubjectCurriculumEntity subjectCurriculumEntity : subjectCurriculumEntityList) {
//            try {
//                TypedQuery<SubjectCurriculumEntity> tmp = em.createQuery("SELECT c FROM SubjectCurriculumEntity c WHERE c.name = :name AND c.description = :description", SubjectCurriculumEntity.class);
//                tmp.setParameter("name", subjectCurriculumEntity.getName());
//                tmp.setParameter("description", subjectCurriculumEntity.getDescription());
//                if (tmp.getResultList().size() == 0) {
//                    em.getTransaction().begin();
//                    em.persist(subjectCurriculumEntity);
//                    em.getTransaction().commit();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    public void updateCurriculum(SubjectCurriculumEntity entity) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
            entity = em.merge(entity);
            em.flush();
            em.refresh(entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.getTransaction().commit();
        }
    }
}
