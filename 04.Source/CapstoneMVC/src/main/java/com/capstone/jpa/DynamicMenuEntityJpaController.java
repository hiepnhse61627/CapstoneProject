/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.entities.DynamicMenuEntity;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Rem
 */
public class DynamicMenuEntityJpaController implements Serializable {

    public DynamicMenuEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DynamicMenuEntity dynamicMenuEntity) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(dynamicMenuEntity);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDynamicMenuEntity(dynamicMenuEntity.getId()) != null) {
                throw new PreexistingEntityException("DynamicMenuEntity " + dynamicMenuEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DynamicMenuEntity dynamicMenuEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            dynamicMenuEntity = em.merge(dynamicMenuEntity);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = dynamicMenuEntity.getId();
                if (findDynamicMenuEntity(id) == null) {
                    throw new NonexistentEntityException("The dynamicMenuEntity with id " + id + " no longer exists.");
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
            DynamicMenuEntity dynamicMenuEntity;
            try {
                dynamicMenuEntity = em.getReference(DynamicMenuEntity.class, id);
                dynamicMenuEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The dynamicMenuEntity with id " + id + " no longer exists.", enfe);
            }
            em.remove(dynamicMenuEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DynamicMenuEntity> findDynamicMenuEntityEntities() {
        return findDynamicMenuEntityEntities(true, -1, -1);
    }

    public List<DynamicMenuEntity> findDynamicMenuEntityEntities(int maxResults, int firstResult) {
        return findDynamicMenuEntityEntities(false, maxResults, firstResult);
    }

    private List<DynamicMenuEntity> findDynamicMenuEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DynamicMenuEntity.class));
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

    public DynamicMenuEntity findDynamicMenuEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DynamicMenuEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getDynamicMenuEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DynamicMenuEntity> rt = cq.from(DynamicMenuEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
