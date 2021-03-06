package com.capstone.jpa.exJpa;

import com.capstone.entities.CredentialsEntity;
import com.capstone.jpa.CredentialsEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

public class ExCredentialsEntityJpaController extends CredentialsEntityJpaController {

    public ExCredentialsEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public CredentialsEntity findCredential(String username) {
        EntityManager manager = getEntityManager();
        TypedQuery<CredentialsEntity> query = manager.createQuery("select a from CredentialsEntity a where a.username = :username", CredentialsEntity.class);
        query.setParameter("username", username);
        List<CredentialsEntity> list = query.getResultList();
        if (list.isEmpty()) return null;
        return list.get(0);
    }

    public CredentialsEntity findCredential(String username, String password) {
        EntityManager manager = getEntityManager();
        TypedQuery<CredentialsEntity> query = manager.createQuery("SELECT a FROM CredentialsEntity a WHERE a.username = :username AND a.password = :password", CredentialsEntity.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        List<CredentialsEntity> list = query.getResultList();
        if (list.isEmpty()) return null;
        return list.get(0);
    }

    public void CreateCredential(CredentialsEntity entity) {
        EntityManager manager = getEntityManager();
        manager.getTransaction().begin();
        manager.persist(entity);
        manager.getTransaction().commit();
    }

    public CredentialsEntity findCredentialByEmail(String email) {
        EntityManager manager = getEntityManager();
        TypedQuery<CredentialsEntity> query = manager.createQuery("SELECT a FROM CredentialsEntity a WHERE a.email = :email", CredentialsEntity.class);
        query.setParameter("email", email);
        List<CredentialsEntity> list = query.getResultList();
        if (list.isEmpty()) return null;
        return list.get(0);
    }

    public void saveCredential(CredentialsEntity entity, boolean persist) {
        EntityManager manager = getEntityManager();
        manager.getTransaction().begin();
        if (persist) manager.persist(entity);
        else manager.merge(entity);
        manager.flush();
        manager.getTransaction().commit();
        entity = manager.merge(entity);
        manager.refresh(entity);
    }
}
