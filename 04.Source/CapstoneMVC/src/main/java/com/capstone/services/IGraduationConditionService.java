package com.capstone.services;

import com.capstone.entities.GraduationConditionEntity;

import java.util.List;

public interface IGraduationConditionService {
    int getGraduateCreditsByStartCourseByProgramId(String startCourse, int programId);
    List<GraduationConditionEntity> findAllGraduationCondition();
}
