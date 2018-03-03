package com.capstone.jpa;

import com.capstone.entities.DepartmentEntity;
import com.capstone.entities.SubjectDepartmentEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

public class SubjectDepartmentEntityJpaController implements Serializable {

    public SubjectDepartmentEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SubjectDepartmentEntity subjectDepartmentEntity) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DepartmentEntity deptId = subjectDepartmentEntity.getDeptId();
            if (deptId != null) {
                deptId = em.getReference(deptId.getClass(), deptId.getDeptId());
                subjectDepartmentEntity.setDeptId(deptId);
            }
            SubjectEntity subjectId = subjectDepartmentEntity.getSubjectId();
            if (subjectId != null) {
                subjectId = em.getReference(subjectId.getClass(), subjectId.getId());
                subjectDepartmentEntity.setSubjectId(subjectId);
            }
            em.persist(subjectDepartmentEntity);
            if (deptId != null) {
                deptId.getSubjectDepartmentEntityList().add(subjectDepartmentEntity);
                deptId = em.merge(deptId);
            }
            if (subjectId != null) {
                subjectId.getSubjectDepartmentEntityList().add(subjectDepartmentEntity);
                subjectId = em.merge(subjectId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findSubjectDepartmentEntity(subjectDepartmentEntity.getId()) != null) {
                throw new PreexistingEntityException("SubjectDepartmentEntity " + subjectDepartmentEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SubjectDepartmentEntity subjectDepartmentEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SubjectDepartmentEntity persistentSubjectDepartmentEntity = em.find(SubjectDepartmentEntity.class, subjectDepartmentEntity.getId());
            DepartmentEntity deptIdOld = persistentSubjectDepartmentEntity.getDeptId();
            DepartmentEntity deptIdNew = subjectDepartmentEntity.getDeptId();
            SubjectEntity subjectIdOld = persistentSubjectDepartmentEntity.getSubjectId();
            SubjectEntity subjectIdNew = subjectDepartmentEntity.getSubjectId();
            if (deptIdNew != null) {
                deptIdNew = em.getReference(deptIdNew.getClass(), deptIdNew.getDeptId());
                subjectDepartmentEntity.setDeptId(deptIdNew);
            }
            if (subjectIdNew != null) {
                subjectIdNew = em.getReference(subjectIdNew.getClass(), subjectIdNew.getId());
                subjectDepartmentEntity.setSubjectId(subjectIdNew);
            }
            subjectDepartmentEntity = em.merge(subjectDepartmentEntity);
            if (deptIdOld != null && !deptIdOld.equals(deptIdNew)) {
                deptIdOld.getSubjectDepartmentEntityList().remove(subjectDepartmentEntity);
                deptIdOld = em.merge(deptIdOld);
            }
            if (deptIdNew != null && !deptIdNew.equals(deptIdOld)) {
                deptIdNew.getSubjectDepartmentEntityList().add(subjectDepartmentEntity);
                deptIdNew = em.merge(deptIdNew);
            }
            if (subjectIdOld != null && !subjectIdOld.equals(subjectIdNew)) {
                subjectIdOld.getSubjectDepartmentEntityList().remove(subjectDepartmentEntity);
                subjectIdOld = em.merge(subjectIdOld);
            }
            if (subjectIdNew != null && !subjectIdNew.equals(subjectIdOld)) {
                subjectIdNew.getSubjectDepartmentEntityList().add(subjectDepartmentEntity);
                subjectIdNew = em.merge(subjectIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = subjectDepartmentEntity.getId();
                if (findSubjectDepartmentEntity(id) == null) {
                    throw new NonexistentEntityException("The subjectDepartmentEntity with id " + id + " no longer exists.");
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
            SubjectDepartmentEntity subjectDepartmentEntity;
            try {
                subjectDepartmentEntity = em.getReference(SubjectDepartmentEntity.class, id);
                subjectDepartmentEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The subjectDepartmentEntity with id " + id + " no longer exists.", enfe);
            }
            DepartmentEntity deptId = subjectDepartmentEntity.getDeptId();
            if (deptId != null) {
                deptId.getSubjectDepartmentEntityList().remove(subjectDepartmentEntity);
                deptId = em.merge(deptId);
            }
            SubjectEntity subjectId = subjectDepartmentEntity.getSubjectId();
            if (subjectId != null) {
                subjectId.getSubjectDepartmentEntityList().remove(subjectDepartmentEntity);
                subjectId = em.merge(subjectId);
            }
            em.remove(subjectDepartmentEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<SubjectDepartmentEntity> findSubjectDepartmentEntityEntities() {
        return findSubjectDepartmentEntityEntities(true, -1, -1);
    }

    public List<SubjectDepartmentEntity> findSubjectDepartmentEntityEntities(int maxResults, int firstResult) {
        return findSubjectDepartmentEntityEntities(false, maxResults, firstResult);
    }

    private List<SubjectDepartmentEntity> findSubjectDepartmentEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SubjectDepartmentEntity.class));
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

    public SubjectDepartmentEntity findSubjectDepartmentEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SubjectDepartmentEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getSubjectDepartmentEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SubjectDepartmentEntity> rt = cq.from(SubjectDepartmentEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
