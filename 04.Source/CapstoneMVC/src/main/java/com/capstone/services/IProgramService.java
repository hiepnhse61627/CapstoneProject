package com.capstone.services;

import com.capstone.entities.ProgramEntity;

import java.util.List;

public interface IProgramService {
    List<ProgramEntity> getAllPrograms();
    ProgramEntity getProgramById(int id);
    ProgramEntity getProgramByName(String name);
    ProgramEntity createProgram(ProgramEntity entity);
}
