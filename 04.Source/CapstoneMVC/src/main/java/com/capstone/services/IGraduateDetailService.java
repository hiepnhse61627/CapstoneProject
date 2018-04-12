package com.capstone.services;

import com.capstone.entities.GraduateDetailEntity;

public interface IGraduateDetailService {
     GraduateDetailEntity findGraduateDetailEntity(Integer id);
    void create(GraduateDetailEntity graduateDetailEntity);
    void edit(GraduateDetailEntity graduateDetailEntity);
}
