package com.capstone.services;


import com.capstone.entities.CourseEntity;

import java.util.List;

public interface ICourseService {
    CourseEntity findCourseByClassAndSubjectCode(String className, String subjectCode);
    void createCourseList(List<CourseEntity> courseEntityList);
    CourseEntity createCourse(CourseEntity entity);
}
