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
import com.capstone.entities.CredentialsRolesEntity;
import java.util.ArrayList;
import java.util.Collection;
import com.capstone.entities.RolesAuthorityEntity;
import com.capstone.entities.RolesEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.capstone.jpa.exceptions.IllegalOrphanException;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

/**
 *
 * @author StormNs
 */
public class RolesEntityJpaController implements Serializable {

    public RolesEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RolesEntity rolesEntity) throws PreexistingEntityException, Exception {
        if (rolesEntity.getCredentialsRolesEntityCollection() == null) {
            rolesEntity.setCredentialsRolesEntityCollection(new ArrayList<CredentialsRolesEntity>());
        }
        if (rolesEntity.getRolesAuthorityEntityCollection() == null) {
            rolesEntity.setRolesAuthorityEntityCollection(new ArrayList<RolesAuthorityEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<CredentialsRolesEntity> attachedCredentialsRolesEntityCollection = new ArrayList<CredentialsRolesEntity>();
            for (CredentialsRolesEntity credentialsRolesEntityCollectionCredentialsRolesEntityToAttach : rolesEntity.getCredentialsRolesEntityCollection()) {
                credentialsRolesEntityCollectionCredentialsRolesEntityToAttach = em.getReference(credentialsRolesEntityCollectionCredentialsRolesEntityToAttach.getClass(), credentialsRolesEntityCollectionCredentialsRolesEntityToAttach.getId());
                attachedCredentialsRolesEntityCollection.add(credentialsRolesEntityCollectionCredentialsRolesEntityToAttach);
            }
            rolesEntity.setCredentialsRolesEntityCollection(attachedCredentialsRolesEntityCollection);
            Collection<RolesAuthorityEntity> attachedRolesAuthorityEntityCollection = new ArrayList<RolesAuthorityEntity>();
            for (RolesAuthorityEntity rolesAuthorityEntityCollectionRolesAuthorityEntityToAttach : rolesEntity.getRolesAuthorityEntityCollection()) {
                rolesAuthorityEntityCollectionRolesAuthorityEntityToAttach = em.getReference(rolesAuthorityEntityCollectionRolesAuthorityEntityToAttach.getClass(), rolesAuthorityEntityCollectionRolesAuthorityEntityToAttach.getId());
                attachedRolesAuthorityEntityCollection.add(rolesAuthorityEntityCollectionRolesAuthorityEntityToAttach);
            }
            rolesEntity.setRolesAuthorityEntityCollection(attachedRolesAuthorityEntityCollection);
            em.persist(rolesEntity);
            for (CredentialsRolesEntity credentialsRolesEntityCollectionCredentialsRolesEntity : rolesEntity.getCredentialsRolesEntityCollection()) {
                RolesEntity oldRolesIdOfCredentialsRolesEntityCollectionCredentialsRolesEntity = credentialsRolesEntityCollectionCredentialsRolesEntity.getRolesId();
                credentialsRolesEntityCollectionCredentialsRolesEntity.setRolesId(rolesEntity);
                credentialsRolesEntityCollectionCredentialsRolesEntity = em.merge(credentialsRolesEntityCollectionCredentialsRolesEntity);
                if (oldRolesIdOfCredentialsRolesEntityCollectionCredentialsRolesEntity != null) {
                    oldRolesIdOfCredentialsRolesEntityCollectionCredentialsRolesEntity.getCredentialsRolesEntityCollection().remove(credentialsRolesEntityCollectionCredentialsRolesEntity);
                    oldRolesIdOfCredentialsRolesEntityCollectionCredentialsRolesEntity = em.merge(oldRolesIdOfCredentialsRolesEntityCollectionCredentialsRolesEntity);
                }
            }
            for (RolesAuthorityEntity rolesAuthorityEntityCollectionRolesAuthorityEntity : rolesEntity.getRolesAuthorityEntityCollection()) {
                RolesEntity oldRolesIdOfRolesAuthorityEntityCollectionRolesAuthorityEntity = rolesAuthorityEntityCollectionRolesAuthorityEntity.getRolesId();
                rolesAuthorityEntityCollectionRolesAuthorityEntity.setRolesId(rolesEntity);
                rolesAuthorityEntityCollectionRolesAuthorityEntity = em.merge(rolesAuthorityEntityCollectionRolesAuthorityEntity);
                if (oldRolesIdOfRolesAuthorityEntityCollectionRolesAuthorityEntity != null) {
                    oldRolesIdOfRolesAuthorityEntityCollectionRolesAuthorityEntity.getRolesAuthorityEntityCollection().remove(rolesAuthorityEntityCollectionRolesAuthorityEntity);
                    oldRolesIdOfRolesAuthorityEntityCollectionRolesAuthorityEntity = em.merge(oldRolesIdOfRolesAuthorityEntityCollectionRolesAuthorityEntity);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRolesEntity(rolesEntity.getId()) != null) {
                throw new PreexistingEntityException("RolesEntity " + rolesEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RolesEntity rolesEntity) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RolesEntity persistentRolesEntity = em.find(RolesEntity.class, rolesEntity.getId());
            Collection<CredentialsRolesEntity> credentialsRolesEntityCollectionOld = persistentRolesEntity.getCredentialsRolesEntityCollection();
            Collection<CredentialsRolesEntity> credentialsRolesEntityCollectionNew = rolesEntity.getCredentialsRolesEntityCollection();
            Collection<RolesAuthorityEntity> rolesAuthorityEntityCollectionOld = persistentRolesEntity.getRolesAuthorityEntityCollection();
            Collection<RolesAuthorityEntity> rolesAuthorityEntityCollectionNew = rolesEntity.getRolesAuthorityEntityCollection();
            List<String> illegalOrphanMessages = null;
            for (RolesAuthorityEntity rolesAuthorityEntityCollectionOldRolesAuthorityEntity : rolesAuthorityEntityCollectionOld) {
                if (!rolesAuthorityEntityCollectionNew.contains(rolesAuthorityEntityCollectionOldRolesAuthorityEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RolesAuthorityEntity " + rolesAuthorityEntityCollectionOldRolesAuthorityEntity + " since its rolesId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<CredentialsRolesEntity> attachedCredentialsRolesEntityCollectionNew = new ArrayList<CredentialsRolesEntity>();
            for (CredentialsRolesEntity credentialsRolesEntityCollectionNewCredentialsRolesEntityToAttach : credentialsRolesEntityCollectionNew) {
                credentialsRolesEntityCollectionNewCredentialsRolesEntityToAttach = em.getReference(credentialsRolesEntityCollectionNewCredentialsRolesEntityToAttach.getClass(), credentialsRolesEntityCollectionNewCredentialsRolesEntityToAttach.getId());
                attachedCredentialsRolesEntityCollectionNew.add(credentialsRolesEntityCollectionNewCredentialsRolesEntityToAttach);
            }
            credentialsRolesEntityCollectionNew = attachedCredentialsRolesEntityCollectionNew;
            rolesEntity.setCredentialsRolesEntityCollection(credentialsRolesEntityCollectionNew);
            Collection<RolesAuthorityEntity> attachedRolesAuthorityEntityCollectionNew = new ArrayList<RolesAuthorityEntity>();
            for (RolesAuthorityEntity rolesAuthorityEntityCollectionNewRolesAuthorityEntityToAttach : rolesAuthorityEntityCollectionNew) {
                rolesAuthorityEntityCollectionNewRolesAuthorityEntityToAttach = em.getReference(rolesAuthorityEntityCollectionNewRolesAuthorityEntityToAttach.getClass(), rolesAuthorityEntityCollectionNewRolesAuthorityEntityToAttach.getId());
                attachedRolesAuthorityEntityCollectionNew.add(rolesAuthorityEntityCollectionNewRolesAuthorityEntityToAttach);
            }
            rolesAuthorityEntityCollectionNew = attachedRolesAuthorityEntityCollectionNew;
            rolesEntity.setRolesAuthorityEntityCollection(rolesAuthorityEntityCollectionNew);
            rolesEntity = em.merge(rolesEntity);
            for (CredentialsRolesEntity credentialsRolesEntityCollectionOldCredentialsRolesEntity : credentialsRolesEntityCollectionOld) {
                if (!credentialsRolesEntityCollectionNew.contains(credentialsRolesEntityCollectionOldCredentialsRolesEntity)) {
                    credentialsRolesEntityCollectionOldCredentialsRolesEntity.setRolesId(null);
                    credentialsRolesEntityCollectionOldCredentialsRolesEntity = em.merge(credentialsRolesEntityCollectionOldCredentialsRolesEntity);
                }
            }
            for (CredentialsRolesEntity credentialsRolesEntityCollectionNewCredentialsRolesEntity : credentialsRolesEntityCollectionNew) {
                if (!credentialsRolesEntityCollectionOld.contains(credentialsRolesEntityCollectionNewCredentialsRolesEntity)) {
                    RolesEntity oldRolesIdOfCredentialsRolesEntityCollectionNewCredentialsRolesEntity = credentialsRolesEntityCollectionNewCredentialsRolesEntity.getRolesId();
                    credentialsRolesEntityCollectionNewCredentialsRolesEntity.setRolesId(rolesEntity);
                    credentialsRolesEntityCollectionNewCredentialsRolesEntity = em.merge(credentialsRolesEntityCollectionNewCredentialsRolesEntity);
                    if (oldRolesIdOfCredentialsRolesEntityCollectionNewCredentialsRolesEntity != null && !oldRolesIdOfCredentialsRolesEntityCollectionNewCredentialsRolesEntity.equals(rolesEntity)) {
                        oldRolesIdOfCredentialsRolesEntityCollectionNewCredentialsRolesEntity.getCredentialsRolesEntityCollection().remove(credentialsRolesEntityCollectionNewCredentialsRolesEntity);
                        oldRolesIdOfCredentialsRolesEntityCollectionNewCredentialsRolesEntity = em.merge(oldRolesIdOfCredentialsRolesEntityCollectionNewCredentialsRolesEntity);
                    }
                }
            }
            for (RolesAuthorityEntity rolesAuthorityEntityCollectionNewRolesAuthorityEntity : rolesAuthorityEntityCollectionNew) {
                if (!rolesAuthorityEntityCollectionOld.contains(rolesAuthorityEntityCollectionNewRolesAuthorityEntity)) {
                    RolesEntity oldRolesIdOfRolesAuthorityEntityCollectionNewRolesAuthorityEntity = rolesAuthorityEntityCollectionNewRolesAuthorityEntity.getRolesId();
                    rolesAuthorityEntityCollectionNewRolesAuthorityEntity.setRolesId(rolesEntity);
                    rolesAuthorityEntityCollectionNewRolesAuthorityEntity = em.merge(rolesAuthorityEntityCollectionNewRolesAuthorityEntity);
                    if (oldRolesIdOfRolesAuthorityEntityCollectionNewRolesAuthorityEntity != null && !oldRolesIdOfRolesAuthorityEntityCollectionNewRolesAuthorityEntity.equals(rolesEntity)) {
                        oldRolesIdOfRolesAuthorityEntityCollectionNewRolesAuthorityEntity.getRolesAuthorityEntityCollection().remove(rolesAuthorityEntityCollectionNewRolesAuthorityEntity);
                        oldRolesIdOfRolesAuthorityEntityCollectionNewRolesAuthorityEntity = em.merge(oldRolesIdOfRolesAuthorityEntityCollectionNewRolesAuthorityEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = rolesEntity.getId();
                if (findRolesEntity(id) == null) {
                    throw new NonexistentEntityException("The rolesEntity with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RolesEntity rolesEntity;
            try {
                rolesEntity = em.getReference(RolesEntity.class, id);
                rolesEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rolesEntity with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<RolesAuthorityEntity> rolesAuthorityEntityCollectionOrphanCheck = rolesEntity.getRolesAuthorityEntityCollection();
            for (RolesAuthorityEntity rolesAuthorityEntityCollectionOrphanCheckRolesAuthorityEntity : rolesAuthorityEntityCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This RolesEntity (" + rolesEntity + ") cannot be destroyed since the RolesAuthorityEntity " + rolesAuthorityEntityCollectionOrphanCheckRolesAuthorityEntity + " in its rolesAuthorityEntityCollection field has a non-nullable rolesId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<CredentialsRolesEntity> credentialsRolesEntityCollection = rolesEntity.getCredentialsRolesEntityCollection();
            for (CredentialsRolesEntity credentialsRolesEntityCollectionCredentialsRolesEntity : credentialsRolesEntityCollection) {
                credentialsRolesEntityCollectionCredentialsRolesEntity.setRolesId(null);
                credentialsRolesEntityCollectionCredentialsRolesEntity = em.merge(credentialsRolesEntityCollectionCredentialsRolesEntity);
            }
            em.remove(rolesEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RolesEntity> findRolesEntityEntities() {
        return findRolesEntityEntities(true, -1, -1);
    }

    public List<RolesEntity> findRolesEntityEntities(int maxResults, int firstResult) {
        return findRolesEntityEntities(false, maxResults, firstResult);
    }

    private List<RolesEntity> findRolesEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RolesEntity.class));
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

    public RolesEntity findRolesEntity(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RolesEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getRolesEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RolesEntity> rt = cq.from(RolesEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
