package com.capstone.jpa.exJpa;

import com.capstone.entities.DepartmentEntity;
import com.capstone.entities.SubjectDepartmentEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.jpa.SubjectDepartmentEntityJpaController;
import com.capstone.models.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

public class ExSubjectDepartmentEntityJpaController extends SubjectDepartmentEntityJpaController {

    public ExSubjectDepartmentEntityJpaController(EntityManagerFactory emf) {
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

    public void saveSubjectDepartment(SubjectDepartmentEntity SubjectDepartment) {
        try {
            EntityManager manager = getEntityManager();
            manager.getTransaction().begin();
            manager.merge(SubjectDepartment);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }
    }

    public void createSubjectDepartmentList(List<SubjectDepartmentEntity> SubjectDepartments) {
        totalLine = SubjectDepartments.size();
        currentLine = 0;

        EntityManager em = null;
        try {
            em = getEntityManager();
            for (SubjectDepartmentEntity SubjectDepartment : SubjectDepartments) {
                try {
                    em.getTransaction().begin();

                    // Create SubjectDepartment
                    TypedQuery<SubjectDepartmentEntity> querySubjectDepartment = em.createQuery(
                            "SELECT c FROM SubjectDepartmentEntity c WHERE c.deptId.deptName = :deptName AND c.subjectId.name = :subName ", SubjectDepartmentEntity.class);
                    querySubjectDepartment.setParameter("deptName", SubjectDepartment.getDeptId().getDeptName());
                    querySubjectDepartment.setParameter("subName", SubjectDepartment.getSubjectId().getName());

                    List<SubjectDepartmentEntity> std = querySubjectDepartment.getResultList();
                    if (std.isEmpty()) {
                        em.persist(SubjectDepartment);
                        em.flush();
                    }
                    em.getTransaction().commit();
                } catch (Exception e) {
                    System.out.println("SubjectDepartment " + SubjectDepartment.getId() + "caused " + e.getMessage());
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


    public List<SubjectDepartmentEntity> findSubjectDepartmentsByDepartment(DepartmentEntity departmentEntity) {
        EntityManager em = getEntityManager();
        List<SubjectDepartmentEntity> result = null;

        try {
            String queryStr = "SELECT s FROM SubjectDepartmentEntity s" +
                    " WHERE s.deptId.deptName LIKE :name";
            TypedQuery<SubjectDepartmentEntity> query = em.createQuery(queryStr, SubjectDepartmentEntity.class);
            query.setParameter("name", "%" + departmentEntity.getDeptName() + "%");

            result = query.getResultList();

        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    public List<SubjectDepartmentEntity> findSubjectDepartmentsBySubject(SubjectEntity subjectEntity) {
        EntityManager em = getEntityManager();
        List<SubjectDepartmentEntity> result = null;

        try {
            String queryStr = "SELECT s FROM SubjectDepartmentEntity s" +
                    " WHERE s.subjectId.name LIKE :name";
            TypedQuery<SubjectDepartmentEntity> query = em.createQuery(queryStr, SubjectDepartmentEntity.class);
            query.setParameter("name", "%" + subjectEntity.getName() + "%");

            result = query.getResultList();

        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    public List<SubjectDepartmentEntity> findSubjectDepartmentsBySubjectAndDepartment(SubjectEntity subjectEntity, DepartmentEntity departmentEntity) {
        EntityManager em = getEntityManager();
        List<SubjectDepartmentEntity> result = null;

        try {
            String queryStr = "SELECT s FROM SubjectDepartmentEntity s" +
                    " WHERE s.subjectId.name LIKE :subName AND s.deptId.deptName LIKE :deptName";
            TypedQuery<SubjectDepartmentEntity> query = em.createQuery(queryStr, SubjectDepartmentEntity.class);
            query.setParameter("subName", "%" + subjectEntity.getName() + "%");
            query.setParameter("deptName", "%" + departmentEntity.getDeptName() + "%");

            result = query.getResultList();

        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    public SubjectDepartmentEntity createSubjectDepartment(SubjectDepartmentEntity SubjectDepartmentEntity) {
        EntityManager em = null;

        try {
            em = getEntityManager();

            em.getTransaction().begin();
            em.persist(SubjectDepartmentEntity);
            em.flush();
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return SubjectDepartmentEntity;
    }

}
