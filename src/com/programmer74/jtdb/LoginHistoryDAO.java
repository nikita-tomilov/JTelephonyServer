package com.programmer74.jtdb;

import com.programmer74.jtelephony.Main;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoginHistoryDAO {

    public List<LoginHistory> getAllLoginHistory() throws SQLException {
        List<LoginHistory> LoginHistory=null;
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();

            LoginHistory=session.createCriteria(LoginHistory.class).list();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }
        System.out.println(LoginHistory.isEmpty());
        return LoginHistory;
    }

    public void addLoginHistory(LoginHistory lh) throws Exception {
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            session.save(lh);
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

    public List<LoginHistory> getLoginHistoryForPerson(Integer cid) throws SQLException {
        List<LoginHistory> LoginHistory=null;
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();

            LoginHistory=session.createCriteria(LoginHistory.class).add(Restrictions.eq("CredentialID", cid)).list();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }

        return LoginHistory;
    }
}

