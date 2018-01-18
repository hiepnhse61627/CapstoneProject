package com.capstone.services;

import com.capstone.entities.*;
import com.capstone.jpa.exJpa.ExStudentEntityJpaController;
import com.capstone.jpa.exJpa.ExSubjectCurriculumJpaController;
import com.capstone.jpa.exJpa.ExSubjectEntityJpaController;
import com.capstone.jpa.exJpa.ExSubjectMarkComponentJpaController;
import com.capstone.models.FailedSubject;
import com.capstone.models.ReplacementSubject;
import com.capstone.models.StudentFailedSubject;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class StudentServiceImpl implements IStudentService {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("CapstonePersistence");
    ExStudentEntityJpaController studentEntityJpaController = new ExStudentEntityJpaController(emf);
    ExSubjectEntityJpaController subjectEntityJpaController = new ExSubjectEntityJpaController(emf);

    @Override
    public void createStudentList(List<StudentEntity> studentEntityList) {
        studentEntityJpaController.createStudentList(studentEntityList);
    }

    @Override
    public StudentEntity findStudentById(int id) {
        return studentEntityJpaController.findStudentEntity(id);
    }

    @Override
    public StudentEntity findStudentByRollNumber(String rollNumber) {
        return studentEntityJpaController.findStudentByRollNumber(rollNumber);
    }

    @Override
    public StudentEntity findStudentByEmail(String email) {
        return studentEntityJpaController.findStudentByEmail(email);
    }

    @Override
    public List<StudentEntity> findStudentsByFullNameOrRollNumber(String searchValue) {
        return studentEntityJpaController.findStudentsByFullNameOrRollNumber(searchValue);
    }

    public List<StudentEntity> findAllStudents() {
        return studentEntityJpaController.findStudentEntityEntities();
    }

    @Override
    public List<StudentEntity> findAllStudentsWithoutCurChange() {
        return studentEntityJpaController.findStudentByIsActivated();
    }

    @Override
    public List<StudentEntity> findStudentsByProgramName(String programName) {
        return studentEntityJpaController.findStudentByProgramName(programName);
    }

    @Override
    public void saveStudent(StudentEntity stu) throws Exception {
        studentEntityJpaController.saveStudent(stu);
    }

    @Override
    public StudentEntity cleanDocumentAndOldRollNumber(StudentEntity stu) {
        return studentEntityJpaController.cleanDocumentAndOldRollNumber(stu);
    }

    @Override
    public List<StudentEntity> getStudentByDocType(int type) {
        return studentEntityJpaController.getStudentByDocType(type);
    }

    @Override
    public List<StudentEntity> getStudentByProgram(int programId) {
        return studentEntityJpaController.getStudentByProgram(programId);
    }

    @Override
    public StudentEntity createStudent(StudentEntity studentEntity) {
        return studentEntityJpaController.createStudent(studentEntity);
    }

    @Override
    public List<StudentEntity> findStudentByProgramId(Integer programId) {
        return studentEntityJpaController.findStudentByProgramId(programId);
    }

    @Override
    public void updateStudent(StudentEntity entity) {
        try {
            studentEntityJpaController.edit(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<StudentFailedSubject> getStudentFailCreditsByCredits(int numOfCredit) {
        List<StudentEntity> students = findAllStudents();
        ExSubjectCurriculumJpaController subjectCurriculumJpaController = new ExSubjectCurriculumJpaController(emf);
//        HashMap<String,List<String>> studentFailedSubjects= new HashMap<>();
        List<StudentFailedSubject> studentFailedSubjects = new ArrayList<>();
        List<Object[]> replaceSubjectList = subjectEntityJpaController.getAllReplaceSubjects();
        HashMap<String, String> replaceSubjects = new HashMap<String, String>();
        for (Object[] i : replaceSubjectList) replaceSubjects.put(i[0].toString(), i[1].toString());
        try {
            for (StudentEntity student : students) {
                int failedCredit = 0;
//                List<MarksEntity> markList = student.getMarksEntityList();
                List<Object[]> subjectMarkCompList = studentEntityJpaController.getSubjectMarkComByStudent(student).stream().collect(Collectors.toList());
//            List<String> subjectIdList = subjectMarkCompList.stream().map(x-> x.getSubjectId().getId()).collect(Collectors.toList());
                HashMap<String, FailedSubject> FailedSubjects = new HashMap<>();

                List<Object[]> studentSubjectCredits = studentEntityJpaController.getSubjectsWithCreditsByStudent(student.getId()).stream().collect(Collectors.toList());
                for (int i = 0; i < subjectMarkCompList.size(); i++) {
                    if (i == 0) {
                        if (subjectMarkCompList.get(i)[3].toString().equals("Fail")) {
                            FailedSubject failedSubject = new FailedSubject("Fail", subjectMarkCompList.get(i)[2].toString());
                            FailedSubjects.put(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                        }
                    } else {
                        if (FailedSubjects.containsKey(subjectMarkCompList.get(i)[1].toString())) {
                            //0:studentId, 1:SubjectId, 2:Semester, 3:Status
                            String preSemester = FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).getSemester();
                            String semester = subjectMarkCompList.get(i)[2].toString();
                            int preYear = Integer.parseInt(preSemester.substring(preSemester.length() - 4, preSemester.length()));
                            int semesterYear = Integer.parseInt(semester.substring(semester.length() - 4, semester.length()));
                            if (preYear == semesterYear) {
                                String preSeason = preSemester.substring(0, preSemester.length() - 4);
                                int preNumber = 0;
                                String season = semester.substring(0, preSemester.length() - 4);
                                int number = 0;
                                preNumber = getSeasonNumber(preSeason);
                                number = getSeasonNumber(season);
                                if (number > preNumber) {
                                    if (subjectMarkCompList.get(i)[3].toString().equals("Fail")) {
                                        FailedSubject failedSubject = new FailedSubject("Fail", subjectMarkCompList.get(i)[2].toString());
                                        FailedSubjects.put(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                                    }
                                    if (subjectMarkCompList.get(i)[3].toString().equals("Passed")
                                            && FailedSubjects.containsKey((subjectMarkCompList.get(i)[1].toString()))) {
                                        FailedSubject failedSubject = new FailedSubject("Passed", subjectMarkCompList.get(i)[2].toString());
                                        FailedSubjects.replace(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                                    }
                                }
                                if (number == preNumber) {
                                    //Thi lai pass
                                    if (subjectMarkCompList.get(i)[3].toString().equals("Passed")
                                            && FailedSubjects.containsKey((subjectMarkCompList.get(i)[1].toString()))) {
                                        FailedSubject failedSubject = new FailedSubject("Passed", subjectMarkCompList.get(i)[2].toString());
                                        FailedSubjects.replace(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                                    }
                                }
                            }

                            if (preYear < semesterYear) {
                                if (subjectMarkCompList.get(i)[3].toString().equals("Fail")) {
                                    FailedSubject failedSubject = new FailedSubject("Fail", subjectMarkCompList.get(i)[2].toString());
                                    FailedSubjects.replace(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                                }
                                if (subjectMarkCompList.get(i)[3].toString().equals("Passed")
                                        && FailedSubjects.containsKey((subjectMarkCompList.get(i)[1].toString()))) {
                                    //Thi lai pass
                                    FailedSubject failedSubject = new FailedSubject("Passed", subjectMarkCompList.get(i)[2].toString());
                                    FailedSubjects.replace(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                                }
                            }
                            FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).setRedo(true);
                        } else {
                            if (subjectMarkCompList.get(i)[3].toString().equals("Fail")) {
                                FailedSubject failedSubject = new FailedSubject("Fail", subjectMarkCompList.get(i)[2].toString());
                                FailedSubjects.put(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                            } else {
                                FailedSubject failedSubject = new FailedSubject("Passed", subjectMarkCompList.get(i)[2].toString());
                                FailedSubjects.put(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                            }
                        }
                    }
                }
                if (FailedSubjects.size() != 0) {
                    String failedSub = "";
                    String relearnSub = "";
                    for (Map.Entry<String, FailedSubject> entry : FailedSubjects.entrySet()) {

                        if (entry.getValue().getStatus().equals("Fail")) {
                            if (failedSub == "") {
                                for (Object[] subjectCredit : studentSubjectCredits) {
                                    if (entry.getKey().equals(subjectCredit[0].toString())) {
                                        //
                                        if (replaceSubjects.containsKey(entry.getKey())) {
                                            if (FailedSubjects.containsKey(replaceSubjects.get(entry.getKey()))) {
                                                if (FailedSubjects.get(replaceSubjects.get(entry.getKey())).getStatus().equals("Passed")) {
                                                    entry.getValue().setStatus("PRS");//PassReplacementSubject

                                                } else {
                                                    failedCredit += Integer.parseInt(subjectCredit[1].toString());
                                                    //In cac mon chuyen nganh failed
                                                    failedSub += entry.getKey();

                                                }
                                            }
                                            else {
                                                failedCredit += Integer.parseInt(subjectCredit[1].toString());
                                                //In cac mon chuyen nganh failed
                                                failedSub += entry.getKey();
                                            }
                                        }
                                        else {
                                            failedCredit += Integer.parseInt(subjectCredit[1].toString());
                                            //In cac mon chuyen nganh failed
                                            failedSub += entry.getKey();
                                        }
                                    }
                                }
                                //In tat ca cac mon failed
                                //failedSub += entry.getKey();
                            } else {
                                for (Object[] subjectCredit : studentSubjectCredits) {
                                    if (entry.getKey().equals(subjectCredit[0].toString())) {
                                        if (replaceSubjects.containsKey(entry.getKey())) {
                                            if (FailedSubjects.containsKey(replaceSubjects.get(entry.getKey()))) {
                                                if (FailedSubjects.get(replaceSubjects.get(entry.getKey())).getStatus().equals("Passed")) {
                                                    entry.getValue().setStatus("PRS");//PassReplacementSubject

                                                } else {
                                                    failedCredit += Integer.parseInt(subjectCredit[1].toString());
                                                    //In cac mon chuyen nganh failed
                                                    failedSub += ", "+entry.getKey();

                                                }
                                            }
                                            else {
                                                failedCredit += Integer.parseInt(subjectCredit[1].toString());
                                                //In cac mon chuyen nganh failed
                                                failedSub += ", "+entry.getKey();
                                            }
                                        }
                                        else {
                                            failedCredit += Integer.parseInt(subjectCredit[1].toString());
                                            //In cac mon chuyen nganh failed
                                            failedSub += ", "+entry.getKey();
                                        }

                                    }


                                }
                                //In tat ca cac mon failed
                                //failedSub += " ," + entry.getKey();

                            }
                        }

                        if (entry.getValue().isRedo()) {
                            if (relearnSub == "") {
                                for (Object[] subjectCredit : studentSubjectCredits) {
                                    if (entry.getKey().equals(subjectCredit[0].toString())) {
                                        //In cac mon chuyen nganh hoc lai
                                        relearnSub += entry.getKey();
                                    }
                                }
                                //In tat ca cac mon hoc lai
                                //relearnSub += entry.getKey();
                            } else {
                                if (entry.getValue().isRedo()) {
                                    for (Object[] subjectCredit : studentSubjectCredits) {
                                        if (entry.getKey().equals(subjectCredit[0].toString())) {
                                            //In cac mon chuyen nganh hoc lai
                                            relearnSub += ", " + entry.getKey();
                                        }
                                    }
                                    //In tat ca cac mon hoc lai
                                    //relearnSub += " ,"+entry.getKey();

                                }
                            }

                        }
                    }
                    if (failedCredit >= numOfCredit) {
                        studentFailedSubjects.add(new StudentFailedSubject(student.getFullName(), student.getRollNumber(), failedSub, relearnSub, failedCredit));
                    }
                }
//                studentFailedSubjects.put(student.getRollNumber(), FailedSubject);


            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        int size = studentFailedSubjects.size();
        return studentFailedSubjects;
    }


    private int getSeasonNumber(String season) {
        int number;
        switch (season.toLowerCase()) {
            case "spring": {
                number = 1;
            }
            case "summer": {
                number = 2;
            }
            case "fall": {
                number = 3;
            }
            default: {
                number = 0;
            }
        }
        return number;
    }

    @Override
    public List<StudentEntity> getStudentFailedMoreThanRequiredCredits(Integer credits) {
        List<StudentEntity> students = findAllStudents();
        List<StudentEntity> onGoingStudents = new ArrayList<>();
        List<StudentEntity> resultList = new ArrayList<>();

        if (students != null && !students.isEmpty()) {
            for (StudentEntity student : students) {
                List<StudentStatusEntity> studentStatusList = student.getStudentStatusEntityList();
                if (studentStatusList != null && !studentStatusList.isEmpty()) {
                    for (StudentStatusEntity studentStatus : studentStatusList) {
                        if (!studentStatus.getStatus().equals("G")) {
                            onGoingStudents.add(student);
                            break;
                        }
                    }
                }
            }
        }

        if (!onGoingStudents.isEmpty()) {
            for (StudentEntity student : onGoingStudents) {
                Integer passFailCredits = student.getPassFailCredits();
                Integer passCredits = student.getPassCredits();
                if (passFailCredits - passCredits >= credits) {
                    resultList.add(student);
                }
            }
        }

        return resultList;
    }

    @Override
    public List<StudentEntity> findStudentsBySemesterId(int semesterId) {
        return studentEntityJpaController.findStudentBySemesterId(semesterId);
    }

    @Override
    public int getCurrentLine() {
        return studentEntityJpaController.getCurrentLine();
    }

    @Override
    public int getTotalLine() {
        return studentEntityJpaController.getTotalLine();
    }
}
