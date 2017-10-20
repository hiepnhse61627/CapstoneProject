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
import com.capstone.entities.PrequisiteEntity;
import com.capstone.entities.SubjectMarkComponentEntity;
import com.capstone.entities.SubjectEntity;
import java.util.ArrayList;
import java.util.List;
import com.capstone.entities.CurriculumMappingEntity;
import com.capstone.jpa.exceptions.IllegalOrphanException;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;
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
        if (subjectEntity.getReplacementSubjectList() == null) {
            subjectEntity.setReplacementSubjectList(new ArrayList<SubjectEntity>());
        }
        if (subjectEntity.getSubjectEntityList() == null) {
            subjectEntity.setSubjectEntityList(new ArrayList<SubjectEntity>());
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
            PrequisiteEntity prequisite = subjectEntity.getPrequisiteEntity();
            if (prequisite != null) {
                prequisite = em.getReference(prequisite.getClass(), prequisite.getSubId());
                subjectEntity.setPrequisiteEntity(prequisite);
            }
            SubjectMarkComponentEntity subjectMarkComponentEntity = subjectEntity.getSubjectMarkComponentEntity();
            if (subjectMarkComponentEntity != null) {
                subjectMarkComponentEntity = em.getReference(subjectMarkComponentEntity.getClass(), subjectMarkComponentEntity.getSubjectId());
                subjectEntity.setSubjectMarkComponentEntity(subjectMarkComponentEntity);
            }
            List<SubjectEntity> attachedSubjectEntityList = new ArrayList<SubjectEntity>();
            for (SubjectEntity subjectEntityListSubjectEntityToAttach : subjectEntity.getReplacementSubjectList()) {
                subjectEntityListSubjectEntityToAttach = em.getReference(subjectEntityListSubjectEntityToAttach.getClass(), subjectEntityListSubjectEntityToAttach.getId());
                attachedSubjectEntityList.add(subjectEntityListSubjectEntityToAttach);
            }
            subjectEntity.setReplacementSubjectList(attachedSubjectEntityList);
            List<SubjectEntity> attachedSubjectEntityList1 = new ArrayList<SubjectEntity>();
            for (SubjectEntity subjectEntityList1SubjectEntityToAttach : subjectEntity.getSubjectEntityList()) {
                subjectEntityList1SubjectEntityToAttach = em.getReference(subjectEntityList1SubjectEntityToAttach.getClass(), subjectEntityList1SubjectEntityToAttach.getId());
                attachedSubjectEntityList1.add(subjectEntityList1SubjectEntityToAttach);
            }
            subjectEntity.setSubjectEntityList(attachedSubjectEntityList1);
            List<CurriculumMappingEntity> attachedCurriculumMappingEntityList = new ArrayList<CurriculumMappingEntity>();
            for (CurriculumMappingEntity curriculumMappingEntityListCurriculumMappingEntityToAttach : subjectEntity.getCurriculumMappingEntityList()) {
                curriculumMappingEntityListCurriculumMappingEntityToAttach = em.getReference(curriculumMappingEntityListCurriculumMappingEntityToAttach.getClass(), curriculumMappingEntityListCurriculumMappingEntityToAttach.getCurriculumMappingEntityPK());
                attachedCurriculumMappingEntityList.add(curriculumMappingEntityListCurriculumMappingEntityToAttach);
            }
            subjectEntity.setCurriculumMappingEntityList(attachedCurriculumMappingEntityList);
            em.persist(subjectEntity);
            if (prequisite != null) {
                SubjectEntity oldSubjectEntityOfPrequisite = prequisite.getSubjectEntity();
                if (oldSubjectEntityOfPrequisite != null) {
                    oldSubjectEntityOfPrequisite.setPrequisiteEntity(null);
                    oldSubjectEntityOfPrequisite = em.merge(oldSubjectEntityOfPrequisite);
                }
                prequisite.setSubjectEntity(subjectEntity);
                prequisite = em.merge(prequisite);
            }
            if (subjectMarkComponentEntity != null) {
                subjectMarkComponentEntity.setSubjectEntity(subjectEntity);
                subjectMarkComponentEntity = em.merge(subjectMarkComponentEntity);
            }
            for (SubjectEntity subjectEntityListSubjectEntity : subjectEntity.getReplacementSubjectList()) {
                subjectEntityListSubjectEntity.getReplacementSubjectList().add(subjectEntity);
                subjectEntityListSubjectEntity = em.merge(subjectEntityListSubjectEntity);
            }
            for (SubjectEntity subjectEntityList1SubjectEntity : subjectEntity.getSubjectEntityList()) {
                subjectEntityList1SubjectEntity.getReplacementSubjectList().add(subjectEntity);
                subjectEntityList1SubjectEntity = em.merge(subjectEntityList1SubjectEntity);
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
            PrequisiteEntity prequisiteOld = persistentSubjectEntity.getPrequisiteEntity();
            PrequisiteEntity prequisiteNew = subjectEntity.getPrequisiteEntity();
            SubjectMarkComponentEntity subjectMarkComponentEntityOld = persistentSubjectEntity.getSubjectMarkComponentEntity();
            SubjectMarkComponentEntity subjectMarkComponentEntityNew = subjectEntity.getSubjectMarkComponentEntity();
            List<SubjectEntity> subjectEntityListOld = persistentSubjectEntity.getReplacementSubjectList();
            List<SubjectEntity> subjectEntityListNew = subjectEntity.getReplacementSubjectList();
            List<SubjectEntity> subjectEntityList1Old = persistentSubjectEntity.getSubjectEntityList();
            List<SubjectEntity> subjectEntityList1New = subjectEntity.getSubjectEntityList();
            List<CurriculumMappingEntity> curriculumMappingEntityListOld = persistentSubjectEntity.getCurriculumMappingEntityList();
            List<CurriculumMappingEntity> curriculumMappingEntityListNew = subjectEntity.getCurriculumMappingEntityList();
            List<String> illegalOrphanMessages = null;
            if (prequisiteOld != null && !prequisiteOld.equals(prequisiteNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain PrequisiteEntity " + prequisiteOld + " since its subjectEntity field is not nullable.");
            }
            if (subjectMarkComponentEntityNew != null && !subjectMarkComponentEntityNew.equals(subjectMarkComponentEntityOld)) {
                SubjectEntity oldSubjectEntityOfSubjectMarkComponentEntity = subjectMarkComponentEntityNew.getSubjectEntity();
                if (oldSubjectEntityOfSubjectMarkComponentEntity != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The SubjectMarkComponentEntity " + subjectMarkComponentEntityNew + " already has an item of type SubjectEntity whose subjectMarkComponentEntity column cannot be null. Please make another selection for the subjectMarkComponentEntity field.");
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
            if (prequisiteNew != null) {
                prequisiteNew = em.getReference(prequisiteNew.getClass(), prequisiteNew.getSubId());
                subjectEntity.setPrequisiteEntity(prequisiteNew);
            }
            if (subjectMarkComponentEntityNew != null) {
                subjectMarkComponentEntityNew = em.getReference(subjectMarkComponentEntityNew.getClass(), subjectMarkComponentEntityNew.getSubjectId());
                subjectEntity.setSubjectMarkComponentEntity(subjectMarkComponentEntityNew);
            }
            List<SubjectEntity> attachedSubjectEntityListNew = new ArrayList<SubjectEntity>();
            for (SubjectEntity subjectEntityListNewSubjectEntityToAttach : subjectEntityListNew) {
                subjectEntityListNewSubjectEntityToAttach = em.getReference(subjectEntityListNewSubjectEntityToAttach.getClass(), subjectEntityListNewSubjectEntityToAttach.getId());
                attachedSubjectEntityListNew.add(subjectEntityListNewSubjectEntityToAttach);
            }
            subjectEntityListNew = attachedSubjectEntityListNew;
            subjectEntity.setReplacementSubjectList(subjectEntityListNew);
            List<SubjectEntity> attachedSubjectEntityList1New = new ArrayList<SubjectEntity>();
            for (SubjectEntity subjectEntityList1NewSubjectEntityToAttach : subjectEntityList1New) {
                subjectEntityList1NewSubjectEntityToAttach = em.getReference(subjectEntityList1NewSubjectEntityToAttach.getClass(), subjectEntityList1NewSubjectEntityToAttach.getId());
                attachedSubjectEntityList1New.add(subjectEntityList1NewSubjectEntityToAttach);
            }
            subjectEntityList1New = attachedSubjectEntityList1New;
            subjectEntity.setSubjectEntityList(subjectEntityList1New);
            List<CurriculumMappingEntity> attachedCurriculumMappingEntityListNew = new ArrayList<CurriculumMappingEntity>();
            for (CurriculumMappingEntity curriculumMappingEntityListNewCurriculumMappingEntityToAttach : curriculumMappingEntityListNew) {
                curriculumMappingEntityListNewCurriculumMappingEntityToAttach = em.getReference(curriculumMappingEntityListNewCurriculumMappingEntityToAttach.getClass(), curriculumMappingEntityListNewCurriculumMappingEntityToAttach.getCurriculumMappingEntityPK());
                attachedCurriculumMappingEntityListNew.add(curriculumMappingEntityListNewCurriculumMappingEntityToAttach);
            }
            curriculumMappingEntityListNew = attachedCurriculumMappingEntityListNew;
            subjectEntity.setCurriculumMappingEntityList(curriculumMappingEntityListNew);
            subjectEntity = em.merge(subjectEntity);
            if (prequisiteNew != null && !prequisiteNew.equals(prequisiteOld)) {
                SubjectEntity oldSubjectEntityOfPrequisite = prequisiteNew.getSubjectEntity();
                if (oldSubjectEntityOfPrequisite != null) {
                    oldSubjectEntityOfPrequisite.setPrequisiteEntity(null);
                    oldSubjectEntityOfPrequisite = em.merge(oldSubjectEntityOfPrequisite);
                }
                prequisiteNew.setSubjectEntity(subjectEntity);
                prequisiteNew = em.merge(prequisiteNew);
            }
            if (subjectMarkComponentEntityOld != null && !subjectMarkComponentEntityOld.equals(subjectMarkComponentEntityNew)) {
                subjectMarkComponentEntityOld.setSubjectEntity(null);
                subjectMarkComponentEntityOld = em.merge(subjectMarkComponentEntityOld);
            }
            if (subjectMarkComponentEntityNew != null && !subjectMarkComponentEntityNew.equals(subjectMarkComponentEntityOld)) {
                subjectMarkComponentEntityNew.setSubjectEntity(subjectEntity);
                subjectMarkComponentEntityNew = em.merge(subjectMarkComponentEntityNew);
            }
            for (SubjectEntity subjectEntityListOldSubjectEntity : subjectEntityListOld) {
                if (!subjectEntityListNew.contains(subjectEntityListOldSubjectEntity)) {
                    subjectEntityListOldSubjectEntity.getReplacementSubjectList().remove(subjectEntity);
                    subjectEntityListOldSubjectEntity = em.merge(subjectEntityListOldSubjectEntity);
                }
            }
            for (SubjectEntity subjectEntityListNewSubjectEntity : subjectEntityListNew) {
                if (!subjectEntityListOld.contains(subjectEntityListNewSubjectEntity)) {
                    subjectEntityListNewSubjectEntity.getReplacementSubjectList().add(subjectEntity);
                    subjectEntityListNewSubjectEntity = em.merge(subjectEntityListNewSubjectEntity);
                }
            }
            for (SubjectEntity subjectEntityList1OldSubjectEntity : subjectEntityList1Old) {
                if (!subjectEntityList1New.contains(subjectEntityList1OldSubjectEntity)) {
                    subjectEntityList1OldSubjectEntity.getReplacementSubjectList().remove(subjectEntity);
                    subjectEntityList1OldSubjectEntity = em.merge(subjectEntityList1OldSubjectEntity);
                }
            }
            for (SubjectEntity subjectEntityList1NewSubjectEntity : subjectEntityList1New) {
                if (!subjectEntityList1Old.contains(subjectEntityList1NewSubjectEntity)) {
                    subjectEntityList1NewSubjectEntity.getReplacementSubjectList().add(subjectEntity);
                    subjectEntityList1NewSubjectEntity = em.merge(subjectEntityList1NewSubjectEntity);
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
            PrequisiteEntity prequisiteOrphanCheck = subjectEntity.getPrequisiteEntity();
            if (prequisiteOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This SubjectEntity (" + subjectEntity + ") cannot be destroyed since the PrequisiteEntity " + prequisiteOrphanCheck + " in its prequisite field has a non-nullable subjectEntity field.");
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
            List<SubjectEntity> subjectEntityList = subjectEntity.getReplacementSubjectList();
            for (SubjectEntity subjectEntityListSubjectEntity : subjectEntityList) {
                subjectEntityListSubjectEntity.getReplacementSubjectList().remove(subjectEntity);
                subjectEntityListSubjectEntity = em.merge(subjectEntityListSubjectEntity);
            }
            List<SubjectEntity> subjectEntityList1 = subjectEntity.getSubjectEntityList();
            for (SubjectEntity subjectEntityList1SubjectEntity : subjectEntityList1) {
                subjectEntityList1SubjectEntity.getReplacementSubjectList().remove(subjectEntity);
                subjectEntityList1SubjectEntity = em.merge(subjectEntityList1SubjectEntity);
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
