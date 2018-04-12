package com.capstone.services;

import com.capstone.entities.GraduationConditionEntity;
import com.capstone.jpa.exJpa.ExGraduationConditionEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class GraduationConditionServiceImpl implements  IGraduationConditionService {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExGraduationConditionEntityJpaController controller = new ExGraduationConditionEntityJpaController(emf);

    @Override
    public int getGraduateCreditsByStartCourseByProgramId(String startCourse, int programId) {
        return controller.getGraduateCreditsByStartCourseByProgramId(startCourse, programId);
    }

    @Override
    public List<GraduationConditionEntity> findAllGraduationCondition() {
        return controller.findAllGraduationCondition();
    }
}
