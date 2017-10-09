package com.capstone.jpa.exJpa;

import com.capstone.entities.*;
import com.capstone.jpa.MarksEntityJpaController;
import com.capstone.jpa.exceptions.PreexistingEntityException;
import com.capstone.models.Ultilities;
import com.capstone.services.RealSemesterServiceImpl;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.o;

public class ExMarksEntityJpaController extends MarksEntityJpaController {

    private int totalExistStudent;
    private int successSavedStudent;
    private List<RealSemesterEntity> realSemesters;

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
        int batchSize = 1000;
        try {
            em = getEntityManager();

            em.getTransaction().begin();
            for (int i = 0; i < marks.size(); i++) {
                MarksEntity marksEntity = marks.get(i);
                em.persist(marksEntity);

                if (i > 0 && i % batchSize == 0) {
                    em.flush();
                    em.clear();
                }

                ++this.successSavedStudent;
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<MarksEntity> getMarksByConditions(String semesterId, String subjectId, String searchKey) {
        if (realSemesters == null)
            realSemesters = Ultilities.SortSemesters(new RealSemesterServiceImpl().getAllSemester());

        List<MarksEntity> marks = new ArrayList<>();
        int row = -1;
        for (RealSemesterEntity r : realSemesters) {
            if (r.getId() == Integer.parseInt(semesterId)) {
                row = realSemesters.indexOf(r);
            }
        }

        if (row < 0) {
            marks = buildQuery(semesterId, subjectId, searchKey);
        } else {
            for (int i = 0; i < row + 1; i++) {
                semesterId = realSemesters.get(i).getId().toString();
                List<MarksEntity> finalMarks = marks;
                buildQuery(semesterId, subjectId, searchKey).forEach(o -> {
                    if (!finalMarks.contains((MarksEntity) o)) {
                        finalMarks.add((MarksEntity) o);
                    }
                });
                marks = finalMarks;
            }
        }
        return marks;
    }

    private List<MarksEntity> buildQuery(String semesterId, String subjectId, String searchKey) {
        EntityManager em = null;
        List<MarksEntity> marks = new ArrayList<>();
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
                Expression<String> classExpression = marksEntityRoot.get("courseId").get("class1");
                Predicate fullNamePredicate = criteriaBuilder.like(studentFullnameExpression, "%" + searchKey + "%");
                Predicate rollNumberPredicate = criteriaBuilder.like(studentRollNumberExpression, "%" + searchKey + "%");
                Predicate classPredicate = criteriaBuilder.like(classExpression, "%" + searchKey + "%");

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

    public List<MarksEntity> getStudentMarksById(int stuId) {
        EntityManager em = getEntityManager();
        TypedQuery<MarksEntity> query = em.createQuery("SELECT m FROM MarksEntity m WHERE m.studentId.id = :sid", MarksEntity.class);
        query.setParameter("sid", stuId);
        return query.getResultList();
    }

    public int countMarksByCourseId(int courseId) {
        EntityManager em = getEntityManager();
        int count = 0;

        try {
            String queryStr = "SELECT COUNT(m) FROM MarksEntity m WHERE m.courseId.id = :courseId";
            TypedQuery<Integer> queryCountMarks = em.createQuery(queryStr, Integer.class);
            queryCountMarks.setParameter("courseId", courseId);
            count = ((Number) queryCountMarks.getSingleResult()).intValue();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }
}
