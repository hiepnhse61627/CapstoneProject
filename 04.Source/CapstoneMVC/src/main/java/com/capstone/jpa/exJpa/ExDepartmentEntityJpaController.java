package com.capstone.jpa.exJpa;

import com.capstone.entities.DepartmentEntity;
import com.capstone.jpa.DepartmentEntityJpaController;
import com.capstone.models.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

public class ExDepartmentEntityJpaController extends DepartmentEntityJpaController {

    public ExDepartmentEntityJpaController(EntityManagerFactory emf) {
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

    public void saveDepartment(DepartmentEntity Department) {
        try {
            EntityManager manager = getEntityManager();
            manager.getTransaction().begin();
            manager.merge(Department);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }
    }

    public void createDepartmentList(List<DepartmentEntity> departments) {
        totalLine = departments.size();
        currentLine = 0;

        EntityManager em = null;
        try {
            em = getEntityManager();
            for (DepartmentEntity department : departments) {
                try {
                    em.getTransaction().begin();

                    // Create department
                    TypedQuery<DepartmentEntity> queryDepartment = em.createQuery(
                            "SELECT c FROM DepartmentEntity c WHERE c.deptName = :name", DepartmentEntity.class);
                    queryDepartment.setParameter("name", department.getDeptName());

                    List<DepartmentEntity> std = queryDepartment.getResultList();
                    if (std.isEmpty()) {
                        em.persist(department);
                        em.flush();
                    }
                    em.getTransaction().commit();
                } catch (Exception e) {
                    System.out.println("department " + department.getDeptName() + "caused " + e.getMessage());
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


    public List<DepartmentEntity> findDepartmentsByName(String searchValue) {
        EntityManager em = getEntityManager();
        List<DepartmentEntity> result = null;

        try {
            String queryStr = "SELECT s FROM DepartmentEntity s" +
                    " WHERE s.deptName LIKE :name";
            TypedQuery<DepartmentEntity> query = em.createQuery(queryStr, DepartmentEntity.class);
            query.setParameter("name", "%" + searchValue + "%");

            result = query.getResultList();

        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    public List<DepartmentEntity> findDepartmentsByShortName(String searchValue) {
        EntityManager em = getEntityManager();
        List<DepartmentEntity> result = null;

        try {
            String queryStr = "SELECT s FROM DepartmentEntity s" +
                    " WHERE s.deptShortName LIKE :name";
            TypedQuery<DepartmentEntity> query = em.createQuery(queryStr, DepartmentEntity.class);
            query.setParameter("name", "%" + searchValue + "%");

            result = query.getResultList();

        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }


    public DepartmentEntity createDepartment(DepartmentEntity DepartmentEntity) {
        EntityManager em = null;

        try {
            em = getEntityManager();

            em.getTransaction().begin();
            em.persist(DepartmentEntity);
            em.flush();
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return DepartmentEntity;
    }

}
