/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.entities.DaySlotEntity;
import com.capstone.entities.ScheduleEntity;
import com.capstone.entities.SlotEntity;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author hoanglong
 */
public class DaySlotEntityJpaController implements Serializable {

    public DaySlotEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DaySlotEntity daySlotEntity) throws PreexistingEntityException, Exception {
        if (daySlotEntity.getScheduleEntityCollection() == null) {
            daySlotEntity.setScheduleEntityCollection(new ArrayList<ScheduleEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SlotEntity slotId = daySlotEntity.getSlotId();
            if (slotId != null) {
                slotId = em.getReference(slotId.getClass(), slotId.getId());
                daySlotEntity.setSlotId(slotId);
            }
            List<ScheduleEntity> attachedScheduleEntityCollection = new ArrayList<ScheduleEntity>();
            for (ScheduleEntity scheduleEntityCollectionScheduleEntityToAttach : daySlotEntity.getScheduleEntityCollection()) {
                scheduleEntityCollectionScheduleEntityToAttach = em.getReference(scheduleEntityCollectionScheduleEntityToAttach.getClass(), scheduleEntityCollectionScheduleEntityToAttach.getId());
                attachedScheduleEntityCollection.add(scheduleEntityCollectionScheduleEntityToAttach);
            }
            daySlotEntity.setScheduleEntityCollection(attachedScheduleEntityCollection);
            em.persist(daySlotEntity);
            if (slotId != null) {
                slotId.getDaySlotEntityCollection().add(daySlotEntity);
                slotId = em.merge(slotId);
            }
            for (ScheduleEntity scheduleEntityCollectionScheduleEntity : daySlotEntity.getScheduleEntityCollection()) {
                DaySlotEntity oldDateIdOfScheduleEntityCollectionScheduleEntity = scheduleEntityCollectionScheduleEntity.getDateId();
                scheduleEntityCollectionScheduleEntity.setDateId(daySlotEntity);
                scheduleEntityCollectionScheduleEntity = em.merge(scheduleEntityCollectionScheduleEntity);
                if (oldDateIdOfScheduleEntityCollectionScheduleEntity != null) {
                    oldDateIdOfScheduleEntityCollectionScheduleEntity.getScheduleEntityCollection().remove(scheduleEntityCollectionScheduleEntity);
                    oldDateIdOfScheduleEntityCollectionScheduleEntity = em.merge(oldDateIdOfScheduleEntityCollectionScheduleEntity);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDaySlotEntity(daySlotEntity.getId()) != null) {
                throw new PreexistingEntityException("DaySlotEntity " + daySlotEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DaySlotEntity daySlotEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DaySlotEntity persistentDaySlotEntity = em.find(DaySlotEntity.class, daySlotEntity.getId());
            SlotEntity slotIdOld = persistentDaySlotEntity.getSlotId();
            SlotEntity slotIdNew = daySlotEntity.getSlotId();
            List<ScheduleEntity> scheduleEntityCollectionOld = persistentDaySlotEntity.getScheduleEntityCollection();
            List<ScheduleEntity> scheduleEntityCollectionNew = daySlotEntity.getScheduleEntityCollection();
            if (slotIdNew != null) {
                slotIdNew = em.getReference(slotIdNew.getClass(), slotIdNew.getId());
                daySlotEntity.setSlotId(slotIdNew);
            }
            List<ScheduleEntity> attachedScheduleEntityCollectionNew = new ArrayList<ScheduleEntity>();
            for (ScheduleEntity scheduleEntityCollectionNewScheduleEntityToAttach : scheduleEntityCollectionNew) {
                scheduleEntityCollectionNewScheduleEntityToAttach = em.getReference(scheduleEntityCollectionNewScheduleEntityToAttach.getClass(), scheduleEntityCollectionNewScheduleEntityToAttach.getId());
                attachedScheduleEntityCollectionNew.add(scheduleEntityCollectionNewScheduleEntityToAttach);
            }
            scheduleEntityCollectionNew = attachedScheduleEntityCollectionNew;
            daySlotEntity.setScheduleEntityCollection(scheduleEntityCollectionNew);
            daySlotEntity = em.merge(daySlotEntity);
            if (slotIdOld != null && !slotIdOld.equals(slotIdNew)) {
                slotIdOld.getDaySlotEntityCollection().remove(daySlotEntity);
                slotIdOld = em.merge(slotIdOld);
            }
            if (slotIdNew != null && !slotIdNew.equals(slotIdOld)) {
                slotIdNew.getDaySlotEntityCollection().add(daySlotEntity);
                slotIdNew = em.merge(slotIdNew);
            }
            for (ScheduleEntity scheduleEntityCollectionOldScheduleEntity : scheduleEntityCollectionOld) {
                if (!scheduleEntityCollectionNew.contains(scheduleEntityCollectionOldScheduleEntity)) {
                    scheduleEntityCollectionOldScheduleEntity.setDateId(null);
                    scheduleEntityCollectionOldScheduleEntity = em.merge(scheduleEntityCollectionOldScheduleEntity);
                }
            }
            for (ScheduleEntity scheduleEntityCollectionNewScheduleEntity : scheduleEntityCollectionNew) {
                if (!scheduleEntityCollectionOld.contains(scheduleEntityCollectionNewScheduleEntity)) {
                    DaySlotEntity oldDateIdOfScheduleEntityCollectionNewScheduleEntity = scheduleEntityCollectionNewScheduleEntity.getDateId();
                    scheduleEntityCollectionNewScheduleEntity.setDateId(daySlotEntity);
                    scheduleEntityCollectionNewScheduleEntity = em.merge(scheduleEntityCollectionNewScheduleEntity);
                    if (oldDateIdOfScheduleEntityCollectionNewScheduleEntity != null && !oldDateIdOfScheduleEntityCollectionNewScheduleEntity.equals(daySlotEntity)) {
                        oldDateIdOfScheduleEntityCollectionNewScheduleEntity.getScheduleEntityCollection().remove(scheduleEntityCollectionNewScheduleEntity);
                        oldDateIdOfScheduleEntityCollectionNewScheduleEntity = em.merge(oldDateIdOfScheduleEntityCollectionNewScheduleEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = daySlotEntity.getId();
                if (findDaySlotEntity(id) == null) {
                    throw new NonexistentEntityException("The daySlotEntity with id " + id + " no longer exists.");
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
            DaySlotEntity daySlotEntity;
            try {
                daySlotEntity = em.getReference(DaySlotEntity.class, id);
                daySlotEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The daySlotEntity with id " + id + " no longer exists.", enfe);
            }
            SlotEntity slotId = daySlotEntity.getSlotId();
            if (slotId != null) {
                slotId.getDaySlotEntityCollection().remove(daySlotEntity);
                slotId = em.merge(slotId);
            }
            Collection<ScheduleEntity> scheduleEntityCollection = daySlotEntity.getScheduleEntityCollection();
            for (ScheduleEntity scheduleEntityCollectionScheduleEntity : scheduleEntityCollection) {
                scheduleEntityCollectionScheduleEntity.setDateId(null);
                scheduleEntityCollectionScheduleEntity = em.merge(scheduleEntityCollectionScheduleEntity);
            }
            em.remove(daySlotEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DaySlotEntity> findDaySlotEntityEntities() {
        return findDaySlotEntityEntities(true, -1, -1);
    }

    public List<DaySlotEntity> findDaySlotEntityEntities(int maxResults, int firstResult) {
        return findDaySlotEntityEntities(false, maxResults, firstResult);
    }

    private List<DaySlotEntity> findDaySlotEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DaySlotEntity.class));
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

    public DaySlotEntity findDaySlotEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DaySlotEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getDaySlotEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DaySlotEntity> rt = cq.from(DaySlotEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
