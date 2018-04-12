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
        if (credentialsEntity.getCredentialsRolesEntityList() == null) {
            credentialsEntity.setCredentialsRolesEntityList(new ArrayList<CredentialsRolesEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<CredentialsRolesEntity> attachedCredentialsRolesEntityList = new ArrayList<CredentialsRolesEntity>();
            for (CredentialsRolesEntity credentialsRolesEntityListCredentialsRolesEntityToAttach : credentialsEntity.getCredentialsRolesEntityList()) {
                credentialsRolesEntityListCredentialsRolesEntityToAttach = em.getReference(credentialsRolesEntityListCredentialsRolesEntityToAttach.getClass(), credentialsRolesEntityListCredentialsRolesEntityToAttach.getId());
                attachedCredentialsRolesEntityList.add(credentialsRolesEntityListCredentialsRolesEntityToAttach);
            }
            credentialsEntity.setCredentialsRolesEntityList(attachedCredentialsRolesEntityList);
            em.persist(credentialsEntity);
            for (CredentialsRolesEntity credentialsRolesEntityListCredentialsRolesEntity : credentialsEntity.getCredentialsRolesEntityList()) {
                CredentialsEntity oldCredentialsIdOfCredentialsRolesEntityListCredentialsRolesEntity = credentialsRolesEntityListCredentialsRolesEntity.getCredentialsId();
                credentialsRolesEntityListCredentialsRolesEntity.setCredentialsId(credentialsEntity);
                credentialsRolesEntityListCredentialsRolesEntity = em.merge(credentialsRolesEntityListCredentialsRolesEntity);
                if (oldCredentialsIdOfCredentialsRolesEntityListCredentialsRolesEntity != null) {
                    oldCredentialsIdOfCredentialsRolesEntityListCredentialsRolesEntity.getCredentialsRolesEntityList().remove(credentialsRolesEntityListCredentialsRolesEntity);
                    oldCredentialsIdOfCredentialsRolesEntityListCredentialsRolesEntity = em.merge(oldCredentialsIdOfCredentialsRolesEntityListCredentialsRolesEntity);
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
            List<CredentialsRolesEntity> credentialsRolesEntityListOld = persistentCredentialsEntity.getCredentialsRolesEntityList();
            List<CredentialsRolesEntity> credentialsRolesEntityListNew = credentialsEntity.getCredentialsRolesEntityList();
            List<CredentialsRolesEntity> attachedCredentialsRolesEntityListNew = new ArrayList<CredentialsRolesEntity>();
            for (CredentialsRolesEntity credentialsRolesEntityListNewCredentialsRolesEntityToAttach : credentialsRolesEntityListNew) {
                credentialsRolesEntityListNewCredentialsRolesEntityToAttach = em.getReference(credentialsRolesEntityListNewCredentialsRolesEntityToAttach.getClass(), credentialsRolesEntityListNewCredentialsRolesEntityToAttach.getId());
                attachedCredentialsRolesEntityListNew.add(credentialsRolesEntityListNewCredentialsRolesEntityToAttach);
            }
            credentialsRolesEntityListNew = attachedCredentialsRolesEntityListNew;
            credentialsEntity.setCredentialsRolesEntityList(credentialsRolesEntityListNew);
            credentialsEntity = em.merge(credentialsEntity);
            for (CredentialsRolesEntity credentialsRolesEntityListOldCredentialsRolesEntity : credentialsRolesEntityListOld) {
                if (!credentialsRolesEntityListNew.contains(credentialsRolesEntityListOldCredentialsRolesEntity)) {
                    credentialsRolesEntityListOldCredentialsRolesEntity.setCredentialsId(null);
                    credentialsRolesEntityListOldCredentialsRolesEntity = em.merge(credentialsRolesEntityListOldCredentialsRolesEntity);
                }
            }
            for (CredentialsRolesEntity credentialsRolesEntityListNewCredentialsRolesEntity : credentialsRolesEntityListNew) {
                if (!credentialsRolesEntityListOld.contains(credentialsRolesEntityListNewCredentialsRolesEntity)) {
                    CredentialsEntity oldCredentialsIdOfCredentialsRolesEntityListNewCredentialsRolesEntity = credentialsRolesEntityListNewCredentialsRolesEntity.getCredentialsId();
                    credentialsRolesEntityListNewCredentialsRolesEntity.setCredentialsId(credentialsEntity);
                    credentialsRolesEntityListNewCredentialsRolesEntity = em.merge(credentialsRolesEntityListNewCredentialsRolesEntity);
                    if (oldCredentialsIdOfCredentialsRolesEntityListNewCredentialsRolesEntity != null && !oldCredentialsIdOfCredentialsRolesEntityListNewCredentialsRolesEntity.equals(credentialsEntity)) {
                        oldCredentialsIdOfCredentialsRolesEntityListNewCredentialsRolesEntity.getCredentialsRolesEntityList().remove(credentialsRolesEntityListNewCredentialsRolesEntity);
                        oldCredentialsIdOfCredentialsRolesEntityListNewCredentialsRolesEntity = em.merge(oldCredentialsIdOfCredentialsRolesEntityListNewCredentialsRolesEntity);
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
            List<CredentialsRolesEntity> credentialsRolesEntityList = credentialsEntity.getCredentialsRolesEntityList();
            for (CredentialsRolesEntity credentialsRolesEntityListCredentialsRolesEntity : credentialsRolesEntityList) {
                credentialsRolesEntityListCredentialsRolesEntity.setCredentialsId(null);
                credentialsRolesEntityListCredentialsRolesEntity = em.merge(credentialsRolesEntityListCredentialsRolesEntity);
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