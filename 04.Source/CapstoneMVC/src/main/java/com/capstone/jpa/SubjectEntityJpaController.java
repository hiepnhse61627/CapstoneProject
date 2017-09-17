/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.entities.SubjectEntity;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.SubjectMarkComponentEntity;
import java.util.ArrayList;
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
public class SubjectEntityJpaController implements Serializable {

    public SubjectEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SubjectEntity subjectEntity) throws IllegalOrphanException, PreexistingEntityException, Exception {
        List<String> illegalOrphanMessages = null;
        SubjectMarkComponentEntity subjectMarkComponentByIdOrphanCheck = subjectEntity.getSubjectMarkComponentById();
        if (subjectMarkComponentByIdOrphanCheck != null) {
            SubjectEntity oldSubjectBySubjectIdOfSubjectMarkComponentById = subjectMarkComponentByIdOrphanCheck.getSubjectBySubjectId();
            if (oldSubjectBySubjectIdOfSubjectMarkComponentById != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The SubjectMarkComponentEntity " + subjectMarkComponentByIdOrphanCheck + " already has an item of type SubjectEntity whose subjectMarkComponentById column cannot be null. Please make another selection for the subjectMarkComponentById field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectMarkComponentEntity subjectMarkComponentById = subjectEntity.getSubjectMarkComponentById();
            if (subjectMarkComponentById != null) {
                subjectMarkComponentById = em.getReference(subjectMarkComponentById.getClass(), subjectMarkComponentById.getSubjectId());
                subjectEntity.setSubjectMarkComponentById(subjectMarkComponentById);
            }
            em.persist(subjectEntity);
            if (subjectMarkComponentById != null) {
                subjectMarkComponentById.setSubjectBySubjectId(subjectEntity);
                subjectMarkComponentById = em.merge(subjectMarkComponentById);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findSubjectEntity(subjectEntity.getId()) != null) {
                throw new PreexistingEntityException("SubjectEntity " + subjectEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SubjectEntity subjectEntity) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectEntity persistentSubjectEntity = em.find(SubjectEntity.class, subjectEntity.getId());
            SubjectMarkComponentEntity subjectMarkComponentByIdOld = persistentSubjectEntity.getSubjectMarkComponentById();
            SubjectMarkComponentEntity subjectMarkComponentByIdNew = subjectEntity.getSubjectMarkComponentById();
            List<String> illegalOrphanMessages = null;
            if (subjectMarkComponentByIdNew != null && !subjectMarkComponentByIdNew.equals(subjectMarkComponentByIdOld)) {
                SubjectEntity oldSubjectBySubjectIdOfSubjectMarkComponentById = subjectMarkComponentByIdNew.getSubjectBySubjectId();
                if (oldSubjectBySubjectIdOfSubjectMarkComponentById != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The SubjectMarkComponentEntity " + subjectMarkComponentByIdNew + " already has an item of type SubjectEntity whose subjectMarkComponentById column cannot be null. Please make another selection for the subjectMarkComponentById field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (subjectMarkComponentByIdNew != null) {
                subjectMarkComponentByIdNew = em.getReference(subjectMarkComponentByIdNew.getClass(), subjectMarkComponentByIdNew.getSubjectId());
                subjectEntity.setSubjectMarkComponentById(subjectMarkComponentByIdNew);
            }
            subjectEntity = em.merge(subjectEntity);
            if (subjectMarkComponentByIdOld != null && !subjectMarkComponentByIdOld.equals(subjectMarkComponentByIdNew)) {
                subjectMarkComponentByIdOld.setSubjectBySubjectId(null);
                subjectMarkComponentByIdOld = em.merge(subjectMarkComponentByIdOld);
            }
            if (subjectMarkComponentByIdNew != null && !subjectMarkComponentByIdNew.equals(subjectMarkComponentByIdOld)) {
                subjectMarkComponentByIdNew.setSubjectBySubjectId(subjectEntity);
                subjectMarkComponentByIdNew = em.merge(subjectMarkComponentByIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = subjectEntity.getId();
                if (findSubjectEntity(id) == null) {
                    throw new NonexistentEntityException("The subjectEntity with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectEntity subjectEntity;
            try {
                subjectEntity = em.getReference(SubjectEntity.class, id);
                subjectEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The subjectEntity with id " + id + " no longer exists.", enfe);
            }
            SubjectMarkComponentEntity subjectMarkComponentById = subjectEntity.getSubjectMarkComponentById();
            if (subjectMarkComponentById != null) {
                subjectMarkComponentById.setSubjectBySubjectId(null);
                subjectMarkComponentById = em.merge(subjectMarkComponentById);
            }
            em.remove(subjectEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<SubjectEntity> findSubjectEntityEntities() {
        return findSubjectEntityEntities(true, -1, -1);
    }

    public List<SubjectEntity> findSubjectEntityEntities(int maxResults, int firstResult) {
        return findSubjectEntityEntities(false, maxResults, firstResult);
    }

    private List<SubjectEntity> findSubjectEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SubjectEntity.class));
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

    public SubjectEntity findSubjectEntity(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SubjectEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getSubjectEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SubjectEntity> rt = cq.from(SubjectEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
