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

import com.capstone.entities.CourseStudentEntity;
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
        if (courseEntity.getCourseStudentEntityList() == null) {
            courseEntity.setCourseStudentEntityList(new ArrayList<CourseStudentEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<MarksEntity> attachedMarksEntityList = new ArrayList<MarksEntity>();
            for (MarksEntity marksEntityListMarksEntityToAttach : courseEntity.getMarksEntityList()) {
                marksEntityListMarksEntityToAttach = em.getReference(marksEntityListMarksEntityToAttach.getClass(), marksEntityListMarksEntityToAttach.getId());
                attachedMarksEntityList.add(marksEntityListMarksEntityToAttach);
            }
            courseEntity.setMarksEntityList(attachedMarksEntityList);
            List<ScheduleEntity> attachedScheduleEntityList = new ArrayList<ScheduleEntity>();
            for (ScheduleEntity scheduleEntityListScheduleEntityToAttach : courseEntity.getScheduleEntityList()) {
                scheduleEntityListScheduleEntityToAttach = em.getReference(scheduleEntityListScheduleEntityToAttach.getClass(), scheduleEntityListScheduleEntityToAttach.getId());
                attachedScheduleEntityList.add(scheduleEntityListScheduleEntityToAttach);
            }
            courseEntity.setScheduleEntityList(attachedScheduleEntityList);
            List<CourseStudentEntity> attachedCourseStudentEntityList = new ArrayList<CourseStudentEntity>();
            for (CourseStudentEntity courseStudentEntityListCourseStudentEntityToAttach : courseEntity.getCourseStudentEntityList()) {
                courseStudentEntityListCourseStudentEntityToAttach = em.getReference(courseStudentEntityListCourseStudentEntityToAttach.getClass(), courseStudentEntityListCourseStudentEntityToAttach.getId());
                attachedCourseStudentEntityList.add(courseStudentEntityListCourseStudentEntityToAttach);
            }
            courseEntity.setCourseStudentEntityList(attachedCourseStudentEntityList);
            em.persist(courseEntity);
            for (MarksEntity marksEntityListMarksEntity : courseEntity.getMarksEntityList()) {
                CourseEntity oldCourseIdOfMarksEntityListMarksEntity = marksEntityListMarksEntity.getCourseId();
                marksEntityListMarksEntity.setCourseId(courseEntity);
                marksEntityListMarksEntity = em.merge(marksEntityListMarksEntity);
                if (oldCourseIdOfMarksEntityListMarksEntity != null) {
                    oldCourseIdOfMarksEntityListMarksEntity.getMarksEntityList().remove(marksEntityListMarksEntity);
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
            for (CourseStudentEntity courseStudentEntityListCourseStudentEntity : courseEntity.getCourseStudentEntityList()) {
                CourseEntity oldCourseIdOfCourseStudentEntityListCourseStudentEntity = courseStudentEntityListCourseStudentEntity.getCourseId();
                courseStudentEntityListCourseStudentEntity.setCourseId(courseEntity);
                courseStudentEntityListCourseStudentEntity = em.merge(courseStudentEntityListCourseStudentEntity);
                if (oldCourseIdOfCourseStudentEntityListCourseStudentEntity != null) {
                    oldCourseIdOfCourseStudentEntityListCourseStudentEntity.getCourseStudentEntityList().remove(courseStudentEntityListCourseStudentEntity);
                    oldCourseIdOfCourseStudentEntityListCourseStudentEntity = em.merge(oldCourseIdOfCourseStudentEntityListCourseStudentEntity);
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
            List<MarksEntity> marksEntityListOld = persistentCourseEntity.getMarksEntityList();
            List<MarksEntity> marksEntityListNew = courseEntity.getMarksEntityList();
            List<ScheduleEntity> scheduleEntityListOld = persistentCourseEntity.getScheduleEntityList();
            List<ScheduleEntity> scheduleEntityListNew = courseEntity.getScheduleEntityList();
            List<CourseStudentEntity> courseStudentEntityListOld = persistentCourseEntity.getCourseStudentEntityList();
            List<CourseStudentEntity> courseStudentEntityListNew = courseEntity.getCourseStudentEntityList();
            List<MarksEntity> attachedMarksEntityListNew = new ArrayList<MarksEntity>();
            for (MarksEntity marksEntityListNewMarksEntityToAttach : marksEntityListNew) {
                marksEntityListNewMarksEntityToAttach = em.getReference(marksEntityListNewMarksEntityToAttach.getClass(), marksEntityListNewMarksEntityToAttach.getId());
                attachedMarksEntityListNew.add(marksEntityListNewMarksEntityToAttach);
            }
            marksEntityListNew = attachedMarksEntityListNew;
            courseEntity.setMarksEntityList(marksEntityListNew);
            List<ScheduleEntity> attachedScheduleEntityListNew = new ArrayList<ScheduleEntity>();
            for (ScheduleEntity scheduleEntityListNewScheduleEntityToAttach : scheduleEntityListNew) {
                scheduleEntityListNewScheduleEntityToAttach = em.getReference(scheduleEntityListNewScheduleEntityToAttach.getClass(), scheduleEntityListNewScheduleEntityToAttach.getId());
                attachedScheduleEntityListNew.add(scheduleEntityListNewScheduleEntityToAttach);
            }
            scheduleEntityListNew = attachedScheduleEntityListNew;
            courseEntity.setScheduleEntityList(scheduleEntityListNew);
            List<CourseStudentEntity> attachedCourseStudentEntityListNew = new ArrayList<CourseStudentEntity>();
            for (CourseStudentEntity courseStudentEntityListNewCourseStudentEntityToAttach : courseStudentEntityListNew) {
                courseStudentEntityListNewCourseStudentEntityToAttach = em.getReference(courseStudentEntityListNewCourseStudentEntityToAttach.getClass(), courseStudentEntityListNewCourseStudentEntityToAttach.getId());
                attachedCourseStudentEntityListNew.add(courseStudentEntityListNewCourseStudentEntityToAttach);
            }
            courseStudentEntityListNew = attachedCourseStudentEntityListNew;
            courseEntity.setCourseStudentEntityList(courseStudentEntityListNew);
            courseEntity = em.merge(courseEntity);
            for (MarksEntity marksEntityListOldMarksEntity : marksEntityListOld) {
                if (!marksEntityListNew.contains(marksEntityListOldMarksEntity)) {
                    marksEntityListOldMarksEntity.setCourseId(null);
                    marksEntityListOldMarksEntity = em.merge(marksEntityListOldMarksEntity);
                }
            }
            for (MarksEntity marksEntityListNewMarksEntity : marksEntityListNew) {
                if (!marksEntityListOld.contains(marksEntityListNewMarksEntity)) {
                    CourseEntity oldCourseIdOfMarksEntityListNewMarksEntity = marksEntityListNewMarksEntity.getCourseId();
                    marksEntityListNewMarksEntity.setCourseId(courseEntity);
                    marksEntityListNewMarksEntity = em.merge(marksEntityListNewMarksEntity);
                    if (oldCourseIdOfMarksEntityListNewMarksEntity != null && !oldCourseIdOfMarksEntityListNewMarksEntity.equals(courseEntity)) {
                        oldCourseIdOfMarksEntityListNewMarksEntity.getMarksEntityList().remove(marksEntityListNewMarksEntity);
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
            for (CourseStudentEntity courseStudentEntityListOldCourseStudentEntity : courseStudentEntityListOld) {
                if (!courseStudentEntityListNew.contains(courseStudentEntityListOldCourseStudentEntity)) {
                    courseStudentEntityListOldCourseStudentEntity.setCourseId(null);
                    courseStudentEntityListOldCourseStudentEntity = em.merge(courseStudentEntityListOldCourseStudentEntity);
                }
            }
            for (CourseStudentEntity courseStudentEntityListNewCourseStudentEntity : courseStudentEntityListNew) {
                if (!courseStudentEntityListOld.contains(courseStudentEntityListNewCourseStudentEntity)) {
                    CourseEntity oldCourseIdOfCourseStudentEntityListNewCourseStudentEntity = courseStudentEntityListNewCourseStudentEntity.getCourseId();
                    courseStudentEntityListNewCourseStudentEntity.setCourseId(courseEntity);
                    courseStudentEntityListNewCourseStudentEntity = em.merge(courseStudentEntityListNewCourseStudentEntity);
                    if (oldCourseIdOfCourseStudentEntityListNewCourseStudentEntity != null && !oldCourseIdOfCourseStudentEntityListNewCourseStudentEntity.equals(courseEntity)) {
                        oldCourseIdOfCourseStudentEntityListNewCourseStudentEntity.getCourseStudentEntityList().remove(courseStudentEntityListNewCourseStudentEntity);
                        oldCourseIdOfCourseStudentEntityListNewCourseStudentEntity = em.merge(oldCourseIdOfCourseStudentEntityListNewCourseStudentEntity);
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
            List<MarksEntity> marksEntityList = courseEntity.getMarksEntityList();
            for (MarksEntity marksEntityListMarksEntity : marksEntityList) {
                marksEntityListMarksEntity.setCourseId(null);
                marksEntityListMarksEntity = em.merge(marksEntityListMarksEntity);
            }
            List<ScheduleEntity> scheduleEntityList = courseEntity.getScheduleEntityList();
            for (ScheduleEntity scheduleEntityListScheduleEntity : scheduleEntityList) {
                scheduleEntityListScheduleEntity.setCourseId(null);
                scheduleEntityListScheduleEntity = em.merge(scheduleEntityListScheduleEntity);
            }
            List<CourseStudentEntity> courseStudentEntityList = courseEntity.getCourseStudentEntityList();
            for (CourseStudentEntity courseStudentEntityListCourseStudentEntity : courseStudentEntityList) {
                courseStudentEntityListCourseStudentEntity.setCourseId(null);
                courseStudentEntityListCourseStudentEntity = em.merge(courseStudentEntityListCourseStudentEntity);
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

