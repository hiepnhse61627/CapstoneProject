package com.capstone.jpa.exJpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WebBaseJpaController implements Serializable {

    public WebBaseJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<Object> getDataInDatabaseByQuery(String sqlString) {
        EntityManager em = null;
        List<Object> objects = new ArrayList<>();

        try {
            em = getEntityManager();
            Query query = em.createNativeQuery(sqlString);
            objects = query.getResultList();

            return objects;
        } catch (NoResultException nrEx) {
            nrEx.printStackTrace();
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
