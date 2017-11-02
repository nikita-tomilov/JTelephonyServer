package com.programmer74.jtdb;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.SourceType;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.sql.SQLException;
import java.util.List;

public class CredentialsDAO {
    public List<Credential> getCredentials() throws SQLException {
        List<Credential> Credentials=null;
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();
            //session.flush();
            //Credentials=session.createCriteria(Credential.class).addOrder(Order.desc("added_date")).list();
            Credentials=session.createCriteria(Credential.class).list();
            //Credentials=session.createCriteria(Credential.class).addOrder(Order.desc("added_date")).list();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }
        return Credentials;
    }
}
