package com.capstone.jpa.exJpa;

import com.capstone.entities.DynamicMenuEntity;
import com.capstone.entities.RolesAuthorityEntity;
import com.capstone.jpa.RolesAuthorityEntityJpaController;
import com.capstone.jpa.RolesAuthorityEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
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

            List<RolesAuthorityEntity> result = query.getResultList();

            if (!result.isEmpty()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println(e);
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RolesAuthorityEntity> findRolesAuthorityByMenuId(int dynamicMenuId) {
        EntityManager em = null;
        List<RolesAuthorityEntity> result = new ArrayList<>();
        try {
            em = getEntityManager();
            TypedQuery query = em.createQuery("SELECT a " +
                    "FROM RolesAuthorityEntity a" +
                    " WHERE a.menuId = :menuId", RolesAuthorityEntity.class);
            query.setParameter("menuId", dynamicMenuId);

            result = query.getResultList();


        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return result;
    }

    public boolean deleteRolesAuthorityByIdList(List<RolesAuthorityEntity> roleAuthorIds) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();

            for (int i = 0; i < roleAuthorIds.size(); i++) {
                RolesAuthorityEntity item = roleAuthorIds.get(i);
                RolesAuthorityEntity removeItem = em.find(RolesAuthorityEntity.class, item.getId());
                em.remove(removeItem);
            }
            em.flush();
            em.getTransaction().commit();

        } catch (Exception e) {
            em.getTransaction().rollback();
            System.out.println(e);
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return true;
    }

    public List<DynamicMenuEntity> findMenuByRoleId(int roleId) {
        EntityManager em = null;
        List<DynamicMenuEntity> resultList = null;
        try {
            em = getEntityManager();
            Query query = em.createQuery("SELECT r.menuId FROM RolesAuthorityEntity r WHERE r.rolesId.id = :roleId");
            query.setParameter("roleId", roleId);

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

    public List<RolesAuthorityEntity> findRolesAuthorityByRoleId(int roleId) {
        EntityManager em = null;
        List<RolesAuthorityEntity> resultList = null;
        try {
            em = getEntityManager();
            Query query = em.createQuery("SELECT r FROM RolesAuthorityEntity r WHERE r.rolesId.id = :roleId");
            query.setParameter("roleId", roleId);

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

    public List<RolesAuthorityEntity> findRolesAuthorityByRoleIdByUrl(int roleId, String url) {
        EntityManager em = null;
        List<RolesAuthorityEntity> resultList = null;
        try {
            em = getEntityManager();
            Query query = em.createQuery("SELECT r FROM RolesAuthorityEntity r" +
                    " WHERE r.rolesId.id = :roleId AND r.menuId.link LIKE :url");
            query.setParameter("roleId", roleId);
            query.setParameter("url", "%" + url + "%");

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

    public List<RolesAuthorityEntity> findRolesAuthorityByRoleIdByMenuId(int roleId, int dynamicMenuId) {
        EntityManager em = null;
        List<RolesAuthorityEntity> resultList = null;
        try {
            em = getEntityManager();
            TypedQuery<RolesAuthorityEntity> query = em.createQuery("SELECT r FROM RolesAuthorityEntity r" +
                    " WHERE r.rolesId.id = :roleId AND r.menuId.id = :dynamicMenuId", RolesAuthorityEntity.class);
            query.setParameter("roleId", roleId);
            query.setParameter("dynamicMenuId", dynamicMenuId);

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
