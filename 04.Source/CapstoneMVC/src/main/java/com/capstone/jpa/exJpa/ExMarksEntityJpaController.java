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
                    "AND m.active = TRUE";
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

        String queryStr = "select a from MarksEntity a where a.active = true and a.semesterId.id IN :listSemester";
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

    public List<MarksEntity> getMarkByProgramAndSemester(int programId, int semesterId) {
        List<MarksEntity> result = null;
        EntityManager em = null;
        boolean createWhere = false;

        try {
            em = getEntityManager();
            String queryStr = "SELECT m FROM MarksEntity m WHERE m.active = true";

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
        TypedQuery<MarksEntity> query = manager.createQuery("SELECT c FROM MarksEntity c WHERE c.active = true and c.studentId.id = :id", MarksEntity.class);
        query.setParameter("id", studentId);
        return query.getResultList();
    }

    public List<MarksEntity> getMarksByStudentIdAndStatus(int studentId, String status) {
        EntityManager manager = getEntityManager();
        TypedQuery<MarksEntity> query = manager.createQuery("SELECT a FROM MarksEntity a WHERE a.active = true and a.studentId.id = :id AND LOWER(a.status) LIKE :stat", MarksEntity.class);
        query.setParameter("id", studentId);
        query.setParameter("stat", "%" + status + "%");
        return query.getResultList();
    }

    public List<MarksEntity> getStudyingStudents(String subjectId, String[] statuses) {
        EntityManager manager = getEntityManager();
        String queryStr = "SELECT c FROM MarksEntity c WHERE c.active = true and c.status IN :list ";
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
            String sqlString = "SELECT m FROM MarksEntity m WHERE m.active = true and m.status IN :statuses AND m.semesterId.id IN :semesterIds";
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
            String queryStr = "SELECT m FROM MarksEntity m WHERE m.active = true and m.studentId.id = :studentId" +
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

    public void getAverageSubjectLearnedByStudent(int programId) {
        IMarkComponentService markComponentService = new MarkComponentServiceImpl();
        IDocumentStudentService documentStudentService = new DocumentStudentServiceImpl();
        IProgramService programService = new ProgramServiceImpl();
        EntityManager em = null;

        try {
//            em = getEntityManager();
//
//            MarkComponentEntity markComponent = markComponentService
//                    .getMarkComponentByName(Enums.MarkComponent.AVERAGE.getValue());
//
//            // Get mark list by programId
//            String queryStr =
//                    "SELECT m.studentId.id, (SELECT smc.subjectId.id" +
//                            "    FROM SubjectMarkComponentEntity smc" +
//                            "    WHERE smc.id = m.subjectMarkComponentId.id AND smc.markComponentId.id = :markComponentId)" +
//                            " FROM MarksEntity m " +
//                            (programId > 0 ? " INNER JOIN DocumentStudentEntity ds ON m.studentId.id = ds.studentId.id" +
//                                    " AND ds.curriculumId.programId.id = :programId" +
//                                    " AND ds.CreatedDate = (SELECT MAX(tDS.CreatedDate) FROM DocumentStudentEntity tDS WHERE tDS.id = ds.id)" +
//                                    " AND" : " WHERE") +
//                            " m.status != :status" +
//                            " GROUP BY m.studentId.id, m.subjectMarkComponentId.id";
//            Query queryMarkList = em.createQuery(queryStr);
//            queryMarkList.setParameter("status", Enums.MarkStatus.NOT_START.getValue());
//            queryMarkList.setParameter("markComponentId", markComponent.getId());
//            if (programId > 0) {
//                queryMarkList.setParameter("programId", programId);
//            }
//
//            // Object[] -> [0]: studentId, [1]: subjectId
//            List<Object[]> studentSubjectList = queryMarkList.getResultList();
//
//            List<AverageSubject_StudentModel> studentList = new ArrayList<>();
//            for (Object[] row : studentSubjectList) {
//                int studentId = (int) row[0];
//                String subjectId = (String) row[1];
//
//                AverageSubject_StudentModel curStudent = null;
//                for (AverageSubject_StudentModel student : studentList) {
//                    if (student.studentId == studentId) {
//                        curStudent = student;
//                        break;
//                    }
//                }
//
//                if (curStudent == null) {
//                    curStudent = new AverageSubject_StudentModel();
//
//                    curStudent.studentId = studentId;
//                    curStudent.subjectList = new ArrayList<>();
//                    studentList.add(curStudent);
//                }
//                curStudent.subjectList.add(subjectId);
//            }
//
//            // Get lastest document_student
//            List<DocumentStudentEntity> docStudentList;
//            if (programId > 0) {
//                docStudentList = documentStudentService.getAllLatestDocumentStudentByProgramId(programId);
//            } else {
//                docStudentList = documentStudentService.getAllLatestDocumentStudent();
//            }
//
//            Map<Integer, List<AverageSubject_StudentModel>> currciculumStudentMap = new HashMap<>();
//            for (DocumentStudentEntity docStudent : docStudentList) {
//                for (AverageSubject_StudentModel student : studentList) {
//                    if (student.studentId == docStudent.getStudentId().getId()) {
//                        int curCurriculumId = docStudent.getCurriculumId().getId();
//                        student.curriculumId = curCurriculumId;
//                        List<AverageSubject_StudentModel> list = currciculumStudentMap.get(curCurriculumId);
//                        if (list == null) {
//                            list = new ArrayList<>();
//                            list.add(student);
//                            currciculumStudentMap.put(curCurriculumId, list);
//                        } else {
//                            list.add(student);
//                        }
//                    }
//                    break;
//                }
//            }
//
//            // Get program list
//            List<ProgramEntity> programEntityList;
//            if (programId > 0) {
//                List<ProgramEntity> temp = new ArrayList<>();
//                temp.add(programService.getProgramById(programId));
//                programEntityList = temp;
//            } else {
//                programEntityList = programService.getAllPrograms();
//            }
//
//            List<AverageSubject_ProgramModel> programList = new ArrayList<>();
//            for (ProgramEntity program : programEntityList) {
//                AverageSubject_ProgramModel newProgramModel = new AverageSubject_ProgramModel();
//                newProgramModel.program = program;
//                newProgramModel.students = new ArrayList<>();
//
//                for (CurriculumEntity curriculum : program.getCurriculumEntityList()) {
//                    for (Integer curriculumId : currciculumStudentMap.keySet()) {
//                        if (curriculum.getId() == curriculumId) {
//                            newProgramModel.students.addAll(currciculumStudentMap.get(curriculumId));
//                        }
//                    }
//                }
//                programList.add(newProgramModel);
//            }


        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    private class AverageSubject_ProgramModel {
        public ProgramEntity program;
        public List<AverageSubject_StudentModel> students;
    }

    private class AverageSubject_StudentModel {
        public int studentId;
        public int curriculumId;
        public List<String> subjectList;
    }
}
