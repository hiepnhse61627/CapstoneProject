package com.capstone.jpa;

import com.capstone.entities.DepartmentEntity;
import com.capstone.entities.SubjectDepartmentEntity;
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
        if (departmentEntity.getSubjectDepartmentEntityList() == null) {
            departmentEntity.setSubjectDepartmentEntityList(new ArrayList<SubjectDepartmentEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<SubjectDepartmentEntity> attachedSubjectDepartmentEntityList = new ArrayList<SubjectDepartmentEntity>();
            for (SubjectDepartmentEntity subjectDepartmentEntityListSubjectDepartmentEntityToAttach : departmentEntity.getSubjectDepartmentEntityList()) {
                subjectDepartmentEntityListSubjectDepartmentEntityToAttach = em.getReference(subjectDepartmentEntityListSubjectDepartmentEntityToAttach.getClass(), subjectDepartmentEntityListSubjectDepartmentEntityToAttach.getId());
                attachedSubjectDepartmentEntityList.add(subjectDepartmentEntityListSubjectDepartmentEntityToAttach);
            }
            departmentEntity.setSubjectDepartmentEntityList(attachedSubjectDepartmentEntityList);
            em.persist(departmentEntity);
            for (SubjectDepartmentEntity subjectDepartmentEntityListSubjectDepartmentEntity : departmentEntity.getSubjectDepartmentEntityList()) {
                DepartmentEntity oldDeptIdOfSubjectDepartmentEntityListSubjectDepartmentEntity = subjectDepartmentEntityListSubjectDepartmentEntity.getDeptId();
                subjectDepartmentEntityListSubjectDepartmentEntity.setDeptId(departmentEntity);
                subjectDepartmentEntityListSubjectDepartmentEntity = em.merge(subjectDepartmentEntityListSubjectDepartmentEntity);
                if (oldDeptIdOfSubjectDepartmentEntityListSubjectDepartmentEntity != null) {
                    oldDeptIdOfSubjectDepartmentEntityListSubjectDepartmentEntity.getSubjectDepartmentEntityList().remove(subjectDepartmentEntityListSubjectDepartmentEntity);
                    oldDeptIdOfSubjectDepartmentEntityListSubjectDepartmentEntity = em.merge(oldDeptIdOfSubjectDepartmentEntityListSubjectDepartmentEntity);
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
            List<SubjectDepartmentEntity> subjectDepartmentEntityListOld = persistentDepartmentEntity.getSubjectDepartmentEntityList();
            List<SubjectDepartmentEntity> subjectDepartmentEntityListNew = departmentEntity.getSubjectDepartmentEntityList();
            List<SubjectDepartmentEntity> attachedSubjectDepartmentEntityListNew = new ArrayList<SubjectDepartmentEntity>();
            for (SubjectDepartmentEntity subjectDepartmentEntityListNewSubjectDepartmentEntityToAttach : subjectDepartmentEntityListNew) {
                subjectDepartmentEntityListNewSubjectDepartmentEntityToAttach = em.getReference(subjectDepartmentEntityListNewSubjectDepartmentEntityToAttach.getClass(), subjectDepartmentEntityListNewSubjectDepartmentEntityToAttach.getId());
                attachedSubjectDepartmentEntityListNew.add(subjectDepartmentEntityListNewSubjectDepartmentEntityToAttach);
            }
            subjectDepartmentEntityListNew = attachedSubjectDepartmentEntityListNew;
            departmentEntity.setSubjectDepartmentEntityList(subjectDepartmentEntityListNew);
            departmentEntity = em.merge(departmentEntity);
            for (SubjectDepartmentEntity subjectDepartmentEntityListOldSubjectDepartmentEntity : subjectDepartmentEntityListOld) {
                if (!subjectDepartmentEntityListNew.contains(subjectDepartmentEntityListOldSubjectDepartmentEntity)) {
                    subjectDepartmentEntityListOldSubjectDepartmentEntity.setDeptId(null);
                    subjectDepartmentEntityListOldSubjectDepartmentEntity = em.merge(subjectDepartmentEntityListOldSubjectDepartmentEntity);
                }
            }
            for (SubjectDepartmentEntity subjectDepartmentEntityListNewSubjectDepartmentEntity : subjectDepartmentEntityListNew) {
                if (!subjectDepartmentEntityListOld.contains(subjectDepartmentEntityListNewSubjectDepartmentEntity)) {
                    DepartmentEntity oldDeptIdOfSubjectDepartmentEntityListNewSubjectDepartmentEntity = subjectDepartmentEntityListNewSubjectDepartmentEntity.getDeptId();
                    subjectDepartmentEntityListNewSubjectDepartmentEntity.setDeptId(departmentEntity);
                    subjectDepartmentEntityListNewSubjectDepartmentEntity = em.merge(subjectDepartmentEntityListNewSubjectDepartmentEntity);
                    if (oldDeptIdOfSubjectDepartmentEntityListNewSubjectDepartmentEntity != null && !oldDeptIdOfSubjectDepartmentEntityListNewSubjectDepartmentEntity.equals(departmentEntity)) {
                        oldDeptIdOfSubjectDepartmentEntityListNewSubjectDepartmentEntity.getSubjectDepartmentEntityList().remove(subjectDepartmentEntityListNewSubjectDepartmentEntity);
                        oldDeptIdOfSubjectDepartmentEntityListNewSubjectDepartmentEntity = em.merge(oldDeptIdOfSubjectDepartmentEntityListNewSubjectDepartmentEntity);
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
            List<SubjectDepartmentEntity> subjectDepartmentEntityList = departmentEntity.getSubjectDepartmentEntityList();
            for (SubjectDepartmentEntity subjectDepartmentEntityListSubjectDepartmentEntity : subjectDepartmentEntityList) {
                subjectDepartmentEntityListSubjectDepartmentEntity.setDeptId(null);
                subjectDepartmentEntityListSubjectDepartmentEntity = em.merge(subjectDepartmentEntityListSubjectDepartmentEntity);
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
