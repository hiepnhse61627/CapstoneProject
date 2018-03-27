package com.capstone.models;

import com.capstone.entities.StudentEntity;

import javax.xml.bind.annotation.*;
import java.util.Comparator;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"student", "markList","average"})
@XmlRootElement(name="studentInformation")
public class StudentAndMark {

    public StudentAndMark() {
    }

    @XmlElement(required = true, name = "markDetail")
    List<MarkCreditTermModel> markList;
    @XmlElement(required = true)
    StudentEntity student;
    @XmlElement(required = true)
    Double average;




    public StudentAndMark(List<MarkCreditTermModel> markList, StudentEntity student) {
        this.markList = markList;
        this.student = student;
        this.average = caculateAverage(markList);
    }

    public List<MarkCreditTermModel> getMarkList() {
        return markList;
    }

    public void setMarkList(List<MarkCreditTermModel> markList) {
        this.markList = markList;
    }

    public StudentEntity getStudent() {
        return student;
    }

    public void setStudent(StudentEntity student) {
        this.student = student;
    }

    public Double getAverage() {
        return average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    public Double caculateAverage(List<MarkCreditTermModel> markList) {
        Double sumCredits = 0.0;
        Double sumMarks = 0.0;
        double average = 0.0;
        for (MarkCreditTermModel item : markList) {
            //ko tính những môn như lab, hoặc những môn pass mà ko có điểm
            if (item.getMark().getAverageMark() != 0
                    || item.getMark().getSubjectMarkComponentId().getSubjectId().getId().contains("LAB")) {
                Double credit = item.getCredit() * 1.0;
                sumCredits += credit;
                sumMarks += item.getMark().getAverageMark() * credit;
            }
        }
        //điểm trung bình tính = tổng số (điểm*tín chỉ) / tổng tín chỉ, làm tròn 2 chữ số thập phân
        average = Math.round((sumMarks / sumCredits) * 100.0) / 100.0;

        return average;
    }

}

