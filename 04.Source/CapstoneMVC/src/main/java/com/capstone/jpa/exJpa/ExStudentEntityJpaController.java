package com.capstone.jpa.exJpa;

import com.capstone.entities.StudentEntity;
import com.capstone.jpa.StudentEntityJpaController;

import javax.persistence.*;
import java.util.List;

public class ExStudentEntityJpaController extends StudentEntityJpaController {

    public ExStudentEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public void createStudentList(List<StudentEntity> students) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            for (StudentEntity student : students) {
                TypedQuery<StudentEntity> single = em.createQuery("SELECT c FROM StudentEntity c WHERE c.rollNumber = :roll", StudentEntity.class);
                single.setParameter("roll", student.getRollNumber());
                if (single.getResultList().size() == 0) {
                    em.persist(student);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public StudentEntity findStudentByRollNumber(String rollNumber) {
        EntityManager em = getEntityManager();
        StudentEntity studentEntity = new StudentEntity();
        try {
            String sqlString = "SELECT s FROM StudentEntity s WHERE s.rollNumber = :rollNumber";
            Query query = em.createQuery(sqlString);
            query.setParameter("rollNumber", rollNumber);

            studentEntity = (StudentEntity) query.getSingleResult();

            return studentEntity;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
