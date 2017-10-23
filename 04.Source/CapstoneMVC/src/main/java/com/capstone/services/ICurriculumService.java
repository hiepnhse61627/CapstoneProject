package com.capstone.services;

import com.capstone.entities.CurriculumEntity;

import java.util.List;

public interface ICurriculumService {
    List<CurriculumEntity> getAllCurriculums();
    List<CurriculumEntity> getCurriculums(int firstResult, int maxResult, String searchValue);
    int countCurriculums(String searchValue);
    int countAllCurriculums();
    CurriculumEntity getCurriculumById(int id);
    CurriculumEntity getCurriculumByName(String name);
    CurriculumEntity createCurriculum(CurriculumEntity entity);
}
