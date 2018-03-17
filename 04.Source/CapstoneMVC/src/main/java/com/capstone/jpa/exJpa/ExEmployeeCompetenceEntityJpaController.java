package com.capstone.jpa.exJpa;

import com.capstone.entities.EmpCompetenceEntity;
import com.capstone.entities.EmployeeEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.jpa.EmpCompetenceEntityJpaController;
import com.capstone.jpa.EmployeeEntityJpaController;
import com.capstone.models.Logger;

import javax.persistence.*;
import java.util.List;

public class ExEmployeeCompetenceEntityJpaController extends EmpCompetenceEntityJpaController {

    public ExEmployeeCompetenceEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    private int currentLine = 0;
    private int totalLine = 0;

    public int getCurrentLine() {
        return currentLine;
    }

    public int getTotalLine() {
        return totalLine;
    }

    public void saveEmployeeCompetence(EmpCompetenceEntity empComp) {
        try {
            EntityManager manager = getEntityManager();
            manager.getTransaction().begin();

            manager.merge(empComp);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }
    }

    public void createEmployeeCompetenceList(List<EmpCompetenceEntity> employeeCompetences) {
        totalLine = employeeCompetences.size();
        currentLine = 0;

        EntityManager em = null;
        try {
            em = getEntityManager();
            for (EmpCompetenceEntity empCompetenceEntity : employeeCompetences) {
                try {
                    em.getTransaction().begin();

                    // Create empCompetenceEntity
                    TypedQuery<EmpCompetenceEntity> queryEmployee = em.createQuery(
                            "SELECT c FROM EmpCompetenceEntity c WHERE c.employeeId = :emp AND c.subjectId = :sub", EmpCompetenceEntity.class);
                    queryEmployee.setParameter("emp", empCompetenceEntity.getEmployeeId());
                    queryEmployee.setParameter("sub", empCompetenceEntity.getSubjectId());

                    List<EmpCompetenceEntity> std = queryEmployee.getResultList();
                    if (std.isEmpty()) {
                        em.persist(empCompetenceEntity);
                        em.flush();
                    }
                    em.getTransaction().commit();
                } catch (Exception e) {
                    System.out.println("empCompetenceEntity " + empCompetenceEntity.getId() + "caused " + e.getMessage());
                    e.printStackTrace();
                }

                currentLine++;

                System.out.println(currentLine + "-" + totalLine);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }

        totalLine = 0;
        currentLine = 0;
    }

    public List<EmpCompetenceEntity> findEmployeeCompetencesByEmployee(Integer empId) {
        EntityManager em = getEntityManager();
        List<EmpCompetenceEntity> result = null;

        try {
            if(empId!=null){
                String queryStr = "SELECT s FROM EmpCompetenceEntity s" +
                        " WHERE s.employeeId.id = :empId";
                TypedQuery<EmpCompetenceEntity> query = em.createQuery(queryStr, EmpCompetenceEntity.class);
                query.setParameter("empId", empId);

                result = query.getResultList();
            }

        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    public List<EmpCompetenceEntity> findEmployeeCompetencesBySubject(SubjectEntity sub) {
        EntityManager em = getEntityManager();
        List<EmpCompetenceEntity> result = null;

        try {
            String queryStr = "SELECT s FROM EmpCompetenceEntity s" +
                    " WHERE s.subjectId.id = :sub";
            TypedQuery<EmpCompetenceEntity> query = em.createQuery(queryStr, EmpCompetenceEntity.class);
            query.setParameter("sub", sub.getId());

            result = query.getResultList();

        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    public EmpCompetenceEntity createEmployeeCompetence(EmpCompetenceEntity employeeConpetenceEntity) {
        EntityManager em = null;

        try {
            em = getEntityManager();

            em.getTransaction().begin();
            em.persist(employeeConpetenceEntity);
            em.flush();
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return employeeConpetenceEntity;
    }

}
