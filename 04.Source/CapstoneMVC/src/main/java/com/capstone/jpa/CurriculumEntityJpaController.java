/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.jpa.exceptions.IllegalOrphanException;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;
import com.capstone.entities.CurriculumEntity;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.ProgramEntity;
import com.capstone.entities.DocumentStudentEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rem
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
        if (curriculumEntity.getDocumentStudentEntityList() == null) {
            curriculumEntity.setDocumentStudentEntityList(new ArrayList<DocumentStudentEntity>());
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
            List<DocumentStudentEntity> attachedDocumentStudentEntityList = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentEntityListDocumentStudentEntityToAttach : curriculumEntity.getDocumentStudentEntityList()) {
                documentStudentEntityListDocumentStudentEntityToAttach = em.getReference(documentStudentEntityListDocumentStudentEntityToAttach.getClass(), documentStudentEntityListDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentEntityList.add(documentStudentEntityListDocumentStudentEntityToAttach);
            }
            curriculumEntity.setDocumentStudentEntityList(attachedDocumentStudentEntityList);
            em.persist(curriculumEntity);
            if (programId != null) {
                programId.getCurriculumEntityList().add(curriculumEntity);
                programId = em.merge(programId);
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
            ProgramEntity programIdOld = persistentCurriculumEntity.getProgramId();
            ProgramEntity programIdNew = curriculumEntity.getProgramId();
            List<DocumentStudentEntity> documentStudentEntityListOld = persistentCurriculumEntity.getDocumentStudentEntityList();
            List<DocumentStudentEntity> documentStudentEntityListNew = curriculumEntity.getDocumentStudentEntityList();
            List<String> illegalOrphanMessages = null;
            for (DocumentStudentEntity documentStudentEntityListOldDocumentStudentEntity : documentStudentEntityListOld) {
                if (!documentStudentEntityListNew.contains(documentStudentEntityListOldDocumentStudentEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DocumentStudentEntity " + documentStudentEntityListOldDocumentStudentEntity + " since its curriculumId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (programIdNew != null) {
                programIdNew = em.getReference(programIdNew.getClass(), programIdNew.getId());
                curriculumEntity.setProgramId(programIdNew);
            }
            List<DocumentStudentEntity> attachedDocumentStudentEntityListNew = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentEntityListNewDocumentStudentEntityToAttach : documentStudentEntityListNew) {
                documentStudentEntityListNewDocumentStudentEntityToAttach = em.getReference(documentStudentEntityListNewDocumentStudentEntityToAttach.getClass(), documentStudentEntityListNewDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentEntityListNew.add(documentStudentEntityListNewDocumentStudentEntityToAttach);
            }
            documentStudentEntityListNew = attachedDocumentStudentEntityListNew;
            curriculumEntity.setDocumentStudentEntityList(documentStudentEntityListNew);
            curriculumEntity = em.merge(curriculumEntity);
            if (programIdOld != null && !programIdOld.equals(programIdNew)) {
                programIdOld.getCurriculumEntityList().remove(curriculumEntity);
                programIdOld = em.merge(programIdOld);
            }
            if (programIdNew != null && !programIdNew.equals(programIdOld)) {
                programIdNew.getCurriculumEntityList().add(curriculumEntity);
                programIdNew = em.merge(programIdNew);
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

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
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
            List<DocumentStudentEntity> documentStudentEntityListOrphanCheck = curriculumEntity.getDocumentStudentEntityList();
            for (DocumentStudentEntity documentStudentEntityListOrphanCheckDocumentStudentEntity : documentStudentEntityListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This CurriculumEntity (" + curriculumEntity + ") cannot be destroyed since the DocumentStudentEntity " + documentStudentEntityListOrphanCheckDocumentStudentEntity + " in its documentStudentEntityList field has a non-nullable curriculumId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            ProgramEntity programId = curriculumEntity.getProgramId();
            if (programId != null) {
                programId.getCurriculumEntityList().remove(curriculumEntity);
                programId = em.merge(programId);
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
