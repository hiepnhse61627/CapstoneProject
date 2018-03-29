package com.capstone.models;

import com.capstone.entities.StudentEntity;

import javax.xml.bind.annotation.*;
import java.util.Comparator;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"student", "markList","average", "highschoolGraduate", "idCard",
        "birthRecords", "engThesisName", "vnThesisName"})
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
    //bằng tốt nghiệp phổ thông
    @XmlElement(required = true)
    boolean highschoolGraduate;
    //chứng minh nhân dân
    @XmlElement(required = true)
    boolean idCard;
    //giấy khai sinh
    @XmlElement(required = true)
    boolean birthRecords;
    //tên đồ án = tiếng anh
    @XmlElement(required = true)
    String engThesisName;
    //tên đồ án = tiếng việt
    @XmlElement(required = true)
    String vnThesisName;
    //hạn chót nộp
    @XmlElement(required = true)
    String dueDate;
    //đợt tốt nghiệp trong năm
    @XmlElement(required = true)
    String graduateTime;




    public StudentAndMark(List<MarkCreditTermModel> markList, StudentEntity student) {
        this.markList = markList;
        this.student = student;
        this.average = caculateAverage(markList);
        this.highschoolGraduate = false;
        this.idCard = false;
        this.birthRecords = false;
        engThesisName ="";
        vnThesisName = "";
        dueDate = "";
        graduateTime = "";
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

    public boolean hasHighschoolGraduate() {
        return highschoolGraduate;
    }

    public void setHighschoolGraduate(boolean highschoolGraduate) {
        this.highschoolGraduate = highschoolGraduate;
    }

    public boolean hasIdCard() {
        return idCard;
    }

    public void setIdCard(boolean idCard) {
        this.idCard = idCard;
    }

    public boolean hasBirthRecords() {
        return birthRecords;
    }

    public void setBirthRecords(boolean birthRecords) {
        this.birthRecords = birthRecords;
    }

    public String getEngThesisName() {
        return engThesisName;
    }

    public void setEngThesisName(String engThesisName) {
        this.engThesisName = engThesisName;
    }

    public String getVnThesisName() {
        return vnThesisName;
    }

    public void setVnThesisName(String vnThesisnName) {
        this.vnThesisName = vnThesisnName;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getGraduateTime() {
        return graduateTime;
    }

    public void setGraduateTime(String graduateTime) {
        this.graduateTime = graduateTime;
    }

    public Double caculateAverage(List<MarkCreditTermModel> markList) {
        Double sumCredits = 0.0;
        Double sumMarks = 0.0;
        double average = 0.0;
        for (MarkCreditTermModel item : markList) {
            //ko tính những môn như lab, hoặc những môn pass mà ko có điểm
            if (item.getMark().getAverageMark() != 0
                    || item.getMark().getSubjectMarkComponentId().getSubjectId().getId().toLowerCase().contains("lab")
                    || item.getMark().getSubjectMarkComponentId().getSubjectId().getId().toLowerCase().contains("vov")) {
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

