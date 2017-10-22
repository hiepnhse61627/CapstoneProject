package com.capstone.jpa.exJpa;

import com.capstone.entities.DocTypeEntity;
import com.capstone.jpa.DocTypeEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class ExDocTypeEntityJpaController extends DocTypeEntityJpaController {
    public ExDocTypeEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public List<DocTypeEntity> getAllDocTypes() {
        return this.findDocTypeEntityEntities();
    }

    public DocTypeEntity createDocType(DocTypeEntity entity) {
        EntityManager em = null;

        try {
            em = getEntityManager();

            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } finally {
            em.close();
        }


        return entity;
    }

}
