/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.entities.CourseEntity;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.MarksEntity;
import com.capstone.entities.ScheduleEntity;
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
public class CourseEntityJpaController implements Serializable {

    public CourseEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CourseEntity courseEntity) throws PreexistingEntityException, Exception {
        if (courseEntity.getMarksEntityList() == null) {
            courseEntity.setMarksEntityList(new ArrayList<MarksEntity>());
        }
        if (courseEntity.getScheduleEntityList() == null) {
            courseEntity.setScheduleEntityList(new ArrayList<ScheduleEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<MarksEntity> attachedMarksEntityList = new ArrayList<MarksEntity>();
            for (MarksEntity MarksEntityListMarksEntityToAttach : courseEntity.getMarksEntityList()) {
                MarksEntityListMarksEntityToAttach = em.getReference(MarksEntityListMarksEntityToAttach.getClass(), MarksEntityListMarksEntityToAttach.getId());
                attachedMarksEntityList.add(MarksEntityListMarksEntityToAttach);
            }
            courseEntity.setMarksEntityList(attachedMarksEntityList);
            List<ScheduleEntity> attachedScheduleEntityList = new ArrayList<ScheduleEntity>();
            for (ScheduleEntity scheduleEntityListScheduleEntityToAttach : courseEntity.getScheduleEntityList()) {
                scheduleEntityListScheduleEntityToAttach = em.getReference(scheduleEntityListScheduleEntityToAttach.getClass(), scheduleEntityListScheduleEntityToAttach.getId());
                attachedScheduleEntityList.add(scheduleEntityListScheduleEntityToAttach);
            }
            courseEntity.setScheduleEntityList(attachedScheduleEntityList);
            em.persist(courseEntity);
            for (MarksEntity MarksEntityListMarksEntity : courseEntity.getMarksEntityList()) {
                CourseEntity oldCourseIdOfMarksEntityListMarksEntity = MarksEntityListMarksEntity.getCourseId();
                MarksEntityListMarksEntity.setCourseId(courseEntity);
                MarksEntityListMarksEntity = em.merge(MarksEntityListMarksEntity);
                if (oldCourseIdOfMarksEntityListMarksEntity != null) {
                    oldCourseIdOfMarksEntityListMarksEntity.getMarksEntityList().remove(MarksEntityListMarksEntity);
                    oldCourseIdOfMarksEntityListMarksEntity = em.merge(oldCourseIdOfMarksEntityListMarksEntity);
                }
            }
            for (ScheduleEntity scheduleEntityListScheduleEntity : courseEntity.getScheduleEntityList()) {
                CourseEntity oldCourseIdOfScheduleEntityListScheduleEntity = scheduleEntityListScheduleEntity.getCourseId();
                scheduleEntityListScheduleEntity.setCourseId(courseEntity);
                scheduleEntityListScheduleEntity = em.merge(scheduleEntityListScheduleEntity);
                if (oldCourseIdOfScheduleEntityListScheduleEntity != null) {
                    oldCourseIdOfScheduleEntityListScheduleEntity.getScheduleEntityList().remove(scheduleEntityListScheduleEntity);
                    oldCourseIdOfScheduleEntityListScheduleEntity = em.merge(oldCourseIdOfScheduleEntityListScheduleEntity);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCourseEntity(courseEntity.getId()) != null) {
                throw new PreexistingEntityException("CourseEntity " + courseEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CourseEntity courseEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CourseEntity persistentCourseEntity = em.find(CourseEntity.class, courseEntity.getId());
            List<MarksEntity> MarksEntityListOld = persistentCourseEntity.getMarksEntityList();
            List<MarksEntity> MarksEntityListNew = courseEntity.getMarksEntityList();
            List<ScheduleEntity> scheduleEntityListOld = persistentCourseEntity.getScheduleEntityList();
            List<ScheduleEntity> scheduleEntityListNew = courseEntity.getScheduleEntityList();
            List<MarksEntity> attachedMarksEntityListNew = new ArrayList<MarksEntity>();
            for (MarksEntity MarksEntityListNewMarksEntityToAttach : MarksEntityListNew) {
                MarksEntityListNewMarksEntityToAttach = em.getReference(MarksEntityListNewMarksEntityToAttach.getClass(), MarksEntityListNewMarksEntityToAttach.getId());
                attachedMarksEntityListNew.add(MarksEntityListNewMarksEntityToAttach);
            }
            MarksEntityListNew = attachedMarksEntityListNew;
            courseEntity.setMarksEntityList(MarksEntityListNew);
            List<ScheduleEntity> attachedScheduleEntityListNew = new ArrayList<ScheduleEntity>();
            for (ScheduleEntity scheduleEntityListNewScheduleEntityToAttach : scheduleEntityListNew) {
                scheduleEntityListNewScheduleEntityToAttach = em.getReference(scheduleEntityListNewScheduleEntityToAttach.getClass(), scheduleEntityListNewScheduleEntityToAttach.getId());
                attachedScheduleEntityListNew.add(scheduleEntityListNewScheduleEntityToAttach);
            }
            scheduleEntityListNew = attachedScheduleEntityListNew;
            courseEntity.setScheduleEntityList(scheduleEntityListNew);
            courseEntity = em.merge(courseEntity);
            for (MarksEntity MarksEntityListOldMarksEntity : MarksEntityListOld) {
                if (!MarksEntityListNew.contains(MarksEntityListOldMarksEntity)) {
                    MarksEntityListOldMarksEntity.setCourseId(null);
                    MarksEntityListOldMarksEntity = em.merge(MarksEntityListOldMarksEntity);
                }
            }
            for (MarksEntity MarksEntityListNewMarksEntity : MarksEntityListNew) {
                if (!MarksEntityListOld.contains(MarksEntityListNewMarksEntity)) {
                    CourseEntity oldCourseIdOfMarksEntityListNewMarksEntity = MarksEntityListNewMarksEntity.getCourseId();
                    MarksEntityListNewMarksEntity.setCourseId(courseEntity);
                    MarksEntityListNewMarksEntity = em.merge(MarksEntityListNewMarksEntity);
                    if (oldCourseIdOfMarksEntityListNewMarksEntity != null && !oldCourseIdOfMarksEntityListNewMarksEntity.equals(courseEntity)) {
                        oldCourseIdOfMarksEntityListNewMarksEntity.getMarksEntityList().remove(MarksEntityListNewMarksEntity);
                        oldCourseIdOfMarksEntityListNewMarksEntity = em.merge(oldCourseIdOfMarksEntityListNewMarksEntity);
                    }
                }
            }
            for (ScheduleEntity scheduleEntityListOldScheduleEntity : scheduleEntityListOld) {
                if (!scheduleEntityListNew.contains(scheduleEntityListOldScheduleEntity)) {
                    scheduleEntityListOldScheduleEntity.setCourseId(null);
                    scheduleEntityListOldScheduleEntity = em.merge(scheduleEntityListOldScheduleEntity);
                }
            }
            for (ScheduleEntity scheduleEntityListNewScheduleEntity : scheduleEntityListNew) {
                if (!scheduleEntityListOld.contains(scheduleEntityListNewScheduleEntity)) {
                    CourseEntity oldCourseIdOfScheduleEntityListNewScheduleEntity = scheduleEntityListNewScheduleEntity.getCourseId();
                    scheduleEntityListNewScheduleEntity.setCourseId(courseEntity);
                    scheduleEntityListNewScheduleEntity = em.merge(scheduleEntityListNewScheduleEntity);
                    if (oldCourseIdOfScheduleEntityListNewScheduleEntity != null && !oldCourseIdOfScheduleEntityListNewScheduleEntity.equals(courseEntity)) {
                        oldCourseIdOfScheduleEntityListNewScheduleEntity.getScheduleEntityList().remove(scheduleEntityListNewScheduleEntity);
                        oldCourseIdOfScheduleEntityListNewScheduleEntity = em.merge(oldCourseIdOfScheduleEntityListNewScheduleEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = courseEntity.getId();
                if (findCourseEntity(id) == null) {
                    throw new NonexistentEntityException("The courseEntity with id " + id + " no longer exists.");
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
            CourseEntity courseEntity;
            try {
                courseEntity = em.getReference(CourseEntity.class, id);
                courseEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The courseEntity with id " + id + " no longer exists.", enfe);
            }
            List<MarksEntity> MarksEntityList = courseEntity.getMarksEntityList();
            for (MarksEntity MarksEntityListMarksEntity : MarksEntityList) {
                MarksEntityListMarksEntity.setCourseId(null);
                MarksEntityListMarksEntity = em.merge(MarksEntityListMarksEntity);
            }
            List<ScheduleEntity> scheduleEntityList = courseEntity.getScheduleEntityList();
            for (ScheduleEntity scheduleEntityListScheduleEntity : scheduleEntityList) {
                scheduleEntityListScheduleEntity.setCourseId(null);
                scheduleEntityListScheduleEntity = em.merge(scheduleEntityListScheduleEntity);
            }
            em.remove(courseEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CourseEntity> findCourseEntityEntities() {
        return findCourseEntityEntities(true, -1, -1);
    }

    public List<CourseEntity> findCourseEntityEntities(int maxResults, int firstResult) {
        return findCourseEntityEntities(false, maxResults, firstResult);
    }

    private List<CourseEntity> findCourseEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CourseEntity.class));
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

    public CourseEntity findCourseEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CourseEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getCourseEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CourseEntity> rt = cq.from(CourseEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}

