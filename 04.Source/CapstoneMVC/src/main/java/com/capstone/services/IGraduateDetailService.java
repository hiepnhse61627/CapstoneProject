package com.capstone.services;

import com.capstone.entities.GraduateDetailEntity;

public interface IGraduateDetailService {
     GraduateDetailEntity findGraduateDetailEntity(Integer studentId);
    void create(GraduateDetailEntity graduateDetailEntity);
    void edit(GraduateDetailEntity graduateDetailEntity);
}
