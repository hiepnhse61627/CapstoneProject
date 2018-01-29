package com.capstone.services;

import com.capstone.entities.DaySlotEntity;
import com.capstone.entities.RoomEntity;
import com.capstone.entities.ScheduleEntity;
import com.capstone.entities.SlotEntity;
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
    public List<ScheduleEntity> findAllSchedule() {
        return null;
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
            ScheduleEntityJpaController.edit(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
