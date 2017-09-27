package com.capstone.jpa.exJpa;

import com.capstone.entities.*;
import com.capstone.jpa.MarksEntityJpaController;
import com.capstone.jpa.exceptions.PreexistingEntityException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.ArrayList;
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
                    courseId.getMarksEntityList().add(marksEntity);
                    courseId = em.merge(courseId);
                }
                if (semesterId != null) {
                    semesterId.getMarksEntityList().add(marksEntity);
                    semesterId = em.merge(semesterId);
                }
                if (studentId != null) {
                    studentId.getMarksEntityList().add(marksEntity);
                    studentId = em.merge(studentId);
                }
                if (subjectId != null) {
                    subjectId.getMarksEntityList().add(marksEntity);
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

    public List<MarksEntity> getMarksByConditions(String semesterId, String subjectId, String searchKey) {
        List<MarksEntity> marks = new ArrayList<>();
        EntityManager em = null;

        try {
            em = getEntityManager();

            //Create criteria builder
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();

            Root<MarksEntity> marksEntityRoot = criteriaQuery.from(MarksEntity.class);
            Predicate predicate = null;

            // Semester Name condition
            Predicate semesterNamePredicate = null;
            if (!semesterId.equals("0")) {
                Expression<Integer> semesterExpression = marksEntityRoot.get("semesterId").get("id");
                semesterNamePredicate = criteriaBuilder.equal(semesterExpression, Integer.parseInt(semesterId));
                predicate = predicate == null ? semesterNamePredicate : criteriaBuilder.and(predicate, semesterNamePredicate);
            }

            // Subject Component Id Condition
            Predicate subjectComponentPredicate = null;
            if (!subjectId.equals("0")) {
                Expression<String> subjectExpression = marksEntityRoot.get("subjectId").get("subjectId");
                subjectComponentPredicate = criteriaBuilder.equal(subjectExpression, subjectId);
                predicate = predicate == null ? subjectComponentPredicate : criteriaBuilder.and(predicate, subjectComponentPredicate);
            }

            // User's search keys condition
            Predicate searchKeyPredicate = null;
            if (searchKey != null && !searchKey.isEmpty()) {
                Expression<String> studentFullnameExpression = marksEntityRoot.get("studentId").get("fullName");
                Expression<String> studentRollNumberExpression = marksEntityRoot.get("studentId").get("rollNumber");
                Expression<String> classExpression = marksEntityRoot.get("courseId").get("clazz");
                Predicate fullNamePredicate = criteriaBuilder.like(studentFullnameExpression, searchKey);
                Predicate rollNumberPredicate = criteriaBuilder.like(studentRollNumberExpression, searchKey);
                Predicate classPredicate = criteriaBuilder.like(classExpression, searchKey);

                searchKeyPredicate = criteriaBuilder.or(fullNamePredicate, rollNumberPredicate, classPredicate);
                predicate = predicate == null ? searchKeyPredicate : criteriaBuilder.and(predicate, searchKeyPredicate);
            }

            if (predicate != null) {
                criteriaQuery.where(predicate);
            } else {
                criteriaQuery.select(marksEntityRoot);
            }

            Query query = em.createQuery(criteriaQuery);
            marks = query.getResultList();
        } finally {
            em.close();
        }
        return marks;
    }
}
