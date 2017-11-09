package com.programmer74.jtdb;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name="profiles")
public class Profile {
    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", FirstName='" + FirstName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", City='" + City + '\'' +
                ", PictureID=" + PictureID +
                ", Status='" + Status + '\'' +
                ", CredentialsID=" + CredentialsID +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public Integer getPictureID() {
        return PictureID;
    }

    public void setPictureID(Integer pictureID) {
        PictureID = pictureID;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Integer getCredentialsID() {
        return CredentialsID;
    }

    public void setCredentialsID(Integer credentialsID) {
        CredentialsID = credentialsID;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator="prf_increment")
    @GenericGenerator(name="prf_increment", strategy = "increment")
    @Column
    private Integer id;

    @Column
    private String FirstName;
    @Column
    private String LastName;
    @Column
    private String City;
    @Column
    private Integer PictureID;
    @Column
    private String Status;
    @Column
    private Integer CredentialsID;
}
