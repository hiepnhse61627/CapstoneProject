package com.capstone.jpa.exJpa;

import com.capstone.entities.DocumentEntity;
import com.capstone.jpa.DocumentEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class ExDocumentEntityJpaController extends DocumentEntityJpaController {
    public ExDocumentEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public List<DocumentEntity> getAllDocuments() {
        return this.findDocumentEntityEntities();
    }

    public DocumentEntity createDocument(DocumentEntity entity) {
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
