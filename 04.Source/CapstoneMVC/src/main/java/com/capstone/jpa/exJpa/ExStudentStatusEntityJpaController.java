package com.capstone.jpa.exJpa;

import com.capstone.entities.StudentStatusEntity;
import com.capstone.jpa.StudentStatusEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
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
                    " AND s.studentId.shift IS NOT NULL AND s.studentId.payRollClass IS NOT NULL";
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
}
