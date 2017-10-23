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
import java.util.ArrayList;
import java.util.List;
import com.capstone.entities.SubjectMarkComponentEntity;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

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

    public void create(SubjectEntity subjectEntity) throws PreexistingEntityException, Exception {
        if (subjectEntity.getSubjectEntityList() == null) {
            subjectEntity.setSubjectEntityList(new ArrayList<SubjectEntity>());
        }
        if (subjectEntity.getSubjectEntityList1() == null) {
            subjectEntity.setSubjectEntityList1(new ArrayList<SubjectEntity>());
        }
        if (subjectEntity.getSubjectMarkComponentEntityList() == null) {
            subjectEntity.setSubjectMarkComponentEntityList(new ArrayList<SubjectMarkComponentEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<SubjectEntity> attachedSubjectEntityList = new ArrayList<SubjectEntity>();
            for (SubjectEntity subjectEntityListSubjectEntityToAttach : subjectEntity.getSubjectEntityList()) {
                subjectEntityListSubjectEntityToAttach = em.getReference(subjectEntityListSubjectEntityToAttach.getClass(), subjectEntityListSubjectEntityToAttach.getId());
                attachedSubjectEntityList.add(subjectEntityListSubjectEntityToAttach);
            }
            subjectEntity.setSubjectEntityList(attachedSubjectEntityList);
            List<SubjectEntity> attachedSubjectEntityList1 = new ArrayList<SubjectEntity>();
            for (SubjectEntity subjectEntityList1SubjectEntityToAttach : subjectEntity.getSubjectEntityList1()) {
                subjectEntityList1SubjectEntityToAttach = em.getReference(subjectEntityList1SubjectEntityToAttach.getClass(), subjectEntityList1SubjectEntityToAttach.getId());
                attachedSubjectEntityList1.add(subjectEntityList1SubjectEntityToAttach);
            }
            subjectEntity.setSubjectEntityList1(attachedSubjectEntityList1);
            List<SubjectMarkComponentEntity> attachedSubjectMarkComponentEntityList = new ArrayList<SubjectMarkComponentEntity>();
            for (SubjectMarkComponentEntity subjectMarkComponentEntityListSubjectMarkComponentEntityToAttach : subjectEntity.getSubjectMarkComponentEntityList()) {
                subjectMarkComponentEntityListSubjectMarkComponentEntityToAttach = em.getReference(subjectMarkComponentEntityListSubjectMarkComponentEntityToAttach.getClass(), subjectMarkComponentEntityListSubjectMarkComponentEntityToAttach.getId());
                attachedSubjectMarkComponentEntityList.add(subjectMarkComponentEntityListSubjectMarkComponentEntityToAttach);
            }
            subjectEntity.setSubjectMarkComponentEntityList(attachedSubjectMarkComponentEntityList);
            em.persist(subjectEntity);
            for (SubjectEntity subjectEntityListSubjectEntity : subjectEntity.getSubjectEntityList()) {
                subjectEntityListSubjectEntity.getSubjectEntityList().add(subjectEntity);
                subjectEntityListSubjectEntity = em.merge(subjectEntityListSubjectEntity);
            }
            for (SubjectEntity subjectEntityList1SubjectEntity : subjectEntity.getSubjectEntityList1()) {
                subjectEntityList1SubjectEntity.getSubjectEntityList().add(subjectEntity);
                subjectEntityList1SubjectEntity = em.merge(subjectEntityList1SubjectEntity);
            }
            for (SubjectMarkComponentEntity subjectMarkComponentEntityListSubjectMarkComponentEntity : subjectEntity.getSubjectMarkComponentEntityList()) {
                SubjectEntity oldSubjectIdOfSubjectMarkComponentEntityListSubjectMarkComponentEntity = subjectMarkComponentEntityListSubjectMarkComponentEntity.getSubjectId();
                subjectMarkComponentEntityListSubjectMarkComponentEntity.setSubjectId(subjectEntity);
                subjectMarkComponentEntityListSubjectMarkComponentEntity = em.merge(subjectMarkComponentEntityListSubjectMarkComponentEntity);
                if (oldSubjectIdOfSubjectMarkComponentEntityListSubjectMarkComponentEntity != null) {
                    oldSubjectIdOfSubjectMarkComponentEntityListSubjectMarkComponentEntity.getSubjectMarkComponentEntityList().remove(subjectMarkComponentEntityListSubjectMarkComponentEntity);
                    oldSubjectIdOfSubjectMarkComponentEntityListSubjectMarkComponentEntity = em.merge(oldSubjectIdOfSubjectMarkComponentEntityListSubjectMarkComponentEntity);
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

    public void edit(SubjectEntity subjectEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectEntity persistentSubjectEntity = em.find(SubjectEntity.class, subjectEntity.getId());
            List<SubjectEntity> subjectEntityListOld = persistentSubjectEntity.getSubjectEntityList();
            List<SubjectEntity> subjectEntityListNew = subjectEntity.getSubjectEntityList();
            List<SubjectEntity> subjectEntityList1Old = persistentSubjectEntity.getSubjectEntityList1();
            List<SubjectEntity> subjectEntityList1New = subjectEntity.getSubjectEntityList1();
            List<SubjectMarkComponentEntity> subjectMarkComponentEntityListOld = persistentSubjectEntity.getSubjectMarkComponentEntityList();
            List<SubjectMarkComponentEntity> subjectMarkComponentEntityListNew = subjectEntity.getSubjectMarkComponentEntityList();
            List<SubjectEntity> attachedSubjectEntityListNew = new ArrayList<SubjectEntity>();
            for (SubjectEntity subjectEntityListNewSubjectEntityToAttach : subjectEntityListNew) {
                subjectEntityListNewSubjectEntityToAttach = em.getReference(subjectEntityListNewSubjectEntityToAttach.getClass(), subjectEntityListNewSubjectEntityToAttach.getId());
                attachedSubjectEntityListNew.add(subjectEntityListNewSubjectEntityToAttach);
            }
            subjectEntityListNew = attachedSubjectEntityListNew;
            subjectEntity.setSubjectEntityList(subjectEntityListNew);
            List<SubjectEntity> attachedSubjectEntityList1New = new ArrayList<SubjectEntity>();
            for (SubjectEntity subjectEntityList1NewSubjectEntityToAttach : subjectEntityList1New) {
                subjectEntityList1NewSubjectEntityToAttach = em.getReference(subjectEntityList1NewSubjectEntityToAttach.getClass(), subjectEntityList1NewSubjectEntityToAttach.getId());
                attachedSubjectEntityList1New.add(subjectEntityList1NewSubjectEntityToAttach);
            }
            subjectEntityList1New = attachedSubjectEntityList1New;
            subjectEntity.setSubjectEntityList1(subjectEntityList1New);
            List<SubjectMarkComponentEntity> attachedSubjectMarkComponentEntityListNew = new ArrayList<SubjectMarkComponentEntity>();
            for (SubjectMarkComponentEntity subjectMarkComponentEntityListNewSubjectMarkComponentEntityToAttach : subjectMarkComponentEntityListNew) {
                subjectMarkComponentEntityListNewSubjectMarkComponentEntityToAttach = em.getReference(subjectMarkComponentEntityListNewSubjectMarkComponentEntityToAttach.getClass(), subjectMarkComponentEntityListNewSubjectMarkComponentEntityToAttach.getId());
                attachedSubjectMarkComponentEntityListNew.add(subjectMarkComponentEntityListNewSubjectMarkComponentEntityToAttach);
            }
            subjectMarkComponentEntityListNew = attachedSubjectMarkComponentEntityListNew;
            subjectEntity.setSubjectMarkComponentEntityList(subjectMarkComponentEntityListNew);
            subjectEntity = em.merge(subjectEntity);
            for (SubjectEntity subjectEntityListOldSubjectEntity : subjectEntityListOld) {
                if (!subjectEntityListNew.contains(subjectEntityListOldSubjectEntity)) {
                    subjectEntityListOldSubjectEntity.getSubjectEntityList().remove(subjectEntity);
                    subjectEntityListOldSubjectEntity = em.merge(subjectEntityListOldSubjectEntity);
                }
            }
            for (SubjectEntity subjectEntityListNewSubjectEntity : subjectEntityListNew) {
                if (!subjectEntityListOld.contains(subjectEntityListNewSubjectEntity)) {
                    subjectEntityListNewSubjectEntity.getSubjectEntityList().add(subjectEntity);
                    subjectEntityListNewSubjectEntity = em.merge(subjectEntityListNewSubjectEntity);
                }
            }
            for (SubjectEntity subjectEntityList1OldSubjectEntity : subjectEntityList1Old) {
                if (!subjectEntityList1New.contains(subjectEntityList1OldSubjectEntity)) {
                    subjectEntityList1OldSubjectEntity.getSubjectEntityList().remove(subjectEntity);
                    subjectEntityList1OldSubjectEntity = em.merge(subjectEntityList1OldSubjectEntity);
                }
            }
            for (SubjectEntity subjectEntityList1NewSubjectEntity : subjectEntityList1New) {
                if (!subjectEntityList1Old.contains(subjectEntityList1NewSubjectEntity)) {
                    subjectEntityList1NewSubjectEntity.getSubjectEntityList().add(subjectEntity);
                    subjectEntityList1NewSubjectEntity = em.merge(subjectEntityList1NewSubjectEntity);
                }
            }
            for (SubjectMarkComponentEntity subjectMarkComponentEntityListOldSubjectMarkComponentEntity : subjectMarkComponentEntityListOld) {
                if (!subjectMarkComponentEntityListNew.contains(subjectMarkComponentEntityListOldSubjectMarkComponentEntity)) {
                    subjectMarkComponentEntityListOldSubjectMarkComponentEntity.setSubjectId(null);
                    subjectMarkComponentEntityListOldSubjectMarkComponentEntity = em.merge(subjectMarkComponentEntityListOldSubjectMarkComponentEntity);
                }
            }
            for (SubjectMarkComponentEntity subjectMarkComponentEntityListNewSubjectMarkComponentEntity : subjectMarkComponentEntityListNew) {
                if (!subjectMarkComponentEntityListOld.contains(subjectMarkComponentEntityListNewSubjectMarkComponentEntity)) {
                    SubjectEntity oldSubjectIdOfSubjectMarkComponentEntityListNewSubjectMarkComponentEntity = subjectMarkComponentEntityListNewSubjectMarkComponentEntity.getSubjectId();
                    subjectMarkComponentEntityListNewSubjectMarkComponentEntity.setSubjectId(subjectEntity);
                    subjectMarkComponentEntityListNewSubjectMarkComponentEntity = em.merge(subjectMarkComponentEntityListNewSubjectMarkComponentEntity);
                    if (oldSubjectIdOfSubjectMarkComponentEntityListNewSubjectMarkComponentEntity != null && !oldSubjectIdOfSubjectMarkComponentEntityListNewSubjectMarkComponentEntity.equals(subjectEntity)) {
                        oldSubjectIdOfSubjectMarkComponentEntityListNewSubjectMarkComponentEntity.getSubjectMarkComponentEntityList().remove(subjectMarkComponentEntityListNewSubjectMarkComponentEntity);
                        oldSubjectIdOfSubjectMarkComponentEntityListNewSubjectMarkComponentEntity = em.merge(oldSubjectIdOfSubjectMarkComponentEntityListNewSubjectMarkComponentEntity);
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
            List<SubjectEntity> subjectEntityList = subjectEntity.getSubjectEntityList();
            for (SubjectEntity subjectEntityListSubjectEntity : subjectEntityList) {
                subjectEntityListSubjectEntity.getSubjectEntityList().remove(subjectEntity);
                subjectEntityListSubjectEntity = em.merge(subjectEntityListSubjectEntity);
            }
            List<SubjectEntity> subjectEntityList1 = subjectEntity.getSubjectEntityList1();
            for (SubjectEntity subjectEntityList1SubjectEntity : subjectEntityList1) {
                subjectEntityList1SubjectEntity.getSubjectEntityList().remove(subjectEntity);
                subjectEntityList1SubjectEntity = em.merge(subjectEntityList1SubjectEntity);
            }
            List<SubjectMarkComponentEntity> subjectMarkComponentEntityList = subjectEntity.getSubjectMarkComponentEntityList();
            for (SubjectMarkComponentEntity subjectMarkComponentEntityListSubjectMarkComponentEntity : subjectMarkComponentEntityList) {
                subjectMarkComponentEntityListSubjectMarkComponentEntity.setSubjectId(null);
                subjectMarkComponentEntityListSubjectMarkComponentEntity = em.merge(subjectMarkComponentEntityListSubjectMarkComponentEntity);
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
