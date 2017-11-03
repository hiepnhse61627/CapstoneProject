package com.capstone.jpa.exJpa;

import com.capstone.entities.DocumentStudentEntity;
import com.capstone.jpa.DocumentStudentEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

public class ExDocumentStudentEntityJpaController extends DocumentStudentEntityJpaController {
    public ExDocumentStudentEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public DocumentStudentEntity createDocumentStudent(DocumentStudentEntity entity) {
        EntityManager em = null;

        try {
            em = getEntityManager();

            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return entity;
    }

    public DocumentStudentEntity getLastestDocumentStudentById(int studentId) {
        EntityManager em = null;
        DocumentStudentEntity entity = null;

        try {
            em = getEntityManager();

            String queryStr = "SELECT ds FROM DocumentStudentEntity ds" +
                    " WHERE ds.studentId.id = :studentId" +
                    " AND ds.createdDate = (SELECT (tDS.createdDate) FROM DocumentStudentEntity tDS WHERE tDS.id = ds.id)";
            TypedQuery<DocumentStudentEntity> query = em.createQuery(queryStr, DocumentStudentEntity.class);
            query.setParameter("studentId", studentId);

            List<DocumentStudentEntity> list = query.getResultList();
            if (!list.isEmpty()) {
                entity = list.get(0);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return entity;
    }

    public List<DocumentStudentEntity> getAllLatestDocumentStudent() {
        List<DocumentStudentEntity> result = null;
        EntityManager em = null;

        try {
            em = getEntityManager();

            String queryStr = "SELECT ds FROM DocumentStudentEntity ds" +
                    " WHERE ds.createdDate = (SELECT MAX(tDS.createdDate) FROM DocumentStudentEntity tDS WHERE tDS.id = ds.id)";
            TypedQuery<DocumentStudentEntity> query = em.createQuery(queryStr, DocumentStudentEntity.class);
            result = query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }


        return result;
    }

    public List<DocumentStudentEntity> getAllLatestDocumentStudentByProgramId(int programId) {
        List<DocumentStudentEntity> result = null;
        EntityManager em = null;

        try {
            em = getEntityManager();

            String queryStr = "SELECT ds FROM DocumentStudentEntity ds" +
                    " WHERE ds.createdDate = (SELECT MAX(tDS.createdDate) FROM DocumentStudentEntity tDS WHERE tDS.id = ds.id)" +
                    " AND ds.curriculumId.programId.id = :programId";
            TypedQuery<DocumentStudentEntity> query = em.createQuery(queryStr, DocumentStudentEntity.class);
            query.setParameter("programId", programId);
            result = query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }


        return result;
    }

    public List<DocumentStudentEntity> getDocumentStudentByIdList(List<Integer> idList) {
        List<DocumentStudentEntity> result = null;
        EntityManager em = null;

        try {
            em = getEntityManager();

            String queryStr = "SELECT ds FROM DocumentStudentEntity ds" +
                    " WHERE ds.createdDate = (SELECT MAX(tDS.createdDate) FROM DocumentStudentEntity tDS WHERE tDS.id = ds.id)" +
                    " AND ds.studentId.id IN :idList";
            TypedQuery<DocumentStudentEntity> query = em.createQuery(queryStr, DocumentStudentEntity.class);
            query.setParameter("idList", idList);
            result = query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }
}
