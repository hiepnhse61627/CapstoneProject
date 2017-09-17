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
import com.capstone.entities.SubjectEntity;
import com.capstone.entities.MarksEntity;
import com.capstone.entities.SubjectMarkComponentEntity;
import java.util.ArrayList;
import java.util.Collection;
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
public class SubjectMarkComponentEntityJpaController implements Serializable {

    public SubjectMarkComponentEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SubjectMarkComponentEntity subjectMarkComponentEntity) throws PreexistingEntityException, Exception {
        if (subjectMarkComponentEntity.getMarksBySubjectId() == null) {
            subjectMarkComponentEntity.setMarksBySubjectId(new ArrayList<MarksEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectEntity subjectBySubjectId = subjectMarkComponentEntity.getSubjectBySubjectId();
            if (subjectBySubjectId != null) {
                subjectBySubjectId = em.getReference(subjectBySubjectId.getClass(), subjectBySubjectId.getId());
                subjectMarkComponentEntity.setSubjectBySubjectId(subjectBySubjectId);
            }
            Collection<MarksEntity> attachedMarksBySubjectId = new ArrayList<MarksEntity>();
            for (MarksEntity marksBySubjectIdMarksEntityToAttach : subjectMarkComponentEntity.getMarksBySubjectId()) {
                marksBySubjectIdMarksEntityToAttach = em.getReference(marksBySubjectIdMarksEntityToAttach.getClass(), marksBySubjectIdMarksEntityToAttach.getId());
                attachedMarksBySubjectId.add(marksBySubjectIdMarksEntityToAttach);
            }
            subjectMarkComponentEntity.setMarksBySubjectId(attachedMarksBySubjectId);
            em.persist(subjectMarkComponentEntity);
            if (subjectBySubjectId != null) {
                SubjectMarkComponentEntity oldSubjectMarkComponentByIdOfSubjectBySubjectId = subjectBySubjectId.getSubjectMarkComponentById();
                if (oldSubjectMarkComponentByIdOfSubjectBySubjectId != null) {
                    oldSubjectMarkComponentByIdOfSubjectBySubjectId.setSubjectBySubjectId(null);
                    oldSubjectMarkComponentByIdOfSubjectBySubjectId = em.merge(oldSubjectMarkComponentByIdOfSubjectBySubjectId);
                }
                subjectBySubjectId.setSubjectMarkComponentById(subjectMarkComponentEntity);
                subjectBySubjectId = em.merge(subjectBySubjectId);
            }
            for (MarksEntity marksBySubjectIdMarksEntity : subjectMarkComponentEntity.getMarksBySubjectId()) {
                SubjectMarkComponentEntity oldSubjectMarkComponentBySubjectIdOfMarksBySubjectIdMarksEntity = marksBySubjectIdMarksEntity.getSubjectMarkComponentBySubjectId();
                marksBySubjectIdMarksEntity.setSubjectMarkComponentBySubjectId(subjectMarkComponentEntity);
                marksBySubjectIdMarksEntity = em.merge(marksBySubjectIdMarksEntity);
                if (oldSubjectMarkComponentBySubjectIdOfMarksBySubjectIdMarksEntity != null) {
                    oldSubjectMarkComponentBySubjectIdOfMarksBySubjectIdMarksEntity.getMarksBySubjectId().remove(marksBySubjectIdMarksEntity);
                    oldSubjectMarkComponentBySubjectIdOfMarksBySubjectIdMarksEntity = em.merge(oldSubjectMarkComponentBySubjectIdOfMarksBySubjectIdMarksEntity);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findSubjectMarkComponentEntity(subjectMarkComponentEntity.getSubjectId()) != null) {
                throw new PreexistingEntityException("SubjectMarkComponentEntity " + subjectMarkComponentEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SubjectMarkComponentEntity subjectMarkComponentEntity) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectMarkComponentEntity persistentSubjectMarkComponentEntity = em.find(SubjectMarkComponentEntity.class, subjectMarkComponentEntity.getSubjectId());
            SubjectEntity subjectBySubjectIdOld = persistentSubjectMarkComponentEntity.getSubjectBySubjectId();
            SubjectEntity subjectBySubjectIdNew = subjectMarkComponentEntity.getSubjectBySubjectId();
            Collection<MarksEntity> marksBySubjectIdOld = persistentSubjectMarkComponentEntity.getMarksBySubjectId();
            Collection<MarksEntity> marksBySubjectIdNew = subjectMarkComponentEntity.getMarksBySubjectId();
            List<String> illegalOrphanMessages = null;
            if (subjectBySubjectIdOld != null && !subjectBySubjectIdOld.equals(subjectBySubjectIdNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain SubjectEntity " + subjectBySubjectIdOld + " since its subjectMarkComponentById field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (subjectBySubjectIdNew != null) {
                subjectBySubjectIdNew = em.getReference(subjectBySubjectIdNew.getClass(), subjectBySubjectIdNew.getId());
                subjectMarkComponentEntity.setSubjectBySubjectId(subjectBySubjectIdNew);
            }
            Collection<MarksEntity> attachedMarksBySubjectIdNew = new ArrayList<MarksEntity>();
            for (MarksEntity marksBySubjectIdNewMarksEntityToAttach : marksBySubjectIdNew) {
                marksBySubjectIdNewMarksEntityToAttach = em.getReference(marksBySubjectIdNewMarksEntityToAttach.getClass(), marksBySubjectIdNewMarksEntityToAttach.getId());
                attachedMarksBySubjectIdNew.add(marksBySubjectIdNewMarksEntityToAttach);
            }
            marksBySubjectIdNew = attachedMarksBySubjectIdNew;
            subjectMarkComponentEntity.setMarksBySubjectId(marksBySubjectIdNew);
            subjectMarkComponentEntity = em.merge(subjectMarkComponentEntity);
            if (subjectBySubjectIdNew != null && !subjectBySubjectIdNew.equals(subjectBySubjectIdOld)) {
                SubjectMarkComponentEntity oldSubjectMarkComponentByIdOfSubjectBySubjectId = subjectBySubjectIdNew.getSubjectMarkComponentById();
                if (oldSubjectMarkComponentByIdOfSubjectBySubjectId != null) {
                    oldSubjectMarkComponentByIdOfSubjectBySubjectId.setSubjectBySubjectId(null);
                    oldSubjectMarkComponentByIdOfSubjectBySubjectId = em.merge(oldSubjectMarkComponentByIdOfSubjectBySubjectId);
                }
                subjectBySubjectIdNew.setSubjectMarkComponentById(subjectMarkComponentEntity);
                subjectBySubjectIdNew = em.merge(subjectBySubjectIdNew);
            }
            for (MarksEntity marksBySubjectIdOldMarksEntity : marksBySubjectIdOld) {
                if (!marksBySubjectIdNew.contains(marksBySubjectIdOldMarksEntity)) {
                    marksBySubjectIdOldMarksEntity.setSubjectMarkComponentBySubjectId(null);
                    marksBySubjectIdOldMarksEntity = em.merge(marksBySubjectIdOldMarksEntity);
                }
            }
            for (MarksEntity marksBySubjectIdNewMarksEntity : marksBySubjectIdNew) {
                if (!marksBySubjectIdOld.contains(marksBySubjectIdNewMarksEntity)) {
                    SubjectMarkComponentEntity oldSubjectMarkComponentBySubjectIdOfMarksBySubjectIdNewMarksEntity = marksBySubjectIdNewMarksEntity.getSubjectMarkComponentBySubjectId();
                    marksBySubjectIdNewMarksEntity.setSubjectMarkComponentBySubjectId(subjectMarkComponentEntity);
                    marksBySubjectIdNewMarksEntity = em.merge(marksBySubjectIdNewMarksEntity);
                    if (oldSubjectMarkComponentBySubjectIdOfMarksBySubjectIdNewMarksEntity != null && !oldSubjectMarkComponentBySubjectIdOfMarksBySubjectIdNewMarksEntity.equals(subjectMarkComponentEntity)) {
                        oldSubjectMarkComponentBySubjectIdOfMarksBySubjectIdNewMarksEntity.getMarksBySubjectId().remove(marksBySubjectIdNewMarksEntity);
                        oldSubjectMarkComponentBySubjectIdOfMarksBySubjectIdNewMarksEntity = em.merge(oldSubjectMarkComponentBySubjectIdOfMarksBySubjectIdNewMarksEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = subjectMarkComponentEntity.getSubjectId();
                if (findSubjectMarkComponentEntity(id) == null) {
                    throw new NonexistentEntityException("The subjectMarkComponentEntity with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectMarkComponentEntity subjectMarkComponentEntity;
            try {
                subjectMarkComponentEntity = em.getReference(SubjectMarkComponentEntity.class, id);
                subjectMarkComponentEntity.getSubjectId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The subjectMarkComponentEntity with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            SubjectEntity subjectBySubjectIdOrphanCheck = subjectMarkComponentEntity.getSubjectBySubjectId();
            if (subjectBySubjectIdOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This SubjectMarkComponentEntity (" + subjectMarkComponentEntity + ") cannot be destroyed since the SubjectEntity " + subjectBySubjectIdOrphanCheck + " in its subjectBySubjectId field has a non-nullable subjectMarkComponentById field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<MarksEntity> marksBySubjectId = subjectMarkComponentEntity.getMarksBySubjectId();
            for (MarksEntity marksBySubjectIdMarksEntity : marksBySubjectId) {
                marksBySubjectIdMarksEntity.setSubjectMarkComponentBySubjectId(null);
                marksBySubjectIdMarksEntity = em.merge(marksBySubjectIdMarksEntity);
            }
            em.remove(subjectMarkComponentEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<SubjectMarkComponentEntity> findSubjectMarkComponentEntityEntities() {
        return findSubjectMarkComponentEntityEntities(true, -1, -1);
    }

    public List<SubjectMarkComponentEntity> findSubjectMarkComponentEntityEntities(int maxResults, int firstResult) {
        return findSubjectMarkComponentEntityEntities(false, maxResults, firstResult);
    }

    private List<SubjectMarkComponentEntity> findSubjectMarkComponentEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SubjectMarkComponentEntity.class));
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

    public SubjectMarkComponentEntity findSubjectMarkComponentEntity(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SubjectMarkComponentEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getSubjectMarkComponentEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SubjectMarkComponentEntity> rt = cq.from(SubjectMarkComponentEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
