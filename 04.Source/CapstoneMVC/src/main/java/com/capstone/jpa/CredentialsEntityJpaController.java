/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;
import com.capstone.entities.CredentialsEntity;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.CredentialsRolesEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

/**
 *
 * @author StormNs
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
        if (credentialsEntity.getCredentialsRolesEntityCollection() == null) {
            credentialsEntity.setCredentialsRolesEntityCollection(new ArrayList<CredentialsRolesEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<CredentialsRolesEntity> attachedCredentialsRolesEntityCollection = new ArrayList<CredentialsRolesEntity>();
            for (CredentialsRolesEntity credentialsRolesEntityCollectionCredentialsRolesEntityToAttach : credentialsEntity.getCredentialsRolesEntityCollection()) {
                credentialsRolesEntityCollectionCredentialsRolesEntityToAttach = em.getReference(credentialsRolesEntityCollectionCredentialsRolesEntityToAttach.getClass(), credentialsRolesEntityCollectionCredentialsRolesEntityToAttach.getId());
                attachedCredentialsRolesEntityCollection.add(credentialsRolesEntityCollectionCredentialsRolesEntityToAttach);
            }
            credentialsEntity.setCredentialsRolesEntityCollection(attachedCredentialsRolesEntityCollection);
            em.persist(credentialsEntity);
            for (CredentialsRolesEntity credentialsRolesEntityCollectionCredentialsRolesEntity : credentialsEntity.getCredentialsRolesEntityCollection()) {
                CredentialsEntity oldCredentialsIdOfCredentialsRolesEntityCollectionCredentialsRolesEntity = credentialsRolesEntityCollectionCredentialsRolesEntity.getCredentialsId();
                credentialsRolesEntityCollectionCredentialsRolesEntity.setCredentialsId(credentialsEntity);
                credentialsRolesEntityCollectionCredentialsRolesEntity = em.merge(credentialsRolesEntityCollectionCredentialsRolesEntity);
                if (oldCredentialsIdOfCredentialsRolesEntityCollectionCredentialsRolesEntity != null) {
                    oldCredentialsIdOfCredentialsRolesEntityCollectionCredentialsRolesEntity.getCredentialsRolesEntityCollection().remove(credentialsRolesEntityCollectionCredentialsRolesEntity);
                    oldCredentialsIdOfCredentialsRolesEntityCollectionCredentialsRolesEntity = em.merge(oldCredentialsIdOfCredentialsRolesEntityCollectionCredentialsRolesEntity);
                }
            }
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
            CredentialsEntity persistentCredentialsEntity = em.find(CredentialsEntity.class, credentialsEntity.getId());
            Collection<CredentialsRolesEntity> credentialsRolesEntityCollectionOld = persistentCredentialsEntity.getCredentialsRolesEntityCollection();
            Collection<CredentialsRolesEntity> credentialsRolesEntityCollectionNew = credentialsEntity.getCredentialsRolesEntityCollection();
            Collection<CredentialsRolesEntity> attachedCredentialsRolesEntityCollectionNew = new ArrayList<CredentialsRolesEntity>();
            for (CredentialsRolesEntity credentialsRolesEntityCollectionNewCredentialsRolesEntityToAttach : credentialsRolesEntityCollectionNew) {
                credentialsRolesEntityCollectionNewCredentialsRolesEntityToAttach = em.getReference(credentialsRolesEntityCollectionNewCredentialsRolesEntityToAttach.getClass(), credentialsRolesEntityCollectionNewCredentialsRolesEntityToAttach.getId());
                attachedCredentialsRolesEntityCollectionNew.add(credentialsRolesEntityCollectionNewCredentialsRolesEntityToAttach);
            }
            credentialsRolesEntityCollectionNew = attachedCredentialsRolesEntityCollectionNew;
            credentialsEntity.setCredentialsRolesEntityCollection(credentialsRolesEntityCollectionNew);
            credentialsEntity = em.merge(credentialsEntity);
            for (CredentialsRolesEntity credentialsRolesEntityCollectionOldCredentialsRolesEntity : credentialsRolesEntityCollectionOld) {
                if (!credentialsRolesEntityCollectionNew.contains(credentialsRolesEntityCollectionOldCredentialsRolesEntity)) {
                    credentialsRolesEntityCollectionOldCredentialsRolesEntity.setCredentialsId(null);
                    credentialsRolesEntityCollectionOldCredentialsRolesEntity = em.merge(credentialsRolesEntityCollectionOldCredentialsRolesEntity);
                }
            }
            for (CredentialsRolesEntity credentialsRolesEntityCollectionNewCredentialsRolesEntity : credentialsRolesEntityCollectionNew) {
                if (!credentialsRolesEntityCollectionOld.contains(credentialsRolesEntityCollectionNewCredentialsRolesEntity)) {
                    CredentialsEntity oldCredentialsIdOfCredentialsRolesEntityCollectionNewCredentialsRolesEntity = credentialsRolesEntityCollectionNewCredentialsRolesEntity.getCredentialsId();
                    credentialsRolesEntityCollectionNewCredentialsRolesEntity.setCredentialsId(credentialsEntity);
                    credentialsRolesEntityCollectionNewCredentialsRolesEntity = em.merge(credentialsRolesEntityCollectionNewCredentialsRolesEntity);
                    if (oldCredentialsIdOfCredentialsRolesEntityCollectionNewCredentialsRolesEntity != null && !oldCredentialsIdOfCredentialsRolesEntityCollectionNewCredentialsRolesEntity.equals(credentialsEntity)) {
                        oldCredentialsIdOfCredentialsRolesEntityCollectionNewCredentialsRolesEntity.getCredentialsRolesEntityCollection().remove(credentialsRolesEntityCollectionNewCredentialsRolesEntity);
                        oldCredentialsIdOfCredentialsRolesEntityCollectionNewCredentialsRolesEntity = em.merge(oldCredentialsIdOfCredentialsRolesEntityCollectionNewCredentialsRolesEntity);
                    }
                }
            }
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
            Collection<CredentialsRolesEntity> credentialsRolesEntityCollection = credentialsEntity.getCredentialsRolesEntityCollection();
            for (CredentialsRolesEntity credentialsRolesEntityCollectionCredentialsRolesEntity : credentialsRolesEntityCollection) {
                credentialsRolesEntityCollectionCredentialsRolesEntity.setCredentialsId(null);
                credentialsRolesEntityCollectionCredentialsRolesEntity = em.merge(credentialsRolesEntityCollectionCredentialsRolesEntity);
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