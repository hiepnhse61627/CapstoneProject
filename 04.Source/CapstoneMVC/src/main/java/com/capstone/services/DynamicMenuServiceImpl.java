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

    @Override
    public DynamicMenuEntity findDynamicMenuByLink(String link) {
        return controller.findDynamicMenuByLink(link);
    }

    @Override
    public boolean createNewMenu(DynamicMenuEntity newMenu) {
        return controller.createNewMenu(newMenu);
    }

    @Override
    public DynamicMenuEntity findDynamicMenuEntity(Integer id) {
        return controller.findDynamicMenuEntity(id);
    }

    @Override
    public boolean updateMenu(DynamicMenuEntity menu) {
        return controller.updateMenu(menu);
    }

    @Override
    public boolean deleteMenu(DynamicMenuEntity menu) {
        return controller.deleteMenu(menu);
    }

}
