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
import com.capstone.entities.DocumentStudentEntity;
import com.capstone.jpa.exceptions.IllegalOrphanException;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

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
        if (documentEntity.getDocumentStudentList() == null) {
            documentEntity.setDocumentStudentList(new ArrayList<DocumentStudentEntity>());
        }
        if (documentEntity.getDocumentList() == null) {
            documentEntity.setDocumentList(new ArrayList<DocumentEntity>());
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
            List<DocumentStudentEntity> attachedDocumentStudentList = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentListDocumentStudentEntityToAttach : documentEntity.getDocumentStudentList()) {
                documentStudentListDocumentStudentEntityToAttach = em.getReference(documentStudentListDocumentStudentEntityToAttach.getClass(), documentStudentListDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentList.add(documentStudentListDocumentStudentEntityToAttach);
            }
            documentEntity.setDocumentStudentList(attachedDocumentStudentList);
            List<DocumentEntity> attachedDocumentList = new ArrayList<DocumentEntity>();
            for (DocumentEntity documentListDocumentEntityToAttach : documentEntity.getDocumentList()) {
                documentListDocumentEntityToAttach = em.getReference(documentListDocumentEntityToAttach.getClass(), documentListDocumentEntityToAttach.getId());
                attachedDocumentList.add(documentListDocumentEntityToAttach);
            }
            documentEntity.setDocumentList(attachedDocumentList);
            em.persist(documentEntity);
            if (docTypeId != null) {
                docTypeId.getDocumentList().add(documentEntity);
                docTypeId = em.merge(docTypeId);
            }
            if (docParentId != null) {
                docParentId.getDocumentList().add(documentEntity);
                docParentId = em.merge(docParentId);
            }
            for (DocumentStudentEntity documentStudentListDocumentStudentEntity : documentEntity.getDocumentStudentList()) {
                DocumentEntity oldDocumentIdOfDocumentStudentListDocumentStudentEntity = documentStudentListDocumentStudentEntity.getDocumentId();
                documentStudentListDocumentStudentEntity.setDocumentId(documentEntity);
                documentStudentListDocumentStudentEntity = em.merge(documentStudentListDocumentStudentEntity);
                if (oldDocumentIdOfDocumentStudentListDocumentStudentEntity != null) {
                    oldDocumentIdOfDocumentStudentListDocumentStudentEntity.getDocumentStudentList().remove(documentStudentListDocumentStudentEntity);
                    oldDocumentIdOfDocumentStudentListDocumentStudentEntity = em.merge(oldDocumentIdOfDocumentStudentListDocumentStudentEntity);
                }
            }
            for (DocumentEntity documentListDocumentEntity : documentEntity.getDocumentList()) {
                DocumentEntity oldDocParentIdOfDocumentListDocumentEntity = documentListDocumentEntity.getDocParentId();
                documentListDocumentEntity.setDocParentId(documentEntity);
                documentListDocumentEntity = em.merge(documentListDocumentEntity);
                if (oldDocParentIdOfDocumentListDocumentEntity != null) {
                    oldDocParentIdOfDocumentListDocumentEntity.getDocumentList().remove(documentListDocumentEntity);
                    oldDocParentIdOfDocumentListDocumentEntity = em.merge(oldDocParentIdOfDocumentListDocumentEntity);
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
            List<DocumentStudentEntity> documentStudentListOld = persistentDocumentEntity.getDocumentStudentList();
            List<DocumentStudentEntity> documentStudentListNew = documentEntity.getDocumentStudentList();
            List<DocumentEntity> documentListOld = persistentDocumentEntity.getDocumentList();
            List<DocumentEntity> documentListNew = documentEntity.getDocumentList();
            List<String> illegalOrphanMessages = null;
            for (DocumentStudentEntity documentStudentListOldDocumentStudentEntity : documentStudentListOld) {
                if (!documentStudentListNew.contains(documentStudentListOldDocumentStudentEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DocumentStudentEntity " + documentStudentListOldDocumentStudentEntity + " since its documentId field is not nullable.");
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
            List<DocumentStudentEntity> attachedDocumentStudentListNew = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentListNewDocumentStudentEntityToAttach : documentStudentListNew) {
                documentStudentListNewDocumentStudentEntityToAttach = em.getReference(documentStudentListNewDocumentStudentEntityToAttach.getClass(), documentStudentListNewDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentListNew.add(documentStudentListNewDocumentStudentEntityToAttach);
            }
            documentStudentListNew = attachedDocumentStudentListNew;
            documentEntity.setDocumentStudentList(documentStudentListNew);
            List<DocumentEntity> attachedDocumentListNew = new ArrayList<DocumentEntity>();
            for (DocumentEntity documentListNewDocumentEntityToAttach : documentListNew) {
                documentListNewDocumentEntityToAttach = em.getReference(documentListNewDocumentEntityToAttach.getClass(), documentListNewDocumentEntityToAttach.getId());
                attachedDocumentListNew.add(documentListNewDocumentEntityToAttach);
            }
            documentListNew = attachedDocumentListNew;
            documentEntity.setDocumentList(documentListNew);
            documentEntity = em.merge(documentEntity);
            if (docTypeIdOld != null && !docTypeIdOld.equals(docTypeIdNew)) {
                docTypeIdOld.getDocumentList().remove(documentEntity);
                docTypeIdOld = em.merge(docTypeIdOld);
            }
            if (docTypeIdNew != null && !docTypeIdNew.equals(docTypeIdOld)) {
                docTypeIdNew.getDocumentList().add(documentEntity);
                docTypeIdNew = em.merge(docTypeIdNew);
            }
            if (docParentIdOld != null && !docParentIdOld.equals(docParentIdNew)) {
                docParentIdOld.getDocumentList().remove(documentEntity);
                docParentIdOld = em.merge(docParentIdOld);
            }
            if (docParentIdNew != null && !docParentIdNew.equals(docParentIdOld)) {
                docParentIdNew.getDocumentList().add(documentEntity);
                docParentIdNew = em.merge(docParentIdNew);
            }
            for (DocumentStudentEntity documentStudentListNewDocumentStudentEntity : documentStudentListNew) {
                if (!documentStudentListOld.contains(documentStudentListNewDocumentStudentEntity)) {
                    DocumentEntity oldDocumentIdOfDocumentStudentListNewDocumentStudentEntity = documentStudentListNewDocumentStudentEntity.getDocumentId();
                    documentStudentListNewDocumentStudentEntity.setDocumentId(documentEntity);
                    documentStudentListNewDocumentStudentEntity = em.merge(documentStudentListNewDocumentStudentEntity);
                    if (oldDocumentIdOfDocumentStudentListNewDocumentStudentEntity != null && !oldDocumentIdOfDocumentStudentListNewDocumentStudentEntity.equals(documentEntity)) {
                        oldDocumentIdOfDocumentStudentListNewDocumentStudentEntity.getDocumentStudentList().remove(documentStudentListNewDocumentStudentEntity);
                        oldDocumentIdOfDocumentStudentListNewDocumentStudentEntity = em.merge(oldDocumentIdOfDocumentStudentListNewDocumentStudentEntity);
                    }
                }
            }
            for (DocumentEntity documentListOldDocumentEntity : documentListOld) {
                if (!documentListNew.contains(documentListOldDocumentEntity)) {
                    documentListOldDocumentEntity.setDocParentId(null);
                    documentListOldDocumentEntity = em.merge(documentListOldDocumentEntity);
                }
            }
            for (DocumentEntity documentListNewDocumentEntity : documentListNew) {
                if (!documentListOld.contains(documentListNewDocumentEntity)) {
                    DocumentEntity oldDocParentIdOfDocumentListNewDocumentEntity = documentListNewDocumentEntity.getDocParentId();
                    documentListNewDocumentEntity.setDocParentId(documentEntity);
                    documentListNewDocumentEntity = em.merge(documentListNewDocumentEntity);
                    if (oldDocParentIdOfDocumentListNewDocumentEntity != null && !oldDocParentIdOfDocumentListNewDocumentEntity.equals(documentEntity)) {
                        oldDocParentIdOfDocumentListNewDocumentEntity.getDocumentList().remove(documentListNewDocumentEntity);
                        oldDocParentIdOfDocumentListNewDocumentEntity = em.merge(oldDocParentIdOfDocumentListNewDocumentEntity);
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
            List<DocumentStudentEntity> documentStudentListOrphanCheck = documentEntity.getDocumentStudentList();
            for (DocumentStudentEntity documentStudentListOrphanCheckDocumentStudentEntity : documentStudentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This DocumentEntity (" + documentEntity + ") cannot be destroyed since the DocumentStudentEntity " + documentStudentListOrphanCheckDocumentStudentEntity + " in its documentStudentList field has a non-nullable documentId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            DocTypeEntity docTypeId = documentEntity.getDocTypeId();
            if (docTypeId != null) {
                docTypeId.getDocumentList().remove(documentEntity);
                docTypeId = em.merge(docTypeId);
            }
            DocumentEntity docParentId = documentEntity.getDocParentId();
            if (docParentId != null) {
                docParentId.getDocumentList().remove(documentEntity);
                docParentId = em.merge(docParentId);
            }
            List<DocumentEntity> documentList = documentEntity.getDocumentList();
            for (DocumentEntity documentListDocumentEntity : documentList) {
                documentListDocumentEntity.setDocParentId(null);
                documentListDocumentEntity = em.merge(documentListDocumentEntity);
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
