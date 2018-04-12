package com.capstone.jpa.exJpa;

import com.capstone.entities.DepartmentEntity;
import com.capstone.entities.PrequisiteEntity;
import com.capstone.entities.ScheduleEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.jpa.SubjectEntityJpaController;
import com.capstone.models.Logger;
import com.capstone.models.ReplacementSubject;
import com.capstone.models.SubjectModel;
import com.capstone.services.ISubjectService;
import com.capstone.services.SubjectServiceImpl;

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

    public SubjectModel createSubject(SubjectModel subject) {
        EntityManager manager = getEntityManager();
        ISubjectService subjectService = new SubjectServiceImpl();

        try {

            manager.getTransaction().begin();
            //create SubjectEntity match SubjectID
            SubjectEntity uSubject = manager.find(SubjectEntity.class, subject.getSubjectID());
            if (uSubject == null) {
                uSubject = new SubjectEntity();
                uSubject.setId(subject.getSubjectID());
                uSubject.setAbbreviation(subject.getSubjectID().substring(0, 3));
                uSubject.setName(subject.getSubjectName());
                uSubject.setIsSpecialized(false);

                PrequisiteEntity uPrerequisite = new PrequisiteEntity();
                uPrerequisite.setSubjectId(subject.getSubjectID());
                if (subject.getEffectionSemester().equals("0")) {
                    uPrerequisite.setEffectionSemester(null);
                    uPrerequisite.setPrequisiteSubs(subject.getPrerequisiteSubject());
                    uPrerequisite.setFailMark(subject.getFailMark());
                } else {
                    uPrerequisite.setEffectionSemester(subject.getEffectionSemester());
                    uPrerequisite.setNewPrequisiteSubs(subject.getPrerequisiteSubject());
                    uPrerequisite.setNewFailMark(subject.getFailMark());
                }

                //check if prerequisite is available or not
                if (uPrerequisite.getPrequisiteSubs() != null && !uPrerequisite.getPrequisiteSubs().isEmpty()) {
                    String[] checkers = uPrerequisite.getPrequisiteSubs().split(",");
                    for (String subjectCheck : checkers) {
                        if (manager.find(SubjectEntity.class, subjectCheck) == null) {
                            subject.setErrorMessage("Môn " + subjectCheck + " không tồn tại");
                            subject.setResult(false);
                            return subject;
                        }
                    }
                }
                uSubject.setPrequisiteEntity(uPrerequisite);

//                uSubject.getPrequisiteEntity().setPrerequisiteEffectStart(subject.getPrerequisiteEffectStart());
//                uSubject.getPrequisiteEntity().setPrerequisiteEffectEnd(subject.getPrerequisiteEffectEnd());
//                uSubject.setCredits(subject.getCredits());

                manager.persist(uSubject);
                manager.flush();
            } else {
                subject.setResult(false);
                subject.setErrorMessage("Môn " + subject.getSubjectID() + "đã tồn tại!");
                return subject;
            }


            //create Prerequisite match SubjectID
//            PrequisiteEntity uPrerequisite = manager.find(PrequisiteEntity.class, subject.getSubjectID());
//            if (!(uPrerequisite == null)) {
//                return false;
//            } else {
//                uPrerequisite = new PrequisiteEntity();
//                uPrerequisite.setSubjectId(subject.getSubjectID());
//                uPrerequisite.setPrequisiteSubs(subject.getPrerequisiteSubject());
//                uPrerequisite.setFailMark(4);
//                //check if prerequisite is available or not
//                String[] checkers = uPrerequisite.getPrequisiteSubs().split(",");
//                for (String subjectCheck : checkers) {
//                    if (manager.find(SubjectEntity.class, subjectCheck) == null) {
//                        return false;
//                    }
//                }
//                manager.persist(uPrerequisite);
//                manager.flush();
//            }

            //create Replacement
            try {
                String[] newRpSubjects = subject.getReplacementSubject().split(",");
                for (String replacer : newRpSubjects) {

                    if (replacer != null && !replacer.isEmpty()) {
                        if (manager.find(SubjectEntity.class, replacer) == null) {
                            subject.setErrorMessage("Môn " + replacer + "không tồn tại!");
                            subject.setResult(false);
                            return subject;
                        }
                        SubjectEntity sub = manager.find(SubjectEntity.class, subject.getSubjectID());
                        if (sub != null) {
                            String[] rep = replacer.split(",");
                            for (String r : rep) {
                                SubjectEntity replace = manager.find(SubjectEntity.class, r.trim());
                                sub.getSubjectEntityList().clear();
                                manager.merge(sub);
                                manager.flush();
                                if (!sub.getSubjectEntityList().contains(replace)) {
                                    sub.getSubjectEntityList().add(replace);
                                }
                            }
                            manager.persist(sub);
                            manager.flush();
                        }
                    }
                }
            } catch (Exception e) {
                Logger.writeLog(e);
                subject.setResult(false);
                subject.setErrorMessage(e.getMessage());
                return subject;
            }


        } catch (Exception e) {
            Logger.writeLog(e);
            subject.setResult(false);
            subject.setErrorMessage(e.getMessage());
            return subject;
        }
        manager.getTransaction().commit();
        subject.setResult(true);
        return subject;
    }


    public SubjectModel updateSubject(SubjectModel subject) {
        EntityManager manager = getEntityManager();
        ISubjectService subjectService = new SubjectServiceImpl();
        manager.getTransaction().begin();
        try {
            //update SubjectEntity match SubjectID
            SubjectEntity uSubject = manager.find(SubjectEntity.class, subject.getSubjectID());
            uSubject.setName(subject.getSubjectName());
//            uSubject.getPrequisiteEntity().setPrerequisiteEffectStart(subject.getPrerequisiteEffectStart());
//            uSubject.getPrequisiteEntity().setPrerequisiteEffectEnd(subject.getPrerequisiteEffectEnd());
            uSubject.getPrequisiteEntity().setPrequisiteSubs(null);
//            uSubject.setCredits(subject.getCredits());
//            uSubject.setSubjectEntityList(new ArrayList<SubjectEntity>());
//            uSubject.setSubjectEntityList1(new ArrayList<SubjectEntity>());
            if (!subject.getEffectionSemester().equals("0")) {
                uSubject.getPrequisiteEntity().setFailMark(uSubject.getPrequisiteEntity().getFailMark());
                uSubject.getPrequisiteEntity().setPrequisiteSubs(uSubject.getPrequisiteEntity().getPrequisiteSubs());
                uSubject.getPrequisiteEntity().setEffectionSemester(subject.getEffectionSemester());
                uSubject.getPrequisiteEntity().setNewFailMark(subject.getFailMark());
                uSubject.getPrequisiteEntity().setNewPrequisiteSubs(subject.getPrerequisiteSubject());
            } else {
                uSubject.getPrequisiteEntity().setNewFailMark(null);
                uSubject.getPrequisiteEntity().setNewPrequisiteSubs(null);
                uSubject.getPrequisiteEntity().setFailMark(subject.getFailMark());
                uSubject.getPrequisiteEntity().setEffectionSemester(null);
                uSubject.getPrequisiteEntity().setPrequisiteSubs(subject.getPrerequisiteSubject());
            }


            //check if prerequisite is available or not
            if (subject.getPrerequisiteSubject() != null && !subject.getPrerequisiteSubject().isEmpty()) {
                String[] checkers = subject.getPrerequisiteSubject().split(",");
                for (String subjectCheck : checkers) {
                    if (manager.find(SubjectEntity.class, subjectCheck) == null) {
                        subject.setResult(false);
                        subject.setErrorMessage("Môn " + subjectCheck + " không tồn tại");
                        return subject;
                    }
                }
            }

            if (subject.getReplacementSubject() != null && !subject.getReplacementSubject().isEmpty()) {
                String[] newRpSubjects = subject.getReplacementSubject().split(",");
                for (String replacer : newRpSubjects) {
                    if (replacer != null && !replacer.isEmpty()) {
                        if (manager.find(SubjectEntity.class, replacer) == null) {
                            subject.setResult(false);
                            subject.setErrorMessage("Môn " + replacer + " không tồn tại!");
                            return subject;
                        }
                        SubjectEntity sub = manager.find(SubjectEntity.class, subject.getSubjectID());
                        if (sub != null) {
                            String[] rep = replacer.split(",");
                            for (String r : rep) {
                                SubjectEntity replace = manager.find(SubjectEntity.class, r.trim());
                                if (!sub.getSubjectEntityList().contains(replace)) {
                                    sub.getSubjectEntityList().add(replace);
                                }
                            }
                        }
                    }
                }
            }
            manager.merge(uSubject);
            manager.flush();
        } catch (Exception e) {
            Logger.writeLog(e);
            subject.setResult(false);
            subject.setErrorMessage(e.getMessage());
            return subject;
        }
        manager.getTransaction().commit();
        subject.setResult(true);
        return subject;
    }

    public void insertSubjectList(List<SubjectEntity> list) {
        EntityManager manager = getEntityManager();

        this.totalLine = list.size();
        this.currentLine = 0;

        manager.getTransaction().begin();

        for (SubjectEntity en : list) {
            if (en.getId() == null) {
                manager.persist(en);
            } else {
                manager.merge(en);
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
            query.setParameter("sub", id.trim());
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

    public void cleanReplacers() {
        try {
            EntityManager em = getEntityManager();
            em.getTransaction().begin();
            Query query = em.createNativeQuery("DELETE FROM Replacement_Subject");
            query.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Object[]> getAllReplaceSubjects() {
        try {
            EntityManager em = getEntityManager();
            Query query = em.createNativeQuery("SELECT SubjectId,ReplacementId FROM Replacement_Subject");
            return query.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<Object[]> getAllSubjects() {
        try {
            EntityManager em = getEntityManager();
            Query query = em.createNativeQuery("SELECT Id, Name FROM Subject");
            return query.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean bulkUpdateSubjects(List<SubjectEntity> subjectList) {
        EntityManager em = null;
        int bulkSize = 1000;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            for (int i = 0; i < subjectList.size(); i++) {
                if (i > 0 && i % bulkSize == 0) {
                    em.flush();
                    em.clear();
                    em.getTransaction().commit();
                    em.getTransaction().begin();
                }
                SubjectEntity updateSubject = subjectList.get(i);
//                SubjectEntity subject = em.find(SubjectEntity.class, updateSubject.getId());
//                subject.setVnName(updateSubject.getVnName());
                em.merge(updateSubject);
                System.out.println("Update - " + (i + 1));
            }

            //đẩy xuống những phần còn lại
            em.flush();

        } catch (Exception e) {
            em.getTransaction().rollback();
            Logger.writeLog(e);
            e.printStackTrace();
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        em.getTransaction().commit();
        return true;
    }


    public List<SubjectEntity> findSubjectByDepartment(DepartmentEntity dept) {
        EntityManager em = getEntityManager();
        try {
            String sqlString = "SELECT c FROM SubjectEntity c " +
                    "WHERE (c.departmentId = :dept)";
            Query query = em.createQuery(sqlString);
            query.setParameter("dept", dept);

            List<SubjectEntity> std = query.getResultList();

            return std;
        } catch (NoResultException nrEx) {
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
