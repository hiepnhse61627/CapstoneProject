package com.capstone.services;

import com.capstone.entities.*;
import com.capstone.entities.CourseStudentEntity;

import java.util.List;

public interface ICourseStudentService {
    int getCurrentLine();
    int getTotalLine();
    void createCourseStudentList(List<CourseStudentEntity> CourseStudentEntityList);
    CourseStudentEntity findCourseStudentById(int id);
    List<CourseStudentEntity> findCourseStudentByGroupNameAndCourse(String groupName, CourseEntity course);
    CourseStudentEntity findCourseStudentByCourseAndStudent(CourseEntity course, StudentEntity student);
    List<CourseStudentEntity> findCourseStudentByGroupName(String groupName);
    List<CourseStudentEntity> findAllCourseStudent();
    void saveCourseStudent(CourseStudentEntity emp) throws Exception;
    CourseStudentEntity createCourseStudent(CourseStudentEntity CourseStudentEntity);
    void updateCourseStudent(CourseStudentEntity entity);
}
