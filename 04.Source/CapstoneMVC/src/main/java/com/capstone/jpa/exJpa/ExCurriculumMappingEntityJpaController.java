package com.capstone.jpa.exJpa;

import com.capstone.entities.CurriculumMappingEntity;
import com.capstone.jpa.CurriculumEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class ExCurriculumMappingEntityJpaController extends CurriculumEntityJpaController {
    public ExCurriculumMappingEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public CurriculumMappingEntity createCurriculumMapping(CurriculumMappingEntity entity) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(entity);
            em.flush();
            em.refresh(entity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return entity;
    }

    public String getSemesterTermByStudentIdAndProgramId(int studentId, int programId) {
        EntityManager em = getEntityManager();
        String semesterTerm = "";
        try {
            String sqlString = "SELECT DISTINCT c.Term FROM student s\n" +
                    "INNER JOIN Marks m\n" +
                    "ON s.ID = m.StudentId\n" +
                    "INNER JOIN Curriculum_Mapping c\n" +
                    "ON c.SubId = m.SubjectId\n" +
                    "INNER JOIN Subject_Curriculum sc\n" +
                    "ON c.CurId = sc.Id AND s.ID = ? AND sc.ProgramId = ? ORDER BY c.Term DESC";
            Query query = em.createNativeQuery(sqlString);
            query.setParameter(1, studentId);
            query.setParameter(2, programId);
            query.setMaxResults(1);

            semesterTerm = query.getSingleResult().toString();

            return semesterTerm;
        } catch (NoResultException nrEx) {
            return null;
        }
    }
}
