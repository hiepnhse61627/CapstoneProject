package com.capstone.services;

import com.capstone.entities.DepartmentEntity;
import com.capstone.entities.SubjectDepartmentEntity;
import com.capstone.entities.SubjectEntity;

import java.util.List;

public interface ISubjectDepartmentService {
    int getCurrentLine();
    int getTotalLine();
    void createSubjectDepartmentList(List<SubjectDepartmentEntity> SubjectDepartmentEntityList);
    SubjectDepartmentEntity createSubjectDepartment(SubjectDepartmentEntity SubjectDepartmentEntity);
    SubjectDepartmentEntity findSubjectDepartmentById(int id);
    List<SubjectDepartmentEntity> findSubjectDepartmentsByDepartment(DepartmentEntity departmentEntity);
    List<SubjectDepartmentEntity> findSubjectDepartmentsBySubject(SubjectEntity subjectEntity);
    List<SubjectDepartmentEntity> findSubjectDepartmentsBySubjectAndDepartment(SubjectEntity subjectEntity, DepartmentEntity departmentEntity);
    List<SubjectDepartmentEntity> findAllSubjectDepartments();
    void saveSubjectDepartment(SubjectDepartmentEntity emp) throws Exception;
    void updateSubjectDepartment(SubjectDepartmentEntity entity);
}
