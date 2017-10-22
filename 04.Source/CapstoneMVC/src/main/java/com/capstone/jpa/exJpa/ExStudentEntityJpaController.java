package com.capstone.jpa.exJpa;

import com.capstone.entities.DocumentStudentEntity;
import com.capstone.entities.MarksEntity;
import com.capstone.entities.RealSemesterEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.jpa.StudentEntityJpaController;
import com.capstone.models.Logger;
import com.capstone.services.DocumentStudentServiceImpl;
import com.capstone.services.IDocumentStudentService;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class ExStudentEntityJpaController extends StudentEntityJpaController {

    public ExStudentEntityJpaController(EntityManagerFactory emf) {
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

    public void createStudentList(List<DocumentStudentEntity> students) {
        totalLine = students.size();
        currentLine = 0;

        EntityManager em = null;
        try {
            em = getEntityManager();
            for (DocumentStudentEntity docStudent : students) {
                try {
                    em.getTransaction().begin();

                    // Create student
                    TypedQuery<StudentEntity> queryStudent = em.createQuery(
                            "SELECT c FROM StudentEntity c WHERE c.rollNumber = :rollNumber", StudentEntity.class);
                    queryStudent.setParameter("rollNumber", docStudent.getStudentId().getRollNumber());

                    List<StudentEntity> std = queryStudent.getResultList();
                    if (std.isEmpty()) {
                        em.persist(docStudent.getStudentId());
                    } else {
                        docStudent.setStudentId(std.get(0));
                    }

                    // Create document student
                    if (docStudent.getCurriculumId() != null && docStudent.getDocumentId() != null) {
                        TypedQuery<DocumentStudentEntity> queryDocStudent = em.createQuery(
                                "SELECT d FROM DocumentStudentEntity d" +
                                        " WHERE d.studentId.id = :studentId" +
                                        " AND d.curriculumId.id = :curriId" +
                                        " AND d.documentId.id = :docId"
                                , DocumentStudentEntity.class);
                        queryDocStudent.setParameter("studentId", docStudent.getStudentId().getId());
                        queryDocStudent.setParameter("curriId", docStudent.getCurriculumId().getId());
                        queryDocStudent.setParameter("docId", docStudent.getDocumentId().getId());

                        List<DocumentStudentEntity> docEntity = queryDocStudent.getResultList();
                        if (docEntity.isEmpty()) {
                            em.persist(docStudent);
                        }
                    }

                    em.getTransaction().commit();
                } catch (Exception e) {
                    System.out.println("Student " + docStudent.getStudentId().getRollNumber() + "caused " + e.getMessage());
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

    public StudentEntity findStudentByRollNumber(String rollNumber) {
        EntityManager em = getEntityManager();
        StudentEntity studentEntity = new StudentEntity();
        try {
            String sqlString = "SELECT s FROM StudentEntity s WHERE s.rollNumber = :rollNumber";
            Query query = em.createQuery(sqlString);
            query.setParameter("rollNumber", rollNumber);

            studentEntity = (StudentEntity) query.getSingleResult();

            return studentEntity;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


    public List<StudentEntity> findStudentByProgramName(String programName) {
        EntityManager em = getEntityManager();
        List<StudentEntity> students = new ArrayList<>();
        try {
            String sqlString = "SELECT s FROM StudentEntity s WHERE s.rollNumber LIKE :rollNumber";
            Query query = em.createQuery(sqlString);
            query.setParameter("rollNumber", programName + "%");

            students = query.getResultList();

            return students;
        } catch (NoResultException nrEx) {
            return null;
		}
	}	
			
    public List<StudentEntity> findStudentsByValue(String value) {
        EntityManager em = getEntityManager();
        List<StudentEntity> result = null;

        try {
            String queryStr = "SELECT s FROM StudentEntity s" +
                    " WHERE s.fullName LIKE :fullName OR s.rollNumber LIKE :rollNumber";
            TypedQuery<StudentEntity> query = em.createQuery(queryStr, StudentEntity.class);
            query.setParameter("fullName", "%" + value + "%");
            query.setParameter("rollNumber", "%" + value + "%");

            result = query.getResultList();

        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }
}
