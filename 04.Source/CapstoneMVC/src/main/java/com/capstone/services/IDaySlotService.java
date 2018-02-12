package com.capstone.services;

import com.capstone.entities.DaySlotEntity;
import com.capstone.entities.RoomEntity;
import com.capstone.entities.SlotEntity;

import java.util.List;

public interface IDaySlotService {
    int getCurrentLine();
    int getTotalLine();
    void createDaySlotList(List<DaySlotEntity> daySlotEntityList);
    DaySlotEntity findDaySlotById(int id);
    DaySlotEntity findDaySlotByDateAndSlot(String date, SlotEntity slot);
    List<DaySlotEntity> findAllDaySlot();
    void saveDateSlot(DaySlotEntity emp) throws Exception;
    DaySlotEntity createDateSlot(DaySlotEntity daySlotEntity);
    void updateDaySlot(DaySlotEntity entity);
}
