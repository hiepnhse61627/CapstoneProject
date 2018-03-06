package com.capstone.services;

import com.capstone.entities.*;
import com.capstone.jpa.exJpa.ExCourseStudentEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class CourseStudentServiceImpl implements ICourseStudentService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExCourseStudentEntityJpaController CourseStudentEntityJpaController = new ExCourseStudentEntityJpaController(emf);

    @Override
    public void createCourseStudentList(List<CourseStudentEntity> CourseStudentEntityList) {
        CourseStudentEntityJpaController.createCourseStudentList(CourseStudentEntityList);
    }

    @Override
    public CourseStudentEntity findCourseStudentById(int id) {
        return CourseStudentEntityJpaController.findCourseStudentEntity(id);
    }

    @Override
    public List<CourseStudentEntity> findCourseStudentByGroupNameAndCourse(String groupName, CourseEntity course) {
        return CourseStudentEntityJpaController.findCourseStudentByGroupNameAndCourse( groupName, course);
    }

    @Override
    public CourseStudentEntity findCourseStudentByCourseAndStudent(CourseEntity course, StudentEntity student) {
        return CourseStudentEntityJpaController.findCourseStudentByCourseAndStudent(course, student);
    }

    @Override
    public List<CourseStudentEntity> findCourseStudentByGroupName(String groupName) {
        return CourseStudentEntityJpaController.findCourseStudentByGroupName(groupName);
    }

    @Override
    public List<CourseStudentEntity> findCourseStudentByStudent(StudentEntity studentEntity) {
        return CourseStudentEntityJpaController.findCourseStudentByStudent(studentEntity);
    }

    @Override
    public List<CourseStudentEntity> findAllCourseStudent() {
        return null;
    }

    @Override
    public void saveCourseStudent(CourseStudentEntity emp) throws Exception {
        CourseStudentEntityJpaController.saveCourseStudent(emp);
    }

    @Override
    public CourseStudentEntity createCourseStudent(CourseStudentEntity CourseStudentEntity) {
        return CourseStudentEntityJpaController.createCourseStudent(CourseStudentEntity);
    }

    public List<CourseStudentEntity> findAllCourseStudents() {
        return CourseStudentEntityJpaController.findCourseStudentEntityEntities();
    }

    @Override
    public void updateCourseStudent(CourseStudentEntity entity) {
        try {
            CourseStudentEntityJpaController.edit(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCurrentLine() {
        return CourseStudentEntityJpaController.getCurrentLine();
    }

    @Override
    public int getTotalLine() {
        return CourseStudentEntityJpaController.getTotalLine();
    }
}
