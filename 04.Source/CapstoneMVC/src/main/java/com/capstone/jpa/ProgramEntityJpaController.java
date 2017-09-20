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
import com.capstone.entities.CurriculumEntity;
import com.capstone.entities.ProgramEntity;
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
public class ProgramEntityJpaController implements Serializable {

    public ProgramEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ProgramEntity programEntity) throws PreexistingEntityException, Exception {
        if (programEntity.getCurriculumList() == null) {
            programEntity.setCurriculumList(new ArrayList<CurriculumEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<CurriculumEntity> attachedCurriculumList = new ArrayList<CurriculumEntity>();
            for (CurriculumEntity curriculumListCurriculumEntityToAttach : programEntity.getCurriculumList()) {
                curriculumListCurriculumEntityToAttach = em.getReference(curriculumListCurriculumEntityToAttach.getClass(), curriculumListCurriculumEntityToAttach.getId());
                attachedCurriculumList.add(curriculumListCurriculumEntityToAttach);
            }
            programEntity.setCurriculumList(attachedCurriculumList);
            em.persist(programEntity);
            for (CurriculumEntity curriculumListCurriculumEntity : programEntity.getCurriculumList()) {
                ProgramEntity oldProgramIdOfCurriculumListCurriculumEntity = curriculumListCurriculumEntity.getProgramId();
                curriculumListCurriculumEntity.setProgramId(programEntity);
                curriculumListCurriculumEntity = em.merge(curriculumListCurriculumEntity);
                if (oldProgramIdOfCurriculumListCurriculumEntity != null) {
                    oldProgramIdOfCurriculumListCurriculumEntity.getCurriculumList().remove(curriculumListCurriculumEntity);
                    oldProgramIdOfCurriculumListCurriculumEntity = em.merge(oldProgramIdOfCurriculumListCurriculumEntity);
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
            List<CurriculumEntity> curriculumListOld = persistentProgramEntity.getCurriculumList();
            List<CurriculumEntity> curriculumListNew = programEntity.getCurriculumList();
            List<String> illegalOrphanMessages = null;
            for (CurriculumEntity curriculumListOldCurriculumEntity : curriculumListOld) {
                if (!curriculumListNew.contains(curriculumListOldCurriculumEntity)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain CurriculumEntity " + curriculumListOldCurriculumEntity + " since its programId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<CurriculumEntity> attachedCurriculumListNew = new ArrayList<CurriculumEntity>();
            for (CurriculumEntity curriculumListNewCurriculumEntityToAttach : curriculumListNew) {
                curriculumListNewCurriculumEntityToAttach = em.getReference(curriculumListNewCurriculumEntityToAttach.getClass(), curriculumListNewCurriculumEntityToAttach.getId());
                attachedCurriculumListNew.add(curriculumListNewCurriculumEntityToAttach);
            }
            curriculumListNew = attachedCurriculumListNew;
            programEntity.setCurriculumList(curriculumListNew);
            programEntity = em.merge(programEntity);
            for (CurriculumEntity curriculumListNewCurriculumEntity : curriculumListNew) {
                if (!curriculumListOld.contains(curriculumListNewCurriculumEntity)) {
                    ProgramEntity oldProgramIdOfCurriculumListNewCurriculumEntity = curriculumListNewCurriculumEntity.getProgramId();
                    curriculumListNewCurriculumEntity.setProgramId(programEntity);
                    curriculumListNewCurriculumEntity = em.merge(curriculumListNewCurriculumEntity);
                    if (oldProgramIdOfCurriculumListNewCurriculumEntity != null && !oldProgramIdOfCurriculumListNewCurriculumEntity.equals(programEntity)) {
                        oldProgramIdOfCurriculumListNewCurriculumEntity.getCurriculumList().remove(curriculumListNewCurriculumEntity);
                        oldProgramIdOfCurriculumListNewCurriculumEntity = em.merge(oldProgramIdOfCurriculumListNewCurriculumEntity);
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
            List<CurriculumEntity> curriculumListOrphanCheck = programEntity.getCurriculumList();
            for (CurriculumEntity curriculumListOrphanCheckCurriculumEntity : curriculumListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This ProgramEntity (" + programEntity + ") cannot be destroyed since the CurriculumEntity " + curriculumListOrphanCheckCurriculumEntity + " in its curriculumList field has a non-nullable programId field.");
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
