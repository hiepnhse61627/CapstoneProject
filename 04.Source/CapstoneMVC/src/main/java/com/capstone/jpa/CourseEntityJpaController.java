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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.capstone.entities.CourseEntity;
import com.capstone.entities.MarksEntity;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

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
        if (courseEntity.getMarksById() == null) {
            courseEntity.setMarksById(new ArrayList<MarksEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<MarksEntity> attachedMarksById = new ArrayList<MarksEntity>();
            for (MarksEntity marksByIdMarksEntityToAttach : courseEntity.getMarksById()) {
                marksByIdMarksEntityToAttach = em.getReference(marksByIdMarksEntityToAttach.getClass(), marksByIdMarksEntityToAttach.getId());
                attachedMarksById.add(marksByIdMarksEntityToAttach);
            }
            courseEntity.setMarksById(attachedMarksById);
            em.persist(courseEntity);
            for (MarksEntity marksByIdMarksEntity : courseEntity.getMarksById()) {
                CourseEntity oldCourseByCourseIdOfMarksByIdMarksEntity = marksByIdMarksEntity.getCourseByCourseId();
                marksByIdMarksEntity.setCourseByCourseId(courseEntity);
                marksByIdMarksEntity = em.merge(marksByIdMarksEntity);
                if (oldCourseByCourseIdOfMarksByIdMarksEntity != null) {
                    oldCourseByCourseIdOfMarksByIdMarksEntity.getMarksById().remove(marksByIdMarksEntity);
                    oldCourseByCourseIdOfMarksByIdMarksEntity = em.merge(oldCourseByCourseIdOfMarksByIdMarksEntity);
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
            Collection<MarksEntity> marksByIdOld = persistentCourseEntity.getMarksById();
            Collection<MarksEntity> marksByIdNew = courseEntity.getMarksById();
            Collection<MarksEntity> attachedMarksByIdNew = new ArrayList<MarksEntity>();
            for (MarksEntity marksByIdNewMarksEntityToAttach : marksByIdNew) {
                marksByIdNewMarksEntityToAttach = em.getReference(marksByIdNewMarksEntityToAttach.getClass(), marksByIdNewMarksEntityToAttach.getId());
                attachedMarksByIdNew.add(marksByIdNewMarksEntityToAttach);
            }
            marksByIdNew = attachedMarksByIdNew;
            courseEntity.setMarksById(marksByIdNew);
            courseEntity = em.merge(courseEntity);
            for (MarksEntity marksByIdOldMarksEntity : marksByIdOld) {
                if (!marksByIdNew.contains(marksByIdOldMarksEntity)) {
                    marksByIdOldMarksEntity.setCourseByCourseId(null);
                    marksByIdOldMarksEntity = em.merge(marksByIdOldMarksEntity);
                }
            }
            for (MarksEntity marksByIdNewMarksEntity : marksByIdNew) {
                if (!marksByIdOld.contains(marksByIdNewMarksEntity)) {
                    CourseEntity oldCourseByCourseIdOfMarksByIdNewMarksEntity = marksByIdNewMarksEntity.getCourseByCourseId();
                    marksByIdNewMarksEntity.setCourseByCourseId(courseEntity);
                    marksByIdNewMarksEntity = em.merge(marksByIdNewMarksEntity);
                    if (oldCourseByCourseIdOfMarksByIdNewMarksEntity != null && !oldCourseByCourseIdOfMarksByIdNewMarksEntity.equals(courseEntity)) {
                        oldCourseByCourseIdOfMarksByIdNewMarksEntity.getMarksById().remove(marksByIdNewMarksEntity);
                        oldCourseByCourseIdOfMarksByIdNewMarksEntity = em.merge(oldCourseByCourseIdOfMarksByIdNewMarksEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = courseEntity.getId();
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

    public void destroy(int id) throws NonexistentEntityException {
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
            Collection<MarksEntity> marksById = courseEntity.getMarksById();
            for (MarksEntity marksByIdMarksEntity : marksById) {
                marksByIdMarksEntity.setCourseByCourseId(null);
                marksByIdMarksEntity = em.merge(marksByIdMarksEntity);
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

    public CourseEntity findCourseEntity(int id) {
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
