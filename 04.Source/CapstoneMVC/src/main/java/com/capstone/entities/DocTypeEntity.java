package com.capstone.entities;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "DocType", schema = "dbo", catalog = "CapstoneProject")
public class DocTypeEntity {
    private int id;
    private String name;
    private Collection<DocumentEntity> documentsById;

    @Id
    @Column(name = "Id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocTypeEntity that = (DocTypeEntity) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "docTypeByDocTypeId")
    public Collection<DocumentEntity> getDocumentsById() {
        return documentsById;
    }

    public void setDocumentsById(Collection<DocumentEntity> documentsById) {
        this.documentsById = documentsById;
    }
}
