package com.capstone.services;

import com.capstone.entities.SubjectEntity;
import com.capstone.entities.fapEntities.StudentAvgMarks;
import com.capstone.entities.fapEntities.StudentStudyingMarks;
import com.capstone.models.Enums;
import com.capstone.models.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Ultilities2ServiceImpl implements IUltilities2Service {
    private EntityManagerFactory emfCapstone = Persistence.createEntityManagerFactory("CapstonePersistence");
    private EntityManagerFactory emfFap = Persistence.createEntityManagerFactory("FapDB");
    private EntityManager em = null;
    private EntityManager emFAP = null;

    public EntityManager getEntityManager() {
        return emfCapstone.createEntityManager();
    }

    public EntityManager getFAPEntityManager() {
        return emfFap.createEntityManager();
    }


    public boolean backupCapstoneDB() {
        boolean result = false;
        try {
            String url = (String) emfCapstone.getProperties().get("javax.persistence.jdbc.url");
            String user = (String) emfCapstone.getProperties().get("javax.persistence.jdbc.url");
            String password = (String) emfCapstone.getProperties().get("javax.persistence.jdbc.url");

            // Step 1: Allocate a database "Connection" object
            Connection conn = DriverManager.getConnection(
                    url, user, password);

            // Step 2: Allocate a "Statement" object in the Connection
            Statement stmt = conn.createStatement();

            String backup = "BACKUP DATABASE 'CapstoneProject' TO DISK = '" + Enums.BackupPath.PATH.getValue() + "' ";
            result = stmt.execute(backup);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog(e);
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return result;
    }


    public List<StudentAvgMarks> getFAPMarksBySemester(String semesterName) {
        EntityManager em = null;
        List<StudentAvgMarks> result = new ArrayList<>();
        try {
            em = getFAPEntityManager();
            Query query = em.createNativeQuery("SELECT sm.RollNumber, sm.AverageMark, sm.IsPassed, c.SubjectCode, c.SemesterName" +
                    " FROM FUMM.StudentMarks sm " +
                    "INNER JOIN FUMM.Courses c on sm.CourseID = c.CourseID WHERE c.SemesterName Like ?1 " +
                    "AND sm.IsPassed IS NOT  NULL ");

            query.setParameter(1, "%" + semesterName + "%");

//           result = getResultList(query, StudentAvgMarks.class);

            List<Object[]> objList = query.getResultList();
            for (Object[] item : objList) {
                String rollNumber = (String) item[0];
                Double avgMark = item[1] != null ? Double.parseDouble(item[1].toString()) : 0.0;
                boolean isPassed = (boolean) item[2];
                String subjCode = (String) item[3];
                String semName = (String) item[4];
                result.add(new StudentAvgMarks(rollNumber, avgMark, isPassed, subjCode, semName));
            }

        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return result;
    }

    public List<StudentStudyingMarks> getFAPStudyingMarkBySemester(String semesterName) {
        EntityManager em = null;
        List<StudentStudyingMarks> result = new ArrayList<>();
        try {
            em = getFAPEntityManager();
            Query query = em.createNativeQuery("SELECT sc.RollNumber, t.SemesterName, s.SubjectCode " +
                    " FROM StudentCourses sc inner join Courses c on sc.CourseID = c.CourseID" +
                    "  inner join Terms t on c.TermID = t.TermID inner join Subjects s on c.SubjectID = s.SubjectID" +
                    "  WHERE t.SemesterName Like ?1");

            query.setParameter(1, "%" + semesterName + "%");

//           result = getResultList(query, StudentAvgMarks.class);

            List<Object[]> objList = query.getResultList();
            for (Object[] item : objList) {
                String rollNumber = (String) item[0];
                String semName = (String) item[1];
                String subjectCode = (String) item[2];

                rollNumber = rollNumber.trim().toUpperCase();
                semName = semName.trim().toUpperCase();
                subjectCode = subjectCode.trim().toUpperCase();


                result.add(new StudentStudyingMarks(rollNumber, semName, subjectCode));
            }

        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return result;
    }

    public List<StudentAvgMarks> getFAPMarksByStudentRollNumber(String studentRollNumber) {
        EntityManager em = null;
        List<StudentAvgMarks> result = new ArrayList<>();
        try {
            em = getFAPEntityManager();
            Query query = em.createNativeQuery("SELECT sm.RollNumber, sm.AverageMark, sm.IsPassed, c.SubjectCode, c.SemesterName" +
                    " FROM FUMM.StudentMarks sm " +
                    "INNER JOIN FUMM.Courses c on sm.CourseID = c.CourseID WHERE sm.RollNumber LIKE ?1 " +
                    "AND sm.IsPassed IS NOT  NULL ");

            query.setParameter(1, "%" + studentRollNumber + "%");

//           result = getResultList(query, StudentAvgMarks.class);

            List<Object[]> objList = query.getResultList();
            for (Object[] item : objList) {
                String rollNumber = (String) item[0];
                Double avgMark = item[1] != null ? Double.parseDouble(item[1].toString()) : 0.0;
                boolean isPassed = (boolean) item[2];
                String subjCode = (String) item[3];
                String semName = ((String) item[4]).toUpperCase();
                result.add(new StudentAvgMarks(rollNumber, avgMark, isPassed, subjCode, semName));
            }

        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return result;
    }


    public List<String> getFAPSubjectCodesHaveMarks(String semesterName) {
        EntityManager em = null;
        List<String> result = new ArrayList<>();
        try {
            em = getFAPEntityManager();
            Query query = em.createNativeQuery("SELECT DISTINCT(c.SubjectCode)" +
                    " FROM FUMM.StudentMarks sm " +
                    "INNER JOIN FUMM.Courses c on sm.CourseID = c.CourseID WHERE c.SemesterName Like ?1" +
                    " AND sm.IsPassed IS NOT NULL");

            query.setParameter(1, "%" + semesterName + "%");

//           result = getResultList(query, StudentAvgMarks.class);

            List<Object> objList = query.getResultList();
            for (Object item : objList) {
                String subjectCode = (String) item;
                result.add(subjectCode);
            }

        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return result;
    }


    public static <T> T map(Class<T> type, Object[] tuple) {
        List<Class<?>> tupleTypes = new ArrayList<>();
        for (Object field : tuple) {
            tupleTypes.add(field.getClass());
        }
        try {
            Constructor<T> ctor = type.getConstructor(tupleTypes.toArray(new Class<?>[tuple.length]));
            return ctor.newInstance(tuple);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> map(Class<T> type, List<Object[]> records) {
        List<T> result = new ArrayList<>();
        for (Object[] record : records) {
            result.add(map(type, record));
        }
        return result;
    }

    public static <T> List<T> getResultList(Query query, Class<T> type) {
        @SuppressWarnings("unchecked")
        List<Object[]> records = query.getResultList();
        return map(type, records);
    }

}
