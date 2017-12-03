package com.programmer74.jtdb;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="messages")
@org.hibernate.annotations.Cache(region="common", usage = CacheConcurrencyStrategy.READ_WRITE)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator="msg_increment")
    @GenericGenerator(name="msg_increment", strategy = "increment")
    private Integer id;

    @Column
    private Integer FromID;
    @Column
    private Integer ToID;
    @Column
    private String Message;
    @Column
    private Date SentAt;
    @Column
    private Integer Attachment;

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", FromID=" + FromID +
                ", ToID=" + ToID +
                ", Message='" + Message + '\'' +
                ", SentAt=" + SentAt +
                ", Attachment=" + Attachment +
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

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Date getSentAt() {
        return SentAt;
    }

    public void setSentAt(Date sentAt) {
        SentAt = sentAt;
    }

    public Integer getAttachment() {
        return Attachment;
    }

    public void setAttachment(Integer attachment) {
        Attachment = attachment;
    }
}
