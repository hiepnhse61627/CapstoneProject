package com.capstone.services;

import com.capstone.entities.RoomEntity;

import java.util.List;

public interface IRoomService {
    int getCurrentLine();
    int getTotalLine();
    void createRoomList(List<RoomEntity> RoomEntityList);
    RoomEntity findRoomById(int id);
    List<RoomEntity> findRoomsByName(String searchValue);
    RoomEntity  findRoomsByExactName(String searchValue);
    List<RoomEntity> findRoomsByCapacity(int searchValue);
    List<RoomEntity> findAllRooms();
    void saveRoom(RoomEntity emp) throws Exception;
    RoomEntity createRoom(RoomEntity RoomEntity);
    void updateRoom(RoomEntity entity);
}
