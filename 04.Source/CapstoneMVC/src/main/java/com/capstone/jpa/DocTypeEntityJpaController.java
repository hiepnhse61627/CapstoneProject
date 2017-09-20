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
public class DocTypeEntityJpaController implements Serializable {

    public DocTypeEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DocTypeEntity docTypeEntity) throws PreexistingEntityException, Exception {
        if (docTypeEntity.getDocumentList() == null) {
            docTypeEntity.setDocumentList(new ArrayList<DocumentEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<DocumentEntity> attachedDocumentList = new ArrayList<DocumentEntity>();
            for (DocumentEntity documentListDocumentEntityToAttach : docTypeEntity.getDocumentList()) {
                documentListDocumentEntityToAttach = em.getReference(documentListDocumentEntityToAttach.getClass(), documentListDocumentEntityToAttach.getId());
                attachedDocumentList.add(documentListDocumentEntityToAttach);
            }
            docTypeEntity.setDocumentList(attachedDocumentList);
            em.persist(docTypeEntity);
            for (DocumentEntity documentListDocumentEntity : docTypeEntity.getDocumentList()) {
                DocTypeEntity oldDocTypeIdOfDocumentListDocumentEntity = documentListDocumentEntity.getDocTypeId();
                documentListDocumentEntity.setDocTypeId(docTypeEntity);
                documentListDocumentEntity = em.merge(documentListDocumentEntity);
                if (oldDocTypeIdOfDocumentListDocumentEntity != null) {
                    oldDocTypeIdOfDocumentListDocumentEntity.getDocumentList().remove(documentListDocumentEntity);
                    oldDocTypeIdOfDocumentListDocumentEntity = em.merge(oldDocTypeIdOfDocumentListDocumentEntity);
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
            List<DocumentEntity> documentListOld = persistentDocTypeEntity.getDocumentList();
            List<DocumentEntity> documentListNew = docTypeEntity.getDocumentList();
            List<String> illegalOrphanMessages = null;
            for (DocumentEntity documentListOldDocumentEntity : documentListOld) {
                if (!documentListNew.contains(documentListOldDocumentEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DocumentEntity " + documentListOldDocumentEntity + " since its docTypeId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<DocumentEntity> attachedDocumentListNew = new ArrayList<DocumentEntity>();
            for (DocumentEntity documentListNewDocumentEntityToAttach : documentListNew) {
                documentListNewDocumentEntityToAttach = em.getReference(documentListNewDocumentEntityToAttach.getClass(), documentListNewDocumentEntityToAttach.getId());
                attachedDocumentListNew.add(documentListNewDocumentEntityToAttach);
            }
            documentListNew = attachedDocumentListNew;
            docTypeEntity.setDocumentList(documentListNew);
            docTypeEntity = em.merge(docTypeEntity);
            for (DocumentEntity documentListNewDocumentEntity : documentListNew) {
                if (!documentListOld.contains(documentListNewDocumentEntity)) {
                    DocTypeEntity oldDocTypeIdOfDocumentListNewDocumentEntity = documentListNewDocumentEntity.getDocTypeId();
                    documentListNewDocumentEntity.setDocTypeId(docTypeEntity);
                    documentListNewDocumentEntity = em.merge(documentListNewDocumentEntity);
                    if (oldDocTypeIdOfDocumentListNewDocumentEntity != null && !oldDocTypeIdOfDocumentListNewDocumentEntity.equals(docTypeEntity)) {
                        oldDocTypeIdOfDocumentListNewDocumentEntity.getDocumentList().remove(documentListNewDocumentEntity);
                        oldDocTypeIdOfDocumentListNewDocumentEntity = em.merge(oldDocTypeIdOfDocumentListNewDocumentEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = docTypeEntity.getId();
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

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
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
            List<DocumentEntity> documentListOrphanCheck = docTypeEntity.getDocumentList();
            for (DocumentEntity documentListOrphanCheckDocumentEntity : documentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This DocTypeEntity (" + docTypeEntity + ") cannot be destroyed since the DocumentEntity " + documentListOrphanCheckDocumentEntity + " in its documentList field has a non-nullable docTypeId field.");
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

    public DocTypeEntity findDocTypeEntity(Integer id) {
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
