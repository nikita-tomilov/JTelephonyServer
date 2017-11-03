package com.programmer74.jtdb;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.SourceType;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.sql.SQLException;
import java.util.List;

public class MessagesDAO {
    public List<Message> getAllMessages() throws SQLException {
        List<Message> Messages=null;
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();

            Messages=session.createCriteria(Message.class).list();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }
        System.out.println(Messages.isEmpty());
        return Messages;
    }

    public void addMessage(Message Message) throws Exception {
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            session.save(Message);
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

    public List<Message> getAllMessagesInDialog(Integer person1, Integer person2, Integer offset, Integer count) throws SQLException {
        List<Message> Messages=null;
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();

            Criterion oneSide= Restrictions.and(Restrictions.eq("FromID", person1),
                                                Restrictions.eq("ToID", person2));
            Criterion anotherSide= Restrictions.and(Restrictions.eq("FromID", person2),
                    Restrictions.eq("ToID", person1));

            Messages=session.createCriteria(Message.class).add(Restrictions.or(oneSide, anotherSide))
                    .addOrder(Order.desc("SentAt"))
                    .setFirstResult(offset).setMaxResults(count).list();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }

        return Messages;
    }
}
