package com.capstone.services;

import com.capstone.entities.EmpCompetenceEntity;
import com.capstone.entities.EmployeeEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.jpa.exJpa.ExEmployeeCompetenceEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class EmployeeCompetenceServiceImpl implements IEmployeeCompetenceService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExEmployeeCompetenceEntityJpaController EmployeeCompetenceEntityJpaController = new ExEmployeeCompetenceEntityJpaController(emf);

    @Override
    public void createEmployeeCompetenceList(List<EmpCompetenceEntity> EmployeeCompetenceEntityList) {
        EmployeeCompetenceEntityJpaController.createEmployeeCompetenceList(EmployeeCompetenceEntityList);
    }

    @Override
    public EmpCompetenceEntity findEmployeeCompetenceById(int id) {
        return EmployeeCompetenceEntityJpaController.findEmpCompetenceEntity(id);
    }

    @Override
    public List<EmpCompetenceEntity> findEmployeeCompetencesByEmployee(Integer empId) {
        return EmployeeCompetenceEntityJpaController.findEmployeeCompetencesByEmployee(empId);
    }

    @Override
    public List<EmpCompetenceEntity> findEmployeeCompetencesBySubject(SubjectEntity sub) {
        return EmployeeCompetenceEntityJpaController.findEmployeeCompetencesBySubject(sub);
    }

    public List<EmpCompetenceEntity> findAllEmployeeCompetences() {
        return EmployeeCompetenceEntityJpaController.findEmpCompetenceEntityEntities();
    }

    @Override
    public void saveEmployeeCompetence(EmpCompetenceEntity emp) throws Exception {
        EmployeeCompetenceEntityJpaController.saveEmployeeCompetence(emp);
    }


    @Override
    public EmpCompetenceEntity createEmployeeCompetence(EmpCompetenceEntity EmployeeCompetenceEntity) {
        return EmployeeCompetenceEntityJpaController.createEmployeeCompetence(EmployeeCompetenceEntity);
    }

    @Override
    public void updateEmployeeCompetence(EmpCompetenceEntity entity) {
        try {
            EmployeeCompetenceEntityJpaController.edit(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCurrentLine() {
        return EmployeeCompetenceEntityJpaController.getCurrentLine();
    }

    @Override
    public int getTotalLine() {
        return EmployeeCompetenceEntityJpaController.getTotalLine();
    }
}
