package com.capstone.jpa.exJpa;

import com.capstone.entities.DocumentEntity;
import com.capstone.jpa.DocumentEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
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

    public DocumentEntity getDocByDocType(int docType) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            TypedQuery<DocumentEntity> query = em.createQuery("SELECT a FROM DocumentEntity a WHERE a.docTypeId.id = :doc", DocumentEntity.class);
            query.setParameter("doc", docType);
            return query.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("not found " + docType);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
