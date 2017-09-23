/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.CourseEntity;
import com.capstone.entities.MarksEntity;
import com.capstone.entities.RealSemesterEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.entities.SubjectMarkComponentEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rem
 */
public class MarksEntityJpaController implements Serializable {

    public MarksEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(MarksEntity marksEntity) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CourseEntity courseId = marksEntity.getCourseId();
            if (courseId != null) {
                courseId = em.getReference(courseId.getClass(), courseId.getId());
                marksEntity.setCourseId(courseId);
            }
            RealSemesterEntity semesterId = marksEntity.getSemesterId();
            if (semesterId != null) {
                semesterId = em.getReference(semesterId.getClass(), semesterId.getId());
                marksEntity.setSemesterId(semesterId);
            }
            StudentEntity studentId = marksEntity.getStudentId();
            if (studentId != null) {
                studentId = em.getReference(studentId.getClass(), studentId.getId());
                marksEntity.setStudentId(studentId);
            }
            SubjectMarkComponentEntity subjectId = marksEntity.getSubjectId();
            if (subjectId != null) {
                subjectId = em.getReference(subjectId.getClass(), subjectId.getSubjectId());
                marksEntity.setSubjectId(subjectId);
            }
            em.persist(marksEntity);
            if (courseId != null) {
                courseId.getMarksEntityList().add(marksEntity);
                courseId = em.merge(courseId);
            }
            if (semesterId != null) {
                semesterId.getMarksEntityList().add(marksEntity);
                semesterId = em.merge(semesterId);
            }
            if (studentId != null) {
                studentId.getMarksEntityList().add(marksEntity);
                studentId = em.merge(studentId);
            }
            if (subjectId != null) {
                subjectId.getMarksEntityList().add(marksEntity);
                subjectId = em.merge(subjectId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMarksEntity(marksEntity.getId()) != null) {
                throw new PreexistingEntityException("MarksEntity " + marksEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(MarksEntity marksEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MarksEntity persistentMarksEntity = em.find(MarksEntity.class, marksEntity.getId());
            CourseEntity courseIdOld = persistentMarksEntity.getCourseId();
            CourseEntity courseIdNew = marksEntity.getCourseId();
            RealSemesterEntity semesterIdOld = persistentMarksEntity.getSemesterId();
            RealSemesterEntity semesterIdNew = marksEntity.getSemesterId();
            StudentEntity studentIdOld = persistentMarksEntity.getStudentId();
            StudentEntity studentIdNew = marksEntity.getStudentId();
            SubjectMarkComponentEntity subjectIdOld = persistentMarksEntity.getSubjectId();
            SubjectMarkComponentEntity subjectIdNew = marksEntity.getSubjectId();
            if (courseIdNew != null) {
                courseIdNew = em.getReference(courseIdNew.getClass(), courseIdNew.getId());
                marksEntity.setCourseId(courseIdNew);
            }
            if (semesterIdNew != null) {
                semesterIdNew = em.getReference(semesterIdNew.getClass(), semesterIdNew.getId());
                marksEntity.setSemesterId(semesterIdNew);
            }
            if (studentIdNew != null) {
                studentIdNew = em.getReference(studentIdNew.getClass(), studentIdNew.getId());
                marksEntity.setStudentId(studentIdNew);
            }
            if (subjectIdNew != null) {
                subjectIdNew = em.getReference(subjectIdNew.getClass(), subjectIdNew.getSubjectId());
                marksEntity.setSubjectId(subjectIdNew);
            }
            marksEntity = em.merge(marksEntity);
            if (courseIdOld != null && !courseIdOld.equals(courseIdNew)) {
                courseIdOld.getMarksEntityList().remove(marksEntity);
                courseIdOld = em.merge(courseIdOld);
            }
            if (courseIdNew != null && !courseIdNew.equals(courseIdOld)) {
                courseIdNew.getMarksEntityList().add(marksEntity);
                courseIdNew = em.merge(courseIdNew);
            }
            if (semesterIdOld != null && !semesterIdOld.equals(semesterIdNew)) {
                semesterIdOld.getMarksEntityList().remove(marksEntity);
                semesterIdOld = em.merge(semesterIdOld);
            }
            if (semesterIdNew != null && !semesterIdNew.equals(semesterIdOld)) {
                semesterIdNew.getMarksEntityList().add(marksEntity);
                semesterIdNew = em.merge(semesterIdNew);
            }
            if (studentIdOld != null && !studentIdOld.equals(studentIdNew)) {
                studentIdOld.getMarksEntityList().remove(marksEntity);
                studentIdOld = em.merge(studentIdOld);
            }
            if (studentIdNew != null && !studentIdNew.equals(studentIdOld)) {
                studentIdNew.getMarksEntityList().add(marksEntity);
                studentIdNew = em.merge(studentIdNew);
            }
            if (subjectIdOld != null && !subjectIdOld.equals(subjectIdNew)) {
                subjectIdOld.getMarksEntityList().remove(marksEntity);
                subjectIdOld = em.merge(subjectIdOld);
            }
            if (subjectIdNew != null && !subjectIdNew.equals(subjectIdOld)) {
                subjectIdNew.getMarksEntityList().add(marksEntity);
                subjectIdNew = em.merge(subjectIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = marksEntity.getId();
                if (findMarksEntity(id) == null) {
                    throw new NonexistentEntityException("The marksEntity with id " + id + " no longer exists.");
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
            MarksEntity marksEntity;
            try {
                marksEntity = em.getReference(MarksEntity.class, id);
                marksEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The marksEntity with id " + id + " no longer exists.", enfe);
            }
            CourseEntity courseId = marksEntity.getCourseId();
            if (courseId != null) {
                courseId.getMarksEntityList().remove(marksEntity);
                courseId = em.merge(courseId);
            }
            RealSemesterEntity semesterId = marksEntity.getSemesterId();
            if (semesterId != null) {
                semesterId.getMarksEntityList().remove(marksEntity);
                semesterId = em.merge(semesterId);
            }
            StudentEntity studentId = marksEntity.getStudentId();
            if (studentId != null) {
                studentId.getMarksEntityList().remove(marksEntity);
                studentId = em.merge(studentId);
            }
            SubjectMarkComponentEntity subjectId = marksEntity.getSubjectId();
            if (subjectId != null) {
                subjectId.getMarksEntityList().remove(marksEntity);
                subjectId = em.merge(subjectId);
            }
            em.remove(marksEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<MarksEntity> findMarksEntityEntities() {
        return findMarksEntityEntities(true, -1, -1);
    }

    public List<MarksEntity> findMarksEntityEntities(int maxResults, int firstResult) {
        return findMarksEntityEntities(false, maxResults, firstResult);
    }

    private List<MarksEntity> findMarksEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(MarksEntity.class));
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

    public MarksEntity findMarksEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(MarksEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getMarksEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<MarksEntity> rt = cq.from(MarksEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
