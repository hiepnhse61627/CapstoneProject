package com.capstone.jpa.exJpa;

import com.capstone.entities.*;
import com.capstone.jpa.StudentEntityJpaController;
import com.capstone.models.Logger;
import com.capstone.services.CurriculumServiceImpl;
import com.capstone.services.DocumentStudentServiceImpl;
import com.capstone.services.ICurriculumService;
import org.apache.poi.ss.formula.functions.T;

import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.swing.text.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<StudentEntity> findStudentBySemesterId(int semesterId) {
        EntityManager em = getEntityManager();
        List<StudentEntity> result = new ArrayList<>();
        try {

            Query query = em.createQuery("SELECT DISTINCT a.studentId " +
                    "FROM StudentStatusEntity a WHERE a.semesterId.id = :semesterId");
            query.setParameter("semesterId", semesterId);
            result = query.getResultList();

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (em != null)
                em.close();
        }
        return result;
    }

    public List<Object[]> getSubjectMarkComByStudentWithoutNotStart(StudentEntity studentEntity) {
        EntityManager em = getEntityManager();
        List<Object[]> result = null;

        try {
            String queryStr = "SELECT m.StudentId, sm.SubjectId, r.Semester, m.Status FROM Marks m  " +
                    "Inner Join Subject_MarkComponent sm " +
                    "On m.SubjectMarkComponentId = sm.Id " +
                    "Inner Join RealSemester r " +
                    "On m.SemesterId = r.Id " +
                    "AND m.StudentId = ? "+
                    "WHERE m.Status != 'NotStart'";
            Query query = em.createNativeQuery(queryStr);
            query.setParameter(1, studentEntity.getId());
            result = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<Object[]> getSubjectsWithCreditsByStudent(int studentId) {
        EntityManager em = getEntityManager();
        List<Object[]> result = null;

        try {
            String queryStr = "Select SubjectId, SubjectCredits, b.CurriculumId From " +
                    "(SELECT CurriculumId From Document_Student Where StudentId= ?) As a " +
                    "Inner Join " +
                    "(Select sc.SubjectId, sc.SubjectCredits, sc.CurriculumId " +
                    "From Subject_Curriculum sc " +
                    "Inner Join Subject s on sc.SubjectId = s.Id) As b " +
                    "on a.CurriculumId =b.CurriculumId";
            Query query = em.createNativeQuery(queryStr);
            query.setParameter(1, studentId);
            result = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<Object[]> getAllSubjectsStudentsReStudyInSameSemester(int semesterId) {
        EntityManager em = getEntityManager();
        List<Object[]> result = null;

        try {
            String queryStr = "Select s.RollNumber, s.FullName,sm.SubjectId From Marks m " +
                    "Inner Join Student s on m.StudentId = s.Id " +
                    "Inner Join RealSemester r On m.SemesterId = r.Id " +
                    "Inner Join Subject_MarkComponent sm On m.subjectMarkComponentId = sm.Id " +
                    "Where r.Id=? Group by s.RollNumber,sm.SubjectId,s.FullName HAVING count(*) > 1 Order By RollNumber";
            Query query = em.createNativeQuery(queryStr);
            query.setParameter(1, semesterId);
            result = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<StudentEntity> getStudentsFromMarksBySemester(int semesterId) {
        EntityManager em = null;
        List<StudentEntity> resultList = new ArrayList<>();
        try {
            em = getEntityManager();
            Query query = em.createQuery("SELECT DISTINCT m.studentId FROM MarksEntity m " +
                    "WHERE m.isActivated = true AND m.semesterId.id = :semesterId");
            query.setParameter("semesterId", semesterId);

            resultList = query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return resultList;
    }

    public List<StudentEntity> getStudentBySemesterIdAndStatus(int semesterId, List<String> statusList) {
        EntityManager em = getEntityManager();
        List<StudentEntity> result = new ArrayList<>();
        try {

            Query query = em.createQuery("SELECT DISTINCT a.studentId " +
                    "FROM StudentStatusEntity a WHERE a.semesterId.id = :semesterId AND a.status IN :statusList ");
            query.setParameter("semesterId", semesterId);
            query.setParameter("statusList", statusList);
            result = query.getResultList();

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (em != null)
                em.close();
        }
        return result;
    }


    public List<StudentEntity> getStudentBySemesterIdAndProgram(int semesterId, int programId) {
            EntityManager em = getEntityManager();
        List<StudentEntity> result = new ArrayList<>();
        try {
            Query query = null;
            if (programId != -1) {
                query = em.createQuery("SELECT DISTINCT a.studentId " +
                        "FROM StudentStatusEntity a WHERE a.semesterId.id = :semesterId" +
                        " AND a.studentId.programId.id = :programId ");
                query.setParameter("programId", programId);
            } else {
                query = em.createQuery("SELECT DISTINCT a.studentId " +
                        "FROM StudentStatusEntity a WHERE a.semesterId.id = :semesterId");

            }
            query.setParameter("semesterId", semesterId);
            result = query.getResultList();

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (em != null)
                em.close();
        }
        return result;
    }


    public void myUpdateStudent(StudentEntity student) {
        try {
            EntityManager manager = getEntityManager();
            manager.getTransaction().begin();
            manager.merge(student);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }
    }


    public boolean myBulkUpdateStudents(List<StudentEntity> studentList) {
        EntityManager em = null;
        int bulkSize = 1000;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            for (int i = 0; i < studentList.size(); i++) {
                if(i > 0 && i % bulkSize == 0){
                    em.flush();
                    em.clear();
                    em.getTransaction().commit();
                    em.getTransaction().begin();
                }
                StudentEntity student = studentList.get(i);
                em.merge(student);
                System.out.println("Update - " + (i + 1));
            }

            //đẩy xuống những phần còn lại
            em.flush();
            em.getTransaction().commit();
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
            return false;
        }finally {
            if(em != null){
                em.close();
            }
        }
        return true;
    }

    public void refresh(StudentEntity s) {
        EntityManager em = getEntityManager();
        em.refresh(s);
    }

}


