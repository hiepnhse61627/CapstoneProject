package com.capstone.services;

import com.capstone.entities.ProgramEntity;
import com.capstone.models.ProgramModel;

import java.util.List;

public interface IProgramService {
    List<ProgramEntity> getAllPrograms();
    ProgramEntity getProgramById(int id);
    ProgramEntity getProgramByName(String name);
    ProgramEntity createProgram(ProgramEntity entity);
    ProgramModel updateProgram(ProgramModel program);
}
