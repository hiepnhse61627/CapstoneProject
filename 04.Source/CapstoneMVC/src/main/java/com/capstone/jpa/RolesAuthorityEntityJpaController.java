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
import com.capstone.entities.DynamicMenuEntity;
import com.capstone.entities.RolesAuthorityEntity;
import com.capstone.entities.RolesEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

/**
 *
 * @author StormNs
 */
public class RolesAuthorityEntityJpaController implements Serializable {

    public RolesAuthorityEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RolesAuthorityEntity rolesAuthorityEntity) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DynamicMenuEntity menuId = rolesAuthorityEntity.getMenuId();
            if (menuId != null) {
                menuId = em.getReference(menuId.getClass(), menuId.getId());
                rolesAuthorityEntity.setMenuId(menuId);
            }
            RolesEntity rolesId = rolesAuthorityEntity.getRolesId();
            if (rolesId != null) {
                rolesId = em.getReference(rolesId.getClass(), rolesId.getId());
                rolesAuthorityEntity.setRolesId(rolesId);
            }
            em.persist(rolesAuthorityEntity);
            if (menuId != null) {
                menuId.getRolesAuthorityEntityList().add(rolesAuthorityEntity);
                menuId = em.merge(menuId);
            }
            if (rolesId != null) {
                rolesId.getRolesAuthorityEntityList().add(rolesAuthorityEntity);
                rolesId = em.merge(rolesId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRolesAuthorityEntity(rolesAuthorityEntity.getId()) != null) {
                throw new PreexistingEntityException("RolesAuthorityEntity " + rolesAuthorityEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RolesAuthorityEntity rolesAuthorityEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RolesAuthorityEntity persistentRolesAuthorityEntity = em.find(RolesAuthorityEntity.class, rolesAuthorityEntity.getId());
            DynamicMenuEntity menuIdOld = persistentRolesAuthorityEntity.getMenuId();
            DynamicMenuEntity menuIdNew = rolesAuthorityEntity.getMenuId();
            RolesEntity rolesIdOld = persistentRolesAuthorityEntity.getRolesId();
            RolesEntity rolesIdNew = rolesAuthorityEntity.getRolesId();
            if (menuIdNew != null) {
                menuIdNew = em.getReference(menuIdNew.getClass(), menuIdNew.getId());
                rolesAuthorityEntity.setMenuId(menuIdNew);
            }
            if (rolesIdNew != null) {
                rolesIdNew = em.getReference(rolesIdNew.getClass(), rolesIdNew.getId());
                rolesAuthorityEntity.setRolesId(rolesIdNew);
            }
            rolesAuthorityEntity = em.merge(rolesAuthorityEntity);
            if (menuIdOld != null && !menuIdOld.equals(menuIdNew)) {
                menuIdOld.getRolesAuthorityEntityList().remove(rolesAuthorityEntity);
                menuIdOld = em.merge(menuIdOld);
            }
            if (menuIdNew != null && !menuIdNew.equals(menuIdOld)) {
                menuIdNew.getRolesAuthorityEntityList().add(rolesAuthorityEntity);
                menuIdNew = em.merge(menuIdNew);
            }
            if (rolesIdOld != null && !rolesIdOld.equals(rolesIdNew)) {
                rolesIdOld.getRolesAuthorityEntityList().remove(rolesAuthorityEntity);
                rolesIdOld = em.merge(rolesIdOld);
            }
            if (rolesIdNew != null && !rolesIdNew.equals(rolesIdOld)) {
                rolesIdNew.getRolesAuthorityEntityList().add(rolesAuthorityEntity);
                rolesIdNew = em.merge(rolesIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = rolesAuthorityEntity.getId();
                if (findRolesAuthorityEntity(id) == null) {
                    throw new NonexistentEntityException("The rolesAuthorityEntity with id " + id + " no longer exists.");
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
            RolesAuthorityEntity rolesAuthorityEntity;
            try {
                rolesAuthorityEntity = em.getReference(RolesAuthorityEntity.class, id);
                rolesAuthorityEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rolesAuthorityEntity with id " + id + " no longer exists.", enfe);
            }
            DynamicMenuEntity menuId = rolesAuthorityEntity.getMenuId();
            if (menuId != null) {
                menuId.getRolesAuthorityEntityList().remove(rolesAuthorityEntity);
                menuId = em.merge(menuId);
            }
            RolesEntity rolesId = rolesAuthorityEntity.getRolesId();
            if (rolesId != null) {
                rolesId.getRolesAuthorityEntityList().remove(rolesAuthorityEntity);
                rolesId = em.merge(rolesId);
            }
            em.remove(rolesAuthorityEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RolesAuthorityEntity> findRolesAuthorityEntityEntities() {
        return findRolesAuthorityEntityEntities(true, -1, -1);
    }

    public List<RolesAuthorityEntity> findRolesAuthorityEntityEntities(int maxResults, int firstResult) {
        return findRolesAuthorityEntityEntities(false, maxResults, firstResult);
    }

    private List<RolesAuthorityEntity> findRolesAuthorityEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RolesAuthorityEntity.class));
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

    public RolesAuthorityEntity findRolesAuthorityEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RolesAuthorityEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getRolesAuthorityEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RolesAuthorityEntity> rt = cq.from(RolesAuthorityEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
