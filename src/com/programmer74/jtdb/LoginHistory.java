package com.programmer74.jtdb;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@org.hibernate.annotations.Cache(region="common", usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="loginhistory")
public class LoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator="lghist_increment")
    @GenericGenerator(name="lghist_increment", strategy = "increment")
    private Integer id;

    @Column
    private Integer CredentialID;
    @Column
    private Date PerformedAt;
    @Column
    private String State;

    public LoginHistory() {

    }

    public LoginHistory(Integer credentialID, Date performedAt, String state) {
        CredentialID = credentialID;
        PerformedAt = performedAt;
        State = state;
    }

    @Override
    public String toString() {
        return "LoginHistory{" +
                "id=" + id +
                ", CredentialID=" + CredentialID +
                ", PerformedAt=" + PerformedAt +
                ", State='" + State + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCredentialID() {
        return CredentialID;
    }

    public void setCredentialID(Integer credentialID) {
        CredentialID = credentialID;
    }

    public Date getPerformedAt() {
        return PerformedAt;
    }

    public void setPerformedAt(Date performedAt) {
        PerformedAt = performedAt;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }
}