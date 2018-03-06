package com.capstone.services;

import com.capstone.entities.DepartmentEntity;
import com.capstone.jpa.exJpa.ExDepartmentEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class DepartmentServiceImpl implements IDepartmentService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExDepartmentEntityJpaController DepartmentEntityJpaController = new ExDepartmentEntityJpaController(emf);

    @Override
    public void createDepartmentList(List<DepartmentEntity> DepartmentEntityList) {
        DepartmentEntityJpaController.createDepartmentList(DepartmentEntityList);
    }

    @Override
    public DepartmentEntity findDepartmentById(int id) {
        return DepartmentEntityJpaController.findDepartmentEntity(id);
    }
    
    @Override
    public List<DepartmentEntity> findDepartmentsByName(String searchValue) {
        return DepartmentEntityJpaController.findDepartmentsByName(searchValue);
    }

    @Override
    public List<DepartmentEntity> findDepartmentsShortName(String searchValue) {
        return DepartmentEntityJpaController.findDepartmentsByShortName(searchValue);
    }

    public List<DepartmentEntity> findAllDepartments() {
        return DepartmentEntityJpaController.findDepartmentEntityEntities();
    }

    @Override
    public void saveDepartment(DepartmentEntity emp) throws Exception {
        DepartmentEntityJpaController.saveDepartment(emp);
    }


    @Override
    public DepartmentEntity createDepartment(DepartmentEntity DepartmentEntity) {
        return DepartmentEntityJpaController.createDepartment(DepartmentEntity);
    }

    @Override
    public void updateDepartment(DepartmentEntity entity) {
        try {
            DepartmentEntityJpaController.edit(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCurrentLine() {
        return DepartmentEntityJpaController.getCurrentLine();
    }

    @Override
    public int getTotalLine() {
        return DepartmentEntityJpaController.getTotalLine();
    }
}
