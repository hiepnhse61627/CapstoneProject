package com.capstone.services;

import com.capstone.entities.EmployeeEntity;
import com.capstone.entities.EmployeeEntity;

import java.util.List;

public interface IEmployeeService {
    int getCurrentLine();
    int getTotalLine();
    void createEmployeeList(List<EmployeeEntity> employeeEntityList);
    EmployeeEntity findEmployeeById(int id);
    EmployeeEntity findEmployeeByCode(String code);
    EmployeeEntity findEmployeeByShortName(String name);
    EmployeeEntity findEmployeeByEmail(String mail);
    List<EmployeeEntity> findEmployeesByFullName(String searchValue);
    List<EmployeeEntity> findAllEmployees();
    void saveEmployee(EmployeeEntity emp) throws Exception;
    EmployeeEntity createEmployee(EmployeeEntity employeeEntity);
    void updateEmployee(EmployeeEntity entity);
}
