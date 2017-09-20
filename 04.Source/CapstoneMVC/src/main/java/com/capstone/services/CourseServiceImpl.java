package com.capstone.services;

import com.capstone.entities.CourseEntity;
import com.capstone.jpa.exJpa.ExCourseEntityJpaController;
import com.capstone.jpa.exJpa.ExRealSemesterEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class CourseServiceImpl implements ICourseService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExCourseEntityJpaController courseEntityJpaController = new ExCourseEntityJpaController(emf);

    @Override
    public CourseEntity findCourseByClass(String className) {
        return courseEntityJpaController.findCourseByClass(className);
    }

    @Override
    public CourseEntity createCourse(CourseEntity entity) {
        return courseEntityJpaController.createCourse(entity);
    }
}
