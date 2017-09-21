/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.entities.CurriculumEntity;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.ProgramEntity;
import com.capstone.entities.DocumentStudentEntity;
import com.capstone.jpa.exceptions.IllegalOrphanException;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.List;
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
        if (curriculumEntity.getDocumentStudentList() == null) {
            curriculumEntity.setDocumentStudentList(new ArrayList<DocumentStudentEntity>());
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
            List<DocumentStudentEntity> attachedDocumentStudentList = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentListDocumentStudentEntityToAttach : curriculumEntity.getDocumentStudentList()) {
                documentStudentListDocumentStudentEntityToAttach = em.getReference(documentStudentListDocumentStudentEntityToAttach.getClass(), documentStudentListDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentList.add(documentStudentListDocumentStudentEntityToAttach);
            }
            curriculumEntity.setDocumentStudentList(attachedDocumentStudentList);
            em.persist(curriculumEntity);
            if (programId != null) {
                programId.getCurriculumList().add(curriculumEntity);
                programId = em.merge(programId);
            }
            for (DocumentStudentEntity documentStudentListDocumentStudentEntity : curriculumEntity.getDocumentStudentList()) {
                CurriculumEntity oldCurriculumIdOfDocumentStudentListDocumentStudentEntity = documentStudentListDocumentStudentEntity.getCurriculumId();
                documentStudentListDocumentStudentEntity.setCurriculumId(curriculumEntity);
                documentStudentListDocumentStudentEntity = em.merge(documentStudentListDocumentStudentEntity);
                if (oldCurriculumIdOfDocumentStudentListDocumentStudentEntity != null) {
                    oldCurriculumIdOfDocumentStudentListDocumentStudentEntity.getDocumentStudentList().remove(documentStudentListDocumentStudentEntity);
                    oldCurriculumIdOfDocumentStudentListDocumentStudentEntity = em.merge(oldCurriculumIdOfDocumentStudentListDocumentStudentEntity);
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
            List<DocumentStudentEntity> documentStudentListOld = persistentCurriculumEntity.getDocumentStudentList();
            List<DocumentStudentEntity> documentStudentListNew = curriculumEntity.getDocumentStudentList();
            List<String> illegalOrphanMessages = null;
            for (DocumentStudentEntity documentStudentListOldDocumentStudentEntity : documentStudentListOld) {
                if (!documentStudentListNew.contains(documentStudentListOldDocumentStudentEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DocumentStudentEntity " + documentStudentListOldDocumentStudentEntity + " since its curriculumId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (programIdNew != null) {
                programIdNew = em.getReference(programIdNew.getClass(), programIdNew.getId());
                curriculumEntity.setProgramId(programIdNew);
            }
            List<DocumentStudentEntity> attachedDocumentStudentListNew = new ArrayList<DocumentStudentEntity>();
            for (DocumentStudentEntity documentStudentListNewDocumentStudentEntityToAttach : documentStudentListNew) {
                documentStudentListNewDocumentStudentEntityToAttach = em.getReference(documentStudentListNewDocumentStudentEntityToAttach.getClass(), documentStudentListNewDocumentStudentEntityToAttach.getId());
                attachedDocumentStudentListNew.add(documentStudentListNewDocumentStudentEntityToAttach);
            }
            documentStudentListNew = attachedDocumentStudentListNew;
            curriculumEntity.setDocumentStudentList(documentStudentListNew);
            curriculumEntity = em.merge(curriculumEntity);
            if (programIdOld != null && !programIdOld.equals(programIdNew)) {
                programIdOld.getCurriculumList().remove(curriculumEntity);
                programIdOld = em.merge(programIdOld);
            }
            if (programIdNew != null && !programIdNew.equals(programIdOld)) {
                programIdNew.getCurriculumList().add(curriculumEntity);
                programIdNew = em.merge(programIdNew);
            }
            for (DocumentStudentEntity documentStudentListNewDocumentStudentEntity : documentStudentListNew) {
                if (!documentStudentListOld.contains(documentStudentListNewDocumentStudentEntity)) {
                    CurriculumEntity oldCurriculumIdOfDocumentStudentListNewDocumentStudentEntity = documentStudentListNewDocumentStudentEntity.getCurriculumId();
                    documentStudentListNewDocumentStudentEntity.setCurriculumId(curriculumEntity);
                    documentStudentListNewDocumentStudentEntity = em.merge(documentStudentListNewDocumentStudentEntity);
                    if (oldCurriculumIdOfDocumentStudentListNewDocumentStudentEntity != null && !oldCurriculumIdOfDocumentStudentListNewDocumentStudentEntity.equals(curriculumEntity)) {
                        oldCurriculumIdOfDocumentStudentListNewDocumentStudentEntity.getDocumentStudentList().remove(documentStudentListNewDocumentStudentEntity);
                        oldCurriculumIdOfDocumentStudentListNewDocumentStudentEntity = em.merge(oldCurriculumIdOfDocumentStudentListNewDocumentStudentEntity);
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
            List<DocumentStudentEntity> documentStudentListOrphanCheck = curriculumEntity.getDocumentStudentList();
            for (DocumentStudentEntity documentStudentListOrphanCheckDocumentStudentEntity : documentStudentListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This CurriculumEntity (" + curriculumEntity + ") cannot be destroyed since the DocumentStudentEntity " + documentStudentListOrphanCheckDocumentStudentEntity + " in its documentStudentList field has a non-nullable curriculumId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            ProgramEntity programId = curriculumEntity.getProgramId();
            if (programId != null) {
                programId.getCurriculumList().remove(curriculumEntity);
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
