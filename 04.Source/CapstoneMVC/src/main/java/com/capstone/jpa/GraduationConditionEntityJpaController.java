package com.capstone.jpa;

import com.capstone.entities.GraduationConditionEntity;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.ProgramEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

/**
 *
 * @author StormNs
 */
public class GraduationConditionEntityJpaController implements Serializable {

    public GraduationConditionEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(GraduationConditionEntity graduationConditionEntity) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProgramEntity programId = graduationConditionEntity.getProgramId();
            if (programId != null) {
                programId = em.getReference(programId.getClass(), programId.getId());
                graduationConditionEntity.setProgramId(programId);
            }
            em.persist(graduationConditionEntity);
            if (programId != null) {
                programId.getGraduationConditionEntityList().add(graduationConditionEntity);
                programId = em.merge(programId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findGraduationConditionEntity(graduationConditionEntity.getId()) != null) {
                throw new PreexistingEntityException("GraduationConditionEntity " + graduationConditionEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(GraduationConditionEntity graduationConditionEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            GraduationConditionEntity persistentGraduationConditionEntity = em.find(GraduationConditionEntity.class, graduationConditionEntity.getId());
            ProgramEntity programIdOld = persistentGraduationConditionEntity.getProgramId();
            ProgramEntity programIdNew = graduationConditionEntity.getProgramId();
            if (programIdNew != null) {
                programIdNew = em.getReference(programIdNew.getClass(), programIdNew.getId());
                graduationConditionEntity.setProgramId(programIdNew);
            }
            graduationConditionEntity = em.merge(graduationConditionEntity);
            if (programIdOld != null && !programIdOld.equals(programIdNew)) {
                programIdOld.getGraduationConditionEntityList().remove(graduationConditionEntity);
                programIdOld = em.merge(programIdOld);
            }
            if (programIdNew != null && !programIdNew.equals(programIdOld)) {
                programIdNew.getGraduationConditionEntityList().add(graduationConditionEntity);
                programIdNew = em.merge(programIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = graduationConditionEntity.getId();
                if (findGraduationConditionEntity(id) == null) {
                    throw new NonexistentEntityException("The graduationConditionEntity with id " + id + " no longer exists.");
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
            GraduationConditionEntity graduationConditionEntity;
            try {
                graduationConditionEntity = em.getReference(GraduationConditionEntity.class, id);
                graduationConditionEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The graduationConditionEntity with id " + id + " no longer exists.", enfe);
            }
            ProgramEntity programId = graduationConditionEntity.getProgramId();
            if (programId != null) {
                programId.getGraduationConditionEntityList().remove(graduationConditionEntity);
                programId = em.merge(programId);
            }
            em.remove(graduationConditionEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<GraduationConditionEntity> findGraduationConditionEntityEntities() {
        return findGraduationConditionEntityEntities(true, -1, -1);
    }

    public List<GraduationConditionEntity> findGraduationConditionEntityEntities(int maxResults, int firstResult) {
        return findGraduationConditionEntityEntities(false, maxResults, firstResult);
    }

    private List<GraduationConditionEntity> findGraduationConditionEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(GraduationConditionEntity.class));
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

    public GraduationConditionEntity findGraduationConditionEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(GraduationConditionEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getGraduationConditionEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<GraduationConditionEntity> rt = cq.from(GraduationConditionEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}