package com.capstone.jpa.exJpa;

import com.capstone.entities.SubjectEntity;
import com.capstone.entities.SubjectMarkComponentEntity;
import com.capstone.jpa.SubjectEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExSubjectEntityJpaController extends SubjectEntityJpaController {

    private int currentLine;
    private int totalLine;

    public ExSubjectEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public int getCurrentLine() {
        return currentLine;
    }

    public int getTotalLine() {
        return totalLine;
    }

    public void insertSubjectList(List<SubjectEntity> list) {
        EntityManager manager = getEntityManager();
        TypedQuery<SubjectEntity> query = manager.createQuery("SELECT c FROM SubjectEntity c", SubjectEntity.class);
        List<SubjectEntity> cur = query.getResultList();

        this.totalLine = list.size();
        this.currentLine = 0;

        manager.getTransaction().begin();

        for (SubjectEntity en : list) {
            if (!cur.stream().anyMatch(c -> c.getId().equals(en.getId()))) {

                SubjectMarkComponentEntity entity = new SubjectMarkComponentEntity();
                entity.setSubjectId(en.getId());

                en.setSubjectMarkComponent(entity);
                entity.setSubject(en);

                manager.persist(en);
                manager.persist(entity);
            } else {
                System.out.println(en.getId() + " has exist!");
            }

            ++this.currentLine;
        }

        manager.getTransaction().commit();
    }
}
