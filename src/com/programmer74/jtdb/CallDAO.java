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

public class CallDAO {

    public List<Call> getAllCalls() throws SQLException {
        List<Call> Call=null;
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();

            Call=session.createCriteria(Call.class).list();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }
        System.out.println(Call.isEmpty());
        return Call;
    }

    public void addCall(Call call) throws Exception {
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            session.save(call);
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

    public void updateCall(Call call) throws Exception {
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            session.update(call);
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

    public List<Call> getCallForPerson(Integer profileId) throws SQLException {
        List<Call> Calls=null;
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();

            Criterion oneSide = Restrictions.eq("FromID", profileId);
            Criterion anotherSide = Restrictions.eq("ToID", profileId);

            Calls=session.createCriteria(Call.class).add(Restrictions.or(oneSide, anotherSide)).list();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }

        return Calls;
    }
}

