/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.entities.MarkComponentEntity;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.SubjectMarkComponentEntity;
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
public class MarkComponentEntityJpaController implements Serializable {

    public MarkComponentEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(MarkComponentEntity markComponentEntity) throws PreexistingEntityException, Exception {
        if (markComponentEntity.getSubjectMarkComponentEntityList() == null) {
            markComponentEntity.setSubjectMarkComponentEntityList(new ArrayList<SubjectMarkComponentEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<SubjectMarkComponentEntity> attachedSubjectMarkComponentEntityList = new ArrayList<SubjectMarkComponentEntity>();
            for (SubjectMarkComponentEntity subjectMarkComponentEntityListSubjectMarkComponentEntityToAttach : markComponentEntity.getSubjectMarkComponentEntityList()) {
                subjectMarkComponentEntityListSubjectMarkComponentEntityToAttach = em.getReference(subjectMarkComponentEntityListSubjectMarkComponentEntityToAttach.getClass(), subjectMarkComponentEntityListSubjectMarkComponentEntityToAttach.getId());
                attachedSubjectMarkComponentEntityList.add(subjectMarkComponentEntityListSubjectMarkComponentEntityToAttach);
            }
            markComponentEntity.setSubjectMarkComponentEntityList(attachedSubjectMarkComponentEntityList);
            em.persist(markComponentEntity);
            for (SubjectMarkComponentEntity subjectMarkComponentEntityListSubjectMarkComponentEntity : markComponentEntity.getSubjectMarkComponentEntityList()) {
                MarkComponentEntity oldMarkComponentIdOfSubjectMarkComponentEntityListSubjectMarkComponentEntity = subjectMarkComponentEntityListSubjectMarkComponentEntity.getMarkComponentId();
                subjectMarkComponentEntityListSubjectMarkComponentEntity.setMarkComponentId(markComponentEntity);
                subjectMarkComponentEntityListSubjectMarkComponentEntity = em.merge(subjectMarkComponentEntityListSubjectMarkComponentEntity);
                if (oldMarkComponentIdOfSubjectMarkComponentEntityListSubjectMarkComponentEntity != null) {
                    oldMarkComponentIdOfSubjectMarkComponentEntityListSubjectMarkComponentEntity.getSubjectMarkComponentEntityList().remove(subjectMarkComponentEntityListSubjectMarkComponentEntity);
                    oldMarkComponentIdOfSubjectMarkComponentEntityListSubjectMarkComponentEntity = em.merge(oldMarkComponentIdOfSubjectMarkComponentEntityListSubjectMarkComponentEntity);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMarkComponentEntity(markComponentEntity.getId()) != null) {
                throw new PreexistingEntityException("MarkComponentEntity " + markComponentEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(MarkComponentEntity markComponentEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MarkComponentEntity persistentMarkComponentEntity = em.find(MarkComponentEntity.class, markComponentEntity.getId());
            List<SubjectMarkComponentEntity> subjectMarkComponentEntityListOld = persistentMarkComponentEntity.getSubjectMarkComponentEntityList();
            List<SubjectMarkComponentEntity> subjectMarkComponentEntityListNew = markComponentEntity.getSubjectMarkComponentEntityList();
            List<SubjectMarkComponentEntity> attachedSubjectMarkComponentEntityListNew = new ArrayList<SubjectMarkComponentEntity>();
            for (SubjectMarkComponentEntity subjectMarkComponentEntityListNewSubjectMarkComponentEntityToAttach : subjectMarkComponentEntityListNew) {
                subjectMarkComponentEntityListNewSubjectMarkComponentEntityToAttach = em.getReference(subjectMarkComponentEntityListNewSubjectMarkComponentEntityToAttach.getClass(), subjectMarkComponentEntityListNewSubjectMarkComponentEntityToAttach.getId());
                attachedSubjectMarkComponentEntityListNew.add(subjectMarkComponentEntityListNewSubjectMarkComponentEntityToAttach);
            }
            subjectMarkComponentEntityListNew = attachedSubjectMarkComponentEntityListNew;
            markComponentEntity.setSubjectMarkComponentEntityList(subjectMarkComponentEntityListNew);
            markComponentEntity = em.merge(markComponentEntity);
            for (SubjectMarkComponentEntity subjectMarkComponentEntityListOldSubjectMarkComponentEntity : subjectMarkComponentEntityListOld) {
                if (!subjectMarkComponentEntityListNew.contains(subjectMarkComponentEntityListOldSubjectMarkComponentEntity)) {
                    subjectMarkComponentEntityListOldSubjectMarkComponentEntity.setMarkComponentId(null);
                    subjectMarkComponentEntityListOldSubjectMarkComponentEntity = em.merge(subjectMarkComponentEntityListOldSubjectMarkComponentEntity);
                }
            }
            for (SubjectMarkComponentEntity subjectMarkComponentEntityListNewSubjectMarkComponentEntity : subjectMarkComponentEntityListNew) {
                if (!subjectMarkComponentEntityListOld.contains(subjectMarkComponentEntityListNewSubjectMarkComponentEntity)) {
                    MarkComponentEntity oldMarkComponentIdOfSubjectMarkComponentEntityListNewSubjectMarkComponentEntity = subjectMarkComponentEntityListNewSubjectMarkComponentEntity.getMarkComponentId();
                    subjectMarkComponentEntityListNewSubjectMarkComponentEntity.setMarkComponentId(markComponentEntity);
                    subjectMarkComponentEntityListNewSubjectMarkComponentEntity = em.merge(subjectMarkComponentEntityListNewSubjectMarkComponentEntity);
                    if (oldMarkComponentIdOfSubjectMarkComponentEntityListNewSubjectMarkComponentEntity != null && !oldMarkComponentIdOfSubjectMarkComponentEntityListNewSubjectMarkComponentEntity.equals(markComponentEntity)) {
                        oldMarkComponentIdOfSubjectMarkComponentEntityListNewSubjectMarkComponentEntity.getSubjectMarkComponentEntityList().remove(subjectMarkComponentEntityListNewSubjectMarkComponentEntity);
                        oldMarkComponentIdOfSubjectMarkComponentEntityListNewSubjectMarkComponentEntity = em.merge(oldMarkComponentIdOfSubjectMarkComponentEntityListNewSubjectMarkComponentEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = markComponentEntity.getId();
                if (findMarkComponentEntity(id) == null) {
                    throw new NonexistentEntityException("The markComponentEntity with id " + id + " no longer exists.");
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
            MarkComponentEntity markComponentEntity;
            try {
                markComponentEntity = em.getReference(MarkComponentEntity.class, id);
                markComponentEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The markComponentEntity with id " + id + " no longer exists.", enfe);
            }
            List<SubjectMarkComponentEntity> subjectMarkComponentEntityList = markComponentEntity.getSubjectMarkComponentEntityList();
            for (SubjectMarkComponentEntity subjectMarkComponentEntityListSubjectMarkComponentEntity : subjectMarkComponentEntityList) {
                subjectMarkComponentEntityListSubjectMarkComponentEntity.setMarkComponentId(null);
                subjectMarkComponentEntityListSubjectMarkComponentEntity = em.merge(subjectMarkComponentEntityListSubjectMarkComponentEntity);
            }
            em.remove(markComponentEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<MarkComponentEntity> findMarkComponentEntityEntities() {
        return findMarkComponentEntityEntities(true, -1, -1);
    }

    public List<MarkComponentEntity> findMarkComponentEntityEntities(int maxResults, int firstResult) {
        return findMarkComponentEntityEntities(false, maxResults, firstResult);
    }

    private List<MarkComponentEntity> findMarkComponentEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(MarkComponentEntity.class));
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

    public MarkComponentEntity findMarkComponentEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(MarkComponentEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getMarkComponentEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<MarkComponentEntity> rt = cq.from(MarkComponentEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
