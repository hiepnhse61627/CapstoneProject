package com.capstone.services;

import com.capstone.entities.*;
import com.capstone.jpa.RealSemesterEntityJpaController;
import com.capstone.jpa.exJpa.ExStudentEntityJpaController;
import com.capstone.jpa.exJpa.ExSubjectCurriculumJpaController;
import com.capstone.jpa.exJpa.ExSubjectEntityJpaController;
import com.capstone.jpa.exJpa.ExSubjectMarkComponentJpaController;
import com.capstone.models.*;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
                                String season = semester.substring(0, semester.length() - 4);
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
                                            } else {
                                                failedCredit += Integer.parseInt(subjectCredit[1].toString());
                                                //In cac mon chuyen nganh failed
                                                failedSub += entry.getKey();
                                            }
                                        } else {
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
                                                    failedSub += ", " + entry.getKey();
                                                }
                                            } else {
                                                failedCredit += Integer.parseInt(subjectCredit[1].toString());
                                                //In cac mon chuyen nganh failed
                                                failedSub += ", " + entry.getKey();
                                            }
                                        } else {
                                            failedCredit += Integer.parseInt(subjectCredit[1].toString());
                                            //In cac mon chuyen nganh failed
                                            failedSub += ", " + entry.getKey();
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

    @Override
    public List<StudentEntity> getStudentsFromMarksBySemester(int semesterId) {
        return studentEntityJpaController.getStudentsFromMarksBySemester(semesterId);
    }

    @Override
    public List<StudentEntity> getStudentBySemesterIdAndStatus(int semesterId, List<String> statusList) {
        return studentEntityJpaController.getStudentBySemesterIdAndStatus(semesterId, statusList);
    }

    @Override
    public List<StudentEntity> getStudentBySemesterIdAndProgram(int semesterId, int programId) {
        return studentEntityJpaController.getStudentBySemesterIdAndProgram(semesterId, programId);
    }


    private int getSeasonNumber(String season) {
        int number = 0;
        switch (season.toLowerCase()) {
            case "spring": {
                number = 1;
                break;
            }
            case "summer": {
                number = 2;
                break;
            }
            case "fall": {
                number = 3;
                break;
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

    public List<StudentFailedSubject> getSubjectsFailedBySemester(Integer selectedSemester) {
        List<StudentEntity> students = findAllStudents();
        List<RealSemesterEntity> realSemesters = Ultilities.SortSemesters(new RealSemesterServiceImpl().getAllSemester());
        RealSemesterEntity choosenSemester = null;
        List<StudentFailedSubject> studentFailedSubjects = new ArrayList<>();
        if (selectedSemester != 0) {
            for (int i = 0; i < realSemesters.size(); i++) {
                if (realSemesters.get(i).getId() == selectedSemester) {
                    choosenSemester = realSemesters.get(i);
                }
            }
        }
        String selectedSem = "";
        if (choosenSemester != null) {
            selectedSem = choosenSemester.getSemester();
        }
        int selectedYear = Integer.parseInt(selectedSem.substring(selectedSem.length() - 4, selectedSem.length()));
        String selectedSeason = selectedSem.substring(0, selectedSem.length() - 4);
        int selectedNum = getSeasonNumber(selectedSeason);

        List<Object[]> replaceSubjectList = subjectEntityJpaController.getAllReplaceSubjects();
//        List<SubjectModel> resultList = new ArrayList<>();
        HashMap<String, String> replaceSubjects = new HashMap<String, String>();
        for (Object[] i : replaceSubjectList) replaceSubjects.put(i[0].toString(), i[1].toString());
        try {
            for (StudentEntity student : students) {
                List<Object[]> subjectMarkCompList = studentEntityJpaController.getSubjectMarkComByStudent(student).stream().collect(Collectors.toList());
                Map<String, FailedSubject> FailedSubjects = new ConcurrentHashMap<String, FailedSubject>();

                List<Object[]> studentSubjectCredits = studentEntityJpaController.getSubjectsWithCreditsByStudent(student.getId()).stream().collect(Collectors.toList());
                FailedSubjects = getFailedSubjects(FailedSubjects,subjectMarkCompList,selectedYear,selectedNum);

                if (FailedSubjects.size() != 0) {
                    String failedSub = "";
                    int numberOfSubjects = 0;
                    for (Map.Entry<String, FailedSubject> entry : FailedSubjects.entrySet()) {

                        if (entry.getValue().isRedo()) {
                            if (failedSub == "") {
                                for (Object[] subjectCredit : studentSubjectCredits) {
                                    if (entry.getKey().equals(subjectCredit[0].toString())) {
                                        //
                                        if (replaceSubjects.containsKey(entry.getKey())) {
                                            if (FailedSubjects.containsKey(replaceSubjects.get(entry.getKey()))) {
                                                if (FailedSubjects.get(replaceSubjects.get(entry.getKey())).getStatus().equals("Passed")) {
                                                    entry.getValue().setStatus("PRS");//PassReplacementSubject
                                                } else {
                                                    //In cac mon chuyen nganh failed
                                                    if (entry.getValue().getSemester().equals(selectedSem)) {
                                                        failedSub += entry.getKey();
                                                        numberOfSubjects += 1;
                                                    }
                                                }
                                            } else {
                                                //In cac mon chuyen nganh failed
                                                if (entry.getValue().getSemester().equals(selectedSem)) {
                                                    failedSub += entry.getKey();
                                                    numberOfSubjects += 1;
                                                }
                                            }
                                        } else {
                                            //In cac mon chuyen nganh failed
                                            if (entry.getValue().getSemester().equals(selectedSem)) {
                                                failedSub += entry.getKey();
                                                numberOfSubjects += 1;
                                            }
                                        }
                                    }
                                }
                            } else {
                                for (Object[] subjectCredit : studentSubjectCredits) {
                                    if (entry.getKey().equals(subjectCredit[0].toString())) {
                                        if (replaceSubjects.containsKey(entry.getKey())) {
                                            if (FailedSubjects.containsKey(replaceSubjects.get(entry.getKey()))) {
                                                if (FailedSubjects.get(replaceSubjects.get(entry.getKey())).getStatus().equals("Passed")) {
                                                    entry.getValue().setStatus("PRS");//PassReplacementSubject
                                                } else {
                                                    //In cac mon chuyen nganh failed
                                                    if (entry.getValue().getSemester().equals(selectedSem)) {
                                                        failedSub += ", " + entry.getKey();
                                                        numberOfSubjects += 1;
                                                    }
                                                }
                                            } else {
                                                //In cac mon chuyen nganh failed
                                                if (entry.getValue().getSemester().equals(selectedSem)) {
                                                    failedSub += ", " + entry.getKey();
                                                    numberOfSubjects += 1;
                                                }
                                            }
                                        } else {
                                            //In cac mon chuyen nganh failed
                                            if (entry.getValue().getSemester().equals(selectedSem)) {
                                                failedSub += ", " + entry.getKey();
                                                numberOfSubjects += 1;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!failedSub.equals("")) {
                        studentFailedSubjects.add(new StudentFailedSubject(student.getFullName(), student.getRollNumber(), failedSub, "", numberOfSubjects));
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return studentFailedSubjects;
    }

    public List<StudentFailedSubject> getSubjectsSlotsFailedBySemester(Integer selectedSemester) {
        List<StudentEntity> students = findAllStudents();
        List<RealSemesterEntity> realSemesters = Ultilities.SortSemesters(new RealSemesterServiceImpl().getAllSemester());
        RealSemesterEntity choosenSemester = null;
        List<StudentFailedSubject> studentFailedSubjects = new ArrayList<>();
        if (selectedSemester != 0) {
            for (int i = 0; i < realSemesters.size(); i++) {
                if (realSemesters.get(i).getId() == selectedSemester) {
                    choosenSemester = realSemesters.get(i);
                }
            }
        }
        String selectedSem = "";
        if (choosenSemester != null) {
            selectedSem = choosenSemester.getSemester();
        }
        int selectedYear = Integer.parseInt(selectedSem.substring(selectedSem.length() - 4, selectedSem.length()));
        String selectedSeason = selectedSem.substring(0, selectedSem.length() - 4);
        int selectedNum = getSeasonNumber(selectedSeason);

        List<Object[]> replaceSubjectList = subjectEntityJpaController.getAllReplaceSubjects();
//        List<SubjectModel> resultList = new ArrayList<>();
        HashMap<String, String> replaceSubjects = new HashMap<String, String>();
        for (Object[] i : replaceSubjectList) replaceSubjects.put(i[0].toString(), i[1].toString());
        try {
            for (StudentEntity student : students) {
                List<Object[]> subjectMarkCompList = studentEntityJpaController.getSubjectMarkComByStudent(student).stream().collect(Collectors.toList());
                Map<String, FailedSubject> FailedSubjects = new ConcurrentHashMap<String, FailedSubject>();

                List<Object[]> studentSubjectCredits = studentEntityJpaController.getSubjectsWithCreditsByStudent(student.getId()).stream().collect(Collectors.toList());

                FailedSubjects = getFailedSubjects(FailedSubjects,subjectMarkCompList,selectedYear,selectedNum);

                if (FailedSubjects.size() != 0) {
                    String failedSub = "";
                    int numberOfSubjects = 0;
                    for (Map.Entry<String, FailedSubject> entry : FailedSubjects.entrySet()) {

                        if (entry.getValue().isRedo()) {
                            if (failedSub == "") {
                                for (Object[] subjectCredit : studentSubjectCredits) {
                                    if (entry.getKey().equals(subjectCredit[0].toString())) {
                                        //
                                        if (replaceSubjects.containsKey(entry.getKey())) {
                                            if (FailedSubjects.containsKey(replaceSubjects.get(entry.getKey()))) {
                                                if (FailedSubjects.get(replaceSubjects.get(entry.getKey())).getStatus().equals("Passed")) {
                                                    entry.getValue().setStatus("PRS");//PassReplacementSubject

                                                } else {
                                                    //In cac mon chuyen nganh failed
                                                    if (entry.getValue().getSemester().equals(selectedSem)) {
                                                        failedSub += entry.getKey() + "(" + entry.getValue().getRedoTimes() + ")";
                                                        numberOfSubjects += 1;
                                                    }
                                                }
                                            } else {
                                                //In cac mon chuyen nganh failed
                                                if (entry.getValue().getSemester().equals(selectedSem)) {
                                                    failedSub += entry.getKey() + "(" + entry.getValue().getRedoTimes() + ")";
                                                    numberOfSubjects += 1;
                                                }
                                            }
                                        } else {
                                            //In cac mon chuyen nganh failed
                                            if (entry.getValue().getSemester().equals(selectedSem)) {
                                                failedSub += entry.getKey() + "(" + entry.getValue().getRedoTimes() + ")";
                                                numberOfSubjects += 1;
                                            }
                                        }
                                    }
                                }
                            } else {
                                for (Object[] subjectCredit : studentSubjectCredits) {
                                    if (entry.getKey().equals(subjectCredit[0].toString())) {
                                        if (replaceSubjects.containsKey(entry.getKey())) {
                                            if (FailedSubjects.containsKey(replaceSubjects.get(entry.getKey()))) {
                                                if (FailedSubjects.get(replaceSubjects.get(entry.getKey())).getStatus().equals("Passed")) {
                                                    entry.getValue().setStatus("PRS");//PassReplacementSubject
                                                } else {
                                                    //In cac mon chuyen nganh failed
                                                    if (entry.getValue().getSemester().equals(selectedSem)) {
                                                        failedSub += ", " + entry.getKey() + "(" + entry.getValue().getRedoTimes() + ")";
                                                        numberOfSubjects += 1;
                                                    }
                                                }
                                            } else {
                                                //In cac mon chuyen nganh failed
                                                if (entry.getValue().getSemester().equals(selectedSem)) {
                                                    failedSub += ", " + entry.getKey() + "(" + entry.getValue().getRedoTimes() + ")";
                                                    numberOfSubjects += 1;
                                                }
                                            }
                                        } else {
                                            //In cac mon chuyen nganh failed
                                            if (entry.getValue().getSemester().equals(selectedSem)) {
                                                failedSub += ", " + entry.getKey() + "(" + entry.getValue().getRedoTimes() + ")";
                                                numberOfSubjects += 1;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!failedSub.equals("")) {
                        studentFailedSubjects.add(new StudentFailedSubject(student.getFullName(), student.getRollNumber(), failedSub, "", numberOfSubjects));
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return studentFailedSubjects;
    }

    public Map<String, FailedSubject> getFailedSubjects(Map<String, FailedSubject> FailedSubjects,List<Object[]> subjectMarkCompList,int selectedYear, int selectedNum){
        for (int i = 0; i < subjectMarkCompList.size(); i++) {
//                    if (student.getRollNumber().equals("SB60462") && subjectMarkCompList.get(i)[1].toString().equals("CHN132")) {
//                        System.out.println();
//                    }
            if (!subjectMarkCompList.get(i)[2].toString().equals("N/A")) {
                String semester = "";
                int semesterYear = 0;
                int number = 0;
                try {
                    semester = subjectMarkCompList.get(i)[2].toString();
                    semesterYear = Integer.parseInt(semester.substring(semester.length() - 4, semester.length()));
                    String season = semester.substring(0, semester.length() - 4);
                    number = getSeasonNumber(season);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (selectedYear > semesterYear) {
                    if (i == 0) {
                        if (subjectMarkCompList.get(i)[3].toString().equals("Fail")) {
                            FailedSubject failedSubject = new FailedSubject("Fail", subjectMarkCompList.get(i)[2].toString());
                            FailedSubjects.put(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                        }
                    } else {
                        if (FailedSubjects.containsKey(subjectMarkCompList.get(i)[1].toString())) {
                            //0:studentId, 1:SubjectId, 2:Semester, 3:Status
                            String preSemester = FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).getSemester();
                            int preYear = Integer.parseInt(preSemester.substring(preSemester.length() - 4, preSemester.length()));
                            if (preYear == semesterYear) {
                                String preSeason = preSemester.substring(0, preSemester.length() - 4);
                                int preNumber = 0;
                                preNumber = getSeasonNumber(preSeason);

                                if (number > preNumber) {
                                    if (subjectMarkCompList.get(i)[3].toString().equals("Fail")) {
                                        FailedSubject failedSubject = new FailedSubject("Fail", subjectMarkCompList.get(i)[2].toString());
                                        failedSubject.setRedoTimes(FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).getRedoTimes() + 1);
                                        FailedSubjects.put(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                                    }
                                    if (subjectMarkCompList.get(i)[3].toString().equals("Passed")) {
                                        FailedSubject failedSubject = new FailedSubject("Timed", subjectMarkCompList.get(i)[2].toString());
                                        failedSubject.setRedoTimes(FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).getRedoTimes() + 1);
                                        FailedSubjects.replace(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                                    }
                                }
                                if (number <= preNumber) {
                                    //Thi lai pass
                                    if (!FailedSubjects.containsKey((subjectMarkCompList.get(i)[1].toString()))) {
                                        FailedSubject failedSubject = new FailedSubject("", subjectMarkCompList.get(i)[2].toString());
                                        failedSubject.setRedoTimes(FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).getRedoTimes() + 1);
                                        FailedSubjects.replace(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                                    } else {
                                        FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).setRedoTimes(FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).getRedoTimes() + 1);
                                    }
                                }

                            }
                            if (preYear < semesterYear) {
                                if (subjectMarkCompList.get(i)[3].toString().equals("Fail")) {
                                    FailedSubject failedSubject = new FailedSubject("Fail", subjectMarkCompList.get(i)[2].toString());
                                    failedSubject.setRedoTimes(FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).getRedoTimes() + 1);
                                    FailedSubjects.replace(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                                }
                                if (subjectMarkCompList.get(i)[3].toString().equals("Passed")) {
                                    FailedSubject failedSubject = new FailedSubject("Timed", subjectMarkCompList.get(i)[2].toString());
                                    failedSubject.setRedoTimes(FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).getRedoTimes() + 1);
                                    FailedSubjects.replace(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                                }
                            }
                            if (preYear > semesterYear) {
                                FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).setRedoTimes(FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).getRedoTimes() + 1);
                            }
                            FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).setRedo(true);
                        } else {
                            if (subjectMarkCompList.get(i)[3].toString().equals("Fail")) {
                                FailedSubject failedSubject = new FailedSubject("Fail", subjectMarkCompList.get(i)[2].toString());
                                FailedSubjects.put(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                            }
//                                    else {
//                                        FailedSubject failedSubject = new FailedSubject("Passed", subjectMarkCompList.get(i)[2].toString());
//                                        FailedSubjects.put(subjectMarkCompList.get(i)[1].toString(), failedSubject);
//                                    }
                        }
                    }
                }
                if (selectedYear == semesterYear) {
                    if (selectedNum >= number) {
                        if (i == 0) {
                            if (subjectMarkCompList.get(i)[3].toString().equals("Fail")) {
                                FailedSubject failedSubject = new FailedSubject("Fail", subjectMarkCompList.get(i)[2].toString());
                                FailedSubjects.put(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                            }
                        } else {
                            if (FailedSubjects.containsKey(subjectMarkCompList.get(i)[1].toString())) {
                                //0:studentId, 1:SubjectId, 2:Semester, 3:Status
                                String preSemester = FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).getSemester();
                                int preYear = Integer.parseInt(preSemester.substring(preSemester.length() - 4, preSemester.length()));
                                if (preYear == semesterYear) {
                                    String preSeason = preSemester.substring(0, preSemester.length() - 4);
                                    int preNumber = 0;
                                    preNumber = getSeasonNumber(preSeason);

                                    if (number > preNumber) {
                                        if (subjectMarkCompList.get(i)[3].toString().equals("Fail")) {
                                            FailedSubject failedSubject = new FailedSubject("Fail", subjectMarkCompList.get(i)[2].toString());
                                            failedSubject.setRedoTimes(FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).getRedoTimes() + 1);
                                            FailedSubjects.put(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                                        }
                                        if (subjectMarkCompList.get(i)[3].toString().equals("Passed")) {
                                            FailedSubject failedSubject = new FailedSubject("Timed", subjectMarkCompList.get(i)[2].toString());
                                            failedSubject.setRedoTimes(FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).getRedoTimes() + 1);
                                            FailedSubjects.replace(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                                        }
                                    }
                                    if (number <= preNumber) {
                                        //Thi lai pass
                                        if (!FailedSubjects.containsKey((subjectMarkCompList.get(i)[1].toString()))) {
                                            FailedSubject failedSubject = new FailedSubject("", subjectMarkCompList.get(i)[2].toString());
                                            failedSubject.setRedoTimes(FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).getRedoTimes() + 1);
                                            FailedSubjects.replace(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                                        } else {
                                            FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).setRedoTimes(FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).getRedoTimes() + 1);
                                        }
                                    }
                                }
                                if (preYear < semesterYear) {
                                    if (subjectMarkCompList.get(i)[3].toString().equals("Fail")) {
                                        FailedSubject failedSubject = new FailedSubject("Fail", subjectMarkCompList.get(i)[2].toString());
                                        failedSubject.setRedoTimes(FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).getRedoTimes() + 1);
                                        FailedSubjects.replace(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                                    }
                                    if (subjectMarkCompList.get(i)[3].toString().equals("Passed")) {
                                        FailedSubject failedSubject = new FailedSubject("Timed", subjectMarkCompList.get(i)[2].toString());
                                        failedSubject.setRedoTimes(FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).getRedoTimes() + 1);
                                        FailedSubjects.replace(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                                    }
                                }
                                if (preYear > semesterYear) {
                                    FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).setRedoTimes(FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).getRedoTimes() + 1);
                                }
                                FailedSubjects.get(subjectMarkCompList.get(i)[1].toString()).setRedo(true);
                            } else {
                                if (subjectMarkCompList.get(i)[3].toString().equals("Fail")) {
                                    FailedSubject failedSubject = new FailedSubject("Fail", subjectMarkCompList.get(i)[2].toString());
                                    FailedSubjects.put(subjectMarkCompList.get(i)[1].toString(), failedSubject);
                                }
//                                        else {
//                                            FailedSubject failedSubject = new FailedSubject("Passed", subjectMarkCompList.get(i)[2].toString());
//                                            FailedSubjects.put(subjectMarkCompList.get(i)[1].toString(), failedSubject);
//                                        }
                            }
                        }
                    }
                }
            }
        }
        return FailedSubjects;
    }

    public Map<String,StudentAndSubject> getSubjectsStudentsStudyInSemester(Integer selectedSemester){
        List<Object[]> subjectsStudentsReLearn = studentEntityJpaController.getAllSubjectsStudentsReStudyInSameSemester(selectedSemester);
        Map<String,StudentAndSubject> subjectsStudentsReLearnMap = new HashMap<>();
        List<Object[]> resultList = new ArrayList<>();
        for (Object[] subjects:subjectsStudentsReLearn) {
            //0: RollNumber, 1: FullName, 2: Subject
            if(!subjectsStudentsReLearnMap.containsKey(subjects[0])) {
                subjectsStudentsReLearnMap.put(subjects[0].toString(),new StudentAndSubject(subjects[1].toString(),subjects[2].toString()));
            }
            else{
                String moreSubjects=subjectsStudentsReLearnMap.get(subjects[0]).getStudySubject()+", "+subjects[2].toString();
                subjectsStudentsReLearnMap.replace(subjects[0].toString(),new StudentAndSubject(subjects[1].toString(),moreSubjects));
            }
        }
        return subjectsStudentsReLearnMap;
    }

    @Override
    public void myUpdateStudent(StudentEntity student) {
        studentEntityJpaController.myUpdateStudent(student);
    }

    @Override
    public boolean myBulkUpdateStudents(List<StudentEntity> studentList) {
        return studentEntityJpaController.myBulkUpdateStudents(studentList);
    }


}
