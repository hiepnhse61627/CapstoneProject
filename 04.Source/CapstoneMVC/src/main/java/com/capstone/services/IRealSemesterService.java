package com.capstone.services;

import com.capstone.entities.RealSemesterEntity;

import java.util.List;

public interface IRealSemesterService {
    RealSemesterEntity findSemesterByName(String name);
    RealSemesterEntity createRealSemester(RealSemesterEntity entity);
    List<RealSemesterEntity> getAllSemester();
}
