package com.capstone.services;

import com.capstone.entities.SlotEntity;

import java.util.List;

public interface ISlotService {
    int getCurrentLine();
    int getTotalLine();
    void createSlotList(List<SlotEntity> slotEntityList);
    SlotEntity findSlotById(int id);
    List<SlotEntity> findSlotsByName(String searchValue);
    List<SlotEntity> findAllSlots();
    void saveSlot(SlotEntity emp) throws Exception;
    SlotEntity createSlot(SlotEntity SlotEntity);
    void updateSlot(SlotEntity entity);
}
