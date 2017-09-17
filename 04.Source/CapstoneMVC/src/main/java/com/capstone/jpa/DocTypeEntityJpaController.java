/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.entities.DocTypeEntity;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.DocumentEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.capstone.jpa.exceptions.IllegalOrphanException;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

/**
 *
 * @author hiepnhse61627
 */
public class DocTypeEntityJpaController implements Serializable {

    public DocTypeEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DocTypeEntity docTypeEntity) throws PreexistingEntityException, Exception {
        if (docTypeEntity.getDocumentsById() == null) {
            docTypeEntity.setDocumentsById(new ArrayList<DocumentEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<DocumentEntity> attachedDocumentsById = new ArrayList<DocumentEntity>();
            for (DocumentEntity documentsByIdDocumentEntityToAttach : docTypeEntity.getDocumentsById()) {
                documentsByIdDocumentEntityToAttach = em.getReference(documentsByIdDocumentEntityToAttach.getClass(), documentsByIdDocumentEntityToAttach.getId());
                attachedDocumentsById.add(documentsByIdDocumentEntityToAttach);
            }
            docTypeEntity.setDocumentsById(attachedDocumentsById);
            em.persist(docTypeEntity);
            for (DocumentEntity documentsByIdDocumentEntity : docTypeEntity.getDocumentsById()) {
                DocTypeEntity oldDocTypeByDocTypeIdOfDocumentsByIdDocumentEntity = documentsByIdDocumentEntity.getDocTypeByDocTypeId();
                documentsByIdDocumentEntity.setDocTypeByDocTypeId(docTypeEntity);
                documentsByIdDocumentEntity = em.merge(documentsByIdDocumentEntity);
                if (oldDocTypeByDocTypeIdOfDocumentsByIdDocumentEntity != null) {
                    oldDocTypeByDocTypeIdOfDocumentsByIdDocumentEntity.getDocumentsById().remove(documentsByIdDocumentEntity);
                    oldDocTypeByDocTypeIdOfDocumentsByIdDocumentEntity = em.merge(oldDocTypeByDocTypeIdOfDocumentsByIdDocumentEntity);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDocTypeEntity(docTypeEntity.getId()) != null) {
                throw new PreexistingEntityException("DocTypeEntity " + docTypeEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DocTypeEntity docTypeEntity) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DocTypeEntity persistentDocTypeEntity = em.find(DocTypeEntity.class, docTypeEntity.getId());
            Collection<DocumentEntity> documentsByIdOld = persistentDocTypeEntity.getDocumentsById();
            Collection<DocumentEntity> documentsByIdNew = docTypeEntity.getDocumentsById();
            List<String> illegalOrphanMessages = null;
            for (DocumentEntity documentsByIdOldDocumentEntity : documentsByIdOld) {
                if (!documentsByIdNew.contains(documentsByIdOldDocumentEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DocumentEntity " + documentsByIdOldDocumentEntity + " since its docTypeByDocTypeId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<DocumentEntity> attachedDocumentsByIdNew = new ArrayList<DocumentEntity>();
            for (DocumentEntity documentsByIdNewDocumentEntityToAttach : documentsByIdNew) {
                documentsByIdNewDocumentEntityToAttach = em.getReference(documentsByIdNewDocumentEntityToAttach.getClass(), documentsByIdNewDocumentEntityToAttach.getId());
                attachedDocumentsByIdNew.add(documentsByIdNewDocumentEntityToAttach);
            }
            documentsByIdNew = attachedDocumentsByIdNew;
            docTypeEntity.setDocumentsById(documentsByIdNew);
            docTypeEntity = em.merge(docTypeEntity);
            for (DocumentEntity documentsByIdNewDocumentEntity : documentsByIdNew) {
                if (!documentsByIdOld.contains(documentsByIdNewDocumentEntity)) {
                    DocTypeEntity oldDocTypeByDocTypeIdOfDocumentsByIdNewDocumentEntity = documentsByIdNewDocumentEntity.getDocTypeByDocTypeId();
                    documentsByIdNewDocumentEntity.setDocTypeByDocTypeId(docTypeEntity);
                    documentsByIdNewDocumentEntity = em.merge(documentsByIdNewDocumentEntity);
                    if (oldDocTypeByDocTypeIdOfDocumentsByIdNewDocumentEntity != null && !oldDocTypeByDocTypeIdOfDocumentsByIdNewDocumentEntity.equals(docTypeEntity)) {
                        oldDocTypeByDocTypeIdOfDocumentsByIdNewDocumentEntity.getDocumentsById().remove(documentsByIdNewDocumentEntity);
                        oldDocTypeByDocTypeIdOfDocumentsByIdNewDocumentEntity = em.merge(oldDocTypeByDocTypeIdOfDocumentsByIdNewDocumentEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = docTypeEntity.getId();
                if (findDocTypeEntity(id) == null) {
                    throw new NonexistentEntityException("The docTypeEntity with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DocTypeEntity docTypeEntity;
            try {
                docTypeEntity = em.getReference(DocTypeEntity.class, id);
                docTypeEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The docTypeEntity with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<DocumentEntity> documentsByIdOrphanCheck = docTypeEntity.getDocumentsById();
            for (DocumentEntity documentsByIdOrphanCheckDocumentEntity : documentsByIdOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This DocTypeEntity (" + docTypeEntity + ") cannot be destroyed since the DocumentEntity " + documentsByIdOrphanCheckDocumentEntity + " in its documentsById field has a non-nullable docTypeByDocTypeId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(docTypeEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DocTypeEntity> findDocTypeEntityEntities() {
        return findDocTypeEntityEntities(true, -1, -1);
    }

    public List<DocTypeEntity> findDocTypeEntityEntities(int maxResults, int firstResult) {
        return findDocTypeEntityEntities(false, maxResults, firstResult);
    }

    private List<DocTypeEntity> findDocTypeEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DocTypeEntity.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public DocTypeEntity findDocTypeEntity(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DocTypeEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getDocTypeEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DocTypeEntity> rt = cq.from(DocTypeEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
