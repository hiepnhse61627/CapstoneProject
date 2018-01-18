package com.capstone.services;

import com.capstone.entities.SlotEntity;
import com.capstone.jpa.exJpa.ExSlotEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class SlotServiceImpl implements ISlotService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExSlotEntityJpaController SlotEntityJpaController = new ExSlotEntityJpaController(emf);

    @Override
    public void createSlotList(List<SlotEntity> SlotEntityList) {
        SlotEntityJpaController.createSlotList(SlotEntityList);
    }

    @Override
    public SlotEntity findSlotById(int id) {
        return SlotEntityJpaController.findSlotEntity(id);
    }
    
    @Override
    public List<SlotEntity> findSlotsByName(String searchValue) {
        return SlotEntityJpaController.findSlotsByName(searchValue);
    }

    public List<SlotEntity> findAllSlots() {
        return SlotEntityJpaController.findSlotEntityEntities();
    }

    @Override
    public void saveSlot(SlotEntity emp) throws Exception {
        SlotEntityJpaController.saveSlot(emp);
    }


    @Override
    public SlotEntity createSlot(SlotEntity SlotEntity) {
        return SlotEntityJpaController.createSlot(SlotEntity);
    }

    @Override
    public void updateSlot(SlotEntity entity) {
        try {
            SlotEntityJpaController.edit(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCurrentLine() {
        return SlotEntityJpaController.getCurrentLine();
    }

    @Override
    public int getTotalLine() {
        return SlotEntityJpaController.getTotalLine();
    }
}
