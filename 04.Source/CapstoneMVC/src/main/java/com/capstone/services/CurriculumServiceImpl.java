package com.capstone.services;

import com.capstone.entities.CurriculumEntity;
import com.capstone.jpa.exJpa.ExCurriculumEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class CurriculumServiceImpl implements  ICurriculumService {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExCurriculumEntityJpaController controller = new ExCurriculumEntityJpaController(emf);

    @Override
    public List<CurriculumEntity> getAllCurriculums() {
        return controller.getAllCurriculums();
    }

    @Override
    public List<CurriculumEntity> getCurriculums(int firstResult, int maxResult, String searchValue) {
        return controller.getCurriculums(firstResult, maxResult, searchValue);
    }

    @Override
    public int countCurriculums(String searchValue) {
        return controller.countCurriculums(searchValue);
    }

    @Override
    public int countAllCurriculums() {
        return controller.countAllCurriculums();
    }

    @Override
    public CurriculumEntity getCurriculumById(int id) {
        return controller.getCurriculumById(id);
    }

    @Override
    public CurriculumEntity getCurriculumByName(String name) {
        return controller.getCurriculumByName(name);
    }

    @Override
    public CurriculumEntity createCurriculum(CurriculumEntity entity) {
        return controller.createCurriculum(entity);
    }
}
