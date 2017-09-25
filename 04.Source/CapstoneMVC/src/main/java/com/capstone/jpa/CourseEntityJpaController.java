/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.entities.CourseEntity;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

import java.io.Serializable;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.MarksEntity;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rem
 */
public class CourseEntityJpaController implements Serializable {

    private int currentLine = 0;
    private int totalLine = 0;


    public int getCurrentLine() {
        return currentLine;
    }

    public int getTotalLine() {
        return totalLine;
    }

    public CourseEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void createCourseList(List<CourseEntity> students) {
        totalLine = students.size();
        currentLine = 0;

        EntityManager em = null;
        try {
            em = getEntityManager();
            for (CourseEntity course : students) {
                try {
                    em.getTransaction().begin();

                    TypedQuery<CourseEntity> single = em.createQuery("SELECT c FROM CourseEntity c WHERE c.clazz = :class", CourseEntity.class);
                    single.setParameter("class", course.getClazz());

                    List<CourseEntity> stus = single.getResultList();
                    if (stus.size() == 0) {
                        em.persist(course);
                    } else {
                        CourseEntity stu = stus.get(0);
                        stu.setClazz(course.getClazz());
                        stu.setStartDate(course.getStartDate());
                        stu.setEndDate(course.getEndDate());
                        em.merge(stu);
                    }

                    em.getTransaction().commit();
                } catch (Exception e) {
                    System.out.println("Course " + course.getClazz() + "caused " + e.getMessage());
                }

                currentLine++;

                System.out.println(currentLine + "-" + totalLine);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }

        totalLine = 0;
        currentLine = 0;
    }

    public void create(CourseEntity courseEntity) throws PreexistingEntityException, Exception {
        if (courseEntity.getMarksEntityList() == null) {
            courseEntity.setMarksEntityList(new ArrayList<MarksEntity>());
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
            List<MarksEntity> attachedMarksEntityListNew = new ArrayList<MarksEntity>();
            for (MarksEntity marksEntityListNewMarksEntityToAttach : marksEntityListNew) {
                marksEntityListNewMarksEntityToAttach = em.getReference(marksEntityListNewMarksEntityToAttach.getClass(), marksEntityListNewMarksEntityToAttach.getId());
                attachedMarksEntityListNew.add(marksEntityListNewMarksEntityToAttach);
            }
            marksEntityListNew = attachedMarksEntityListNew;
            courseEntity.setMarksEntityList(marksEntityListNew);
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
