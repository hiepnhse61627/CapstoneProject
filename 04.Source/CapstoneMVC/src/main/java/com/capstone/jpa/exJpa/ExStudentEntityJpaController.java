package com.capstone.jpa.exJpa;

import com.capstone.entities.StudentEntity;
import com.capstone.jpa.StudentEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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
                em.persist(student);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
