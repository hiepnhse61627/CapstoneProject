package com.capstone.services;

import java.util.List;

public interface IBaseService {
    List<Object> getDataInDatabaseByQuery(String queryString);
    List<String> getColumnHeaders(String sqlString);
}
