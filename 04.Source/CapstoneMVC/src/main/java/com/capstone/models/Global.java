package com.capstone.models;

import com.capstone.entities.MarksEntity;
import com.capstone.entities.RealSemesterEntity;
import com.capstone.services.IRealSemesterService;
import com.capstone.services.RealSemesterServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

public class Global {
    private static RealSemesterEntity currentSemester;
    private static RealSemesterEntity temporarySemester;

    private static List<RealSemesterEntity> sortedList;

//    public Global() {
//        IRealSemesterService semesterService = new RealSemesterServiceImpl();
//        List<RealSemesterEntity> list = semesterService.getAllSemester();
//        sortedList = Ultilities.SortSemesters(list);
//    }

    public static List<RealSemesterEntity> getSortedList() {
//        if (sortedList == null) {
            IRealSemesterService semesterService = new RealSemesterServiceImpl();
            List<RealSemesterEntity> l = semesterService.getAllSemester();
            sortedList = Ultilities.SortSemesters(l);
//        }
        return sortedList;
    }

    public static RealSemesterEntity getCurrentSemester() {
        return currentSemester;
    }

    public static void setCurrentSemester(RealSemesterEntity currentSemester) {
        Global.currentSemester = currentSemester;
    }

    public static RealSemesterEntity getTemporarySemester() {
        return temporarySemester;
    }

    public static void setTemporarySemester(RealSemesterEntity temporarySemester) {
        Global.temporarySemester = temporarySemester;
    }

    public static int SemesterGap() {
        int indexCurrent = sortedList.indexOf(currentSemester);
        int indexTemporary = sortedList.indexOf(temporarySemester);
        return indexCurrent - indexTemporary;
    }

    public static List<MarksEntity> TransformMarksList(List<MarksEntity> list) {
        List<MarksEntity> removeMarks;
        removeMarks = list
                .stream()
                .filter(c -> c.getIsActivated() == true)
                .filter(c -> sortedList.indexOf(c.getSemesterId()) <= sortedList.indexOf(temporarySemester))
                .collect(Collectors.toList());
        if (currentSemester.getId().intValue() != temporarySemester.getId().intValue()) {
            for (MarksEntity mark : removeMarks) {
                if (mark.getSemesterId() != null && mark.getSemesterId().getId() == temporarySemester.getId()) {
                    mark.setStatus("Studying");
                }
//                else if (mark.getSemesterId() != null && mark.getSemesterId().getId() > temporarySemester.getId()) {
//                    mark.setStatus("NotStart");
//                }
            }
        }

        return removeMarks;
    }

    public static int CompareSemesterGap(RealSemesterEntity r1) {
        int gap = sortedList.indexOf(temporarySemester) - sortedList.indexOf(r1);
        System.out.println(gap);
        if (gap < 0)
            return 0;
        else
            return gap;
    }
}
