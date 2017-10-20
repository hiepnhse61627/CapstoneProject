/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.entities.CredentialsEntity;
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
public class CredentialsEntityJpaController implements Serializable {

    public CredentialsEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CredentialsEntity credentialsEntity) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(credentialsEntity);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCredentialsEntity(credentialsEntity.getId()) != null) {
                throw new PreexistingEntityException("CredentialsEntity " + credentialsEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CredentialsEntity credentialsEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            credentialsEntity = em.merge(credentialsEntity);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = credentialsEntity.getId();
                if (findCredentialsEntity(id) == null) {
                    throw new NonexistentEntityException("The credentialsEntity with id " + id + " no longer exists.");
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
            CredentialsEntity credentialsEntity;
            try {
                credentialsEntity = em.getReference(CredentialsEntity.class, id);
                credentialsEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The credentialsEntity with id " + id + " no longer exists.", enfe);
            }
            em.remove(credentialsEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CredentialsEntity> findCredentialsEntityEntities() {
        return findCredentialsEntityEntities(true, -1, -1);
    }

    public List<CredentialsEntity> findCredentialsEntityEntities(int maxResults, int firstResult) {
        return findCredentialsEntityEntities(false, maxResults, firstResult);
    }

    private List<CredentialsEntity> findCredentialsEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CredentialsEntity.class));
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

    public CredentialsEntity findCredentialsEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CredentialsEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getCredentialsEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CredentialsEntity> rt = cq.from(CredentialsEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
