/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.entities.CurriculumMappingEntity;
import com.capstone.entities.CurriculumMappingEntityPK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.SubjectEntity;
import com.capstone.entities.SubjectCurriculumEntity;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rem
 */
public class CurriculumMappingEntityJpaController implements Serializable {

    public CurriculumMappingEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CurriculumMappingEntity curriculumMappingEntity) throws PreexistingEntityException, Exception {
        if (curriculumMappingEntity.getCurriculumMappingEntityPK() == null) {
            curriculumMappingEntity.setCurriculumMappingEntityPK(new CurriculumMappingEntityPK());
        }
        curriculumMappingEntity.getCurriculumMappingEntityPK().setCurId(curriculumMappingEntity.getSubjectCurriculumEntity().getId());
        curriculumMappingEntity.getCurriculumMappingEntityPK().setSubId(curriculumMappingEntity.getSubjectEntity().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectEntity subjectEntity = curriculumMappingEntity.getSubjectEntity();
            if (subjectEntity != null) {
                subjectEntity = em.getReference(subjectEntity.getClass(), subjectEntity.getId());
                curriculumMappingEntity.setSubjectEntity(subjectEntity);
            }
            SubjectCurriculumEntity subjectCurriculumEntity = curriculumMappingEntity.getSubjectCurriculumEntity();
            if (subjectCurriculumEntity != null) {
                subjectCurriculumEntity = em.getReference(subjectCurriculumEntity.getClass(), subjectCurriculumEntity.getId());
                curriculumMappingEntity.setSubjectCurriculumEntity(subjectCurriculumEntity);
            }
            em.persist(curriculumMappingEntity);
            if (subjectEntity != null) {
                subjectEntity.getCurriculumMappingEntityList().add(curriculumMappingEntity);
                subjectEntity = em.merge(subjectEntity);
            }
            if (subjectCurriculumEntity != null) {
                subjectCurriculumEntity.getCurriculumMappingEntityList().add(curriculumMappingEntity);
                subjectCurriculumEntity = em.merge(subjectCurriculumEntity);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCurriculumMappingEntity(curriculumMappingEntity.getCurriculumMappingEntityPK()) != null) {
                throw new PreexistingEntityException("CurriculumMappingEntity " + curriculumMappingEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CurriculumMappingEntity curriculumMappingEntity) throws NonexistentEntityException, Exception {
        curriculumMappingEntity.getCurriculumMappingEntityPK().setCurId(curriculumMappingEntity.getSubjectCurriculumEntity().getId());
        curriculumMappingEntity.getCurriculumMappingEntityPK().setSubId(curriculumMappingEntity.getSubjectEntity().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CurriculumMappingEntity persistentCurriculumMappingEntity = em.find(CurriculumMappingEntity.class, curriculumMappingEntity.getCurriculumMappingEntityPK());
            SubjectEntity subjectEntityOld = persistentCurriculumMappingEntity.getSubjectEntity();
            SubjectEntity subjectEntityNew = curriculumMappingEntity.getSubjectEntity();
            SubjectCurriculumEntity subjectCurriculumEntityOld = persistentCurriculumMappingEntity.getSubjectCurriculumEntity();
            SubjectCurriculumEntity subjectCurriculumEntityNew = curriculumMappingEntity.getSubjectCurriculumEntity();
            if (subjectEntityNew != null) {
                subjectEntityNew = em.getReference(subjectEntityNew.getClass(), subjectEntityNew.getId());
                curriculumMappingEntity.setSubjectEntity(subjectEntityNew);
            }
            if (subjectCurriculumEntityNew != null) {
                subjectCurriculumEntityNew = em.getReference(subjectCurriculumEntityNew.getClass(), subjectCurriculumEntityNew.getId());
                curriculumMappingEntity.setSubjectCurriculumEntity(subjectCurriculumEntityNew);
            }
            curriculumMappingEntity = em.merge(curriculumMappingEntity);
            if (subjectEntityOld != null && !subjectEntityOld.equals(subjectEntityNew)) {
                subjectEntityOld.getCurriculumMappingEntityList().remove(curriculumMappingEntity);
                subjectEntityOld = em.merge(subjectEntityOld);
            }
            if (subjectEntityNew != null && !subjectEntityNew.equals(subjectEntityOld)) {
                subjectEntityNew.getCurriculumMappingEntityList().add(curriculumMappingEntity);
                subjectEntityNew = em.merge(subjectEntityNew);
            }
            if (subjectCurriculumEntityOld != null && !subjectCurriculumEntityOld.equals(subjectCurriculumEntityNew)) {
                subjectCurriculumEntityOld.getCurriculumMappingEntityList().remove(curriculumMappingEntity);
                subjectCurriculumEntityOld = em.merge(subjectCurriculumEntityOld);
            }
            if (subjectCurriculumEntityNew != null && !subjectCurriculumEntityNew.equals(subjectCurriculumEntityOld)) {
                subjectCurriculumEntityNew.getCurriculumMappingEntityList().add(curriculumMappingEntity);
                subjectCurriculumEntityNew = em.merge(subjectCurriculumEntityNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                CurriculumMappingEntityPK id = curriculumMappingEntity.getCurriculumMappingEntityPK();
                if (findCurriculumMappingEntity(id) == null) {
                    throw new NonexistentEntityException("The curriculumMappingEntity with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(CurriculumMappingEntityPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CurriculumMappingEntity curriculumMappingEntity;
            try {
                curriculumMappingEntity = em.getReference(CurriculumMappingEntity.class, id);
                curriculumMappingEntity.getCurriculumMappingEntityPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The curriculumMappingEntity with id " + id + " no longer exists.", enfe);
            }
            SubjectEntity subjectEntity = curriculumMappingEntity.getSubjectEntity();
            if (subjectEntity != null) {
                subjectEntity.getCurriculumMappingEntityList().remove(curriculumMappingEntity);
                subjectEntity = em.merge(subjectEntity);
            }
            SubjectCurriculumEntity subjectCurriculumEntity = curriculumMappingEntity.getSubjectCurriculumEntity();
            if (subjectCurriculumEntity != null) {
                subjectCurriculumEntity.getCurriculumMappingEntityList().remove(curriculumMappingEntity);
                subjectCurriculumEntity = em.merge(subjectCurriculumEntity);
            }
            em.remove(curriculumMappingEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CurriculumMappingEntity> findCurriculumMappingEntityEntities() {
        return findCurriculumMappingEntityEntities(true, -1, -1);
    }

    public List<CurriculumMappingEntity> findCurriculumMappingEntityEntities(int maxResults, int firstResult) {
        return findCurriculumMappingEntityEntities(false, maxResults, firstResult);
    }

    private List<CurriculumMappingEntity> findCurriculumMappingEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CurriculumMappingEntity.class));
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

    public CurriculumMappingEntity findCurriculumMappingEntity(CurriculumMappingEntityPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CurriculumMappingEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getCurriculumMappingEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CurriculumMappingEntity> rt = cq.from(CurriculumMappingEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
