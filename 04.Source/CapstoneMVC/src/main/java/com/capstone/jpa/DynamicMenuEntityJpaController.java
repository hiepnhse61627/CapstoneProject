/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.entities.DynamicMenuEntity;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.RolesAuthorityEntity;
import java.util.ArrayList;
import java.util.Collection;
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
public class DynamicMenuEntityJpaController implements Serializable {

    public DynamicMenuEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DynamicMenuEntity dynamicMenuEntity) throws PreexistingEntityException, Exception {
        if (dynamicMenuEntity.getRolesAuthorityEntityCollection() == null) {
            dynamicMenuEntity.setRolesAuthorityEntityCollection(new ArrayList<RolesAuthorityEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<RolesAuthorityEntity> attachedRolesAuthorityEntityCollection = new ArrayList<RolesAuthorityEntity>();
            for (RolesAuthorityEntity rolesAuthorityEntityCollectionRolesAuthorityEntityToAttach : dynamicMenuEntity.getRolesAuthorityEntityCollection()) {
                rolesAuthorityEntityCollectionRolesAuthorityEntityToAttach = em.getReference(rolesAuthorityEntityCollectionRolesAuthorityEntityToAttach.getClass(), rolesAuthorityEntityCollectionRolesAuthorityEntityToAttach.getId());
                attachedRolesAuthorityEntityCollection.add(rolesAuthorityEntityCollectionRolesAuthorityEntityToAttach);
            }
            dynamicMenuEntity.setRolesAuthorityEntityCollection(attachedRolesAuthorityEntityCollection);
            em.persist(dynamicMenuEntity);
            for (RolesAuthorityEntity rolesAuthorityEntityCollectionRolesAuthorityEntity : dynamicMenuEntity.getRolesAuthorityEntityCollection()) {
                DynamicMenuEntity oldMenuIdOfRolesAuthorityEntityCollectionRolesAuthorityEntity = rolesAuthorityEntityCollectionRolesAuthorityEntity.getMenuId();
                rolesAuthorityEntityCollectionRolesAuthorityEntity.setMenuId(dynamicMenuEntity);
                rolesAuthorityEntityCollectionRolesAuthorityEntity = em.merge(rolesAuthorityEntityCollectionRolesAuthorityEntity);
                if (oldMenuIdOfRolesAuthorityEntityCollectionRolesAuthorityEntity != null) {
                    oldMenuIdOfRolesAuthorityEntityCollectionRolesAuthorityEntity.getRolesAuthorityEntityCollection().remove(rolesAuthorityEntityCollectionRolesAuthorityEntity);
                    oldMenuIdOfRolesAuthorityEntityCollectionRolesAuthorityEntity = em.merge(oldMenuIdOfRolesAuthorityEntityCollectionRolesAuthorityEntity);
                }
            }
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

    public void edit(DynamicMenuEntity dynamicMenuEntity) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DynamicMenuEntity persistentDynamicMenuEntity = em.find(DynamicMenuEntity.class, dynamicMenuEntity.getId());
            Collection<RolesAuthorityEntity> rolesAuthorityEntityCollectionOld = persistentDynamicMenuEntity.getRolesAuthorityEntityCollection();
            Collection<RolesAuthorityEntity> rolesAuthorityEntityCollectionNew = dynamicMenuEntity.getRolesAuthorityEntityCollection();
            List<String> illegalOrphanMessages = null;
            for (RolesAuthorityEntity rolesAuthorityEntityCollectionOldRolesAuthorityEntity : rolesAuthorityEntityCollectionOld) {
                if (!rolesAuthorityEntityCollectionNew.contains(rolesAuthorityEntityCollectionOldRolesAuthorityEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RolesAuthorityEntity " + rolesAuthorityEntityCollectionOldRolesAuthorityEntity + " since its menuId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<RolesAuthorityEntity> attachedRolesAuthorityEntityCollectionNew = new ArrayList<RolesAuthorityEntity>();
            for (RolesAuthorityEntity rolesAuthorityEntityCollectionNewRolesAuthorityEntityToAttach : rolesAuthorityEntityCollectionNew) {
                rolesAuthorityEntityCollectionNewRolesAuthorityEntityToAttach = em.getReference(rolesAuthorityEntityCollectionNewRolesAuthorityEntityToAttach.getClass(), rolesAuthorityEntityCollectionNewRolesAuthorityEntityToAttach.getId());
                attachedRolesAuthorityEntityCollectionNew.add(rolesAuthorityEntityCollectionNewRolesAuthorityEntityToAttach);
            }
            rolesAuthorityEntityCollectionNew = attachedRolesAuthorityEntityCollectionNew;
            dynamicMenuEntity.setRolesAuthorityEntityCollection(rolesAuthorityEntityCollectionNew);
            dynamicMenuEntity = em.merge(dynamicMenuEntity);
            for (RolesAuthorityEntity rolesAuthorityEntityCollectionNewRolesAuthorityEntity : rolesAuthorityEntityCollectionNew) {
                if (!rolesAuthorityEntityCollectionOld.contains(rolesAuthorityEntityCollectionNewRolesAuthorityEntity)) {
                    DynamicMenuEntity oldMenuIdOfRolesAuthorityEntityCollectionNewRolesAuthorityEntity = rolesAuthorityEntityCollectionNewRolesAuthorityEntity.getMenuId();
                    rolesAuthorityEntityCollectionNewRolesAuthorityEntity.setMenuId(dynamicMenuEntity);
                    rolesAuthorityEntityCollectionNewRolesAuthorityEntity = em.merge(rolesAuthorityEntityCollectionNewRolesAuthorityEntity);
                    if (oldMenuIdOfRolesAuthorityEntityCollectionNewRolesAuthorityEntity != null && !oldMenuIdOfRolesAuthorityEntityCollectionNewRolesAuthorityEntity.equals(dynamicMenuEntity)) {
                        oldMenuIdOfRolesAuthorityEntityCollectionNewRolesAuthorityEntity.getRolesAuthorityEntityCollection().remove(rolesAuthorityEntityCollectionNewRolesAuthorityEntity);
                        oldMenuIdOfRolesAuthorityEntityCollectionNewRolesAuthorityEntity = em.merge(oldMenuIdOfRolesAuthorityEntityCollectionNewRolesAuthorityEntity);
                    }
                }
            }
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

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
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
            List<String> illegalOrphanMessages = null;
            Collection<RolesAuthorityEntity> rolesAuthorityEntityCollectionOrphanCheck = dynamicMenuEntity.getRolesAuthorityEntityCollection();
            for (RolesAuthorityEntity rolesAuthorityEntityCollectionOrphanCheckRolesAuthorityEntity : rolesAuthorityEntityCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This DynamicMenuEntity (" + dynamicMenuEntity + ") cannot be destroyed since the RolesAuthorityEntity " + rolesAuthorityEntityCollectionOrphanCheckRolesAuthorityEntity + " in its rolesAuthorityEntityCollection field has a non-nullable menuId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
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

