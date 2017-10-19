/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.entities.PrequisiteEntity;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.SubjectEntity;
import com.capstone.jpa.exceptions.NonexistentEntityException;
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

    public void create(PrequisiteEntity prequisiteEntity) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectEntity subId = prequisiteEntity.getSubId();
            if (subId != null) {
                subId = em.getReference(subId.getClass(), subId.getId());
                prequisiteEntity.setSubId(subId);
            }
            em.persist(prequisiteEntity);
            if (subId != null) {
                subId.getPrequisiteEntityList().add(prequisiteEntity);
                subId = em.merge(subId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(PrequisiteEntity prequisiteEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PrequisiteEntity persistentPrequisiteEntity = em.find(PrequisiteEntity.class, prequisiteEntity.getId());
            SubjectEntity subIdOld = persistentPrequisiteEntity.getSubId();
            SubjectEntity subIdNew = prequisiteEntity.getSubId();
            if (subIdNew != null) {
                subIdNew = em.getReference(subIdNew.getClass(), subIdNew.getId());
                prequisiteEntity.setSubId(subIdNew);
            }
            prequisiteEntity = em.merge(prequisiteEntity);
            if (subIdOld != null && !subIdOld.equals(subIdNew)) {
                subIdOld.getPrequisiteEntityList().remove(prequisiteEntity);
                subIdOld = em.merge(subIdOld);
            }
            if (subIdNew != null && !subIdNew.equals(subIdOld)) {
                subIdNew.getPrequisiteEntityList().add(prequisiteEntity);
                subIdNew = em.merge(subIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = prequisiteEntity.getId();
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

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PrequisiteEntity prequisiteEntity;
            try {
                prequisiteEntity = em.getReference(PrequisiteEntity.class, id);
                prequisiteEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The prequisiteEntity with id " + id + " no longer exists.", enfe);
            }
            SubjectEntity subId = prequisiteEntity.getSubId();
            if (subId != null) {
                subId.getPrequisiteEntityList().remove(prequisiteEntity);
                subId = em.merge(subId);
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

    public PrequisiteEntity findPrequisiteEntity(Integer id) {
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
