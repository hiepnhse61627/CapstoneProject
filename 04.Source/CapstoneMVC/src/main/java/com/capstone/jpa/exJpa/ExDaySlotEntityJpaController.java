package com.capstone.jpa.exJpa;

import com.capstone.entities.DaySlotEntity;
import com.capstone.entities.SlotEntity;
import com.capstone.jpa.DaySlotEntityJpaController;
import com.capstone.models.Logger;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class ExDaySlotEntityJpaController extends DaySlotEntityJpaController {

    public ExDaySlotEntityJpaController(EntityManagerFactory emf) {
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

    public void saveDaySlot(DaySlotEntity DaySlot) {
        try {
            EntityManager manager = getEntityManager();
            manager.getTransaction().begin();
            manager.merge(DaySlot);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }
    }

    public void createDaySlotList(List<DaySlotEntity> DaySlots) {
        totalLine = DaySlots.size();
        currentLine = 0;

        EntityManager em = null;
        try {
            em = getEntityManager();
            for (DaySlotEntity DaySlot : DaySlots) {
                try {
                    em.getTransaction().begin();

                    // Create DaySlot
                    TypedQuery<DaySlotEntity> queryDaySlot = em.createQuery(
                            "SELECT c FROM DaySlotEntity c " +
                                    "WHERE (c.date = :date)" +
                                    "AND (c.slotId= :slotId)", DaySlotEntity.class);
                    queryDaySlot.setParameter("date", DaySlot.getDate());
                    queryDaySlot.setParameter("slotId", DaySlot.getSlotId());

                    List<DaySlotEntity> std = queryDaySlot.getResultList();
                    if (std.isEmpty()) {
                        em.persist(DaySlot);
                        em.flush();
                    }
                    em.getTransaction().commit();
                } catch (Exception e) {
                    System.out.println("DaySlot " + DaySlot.getDate() + "caused " + e.getMessage());
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

    public DaySlotEntity findDaySlotByDateAndSlot(String date, SlotEntity slotId) {
        EntityManager em = getEntityManager();
        DaySlotEntity DaySlotEntity = new DaySlotEntity();
        try {
            String sqlString = "SELECT c FROM DaySlotEntity c " +
                    "WHERE (c.date = :date)" +
                    "AND (c.slotId= :slotId)";
            Query query = em.createQuery(sqlString);
            query.setParameter("date", date);
            query.setParameter("slotId", slotId);

            DaySlotEntity = (DaySlotEntity) query.getSingleResult();

            return DaySlotEntity;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


    public List<DaySlotEntity> findDaySlotByDate(String date) {
        EntityManager em = getEntityManager();
        List<DaySlotEntity> DaySlotEntity = new ArrayList<>();
        try {
            String sqlString = "SELECT c FROM DaySlotEntity c " +
                    "WHERE (c.date = :date)";
            Query query = em.createQuery(sqlString);
            query.setParameter("date", date);

            DaySlotEntity = query.getResultList();

            return DaySlotEntity;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


    public DaySlotEntity createDaySlot(DaySlotEntity DaySlotEntity) {
        EntityManager em = null;

        try {
            em = getEntityManager();

            em.getTransaction().begin();
            em.persist(DaySlotEntity);
            em.flush();
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return DaySlotEntity;
    }

}
