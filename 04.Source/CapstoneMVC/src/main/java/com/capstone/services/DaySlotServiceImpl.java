package com.capstone.services;

import com.capstone.entities.DaySlotEntity;
import com.capstone.entities.SlotEntity;
import com.capstone.jpa.exJpa.ExDaySlotEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class DaySlotServiceImpl implements IDaySlotService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExDaySlotEntityJpaController DaySlotEntityJpaController = new ExDaySlotEntityJpaController(emf);

    @Override
    public void createDaySlotList(List<DaySlotEntity> DaySlotEntityList) {
        DaySlotEntityJpaController.createDaySlotList(DaySlotEntityList);
    }

    @Override
    public DaySlotEntity findDaySlotById(int id) {
        return DaySlotEntityJpaController.findDaySlotEntity(id);
    }

    @Override
    public DaySlotEntity findDaySlotByDateAndSlot(String date, SlotEntity slot) {
        return DaySlotEntityJpaController.findDaySlotByDateAndSlot(date, slot);
    }

    @Override
    public List<DaySlotEntity> findDaySlotByDate(String date) {
        return DaySlotEntityJpaController.findDaySlotByDate(date);
    }

    @Override
    public List<DaySlotEntity> findAllDaySlot() {
        return null;
    }

    @Override
    public void saveDateSlot(DaySlotEntity emp) throws Exception {
        DaySlotEntityJpaController.saveDaySlot(emp);
    }

    @Override
    public DaySlotEntity createDateSlot(DaySlotEntity daySlotEntity) {
        return DaySlotEntityJpaController.createDaySlot(daySlotEntity);
    }

    public List<DaySlotEntity> findAllDaySlots() {
        return DaySlotEntityJpaController.findDaySlotEntityEntities();
    }

    @Override
    public void updateDaySlot(DaySlotEntity entity) {
        try {
            DaySlotEntityJpaController.edit(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCurrentLine() {
        return DaySlotEntityJpaController.getCurrentLine();
    }

    @Override
    public int getTotalLine() {
        return DaySlotEntityJpaController.getTotalLine();
    }
}
