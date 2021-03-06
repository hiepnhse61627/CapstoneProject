package com.capstone.jpa.exJpa;

import com.capstone.entities.CourseEntity;
import com.capstone.jpa.CourseEntityJpaController;
import com.capstone.models.DatatableModel;

import javax.persistence.*;
import java.util.List;

public class ExCourseEntityJpaController extends CourseEntityJpaController {
    public ExCourseEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public CourseEntity findCourseBySemesterAndSubjectCode(String semesterName, String subjectCode) {
        EntityManager em = getEntityManager();
        try {
            String sqlString = "SELECT c FROM CourseEntity c WHERE c.semester = :semester AND c.subjectCode = :subjectCode";
            Query query = em.createQuery(sqlString);
            query.setParameter("semester", semesterName);
            query.setParameter("subjectCode", subjectCode);

            CourseEntity courseEntity = (CourseEntity) query.getSingleResult();

            return courseEntity;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public CourseEntity findCourseBySubjectCode(String subjectCode) {
        EntityManager em = getEntityManager();
        try {
            String sqlString = "SELECT c FROM CourseEntity c WHERE c.subjectCode = :subjectCode";
            TypedQuery<CourseEntity> query = em.createQuery(sqlString, CourseEntity.class);
            query.setParameter("subjectCode", subjectCode);

            List<CourseEntity> courseEntityList = query.getResultList();

//            int lastSemester = 0;
//            int lastYear = 0;
//            for (CourseEntity entity : courseEntityList) {
//                String tmpYear = entity.getSemester().substring(entity.getSemester().length() - 4, entity.getSemester().length() - 1);
//                lastYear = lastYear < Integer.parseInt(tmpYear) ? Integer.parseInt(tmpYear) : lastYear;
//                String tmpSemester = entity.getSemester().substring(0, entity.getSemester().length() - 4);
//                int flag = 0;
//                if (tmpSemester.equals("SPRING")) {
//                    flag = 1;
//                }
//
//                if (tmpSemester.equals("SUMMER")) {
//                    flag = 2;
//                }
//
//                if (tmpSemester.equals("FALL")) {
//                    flag = 3;
//                }
//
//                if (flag > lastSemester) {
//                    lastSemester = flag;
//                }
//            }
//
//            String lastSemesterStr = "";
//            if(lastSemester==1){
//                lastSemesterStr="SPRING";
//            }

            return courseEntityList.get(0);
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public CourseEntity createCourse(CourseEntity entity) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(entity);
            em.flush();
            em.getTransaction().commit();

            return entity;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void createCourseList(List<CourseEntity> courseEntityList) {
//        EntityManager em = getEntityManager();
//        for (CourseEntity courseEntity: courseEntityList) {
//            try {
//                TypedQuery<CourseEntity> tmp = em.createQuery("SELECT c FROM CourseEntity c WHERE c.class1 = :class", CourseEntity.class);
//                tmp.setParameter("class", courseEntity.getClass1());
//                if (tmp.getResultList().size() == 0) {
//                    em.getTransaction().begin();
//                    em.persist(courseEntity);
//                    em.getTransaction().commit();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    public void updateCourse(CourseEntity model) {
//        EntityManager em = getEntityManager();
//
//        try {
//            CourseEntity course = this.findCourseEntity(model.getId());
//            course.setClass1(model.getClass1());
//
//            em.getTransaction().begin();
//            em.merge(course);
//            em.getTransaction().commit();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void deleteCourse(int courseId) {
        EntityManager em = getEntityManager();

        try {
            CourseEntity course = this.findCourseEntity(courseId);
            if (!em.contains(course)) {
                course = em.merge(course);
            }

            em.getTransaction().begin();
            em.remove(course);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<CourseEntity> getCourseListForDatatable(DatatableModel model) {
        EntityManager em = getEntityManager();
        List<CourseEntity> result = null;

//        try {
//            TypedQuery<Integer> queryCount;
//
//            // Đếm số khóa học
//            queryCount = em.createQuery("SELECT COUNT(c) FROM CourseEntity c", Integer.class);
//            model.iTotalRecords = ((Number) queryCount.getSingleResult()).intValue();
//
//            // Đếm số khóa học sau khi filter
//            if (model.sSearch.isEmpty()) {
//                model.iTotalDisplayRecords = model.iTotalRecords;
//            } else {
//                queryCount = em.createQuery("SELECT COUNT(c) FROM CourseEntity c " +
//                        "WHERE c.class1 LIKE :class", Integer.class);
//                queryCount.setParameter("class", "%" + model.sSearch + "%");
//                model.iTotalDisplayRecords = ((Number) queryCount.getSingleResult()).intValue();
//            }
//
//            // Danh sách khóa học
//            String queryStr = "SELECT c FROM CourseEntity c";
//            if (!model.sSearch.isEmpty()) {
//                queryStr += " WHERE c.subjectCode LIKE :subCode OR c.class1 LIKE :class";
//            }
//
//            TypedQuery<CourseEntity> query = em.createQuery(queryStr, CourseEntity.class)
//                    .setFirstResult(model.iDisplayStart)
//                    .setMaxResults(model.iDisplayLength);
//
//            if (!model.sSearch.isEmpty()) {
//                query.setParameter("subCode", "%" + model.sSearch + "%");
//                query.setParameter("class", "%" + model.sSearch + "%");
//            }
//
//            result = query.getResultList();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return result;
    }

    public List<String> findAllToString() {
//        EntityManager em = getEntityManager();
//        TypedQuery<String> q = em.createQuery("SELECT distinct c.class1 FROM CourseEntity c", String.class);
//        return q.getResultList();
        return null;
    }
}
