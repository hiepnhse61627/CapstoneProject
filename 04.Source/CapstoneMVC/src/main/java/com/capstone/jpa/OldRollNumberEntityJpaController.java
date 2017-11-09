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
import com.capstone.entities.StudentEntity;
import com.capstone.entities.DocumentStudentEntity;
import com.capstone.entities.OldRollNumberEntity;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author hiepnhse61627
 */
public class OldRollNumberEntityJpaController implements Serializable {

    public OldRollNumberEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(OldRollNumberEntity oldRollNumberEntity) {
        if (oldRollNumberEntity.getDocumentStudentEntityList() == null) {
            oldRollNumberEntity.setDocumentStudentEntityList(new ArrayList<DocumentStudentEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProgramEntity programId = oldRollNumberEntity.getProgramId();
            if (programId != null) {
                programId = em.getReference(programId.getClass(), programId.getId());
                oldRollNumberEntity.setProgramId(programId);
            }
            StudentEntity studentId = oldRollNumberEntity.getStudentId();
            if (studentId != null) {
                studentId = em.getReference(studentId.getClass(), studentId.getId());
                oldRollNumberEntity.setStudentId(studentId);
            }
            List<DocumentStudentEntity> attachedDocumentStudentEntityList = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentEntityListDocumentStudentEntityToAttach : oldRollNumberEntity.getDocumentStudentEntityList()) {
                documentStudentEntityListDocumentStudentEntityToAttach = em.getReference(documentStudentEntityListDocumentStudentEntityToAttach.getClass(), documentStudentEntityListDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentEntityList.add(documentStudentEntityListDocumentStudentEntityToAttach);
            }
            oldRollNumberEntity.setDocumentStudentEntityList(attachedDocumentStudentEntityList);
            em.persist(oldRollNumberEntity);
            if (programId != null) {
                programId.getOldRollNumberEntityList().add(oldRollNumberEntity);
                programId = em.merge(programId);
            }
            if (studentId != null) {
                studentId.getOldRollNumberEntityList().add(oldRollNumberEntity);
                studentId = em.merge(studentId);
            }
            for (DocumentStudentEntity documentStudentEntityListDocumentStudentEntity : oldRollNumberEntity.getDocumentStudentEntityList()) {
                OldRollNumberEntity oldOldStudentIdOfDocumentStudentEntityListDocumentStudentEntity = documentStudentEntityListDocumentStudentEntity.getOldStudentId();
                documentStudentEntityListDocumentStudentEntity.setOldStudentId(oldRollNumberEntity);
                documentStudentEntityListDocumentStudentEntity = em.merge(documentStudentEntityListDocumentStudentEntity);
                if (oldOldStudentIdOfDocumentStudentEntityListDocumentStudentEntity != null) {
                    oldOldStudentIdOfDocumentStudentEntityListDocumentStudentEntity.getDocumentStudentEntityList().remove(documentStudentEntityListDocumentStudentEntity);
                    oldOldStudentIdOfDocumentStudentEntityListDocumentStudentEntity = em.merge(oldOldStudentIdOfDocumentStudentEntityListDocumentStudentEntity);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(OldRollNumberEntity oldRollNumberEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            OldRollNumberEntity persistentOldRollNumberEntity = em.find(OldRollNumberEntity.class, oldRollNumberEntity.getId());
            ProgramEntity programIdOld = persistentOldRollNumberEntity.getProgramId();
            ProgramEntity programIdNew = oldRollNumberEntity.getProgramId();
            StudentEntity studentIdOld = persistentOldRollNumberEntity.getStudentId();
            StudentEntity studentIdNew = oldRollNumberEntity.getStudentId();
            List<DocumentStudentEntity> documentStudentEntityListOld = persistentOldRollNumberEntity.getDocumentStudentEntityList();
            List<DocumentStudentEntity> documentStudentEntityListNew = oldRollNumberEntity.getDocumentStudentEntityList();
            if (programIdNew != null) {
                programIdNew = em.getReference(programIdNew.getClass(), programIdNew.getId());
                oldRollNumberEntity.setProgramId(programIdNew);
            }
            if (studentIdNew != null) {
                studentIdNew = em.getReference(studentIdNew.getClass(), studentIdNew.getId());
                oldRollNumberEntity.setStudentId(studentIdNew);
            }
            List<DocumentStudentEntity> attachedDocumentStudentEntityListNew = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentEntityListNewDocumentStudentEntityToAttach : documentStudentEntityListNew) {
                documentStudentEntityListNewDocumentStudentEntityToAttach = em.getReference(documentStudentEntityListNewDocumentStudentEntityToAttach.getClass(), documentStudentEntityListNewDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentEntityListNew.add(documentStudentEntityListNewDocumentStudentEntityToAttach);
            }
            documentStudentEntityListNew = attachedDocumentStudentEntityListNew;
            oldRollNumberEntity.setDocumentStudentEntityList(documentStudentEntityListNew);
            oldRollNumberEntity = em.merge(oldRollNumberEntity);
            if (programIdOld != null && !programIdOld.equals(programIdNew)) {
                programIdOld.getOldRollNumberEntityList().remove(oldRollNumberEntity);
                programIdOld = em.merge(programIdOld);
            }
            if (programIdNew != null && !programIdNew.equals(programIdOld)) {
                programIdNew.getOldRollNumberEntityList().add(oldRollNumberEntity);
                programIdNew = em.merge(programIdNew);
            }
            if (studentIdOld != null && !studentIdOld.equals(studentIdNew)) {
                studentIdOld.getOldRollNumberEntityList().remove(oldRollNumberEntity);
                studentIdOld = em.merge(studentIdOld);
            }
            if (studentIdNew != null && !studentIdNew.equals(studentIdOld)) {
                studentIdNew.getOldRollNumberEntityList().add(oldRollNumberEntity);
                studentIdNew = em.merge(studentIdNew);
            }
            for (DocumentStudentEntity documentStudentEntityListOldDocumentStudentEntity : documentStudentEntityListOld) {
                if (!documentStudentEntityListNew.contains(documentStudentEntityListOldDocumentStudentEntity)) {
                    documentStudentEntityListOldDocumentStudentEntity.setOldStudentId(null);
                    documentStudentEntityListOldDocumentStudentEntity = em.merge(documentStudentEntityListOldDocumentStudentEntity);
                }
            }
            for (DocumentStudentEntity documentStudentEntityListNewDocumentStudentEntity : documentStudentEntityListNew) {
                if (!documentStudentEntityListOld.contains(documentStudentEntityListNewDocumentStudentEntity)) {
                    OldRollNumberEntity oldOldStudentIdOfDocumentStudentEntityListNewDocumentStudentEntity = documentStudentEntityListNewDocumentStudentEntity.getOldStudentId();
                    documentStudentEntityListNewDocumentStudentEntity.setOldStudentId(oldRollNumberEntity);
                    documentStudentEntityListNewDocumentStudentEntity = em.merge(documentStudentEntityListNewDocumentStudentEntity);
                    if (oldOldStudentIdOfDocumentStudentEntityListNewDocumentStudentEntity != null && !oldOldStudentIdOfDocumentStudentEntityListNewDocumentStudentEntity.equals(oldRollNumberEntity)) {
                        oldOldStudentIdOfDocumentStudentEntityListNewDocumentStudentEntity.getDocumentStudentEntityList().remove(documentStudentEntityListNewDocumentStudentEntity);
                        oldOldStudentIdOfDocumentStudentEntityListNewDocumentStudentEntity = em.merge(oldOldStudentIdOfDocumentStudentEntityListNewDocumentStudentEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = oldRollNumberEntity.getId();
                if (findOldRollNumberEntity(id) == null) {
                    throw new NonexistentEntityException("The oldRollNumberEntity with id " + id + " no longer exists.");
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
            OldRollNumberEntity oldRollNumberEntity;
            try {
                oldRollNumberEntity = em.getReference(OldRollNumberEntity.class, id);
                oldRollNumberEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The oldRollNumberEntity with id " + id + " no longer exists.", enfe);
            }
            ProgramEntity programId = oldRollNumberEntity.getProgramId();
            if (programId != null) {
                programId.getOldRollNumberEntityList().remove(oldRollNumberEntity);
                programId = em.merge(programId);
            }
            StudentEntity studentId = oldRollNumberEntity.getStudentId();
            if (studentId != null) {
                studentId.getOldRollNumberEntityList().remove(oldRollNumberEntity);
                studentId = em.merge(studentId);
            }
            List<DocumentStudentEntity> documentStudentEntityList = oldRollNumberEntity.getDocumentStudentEntityList();
            for (DocumentStudentEntity documentStudentEntityListDocumentStudentEntity : documentStudentEntityList) {
                documentStudentEntityListDocumentStudentEntity.setOldStudentId(null);
                documentStudentEntityListDocumentStudentEntity = em.merge(documentStudentEntityListDocumentStudentEntity);
            }
            em.remove(oldRollNumberEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<OldRollNumberEntity> findOldRollNumberEntityEntities() {
        return findOldRollNumberEntityEntities(true, -1, -1);
    }

    public List<OldRollNumberEntity> findOldRollNumberEntityEntities(int maxResults, int firstResult) {
        return findOldRollNumberEntityEntities(false, maxResults, firstResult);
    }

    private List<OldRollNumberEntity> findOldRollNumberEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(OldRollNumberEntity.class));
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

    public OldRollNumberEntity findOldRollNumberEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(OldRollNumberEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getOldRollNumberEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<OldRollNumberEntity> rt = cq.from(OldRollNumberEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
