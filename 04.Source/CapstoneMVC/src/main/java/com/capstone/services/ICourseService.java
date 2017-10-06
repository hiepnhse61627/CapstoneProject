package com.capstone.services;


import com.capstone.entities.CourseEntity;
import com.capstone.models.DatatableModel;

import java.util.List;

public interface ICourseService {
    CourseEntity findCourseByClassAndSubjectCode(String className, String subjectCode);
    void createCourseList(List<CourseEntity> courseEntityList);
    CourseEntity createCourse(CourseEntity entity);
    void updateCourse(CourseEntity model);
    void deleteCourse(int courseId);
    List<CourseEntity> getCourseListForDatatable(DatatableModel model);
}
