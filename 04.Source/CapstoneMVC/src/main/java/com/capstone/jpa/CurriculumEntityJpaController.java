/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.entities.*;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
public class CurriculumEntityJpaController implements Serializable {

    public CurriculumEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CurriculumEntity curriculumEntity) throws PreexistingEntityException, Exception {
        if (curriculumEntity.getSubjectCurriculumEntityList() == null) {
            curriculumEntity.setSubjectCurriculumEntityList(new ArrayList<SubjectCurriculumEntity>());
        }
        if (curriculumEntity.getDocumentStudentEntityList() == null) {
            curriculumEntity.setDocumentStudentEntityList(new ArrayList<DocumentStudentEntity>());
        }
        if (curriculumEntity.getSimulateDocumentStudentEntityList() == null) {
            curriculumEntity.setSimulateDocumentStudentEntityList(new ArrayList<SimulateDocumentStudentEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProgramEntity programId = curriculumEntity.getProgramId();
            if (programId != null) {
                programId = em.getReference(programId.getClass(), programId.getId());
                curriculumEntity.setProgramId(programId);
            }
            List<SubjectCurriculumEntity> attachedSubjectCurriculumEntityList = new ArrayList<SubjectCurriculumEntity>();
            for (SubjectCurriculumEntity subjectCurriculumEntityListSubjectCurriculumEntityToAttach : curriculumEntity.getSubjectCurriculumEntityList()) {
                subjectCurriculumEntityListSubjectCurriculumEntityToAttach = em.getReference(subjectCurriculumEntityListSubjectCurriculumEntityToAttach.getClass(), subjectCurriculumEntityListSubjectCurriculumEntityToAttach.getId());
                attachedSubjectCurriculumEntityList.add(subjectCurriculumEntityListSubjectCurriculumEntityToAttach);
            }
            curriculumEntity.setSubjectCurriculumEntityList(attachedSubjectCurriculumEntityList);
            List<DocumentStudentEntity> attachedDocumentStudentEntityList = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentEntityListDocumentStudentEntityToAttach : curriculumEntity.getDocumentStudentEntityList()) {
                documentStudentEntityListDocumentStudentEntityToAttach = em.getReference(documentStudentEntityListDocumentStudentEntityToAttach.getClass(), documentStudentEntityListDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentEntityList.add(documentStudentEntityListDocumentStudentEntityToAttach);
            }
            curriculumEntity.setDocumentStudentEntityList(attachedDocumentStudentEntityList);
            List<SimulateDocumentStudentEntity> attachedSimulateDocumentStudentEntityList = new ArrayList<SimulateDocumentStudentEntity>();
            for (SimulateDocumentStudentEntity simulateDocumentStudentEntityListSimulateDocumentStudentEntityToAttach : curriculumEntity.getSimulateDocumentStudentEntityList()) {
                simulateDocumentStudentEntityListSimulateDocumentStudentEntityToAttach = em.getReference(simulateDocumentStudentEntityListSimulateDocumentStudentEntityToAttach.getClass(), simulateDocumentStudentEntityListSimulateDocumentStudentEntityToAttach.getId());
                attachedSimulateDocumentStudentEntityList.add(simulateDocumentStudentEntityListSimulateDocumentStudentEntityToAttach);
            }
            curriculumEntity.setSimulateDocumentStudentEntityList(attachedSimulateDocumentStudentEntityList);
            em.persist(curriculumEntity);
            if (programId != null) {
                programId.getCurriculumEntityList().add(curriculumEntity);
                programId = em.merge(programId);
            }
            for (SubjectCurriculumEntity subjectCurriculumEntityListSubjectCurriculumEntity : curriculumEntity.getSubjectCurriculumEntityList()) {
                CurriculumEntity oldCurriculumIdOfSubjectCurriculumEntityListSubjectCurriculumEntity = subjectCurriculumEntityListSubjectCurriculumEntity.getCurriculumId();
                subjectCurriculumEntityListSubjectCurriculumEntity.setCurriculumId(curriculumEntity);
                subjectCurriculumEntityListSubjectCurriculumEntity = em.merge(subjectCurriculumEntityListSubjectCurriculumEntity);
                if (oldCurriculumIdOfSubjectCurriculumEntityListSubjectCurriculumEntity != null) {
                    oldCurriculumIdOfSubjectCurriculumEntityListSubjectCurriculumEntity.getSubjectCurriculumEntityList().remove(subjectCurriculumEntityListSubjectCurriculumEntity);
                    oldCurriculumIdOfSubjectCurriculumEntityListSubjectCurriculumEntity = em.merge(oldCurriculumIdOfSubjectCurriculumEntityListSubjectCurriculumEntity);
                }
            }
            for (DocumentStudentEntity documentStudentEntityListDocumentStudentEntity : curriculumEntity.getDocumentStudentEntityList()) {
                CurriculumEntity oldCurriculumIdOfDocumentStudentEntityListDocumentStudentEntity = documentStudentEntityListDocumentStudentEntity.getCurriculumId();
                documentStudentEntityListDocumentStudentEntity.setCurriculumId(curriculumEntity);
                documentStudentEntityListDocumentStudentEntity = em.merge(documentStudentEntityListDocumentStudentEntity);
                if (oldCurriculumIdOfDocumentStudentEntityListDocumentStudentEntity != null) {
                    oldCurriculumIdOfDocumentStudentEntityListDocumentStudentEntity.getDocumentStudentEntityList().remove(documentStudentEntityListDocumentStudentEntity);
                    oldCurriculumIdOfDocumentStudentEntityListDocumentStudentEntity = em.merge(oldCurriculumIdOfDocumentStudentEntityListDocumentStudentEntity);
                }
            }
            for (SimulateDocumentStudentEntity simulateDocumentStudentEntityListSimulateDocumentStudentEntity : curriculumEntity.getSimulateDocumentStudentEntityList()) {
                CurriculumEntity oldCurriculumIdOfSimulateDocumentStudentEntityListSimulateDocumentStudentEntity = simulateDocumentStudentEntityListSimulateDocumentStudentEntity.getCurriculumId();
                simulateDocumentStudentEntityListSimulateDocumentStudentEntity.setCurriculumId(curriculumEntity);
                simulateDocumentStudentEntityListSimulateDocumentStudentEntity = em.merge(simulateDocumentStudentEntityListSimulateDocumentStudentEntity);
                if (oldCurriculumIdOfSimulateDocumentStudentEntityListSimulateDocumentStudentEntity != null) {
                    oldCurriculumIdOfSimulateDocumentStudentEntityListSimulateDocumentStudentEntity.getSimulateDocumentStudentEntityList().remove(simulateDocumentStudentEntityListSimulateDocumentStudentEntity);
                    oldCurriculumIdOfSimulateDocumentStudentEntityListSimulateDocumentStudentEntity = em.merge(oldCurriculumIdOfSimulateDocumentStudentEntityListSimulateDocumentStudentEntity);
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

    public void edit(CurriculumEntity curriculumEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CurriculumEntity persistentCurriculumEntity = em.find(CurriculumEntity.class, curriculumEntity.getId());
            ProgramEntity programIdOld = persistentCurriculumEntity.getProgramId();
            ProgramEntity programIdNew = curriculumEntity.getProgramId();
            List<SubjectCurriculumEntity> subjectCurriculumEntityListOld = persistentCurriculumEntity.getSubjectCurriculumEntityList();
            List<SubjectCurriculumEntity> subjectCurriculumEntityListNew = curriculumEntity.getSubjectCurriculumEntityList();
            List<DocumentStudentEntity> documentStudentEntityListOld = persistentCurriculumEntity.getDocumentStudentEntityList();
            List<DocumentStudentEntity> documentStudentEntityListNew = curriculumEntity.getDocumentStudentEntityList();
            List<SimulateDocumentStudentEntity> simulateDocumentStudentEntityListOld = persistentCurriculumEntity.getSimulateDocumentStudentEntityList();
            List<SimulateDocumentStudentEntity> simulateDocumentStudentEntityListNew = curriculumEntity.getSimulateDocumentStudentEntityList();
            if (programIdNew != null) {
                programIdNew = em.getReference(programIdNew.getClass(), programIdNew.getId());
                curriculumEntity.setProgramId(programIdNew);
            }
            List<SubjectCurriculumEntity> attachedSubjectCurriculumEntityListNew = new ArrayList<SubjectCurriculumEntity>();
            for (SubjectCurriculumEntity subjectCurriculumEntityListNewSubjectCurriculumEntityToAttach : subjectCurriculumEntityListNew) {
                subjectCurriculumEntityListNewSubjectCurriculumEntityToAttach = em.getReference(subjectCurriculumEntityListNewSubjectCurriculumEntityToAttach.getClass(), subjectCurriculumEntityListNewSubjectCurriculumEntityToAttach.getId());
                attachedSubjectCurriculumEntityListNew.add(subjectCurriculumEntityListNewSubjectCurriculumEntityToAttach);
            }
            subjectCurriculumEntityListNew = attachedSubjectCurriculumEntityListNew;
            curriculumEntity.setSubjectCurriculumEntityList(subjectCurriculumEntityListNew);
            List<DocumentStudentEntity> attachedDocumentStudentEntityListNew = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentEntityListNewDocumentStudentEntityToAttach : documentStudentEntityListNew) {
                documentStudentEntityListNewDocumentStudentEntityToAttach = em.getReference(documentStudentEntityListNewDocumentStudentEntityToAttach.getClass(), documentStudentEntityListNewDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentEntityListNew.add(documentStudentEntityListNewDocumentStudentEntityToAttach);
            }
            documentStudentEntityListNew = attachedDocumentStudentEntityListNew;
            curriculumEntity.setDocumentStudentEntityList(documentStudentEntityListNew);
            List<SimulateDocumentStudentEntity> attachedSimulateDocumentStudentEntityListNew = new ArrayList<SimulateDocumentStudentEntity>();
            for (SimulateDocumentStudentEntity simulateDocumentStudentEntityListNewSimulateDocumentStudentEntityToAttach : simulateDocumentStudentEntityListNew) {
                simulateDocumentStudentEntityListNewSimulateDocumentStudentEntityToAttach = em.getReference(simulateDocumentStudentEntityListNewSimulateDocumentStudentEntityToAttach.getClass(), simulateDocumentStudentEntityListNewSimulateDocumentStudentEntityToAttach.getId());
                attachedSimulateDocumentStudentEntityListNew.add(simulateDocumentStudentEntityListNewSimulateDocumentStudentEntityToAttach);
            }
            simulateDocumentStudentEntityListNew = attachedSimulateDocumentStudentEntityListNew;
            curriculumEntity.setSimulateDocumentStudentEntityList(simulateDocumentStudentEntityListNew);
            curriculumEntity = em.merge(curriculumEntity);
            if (programIdOld != null && !programIdOld.equals(programIdNew)) {
                programIdOld.getCurriculumEntityList().remove(curriculumEntity);
                programIdOld = em.merge(programIdOld);
            }
            if (programIdNew != null && !programIdNew.equals(programIdOld)) {
                programIdNew.getCurriculumEntityList().add(curriculumEntity);
                programIdNew = em.merge(programIdNew);
            }
            for (SubjectCurriculumEntity subjectCurriculumEntityListOldSubjectCurriculumEntity : subjectCurriculumEntityListOld) {
                if (!subjectCurriculumEntityListNew.contains(subjectCurriculumEntityListOldSubjectCurriculumEntity)) {
                    subjectCurriculumEntityListOldSubjectCurriculumEntity.setCurriculumId(null);
                    subjectCurriculumEntityListOldSubjectCurriculumEntity = em.merge(subjectCurriculumEntityListOldSubjectCurriculumEntity);
                }
            }
            for (SubjectCurriculumEntity subjectCurriculumEntityListNewSubjectCurriculumEntity : subjectCurriculumEntityListNew) {
                if (!subjectCurriculumEntityListOld.contains(subjectCurriculumEntityListNewSubjectCurriculumEntity)) {
                    CurriculumEntity oldCurriculumIdOfSubjectCurriculumEntityListNewSubjectCurriculumEntity = subjectCurriculumEntityListNewSubjectCurriculumEntity.getCurriculumId();
                    subjectCurriculumEntityListNewSubjectCurriculumEntity.setCurriculumId(curriculumEntity);
                    subjectCurriculumEntityListNewSubjectCurriculumEntity = em.merge(subjectCurriculumEntityListNewSubjectCurriculumEntity);
                    if (oldCurriculumIdOfSubjectCurriculumEntityListNewSubjectCurriculumEntity != null && !oldCurriculumIdOfSubjectCurriculumEntityListNewSubjectCurriculumEntity.equals(curriculumEntity)) {
                        oldCurriculumIdOfSubjectCurriculumEntityListNewSubjectCurriculumEntity.getSubjectCurriculumEntityList().remove(subjectCurriculumEntityListNewSubjectCurriculumEntity);
                        oldCurriculumIdOfSubjectCurriculumEntityListNewSubjectCurriculumEntity = em.merge(oldCurriculumIdOfSubjectCurriculumEntityListNewSubjectCurriculumEntity);
                    }
                }
            }
            for (DocumentStudentEntity documentStudentEntityListOldDocumentStudentEntity : documentStudentEntityListOld) {
                if (!documentStudentEntityListNew.contains(documentStudentEntityListOldDocumentStudentEntity)) {
                    documentStudentEntityListOldDocumentStudentEntity.setCurriculumId(null);
                    documentStudentEntityListOldDocumentStudentEntity = em.merge(documentStudentEntityListOldDocumentStudentEntity);
                }
            }
            for (DocumentStudentEntity documentStudentEntityListNewDocumentStudentEntity : documentStudentEntityListNew) {
                if (!documentStudentEntityListOld.contains(documentStudentEntityListNewDocumentStudentEntity)) {
                    CurriculumEntity oldCurriculumIdOfDocumentStudentEntityListNewDocumentStudentEntity = documentStudentEntityListNewDocumentStudentEntity.getCurriculumId();
                    documentStudentEntityListNewDocumentStudentEntity.setCurriculumId(curriculumEntity);
                    documentStudentEntityListNewDocumentStudentEntity = em.merge(documentStudentEntityListNewDocumentStudentEntity);
                    if (oldCurriculumIdOfDocumentStudentEntityListNewDocumentStudentEntity != null && !oldCurriculumIdOfDocumentStudentEntityListNewDocumentStudentEntity.equals(curriculumEntity)) {
                        oldCurriculumIdOfDocumentStudentEntityListNewDocumentStudentEntity.getDocumentStudentEntityList().remove(documentStudentEntityListNewDocumentStudentEntity);
                        oldCurriculumIdOfDocumentStudentEntityListNewDocumentStudentEntity = em.merge(oldCurriculumIdOfDocumentStudentEntityListNewDocumentStudentEntity);
                    }
                }
            }
            for (SimulateDocumentStudentEntity simulateDocumentStudentEntityListOldSimulateDocumentStudentEntity : simulateDocumentStudentEntityListOld) {
                if (!simulateDocumentStudentEntityListNew.contains(simulateDocumentStudentEntityListOldSimulateDocumentStudentEntity)) {
                    simulateDocumentStudentEntityListOldSimulateDocumentStudentEntity.setCurriculumId(null);
                    simulateDocumentStudentEntityListOldSimulateDocumentStudentEntity = em.merge(simulateDocumentStudentEntityListOldSimulateDocumentStudentEntity);
                }
            }
            for (SimulateDocumentStudentEntity simulateDocumentStudentEntityListNewSimulateDocumentStudentEntity : simulateDocumentStudentEntityListNew) {
                if (!simulateDocumentStudentEntityListOld.contains(simulateDocumentStudentEntityListNewSimulateDocumentStudentEntity)) {
                    CurriculumEntity oldCurriculumIdOfSimulateDocumentStudentEntityListNewSimulateDocumentStudentEntity = simulateDocumentStudentEntityListNewSimulateDocumentStudentEntity.getCurriculumId();
                    simulateDocumentStudentEntityListNewSimulateDocumentStudentEntity.setCurriculumId(curriculumEntity);
                    simulateDocumentStudentEntityListNewSimulateDocumentStudentEntity = em.merge(simulateDocumentStudentEntityListNewSimulateDocumentStudentEntity);
                    if (oldCurriculumIdOfSimulateDocumentStudentEntityListNewSimulateDocumentStudentEntity != null && !oldCurriculumIdOfSimulateDocumentStudentEntityListNewSimulateDocumentStudentEntity.equals(curriculumEntity)) {
                        oldCurriculumIdOfSimulateDocumentStudentEntityListNewSimulateDocumentStudentEntity.getSimulateDocumentStudentEntityList().remove(simulateDocumentStudentEntityListNewSimulateDocumentStudentEntity);
                        oldCurriculumIdOfSimulateDocumentStudentEntityListNewSimulateDocumentStudentEntity = em.merge(oldCurriculumIdOfSimulateDocumentStudentEntityListNewSimulateDocumentStudentEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = curriculumEntity.getId();
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

    public void destroy(Integer id) throws NonexistentEntityException {
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
            ProgramEntity programId = curriculumEntity.getProgramId();
            if (programId != null) {
                programId.getCurriculumEntityList().remove(curriculumEntity);
                programId = em.merge(programId);
            }
            List<SubjectCurriculumEntity> subjectCurriculumEntityList = curriculumEntity.getSubjectCurriculumEntityList();
            for (SubjectCurriculumEntity subjectCurriculumEntityListSubjectCurriculumEntity : subjectCurriculumEntityList) {
                subjectCurriculumEntityListSubjectCurriculumEntity.setCurriculumId(null);
                subjectCurriculumEntityListSubjectCurriculumEntity = em.merge(subjectCurriculumEntityListSubjectCurriculumEntity);
            }
            List<DocumentStudentEntity> documentStudentEntityList = curriculumEntity.getDocumentStudentEntityList();
            for (DocumentStudentEntity documentStudentEntityListDocumentStudentEntity : documentStudentEntityList) {
                documentStudentEntityListDocumentStudentEntity.setCurriculumId(null);
                documentStudentEntityListDocumentStudentEntity = em.merge(documentStudentEntityListDocumentStudentEntity);
            }
            List<SimulateDocumentStudentEntity> simulateDocumentStudentEntityList = curriculumEntity.getSimulateDocumentStudentEntityList();
            for (SimulateDocumentStudentEntity simulateDocumentStudentEntityListSimulateDocumentStudentEntity : simulateDocumentStudentEntityList) {
                simulateDocumentStudentEntityListSimulateDocumentStudentEntity.setCurriculumId(null);
                simulateDocumentStudentEntityListSimulateDocumentStudentEntity = em.merge(simulateDocumentStudentEntityListSimulateDocumentStudentEntity);
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

    public CurriculumEntity findCurriculumEntity(Integer id) {
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

