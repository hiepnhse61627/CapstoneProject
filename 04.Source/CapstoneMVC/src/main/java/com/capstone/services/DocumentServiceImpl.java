package com.capstone.services;

import com.capstone.entities.DocumentEntity;
import com.capstone.jpa.exJpa.ExDocumentEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class DocumentServiceImpl implements IDocumentService {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExDocumentEntityJpaController controller = new ExDocumentEntityJpaController(emf);

    @Override
    public List<DocumentEntity> getAllDocuments() {
        return controller.getAllDocuments();
    }

    @Override
    public DocumentEntity createDocument(DocumentEntity entity) {
        return controller.createDocument(entity);
    }
}
