package com.capstone.jpa.exJpa;

import com.capstone.entities.RolesAuthorityEntity;
import com.capstone.jpa.RolesAuthorityEntityJpaController;
import com.capstone.jpa.RolesAuthorityEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

public class ExRolesAuthorityEntityJpaController extends RolesAuthorityEntityJpaController {

    public ExRolesAuthorityEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public RolesAuthorityEntity createRolesAuthority(RolesAuthorityEntity entity) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(entity);
            em.flush();
            em.getTransaction().commit();
            return entity;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public boolean findRolesAuthorityByRoleIdAndMenuId(String roleId, int dynamicMenuId) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            TypedQuery query = em.createQuery("SELECT a " +
                    "FROM RolesAuthorityEntity a" +
                    " WHERE a.rolesId = :roleId and a.menuId = :menuId", RolesAuthorityEntity.class);
            query.setParameter("roleId", roleId);
            query.setParameter("menuId", dynamicMenuId);

            List<RolesAuthorityEntity> result =  query.getResultList();

            if (!result.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }catch(Exception e){
            System.out.println(e);
            return false;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
