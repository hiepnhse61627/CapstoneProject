package com.capstone.jpa.exJpa;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.PrequisiteEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.entities.SubjectMarkComponentEntity;
import com.capstone.jpa.SubjectEntityJpaController;

import javax.persistence.*;
import javax.security.auth.Subject;
import java.util.ArrayList;
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

    public List<SubjectEntity> findAllSubjects() {
        EntityManager em = getEntityManager();
        TypedQuery<SubjectEntity> query = em.createQuery("SELECT a FROM SubjectEntity a", SubjectEntity.class);
        return query.getResultList();
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

                en.setSubjectMarkComponentEntity(entity);
                entity.setSubjectEntity(en);

                manager.persist(en);
                manager.persist(entity);
            } else {
                System.out.println(en.getId() + " has exist!");
            }

            ++this.currentLine;
        }

        manager.getTransaction().commit();
    }

    private List<SubjectEntity> prequisiteList;

    public List<SubjectEntity> getAllPrequisiteSubjects(String subId) {
        prequisiteList = new ArrayList<>();
        EntityManager manager = getEntityManager();
        SubjectEntity currSub = manager.find(SubjectEntity.class, subId);
//        getPrequisite(currSub, currSub.getId());

        List<PrequisiteEntity> preList = currSub.getPrequisiteEntityList();
        for (PrequisiteEntity entity : preList) {
            prequisiteList.add(entity.getPrequisiteSubjectEntity());
        }

        return prequisiteList;
    }

    public List<SubjectEntity> getAllPrequisite() {
        prequisiteList = new ArrayList<>();
        for (SubjectEntity currSub : this.findSubjectEntityEntities()) {
            getPrequisite(currSub, currSub.getId());
        }
        return prequisiteList;
    }

    private void getPrequisite(SubjectEntity curr, String subId) {
        List<PrequisiteEntity> pre = curr.getPrequisiteEntityList();
        if (!pre.isEmpty()) {
            for (PrequisiteEntity s : pre) {
                getPrequisite(s.getPrequisiteSubjectEntity(), subId);
            }
        }

        if (!curr.getId().equals(subId) && !prequisiteList.stream().anyMatch(a -> a.getId().equals(curr.getId()))) {
            prequisiteList.add(curr);
        }
    }
}
