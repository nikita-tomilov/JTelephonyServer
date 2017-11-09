package com.programmer74.jtdb;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="calls")
public class Call {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator="call_increment")
    @GenericGenerator(name="call_increment", strategy = "increment")
    private Integer id;

    @Column
    private Integer FromID;
    @Column
    private Integer ToID;
    @Column
    private Date Began;
    @Column
    private Date Finished;

    public Call() {

    }

    public Call(Integer fromID, Integer toID, Date began) {
        FromID = fromID;
        ToID = toID;
        Began = began;
        Finished = null;
    }

    @Override
    public String toString() {
        return "Call{" +
                "id=" + id +
                ", FromID=" + FromID +
                ", ToID=" + ToID +
                ", Began=" + Began +
                ", Finished=" + Finished +
                '}';
    }

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

    public Date getBegan() {
        return Began;
    }

    public void setBegan(Date began) {
        Began = began;
    }

    public Date getFinished() {
        return Finished;
    }

    public void setFinished(Date finished) {
        Finished = finished;
    }
}