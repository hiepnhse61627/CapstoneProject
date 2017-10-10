/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.jpa.exceptions.*;
import com.capstone.entities.PrequisiteEntity;
import com.capstone.entities.PrequisiteEntityPK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.SubjectEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rem
 */
public class PrequisiteEntityJpaController implements Serializable {

    public PrequisiteEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(PrequisiteEntity prequisiteEntity) throws PreexistingEntityException, Exception {
        if (prequisiteEntity.getPrequisiteEntityPK() == null) {
            prequisiteEntity.setPrequisiteEntityPK(new PrequisiteEntityPK());
        }
        prequisiteEntity.getPrequisiteEntityPK().setSubId(prequisiteEntity.getSubjectEntity().getId());
        prequisiteEntity.getPrequisiteEntityPK().setPrequisiteSubId(prequisiteEntity.getPrequisiteSubjectEntity().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectEntity subjectEntity = prequisiteEntity.getSubjectEntity();
            if (subjectEntity != null) {
                subjectEntity = em.getReference(subjectEntity.getClass(), subjectEntity.getId());
                prequisiteEntity.setSubjectEntity(subjectEntity);
            }
            SubjectEntity prequisiteSubjectEntity = prequisiteEntity.getPrequisiteSubjectEntity();
            if (prequisiteSubjectEntity != null) {
                prequisiteSubjectEntity = em.getReference(prequisiteSubjectEntity.getClass(), prequisiteSubjectEntity.getId());
                prequisiteEntity.setPrequisiteSubjectEntity(prequisiteSubjectEntity);
            }
            em.persist(prequisiteEntity);
            if (subjectEntity != null) {
                subjectEntity.getSubOfPrequisiteList().add(prequisiteEntity);
                subjectEntity = em.merge(subjectEntity);
            }
            if (prequisiteSubjectEntity != null) {
                prequisiteSubjectEntity.getSubOfPrequisiteList().add(prequisiteEntity);
                prequisiteSubjectEntity = em.merge(prequisiteSubjectEntity);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPrequisiteEntity(prequisiteEntity.getPrequisiteEntityPK()) != null) {
                throw new PreexistingEntityException("PrequisiteEntity " + prequisiteEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(PrequisiteEntity prequisiteEntity) throws NonexistentEntityException, Exception {
        prequisiteEntity.getPrequisiteEntityPK().setSubId(prequisiteEntity.getSubjectEntity().getId());
        prequisiteEntity.getPrequisiteEntityPK().setPrequisiteSubId(prequisiteEntity.getPrequisiteSubjectEntity().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PrequisiteEntity persistentPrequisiteEntity = em.find(PrequisiteEntity.class, prequisiteEntity.getPrequisiteEntityPK());
            SubjectEntity subjectEntityOld = persistentPrequisiteEntity.getSubjectEntity();
            SubjectEntity subjectEntityNew = prequisiteEntity.getSubjectEntity();
            SubjectEntity prequisiteSubjectEntityOld = persistentPrequisiteEntity.getPrequisiteSubjectEntity();
            SubjectEntity prequisiteSubjectEntityNew = prequisiteEntity.getPrequisiteSubjectEntity();
            if (subjectEntityNew != null) {
                subjectEntityNew = em.getReference(subjectEntityNew.getClass(), subjectEntityNew.getId());
                prequisiteEntity.setSubjectEntity(subjectEntityNew);
            }
            if (prequisiteSubjectEntityNew != null) {
                prequisiteSubjectEntityNew = em.getReference(prequisiteSubjectEntityNew.getClass(), prequisiteSubjectEntityNew.getId());
                prequisiteEntity.setPrequisiteSubjectEntity(prequisiteSubjectEntityNew);
            }
            prequisiteEntity = em.merge(prequisiteEntity);
            if (subjectEntityOld != null && !subjectEntityOld.equals(subjectEntityNew)) {
                subjectEntityOld.getSubOfPrequisiteList().remove(prequisiteEntity);
                subjectEntityOld = em.merge(subjectEntityOld);
            }
            if (subjectEntityNew != null && !subjectEntityNew.equals(subjectEntityOld)) {
                subjectEntityNew.getSubOfPrequisiteList().add(prequisiteEntity);
                subjectEntityNew = em.merge(subjectEntityNew);
            }
            if (prequisiteSubjectEntityOld != null && !prequisiteSubjectEntityOld.equals(prequisiteSubjectEntityNew)) {
                prequisiteSubjectEntityOld.getSubOfPrequisiteList().remove(prequisiteEntity);
                prequisiteSubjectEntityOld = em.merge(prequisiteSubjectEntityOld);
            }
            if (prequisiteSubjectEntityNew != null && !prequisiteSubjectEntityNew.equals(prequisiteSubjectEntityOld)) {
                prequisiteSubjectEntityNew.getSubOfPrequisiteList().add(prequisiteEntity);
                prequisiteSubjectEntityNew = em.merge(prequisiteSubjectEntityNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                PrequisiteEntityPK id = prequisiteEntity.getPrequisiteEntityPK();
                if (findPrequisiteEntity(id) == null) {
                    throw new NonexistentEntityException("The prequisiteEntity with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(PrequisiteEntityPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PrequisiteEntity prequisiteEntity;
            try {
                prequisiteEntity = em.getReference(PrequisiteEntity.class, id);
                prequisiteEntity.getPrequisiteEntityPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The prequisiteEntity with id " + id + " no longer exists.", enfe);
            }
            SubjectEntity subjectEntity = prequisiteEntity.getSubjectEntity();
            if (subjectEntity != null) {
                subjectEntity.getSubOfPrequisiteList().remove(prequisiteEntity);
                subjectEntity = em.merge(subjectEntity);
            }
            SubjectEntity prequisiteSubjectEntity = prequisiteEntity.getPrequisiteSubjectEntity();
            if (prequisiteSubjectEntity != null) {
                prequisiteSubjectEntity.getSubOfPrequisiteList().remove(prequisiteEntity);
                prequisiteSubjectEntity = em.merge(prequisiteSubjectEntity);
            }
            em.remove(prequisiteEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<PrequisiteEntity> findPrequisiteEntityEntities() {
        return findPrequisiteEntityEntities(true, -1, -1);
    }

    public List<PrequisiteEntity> findPrequisiteEntityEntities(int maxResults, int firstResult) {
        return findPrequisiteEntityEntities(false, maxResults, firstResult);
    }

    private List<PrequisiteEntity> findPrequisiteEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PrequisiteEntity.class));
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

    public PrequisiteEntity findPrequisiteEntity(PrequisiteEntityPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PrequisiteEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getPrequisiteEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PrequisiteEntity> rt = cq.from(PrequisiteEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
