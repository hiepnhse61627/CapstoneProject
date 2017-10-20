/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.entities.PrequisiteEntity;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.SubjectEntity;
import com.capstone.jpa.exceptions.IllegalOrphanException;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rem
 */
public class PrequisiteEntityJpaController implements Serializable {

    public PrequisiteEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(PrequisiteEntity prequisiteEntity) throws IllegalOrphanException, PreexistingEntityException, Exception {
        List<String> illegalOrphanMessages = null;
        SubjectEntity subjectEntityOrphanCheck = prequisiteEntity.getSubjectEntity();
        if (subjectEntityOrphanCheck != null) {
            PrequisiteEntity oldPrequisiteOfSubjectEntity = subjectEntityOrphanCheck.getPrequisiteEntity();
            if (oldPrequisiteOfSubjectEntity != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The SubjectEntity " + subjectEntityOrphanCheck + " already has an item of type PrequisiteEntity whose subjectEntity column cannot be null. Please make another selection for the subjectEntity field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectEntity subjectEntity = prequisiteEntity.getSubjectEntity();
            if (subjectEntity != null) {
                subjectEntity = em.getReference(subjectEntity.getClass(), subjectEntity.getId());
                prequisiteEntity.setSubjectEntity(subjectEntity);
            }
            em.persist(prequisiteEntity);
            if (subjectEntity != null) {
                subjectEntity.setPrequisiteEntity(prequisiteEntity);
                subjectEntity = em.merge(subjectEntity);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPrequisiteEntity(prequisiteEntity.getSubId()) != null) {
                throw new PreexistingEntityException("PrequisiteEntity " + prequisiteEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(PrequisiteEntity prequisiteEntity) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PrequisiteEntity persistentPrequisiteEntity = em.find(PrequisiteEntity.class, prequisiteEntity.getSubId());
            SubjectEntity subjectEntityOld = persistentPrequisiteEntity.getSubjectEntity();
            SubjectEntity subjectEntityNew = prequisiteEntity.getSubjectEntity();
            List<String> illegalOrphanMessages = null;
            if (subjectEntityNew != null && !subjectEntityNew.equals(subjectEntityOld)) {
                PrequisiteEntity oldPrequisiteOfSubjectEntity = subjectEntityNew.getPrequisiteEntity();
                if (oldPrequisiteOfSubjectEntity != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The SubjectEntity " + subjectEntityNew + " already has an item of type PrequisiteEntity whose subjectEntity column cannot be null. Please make another selection for the subjectEntity field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (subjectEntityNew != null) {
                subjectEntityNew = em.getReference(subjectEntityNew.getClass(), subjectEntityNew.getId());
                prequisiteEntity.setSubjectEntity(subjectEntityNew);
            }
            prequisiteEntity = em.merge(prequisiteEntity);
            if (subjectEntityOld != null && !subjectEntityOld.equals(subjectEntityNew)) {
                subjectEntityOld.setPrequisiteEntity(null);
                subjectEntityOld = em.merge(subjectEntityOld);
            }
            if (subjectEntityNew != null && !subjectEntityNew.equals(subjectEntityOld)) {
                subjectEntityNew.setPrequisiteEntity(prequisiteEntity);
                subjectEntityNew = em.merge(subjectEntityNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = prequisiteEntity.getSubId();
                if (findPrequisiteEntity(id) == null) {
                    throw new NonexistentEntityException("The prequisiteEntity with id " + id + " no longer exists.");
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
            PrequisiteEntity prequisiteEntity;
            try {
                prequisiteEntity = em.getReference(PrequisiteEntity.class, id);
                prequisiteEntity.getSubId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The prequisiteEntity with id " + id + " no longer exists.", enfe);
            }
            SubjectEntity subjectEntity = prequisiteEntity.getSubjectEntity();
            if (subjectEntity != null) {
                subjectEntity.setPrequisiteEntity(null);
                subjectEntity = em.merge(subjectEntity);
            }
            em.remove(prequisiteEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<PrequisiteEntity> findPrequisiteEntityEntities() {
        return findPrequisiteEntityEntities(true, -1, -1);
    }

    public List<PrequisiteEntity> findPrequisiteEntityEntities(int maxResults, int firstResult) {
        return findPrequisiteEntityEntities(false, maxResults, firstResult);
    }

    private List<PrequisiteEntity> findPrequisiteEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(PrequisiteEntity.class));
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

    public PrequisiteEntity findPrequisiteEntity(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PrequisiteEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getPrequisiteEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<PrequisiteEntity> rt = cq.from(PrequisiteEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
