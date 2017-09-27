package com.capstone.services;

import com.capstone.jpa.exJpa.WebBaseJpaController;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class BaseServiceImpl implements IBaseService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    WebBaseJpaController webBaseJpaController = new WebBaseJpaController(emf);

    @Override
    public List<Object> getDataInDatabaseByQuery(String queryString) {
        return webBaseJpaController.getDataInDatabaseByQuery(queryString);
    }

    @Override
    public List<String> getColumnHeaders(String sqlString) {
        return webBaseJpaController.getColumnHeaders(sqlString);
    }
}
