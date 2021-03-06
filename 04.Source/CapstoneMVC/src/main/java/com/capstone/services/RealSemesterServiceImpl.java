package com.capstone.services;

import com.capstone.entities.RealSemesterEntity;
import com.capstone.jpa.exJpa.ExRealSemesterEntityJpaController;
import com.capstone.jpa.exJpa.ExStudentEntityJpaController;
import com.capstone.jpa.exceptions.NonexistentEntityException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class RealSemesterServiceImpl implements IRealSemesterService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExRealSemesterEntityJpaController realSemesterEntityJpaController = new ExRealSemesterEntityJpaController(emf);

    @Override
    public RealSemesterEntity findSemesterByName(String name) {
        return realSemesterEntityJpaController.findRealSemesterByName(name);
    }

    @Override
    public RealSemesterEntity createRealSemester(RealSemesterEntity entity) {
        return realSemesterEntityJpaController.createRealSemester(entity);
    }

    @Override
    public List<RealSemesterEntity> getAllSemester() {
        return realSemesterEntityJpaController.findRealSemesterEntityEntities();
    }

    @Override
    public RealSemesterEntity findSemesterById(Integer id) {
        return realSemesterEntityJpaController.findRealSemesterEntity(id);
    }

    @Override
    public void update(RealSemesterEntity semesterEntity) throws Exception {
        realSemesterEntityJpaController.edit(semesterEntity);
    }
}
