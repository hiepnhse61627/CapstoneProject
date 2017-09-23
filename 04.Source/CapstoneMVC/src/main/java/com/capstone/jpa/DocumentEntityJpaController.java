/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.jpa.exceptions.IllegalOrphanException;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.DocTypeEntity;
import com.capstone.entities.DocumentEntity;
import com.capstone.entities.DocumentStudentEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rem
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
        if (documentEntity.getDocumentStudentEntityList() == null) {
            documentEntity.setDocumentStudentEntityList(new ArrayList<DocumentStudentEntity>());
        }
        if (documentEntity.getDocumentEntityList() == null) {
            documentEntity.setDocumentEntityList(new ArrayList<DocumentEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DocTypeEntity docTypeId = documentEntity.getDocTypeId();
            if (docTypeId != null) {
                docTypeId = em.getReference(docTypeId.getClass(), docTypeId.getId());
                documentEntity.setDocTypeId(docTypeId);
            }
            DocumentEntity docParentId = documentEntity.getDocParentId();
            if (docParentId != null) {
                docParentId = em.getReference(docParentId.getClass(), docParentId.getId());
                documentEntity.setDocParentId(docParentId);
            }
            List<DocumentStudentEntity> attachedDocumentStudentEntityList = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentEntityListDocumentStudentEntityToAttach : documentEntity.getDocumentStudentEntityList()) {
                documentStudentEntityListDocumentStudentEntityToAttach = em.getReference(documentStudentEntityListDocumentStudentEntityToAttach.getClass(), documentStudentEntityListDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentEntityList.add(documentStudentEntityListDocumentStudentEntityToAttach);
            }
            documentEntity.setDocumentStudentEntityList(attachedDocumentStudentEntityList);
            List<DocumentEntity> attachedDocumentEntityList = new ArrayList<DocumentEntity>();
            for (DocumentEntity documentEntityListDocumentEntityToAttach : documentEntity.getDocumentEntityList()) {
                documentEntityListDocumentEntityToAttach = em.getReference(documentEntityListDocumentEntityToAttach.getClass(), documentEntityListDocumentEntityToAttach.getId());
                attachedDocumentEntityList.add(documentEntityListDocumentEntityToAttach);
            }
            documentEntity.setDocumentEntityList(attachedDocumentEntityList);
            em.persist(documentEntity);
            if (docTypeId != null) {
                docTypeId.getDocumentEntityList().add(documentEntity);
                docTypeId = em.merge(docTypeId);
            }
            if (docParentId != null) {
                docParentId.getDocumentEntityList().add(documentEntity);
                docParentId = em.merge(docParentId);
            }
            for (DocumentStudentEntity documentStudentEntityListDocumentStudentEntity : documentEntity.getDocumentStudentEntityList()) {
                DocumentEntity oldDocumentIdOfDocumentStudentEntityListDocumentStudentEntity = documentStudentEntityListDocumentStudentEntity.getDocumentId();
                documentStudentEntityListDocumentStudentEntity.setDocumentId(documentEntity);
                documentStudentEntityListDocumentStudentEntity = em.merge(documentStudentEntityListDocumentStudentEntity);
                if (oldDocumentIdOfDocumentStudentEntityListDocumentStudentEntity != null) {
                    oldDocumentIdOfDocumentStudentEntityListDocumentStudentEntity.getDocumentStudentEntityList().remove(documentStudentEntityListDocumentStudentEntity);
                    oldDocumentIdOfDocumentStudentEntityListDocumentStudentEntity = em.merge(oldDocumentIdOfDocumentStudentEntityListDocumentStudentEntity);
                }
            }
            for (DocumentEntity documentEntityListDocumentEntity : documentEntity.getDocumentEntityList()) {
                DocumentEntity oldDocParentIdOfDocumentEntityListDocumentEntity = documentEntityListDocumentEntity.getDocParentId();
                documentEntityListDocumentEntity.setDocParentId(documentEntity);
                documentEntityListDocumentEntity = em.merge(documentEntityListDocumentEntity);
                if (oldDocParentIdOfDocumentEntityListDocumentEntity != null) {
                    oldDocParentIdOfDocumentEntityListDocumentEntity.getDocumentEntityList().remove(documentEntityListDocumentEntity);
                    oldDocParentIdOfDocumentEntityListDocumentEntity = em.merge(oldDocParentIdOfDocumentEntityListDocumentEntity);
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
            DocTypeEntity docTypeIdOld = persistentDocumentEntity.getDocTypeId();
            DocTypeEntity docTypeIdNew = documentEntity.getDocTypeId();
            DocumentEntity docParentIdOld = persistentDocumentEntity.getDocParentId();
            DocumentEntity docParentIdNew = documentEntity.getDocParentId();
            List<DocumentStudentEntity> documentStudentEntityListOld = persistentDocumentEntity.getDocumentStudentEntityList();
            List<DocumentStudentEntity> documentStudentEntityListNew = documentEntity.getDocumentStudentEntityList();
            List<DocumentEntity> documentEntityListOld = persistentDocumentEntity.getDocumentEntityList();
            List<DocumentEntity> documentEntityListNew = documentEntity.getDocumentEntityList();
            List<String> illegalOrphanMessages = null;
            for (DocumentStudentEntity documentStudentEntityListOldDocumentStudentEntity : documentStudentEntityListOld) {
                if (!documentStudentEntityListNew.contains(documentStudentEntityListOldDocumentStudentEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DocumentStudentEntity " + documentStudentEntityListOldDocumentStudentEntity + " since its documentId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (docTypeIdNew != null) {
                docTypeIdNew = em.getReference(docTypeIdNew.getClass(), docTypeIdNew.getId());
                documentEntity.setDocTypeId(docTypeIdNew);
            }
            if (docParentIdNew != null) {
                docParentIdNew = em.getReference(docParentIdNew.getClass(), docParentIdNew.getId());
                documentEntity.setDocParentId(docParentIdNew);
            }
            List<DocumentStudentEntity> attachedDocumentStudentEntityListNew = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentEntityListNewDocumentStudentEntityToAttach : documentStudentEntityListNew) {
                documentStudentEntityListNewDocumentStudentEntityToAttach = em.getReference(documentStudentEntityListNewDocumentStudentEntityToAttach.getClass(), documentStudentEntityListNewDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentEntityListNew.add(documentStudentEntityListNewDocumentStudentEntityToAttach);
            }
            documentStudentEntityListNew = attachedDocumentStudentEntityListNew;
            documentEntity.setDocumentStudentEntityList(documentStudentEntityListNew);
            List<DocumentEntity> attachedDocumentEntityListNew = new ArrayList<DocumentEntity>();
            for (DocumentEntity documentEntityListNewDocumentEntityToAttach : documentEntityListNew) {
                documentEntityListNewDocumentEntityToAttach = em.getReference(documentEntityListNewDocumentEntityToAttach.getClass(), documentEntityListNewDocumentEntityToAttach.getId());
                attachedDocumentEntityListNew.add(documentEntityListNewDocumentEntityToAttach);
            }
            documentEntityListNew = attachedDocumentEntityListNew;
            documentEntity.setDocumentEntityList(documentEntityListNew);
            documentEntity = em.merge(documentEntity);
            if (docTypeIdOld != null && !docTypeIdOld.equals(docTypeIdNew)) {
                docTypeIdOld.getDocumentEntityList().remove(documentEntity);
                docTypeIdOld = em.merge(docTypeIdOld);
            }
            if (docTypeIdNew != null && !docTypeIdNew.equals(docTypeIdOld)) {
                docTypeIdNew.getDocumentEntityList().add(documentEntity);
                docTypeIdNew = em.merge(docTypeIdNew);
            }
            if (docParentIdOld != null && !docParentIdOld.equals(docParentIdNew)) {
                docParentIdOld.getDocumentEntityList().remove(documentEntity);
                docParentIdOld = em.merge(docParentIdOld);
            }
            if (docParentIdNew != null && !docParentIdNew.equals(docParentIdOld)) {
                docParentIdNew.getDocumentEntityList().add(documentEntity);
                docParentIdNew = em.merge(docParentIdNew);
            }
            for (DocumentStudentEntity documentStudentEntityListNewDocumentStudentEntity : documentStudentEntityListNew) {
                if (!documentStudentEntityListOld.contains(documentStudentEntityListNewDocumentStudentEntity)) {
                    DocumentEntity oldDocumentIdOfDocumentStudentEntityListNewDocumentStudentEntity = documentStudentEntityListNewDocumentStudentEntity.getDocumentId();
                    documentStudentEntityListNewDocumentStudentEntity.setDocumentId(documentEntity);
                    documentStudentEntityListNewDocumentStudentEntity = em.merge(documentStudentEntityListNewDocumentStudentEntity);
                    if (oldDocumentIdOfDocumentStudentEntityListNewDocumentStudentEntity != null && !oldDocumentIdOfDocumentStudentEntityListNewDocumentStudentEntity.equals(documentEntity)) {
                        oldDocumentIdOfDocumentStudentEntityListNewDocumentStudentEntity.getDocumentStudentEntityList().remove(documentStudentEntityListNewDocumentStudentEntity);
                        oldDocumentIdOfDocumentStudentEntityListNewDocumentStudentEntity = em.merge(oldDocumentIdOfDocumentStudentEntityListNewDocumentStudentEntity);
                    }
                }
            }
            for (DocumentEntity documentEntityListOldDocumentEntity : documentEntityListOld) {
                if (!documentEntityListNew.contains(documentEntityListOldDocumentEntity)) {
                    documentEntityListOldDocumentEntity.setDocParentId(null);
                    documentEntityListOldDocumentEntity = em.merge(documentEntityListOldDocumentEntity);
                }
            }
            for (DocumentEntity documentEntityListNewDocumentEntity : documentEntityListNew) {
                if (!documentEntityListOld.contains(documentEntityListNewDocumentEntity)) {
                    DocumentEntity oldDocParentIdOfDocumentEntityListNewDocumentEntity = documentEntityListNewDocumentEntity.getDocParentId();
                    documentEntityListNewDocumentEntity.setDocParentId(documentEntity);
                    documentEntityListNewDocumentEntity = em.merge(documentEntityListNewDocumentEntity);
                    if (oldDocParentIdOfDocumentEntityListNewDocumentEntity != null && !oldDocParentIdOfDocumentEntityListNewDocumentEntity.equals(documentEntity)) {
                        oldDocParentIdOfDocumentEntityListNewDocumentEntity.getDocumentEntityList().remove(documentEntityListNewDocumentEntity);
                        oldDocParentIdOfDocumentEntityListNewDocumentEntity = em.merge(oldDocParentIdOfDocumentEntityListNewDocumentEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = documentEntity.getId();
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

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
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
            List<DocumentStudentEntity> documentStudentEntityListOrphanCheck = documentEntity.getDocumentStudentEntityList();
            for (DocumentStudentEntity documentStudentEntityListOrphanCheckDocumentStudentEntity : documentStudentEntityListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This DocumentEntity (" + documentEntity + ") cannot be destroyed since the DocumentStudentEntity " + documentStudentEntityListOrphanCheckDocumentStudentEntity + " in its documentStudentEntityList field has a non-nullable documentId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            DocTypeEntity docTypeId = documentEntity.getDocTypeId();
            if (docTypeId != null) {
                docTypeId.getDocumentEntityList().remove(documentEntity);
                docTypeId = em.merge(docTypeId);
            }
            DocumentEntity docParentId = documentEntity.getDocParentId();
            if (docParentId != null) {
                docParentId.getDocumentEntityList().remove(documentEntity);
                docParentId = em.merge(docParentId);
            }
            List<DocumentEntity> documentEntityList = documentEntity.getDocumentEntityList();
            for (DocumentEntity documentEntityListDocumentEntity : documentEntityList) {
                documentEntityListDocumentEntity.setDocParentId(null);
                documentEntityListDocumentEntity = em.merge(documentEntityListDocumentEntity);
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

    public DocumentEntity findDocumentEntity(Integer id) {
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
