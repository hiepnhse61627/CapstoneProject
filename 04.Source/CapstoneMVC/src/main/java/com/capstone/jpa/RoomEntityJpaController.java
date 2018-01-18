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

import com.capstone.entities.RoomEntity;
import com.capstone.entities.ScheduleEntity;
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
public class RoomEntityJpaController implements Serializable {

    public RoomEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RoomEntity roomEntity) throws PreexistingEntityException, Exception {
        if (roomEntity.getScheduleEntityList() == null) {
            roomEntity.setScheduleEntityList(new ArrayList<ScheduleEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<ScheduleEntity> attachedScheduleEntityCollection = new ArrayList<ScheduleEntity>();
            for (ScheduleEntity scheduleEntityCollectionScheduleEntityToAttach : roomEntity.getScheduleEntityList()) {
                scheduleEntityCollectionScheduleEntityToAttach = em.getReference(scheduleEntityCollectionScheduleEntityToAttach.getClass(), scheduleEntityCollectionScheduleEntityToAttach.getId());
                attachedScheduleEntityCollection.add(scheduleEntityCollectionScheduleEntityToAttach);
            }
            roomEntity.setScheduleEntityList(attachedScheduleEntityCollection);
            em.persist(roomEntity);
            for (ScheduleEntity scheduleEntityCollectionScheduleEntity : roomEntity.getScheduleEntityList()) {
                RoomEntity oldRoomIdOfScheduleEntityCollectionScheduleEntity = scheduleEntityCollectionScheduleEntity.getRoomId();
                scheduleEntityCollectionScheduleEntity.setRoomId(roomEntity);
                scheduleEntityCollectionScheduleEntity = em.merge(scheduleEntityCollectionScheduleEntity);
                if (oldRoomIdOfScheduleEntityCollectionScheduleEntity != null) {
                    oldRoomIdOfScheduleEntityCollectionScheduleEntity.getScheduleEntityList().remove(scheduleEntityCollectionScheduleEntity);
                    oldRoomIdOfScheduleEntityCollectionScheduleEntity = em.merge(oldRoomIdOfScheduleEntityCollectionScheduleEntity);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRoomEntity(roomEntity.getId()) != null) {
                throw new PreexistingEntityException("RoomEntity " + roomEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RoomEntity roomEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RoomEntity persistentRoomEntity = em.find(RoomEntity.class, roomEntity.getId());
            List<ScheduleEntity> scheduleEntityCollectionOld = persistentRoomEntity.getScheduleEntityList();
            List<ScheduleEntity> scheduleEntityCollectionNew = roomEntity.getScheduleEntityList();
            List<ScheduleEntity> attachedScheduleEntityCollectionNew = new ArrayList<ScheduleEntity>();
            for (ScheduleEntity scheduleEntityCollectionNewScheduleEntityToAttach : scheduleEntityCollectionNew) {
                scheduleEntityCollectionNewScheduleEntityToAttach = em.getReference(scheduleEntityCollectionNewScheduleEntityToAttach.getClass(), scheduleEntityCollectionNewScheduleEntityToAttach.getId());
                attachedScheduleEntityCollectionNew.add(scheduleEntityCollectionNewScheduleEntityToAttach);
            }
            scheduleEntityCollectionNew = attachedScheduleEntityCollectionNew;
            roomEntity.setScheduleEntityList(scheduleEntityCollectionNew);
            roomEntity = em.merge(roomEntity);
            for (ScheduleEntity scheduleEntityCollectionOldScheduleEntity : scheduleEntityCollectionOld) {
                if (!scheduleEntityCollectionNew.contains(scheduleEntityCollectionOldScheduleEntity)) {
                    scheduleEntityCollectionOldScheduleEntity.setRoomId(null);
                    scheduleEntityCollectionOldScheduleEntity = em.merge(scheduleEntityCollectionOldScheduleEntity);
                }
            }
            for (ScheduleEntity scheduleEntityCollectionNewScheduleEntity : scheduleEntityCollectionNew) {
                if (!scheduleEntityCollectionOld.contains(scheduleEntityCollectionNewScheduleEntity)) {
                    RoomEntity oldRoomIdOfScheduleEntityCollectionNewScheduleEntity = scheduleEntityCollectionNewScheduleEntity.getRoomId();
                    scheduleEntityCollectionNewScheduleEntity.setRoomId(roomEntity);
                    scheduleEntityCollectionNewScheduleEntity = em.merge(scheduleEntityCollectionNewScheduleEntity);
                    if (oldRoomIdOfScheduleEntityCollectionNewScheduleEntity != null && !oldRoomIdOfScheduleEntityCollectionNewScheduleEntity.equals(roomEntity)) {
                        oldRoomIdOfScheduleEntityCollectionNewScheduleEntity.getScheduleEntityList().remove(scheduleEntityCollectionNewScheduleEntity);
                        oldRoomIdOfScheduleEntityCollectionNewScheduleEntity = em.merge(oldRoomIdOfScheduleEntityCollectionNewScheduleEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = roomEntity.getId();
                if (findRoomEntity(id) == null) {
                    throw new NonexistentEntityException("The roomEntity with id " + id + " no longer exists.");
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
            RoomEntity roomEntity;
            try {
                roomEntity = em.getReference(RoomEntity.class, id);
                roomEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The roomEntity with id " + id + " no longer exists.", enfe);
            }
            Collection<ScheduleEntity> scheduleEntityCollection = roomEntity.getScheduleEntityList();
            for (ScheduleEntity scheduleEntityCollectionScheduleEntity : scheduleEntityCollection) {
                scheduleEntityCollectionScheduleEntity.setRoomId(null);
                scheduleEntityCollectionScheduleEntity = em.merge(scheduleEntityCollectionScheduleEntity);
            }
            em.remove(roomEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RoomEntity> findRoomEntityEntities() {
        return findRoomEntityEntities(true, -1, -1);
    }

    public List<RoomEntity> findRoomEntityEntities(int maxResults, int firstResult) {
        return findRoomEntityEntities(false, maxResults, firstResult);
    }

    private List<RoomEntity> findRoomEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RoomEntity.class));
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

    public RoomEntity findRoomEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RoomEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getRoomEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RoomEntity> rt = cq.from(RoomEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
