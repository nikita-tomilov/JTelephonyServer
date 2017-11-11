package com.programmer74.jtdb;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.SourceType;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.sql.SQLException;
import java.util.List;

public class PicturesDAO {
    public List<Picture> getPictures() throws SQLException {
        List<Picture> Pictures=null;
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();

            Pictures=session.createCriteria(Picture.class).list();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }
        System.out.println(Pictures.isEmpty());
        return Pictures;
    }
    public void addPicture(Picture Picture) throws Exception {
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            session.save(Picture);
            session.getTransaction().commit();

            session.close();
            session = HibernateUtil.getSessionFactory().openSession();

            Picture = (Picture) (session.createCriteria(Picture.class).add(Restrictions.eq("SentBy", Picture.getSentBy())).addOrder(Order.desc("id")).list().get(0));

        }catch (Exception e){
            //e.printStackTrace();
            throw e;
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }
    }
    public void updatePicture(Picture Picture) throws Exception {
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            session.update(Picture);
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

    public Picture getPictureByID(Integer prid) throws SQLException {
        Picture prf=null;
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();

            prf = (Picture)(session.get(Picture.class, prid));

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
