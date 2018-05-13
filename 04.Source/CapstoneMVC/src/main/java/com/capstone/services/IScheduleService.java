package com.capstone.services;

import com.capstone.entities.*;

import java.util.List;

public interface IScheduleService {
    int getCurrentLine();
    int getTotalLine();
    List<ScheduleEntity> findAllSchedules();
    void createScheduleList(List<ScheduleEntity> ScheduleEntityList);
    ScheduleEntity findScheduleById(int id);
    ScheduleEntity findScheduleByDateSlotAndRoom(DaySlotEntity dateSlot, RoomEntity room);
    ScheduleEntity findScheduleByDateSlotAndGroupName(DaySlotEntity dateSlot, String groupName);
    ScheduleEntity findScheduleByDateSlotAndLecture(DaySlotEntity dateSlot, EmployeeEntity emp);
    ScheduleEntity findScheduleByDateSlotAndLectureAndCourseDontCareIsActive(DaySlotEntity dateSlot, EmployeeEntity emp, CourseEntity course);
    List<ScheduleEntity> findScheduleByGroupName(String groupName);
    List<ScheduleEntity> findScheduleByGroupNameAndCourse(String groupName, CourseEntity course);
    List<ScheduleEntity> findScheduleByLectureHaveParentSchedule(Integer lectureId);
    List<ScheduleEntity> findScheduleByGroupnameAndCourseAndLecture(CourseEntity course, String groupName, EmployeeEntity emp);
    ScheduleEntity findScheduleByDateSlotAndLectureAndRoomAndCourse(DaySlotEntity date, EmployeeEntity lecture, RoomEntity room, CourseEntity course);
    List<ScheduleEntity> findScheduleBySubjectCodeAndGroupNameBeforeNowTime(String subjectCode, String groupName);
    List<ScheduleEntity> findScheduleByLecture(Integer lectureId);
    void saveSchedule(ScheduleEntity emp) throws Exception;
    ScheduleEntity createSchedule(ScheduleEntity ScheduleEntity);
    void updateSchedule(ScheduleEntity entity);
    List<ScheduleEntity> findScheduleInRange(Integer lecture, int iDisplayStart, int iDisplayLength);
    ScheduleEntity findScheduleByDateSlotAndLectureAndRoomAndCourseDontCareIsActive(DaySlotEntity date, EmployeeEntity lecture, RoomEntity room, CourseEntity course);
    List<ScheduleEntity> findScheduleByDateSlot(DaySlotEntity dateSlot);

    }
