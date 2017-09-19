package com.capstone.services;

import com.capstone.entities.RealSemesterEntity;
import com.capstone.jpa.exJpa.ExRealSemesterEntityJpaController;
import com.capstone.jpa.exJpa.ExStudentEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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
}
