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
        if (realSemesterEntity.getMarksEntityList() == null) {
            realSemesterEntity.setMarksEntityList(new ArrayList<MarksEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<MarksEntity> attachedMarksEntityList = new ArrayList<MarksEntity>();
            for (MarksEntity marksEntityListMarksEntityToAttach : realSemesterEntity.getMarksEntityList()) {
                marksEntityListMarksEntityToAttach = em.getReference(marksEntityListMarksEntityToAttach.getClass(), marksEntityListMarksEntityToAttach.getId());
                attachedMarksEntityList.add(marksEntityListMarksEntityToAttach);
            }
            realSemesterEntity.setMarksEntityList(attachedMarksEntityList);
            em.persist(realSemesterEntity);
            for (MarksEntity marksEntityListMarksEntity : realSemesterEntity.getMarksEntityList()) {
                RealSemesterEntity oldSemesterIdOfMarksEntityListMarksEntity = marksEntityListMarksEntity.getSemesterId();
                marksEntityListMarksEntity.setSemesterId(realSemesterEntity);
                marksEntityListMarksEntity = em.merge(marksEntityListMarksEntity);
                if (oldSemesterIdOfMarksEntityListMarksEntity != null) {
                    oldSemesterIdOfMarksEntityListMarksEntity.getMarksEntityList().remove(marksEntityListMarksEntity);
                    oldSemesterIdOfMarksEntityListMarksEntity = em.merge(oldSemesterIdOfMarksEntityListMarksEntity);
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
            List<MarksEntity> marksEntityListOld = persistentRealSemesterEntity.getMarksEntityList();
            List<MarksEntity> marksEntityListNew = realSemesterEntity.getMarksEntityList();
            List<MarksEntity> attachedMarksEntityListNew = new ArrayList<MarksEntity>();
            for (MarksEntity marksEntityListNewMarksEntityToAttach : marksEntityListNew) {
                marksEntityListNewMarksEntityToAttach = em.getReference(marksEntityListNewMarksEntityToAttach.getClass(), marksEntityListNewMarksEntityToAttach.getId());
                attachedMarksEntityListNew.add(marksEntityListNewMarksEntityToAttach);
            }
            marksEntityListNew = attachedMarksEntityListNew;
            realSemesterEntity.setMarksEntityList(marksEntityListNew);
            realSemesterEntity = em.merge(realSemesterEntity);
            for (MarksEntity marksEntityListOldMarksEntity : marksEntityListOld) {
                if (!marksEntityListNew.contains(marksEntityListOldMarksEntity)) {
                    marksEntityListOldMarksEntity.setSemesterId(null);
                    marksEntityListOldMarksEntity = em.merge(marksEntityListOldMarksEntity);
                }
            }
            for (MarksEntity marksEntityListNewMarksEntity : marksEntityListNew) {
                if (!marksEntityListOld.contains(marksEntityListNewMarksEntity)) {
                    RealSemesterEntity oldSemesterIdOfMarksEntityListNewMarksEntity = marksEntityListNewMarksEntity.getSemesterId();
                    marksEntityListNewMarksEntity.setSemesterId(realSemesterEntity);
                    marksEntityListNewMarksEntity = em.merge(marksEntityListNewMarksEntity);
                    if (oldSemesterIdOfMarksEntityListNewMarksEntity != null && !oldSemesterIdOfMarksEntityListNewMarksEntity.equals(realSemesterEntity)) {
                        oldSemesterIdOfMarksEntityListNewMarksEntity.getMarksEntityList().remove(marksEntityListNewMarksEntity);
                        oldSemesterIdOfMarksEntityListNewMarksEntity = em.merge(oldSemesterIdOfMarksEntityListNewMarksEntity);
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
            List<MarksEntity> marksEntityList = realSemesterEntity.getMarksEntityList();
            for (MarksEntity marksEntityListMarksEntity : marksEntityList) {
                marksEntityListMarksEntity.setSemesterId(null);
                marksEntityListMarksEntity = em.merge(marksEntityListMarksEntity);
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
