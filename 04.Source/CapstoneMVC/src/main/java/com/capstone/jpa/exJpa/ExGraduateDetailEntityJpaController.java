package com.capstone.jpa.exJpa;

import com.capstone.entities.GraduateDetailEntity;
import com.capstone.jpa.GraduateDetailEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class ExGraduateDetailEntityJpaController  extends GraduateDetailEntityJpaController{
    public ExGraduateDetailEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }


}
