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
import com.capstone.entities.RealSemesterEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.entities.StudentStatusEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

/**
 *
 * @author StormNs
 */
public class StudentStatusEntityJpaController implements Serializable {

    public StudentStatusEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(StudentStatusEntity studentStatusEntity) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RealSemesterEntity semesterId = studentStatusEntity.getSemesterId();
            if (semesterId != null) {
                semesterId = em.getReference(semesterId.getClass(), semesterId.getId());
                studentStatusEntity.setSemesterId(semesterId);
            }
            StudentEntity studentId = studentStatusEntity.getStudentId();
            if (studentId != null) {
                studentId = em.getReference(studentId.getClass(), studentId.getId());
                studentStatusEntity.setStudentId(studentId);
            }
            em.persist(studentStatusEntity);
            if (semesterId != null) {
                semesterId.getStudentStatusEntityList().add(studentStatusEntity);
                semesterId = em.merge(semesterId);
            }
            if (studentId != null) {
                studentId.getStudentStatusEntityList().add(studentStatusEntity);
                studentId = em.merge(studentId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findStudentStatusEntity(studentStatusEntity.getId()) != null) {
                throw new PreexistingEntityException("StudentStatusEntity " + studentStatusEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(StudentStatusEntity studentStatusEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            StudentStatusEntity persistentStudentStatusEntity = em.find(StudentStatusEntity.class, studentStatusEntity.getId());
            RealSemesterEntity semesterIdOld = persistentStudentStatusEntity.getSemesterId();
            RealSemesterEntity semesterIdNew = studentStatusEntity.getSemesterId();
            StudentEntity studentIdOld = persistentStudentStatusEntity.getStudentId();
            StudentEntity studentIdNew = studentStatusEntity.getStudentId();
            if (semesterIdNew != null) {
                semesterIdNew = em.getReference(semesterIdNew.getClass(), semesterIdNew.getId());
                studentStatusEntity.setSemesterId(semesterIdNew);
            }
            if (studentIdNew != null) {
                studentIdNew = em.getReference(studentIdNew.getClass(), studentIdNew.getId());
                studentStatusEntity.setStudentId(studentIdNew);
            }
            studentStatusEntity = em.merge(studentStatusEntity);
            if (semesterIdOld != null && !semesterIdOld.equals(semesterIdNew)) {
                semesterIdOld.getStudentStatusEntityList().remove(studentStatusEntity);
                semesterIdOld = em.merge(semesterIdOld);
            }
            if (semesterIdNew != null && !semesterIdNew.equals(semesterIdOld)) {
                semesterIdNew.getStudentStatusEntityList().add(studentStatusEntity);
                semesterIdNew = em.merge(semesterIdNew);
            }
            if (studentIdOld != null && !studentIdOld.equals(studentIdNew)) {
                studentIdOld.getStudentStatusEntityList().remove(studentStatusEntity);
                studentIdOld = em.merge(studentIdOld);
            }
            if (studentIdNew != null && !studentIdNew.equals(studentIdOld)) {
                studentIdNew.getStudentStatusEntityList().add(studentStatusEntity);
                studentIdNew = em.merge(studentIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = studentStatusEntity.getId();
                if (findStudentStatusEntity(id) == null) {
                    throw new NonexistentEntityException("The studentStatusEntity with id " + id + " no longer exists.");
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
            StudentStatusEntity studentStatusEntity;
            try {
                studentStatusEntity = em.getReference(StudentStatusEntity.class, id);
                studentStatusEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The studentStatusEntity with id " + id + " no longer exists.", enfe);
            }
            RealSemesterEntity semesterId = studentStatusEntity.getSemesterId();
            if (semesterId != null) {
                semesterId.getStudentStatusEntityList().remove(studentStatusEntity);
                semesterId = em.merge(semesterId);
            }
            StudentEntity studentId = studentStatusEntity.getStudentId();
            if (studentId != null) {
                studentId.getStudentStatusEntityList().remove(studentStatusEntity);
                studentId = em.merge(studentId);
            }
            em.remove(studentStatusEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<StudentStatusEntity> findStudentStatusEntityEntities() {
        return findStudentStatusEntityEntities(true, -1, -1);
    }

    public List<StudentStatusEntity> findStudentStatusEntityEntities(int maxResults, int firstResult) {
        return findStudentStatusEntityEntities(false, maxResults, firstResult);
    }

    private List<StudentStatusEntity> findStudentStatusEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(StudentStatusEntity.class));
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

    public StudentStatusEntity findStudentStatusEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(StudentStatusEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getStudentStatusEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<StudentStatusEntity> rt = cq.from(StudentStatusEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}