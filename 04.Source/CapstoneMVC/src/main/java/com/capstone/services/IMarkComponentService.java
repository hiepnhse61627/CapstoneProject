package com.capstone.services;

import com.capstone.entities.MarkComponentEntity;

public interface IMarkComponentService {
    MarkComponentEntity getMarkComponentByName(String name);
}
