package com.programmer74.jtdb;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.annotations.SourceType;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.sql.SQLException;
import java.util.List;

public class DocumentsDAO {
    public List<Document> getDocuments() throws SQLException {
        List<Document> Documents=null;
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();

            Documents=session.createCriteria(Document.class).list();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(session!=null&&session.isOpen()){
                session.close();
            }
        }
        System.out.println(Documents.isEmpty());
        return Documents;
    }
    public void addDocument(Document Document) throws Exception {
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            session.save(Document);
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
    public void updateDocument(Document Document) throws Exception {
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            session.update(Document);
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

    public Document getDocumentByID(Integer prid) throws SQLException {
        Document prf=null;
        Session session=null;
        try{
            session= HibernateUtil.getSessionFactory().openSession();

            prf = (Document)(session.get(Document.class, prid));

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
