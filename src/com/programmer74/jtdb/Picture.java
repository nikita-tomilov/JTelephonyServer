package com.programmer74.jtdb;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;

@Entity
@org.hibernate.annotations.Cache(region="common", usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="Pictures")
public class Picture {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "pic_increment")
    @GenericGenerator(name = "pic_increment", strategy = "increment")
    private Integer id;

    @Column
    private byte[] Data;

    @Column
    private Integer SentBy;

    @Override
    public String toString() {
        return "Picture{" +
                "id=" + id +
                ", Data=" + Arrays.toString(Data) +
                ", SentBy=" + SentBy +
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

    public Integer getSentBy() {
        return SentBy;
    }

    public void setSentBy(Integer sentBy) {
        SentBy = sentBy;
    }
}