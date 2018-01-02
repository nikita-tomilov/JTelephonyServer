package com.programmer74.jtdb;

import oracle.jdbc.OracleType;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.SourceType;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

public class ActiveTokenDAO {
    public List<ActiveToken> getActiveTokens() throws SQLException {
        List<ActiveToken> ActiveTokens=null;
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();

            ActiveTokens=session.createCriteria(ActiveToken.class).list();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }
        return ActiveTokens;
    }

    public ActiveToken getTokenByTokenString(String tokenString) throws SQLException {
        List<ActiveToken> activeTokens = getActiveTokens();
        for (ActiveToken at : activeTokens) {
            if (at.getTokenString().equals(tokenString)) return at;
        }
        return null;
    }

    public void removeTokenByTokenString(String tokenString) throws SQLException {
        ActiveToken at=null;
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();
            at = getTokenByTokenString(tokenString);
            if (at == null) return;
            session.delete(at);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }
    }

    public void addActiveToken(ActiveToken ActiveToken) throws Exception {
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            session.save(ActiveToken);
            session.getTransaction().commit();
            //session.flush();

        }catch (Exception e){
            //e.printStackTrace();
            throw e;
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }
    }
    private Integer tokenId = null;

    public Integer registerActiveToken(String tokenString, Integer credentialID, Date expiresAt) throws Exception {
        Session session=null;

        try{
            session= HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            ActiveToken at = new ActiveToken();
            at.setCredentialID(credentialID);
            at.setTokenString(tokenString);
            at.setExpiresAt(expiresAt);
            session.save(at);
            session.getTransaction().commit();
            session.close();
            session = HibernateUtil.getSessionFactory().openSession();

            at = (ActiveToken) (session.createCriteria(ActiveToken.class).add(Restrictions.eq("TokenString", tokenString)).list().get(0));
            tokenId = at.getId();

        } catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }
        return tokenId;
    }

    public void updateActiveToken(ActiveToken ActiveToken) throws Exception {
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            session.update(ActiveToken);
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

    public ActiveToken getActiveTokenByID(Integer prid) throws SQLException {
        ActiveToken prf=null;
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();

            prf = (ActiveToken)(session.get(ActiveToken.class, prid));

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
