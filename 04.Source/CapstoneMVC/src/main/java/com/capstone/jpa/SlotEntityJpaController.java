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

import com.capstone.entities.DaySlotEntity;
import com.capstone.entities.SlotEntity;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author hoanglong
 */
public class SlotEntityJpaController implements Serializable {

    public SlotEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SlotEntity slotEntity) throws PreexistingEntityException, Exception {
        if (slotEntity.getDaySlotEntityCollection() == null) {
            slotEntity.setDaySlotEntityCollection(new ArrayList<DaySlotEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<DaySlotEntity> attachedDaySlotEntityCollection = new ArrayList<DaySlotEntity>();
            for (DaySlotEntity daySlotEntityCollectionDaySlotEntityToAttach : slotEntity.getDaySlotEntityCollection()) {
                daySlotEntityCollectionDaySlotEntityToAttach = em.getReference(daySlotEntityCollectionDaySlotEntityToAttach.getClass(), daySlotEntityCollectionDaySlotEntityToAttach.getId());
                attachedDaySlotEntityCollection.add(daySlotEntityCollectionDaySlotEntityToAttach);
            }
            slotEntity.setDaySlotEntityCollection(attachedDaySlotEntityCollection);
            em.persist(slotEntity);
            for (DaySlotEntity daySlotEntityCollectionDaySlotEntity : slotEntity.getDaySlotEntityCollection()) {
                SlotEntity oldSlotIdOfDaySlotEntityCollectionDaySlotEntity = daySlotEntityCollectionDaySlotEntity.getSlotId();
                daySlotEntityCollectionDaySlotEntity.setSlotId(slotEntity);
                daySlotEntityCollectionDaySlotEntity = em.merge(daySlotEntityCollectionDaySlotEntity);
                if (oldSlotIdOfDaySlotEntityCollectionDaySlotEntity != null) {
                    oldSlotIdOfDaySlotEntityCollectionDaySlotEntity.getDaySlotEntityCollection().remove(daySlotEntityCollectionDaySlotEntity);
                    oldSlotIdOfDaySlotEntityCollectionDaySlotEntity = em.merge(oldSlotIdOfDaySlotEntityCollectionDaySlotEntity);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findSlotEntity(slotEntity.getId()) != null) {
                throw new PreexistingEntityException("SlotEntity " + slotEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SlotEntity slotEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SlotEntity persistentSlotEntity = em.find(SlotEntity.class, slotEntity.getId());
            List<DaySlotEntity> daySlotEntityCollectionOld = persistentSlotEntity.getDaySlotEntityCollection();
            List<DaySlotEntity> daySlotEntityCollectionNew = slotEntity.getDaySlotEntityCollection();
            List<DaySlotEntity> attachedDaySlotEntityCollectionNew = new ArrayList<DaySlotEntity>();
            for (DaySlotEntity daySlotEntityCollectionNewDaySlotEntityToAttach : daySlotEntityCollectionNew) {
                daySlotEntityCollectionNewDaySlotEntityToAttach = em.getReference(daySlotEntityCollectionNewDaySlotEntityToAttach.getClass(), daySlotEntityCollectionNewDaySlotEntityToAttach.getId());
                attachedDaySlotEntityCollectionNew.add(daySlotEntityCollectionNewDaySlotEntityToAttach);
            }
            daySlotEntityCollectionNew = attachedDaySlotEntityCollectionNew;
            slotEntity.setDaySlotEntityCollection(daySlotEntityCollectionNew);
            slotEntity = em.merge(slotEntity);
            for (DaySlotEntity daySlotEntityCollectionOldDaySlotEntity : daySlotEntityCollectionOld) {
                if (!daySlotEntityCollectionNew.contains(daySlotEntityCollectionOldDaySlotEntity)) {
                    daySlotEntityCollectionOldDaySlotEntity.setSlotId(null);
                    daySlotEntityCollectionOldDaySlotEntity = em.merge(daySlotEntityCollectionOldDaySlotEntity);
                }
            }
            for (DaySlotEntity daySlotEntityCollectionNewDaySlotEntity : daySlotEntityCollectionNew) {
                if (!daySlotEntityCollectionOld.contains(daySlotEntityCollectionNewDaySlotEntity)) {
                    SlotEntity oldSlotIdOfDaySlotEntityCollectionNewDaySlotEntity = daySlotEntityCollectionNewDaySlotEntity.getSlotId();
                    daySlotEntityCollectionNewDaySlotEntity.setSlotId(slotEntity);
                    daySlotEntityCollectionNewDaySlotEntity = em.merge(daySlotEntityCollectionNewDaySlotEntity);
                    if (oldSlotIdOfDaySlotEntityCollectionNewDaySlotEntity != null && !oldSlotIdOfDaySlotEntityCollectionNewDaySlotEntity.equals(slotEntity)) {
                        oldSlotIdOfDaySlotEntityCollectionNewDaySlotEntity.getDaySlotEntityCollection().remove(daySlotEntityCollectionNewDaySlotEntity);
                        oldSlotIdOfDaySlotEntityCollectionNewDaySlotEntity = em.merge(oldSlotIdOfDaySlotEntityCollectionNewDaySlotEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = slotEntity.getId();
                if (findSlotEntity(id) == null) {
                    throw new NonexistentEntityException("The slotEntity with id " + id + " no longer exists.");
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
            SlotEntity slotEntity;
            try {
                slotEntity = em.getReference(SlotEntity.class, id);
                slotEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The slotEntity with id " + id + " no longer exists.", enfe);
            }
            Collection<DaySlotEntity> daySlotEntityCollection = slotEntity.getDaySlotEntityCollection();
            for (DaySlotEntity daySlotEntityCollectionDaySlotEntity : daySlotEntityCollection) {
                daySlotEntityCollectionDaySlotEntity.setSlotId(null);
                daySlotEntityCollectionDaySlotEntity = em.merge(daySlotEntityCollectionDaySlotEntity);
            }
            em.remove(slotEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<SlotEntity> findSlotEntityEntities() {
        return findSlotEntityEntities(true, -1, -1);
    }

    public List<SlotEntity> findSlotEntityEntities(int maxResults, int firstResult) {
        return findSlotEntityEntities(false, maxResults, firstResult);
    }

    private List<SlotEntity> findSlotEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SlotEntity.class));
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

    public SlotEntity findSlotEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SlotEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getSlotEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SlotEntity> rt = cq.from(SlotEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
