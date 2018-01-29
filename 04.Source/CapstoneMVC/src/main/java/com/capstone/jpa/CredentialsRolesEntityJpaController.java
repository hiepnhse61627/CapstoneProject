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
import com.capstone.entities.CredentialsEntity;
import com.capstone.entities.CredentialsRolesEntity;
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
public class CredentialsRolesEntityJpaController implements Serializable {

    public CredentialsRolesEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CredentialsRolesEntity credentialsRolesEntity) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CredentialsEntity credentialsId = credentialsRolesEntity.getCredentialsId();
            if (credentialsId != null) {
                credentialsId = em.getReference(credentialsId.getClass(), credentialsId.getId());
                credentialsRolesEntity.setCredentialsId(credentialsId);
            }
            RolesEntity rolesId = credentialsRolesEntity.getRolesId();
            if (rolesId != null) {
                rolesId = em.getReference(rolesId.getClass(), rolesId.getId());
                credentialsRolesEntity.setRolesId(rolesId);
            }
            em.persist(credentialsRolesEntity);
            if (credentialsId != null) {
                credentialsId.getCredentialsRolesEntityCollection().add(credentialsRolesEntity);
                credentialsId = em.merge(credentialsId);
            }
            if (rolesId != null) {
                rolesId.getCredentialsRolesEntityCollection().add(credentialsRolesEntity);
                rolesId = em.merge(rolesId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCredentialsRolesEntity(credentialsRolesEntity.getId()) != null) {
                throw new PreexistingEntityException("CredentialsRolesEntity " + credentialsRolesEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CredentialsRolesEntity credentialsRolesEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CredentialsRolesEntity persistentCredentialsRolesEntity = em.find(CredentialsRolesEntity.class, credentialsRolesEntity.getId());
            CredentialsEntity credentialsIdOld = persistentCredentialsRolesEntity.getCredentialsId();
            CredentialsEntity credentialsIdNew = credentialsRolesEntity.getCredentialsId();
            RolesEntity rolesIdOld = persistentCredentialsRolesEntity.getRolesId();
            RolesEntity rolesIdNew = credentialsRolesEntity.getRolesId();
            if (credentialsIdNew != null) {
                credentialsIdNew = em.getReference(credentialsIdNew.getClass(), credentialsIdNew.getId());
                credentialsRolesEntity.setCredentialsId(credentialsIdNew);
            }
            if (rolesIdNew != null) {
                rolesIdNew = em.getReference(rolesIdNew.getClass(), rolesIdNew.getId());
                credentialsRolesEntity.setRolesId(rolesIdNew);
            }
            credentialsRolesEntity = em.merge(credentialsRolesEntity);
            if (credentialsIdOld != null && !credentialsIdOld.equals(credentialsIdNew)) {
                credentialsIdOld.getCredentialsRolesEntityCollection().remove(credentialsRolesEntity);
                credentialsIdOld = em.merge(credentialsIdOld);
            }
            if (credentialsIdNew != null && !credentialsIdNew.equals(credentialsIdOld)) {
                credentialsIdNew.getCredentialsRolesEntityCollection().add(credentialsRolesEntity);
                credentialsIdNew = em.merge(credentialsIdNew);
            }
            if (rolesIdOld != null && !rolesIdOld.equals(rolesIdNew)) {
                rolesIdOld.getCredentialsRolesEntityCollection().remove(credentialsRolesEntity);
                rolesIdOld = em.merge(rolesIdOld);
            }
            if (rolesIdNew != null && !rolesIdNew.equals(rolesIdOld)) {
                rolesIdNew.getCredentialsRolesEntityCollection().add(credentialsRolesEntity);
                rolesIdNew = em.merge(rolesIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = credentialsRolesEntity.getId();
                if (findCredentialsRolesEntity(id) == null) {
                    throw new NonexistentEntityException("The credentialsRolesEntity with id " + id + " no longer exists.");
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
            CredentialsRolesEntity credentialsRolesEntity;
            try {
                credentialsRolesEntity = em.getReference(CredentialsRolesEntity.class, id);
                credentialsRolesEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The credentialsRolesEntity with id " + id + " no longer exists.", enfe);
            }
            CredentialsEntity credentialsId = credentialsRolesEntity.getCredentialsId();
            if (credentialsId != null) {
                credentialsId.getCredentialsRolesEntityCollection().remove(credentialsRolesEntity);
                credentialsId = em.merge(credentialsId);
            }
            RolesEntity rolesId = credentialsRolesEntity.getRolesId();
            if (rolesId != null) {
                rolesId.getCredentialsRolesEntityCollection().remove(credentialsRolesEntity);
                rolesId = em.merge(rolesId);
            }
            em.remove(credentialsRolesEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CredentialsRolesEntity> findCredentialsRolesEntityEntities() {
        return findCredentialsRolesEntityEntities(true, -1, -1);
    }

    public List<CredentialsRolesEntity> findCredentialsRolesEntityEntities(int maxResults, int firstResult) {
        return findCredentialsRolesEntityEntities(false, maxResults, firstResult);
    }

    private List<CredentialsRolesEntity> findCredentialsRolesEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CredentialsRolesEntity.class));
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

    public CredentialsRolesEntity findCredentialsRolesEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CredentialsRolesEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getCredentialsRolesEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CredentialsRolesEntity> rt = cq.from(CredentialsRolesEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
