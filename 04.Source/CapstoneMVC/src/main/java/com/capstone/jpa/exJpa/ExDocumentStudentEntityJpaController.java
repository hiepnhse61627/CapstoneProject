package com.capstone.jpa.exJpa;

import com.capstone.entities.*;
import com.capstone.jpa.DocumentStudentEntityJpaController;
import com.capstone.models.Ultilities;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class ExDocumentStudentEntityJpaController extends DocumentStudentEntityJpaController {
    public ExDocumentStudentEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public DocumentStudentEntity createDocumentStudent(DocumentStudentEntity documentStudentEntity) {
        EntityManager em = null;

        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CurriculumEntity curriculumId = documentStudentEntity.getCurriculumId();
            if (curriculumId != null) {
                curriculumId = em.getReference(curriculumId.getClass(), curriculumId.getId());
                documentStudentEntity.setCurriculumId(curriculumId);
            }
            DocumentEntity documentId = documentStudentEntity.getDocumentId();
            if (documentId != null) {
                documentId = em.getReference(documentId.getClass(), documentId.getId());
                documentStudentEntity.setDocumentId(documentId);
            }
            OldRollNumberEntity oldStudentId = documentStudentEntity.getOldStudentId();
            if (oldStudentId != null) {
                oldStudentId = em.getReference(oldStudentId.getClass(), oldStudentId.getId());
                documentStudentEntity.setOldStudentId(oldStudentId);
            }
            StudentEntity studentId = documentStudentEntity.getStudentId();
            if (studentId != null) {
                studentId = em.getReference(studentId.getClass(), studentId.getId());
                documentStudentEntity.setStudentId(studentId);
            }
            em.persist(documentStudentEntity);
            if (curriculumId != null) {
                curriculumId.getDocumentStudentEntityList().add(documentStudentEntity);
                curriculumId = em.merge(curriculumId);
            }
            if (documentId != null) {
                documentId.getDocumentStudentEntityList().add(documentStudentEntity);
                documentId = em.merge(documentId);
            }
            if (oldStudentId != null) {
                oldStudentId.getDocumentStudentEntityList().add(documentStudentEntity);
                oldStudentId = em.merge(oldStudentId);
            }
            if (studentId != null) {
                studentId.getDocumentStudentEntityList().add(documentStudentEntity);
                studentId = em.merge(studentId);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return documentStudentEntity;
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
                    " WHERE ds.createdDate = (SELECT MAX(tDS.createdDate) FROM DocumentStudentEntity tDS WHERE tDS.studentId.id = ds.studentId.id)";
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
                    " WHERE ds.createdDate = (SELECT MAX(tDS.createdDate) FROM DocumentStudentEntity tDS WHERE tDS.studentId.id = ds.studentId.id)" +
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
                    " WHERE ds.createdDate = (SELECT MAX(tDS.createdDate) FROM DocumentStudentEntity tDS WHERE tDS.studentId.id = ds.studentId.id)" +
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

    public List<DocumentStudentEntity> getDocumentStudentByByStudentId(List<Integer> idList) {
        List<DocumentStudentEntity> result = null;
        EntityManager em = null;

        try {
            em = getEntityManager();

            String queryStr = "SELECT d FROM DocumentStudentEntity d WHERE d.studentId.id IN :idList";
            TypedQuery<DocumentStudentEntity> query = em.createQuery(queryStr, DocumentStudentEntity.class);
            query.setParameter("idList", idList);
            result = query.getResultList();
//            result = Ultilities.sortDocumentStudentListByDate(result);
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    public List<DocumentStudentEntity> getDocumentStudentListByStudentId(Integer studentId) {
        List<DocumentStudentEntity> result = null;
        EntityManager em = null;

        try {
            em = getEntityManager();

            String sqlString = "SELECT d FROM DocumentStudentEntity d WHERE d.studentId.id = :id";
            Query query = em.createQuery(sqlString);
            query.setParameter("id", studentId);

            result = query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

}
