/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.CurriculumEntity;
import com.capstone.entities.SubjectCurriculumEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author hiepnhse61627
 */
public class SubjectCurriculumEntityJpaController implements Serializable {

    public SubjectCurriculumEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SubjectCurriculumEntity subjectCurriculumEntity) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CurriculumEntity curriculumId = subjectCurriculumEntity.getCurriculumId();
            if (curriculumId != null) {
                curriculumId = em.getReference(curriculumId.getClass(), curriculumId.getId());
                subjectCurriculumEntity.setCurriculumId(curriculumId);
            }
            SubjectEntity subjectId = subjectCurriculumEntity.getSubjectId();
            if (subjectId != null) {
                subjectId = em.getReference(subjectId.getClass(), subjectId.getId());
                subjectCurriculumEntity.setSubjectId(subjectId);
            }
            em.persist(subjectCurriculumEntity);
            if (curriculumId != null) {
                curriculumId.getSubjectCurriculumEntityList().add(subjectCurriculumEntity);
                curriculumId = em.merge(curriculumId);
            }
            if (subjectId != null) {
                subjectId.getSubjectCurriculumEntityList().add(subjectCurriculumEntity);
                subjectId = em.merge(subjectId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SubjectCurriculumEntity subjectCurriculumEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectCurriculumEntity persistentSubjectCurriculumEntity = em.find(SubjectCurriculumEntity.class, subjectCurriculumEntity.getId());
            CurriculumEntity curriculumIdOld = persistentSubjectCurriculumEntity.getCurriculumId();
            CurriculumEntity curriculumIdNew = subjectCurriculumEntity.getCurriculumId();
            SubjectEntity subjectIdOld = persistentSubjectCurriculumEntity.getSubjectId();
            SubjectEntity subjectIdNew = subjectCurriculumEntity.getSubjectId();
            if (curriculumIdNew != null) {
                curriculumIdNew = em.getReference(curriculumIdNew.getClass(), curriculumIdNew.getId());
                subjectCurriculumEntity.setCurriculumId(curriculumIdNew);
            }
            if (subjectIdNew != null) {
                subjectIdNew = em.getReference(subjectIdNew.getClass(), subjectIdNew.getId());
                subjectCurriculumEntity.setSubjectId(subjectIdNew);
            }
            subjectCurriculumEntity = em.merge(subjectCurriculumEntity);
            if (curriculumIdOld != null && !curriculumIdOld.equals(curriculumIdNew)) {
                curriculumIdOld.getSubjectCurriculumEntityList().remove(subjectCurriculumEntity);
                curriculumIdOld = em.merge(curriculumIdOld);
            }
            if (curriculumIdNew != null && !curriculumIdNew.equals(curriculumIdOld)) {
                curriculumIdNew.getSubjectCurriculumEntityList().add(subjectCurriculumEntity);
                curriculumIdNew = em.merge(curriculumIdNew);
            }
            if (subjectIdOld != null && !subjectIdOld.equals(subjectIdNew)) {
                subjectIdOld.getSubjectCurriculumEntityList().remove(subjectCurriculumEntity);
                subjectIdOld = em.merge(subjectIdOld);
            }
            if (subjectIdNew != null && !subjectIdNew.equals(subjectIdOld)) {
                subjectIdNew.getSubjectCurriculumEntityList().add(subjectCurriculumEntity);
                subjectIdNew = em.merge(subjectIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = subjectCurriculumEntity.getId();
                if (findSubjectCurriculumEntity(id) == null) {
                    throw new NonexistentEntityException("The subjectCurriculumEntity with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectCurriculumEntity subjectCurriculumEntity;
            try {
                subjectCurriculumEntity = em.getReference(SubjectCurriculumEntity.class, id);
                subjectCurriculumEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The subjectCurriculumEntity with id " + id + " no longer exists.", enfe);
            }
            CurriculumEntity curriculumId = subjectCurriculumEntity.getCurriculumId();
            if (curriculumId != null) {
                curriculumId.getSubjectCurriculumEntityList().remove(subjectCurriculumEntity);
                curriculumId = em.merge(curriculumId);
            }
            SubjectEntity subjectId = subjectCurriculumEntity.getSubjectId();
            if (subjectId != null) {
                subjectId.getSubjectCurriculumEntityList().remove(subjectCurriculumEntity);
                subjectId = em.merge(subjectId);
            }
            em.remove(subjectCurriculumEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<SubjectCurriculumEntity> findSubjectCurriculumEntityEntities() {
        return findSubjectCurriculumEntityEntities(true, -1, -1);
    }

    public List<SubjectCurriculumEntity> findSubjectCurriculumEntityEntities(int maxResults, int firstResult) {
        return findSubjectCurriculumEntityEntities(false, maxResults, firstResult);
    }

    private List<SubjectCurriculumEntity> findSubjectCurriculumEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SubjectCurriculumEntity.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public SubjectCurriculumEntity findSubjectCurriculumEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SubjectCurriculumEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getSubjectCurriculumEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SubjectCurriculumEntity> rt = cq.from(SubjectCurriculumEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
