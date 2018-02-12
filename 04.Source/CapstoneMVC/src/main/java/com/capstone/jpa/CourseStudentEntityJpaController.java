package com.capstone.jpa;
import java.io.Serializable;
import java.util.List;

import com.capstone.entities.CourseEntity;
import com.capstone.entities.CourseStudentEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author hoanglong
 */
public class CourseStudentEntityJpaController implements Serializable {

    public CourseStudentEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(CourseStudentEntity courseStudentEntity) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CourseEntity courseId = courseStudentEntity.getCourseId();
            if (courseId != null) {
                courseId = em.getReference(courseId.getClass(), courseId.getId());
                courseStudentEntity.setCourseId(courseId);
            }
            StudentEntity studentId = courseStudentEntity.getStudentId();
            if (studentId != null) {
                studentId = em.getReference(studentId.getClass(), studentId.getId());
                courseStudentEntity.setStudentId(studentId);
            }
            em.persist(courseStudentEntity);
            if (courseId != null) {
                courseId.getCourseStudentEntityList().add(courseStudentEntity);
                courseId = em.merge(courseId);
            }
            if (studentId != null) {
                studentId.getCourseStudentEntityList().add(courseStudentEntity);
                studentId = em.merge(studentId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCourseStudentEntity(courseStudentEntity.getId()) != null) {
                throw new PreexistingEntityException("CourseStudentEntity " + courseStudentEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(CourseStudentEntity courseStudentEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CourseStudentEntity persistentCourseStudentEntity = em.find(CourseStudentEntity.class, courseStudentEntity.getId());
            CourseEntity courseIdOld = persistentCourseStudentEntity.getCourseId();
            CourseEntity courseIdNew = courseStudentEntity.getCourseId();
            StudentEntity studentIdOld = persistentCourseStudentEntity.getStudentId();
            StudentEntity studentIdNew = courseStudentEntity.getStudentId();
            if (courseIdNew != null) {
                courseIdNew = em.getReference(courseIdNew.getClass(), courseIdNew.getId());
                courseStudentEntity.setCourseId(courseIdNew);
            }
            if (studentIdNew != null) {
                studentIdNew = em.getReference(studentIdNew.getClass(), studentIdNew.getId());
                courseStudentEntity.setStudentId(studentIdNew);
            }
            courseStudentEntity = em.merge(courseStudentEntity);
            if (courseIdOld != null && !courseIdOld.equals(courseIdNew)) {
                courseIdOld.getCourseStudentEntityList().remove(courseStudentEntity);
                courseIdOld = em.merge(courseIdOld);
            }
            if (courseIdNew != null && !courseIdNew.equals(courseIdOld)) {
                courseIdNew.getCourseStudentEntityList().add(courseStudentEntity);
                courseIdNew = em.merge(courseIdNew);
            }
            if (studentIdOld != null && !studentIdOld.equals(studentIdNew)) {
                studentIdOld.getCourseStudentEntityList().remove(courseStudentEntity);
                studentIdOld = em.merge(studentIdOld);
            }
            if (studentIdNew != null && !studentIdNew.equals(studentIdOld)) {
                studentIdNew.getCourseStudentEntityList().add(courseStudentEntity);
                studentIdNew = em.merge(studentIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = courseStudentEntity.getId();
                if (findCourseStudentEntity(id) == null) {
                    throw new NonexistentEntityException("The courseStudentEntity with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CourseStudentEntity courseStudentEntity;
            try {
                courseStudentEntity = em.getReference(CourseStudentEntity.class, id);
                courseStudentEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The courseStudentEntity with id " + id + " no longer exists.", enfe);
            }
            CourseEntity courseId = courseStudentEntity.getCourseId();
            if (courseId != null) {
                courseId.getCourseStudentEntityList().remove(courseStudentEntity);
                courseId = em.merge(courseId);
            }
            StudentEntity studentId = courseStudentEntity.getStudentId();
            if (studentId != null) {
                studentId.getCourseStudentEntityList().remove(courseStudentEntity);
                studentId = em.merge(studentId);
            }
            em.remove(courseStudentEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<CourseStudentEntity> findCourseStudentEntityEntities() {
        return findCourseStudentEntityEntities(true, -1, -1);
    }

    public List<CourseStudentEntity> findCourseStudentEntityEntities(int maxResults, int firstResult) {
        return findCourseStudentEntityEntities(false, maxResults, firstResult);
    }

    private List<CourseStudentEntity> findCourseStudentEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(CourseStudentEntity.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public CourseStudentEntity findCourseStudentEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CourseStudentEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getCourseStudentEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<CourseStudentEntity> rt = cq.from(CourseStudentEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
