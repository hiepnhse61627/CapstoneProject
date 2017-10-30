package com.capstone.services;

import com.capstone.entities.DocumentStudentEntity;

import java.util.List;

public interface IDocumentStudentService {
    DocumentStudentEntity createDocumentStudent(DocumentStudentEntity entity);
    List<DocumentStudentEntity> getAllLatestDocumentStudent();
    List<DocumentStudentEntity> getDocumentStudentByIdList(List<Integer> idList);
}
