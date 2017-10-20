package com.capstone.jpa.exJpa;

import com.capstone.entities.SubjectEntity;
import com.capstone.entities.SubjectMarkComponentEntity;
import com.capstone.jpa.SubjectEntityJpaController;
import com.capstone.models.ReplacementSubject;

import javax.persistence.*;
import java.util.List;

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
//        prequisiteList = new ArrayList<>();
//        EntityManager manager = getEntityManager();
//        SubjectEntity currSub = manager.find(SubjectEntity.class, subId);
////        getPrequisite(currSub, currSub.getId());
//
//        List<PrequisiteEntity> preList = currSub.getPrequisiteEntityList();
//        for (PrequisiteEntity entity : preList) {
//            prequisiteList.add(entity.getSubId());
//        }
//
//        return prequisiteList;
        return null;
    }

    public List<SubjectEntity> getAllPrequisite() {
//        prequisiteList = new ArrayList<>();
//        for (SubjectEntity currSub : this.findSubjectEntityEntities()) {
//            getPrequisite(currSub, currSub.getId());
//        }
//        return prequisiteList;
        return null;
    }

    private void getPrequisite(SubjectEntity curr, String subId) {
//        List<PrequisiteEntity> pre = curr.getPrequisiteEntityList();
//        if (!pre.isEmpty()) {
//            for (PrequisiteEntity s : pre) {
//                getPrequisite(s.getSubId(), subId);
//            }
//        }
//
//        if (!curr.getId().equals(subId) && !prequisiteList.stream().anyMatch(a -> a.getId().equals(curr.getId()))) {
//            prequisiteList.add(curr);
//        }
    }

    public int countStudentCredits(int studentId) {
        EntityManager em = getEntityManager();
        Object totalCredits = 0;
        try {
            String sqlString = "select SUM(s.Credits) from Marks m, Subject s \n" +
                    "where m.SubjectId = s.Id and m.StudentId = ? and m.Status = 'Passed' and m.SubjectId is not null \n" +
                    "and m.SubjectId in\n" +
                    "(select SubId from Curriculum_Mapping)";
            Query query = em.createNativeQuery(sqlString);
            query.setParameter(1, studentId);

            totalCredits = query.getSingleResult();
            return totalCredits != null ? Integer.parseInt(totalCredits.toString()) : -1;
        } catch (NoResultException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public void insertReplacementList(List<ReplacementSubject> list) {
        EntityManager manager = getEntityManager();
        manager.getTransaction().begin();
        for (ReplacementSubject replacer : list) {
            if (replacer.getReplaceCode() != null && !replacer.getReplaceCode().isEmpty()) {
                SubjectEntity sub = manager.find(SubjectEntity.class, replacer.getSubCode());
                if (sub != null) {
                    String[] rep = replacer.getReplaceCode().split("OR");
                    for (String r : rep ) {
                        SubjectEntity replace = manager.find(SubjectEntity.class, r.trim());
                        if (!sub.getReplacementSubjectList().contains(replace)) {
                            sub.getReplacementSubjectList().add(replace);
                        }
                    }
                    manager.merge(sub);
                    manager.flush();
                }
            }
        }
        manager.getTransaction().commit();
    }
}
