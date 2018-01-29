package com.capstone.services;

import com.capstone.entities.DaySlotEntity;
import com.capstone.entities.RoomEntity;
import com.capstone.entities.ScheduleEntity;
import com.capstone.entities.SlotEntity;

import java.util.List;

public interface IScheduleService {
    int getCurrentLine();
    int getTotalLine();
    void createScheduleList(List<ScheduleEntity> ScheduleEntityList);
    ScheduleEntity findScheduleById(int id);
    ScheduleEntity findScheduleByDateSlotAndRoom(DaySlotEntity dateSlot, RoomEntity room);
    List<ScheduleEntity> findAllSchedule();
    void saveSchedule(ScheduleEntity emp) throws Exception;
    ScheduleEntity createSchedule(ScheduleEntity ScheduleEntity);
    void updateSchedule(ScheduleEntity entity);
}
