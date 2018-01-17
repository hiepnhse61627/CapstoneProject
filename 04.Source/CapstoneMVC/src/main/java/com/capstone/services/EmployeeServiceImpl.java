package com.capstone.services;

import com.capstone.entities.EmployeeEntity;
import com.capstone.jpa.exJpa.ExEmployeeEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;

public class EmployeeServiceImpl implements IEmployeeService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExEmployeeEntityJpaController employeeEntityJpaController = new ExEmployeeEntityJpaController(emf);

    @Override
    public void createEmployeeList(List<EmployeeEntity> EmployeeEntityList) {
        employeeEntityJpaController.createEmployeeList(EmployeeEntityList);
    }

    @Override
    public EmployeeEntity findEmployeeById(int id) {
        return employeeEntityJpaController.findEmployeeEntity(id);
    }

    @Override
    public EmployeeEntity findEmployeeByCode(String code) {
        return employeeEntityJpaController.findEmployeeByCode(code);
    }

    @Override
    public List<EmployeeEntity> findEmployeesByFullName(String searchValue) {
        return employeeEntityJpaController.findEmployeesByFullName(searchValue);
    }

    public List<EmployeeEntity> findAllEmployees() {
        return employeeEntityJpaController.findEmployeeEntityEntities();
    }

    @Override
    public void saveEmployee(EmployeeEntity emp) throws Exception {
        employeeEntityJpaController.saveEmployee(emp);
    }


    @Override
    public EmployeeEntity createEmployee(EmployeeEntity employeeEntity) {
        return employeeEntityJpaController.createEmployee(employeeEntity);
    }

    @Override
    public void updateEmployee(EmployeeEntity entity) {
        try {
            employeeEntityJpaController.edit(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCurrentLine() {
        return employeeEntityJpaController.getCurrentLine();
    }

    @Override
    public int getTotalLine() {
        return employeeEntityJpaController.getTotalLine();
    }
}
