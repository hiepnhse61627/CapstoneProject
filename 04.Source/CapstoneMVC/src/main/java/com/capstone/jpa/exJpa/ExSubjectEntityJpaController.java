package com.capstone.jpa.exJpa;

import com.capstone.entities.SubjectEntity;
import com.capstone.entities.SubjectMarkComponentEntity;
import com.capstone.jpa.StudentEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

public class ExSubjectEntityJpaController extends StudentEntityJpaController {

    public ExSubjectEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public void insertSubjectList(List<SubjectEntity> list) {
        EntityManager manager = getEntityManager();
        TypedQuery<SubjectEntity> query = manager.createQuery("SELECT c FROM SubjectEntity c", SubjectEntity.class);
        List<SubjectEntity> cur = query.getResultList();

        manager.getTransaction().begin();
        for (SubjectEntity en : list) {
            try {
                if (!cur.contains(en)) {
                    SubjectMarkComponentEntity entity = new SubjectMarkComponentEntity();
                    entity.setSubjectId(en.getId());

                    entity.setSubjectBySubjectId(en);
                    en.setSubjectMarkComponentById(entity);

                    manager.persist(entity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        manager.getTransaction().commit();
    }
}
