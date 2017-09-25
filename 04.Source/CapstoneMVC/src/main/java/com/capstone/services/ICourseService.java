package com.capstone.services;


import com.capstone.entities.CourseEntity;

import java.util.List;

public interface ICourseService {
    CourseEntity findCourseByClass(String className);
    void createCourseList(List<CourseEntity> courseEntityList);
    CourseEntity createCourse(CourseEntity entity);
}
