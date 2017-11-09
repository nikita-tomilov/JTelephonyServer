package com.programmer74.jtdb;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="contacts")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "cntct_increment")
    @GenericGenerator(name = "cntct_increment", strategy = "increment")
    private Integer id;

    @Column
    private Integer FromID;
    @Column
    private Integer ToID;
    @Column
    private Integer IsAccepted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromID() {
        return FromID;
    }

    public void setFromID(Integer fromID) {
        FromID = fromID;
    }

    public Integer getToID() {
        return ToID;
    }

    public void setToID(Integer toID) {
        ToID = toID;
    }

    public Integer getIsAccepted() {
        return IsAccepted;
    }

    public void setIsAccepted(Integer isAccepted) {
        IsAccepted = isAccepted;
    }
}