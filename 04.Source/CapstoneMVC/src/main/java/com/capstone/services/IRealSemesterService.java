package com.capstone.services;

import com.capstone.entities.RealSemesterEntity;

public interface IRealSemesterService {
    RealSemesterEntity findSemesterByName(String name);
    RealSemesterEntity createRealSemester(RealSemesterEntity entity);
}
