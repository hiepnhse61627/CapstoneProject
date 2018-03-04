package com.capstone.services;

import com.capstone.entities.PrequisiteEntity;
import com.capstone.jpa.ExPrerequisiteJpaController;
import com.capstone.jpa.exJpa.ExCourseEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class PrerequisiteServiceImpl implements IPrerequisiteService {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExPrerequisiteJpaController prerequisiteJpaController = new ExPrerequisiteJpaController(emf);


    @Override
    public PrequisiteEntity getPrerequisiteBySubjectId(int subjectId) {
        return prerequisiteJpaController.getPrerequisiteBySubjectId(subjectId);
    }

    @Override
    public List<PrequisiteEntity> getAllPrerequisite() {
        return prerequisiteJpaController.getAllPrerequisite();
    }
}
