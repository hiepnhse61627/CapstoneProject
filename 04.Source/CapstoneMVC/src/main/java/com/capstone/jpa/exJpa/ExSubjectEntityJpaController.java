package com.capstone.jpa.exJpa;

import com.capstone.entities.SubjectEntity;
import com.capstone.entities.SubjectMarkComponentEntity;
import com.capstone.jpa.StudentEntityJpaController;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExSubjectEntityJpaController extends StudentEntityJpaController {

    public ExSubjectEntityJpaController(EntityManagerFactory emf) {
        super(emf);
    }

    public void insertSubjectList(List<SubjectEntity> list, Map<String, String> prerequisiteList) {
        EntityManager manager = getEntityManager();
        TypedQuery<SubjectEntity> query = manager.createQuery("SELECT c FROM SubjectEntity c", SubjectEntity.class);
        List<SubjectEntity> cur = query.getResultList();
//
//        List<SubjectEntity> list1 = list.stream().filter(c -> c.getPrequisiteId() == null).collect(Collectors.toList());
//        List<SubjectEntity> list2 = list.stream().filter(c -> c.getPrequisiteId() != null).collect(Collectors.toList());

        manager.getTransaction().begin();
        for (SubjectEntity en : list) {
            if (!cur.stream().anyMatch(c -> c.getId().equals(en.getId()))) {

                SubjectMarkComponentEntity entity = new SubjectMarkComponentEntity();
                entity.setSubjectId(en.getId());

                en.setSubjectMarkComponentById(entity);
                entity.setSubjectBySubjectId(en);

                manager.persist(en);
                manager.persist(entity);
            } else {
                System.out.println(en.getId() + " has exist!");
            }
        }
        manager.getTransaction().commit();

        manager.getTransaction().begin();
        for (String subjectCode : prerequisiteList.keySet()) {
            SubjectEntity subject = manager.find(SubjectEntity.class, subjectCode);
            subject.setPrequisiteId(prerequisiteList.get(subjectCode));
            manager.merge(subject);
        }
        manager.getTransaction().commit();

//        manager.getTransaction().begin();
//
//        for (SubjectEntity en : list2) {
//            if (!cur.stream().anyMatch(c -> c.getId().equals(en.getId()))) {
//                String tmp = en.getPrequisiteId();
//
//                SubjectMarkComponentEntity entity = new SubjectMarkComponentEntity();
//                entity.setSubjectId(en.getId());
//
//                en.setSubjectMarkComponentById(entity);
//                en.setPrequisiteId(null);
//                entity.setSubjectBySubjectId(en);
//
//                manager.persist(en);
//                manager.persist(entity);
//
//                en.setPrequisiteId(tmp);
//                manager.merge(en);
//            }
//        }
//
//        manager.getTransaction().commit();
    }
}
