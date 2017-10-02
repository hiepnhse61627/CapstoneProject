package com.capstone.jpa.exJpa;

import com.capstone.entities.SubjectCurriculumEntity;
import com.capstone.jpa.SubjectCurriculumEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

public class ExSubjectCurriculumJpaController extends SubjectCurriculumEntityJpaController {

    public ExSubjectCurriculumJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public SubjectCurriculumEntity createCurriculum(SubjectCurriculumEntity entity) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(entity);
            em.flush();
            em.getTransaction().commit();

            return entity;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void createCurriculumList(List<SubjectCurriculumEntity> subjectCurriculumEntityList) {
        EntityManager em = getEntityManager();
        for (SubjectCurriculumEntity subjectCurriculumEntity : subjectCurriculumEntityList) {
            try {
                TypedQuery<SubjectCurriculumEntity> tmp = em.createQuery("SELECT c FROM SubjectCurriculumEntity c WHERE c.name = :name AND c.description = :description", SubjectCurriculumEntity.class);
                tmp.setParameter("name", subjectCurriculumEntity.getName());
                tmp.setParameter("description", subjectCurriculumEntity.getDescription());
                if (tmp.getResultList().size() == 0) {
                    em.getTransaction().begin();
                    em.persist(subjectCurriculumEntity);
                    em.getTransaction().commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
