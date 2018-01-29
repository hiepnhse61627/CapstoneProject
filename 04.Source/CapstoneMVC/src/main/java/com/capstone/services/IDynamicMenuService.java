package com.capstone.services;

import com.capstone.entities.DynamicMenuEntity;

import java.util.List;

public interface IDynamicMenuService {
    List<DynamicMenuEntity> getAllMenu();
    DynamicMenuEntity findDynamicMenuByLink(String link);
}
