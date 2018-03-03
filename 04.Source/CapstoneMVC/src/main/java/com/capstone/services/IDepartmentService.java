package com.capstone.services;

import com.capstone.entities.DepartmentEntity;

import java.util.List;

public interface IDepartmentService {
    int getCurrentLine();
    int getTotalLine();
    void createDepartmentList(List<DepartmentEntity> DepartmentEntityList);
    DepartmentEntity createDepartment(DepartmentEntity DepartmentEntity);
    DepartmentEntity findDepartmentById(int id);
    List<DepartmentEntity> findDepartmentsByName(String searchValue);
    List<DepartmentEntity> findDepartmentsShortName(String searchValue);
    List<DepartmentEntity> findAllDepartments();
    void saveDepartment(DepartmentEntity emp) throws Exception;
    void updateDepartment(DepartmentEntity entity);
}
