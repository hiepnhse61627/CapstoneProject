package com.capstone.services;

import com.capstone.entities.CurriculumMappingEntity;
import com.capstone.jpa.exJpa.ExCurriculumMappingEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class CurriculumMappingServiceImpl implements ICurriculumMappingService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExCurriculumMappingEntityJpaController controller = new ExCurriculumMappingEntityJpaController(emf);

    @Override
    public CurriculumMappingEntity createCurriculumMapping(CurriculumMappingEntity entity) {
        return controller.createCurriculumMapping(entity);
    }
}
