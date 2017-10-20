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
import com.capstone.jpa.exceptions.IllegalOrphanException;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;
import com.capstone.models.Ultilities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rem
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
        if (subjectMarkComponentEntity.getMarksEntityList() == null) {
            subjectMarkComponentEntity.setMarksEntityList(new ArrayList<MarksEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectEntity subjectEntity = subjectMarkComponentEntity.getSubjectEntity();
            if (subjectEntity != null) {
                subjectEntity = em.getReference(subjectEntity.getClass(), subjectEntity.getId());
                subjectMarkComponentEntity.setSubjectEntity(subjectEntity);
            }
            List<MarksEntity> attachedMarksEntityList = new ArrayList<MarksEntity>();
            for (MarksEntity marksEntityListMarksEntityToAttach : subjectMarkComponentEntity.getMarksEntityList()) {
                marksEntityListMarksEntityToAttach = em.getReference(marksEntityListMarksEntityToAttach.getClass(), marksEntityListMarksEntityToAttach.getId());
                attachedMarksEntityList.add(marksEntityListMarksEntityToAttach);
            }
            subjectMarkComponentEntity.setMarksEntityList(attachedMarksEntityList);
            em.persist(subjectMarkComponentEntity);
            if (subjectEntity != null) {
                SubjectMarkComponentEntity oldSubjectMarkComponentEntityOfSubjectEntity = subjectEntity.getSubjectMarkComponentEntity();
                if (oldSubjectMarkComponentEntityOfSubjectEntity != null) {
                    oldSubjectMarkComponentEntityOfSubjectEntity.setSubjectEntity(null);
                    oldSubjectMarkComponentEntityOfSubjectEntity = em.merge(oldSubjectMarkComponentEntityOfSubjectEntity);
                }
                subjectEntity.setSubjectMarkComponentEntity(subjectMarkComponentEntity);
                subjectEntity = em.merge(subjectEntity);
            }
            for (MarksEntity marksEntityListMarksEntity : subjectMarkComponentEntity.getMarksEntityList()) {
                SubjectMarkComponentEntity oldSubjectIdOfMarksEntityListMarksEntity = marksEntityListMarksEntity.getSubjectId();
                marksEntityListMarksEntity.setSubjectId(subjectMarkComponentEntity);
                marksEntityListMarksEntity = em.merge(marksEntityListMarksEntity);
                if (oldSubjectIdOfMarksEntityListMarksEntity != null) {
                    oldSubjectIdOfMarksEntityListMarksEntity.getMarksEntityList().remove(marksEntityListMarksEntity);
                    oldSubjectIdOfMarksEntityListMarksEntity = em.merge(oldSubjectIdOfMarksEntityListMarksEntity);
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
            SubjectEntity subjectEntityOld = persistentSubjectMarkComponentEntity.getSubjectEntity();
            SubjectEntity subjectEntityNew = subjectMarkComponentEntity.getSubjectEntity();
            List<MarksEntity> marksEntityListOld = persistentSubjectMarkComponentEntity.getMarksEntityList();
            List<MarksEntity> marksEntityListNew = subjectMarkComponentEntity.getMarksEntityList();
            List<String> illegalOrphanMessages = null;
            if (subjectEntityOld != null && !subjectEntityOld.equals(subjectEntityNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain SubjectEntity " + subjectEntityOld + " since its subjectMarkComponentEntity field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (subjectEntityNew != null) {
                subjectEntityNew = em.getReference(subjectEntityNew.getClass(), subjectEntityNew.getId());
                subjectMarkComponentEntity.setSubjectEntity(subjectEntityNew);
            }
            List<MarksEntity> attachedMarksEntityListNew = new ArrayList<MarksEntity>();
            for (MarksEntity marksEntityListNewMarksEntityToAttach : marksEntityListNew) {
                marksEntityListNewMarksEntityToAttach = em.getReference(marksEntityListNewMarksEntityToAttach.getClass(), marksEntityListNewMarksEntityToAttach.getId());
                attachedMarksEntityListNew.add(marksEntityListNewMarksEntityToAttach);
            }
            marksEntityListNew = attachedMarksEntityListNew;
            subjectMarkComponentEntity.setMarksEntityList(marksEntityListNew);
            subjectMarkComponentEntity = em.merge(subjectMarkComponentEntity);
            if (subjectEntityNew != null && !subjectEntityNew.equals(subjectEntityOld)) {
                SubjectMarkComponentEntity oldSubjectMarkComponentEntityOfSubjectEntity = subjectEntityNew.getSubjectMarkComponentEntity();
                if (oldSubjectMarkComponentEntityOfSubjectEntity != null) {
                    oldSubjectMarkComponentEntityOfSubjectEntity.setSubjectEntity(null);
                    oldSubjectMarkComponentEntityOfSubjectEntity = em.merge(oldSubjectMarkComponentEntityOfSubjectEntity);
                }
                subjectEntityNew.setSubjectMarkComponentEntity(subjectMarkComponentEntity);
                subjectEntityNew = em.merge(subjectEntityNew);
            }
            for (MarksEntity marksEntityListOldMarksEntity : marksEntityListOld) {
                if (!marksEntityListNew.contains(marksEntityListOldMarksEntity)) {
                    marksEntityListOldMarksEntity.setSubjectId(null);
                    marksEntityListOldMarksEntity = em.merge(marksEntityListOldMarksEntity);
                }
            }
            for (MarksEntity marksEntityListNewMarksEntity : marksEntityListNew) {
                if (!marksEntityListOld.contains(marksEntityListNewMarksEntity)) {
                    SubjectMarkComponentEntity oldSubjectIdOfMarksEntityListNewMarksEntity = marksEntityListNewMarksEntity.getSubjectId();
                    marksEntityListNewMarksEntity.setSubjectId(subjectMarkComponentEntity);
                    marksEntityListNewMarksEntity = em.merge(marksEntityListNewMarksEntity);
                    if (oldSubjectIdOfMarksEntityListNewMarksEntity != null && !oldSubjectIdOfMarksEntityListNewMarksEntity.equals(subjectMarkComponentEntity)) {
                        oldSubjectIdOfMarksEntityListNewMarksEntity.getMarksEntityList().remove(marksEntityListNewMarksEntity);
                        oldSubjectIdOfMarksEntityListNewMarksEntity = em.merge(oldSubjectIdOfMarksEntityListNewMarksEntity);
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
            SubjectEntity subjectEntityOrphanCheck = subjectMarkComponentEntity.getSubjectEntity();
            if (subjectEntityOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This SubjectMarkComponentEntity (" + subjectMarkComponentEntity + ") cannot be destroyed since the SubjectEntity " + subjectEntityOrphanCheck + " in its subjectEntity field has a non-nullable subjectMarkComponentEntity field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<MarksEntity> marksEntityList = subjectMarkComponentEntity.getMarksEntityList();
            for (MarksEntity marksEntityListMarksEntity : marksEntityList) {
                marksEntityListMarksEntity.setSubjectId(null);
                marksEntityListMarksEntity = em.merge(marksEntityListMarksEntity);
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
            SubjectMarkComponentEntity en = em.find(SubjectMarkComponentEntity.class, id);
            return en;
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
