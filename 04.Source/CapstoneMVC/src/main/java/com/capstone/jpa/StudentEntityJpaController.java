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
import com.capstone.entities.DocumentStudentEntity;
import java.util.ArrayList;
import java.util.Collection;
import com.capstone.entities.MarksEntity;
import com.capstone.entities.StudentEntity;
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
public class StudentEntityJpaController implements Serializable {

    public StudentEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(StudentEntity studentEntity) throws PreexistingEntityException, Exception {
        if (studentEntity.getDocumentStudentsById() == null) {
            studentEntity.setDocumentStudentsById(new ArrayList<DocumentStudentEntity>());
        }
        if (studentEntity.getMarksById() == null) {
            studentEntity.setMarksById(new ArrayList<MarksEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<DocumentStudentEntity> attachedDocumentStudentsById = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentsByIdDocumentStudentEntityToAttach : studentEntity.getDocumentStudentsById()) {
                documentStudentsByIdDocumentStudentEntityToAttach = em.getReference(documentStudentsByIdDocumentStudentEntityToAttach.getClass(), documentStudentsByIdDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentsById.add(documentStudentsByIdDocumentStudentEntityToAttach);
            }
            studentEntity.setDocumentStudentsById(attachedDocumentStudentsById);
            Collection<MarksEntity> attachedMarksById = new ArrayList<MarksEntity>();
            for (MarksEntity marksByIdMarksEntityToAttach : studentEntity.getMarksById()) {
                marksByIdMarksEntityToAttach = em.getReference(marksByIdMarksEntityToAttach.getClass(), marksByIdMarksEntityToAttach.getId());
                attachedMarksById.add(marksByIdMarksEntityToAttach);
            }
            studentEntity.setMarksById(attachedMarksById);
            em.persist(studentEntity);
            for (DocumentStudentEntity documentStudentsByIdDocumentStudentEntity : studentEntity.getDocumentStudentsById()) {
                StudentEntity oldStudentByStudentIdOfDocumentStudentsByIdDocumentStudentEntity = documentStudentsByIdDocumentStudentEntity.getStudentByStudentId();
                documentStudentsByIdDocumentStudentEntity.setStudentByStudentId(studentEntity);
                documentStudentsByIdDocumentStudentEntity = em.merge(documentStudentsByIdDocumentStudentEntity);
                if (oldStudentByStudentIdOfDocumentStudentsByIdDocumentStudentEntity != null) {
                    oldStudentByStudentIdOfDocumentStudentsByIdDocumentStudentEntity.getDocumentStudentsById().remove(documentStudentsByIdDocumentStudentEntity);
                    oldStudentByStudentIdOfDocumentStudentsByIdDocumentStudentEntity = em.merge(oldStudentByStudentIdOfDocumentStudentsByIdDocumentStudentEntity);
                }
            }
            for (MarksEntity marksByIdMarksEntity : studentEntity.getMarksById()) {
                StudentEntity oldStudentByStudentIdOfMarksByIdMarksEntity = marksByIdMarksEntity.getStudentByStudentId();
                marksByIdMarksEntity.setStudentByStudentId(studentEntity);
                marksByIdMarksEntity = em.merge(marksByIdMarksEntity);
                if (oldStudentByStudentIdOfMarksByIdMarksEntity != null) {
                    oldStudentByStudentIdOfMarksByIdMarksEntity.getMarksById().remove(marksByIdMarksEntity);
                    oldStudentByStudentIdOfMarksByIdMarksEntity = em.merge(oldStudentByStudentIdOfMarksByIdMarksEntity);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findStudentEntity(studentEntity.getId()) != null) {
                throw new PreexistingEntityException("StudentEntity " + studentEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(StudentEntity studentEntity) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            StudentEntity persistentStudentEntity = em.find(StudentEntity.class, studentEntity.getId());
            Collection<DocumentStudentEntity> documentStudentsByIdOld = persistentStudentEntity.getDocumentStudentsById();
            Collection<DocumentStudentEntity> documentStudentsByIdNew = studentEntity.getDocumentStudentsById();
            Collection<MarksEntity> marksByIdOld = persistentStudentEntity.getMarksById();
            Collection<MarksEntity> marksByIdNew = studentEntity.getMarksById();
            List<String> illegalOrphanMessages = null;
            for (DocumentStudentEntity documentStudentsByIdOldDocumentStudentEntity : documentStudentsByIdOld) {
                if (!documentStudentsByIdNew.contains(documentStudentsByIdOldDocumentStudentEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DocumentStudentEntity " + documentStudentsByIdOldDocumentStudentEntity + " since its studentByStudentId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<DocumentStudentEntity> attachedDocumentStudentsByIdNew = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentsByIdNewDocumentStudentEntityToAttach : documentStudentsByIdNew) {
                documentStudentsByIdNewDocumentStudentEntityToAttach = em.getReference(documentStudentsByIdNewDocumentStudentEntityToAttach.getClass(), documentStudentsByIdNewDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentsByIdNew.add(documentStudentsByIdNewDocumentStudentEntityToAttach);
            }
            documentStudentsByIdNew = attachedDocumentStudentsByIdNew;
            studentEntity.setDocumentStudentsById(documentStudentsByIdNew);
            Collection<MarksEntity> attachedMarksByIdNew = new ArrayList<MarksEntity>();
            for (MarksEntity marksByIdNewMarksEntityToAttach : marksByIdNew) {
                marksByIdNewMarksEntityToAttach = em.getReference(marksByIdNewMarksEntityToAttach.getClass(), marksByIdNewMarksEntityToAttach.getId());
                attachedMarksByIdNew.add(marksByIdNewMarksEntityToAttach);
            }
            marksByIdNew = attachedMarksByIdNew;
            studentEntity.setMarksById(marksByIdNew);
            studentEntity = em.merge(studentEntity);
            for (DocumentStudentEntity documentStudentsByIdNewDocumentStudentEntity : documentStudentsByIdNew) {
                if (!documentStudentsByIdOld.contains(documentStudentsByIdNewDocumentStudentEntity)) {
                    StudentEntity oldStudentByStudentIdOfDocumentStudentsByIdNewDocumentStudentEntity = documentStudentsByIdNewDocumentStudentEntity.getStudentByStudentId();
                    documentStudentsByIdNewDocumentStudentEntity.setStudentByStudentId(studentEntity);
                    documentStudentsByIdNewDocumentStudentEntity = em.merge(documentStudentsByIdNewDocumentStudentEntity);
                    if (oldStudentByStudentIdOfDocumentStudentsByIdNewDocumentStudentEntity != null && !oldStudentByStudentIdOfDocumentStudentsByIdNewDocumentStudentEntity.equals(studentEntity)) {
                        oldStudentByStudentIdOfDocumentStudentsByIdNewDocumentStudentEntity.getDocumentStudentsById().remove(documentStudentsByIdNewDocumentStudentEntity);
                        oldStudentByStudentIdOfDocumentStudentsByIdNewDocumentStudentEntity = em.merge(oldStudentByStudentIdOfDocumentStudentsByIdNewDocumentStudentEntity);
                    }
                }
            }
            for (MarksEntity marksByIdOldMarksEntity : marksByIdOld) {
                if (!marksByIdNew.contains(marksByIdOldMarksEntity)) {
                    marksByIdOldMarksEntity.setStudentByStudentId(null);
                    marksByIdOldMarksEntity = em.merge(marksByIdOldMarksEntity);
                }
            }
            for (MarksEntity marksByIdNewMarksEntity : marksByIdNew) {
                if (!marksByIdOld.contains(marksByIdNewMarksEntity)) {
                    StudentEntity oldStudentByStudentIdOfMarksByIdNewMarksEntity = marksByIdNewMarksEntity.getStudentByStudentId();
                    marksByIdNewMarksEntity.setStudentByStudentId(studentEntity);
                    marksByIdNewMarksEntity = em.merge(marksByIdNewMarksEntity);
                    if (oldStudentByStudentIdOfMarksByIdNewMarksEntity != null && !oldStudentByStudentIdOfMarksByIdNewMarksEntity.equals(studentEntity)) {
                        oldStudentByStudentIdOfMarksByIdNewMarksEntity.getMarksById().remove(marksByIdNewMarksEntity);
                        oldStudentByStudentIdOfMarksByIdNewMarksEntity = em.merge(oldStudentByStudentIdOfMarksByIdNewMarksEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = studentEntity.getId();
                if (findStudentEntity(id) == null) {
                    throw new NonexistentEntityException("The studentEntity with id " + id + " no longer exists.");
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
            StudentEntity studentEntity;
            try {
                studentEntity = em.getReference(StudentEntity.class, id);
                studentEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The studentEntity with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<DocumentStudentEntity> documentStudentsByIdOrphanCheck = studentEntity.getDocumentStudentsById();
            for (DocumentStudentEntity documentStudentsByIdOrphanCheckDocumentStudentEntity : documentStudentsByIdOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This StudentEntity (" + studentEntity + ") cannot be destroyed since the DocumentStudentEntity " + documentStudentsByIdOrphanCheckDocumentStudentEntity + " in its documentStudentsById field has a non-nullable studentByStudentId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<MarksEntity> marksById = studentEntity.getMarksById();
            for (MarksEntity marksByIdMarksEntity : marksById) {
                marksByIdMarksEntity.setStudentByStudentId(null);
                marksByIdMarksEntity = em.merge(marksByIdMarksEntity);
            }
            em.remove(studentEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<StudentEntity> findStudentEntityEntities() {
        return findStudentEntityEntities(true, -1, -1);
    }

    public List<StudentEntity> findStudentEntityEntities(int maxResults, int firstResult) {
        return findStudentEntityEntities(false, maxResults, firstResult);
    }

    private List<StudentEntity> findStudentEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(StudentEntity.class));
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

    public StudentEntity findStudentEntity(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(StudentEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getStudentEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<StudentEntity> rt = cq.from(StudentEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
