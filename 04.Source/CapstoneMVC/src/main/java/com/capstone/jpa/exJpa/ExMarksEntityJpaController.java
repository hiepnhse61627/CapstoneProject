package com.capstone.jpa.exJpa;

import com.capstone.entities.*;
import com.capstone.jpa.MarksEntityJpaController;
import com.capstone.jpa.exceptions.PreexistingEntityException;
import com.capstone.models.Enums;
import com.capstone.models.Ultilities;
import com.capstone.services.*;
import org.apache.commons.lang3.reflect.Typed;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

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
                    "AND m.semesterId.id = :semesterId " +
                    "AND m.isActivated = TRUE";
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

    public MarksEntity getMarkByAllFields(int studentId, String subjectCode, int semesterId, double mark, String status, int markComponentId) {
        MarksEntity entity = null;
        EntityManager em = null;

        try {
            em = getEntityManager();
            String queryStr = "SELECT m FROM MarksEntity m" +
                    " WHERE m.studentId.id = :studentId" +
                    " AND m.subjectMarkComponentId.subjectId.id = :subjectId" +
                    " AND m.subjectMarkComponentId.markComponentId.id = :markComponentId" +
                    " AND m.semesterId.id = :semesterId" +
                    " AND m.averageMark = :mark" +
                    " AND m.status = :status";
            TypedQuery<MarksEntity> query = em.createQuery(queryStr, MarksEntity.class);
            query.setParameter("studentId", studentId);
            query.setParameter("subjectId", subjectCode);
            query.setParameter("markComponentId", markComponentId);
            query.setParameter("semesterId", semesterId);
            query.setParameter("mark", mark);
            query.setParameter("status", status);

            entity = query.getSingleResult();
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return entity;
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

            em.getTransaction().begin();
            for (int i = 0; i < marks.size(); i++) {
                MarksEntity markEntity = marks.get(i);
                em.persist(markEntity);

                if (i > 0 && i % batchSize == 0) {
                    em.flush();
                    em.clear();
                }

                ++this.successSavedStudent;
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (em != null) {
                em.close();
            }
        }
        this.totalExistStudent = 0;
        this.successSavedStudent = 0;
    }

    public List<MarksEntity> getMarksByConditions(String semesterId, String subjectId, String searchKey) {
        if (realSemesters == null) {
            realSemesters = Ultilities.SortSemesters(new RealSemesterServiceImpl().getAllSemester());
        }

        List<MarksEntity> marks = new ArrayList<>();
//        int row = -1;

        List<Integer> allSemesters = new ArrayList<>();
        for (RealSemesterEntity r : realSemesters) {
            allSemesters.add(r.getId());
            if (r.getId() == Integer.parseInt(semesterId)) {
//                row = realSemesters.indexOf(r);
                break;
            }
        }

        List<String> allSubs = new ArrayList<>();
        if (!subjectId.equals("0")) {
            ISubjectService subjectService = new SubjectServiceImpl();
            SubjectEntity aSub = subjectService.findSubjectById(subjectId);
            allSubs.add(aSub.getId());
            for (SubjectEntity s : aSub.getSubjectEntityList()) {
                allSubs.add(s.getId());
            }
        }

        EntityManager em = getEntityManager();

        String queryStr = "select a from MarksEntity a where a.isActivated = true and a.semesterId.id IN :listSemester";
        if (!subjectId.equals("0")) {
            queryStr += " and a.subjectMarkComponentId.subjectId.id IN :sub";
        }

        TypedQuery<MarksEntity> query = em.createQuery(queryStr, MarksEntity.class);
        query.setParameter("listSemester", allSemesters);
        if (!subjectId.equals("0")) {
            query.setParameter("sub", allSubs);
        }
        marks = query.getResultList();

//        if (row < 0) {
//            marks = buildQuery(semesterId, subjectId, searchKey);
//        } else {
//            for (int i = 0; i < row + 1; i++) {
//                semesterId = realSemesters.get(i).getId().toString();
//                System.out.println("Current " + realSemesters.get(i).getSemester());
//                List<MarksEntity> finalMarks = marks;
//                buildQuery(semesterId, subjectId, searchKey).forEach(o -> {
//                    if (!finalMarks.contains((MarksEntity) o)) {
//                        finalMarks.add((MarksEntity) o);
//                    }
//                });
//                marks = finalMarks;
//            }
//        }
        return marks;
    }

//    private List<MarksEntity> buildQuery(String semesterId, String subjectId, String searchKey) {
//        EntityManager em = null;
//        List<MarksEntity> marks = new ArrayList<>();
//        try {
//            em = getEntityManager();
//
//            //Create criteria builder
//            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
//            CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
//
//            Root<MarksEntity> marksEntityRoot = criteriaQuery.from(MarksEntity.class);
//            Predicate predicate = null;
//
//            // Semester Name condition
//            Predicate semesterNamePredicate = null;
//            if (!semesterId.equals("0")) {
//                Expression<Integer> semesterExpression = marksEntityRoot.get("semesterId").get("id");
//                semesterNamePredicate = criteriaBuilder.equal(semesterExpression, Integer.parseInt(semesterId));
//                predicate = predicate == null ? semesterNamePredicate : criteriaBuilder.and(predicate, semesterNamePredicate);
//            }
//
//            // Subject Component Id Condition
//            Predicate subjectComponentPredicate = null;
//            if (!subjectId.equals("0")) {
//                Expression<String> subjectExpression = marksEntityRoot.get("subjectMarkComponentId").get("subjectId").get("id");
//                Expression<Integer> markComponentExpression = marksEntityRoot.get("subjectMarkComponentId").get("markComponentId").get("id");
//                Predicate subjectPredicate = criteriaBuilder.equal(subjectExpression, subjectId);
//                Predicate markComponentPredicate = criteriaBuilder.equal(markComponentExpression, 6);
//                subjectComponentPredicate = criteriaBuilder.and(subjectPredicate, markComponentPredicate);
//                predicate = predicate == null ? subjectComponentPredicate : criteriaBuilder.and(predicate, subjectComponentPredicate);
//            }
//
//            // User's search keys condition
//            Predicate searchKeyPredicate = null;
//            if (searchKey != null && !searchKey.isEmpty()) {
//                Expression<String> studentFullnameExpression = marksEntityRoot.get("studentId").get("fullName");
//                Expression<String> studentRollNumberExpression = marksEntityRoot.get("studentId").get("rollNumber");
//                Predicate fullNamePredicate = criteriaBuilder.like(studentFullnameExpression, "%" + searchKey + "%");
//                Predicate rollNumberPredicate = criteriaBuilder.like(studentRollNumberExpression, "%" + searchKey + "%");
//
//                searchKeyPredicate = criteriaBuilder.or(fullNamePredicate, rollNumberPredicate);
//                predicate = predicate == null ? searchKeyPredicate : criteriaBuilder.and(predicate, searchKeyPredicate);
//            }
//
//            if (predicate != null) {
//                criteriaQuery.where(predicate);
//            } else {
//                criteriaQuery.select(marksEntityRoot);
//            }
//
//            Query query = em.createQuery(criteriaQuery);
//            marks = query.getResultList();
//        } finally {
//            em.close();
//        }
//        return marks;
//    }

    public List<List<String>> getMarksForGraduatedStudent(int programId, int semesterId, int limitTotalCredits, int limitTotalSCredits) {
        List<List<String>> result = new ArrayList<>();
        IMarkComponentService markComponentService = new MarkComponentServiceImpl();
        EntityManager em = null;

        try {
            em = getEntityManager();

            MarkComponentEntity markComponent = markComponentService
                    .getMarkComponentByName(Enums.MarkComponent.AVERAGE.getValue());

            String queryStr = "SELECT s.id, s.rollNumber, s.fullName, sub.id, sub.isSpecialized" +
                    " FROM MarksEntity m" +
                    " INNER JOIN StudentEntity s ON m.studentId.id = s.id" +
                    " INNER JOIN SubjectMarkComponentEntity smc ON m.subjectMarkComponentId.id = smc.id" +
                    " INNER JOIN SubjectEntity sub ON smc.subjectId.id = sub.id";

            if (programId != 0) {
                queryStr += " INNER JOIN DocumentStudentEntity ds ON m.studentId.id = ds.studentId.id" +
                        " AND ds.createdDate = (SELECT MAX(ds1.createdDate) FROM DocumentStudentEntity ds1" +
                        "    WHERE ds1.studentId.id = ds.studentId.id)" +
                        " AND ds.curriculumId.programId.id = :programId";
            }

            List<Integer> semesterIds = null;
            if (semesterId > 0) {
                IRealSemesterService semesterService = new RealSemesterServiceImpl();
                List<RealSemesterEntity> semesterList = semesterService.getAllSemester();
                semesterList = Ultilities.SortSemesters(semesterList);

                int semesterPosition = 0;
                for (int i = 0; i < semesterList.size(); ++i) {
                    if (semesterList.get(i).getId() == semesterId) {
                        semesterPosition = i;
                        break;
                    }
                }

                semesterIds = new ArrayList<>();
                for (int i = 0; i <= semesterPosition; ++i) {
                    semesterIds.add(semesterList.get(i).getId());
                }

                queryStr += " AND m.semesterId.id IN :semesterIds";
            }

            queryStr += " AND smc.markComponentId.id = :markComponentId AND m.isActivated = :active" +
                    " AND (m.status = :passedStatus OR m.status = :isExemptStatus)" +
                    " GROUP BY s.id, s.rollNumber, s.fullName, sub.id, sub.credits, sub.isSpecialized";

            Query query = em.createQuery(queryStr);
            query.setParameter("markComponentId", markComponent.getId());
            query.setParameter("active", true);
            query.setParameter("passedStatus", Enums.MarkStatus.PASSED.getValue());
            query.setParameter("isExemptStatus", Enums.MarkStatus.IS_EXEMPT.getValue());
            if (programId != 0) query.setParameter("programId", programId);
            if (semesterId != 0) query.setParameter("semesterIds", semesterIds);

            // StudentId, RollNumber, FullName, SubjectId, Credits, IsSpecialized
            List<Object[]> searchList = query.getResultList();
            Map<Integer, GraduatedStudent_StudentData> studentMap = new HashMap<>();
            for (Object[] data : searchList) {
                int studentId = (int) data[0];
                int credits = (int) data[4];
                boolean isSpecialized = (boolean) data[5];

                GraduatedStudent_StudentData studentData = studentMap.get(studentId);
                if (studentData == null) {
                    studentData = new GraduatedStudent_StudentData();
                    studentData.rollNumber = data[1].toString();
                    studentData.fullName = data[2].toString();
                    studentData.totalCredits = 0;
                    studentData.totalSpecializedCredits = 0;

                    studentMap.put(studentId, studentData);
                }

                studentData.totalCredits += credits;
                if (isSpecialized) {
                    studentData.totalSpecializedCredits += credits;
                }
            }

            for (Integer studentId : studentMap.keySet()) {
                GraduatedStudent_StudentData studentData = studentMap.get(studentId);
                if (studentData.totalCredits >= limitTotalCredits
                        && studentData.totalSpecializedCredits >= limitTotalSCredits) {
                    List<String> row = new ArrayList<>();
                    row.add(studentData.rollNumber);
                    row.add(studentData.fullName);
                    row.add(studentData.totalCredits + "");
                    row.add(studentData.totalSpecializedCredits + "");

                    result.add(row);
                }
            }

        } finally {
            em.close();
        }

        return result;
    }

    public List<MarksEntity> getMarksForMarkPage(int studentId) {
        IMarkComponentService markComponentService = new MarkComponentServiceImpl();
        List<MarksEntity> result = null;
        EntityManager em = null;

        try {
            em = getEntityManager();

            MarkComponentEntity markComponent = markComponentService.getMarkComponentByName(
                    Enums.MarkComponent.AVERAGE.getValue());

            String queryStr = "SELECT m FROM MarksEntity m" +
                    " WHERE m.subjectMarkComponentId.markComponentId.id = :markComponentId" +
                    ((studentId > 0) ? " AND m.studentId.id = :studentId" : "") +
                    " ORDER BY m.studentId.id, m.subjectMarkComponentId.subjectId.id";
            TypedQuery<MarksEntity> query = em.createQuery(queryStr, MarksEntity.class);
            query.setParameter("markComponentId", markComponent.getId());
            if (studentId > 0) {
                query.setParameter("studentId", studentId);
            }

            result = query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
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
        TypedQuery<MarksEntity> query = manager.createQuery("SELECT c FROM MarksEntity c WHERE c.isActivated = true and c.studentId.id = :id", MarksEntity.class);
        query.setParameter("id", studentId);
        return query.getResultList();
    }

    public List<MarksEntity> getMarksByStudentIdAndStatus(int studentId, String status) {
        EntityManager manager = getEntityManager();
        TypedQuery<MarksEntity> query = manager.createQuery("SELECT a FROM MarksEntity a WHERE a.isActivated = true and a.studentId.id = :id AND LOWER(a.status) LIKE :stat", MarksEntity.class);
        query.setParameter("id", studentId);
        query.setParameter("stat", "%" + status + "%");
        return query.getResultList();
    }

    public List<MarksEntity> getMarksByStudentIdAndStatusAndSemester(int studentId, String status, List<String> semesters) {
        EntityManager manager = getEntityManager();
        TypedQuery<MarksEntity> query = manager.createQuery("SELECT a FROM MarksEntity a WHERE a.isActivated = true and a.studentId.id = :id AND LOWER(a.status) LIKE :stat AND a.semesterId.semester IN :sList", MarksEntity.class);
        query.setParameter("id", studentId);
        query.setParameter("stat", "%" + status + "%");
        query.setParameter("sList", semesters);
        return query.getResultList();
    }

    public List<MarksEntity> getStudyingStudents(String subjectId, String[] statuses) {
        EntityManager manager = getEntityManager();
        String queryStr = "SELECT c FROM MarksEntity c WHERE c.isActivated = true and c.status IN :list ";
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

    public List<MarksEntity> getListMarkToCurrentSemester(List<Integer> semesterIds, String[] statuses) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            String sqlString = "SELECT m FROM MarksEntity m WHERE m.isActivated = true and m.status IN :statuses AND m.semesterId.id IN :semesterIds";
            Query query = em.createQuery(sqlString);
            query.setParameter("statuses", Arrays.asList(statuses));
            query.setParameter("semesterIds", semesterIds);

            List<MarksEntity> result = query.getResultList();

            return result;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<MarksEntity> getStudentMarksByStudentIdAndSortBySubjectName(int studentId) {
        List<MarksEntity> result = new ArrayList<>();
        EntityManager em = null;

        try {
            em = getEntityManager();
            String queryStr = "SELECT m FROM MarksEntity m WHERE m.isActivated = true and m.studentId.id = :studentId" +
                    " ORDER BY m.subjectMarkComponentId.subjectId.name";
            TypedQuery<MarksEntity> query = em.createQuery(queryStr, MarksEntity.class);
            query.setParameter("studentId", studentId);

            result = query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    public List<Object[]> getTotalStudentsGroupBySemesterAndSubject(int semesterId) {
        IMarkComponentService markComponentService = new MarkComponentServiceImpl();
        List<Object[]> result = null;
        EntityManager em = null;

        try {
            em = getEntityManager();

            MarkComponentEntity markComponent = markComponentService.getMarkComponentByName(
                    Enums.MarkComponent.AVERAGE.getValue());

            String queryStr = "SELECT m.semesterId.id, smc.subjectId.id, COUNT(m) FROM MarksEntity m" +
                    " INNER JOIN SubjectMarkComponentEntity smc ON m.subjectMarkComponentId.id = smc.id" +
                    " AND smc.markComponentId.id = :markComponentId" +
                    (semesterId > 0 ? " AND m.semesterId.id = :semesterId" : "") +
                    " GROUP BY m.semesterId.id, smc.subjectId.id";
            Query query = em.createQuery(queryStr);
            query.setParameter("markComponentId", markComponent.getId());
            if (semesterId > 0) {
                query.setParameter("semesterId", semesterId);
            }

            result = query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    public List<List<String>> getAverageSubjectLearnedByStudent(int programId) {
        List<List<String>> result = new ArrayList<>();

        IMarkComponentService markComponentService = new MarkComponentServiceImpl();
        IDocumentStudentService documentStudentService = new DocumentStudentServiceImpl();
        IProgramService programService = new ProgramServiceImpl();
        EntityManager em = null;

        try {
            em = getEntityManager();

            MarkComponentEntity markComponent = markComponentService
                    .getMarkComponentByName(Enums.MarkComponent.AVERAGE.getValue());

            String queryStr = "SELECT p.Name, m.StudentId, smc.SubjectId, sc.CurriculumId" +
                    " FROM Marks m" +
                    " INNER JOIN Subject_MarkComponent smc ON m.SubjectMarkComponentId = smc.Id" +
                    " INNER JOIN Document_Student ds ON m.StudentId = ds.StudentId" +
                    " INNER JOIN Curriculum c ON ds.CurriculumId = c.Id" +
                    " INNER JOIN Program p ON c.ProgramId = p.Id" +
                    " INNER JOIN Subject_Curriculum sc ON ds.CurriculumId = sc.CurriculumId" +
                    " AND sc.SubjectId = smc.SubjectId" +
                    " AND smc.MarkComponentId = ?" +
                    " AND m.Status != ?" +
                    (programId != 0 ? " AND p.Id = ?" : "") +
                    " AND ds.CreatedDate = (SELECT MAX(CreatedDate) FROM Document_Student WHERE StudentId = ds.StudentId)" +
                    " GROUP BY p.Name, m.StudentId, smc.SubjectId, sc.CurriculumId";

            Query query = em.createNativeQuery(queryStr);
            query.setParameter(1, markComponent.getId());
            query.setParameter(2, Enums.MarkStatus.NOT_START.getValue());
            if (programId != 0) query.setParameter(3, programId);
            List<Object[]> searchList = query.getResultList();

            // Map<ProgramName, Map<StudentId, List<SubjectCode>>>
            Map<String, Map<Integer, AverageSubject_StudentData>> programMap = new HashMap<>();
            for (Object[] data : searchList) {
                String programName = data[0].toString();
                int studentId = (int) data[1];
                String subjectCode = data[2].toString();
                int curriculumId = (int) data[3];

                Map<Integer, AverageSubject_StudentData> studentMap = programMap.get(programName);
                if (studentMap == null) {
                    studentMap = new HashMap<>();
                    programMap.put(programName, studentMap);
                }

                AverageSubject_StudentData studentData = studentMap.get(studentId);
                if (studentData != null) {
                    studentData.subjectList.add(subjectCode);
                } else {
                    studentData = new AverageSubject_StudentData();
                    studentData.curriculumId = curriculumId;
                    studentData.subjectList = new ArrayList<String>() {{
                        add(subjectCode);
                    }};
                    studentMap.put(studentId, studentData);
                }
            }


            queryStr = "SELECT sc.curriculumId.id, COUNT(sc) FROM SubjectCurriculumEntity sc" +
                    " GROUP BY sc.curriculumId.id";
            query = em.createQuery(queryStr);
            List<Object[]> totalList = query.getResultList();

            Map<Integer, Long> totalSubjectsMap = new HashMap<>();
            for (Object[] data : totalList) {
                totalSubjectsMap.put((int) data[0], (long) data[1]);
            }

            for (String programName : programMap.keySet()) {
                int totalSubjectsOfAllStudents = 0;
                int totalSubjectsInCurriculumOfAllStudents = 0;

                Map<Integer, AverageSubject_StudentData> studentMap = programMap.get(programName);
                for (Integer studenId : studentMap.keySet()) {
                    AverageSubject_StudentData studentData = studentMap.get(studenId);

                    totalSubjectsOfAllStudents += studentData.subjectList.size();
                    totalSubjectsInCurriculumOfAllStudents += totalSubjectsMap.get(studentData.curriculumId);
                }

                double totalStudentSubjectsOnTotalSubjects = (1.0 * totalSubjectsOfAllStudents)
                        / totalSubjectsInCurriculumOfAllStudents;
                double totalSubjectsOnNumOfStudents = (1.0 * totalSubjectsInCurriculumOfAllStudents)
                        / studentMap.keySet().size();

                List<String> row = new ArrayList<>();
                row.add(programName);
                row.add(studentMap.keySet().size() + "");
                row.add(Math.round((totalSubjectsOnNumOfStudents * 100.0)) / 100.0 + "");
                row.add((Math.round(totalStudentSubjectsOnTotalSubjects
                        * totalSubjectsOnNumOfStudents * 100.0) / 100.0) + "");
                result.add(row);
            }

        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    public List<Object[]> getLatestPassFailMarksAndCredits(int studentId) {
        List<Object[]> result = new ArrayList<>();
        EntityManager em = null;

        try {
            em = getEntityManager();
            List<MarksEntity> markList = this.getLatestMarksByStudentId(studentId);
            markList = markList.stream().filter(m ->
                    m.getStatus().equalsIgnoreCase(Enums.MarkStatus.PASSED.getValue())
                            || m.getStatus().equalsIgnoreCase(Enums.MarkStatus.FAIL.getValue()))
                    .collect(Collectors.toList());

            if (!markList.isEmpty()) {
                List<Integer> markIds = markList.stream().map(m -> m.getId()).collect(Collectors.toList());

                String queryStr = "SELECT sc.SubjectId, sc.SubjectCredits, m.AverageMark, m.Status" +
                        " FROM Marks m" +
                        " INNER JOIN Subject_MarkComponent smc ON m.SubjectMarkComponentId = smc.Id" +
                        " INNER JOIN MarkComponent mc ON smc.MarkComponentId = mc.Id" +
                        " INNER JOIN Subject sub ON smc.SubjectId = sub.Id" +
                        " INNER JOIN Document_Student ds ON m.StudentId = ds.StudentId" +
                        " INNER JOIN Curriculum c ON ds.CurriculumId = c.Id" +
                        " INNER JOIN Program p ON c.ProgramId = p.Id" +
                        " INNER JOIN Subject_Curriculum sc ON sc.CurriculumId = c.Id" +
                        " AND p.Name != ?" +
                        " AND sc.SubjectId = sub.Id" +
                        " AND m.IsActivated = 1" +
                        " AND m.StudentId = ?" +
                        " AND mc.Name = ?" +
                        " AND ds.CurriculumId IS NOT NULL" +
                        " AND m.Id IN (" + Ultilities.parseIntegerListToString(markIds) + ")";
                Query query = em.createNativeQuery(queryStr);
                query.setParameter(1, "PC");
                query.setParameter(2, studentId);
                query.setParameter(3, Enums.MarkComponent.AVERAGE.getValue());

                result = query.getResultList();
            }

        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    public List<MarksEntity> getLatestMarksByStudentId(int studentId) {
        List<MarksEntity> result = null;
        EntityManager em = null;

        try {
            em = getEntityManager();

            String queryStr = "SELECT m FROM MarksEntity m" +
                    " WHERE m.isActivated = true AND m.isEnabled = true AND m.studentId.id = :studentId";
            TypedQuery<MarksEntity> query = em.createQuery(queryStr, MarksEntity.class);
            query.setParameter("studentId", studentId);
            List<MarksEntity> markList = query.getResultList();

            // Remove duplicate and get last record if duplicate
            result = new ArrayList<>();
            Map<Integer, MarksEntity> map = new HashMap<>();
            for (MarksEntity m : markList) {
                map.put(m.getSubjectMarkComponentId().getId(), m);
            }

            for (Integer key : map.keySet()) {
                result.add(map.get(key));
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }


    public List<MarksEntity> getMarksByConditions(int semesterId, List<String> subjects, int studentId) {
        if (realSemesters == null) {
            realSemesters = Ultilities.SortSemesters(new RealSemesterServiceImpl().getAllSemester());
        }

        List<MarksEntity> marks = new ArrayList<>();

        List<Integer> allSemesters = new ArrayList<>();
        for (RealSemesterEntity r : realSemesters) {
            allSemesters.add(r.getId());
            if (r.getId() == semesterId) {
                break;
            }
        }

        EntityManager em = getEntityManager();

        String queryStr = "select a from MarksEntity a where a.isActivated = true and a.studentId.id = :id and a.semesterId.id IN :listSemester";
        if (subjects != null && !subjects.isEmpty()) {
            queryStr += " and a.subjectMarkComponentId.subjectId.id IN :sub";
        }

        TypedQuery<MarksEntity> query = em.createQuery(queryStr, MarksEntity.class);
        query.setParameter("listSemester", allSemesters);
        query.setParameter("id", studentId);
        if (subjects != null && !subjects.isEmpty()) {
            query.setParameter("sub", subjects);
        }
        marks = query.getResultList();

        return marks;
    }

    public List<MarksEntity> findMarksBySemesterId(Integer semesterId) {
        EntityManager em = getEntityManager();
        List<MarksEntity> marksEntities = new ArrayList<>();
        try {
            String sqlString = "SELECT m FROM MarksEntity m WHERE m.semesterId.id = :semesterId";
            Query query = em.createQuery(sqlString);
            query.setParameter("semesterId", semesterId);

            marksEntities = query.getResultList();
            return marksEntities;
        } catch (NoResultException nrEx) {
            return null;
        }
    }

    public List<MarksEntity> findMarksByStudentIdAndSubjectCdAndSemesterId(Integer studentId, String subjectCd, Integer semesterId) {
        EntityManager em = getEntityManager();
        List<MarksEntity> marksEntities = new ArrayList<>();
        try {
            String sqlString = "SELECT m FROM MarksEntity m WHERE m.studentId.id = :studentId " +
                                                             "AND m.subjectMarkComponentId.subjectId.id = :subjectCd " +
                                                             "AND m.semesterId.id = :semesterId";
            Query query = em.createQuery(sqlString);
            query.setParameter("studentId", studentId);
            query.setParameter("subjectCd", subjectCd);
            query.setParameter("semesterId", semesterId);

            marksEntities = query.getResultList();
            return marksEntities;
        } catch (NoResultException nrEx) {
            return null;
        }
    }

    private class AverageSubject_StudentData {
        public int curriculumId;
        public List<String> subjectList;
    }

    private class GraduatedStudent_StudentData {
        public String rollNumber;
        public String fullName;
        public int totalCredits;
        public int totalSpecializedCredits;
    }
}
