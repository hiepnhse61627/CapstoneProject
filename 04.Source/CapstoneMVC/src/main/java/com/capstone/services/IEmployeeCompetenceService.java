package com.capstone.services;

import com.capstone.entities.EmpCompetenceEntity;
import com.capstone.entities.EmployeeEntity;
import com.capstone.entities.SubjectEntity;

import java.util.List;

public interface IEmployeeCompetenceService {
    int getCurrentLine();
    int getTotalLine();
    void createEmployeeCompetenceList(List<EmpCompetenceEntity> EmployeeCompetenceEntityList);
    EmpCompetenceEntity findEmployeeCompetenceById(int id);
    List<EmpCompetenceEntity> findEmployeeCompetencesByEmployee(Integer empId);
    List<EmpCompetenceEntity> findEmployeeCompetencesBySubject(SubjectEntity sub);
    List<EmpCompetenceEntity> findAllEmployeeCompetences();
    void saveEmployeeCompetence(EmpCompetenceEntity emp) throws Exception;
    EmpCompetenceEntity createEmployeeCompetence(EmpCompetenceEntity EmployeeCompetenceEntity);
    void updateEmployeeCompetence(EmpCompetenceEntity entity);
}
