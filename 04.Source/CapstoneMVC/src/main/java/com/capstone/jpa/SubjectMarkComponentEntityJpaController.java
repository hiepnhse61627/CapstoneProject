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

    public void create(SubjectMarkComponentEntity subjectMarkComponentEntity) throws PreexistingEntityException, Exception {
        if (subjectMarkComponentEntity.getMarksList() == null) {
            subjectMarkComponentEntity.setMarksList(new ArrayList<MarksEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectEntity subject = subjectMarkComponentEntity.getSubject();
            if (subject != null) {
                subject = em.getReference(subject.getClass(), subject.getId());
                subjectMarkComponentEntity.setSubject(subject);
            }
            List<MarksEntity> attachedMarksList = new ArrayList<MarksEntity>();
            for (MarksEntity marksListMarksEntityToAttach : subjectMarkComponentEntity.getMarksList()) {
                marksListMarksEntityToAttach = em.getReference(marksListMarksEntityToAttach.getClass(), marksListMarksEntityToAttach.getId());
                attachedMarksList.add(marksListMarksEntityToAttach);
            }
            subjectMarkComponentEntity.setMarksList(attachedMarksList);
            em.persist(subjectMarkComponentEntity);
            if (subject != null) {
                SubjectMarkComponentEntity oldSubjectMarkComponentOfSubject = subject.getSubjectMarkComponent();
                if (oldSubjectMarkComponentOfSubject != null) {
                    oldSubjectMarkComponentOfSubject.setSubject(null);
                    oldSubjectMarkComponentOfSubject = em.merge(oldSubjectMarkComponentOfSubject);
                }
                subject.setSubjectMarkComponent(subjectMarkComponentEntity);
                subject = em.merge(subject);
            }
            for (MarksEntity marksListMarksEntity : subjectMarkComponentEntity.getMarksList()) {
                SubjectMarkComponentEntity oldSubjectIdOfMarksListMarksEntity = marksListMarksEntity.getSubjectId();
                marksListMarksEntity.setSubjectId(subjectMarkComponentEntity);
                marksListMarksEntity = em.merge(marksListMarksEntity);
                if (oldSubjectIdOfMarksListMarksEntity != null) {
                    oldSubjectIdOfMarksListMarksEntity.getMarksList().remove(marksListMarksEntity);
                    oldSubjectIdOfMarksListMarksEntity = em.merge(oldSubjectIdOfMarksListMarksEntity);
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
            SubjectEntity subjectOld = persistentSubjectMarkComponentEntity.getSubject();
            SubjectEntity subjectNew = subjectMarkComponentEntity.getSubject();
            List<MarksEntity> marksListOld = persistentSubjectMarkComponentEntity.getMarksList();
            List<MarksEntity> marksListNew = subjectMarkComponentEntity.getMarksList();
            List<String> illegalOrphanMessages = null;
            if (subjectOld != null && !subjectOld.equals(subjectNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain SubjectEntity " + subjectOld + " since its subjectMarkComponent field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (subjectNew != null) {
                subjectNew = em.getReference(subjectNew.getClass(), subjectNew.getId());
                subjectMarkComponentEntity.setSubject(subjectNew);
            }
            List<MarksEntity> attachedMarksListNew = new ArrayList<MarksEntity>();
            for (MarksEntity marksListNewMarksEntityToAttach : marksListNew) {
                marksListNewMarksEntityToAttach = em.getReference(marksListNewMarksEntityToAttach.getClass(), marksListNewMarksEntityToAttach.getId());
                attachedMarksListNew.add(marksListNewMarksEntityToAttach);
            }
            marksListNew = attachedMarksListNew;
            subjectMarkComponentEntity.setMarksList(marksListNew);
            subjectMarkComponentEntity = em.merge(subjectMarkComponentEntity);
            if (subjectNew != null && !subjectNew.equals(subjectOld)) {
                SubjectMarkComponentEntity oldSubjectMarkComponentOfSubject = subjectNew.getSubjectMarkComponent();
                if (oldSubjectMarkComponentOfSubject != null) {
                    oldSubjectMarkComponentOfSubject.setSubject(null);
                    oldSubjectMarkComponentOfSubject = em.merge(oldSubjectMarkComponentOfSubject);
                }
                subjectNew.setSubjectMarkComponent(subjectMarkComponentEntity);
                subjectNew = em.merge(subjectNew);
            }
            for (MarksEntity marksListOldMarksEntity : marksListOld) {
                if (!marksListNew.contains(marksListOldMarksEntity)) {
                    marksListOldMarksEntity.setSubjectId(null);
                    marksListOldMarksEntity = em.merge(marksListOldMarksEntity);
                }
            }
            for (MarksEntity marksListNewMarksEntity : marksListNew) {
                if (!marksListOld.contains(marksListNewMarksEntity)) {
                    SubjectMarkComponentEntity oldSubjectIdOfMarksListNewMarksEntity = marksListNewMarksEntity.getSubjectId();
                    marksListNewMarksEntity.setSubjectId(subjectMarkComponentEntity);
                    marksListNewMarksEntity = em.merge(marksListNewMarksEntity);
                    if (oldSubjectIdOfMarksListNewMarksEntity != null && !oldSubjectIdOfMarksListNewMarksEntity.equals(subjectMarkComponentEntity)) {
                        oldSubjectIdOfMarksListNewMarksEntity.getMarksList().remove(marksListNewMarksEntity);
                        oldSubjectIdOfMarksListNewMarksEntity = em.merge(oldSubjectIdOfMarksListNewMarksEntity);
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
            SubjectEntity subjectOrphanCheck = subjectMarkComponentEntity.getSubject();
            if (subjectOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This SubjectMarkComponentEntity (" + subjectMarkComponentEntity + ") cannot be destroyed since the SubjectEntity " + subjectOrphanCheck + " in its subject field has a non-nullable subjectMarkComponent field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<MarksEntity> marksList = subjectMarkComponentEntity.getMarksList();
            for (MarksEntity marksListMarksEntity : marksList) {
                marksListMarksEntity.setSubjectId(null);
                marksListMarksEntity = em.merge(marksListMarksEntity);
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
