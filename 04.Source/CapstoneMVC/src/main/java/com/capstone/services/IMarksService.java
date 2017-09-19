package com.capstone.services;

import com.capstone.entities.MarksEntity;

import java.util.List;

public interface IMarksService {
    void createMark(MarksEntity entity);
    void createMarks(List<MarksEntity> marksEntities);
}
