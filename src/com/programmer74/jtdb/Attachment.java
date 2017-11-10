package com.programmer74.jtdb;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="attachments")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "attch_increment")
    @GenericGenerator(name = "attch_increment", strategy = "increment")
    private Integer id;

    @Column
    private String Type;

    @Column
    private Integer AttachmentID;

    @Override
    public String toString() {
        return "Attachment{" +
                "id=" + id +
                ", Type='" + Type + '\'' +
                ", AttachmentID=" + AttachmentID +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public Integer getAttachmentID() {
        return AttachmentID;
    }

    public void setAttachmentID(Integer attachmentID) {
        AttachmentID = attachmentID;
    }
}