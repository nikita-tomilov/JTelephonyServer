package com.programmer74.jtdb;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@org.hibernate.annotations.Cache(region="common", usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="attachments")
public class Attachment {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "attch_increment")
    @GenericGenerator(name = "attch_increment", strategy = "increment")
    private Integer id;

    @Column
    private String Type;

    @Column
    private Integer AttachmentID;

    @Column
    private Integer SentBy;

    @Override
    public String toString() {
        return "Attachment{" +
                "id=" + id +
                ", Type='" + Type + '\'' +
                ", AttachmentID=" + AttachmentID +
                ", SentBy=" + SentBy +
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

    public Integer getSentBy() {
        return SentBy;
    }

    public void setSentBy(Integer sentBy) {
        SentBy = sentBy;
    }
}