package com.capstone.jpa.exJpa;

import com.capstone.entities.ProgramEntity;
import com.capstone.jpa.ProgramEntityJpaController;
import com.capstone.models.Logger;

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
        return this.findProgramEntityEntities();
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

}
