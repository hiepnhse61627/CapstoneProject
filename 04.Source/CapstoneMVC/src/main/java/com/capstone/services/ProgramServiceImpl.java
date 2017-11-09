package com.capstone.services;

import com.capstone.entities.ProgramEntity;
import com.capstone.jpa.exJpa.ExProgramEntityJpaController;
import com.capstone.models.ProgramModel;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class ProgramServiceImpl implements IProgramService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExProgramEntityJpaController controller = new ExProgramEntityJpaController(emf);

    @Override
    public List<ProgramEntity> getAllPrograms() {
        return controller.getAllPrograms();
    }

    @Override
    public ProgramEntity getProgramById(int id) {
        return controller.getProgramById(id);
    }

    @Override
    public ProgramEntity getProgramByName(String name) {
        return controller.getProgramByName(name);
    }

    @Override
    public ProgramEntity createProgram(ProgramEntity entity) {
        return controller.createProgram(entity);
    }

    @Override
    public ProgramModel updateProgram(ProgramModel program) {
        return controller.updateProgram(program);
    }
}
