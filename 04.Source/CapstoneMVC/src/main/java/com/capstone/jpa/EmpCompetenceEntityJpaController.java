/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.entities.EmpCompetenceEntity;
import com.capstone.entities.EmployeeEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author hoanglong
 */
public class EmpCompetenceEntityJpaController implements Serializable {

    public EmpCompetenceEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(EmpCompetenceEntity empCompetenceEntity) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            EmployeeEntity employeeId = empCompetenceEntity.getEmployeeId();
            if (employeeId != null) {
                employeeId = em.getReference(employeeId.getClass(), employeeId.getId());
                empCompetenceEntity.setEmployeeId(employeeId);
            }
            SubjectEntity subjectId = empCompetenceEntity.getSubjectId();
            if (subjectId != null) {
                subjectId = em.getReference(subjectId.getClass(), subjectId.getId());
                empCompetenceEntity.setSubjectId(subjectId);
            }
            em.persist(empCompetenceEntity);
            if (employeeId != null) {
                employeeId.getEmpCompetenceEntityList().add(empCompetenceEntity);
                employeeId = em.merge(employeeId);
            }
            if (subjectId != null) {
                subjectId.getEmpCompetenceEntityList().add(empCompetenceEntity);
                subjectId = em.merge(subjectId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findEmpCompetenceEntity(empCompetenceEntity.getId()) != null) {
                throw new PreexistingEntityException("EmpCompetenceEntity " + empCompetenceEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(EmpCompetenceEntity empCompetenceEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            EmpCompetenceEntity persistentEmpCompetenceEntity = em.find(EmpCompetenceEntity.class, empCompetenceEntity.getId());
            EmployeeEntity employeeIdOld = persistentEmpCompetenceEntity.getEmployeeId();
            EmployeeEntity employeeIdNew = empCompetenceEntity.getEmployeeId();
            SubjectEntity subjectIdOld = persistentEmpCompetenceEntity.getSubjectId();
            SubjectEntity subjectIdNew = empCompetenceEntity.getSubjectId();
            if (employeeIdNew != null) {
                employeeIdNew = em.getReference(employeeIdNew.getClass(), employeeIdNew.getId());
                empCompetenceEntity.setEmployeeId(employeeIdNew);
            }
            if (subjectIdNew != null) {
                subjectIdNew = em.getReference(subjectIdNew.getClass(), subjectIdNew.getId());
                empCompetenceEntity.setSubjectId(subjectIdNew);
            }
            empCompetenceEntity = em.merge(empCompetenceEntity);
            if (employeeIdOld != null && !employeeIdOld.equals(employeeIdNew)) {
                employeeIdOld.getEmpCompetenceEntityList().remove(empCompetenceEntity);
                employeeIdOld = em.merge(employeeIdOld);
            }
            if (employeeIdNew != null && !employeeIdNew.equals(employeeIdOld)) {
                employeeIdNew.getEmpCompetenceEntityList().add(empCompetenceEntity);
                employeeIdNew = em.merge(employeeIdNew);
            }
            if (subjectIdOld != null && !subjectIdOld.equals(subjectIdNew)) {
                subjectIdOld.getEmpCompetenceEntityList().remove(empCompetenceEntity);
                subjectIdOld = em.merge(subjectIdOld);
            }
            if (subjectIdNew != null && !subjectIdNew.equals(subjectIdOld)) {
                subjectIdNew.getEmpCompetenceEntityList().add(empCompetenceEntity);
                subjectIdNew = em.merge(subjectIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = empCompetenceEntity.getId();
                if (findEmpCompetenceEntity(id) == null) {
                    throw new NonexistentEntityException("The empCompetenceEntity with id " + id + " no longer exists.");
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
            EmpCompetenceEntity empCompetenceEntity;
            try {
                empCompetenceEntity = em.getReference(EmpCompetenceEntity.class, id);
                empCompetenceEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The empCompetenceEntity with id " + id + " no longer exists.", enfe);
            }
            EmployeeEntity employeeId = empCompetenceEntity.getEmployeeId();
            if (employeeId != null) {
                employeeId.getEmpCompetenceEntityList().remove(empCompetenceEntity);
                employeeId = em.merge(employeeId);
            }
            SubjectEntity subjectId = empCompetenceEntity.getSubjectId();
            if (subjectId != null) {
                subjectId.getEmpCompetenceEntityList().remove(empCompetenceEntity);
                subjectId = em.merge(subjectId);
            }
            em.remove(empCompetenceEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<EmpCompetenceEntity> findEmpCompetenceEntityEntities() {
        return findEmpCompetenceEntityEntities(true, -1, -1);
    }

    public List<EmpCompetenceEntity> findEmpCompetenceEntityEntities(int maxResults, int firstResult) {
        return findEmpCompetenceEntityEntities(false, maxResults, firstResult);
    }

    private List<EmpCompetenceEntity> findEmpCompetenceEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(EmpCompetenceEntity.class));
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

    public EmpCompetenceEntity findEmpCompetenceEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(EmpCompetenceEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getEmpCompetenceEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<EmpCompetenceEntity> rt = cq.from(EmpCompetenceEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
