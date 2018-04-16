package com.capstone.services;

import com.capstone.entities.DocumentStudentEntity;

import java.util.List;

public interface IDocumentStudentService {
    DocumentStudentEntity createDocumentStudent(DocumentStudentEntity entity);
    void editDocumentStudent(DocumentStudentEntity entity);
    DocumentStudentEntity getLastestDocumentStudentById(int studentId);
    List<DocumentStudentEntity> getAllLatestDocumentStudent();
    List<DocumentStudentEntity> getAllLatestDocumentStudentByProgramId(int programId);
    List<DocumentStudentEntity> getDocumentStudentByIdList(List<Integer> idList);
    List<DocumentStudentEntity> getDocumentStudentByByStudentId(List<Integer> idList);
    List<DocumentStudentEntity> getDocumentStudentListByStudentId(Integer studentId);
    void deleteDocumentStudent(Integer entityId);
}
