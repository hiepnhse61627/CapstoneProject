package com.capstone.jpa.exJpa;

import com.capstone.entities.StudentStatusEntity;
import com.capstone.jpa.StudentStatusEntityJpaController;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class ExStudentStatusEntityJpaController extends StudentStatusEntityJpaController {
    public ExStudentStatusEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public List<StudentStatusEntity> getStudentStatusForStudentArrangement(int semesterId, List<String> statusList) {
        List<StudentStatusEntity> result = null;
        EntityManager em = null;

        try {
            em = getEntityManager();

            String queryStr = "SELECT s FROM StudentStatusEntity s" +
                    " WHERE s.semesterId.id = :semesterId AND s.status IN :statusList" +
                    " AND s.studentId.shift IS NOT NULL";
            TypedQuery<StudentStatusEntity> query = em.createQuery(queryStr, StudentStatusEntity.class);
            query.setParameter("semesterId", semesterId);
            query.setParameter("statusList", statusList);

            result = query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    public StudentStatusEntity getStudentStatusBySemesterIdAndStudentId(Integer semesterId, Integer studentId) {
        StudentStatusEntity studentStatusEntity = new StudentStatusEntity();
        EntityManager em = getEntityManager();

        try {
            String sqlstring = "SELECT s FROM StudentStatusEntity s WHERE s.semesterId.id = :semesterId AND s.studentId.id = :studentId";
            Query query = em.createQuery(sqlstring);
            query.setParameter("semesterId", semesterId);
            query.setParameter("studentId", studentId);

            studentStatusEntity = (StudentStatusEntity) query.getSingleResult();

            return studentStatusEntity;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<StudentStatusEntity> getStudentStatusBySemesterId(Integer semesterId) {
        List<StudentStatusEntity> studentStatusList ;
        EntityManager em = getEntityManager();

        try {
            String sqlstring = "SELECT s FROM StudentStatusEntity s WHERE s.semesterId.id = :semesterId";
            Query query = em.createQuery(sqlstring);
            query.setParameter("semesterId", semesterId);

            studentStatusList = query.getResultList();

            return studentStatusList;
        } catch (NoResultException nrEx) {
            System.out.println(nrEx.getMessage());
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<StudentStatusEntity> getStudentStatusesByStudentId(int studentId) {
        List<StudentStatusEntity> result = null;
        EntityManager em = null;

        try {
            em = getEntityManager();

            String queryStr = "SELECT s FROM StudentStatusEntity s" +
                    " WHERE s.studentId.id = :studentId";
            TypedQuery<StudentStatusEntity> query = em.createQuery(queryStr, StudentStatusEntity.class);
            query.setParameter("studentId", studentId);

            result = query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }
}

