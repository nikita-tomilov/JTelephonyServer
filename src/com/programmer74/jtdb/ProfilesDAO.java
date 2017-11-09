package com.programmer74.jtdb;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.SourceType;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.sql.SQLException;
import java.util.List;

public class ProfilesDAO {
    public List<Profile> getProfiles() throws SQLException {
        List<Profile> Profiles=null;
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();

            Profiles=session.createCriteria(Profile.class).list();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }
        System.out.println(Profiles.isEmpty());
        return Profiles;
    }
    public void addProfile(Profile profile) throws Exception {
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            session.save(profile);
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
    public void updateProfile(Profile profile) throws Exception {
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            session.update(profile);
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
    public Profile getProfileByCredentialID(Integer crid) throws SQLException {
        Profile prf=null;
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();

            prf = (Profile)(session.createCriteria(Profile.class).add(Restrictions.eq("CredentialsID", crid)).list().get(0));

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
