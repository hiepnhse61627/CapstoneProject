package com.capstone.jpa.exJpa;

import com.capstone.entities.ProgramEntity;
import com.capstone.jpa.ProgramEntityJpaController;
import com.capstone.models.Logger;
import com.sun.deploy.security.ValidationState;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

public class ExProgramEntityJpaController extends ProgramEntityJpaController {

    public ExProgramEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public List<ProgramEntity> getAllPrograms() {
        List<ProgramEntity> result = null;
        EntityManager em = null;

        try {
            em = getEntityManager();

            String queryStr = "SELECT p FROM ProgramEntity p ORDER BY p.name";
            TypedQuery<ProgramEntity> query = em.createQuery(queryStr, ProgramEntity.class);
            result = query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
//        return this.findProgramEntityEntities();
    }

    public ProgramEntity getProgramById(int id) {
        return this.findProgramEntity(id);
    }

    public ProgramEntity getProgramByName(String name) {
        EntityManager em = getEntityManager();
        ProgramEntity program = null;
        try {
            TypedQuery<ProgramEntity> query = em.createQuery(
                    "SELECT p FROM ProgramEntity p WHERE p.name = :name", ProgramEntity.class);
            query.setParameter("name", name);

            program = query.getSingleResult();
        } catch (NoResultException ex) {
            program = null;
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.writeLog(ex);
        } finally {
            em.close();
        }

        return program;
    }

    public ProgramEntity createProgram(ProgramEntity entity) {
        EntityManager em = null;

        try {
            em = getEntityManager();

            em.getTransaction().begin();
            em.persist(entity);
            em.flush();
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return entity;
    }

}
