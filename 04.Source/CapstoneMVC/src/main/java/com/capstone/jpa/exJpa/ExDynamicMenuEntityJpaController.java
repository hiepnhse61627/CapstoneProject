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
            query.setParameter("link" ,"%" + link + "%");
                   List<DynamicMenuEntity> list = query.getResultList();
            return list.get(0);
        } finally {
            em.close();
        }
    }


}
