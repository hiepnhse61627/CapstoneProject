package com.capstone.jpa.exJpa;

import com.capstone.entities.CredentialsRolesEntity;
import com.capstone.jpa.CredentialsRolesEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

public class ExCredentialsRolesEntityJpaController extends CredentialsRolesEntityJpaController {

    public ExCredentialsRolesEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public List<CredentialsRolesEntity> getCredentialsRolesByCredentialsId(int credentialsId) {
        EntityManager em = null;
        List<CredentialsRolesEntity> resultList = null;
        try {
            em = getEntityManager();
            TypedQuery<CredentialsRolesEntity> query =
                    em.createQuery("SELECT a FROM" +
                            " CredentialsRolesEntity a WHERE a.credentialsId = :userId", CredentialsRolesEntity.class);

            query.setParameter("userId", credentialsId);
            resultList = query.getResultList();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return resultList;
    }
}
