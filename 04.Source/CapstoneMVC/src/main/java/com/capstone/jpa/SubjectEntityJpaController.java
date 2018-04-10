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

import com.capstone.entities.*;

import java.util.ArrayList;
import java.util.List;

import com.capstone.jpa.exceptions.IllegalOrphanException;
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
        if (subjectEntity.getEmpCompetenceEntityList() == null) {
            subjectEntity.setEmpCompetenceEntityList(new ArrayList<EmpCompetenceEntity>());
        }
        if (subjectEntity.getSubjectCurriculumEntityList() == null) {
            subjectEntity.setSubjectCurriculumEntityList(new ArrayList<SubjectCurriculumEntity>());
        }
        if (subjectEntity.getSubjectMarkComponentEntityList() == null) {
            subjectEntity.setSubjectMarkComponentEntityList(new ArrayList<SubjectMarkComponentEntity>());
        }

        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            PrequisiteEntity prequisiteEntity = subjectEntity.getPrequisiteEntity();
            if (prequisiteEntity != null) {
                prequisiteEntity = em.getReference(prequisiteEntity.getClass(), prequisiteEntity.getSubjectId());
                subjectEntity.setPrequisiteEntity(prequisiteEntity);
            }
            DepartmentEntity departmentId = subjectEntity.getDepartmentId();
            if (departmentId != null) {
                departmentId = em.getReference(departmentId.getClass(), departmentId.getDeptId());
                subjectEntity.setDepartmentId(departmentId);
            }
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
            List<EmpCompetenceEntity> attachedEmpCompetenceEntityList = new ArrayList<EmpCompetenceEntity>();
            for (EmpCompetenceEntity empCompetenceEntityListEmpCompetenceEntityToAttach : subjectEntity.getEmpCompetenceEntityList()) {
                empCompetenceEntityListEmpCompetenceEntityToAttach = em.getReference(empCompetenceEntityListEmpCompetenceEntityToAttach.getClass(), empCompetenceEntityListEmpCompetenceEntityToAttach.getId());
                attachedEmpCompetenceEntityList.add(empCompetenceEntityListEmpCompetenceEntityToAttach);
            }
            subjectEntity.setEmpCompetenceEntityList(attachedEmpCompetenceEntityList);
            List<SubjectCurriculumEntity> attachedSubjectCurriculumEntityList = new ArrayList<SubjectCurriculumEntity>();
            for (SubjectCurriculumEntity subjectCurriculumEntityListSubjectCurriculumEntityToAttach : subjectEntity.getSubjectCurriculumEntityList()) {
                subjectCurriculumEntityListSubjectCurriculumEntityToAttach = em.getReference(subjectCurriculumEntityListSubjectCurriculumEntityToAttach.getClass(), subjectCurriculumEntityListSubjectCurriculumEntityToAttach.getId());
                attachedSubjectCurriculumEntityList.add(subjectCurriculumEntityListSubjectCurriculumEntityToAttach);
            }
            subjectEntity.setSubjectCurriculumEntityList(attachedSubjectCurriculumEntityList);
            List<SubjectMarkComponentEntity> attachedSubjectMarkComponentEntityList = new ArrayList<SubjectMarkComponentEntity>();
            for (SubjectMarkComponentEntity subjectMarkComponentEntityListSubjectMarkComponentEntityToAttach : subjectEntity.getSubjectMarkComponentEntityList()) {
                subjectMarkComponentEntityListSubjectMarkComponentEntityToAttach = em.getReference(subjectMarkComponentEntityListSubjectMarkComponentEntityToAttach.getClass(), subjectMarkComponentEntityListSubjectMarkComponentEntityToAttach.getId());
                attachedSubjectMarkComponentEntityList.add(subjectMarkComponentEntityListSubjectMarkComponentEntityToAttach);
            }
            subjectEntity.setSubjectMarkComponentEntityList(attachedSubjectMarkComponentEntityList);

            em.persist(subjectEntity);
            if (prequisiteEntity != null) {
                SubjectEntity oldSubjectEntityOfPrequisiteEntity = prequisiteEntity.getSubjectEntity();
                if (oldSubjectEntityOfPrequisiteEntity != null) {
                    oldSubjectEntityOfPrequisiteEntity.setPrequisiteEntity(null);
                    oldSubjectEntityOfPrequisiteEntity = em.merge(oldSubjectEntityOfPrequisiteEntity);
                }
                prequisiteEntity.setSubjectEntity(subjectEntity);
                prequisiteEntity = em.merge(prequisiteEntity);
            }
            if (departmentId != null) {
                departmentId.getSubjectEntityList().add(subjectEntity);
                departmentId = em.merge(departmentId);
            }
            for (SubjectEntity subjectEntityListSubjectEntity : subjectEntity.getSubjectEntityList()) {
                subjectEntityListSubjectEntity.getSubjectEntityList().add(subjectEntity);
                subjectEntityListSubjectEntity = em.merge(subjectEntityListSubjectEntity);
            }
            for (SubjectEntity subjectEntityList1SubjectEntity : subjectEntity.getSubjectEntityList1()) {
                subjectEntityList1SubjectEntity.getSubjectEntityList().add(subjectEntity);
                subjectEntityList1SubjectEntity = em.merge(subjectEntityList1SubjectEntity);
            }
            for (EmpCompetenceEntity empCompetenceEntityListEmpCompetenceEntity : subjectEntity.getEmpCompetenceEntityList()) {
                SubjectEntity oldSubjectIdOfEmpCompetenceEntityListEmpCompetenceEntity = empCompetenceEntityListEmpCompetenceEntity.getSubjectId();
                empCompetenceEntityListEmpCompetenceEntity.setSubjectId(subjectEntity);
                empCompetenceEntityListEmpCompetenceEntity = em.merge(empCompetenceEntityListEmpCompetenceEntity);
                if (oldSubjectIdOfEmpCompetenceEntityListEmpCompetenceEntity != null) {
                    oldSubjectIdOfEmpCompetenceEntityListEmpCompetenceEntity.getEmpCompetenceEntityList().remove(empCompetenceEntityListEmpCompetenceEntity);
                    oldSubjectIdOfEmpCompetenceEntityListEmpCompetenceEntity = em.merge(oldSubjectIdOfEmpCompetenceEntityListEmpCompetenceEntity);
                }
            }
            for (SubjectCurriculumEntity subjectCurriculumEntityListSubjectCurriculumEntity : subjectEntity.getSubjectCurriculumEntityList()) {
                SubjectEntity oldSubjectIdOfSubjectCurriculumEntityListSubjectCurriculumEntity = subjectCurriculumEntityListSubjectCurriculumEntity.getSubjectId();
                subjectCurriculumEntityListSubjectCurriculumEntity.setSubjectId(subjectEntity);
                subjectCurriculumEntityListSubjectCurriculumEntity = em.merge(subjectCurriculumEntityListSubjectCurriculumEntity);
                if (oldSubjectIdOfSubjectCurriculumEntityListSubjectCurriculumEntity != null) {
                    oldSubjectIdOfSubjectCurriculumEntityListSubjectCurriculumEntity.getSubjectCurriculumEntityList().remove(subjectCurriculumEntityListSubjectCurriculumEntity);
                    oldSubjectIdOfSubjectCurriculumEntityListSubjectCurriculumEntity = em.merge(oldSubjectIdOfSubjectCurriculumEntityListSubjectCurriculumEntity);
                }
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

    public void edit(SubjectEntity subjectEntity) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectEntity persistentSubjectEntity = em.find(SubjectEntity.class, subjectEntity.getId());
            PrequisiteEntity prequisiteEntityOld = persistentSubjectEntity.getPrequisiteEntity();
            PrequisiteEntity prequisiteEntityNew = subjectEntity.getPrequisiteEntity();
            DepartmentEntity departmentIdOld = persistentSubjectEntity.getDepartmentId();
            DepartmentEntity departmentIdNew = subjectEntity.getDepartmentId();
            List<SubjectEntity> subjectEntityListOld = persistentSubjectEntity.getSubjectEntityList();
            List<SubjectEntity> subjectEntityListNew = subjectEntity.getSubjectEntityList();
            List<SubjectEntity> subjectEntityList1Old = persistentSubjectEntity.getSubjectEntityList1();
            List<SubjectEntity> subjectEntityList1New = subjectEntity.getSubjectEntityList1();
            List<EmpCompetenceEntity> empCompetenceEntityListOld = persistentSubjectEntity.getEmpCompetenceEntityList();
            List<EmpCompetenceEntity> empCompetenceEntityListNew = subjectEntity.getEmpCompetenceEntityList();
            List<SubjectCurriculumEntity> subjectCurriculumEntityListOld = persistentSubjectEntity.getSubjectCurriculumEntityList();
            List<SubjectCurriculumEntity> subjectCurriculumEntityListNew = subjectEntity.getSubjectCurriculumEntityList();
            List<SubjectMarkComponentEntity> subjectMarkComponentEntityListOld = persistentSubjectEntity.getSubjectMarkComponentEntityList();
            List<SubjectMarkComponentEntity> subjectMarkComponentEntityListNew = subjectEntity.getSubjectMarkComponentEntityList();
            List<String> illegalOrphanMessages = null;
            if (prequisiteEntityOld != null && !prequisiteEntityOld.equals(prequisiteEntityNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain PrequisiteEntity " + prequisiteEntityOld + " since its subjectEntity field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (prequisiteEntityNew != null) {
                prequisiteEntityNew = em.getReference(prequisiteEntityNew.getClass(), prequisiteEntityNew.getSubjectId());
                subjectEntity.setPrequisiteEntity(prequisiteEntityNew);
            }
            if (departmentIdNew != null) {
                departmentIdNew = em.getReference(departmentIdNew.getClass(), departmentIdNew.getDeptId());
                subjectEntity.setDepartmentId(departmentIdNew);
            }
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
            List<EmpCompetenceEntity> attachedEmpCompetenceEntityListNew = new ArrayList<EmpCompetenceEntity>();
            for (EmpCompetenceEntity empCompetenceEntityListNewEmpCompetenceEntityToAttach : empCompetenceEntityListNew) {
                empCompetenceEntityListNewEmpCompetenceEntityToAttach = em.getReference(empCompetenceEntityListNewEmpCompetenceEntityToAttach.getClass(), empCompetenceEntityListNewEmpCompetenceEntityToAttach.getId());
                attachedEmpCompetenceEntityListNew.add(empCompetenceEntityListNewEmpCompetenceEntityToAttach);
            }
            empCompetenceEntityListNew = attachedEmpCompetenceEntityListNew;
            subjectEntity.setEmpCompetenceEntityList(empCompetenceEntityListNew);
            List<SubjectCurriculumEntity> attachedSubjectCurriculumEntityListNew = new ArrayList<SubjectCurriculumEntity>();
            for (SubjectCurriculumEntity subjectCurriculumEntityListNewSubjectCurriculumEntityToAttach : subjectCurriculumEntityListNew) {
                subjectCurriculumEntityListNewSubjectCurriculumEntityToAttach = em.getReference(subjectCurriculumEntityListNewSubjectCurriculumEntityToAttach.getClass(), subjectCurriculumEntityListNewSubjectCurriculumEntityToAttach.getId());
                attachedSubjectCurriculumEntityListNew.add(subjectCurriculumEntityListNewSubjectCurriculumEntityToAttach);
            }
            subjectCurriculumEntityListNew = attachedSubjectCurriculumEntityListNew;
            subjectEntity.setSubjectCurriculumEntityList(subjectCurriculumEntityListNew);
            List<SubjectMarkComponentEntity> attachedSubjectMarkComponentEntityListNew = new ArrayList<SubjectMarkComponentEntity>();
            for (SubjectMarkComponentEntity subjectMarkComponentEntityListNewSubjectMarkComponentEntityToAttach : subjectMarkComponentEntityListNew) {
                subjectMarkComponentEntityListNewSubjectMarkComponentEntityToAttach = em.getReference(subjectMarkComponentEntityListNewSubjectMarkComponentEntityToAttach.getClass(), subjectMarkComponentEntityListNewSubjectMarkComponentEntityToAttach.getId());
                attachedSubjectMarkComponentEntityListNew.add(subjectMarkComponentEntityListNewSubjectMarkComponentEntityToAttach);
            }
            subjectMarkComponentEntityListNew = attachedSubjectMarkComponentEntityListNew;
            subjectEntity.setSubjectMarkComponentEntityList(subjectMarkComponentEntityListNew);

            subjectEntity = em.merge(subjectEntity);
            if (prequisiteEntityNew != null && !prequisiteEntityNew.equals(prequisiteEntityOld)) {
                SubjectEntity oldSubjectEntityOfPrequisiteEntity = prequisiteEntityNew.getSubjectEntity();
                if (oldSubjectEntityOfPrequisiteEntity != null) {
                    oldSubjectEntityOfPrequisiteEntity.setPrequisiteEntity(null);
                    oldSubjectEntityOfPrequisiteEntity = em.merge(oldSubjectEntityOfPrequisiteEntity);
                }
                prequisiteEntityNew.setSubjectEntity(subjectEntity);
                prequisiteEntityNew = em.merge(prequisiteEntityNew);
            }
            if (departmentIdOld != null && !departmentIdOld.equals(departmentIdNew)) {
                departmentIdOld.getSubjectEntityList().remove(subjectEntity);
                departmentIdOld = em.merge(departmentIdOld);
            }
            if (departmentIdNew != null && !departmentIdNew.equals(departmentIdOld)) {
                departmentIdNew.getSubjectEntityList().add(subjectEntity);
                departmentIdNew = em.merge(departmentIdNew);
            }
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
            for (EmpCompetenceEntity empCompetenceEntityListOldEmpCompetenceEntity : empCompetenceEntityListOld) {
                if (!empCompetenceEntityListNew.contains(empCompetenceEntityListOldEmpCompetenceEntity)) {
                    empCompetenceEntityListOldEmpCompetenceEntity.setSubjectId(null);
                    empCompetenceEntityListOldEmpCompetenceEntity = em.merge(empCompetenceEntityListOldEmpCompetenceEntity);
                }
            }
            for (EmpCompetenceEntity empCompetenceEntityListNewEmpCompetenceEntity : empCompetenceEntityListNew) {
                if (!empCompetenceEntityListOld.contains(empCompetenceEntityListNewEmpCompetenceEntity)) {
                    SubjectEntity oldSubjectIdOfEmpCompetenceEntityListNewEmpCompetenceEntity = empCompetenceEntityListNewEmpCompetenceEntity.getSubjectId();
                    empCompetenceEntityListNewEmpCompetenceEntity.setSubjectId(subjectEntity);
                    empCompetenceEntityListNewEmpCompetenceEntity = em.merge(empCompetenceEntityListNewEmpCompetenceEntity);
                    if (oldSubjectIdOfEmpCompetenceEntityListNewEmpCompetenceEntity != null && !oldSubjectIdOfEmpCompetenceEntityListNewEmpCompetenceEntity.equals(subjectEntity)) {
                        oldSubjectIdOfEmpCompetenceEntityListNewEmpCompetenceEntity.getEmpCompetenceEntityList().remove(empCompetenceEntityListNewEmpCompetenceEntity);
                        oldSubjectIdOfEmpCompetenceEntityListNewEmpCompetenceEntity = em.merge(oldSubjectIdOfEmpCompetenceEntityListNewEmpCompetenceEntity);
                    }
                }
            }
            for (SubjectCurriculumEntity subjectCurriculumEntityListOldSubjectCurriculumEntity : subjectCurriculumEntityListOld) {
                if (!subjectCurriculumEntityListNew.contains(subjectCurriculumEntityListOldSubjectCurriculumEntity)) {
                    subjectCurriculumEntityListOldSubjectCurriculumEntity.setSubjectId(null);
                    subjectCurriculumEntityListOldSubjectCurriculumEntity = em.merge(subjectCurriculumEntityListOldSubjectCurriculumEntity);
                }
            }
            for (SubjectCurriculumEntity subjectCurriculumEntityListNewSubjectCurriculumEntity : subjectCurriculumEntityListNew) {
                if (!subjectCurriculumEntityListOld.contains(subjectCurriculumEntityListNewSubjectCurriculumEntity)) {
                    SubjectEntity oldSubjectIdOfSubjectCurriculumEntityListNewSubjectCurriculumEntity = subjectCurriculumEntityListNewSubjectCurriculumEntity.getSubjectId();
                    subjectCurriculumEntityListNewSubjectCurriculumEntity.setSubjectId(subjectEntity);
                    subjectCurriculumEntityListNewSubjectCurriculumEntity = em.merge(subjectCurriculumEntityListNewSubjectCurriculumEntity);
                    if (oldSubjectIdOfSubjectCurriculumEntityListNewSubjectCurriculumEntity != null && !oldSubjectIdOfSubjectCurriculumEntityListNewSubjectCurriculumEntity.equals(subjectEntity)) {
                        oldSubjectIdOfSubjectCurriculumEntityListNewSubjectCurriculumEntity.getSubjectCurriculumEntityList().remove(subjectCurriculumEntityListNewSubjectCurriculumEntity);
                        oldSubjectIdOfSubjectCurriculumEntityListNewSubjectCurriculumEntity = em.merge(oldSubjectIdOfSubjectCurriculumEntityListNewSubjectCurriculumEntity);
                    }
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
            PrequisiteEntity prequisiteEntityOrphanCheck = subjectEntity.getPrequisiteEntity();
            if (prequisiteEntityOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This SubjectEntity (" + subjectEntity + ") cannot be destroyed since the PrequisiteEntity " + prequisiteEntityOrphanCheck + " in its prequisiteEntity field has a non-nullable subjectEntity field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            DepartmentEntity departmentId = subjectEntity.getDepartmentId();
            if (departmentId != null) {
                departmentId.getSubjectEntityList().remove(subjectEntity);
                departmentId = em.merge(departmentId);
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
            List<EmpCompetenceEntity> empCompetenceEntityList = subjectEntity.getEmpCompetenceEntityList();
            for (EmpCompetenceEntity empCompetenceEntityListEmpCompetenceEntity : empCompetenceEntityList) {
                empCompetenceEntityListEmpCompetenceEntity.setSubjectId(null);
                empCompetenceEntityListEmpCompetenceEntity = em.merge(empCompetenceEntityListEmpCompetenceEntity);
            }
            List<SubjectCurriculumEntity> subjectCurriculumEntityList = subjectEntity.getSubjectCurriculumEntityList();
            for (SubjectCurriculumEntity subjectCurriculumEntityListSubjectCurriculumEntity : subjectCurriculumEntityList) {
                subjectCurriculumEntityListSubjectCurriculumEntity.setSubjectId(null);
                subjectCurriculumEntityListSubjectCurriculumEntity = em.merge(subjectCurriculumEntityListSubjectCurriculumEntity);
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
