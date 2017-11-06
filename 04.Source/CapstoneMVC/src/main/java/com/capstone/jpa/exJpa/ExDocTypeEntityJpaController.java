package com.capstone.jpa.exJpa;

import com.capstone.entities.DocTypeEntity;
import com.capstone.jpa.DocTypeEntityJpaController;
import org.apache.commons.lang3.reflect.Typed;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.print.Doc;
import java.util.List;

public class ExDocTypeEntityJpaController extends DocTypeEntityJpaController {
    public ExDocTypeEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public List<DocTypeEntity> getAllDocTypes() {
        return this.findDocTypeEntityEntities();
    }

    public DocTypeEntity createDocType(DocTypeEntity entity) {
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

    public DocTypeEntity findDocType(int id) {
        try {
            EntityManager em = getEntityManager();
            return em.find(DocTypeEntity.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public DocTypeEntity findDocType(String name) {
        try {
            EntityManager em = getEntityManager();
            TypedQuery<DocTypeEntity> query = em.createQuery("SELECT a FROM DocTypeEntity a WHERE a.name = :name", DocTypeEntity.class);
            query.setParameter("name", name);
            return query.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("DocType" + name + "not exist");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
