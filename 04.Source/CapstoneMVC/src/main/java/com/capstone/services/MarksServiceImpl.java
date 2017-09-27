package com.capstone.services;

import com.capstone.entities.MarksEntity;
import com.capstone.jpa.exJpa.ExMarksEntityJpaController;
import com.capstone.jpa.exJpa.ExRealSemesterEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class MarksServiceImpl implements IMarksService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExMarksEntityJpaController marksEntityJpaController = new ExMarksEntityJpaController(emf);

    @Override
    public void createMark(MarksEntity entity) {
        try {
            marksEntityJpaController.create(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createMarks(List<MarksEntity> marksEntities) {
        try {
            marksEntityJpaController.createMarks(marksEntities);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getTotalExistMarks() {
        return marksEntityJpaController.getTotalExistMarks();
    }

    @Override
    public int getSuccessSavedMark() {
        return marksEntityJpaController.getSuccessSavedMark();
    }

    @Override
    public List<MarksEntity> getMarkByConditions(String semesterId, String subjectId, String searchKey) {
        return marksEntityJpaController.getMarksByConditions(semesterId, subjectId, searchKey);
    }
}
