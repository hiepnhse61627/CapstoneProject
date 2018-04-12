package com.capstone.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 *
 * @author StormNs
 */
@Entity
@Table(name = "GraduationCondition")
@NamedQueries({
        @NamedQuery(name = "GraduationConditionEntity.findAll", query = "SELECT g FROM GraduationConditionEntity g")
        , @NamedQuery(name = "GraduationConditionEntity.findById", query = "SELECT g FROM GraduationConditionEntity g WHERE g.id = :id")
        , @NamedQuery(name = "GraduationConditionEntity.findByStartCourse", query = "SELECT g FROM GraduationConditionEntity g WHERE g.startCourse = :startCourse")
        , @NamedQuery(name = "GraduationConditionEntity.findByGraduateCredits", query = "SELECT g FROM GraduationConditionEntity g WHERE g.graduateCredits = :graduateCredits")})
public class GraduationConditionEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "StartCourse")
    private String startCourse;
    @Column(name = "GraduateCredits")
    private Integer graduateCredits;
    @JoinColumn(name = "ProgramId", referencedColumnName = "Id")
    @ManyToOne
    private ProgramEntity programId;

    public GraduationConditionEntity() {
    }

    public GraduationConditionEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStartCourse() {
        return startCourse;
    }

    public void setStartCourse(String startCourse) {
        this.startCourse = startCourse;
    }

    public Integer getGraduateCredits() {
        return graduateCredits;
    }

    public void setGraduateCredits(Integer graduateCredits) {
        this.graduateCredits = graduateCredits;
    }

    public ProgramEntity getProgramId() {
        return programId;
    }

    public void setProgramId(ProgramEntity programId) {
        this.programId = programId;
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
        if (!(object instanceof GraduationConditionEntity)) {
            return false;
        }
        GraduationConditionEntity other = (GraduationConditionEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.GraduationConditionEntity[ id=" + id + " ]";
    }

}
