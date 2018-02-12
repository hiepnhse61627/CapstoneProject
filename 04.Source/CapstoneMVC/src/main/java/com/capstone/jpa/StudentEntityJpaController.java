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

import com.capstone.entities.*;

import java.util.ArrayList;
import java.util.List;

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
        if (studentEntity.getOldRollNumberEntityList() == null) {
            studentEntity.setOldRollNumberEntityList(new ArrayList<OldRollNumberEntity>());
        }
        if (studentEntity.getCourseStudentEntityList() == null) {
            studentEntity.setCourseStudentEntityList(new ArrayList<CourseStudentEntity>());
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
            List<OldRollNumberEntity> attachedOldRollNumberEntityList = new ArrayList<OldRollNumberEntity>();
            for (OldRollNumberEntity oldRollNumberEntityListOldRollNumberEntityToAttach : studentEntity.getOldRollNumberEntityList()) {
                oldRollNumberEntityListOldRollNumberEntityToAttach = em.getReference(oldRollNumberEntityListOldRollNumberEntityToAttach.getClass(), oldRollNumberEntityListOldRollNumberEntityToAttach.getId());
                attachedOldRollNumberEntityList.add(oldRollNumberEntityListOldRollNumberEntityToAttach);
            }
            studentEntity.setOldRollNumberEntityList(attachedOldRollNumberEntityList);
            List<CourseStudentEntity> attachedCourseStudentEntityList = new ArrayList<CourseStudentEntity>();
            for (CourseStudentEntity courseStudentEntityListCourseStudentEntityToAttach : studentEntity.getCourseStudentEntityList()) {
                courseStudentEntityListCourseStudentEntityToAttach = em.getReference(courseStudentEntityListCourseStudentEntityToAttach.getClass(), courseStudentEntityListCourseStudentEntityToAttach.getId());
                attachedCourseStudentEntityList.add(courseStudentEntityListCourseStudentEntityToAttach);
            }
            studentEntity.setCourseStudentEntityList(attachedCourseStudentEntityList);
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
            for (OldRollNumberEntity oldRollNumberEntityListOldRollNumberEntity : studentEntity.getOldRollNumberEntityList()) {
                StudentEntity oldStudentIdOfOldRollNumberEntityListOldRollNumberEntity = oldRollNumberEntityListOldRollNumberEntity.getStudentId();
                oldRollNumberEntityListOldRollNumberEntity.setStudentId(studentEntity);
                oldRollNumberEntityListOldRollNumberEntity = em.merge(oldRollNumberEntityListOldRollNumberEntity);
                if (oldStudentIdOfOldRollNumberEntityListOldRollNumberEntity != null) {
                    oldStudentIdOfOldRollNumberEntityListOldRollNumberEntity.getOldRollNumberEntityList().remove(oldRollNumberEntityListOldRollNumberEntity);
                    oldStudentIdOfOldRollNumberEntityListOldRollNumberEntity = em.merge(oldStudentIdOfOldRollNumberEntityListOldRollNumberEntity);
                }
            }
            for (CourseStudentEntity courseStudentEntityListCourseStudentEntity : studentEntity.getCourseStudentEntityList()) {
                StudentEntity oldStudentIdOfCourseStudentEntityListCourseStudentEntity = courseStudentEntityListCourseStudentEntity.getStudentId();
                courseStudentEntityListCourseStudentEntity.setStudentId(studentEntity);
                courseStudentEntityListCourseStudentEntity = em.merge(courseStudentEntityListCourseStudentEntity);
                if (oldStudentIdOfCourseStudentEntityListCourseStudentEntity != null) {
                    oldStudentIdOfCourseStudentEntityListCourseStudentEntity.getCourseStudentEntityList().remove(courseStudentEntityListCourseStudentEntity);
                    oldStudentIdOfCourseStudentEntityListCourseStudentEntity = em.merge(oldStudentIdOfCourseStudentEntityListCourseStudentEntity);
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

    public void edit(StudentEntity studentEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            StudentEntity persistentStudentEntity = em.find(StudentEntity.class, studentEntity.getId());
            ProgramEntity programIdOld = persistentStudentEntity.getProgramId();
            ProgramEntity programIdNew = studentEntity.getProgramId();
            List<DocumentStudentEntity> documentStudentEntityListOld = persistentStudentEntity.getDocumentStudentEntityList();
            List<DocumentStudentEntity> documentStudentEntityListNew = studentEntity.getDocumentStudentEntityList();
            List<OldRollNumberEntity> oldRollNumberEntityListOld = persistentStudentEntity.getOldRollNumberEntityList();
            List<OldRollNumberEntity> oldRollNumberEntityListNew = studentEntity.getOldRollNumberEntityList();
            List<CourseStudentEntity> courseStudentEntityListOld = persistentStudentEntity.getCourseStudentEntityList();
            List<CourseStudentEntity> courseStudentEntityListNew = studentEntity.getCourseStudentEntityList();
            List<MarksEntity> marksEntityListOld = persistentStudentEntity.getMarksEntityList();
            List<MarksEntity> marksEntityListNew = studentEntity.getMarksEntityList();
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
            List<OldRollNumberEntity> attachedOldRollNumberEntityListNew = new ArrayList<OldRollNumberEntity>();
            for (OldRollNumberEntity oldRollNumberEntityListNewOldRollNumberEntityToAttach : oldRollNumberEntityListNew) {
                oldRollNumberEntityListNewOldRollNumberEntityToAttach = em.getReference(oldRollNumberEntityListNewOldRollNumberEntityToAttach.getClass(), oldRollNumberEntityListNewOldRollNumberEntityToAttach.getId());
                attachedOldRollNumberEntityListNew.add(oldRollNumberEntityListNewOldRollNumberEntityToAttach);
            }
            oldRollNumberEntityListNew = attachedOldRollNumberEntityListNew;
            studentEntity.setOldRollNumberEntityList(oldRollNumberEntityListNew);
            List<CourseStudentEntity> attachedCourseStudentEntityListNew = new ArrayList<CourseStudentEntity>();
            for (CourseStudentEntity courseStudentEntityListNewCourseStudentEntityToAttach : courseStudentEntityListNew) {
                courseStudentEntityListNewCourseStudentEntityToAttach = em.getReference(courseStudentEntityListNewCourseStudentEntityToAttach.getClass(), courseStudentEntityListNewCourseStudentEntityToAttach.getId());
                attachedCourseStudentEntityListNew.add(courseStudentEntityListNewCourseStudentEntityToAttach);
            }
            courseStudentEntityListNew = attachedCourseStudentEntityListNew;
            studentEntity.setCourseStudentEntityList(courseStudentEntityListNew);
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
            for (DocumentStudentEntity documentStudentEntityListOldDocumentStudentEntity : documentStudentEntityListOld) {
                if (!documentStudentEntityListNew.contains(documentStudentEntityListOldDocumentStudentEntity)) {
                    documentStudentEntityListOldDocumentStudentEntity.setStudentId(null);
                    documentStudentEntityListOldDocumentStudentEntity = em.merge(documentStudentEntityListOldDocumentStudentEntity);
                }
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
            for (OldRollNumberEntity oldRollNumberEntityListOldOldRollNumberEntity : oldRollNumberEntityListOld) {
                if (!oldRollNumberEntityListNew.contains(oldRollNumberEntityListOldOldRollNumberEntity)) {
                    oldRollNumberEntityListOldOldRollNumberEntity.setStudentId(null);
                    oldRollNumberEntityListOldOldRollNumberEntity = em.merge(oldRollNumberEntityListOldOldRollNumberEntity);
                }
            }
            for (OldRollNumberEntity oldRollNumberEntityListNewOldRollNumberEntity : oldRollNumberEntityListNew) {
                if (!oldRollNumberEntityListOld.contains(oldRollNumberEntityListNewOldRollNumberEntity)) {
                    StudentEntity oldStudentIdOfOldRollNumberEntityListNewOldRollNumberEntity = oldRollNumberEntityListNewOldRollNumberEntity.getStudentId();
                    oldRollNumberEntityListNewOldRollNumberEntity.setStudentId(studentEntity);
                    oldRollNumberEntityListNewOldRollNumberEntity = em.merge(oldRollNumberEntityListNewOldRollNumberEntity);
                    if (oldStudentIdOfOldRollNumberEntityListNewOldRollNumberEntity != null && !oldStudentIdOfOldRollNumberEntityListNewOldRollNumberEntity.equals(studentEntity)) {
                        oldStudentIdOfOldRollNumberEntityListNewOldRollNumberEntity.getOldRollNumberEntityList().remove(oldRollNumberEntityListNewOldRollNumberEntity);
                        oldStudentIdOfOldRollNumberEntityListNewOldRollNumberEntity = em.merge(oldStudentIdOfOldRollNumberEntityListNewOldRollNumberEntity);
                    }
                }
            }
            for (CourseStudentEntity courseStudentEntityListOldCourseStudentEntity : courseStudentEntityListOld) {
                if (!courseStudentEntityListNew.contains(courseStudentEntityListOldCourseStudentEntity)) {
                    courseStudentEntityListOldCourseStudentEntity.setStudentId(null);
                    courseStudentEntityListOldCourseStudentEntity = em.merge(courseStudentEntityListOldCourseStudentEntity);
                }
            }
            for (CourseStudentEntity courseStudentEntityListNewCourseStudentEntity : courseStudentEntityListNew) {
                if (!courseStudentEntityListOld.contains(courseStudentEntityListNewCourseStudentEntity)) {
                    StudentEntity oldStudentIdOfCourseStudentEntityListNewCourseStudentEntity = courseStudentEntityListNewCourseStudentEntity.getStudentId();
                    courseStudentEntityListNewCourseStudentEntity.setStudentId(studentEntity);
                    courseStudentEntityListNewCourseStudentEntity = em.merge(courseStudentEntityListNewCourseStudentEntity);
                    if (oldStudentIdOfCourseStudentEntityListNewCourseStudentEntity != null && !oldStudentIdOfCourseStudentEntityListNewCourseStudentEntity.equals(studentEntity)) {
                        oldStudentIdOfCourseStudentEntityListNewCourseStudentEntity.getCourseStudentEntityList().remove(courseStudentEntityListNewCourseStudentEntity);
                        oldStudentIdOfCourseStudentEntityListNewCourseStudentEntity = em.merge(oldStudentIdOfCourseStudentEntityListNewCourseStudentEntity);
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

    public void destroy(Integer id) throws NonexistentEntityException {
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
            ProgramEntity programId = studentEntity.getProgramId();
            if (programId != null) {
                programId.getStudentEntityList().remove(studentEntity);
                programId = em.merge(programId);
            }
            List<DocumentStudentEntity> documentStudentEntityList = studentEntity.getDocumentStudentEntityList();
            for (DocumentStudentEntity documentStudentEntityListDocumentStudentEntity : documentStudentEntityList) {
                documentStudentEntityListDocumentStudentEntity.setStudentId(null);
                documentStudentEntityListDocumentStudentEntity = em.merge(documentStudentEntityListDocumentStudentEntity);
            }
            List<OldRollNumberEntity> oldRollNumberEntityList = studentEntity.getOldRollNumberEntityList();
            for (OldRollNumberEntity oldRollNumberEntityListOldRollNumberEntity : oldRollNumberEntityList) {
                oldRollNumberEntityListOldRollNumberEntity.setStudentId(null);
                oldRollNumberEntityListOldRollNumberEntity = em.merge(oldRollNumberEntityListOldRollNumberEntity);
            }
            List<CourseStudentEntity> courseStudentEntityList = studentEntity.getCourseStudentEntityList();
            for (CourseStudentEntity courseStudentEntityListCourseStudentEntity : courseStudentEntityList) {
                courseStudentEntityListCourseStudentEntity.setStudentId(null);
                courseStudentEntityListCourseStudentEntity = em.merge(courseStudentEntityListCourseStudentEntity);
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
