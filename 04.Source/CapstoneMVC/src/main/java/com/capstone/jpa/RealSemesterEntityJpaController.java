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
import com.capstone.entities.MarksEntity;
import com.capstone.entities.RealSemesterEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

/**
 *
 * @author hiepnhse61627
 */
public class RealSemesterEntityJpaController implements Serializable {

    public RealSemesterEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RealSemesterEntity realSemesterEntity) throws PreexistingEntityException, Exception {
        if (realSemesterEntity.getMarksById() == null) {
            realSemesterEntity.setMarksById(new ArrayList<MarksEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<MarksEntity> attachedMarksById = new ArrayList<MarksEntity>();
            for (MarksEntity marksByIdMarksEntityToAttach : realSemesterEntity.getMarksById()) {
                marksByIdMarksEntityToAttach = em.getReference(marksByIdMarksEntityToAttach.getClass(), marksByIdMarksEntityToAttach.getId());
                attachedMarksById.add(marksByIdMarksEntityToAttach);
            }
            realSemesterEntity.setMarksById(attachedMarksById);
            em.persist(realSemesterEntity);
            for (MarksEntity marksByIdMarksEntity : realSemesterEntity.getMarksById()) {
                RealSemesterEntity oldRealSemesterBySemesterIdOfMarksByIdMarksEntity = marksByIdMarksEntity.getRealSemesterBySemesterId();
                marksByIdMarksEntity.setRealSemesterBySemesterId(realSemesterEntity);
                marksByIdMarksEntity = em.merge(marksByIdMarksEntity);
                if (oldRealSemesterBySemesterIdOfMarksByIdMarksEntity != null) {
                    oldRealSemesterBySemesterIdOfMarksByIdMarksEntity.getMarksById().remove(marksByIdMarksEntity);
                    oldRealSemesterBySemesterIdOfMarksByIdMarksEntity = em.merge(oldRealSemesterBySemesterIdOfMarksByIdMarksEntity);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRealSemesterEntity(realSemesterEntity.getId()) != null) {
                throw new PreexistingEntityException("RealSemesterEntity " + realSemesterEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RealSemesterEntity realSemesterEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RealSemesterEntity persistentRealSemesterEntity = em.find(RealSemesterEntity.class, realSemesterEntity.getId());
            Collection<MarksEntity> marksByIdOld = persistentRealSemesterEntity.getMarksById();
            Collection<MarksEntity> marksByIdNew = realSemesterEntity.getMarksById();
            Collection<MarksEntity> attachedMarksByIdNew = new ArrayList<MarksEntity>();
            for (MarksEntity marksByIdNewMarksEntityToAttach : marksByIdNew) {
                marksByIdNewMarksEntityToAttach = em.getReference(marksByIdNewMarksEntityToAttach.getClass(), marksByIdNewMarksEntityToAttach.getId());
                attachedMarksByIdNew.add(marksByIdNewMarksEntityToAttach);
            }
            marksByIdNew = attachedMarksByIdNew;
            realSemesterEntity.setMarksById(marksByIdNew);
            realSemesterEntity = em.merge(realSemesterEntity);
            for (MarksEntity marksByIdOldMarksEntity : marksByIdOld) {
                if (!marksByIdNew.contains(marksByIdOldMarksEntity)) {
                    marksByIdOldMarksEntity.setRealSemesterBySemesterId(null);
                    marksByIdOldMarksEntity = em.merge(marksByIdOldMarksEntity);
                }
            }
            for (MarksEntity marksByIdNewMarksEntity : marksByIdNew) {
                if (!marksByIdOld.contains(marksByIdNewMarksEntity)) {
                    RealSemesterEntity oldRealSemesterBySemesterIdOfMarksByIdNewMarksEntity = marksByIdNewMarksEntity.getRealSemesterBySemesterId();
                    marksByIdNewMarksEntity.setRealSemesterBySemesterId(realSemesterEntity);
                    marksByIdNewMarksEntity = em.merge(marksByIdNewMarksEntity);
                    if (oldRealSemesterBySemesterIdOfMarksByIdNewMarksEntity != null && !oldRealSemesterBySemesterIdOfMarksByIdNewMarksEntity.equals(realSemesterEntity)) {
                        oldRealSemesterBySemesterIdOfMarksByIdNewMarksEntity.getMarksById().remove(marksByIdNewMarksEntity);
                        oldRealSemesterBySemesterIdOfMarksByIdNewMarksEntity = em.merge(oldRealSemesterBySemesterIdOfMarksByIdNewMarksEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = realSemesterEntity.getId();
                if (findRealSemesterEntity(id) == null) {
                    throw new NonexistentEntityException("The realSemesterEntity with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RealSemesterEntity realSemesterEntity;
            try {
                realSemesterEntity = em.getReference(RealSemesterEntity.class, id);
                realSemesterEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The realSemesterEntity with id " + id + " no longer exists.", enfe);
            }
            Collection<MarksEntity> marksById = realSemesterEntity.getMarksById();
            for (MarksEntity marksByIdMarksEntity : marksById) {
                marksByIdMarksEntity.setRealSemesterBySemesterId(null);
                marksByIdMarksEntity = em.merge(marksByIdMarksEntity);
            }
            em.remove(realSemesterEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RealSemesterEntity> findRealSemesterEntityEntities() {
        return findRealSemesterEntityEntities(true, -1, -1);
    }

    public List<RealSemesterEntity> findRealSemesterEntityEntities(int maxResults, int firstResult) {
        return findRealSemesterEntityEntities(false, maxResults, firstResult);
    }

    private List<RealSemesterEntity> findRealSemesterEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RealSemesterEntity.class));
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

    public RealSemesterEntity findRealSemesterEntity(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RealSemesterEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getRealSemesterEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RealSemesterEntity> rt = cq.from(RealSemesterEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
