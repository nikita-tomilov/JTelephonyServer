package com.programmer74.jtdb;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.SourceType;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.sql.SQLException;
import java.util.List;

public class ContactDAO {
    public List<Contact> getAllContact() throws SQLException {
        List<Contact> Contact=null;
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();

            Contact=session.createCriteria(Contact.class).list();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }
        System.out.println(Contact.isEmpty());
        return Contact;
    }

    public void addContact(Contact Contact) throws Exception {
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            session.save(Contact);
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

    public List<Contact> getAllContactsForProfile(Integer profileId) throws SQLException {
        List<Contact> Contacts=null;
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();

            Criterion oneSide = Restrictions.eq("FromID", profileId);
            Criterion anotherSide = Restrictions.eq("ToID", profileId);

            Contacts=session.createCriteria(Contact.class).add(Restrictions.or(oneSide, anotherSide))
                    .list();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }

        return Contacts;
    }

    public List<Contact> getApprovedContactsForProfile(Integer profileId) throws SQLException {
        List<Contact> Contacts=null;
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();

            Criterion oneSide = Restrictions.eq("FromID", profileId);
            Criterion anotherSide = Restrictions.eq("ToID", profileId);

            Contacts=session.createCriteria(Contact.class).add(Restrictions.or(oneSide, anotherSide))
                    .add(Restrictions.eq("IsAccepted", 1))
                    .list();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }

        return Contacts;
    }
}
