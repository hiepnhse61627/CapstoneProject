package com.capstone.services;

import com.capstone.entities.SubjectEntity;
import com.capstone.jpa.exJpa.ExSubjectEntityJpaController;
import com.capstone.models.ReplacementSubject;
import com.capstone.models.SubjectModel;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class SubjectServiceImpl implements ISubjectService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExSubjectEntityJpaController controller = new ExSubjectEntityJpaController(emf);

    @Override
    public SubjectModel createSubject(SubjectModel subject) {
        return controller.createSubject(subject);
    }

    @Override
    public SubjectModel updateSubject(SubjectModel subject) {
        return controller.updateSubject(subject);
    }

    @Override
    public void insertSubjectList(List<SubjectEntity> list) {
        controller.insertSubjectList(list);
    }

    @Override
    public void insertReplacementList(List<ReplacementSubject> list) {
        controller.insertReplacementList(list);
    }

    @Override
    public List<SubjectEntity> getAllSubjects() {
        return controller.findAllSubjects();
    }

    @Override
    public List<List<SubjectEntity>> getAllPrequisiteSubjects(String subId) {
        return controller.getAllPrequisiteSubjects(subId);
    }

    @Override
    public List<List<SubjectEntity>> getAlllPrequisite() {
        return controller.getAllPrequisite();
    }

    @Override
    public SubjectEntity findSubjectById(String id) {
        return controller.findSubjectEntity(id);
    }

    @Override
    public int getCurrentLine() {
        return controller.getCurrentLine();
    }

    @Override
    public int getTotalLine() {
        return controller.getTotalLine();
    }

    @Override
    public int countStudentCredits(int studentId) {
        return controller.countStudentCredits(studentId);
    }

    @Override
    public void cleanReplacers() {
        controller.cleanReplacers();
    }

    @Override
    public List<SubjectEntity> getSubjectsByMarkStatus(String[] statuses) {
        return controller.getSubjectsByMarkStatus(statuses);
    }

    @Override
    public boolean bulkUpdateSubjects(List<SubjectEntity> subjectList) {
        return controller.bulkUpdateSubjects(subjectList);
    }
}
