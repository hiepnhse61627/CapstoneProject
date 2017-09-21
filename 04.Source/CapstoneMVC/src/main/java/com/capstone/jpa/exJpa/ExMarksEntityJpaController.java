package com.capstone.jpa.exJpa;

import com.capstone.entities.*;
import com.capstone.jpa.MarksEntityJpaController;
import com.capstone.jpa.exceptions.PreexistingEntityException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

public class ExMarksEntityJpaController extends MarksEntityJpaController {

    private int totalExistStudent;
    private int successSavedStudent;

    public int getTotalExistMarks() {return totalExistStudent;}

    public int getSuccessSavedMark() {return successSavedStudent;}

    public ExMarksEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public int findMarksByProperties(Integer courseId, Integer semesterId, Integer studentId, String subjectId, Double averageMark, String status) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            String sqlString = "SELECT COUNT(StudentId) FROM Marks WHERE CourseId = " + courseId +
                    " AND SemesterId = " + semesterId + " AND StudentId = " + studentId +
                    " AND AverageMark = " + averageMark + " AND Status = '" + status + "'";

            if (subjectId != null) {
                sqlString += " AND SubjectId = '" + subjectId + "'";
            }

            Query query = em.createNativeQuery(sqlString);
            return (int) query.getSingleResult();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void createMarks(List<MarksEntity> marks) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        this.totalExistStudent = marks.size();
        this.successSavedStudent = 0;
        try {
            em = getEntityManager();

            for (MarksEntity marksEntity : marks) {
                em.getTransaction().begin();
                CourseEntity courseId = marksEntity.getCourseId();
                if (courseId != null) {
                    courseId = em.getReference(courseId.getClass(), courseId.getId());
                    marksEntity.setCourseId(courseId);
                }
                RealSemesterEntity semesterId = marksEntity.getSemesterId();
                if (semesterId != null) {
                    semesterId = em.getReference(semesterId.getClass(), semesterId.getId());
                    marksEntity.setSemesterId(semesterId);
                }
                StudentEntity studentId = marksEntity.getStudentId();
                if (studentId != null) {
                    studentId = em.getReference(studentId.getClass(), studentId.getId());
                    marksEntity.setStudentId(studentId);
                }
                SubjectMarkComponentEntity subjectId = marksEntity.getSubjectId();
                if (subjectId != null) {
                    subjectId = em.getReference(subjectId.getClass(), subjectId.getSubjectId());
                    marksEntity.setSubjectId(subjectId);
                }

                // Check marks in database
                int entityDB =
                        findMarksByProperties(marksEntity.getCourseId().getId(), marksEntity.getSemesterId().getId(),
                                marksEntity.getStudentId().getId(),
                                marksEntity.getSubjectId() != null ? marksEntity.getSubjectId().getSubjectId() : null,
                                marksEntity.getAverageMark(), marksEntity.getStatus());

                if (entityDB == 0) {
                    em.persist(marksEntity);
                } else {
                    // do nothing
                }

                if (courseId != null) {
                    courseId.getMarksList().add(marksEntity);
                    courseId = em.merge(courseId);
                }
                if (semesterId != null) {
                    semesterId.getMarksList().add(marksEntity);
                    semesterId = em.merge(semesterId);
                }
                if (studentId != null) {
                    studentId.getMarksList().add(marksEntity);
                    studentId = em.merge(studentId);
                }
                if (subjectId != null) {
                    subjectId.getMarksList().add(marksEntity);
                    subjectId = em.merge(subjectId);

                }
                em.getTransaction().commit();
                ++this.successSavedStudent;
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
