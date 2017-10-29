package com.capstone.jpa.exJpa;

import com.capstone.entities.*;
import com.capstone.jpa.MarksEntityJpaController;
import com.capstone.jpa.exceptions.PreexistingEntityException;
import com.capstone.models.Ultilities;
import com.capstone.services.IProgramService;
import com.capstone.services.IRealSemesterService;
import com.capstone.services.ProgramServiceImpl;
import com.capstone.services.RealSemesterServiceImpl;
import org.apache.commons.lang3.reflect.Typed;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.o;

public class ExMarksEntityJpaController extends MarksEntityJpaController {

    private int totalExistStudent = 0;
    private int successSavedStudent = 0;
    private List<RealSemesterEntity> realSemesters;

    public int getTotalExistMarks() {
        return totalExistStudent;
    }

    public int getSuccessSavedMark() {
        return successSavedStudent;
    }

    public ExMarksEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public List<MarksEntity> findMarksByProperties(MarksEntity marksEntity) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            String sqlString = "SELECT m FROM MarksEntity m " +
                                "WHERE m.studentId.id = :studentId " +
                                  "AND m.subjectMarkComponentId.id = :subjectMarkComponentId " +
                                  "AND m.semesterId.id = :semesterId";
            Query query = em.createQuery(sqlString);
            query.setParameter("studentId", marksEntity.getStudentId().getId());
            query.setParameter("subjectMarkComponentId", marksEntity.getSubjectMarkComponentId().getId());
            query.setParameter("semesterId", marksEntity.getSemesterId().getId());

            List<MarksEntity> result = (List<MarksEntity>) query.getResultList();

            return result;
        } catch (NoResultException nrEx) {
            System.out.println("No records were found with: " + marksEntity.getStudentId().getRollNumber());
            return null;
        } catch (NonUniqueResultException nuEx) {
            System.out.println("Many records were found with: " + marksEntity.getStudentId().getRollNumber());
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public int countAllMarks() {
        EntityManager em = null;
        int count = 0;

        try {
            em = getEntityManager();
            String queryStr = "SELECT COUNT(m) FROM MarksEntity m";
            TypedQuery<Integer> query = em.createQuery(queryStr, Integer.class);

            count = ((Number) query.getSingleResult()).intValue();
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return count;
    }

    public void createMarks(List<MarksEntity> marks) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        this.totalExistStudent = marks.size();
        this.successSavedStudent = 0;
        int batchSize = 1000;
        try {
            em = getEntityManager();

            for (int i = 0; i < marks.size(); i++) {
                em.getTransaction().begin();
                MarksEntity markEntity = marks.get(i);
                List<MarksEntity> marksInDB = findMarksByProperties(markEntity);
                if (marksInDB != null && !marksInDB.isEmpty()) {
                    MarksEntity markDB = new MarksEntity();
                    for (MarksEntity tmp : marksInDB) {
                        if (tmp.getStatus().contains("Studying")) {
                            markDB = tmp;
                            break;
                        }
                    }

                    if (markDB.getId() != null) {
                        markEntity.setId(markDB.getId());
                        em.merge(markEntity);
                    } else {
                        if (marksInDB.size() < 2) {
                            em.persist(markEntity);
                        }
                    }
                } else {
                    em.persist(markEntity);
                }

                if (i > 0 && i % batchSize == 0) {
                    em.flush();
                    em.clear();
                }

                ++this.successSavedStudent;
                em.getTransaction().commit();
            }
        } catch (Exception ex) {
            em.getTransaction().rollback();
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        this.totalExistStudent = 0;
        this.successSavedStudent = 0;
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
                Expression<String> subjectExpression = marksEntityRoot.get("subjectMarkComponentId").get("subjectId").get("id");
                Expression<Integer> markComponentExpression = marksEntityRoot.get("subjectMarkComponentId").get("markComponentId").get("id");
                Predicate subjectPredicate = criteriaBuilder.equal(subjectExpression, subjectId);
                Predicate markComponentPredicate = criteriaBuilder.equal(markComponentExpression, 6);
                subjectComponentPredicate = criteriaBuilder.and(subjectPredicate, markComponentPredicate);
                predicate = predicate == null ? subjectComponentPredicate : criteriaBuilder.and(predicate, subjectComponentPredicate);
            }

            // User's search keys condition
            Predicate searchKeyPredicate = null;
            if (searchKey != null && !searchKey.isEmpty()) {
                Expression<String> studentFullnameExpression = marksEntityRoot.get("studentId").get("fullName");
                Expression<String> studentRollNumberExpression = marksEntityRoot.get("studentId").get("rollNumber");
                Predicate fullNamePredicate = criteriaBuilder.like(studentFullnameExpression, "%" + searchKey + "%");
                Predicate rollNumberPredicate = criteriaBuilder.like(studentRollNumberExpression, "%" + searchKey + "%");

                searchKeyPredicate = criteriaBuilder.or(fullNamePredicate, rollNumberPredicate);
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

    public List<MarksEntity> getMarkByProgramAndSemester(int programId, int semesterId) {
        List<MarksEntity> result = null;
        EntityManager em = null;
        boolean createWhere = false;

        try {
            em = getEntityManager();
            String queryStr = "SELECT m FROM MarksEntity m";

            ProgramEntity program = null;
            if (programId != 0) {
                IProgramService programService = new ProgramServiceImpl();
                program = programService.getProgramById(programId);
                queryStr += " WHERE m.studentId.rollNumber LIKE :rollNumber";
                createWhere = true;
            }

            List<Integer> semesterIds = null;
            if (semesterId != 0) {
                IRealSemesterService semesterService = new RealSemesterServiceImpl();
                List<RealSemesterEntity> semesterList = semesterService.getAllSemester();
                semesterList = Ultilities.SortSemesters(semesterList);

                semesterIds = new ArrayList<>();
                boolean isFound = false;
                for (RealSemesterEntity s : semesterList) {
                    if (s.getId().equals(semesterId)) {
                        isFound = true;
                    }

                    if (isFound) {
                        semesterIds.add(s.getId());
                    }
                }

                queryStr += !createWhere ? " WHERE" : " AND";
                queryStr += " m.semesterId.id IN :semesterIds";
            }

            TypedQuery<MarksEntity> query = em.createQuery(queryStr, MarksEntity.class);
            if (programId != 0) query.setParameter("rollNumber", program.getName() + "%");
            if (semesterId != 0) query.setParameter("semesterIds", semesterIds);

            result = query.getResultList();
        } finally {
            em.close();
        }

        return result;
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

    public List<MarksEntity> getAllMarksByStudent(int studentId) {
        EntityManager manager = getEntityManager();
        TypedQuery<MarksEntity> query = manager.createQuery("SELECT c FROM MarksEntity c WHERE c.studentId.id = :id", MarksEntity.class);
        query.setParameter("id", studentId);
        return query.getResultList();
    }

    public List<MarksEntity> getStudyingStudents(String subjectId, String[] statuses) {
        EntityManager manager = getEntityManager();
        String queryStr = "SELECT c FROM MarksEntity c WHERE c.status IN :list ";
        if (subjectId != null) {
            queryStr += "AND c.subjectMarkComponentId.subjectId.id = :sub";
        }
        TypedQuery<MarksEntity> query = manager.createQuery(queryStr, MarksEntity.class);
        if (subjectId != null) {
            query.setParameter("sub", subjectId);
        }
        query.setParameter("list", Arrays.asList(statuses));
        return query.getResultList();
    }

    public List<MarksEntity> getAllMarksByStudentAndSubject(int studentId, String subjectId, String semesterId) {
        EntityManager manager = getEntityManager();

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
            marks = buildQuery2(semesterId, subjectId, studentId);
        } else {
            for (int i = 0; i < row + 1; i++) {
                semesterId = realSemesters.get(i).getId().toString();
                List<MarksEntity> finalMarks = marks;
                buildQuery2(semesterId, subjectId, studentId).forEach(o -> {
                    if (!finalMarks.contains((MarksEntity) o)) {
                        finalMarks.add((MarksEntity) o);
                    }
                });
                marks = finalMarks;
            }
        }
        return marks;

    }

    private List<MarksEntity> buildQuery2(String semesterId, String subjectId, int studentId) {
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
                Expression<String> subjectExpression = marksEntityRoot.get("subjectMarkComponentId").get("subjectId").get("id");
                Expression<Integer> markComponentExpression = marksEntityRoot.get("subjectMarkComponentId").get("markComponentId").get("id");
                Predicate subjectPredicate = criteriaBuilder.equal(subjectExpression, subjectId);
                Predicate markComponentPredicate = criteriaBuilder.equal(markComponentExpression, 6);
                subjectComponentPredicate = criteriaBuilder.and(subjectPredicate, markComponentPredicate);
                predicate = predicate == null ? subjectComponentPredicate : criteriaBuilder.and(predicate, subjectComponentPredicate);
            }

            // User's search keys condition
            Predicate studentIdPredicate = null;
            if (studentId != 0) {
                Expression<String> studentIdExpression = marksEntityRoot.get("studentId").get("id");
                studentIdPredicate = criteriaBuilder.equal(studentIdExpression, studentId);
                predicate = predicate == null ? studentIdPredicate : criteriaBuilder.and(predicate, studentIdPredicate);
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
