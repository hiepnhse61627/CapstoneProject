package com.capstone.jpa;

import com.capstone.entities.PrequisiteEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;

public class ExPrerequisiteJpaController extends PrequisiteEntityJpaController {
    public ExPrerequisiteJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public PrequisiteEntity getPrerequisiteBySubjectId(int subjectId) {
        EntityManager em = null;
        PrequisiteEntity result = null;
        try {
            em = getEntityManager();
           Query query = em.createQuery("SELECT p FROM PrequisiteEntity p Where p.subjectId = :subjectId");
            query.setParameter("subjectId", subjectId);

            List<PrequisiteEntity> list = query.getResultList();
            result = list.get(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return result;
    }

    public List<PrequisiteEntity> getAllPrerequisite() {
        EntityManager em = null;
        List<PrequisiteEntity> result = null;
        try {
            em = getEntityManager();
            Query query = em.createQuery("SELECT p FROM PrequisiteEntity p");

            result = query.getResultList();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return result;
    }
}
