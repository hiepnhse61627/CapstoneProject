package com.capstone.jpa;

import com.capstone.entities.GraduateDetailEntity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

/**
 *
 * @author StormNs
 */
public class GraduateDetailEntityJpaController implements Serializable {

    public GraduateDetailEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(GraduateDetailEntity graduateDetailEntity) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(graduateDetailEntity);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findGraduateDetailEntity(graduateDetailEntity.getStudentId()) != null) {
                throw new PreexistingEntityException("GraduateDetailEntity " + graduateDetailEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(GraduateDetailEntity graduateDetailEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            graduateDetailEntity = em.merge(graduateDetailEntity);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = graduateDetailEntity.getStudentId();
                if (findGraduateDetailEntity(id) == null) {
                    throw new NonexistentEntityException("The graduateDetailEntity with id " + id + " no longer exists.");
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
            GraduateDetailEntity graduateDetailEntity;
            try {
                graduateDetailEntity = em.getReference(GraduateDetailEntity.class, id);
                graduateDetailEntity.getStudentId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The graduateDetailEntity with id " + id + " no longer exists.", enfe);
            }
            em.remove(graduateDetailEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<GraduateDetailEntity> findGraduateDetailEntityEntities() {
        return findGraduateDetailEntityEntities(true, -1, -1);
    }

    public List<GraduateDetailEntity> findGraduateDetailEntityEntities(int maxResults, int firstResult) {
        return findGraduateDetailEntityEntities(false, maxResults, firstResult);
    }

    private List<GraduateDetailEntity> findGraduateDetailEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(GraduateDetailEntity.class));
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

    public GraduateDetailEntity findGraduateDetailEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(GraduateDetailEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getGraduateDetailEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<GraduateDetailEntity> rt = cq.from(GraduateDetailEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
