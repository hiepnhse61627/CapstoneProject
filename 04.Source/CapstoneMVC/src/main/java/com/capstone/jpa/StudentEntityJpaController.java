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
import com.capstone.entities.ProgramEntity;
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
        if (studentEntity.getDocumentStudentEntityList() == null) {
            studentEntity.setDocumentStudentEntityList(new ArrayList<DocumentStudentEntity>());
        }
        if (studentEntity.getMarksEntityList() == null) {
            studentEntity.setMarksEntityList(new ArrayList<MarksEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProgramEntity programId = studentEntity.getProgramId();
            if (programId != null) {
                programId = em.getReference(programId.getClass(), programId.getId());
                studentEntity.setProgramId(programId);
            }
            List<DocumentStudentEntity> attachedDocumentStudentEntityList = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentEntityListDocumentStudentEntityToAttach : studentEntity.getDocumentStudentEntityList()) {
                documentStudentEntityListDocumentStudentEntityToAttach = em.getReference(documentStudentEntityListDocumentStudentEntityToAttach.getClass(), documentStudentEntityListDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentEntityList.add(documentStudentEntityListDocumentStudentEntityToAttach);
            }
            studentEntity.setDocumentStudentEntityList(attachedDocumentStudentEntityList);
            List<MarksEntity> attachedMarksEntityList = new ArrayList<MarksEntity>();
            for (MarksEntity marksEntityListMarksEntityToAttach : studentEntity.getMarksEntityList()) {
                marksEntityListMarksEntityToAttach = em.getReference(marksEntityListMarksEntityToAttach.getClass(), marksEntityListMarksEntityToAttach.getId());
                attachedMarksEntityList.add(marksEntityListMarksEntityToAttach);
            }
            studentEntity.setMarksEntityList(attachedMarksEntityList);
            em.persist(studentEntity);
            if (programId != null) {
                programId.getStudentEntityList().add(studentEntity);
                programId = em.merge(programId);
            }
            for (DocumentStudentEntity documentStudentEntityListDocumentStudentEntity : studentEntity.getDocumentStudentEntityList()) {
                StudentEntity oldStudentIdOfDocumentStudentEntityListDocumentStudentEntity = documentStudentEntityListDocumentStudentEntity.getStudentId();
                documentStudentEntityListDocumentStudentEntity.setStudentId(studentEntity);
                documentStudentEntityListDocumentStudentEntity = em.merge(documentStudentEntityListDocumentStudentEntity);
                if (oldStudentIdOfDocumentStudentEntityListDocumentStudentEntity != null) {
                    oldStudentIdOfDocumentStudentEntityListDocumentStudentEntity.getDocumentStudentEntityList().remove(documentStudentEntityListDocumentStudentEntity);
                    oldStudentIdOfDocumentStudentEntityListDocumentStudentEntity = em.merge(oldStudentIdOfDocumentStudentEntityListDocumentStudentEntity);
                }
            }
            for (MarksEntity marksEntityListMarksEntity : studentEntity.getMarksEntityList()) {
                StudentEntity oldStudentIdOfMarksEntityListMarksEntity = marksEntityListMarksEntity.getStudentId();
                marksEntityListMarksEntity.setStudentId(studentEntity);
                marksEntityListMarksEntity = em.merge(marksEntityListMarksEntity);
                if (oldStudentIdOfMarksEntityListMarksEntity != null) {
                    oldStudentIdOfMarksEntityListMarksEntity.getMarksEntityList().remove(marksEntityListMarksEntity);
                    oldStudentIdOfMarksEntityListMarksEntity = em.merge(oldStudentIdOfMarksEntityListMarksEntity);
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
            ProgramEntity programIdOld = persistentStudentEntity.getProgramId();
            ProgramEntity programIdNew = studentEntity.getProgramId();
            List<DocumentStudentEntity> documentStudentEntityListOld = persistentStudentEntity.getDocumentStudentEntityList();
            List<DocumentStudentEntity> documentStudentEntityListNew = studentEntity.getDocumentStudentEntityList();
            List<MarksEntity> marksEntityListOld = persistentStudentEntity.getMarksEntityList();
            List<MarksEntity> marksEntityListNew = studentEntity.getMarksEntityList();
            List<String> illegalOrphanMessages = null;
            for (DocumentStudentEntity documentStudentEntityListOldDocumentStudentEntity : documentStudentEntityListOld) {
                if (!documentStudentEntityListNew.contains(documentStudentEntityListOldDocumentStudentEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DocumentStudentEntity " + documentStudentEntityListOldDocumentStudentEntity + " since its studentId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (programIdNew != null) {
                programIdNew = em.getReference(programIdNew.getClass(), programIdNew.getId());
                studentEntity.setProgramId(programIdNew);
            }
            List<DocumentStudentEntity> attachedDocumentStudentEntityListNew = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentEntityListNewDocumentStudentEntityToAttach : documentStudentEntityListNew) {
                documentStudentEntityListNewDocumentStudentEntityToAttach = em.getReference(documentStudentEntityListNewDocumentStudentEntityToAttach.getClass(), documentStudentEntityListNewDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentEntityListNew.add(documentStudentEntityListNewDocumentStudentEntityToAttach);
            }
            documentStudentEntityListNew = attachedDocumentStudentEntityListNew;
            studentEntity.setDocumentStudentEntityList(documentStudentEntityListNew);
            List<MarksEntity> attachedMarksEntityListNew = new ArrayList<MarksEntity>();
            for (MarksEntity marksEntityListNewMarksEntityToAttach : marksEntityListNew) {
                marksEntityListNewMarksEntityToAttach = em.getReference(marksEntityListNewMarksEntityToAttach.getClass(), marksEntityListNewMarksEntityToAttach.getId());
                attachedMarksEntityListNew.add(marksEntityListNewMarksEntityToAttach);
            }
            marksEntityListNew = attachedMarksEntityListNew;
            studentEntity.setMarksEntityList(marksEntityListNew);
            studentEntity = em.merge(studentEntity);
            if (programIdOld != null && !programIdOld.equals(programIdNew)) {
                programIdOld.getStudentEntityList().remove(studentEntity);
                programIdOld = em.merge(programIdOld);
            }
            if (programIdNew != null && !programIdNew.equals(programIdOld)) {
                programIdNew.getStudentEntityList().add(studentEntity);
                programIdNew = em.merge(programIdNew);
            }
            for (DocumentStudentEntity documentStudentEntityListNewDocumentStudentEntity : documentStudentEntityListNew) {
                if (!documentStudentEntityListOld.contains(documentStudentEntityListNewDocumentStudentEntity)) {
                    StudentEntity oldStudentIdOfDocumentStudentEntityListNewDocumentStudentEntity = documentStudentEntityListNewDocumentStudentEntity.getStudentId();
                    documentStudentEntityListNewDocumentStudentEntity.setStudentId(studentEntity);
                    documentStudentEntityListNewDocumentStudentEntity = em.merge(documentStudentEntityListNewDocumentStudentEntity);
                    if (oldStudentIdOfDocumentStudentEntityListNewDocumentStudentEntity != null && !oldStudentIdOfDocumentStudentEntityListNewDocumentStudentEntity.equals(studentEntity)) {
                        oldStudentIdOfDocumentStudentEntityListNewDocumentStudentEntity.getDocumentStudentEntityList().remove(documentStudentEntityListNewDocumentStudentEntity);
                        oldStudentIdOfDocumentStudentEntityListNewDocumentStudentEntity = em.merge(oldStudentIdOfDocumentStudentEntityListNewDocumentStudentEntity);
                    }
                }
            }
            for (MarksEntity marksEntityListOldMarksEntity : marksEntityListOld) {
                if (!marksEntityListNew.contains(marksEntityListOldMarksEntity)) {
                    marksEntityListOldMarksEntity.setStudentId(null);
                    marksEntityListOldMarksEntity = em.merge(marksEntityListOldMarksEntity);
                }
            }
            for (MarksEntity marksEntityListNewMarksEntity : marksEntityListNew) {
                if (!marksEntityListOld.contains(marksEntityListNewMarksEntity)) {
                    StudentEntity oldStudentIdOfMarksEntityListNewMarksEntity = marksEntityListNewMarksEntity.getStudentId();
                    marksEntityListNewMarksEntity.setStudentId(studentEntity);
                    marksEntityListNewMarksEntity = em.merge(marksEntityListNewMarksEntity);
                    if (oldStudentIdOfMarksEntityListNewMarksEntity != null && !oldStudentIdOfMarksEntityListNewMarksEntity.equals(studentEntity)) {
                        oldStudentIdOfMarksEntityListNewMarksEntity.getMarksEntityList().remove(marksEntityListNewMarksEntity);
                        oldStudentIdOfMarksEntityListNewMarksEntity = em.merge(oldStudentIdOfMarksEntityListNewMarksEntity);
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
            List<DocumentStudentEntity> documentStudentEntityListOrphanCheck = studentEntity.getDocumentStudentEntityList();
            for (DocumentStudentEntity documentStudentEntityListOrphanCheckDocumentStudentEntity : documentStudentEntityListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This StudentEntity (" + studentEntity + ") cannot be destroyed since the DocumentStudentEntity " + documentStudentEntityListOrphanCheckDocumentStudentEntity + " in its documentStudentEntityList field has a non-nullable studentId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            ProgramEntity programId = studentEntity.getProgramId();
            if (programId != null) {
                programId.getStudentEntityList().remove(studentEntity);
                programId = em.merge(programId);
            }
            List<MarksEntity> marksEntityList = studentEntity.getMarksEntityList();
            for (MarksEntity marksEntityListMarksEntity : marksEntityList) {
                marksEntityListMarksEntity.setStudentId(null);
                marksEntityListMarksEntity = em.merge(marksEntityListMarksEntity);
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
