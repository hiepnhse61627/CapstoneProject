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
import com.capstone.entities.MarksEntity;
import com.capstone.entities.SubjectMarkComponentEntity;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import java.util.ArrayList;
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

    public void create(SubjectMarkComponentEntity subjectMarkComponentEntity) {
        if (subjectMarkComponentEntity.getMarksEntityList() == null) {
            subjectMarkComponentEntity.setMarksEntityList(new ArrayList<MarksEntity>());
        }
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
            List<MarksEntity> attachedMarksEntityList = new ArrayList<MarksEntity>();
            for (MarksEntity marksEntityListMarksEntityToAttach : subjectMarkComponentEntity.getMarksEntityList()) {
                marksEntityListMarksEntityToAttach = em.getReference(marksEntityListMarksEntityToAttach.getClass(), marksEntityListMarksEntityToAttach.getId());
                attachedMarksEntityList.add(marksEntityListMarksEntityToAttach);
            }
            subjectMarkComponentEntity.setMarksEntityList(attachedMarksEntityList);
            em.persist(subjectMarkComponentEntity);
            if (markComponentId != null) {
                markComponentId.getSubjectMarkComponentEntityList().add(subjectMarkComponentEntity);
                markComponentId = em.merge(markComponentId);
            }
            if (subjectId != null) {
                subjectId.getSubjectMarkComponentEntityList().add(subjectMarkComponentEntity);
                subjectId = em.merge(subjectId);
            }
            for (MarksEntity marksEntityListMarksEntity : subjectMarkComponentEntity.getMarksEntityList()) {
                SubjectMarkComponentEntity oldSubjectMarkComponentIdOfMarksEntityListMarksEntity = marksEntityListMarksEntity.getSubjectMarkComponentId();
                marksEntityListMarksEntity.setSubjectMarkComponentId(subjectMarkComponentEntity);
                marksEntityListMarksEntity = em.merge(marksEntityListMarksEntity);
                if (oldSubjectMarkComponentIdOfMarksEntityListMarksEntity != null) {
                    oldSubjectMarkComponentIdOfMarksEntityListMarksEntity.getMarksEntityList().remove(marksEntityListMarksEntity);
                    oldSubjectMarkComponentIdOfMarksEntityListMarksEntity = em.merge(oldSubjectMarkComponentIdOfMarksEntityListMarksEntity);
                }
            }
            em.getTransaction().commit();
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
            List<MarksEntity> marksEntityListOld = persistentSubjectMarkComponentEntity.getMarksEntityList();
            List<MarksEntity> marksEntityListNew = subjectMarkComponentEntity.getMarksEntityList();
            if (markComponentIdNew != null) {
                markComponentIdNew = em.getReference(markComponentIdNew.getClass(), markComponentIdNew.getId());
                subjectMarkComponentEntity.setMarkComponentId(markComponentIdNew);
            }
            if (subjectIdNew != null) {
                subjectIdNew = em.getReference(subjectIdNew.getClass(), subjectIdNew.getId());
                subjectMarkComponentEntity.setSubjectId(subjectIdNew);
            }
            List<MarksEntity> attachedMarksEntityListNew = new ArrayList<MarksEntity>();
            for (MarksEntity marksEntityListNewMarksEntityToAttach : marksEntityListNew) {
                marksEntityListNewMarksEntityToAttach = em.getReference(marksEntityListNewMarksEntityToAttach.getClass(), marksEntityListNewMarksEntityToAttach.getId());
                attachedMarksEntityListNew.add(marksEntityListNewMarksEntityToAttach);
            }
            marksEntityListNew = attachedMarksEntityListNew;
            subjectMarkComponentEntity.setMarksEntityList(marksEntityListNew);
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
            for (MarksEntity marksEntityListOldMarksEntity : marksEntityListOld) {
                if (!marksEntityListNew.contains(marksEntityListOldMarksEntity)) {
                    marksEntityListOldMarksEntity.setSubjectMarkComponentId(null);
                    marksEntityListOldMarksEntity = em.merge(marksEntityListOldMarksEntity);
                }
            }
            for (MarksEntity marksEntityListNewMarksEntity : marksEntityListNew) {
                if (!marksEntityListOld.contains(marksEntityListNewMarksEntity)) {
                    SubjectMarkComponentEntity oldSubjectMarkComponentIdOfMarksEntityListNewMarksEntity = marksEntityListNewMarksEntity.getSubjectMarkComponentId();
                    marksEntityListNewMarksEntity.setSubjectMarkComponentId(subjectMarkComponentEntity);
                    marksEntityListNewMarksEntity = em.merge(marksEntityListNewMarksEntity);
                    if (oldSubjectMarkComponentIdOfMarksEntityListNewMarksEntity != null && !oldSubjectMarkComponentIdOfMarksEntityListNewMarksEntity.equals(subjectMarkComponentEntity)) {
                        oldSubjectMarkComponentIdOfMarksEntityListNewMarksEntity.getMarksEntityList().remove(marksEntityListNewMarksEntity);
                        oldSubjectMarkComponentIdOfMarksEntityListNewMarksEntity = em.merge(oldSubjectMarkComponentIdOfMarksEntityListNewMarksEntity);
                    }
                }
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
            List<MarksEntity> marksEntityList = subjectMarkComponentEntity.getMarksEntityList();
            for (MarksEntity marksEntityListMarksEntity : marksEntityList) {
                marksEntityListMarksEntity.setSubjectMarkComponentId(null);
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
