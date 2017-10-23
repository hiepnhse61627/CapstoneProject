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
import com.capstone.entities.MarkComponentEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.entities.SubjectMarkComponentEntity;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

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
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MarkComponentEntity markComponentId = subjectMarkComponentEntity.getMarkComponentId();
            if (markComponentId != null) {
                markComponentId = em.getReference(markComponentId.getClass(), markComponentId.getId());
                subjectMarkComponentEntity.setMarkComponentId(markComponentId);
            }
            SubjectEntity subjectId = subjectMarkComponentEntity.getSubjectId();
            if (subjectId != null) {
                subjectId = em.getReference(subjectId.getClass(), subjectId.getId());
                subjectMarkComponentEntity.setSubjectId(subjectId);
            }
            em.persist(subjectMarkComponentEntity);
            if (markComponentId != null) {
                markComponentId.getSubjectMarkComponentEntityList().add(subjectMarkComponentEntity);
                markComponentId = em.merge(markComponentId);
            }
            if (subjectId != null) {
                subjectId.getSubjectMarkComponentEntityList().add(subjectMarkComponentEntity);
                subjectId = em.merge(subjectId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findSubjectMarkComponentEntity(subjectMarkComponentEntity.getId()) != null) {
                throw new PreexistingEntityException("SubjectMarkComponentEntity " + subjectMarkComponentEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SubjectMarkComponentEntity subjectMarkComponentEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectMarkComponentEntity persistentSubjectMarkComponentEntity = em.find(SubjectMarkComponentEntity.class, subjectMarkComponentEntity.getId());
            MarkComponentEntity markComponentIdOld = persistentSubjectMarkComponentEntity.getMarkComponentId();
            MarkComponentEntity markComponentIdNew = subjectMarkComponentEntity.getMarkComponentId();
            SubjectEntity subjectIdOld = persistentSubjectMarkComponentEntity.getSubjectId();
            SubjectEntity subjectIdNew = subjectMarkComponentEntity.getSubjectId();
            if (markComponentIdNew != null) {
                markComponentIdNew = em.getReference(markComponentIdNew.getClass(), markComponentIdNew.getId());
                subjectMarkComponentEntity.setMarkComponentId(markComponentIdNew);
            }
            if (subjectIdNew != null) {
                subjectIdNew = em.getReference(subjectIdNew.getClass(), subjectIdNew.getId());
                subjectMarkComponentEntity.setSubjectId(subjectIdNew);
            }
            subjectMarkComponentEntity = em.merge(subjectMarkComponentEntity);
            if (markComponentIdOld != null && !markComponentIdOld.equals(markComponentIdNew)) {
                markComponentIdOld.getSubjectMarkComponentEntityList().remove(subjectMarkComponentEntity);
                markComponentIdOld = em.merge(markComponentIdOld);
            }
            if (markComponentIdNew != null && !markComponentIdNew.equals(markComponentIdOld)) {
                markComponentIdNew.getSubjectMarkComponentEntityList().add(subjectMarkComponentEntity);
                markComponentIdNew = em.merge(markComponentIdNew);
            }
            if (subjectIdOld != null && !subjectIdOld.equals(subjectIdNew)) {
                subjectIdOld.getSubjectMarkComponentEntityList().remove(subjectMarkComponentEntity);
                subjectIdOld = em.merge(subjectIdOld);
            }
            if (subjectIdNew != null && !subjectIdNew.equals(subjectIdOld)) {
                subjectIdNew.getSubjectMarkComponentEntityList().add(subjectMarkComponentEntity);
                subjectIdNew = em.merge(subjectIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = subjectMarkComponentEntity.getId();
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

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectMarkComponentEntity subjectMarkComponentEntity;
            try {
                subjectMarkComponentEntity = em.getReference(SubjectMarkComponentEntity.class, id);
                subjectMarkComponentEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The subjectMarkComponentEntity with id " + id + " no longer exists.", enfe);
            }
            MarkComponentEntity markComponentId = subjectMarkComponentEntity.getMarkComponentId();
            if (markComponentId != null) {
                markComponentId.getSubjectMarkComponentEntityList().remove(subjectMarkComponentEntity);
                markComponentId = em.merge(markComponentId);
            }
            SubjectEntity subjectId = subjectMarkComponentEntity.getSubjectId();
            if (subjectId != null) {
                subjectId.getSubjectMarkComponentEntityList().remove(subjectMarkComponentEntity);
                subjectId = em.merge(subjectId);
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

    public SubjectMarkComponentEntity findSubjectMarkComponentEntity(Integer id) {
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
