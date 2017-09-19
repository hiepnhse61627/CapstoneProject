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
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

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
        if (realSemesterEntity.getMarksList() == null) {
            realSemesterEntity.setMarksList(new ArrayList<MarksEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<MarksEntity> attachedMarksList = new ArrayList<MarksEntity>();
            for (MarksEntity marksListMarksEntityToAttach : realSemesterEntity.getMarksList()) {
                marksListMarksEntityToAttach = em.getReference(marksListMarksEntityToAttach.getClass(), marksListMarksEntityToAttach.getId());
                attachedMarksList.add(marksListMarksEntityToAttach);
            }
            realSemesterEntity.setMarksList(attachedMarksList);
            em.persist(realSemesterEntity);
            for (MarksEntity marksListMarksEntity : realSemesterEntity.getMarksList()) {
                RealSemesterEntity oldSemesterIdOfMarksListMarksEntity = marksListMarksEntity.getSemesterId();
                marksListMarksEntity.setSemesterId(realSemesterEntity);
                marksListMarksEntity = em.merge(marksListMarksEntity);
                if (oldSemesterIdOfMarksListMarksEntity != null) {
                    oldSemesterIdOfMarksListMarksEntity.getMarksList().remove(marksListMarksEntity);
                    oldSemesterIdOfMarksListMarksEntity = em.merge(oldSemesterIdOfMarksListMarksEntity);
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
            List<MarksEntity> marksListOld = persistentRealSemesterEntity.getMarksList();
            List<MarksEntity> marksListNew = realSemesterEntity.getMarksList();
            List<MarksEntity> attachedMarksListNew = new ArrayList<MarksEntity>();
            for (MarksEntity marksListNewMarksEntityToAttach : marksListNew) {
                marksListNewMarksEntityToAttach = em.getReference(marksListNewMarksEntityToAttach.getClass(), marksListNewMarksEntityToAttach.getId());
                attachedMarksListNew.add(marksListNewMarksEntityToAttach);
            }
            marksListNew = attachedMarksListNew;
            realSemesterEntity.setMarksList(marksListNew);
            realSemesterEntity = em.merge(realSemesterEntity);
            for (MarksEntity marksListOldMarksEntity : marksListOld) {
                if (!marksListNew.contains(marksListOldMarksEntity)) {
                    marksListOldMarksEntity.setSemesterId(null);
                    marksListOldMarksEntity = em.merge(marksListOldMarksEntity);
                }
            }
            for (MarksEntity marksListNewMarksEntity : marksListNew) {
                if (!marksListOld.contains(marksListNewMarksEntity)) {
                    RealSemesterEntity oldSemesterIdOfMarksListNewMarksEntity = marksListNewMarksEntity.getSemesterId();
                    marksListNewMarksEntity.setSemesterId(realSemesterEntity);
                    marksListNewMarksEntity = em.merge(marksListNewMarksEntity);
                    if (oldSemesterIdOfMarksListNewMarksEntity != null && !oldSemesterIdOfMarksListNewMarksEntity.equals(realSemesterEntity)) {
                        oldSemesterIdOfMarksListNewMarksEntity.getMarksList().remove(marksListNewMarksEntity);
                        oldSemesterIdOfMarksListNewMarksEntity = em.merge(oldSemesterIdOfMarksListNewMarksEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = realSemesterEntity.getId();
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

    public void destroy(Integer id) throws NonexistentEntityException {
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
            List<MarksEntity> marksList = realSemesterEntity.getMarksList();
            for (MarksEntity marksListMarksEntity : marksList) {
                marksListMarksEntity.setSemesterId(null);
                marksListMarksEntity = em.merge(marksListMarksEntity);
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

    public RealSemesterEntity findRealSemesterEntity(Integer id) {
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
