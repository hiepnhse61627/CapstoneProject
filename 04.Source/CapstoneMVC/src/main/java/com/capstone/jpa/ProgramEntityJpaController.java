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
import com.capstone.entities.GraduationConditionEntity;
import java.util.ArrayList;
import java.util.List;
import com.capstone.entities.OldRollNumberEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.entities.CurriculumEntity;
import com.capstone.entities.ProgramEntity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.capstone.jpa.exceptions.IllegalOrphanException;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

/**
 *
 * @author StormNs
 */
public class ProgramEntityJpaController implements Serializable {

    public ProgramEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ProgramEntity programEntity) throws PreexistingEntityException, Exception {
        if (programEntity.getGraduationConditionEntityList() == null) {
            programEntity.setGraduationConditionEntityList(new ArrayList<GraduationConditionEntity>());
        }
        if (programEntity.getOldRollNumberEntityList() == null) {
            programEntity.setOldRollNumberEntityList(new ArrayList<OldRollNumberEntity>());
        }
        if (programEntity.getStudentEntityList() == null) {
            programEntity.setStudentEntityList(new ArrayList<StudentEntity>());
        }
        if (programEntity.getCurriculumEntityList() == null) {
            programEntity.setCurriculumEntityList(new ArrayList<CurriculumEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<GraduationConditionEntity> attachedGraduationConditionEntityList = new ArrayList<GraduationConditionEntity>();
            for (GraduationConditionEntity graduationConditionEntityListGraduationConditionEntityToAttach : programEntity.getGraduationConditionEntityList()) {
                graduationConditionEntityListGraduationConditionEntityToAttach = em.getReference(graduationConditionEntityListGraduationConditionEntityToAttach.getClass(), graduationConditionEntityListGraduationConditionEntityToAttach.getId());
                attachedGraduationConditionEntityList.add(graduationConditionEntityListGraduationConditionEntityToAttach);
            }
            programEntity.setGraduationConditionEntityList(attachedGraduationConditionEntityList);
            List<OldRollNumberEntity> attachedOldRollNumberEntityList = new ArrayList<OldRollNumberEntity>();
            for (OldRollNumberEntity oldRollNumberEntityListOldRollNumberEntityToAttach : programEntity.getOldRollNumberEntityList()) {
                oldRollNumberEntityListOldRollNumberEntityToAttach = em.getReference(oldRollNumberEntityListOldRollNumberEntityToAttach.getClass(), oldRollNumberEntityListOldRollNumberEntityToAttach.getId());
                attachedOldRollNumberEntityList.add(oldRollNumberEntityListOldRollNumberEntityToAttach);
            }
            programEntity.setOldRollNumberEntityList(attachedOldRollNumberEntityList);
            List<StudentEntity> attachedStudentEntityList = new ArrayList<StudentEntity>();
            for (StudentEntity studentEntityListStudentEntityToAttach : programEntity.getStudentEntityList()) {
                studentEntityListStudentEntityToAttach = em.getReference(studentEntityListStudentEntityToAttach.getClass(), studentEntityListStudentEntityToAttach.getId());
                attachedStudentEntityList.add(studentEntityListStudentEntityToAttach);
            }
            programEntity.setStudentEntityList(attachedStudentEntityList);
            List<CurriculumEntity> attachedCurriculumEntityList = new ArrayList<CurriculumEntity>();
            for (CurriculumEntity curriculumEntityListCurriculumEntityToAttach : programEntity.getCurriculumEntityList()) {
                curriculumEntityListCurriculumEntityToAttach = em.getReference(curriculumEntityListCurriculumEntityToAttach.getClass(), curriculumEntityListCurriculumEntityToAttach.getId());
                attachedCurriculumEntityList.add(curriculumEntityListCurriculumEntityToAttach);
            }
            programEntity.setCurriculumEntityList(attachedCurriculumEntityList);
            em.persist(programEntity);
            for (GraduationConditionEntity graduationConditionEntityListGraduationConditionEntity : programEntity.getGraduationConditionEntityList()) {
                ProgramEntity oldProgramIdOfGraduationConditionEntityListGraduationConditionEntity = graduationConditionEntityListGraduationConditionEntity.getProgramId();
                graduationConditionEntityListGraduationConditionEntity.setProgramId(programEntity);
                graduationConditionEntityListGraduationConditionEntity = em.merge(graduationConditionEntityListGraduationConditionEntity);
                if (oldProgramIdOfGraduationConditionEntityListGraduationConditionEntity != null) {
                    oldProgramIdOfGraduationConditionEntityListGraduationConditionEntity.getGraduationConditionEntityList().remove(graduationConditionEntityListGraduationConditionEntity);
                    oldProgramIdOfGraduationConditionEntityListGraduationConditionEntity = em.merge(oldProgramIdOfGraduationConditionEntityListGraduationConditionEntity);
                }
            }
            for (OldRollNumberEntity oldRollNumberEntityListOldRollNumberEntity : programEntity.getOldRollNumberEntityList()) {
                ProgramEntity oldProgramIdOfOldRollNumberEntityListOldRollNumberEntity = oldRollNumberEntityListOldRollNumberEntity.getProgramId();
                oldRollNumberEntityListOldRollNumberEntity.setProgramId(programEntity);
                oldRollNumberEntityListOldRollNumberEntity = em.merge(oldRollNumberEntityListOldRollNumberEntity);
                if (oldProgramIdOfOldRollNumberEntityListOldRollNumberEntity != null) {
                    oldProgramIdOfOldRollNumberEntityListOldRollNumberEntity.getOldRollNumberEntityList().remove(oldRollNumberEntityListOldRollNumberEntity);
                    oldProgramIdOfOldRollNumberEntityListOldRollNumberEntity = em.merge(oldProgramIdOfOldRollNumberEntityListOldRollNumberEntity);
                }
            }
            for (StudentEntity studentEntityListStudentEntity : programEntity.getStudentEntityList()) {
                ProgramEntity oldProgramIdOfStudentEntityListStudentEntity = studentEntityListStudentEntity.getProgramId();
                studentEntityListStudentEntity.setProgramId(programEntity);
                studentEntityListStudentEntity = em.merge(studentEntityListStudentEntity);
                if (oldProgramIdOfStudentEntityListStudentEntity != null) {
                    oldProgramIdOfStudentEntityListStudentEntity.getStudentEntityList().remove(studentEntityListStudentEntity);
                    oldProgramIdOfStudentEntityListStudentEntity = em.merge(oldProgramIdOfStudentEntityListStudentEntity);
                }
            }
            for (CurriculumEntity curriculumEntityListCurriculumEntity : programEntity.getCurriculumEntityList()) {
                ProgramEntity oldProgramIdOfCurriculumEntityListCurriculumEntity = curriculumEntityListCurriculumEntity.getProgramId();
                curriculumEntityListCurriculumEntity.setProgramId(programEntity);
                curriculumEntityListCurriculumEntity = em.merge(curriculumEntityListCurriculumEntity);
                if (oldProgramIdOfCurriculumEntityListCurriculumEntity != null) {
                    oldProgramIdOfCurriculumEntityListCurriculumEntity.getCurriculumEntityList().remove(curriculumEntityListCurriculumEntity);
                    oldProgramIdOfCurriculumEntityListCurriculumEntity = em.merge(oldProgramIdOfCurriculumEntityListCurriculumEntity);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findProgramEntity(programEntity.getId()) != null) {
                throw new PreexistingEntityException("ProgramEntity " + programEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ProgramEntity programEntity) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProgramEntity persistentProgramEntity = em.find(ProgramEntity.class, programEntity.getId());
            List<GraduationConditionEntity> graduationConditionEntityListOld = persistentProgramEntity.getGraduationConditionEntityList();
            List<GraduationConditionEntity> graduationConditionEntityListNew = programEntity.getGraduationConditionEntityList();
            List<OldRollNumberEntity> oldRollNumberEntityListOld = persistentProgramEntity.getOldRollNumberEntityList();
            List<OldRollNumberEntity> oldRollNumberEntityListNew = programEntity.getOldRollNumberEntityList();
            List<StudentEntity> studentEntityListOld = persistentProgramEntity.getStudentEntityList();
            List<StudentEntity> studentEntityListNew = programEntity.getStudentEntityList();
            List<CurriculumEntity> curriculumEntityListOld = persistentProgramEntity.getCurriculumEntityList();
            List<CurriculumEntity> curriculumEntityListNew = programEntity.getCurriculumEntityList();
            List<String> illegalOrphanMessages = null;
            for (CurriculumEntity curriculumEntityListOldCurriculumEntity : curriculumEntityListOld) {
                if (!curriculumEntityListNew.contains(curriculumEntityListOldCurriculumEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain CurriculumEntity " + curriculumEntityListOldCurriculumEntity + " since its programId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<GraduationConditionEntity> attachedGraduationConditionEntityListNew = new ArrayList<GraduationConditionEntity>();
            for (GraduationConditionEntity graduationConditionEntityListNewGraduationConditionEntityToAttach : graduationConditionEntityListNew) {
                graduationConditionEntityListNewGraduationConditionEntityToAttach = em.getReference(graduationConditionEntityListNewGraduationConditionEntityToAttach.getClass(), graduationConditionEntityListNewGraduationConditionEntityToAttach.getId());
                attachedGraduationConditionEntityListNew.add(graduationConditionEntityListNewGraduationConditionEntityToAttach);
            }
            graduationConditionEntityListNew = attachedGraduationConditionEntityListNew;
            programEntity.setGraduationConditionEntityList(graduationConditionEntityListNew);
            List<OldRollNumberEntity> attachedOldRollNumberEntityListNew = new ArrayList<OldRollNumberEntity>();
            for (OldRollNumberEntity oldRollNumberEntityListNewOldRollNumberEntityToAttach : oldRollNumberEntityListNew) {
                oldRollNumberEntityListNewOldRollNumberEntityToAttach = em.getReference(oldRollNumberEntityListNewOldRollNumberEntityToAttach.getClass(), oldRollNumberEntityListNewOldRollNumberEntityToAttach.getId());
                attachedOldRollNumberEntityListNew.add(oldRollNumberEntityListNewOldRollNumberEntityToAttach);
            }
            oldRollNumberEntityListNew = attachedOldRollNumberEntityListNew;
            programEntity.setOldRollNumberEntityList(oldRollNumberEntityListNew);
            List<StudentEntity> attachedStudentEntityListNew = new ArrayList<StudentEntity>();
            for (StudentEntity studentEntityListNewStudentEntityToAttach : studentEntityListNew) {
                studentEntityListNewStudentEntityToAttach = em.getReference(studentEntityListNewStudentEntityToAttach.getClass(), studentEntityListNewStudentEntityToAttach.getId());
                attachedStudentEntityListNew.add(studentEntityListNewStudentEntityToAttach);
            }
            studentEntityListNew = attachedStudentEntityListNew;
            programEntity.setStudentEntityList(studentEntityListNew);
            List<CurriculumEntity> attachedCurriculumEntityListNew = new ArrayList<CurriculumEntity>();
            for (CurriculumEntity curriculumEntityListNewCurriculumEntityToAttach : curriculumEntityListNew) {
                curriculumEntityListNewCurriculumEntityToAttach = em.getReference(curriculumEntityListNewCurriculumEntityToAttach.getClass(), curriculumEntityListNewCurriculumEntityToAttach.getId());
                attachedCurriculumEntityListNew.add(curriculumEntityListNewCurriculumEntityToAttach);
            }
            curriculumEntityListNew = attachedCurriculumEntityListNew;
            programEntity.setCurriculumEntityList(curriculumEntityListNew);
            programEntity = em.merge(programEntity);
            for (GraduationConditionEntity graduationConditionEntityListOldGraduationConditionEntity : graduationConditionEntityListOld) {
                if (!graduationConditionEntityListNew.contains(graduationConditionEntityListOldGraduationConditionEntity)) {
                    graduationConditionEntityListOldGraduationConditionEntity.setProgramId(null);
                    graduationConditionEntityListOldGraduationConditionEntity = em.merge(graduationConditionEntityListOldGraduationConditionEntity);
                }
            }
            for (GraduationConditionEntity graduationConditionEntityListNewGraduationConditionEntity : graduationConditionEntityListNew) {
                if (!graduationConditionEntityListOld.contains(graduationConditionEntityListNewGraduationConditionEntity)) {
                    ProgramEntity oldProgramIdOfGraduationConditionEntityListNewGraduationConditionEntity = graduationConditionEntityListNewGraduationConditionEntity.getProgramId();
                    graduationConditionEntityListNewGraduationConditionEntity.setProgramId(programEntity);
                    graduationConditionEntityListNewGraduationConditionEntity = em.merge(graduationConditionEntityListNewGraduationConditionEntity);
                    if (oldProgramIdOfGraduationConditionEntityListNewGraduationConditionEntity != null && !oldProgramIdOfGraduationConditionEntityListNewGraduationConditionEntity.equals(programEntity)) {
                        oldProgramIdOfGraduationConditionEntityListNewGraduationConditionEntity.getGraduationConditionEntityList().remove(graduationConditionEntityListNewGraduationConditionEntity);
                        oldProgramIdOfGraduationConditionEntityListNewGraduationConditionEntity = em.merge(oldProgramIdOfGraduationConditionEntityListNewGraduationConditionEntity);
                    }
                }
            }
            for (OldRollNumberEntity oldRollNumberEntityListOldOldRollNumberEntity : oldRollNumberEntityListOld) {
                if (!oldRollNumberEntityListNew.contains(oldRollNumberEntityListOldOldRollNumberEntity)) {
                    oldRollNumberEntityListOldOldRollNumberEntity.setProgramId(null);
                    oldRollNumberEntityListOldOldRollNumberEntity = em.merge(oldRollNumberEntityListOldOldRollNumberEntity);
                }
            }
            for (OldRollNumberEntity oldRollNumberEntityListNewOldRollNumberEntity : oldRollNumberEntityListNew) {
                if (!oldRollNumberEntityListOld.contains(oldRollNumberEntityListNewOldRollNumberEntity)) {
                    ProgramEntity oldProgramIdOfOldRollNumberEntityListNewOldRollNumberEntity = oldRollNumberEntityListNewOldRollNumberEntity.getProgramId();
                    oldRollNumberEntityListNewOldRollNumberEntity.setProgramId(programEntity);
                    oldRollNumberEntityListNewOldRollNumberEntity = em.merge(oldRollNumberEntityListNewOldRollNumberEntity);
                    if (oldProgramIdOfOldRollNumberEntityListNewOldRollNumberEntity != null && !oldProgramIdOfOldRollNumberEntityListNewOldRollNumberEntity.equals(programEntity)) {
                        oldProgramIdOfOldRollNumberEntityListNewOldRollNumberEntity.getOldRollNumberEntityList().remove(oldRollNumberEntityListNewOldRollNumberEntity);
                        oldProgramIdOfOldRollNumberEntityListNewOldRollNumberEntity = em.merge(oldProgramIdOfOldRollNumberEntityListNewOldRollNumberEntity);
                    }
                }
            }
            for (StudentEntity studentEntityListOldStudentEntity : studentEntityListOld) {
                if (!studentEntityListNew.contains(studentEntityListOldStudentEntity)) {
                    studentEntityListOldStudentEntity.setProgramId(null);
                    studentEntityListOldStudentEntity = em.merge(studentEntityListOldStudentEntity);
                }
            }
            for (StudentEntity studentEntityListNewStudentEntity : studentEntityListNew) {
                if (!studentEntityListOld.contains(studentEntityListNewStudentEntity)) {
                    ProgramEntity oldProgramIdOfStudentEntityListNewStudentEntity = studentEntityListNewStudentEntity.getProgramId();
                    studentEntityListNewStudentEntity.setProgramId(programEntity);
                    studentEntityListNewStudentEntity = em.merge(studentEntityListNewStudentEntity);
                    if (oldProgramIdOfStudentEntityListNewStudentEntity != null && !oldProgramIdOfStudentEntityListNewStudentEntity.equals(programEntity)) {
                        oldProgramIdOfStudentEntityListNewStudentEntity.getStudentEntityList().remove(studentEntityListNewStudentEntity);
                        oldProgramIdOfStudentEntityListNewStudentEntity = em.merge(oldProgramIdOfStudentEntityListNewStudentEntity);
                    }
                }
            }
            for (CurriculumEntity curriculumEntityListNewCurriculumEntity : curriculumEntityListNew) {
                if (!curriculumEntityListOld.contains(curriculumEntityListNewCurriculumEntity)) {
                    ProgramEntity oldProgramIdOfCurriculumEntityListNewCurriculumEntity = curriculumEntityListNewCurriculumEntity.getProgramId();
                    curriculumEntityListNewCurriculumEntity.setProgramId(programEntity);
                    curriculumEntityListNewCurriculumEntity = em.merge(curriculumEntityListNewCurriculumEntity);
                    if (oldProgramIdOfCurriculumEntityListNewCurriculumEntity != null && !oldProgramIdOfCurriculumEntityListNewCurriculumEntity.equals(programEntity)) {
                        oldProgramIdOfCurriculumEntityListNewCurriculumEntity.getCurriculumEntityList().remove(curriculumEntityListNewCurriculumEntity);
                        oldProgramIdOfCurriculumEntityListNewCurriculumEntity = em.merge(oldProgramIdOfCurriculumEntityListNewCurriculumEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = programEntity.getId();
                if (findProgramEntity(id) == null) {
                    throw new NonexistentEntityException("The programEntity with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProgramEntity programEntity;
            try {
                programEntity = em.getReference(ProgramEntity.class, id);
                programEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The programEntity with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<CurriculumEntity> curriculumEntityListOrphanCheck = programEntity.getCurriculumEntityList();
            for (CurriculumEntity curriculumEntityListOrphanCheckCurriculumEntity : curriculumEntityListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This ProgramEntity (" + programEntity + ") cannot be destroyed since the CurriculumEntity " + curriculumEntityListOrphanCheckCurriculumEntity + " in its curriculumEntityList field has a non-nullable programId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<GraduationConditionEntity> graduationConditionEntityList = programEntity.getGraduationConditionEntityList();
            for (GraduationConditionEntity graduationConditionEntityListGraduationConditionEntity : graduationConditionEntityList) {
                graduationConditionEntityListGraduationConditionEntity.setProgramId(null);
                graduationConditionEntityListGraduationConditionEntity = em.merge(graduationConditionEntityListGraduationConditionEntity);
            }
            List<OldRollNumberEntity> oldRollNumberEntityList = programEntity.getOldRollNumberEntityList();
            for (OldRollNumberEntity oldRollNumberEntityListOldRollNumberEntity : oldRollNumberEntityList) {
                oldRollNumberEntityListOldRollNumberEntity.setProgramId(null);
                oldRollNumberEntityListOldRollNumberEntity = em.merge(oldRollNumberEntityListOldRollNumberEntity);
            }
            List<StudentEntity> studentEntityList = programEntity.getStudentEntityList();
            for (StudentEntity studentEntityListStudentEntity : studentEntityList) {
                studentEntityListStudentEntity.setProgramId(null);
                studentEntityListStudentEntity = em.merge(studentEntityListStudentEntity);
            }
            em.remove(programEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ProgramEntity> findProgramEntityEntities() {
        return findProgramEntityEntities(true, -1, -1);
    }

    public List<ProgramEntity> findProgramEntityEntities(int maxResults, int firstResult) {
        return findProgramEntityEntities(false, maxResults, firstResult);
    }

    private List<ProgramEntity> findProgramEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ProgramEntity.class));
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

    public ProgramEntity findProgramEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ProgramEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getProgramEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ProgramEntity> rt = cq.from(ProgramEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
