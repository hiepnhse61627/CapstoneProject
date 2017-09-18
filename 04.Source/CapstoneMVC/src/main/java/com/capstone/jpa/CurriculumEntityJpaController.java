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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.capstone.entities.CurriculumEntity;
import com.capstone.entities.DocumentStudentEntity;
import com.capstone.entities.ProgramEntity;
import com.capstone.jpa.exceptions.IllegalOrphanException;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

/**
 *
 * @author hiepnhse61627
 */
public class CurriculumEntityJpaController implements Serializable {

    public CurriculumEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CurriculumEntity curriculumEntity) throws PreexistingEntityException, Exception {
        if (curriculumEntity.getDocumentStudentsById() == null) {
            curriculumEntity.setDocumentStudentsById(new ArrayList<DocumentStudentEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProgramEntity programByProgramId = curriculumEntity.getProgramByProgramId();
            if (programByProgramId != null) {
                programByProgramId = em.getReference(programByProgramId.getClass(), programByProgramId.getId());
                curriculumEntity.setProgramByProgramId(programByProgramId);
            }
            Collection<DocumentStudentEntity> attachedDocumentStudentsById = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentsByIdDocumentStudentEntityToAttach : curriculumEntity.getDocumentStudentsById()) {
                documentStudentsByIdDocumentStudentEntityToAttach = em.getReference(documentStudentsByIdDocumentStudentEntityToAttach.getClass(), documentStudentsByIdDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentsById.add(documentStudentsByIdDocumentStudentEntityToAttach);
            }
            curriculumEntity.setDocumentStudentsById(attachedDocumentStudentsById);
            em.persist(curriculumEntity);
            if (programByProgramId != null) {
                programByProgramId.getCurriculaById().add(curriculumEntity);
                programByProgramId = em.merge(programByProgramId);
            }
            for (DocumentStudentEntity documentStudentsByIdDocumentStudentEntity : curriculumEntity.getDocumentStudentsById()) {
                CurriculumEntity oldCurriculumByCurriculumIdOfDocumentStudentsByIdDocumentStudentEntity = documentStudentsByIdDocumentStudentEntity.getCurriculumByCurriculumId();
                documentStudentsByIdDocumentStudentEntity.setCurriculumByCurriculumId(curriculumEntity);
                documentStudentsByIdDocumentStudentEntity = em.merge(documentStudentsByIdDocumentStudentEntity);
                if (oldCurriculumByCurriculumIdOfDocumentStudentsByIdDocumentStudentEntity != null) {
                    oldCurriculumByCurriculumIdOfDocumentStudentsByIdDocumentStudentEntity.getDocumentStudentsById().remove(documentStudentsByIdDocumentStudentEntity);
                    oldCurriculumByCurriculumIdOfDocumentStudentsByIdDocumentStudentEntity = em.merge(oldCurriculumByCurriculumIdOfDocumentStudentsByIdDocumentStudentEntity);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCurriculumEntity(curriculumEntity.getId()) != null) {
                throw new PreexistingEntityException("CurriculumEntity " + curriculumEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CurriculumEntity curriculumEntity) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CurriculumEntity persistentCurriculumEntity = em.find(CurriculumEntity.class, curriculumEntity.getId());
            ProgramEntity programByProgramIdOld = persistentCurriculumEntity.getProgramByProgramId();
            ProgramEntity programByProgramIdNew = curriculumEntity.getProgramByProgramId();
            Collection<DocumentStudentEntity> documentStudentsByIdOld = persistentCurriculumEntity.getDocumentStudentsById();
            Collection<DocumentStudentEntity> documentStudentsByIdNew = curriculumEntity.getDocumentStudentsById();
            List<String> illegalOrphanMessages = null;
            for (DocumentStudentEntity documentStudentsByIdOldDocumentStudentEntity : documentStudentsByIdOld) {
                if (!documentStudentsByIdNew.contains(documentStudentsByIdOldDocumentStudentEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DocumentStudentEntity " + documentStudentsByIdOldDocumentStudentEntity + " since its curriculumByCurriculumId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (programByProgramIdNew != null) {
                programByProgramIdNew = em.getReference(programByProgramIdNew.getClass(), programByProgramIdNew.getId());
                curriculumEntity.setProgramByProgramId(programByProgramIdNew);
            }
            Collection<DocumentStudentEntity> attachedDocumentStudentsByIdNew = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentsByIdNewDocumentStudentEntityToAttach : documentStudentsByIdNew) {
                documentStudentsByIdNewDocumentStudentEntityToAttach = em.getReference(documentStudentsByIdNewDocumentStudentEntityToAttach.getClass(), documentStudentsByIdNewDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentsByIdNew.add(documentStudentsByIdNewDocumentStudentEntityToAttach);
            }
            documentStudentsByIdNew = attachedDocumentStudentsByIdNew;
            curriculumEntity.setDocumentStudentsById(documentStudentsByIdNew);
            curriculumEntity = em.merge(curriculumEntity);
            if (programByProgramIdOld != null && !programByProgramIdOld.equals(programByProgramIdNew)) {
                programByProgramIdOld.getCurriculaById().remove(curriculumEntity);
                programByProgramIdOld = em.merge(programByProgramIdOld);
            }
            if (programByProgramIdNew != null && !programByProgramIdNew.equals(programByProgramIdOld)) {
                programByProgramIdNew.getCurriculaById().add(curriculumEntity);
                programByProgramIdNew = em.merge(programByProgramIdNew);
            }
            for (DocumentStudentEntity documentStudentsByIdNewDocumentStudentEntity : documentStudentsByIdNew) {
                if (!documentStudentsByIdOld.contains(documentStudentsByIdNewDocumentStudentEntity)) {
                    CurriculumEntity oldCurriculumByCurriculumIdOfDocumentStudentsByIdNewDocumentStudentEntity = documentStudentsByIdNewDocumentStudentEntity.getCurriculumByCurriculumId();
                    documentStudentsByIdNewDocumentStudentEntity.setCurriculumByCurriculumId(curriculumEntity);
                    documentStudentsByIdNewDocumentStudentEntity = em.merge(documentStudentsByIdNewDocumentStudentEntity);
                    if (oldCurriculumByCurriculumIdOfDocumentStudentsByIdNewDocumentStudentEntity != null && !oldCurriculumByCurriculumIdOfDocumentStudentsByIdNewDocumentStudentEntity.equals(curriculumEntity)) {
                        oldCurriculumByCurriculumIdOfDocumentStudentsByIdNewDocumentStudentEntity.getDocumentStudentsById().remove(documentStudentsByIdNewDocumentStudentEntity);
                        oldCurriculumByCurriculumIdOfDocumentStudentsByIdNewDocumentStudentEntity = em.merge(oldCurriculumByCurriculumIdOfDocumentStudentsByIdNewDocumentStudentEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = curriculumEntity.getId();
                if (findCurriculumEntity(id) == null) {
                    throw new NonexistentEntityException("The curriculumEntity with id " + id + " no longer exists.");
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
            CurriculumEntity curriculumEntity;
            try {
                curriculumEntity = em.getReference(CurriculumEntity.class, id);
                curriculumEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The curriculumEntity with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<DocumentStudentEntity> documentStudentsByIdOrphanCheck = curriculumEntity.getDocumentStudentsById();
            for (DocumentStudentEntity documentStudentsByIdOrphanCheckDocumentStudentEntity : documentStudentsByIdOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This CurriculumEntity (" + curriculumEntity + ") cannot be destroyed since the DocumentStudentEntity " + documentStudentsByIdOrphanCheckDocumentStudentEntity + " in its documentStudentsById field has a non-nullable curriculumByCurriculumId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            ProgramEntity programByProgramId = curriculumEntity.getProgramByProgramId();
            if (programByProgramId != null) {
                programByProgramId.getCurriculaById().remove(curriculumEntity);
                programByProgramId = em.merge(programByProgramId);
            }
            em.remove(curriculumEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CurriculumEntity> findCurriculumEntityEntities() {
        return findCurriculumEntityEntities(true, -1, -1);
    }

    public List<CurriculumEntity> findCurriculumEntityEntities(int maxResults, int firstResult) {
        return findCurriculumEntityEntities(false, maxResults, firstResult);
    }

    private List<CurriculumEntity> findCurriculumEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CurriculumEntity.class));
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

    public CurriculumEntity findCurriculumEntity(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CurriculumEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getCurriculumEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CurriculumEntity> rt = cq.from(CurriculumEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
