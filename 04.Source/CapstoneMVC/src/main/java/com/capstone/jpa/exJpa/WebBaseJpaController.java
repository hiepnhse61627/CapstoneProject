package com.capstone.jpa.exJpa;

import com.capstone.models.Ultilities;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WebBaseJpaController implements Serializable {

    public WebBaseJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }

    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public List<String> getColumnHeaders(String sqlString) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ResultSetMetaData resultSetMetaData = null;
        List<String> columnHeaders = new ArrayList<>();

        try {
            connection = Ultilities.getConnection();
            preparedStatement = connection.prepareStatement(sqlString);
            resultSet = preparedStatement.executeQuery();
            resultSetMetaData = resultSet.getMetaData();

            int columnCount = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = resultSetMetaData.getColumnName(i);
                columnHeaders.add(columnName);
            }

            return columnHeaders;
        } catch (Exception ex) {
            return null;
        }
    }

    public List<Object> getDataInDatabaseByQuery(String sqlString) {
        EntityManager em = null;
        List<Object> objects = new ArrayList<>();

        try {
            em = getEntityManager();
            Query query = em.createNativeQuery(sqlString);
            objects = query.getResultList();

            return objects;
        } catch (NoResultException nrEx) {
            nrEx.printStackTrace();
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
