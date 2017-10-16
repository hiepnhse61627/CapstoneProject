package com.capstone.services;

import com.capstone.entities.CurriculumMappingEntity;

public interface ICurriculumMappingService {
    CurriculumMappingEntity createCurriculumMapping(CurriculumMappingEntity entity);

    String getSemesterTermByStudentIdAndProgramId(int studentId, int programId);
}
