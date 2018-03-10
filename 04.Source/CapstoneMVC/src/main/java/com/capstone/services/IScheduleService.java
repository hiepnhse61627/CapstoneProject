package com.capstone.services;

import com.capstone.entities.*;

import java.util.List;

public interface IScheduleService {
    int getCurrentLine();
    int getTotalLine();
    void createScheduleList(List<ScheduleEntity> ScheduleEntityList);
    ScheduleEntity findScheduleById(int id);
    ScheduleEntity findScheduleByDateSlotAndRoom(DaySlotEntity dateSlot, RoomEntity room);
    ScheduleEntity findScheduleByDateSlotAndGroupName(DaySlotEntity dateSlot, String groupName);
    ScheduleEntity findScheduleByDateSlotAndLecture(DaySlotEntity dateSlot, EmployeeEntity emp);
    List<ScheduleEntity> findScheduleByGroupName(String groupName);
    List<ScheduleEntity> findScheduleByLectureHaveParentSchedule(Integer lectureId);
    List<ScheduleEntity> findScheduleByGroupnameAndCourseAndLecture(CourseEntity course, String groupName, EmployeeEntity emp);
    ScheduleEntity findScheduleByDateSlotAndLectureAndRoomAndCourse(DaySlotEntity date, EmployeeEntity lecture, RoomEntity room, CourseEntity course);
    List<ScheduleEntity> findAllSchedule();
    List<ScheduleEntity> findScheduleByLecture(Integer lecture);
    void saveSchedule(ScheduleEntity emp) throws Exception;
    ScheduleEntity createSchedule(ScheduleEntity ScheduleEntity);
    void updateSchedule(ScheduleEntity entity);

}
