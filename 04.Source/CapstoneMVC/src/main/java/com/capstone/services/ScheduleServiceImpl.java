package com.capstone.services;

import com.capstone.entities.*;
import com.capstone.jpa.exJpa.ExScheduleEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class ScheduleServiceImpl implements IScheduleService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExScheduleEntityJpaController ScheduleEntityJpaController = new ExScheduleEntityJpaController(emf);

    @Override
    public void createScheduleList(List<ScheduleEntity> ScheduleEntityList) {
        ScheduleEntityJpaController.createScheduleList(ScheduleEntityList);
    }

    @Override
    public ScheduleEntity findScheduleById(int id) {
        return ScheduleEntityJpaController.findScheduleEntity(id);
    }

    @Override
    public ScheduleEntity findScheduleByDateSlotAndRoom(DaySlotEntity date, RoomEntity room) {
        return ScheduleEntityJpaController.findScheduleByDateSlotAndRoom(date, room);
    }

    @Override
    public ScheduleEntity findScheduleByDateSlotAndGroupName(DaySlotEntity dateSlot, String groupName) {
        return ScheduleEntityJpaController.findScheduleByDateSlotAndGroupName(dateSlot, groupName);
    }

    @Override
    public ScheduleEntity findScheduleByDateSlotAndLecture(DaySlotEntity dateSlot, EmployeeEntity emp) {
        return ScheduleEntityJpaController.findScheduleByDateSlotAndLecture(dateSlot, emp);
    }

    @Override
    public ScheduleEntity findScheduleByDateSlotAndLectureAndCourseDontCareIsActive(DaySlotEntity dateSlot, EmployeeEntity emp, CourseEntity course) {
        return ScheduleEntityJpaController.findScheduleByDateSlotAndLectureAndCourseDontCareIsActive(dateSlot, emp, course);
    }

    @Override
    public List<ScheduleEntity> findScheduleByGroupName(String groupName) {
        return ScheduleEntityJpaController.findScheduleByGroupName(groupName);
    }

    @Override
    public List<ScheduleEntity> findScheduleByLectureHaveParentSchedule(Integer lectureId) {
        return ScheduleEntityJpaController.findScheduleByLectureHaveParentSchedule(lectureId);
    }

    @Override
    public List<ScheduleEntity> findScheduleByGroupnameAndCourseAndLecture(CourseEntity course, String groupName, EmployeeEntity emp) {
        return ScheduleEntityJpaController.findScheduleByGroupnameAndCourseAndLecture(course, groupName, emp);
    }

    @Override
    public ScheduleEntity findScheduleByDateSlotAndLectureAndRoomAndCourse(DaySlotEntity date, EmployeeEntity lecture, RoomEntity room, CourseEntity course) {
        return ScheduleEntityJpaController.findScheduleByDateSlotAndLectureAndRoomAndCourse( date,lecture,room, course);
    }

    @Override
    public List<ScheduleEntity> findAllSchedule() {
        return ScheduleEntityJpaController.findAllSchedule();
    }

    @Override
    public List<ScheduleEntity> findScheduleByLecture(Integer lecture) {
        return ScheduleEntityJpaController.findScheduleByLecture(lecture);

    }

    @Override
    public void saveSchedule(ScheduleEntity emp) throws Exception {
        ScheduleEntityJpaController.saveSchedule(emp);
    }

    @Override
    public ScheduleEntity createSchedule(ScheduleEntity ScheduleEntity) {
        return ScheduleEntityJpaController.createSchedule(ScheduleEntity);
    }

    public List<ScheduleEntity> findAllSchedules() {
        return ScheduleEntityJpaController.findScheduleEntityEntities();
    }

    @Override
    public void updateSchedule(ScheduleEntity entity) {
        try {
//            ScheduleEntityJpaController.edit(entity);
            ScheduleEntityJpaController.updateSchedule(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<ScheduleEntity> findScheduleInRange(Integer lecture, int iDisplayStart, int iDisplayLength) {
        return ScheduleEntityJpaController.findScheduleInRange(lecture, iDisplayStart,iDisplayLength);
    }

    @Override
    public ScheduleEntity findScheduleByDateSlotAndLectureAndRoomAndCourseDontCareIsActive(DaySlotEntity date, EmployeeEntity lecture, RoomEntity room, CourseEntity course) {
        return ScheduleEntityJpaController.findScheduleByDateSlotAndLectureAndRoomAndCourseDontCareIsActive(date,lecture,room, course);
    }

    @Override
    public int getCurrentLine() {
        return ScheduleEntityJpaController.getCurrentLine();
    }

    @Override
    public int getTotalLine() {
        return ScheduleEntityJpaController.getTotalLine();
    }
}
