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
        if (dynamicMenuEntity.getRolesAuthorityEntityList() == null) {
            dynamicMenuEntity.setRolesAuthorityEntityList(new ArrayList<RolesAuthorityEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<RolesAuthorityEntity> attachedRolesAuthorityEntityList = new ArrayList<RolesAuthorityEntity>();
            for (RolesAuthorityEntity rolesAuthorityEntityListRolesAuthorityEntityToAttach : dynamicMenuEntity.getRolesAuthorityEntityList()) {
                rolesAuthorityEntityListRolesAuthorityEntityToAttach = em.getReference(rolesAuthorityEntityListRolesAuthorityEntityToAttach.getClass(), rolesAuthorityEntityListRolesAuthorityEntityToAttach.getId());
                attachedRolesAuthorityEntityList.add(rolesAuthorityEntityListRolesAuthorityEntityToAttach);
            }
            dynamicMenuEntity.setRolesAuthorityEntityList(attachedRolesAuthorityEntityList);
            em.persist(dynamicMenuEntity);
            for (RolesAuthorityEntity rolesAuthorityEntityListRolesAuthorityEntity : dynamicMenuEntity.getRolesAuthorityEntityList()) {
                DynamicMenuEntity oldMenuIdOfRolesAuthorityEntityListRolesAuthorityEntity = rolesAuthorityEntityListRolesAuthorityEntity.getMenuId();
                rolesAuthorityEntityListRolesAuthorityEntity.setMenuId(dynamicMenuEntity);
                rolesAuthorityEntityListRolesAuthorityEntity = em.merge(rolesAuthorityEntityListRolesAuthorityEntity);
                if (oldMenuIdOfRolesAuthorityEntityListRolesAuthorityEntity != null) {
                    oldMenuIdOfRolesAuthorityEntityListRolesAuthorityEntity.getRolesAuthorityEntityList().remove(rolesAuthorityEntityListRolesAuthorityEntity);
                    oldMenuIdOfRolesAuthorityEntityListRolesAuthorityEntity = em.merge(oldMenuIdOfRolesAuthorityEntityListRolesAuthorityEntity);
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
            List<RolesAuthorityEntity> rolesAuthorityEntityListOld = persistentDynamicMenuEntity.getRolesAuthorityEntityList();
            List<RolesAuthorityEntity> rolesAuthorityEntityListNew = dynamicMenuEntity.getRolesAuthorityEntityList();
            List<String> illegalOrphanMessages = null;
            for (RolesAuthorityEntity rolesAuthorityEntityListOldRolesAuthorityEntity : rolesAuthorityEntityListOld) {
                if (!rolesAuthorityEntityListNew.contains(rolesAuthorityEntityListOldRolesAuthorityEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain RolesAuthorityEntity " + rolesAuthorityEntityListOldRolesAuthorityEntity + " since its menuId field is not nullable.");
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
            dynamicMenuEntity.setRolesAuthorityEntityList(rolesAuthorityEntityListNew);
            dynamicMenuEntity = em.merge(dynamicMenuEntity);
            for (RolesAuthorityEntity rolesAuthorityEntityListNewRolesAuthorityEntity : rolesAuthorityEntityListNew) {
                if (!rolesAuthorityEntityListOld.contains(rolesAuthorityEntityListNewRolesAuthorityEntity)) {
                    DynamicMenuEntity oldMenuIdOfRolesAuthorityEntityListNewRolesAuthorityEntity = rolesAuthorityEntityListNewRolesAuthorityEntity.getMenuId();
                    rolesAuthorityEntityListNewRolesAuthorityEntity.setMenuId(dynamicMenuEntity);
                    rolesAuthorityEntityListNewRolesAuthorityEntity = em.merge(rolesAuthorityEntityListNewRolesAuthorityEntity);
                    if (oldMenuIdOfRolesAuthorityEntityListNewRolesAuthorityEntity != null && !oldMenuIdOfRolesAuthorityEntityListNewRolesAuthorityEntity.equals(dynamicMenuEntity)) {
                        oldMenuIdOfRolesAuthorityEntityListNewRolesAuthorityEntity.getRolesAuthorityEntityList().remove(rolesAuthorityEntityListNewRolesAuthorityEntity);
                        oldMenuIdOfRolesAuthorityEntityListNewRolesAuthorityEntity = em.merge(oldMenuIdOfRolesAuthorityEntityListNewRolesAuthorityEntity);
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
            List<RolesAuthorityEntity> rolesAuthorityEntityListOrphanCheck = dynamicMenuEntity.getRolesAuthorityEntityList();
            for (RolesAuthorityEntity rolesAuthorityEntityListOrphanCheckRolesAuthorityEntity : rolesAuthorityEntityListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This DynamicMenuEntity (" + dynamicMenuEntity + ") cannot be destroyed since the RolesAuthorityEntity " + rolesAuthorityEntityListOrphanCheckRolesAuthorityEntity + " in its rolesAuthorityEntityList field has a non-nullable menuId field.");
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

