/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.jpa.exceptions.*;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.capstone.entities.SubjectMarkComponentEntity;
import com.capstone.entities.PrequisiteEntity;

import java.util.ArrayList;
import java.util.List;

import com.capstone.entities.CurriculumMappingEntity;
import com.capstone.entities.SubjectEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
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
        if (subjectEntity.getSubOfPrequisiteList() == null) {
            subjectEntity.setSubOfPrequisiteList(new ArrayList<PrequisiteEntity>());
        }
        if (subjectEntity.getSubOfPrequisiteList() == null) {
            subjectEntity.setSubOfPrequisiteList(new ArrayList<PrequisiteEntity>());
        }
        if (subjectEntity.getCurriculumMappingEntityList() == null) {
            subjectEntity.setCurriculumMappingEntityList(new ArrayList<CurriculumMappingEntity>());
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
            List<PrequisiteEntity> attachedSubOfPrequisiteList = new ArrayList<PrequisiteEntity>();
            for (PrequisiteEntity subOfPrequisiteListPrequisiteEntityToAttach : subjectEntity.getSubOfPrequisiteList()) {
                subOfPrequisiteListPrequisiteEntityToAttach = em.getReference(subOfPrequisiteListPrequisiteEntityToAttach.getClass(), subOfPrequisiteListPrequisiteEntityToAttach.getPrequisiteEntityPK());
                attachedSubOfPrequisiteList.add(subOfPrequisiteListPrequisiteEntityToAttach);
            }
            subjectEntity.setSubOfPrequisiteList(attachedSubOfPrequisiteList);
            List<PrequisiteEntity> attachedPrequisiteEntityList = new ArrayList<PrequisiteEntity>();
            for (PrequisiteEntity prequisiteEntityListPrequisiteEntityToAttach : subjectEntity.getSubOfPrequisiteList()) {
                prequisiteEntityListPrequisiteEntityToAttach = em.getReference(prequisiteEntityListPrequisiteEntityToAttach.getClass(), prequisiteEntityListPrequisiteEntityToAttach.getPrequisiteEntityPK());
                attachedPrequisiteEntityList.add(prequisiteEntityListPrequisiteEntityToAttach);
            }
            subjectEntity.setSubOfPrequisiteList(attachedPrequisiteEntityList);
            List<CurriculumMappingEntity> attachedCurriculumMappingEntityList = new ArrayList<CurriculumMappingEntity>();
            for (CurriculumMappingEntity curriculumMappingEntityListCurriculumMappingEntityToAttach : subjectEntity.getCurriculumMappingEntityList()) {
                curriculumMappingEntityListCurriculumMappingEntityToAttach = em.getReference(curriculumMappingEntityListCurriculumMappingEntityToAttach.getClass(), curriculumMappingEntityListCurriculumMappingEntityToAttach.getCurriculumMappingEntityPK());
                attachedCurriculumMappingEntityList.add(curriculumMappingEntityListCurriculumMappingEntityToAttach);
            }
            subjectEntity.setCurriculumMappingEntityList(attachedCurriculumMappingEntityList);
            em.persist(subjectEntity);
            if (subjectMarkComponentEntity != null) {
                subjectMarkComponentEntity.setSubjectEntity(subjectEntity);
                subjectMarkComponentEntity = em.merge(subjectMarkComponentEntity);
            }
            for (PrequisiteEntity subOfPrequisiteListPrequisiteEntity : subjectEntity.getSubOfPrequisiteList()) {
                SubjectEntity oldSubjectEntityOfSubOfPrequisiteListPrequisiteEntity = subOfPrequisiteListPrequisiteEntity.getSubjectEntity();
                subOfPrequisiteListPrequisiteEntity.setSubjectEntity(subjectEntity);
                subOfPrequisiteListPrequisiteEntity = em.merge(subOfPrequisiteListPrequisiteEntity);
                if (oldSubjectEntityOfSubOfPrequisiteListPrequisiteEntity != null) {
                    oldSubjectEntityOfSubOfPrequisiteListPrequisiteEntity.getSubOfPrequisiteList().remove(subOfPrequisiteListPrequisiteEntity);
                    oldSubjectEntityOfSubOfPrequisiteListPrequisiteEntity = em.merge(oldSubjectEntityOfSubOfPrequisiteListPrequisiteEntity);
                }
            }
            for (PrequisiteEntity prequisiteEntityListPrequisiteEntity : subjectEntity.getSubOfPrequisiteList()) {
                SubjectEntity oldPrequisiteSubjectEntityOfPrequisiteEntityListPrequisiteEntity = prequisiteEntityListPrequisiteEntity.getPrequisiteSubjectEntity();
                prequisiteEntityListPrequisiteEntity.setPrequisiteSubjectEntity(subjectEntity);
                prequisiteEntityListPrequisiteEntity = em.merge(prequisiteEntityListPrequisiteEntity);
                if (oldPrequisiteSubjectEntityOfPrequisiteEntityListPrequisiteEntity != null) {
                    oldPrequisiteSubjectEntityOfPrequisiteEntityListPrequisiteEntity.getSubOfPrequisiteList().remove(prequisiteEntityListPrequisiteEntity);
                    oldPrequisiteSubjectEntityOfPrequisiteEntityListPrequisiteEntity = em.merge(oldPrequisiteSubjectEntityOfPrequisiteEntityListPrequisiteEntity);
                }
            }
            for (CurriculumMappingEntity curriculumMappingEntityListCurriculumMappingEntity : subjectEntity.getCurriculumMappingEntityList()) {
                SubjectEntity oldSubjectEntityOfCurriculumMappingEntityListCurriculumMappingEntity = curriculumMappingEntityListCurriculumMappingEntity.getSubjectEntity();
                curriculumMappingEntityListCurriculumMappingEntity.setSubjectEntity(subjectEntity);
                curriculumMappingEntityListCurriculumMappingEntity = em.merge(curriculumMappingEntityListCurriculumMappingEntity);
                if (oldSubjectEntityOfCurriculumMappingEntityListCurriculumMappingEntity != null) {
                    oldSubjectEntityOfCurriculumMappingEntityListCurriculumMappingEntity.getCurriculumMappingEntityList().remove(curriculumMappingEntityListCurriculumMappingEntity);
                    oldSubjectEntityOfCurriculumMappingEntityListCurriculumMappingEntity = em.merge(oldSubjectEntityOfCurriculumMappingEntityListCurriculumMappingEntity);
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
            List<PrequisiteEntity> subOfPrequisiteListOld = persistentSubjectEntity.getSubOfPrequisiteList();
            List<PrequisiteEntity> subOfPrequisiteListNew = subjectEntity.getSubOfPrequisiteList();
            List<PrequisiteEntity> prequisiteEntityListOld = persistentSubjectEntity.getSubOfPrequisiteList();
            List<PrequisiteEntity> prequisiteEntityListNew = subjectEntity.getSubOfPrequisiteList();
            List<CurriculumMappingEntity> curriculumMappingEntityListOld = persistentSubjectEntity.getCurriculumMappingEntityList();
            List<CurriculumMappingEntity> curriculumMappingEntityListNew = subjectEntity.getCurriculumMappingEntityList();
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
            for (PrequisiteEntity subOfPrequisiteListOldPrequisiteEntity : subOfPrequisiteListOld) {
                if (!subOfPrequisiteListNew.contains(subOfPrequisiteListOldPrequisiteEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain PrequisiteEntity " + subOfPrequisiteListOldPrequisiteEntity + " since its subjectEntity field is not nullable.");
                }
            }
            for (PrequisiteEntity prequisiteEntityListOldPrequisiteEntity : prequisiteEntityListOld) {
                if (!prequisiteEntityListNew.contains(prequisiteEntityListOldPrequisiteEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain PrequisiteEntity " + prequisiteEntityListOldPrequisiteEntity + " since its prequisiteSubjectEntity field is not nullable.");
                }
            }
            for (CurriculumMappingEntity curriculumMappingEntityListOldCurriculumMappingEntity : curriculumMappingEntityListOld) {
                if (!curriculumMappingEntityListNew.contains(curriculumMappingEntityListOldCurriculumMappingEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain CurriculumMappingEntity " + curriculumMappingEntityListOldCurriculumMappingEntity + " since its subjectEntity field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (subjectMarkComponentEntityNew != null) {
                subjectMarkComponentEntityNew = em.getReference(subjectMarkComponentEntityNew.getClass(), subjectMarkComponentEntityNew.getSubjectId());
                subjectEntity.setSubjectMarkComponentEntity(subjectMarkComponentEntityNew);
            }
            List<PrequisiteEntity> attachedSubOfPrequisiteListNew = new ArrayList<PrequisiteEntity>();
            for (PrequisiteEntity subOfPrequisiteListNewPrequisiteEntityToAttach : subOfPrequisiteListNew) {
                subOfPrequisiteListNewPrequisiteEntityToAttach = em.getReference(subOfPrequisiteListNewPrequisiteEntityToAttach.getClass(), subOfPrequisiteListNewPrequisiteEntityToAttach.getPrequisiteEntityPK());
                attachedSubOfPrequisiteListNew.add(subOfPrequisiteListNewPrequisiteEntityToAttach);
            }
            subOfPrequisiteListNew = attachedSubOfPrequisiteListNew;
            subjectEntity.setSubOfPrequisiteList(subOfPrequisiteListNew);
            List<PrequisiteEntity> attachedPrequisiteEntityListNew = new ArrayList<PrequisiteEntity>();
            for (PrequisiteEntity prequisiteEntityListNewPrequisiteEntityToAttach : prequisiteEntityListNew) {
                prequisiteEntityListNewPrequisiteEntityToAttach = em.getReference(prequisiteEntityListNewPrequisiteEntityToAttach.getClass(), prequisiteEntityListNewPrequisiteEntityToAttach.getPrequisiteEntityPK());
                attachedPrequisiteEntityListNew.add(prequisiteEntityListNewPrequisiteEntityToAttach);
            }
            prequisiteEntityListNew = attachedPrequisiteEntityListNew;
            subjectEntity.setSubOfPrequisiteList(prequisiteEntityListNew);
            List<CurriculumMappingEntity> attachedCurriculumMappingEntityListNew = new ArrayList<CurriculumMappingEntity>();
            for (CurriculumMappingEntity curriculumMappingEntityListNewCurriculumMappingEntityToAttach : curriculumMappingEntityListNew) {
                curriculumMappingEntityListNewCurriculumMappingEntityToAttach = em.getReference(curriculumMappingEntityListNewCurriculumMappingEntityToAttach.getClass(), curriculumMappingEntityListNewCurriculumMappingEntityToAttach.getCurriculumMappingEntityPK());
                attachedCurriculumMappingEntityListNew.add(curriculumMappingEntityListNewCurriculumMappingEntityToAttach);
            }
            curriculumMappingEntityListNew = attachedCurriculumMappingEntityListNew;
            subjectEntity.setCurriculumMappingEntityList(curriculumMappingEntityListNew);
            subjectEntity = em.merge(subjectEntity);
            if (subjectMarkComponentEntityOld != null && !subjectMarkComponentEntityOld.equals(subjectMarkComponentEntityNew)) {
                subjectMarkComponentEntityOld.setSubjectEntity(null);
                subjectMarkComponentEntityOld = em.merge(subjectMarkComponentEntityOld);
            }
            if (subjectMarkComponentEntityNew != null && !subjectMarkComponentEntityNew.equals(subjectMarkComponentEntityOld)) {
                subjectMarkComponentEntityNew.setSubjectEntity(subjectEntity);
                subjectMarkComponentEntityNew = em.merge(subjectMarkComponentEntityNew);
            }
            for (PrequisiteEntity subOfPrequisiteListNewPrequisiteEntity : subOfPrequisiteListNew) {
                if (!subOfPrequisiteListOld.contains(subOfPrequisiteListNewPrequisiteEntity)) {
                    SubjectEntity oldSubjectEntityOfSubOfPrequisiteListNewPrequisiteEntity = subOfPrequisiteListNewPrequisiteEntity.getSubjectEntity();
                    subOfPrequisiteListNewPrequisiteEntity.setSubjectEntity(subjectEntity);
                    subOfPrequisiteListNewPrequisiteEntity = em.merge(subOfPrequisiteListNewPrequisiteEntity);
                    if (oldSubjectEntityOfSubOfPrequisiteListNewPrequisiteEntity != null && !oldSubjectEntityOfSubOfPrequisiteListNewPrequisiteEntity.equals(subjectEntity)) {
                        oldSubjectEntityOfSubOfPrequisiteListNewPrequisiteEntity.getSubOfPrequisiteList().remove(subOfPrequisiteListNewPrequisiteEntity);
                        oldSubjectEntityOfSubOfPrequisiteListNewPrequisiteEntity = em.merge(oldSubjectEntityOfSubOfPrequisiteListNewPrequisiteEntity);
                    }
                }
            }
            for (PrequisiteEntity prequisiteEntityListNewPrequisiteEntity : prequisiteEntityListNew) {
                if (!prequisiteEntityListOld.contains(prequisiteEntityListNewPrequisiteEntity)) {
                    SubjectEntity oldPrequisiteSubjectEntityOfPrequisiteEntityListNewPrequisiteEntity = prequisiteEntityListNewPrequisiteEntity.getPrequisiteSubjectEntity();
                    prequisiteEntityListNewPrequisiteEntity.setPrequisiteSubjectEntity(subjectEntity);
                    prequisiteEntityListNewPrequisiteEntity = em.merge(prequisiteEntityListNewPrequisiteEntity);
                    if (oldPrequisiteSubjectEntityOfPrequisiteEntityListNewPrequisiteEntity != null && !oldPrequisiteSubjectEntityOfPrequisiteEntityListNewPrequisiteEntity.equals(subjectEntity)) {
                        oldPrequisiteSubjectEntityOfPrequisiteEntityListNewPrequisiteEntity.getSubOfPrequisiteList().remove(prequisiteEntityListNewPrequisiteEntity);
                        oldPrequisiteSubjectEntityOfPrequisiteEntityListNewPrequisiteEntity = em.merge(oldPrequisiteSubjectEntityOfPrequisiteEntityListNewPrequisiteEntity);
                    }
                }
            }
            for (CurriculumMappingEntity curriculumMappingEntityListNewCurriculumMappingEntity : curriculumMappingEntityListNew) {
                if (!curriculumMappingEntityListOld.contains(curriculumMappingEntityListNewCurriculumMappingEntity)) {
                    SubjectEntity oldSubjectEntityOfCurriculumMappingEntityListNewCurriculumMappingEntity = curriculumMappingEntityListNewCurriculumMappingEntity.getSubjectEntity();
                    curriculumMappingEntityListNewCurriculumMappingEntity.setSubjectEntity(subjectEntity);
                    curriculumMappingEntityListNewCurriculumMappingEntity = em.merge(curriculumMappingEntityListNewCurriculumMappingEntity);
                    if (oldSubjectEntityOfCurriculumMappingEntityListNewCurriculumMappingEntity != null && !oldSubjectEntityOfCurriculumMappingEntityListNewCurriculumMappingEntity.equals(subjectEntity)) {
                        oldSubjectEntityOfCurriculumMappingEntityListNewCurriculumMappingEntity.getCurriculumMappingEntityList().remove(curriculumMappingEntityListNewCurriculumMappingEntity);
                        oldSubjectEntityOfCurriculumMappingEntityListNewCurriculumMappingEntity = em.merge(oldSubjectEntityOfCurriculumMappingEntityListNewCurriculumMappingEntity);
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
            List<PrequisiteEntity> subOfPrequisiteListOrphanCheck = subjectEntity.getSubOfPrequisiteList();
            for (PrequisiteEntity subOfPrequisiteListOrphanCheckPrequisiteEntity : subOfPrequisiteListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This SubjectEntity (" + subjectEntity + ") cannot be destroyed since the PrequisiteEntity " + subOfPrequisiteListOrphanCheckPrequisiteEntity + " in its subOfPrequisiteList field has a non-nullable subjectEntity field.");
            }
            List<PrequisiteEntity> prequisiteEntityListOrphanCheck = subjectEntity.getSubOfPrequisiteList();
            for (PrequisiteEntity prequisiteEntityListOrphanCheckPrequisiteEntity : prequisiteEntityListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This SubjectEntity (" + subjectEntity + ") cannot be destroyed since the PrequisiteEntity " + prequisiteEntityListOrphanCheckPrequisiteEntity + " in its prequisiteEntityList field has a non-nullable prequisiteSubjectEntity field.");
            }
            List<CurriculumMappingEntity> curriculumMappingEntityListOrphanCheck = subjectEntity.getCurriculumMappingEntityList();
            for (CurriculumMappingEntity curriculumMappingEntityListOrphanCheckCurriculumMappingEntity : curriculumMappingEntityListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This SubjectEntity (" + subjectEntity + ") cannot be destroyed since the CurriculumMappingEntity " + curriculumMappingEntityListOrphanCheckCurriculumMappingEntity + " in its curriculumMappingEntityList field has a non-nullable subjectEntity field.");
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return null;
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
