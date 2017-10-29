package com.capstone.jpa.exJpa;

import com.capstone.entities.PrequisiteEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.jpa.SubjectEntityJpaController;
import com.capstone.models.Logger;
import com.capstone.models.ReplacementSubject;
import com.capstone.models.SubjectModel;

import javax.persistence.*;
import javax.security.auth.Subject;
import java.util.ArrayList;
import java.util.Arrays;
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

    public boolean updateSubject(SubjectModel subject) {
        EntityManager manager = getEntityManager();
        manager.getTransaction().begin();
        try {
            //update SubjectEntity match SubjectID
//            SubjectEntity uSubject = manager.find(SubjectEntity.class, subject.getSubjectID());
//            uSubject.setName(subject.getSubjectName());
//            uSubject.setPrerequisiteEffectStart(subject.getPrerequisiteEffectStart());
//            uSubject.setPrerequisiteEffectEnd(subject.getPrerequisiteEffectEnd());
//            uSubject.setCredits(subject.getCredits());

//            manager.merge(uSubject);
//            manager.flush();

            //update Prerequisite match SubjectID
            PrequisiteEntity uPrerequisite = manager.find(PrequisiteEntity.class, subject.getSubjectID());
            uPrerequisite.setPrequisiteSubs(subject.getPrerequisiteSubject());
            //check if prerequisite is available or not
            String[] checkers = uPrerequisite.getPrequisiteSubs().split(",");
            for (String subjectCheck : checkers) {
                if (manager.find(SubjectEntity.class, subjectCheck) == null) {
                    return false;
                }
            }



            manager.merge(uPrerequisite);
            manager.flush();

            //update Replacement if there is any change

        } catch (Exception e) {

            Logger.writeLog(e);
            return false;
        }
        manager.getTransaction().commit();
        return true;
    }

    public void insertSubjectList(List<SubjectEntity> list) {
        EntityManager manager = getEntityManager();

        this.totalLine = list.size();
        this.currentLine = 0;

        manager.getTransaction().begin();

        for (SubjectEntity en : list) {
            if (manager.find(SubjectEntity.class, en.getId()) == null) {
                manager.persist(en);
            }
            ++this.currentLine;
        }

        manager.getTransaction().commit();
    }

    private List<List<SubjectEntity>> prequisiteList;

    public List<List<SubjectEntity>> getAllPrequisiteSubjects(String subId) {
        prequisiteList = new ArrayList<>();
        EntityManager manager = getEntityManager();
        SubjectEntity currSub = manager.find(SubjectEntity.class, subId);
        PrequisiteEntity prequisite = currSub.getPrequisiteEntity();
        if (prequisite == null) System.out.println(subId);
        if (prequisite.getPrequisiteSubs() != null) {
            String[] prequisitesRow = prequisite.getPrequisiteSubs().split("OR");
            for (String row : prequisitesRow) {
                List<SubjectEntity> list = new ArrayList<>();
                String[] processedRows = row.replaceAll("\\(", "").replaceAll("\\)", "").split(",");
                for (String sub : processedRows) {
                    SubjectEntity pre = manager.find(SubjectEntity.class, sub.trim());
                    if (pre != null) {
                        list.add(pre);
                    }
                }
                if (!list.isEmpty()) {
                    prequisiteList.add(list);
                }
            }
        }

        return prequisiteList;
    }

    @Override
    public SubjectEntity findSubjectEntity(String id) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<SubjectEntity> query = em.createQuery("SELECT a FROM SubjectEntity a WHERE a.id = :sub", SubjectEntity.class);
            query.setParameter("sub", id);
            return query.getSingleResult();
        } catch (NoResultException e) {
//            try {
//                TypedQuery<SubjectEntity> query = em.createQuery("SELECT a FROM SubjectEntity a WHERE a.replacementId LIKE :replace", SubjectEntity.class);
//                query.setParameter("replace", "%" + id + "%");
//                return query.getSingleResult();
//            } catch (NoResultException ex) {
            System.out.println("Subject " + id + " not found!");
            return null;
//            } catch (NonUniqueResultException ex) {
//                System.out.println("Subject " + id + " more than one result!");
//                return null;
//            }
        } catch (NonUniqueResultException e) {
            System.out.println("Subject " + id + " more than one result!");
            return null;
        } finally {
            em.close();
        }
    }

    public List<List<SubjectEntity>> getAllPrequisite() {
        prequisiteList = new ArrayList<>();
        EntityManager manager = getEntityManager();
        for (SubjectEntity currSub : this.findSubjectEntityEntities()) {
            PrequisiteEntity prequisite = currSub.getPrequisiteEntity();
            if (prequisite.getPrequisiteSubs() != null) {
                String[] prequisitesRow = prequisite.getPrequisiteSubs().split("OR");
                for (String row : prequisitesRow) {
                    List<SubjectEntity> list = new ArrayList<>();
                    String[] processedRows = row.replaceAll("\\(", "").replaceAll("\\)", "").split(",");
                    for (String sub : processedRows) {
                        SubjectEntity pre = manager.find(SubjectEntity.class, sub.trim());
                        if (pre != null) {
                            list.add(pre);
                        }
                    }
                    if (!list.isEmpty()) {
                        prequisiteList.add(list);
                    }
                }
            }
        }

        return prequisiteList;
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
                    String[] rep = replacer.getReplaceCode().split(",");
                    for (String r : rep) {
                        SubjectEntity replace = manager.find(SubjectEntity.class, r.trim());
                        if (!sub.getSubjectEntityList().contains(replace)) {
                            sub.getSubjectEntityList().add(replace);
                        }
                    }
                    manager.merge(sub);
                    manager.flush();
                }
            }
        }
        manager.getTransaction().commit();
    }

    public List<SubjectEntity> getSubjectsByMarkStatus(String[] statuses) {
        try {
            EntityManager em = getEntityManager();
            TypedQuery<SubjectEntity> query = em.createQuery("SELECT distinct a FROM SubjectEntity a, MarksEntity b, SubjectMarkComponentEntity c WHERE " +
                    "b.subjectMarkComponentId.id = c.id AND " +
                    "c.subjectId.id = a.id AND " +
                    "b.status IN :list", SubjectEntity.class);
            query.setParameter("list", Arrays.asList(statuses));
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
