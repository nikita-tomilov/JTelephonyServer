package com.programmer74.jtdb;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;

@Entity
@Table(name="Pictures")
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "pic_increment")
    @GenericGenerator(name = "pic_increment", strategy = "increment")
    private Integer id;

    @Column
    private byte[] Data;

    @Override
    public String toString() {
        return "Picture{" +
                "id=" + id +
                ", Data: " + (Data.length) +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public byte[] getData() {
        return Data;
    }

    public void setData(byte[] data) {
        Data = data;
    }
}