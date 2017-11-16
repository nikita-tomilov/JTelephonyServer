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
            if (at.getToken().getTokenString().equals(tokenString)) return at;
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

            session.doWork(new Work() {
                public void execute(Connection connection) throws SQLException {
                    CallableStatement call = connection.prepareCall("{ ? = call AdvancedOperations.register_token(?,?,?) }");
                    //call.registerOutParameter( 1, Types.INTEGER ); // or whatever it is
                    //call.setLong(2, id);
                    //call.setLong(3, transId);

                    /*call.registerOutParameter("TokenString", OracleType.VARCHAR2);
                    call.registerOutParameter("TokenCredentialID", OracleType.NUMBER);
                    call.registerOutParameter("ExpiresAt", OracleType.TIMESTAMP);

                    call.setString("TokenString", tokenString);
                    call.setInt("TokenCredentialID", credentialID);
                    call.setDate("ExpiresAt",  new java.sql.Date(expiresAt.getTime()));*/
                    call.registerOutParameter( 1, Types.INTEGER);
                    //call.registerOutParameter(2, OracleType.VARCHAR2);
                    //call.registerOutParameter(3, OracleType.NUMBER);
                    //call.registerOutParameter(4, OracleType.TIMESTAMP);

                    call.setString(2, tokenString);
                    call.setInt(3, credentialID);
                    call.setDate(4,  new java.sql.Date(expiresAt.getTime()));
                    call.execute();
                    Integer result = call.getInt(1);
                    tokenId = result;
                }
            });

        }catch (Exception e){
            //e.printStackTrace();
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
