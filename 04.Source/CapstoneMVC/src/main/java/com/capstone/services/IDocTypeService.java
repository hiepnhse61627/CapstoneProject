package com.capstone.services;

import com.capstone.entities.DocTypeEntity;

import java.util.List;

public interface IDocTypeService {
    List<DocTypeEntity> getAllDocTypes();
    DocTypeEntity createDocType(DocTypeEntity entity);
    DocTypeEntity findDocType(int id);
    DocTypeEntity findDocType(String name);
}
