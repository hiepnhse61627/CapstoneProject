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
    List<ScheduleEntity> findScheduleByGroupnameAndCourse(CourseEntity course, String groupName);
    List<ScheduleEntity> findAllSchedule();
    void saveSchedule(ScheduleEntity emp) throws Exception;
    ScheduleEntity createSchedule(ScheduleEntity ScheduleEntity);
    void updateSchedule(ScheduleEntity entity);
}
