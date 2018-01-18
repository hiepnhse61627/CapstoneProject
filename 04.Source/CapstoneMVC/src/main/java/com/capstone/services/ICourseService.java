package com.capstone.services;


import com.capstone.entities.CourseEntity;
import com.capstone.models.DatatableModel;

import java.util.List;

public interface ICourseService {
    CourseEntity findCourseBySemesterAndSubjectCode(String semesterName, String subjectCode);
    CourseEntity findCourseBySubjectCode(String subjectCode);
    void createCourseList(List<CourseEntity> courseEntityList);
    CourseEntity createCourse(CourseEntity entity);
    void updateCourse(CourseEntity model);
    void deleteCourse(int courseId);
    List<CourseEntity> getCourseListForDatatable(DatatableModel model);
    List<CourseEntity> getAllCourse();
    CourseEntity findCourseById(int id);
}
