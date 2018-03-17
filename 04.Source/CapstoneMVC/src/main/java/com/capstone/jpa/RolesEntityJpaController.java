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
        if (rolesEntity.getRolesAuthorityEntityList() == null) {
            rolesEntity.setRolesAuthorityEntityList(new ArrayList<RolesAuthorityEntity>());
        }
        if (rolesEntity.getCredentialsRolesEntityList() == null) {
            rolesEntity.setCredentialsRolesEntityList(new ArrayList<CredentialsRolesEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<RolesAuthorityEntity> attachedRolesAuthorityEntityList = new ArrayList<RolesAuthorityEntity>();
            for (RolesAuthorityEntity rolesAuthorityEntityListRolesAuthorityEntityToAttach : rolesEntity.getRolesAuthorityEntityList()) {
                rolesAuthorityEntityListRolesAuthorityEntityToAttach = em.getReference(rolesAuthorityEntityListRolesAuthorityEntityToAttach.getClass(), rolesAuthorityEntityListRolesAuthorityEntityToAttach.getId());
                attachedRolesAuthorityEntityList.add(rolesAuthorityEntityListRolesAuthorityEntityToAttach);
            }
            rolesEntity.setRolesAuthorityEntityList(attachedRolesAuthorityEntityList);
            List<CredentialsRolesEntity> attachedCredentialsRolesEntityList = new ArrayList<CredentialsRolesEntity>();
            for (CredentialsRolesEntity credentialsRolesEntityListCredentialsRolesEntityToAttach : rolesEntity.getCredentialsRolesEntityList()) {
                credentialsRolesEntityListCredentialsRolesEntityToAttach = em.getReference(credentialsRolesEntityListCredentialsRolesEntityToAttach.getClass(), credentialsRolesEntityListCredentialsRolesEntityToAttach.getId());
                attachedCredentialsRolesEntityList.add(credentialsRolesEntityListCredentialsRolesEntityToAttach);
            }
            rolesEntity.setCredentialsRolesEntityList(attachedCredentialsRolesEntityList);
            em.persist(rolesEntity);
            for (RolesAuthorityEntity rolesAuthorityEntityListRolesAuthorityEntity : rolesEntity.getRolesAuthorityEntityList()) {
                RolesEntity oldRolesIdOfRolesAuthorityEntityListRolesAuthorityEntity = rolesAuthorityEntityListRolesAuthorityEntity.getRolesId();
                rolesAuthorityEntityListRolesAuthorityEntity.setRolesId(rolesEntity);
                rolesAuthorityEntityListRolesAuthorityEntity = em.merge(rolesAuthorityEntityListRolesAuthorityEntity);
                if (oldRolesIdOfRolesAuthorityEntityListRolesAuthorityEntity != null) {
                    oldRolesIdOfRolesAuthorityEntityListRolesAuthorityEntity.getRolesAuthorityEntityList().remove(rolesAuthorityEntityListRolesAuthorityEntity);
                    oldRolesIdOfRolesAuthorityEntityListRolesAuthorityEntity = em.merge(oldRolesIdOfRolesAuthorityEntityListRolesAuthorityEntity);
                }
            }
            for (CredentialsRolesEntity credentialsRolesEntityListCredentialsRolesEntity : rolesEntity.getCredentialsRolesEntityList()) {
                RolesEntity oldRolesIdOfCredentialsRolesEntityListCredentialsRolesEntity = credentialsRolesEntityListCredentialsRolesEntity.getRolesId();
                credentialsRolesEntityListCredentialsRolesEntity.setRolesId(rolesEntity);
                credentialsRolesEntityListCredentialsRolesEntity = em.merge(credentialsRolesEntityListCredentialsRolesEntity);
                if (oldRolesIdOfCredentialsRolesEntityListCredentialsRolesEntity != null) {
                    oldRolesIdOfCredentialsRolesEntityListCredentialsRolesEntity.getCredentialsRolesEntityList().remove(credentialsRolesEntityListCredentialsRolesEntity);
                    oldRolesIdOfCredentialsRolesEntityListCredentialsRolesEntity = em.merge(oldRolesIdOfCredentialsRolesEntityListCredentialsRolesEntity);
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
            List<RolesAuthorityEntity> rolesAuthorityEntityListOld = persistentRolesEntity.getRolesAuthorityEntityList();
            List<RolesAuthorityEntity> rolesAuthorityEntityListNew = rolesEntity.getRolesAuthorityEntityList();
            List<CredentialsRolesEntity> credentialsRolesEntityListOld = persistentRolesEntity.getCredentialsRolesEntityList();
            List<CredentialsRolesEntity> credentialsRolesEntityListNew = rolesEntity.getCredentialsRolesEntityList();
            List<String> illegalOrphanMessages = null;
            for (RolesAuthorityEntity rolesAuthorityEntityListOldRolesAuthorityEntity : rolesAuthorityEntityListOld) {
                if (!rolesAuthorityEntityListNew.contains(rolesAuthorityEntityListOldRolesAuthorityEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RolesAuthorityEntity " + rolesAuthorityEntityListOldRolesAuthorityEntity + " since its rolesId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<RolesAuthorityEntity> attachedRolesAuthorityEntityListNew = new ArrayList<RolesAuthorityEntity>();
            for (RolesAuthorityEntity rolesAuthorityEntityListNewRolesAuthorityEntityToAttach : rolesAuthorityEntityListNew) {
                rolesAuthorityEntityListNewRolesAuthorityEntityToAttach = em.getReference(rolesAuthorityEntityListNewRolesAuthorityEntityToAttach.getClass(), rolesAuthorityEntityListNewRolesAuthorityEntityToAttach.getId());
                attachedRolesAuthorityEntityListNew.add(rolesAuthorityEntityListNewRolesAuthorityEntityToAttach);
            }
            rolesAuthorityEntityListNew = attachedRolesAuthorityEntityListNew;
            rolesEntity.setRolesAuthorityEntityList(rolesAuthorityEntityListNew);
            List<CredentialsRolesEntity> attachedCredentialsRolesEntityListNew = new ArrayList<CredentialsRolesEntity>();
            for (CredentialsRolesEntity credentialsRolesEntityListNewCredentialsRolesEntityToAttach : credentialsRolesEntityListNew) {
                credentialsRolesEntityListNewCredentialsRolesEntityToAttach = em.getReference(credentialsRolesEntityListNewCredentialsRolesEntityToAttach.getClass(), credentialsRolesEntityListNewCredentialsRolesEntityToAttach.getId());
                attachedCredentialsRolesEntityListNew.add(credentialsRolesEntityListNewCredentialsRolesEntityToAttach);
            }
            credentialsRolesEntityListNew = attachedCredentialsRolesEntityListNew;
            rolesEntity.setCredentialsRolesEntityList(credentialsRolesEntityListNew);
            rolesEntity = em.merge(rolesEntity);
            for (RolesAuthorityEntity rolesAuthorityEntityListNewRolesAuthorityEntity : rolesAuthorityEntityListNew) {
                if (!rolesAuthorityEntityListOld.contains(rolesAuthorityEntityListNewRolesAuthorityEntity)) {
                    RolesEntity oldRolesIdOfRolesAuthorityEntityListNewRolesAuthorityEntity = rolesAuthorityEntityListNewRolesAuthorityEntity.getRolesId();
                    rolesAuthorityEntityListNewRolesAuthorityEntity.setRolesId(rolesEntity);
                    rolesAuthorityEntityListNewRolesAuthorityEntity = em.merge(rolesAuthorityEntityListNewRolesAuthorityEntity);
                    if (oldRolesIdOfRolesAuthorityEntityListNewRolesAuthorityEntity != null && !oldRolesIdOfRolesAuthorityEntityListNewRolesAuthorityEntity.equals(rolesEntity)) {
                        oldRolesIdOfRolesAuthorityEntityListNewRolesAuthorityEntity.getRolesAuthorityEntityList().remove(rolesAuthorityEntityListNewRolesAuthorityEntity);
                        oldRolesIdOfRolesAuthorityEntityListNewRolesAuthorityEntity = em.merge(oldRolesIdOfRolesAuthorityEntityListNewRolesAuthorityEntity);
                    }
                }
            }
            for (CredentialsRolesEntity credentialsRolesEntityListOldCredentialsRolesEntity : credentialsRolesEntityListOld) {
                if (!credentialsRolesEntityListNew.contains(credentialsRolesEntityListOldCredentialsRolesEntity)) {
                    credentialsRolesEntityListOldCredentialsRolesEntity.setRolesId(null);
                    credentialsRolesEntityListOldCredentialsRolesEntity = em.merge(credentialsRolesEntityListOldCredentialsRolesEntity);
                }
            }
            for (CredentialsRolesEntity credentialsRolesEntityListNewCredentialsRolesEntity : credentialsRolesEntityListNew) {
                if (!credentialsRolesEntityListOld.contains(credentialsRolesEntityListNewCredentialsRolesEntity)) {
                    RolesEntity oldRolesIdOfCredentialsRolesEntityListNewCredentialsRolesEntity = credentialsRolesEntityListNewCredentialsRolesEntity.getRolesId();
                    credentialsRolesEntityListNewCredentialsRolesEntity.setRolesId(rolesEntity);
                    credentialsRolesEntityListNewCredentialsRolesEntity = em.merge(credentialsRolesEntityListNewCredentialsRolesEntity);
                    if (oldRolesIdOfCredentialsRolesEntityListNewCredentialsRolesEntity != null && !oldRolesIdOfCredentialsRolesEntityListNewCredentialsRolesEntity.equals(rolesEntity)) {
                        oldRolesIdOfCredentialsRolesEntityListNewCredentialsRolesEntity.getCredentialsRolesEntityList().remove(credentialsRolesEntityListNewCredentialsRolesEntity);
                        oldRolesIdOfCredentialsRolesEntityListNewCredentialsRolesEntity = em.merge(oldRolesIdOfCredentialsRolesEntityListNewCredentialsRolesEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = rolesEntity.getId();
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

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
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
            List<RolesAuthorityEntity> rolesAuthorityEntityListOrphanCheck = rolesEntity.getRolesAuthorityEntityList();
            for (RolesAuthorityEntity rolesAuthorityEntityListOrphanCheckRolesAuthorityEntity : rolesAuthorityEntityListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This RolesEntity (" + rolesEntity + ") cannot be destroyed since the RolesAuthorityEntity " + rolesAuthorityEntityListOrphanCheckRolesAuthorityEntity + " in its rolesAuthorityEntityList field has a non-nullable rolesId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<CredentialsRolesEntity> credentialsRolesEntityList = rolesEntity.getCredentialsRolesEntityList();
            for (CredentialsRolesEntity credentialsRolesEntityListCredentialsRolesEntity : credentialsRolesEntityList) {
                credentialsRolesEntityListCredentialsRolesEntity.setRolesId(null);
                credentialsRolesEntityListCredentialsRolesEntity = em.merge(credentialsRolesEntityListCredentialsRolesEntity);
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

    public RolesEntity findRolesEntity(Integer id) {
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
