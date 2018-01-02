package com.programmer74.jtdb;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity
@org.hibernate.annotations.Cache(region="common", usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="ActiveTokens")
public class ActiveToken implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "act_increment")
    @GenericGenerator(name = "act_increment", strategy = "increment")
    private Integer id;

    @Column
    private String TokenString;

    @Column
    private Date ExpiresAt;

    @Column
    private Integer CredentialID;

    @Override
    public String toString() {
        return "ActiveToken{" +
                "id=" + id +
                ", TokenString='" + TokenString + '\'' +
                ", ExpiresAt=" + ExpiresAt +
                ", CredentialID=" + CredentialID +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTokenString() {
        return TokenString;
    }

    public void setTokenString(String tokenString) {
        TokenString = tokenString;
    }

    public Date getExpiresAt() {
        return ExpiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        ExpiresAt = expiresAt;
    }

    public Integer getCredentialID() {
        return CredentialID;
    }

    public void setCredentialID(Integer credentialID) {
        CredentialID = credentialID;
    }
}