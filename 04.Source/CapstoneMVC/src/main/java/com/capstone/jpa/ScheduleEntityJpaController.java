/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.entities.*;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author hoanglong
 */
public class ScheduleEntityJpaController implements Serializable {

    public ScheduleEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ScheduleEntity scheduleEntity) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CourseEntity courseId = scheduleEntity.getCourseId();
            if (courseId != null) {
                courseId = em.getReference(courseId.getClass(), courseId.getId());
                scheduleEntity.setCourseId(courseId);
            }
            DaySlotEntity dateId = scheduleEntity.getDateId();
            if (dateId != null) {
                dateId = em.getReference(dateId.getClass(), dateId.getId());
                scheduleEntity.setDateId(dateId);
            }
            EmployeeEntity empId = scheduleEntity.getEmpId();
            if (empId != null) {
                empId = em.getReference(empId.getClass(), empId.getId());
                scheduleEntity.setEmpId(empId);
            }
            RoomEntity roomId = scheduleEntity.getRoomId();
            if (roomId != null) {
                roomId = em.getReference(roomId.getClass(), roomId.getId());
                scheduleEntity.setRoomId(roomId);
            }
            em.persist(scheduleEntity);
            if (courseId != null) {
                courseId.getScheduleEntityList().add(scheduleEntity);
                courseId = em.merge(courseId);
            }
            if (dateId != null) {
                dateId.getScheduleEntityCollection().add(scheduleEntity);
                dateId = em.merge(dateId);
            }
            if (empId != null) {
                empId.getScheduleEntityList().add(scheduleEntity);
                empId = em.merge(empId);
            }
            if (roomId != null) {
                roomId.getScheduleEntityCollection().add(scheduleEntity);
                roomId = em.merge(roomId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findScheduleEntity(scheduleEntity.getId()) != null) {
                throw new PreexistingEntityException("ScheduleEntity " + scheduleEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ScheduleEntity scheduleEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ScheduleEntity persistentScheduleEntity = em.find(ScheduleEntity.class, scheduleEntity.getId());
            CourseEntity courseIdOld = persistentScheduleEntity.getCourseId();
            CourseEntity courseIdNew = scheduleEntity.getCourseId();
            DaySlotEntity dateIdOld = persistentScheduleEntity.getDateId();
            DaySlotEntity dateIdNew = scheduleEntity.getDateId();
            EmployeeEntity empIdOld = persistentScheduleEntity.getEmpId();
            EmployeeEntity empIdNew = scheduleEntity.getEmpId();
            RoomEntity roomIdOld = persistentScheduleEntity.getRoomId();
            RoomEntity roomIdNew = scheduleEntity.getRoomId();
            if (courseIdNew != null) {
                courseIdNew = em.getReference(courseIdNew.getClass(), courseIdNew.getId());
                scheduleEntity.setCourseId(courseIdNew);
            }
            if (dateIdNew != null) {
                dateIdNew = em.getReference(dateIdNew.getClass(), dateIdNew.getId());
                scheduleEntity.setDateId(dateIdNew);
            }
            if (empIdNew != null) {
                empIdNew = em.getReference(empIdNew.getClass(), empIdNew.getId());
                scheduleEntity.setEmpId(empIdNew);
            }
            if (roomIdNew != null) {
                roomIdNew = em.getReference(roomIdNew.getClass(), roomIdNew.getId());
                scheduleEntity.setRoomId(roomIdNew);
            }
            scheduleEntity = em.merge(scheduleEntity);
            if (courseIdOld != null && !courseIdOld.equals(courseIdNew)) {
                courseIdOld.getScheduleEntityList().remove(scheduleEntity);
                courseIdOld = em.merge(courseIdOld);
            }
            if (courseIdNew != null && !courseIdNew.equals(courseIdOld)) {
                courseIdNew.getScheduleEntityList().add(scheduleEntity);
                courseIdNew = em.merge(courseIdNew);
            }
            if (dateIdOld != null && !dateIdOld.equals(dateIdNew)) {
                dateIdOld.getScheduleEntityCollection().remove(scheduleEntity);
                dateIdOld = em.merge(dateIdOld);
            }
            if (dateIdNew != null && !dateIdNew.equals(dateIdOld)) {
                dateIdNew.getScheduleEntityCollection().add(scheduleEntity);
                dateIdNew = em.merge(dateIdNew);
            }
            if (empIdOld != null && !empIdOld.equals(empIdNew)) {
                empIdOld.getScheduleEntityList().remove(scheduleEntity);
                empIdOld = em.merge(empIdOld);
            }
            if (empIdNew != null && !empIdNew.equals(empIdOld)) {
                empIdNew.getScheduleEntityList().add(scheduleEntity);
                empIdNew = em.merge(empIdNew);
            }
            if (roomIdOld != null && !roomIdOld.equals(roomIdNew)) {
                roomIdOld.getScheduleEntityCollection().remove(scheduleEntity);
                roomIdOld = em.merge(roomIdOld);
            }
            if (roomIdNew != null && !roomIdNew.equals(roomIdOld)) {
                roomIdNew.getScheduleEntityCollection().add(scheduleEntity);
                roomIdNew = em.merge(roomIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = scheduleEntity.getId();
                if (findScheduleEntity(id) == null) {
                    throw new NonexistentEntityException("The scheduleEntity with id " + id + " no longer exists.");
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
            ScheduleEntity scheduleEntity;
            try {
                scheduleEntity = em.getReference(ScheduleEntity.class, id);
                scheduleEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The scheduleEntity with id " + id + " no longer exists.", enfe);
            }
            CourseEntity courseId = scheduleEntity.getCourseId();
            if (courseId != null) {
                courseId.getScheduleEntityList().remove(scheduleEntity);
                courseId = em.merge(courseId);
            }
            DaySlotEntity dateId = scheduleEntity.getDateId();
            if (dateId != null) {
                dateId.getScheduleEntityCollection().remove(scheduleEntity);
                dateId = em.merge(dateId);
            }
            EmployeeEntity empId = scheduleEntity.getEmpId();
            if (empId != null) {
                empId.getScheduleEntityList().remove(scheduleEntity);
                empId = em.merge(empId);
            }
            RoomEntity roomId = scheduleEntity.getRoomId();
            if (roomId != null) {
                roomId.getScheduleEntityCollection().remove(scheduleEntity);
                roomId = em.merge(roomId);
            }
            em.remove(scheduleEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ScheduleEntity> findScheduleEntityEntities() {
        return findScheduleEntityEntities(true, -1, -1);
    }

    public List<ScheduleEntity> findScheduleEntityEntities(int maxResults, int firstResult) {
        return findScheduleEntityEntities(false, maxResults, firstResult);
    }

    private List<ScheduleEntity> findScheduleEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ScheduleEntity.class));
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

    public ScheduleEntity findScheduleEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ScheduleEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getScheduleEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ScheduleEntity> rt = cq.from(ScheduleEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
