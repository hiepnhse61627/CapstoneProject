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
import com.capstone.entities.DocumentEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.entities.CurriculumEntity;
import com.capstone.entities.DocumentStudentEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

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
            DocumentEntity documentByDocumentId = documentStudentEntity.getDocumentByDocumentId();
            if (documentByDocumentId != null) {
                documentByDocumentId = em.getReference(documentByDocumentId.getClass(), documentByDocumentId.getId());
                documentStudentEntity.setDocumentByDocumentId(documentByDocumentId);
            }
            StudentEntity studentByStudentId = documentStudentEntity.getStudentByStudentId();
            if (studentByStudentId != null) {
                studentByStudentId = em.getReference(studentByStudentId.getClass(), studentByStudentId.getId());
                documentStudentEntity.setStudentByStudentId(studentByStudentId);
            }
            CurriculumEntity curriculumByCurriculumId = documentStudentEntity.getCurriculumByCurriculumId();
            if (curriculumByCurriculumId != null) {
                curriculumByCurriculumId = em.getReference(curriculumByCurriculumId.getClass(), curriculumByCurriculumId.getId());
                documentStudentEntity.setCurriculumByCurriculumId(curriculumByCurriculumId);
            }
            em.persist(documentStudentEntity);
            if (documentByDocumentId != null) {
                documentByDocumentId.getDocumentStudentsById().add(documentStudentEntity);
                documentByDocumentId = em.merge(documentByDocumentId);
            }
            if (studentByStudentId != null) {
                studentByStudentId.getDocumentStudentsById().add(documentStudentEntity);
                studentByStudentId = em.merge(studentByStudentId);
            }
            if (curriculumByCurriculumId != null) {
                curriculumByCurriculumId.getDocumentStudentsById().add(documentStudentEntity);
                curriculumByCurriculumId = em.merge(curriculumByCurriculumId);
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
            DocumentEntity documentByDocumentIdOld = persistentDocumentStudentEntity.getDocumentByDocumentId();
            DocumentEntity documentByDocumentIdNew = documentStudentEntity.getDocumentByDocumentId();
            StudentEntity studentByStudentIdOld = persistentDocumentStudentEntity.getStudentByStudentId();
            StudentEntity studentByStudentIdNew = documentStudentEntity.getStudentByStudentId();
            CurriculumEntity curriculumByCurriculumIdOld = persistentDocumentStudentEntity.getCurriculumByCurriculumId();
            CurriculumEntity curriculumByCurriculumIdNew = documentStudentEntity.getCurriculumByCurriculumId();
            if (documentByDocumentIdNew != null) {
                documentByDocumentIdNew = em.getReference(documentByDocumentIdNew.getClass(), documentByDocumentIdNew.getId());
                documentStudentEntity.setDocumentByDocumentId(documentByDocumentIdNew);
            }
            if (studentByStudentIdNew != null) {
                studentByStudentIdNew = em.getReference(studentByStudentIdNew.getClass(), studentByStudentIdNew.getId());
                documentStudentEntity.setStudentByStudentId(studentByStudentIdNew);
            }
            if (curriculumByCurriculumIdNew != null) {
                curriculumByCurriculumIdNew = em.getReference(curriculumByCurriculumIdNew.getClass(), curriculumByCurriculumIdNew.getId());
                documentStudentEntity.setCurriculumByCurriculumId(curriculumByCurriculumIdNew);
            }
            documentStudentEntity = em.merge(documentStudentEntity);
            if (documentByDocumentIdOld != null && !documentByDocumentIdOld.equals(documentByDocumentIdNew)) {
                documentByDocumentIdOld.getDocumentStudentsById().remove(documentStudentEntity);
                documentByDocumentIdOld = em.merge(documentByDocumentIdOld);
            }
            if (documentByDocumentIdNew != null && !documentByDocumentIdNew.equals(documentByDocumentIdOld)) {
                documentByDocumentIdNew.getDocumentStudentsById().add(documentStudentEntity);
                documentByDocumentIdNew = em.merge(documentByDocumentIdNew);
            }
            if (studentByStudentIdOld != null && !studentByStudentIdOld.equals(studentByStudentIdNew)) {
                studentByStudentIdOld.getDocumentStudentsById().remove(documentStudentEntity);
                studentByStudentIdOld = em.merge(studentByStudentIdOld);
            }
            if (studentByStudentIdNew != null && !studentByStudentIdNew.equals(studentByStudentIdOld)) {
                studentByStudentIdNew.getDocumentStudentsById().add(documentStudentEntity);
                studentByStudentIdNew = em.merge(studentByStudentIdNew);
            }
            if (curriculumByCurriculumIdOld != null && !curriculumByCurriculumIdOld.equals(curriculumByCurriculumIdNew)) {
                curriculumByCurriculumIdOld.getDocumentStudentsById().remove(documentStudentEntity);
                curriculumByCurriculumIdOld = em.merge(curriculumByCurriculumIdOld);
            }
            if (curriculumByCurriculumIdNew != null && !curriculumByCurriculumIdNew.equals(curriculumByCurriculumIdOld)) {
                curriculumByCurriculumIdNew.getDocumentStudentsById().add(documentStudentEntity);
                curriculumByCurriculumIdNew = em.merge(curriculumByCurriculumIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = documentStudentEntity.getId();
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

    public void destroy(int id) throws NonexistentEntityException {
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
            DocumentEntity documentByDocumentId = documentStudentEntity.getDocumentByDocumentId();
            if (documentByDocumentId != null) {
                documentByDocumentId.getDocumentStudentsById().remove(documentStudentEntity);
                documentByDocumentId = em.merge(documentByDocumentId);
            }
            StudentEntity studentByStudentId = documentStudentEntity.getStudentByStudentId();
            if (studentByStudentId != null) {
                studentByStudentId.getDocumentStudentsById().remove(documentStudentEntity);
                studentByStudentId = em.merge(studentByStudentId);
            }
            CurriculumEntity curriculumByCurriculumId = documentStudentEntity.getCurriculumByCurriculumId();
            if (curriculumByCurriculumId != null) {
                curriculumByCurriculumId.getDocumentStudentsById().remove(documentStudentEntity);
                curriculumByCurriculumId = em.merge(curriculumByCurriculumId);
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

    public DocumentStudentEntity findDocumentStudentEntity(int id) {
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
