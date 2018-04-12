package com.capstone.jpa.exJpa;

import com.capstone.entities.GraduationConditionEntity;
import com.capstone.jpa.GraduationConditionEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class ExGraduationConditionEntityJpaController extends GraduationConditionEntityJpaController {

    public ExGraduationConditionEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }


    public int getGraduateCreditsByStartCourseByProgramId(String startCourse, int programId){
        int result = -1;
        EntityManager em = null;
        try{
            em = getEntityManager();
           Query query = em.createQuery("SELECT gc.graduateCredits FROM GraduationConditionEntity gc " +
                    "WHERE gc.programId = :programId AND gc.startCourse = :startCourse");
            query.setParameter("programId", programId);
            query.setParameter("startCourse", startCourse);

             result = (int)query.getSingleResult();
        }catch(Exception e){
            e.printStackTrace();
        }
        finally {
            if(em != null){
                em.close();
            }
        }
        return result;
    }

    public List<GraduationConditionEntity> findAllGraduationCondition(){
        EntityManager em = null;
        List<GraduationConditionEntity> result = new ArrayList<>();
        try{
            em = getEntityManager();
            Query query = em.createQuery("SELECT gc FROM GraduationConditionEntity gc");

            result = query.getResultList();
        }catch(Exception e){
            e.printStackTrace();
        }
        finally {
            if(em != null){
                em.close();
            }
        }
        return result;
    }

}
