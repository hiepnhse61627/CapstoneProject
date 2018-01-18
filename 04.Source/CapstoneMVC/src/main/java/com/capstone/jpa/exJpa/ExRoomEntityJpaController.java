package com.capstone.jpa.exJpa;

import com.capstone.entities.RoomEntity;
import com.capstone.jpa.RoomEntityJpaController;
import com.capstone.models.Logger;

import javax.persistence.*;
import java.util.List;

public class ExRoomEntityJpaController extends RoomEntityJpaController {

    public ExRoomEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    private int currentLine = 0;
    private int totalLine = 0;

    public int getCurrentLine() {
        return currentLine;
    }

    public int getTotalLine() {
        return totalLine;
    }

    public void saveRoom(RoomEntity Room) {
        try {
            EntityManager manager = getEntityManager();
            manager.getTransaction().begin();
//            for (DocumentRoomEntity doc : Room.getDocumentRoomEntityList()) {
//                if (doc.getId() == null) {
//                    manager.persist(doc);
//                    manager.flush();
//                    manager.merge(doc);
//                    manager.refresh(doc);
//                }
//            }
            manager.merge(Room);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }
    }

    public void createRoomList(List<RoomEntity> Rooms) {
        totalLine = Rooms.size();
        currentLine = 0;

        EntityManager em = null;
        try {
            em = getEntityManager();
            for (RoomEntity Room : Rooms) {
                try {
                    em.getTransaction().begin();

                    // Create Room
                    TypedQuery<RoomEntity> queryRoom = em.createQuery(
                            "SELECT c FROM RoomEntity c WHERE c.name = :name", RoomEntity.class);
                    queryRoom.setParameter("name", Room.getName());

                    List<RoomEntity> std = queryRoom.getResultList();
                    if (std.isEmpty()) {
                        em.persist(Room);
                        em.flush();
                    }
                    em.getTransaction().commit();
                } catch (Exception e) {
                    System.out.println("Room " + Room.getName() + "caused " + e.getMessage());
                    e.printStackTrace();
                }

                currentLine++;

                System.out.println(currentLine + "-" + totalLine);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }

        totalLine = 0;
        currentLine = 0;
    }


    public List<RoomEntity> findRoomsByName(String searchValue) {
        EntityManager em = getEntityManager();
        List<RoomEntity> result = null;

        try {
            String queryStr = "SELECT s FROM RoomEntity s" +
                    " WHERE s.name LIKE :name";
            TypedQuery<RoomEntity> query = em.createQuery(queryStr, RoomEntity.class);
            query.setParameter("name", "%" + searchValue + "%");

            result = query.getResultList();

        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    public RoomEntity createRoom(RoomEntity RoomEntity) {
        EntityManager em = null;

        try {
            em = getEntityManager();

            em.getTransaction().begin();
            em.persist(RoomEntity);
            em.flush();
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return RoomEntity;
    }

}
