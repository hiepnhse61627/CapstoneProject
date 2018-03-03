package com.capstone.services;

import com.capstone.entities.DepartmentEntity;
import com.capstone.entities.SubjectDepartmentEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.jpa.exJpa.ExSubjectDepartmentEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class SubjectDepartmentServiceImpl implements ISubjectDepartmentService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExSubjectDepartmentEntityJpaController SubjectDepartmentEntityJpaController = new ExSubjectDepartmentEntityJpaController(emf);

    @Override
    public void createSubjectDepartmentList(List<SubjectDepartmentEntity> SubjectDepartmentEntityList) {
        SubjectDepartmentEntityJpaController.createSubjectDepartmentList(SubjectDepartmentEntityList);
    }

    @Override
    public SubjectDepartmentEntity findSubjectDepartmentById(int id) {
        return SubjectDepartmentEntityJpaController.findSubjectDepartmentEntity(id);
    }

    @Override
    public List<SubjectDepartmentEntity> findSubjectDepartmentsByDepartment(DepartmentEntity departmentEntity) {
        return SubjectDepartmentEntityJpaController.findSubjectDepartmentsByDepartment(departmentEntity);
    }

    @Override
    public List<SubjectDepartmentEntity> findSubjectDepartmentsBySubject(SubjectEntity subjectEntity) {
        return SubjectDepartmentEntityJpaController.findSubjectDepartmentsBySubject(subjectEntity);
    }

    @Override
    public List<SubjectDepartmentEntity> findSubjectDepartmentsBySubjectAndDepartment(SubjectEntity subjectEntity, DepartmentEntity departmentEntity) {
        return SubjectDepartmentEntityJpaController.findSubjectDepartmentsBySubjectAndDepartment(subjectEntity, departmentEntity);
    }


    public List<SubjectDepartmentEntity> findAllSubjectDepartments() {
        return SubjectDepartmentEntityJpaController.findSubjectDepartmentEntityEntities();
    }

    @Override
    public void saveSubjectDepartment(SubjectDepartmentEntity emp) throws Exception {
        SubjectDepartmentEntityJpaController.saveSubjectDepartment(emp);
    }


    @Override
    public SubjectDepartmentEntity createSubjectDepartment(SubjectDepartmentEntity SubjectDepartmentEntity) {
        return SubjectDepartmentEntityJpaController.createSubjectDepartment(SubjectDepartmentEntity);
    }

    @Override
    public void updateSubjectDepartment(SubjectDepartmentEntity entity) {
        try {
            SubjectDepartmentEntityJpaController.edit(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCurrentLine() {
        return SubjectDepartmentEntityJpaController.getCurrentLine();
    }

    @Override
    public int getTotalLine() {
        return SubjectDepartmentEntityJpaController.getTotalLine();
    }
}
