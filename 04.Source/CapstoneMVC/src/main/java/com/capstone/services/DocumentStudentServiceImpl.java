package com.capstone.services;

import com.capstone.entities.DocumentEntity;
import com.capstone.entities.DocumentStudentEntity;
import com.capstone.jpa.exJpa.ExDocumentStudentEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class DocumentStudentServiceImpl implements IDocumentStudentService {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExDocumentStudentEntityJpaController controller = new ExDocumentStudentEntityJpaController(emf);

    @Override
    public DocumentStudentEntity createDocumentStudent(DocumentStudentEntity entity) {
        return controller.createDocumentStudent(entity);
    }

    @Override
    public DocumentStudentEntity getLastestDocumentStudentById(int studentId) {
        return controller.getLastestDocumentStudentById(studentId);
    }

    @Override
    public List<DocumentStudentEntity> getAllLatestDocumentStudent() {
        return controller.getAllLatestDocumentStudent();
    }

    @Override
    public List<DocumentStudentEntity> getDocumentStudentByIdList(List<Integer> idList) {
        return controller.getDocumentStudentByIdList(idList);
    }
}
