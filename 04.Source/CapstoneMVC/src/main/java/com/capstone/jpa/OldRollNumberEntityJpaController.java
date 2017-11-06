/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.entities.OldRollNumberEntity;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.StudentEntity;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author hiepnhse61627
 */
public class OldRollNumberEntityJpaController implements Serializable {

    public OldRollNumberEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(OldRollNumberEntity oldRollNumberEntity) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            StudentEntity studentId = oldRollNumberEntity.getStudentId();
            if (studentId != null) {
                studentId = em.getReference(studentId.getClass(), studentId.getId());
                oldRollNumberEntity.setStudentId(studentId);
            }
            em.persist(oldRollNumberEntity);
            if (studentId != null) {
                studentId.getOldRollNumberEntityList().add(oldRollNumberEntity);
                studentId = em.merge(studentId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findOldRollNumberEntity(oldRollNumberEntity.getId()) != null) {
                throw new PreexistingEntityException("OldRollNumberEntity " + oldRollNumberEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(OldRollNumberEntity oldRollNumberEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            OldRollNumberEntity persistentOldRollNumberEntity = em.find(OldRollNumberEntity.class, oldRollNumberEntity.getId());
            StudentEntity studentIdOld = persistentOldRollNumberEntity.getStudentId();
            StudentEntity studentIdNew = oldRollNumberEntity.getStudentId();
            if (studentIdNew != null) {
                studentIdNew = em.getReference(studentIdNew.getClass(), studentIdNew.getId());
                oldRollNumberEntity.setStudentId(studentIdNew);
            }
            oldRollNumberEntity = em.merge(oldRollNumberEntity);
            if (studentIdOld != null && !studentIdOld.equals(studentIdNew)) {
                studentIdOld.getOldRollNumberEntityList().remove(oldRollNumberEntity);
                studentIdOld = em.merge(studentIdOld);
            }
            if (studentIdNew != null && !studentIdNew.equals(studentIdOld)) {
                studentIdNew.getOldRollNumberEntityList().add(oldRollNumberEntity);
                studentIdNew = em.merge(studentIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = oldRollNumberEntity.getId();
                if (findOldRollNumberEntity(id) == null) {
                    throw new NonexistentEntityException("The oldRollNumberEntity with id " + id + " no longer exists.");
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
            OldRollNumberEntity oldRollNumberEntity;
            try {
                oldRollNumberEntity = em.getReference(OldRollNumberEntity.class, id);
                oldRollNumberEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The oldRollNumberEntity with id " + id + " no longer exists.", enfe);
            }
            StudentEntity studentId = oldRollNumberEntity.getStudentId();
            if (studentId != null) {
                studentId.getOldRollNumberEntityList().remove(oldRollNumberEntity);
                studentId = em.merge(studentId);
            }
            em.remove(oldRollNumberEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<OldRollNumberEntity> findOldRollNumberEntityEntities() {
        return findOldRollNumberEntityEntities(true, -1, -1);
    }

    public List<OldRollNumberEntity> findOldRollNumberEntityEntities(int maxResults, int firstResult) {
        return findOldRollNumberEntityEntities(false, maxResults, firstResult);
    }

    private List<OldRollNumberEntity> findOldRollNumberEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(OldRollNumberEntity.class));
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

    public OldRollNumberEntity findOldRollNumberEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(OldRollNumberEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getOldRollNumberEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<OldRollNumberEntity> rt = cq.from(OldRollNumberEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
