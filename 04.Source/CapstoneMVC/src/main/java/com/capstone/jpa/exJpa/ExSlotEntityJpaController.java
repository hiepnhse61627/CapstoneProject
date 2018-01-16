package com.capstone.jpa.exJpa;

import com.capstone.entities.SlotEntity;
import com.capstone.jpa.SlotEntityJpaController;
import com.capstone.models.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

public class ExSlotEntityJpaController extends SlotEntityJpaController {

    public ExSlotEntityJpaController(EntityManagerFactory emf) {
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

    public void saveSlot(SlotEntity Slot) {
        try {
            EntityManager manager = getEntityManager();
            manager.getTransaction().begin();
            manager.merge(Slot);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e) {
            Logger.writeLog(e);
            e.printStackTrace();
        }
    }

    public void createSlotList(List<SlotEntity> slots) {
        totalLine = slots.size();
        currentLine = 0;

        EntityManager em = null;
        try {
            em = getEntityManager();
            for (SlotEntity slot : slots) {
                try {
                    em.getTransaction().begin();

                    // Create Slot
                    TypedQuery<SlotEntity> querySlot = em.createQuery(
                            "SELECT c FROM SlotEntity c WHERE c.slotName = :name", SlotEntity.class);
                    querySlot.setParameter("name", slot.getSlotName());

                    List<SlotEntity> std = querySlot.getResultList();
                    if (std.isEmpty()) {
                        em.persist(slot);
                        em.flush();
                    }
                    em.getTransaction().commit();
                } catch (Exception e) {
                    System.out.println("Slot " + slot.getSlotName() + "caused " + e.getMessage());
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


    public List<SlotEntity> findSlotsByName(String searchValue) {
        EntityManager em = getEntityManager();
        List<SlotEntity> result = null;

        try {
            String queryStr = "SELECT s FROM SlotEntity s" +
                    " WHERE s.slotName LIKE :name";
            TypedQuery<SlotEntity> query = em.createQuery(queryStr, SlotEntity.class);
            query.setParameter("name", "%" + searchValue + "%");

            result = query.getResultList();

        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    public SlotEntity createSlot(SlotEntity SlotEntity) {
        EntityManager em = null;

        try {
            em = getEntityManager();

            em.getTransaction().begin();
            em.persist(SlotEntity);
            em.flush();
            em.getTransaction().commit();
        } finally {
            em.close();
        }

        return SlotEntity;
    }

}
