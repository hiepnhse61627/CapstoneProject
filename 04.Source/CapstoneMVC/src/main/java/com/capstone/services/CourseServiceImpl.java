package com.capstone.services;

import com.capstone.entities.CourseEntity;
import com.capstone.jpa.CourseEntityJpaController;
import com.capstone.jpa.exJpa.ExCourseEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class CourseServiceImpl implements ICourseService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExCourseEntityJpaController courseEntityJpaController = new ExCourseEntityJpaController(emf);

    @Override
    public CourseEntity findCourseByClass(String className) {
        return courseEntityJpaController.findCourseByClass(className);
    }

    @Override
    public void createCourseList(List<CourseEntity> courseEntityList) {
        courseEntityJpaController.createCourseList(courseEntityList);
    }

    @Override
    public CourseEntity createCourse(CourseEntity entity) {
        return courseEntityJpaController.createCourse(entity);
    }
}
