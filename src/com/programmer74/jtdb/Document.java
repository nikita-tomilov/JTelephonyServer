package com.programmer74.jtdb;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="Documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "doc_increment")
    @GenericGenerator(name = "doc_increment", strategy = "increment")
    private Integer id;

    @Column
    private String Path;

    @Column
    private Integer SentBy;

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", Path='" + Path + '\'' +
                ", SentBy=" + SentBy +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    public Integer getSentBy() {
        return SentBy;
    }

    public void setSentBy(Integer sentBy) {
        SentBy = sentBy;
    }
}