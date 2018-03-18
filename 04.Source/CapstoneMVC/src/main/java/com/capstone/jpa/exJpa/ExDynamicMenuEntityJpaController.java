package com.capstone.jpa.exJpa;

import com.capstone.entities.DynamicMenuEntity;
import com.capstone.jpa.DynamicMenuEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

public class ExDynamicMenuEntityJpaController extends DynamicMenuEntityJpaController {
    public ExDynamicMenuEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public DynamicMenuEntity findDynamicMenuByLink(String link) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<DynamicMenuEntity> query = em.createQuery("SELECT a" +
                    " FROM DynamicMenuEntity a WHERE a.link LIKE :link", DynamicMenuEntity.class);
            query.setParameter("link", "%" + link + "%");
            List<DynamicMenuEntity> list = query.getResultList();
            if (!list.isEmpty()) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public boolean createNewMenu(DynamicMenuEntity newMenu) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(newMenu);
            em.flush();
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            //rollback transaction if fail
            em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            if (em != null)
                em.close();
        }
    }


    public boolean updateMenu(DynamicMenuEntity menu) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.merge(menu);
            em.flush();
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            //rollback transaction if fail
            em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            if (em != null)
                em.close();
        }
    }


    public boolean deleteMenu(DynamicMenuEntity menu) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            DynamicMenuEntity removeItem = em.find(DynamicMenuEntity.class, menu.getId());
            em.remove(removeItem);
            em.flush();
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            //rollback transaction if fail
            em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            if (em != null)
                em.close();
        }
    }
}
