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
import java.util.List;
import com.capstone.entities.MarksEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.jpa.exceptions.IllegalOrphanException;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

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
        if (studentEntity.getDocumentStudentList() == null) {
            studentEntity.setDocumentStudentList(new ArrayList<DocumentStudentEntity>());
        }
        if (studentEntity.getMarksList() == null) {
            studentEntity.setMarksList(new ArrayList<MarksEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<DocumentStudentEntity> attachedDocumentStudentList = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentListDocumentStudentEntityToAttach : studentEntity.getDocumentStudentList()) {
                documentStudentListDocumentStudentEntityToAttach = em.getReference(documentStudentListDocumentStudentEntityToAttach.getClass(), documentStudentListDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentList.add(documentStudentListDocumentStudentEntityToAttach);
            }
            studentEntity.setDocumentStudentList(attachedDocumentStudentList);
            List<MarksEntity> attachedMarksList = new ArrayList<MarksEntity>();
            for (MarksEntity marksListMarksEntityToAttach : studentEntity.getMarksList()) {
                marksListMarksEntityToAttach = em.getReference(marksListMarksEntityToAttach.getClass(), marksListMarksEntityToAttach.getId());
                attachedMarksList.add(marksListMarksEntityToAttach);
            }
            studentEntity.setMarksList(attachedMarksList);
            em.persist(studentEntity);
            for (DocumentStudentEntity documentStudentListDocumentStudentEntity : studentEntity.getDocumentStudentList()) {
                StudentEntity oldStudentIdOfDocumentStudentListDocumentStudentEntity = documentStudentListDocumentStudentEntity.getStudentId();
                documentStudentListDocumentStudentEntity.setStudentId(studentEntity);
                documentStudentListDocumentStudentEntity = em.merge(documentStudentListDocumentStudentEntity);
                if (oldStudentIdOfDocumentStudentListDocumentStudentEntity != null) {
                    oldStudentIdOfDocumentStudentListDocumentStudentEntity.getDocumentStudentList().remove(documentStudentListDocumentStudentEntity);
                    oldStudentIdOfDocumentStudentListDocumentStudentEntity = em.merge(oldStudentIdOfDocumentStudentListDocumentStudentEntity);
                }
            }
            for (MarksEntity marksListMarksEntity : studentEntity.getMarksList()) {
                StudentEntity oldStudentIdOfMarksListMarksEntity = marksListMarksEntity.getStudentId();
                marksListMarksEntity.setStudentId(studentEntity);
                marksListMarksEntity = em.merge(marksListMarksEntity);
                if (oldStudentIdOfMarksListMarksEntity != null) {
                    oldStudentIdOfMarksListMarksEntity.getMarksList().remove(marksListMarksEntity);
                    oldStudentIdOfMarksListMarksEntity = em.merge(oldStudentIdOfMarksListMarksEntity);
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
            List<DocumentStudentEntity> documentStudentListOld = persistentStudentEntity.getDocumentStudentList();
            List<DocumentStudentEntity> documentStudentListNew = studentEntity.getDocumentStudentList();
            List<MarksEntity> marksListOld = persistentStudentEntity.getMarksList();
            List<MarksEntity> marksListNew = studentEntity.getMarksList();
            List<String> illegalOrphanMessages = null;
            for (DocumentStudentEntity documentStudentListOldDocumentStudentEntity : documentStudentListOld) {
                if (!documentStudentListNew.contains(documentStudentListOldDocumentStudentEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DocumentStudentEntity " + documentStudentListOldDocumentStudentEntity + " since its studentId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<DocumentStudentEntity> attachedDocumentStudentListNew = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentListNewDocumentStudentEntityToAttach : documentStudentListNew) {
                documentStudentListNewDocumentStudentEntityToAttach = em.getReference(documentStudentListNewDocumentStudentEntityToAttach.getClass(), documentStudentListNewDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentListNew.add(documentStudentListNewDocumentStudentEntityToAttach);
            }
            documentStudentListNew = attachedDocumentStudentListNew;
            studentEntity.setDocumentStudentList(documentStudentListNew);
            List<MarksEntity> attachedMarksListNew = new ArrayList<MarksEntity>();
            for (MarksEntity marksListNewMarksEntityToAttach : marksListNew) {
                marksListNewMarksEntityToAttach = em.getReference(marksListNewMarksEntityToAttach.getClass(), marksListNewMarksEntityToAttach.getId());
                attachedMarksListNew.add(marksListNewMarksEntityToAttach);
            }
            marksListNew = attachedMarksListNew;
            studentEntity.setMarksList(marksListNew);
            studentEntity = em.merge(studentEntity);
            for (DocumentStudentEntity documentStudentListNewDocumentStudentEntity : documentStudentListNew) {
                if (!documentStudentListOld.contains(documentStudentListNewDocumentStudentEntity)) {
                    StudentEntity oldStudentIdOfDocumentStudentListNewDocumentStudentEntity = documentStudentListNewDocumentStudentEntity.getStudentId();
                    documentStudentListNewDocumentStudentEntity.setStudentId(studentEntity);
                    documentStudentListNewDocumentStudentEntity = em.merge(documentStudentListNewDocumentStudentEntity);
                    if (oldStudentIdOfDocumentStudentListNewDocumentStudentEntity != null && !oldStudentIdOfDocumentStudentListNewDocumentStudentEntity.equals(studentEntity)) {
                        oldStudentIdOfDocumentStudentListNewDocumentStudentEntity.getDocumentStudentList().remove(documentStudentListNewDocumentStudentEntity);
                        oldStudentIdOfDocumentStudentListNewDocumentStudentEntity = em.merge(oldStudentIdOfDocumentStudentListNewDocumentStudentEntity);
                    }
                }
            }
            for (MarksEntity marksListOldMarksEntity : marksListOld) {
                if (!marksListNew.contains(marksListOldMarksEntity)) {
                    marksListOldMarksEntity.setStudentId(null);
                    marksListOldMarksEntity = em.merge(marksListOldMarksEntity);
                }
            }
            for (MarksEntity marksListNewMarksEntity : marksListNew) {
                if (!marksListOld.contains(marksListNewMarksEntity)) {
                    StudentEntity oldStudentIdOfMarksListNewMarksEntity = marksListNewMarksEntity.getStudentId();
                    marksListNewMarksEntity.setStudentId(studentEntity);
                    marksListNewMarksEntity = em.merge(marksListNewMarksEntity);
                    if (oldStudentIdOfMarksListNewMarksEntity != null && !oldStudentIdOfMarksListNewMarksEntity.equals(studentEntity)) {
                        oldStudentIdOfMarksListNewMarksEntity.getMarksList().remove(marksListNewMarksEntity);
                        oldStudentIdOfMarksListNewMarksEntity = em.merge(oldStudentIdOfMarksListNewMarksEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = studentEntity.getId();
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

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
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
            List<DocumentStudentEntity> documentStudentListOrphanCheck = studentEntity.getDocumentStudentList();
            for (DocumentStudentEntity documentStudentListOrphanCheckDocumentStudentEntity : documentStudentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This StudentEntity (" + studentEntity + ") cannot be destroyed since the DocumentStudentEntity " + documentStudentListOrphanCheckDocumentStudentEntity + " in its documentStudentList field has a non-nullable studentId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<MarksEntity> marksList = studentEntity.getMarksList();
            for (MarksEntity marksListMarksEntity : marksList) {
                marksListMarksEntity.setStudentId(null);
                marksListMarksEntity = em.merge(marksListMarksEntity);
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

    public StudentEntity findStudentEntity(Integer id) {
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
