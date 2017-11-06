package com.capstone.services;

import com.capstone.entities.DocumentEntity;

import java.util.List;

public interface IDocumentService {
    List<DocumentEntity> getAllDocuments();
    DocumentEntity createDocument(DocumentEntity entity);
    DocumentEntity getDocumentById(int id);
    DocumentEntity getDocumentByDocTypeId(int id);
}
