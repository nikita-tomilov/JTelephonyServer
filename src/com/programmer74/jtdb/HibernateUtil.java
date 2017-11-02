package com.programmer74.jtdb;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private  HibernateUtil(){}
    static{
        try {
            sessionFactory = new Configuration()
                    .configure("hibernate.cfg.xml") // configures settings from hibernate.cfg.xml
                    .buildSessionFactory();
        }
        catch (Exception e){
            System.out.println("!!!");
            e.printStackTrace();
            System.out.println("!!!");
        }
    }
    public  static SessionFactory getSessionFactory(){
        return  sessionFactory;
    }
}
