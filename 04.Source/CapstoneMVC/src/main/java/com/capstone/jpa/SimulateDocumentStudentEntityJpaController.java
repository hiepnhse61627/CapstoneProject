package com.capstone.jpa;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.capstone.entities.CurriculumEntity;
import com.capstone.entities.DocumentEntity;
import com.capstone.entities.SimulateDocumentStudentEntity;
import com.capstone.entities.StudentEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.capstone.jpa.exceptions.NonexistentEntityException;
import com.capstone.jpa.exceptions.PreexistingEntityException;

/**
 *
 * @author StormNs
 */
public class SimulateDocumentStudentEntityJpaController implements Serializable {

    public SimulateDocumentStudentEntityJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(SimulateDocumentStudentEntity simulateDocumentStudentEntity) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            CurriculumEntity curriculumId = simulateDocumentStudentEntity.getCurriculumId();
            if (curriculumId != null) {
                curriculumId = em.getReference(curriculumId.getClass(), curriculumId.getId());
                simulateDocumentStudentEntity.setCurriculumId(curriculumId);
            }
            DocumentEntity documentId = simulateDocumentStudentEntity.getDocumentId();
            if (documentId != null) {
                documentId = em.getReference(documentId.getClass(), documentId.getId());
                simulateDocumentStudentEntity.setDocumentId(documentId);
            }
            StudentEntity studentId = simulateDocumentStudentEntity.getStudentId();
            if (studentId != null) {
                studentId = em.getReference(studentId.getClass(), studentId.getId());
                simulateDocumentStudentEntity.setStudentId(studentId);
            }
            em.persist(simulateDocumentStudentEntity);
            if (curriculumId != null) {
                curriculumId.getSimulateDocumentStudentEntityList().add(simulateDocumentStudentEntity);
                curriculumId = em.merge(curriculumId);
            }
            if (documentId != null) {
                documentId.getSimulateDocumentStudentEntityList().add(simulateDocumentStudentEntity);
                documentId = em.merge(documentId);
            }
            if (studentId != null) {
                studentId.getSimulateDocumentStudentEntityList().add(simulateDocumentStudentEntity);
                studentId = em.merge(studentId);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findSimulateDocumentStudentEntity(simulateDocumentStudentEntity.getId()) != null) {
                throw new PreexistingEntityException("SimulateDocumentStudentEntity " + simulateDocumentStudentEntity + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(SimulateDocumentStudentEntity simulateDocumentStudentEntity) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SimulateDocumentStudentEntity persistentSimulateDocumentStudentEntity = em.find(SimulateDocumentStudentEntity.class, simulateDocumentStudentEntity.getId());
            CurriculumEntity curriculumIdOld = persistentSimulateDocumentStudentEntity.getCurriculumId();
            CurriculumEntity curriculumIdNew = simulateDocumentStudentEntity.getCurriculumId();
            DocumentEntity documentIdOld = persistentSimulateDocumentStudentEntity.getDocumentId();
            DocumentEntity documentIdNew = simulateDocumentStudentEntity.getDocumentId();
            StudentEntity studentIdOld = persistentSimulateDocumentStudentEntity.getStudentId();
            StudentEntity studentIdNew = simulateDocumentStudentEntity.getStudentId();
            if (curriculumIdNew != null) {
                curriculumIdNew = em.getReference(curriculumIdNew.getClass(), curriculumIdNew.getId());
                simulateDocumentStudentEntity.setCurriculumId(curriculumIdNew);
            }
            if (documentIdNew != null) {
                documentIdNew = em.getReference(documentIdNew.getClass(), documentIdNew.getId());
                simulateDocumentStudentEntity.setDocumentId(documentIdNew);
            }
            if (studentIdNew != null) {
                studentIdNew = em.getReference(studentIdNew.getClass(), studentIdNew.getId());
                simulateDocumentStudentEntity.setStudentId(studentIdNew);
            }
            simulateDocumentStudentEntity = em.merge(simulateDocumentStudentEntity);
            if (curriculumIdOld != null && !curriculumIdOld.equals(curriculumIdNew)) {
                curriculumIdOld.getSimulateDocumentStudentEntityList().remove(simulateDocumentStudentEntity);
                curriculumIdOld = em.merge(curriculumIdOld);
            }
            if (curriculumIdNew != null && !curriculumIdNew.equals(curriculumIdOld)) {
                curriculumIdNew.getSimulateDocumentStudentEntityList().add(simulateDocumentStudentEntity);
                curriculumIdNew = em.merge(curriculumIdNew);
            }
            if (documentIdOld != null && !documentIdOld.equals(documentIdNew)) {
                documentIdOld.getSimulateDocumentStudentEntityList().remove(simulateDocumentStudentEntity);
                documentIdOld = em.merge(documentIdOld);
            }
            if (documentIdNew != null && !documentIdNew.equals(documentIdOld)) {
                documentIdNew.getSimulateDocumentStudentEntityList().add(simulateDocumentStudentEntity);
                documentIdNew = em.merge(documentIdNew);
            }
            if (studentIdOld != null && !studentIdOld.equals(studentIdNew)) {
                studentIdOld.getSimulateDocumentStudentEntityList().remove(simulateDocumentStudentEntity);
                studentIdOld = em.merge(studentIdOld);
            }
            if (studentIdNew != null && !studentIdNew.equals(studentIdOld)) {
                studentIdNew.getSimulateDocumentStudentEntityList().add(simulateDocumentStudentEntity);
                studentIdNew = em.merge(studentIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = simulateDocumentStudentEntity.getId();
                if (findSimulateDocumentStudentEntity(id) == null) {
                    throw new NonexistentEntityException("The simulateDocumentStudentEntity with id " + id + " no longer exists.");
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
            SimulateDocumentStudentEntity simulateDocumentStudentEntity;
            try {
                simulateDocumentStudentEntity = em.getReference(SimulateDocumentStudentEntity.class, id);
                simulateDocumentStudentEntity.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The simulateDocumentStudentEntity with id " + id + " no longer exists.", enfe);
            }
            CurriculumEntity curriculumId = simulateDocumentStudentEntity.getCurriculumId();
            if (curriculumId != null) {
                curriculumId.getSimulateDocumentStudentEntityList().remove(simulateDocumentStudentEntity);
                curriculumId = em.merge(curriculumId);
            }
            DocumentEntity documentId = simulateDocumentStudentEntity.getDocumentId();
            if (documentId != null) {
                documentId.getSimulateDocumentStudentEntityList().remove(simulateDocumentStudentEntity);
                documentId = em.merge(documentId);
            }
            StudentEntity studentId = simulateDocumentStudentEntity.getStudentId();
            if (studentId != null) {
                studentId.getSimulateDocumentStudentEntityList().remove(simulateDocumentStudentEntity);
                studentId = em.merge(studentId);
            }
            em.remove(simulateDocumentStudentEntity);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<SimulateDocumentStudentEntity> findSimulateDocumentStudentEntityEntities() {
        return findSimulateDocumentStudentEntityEntities(true, -1, -1);
    }

    public List<SimulateDocumentStudentEntity> findSimulateDocumentStudentEntityEntities(int maxResults, int firstResult) {
        return findSimulateDocumentStudentEntityEntities(false, maxResults, firstResult);
    }

    private List<SimulateDocumentStudentEntity> findSimulateDocumentStudentEntityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(SimulateDocumentStudentEntity.class));
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

    public SimulateDocumentStudentEntity findSimulateDocumentStudentEntity(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(SimulateDocumentStudentEntity.class, id);
        } finally {
            em.close();
        }
    }

    public int getSimulateDocumentStudentEntityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<SimulateDocumentStudentEntity> rt = cq.from(SimulateDocumentStudentEntity.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
