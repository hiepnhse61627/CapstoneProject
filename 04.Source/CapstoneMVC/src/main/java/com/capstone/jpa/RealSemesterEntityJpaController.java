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
import com.capstone.entities.MarksEntity;
import com.capstone.entities.RealSemesterEntity;
import java.util.ArrayList;
import java.util.List;
import com.capstone.entities.StudentStatusEntity;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author hiepnhse61627
 */
public class RealSemesterEntityJpaController implements Serializable {

    public RealSemesterEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(RealSemesterEntity realSemesterEntity) {
        if (realSemesterEntity.getMarksEntityList() == null) {
            realSemesterEntity.setMarksEntityList(new ArrayList<MarksEntity>());
        }
        if (realSemesterEntity.getStudentStatusEntityList() == null) {
            realSemesterEntity.setStudentStatusEntityList(new ArrayList<StudentStatusEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<MarksEntity> attachedMarksEntityList = new ArrayList<MarksEntity>();
            for (MarksEntity marksEntityListMarksEntityToAttach : realSemesterEntity.getMarksEntityList()) {
                marksEntityListMarksEntityToAttach = em.getReference(marksEntityListMarksEntityToAttach.getClass(), marksEntityListMarksEntityToAttach.getId());
                attachedMarksEntityList.add(marksEntityListMarksEntityToAttach);
            }
            realSemesterEntity.setMarksEntityList(attachedMarksEntityList);
            List<StudentStatusEntity> attachedStudentStatusEntityList = new ArrayList<StudentStatusEntity>();
            for (StudentStatusEntity studentStatusEntityListStudentStatusEntityToAttach : realSemesterEntity.getStudentStatusEntityList()) {
                studentStatusEntityListStudentStatusEntityToAttach = em.getReference(studentStatusEntityListStudentStatusEntityToAttach.getClass(), studentStatusEntityListStudentStatusEntityToAttach.getId());
                attachedStudentStatusEntityList.add(studentStatusEntityListStudentStatusEntityToAttach);
            }
            realSemesterEntity.setStudentStatusEntityList(attachedStudentStatusEntityList);
            em.persist(realSemesterEntity);
            for (MarksEntity marksEntityListMarksEntity : realSemesterEntity.getMarksEntityList()) {
                RealSemesterEntity oldSemesterIdOfMarksEntityListMarksEntity = marksEntityListMarksEntity.getSemesterId();
                marksEntityListMarksEntity.setSemesterId(realSemesterEntity);
                marksEntityListMarksEntity = em.merge(marksEntityListMarksEntity);
                if (oldSemesterIdOfMarksEntityListMarksEntity != null) {
                    oldSemesterIdOfMarksEntityListMarksEntity.getMarksEntityList().remove(marksEntityListMarksEntity);
                    oldSemesterIdOfMarksEntityListMarksEntity = em.merge(oldSemesterIdOfMarksEntityListMarksEntity);
                }
            }
            for (StudentStatusEntity studentStatusEntityListStudentStatusEntity : realSemesterEntity.getStudentStatusEntityList()) {
                RealSemesterEntity oldSemesterIdOfStudentStatusEntityListStudentStatusEntity = studentStatusEntityListStudentStatusEntity.getSemesterId();
                studentStatusEntityListStudentStatusEntity.setSemesterId(realSemesterEntity);
                studentStatusEntityListStudentStatusEntity = em.merge(studentStatusEntityListStudentStatusEntity);
                if (oldSemesterIdOfStudentStatusEntityListStudentStatusEntity != null) {
                    oldSemesterIdOfStudentStatusEntityListStudentStatusEntity.getStudentStatusEntityList().remove(studentStatusEntityListStudentStatusEntity);
                    oldSemesterIdOfStudentStatusEntityListStudentStatusEntity = em.merge(oldSemesterIdOfStudentStatusEntityListStudentStatusEntity);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(RealSemesterEntity realSemesterEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            RealSemesterEntity persistentRealSemesterEntity = em.find(RealSemesterEntity.class, realSemesterEntity.getId());
            List<MarksEntity> marksEntityListOld = persistentRealSemesterEntity.getMarksEntityList();
            List<MarksEntity> marksEntityListNew = realSemesterEntity.getMarksEntityList();
            List<StudentStatusEntity> studentStatusEntityListOld = persistentRealSemesterEntity.getStudentStatusEntityList();
            List<StudentStatusEntity> studentStatusEntityListNew = realSemesterEntity.getStudentStatusEntityList();
            List<MarksEntity> attachedMarksEntityListNew = new ArrayList<MarksEntity>();
            for (MarksEntity marksEntityListNewMarksEntityToAttach : marksEntityListNew) {
                marksEntityListNewMarksEntityToAttach = em.getReference(marksEntityListNewMarksEntityToAttach.getClass(), marksEntityListNewMarksEntityToAttach.getId());
                attachedMarksEntityListNew.add(marksEntityListNewMarksEntityToAttach);
            }
            marksEntityListNew = attachedMarksEntityListNew;
            realSemesterEntity.setMarksEntityList(marksEntityListNew);
            List<StudentStatusEntity> attachedStudentStatusEntityListNew = new ArrayList<StudentStatusEntity>();
            for (StudentStatusEntity studentStatusEntityListNewStudentStatusEntityToAttach : studentStatusEntityListNew) {
                studentStatusEntityListNewStudentStatusEntityToAttach = em.getReference(studentStatusEntityListNewStudentStatusEntityToAttach.getClass(), studentStatusEntityListNewStudentStatusEntityToAttach.getId());
                attachedStudentStatusEntityListNew.add(studentStatusEntityListNewStudentStatusEntityToAttach);
            }
            studentStatusEntityListNew = attachedStudentStatusEntityListNew;
            realSemesterEntity.setStudentStatusEntityList(studentStatusEntityListNew);
            realSemesterEntity = em.merge(realSemesterEntity);
            for (MarksEntity marksEntityListOldMarksEntity : marksEntityListOld) {
                if (!marksEntityListNew.contains(marksEntityListOldMarksEntity)) {
                    marksEntityListOldMarksEntity.setSemesterId(null);
                    marksEntityListOldMarksEntity = em.merge(marksEntityListOldMarksEntity);
                }
            }
            for (MarksEntity marksEntityListNewMarksEntity : marksEntityListNew) {
                if (!marksEntityListOld.contains(marksEntityListNewMarksEntity)) {
                    RealSemesterEntity oldSemesterIdOfMarksEntityListNewMarksEntity = marksEntityListNewMarksEntity.getSemesterId();
                    marksEntityListNewMarksEntity.setSemesterId(realSemesterEntity);
                    marksEntityListNewMarksEntity = em.merge(marksEntityListNewMarksEntity);
                    if (oldSemesterIdOfMarksEntityListNewMarksEntity != null && !oldSemesterIdOfMarksEntityListNewMarksEntity.equals(realSemesterEntity)) {
                        oldSemesterIdOfMarksEntityListNewMarksEntity.getMarksEntityList().remove(marksEntityListNewMarksEntity);
                        oldSemesterIdOfMarksEntityListNewMarksEntity = em.merge(oldSemesterIdOfMarksEntityListNewMarksEntity);
                    }
                }
            }
            for (StudentStatusEntity studentStatusEntityListOldStudentStatusEntity : studentStatusEntityListOld) {
                if (!studentStatusEntityListNew.contains(studentStatusEntityListOldStudentStatusEntity)) {
                    studentStatusEntityListOldStudentStatusEntity.setSemesterId(null);
                    studentStatusEntityListOldStudentStatusEntity = em.merge(studentStatusEntityListOldStudentStatusEntity);
                }
            }
            for (StudentStatusEntity studentStatusEntityListNewStudentStatusEntity : studentStatusEntityListNew) {
                if (!studentStatusEntityListOld.contains(studentStatusEntityListNewStudentStatusEntity)) {
                    RealSemesterEntity oldSemesterIdOfStudentStatusEntityListNewStudentStatusEntity = studentStatusEntityListNewStudentStatusEntity.getSemesterId();
                    studentStatusEntityListNewStudentStatusEntity.setSemesterId(realSemesterEntity);
                    studentStatusEntityListNewStudentStatusEntity = em.merge(studentStatusEntityListNewStudentStatusEntity);
                    if (oldSemesterIdOfStudentStatusEntityListNewStudentStatusEntity != null && !oldSemesterIdOfStudentStatusEntityListNewStudentStatusEntity.equals(realSemesterEntity)) {
                        oldSemesterIdOfStudentStatusEntityListNewStudentStatusEntity.getStudentStatusEntityList().remove(studentStatusEntityListNewStudentStatusEntity);
                        oldSemesterIdOfStudentStatusEntityListNewStudentStatusEntity = em.merge(oldSemesterIdOfStudentStatusEntityListNewStudentStatusEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = realSemesterEntity.getId();
                if (findRealSemesterEntity(id) == null) {
                    throw new NonexistentEntityException("The realSemesterEntity with id " + id + " no longer exists.");
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
            RealSemesterEntity realSemesterEntity;
            try {
                realSemesterEntity = em.getReference(RealSemesterEntity.class, id);
                realSemesterEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The realSemesterEntity with id " + id + " no longer exists.", enfe);
            }
            List<MarksEntity> marksEntityList = realSemesterEntity.getMarksEntityList();
            for (MarksEntity marksEntityListMarksEntity : marksEntityList) {
                marksEntityListMarksEntity.setSemesterId(null);
                marksEntityListMarksEntity = em.merge(marksEntityListMarksEntity);
            }
            List<StudentStatusEntity> studentStatusEntityList = realSemesterEntity.getStudentStatusEntityList();
            for (StudentStatusEntity studentStatusEntityListStudentStatusEntity : studentStatusEntityList) {
                studentStatusEntityListStudentStatusEntity.setSemesterId(null);
                studentStatusEntityListStudentStatusEntity = em.merge(studentStatusEntityListStudentStatusEntity);
            }
            em.remove(realSemesterEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RealSemesterEntity> findRealSemesterEntityEntities() {
        return findRealSemesterEntityEntities(true, -1, -1);
    }

    public List<RealSemesterEntity> findRealSemesterEntityEntities(int maxResults, int firstResult) {
        return findRealSemesterEntityEntities(false, maxResults, firstResult);
    }

    private List<RealSemesterEntity> findRealSemesterEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(RealSemesterEntity.class));
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

    public RealSemesterEntity findRealSemesterEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(RealSemesterEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getRealSemesterEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<RealSemesterEntity> rt = cq.from(RealSemesterEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
