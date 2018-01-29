package com.capstone.jpa.exJpa;

import com.capstone.entities.DaySlotEntity;
import com.capstone.entities.RoomEntity;
import com.capstone.entities.ScheduleEntity;
import com.capstone.entities.SlotEntity;
import com.capstone.jpa.ScheduleEntityJpaController;
import com.capstone.models.Logger;

import javax.persistence.*;
import java.util.List;

public class ExScheduleEntityJpaController extends ScheduleEntityJpaController {

    public ExScheduleEntityJpaController(EntityManagerFactory emf) {
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

    public void saveSchedule(ScheduleEntity Schedule) {
        try {
            EntityManager manager = getEntityManager();
            manager.getTransaction().begin();
            manager.merge(Schedule);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }
    }

    public void createScheduleList(List<ScheduleEntity> Schedules) {
        totalLine = Schedules.size();
        currentLine = 0;

        EntityManager em = null;
        try {
            em = getEntityManager();
            for (ScheduleEntity Schedule : Schedules) {
                try {
                    em.getTransaction().begin();

                    // Create Schedule
                    TypedQuery<ScheduleEntity> querySchedule = em.createQuery(
                            "SELECT c FROM ScheduleEntity c " +
                                    "WHERE (c.dateId = :date)" +
                                    "AND (c.roomId= :slotId)", ScheduleEntity.class);
                    querySchedule.setParameter("date", Schedule.getDateId());
                    querySchedule.setParameter("slotId", Schedule.getRoomId());

                    List<ScheduleEntity> std = querySchedule.getResultList();
                    if (std.isEmpty()) {
                        em.persist(Schedule);
                        em.flush();
                    }
                    em.getTransaction().commit();
                } catch (Exception e) {
                    System.out.println("Schedule " + Schedule.getId() + "caused " + e.getMessage());
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

    public ScheduleEntity findScheduleByDateSlotAndRoom(DaySlotEntity date, RoomEntity room) {
        EntityManager em = getEntityManager();
        ScheduleEntity ScheduleEntity = new ScheduleEntity();
        try {
            String sqlString = "SELECT c FROM ScheduleEntity c " +
                    "WHERE (c.dateId = :date)" +
                    "AND (c.roomId= :room)";
            Query query = em.createQuery(sqlString);
            query.setParameter("date", date);
            query.setParameter("room", room);

            ScheduleEntity = (ScheduleEntity) query.getSingleResult();

            return ScheduleEntity;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


    public ScheduleEntity createSchedule(ScheduleEntity ScheduleEntity) {
        EntityManager em = null;

        try {
            em = getEntityManager();

            em.getTransaction().begin();
            em.persist(ScheduleEntity);
            em.flush();
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return ScheduleEntity;
    }

}
