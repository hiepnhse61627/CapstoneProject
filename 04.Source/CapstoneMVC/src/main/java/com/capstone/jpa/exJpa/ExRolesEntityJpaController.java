package com.capstone.jpa.exJpa;

import com.capstone.entities.CurriculumEntity;
import com.capstone.entities.RolesEntity;
import com.capstone.jpa.RolesEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

public class ExRolesEntityJpaController extends RolesEntityJpaController {
    public ExRolesEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public List<RolesEntity> getAllRoles() {
        EntityManager em = null;
        List<RolesEntity> list;
        try {
            em = getEntityManager();
            TypedQuery<RolesEntity> query = em.createQuery(
                    "SELECT r FROM RolesEntity r", RolesEntity.class);

            list = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null)
                em.close();
        }
        return list;
    }


    public boolean createNewRole(RolesEntity newRole) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(newRole);
            em.flush();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (em != null)
                em.close();
        }
        return true;
    }

    public boolean updateRole(RolesEntity currentRole) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.merge(currentRole);
            em.flush();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (em != null)
                em.close();
        }
        return true;
    }

    public List<RolesEntity> getRolesByName(String role) {
        EntityManager em = null;
        List<RolesEntity> list;
        try {
            em = getEntityManager();
            TypedQuery<RolesEntity> query = em.createQuery(
                    "SELECT r FROM RolesEntity r Where r.name = :role", RolesEntity.class);
            query.setParameter("role", role);

            list = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (em != null)
                em.close();
        }
        return list;
    }


}
