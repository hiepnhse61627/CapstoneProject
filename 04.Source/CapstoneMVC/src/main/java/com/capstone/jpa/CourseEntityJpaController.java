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
        if (courseEntity.getMarksList() == null) {
            courseEntity.setMarksList(new ArrayList<MarksEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<MarksEntity> attachedMarksList = new ArrayList<MarksEntity>();
            for (MarksEntity marksListMarksEntityToAttach : courseEntity.getMarksList()) {
                marksListMarksEntityToAttach = em.getReference(marksListMarksEntityToAttach.getClass(), marksListMarksEntityToAttach.getId());
                attachedMarksList.add(marksListMarksEntityToAttach);
            }
            courseEntity.setMarksList(attachedMarksList);
            em.persist(courseEntity);
            for (MarksEntity marksListMarksEntity : courseEntity.getMarksList()) {
                CourseEntity oldCourseIdOfMarksListMarksEntity = marksListMarksEntity.getCourseId();
                marksListMarksEntity.setCourseId(courseEntity);
                marksListMarksEntity = em.merge(marksListMarksEntity);
                if (oldCourseIdOfMarksListMarksEntity != null) {
                    oldCourseIdOfMarksListMarksEntity.getMarksList().remove(marksListMarksEntity);
                    oldCourseIdOfMarksListMarksEntity = em.merge(oldCourseIdOfMarksListMarksEntity);
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
            List<MarksEntity> marksListOld = persistentCourseEntity.getMarksList();
            List<MarksEntity> marksListNew = courseEntity.getMarksList();
            List<MarksEntity> attachedMarksListNew = new ArrayList<MarksEntity>();
            for (MarksEntity marksListNewMarksEntityToAttach : marksListNew) {
                marksListNewMarksEntityToAttach = em.getReference(marksListNewMarksEntityToAttach.getClass(), marksListNewMarksEntityToAttach.getId());
                attachedMarksListNew.add(marksListNewMarksEntityToAttach);
            }
            marksListNew = attachedMarksListNew;
            courseEntity.setMarksList(marksListNew);
            courseEntity = em.merge(courseEntity);
            for (MarksEntity marksListOldMarksEntity : marksListOld) {
                if (!marksListNew.contains(marksListOldMarksEntity)) {
                    marksListOldMarksEntity.setCourseId(null);
                    marksListOldMarksEntity = em.merge(marksListOldMarksEntity);
                }
            }
            for (MarksEntity marksListNewMarksEntity : marksListNew) {
                if (!marksListOld.contains(marksListNewMarksEntity)) {
                    CourseEntity oldCourseIdOfMarksListNewMarksEntity = marksListNewMarksEntity.getCourseId();
                    marksListNewMarksEntity.setCourseId(courseEntity);
                    marksListNewMarksEntity = em.merge(marksListNewMarksEntity);
                    if (oldCourseIdOfMarksListNewMarksEntity != null && !oldCourseIdOfMarksListNewMarksEntity.equals(courseEntity)) {
                        oldCourseIdOfMarksListNewMarksEntity.getMarksList().remove(marksListNewMarksEntity);
                        oldCourseIdOfMarksListNewMarksEntity = em.merge(oldCourseIdOfMarksListNewMarksEntity);
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
            List<MarksEntity> marksList = courseEntity.getMarksList();
            for (MarksEntity marksListMarksEntity : marksList) {
                marksListMarksEntity.setCourseId(null);
                marksListMarksEntity = em.merge(marksListMarksEntity);
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
