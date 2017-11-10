package com.capstone.jpa.exJpa;

import com.capstone.entities.CurriculumEntity;
import com.capstone.jpa.CurriculumEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;

public class ExCurriculumEntityJpaController extends CurriculumEntityJpaController {
    public ExCurriculumEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public List<CurriculumEntity> getAllCurriculums() {
        return this.findCurriculumEntityEntities();
    }

    public CurriculumEntity getCurriculumById(int id) {
        CurriculumEntity result = null;
        EntityManager em = null;

        try {
            em = getEntityManager();
            String queryStr = "SELECT c FROM CurriculumEntity c WHERE c.id = :id";
            TypedQuery<CurriculumEntity> query = em.createQuery(queryStr, CurriculumEntity.class);
            query.setParameter("id", id);
            result = query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<CurriculumEntity> getAllCurriculumsByProgramId(int programId) {
        List<CurriculumEntity> result = null;
        EntityManager em = null;

        try {
            em = getEntityManager();

            String queryStr = "SELECT c FROM CurriculumEntity c WHERE c.programId.id = :programId";
            TypedQuery<CurriculumEntity> query = em.createQuery(queryStr, CurriculumEntity.class);
            query.setParameter("programId", programId);

            result = query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    public int countAllCurriculums() {
        EntityManager em = null;
        int result = 0;

        try {
            em = getEntityManager();

            String queryStr = "SELECT COUNT(c) FROM CurriculumEntity c";
            TypedQuery<Integer> query = em.createQuery(queryStr, Integer.class);

            result = ((Number) query.getSingleResult()).intValue();
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    public int countCurriculums(String searchValue) {
        EntityManager em = null;
        int result = 0;

        try {
            em = getEntityManager();

            String queryStr = "SELECT COUNT(c) FROM CurriculumEntity c" +
                    " WHERE c.programId.name LIKE :programName" +
                    " OR c.name LIKE :curriculumName";
            TypedQuery<Integer> query = em.createQuery(queryStr, Integer.class);
            if (!searchValue.isEmpty()) {
                int pos = searchValue.indexOf("_");
                if (pos != -1) {
                    String programName = searchValue.substring(0, pos);
                    String curriculumName = searchValue.substring(pos + 1);

                    query.setParameter("programName", programName);
                    query.setParameter("curriculumName", curriculumName + "%");
                } else {
                    query.setParameter("programName", "%" + searchValue + "%");
                    query.setParameter("curriculumName", "%" + searchValue + "%");
                }
            } else {
                query.setParameter("programName", "%%");
                query.setParameter("curriculumName", "%%");
            }

            result = ((Number) query.getSingleResult()).intValue();
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    public CurriculumEntity getCurriculumByNameAndProgramId(String name, int programId) {
        CurriculumEntity entity;
        EntityManager em = null;

        try {
            em = getEntityManager();

            String queryStr = "SELECT c FROM CurriculumEntity c WHERE c.programId.id = :programId AND c.name = :name";
            TypedQuery<CurriculumEntity> query = em.createQuery(queryStr, CurriculumEntity.class);
            query.setParameter("programId", programId);
            query.setParameter("name", name);

            List<CurriculumEntity> list = query.getResultList();
            entity = !list.isEmpty() ? list.get(0) : null;
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return entity;
    }

    public List<CurriculumEntity> getCurriculums(int firstResult, int maxResult, String searchValue) {
        List<CurriculumEntity> result;
        EntityManager em = null;

        try {
            em = getEntityManager();

            String queryStr = "SELECT c FROM CurriculumEntity c" +
                    " WHERE c.programId.name LIKE :programName" +
                    " OR c.name LIKE :curriculumName" +
                    " ORDER BY c.programId.name, c.name DESC";
            TypedQuery<CurriculumEntity> query = em.createQuery(queryStr, CurriculumEntity.class)
                    .setFirstResult(firstResult)
                    .setMaxResults(maxResult);

            if (!searchValue.isEmpty()) {
                int pos = searchValue.indexOf("_");
                if (pos != -1) {
                    String programName = searchValue.substring(0, pos);
                    String curriculumName = searchValue.substring(pos + 1);

                    query.setParameter("programName", programName);
                    query.setParameter("curriculumName", curriculumName + "%");
                } else {
                    query.setParameter("programName", "%" + searchValue + "%");
                    query.setParameter("curriculumName", "%" + searchValue + "%");
                }
            } else {
                query.setParameter("programName", "%%");
                query.setParameter("curriculumName", "%%");
            }

            result = query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    public CurriculumEntity getCurriculumByName(String name) {
        CurriculumEntity entity = null;
        EntityManager em = null;

        try {
            em = getEntityManager();
            TypedQuery<CurriculumEntity> query = em.createQuery(
                    "SELECT c FROM CurriculumEntity c WHERE c.name = :name", CurriculumEntity.class);
            query.setParameter("name", name);

            entity = query.getSingleResult();
        } finally {
            em.close();
        }

        return entity;
    }

    public CurriculumEntity createCurriculum(CurriculumEntity entity) {
        EntityManager em = null;

        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(entity);
            em.flush();
//            entity = em.merge(entity);
//            em.refresh(entity);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return entity;
    }
}
