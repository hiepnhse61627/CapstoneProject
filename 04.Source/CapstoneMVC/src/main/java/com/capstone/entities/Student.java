package com.capstone.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author hiepnhse61627
 */
@Entity
@Table(name = "Student", catalog = "CapstoneProject", schema = "dbo", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"RollNumber"})})
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "RollNumber", nullable = false, length = 50)
    private String rollNumber;
    @Column(name = "MemberCode", length = 250)
    private String memberCode;
    @Column(name = "LastName", length = 50)
    private String lastName;
    @Column(name = "MiddleName", length = 50)
    private String middleName;
    @Column(name = "FirstName", length = 50)
    private String firstName;
    @Column(name = "DateOfBirth")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfBirth;
    @Column(name = "Gender")
    private Boolean gender;
    @Column(name = "IDCard", length = 50)
    private String iDCard;
    @Column(name = "DateOfIssue")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfIssue;
    @Column(name = "PlaceOfIssue", length = 100)
    private String placeOfIssue;
    @Column(name = "Address", length = 200)
    private String address;
    @Column(name = "HomePhone", length = 11)
    private String homePhone;
    @Column(name = "MobilePhone", length = 11)
    private String mobilePhone;
    @Column(name = "Email", length = 50)
    private String email;
    @Column(name = "ParentName", length = 100)
    private String parentName;
    @Column(name = "ParentJob", length = 100)
    private String parentJob;
    @Column(name = "PlaceOfWork", length = 200)
    private String placeOfWork;
    @Column(name = "ParentPhone", length = 11)
    private String parentPhone;
    @Column(name = "ParentAddress", length = 200)
    private String parentAddress;
    @Column(name = "ParentEmail", length = 50)
    private String parentEmail;
    @Column(name = "ModeID", length = 50)
    private String modeID;
    @Column(name = "Major", length = 100)
    private String major;
    @Column(name = "Nganh", length = 50)
    private String nganh;
    @OneToMany(mappedBy = "rollNumber")
    private List<Marks> marksList;

    public Student() {
    }

    public Student(Integer id) {
        this.id = id;
    }

    public Student(Integer id, String rollNumber) {
        this.id = id;
        this.rollNumber = rollNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getIDCard() {
        return iDCard;
    }

    public void setIDCard(String iDCard) {
        this.iDCard = iDCard;
    }

    public Date getDateOfIssue() {
        return dateOfIssue;
    }

    public void setDateOfIssue(Date dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    public String getPlaceOfIssue() {
        return placeOfIssue;
    }

    public void setPlaceOfIssue(String placeOfIssue) {
        this.placeOfIssue = placeOfIssue;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentJob() {
        return parentJob;
    }

    public void setParentJob(String parentJob) {
        this.parentJob = parentJob;
    }

    public String getPlaceOfWork() {
        return placeOfWork;
    }

    public void setPlaceOfWork(String placeOfWork) {
        this.placeOfWork = placeOfWork;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }

    public String getParentAddress() {
        return parentAddress;
    }

    public void setParentAddress(String parentAddress) {
        this.parentAddress = parentAddress;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public String getModeID() {
        return modeID;
    }

    public void setModeID(String modeID) {
        this.modeID = modeID;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getNganh() {
        return nganh;
    }

    public void setNganh(String nganh) {
        this.nganh = nganh;
    }

    public List<Marks> getMarksList() {
        return marksList;
    }

    public void setMarksList(List<Marks> marksList) {
        this.marksList = marksList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Student)) {
            return false;
        }
        Student other = (Student) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Student[ id=" + id + " ]";
    }

}

