package com.programmer74.jtdb;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.SourceType;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.sql.SQLException;
import java.util.List;

public class AttachmentsDAO {
    public List<Attachment> getAttachments() throws SQLException {
        List<Attachment> Attachments=null;
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();

            Attachments=session.createCriteria(Attachment.class).list();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }
        System.out.println(Attachments.isEmpty());
        return Attachments;
    }
    public void addAttachment(Attachment Attachment) throws Exception {
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            session.save(Attachment);
            session.getTransaction().commit();
            //session.flush();

            session.close();
            session = HibernateUtil.getSessionFactory().openSession();
            Attachment = (Attachment) (session.createCriteria(Attachment.class).add(Restrictions.eq("SentBy", Attachment.getSentBy())).addOrder(Order.desc("id")).list().get(0));

        }catch (Exception e){
            //e.printStackTrace();
            throw e;
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }
    }
    public void updateAttachment(Attachment Attachment) throws Exception {
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            session.update(Attachment);
            session.getTransaction().commit();
        }catch (Exception e){
            //e.printStackTrace();
            throw e;
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }
    }

    public Attachment getAttachmentByID(Integer prid) throws SQLException {
        Attachment prf=null;
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();

            prf = (Attachment)(session.get(Attachment.class, prid));

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }
        return prf;
    }
}
