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
import com.capstone.entities.SubjectMarkComponentEntity;
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
public class SubjectEntityJpaController implements Serializable {

    public SubjectEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SubjectEntity subjectEntity) throws IllegalOrphanException, PreexistingEntityException, Exception {
        if (subjectEntity.getSubjectList() == null) {
            subjectEntity.setSubjectList(new ArrayList<SubjectEntity>());
        }
        List<String> illegalOrphanMessages = null;
        SubjectMarkComponentEntity subjectMarkComponentOrphanCheck = subjectEntity.getSubjectMarkComponent();
        if (subjectMarkComponentOrphanCheck != null) {
            SubjectEntity oldSubjectOfSubjectMarkComponent = subjectMarkComponentOrphanCheck.getSubject();
            if (oldSubjectOfSubjectMarkComponent != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The SubjectMarkComponentEntity " + subjectMarkComponentOrphanCheck + " already has an item of type SubjectEntity whose subjectMarkComponent column cannot be null. Please make another selection for the subjectMarkComponent field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectEntity prequisiteId = subjectEntity.getPrequisiteId();
            if (prequisiteId != null) {
                prequisiteId = em.getReference(prequisiteId.getClass(), prequisiteId.getId());
                subjectEntity.setPrequisiteId(prequisiteId);
            }
            SubjectMarkComponentEntity subjectMarkComponent = subjectEntity.getSubjectMarkComponent();
            if (subjectMarkComponent != null) {
                subjectMarkComponent = em.getReference(subjectMarkComponent.getClass(), subjectMarkComponent.getSubjectId());
                subjectEntity.setSubjectMarkComponent(subjectMarkComponent);
            }
            List<SubjectEntity> attachedSubjectList = new ArrayList<SubjectEntity>();
            for (SubjectEntity subjectListSubjectEntityToAttach : subjectEntity.getSubjectList()) {
                subjectListSubjectEntityToAttach = em.getReference(subjectListSubjectEntityToAttach.getClass(), subjectListSubjectEntityToAttach.getId());
                attachedSubjectList.add(subjectListSubjectEntityToAttach);
            }
            subjectEntity.setSubjectList(attachedSubjectList);
            em.persist(subjectEntity);
            if (prequisiteId != null) {
                prequisiteId.getSubjectList().add(subjectEntity);
                prequisiteId = em.merge(prequisiteId);
            }
            if (subjectMarkComponent != null) {
                subjectMarkComponent.setSubject(subjectEntity);
                subjectMarkComponent = em.merge(subjectMarkComponent);
            }
            for (SubjectEntity subjectListSubjectEntity : subjectEntity.getSubjectList()) {
                SubjectEntity oldPrequisiteIdOfSubjectListSubjectEntity = subjectListSubjectEntity.getPrequisiteId();
                subjectListSubjectEntity.setPrequisiteId(subjectEntity);
                subjectListSubjectEntity = em.merge(subjectListSubjectEntity);
                if (oldPrequisiteIdOfSubjectListSubjectEntity != null) {
                    oldPrequisiteIdOfSubjectListSubjectEntity.getSubjectList().remove(subjectListSubjectEntity);
                    oldPrequisiteIdOfSubjectListSubjectEntity = em.merge(oldPrequisiteIdOfSubjectListSubjectEntity);
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
            SubjectEntity prequisiteIdOld = persistentSubjectEntity.getPrequisiteId();
            SubjectEntity prequisiteIdNew = subjectEntity.getPrequisiteId();
            SubjectMarkComponentEntity subjectMarkComponentOld = persistentSubjectEntity.getSubjectMarkComponent();
            SubjectMarkComponentEntity subjectMarkComponentNew = subjectEntity.getSubjectMarkComponent();
            List<SubjectEntity> subjectListOld = persistentSubjectEntity.getSubjectList();
            List<SubjectEntity> subjectListNew = subjectEntity.getSubjectList();
            List<String> illegalOrphanMessages = null;
            if (subjectMarkComponentNew != null && !subjectMarkComponentNew.equals(subjectMarkComponentOld)) {
                SubjectEntity oldSubjectOfSubjectMarkComponent = subjectMarkComponentNew.getSubject();
                if (oldSubjectOfSubjectMarkComponent != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The SubjectMarkComponentEntity " + subjectMarkComponentNew + " already has an item of type SubjectEntity whose subjectMarkComponent column cannot be null. Please make another selection for the subjectMarkComponent field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (prequisiteIdNew != null) {
                prequisiteIdNew = em.getReference(prequisiteIdNew.getClass(), prequisiteIdNew.getId());
                subjectEntity.setPrequisiteId(prequisiteIdNew);
            }
            if (subjectMarkComponentNew != null) {
                subjectMarkComponentNew = em.getReference(subjectMarkComponentNew.getClass(), subjectMarkComponentNew.getSubjectId());
                subjectEntity.setSubjectMarkComponent(subjectMarkComponentNew);
            }
            List<SubjectEntity> attachedSubjectListNew = new ArrayList<SubjectEntity>();
            for (SubjectEntity subjectListNewSubjectEntityToAttach : subjectListNew) {
                subjectListNewSubjectEntityToAttach = em.getReference(subjectListNewSubjectEntityToAttach.getClass(), subjectListNewSubjectEntityToAttach.getId());
                attachedSubjectListNew.add(subjectListNewSubjectEntityToAttach);
            }
            subjectListNew = attachedSubjectListNew;
            subjectEntity.setSubjectList(subjectListNew);
            subjectEntity = em.merge(subjectEntity);
            if (prequisiteIdOld != null && !prequisiteIdOld.equals(prequisiteIdNew)) {
                prequisiteIdOld.getSubjectList().remove(subjectEntity);
                prequisiteIdOld = em.merge(prequisiteIdOld);
            }
            if (prequisiteIdNew != null && !prequisiteIdNew.equals(prequisiteIdOld)) {
                prequisiteIdNew.getSubjectList().add(subjectEntity);
                prequisiteIdNew = em.merge(prequisiteIdNew);
            }
            if (subjectMarkComponentOld != null && !subjectMarkComponentOld.equals(subjectMarkComponentNew)) {
                subjectMarkComponentOld.setSubject(null);
                subjectMarkComponentOld = em.merge(subjectMarkComponentOld);
            }
            if (subjectMarkComponentNew != null && !subjectMarkComponentNew.equals(subjectMarkComponentOld)) {
                subjectMarkComponentNew.setSubject(subjectEntity);
                subjectMarkComponentNew = em.merge(subjectMarkComponentNew);
            }
            for (SubjectEntity subjectListOldSubjectEntity : subjectListOld) {
                if (!subjectListNew.contains(subjectListOldSubjectEntity)) {
                    subjectListOldSubjectEntity.setPrequisiteId(null);
                    subjectListOldSubjectEntity = em.merge(subjectListOldSubjectEntity);
                }
            }
            for (SubjectEntity subjectListNewSubjectEntity : subjectListNew) {
                if (!subjectListOld.contains(subjectListNewSubjectEntity)) {
                    SubjectEntity oldPrequisiteIdOfSubjectListNewSubjectEntity = subjectListNewSubjectEntity.getPrequisiteId();
                    subjectListNewSubjectEntity.setPrequisiteId(subjectEntity);
                    subjectListNewSubjectEntity = em.merge(subjectListNewSubjectEntity);
                    if (oldPrequisiteIdOfSubjectListNewSubjectEntity != null && !oldPrequisiteIdOfSubjectListNewSubjectEntity.equals(subjectEntity)) {
                        oldPrequisiteIdOfSubjectListNewSubjectEntity.getSubjectList().remove(subjectListNewSubjectEntity);
                        oldPrequisiteIdOfSubjectListNewSubjectEntity = em.merge(oldPrequisiteIdOfSubjectListNewSubjectEntity);
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
            SubjectEntity prequisiteId = subjectEntity.getPrequisiteId();
            if (prequisiteId != null) {
                prequisiteId.getSubjectList().remove(subjectEntity);
                prequisiteId = em.merge(prequisiteId);
            }
            SubjectMarkComponentEntity subjectMarkComponent = subjectEntity.getSubjectMarkComponent();
            if (subjectMarkComponent != null) {
                subjectMarkComponent.setSubject(null);
                subjectMarkComponent = em.merge(subjectMarkComponent);
            }
            List<SubjectEntity> subjectList = subjectEntity.getSubjectList();
            for (SubjectEntity subjectListSubjectEntity : subjectList) {
                subjectListSubjectEntity.setPrequisiteId(null);
                subjectListSubjectEntity = em.merge(subjectListSubjectEntity);
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
