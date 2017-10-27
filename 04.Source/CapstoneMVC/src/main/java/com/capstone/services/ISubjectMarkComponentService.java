package com.capstone.services;

import com.capstone.entities.SubjectMarkComponentEntity;

public interface ISubjectMarkComponentService {
    SubjectMarkComponentEntity findSubjectMarkComponentById(Integer id);
    SubjectMarkComponentEntity createSubjectMarkComponent(SubjectMarkComponentEntity entity);
    SubjectMarkComponentEntity findSubjectMarkComponentByNameAndSubjectCd(String name, String subjectCd);
}
