/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.jpa.exceptions.IllegalOrphanException;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.CurriculumEntity;
import com.capstone.entities.ProgramEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Rem
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
        if (programEntity.getCurriculumEntityList() == null) {
            programEntity.setCurriculumEntityList(new ArrayList<CurriculumEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<CurriculumEntity> attachedCurriculumEntityList = new ArrayList<CurriculumEntity>();
            for (CurriculumEntity curriculumEntityListCurriculumEntityToAttach : programEntity.getCurriculumEntityList()) {
                curriculumEntityListCurriculumEntityToAttach = em.getReference(curriculumEntityListCurriculumEntityToAttach.getClass(), curriculumEntityListCurriculumEntityToAttach.getId());
                attachedCurriculumEntityList.add(curriculumEntityListCurriculumEntityToAttach);
            }
            programEntity.setCurriculumEntityList(attachedCurriculumEntityList);
            em.persist(programEntity);
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
            List<CurriculumEntity> attachedCurriculumEntityListNew = new ArrayList<CurriculumEntity>();
            for (CurriculumEntity curriculumEntityListNewCurriculumEntityToAttach : curriculumEntityListNew) {
                curriculumEntityListNewCurriculumEntityToAttach = em.getReference(curriculumEntityListNewCurriculumEntityToAttach.getClass(), curriculumEntityListNewCurriculumEntityToAttach.getId());
                attachedCurriculumEntityListNew.add(curriculumEntityListNewCurriculumEntityToAttach);
            }
            curriculumEntityListNew = attachedCurriculumEntityListNew;
            programEntity.setCurriculumEntityList(curriculumEntityListNew);
            programEntity = em.merge(programEntity);
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
