package com.capstone.services;

import com.capstone.entities.RoomEntity;
import com.capstone.jpa.exJpa.ExRoomEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class RoomServiceImpl implements IRoomService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExRoomEntityJpaController RoomEntityJpaController = new ExRoomEntityJpaController(emf);

    @Override
    public void createRoomList(List<RoomEntity> RoomEntityList) {
        RoomEntityJpaController.createRoomList(RoomEntityList);
    }

    @Override
    public RoomEntity findRoomById(int id) {
        return RoomEntityJpaController.findRoomEntity(id);
    }
    
    @Override
    public List<RoomEntity> findRoomsByName(String searchValue) {
        return RoomEntityJpaController.findRoomsByName(searchValue);
    }

    @Override
    public RoomEntity findRoomsByExactName(String searchValue) {
        return RoomEntityJpaController.findRoomsByExactName(searchValue);
    }

    @Override
    public List<RoomEntity> findRoomsByCapacity(int searchValue) {
        return RoomEntityJpaController.findRoomsByCapacity(searchValue);
    }

    public List<RoomEntity> findAllRooms() {
        return RoomEntityJpaController.findRoomEntityEntities();
    }

    @Override
    public void saveRoom(RoomEntity emp) throws Exception {
        RoomEntityJpaController.saveRoom(emp);
    }


    @Override
    public RoomEntity createRoom(RoomEntity RoomEntity) {
        return RoomEntityJpaController.createRoom(RoomEntity);
    }

    @Override
    public void updateRoom(RoomEntity entity) {
        try {
            RoomEntityJpaController.edit(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCurrentLine() {
        return RoomEntityJpaController.getCurrentLine();
    }

    @Override
    public int getTotalLine() {
        return RoomEntityJpaController.getTotalLine();
    }
}
