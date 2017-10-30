package com.capstone.services;

import com.capstone.entities.DynamicMenuEntity;
import com.capstone.jpa.exJpa.ExDynamicMenuEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class DynamicMenuServiceImpl implements IDynamicMenuService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExDynamicMenuEntityJpaController controller = new ExDynamicMenuEntityJpaController(emf);

    @Override
    public List<DynamicMenuEntity> getAllMenu() {
        return controller.findDynamicMenuEntityEntities();
    }
}
