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
import java.util.List;
import com.capstone.entities.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

/**
 *
 * @author hiepnhse61627
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
            SubjectMarkComponentEntity subjectMarkComponentBySubjectId = marksEntity.getSubjectMarkComponentBySubjectId();
            if (subjectMarkComponentBySubjectId != null) {
                subjectMarkComponentBySubjectId = em.getReference(subjectMarkComponentBySubjectId.getClass(), subjectMarkComponentBySubjectId.getSubjectId());
                marksEntity.setSubjectMarkComponentBySubjectId(subjectMarkComponentBySubjectId);
            }
            StudentEntity studentByStudentId = marksEntity.getStudentByStudentId();
            if (studentByStudentId != null) {
                studentByStudentId = em.getReference(studentByStudentId.getClass(), studentByStudentId.getId());
                marksEntity.setStudentByStudentId(studentByStudentId);
            }
            RealSemesterEntity realSemesterBySemesterId = marksEntity.getRealSemesterBySemesterId();
            if (realSemesterBySemesterId != null) {
                realSemesterBySemesterId = em.getReference(realSemesterBySemesterId.getClass(), realSemesterBySemesterId.getId());
                marksEntity.setRealSemesterBySemesterId(realSemesterBySemesterId);
            }
            CourseEntity courseByCourseId = marksEntity.getCourseByCourseId();
            if (courseByCourseId != null) {
                courseByCourseId = em.getReference(courseByCourseId.getClass(), courseByCourseId.getId());
                marksEntity.setCourseByCourseId(courseByCourseId);
            }
            em.persist(marksEntity);
            if (subjectMarkComponentBySubjectId != null) {
                subjectMarkComponentBySubjectId.getMarksBySubjectId().add(marksEntity);
                subjectMarkComponentBySubjectId = em.merge(subjectMarkComponentBySubjectId);
            }
            if (studentByStudentId != null) {
                studentByStudentId.getMarksById().add(marksEntity);
                studentByStudentId = em.merge(studentByStudentId);
            }
            if (realSemesterBySemesterId != null) {
                realSemesterBySemesterId.getMarksById().add(marksEntity);
                realSemesterBySemesterId = em.merge(realSemesterBySemesterId);
            }
            if (courseByCourseId != null) {
                courseByCourseId.getMarksById().add(marksEntity);
                courseByCourseId = em.merge(courseByCourseId);
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
            SubjectMarkComponentEntity subjectMarkComponentBySubjectIdOld = persistentMarksEntity.getSubjectMarkComponentBySubjectId();
            SubjectMarkComponentEntity subjectMarkComponentBySubjectIdNew = marksEntity.getSubjectMarkComponentBySubjectId();
            StudentEntity studentByStudentIdOld = persistentMarksEntity.getStudentByStudentId();
            StudentEntity studentByStudentIdNew = marksEntity.getStudentByStudentId();
            RealSemesterEntity realSemesterBySemesterIdOld = persistentMarksEntity.getRealSemesterBySemesterId();
            RealSemesterEntity realSemesterBySemesterIdNew = marksEntity.getRealSemesterBySemesterId();
            CourseEntity courseByCourseIdOld = persistentMarksEntity.getCourseByCourseId();
            CourseEntity courseByCourseIdNew = marksEntity.getCourseByCourseId();
            if (subjectMarkComponentBySubjectIdNew != null) {
                subjectMarkComponentBySubjectIdNew = em.getReference(subjectMarkComponentBySubjectIdNew.getClass(), subjectMarkComponentBySubjectIdNew.getSubjectId());
                marksEntity.setSubjectMarkComponentBySubjectId(subjectMarkComponentBySubjectIdNew);
            }
            if (studentByStudentIdNew != null) {
                studentByStudentIdNew = em.getReference(studentByStudentIdNew.getClass(), studentByStudentIdNew.getId());
                marksEntity.setStudentByStudentId(studentByStudentIdNew);
            }
            if (realSemesterBySemesterIdNew != null) {
                realSemesterBySemesterIdNew = em.getReference(realSemesterBySemesterIdNew.getClass(), realSemesterBySemesterIdNew.getId());
                marksEntity.setRealSemesterBySemesterId(realSemesterBySemesterIdNew);
            }
            if (courseByCourseIdNew != null) {
                courseByCourseIdNew = em.getReference(courseByCourseIdNew.getClass(), courseByCourseIdNew.getId());
                marksEntity.setCourseByCourseId(courseByCourseIdNew);
            }
            marksEntity = em.merge(marksEntity);
            if (subjectMarkComponentBySubjectIdOld != null && !subjectMarkComponentBySubjectIdOld.equals(subjectMarkComponentBySubjectIdNew)) {
                subjectMarkComponentBySubjectIdOld.getMarksBySubjectId().remove(marksEntity);
                subjectMarkComponentBySubjectIdOld = em.merge(subjectMarkComponentBySubjectIdOld);
            }
            if (subjectMarkComponentBySubjectIdNew != null && !subjectMarkComponentBySubjectIdNew.equals(subjectMarkComponentBySubjectIdOld)) {
                subjectMarkComponentBySubjectIdNew.getMarksBySubjectId().add(marksEntity);
                subjectMarkComponentBySubjectIdNew = em.merge(subjectMarkComponentBySubjectIdNew);
            }
            if (studentByStudentIdOld != null && !studentByStudentIdOld.equals(studentByStudentIdNew)) {
                studentByStudentIdOld.getMarksById().remove(marksEntity);
                studentByStudentIdOld = em.merge(studentByStudentIdOld);
            }
            if (studentByStudentIdNew != null && !studentByStudentIdNew.equals(studentByStudentIdOld)) {
                studentByStudentIdNew.getMarksById().add(marksEntity);
                studentByStudentIdNew = em.merge(studentByStudentIdNew);
            }
            if (realSemesterBySemesterIdOld != null && !realSemesterBySemesterIdOld.equals(realSemesterBySemesterIdNew)) {
                realSemesterBySemesterIdOld.getMarksById().remove(marksEntity);
                realSemesterBySemesterIdOld = em.merge(realSemesterBySemesterIdOld);
            }
            if (realSemesterBySemesterIdNew != null && !realSemesterBySemesterIdNew.equals(realSemesterBySemesterIdOld)) {
                realSemesterBySemesterIdNew.getMarksById().add(marksEntity);
                realSemesterBySemesterIdNew = em.merge(realSemesterBySemesterIdNew);
            }
            if (courseByCourseIdOld != null && !courseByCourseIdOld.equals(courseByCourseIdNew)) {
                courseByCourseIdOld.getMarksById().remove(marksEntity);
                courseByCourseIdOld = em.merge(courseByCourseIdOld);
            }
            if (courseByCourseIdNew != null && !courseByCourseIdNew.equals(courseByCourseIdOld)) {
                courseByCourseIdNew.getMarksById().add(marksEntity);
                courseByCourseIdNew = em.merge(courseByCourseIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = marksEntity.getId();
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

    public void destroy(int id) throws NonexistentEntityException {
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
            SubjectMarkComponentEntity subjectMarkComponentBySubjectId = marksEntity.getSubjectMarkComponentBySubjectId();
            if (subjectMarkComponentBySubjectId != null) {
                subjectMarkComponentBySubjectId.getMarksBySubjectId().remove(marksEntity);
                subjectMarkComponentBySubjectId = em.merge(subjectMarkComponentBySubjectId);
            }
            StudentEntity studentByStudentId = marksEntity.getStudentByStudentId();
            if (studentByStudentId != null) {
                studentByStudentId.getMarksById().remove(marksEntity);
                studentByStudentId = em.merge(studentByStudentId);
            }
            RealSemesterEntity realSemesterBySemesterId = marksEntity.getRealSemesterBySemesterId();
            if (realSemesterBySemesterId != null) {
                realSemesterBySemesterId.getMarksById().remove(marksEntity);
                realSemesterBySemesterId = em.merge(realSemesterBySemesterId);
            }
            CourseEntity courseByCourseId = marksEntity.getCourseByCourseId();
            if (courseByCourseId != null) {
                courseByCourseId.getMarksById().remove(marksEntity);
                courseByCourseId = em.merge(courseByCourseId);
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

    public MarksEntity findMarksEntity(int id) {
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
