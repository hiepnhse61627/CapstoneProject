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
import com.capstone.entities.SubjectMarkComponentEntity;
import com.capstone.entities.PrequisiteEntity;
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
public class SubjectEntityJpaController implements Serializable {

    public SubjectEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SubjectEntity subjectEntity) throws IllegalOrphanException, PreexistingEntityException, Exception {
        if (subjectEntity.getPrequisiteEntityList() == null) {
            subjectEntity.setPrequisiteEntityList(new ArrayList<PrequisiteEntity>());
        }
        List<String> illegalOrphanMessages = null;
        SubjectMarkComponentEntity subjectMarkComponentEntityOrphanCheck = subjectEntity.getSubjectMarkComponentEntity();
        if (subjectMarkComponentEntityOrphanCheck != null) {
            SubjectEntity oldSubjectEntityOfSubjectMarkComponentEntity = subjectMarkComponentEntityOrphanCheck.getSubjectEntity();
            if (oldSubjectEntityOfSubjectMarkComponentEntity != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The SubjectMarkComponentEntity " + subjectMarkComponentEntityOrphanCheck + " already has an item of type SubjectEntity whose subjectMarkComponentEntity column cannot be null. Please make another selection for the subjectMarkComponentEntity field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectMarkComponentEntity subjectMarkComponentEntity = subjectEntity.getSubjectMarkComponentEntity();
            if (subjectMarkComponentEntity != null) {
                subjectMarkComponentEntity = em.getReference(subjectMarkComponentEntity.getClass(), subjectMarkComponentEntity.getSubjectId());
                subjectEntity.setSubjectMarkComponentEntity(subjectMarkComponentEntity);
            }
            List<PrequisiteEntity> attachedPrequisiteEntityList = new ArrayList<PrequisiteEntity>();
            for (PrequisiteEntity prequisiteEntityListPrequisiteEntityToAttach : subjectEntity.getPrequisiteEntityList()) {
                prequisiteEntityListPrequisiteEntityToAttach = em.getReference(prequisiteEntityListPrequisiteEntityToAttach.getClass(), prequisiteEntityListPrequisiteEntityToAttach.getId());
                attachedPrequisiteEntityList.add(prequisiteEntityListPrequisiteEntityToAttach);
            }
            subjectEntity.setPrequisiteEntityList(attachedPrequisiteEntityList);
            em.persist(subjectEntity);
            if (subjectMarkComponentEntity != null) {
                subjectMarkComponentEntity.setSubjectEntity(subjectEntity);
                subjectMarkComponentEntity = em.merge(subjectMarkComponentEntity);
            }
            for (PrequisiteEntity prequisiteEntityListPrequisiteEntity : subjectEntity.getPrequisiteEntityList()) {
                SubjectEntity oldSubIdOfPrequisiteEntityListPrequisiteEntity = prequisiteEntityListPrequisiteEntity.getSubId();
                prequisiteEntityListPrequisiteEntity.setSubId(subjectEntity);
                prequisiteEntityListPrequisiteEntity = em.merge(prequisiteEntityListPrequisiteEntity);
                if (oldSubIdOfPrequisiteEntityListPrequisiteEntity != null) {
                    oldSubIdOfPrequisiteEntityListPrequisiteEntity.getPrequisiteEntityList().remove(prequisiteEntityListPrequisiteEntity);
                    oldSubIdOfPrequisiteEntityListPrequisiteEntity = em.merge(oldSubIdOfPrequisiteEntityListPrequisiteEntity);
                }
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
            SubjectMarkComponentEntity subjectMarkComponentEntityOld = persistentSubjectEntity.getSubjectMarkComponentEntity();
            SubjectMarkComponentEntity subjectMarkComponentEntityNew = subjectEntity.getSubjectMarkComponentEntity();
            List<PrequisiteEntity> prequisiteEntityListOld = persistentSubjectEntity.getPrequisiteEntityList();
            List<PrequisiteEntity> prequisiteEntityListNew = subjectEntity.getPrequisiteEntityList();
            List<String> illegalOrphanMessages = null;
            if (subjectMarkComponentEntityNew != null && !subjectMarkComponentEntityNew.equals(subjectMarkComponentEntityOld)) {
                SubjectEntity oldSubjectEntityOfSubjectMarkComponentEntity = subjectMarkComponentEntityNew.getSubjectEntity();
                if (oldSubjectEntityOfSubjectMarkComponentEntity != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The SubjectMarkComponentEntity " + subjectMarkComponentEntityNew + " already has an item of type SubjectEntity whose subjectMarkComponentEntity column cannot be null. Please make another selection for the subjectMarkComponentEntity field.");
                }
            }
            for (PrequisiteEntity prequisiteEntityListOldPrequisiteEntity : prequisiteEntityListOld) {
                if (!prequisiteEntityListNew.contains(prequisiteEntityListOldPrequisiteEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain PrequisiteEntity " + prequisiteEntityListOldPrequisiteEntity + " since its subId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (subjectMarkComponentEntityNew != null) {
                subjectMarkComponentEntityNew = em.getReference(subjectMarkComponentEntityNew.getClass(), subjectMarkComponentEntityNew.getSubjectId());
                subjectEntity.setSubjectMarkComponentEntity(subjectMarkComponentEntityNew);
            }
            List<PrequisiteEntity> attachedPrequisiteEntityListNew = new ArrayList<PrequisiteEntity>();
            for (PrequisiteEntity prequisiteEntityListNewPrequisiteEntityToAttach : prequisiteEntityListNew) {
                prequisiteEntityListNewPrequisiteEntityToAttach = em.getReference(prequisiteEntityListNewPrequisiteEntityToAttach.getClass(), prequisiteEntityListNewPrequisiteEntityToAttach.getId());
                attachedPrequisiteEntityListNew.add(prequisiteEntityListNewPrequisiteEntityToAttach);
            }
            prequisiteEntityListNew = attachedPrequisiteEntityListNew;
            subjectEntity.setPrequisiteEntityList(prequisiteEntityListNew);
            subjectEntity = em.merge(subjectEntity);
            if (subjectMarkComponentEntityOld != null && !subjectMarkComponentEntityOld.equals(subjectMarkComponentEntityNew)) {
                subjectMarkComponentEntityOld.setSubjectEntity(null);
                subjectMarkComponentEntityOld = em.merge(subjectMarkComponentEntityOld);
            }
            if (subjectMarkComponentEntityNew != null && !subjectMarkComponentEntityNew.equals(subjectMarkComponentEntityOld)) {
                subjectMarkComponentEntityNew.setSubjectEntity(subjectEntity);
                subjectMarkComponentEntityNew = em.merge(subjectMarkComponentEntityNew);
            }
            for (PrequisiteEntity prequisiteEntityListNewPrequisiteEntity : prequisiteEntityListNew) {
                if (!prequisiteEntityListOld.contains(prequisiteEntityListNewPrequisiteEntity)) {
                    SubjectEntity oldSubIdOfPrequisiteEntityListNewPrequisiteEntity = prequisiteEntityListNewPrequisiteEntity.getSubId();
                    prequisiteEntityListNewPrequisiteEntity.setSubId(subjectEntity);
                    prequisiteEntityListNewPrequisiteEntity = em.merge(prequisiteEntityListNewPrequisiteEntity);
                    if (oldSubIdOfPrequisiteEntityListNewPrequisiteEntity != null && !oldSubIdOfPrequisiteEntityListNewPrequisiteEntity.equals(subjectEntity)) {
                        oldSubIdOfPrequisiteEntityListNewPrequisiteEntity.getPrequisiteEntityList().remove(prequisiteEntityListNewPrequisiteEntity);
                        oldSubIdOfPrequisiteEntityListNewPrequisiteEntity = em.merge(oldSubIdOfPrequisiteEntityListNewPrequisiteEntity);
                    }
                }
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

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
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
            List<String> illegalOrphanMessages = null;
            List<PrequisiteEntity> prequisiteEntityListOrphanCheck = subjectEntity.getPrequisiteEntityList();
            for (PrequisiteEntity prequisiteEntityListOrphanCheckPrequisiteEntity : prequisiteEntityListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This SubjectEntity (" + subjectEntity + ") cannot be destroyed since the PrequisiteEntity " + prequisiteEntityListOrphanCheckPrequisiteEntity + " in its prequisiteEntityList field has a non-nullable subId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            SubjectMarkComponentEntity subjectMarkComponentEntity = subjectEntity.getSubjectMarkComponentEntity();
            if (subjectMarkComponentEntity != null) {
                subjectMarkComponentEntity.setSubjectEntity(null);
                subjectMarkComponentEntity = em.merge(subjectMarkComponentEntity);
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
