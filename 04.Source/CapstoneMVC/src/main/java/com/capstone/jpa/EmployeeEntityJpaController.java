/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.jpa;

import com.capstone.entities.DepartmentEntity;
import com.capstone.entities.EmpCompetenceEntity;
import com.capstone.entities.EmployeeEntity;
import com.capstone.entities.ScheduleEntity;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;



/**
 *
 * @author hoanglong
 */
public class EmployeeEntityJpaController implements Serializable {

    public EmployeeEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(EmployeeEntity employeeEntity) throws PreexistingEntityException, Exception {
        if (employeeEntity.getEmpCompetenceEntityList() == null) {
            employeeEntity.setEmpCompetenceEntityList(new ArrayList<EmpCompetenceEntity>());
        }
        if (employeeEntity.getScheduleEntityList() == null) {
            employeeEntity.setScheduleEntityList(new ArrayList<ScheduleEntity>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DepartmentEntity deptId = employeeEntity.getDeptId();
            if (deptId != null) {
                deptId = em.getReference(deptId.getClass(), deptId.getDeptId());
                employeeEntity.setDeptId(deptId);
            }
            List<EmpCompetenceEntity> attachedEmpCompetenceEntityList = new ArrayList<EmpCompetenceEntity>();
            for (EmpCompetenceEntity empCompetenceEntityListEmpCompetenceEntityToAttach : employeeEntity.getEmpCompetenceEntityList()) {
                empCompetenceEntityListEmpCompetenceEntityToAttach = em.getReference(empCompetenceEntityListEmpCompetenceEntityToAttach.getClass(), empCompetenceEntityListEmpCompetenceEntityToAttach.getId());
                attachedEmpCompetenceEntityList.add(empCompetenceEntityListEmpCompetenceEntityToAttach);
            }
            employeeEntity.setEmpCompetenceEntityList(attachedEmpCompetenceEntityList);
            List<ScheduleEntity> attachedScheduleEntityList = new ArrayList<ScheduleEntity>();
            for (ScheduleEntity scheduleEntityListScheduleEntityToAttach : employeeEntity.getScheduleEntityList()) {
                scheduleEntityListScheduleEntityToAttach = em.getReference(scheduleEntityListScheduleEntityToAttach.getClass(), scheduleEntityListScheduleEntityToAttach.getId());
                attachedScheduleEntityList.add(scheduleEntityListScheduleEntityToAttach);
            }
            employeeEntity.setScheduleEntityList(attachedScheduleEntityList);
            em.persist(employeeEntity);
            if (deptId != null) {
                deptId.getEmployeeEntityList().add(employeeEntity);
                deptId = em.merge(deptId);
            }
            for (EmpCompetenceEntity empCompetenceEntityListEmpCompetenceEntity : employeeEntity.getEmpCompetenceEntityList()) {
                EmployeeEntity oldEmployeeIdOfEmpCompetenceEntityListEmpCompetenceEntity = empCompetenceEntityListEmpCompetenceEntity.getEmployeeId();
                empCompetenceEntityListEmpCompetenceEntity.setEmployeeId(employeeEntity);
                empCompetenceEntityListEmpCompetenceEntity = em.merge(empCompetenceEntityListEmpCompetenceEntity);
                if (oldEmployeeIdOfEmpCompetenceEntityListEmpCompetenceEntity != null) {
                    oldEmployeeIdOfEmpCompetenceEntityListEmpCompetenceEntity.getEmpCompetenceEntityList().remove(empCompetenceEntityListEmpCompetenceEntity);
                    oldEmployeeIdOfEmpCompetenceEntityListEmpCompetenceEntity = em.merge(oldEmployeeIdOfEmpCompetenceEntityListEmpCompetenceEntity);
                }
            }
            for (ScheduleEntity scheduleEntityListScheduleEntity : employeeEntity.getScheduleEntityList()) {
                EmployeeEntity oldEmpIdOfScheduleEntityListScheduleEntity = scheduleEntityListScheduleEntity.getEmpId();
                scheduleEntityListScheduleEntity.setEmpId(employeeEntity);
                scheduleEntityListScheduleEntity = em.merge(scheduleEntityListScheduleEntity);
                if (oldEmpIdOfScheduleEntityListScheduleEntity != null) {
                    oldEmpIdOfScheduleEntityListScheduleEntity.getScheduleEntityList().remove(scheduleEntityListScheduleEntity);
                    oldEmpIdOfScheduleEntityListScheduleEntity = em.merge(oldEmpIdOfScheduleEntityListScheduleEntity);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findEmployeeEntity(employeeEntity.getId()) != null) {
                throw new PreexistingEntityException("EmployeeEntity " + employeeEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(EmployeeEntity employeeEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            EmployeeEntity persistentEmployeeEntity = em.find(EmployeeEntity.class, employeeEntity.getId());
            DepartmentEntity deptIdOld = persistentEmployeeEntity.getDeptId();
            DepartmentEntity deptIdNew = employeeEntity.getDeptId();
            List<EmpCompetenceEntity> empCompetenceEntityListOld = persistentEmployeeEntity.getEmpCompetenceEntityList();
            List<EmpCompetenceEntity> empCompetenceEntityListNew = employeeEntity.getEmpCompetenceEntityList();
            List<ScheduleEntity> scheduleEntityListOld = persistentEmployeeEntity.getScheduleEntityList();
            List<ScheduleEntity> scheduleEntityListNew = employeeEntity.getScheduleEntityList();
            if (deptIdNew != null) {
                deptIdNew = em.getReference(deptIdNew.getClass(), deptIdNew.getDeptId());
                employeeEntity.setDeptId(deptIdNew);
            }
            List<EmpCompetenceEntity> attachedEmpCompetenceEntityListNew = new ArrayList<EmpCompetenceEntity>();
            for (EmpCompetenceEntity empCompetenceEntityListNewEmpCompetenceEntityToAttach : empCompetenceEntityListNew) {
                empCompetenceEntityListNewEmpCompetenceEntityToAttach = em.getReference(empCompetenceEntityListNewEmpCompetenceEntityToAttach.getClass(), empCompetenceEntityListNewEmpCompetenceEntityToAttach.getId());
                attachedEmpCompetenceEntityListNew.add(empCompetenceEntityListNewEmpCompetenceEntityToAttach);
            }
            empCompetenceEntityListNew = attachedEmpCompetenceEntityListNew;
            employeeEntity.setEmpCompetenceEntityList(empCompetenceEntityListNew);
            List<ScheduleEntity> attachedScheduleEntityListNew = new ArrayList<ScheduleEntity>();
            for (ScheduleEntity scheduleEntityListNewScheduleEntityToAttach : scheduleEntityListNew) {
                scheduleEntityListNewScheduleEntityToAttach = em.getReference(scheduleEntityListNewScheduleEntityToAttach.getClass(), scheduleEntityListNewScheduleEntityToAttach.getId());
                attachedScheduleEntityListNew.add(scheduleEntityListNewScheduleEntityToAttach);
            }
            scheduleEntityListNew = attachedScheduleEntityListNew;
            employeeEntity.setScheduleEntityList(scheduleEntityListNew);
            employeeEntity = em.merge(employeeEntity);
            if (deptIdOld != null && !deptIdOld.equals(deptIdNew)) {
                deptIdOld.getEmployeeEntityList().remove(employeeEntity);
                deptIdOld = em.merge(deptIdOld);
            }
            if (deptIdNew != null && !deptIdNew.equals(deptIdOld)) {
                deptIdNew.getEmployeeEntityList().add(employeeEntity);
                deptIdNew = em.merge(deptIdNew);
            }
            for (EmpCompetenceEntity empCompetenceEntityListOldEmpCompetenceEntity : empCompetenceEntityListOld) {
                if (!empCompetenceEntityListNew.contains(empCompetenceEntityListOldEmpCompetenceEntity)) {
                    empCompetenceEntityListOldEmpCompetenceEntity.setEmployeeId(null);
                    empCompetenceEntityListOldEmpCompetenceEntity = em.merge(empCompetenceEntityListOldEmpCompetenceEntity);
                }
            }
            for (EmpCompetenceEntity empCompetenceEntityListNewEmpCompetenceEntity : empCompetenceEntityListNew) {
                if (!empCompetenceEntityListOld.contains(empCompetenceEntityListNewEmpCompetenceEntity)) {
                    EmployeeEntity oldEmployeeIdOfEmpCompetenceEntityListNewEmpCompetenceEntity = empCompetenceEntityListNewEmpCompetenceEntity.getEmployeeId();
                    empCompetenceEntityListNewEmpCompetenceEntity.setEmployeeId(employeeEntity);
                    empCompetenceEntityListNewEmpCompetenceEntity = em.merge(empCompetenceEntityListNewEmpCompetenceEntity);
                    if (oldEmployeeIdOfEmpCompetenceEntityListNewEmpCompetenceEntity != null && !oldEmployeeIdOfEmpCompetenceEntityListNewEmpCompetenceEntity.equals(employeeEntity)) {
                        oldEmployeeIdOfEmpCompetenceEntityListNewEmpCompetenceEntity.getEmpCompetenceEntityList().remove(empCompetenceEntityListNewEmpCompetenceEntity);
                        oldEmployeeIdOfEmpCompetenceEntityListNewEmpCompetenceEntity = em.merge(oldEmployeeIdOfEmpCompetenceEntityListNewEmpCompetenceEntity);
                    }
                }
            }
            for (ScheduleEntity scheduleEntityListOldScheduleEntity : scheduleEntityListOld) {
                if (!scheduleEntityListNew.contains(scheduleEntityListOldScheduleEntity)) {
                    scheduleEntityListOldScheduleEntity.setEmpId(null);
                    scheduleEntityListOldScheduleEntity = em.merge(scheduleEntityListOldScheduleEntity);
                }
            }
            for (ScheduleEntity scheduleEntityListNewScheduleEntity : scheduleEntityListNew) {
                if (!scheduleEntityListOld.contains(scheduleEntityListNewScheduleEntity)) {
                    EmployeeEntity oldEmpIdOfScheduleEntityListNewScheduleEntity = scheduleEntityListNewScheduleEntity.getEmpId();
                    scheduleEntityListNewScheduleEntity.setEmpId(employeeEntity);
                    scheduleEntityListNewScheduleEntity = em.merge(scheduleEntityListNewScheduleEntity);
                    if (oldEmpIdOfScheduleEntityListNewScheduleEntity != null && !oldEmpIdOfScheduleEntityListNewScheduleEntity.equals(employeeEntity)) {
                        oldEmpIdOfScheduleEntityListNewScheduleEntity.getScheduleEntityList().remove(scheduleEntityListNewScheduleEntity);
                        oldEmpIdOfScheduleEntityListNewScheduleEntity = em.merge(oldEmpIdOfScheduleEntityListNewScheduleEntity);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = employeeEntity.getId();
                if (findEmployeeEntity(id) == null) {
                    throw new NonexistentEntityException("The employeeEntity with id " + id + " no longer exists.");
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
            EmployeeEntity employeeEntity;
            try {
                employeeEntity = em.getReference(EmployeeEntity.class, id);
                employeeEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The employeeEntity with id " + id + " no longer exists.", enfe);
            }
            DepartmentEntity deptId = employeeEntity.getDeptId();
            if (deptId != null) {
                deptId.getEmployeeEntityList().remove(employeeEntity);
                deptId = em.merge(deptId);
            }
            List<EmpCompetenceEntity> empCompetenceEntityList = employeeEntity.getEmpCompetenceEntityList();
            for (EmpCompetenceEntity empCompetenceEntityListEmpCompetenceEntity : empCompetenceEntityList) {
                empCompetenceEntityListEmpCompetenceEntity.setEmployeeId(null);
                empCompetenceEntityListEmpCompetenceEntity = em.merge(empCompetenceEntityListEmpCompetenceEntity);
            }
            List<ScheduleEntity> scheduleEntityList = employeeEntity.getScheduleEntityList();
            for (ScheduleEntity scheduleEntityListScheduleEntity : scheduleEntityList) {
                scheduleEntityListScheduleEntity.setEmpId(null);
                scheduleEntityListScheduleEntity = em.merge(scheduleEntityListScheduleEntity);
            }
            em.remove(employeeEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<EmployeeEntity> findEmployeeEntityEntities() {
        return findEmployeeEntityEntities(true, -1, -1);
    }

    public List<EmployeeEntity> findEmployeeEntityEntities(int maxResults, int firstResult) {
        return findEmployeeEntityEntities(false, maxResults, firstResult);
    }

    private List<EmployeeEntity> findEmployeeEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(EmployeeEntity.class));
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

    public EmployeeEntity findEmployeeEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(EmployeeEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getEmployeeEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<EmployeeEntity> rt = cq.from(EmployeeEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
