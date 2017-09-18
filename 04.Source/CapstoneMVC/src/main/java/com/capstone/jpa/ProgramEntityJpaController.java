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
import java.util.ArrayList;
import com.capstone.entities.*;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.capstone.jpa.exceptions.IllegalOrphanException;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

/**
 *
 * @author hiepnhse61627
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
        if (programEntity.getCurriculaById() == null) {
            programEntity.setCurriculaById(new ArrayList<CurriculumEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<CurriculumEntity> attachedCurriculaById = new ArrayList<CurriculumEntity>();
            for (CurriculumEntity curriculaByIdCurriculumEntityToAttach : programEntity.getCurriculaById()) {
                curriculaByIdCurriculumEntityToAttach = em.getReference(curriculaByIdCurriculumEntityToAttach.getClass(), curriculaByIdCurriculumEntityToAttach.getId());
                attachedCurriculaById.add(curriculaByIdCurriculumEntityToAttach);
            }
            programEntity.setCurriculaById(attachedCurriculaById);
            em.persist(programEntity);
            for (CurriculumEntity curriculaByIdCurriculumEntity : programEntity.getCurriculaById()) {
                ProgramEntity oldProgramByProgramIdOfCurriculaByIdCurriculumEntity = curriculaByIdCurriculumEntity.getProgramByProgramId();
                curriculaByIdCurriculumEntity.setProgramByProgramId(programEntity);
                curriculaByIdCurriculumEntity = em.merge(curriculaByIdCurriculumEntity);
                if (oldProgramByProgramIdOfCurriculaByIdCurriculumEntity != null) {
                    oldProgramByProgramIdOfCurriculaByIdCurriculumEntity.getCurriculaById().remove(curriculaByIdCurriculumEntity);
                    oldProgramByProgramIdOfCurriculaByIdCurriculumEntity = em.merge(oldProgramByProgramIdOfCurriculaByIdCurriculumEntity);
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
            Collection<CurriculumEntity> curriculaByIdOld = persistentProgramEntity.getCurriculaById();
            Collection<CurriculumEntity> curriculaByIdNew = programEntity.getCurriculaById();
            List<String> illegalOrphanMessages = null;
            for (CurriculumEntity curriculaByIdOldCurriculumEntity : curriculaByIdOld) {
                if (!curriculaByIdNew.contains(curriculaByIdOldCurriculumEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain CurriculumEntity " + curriculaByIdOldCurriculumEntity + " since its programByProgramId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<CurriculumEntity> attachedCurriculaByIdNew = new ArrayList<CurriculumEntity>();
            for (CurriculumEntity curriculaByIdNewCurriculumEntityToAttach : curriculaByIdNew) {
                curriculaByIdNewCurriculumEntityToAttach = em.getReference(curriculaByIdNewCurriculumEntityToAttach.getClass(), curriculaByIdNewCurriculumEntityToAttach.getId());
                attachedCurriculaByIdNew.add(curriculaByIdNewCurriculumEntityToAttach);
            }
            curriculaByIdNew = attachedCurriculaByIdNew;
            programEntity.setCurriculaById(curriculaByIdNew);
            programEntity = em.merge(programEntity);
            for (CurriculumEntity curriculaByIdNewCurriculumEntity : curriculaByIdNew) {
                if (!curriculaByIdOld.contains(curriculaByIdNewCurriculumEntity)) {
                    ProgramEntity oldProgramByProgramIdOfCurriculaByIdNewCurriculumEntity = curriculaByIdNewCurriculumEntity.getProgramByProgramId();
                    curriculaByIdNewCurriculumEntity.setProgramByProgramId(programEntity);
                    curriculaByIdNewCurriculumEntity = em.merge(curriculaByIdNewCurriculumEntity);
                    if (oldProgramByProgramIdOfCurriculaByIdNewCurriculumEntity != null && !oldProgramByProgramIdOfCurriculaByIdNewCurriculumEntity.equals(programEntity)) {
                        oldProgramByProgramIdOfCurriculaByIdNewCurriculumEntity.getCurriculaById().remove(curriculaByIdNewCurriculumEntity);
                        oldProgramByProgramIdOfCurriculaByIdNewCurriculumEntity = em.merge(oldProgramByProgramIdOfCurriculaByIdNewCurriculumEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = programEntity.getId();
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

    public void destroy(int id) throws IllegalOrphanException, NonexistentEntityException {
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
            Collection<CurriculumEntity> curriculaByIdOrphanCheck = programEntity.getCurriculaById();
            for (CurriculumEntity curriculaByIdOrphanCheckCurriculumEntity : curriculaByIdOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This ProgramEntity (" + programEntity + ") cannot be destroyed since the CurriculumEntity " + curriculaByIdOrphanCheckCurriculumEntity + " in its curriculaById field has a non-nullable programByProgramId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
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

    public ProgramEntity findProgramEntity(int id) {
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
