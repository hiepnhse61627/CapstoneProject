package com.capstone.jpa.exJpa;

import com.capstone.entities.CurriculumEntity;
import com.capstone.entities.SubjectCurriculumEntity;
import com.capstone.jpa.SubjectCurriculumEntityJpaController;
import org.apache.commons.lang3.reflect.Typed;

import javax.persistence.*;
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

    public List<SubjectCurriculumEntity> getSubjectCurriculums(int curriculumId) {
        List<SubjectCurriculumEntity> result;
        EntityManager em = null;

        try {
            em = getEntityManager();

            String queryStr = "SELECT sc FROM SubjectCurriculumEntity sc WHERE sc.curriculumId.id = :curriculumId ORDER BY sc.ordinalNumber";
            TypedQuery<SubjectCurriculumEntity> query = em.createQuery(queryStr, SubjectCurriculumEntity.class);
            query.setParameter("curriculumId", curriculumId);

            result = query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
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

    public void deleteCurriculum(int subjectCurriculumId) {
        EntityManager em = null;

        try {
            em = getEntityManager();
            SubjectCurriculumEntity entity = this.findSubjectCurriculumEntity(subjectCurriculumId);

            em.getTransaction().begin();
            em.remove(em.merge(entity));
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }

    }

    public void createCurriculumList(List<SubjectCurriculumEntity> subjectCurriculumEntityList) {
        EntityManager em = getEntityManager();
        for (SubjectCurriculumEntity subjectCurriculumEntity : subjectCurriculumEntityList) {
            try {
                em.getTransaction().begin();
                em.persist(subjectCurriculumEntity);
                em.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    public CurriculumEntity findCurriculum(String cur, String program) {
        EntityManager em = getEntityManager();

        try {
            TypedQuery<CurriculumEntity> query = em.createQuery("SELECT a FROM CurriculumEntity a WHERE a.name = :program AND a.programId.name = :cur", CurriculumEntity.class);
            query.setParameter("cur", cur);
            query.setParameter("program", program);
            return query.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("data " + program + "_" + cur + " not exist");
            return null;
        } catch (NonUniqueResultException e) {
            System.out.println("data " + program + "_" + cur + " has multiple results");
            return null;
        }
    }

    public List<SubjectCurriculumEntity> getSubjectIds(Integer studentId, Integer currentTerm) {
        EntityManager em = getEntityManager();

        try {
            String sqlString = "SELECT s FROM SubjectCurriculumEntity s, DocumentStudentEntity d " +
                                        "WHERE s.curriculumId = d.curriculumId AND d.studentId.id = :id AND s.termNumber BETWEEN 1 AND :currentTerm";
            Query query = em.createQuery(sqlString);
            query.setParameter("id", studentId);
            query.setParameter("currentTerm", currentTerm);

            List<SubjectCurriculumEntity> list = query.getResultList();
            return list;
        } catch (NoResultException nrEx) {
            return null;
        }
    }
}
