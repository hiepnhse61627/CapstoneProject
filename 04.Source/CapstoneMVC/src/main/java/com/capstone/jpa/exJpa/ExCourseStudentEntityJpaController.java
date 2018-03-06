package com.capstone.jpa.exJpa;

import com.capstone.entities.*;
import com.capstone.jpa.CourseStudentEntityJpaController;
import com.capstone.models.Logger;

import javax.persistence.*;
import java.util.List;

public class ExCourseStudentEntityJpaController extends CourseStudentEntityJpaController {

    public ExCourseStudentEntityJpaController(EntityManagerFactory emf) {
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

    public void saveCourseStudent(CourseStudentEntity CourseStudent) {
        try {
            EntityManager manager = getEntityManager();
            manager.getTransaction().begin();
            manager.merge(CourseStudent);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }
    }

    public void createCourseStudentList(List<CourseStudentEntity> courseStudents) {
        totalLine = courseStudents.size();
        currentLine = 0;

        EntityManager em = null;
        try {
            em = getEntityManager();
            for (CourseStudentEntity courseStudent : courseStudents) {
                try {
                    em.getTransaction().begin();

                    // Create CourseStudent
                    TypedQuery<CourseStudentEntity> queryCourseStudent = em.createQuery(
                            "SELECT c FROM CourseStudentEntity c " +
                                    "WHERE (c.studentId= :student)" +
                                    "AND (c.courseId= :course)", CourseStudentEntity.class);
                    queryCourseStudent.setParameter("student", courseStudent.getStudentId());
                    queryCourseStudent.setParameter("course", courseStudent.getCourseId());

                    List<CourseStudentEntity> std = queryCourseStudent.getResultList();
                    if (std.isEmpty()) {
                        em.persist(courseStudent);
                        em.flush();
                    }else{
                        System.out.println(courseStudent);
                    }
                    em.getTransaction().commit();
                } catch (Exception e) {
                    System.out.println("CourseStudent " + courseStudent.getId() + "caused " + e.getMessage());
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

    public List<CourseStudentEntity> findCourseStudentByGroupNameAndCourse(String groupName, CourseEntity course) {
        EntityManager em = getEntityManager();
        try {
            String sqlString = "SELECT c FROM CourseStudentEntity c " +
                    "WHERE (c.groupName= :groupName)" +
                    "AND (c.courseId= :course)";
            Query query = em.createQuery(sqlString);
            query.setParameter("groupName", groupName);
            query.setParameter("course", course);

            List<CourseStudentEntity> std  = query.getResultList();

            return std;

        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public CourseStudentEntity findCourseStudentByCourseAndStudent(CourseEntity course, StudentEntity student) {
        EntityManager em = getEntityManager();
        CourseStudentEntity CourseStudentEntity = new CourseStudentEntity();
        try {
            String sqlString = "SELECT c FROM CourseStudentEntity c " +
                    "WHERE (c.courseId = :course)" +
                    "AND (c.studentId= :student)";
            Query query = em.createQuery(sqlString);
            query.setParameter("course", course);
            query.setParameter("student", student);

            CourseStudentEntity = (CourseStudentEntity) query.getSingleResult();

            return CourseStudentEntity;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CourseStudentEntity> findCourseStudentByGroupName(String groupName) {
        EntityManager em = getEntityManager();
        try {
            String sqlString = "SELECT c FROM CourseStudentEntity c " +
                    "WHERE (c.groupName = :groupName)";
            Query query = em.createQuery(sqlString);
            query.setParameter("groupName", groupName);

            List<CourseStudentEntity> std  = query.getResultList();

            return std;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


    public CourseStudentEntity createCourseStudent(CourseStudentEntity CourseStudentEntity) {
        EntityManager em = null;

        try {
            em = getEntityManager();

            em.getTransaction().begin();
            em.persist(CourseStudentEntity);
            em.flush();
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return CourseStudentEntity;
    }

    public List<CourseStudentEntity> findCourseStudentByStudent(StudentEntity studentEntity) {
        EntityManager em = getEntityManager();
        try {
            String sqlString = "SELECT c FROM CourseStudentEntity c " +
                    "WHERE (c.studentId = :student)";
            Query query = em.createQuery(sqlString);
            query.setParameter("student", studentEntity);

            List<CourseStudentEntity> std  = query.getResultList();

            return std;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


}
