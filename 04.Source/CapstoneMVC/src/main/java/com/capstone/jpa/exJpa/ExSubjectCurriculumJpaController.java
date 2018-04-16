package com.capstone.jpa.exJpa;

import com.capstone.entities.CurriculumEntity;
import com.capstone.entities.SubjectCurriculumEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.jpa.SubjectCurriculumEntityJpaController;
import com.capstone.models.Logger;
import com.capstone.models.SubjectModel;
import com.capstone.services.ISubjectCurriculumService;
import com.capstone.services.SubjectCurriculumServiceImpl;
import org.apache.commons.lang3.reflect.Typed;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class ExSubjectCurriculumJpaController extends SubjectCurriculumEntityJpaController {

    public ExSubjectCurriculumJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public SubjectCurriculumEntity findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SubjectCurriculumEntity.class, id);
        } finally {
            em.close();
        }
    }

    public List<SubjectCurriculumEntity> getSubjectCurriculums(int curriculumId) {
        List<SubjectCurriculumEntity> result;
        EntityManager em = null;

        try {
            em = getEntityManager();

            String queryStr = "SELECT sc FROM SubjectCurriculumEntity sc WHERE sc.curriculumId.id = :curriculumId ORDER BY sc.ordinalNumber";
            TypedQuery<SubjectCurriculumEntity> query = em.createQuery(queryStr, SubjectCurriculumEntity.class);
            query.setParameter("curriculumId", curriculumId);

            result = query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    public SubjectCurriculumEntity findByName(String name) {
//        EntityManager em = getEntityManager();
//        try {
//            TypedQuery<SubjectCurriculumEntity> query = em.createQuery("SELECT a FROM SubjectCurriculumEntity a WHERE a.name = :name", SubjectCurriculumEntity.class);
//            query.setParameter("name", name);
//            return query.getSingleResult();
//        } catch (Exception e) {
//            return null;
//        } finally {
//            em.close();
//        }
        return null;
    }

    public SubjectModel updateSubject(SubjectModel subject, int curriculumId) {
        EntityManager manager = getEntityManager();
        ISubjectCurriculumService subjectService = new SubjectCurriculumServiceImpl();
        manager.getTransaction().begin();
        try {
            SubjectCurriculumEntity uSubject = manager.find(SubjectCurriculumEntity.class, curriculumId);
            uSubject.setSubjectCredits(subject.getCredits());

            manager.merge(uSubject);
            manager.flush();
        } catch (Exception e) {
            Logger.writeLog(e);
            subject.setResult(false);
            subject.setErrorMessage(e.getMessage());
            return subject;
        }
        manager.getTransaction().commit();
        subject.setResult(true);
        return subject;
    }

    public SubjectCurriculumEntity createCurriculum(SubjectCurriculumEntity entity) {
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

    public void deleteCurriculum(int subjectCurriculumId) {
        EntityManager em = null;

        try {
            em = getEntityManager();
            SubjectCurriculumEntity entity = this.findSubjectCurriculumEntity(subjectCurriculumId);

            em.getTransaction().begin();
            em.remove(em.merge(entity));
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }

    }

    public void createCurriculumList(List<SubjectCurriculumEntity> subjectCurriculumEntityList) {
        EntityManager em = getEntityManager();
        int batchSize = 1000;
        try {
            em.getTransaction().begin();
            for (int i = 0; i < subjectCurriculumEntityList.size(); i++) {
                if (i > 0 && i % batchSize == 0) {
                    em.flush();
                    em.clear();
                    em.getTransaction().commit();
                    em.getTransaction().begin();
                }
                SubjectCurriculumEntity item = subjectCurriculumEntityList.get(i);
                em.persist(item);
                System.out.println("Insert - " + (i + 1));
            }
            em.flush();
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    public void updateCurriculum(SubjectCurriculumEntity entity) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        try {
            entity = em.merge(entity);
            em.flush();
            em.refresh(entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.getTransaction().commit();
        }
    }

    public CurriculumEntity findCurriculum(String cur, String program) {
        EntityManager em = getEntityManager();

        try {
            TypedQuery<CurriculumEntity> query = em.createQuery("SELECT a FROM CurriculumEntity a WHERE a.name = :program AND a.programId.name = :cur", CurriculumEntity.class);
            query.setParameter("cur", cur);
            query.setParameter("program", program);
            return query.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("data " + program + "_" + cur + " not exist");
            return null;
        } catch (NonUniqueResultException e) {
            System.out.println("data " + program + "_" + cur + " has multiple results");
            return null;
        }
    }

    public List<SubjectCurriculumEntity> getSubjectIds(List<Integer> curriculumIds, Integer currentTerm) {
        EntityManager em = getEntityManager();

        try {
            String sqlString = "SELECT s FROM SubjectCurriculumEntity s WHERE s.curriculumId.id IN :curriculumIds AND s.termNumber BETWEEN 1 AND :currentTerm";
            Query query = em.createQuery(sqlString);
            query.setParameter("curriculumIds", curriculumIds);
            query.setParameter("currentTerm", currentTerm);

            List<SubjectCurriculumEntity> list = query.getResultList();
            return list;
        } catch (NoResultException nrEx) {
            return null;
        }
    }

    public CurriculumEntity cleanCurriculum(CurriculumEntity cur) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            cur = em.merge(cur);
            for (SubjectCurriculumEntity c : cur.getSubjectCurriculumEntityList()) {
                SubjectCurriculumEntity tmp = em.merge(c);
                em.remove(tmp);
                em.flush();
            }
            em.refresh(cur);

            em.getTransaction().commit();

            return cur;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<SubjectCurriculumEntity> getSubjectCurriculumByStudent(int studentId) {
        EntityManager em = getEntityManager();
        List<SubjectCurriculumEntity> subjectCurriculumEntityList = null;
        try {


            Query query = em.createQuery("SELECT sc FROM SubjectCurriculumEntity sc WHERE sc.curriculumId.id IN " +
                    "(SELECT ds.curriculumId.id FROM DocumentStudentEntity" +
                    " ds WHERE ds.studentId.id = :studentId)");
            query.setParameter("studentId", studentId);
            subjectCurriculumEntityList = query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return subjectCurriculumEntityList;
    }

    public List<SubjectCurriculumEntity> getSubjectCurriculumByStudentByTerm(int studentId, int term) {
        EntityManager em = getEntityManager();
        List<SubjectCurriculumEntity> subjectCurriculumEntityList = null;
        try {


            Query query = em.createQuery("SELECT sc FROM SubjectCurriculumEntity sc WHERE sc.curriculumId.id IN " +
                    "(SELECT ds.curriculumId.id FROM DocumentStudentEntity" +
                    " ds WHERE ds.studentId.id = :studentId) AND sc.termNumber = :term");
            query.setParameter("studentId", studentId);
            query.setParameter("term", term);
            subjectCurriculumEntityList = query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return subjectCurriculumEntityList;
    }
}
