package com.capstone.controllers;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.RealSemesterEntity;
import com.capstone.entities.StudentEntity;
import com.capstone.entities.SubjectEntity;
import com.capstone.models.Ultilities;
import com.capstone.services.IMarksService;
import com.capstone.services.IRealSemesterService;
import com.capstone.services.MarksServiceImpl;
import com.capstone.services.RealSemesterServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

/**
  * FAIL STATISTICS CONTROLLER
  * @author HiepNH
  * @DateCreated 28/10/2017
  **/
@Controller
public class FailStatisticsController {

    IRealSemesterService realSemesterService = new RealSemesterServiceImpl();
    IMarksService marksService = new MarksServiceImpl();

    /**
     * [This method return a ModelAndView to go to fail statistics page]
     * @return modelAndView
     * @author HiepNH
     * @DateCreated 28/10/2017
     **/
    @RequestMapping("/failStatistics")
    public ModelAndView failStatisticsPage() {
        ModelAndView modelAndView = new ModelAndView("FailStatistics");

        List<RealSemesterEntity> semesters = realSemesterService.getAllSemester().stream().filter(s -> !s.getSemester().contains("N/A")).collect(Collectors.toList());
        modelAndView.addObject("semesters", semesters);

        return modelAndView;
    }

     /**
      * [This method will call process methods to get list results and convert them to json object to response to jsp page]
      * @return jsonObject
      * @author HiepNH
      * @DateCreated 28/10/2017
      **/
    @RequestMapping("/failStatistics/details")
    @ResponseBody
    public JsonObject statisticsDetail(@RequestParam Map<String, String> params) {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        String semester = params.get("semester");

        ArrayList<ArrayList<String>> resultList = processData(semester);
        JsonArray aaData = (JsonArray) gson.toJsonTree(resultList);

        jsonObject.addProperty("iTotalRecords", resultList.size());
        jsonObject.addProperty("iTotalDisplayRecords",  resultList.size());
        jsonObject.add("aaData", aaData);
        jsonObject.addProperty("sEcho", params.get("sEcho"));

        return jsonObject;
    }

    /**
     * @param semester
     * @return resultList
     * @author HiepNH
     * @DateCreated 28/10/2017
     **/
    public ArrayList<ArrayList<String>> processData(String semester) {
        List<MarksEntity> listFailed = listFailedAtTheBeginningOfSemester(semester);
        List<MarksEntity> listPassed = listPassedInCurrentSemester(semester);
        List<MarksEntity> listPaid = intersectionOfTwoLists(listFailed, listPassed);
        List<MarksEntity> listFailedAtCurrentSemester = listFailedInCurrentSemester(semester);

        ArrayList<ArrayList<String>> resultList = new ArrayList<>();
        ArrayList<String> record = new ArrayList<>();
        record.add(String.valueOf(listFailed.size()));
        record.add(String.valueOf(listPaid.size()));
        record.add(String.valueOf(listFailedAtCurrentSemester.size()));
        record.add(String.valueOf(listFailed.size() - listPaid.size() + listFailedAtCurrentSemester.size()));
        resultList.add(record);

        return resultList;
    }

    /**
     * @return listMarksFailed
     * @author HiepNH
     * @DateCreated 28/10/2017
     **/
    private List<MarksEntity> listFailedAtTheBeginningOfSemester(String semesterName) {
        List<MarksEntity> resultList = new ArrayList<>();
        List<Integer> semesterIds = getToCurrentSemester(semesterName);
        if (semesterIds.size() == 1) {
            return resultList;
        }
        List<MarksEntity> listFailed = marksService.getListMarkToCurrentSemester(semesterIds, new String[] {"Fail", "IsSuspended", "IsAttendanceFail"});
        List<MarksEntity> listPassed = marksService.getListMarkToCurrentSemester(semesterIds, new String[] {"Passed", "IsExempt"});
        List<MarksEntity> comparedList = new ArrayList<>();
        List<MarksEntity> noneDuplicateList = new ArrayList<>();

        // make comparator
        Comparator<MarksEntity> comparator = (o1, o2) -> new CompareToBuilder()
                .append(o1.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase(), o2.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase())
                .append(o1.getStudentId().getRollNumber().toUpperCase(), o2.getStudentId().getRollNumber().toUpperCase())
                .toComparison();
        Collections.sort(listPassed, comparator);
        // start compare failed list to passed list
        for (MarksEntity keySearch : listFailed) {
            int index = Collections.binarySearch(listPassed, keySearch, comparator);
            if (index < 0) {
                comparedList.add(keySearch);
            }
        }
        // remove duplicate
        for (MarksEntity marksEntity : comparedList) {
            if (!noneDuplicateList.stream().anyMatch
                    (r -> r.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase().equals(marksEntity.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase())
                            && r.getStudentId().getRollNumber().toUpperCase().equals(marksEntity.getStudentId().getRollNumber().toUpperCase()))) {
                noneDuplicateList.add(marksEntity);
            }
        }
        // Create Map (Student -> List Failed Marks
        Map<StudentEntity, List<MarksEntity>> studentFailedMarksMap = new HashMap<>();
        for (MarksEntity mark : noneDuplicateList) {
            StudentEntity student = mark.getStudentId();
            if (studentFailedMarksMap.get(student) != null) {
                studentFailedMarksMap.get(student).add(mark);
            } else {
                List<MarksEntity> studentMarks = new ArrayList<>();
                studentMarks.add(mark);
                studentFailedMarksMap.put(student, studentMarks);
            }
        }
        // Create Map (Student -> List Passed Marks
        Map<StudentEntity, List<MarksEntity>> studentPassedMarksMap = new HashMap<>();
        for (MarksEntity mark : listPassed) {
            StudentEntity student = mark.getStudentId();
            if (studentPassedMarksMap.get(student) != null) {
                studentPassedMarksMap.get(student).add(mark);
            } else {
                List<MarksEntity> studentMarks = new ArrayList<>();
                studentMarks.add(mark);
                studentPassedMarksMap.put(student, studentMarks);
            }
        }

        // remove subject has replacementSubject contains in failed list or passed list
        for (Map.Entry<StudentEntity, List<MarksEntity>> entry : studentFailedMarksMap.entrySet()) {
            List<MarksEntity> listFailedMarks = entry.getValue();
            List<MarksEntity> listPassedMarks = studentPassedMarksMap.get(entry.getKey());
            Set<String> passedSubjects = listPassedMarks != null && !listPassedMarks.isEmpty()
                    ? listPassedMarks.stream().map(l -> l.getSubjectMarkComponentId().getSubjectId().getId()).collect(Collectors.toSet()) : new HashSet<>();
            for (int i = 0; i < listFailedMarks.size(); i++) {
                Set<String> subjectsInMark = listFailedMarks.stream().map(l -> l.getSubjectMarkComponentId().getSubjectId().getId()).collect(Collectors.toSet());
                List<SubjectEntity> replacedSubjects = listFailedMarks.get(i).getSubjectMarkComponentId().getSubjectId().getSubjectEntityList();
                if (replacedSubjects != null && !replacedSubjects.isEmpty()) {
                    for (SubjectEntity replaceSubject : replacedSubjects) {
                        String subjectCd = replaceSubject.getId();
                        if (subjectsInMark.contains(subjectCd) || passedSubjects.contains(subjectCd)) {
                            listFailedMarks.remove(i);
                            break;
                        }
                    }
                }
            }
            resultList.addAll(listFailedMarks);
        }

        return resultList;
    }

    /**
     * @param semester
     * @return noneDuplicateList
     * @author HiepNH
     * @DateCreated 28/10/2017
     **/
    private List<MarksEntity> listPassedInCurrentSemester(String semester) {
        List<MarksEntity> noneDuplicateList = new ArrayList<>();
        RealSemesterEntity currentSemester = realSemesterService.findSemesterByName(semester);
        List<Integer> semesterIds = new ArrayList<>();
        semesterIds.add(currentSemester.getId());
        List<MarksEntity> listPassed = marksService.getListMarkToCurrentSemester(semesterIds, new String[] {"Passed", "IsExempt"});
        // remove duplicate
        for (MarksEntity marksEntity : listPassed) {
            if (!noneDuplicateList.stream().anyMatch
                    (r -> r.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase().equals(marksEntity.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase())
                            && r.getStudentId().getRollNumber().toUpperCase().equals(marksEntity.getStudentId().getRollNumber().toUpperCase()))) {
                noneDuplicateList.add(marksEntity);
            }
        }
        return noneDuplicateList;
    }

    /**
     * [This method will find failed subject in current term]
     * @param semester
     * @return paidList
     * @author HiepNH
     * @DateCreated 28/10/2017
     **/
    private List<MarksEntity> listFailedInCurrentSemester(String semester) {
        RealSemesterEntity currentSemester = realSemesterService.findSemesterByName(semester);
        List<Integer> semesterIds = new ArrayList<>();
        semesterIds.add(currentSemester.getId());
        List<MarksEntity> failedList = marksService.getListMarkToCurrentSemester(semesterIds, new String[] {"Fail", "IsSuspended", "IsAttendanceFail"});
        List<MarksEntity> passedList = listPassedInCurrentSemester(semester);
        List<MarksEntity> comparedList = new ArrayList<>();
        List<MarksEntity> noneDuplicateList = new ArrayList<>();
        List<MarksEntity> resultList = new ArrayList<>();
        // make comparator
        Comparator<MarksEntity> comparator = (o1, o2) -> new CompareToBuilder()
                .append(o1.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase(), o2.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase())
                .append(o1.getStudentId().getRollNumber().toUpperCase(), o2.getStudentId().getRollNumber().toUpperCase())
                .toComparison();
        Collections.sort(passedList, comparator);
        // start compare failed list to passed list
        for (MarksEntity keySearch : failedList) {
            int index = Collections.binarySearch(passedList, keySearch, comparator);
            if (index < 0) {
                comparedList.add(keySearch);
            }
        }
        // remove duplicate
        for (MarksEntity marksEntity : comparedList) {
            if (!noneDuplicateList.stream().anyMatch
                    (r -> r.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase().equals(marksEntity.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase())
                            && r.getStudentId().getRollNumber().toUpperCase().equals(marksEntity.getStudentId().getRollNumber().toUpperCase()))) {
                noneDuplicateList.add(marksEntity);
            }
        }
        // Create Map (Student -> List Marks
        Map<StudentEntity, List<MarksEntity>> studentFailedMarksMap = new HashMap<>();
        for (MarksEntity mark : noneDuplicateList) {
            StudentEntity student = mark.getStudentId();
            if (studentFailedMarksMap.get(student) != null) {
                studentFailedMarksMap.get(student).add(mark);
            } else {
                List<MarksEntity> studentMarks = new ArrayList<>();
                studentMarks.add(mark);
                studentFailedMarksMap.put(student, studentMarks);
            }
        }
        // Create Map (Student -> List Passed Marks
        Map<StudentEntity, List<MarksEntity>> studentPassedMarksMap = new HashMap<>();
        for (MarksEntity mark : passedList) {
            StudentEntity student = mark.getStudentId();
            if (studentPassedMarksMap.get(student) != null) {
                studentPassedMarksMap.get(student).add(mark);
            } else {
                List<MarksEntity> studentMarks = new ArrayList<>();
                studentMarks.add(mark);
                studentPassedMarksMap.put(student, studentMarks);
            }
        }
        // remove subject has replacementSubject
        for (Map.Entry<StudentEntity, List<MarksEntity>> entry : studentFailedMarksMap.entrySet()) {
            List<MarksEntity> listMarks = entry.getValue();
            List<MarksEntity> listPassedMarks = studentPassedMarksMap.get(entry.getKey());
            Set<String> passedSubjects = listPassedMarks != null && !listPassedMarks.isEmpty()
                    ? listPassedMarks.stream().map(l -> l.getSubjectMarkComponentId().getSubjectId().getId()).collect(Collectors.toSet()) : new HashSet<>();
            for (int i = 0; i < listMarks.size(); i++) {
                Set<String> subjectsInMark = listMarks.stream().map(l -> l.getSubjectMarkComponentId().getSubjectId().getId()).collect(Collectors.toSet());
                List<SubjectEntity> replacedSubjects = listMarks.get(i).getSubjectMarkComponentId().getSubjectId().getSubjectEntityList();
                if (replacedSubjects != null && !replacedSubjects.isEmpty()) {
                    for (SubjectEntity replaceSubject : replacedSubjects) {
                        String subjectCd = replaceSubject.getId();
                        if (subjectsInMark.contains(subjectCd) || passedSubjects.contains(subjectCd)) {
                            listMarks.remove(i);
                            break;
                        }
                    }
                }
            }
            resultList.addAll(listMarks);
        }

        return resultList;
    }

    /**
     * [This method will find intersection of two list]
     * @param list1
     * @param list2
     * @return paidList
     * @author HiepNH
     * @DateCreated 28/10/2017
     **/
    private List<MarksEntity> intersectionOfTwoLists(List<MarksEntity> list1, List<MarksEntity> list2) {
        List<MarksEntity> intersectionList = new ArrayList<>();
        List<MarksEntity> resultList = new ArrayList<>();
        // make comparator
        Comparator<MarksEntity> comparator = (o1, o2) -> new CompareToBuilder()
                .append(o1.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase(), o2.getSubjectMarkComponentId().getSubjectId().getId().toUpperCase())
                .append(o1.getStudentId().getRollNumber().toUpperCase(), o2.getStudentId().getRollNumber().toUpperCase())
                .toComparison();
        Collections.sort(list2, comparator);
        // start compare failed list to passed list
        for (MarksEntity keySearch : list1) {
            int index = Collections.binarySearch(list2, keySearch, comparator);
            if (index >= 0) {
                intersectionList.add(keySearch);
            }
        }

        // Create Map (Student -> List Marks
        Map<StudentEntity, List<MarksEntity>> studentMarksMap = new HashMap<>();
        for (MarksEntity mark : intersectionList) {
            StudentEntity student = mark.getStudentId();
            if (studentMarksMap.get(student) != null) {
                studentMarksMap.get(student).add(mark);
            } else {
                List<MarksEntity> studentMarks = new ArrayList<>();
                studentMarks.add(mark);
                studentMarksMap.put(student, studentMarks);
            }
        }
        // remove subject has replacementSubject contains in passed list
        for (Map.Entry<StudentEntity, List<MarksEntity>> entry : studentMarksMap.entrySet()) {
            List<MarksEntity> listMarks = entry.getValue();
            for (int i = 0; i < listMarks.size(); i++) {
                Set<String> subjectsInMark = listMarks.stream().map(l -> l.getSubjectMarkComponentId().getSubjectId().getId()).collect(Collectors.toSet());
                List<SubjectEntity> replacedSubjects = listMarks.get(i).getSubjectMarkComponentId().getSubjectId().getSubjectEntityList();
                if (replacedSubjects != null && !replacedSubjects.isEmpty()) {
                    for (SubjectEntity replaceSubject : replacedSubjects) {
                        String subjectCd = replaceSubject.getId();
                        if (subjectsInMark.contains(subjectCd)) {
                            listMarks.remove(i);
                            break;
                        }
                    }
                }
            }
            resultList.addAll(listMarks);
        }

        return resultList;
    }

    /**
     * [This method processes (sort all semesters then iterate over the list, add semester to result list until reaching the current semester)
     *              and returns list semesters from the beginning to current semester]
     * @param currentSemester
     * @return listResult
     * @author HiepNH
     * @DateCreated 28/10/2017
     **/
    private List<Integer> getToCurrentSemester (String currentSemester) {
        List<RealSemesterEntity> semesters = realSemesterService.getAllSemester().stream().filter(s -> !s.getSemester().contains("N/A")).collect(Collectors.toList());
        semesters = Ultilities.SortSemesters(semesters);
        List<Integer> listResult = new ArrayList<>();
        for (RealSemesterEntity semester : semesters) {
            listResult.add(semester.getId());
            if (semester.getSemester().equals(currentSemester)) {
                break;
            }
        }

        if (listResult.size() == 1) { return listResult; }
        listResult.remove(listResult.size() - 1);
        return listResult;
    }
}
