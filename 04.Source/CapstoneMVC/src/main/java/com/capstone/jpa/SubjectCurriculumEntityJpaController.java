/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.entities.*;
import com.capstone.jpa.exceptions.*;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rem
 */
public class SubjectCurriculumEntityJpaController implements Serializable {

    public SubjectCurriculumEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SubjectCurriculumEntity subjectCurriculumEntity) throws PreexistingEntityException, Exception {
        if (subjectCurriculumEntity.getCurriculumMappingEntityList() == null) {
            subjectCurriculumEntity.setCurriculumMappingEntityList(new ArrayList<CurriculumMappingEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<CurriculumMappingEntity> attachedCurriculumMappingEntityList = new ArrayList<CurriculumMappingEntity>();
            for (CurriculumMappingEntity curriculumMappingEntityListCurriculumMappingEntityToAttach : subjectCurriculumEntity.getCurriculumMappingEntityList()) {
                curriculumMappingEntityListCurriculumMappingEntityToAttach = em.getReference(curriculumMappingEntityListCurriculumMappingEntityToAttach.getClass(), curriculumMappingEntityListCurriculumMappingEntityToAttach.getCurriculumMappingEntityPK());
                attachedCurriculumMappingEntityList.add(curriculumMappingEntityListCurriculumMappingEntityToAttach);
            }
            subjectCurriculumEntity.setCurriculumMappingEntityList(attachedCurriculumMappingEntityList);
            em.persist(subjectCurriculumEntity);
            for (CurriculumMappingEntity curriculumMappingEntityListCurriculumMappingEntity : subjectCurriculumEntity.getCurriculumMappingEntityList()) {
                SubjectCurriculumEntity oldSubjectCurriculumEntityOfCurriculumMappingEntityListCurriculumMappingEntity = curriculumMappingEntityListCurriculumMappingEntity.getSubjectCurriculumEntity();
                curriculumMappingEntityListCurriculumMappingEntity.setSubjectCurriculumEntity(subjectCurriculumEntity);
                curriculumMappingEntityListCurriculumMappingEntity = em.merge(curriculumMappingEntityListCurriculumMappingEntity);
                if (oldSubjectCurriculumEntityOfCurriculumMappingEntityListCurriculumMappingEntity != null) {
                    oldSubjectCurriculumEntityOfCurriculumMappingEntityListCurriculumMappingEntity.getCurriculumMappingEntityList().remove(curriculumMappingEntityListCurriculumMappingEntity);
                    oldSubjectCurriculumEntityOfCurriculumMappingEntityListCurriculumMappingEntity = em.merge(oldSubjectCurriculumEntityOfCurriculumMappingEntityListCurriculumMappingEntity);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findSubjectCurriculumEntity(subjectCurriculumEntity.getId()) != null) {
                throw new PreexistingEntityException("SubjectCurriculumEntity " + subjectCurriculumEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SubjectCurriculumEntity subjectCurriculumEntity) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectCurriculumEntity persistentSubjectCurriculumEntity = em.find(SubjectCurriculumEntity.class, subjectCurriculumEntity.getId());
            List<CurriculumMappingEntity> curriculumMappingEntityListOld = persistentSubjectCurriculumEntity.getCurriculumMappingEntityList();
            List<CurriculumMappingEntity> curriculumMappingEntityListNew = subjectCurriculumEntity.getCurriculumMappingEntityList();
            List<String> illegalOrphanMessages = null;
            for (CurriculumMappingEntity curriculumMappingEntityListOldCurriculumMappingEntity : curriculumMappingEntityListOld) {
                if (!curriculumMappingEntityListNew.contains(curriculumMappingEntityListOldCurriculumMappingEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain CurriculumMappingEntity " + curriculumMappingEntityListOldCurriculumMappingEntity + " since its subjectCurriculumEntity field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<CurriculumMappingEntity> attachedCurriculumMappingEntityListNew = new ArrayList<CurriculumMappingEntity>();
            for (CurriculumMappingEntity curriculumMappingEntityListNewCurriculumMappingEntityToAttach : curriculumMappingEntityListNew) {
                curriculumMappingEntityListNewCurriculumMappingEntityToAttach = em.getReference(curriculumMappingEntityListNewCurriculumMappingEntityToAttach.getClass(), curriculumMappingEntityListNewCurriculumMappingEntityToAttach.getCurriculumMappingEntityPK());
                attachedCurriculumMappingEntityListNew.add(curriculumMappingEntityListNewCurriculumMappingEntityToAttach);
            }
            curriculumMappingEntityListNew = attachedCurriculumMappingEntityListNew;
            subjectCurriculumEntity.setCurriculumMappingEntityList(curriculumMappingEntityListNew);
            subjectCurriculumEntity = em.merge(subjectCurriculumEntity);
            for (CurriculumMappingEntity curriculumMappingEntityListNewCurriculumMappingEntity : curriculumMappingEntityListNew) {
                if (!curriculumMappingEntityListOld.contains(curriculumMappingEntityListNewCurriculumMappingEntity)) {
                    SubjectCurriculumEntity oldSubjectCurriculumEntityOfCurriculumMappingEntityListNewCurriculumMappingEntity = curriculumMappingEntityListNewCurriculumMappingEntity.getSubjectCurriculumEntity();
                    curriculumMappingEntityListNewCurriculumMappingEntity.setSubjectCurriculumEntity(subjectCurriculumEntity);
                    curriculumMappingEntityListNewCurriculumMappingEntity = em.merge(curriculumMappingEntityListNewCurriculumMappingEntity);
                    if (oldSubjectCurriculumEntityOfCurriculumMappingEntityListNewCurriculumMappingEntity != null && !oldSubjectCurriculumEntityOfCurriculumMappingEntityListNewCurriculumMappingEntity.equals(subjectCurriculumEntity)) {
                        oldSubjectCurriculumEntityOfCurriculumMappingEntityListNewCurriculumMappingEntity.getCurriculumMappingEntityList().remove(curriculumMappingEntityListNewCurriculumMappingEntity);
                        oldSubjectCurriculumEntityOfCurriculumMappingEntityListNewCurriculumMappingEntity = em.merge(oldSubjectCurriculumEntityOfCurriculumMappingEntityListNewCurriculumMappingEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = subjectCurriculumEntity.getId();
                if (findSubjectCurriculumEntity(id) == null) {
                    throw new NonexistentEntityException("The subjectCurriculumEntity with id " + id + " no longer exists.");
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
            SubjectCurriculumEntity subjectCurriculumEntity;
            try {
                subjectCurriculumEntity = em.getReference(SubjectCurriculumEntity.class, id);
                subjectCurriculumEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The subjectCurriculumEntity with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<CurriculumMappingEntity> curriculumMappingEntityListOrphanCheck = subjectCurriculumEntity.getCurriculumMappingEntityList();
            for (CurriculumMappingEntity curriculumMappingEntityListOrphanCheckCurriculumMappingEntity : curriculumMappingEntityListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This SubjectCurriculumEntity (" + subjectCurriculumEntity + ") cannot be destroyed since the CurriculumMappingEntity " + curriculumMappingEntityListOrphanCheckCurriculumMappingEntity + " in its curriculumMappingEntityList field has a non-nullable subjectCurriculumEntity field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(subjectCurriculumEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<SubjectCurriculumEntity> findSubjectCurriculumEntityEntities() {
        return findSubjectCurriculumEntityEntities(true, -1, -1);
    }

    public List<SubjectCurriculumEntity> findSubjectCurriculumEntityEntities(int maxResults, int firstResult) {
        return findSubjectCurriculumEntityEntities(false, maxResults, firstResult);
    }

    private List<SubjectCurriculumEntity> findSubjectCurriculumEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SubjectCurriculumEntity.class));
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

    public SubjectCurriculumEntity findSubjectCurriculumEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SubjectCurriculumEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getSubjectCurriculumEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SubjectCurriculumEntity> rt = cq.from(SubjectCurriculumEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
