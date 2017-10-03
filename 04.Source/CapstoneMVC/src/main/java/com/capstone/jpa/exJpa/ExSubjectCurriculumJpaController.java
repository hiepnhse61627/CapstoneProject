package com.capstone.jpa.exJpa;

import com.capstone.entities.SubjectCurriculumEntity;
import com.capstone.jpa.SubjectCurriculumEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class ExSubjectCurriculumJpaController extends SubjectCurriculumEntityJpaController {

    public ExSubjectCurriculumJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public SubjectCurriculumEntity findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SubjectCurriculumEntity.class, id);
        } finally {
            em.close();
        }
    }
}
