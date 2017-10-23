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
import com.capstone.entities.CurriculumEntity;
import com.capstone.entities.DocumentEntity;
import com.capstone.entities.DocumentStudentEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author hiepnhse61627
 */
public class DocumentStudentEntityJpaController implements Serializable {

    public DocumentStudentEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DocumentStudentEntity documentStudentEntity) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CurriculumEntity curriculumId = documentStudentEntity.getCurriculumId();
            if (curriculumId != null) {
                curriculumId = em.getReference(curriculumId.getClass(), curriculumId.getId());
                documentStudentEntity.setCurriculumId(curriculumId);
            }
            DocumentEntity documentId = documentStudentEntity.getDocumentId();
            if (documentId != null) {
                documentId = em.getReference(documentId.getClass(), documentId.getId());
                documentStudentEntity.setDocumentId(documentId);
            }
            StudentEntity studentId = documentStudentEntity.getStudentId();
            if (studentId != null) {
                studentId = em.getReference(studentId.getClass(), studentId.getId());
                documentStudentEntity.setStudentId(studentId);
            }
            em.persist(documentStudentEntity);
            if (curriculumId != null) {
                curriculumId.getDocumentStudentEntityList().add(documentStudentEntity);
                curriculumId = em.merge(curriculumId);
            }
            if (documentId != null) {
                documentId.getDocumentStudentEntityList().add(documentStudentEntity);
                documentId = em.merge(documentId);
            }
            if (studentId != null) {
                studentId.getDocumentStudentEntityList().add(documentStudentEntity);
                studentId = em.merge(studentId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDocumentStudentEntity(documentStudentEntity.getId()) != null) {
                throw new PreexistingEntityException("DocumentStudentEntity " + documentStudentEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DocumentStudentEntity documentStudentEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DocumentStudentEntity persistentDocumentStudentEntity = em.find(DocumentStudentEntity.class, documentStudentEntity.getId());
            CurriculumEntity curriculumIdOld = persistentDocumentStudentEntity.getCurriculumId();
            CurriculumEntity curriculumIdNew = documentStudentEntity.getCurriculumId();
            DocumentEntity documentIdOld = persistentDocumentStudentEntity.getDocumentId();
            DocumentEntity documentIdNew = documentStudentEntity.getDocumentId();
            StudentEntity studentIdOld = persistentDocumentStudentEntity.getStudentId();
            StudentEntity studentIdNew = documentStudentEntity.getStudentId();
            if (curriculumIdNew != null) {
                curriculumIdNew = em.getReference(curriculumIdNew.getClass(), curriculumIdNew.getId());
                documentStudentEntity.setCurriculumId(curriculumIdNew);
            }
            if (documentIdNew != null) {
                documentIdNew = em.getReference(documentIdNew.getClass(), documentIdNew.getId());
                documentStudentEntity.setDocumentId(documentIdNew);
            }
            if (studentIdNew != null) {
                studentIdNew = em.getReference(studentIdNew.getClass(), studentIdNew.getId());
                documentStudentEntity.setStudentId(studentIdNew);
            }
            documentStudentEntity = em.merge(documentStudentEntity);
            if (curriculumIdOld != null && !curriculumIdOld.equals(curriculumIdNew)) {
                curriculumIdOld.getDocumentStudentEntityList().remove(documentStudentEntity);
                curriculumIdOld = em.merge(curriculumIdOld);
            }
            if (curriculumIdNew != null && !curriculumIdNew.equals(curriculumIdOld)) {
                curriculumIdNew.getDocumentStudentEntityList().add(documentStudentEntity);
                curriculumIdNew = em.merge(curriculumIdNew);
            }
            if (documentIdOld != null && !documentIdOld.equals(documentIdNew)) {
                documentIdOld.getDocumentStudentEntityList().remove(documentStudentEntity);
                documentIdOld = em.merge(documentIdOld);
            }
            if (documentIdNew != null && !documentIdNew.equals(documentIdOld)) {
                documentIdNew.getDocumentStudentEntityList().add(documentStudentEntity);
                documentIdNew = em.merge(documentIdNew);
            }
            if (studentIdOld != null && !studentIdOld.equals(studentIdNew)) {
                studentIdOld.getDocumentStudentEntityList().remove(documentStudentEntity);
                studentIdOld = em.merge(studentIdOld);
            }
            if (studentIdNew != null && !studentIdNew.equals(studentIdOld)) {
                studentIdNew.getDocumentStudentEntityList().add(documentStudentEntity);
                studentIdNew = em.merge(studentIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = documentStudentEntity.getId();
                if (findDocumentStudentEntity(id) == null) {
                    throw new NonexistentEntityException("The documentStudentEntity with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DocumentStudentEntity documentStudentEntity;
            try {
                documentStudentEntity = em.getReference(DocumentStudentEntity.class, id);
                documentStudentEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The documentStudentEntity with id " + id + " no longer exists.", enfe);
            }
            CurriculumEntity curriculumId = documentStudentEntity.getCurriculumId();
            if (curriculumId != null) {
                curriculumId.getDocumentStudentEntityList().remove(documentStudentEntity);
                curriculumId = em.merge(curriculumId);
            }
            DocumentEntity documentId = documentStudentEntity.getDocumentId();
            if (documentId != null) {
                documentId.getDocumentStudentEntityList().remove(documentStudentEntity);
                documentId = em.merge(documentId);
            }
            StudentEntity studentId = documentStudentEntity.getStudentId();
            if (studentId != null) {
                studentId.getDocumentStudentEntityList().remove(documentStudentEntity);
                studentId = em.merge(studentId);
            }
            em.remove(documentStudentEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DocumentStudentEntity> findDocumentStudentEntityEntities() {
        return findDocumentStudentEntityEntities(true, -1, -1);
    }

    public List<DocumentStudentEntity> findDocumentStudentEntityEntities(int maxResults, int firstResult) {
        return findDocumentStudentEntityEntities(false, maxResults, firstResult);
    }

    private List<DocumentStudentEntity> findDocumentStudentEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DocumentStudentEntity.class));
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

    public DocumentStudentEntity findDocumentStudentEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DocumentStudentEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getDocumentStudentEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DocumentStudentEntity> rt = cq.from(DocumentStudentEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
