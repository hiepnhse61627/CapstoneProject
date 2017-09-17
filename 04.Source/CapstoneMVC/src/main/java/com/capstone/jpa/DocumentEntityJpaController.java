/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.DocTypeEntity;
import com.capstone.entities.DocumentEntity;
import java.util.ArrayList;
import java.util.Collection;
import com.capstone.entities.DocumentStudentEntity;
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
public class DocumentEntityJpaController implements Serializable {

    public DocumentEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DocumentEntity documentEntity) throws PreexistingEntityException, Exception {
        if (documentEntity.getDocumentsById() == null) {
            documentEntity.setDocumentsById(new ArrayList<DocumentEntity>());
        }
        if (documentEntity.getDocumentStudentsById() == null) {
            documentEntity.setDocumentStudentsById(new ArrayList<DocumentStudentEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DocTypeEntity docTypeByDocTypeId = documentEntity.getDocTypeByDocTypeId();
            if (docTypeByDocTypeId != null) {
                docTypeByDocTypeId = em.getReference(docTypeByDocTypeId.getClass(), docTypeByDocTypeId.getId());
                documentEntity.setDocTypeByDocTypeId(docTypeByDocTypeId);
            }
            DocumentEntity documentByDocParentId = documentEntity.getDocumentByDocParentId();
            if (documentByDocParentId != null) {
                documentByDocParentId = em.getReference(documentByDocParentId.getClass(), documentByDocParentId.getId());
                documentEntity.setDocumentByDocParentId(documentByDocParentId);
            }
            Collection<DocumentEntity> attachedDocumentsById = new ArrayList<DocumentEntity>();
            for (DocumentEntity documentsByIdDocumentEntityToAttach : documentEntity.getDocumentsById()) {
                documentsByIdDocumentEntityToAttach = em.getReference(documentsByIdDocumentEntityToAttach.getClass(), documentsByIdDocumentEntityToAttach.getId());
                attachedDocumentsById.add(documentsByIdDocumentEntityToAttach);
            }
            documentEntity.setDocumentsById(attachedDocumentsById);
            Collection<DocumentStudentEntity> attachedDocumentStudentsById = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentsByIdDocumentStudentEntityToAttach : documentEntity.getDocumentStudentsById()) {
                documentStudentsByIdDocumentStudentEntityToAttach = em.getReference(documentStudentsByIdDocumentStudentEntityToAttach.getClass(), documentStudentsByIdDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentsById.add(documentStudentsByIdDocumentStudentEntityToAttach);
            }
            documentEntity.setDocumentStudentsById(attachedDocumentStudentsById);
            em.persist(documentEntity);
            if (docTypeByDocTypeId != null) {
                docTypeByDocTypeId.getDocumentsById().add(documentEntity);
                docTypeByDocTypeId = em.merge(docTypeByDocTypeId);
            }
            if (documentByDocParentId != null) {
                DocumentEntity oldDocumentByDocParentIdOfDocumentByDocParentId = documentByDocParentId.getDocumentByDocParentId();
                if (oldDocumentByDocParentIdOfDocumentByDocParentId != null) {
                    oldDocumentByDocParentIdOfDocumentByDocParentId.setDocumentByDocParentId(null);
                    oldDocumentByDocParentIdOfDocumentByDocParentId = em.merge(oldDocumentByDocParentIdOfDocumentByDocParentId);
                }
                documentByDocParentId.setDocumentByDocParentId(documentEntity);
                documentByDocParentId = em.merge(documentByDocParentId);
            }
            for (DocumentEntity documentsByIdDocumentEntity : documentEntity.getDocumentsById()) {
                DocumentEntity oldDocumentByDocParentIdOfDocumentsByIdDocumentEntity = documentsByIdDocumentEntity.getDocumentByDocParentId();
                documentsByIdDocumentEntity.setDocumentByDocParentId(documentEntity);
                documentsByIdDocumentEntity = em.merge(documentsByIdDocumentEntity);
                if (oldDocumentByDocParentIdOfDocumentsByIdDocumentEntity != null) {
                    oldDocumentByDocParentIdOfDocumentsByIdDocumentEntity.getDocumentsById().remove(documentsByIdDocumentEntity);
                    oldDocumentByDocParentIdOfDocumentsByIdDocumentEntity = em.merge(oldDocumentByDocParentIdOfDocumentsByIdDocumentEntity);
                }
            }
            for (DocumentStudentEntity documentStudentsByIdDocumentStudentEntity : documentEntity.getDocumentStudentsById()) {
                DocumentEntity oldDocumentByDocumentIdOfDocumentStudentsByIdDocumentStudentEntity = documentStudentsByIdDocumentStudentEntity.getDocumentByDocumentId();
                documentStudentsByIdDocumentStudentEntity.setDocumentByDocumentId(documentEntity);
                documentStudentsByIdDocumentStudentEntity = em.merge(documentStudentsByIdDocumentStudentEntity);
                if (oldDocumentByDocumentIdOfDocumentStudentsByIdDocumentStudentEntity != null) {
                    oldDocumentByDocumentIdOfDocumentStudentsByIdDocumentStudentEntity.getDocumentStudentsById().remove(documentStudentsByIdDocumentStudentEntity);
                    oldDocumentByDocumentIdOfDocumentStudentsByIdDocumentStudentEntity = em.merge(oldDocumentByDocumentIdOfDocumentStudentsByIdDocumentStudentEntity);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDocumentEntity(documentEntity.getId()) != null) {
                throw new PreexistingEntityException("DocumentEntity " + documentEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DocumentEntity documentEntity) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DocumentEntity persistentDocumentEntity = em.find(DocumentEntity.class, documentEntity.getId());
            DocTypeEntity docTypeByDocTypeIdOld = persistentDocumentEntity.getDocTypeByDocTypeId();
            DocTypeEntity docTypeByDocTypeIdNew = documentEntity.getDocTypeByDocTypeId();
            DocumentEntity documentByDocParentIdOld = persistentDocumentEntity.getDocumentByDocParentId();
            DocumentEntity documentByDocParentIdNew = documentEntity.getDocumentByDocParentId();
            Collection<DocumentEntity> documentsByIdOld = persistentDocumentEntity.getDocumentsById();
            Collection<DocumentEntity> documentsByIdNew = documentEntity.getDocumentsById();
            Collection<DocumentStudentEntity> documentStudentsByIdOld = persistentDocumentEntity.getDocumentStudentsById();
            Collection<DocumentStudentEntity> documentStudentsByIdNew = documentEntity.getDocumentStudentsById();
            List<String> illegalOrphanMessages = null;
            for (DocumentStudentEntity documentStudentsByIdOldDocumentStudentEntity : documentStudentsByIdOld) {
                if (!documentStudentsByIdNew.contains(documentStudentsByIdOldDocumentStudentEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DocumentStudentEntity " + documentStudentsByIdOldDocumentStudentEntity + " since its documentByDocumentId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (docTypeByDocTypeIdNew != null) {
                docTypeByDocTypeIdNew = em.getReference(docTypeByDocTypeIdNew.getClass(), docTypeByDocTypeIdNew.getId());
                documentEntity.setDocTypeByDocTypeId(docTypeByDocTypeIdNew);
            }
            if (documentByDocParentIdNew != null) {
                documentByDocParentIdNew = em.getReference(documentByDocParentIdNew.getClass(), documentByDocParentIdNew.getId());
                documentEntity.setDocumentByDocParentId(documentByDocParentIdNew);
            }
            Collection<DocumentEntity> attachedDocumentsByIdNew = new ArrayList<DocumentEntity>();
            for (DocumentEntity documentsByIdNewDocumentEntityToAttach : documentsByIdNew) {
                documentsByIdNewDocumentEntityToAttach = em.getReference(documentsByIdNewDocumentEntityToAttach.getClass(), documentsByIdNewDocumentEntityToAttach.getId());
                attachedDocumentsByIdNew.add(documentsByIdNewDocumentEntityToAttach);
            }
            documentsByIdNew = attachedDocumentsByIdNew;
            documentEntity.setDocumentsById(documentsByIdNew);
            Collection<DocumentStudentEntity> attachedDocumentStudentsByIdNew = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentsByIdNewDocumentStudentEntityToAttach : documentStudentsByIdNew) {
                documentStudentsByIdNewDocumentStudentEntityToAttach = em.getReference(documentStudentsByIdNewDocumentStudentEntityToAttach.getClass(), documentStudentsByIdNewDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentsByIdNew.add(documentStudentsByIdNewDocumentStudentEntityToAttach);
            }
            documentStudentsByIdNew = attachedDocumentStudentsByIdNew;
            documentEntity.setDocumentStudentsById(documentStudentsByIdNew);
            documentEntity = em.merge(documentEntity);
            if (docTypeByDocTypeIdOld != null && !docTypeByDocTypeIdOld.equals(docTypeByDocTypeIdNew)) {
                docTypeByDocTypeIdOld.getDocumentsById().remove(documentEntity);
                docTypeByDocTypeIdOld = em.merge(docTypeByDocTypeIdOld);
            }
            if (docTypeByDocTypeIdNew != null && !docTypeByDocTypeIdNew.equals(docTypeByDocTypeIdOld)) {
                docTypeByDocTypeIdNew.getDocumentsById().add(documentEntity);
                docTypeByDocTypeIdNew = em.merge(docTypeByDocTypeIdNew);
            }
            if (documentByDocParentIdOld != null && !documentByDocParentIdOld.equals(documentByDocParentIdNew)) {
                documentByDocParentIdOld.setDocumentByDocParentId(null);
                documentByDocParentIdOld = em.merge(documentByDocParentIdOld);
            }
            if (documentByDocParentIdNew != null && !documentByDocParentIdNew.equals(documentByDocParentIdOld)) {
                DocumentEntity oldDocumentByDocParentIdOfDocumentByDocParentId = documentByDocParentIdNew.getDocumentByDocParentId();
                if (oldDocumentByDocParentIdOfDocumentByDocParentId != null) {
                    oldDocumentByDocParentIdOfDocumentByDocParentId.setDocumentByDocParentId(null);
                    oldDocumentByDocParentIdOfDocumentByDocParentId = em.merge(oldDocumentByDocParentIdOfDocumentByDocParentId);
                }
                documentByDocParentIdNew.setDocumentByDocParentId(documentEntity);
                documentByDocParentIdNew = em.merge(documentByDocParentIdNew);
            }
            for (DocumentEntity documentsByIdOldDocumentEntity : documentsByIdOld) {
                if (!documentsByIdNew.contains(documentsByIdOldDocumentEntity)) {
                    documentsByIdOldDocumentEntity.setDocumentByDocParentId(null);
                    documentsByIdOldDocumentEntity = em.merge(documentsByIdOldDocumentEntity);
                }
            }
            for (DocumentEntity documentsByIdNewDocumentEntity : documentsByIdNew) {
                if (!documentsByIdOld.contains(documentsByIdNewDocumentEntity)) {
                    DocumentEntity oldDocumentByDocParentIdOfDocumentsByIdNewDocumentEntity = documentsByIdNewDocumentEntity.getDocumentByDocParentId();
                    documentsByIdNewDocumentEntity.setDocumentByDocParentId(documentEntity);
                    documentsByIdNewDocumentEntity = em.merge(documentsByIdNewDocumentEntity);
                    if (oldDocumentByDocParentIdOfDocumentsByIdNewDocumentEntity != null && !oldDocumentByDocParentIdOfDocumentsByIdNewDocumentEntity.equals(documentEntity)) {
                        oldDocumentByDocParentIdOfDocumentsByIdNewDocumentEntity.getDocumentsById().remove(documentsByIdNewDocumentEntity);
                        oldDocumentByDocParentIdOfDocumentsByIdNewDocumentEntity = em.merge(oldDocumentByDocParentIdOfDocumentsByIdNewDocumentEntity);
                    }
                }
            }
            for (DocumentStudentEntity documentStudentsByIdNewDocumentStudentEntity : documentStudentsByIdNew) {
                if (!documentStudentsByIdOld.contains(documentStudentsByIdNewDocumentStudentEntity)) {
                    DocumentEntity oldDocumentByDocumentIdOfDocumentStudentsByIdNewDocumentStudentEntity = documentStudentsByIdNewDocumentStudentEntity.getDocumentByDocumentId();
                    documentStudentsByIdNewDocumentStudentEntity.setDocumentByDocumentId(documentEntity);
                    documentStudentsByIdNewDocumentStudentEntity = em.merge(documentStudentsByIdNewDocumentStudentEntity);
                    if (oldDocumentByDocumentIdOfDocumentStudentsByIdNewDocumentStudentEntity != null && !oldDocumentByDocumentIdOfDocumentStudentsByIdNewDocumentStudentEntity.equals(documentEntity)) {
                        oldDocumentByDocumentIdOfDocumentStudentsByIdNewDocumentStudentEntity.getDocumentStudentsById().remove(documentStudentsByIdNewDocumentStudentEntity);
                        oldDocumentByDocumentIdOfDocumentStudentsByIdNewDocumentStudentEntity = em.merge(oldDocumentByDocumentIdOfDocumentStudentsByIdNewDocumentStudentEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = documentEntity.getId();
                if (findDocumentEntity(id) == null) {
                    throw new NonexistentEntityException("The documentEntity with id " + id + " no longer exists.");
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
            DocumentEntity documentEntity;
            try {
                documentEntity = em.getReference(DocumentEntity.class, id);
                documentEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The documentEntity with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<DocumentStudentEntity> documentStudentsByIdOrphanCheck = documentEntity.getDocumentStudentsById();
            for (DocumentStudentEntity documentStudentsByIdOrphanCheckDocumentStudentEntity : documentStudentsByIdOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This DocumentEntity (" + documentEntity + ") cannot be destroyed since the DocumentStudentEntity " + documentStudentsByIdOrphanCheckDocumentStudentEntity + " in its documentStudentsById field has a non-nullable documentByDocumentId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            DocTypeEntity docTypeByDocTypeId = documentEntity.getDocTypeByDocTypeId();
            if (docTypeByDocTypeId != null) {
                docTypeByDocTypeId.getDocumentsById().remove(documentEntity);
                docTypeByDocTypeId = em.merge(docTypeByDocTypeId);
            }
            DocumentEntity documentByDocParentId = documentEntity.getDocumentByDocParentId();
            if (documentByDocParentId != null) {
                documentByDocParentId.setDocumentByDocParentId(null);
                documentByDocParentId = em.merge(documentByDocParentId);
            }
            Collection<DocumentEntity> documentsById = documentEntity.getDocumentsById();
            for (DocumentEntity documentsByIdDocumentEntity : documentsById) {
                documentsByIdDocumentEntity.setDocumentByDocParentId(null);
                documentsByIdDocumentEntity = em.merge(documentsByIdDocumentEntity);
            }
            em.remove(documentEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DocumentEntity> findDocumentEntityEntities() {
        return findDocumentEntityEntities(true, -1, -1);
    }

    public List<DocumentEntity> findDocumentEntityEntities(int maxResults, int firstResult) {
        return findDocumentEntityEntities(false, maxResults, firstResult);
    }

    private List<DocumentEntity> findDocumentEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DocumentEntity.class));
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

    public DocumentEntity findDocumentEntity(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DocumentEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getDocumentEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DocumentEntity> rt = cq.from(DocumentEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
