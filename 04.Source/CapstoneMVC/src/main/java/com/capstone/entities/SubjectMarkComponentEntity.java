/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capstone.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author hiepnhse61627
 */
@Entity
@Table(name = "Subject_MarkComponent", catalog = "CapstoneProject", schema = "dbo")
@NamedQueries({
    @NamedQuery(name = "SubjectMarkComponentEntity.findAll", query = "SELECT s FROM SubjectMarkComponentEntity s")})
public class SubjectMarkComponentEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "PercentWeight", precision = 53)
    private Double percentWeight;
    @Column(name = "Name", length = 2147483647)
    private String name;
    @JoinColumn(name = "MarkComponentId", referencedColumnName = "Id")
    @ManyToOne
    private MarkComponentEntity markComponentId;
    @JoinColumn(name = "SubjectId", referencedColumnName = "Id")
    @ManyToOne
    private SubjectEntity subjectId;
    @OneToMany(mappedBy = "subjectMarkComponentId")
    private List<MarksEntity> marksEntityList;

    public SubjectMarkComponentEntity() {
    }

    public SubjectMarkComponentEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getPercentWeight() {
        return percentWeight;
    }

    public void setPercentWeight(Double percentWeight) {
        this.percentWeight = percentWeight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MarkComponentEntity getMarkComponentId() {
        return markComponentId;
    }

    public void setMarkComponentId(MarkComponentEntity markComponentId) {
        this.markComponentId = markComponentId;
    }

    public SubjectEntity getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(SubjectEntity subjectId) {
        this.subjectId = subjectId;
    }

    public List<MarksEntity> getMarksEntityList() {
        return marksEntityList;
    }

    public void setMarksEntityList(List<MarksEntity> marksEntityList) {
        this.marksEntityList = marksEntityList;
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
        if (!(object instanceof SubjectMarkComponentEntity)) {
            return false;
        }
        SubjectMarkComponentEntity other = (SubjectMarkComponentEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.capstone.entities.SubjectMarkComponentEntity[ id=" + id + " ]";
    }
    
}
