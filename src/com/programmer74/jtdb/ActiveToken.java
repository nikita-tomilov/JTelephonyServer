package com.programmer74.jtdb;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="ActiveTokens")
public class ActiveToken implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "act_increment")
    @GenericGenerator(name = "act_increment", strategy = "increment")
    private Integer id;

    @Column(name="Token")
    @org.hibernate.annotations.Type(type = "com.programmer74.jtdb.TokenUserType")
    Token token;

    @Column
    Integer CredentialID;

    @Override
    public String toString() {
        return "ActiveToken{" +
                "id=" + id +
                ", token=" + token +
                ", CredentialID=" + CredentialID +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Integer getCredentialID() {
        return CredentialID;
    }

    public void setCredentialID(Integer credentialID) {
        CredentialID = credentialID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActiveToken that = (ActiveToken) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (token != null ? !token.equals(that.token) : that.token != null) return false;
        return CredentialID != null ? CredentialID.equals(that.CredentialID) : that.CredentialID == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + (CredentialID != null ? CredentialID.hashCode() : 0);
        return result;
    }
}