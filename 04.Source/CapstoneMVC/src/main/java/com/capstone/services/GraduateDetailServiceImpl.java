package com.capstone.services;

import com.capstone.entities.GraduateDetailEntity;
import com.capstone.jpa.exJpa.ExGraduateDetailEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class GraduateDetailServiceImpl implements IGraduateDetailService {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExGraduateDetailEntityJpaController controller = new ExGraduateDetailEntityJpaController(emf);

    @Override
    public GraduateDetailEntity findGraduateDetailEntity(Integer id) {
        return controller.findGraduateDetailEntity(id);
    }

    @Override
    public void create(GraduateDetailEntity graduateDetailEntity) {
        try {

            controller.create(graduateDetailEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void edit(GraduateDetailEntity graduateDetailEntity) {
        try {
            controller.edit(graduateDetailEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
