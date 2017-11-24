package com.capstone.jpa.exJpa;

import com.capstone.entities.*;
import com.capstone.jpa.StudentEntityJpaController;
import com.capstone.models.Logger;
import com.capstone.services.CurriculumServiceImpl;
import com.capstone.services.DocumentStudentServiceImpl;
import com.capstone.services.ICurriculumService;

import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.swing.text.Document;
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

    public void saveStudent(StudentEntity student) {
        try {
            EntityManager manager = getEntityManager();
            manager.getTransaction().begin();
            for (DocumentStudentEntity doc : student.getDocumentStudentEntityList()) {
                if (doc.getId() == null) {
                    manager.persist(doc);
                    manager.flush();
                    manager.merge(doc);
                    manager.refresh(doc);
                }
            }
            manager.merge(student);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }
    }

    public void createStudentList(List<StudentEntity> students) {
        ICurriculumService curriculumService = new CurriculumServiceImpl();
        totalLine = students.size();
        currentLine = 0;

        EntityManager em = null;
        try {
            em = getEntityManager();
            for (StudentEntity student : students) {
                try {
                    em.getTransaction().begin();

                    // Create student
                    TypedQuery<StudentEntity> queryStudent = em.createQuery(
                            "SELECT c FROM StudentEntity c WHERE c.rollNumber = :rollNumber", StudentEntity.class);
                    queryStudent.setParameter("rollNumber", student.getRollNumber());

                    List<StudentEntity> std = queryStudent.getResultList();
                    if (std.isEmpty()) {
                        em.persist(student);
                        em.flush();
                    }

                    // Create document student
//                    if (student != null) {
//                        TypedQuery<DocumentStudentEntity> queryDocStudent = em.createQuery(
//                                "SELECT d FROM DocumentStudentEntity d" +
//                                        " WHERE d.studentId.id = :studentId" +
//                                        " AND d.curriculumId.id" + (student.getCurriculumId() != null ? " = :curriId" : " IS NULL") +
//                                        " AND d.documentId.id = :docId"
//                                , DocumentStudentEntity.class);
//                        queryDocStudent.setParameter("studentId", student.getStudentId().getId());
//                        queryDocStudent.setParameter("docId", student.getDocumentId().getId());
//                        if (student.getCurriculumId() != null) {
//                            queryDocStudent.setParameter("curriId", student.getCurriculumId().getId());
//                        }
//
//                        List<DocumentStudentEntity> docEntity = queryDocStudent.getResultList();
//                        if (docEntity.isEmpty()) {
//                            if (student.getCurriculumId() != null) {
//                                CurriculumEntity curriculumTemp = curriculumService.getCurriculumById(
//                                        student.getCurriculumId().getId());
//                                student.setCurriculumId(curriculumTemp);
//                            }
//
//                            em.persist(student);
//                        }
//                    }

                    em.getTransaction().commit();
                } catch (Exception e) {
                    System.out.println("Student " + student.getRollNumber() + "caused " + e.getMessage());
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

    public List<StudentEntity> findStudentByIsActivated() {
        EntityManager em = getEntityManager();
        try {
            String sqlString = "select distinct s.* from Student s, Marks m where s.Id = m.StudentId and m.IsActivated = 0";
            Query query = em.createNativeQuery(sqlString, StudentEntity.class);
            List<StudentEntity> objectList = query.getResultList();
            return objectList;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
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

    public StudentEntity findStudentByEmail(String email) {
        EntityManager em = getEntityManager();
        Query query = null;
        try {
            String sqlString = "SELECT s FROM StudentEntity s WHERE s.email = :email";
            query = em.createQuery(sqlString);
            query.setParameter("email", email);

            StudentEntity studentEntity = (StudentEntity) query.getSingleResult();

            return studentEntity;
        } catch (NoResultException nrEx) {
            System.out.println("Student with email " + email + " not found!");
            return null;
        } catch (NonUniqueResultException ex) {
            System.out.println("Student with email " + email + " more than one!");
            return (StudentEntity) query.getResultList().get(0);
        } catch (Exception e) {
            e.printStackTrace();
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

    public List<StudentEntity> findStudentsByFullNameOrRollNumber(String searchValue) {
        EntityManager em = getEntityManager();
        List<StudentEntity> result = null;

        try {
            String queryStr = "SELECT s FROM StudentEntity s" +
                    " WHERE s.fullName LIKE :fullName OR s.rollNumber LIKE :rollNumber";
            TypedQuery<StudentEntity> query = em.createQuery(queryStr, StudentEntity.class);
            query.setParameter("fullName", "%" + searchValue + "%");
            query.setParameter("rollNumber", "%" + searchValue + "%");

            result = query.getResultList();

        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    public StudentEntity cleanDocumentAndOldRollNumber(StudentEntity stu) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            stu = em.merge(stu);
            List<DocumentStudentEntity> docs = stu.getDocumentStudentEntityList();
            for (DocumentStudentEntity d : docs) {
                DocumentStudentEntity stuDoc = em.merge(d);
                em.remove(stuDoc);
                em.flush();
            }
            List<OldRollNumberEntity> olds = stu.getOldRollNumberEntityList();
            for (OldRollNumberEntity o : olds) {
                OldRollNumberEntity oldStu = em.merge(o);
                em.remove(oldStu);
                em.flush();
            }
            em.refresh(stu);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return stu;
    }

    public List<StudentEntity> getStudentByDocType(int type) {
        EntityManager em = getEntityManager();
        List<StudentEntity> result = null;

        try {
            if (type < 0) {
                String queryStr = "SELECT s FROM StudentEntity s";
                TypedQuery<StudentEntity> query = em.createQuery(queryStr, StudentEntity.class);
                result = query.getResultList();
            } else {
                String queryStr = "SELECT s FROM StudentEntity s JOIN s.documentStudentEntityList d WHERE d.documentId.docTypeId.id = :type";
                TypedQuery<StudentEntity> query = em.createQuery(queryStr, StudentEntity.class);
                query.setParameter("type", type);
                result = query.getResultList();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public StudentEntity createStudent(StudentEntity studentEntity) {
        EntityManager em = null;

        try {
            em = getEntityManager();

            em.getTransaction().begin();
            em.persist(studentEntity);
            em.flush();
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return studentEntity;
    }

    public List<StudentEntity> findStudentByProgramId(Integer programId) {
        EntityManager em = getEntityManager();
        List<StudentEntity> studentEntityList = new ArrayList<>();
        try {
            String sqlString = "SELECT s FROM StudentEntity s WHERE s.programId.id = :programId";
            Query query = em.createQuery(sqlString);
            query.setParameter("programId", programId);

            studentEntityList = query.getResultList();

            return studentEntityList;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


    public List<StudentEntity> getStudentByProgram(int programId) {
        EntityManager em = getEntityManager();
        List<StudentEntity> result = null;

        try {
            String queryStr = "SELECT s FROM StudentEntity s";
            if (programId != 0) {
                queryStr += " WHERE s.programId.id = :program";
            }
            TypedQuery<StudentEntity> query = em.createQuery(queryStr, StudentEntity.class);
            if (programId != 0) {
                query.setParameter("program", programId);
            }
            result = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
