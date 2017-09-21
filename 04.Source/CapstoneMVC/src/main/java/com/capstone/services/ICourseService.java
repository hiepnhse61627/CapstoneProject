package com.capstone.services;

import com.capstone.entities.CourseEntity;

public interface ICourseService {
    CourseEntity findCourseByClass(String className);
    CourseEntity createCourse(CourseEntity entity);
}
