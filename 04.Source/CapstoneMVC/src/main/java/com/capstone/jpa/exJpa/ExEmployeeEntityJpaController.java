package com.capstone.jpa.exJpa;

import com.capstone.entities.EmployeeEntity;
import com.capstone.jpa.EmployeeEntityJpaController;
import com.capstone.models.Logger;

import javax.persistence.*;
import java.util.List;

public class ExEmployeeEntityJpaController extends EmployeeEntityJpaController {

    public ExEmployeeEntityJpaController(EntityManagerFactory emf) {
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

    public void saveEmployee(EmployeeEntity employee) {
        try {
            EntityManager manager = getEntityManager();
            manager.getTransaction().begin();
//            for (DocumentEmployeeEntity doc : employee.getDocumentEmployeeEntityList()) {
//                if (doc.getId() == null) {
//                    manager.persist(doc);
//                    manager.flush();
//                    manager.merge(doc);
//                    manager.refresh(doc);
//                }
//            }
            manager.merge(employee);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }
    }

    public void createEmployeeList(List<EmployeeEntity> employees) {
        totalLine = employees.size();
        currentLine = 0;

        EntityManager em = null;
        try {
            em = getEntityManager();
            for (EmployeeEntity Employee : employees) {
                try {
                    em.getTransaction().begin();

                    // Create Employee
                    TypedQuery<EmployeeEntity> queryEmployee = em.createQuery(
                            "SELECT c FROM EmployeeEntity c WHERE c.code = :code", EmployeeEntity.class);
                    queryEmployee.setParameter("code", Employee.getCode());

                    List<EmployeeEntity> std = queryEmployee.getResultList();
                    if (std.isEmpty()) {
                        em.persist(Employee);
                        em.flush();
                    }
                    em.getTransaction().commit();
                } catch (Exception e) {
                    System.out.println("Employee " + Employee.getCode() + "caused " + e.getMessage());
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

    public EmployeeEntity findEmployeeByCode(String code) {
        EntityManager em = getEntityManager();
        EmployeeEntity EmployeeEntity = new EmployeeEntity();
        try {
            String sqlString = "SELECT s FROM EmployeeEntity s WHERE s.code = :code";
            Query query = em.createQuery(sqlString);
            query.setParameter("code", code);

            EmployeeEntity = (EmployeeEntity) query.getSingleResult();

            return EmployeeEntity;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public EmployeeEntity findEmployeesByShortName(String searchValue) {
        EntityManager em = getEntityManager();
        List<EmployeeEntity> results = null;
        EmployeeEntity aEmployee = null;
        try {
            String queryStr = "SELECT s FROM EmployeeEntity s" +
                    " WHERE s.emailFE LIKE :shortName OR  s.emailEDU LIKE :shortName";
            TypedQuery<EmployeeEntity> query = em.createQuery(queryStr, EmployeeEntity.class);
            query.setParameter("shortName", "%" + searchValue + "%");

            results = query.getResultList();

            if (results.size() > 1) {
                for (EmployeeEntity emp : results) {
                    if (emp.getEmailFE().substring(emp.getEmailFE().indexOf(searchValue) + searchValue.length()).indexOf("@") == 0
                            || emp.getEmailEDU().substring(emp.getEmailEDU().indexOf(searchValue) + searchValue.length()).indexOf("@") == 0) {
                        aEmployee = emp;
                    } else {
                        if (Character.isDigit(emp.getEmailFE().substring(emp.getEmailEDU().indexOf(searchValue) + searchValue.length()).substring(0, 1).charAt(0))
                                || Character.isDigit(emp.getEmailFE().substring(emp.getEmailFE().indexOf(searchValue) + searchValue.length()).substring(0, 1).charAt(0))) {
                            aEmployee = emp;
                        }
                    }
                }
            } else {
                if (results.size() == 1) {
                    aEmployee = results.get(0);

                }
            }


        } finally {
            if (em != null) {
                em.close();
            }
        }

        return aEmployee;
    }

    public List<EmployeeEntity> findEmployeesByFullName(String searchValue) {
        EntityManager em = getEntityManager();
        List<EmployeeEntity> result = null;

        try {
            String queryStr = "SELECT s FROM EmployeeEntity s" +
                    " WHERE s.fullName LIKE :fullName";
            TypedQuery<EmployeeEntity> query = em.createQuery(queryStr, EmployeeEntity.class);
            query.setParameter("fullName", "%" + searchValue + "%");

            result = query.getResultList();

        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    public EmployeeEntity createEmployee(EmployeeEntity employeeEntity) {
        EntityManager em = null;

        try {
            em = getEntityManager();

            em.getTransaction().begin();
            em.persist(employeeEntity);
            em.flush();
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return employeeEntity;
    }

}
