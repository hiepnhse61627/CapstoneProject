package com.capstone.jpa.exJpa;

import com.capstone.entities.RealSemesterEntity;
import com.capstone.jpa.RealSemesterEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class ExRealSemesterEntityJpaController extends RealSemesterEntityJpaController {

    public ExRealSemesterEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public List<RealSemesterEntity> getAllSemester() {
//        EntityManager em = getEntityManager();
        return this.findRealSemesterEntityEntities();
    }
}
