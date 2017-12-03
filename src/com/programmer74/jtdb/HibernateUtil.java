package com.programmer74.jtdb;

import org.hibernate.SessionFactory;
import org.hibernate.cache.redis.SingletonRedisRegionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private  HibernateUtil(){}
    static{
        try {
            Configuration cfg = new Configuration()
                    .configure("hibernate.cfg.xml");

            cfg.getProperties().put(Environment.USE_SECOND_LEVEL_CACHE, true);
            cfg.getProperties().put(Environment.USE_QUERY_CACHE, true);
            cfg.getProperties().put(Environment.CACHE_REGION_FACTORY, SingletonRedisRegionFactory.class.getName());
            cfg.getProperties().put(Environment.CACHE_REGION_PREFIX, "hibernate");

            // optional setting for second level cache statistics
            //cfg.setProperty(Environment.GENERATE_STATISTICS, "true");
            cfg.setProperty(Environment.USE_STRUCTURED_CACHE, "true");

            cfg.setProperty(Environment.TRANSACTION_STRATEGY, JdbcTransactionFactory.class.getName());

            // configuration for Redis that used by hibernate
            cfg.getProperties().put(Environment.CACHE_PROVIDER_CONFIG, "hibernate-redis.properties");

            sessionFactory = cfg.buildSessionFactory();
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
