package com.capstone.jpa;

import com.capstone.entities.DepartmentEntity;
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
import java.util.ArrayList;
import java.util.List;

public class DepartmentEntityJpaController implements Serializable {

    public DepartmentEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DepartmentEntity departmentEntity) throws PreexistingEntityException, Exception {

        if (departmentEntity.getSubjectEntityList() == null) {
            departmentEntity.setSubjectEntityList(new ArrayList<SubjectEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();

            List<SubjectEntity> attachedSubjectEntityList = new ArrayList<SubjectEntity>();
            for (SubjectEntity subjectEntityListSubjectEntityToAttach : departmentEntity.getSubjectEntityList()) {
                subjectEntityListSubjectEntityToAttach = em.getReference(subjectEntityListSubjectEntityToAttach.getClass(), subjectEntityListSubjectEntityToAttach.getId());
                attachedSubjectEntityList.add(subjectEntityListSubjectEntityToAttach);
            }
            departmentEntity.setSubjectEntityList(attachedSubjectEntityList);
            em.persist(departmentEntity);

            for (SubjectEntity subjectEntityListSubjectEntity : departmentEntity.getSubjectEntityList()) {
                DepartmentEntity oldDepartmentIdOfSubjectEntityListSubjectEntity = subjectEntityListSubjectEntity.getDepartmentId();
                subjectEntityListSubjectEntity.setDepartmentId(departmentEntity);
                subjectEntityListSubjectEntity = em.merge(subjectEntityListSubjectEntity);
                if (oldDepartmentIdOfSubjectEntityListSubjectEntity != null) {
                    oldDepartmentIdOfSubjectEntityListSubjectEntity.getSubjectEntityList().remove(subjectEntityListSubjectEntity);
                    oldDepartmentIdOfSubjectEntityListSubjectEntity = em.merge(oldDepartmentIdOfSubjectEntityListSubjectEntity);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findDepartmentEntity(departmentEntity.getDeptId()) != null) {
                throw new PreexistingEntityException("DepartmentEntity " + departmentEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DepartmentEntity departmentEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DepartmentEntity persistentDepartmentEntity = em.find(DepartmentEntity.class, departmentEntity.getDeptId());
           List<SubjectEntity> subjectEntityListOld = persistentDepartmentEntity.getSubjectEntityList();
            List<SubjectEntity> subjectEntityListNew = departmentEntity.getSubjectEntityList();

            List<SubjectEntity> attachedSubjectEntityListNew = new ArrayList<SubjectEntity>();
            for (SubjectEntity subjectEntityListNewSubjectEntityToAttach : subjectEntityListNew) {
                subjectEntityListNewSubjectEntityToAttach = em.getReference(subjectEntityListNewSubjectEntityToAttach.getClass(), subjectEntityListNewSubjectEntityToAttach.getId());
                attachedSubjectEntityListNew.add(subjectEntityListNewSubjectEntityToAttach);
            }
            subjectEntityListNew = attachedSubjectEntityListNew;
            departmentEntity.setSubjectEntityList(subjectEntityListNew);
            departmentEntity = em.merge(departmentEntity);

            for (SubjectEntity subjectEntityListOldSubjectEntity : subjectEntityListOld) {
                if (!subjectEntityListNew.contains(subjectEntityListOldSubjectEntity)) {
                    subjectEntityListOldSubjectEntity.setDepartmentId(null);
                    subjectEntityListOldSubjectEntity = em.merge(subjectEntityListOldSubjectEntity);
                }
            }
            for (SubjectEntity subjectEntityListNewSubjectEntity : subjectEntityListNew) {
                if (!subjectEntityListOld.contains(subjectEntityListNewSubjectEntity)) {
                    DepartmentEntity oldDepartmentIdOfSubjectEntityListNewSubjectEntity = subjectEntityListNewSubjectEntity.getDepartmentId();
                    subjectEntityListNewSubjectEntity.setDepartmentId(departmentEntity);
                    subjectEntityListNewSubjectEntity = em.merge(subjectEntityListNewSubjectEntity);
                    if (oldDepartmentIdOfSubjectEntityListNewSubjectEntity != null && !oldDepartmentIdOfSubjectEntityListNewSubjectEntity.equals(departmentEntity)) {
                        oldDepartmentIdOfSubjectEntityListNewSubjectEntity.getSubjectEntityList().remove(subjectEntityListNewSubjectEntity);
                        oldDepartmentIdOfSubjectEntityListNewSubjectEntity = em.merge(oldDepartmentIdOfSubjectEntityListNewSubjectEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = departmentEntity.getDeptId();
                if (findDepartmentEntity(id) == null) {
                    throw new NonexistentEntityException("The departmentEntity with id " + id + " no longer exists.");
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
            DepartmentEntity departmentEntity;
            try {
                departmentEntity = em.getReference(DepartmentEntity.class, id);
                departmentEntity.getDeptId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The departmentEntity with id " + id + " no longer exists.", enfe);
            }

            List<SubjectEntity> subjectEntityList = departmentEntity.getSubjectEntityList();
            for (SubjectEntity subjectEntityListSubjectEntity : subjectEntityList) {
                subjectEntityListSubjectEntity.setDepartmentId(null);
                subjectEntityListSubjectEntity = em.merge(subjectEntityListSubjectEntity);
            }
            em.remove(departmentEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<DepartmentEntity> findDepartmentEntityEntities() {
        return findDepartmentEntityEntities(true, -1, -1);
    }

    public List<DepartmentEntity> findDepartmentEntityEntities(int maxResults, int firstResult) {
        return findDepartmentEntityEntities(false, maxResults, firstResult);
    }

    private List<DepartmentEntity> findDepartmentEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DepartmentEntity.class));
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

    public DepartmentEntity findDepartmentEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DepartmentEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getDepartmentEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DepartmentEntity> rt = cq.from(DepartmentEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
