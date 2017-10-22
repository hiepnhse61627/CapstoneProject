package com.capstone.services;

import com.capstone.entities.DocTypeEntity;
import com.capstone.jpa.exJpa.ExDocTypeEntityJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class DocTypeServiceImpl implements IDocTypeService {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExDocTypeEntityJpaController controller = new ExDocTypeEntityJpaController(emf);

    @Override
    public List<DocTypeEntity> getAllDocTypes() {
        return controller.getAllDocTypes();
    }

    @Override
    public DocTypeEntity createDocType(DocTypeEntity entity) {
        return controller.createDocType(entity);
    }
}
