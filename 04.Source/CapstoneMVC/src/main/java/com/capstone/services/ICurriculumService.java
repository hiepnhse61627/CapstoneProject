package com.capstone.services;

import com.capstone.entities.CurriculumEntity;

import java.util.List;

public interface ICurriculumService {
    List<CurriculumEntity> getAllCurriculums();
    CurriculumEntity getCurriculumById(int id);
    CurriculumEntity getCurriculumByName(String name);
    CurriculumEntity createCurriculum(CurriculumEntity entity);
}
