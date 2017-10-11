/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.jpa.exceptions.*;
import com.capstone.entities.DocTypeEntity;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.DocumentEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rem
 */
public class DocTypeEntityJpaController implements Serializable {

    public DocTypeEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DocTypeEntity docTypeEntity) {
        if (docTypeEntity.getDocumentEntityList() == null) {
            docTypeEntity.setDocumentEntityList(new ArrayList<DocumentEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<DocumentEntity> attachedDocumentEntityList = new ArrayList<DocumentEntity>();
            for (DocumentEntity documentEntityListDocumentEntityToAttach : docTypeEntity.getDocumentEntityList()) {
                documentEntityListDocumentEntityToAttach = em.getReference(documentEntityListDocumentEntityToAttach.getClass(), documentEntityListDocumentEntityToAttach.getId());
                attachedDocumentEntityList.add(documentEntityListDocumentEntityToAttach);
            }
            docTypeEntity.setDocumentEntityList(attachedDocumentEntityList);
            em.persist(docTypeEntity);
            for (DocumentEntity documentEntityListDocumentEntity : docTypeEntity.getDocumentEntityList()) {
                DocTypeEntity oldDocTypeIdOfDocumentEntityListDocumentEntity = documentEntityListDocumentEntity.getDocTypeId();
                documentEntityListDocumentEntity.setDocTypeId(docTypeEntity);
                documentEntityListDocumentEntity = em.merge(documentEntityListDocumentEntity);
                if (oldDocTypeIdOfDocumentEntityListDocumentEntity != null) {
                    oldDocTypeIdOfDocumentEntityListDocumentEntity.getDocumentEntityList().remove(documentEntityListDocumentEntity);
                    oldDocTypeIdOfDocumentEntityListDocumentEntity = em.merge(oldDocTypeIdOfDocumentEntityListDocumentEntity);
                }
            }
            em.getTransaction().commit();
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
            List<DocumentEntity> documentEntityListOld = persistentDocTypeEntity.getDocumentEntityList();
            List<DocumentEntity> documentEntityListNew = docTypeEntity.getDocumentEntityList();
            List<String> illegalOrphanMessages = null;
            for (DocumentEntity documentEntityListOldDocumentEntity : documentEntityListOld) {
                if (!documentEntityListNew.contains(documentEntityListOldDocumentEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DocumentEntity " + documentEntityListOldDocumentEntity + " since its docTypeId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<DocumentEntity> attachedDocumentEntityListNew = new ArrayList<DocumentEntity>();
            for (DocumentEntity documentEntityListNewDocumentEntityToAttach : documentEntityListNew) {
                documentEntityListNewDocumentEntityToAttach = em.getReference(documentEntityListNewDocumentEntityToAttach.getClass(), documentEntityListNewDocumentEntityToAttach.getId());
                attachedDocumentEntityListNew.add(documentEntityListNewDocumentEntityToAttach);
            }
            documentEntityListNew = attachedDocumentEntityListNew;
            docTypeEntity.setDocumentEntityList(documentEntityListNew);
            docTypeEntity = em.merge(docTypeEntity);
            for (DocumentEntity documentEntityListNewDocumentEntity : documentEntityListNew) {
                if (!documentEntityListOld.contains(documentEntityListNewDocumentEntity)) {
                    DocTypeEntity oldDocTypeIdOfDocumentEntityListNewDocumentEntity = documentEntityListNewDocumentEntity.getDocTypeId();
                    documentEntityListNewDocumentEntity.setDocTypeId(docTypeEntity);
                    documentEntityListNewDocumentEntity = em.merge(documentEntityListNewDocumentEntity);
                    if (oldDocTypeIdOfDocumentEntityListNewDocumentEntity != null && !oldDocTypeIdOfDocumentEntityListNewDocumentEntity.equals(docTypeEntity)) {
                        oldDocTypeIdOfDocumentEntityListNewDocumentEntity.getDocumentEntityList().remove(documentEntityListNewDocumentEntity);
                        oldDocTypeIdOfDocumentEntityListNewDocumentEntity = em.merge(oldDocTypeIdOfDocumentEntityListNewDocumentEntity);
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
            List<DocumentEntity> documentEntityListOrphanCheck = docTypeEntity.getDocumentEntityList();
            for (DocumentEntity documentEntityListOrphanCheckDocumentEntity : documentEntityListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This DocTypeEntity (" + docTypeEntity + ") cannot be destroyed since the DocumentEntity " + documentEntityListOrphanCheckDocumentEntity + " in its documentEntityList field has a non-nullable docTypeId field.");
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
