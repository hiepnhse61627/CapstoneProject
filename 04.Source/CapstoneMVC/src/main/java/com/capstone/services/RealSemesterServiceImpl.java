package com.capstone.services;

import com.capstone.entities.RealSemesterEntity;
import com.capstone.jpa.exJpa.ExRealSemesterEntityJpaController;
import com.capstone.jpa.exJpa.ExStudentEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class RealSemesterServiceImpl implements IRealSemesterService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExRealSemesterEntityJpaController con = new ExRealSemesterEntityJpaController(emf);

    @Override
    public List<RealSemesterEntity> getAllSemester() {
        return con.getAllSemester();
    }
}
