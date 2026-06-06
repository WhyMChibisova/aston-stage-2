package ru.aston.hometask.module2.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testcontainers.postgresql.PostgreSQLContainer;
import ru.aston.hometask.module2.entity.User;

public class HibernateSessionFactoryUtilTest {
    private static volatile SessionFactory sessionFactory;

    private HibernateSessionFactoryUtilTest() {}

    public static SessionFactory getSessionFactory(PostgreSQLContainer postgres) {
        if (sessionFactory == null) {
            synchronized (HibernateSessionFactoryUtilTest.class) {
                if (sessionFactory == null) {
                    try {
                        Configuration configuration = new Configuration();

                        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
                        configuration.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
                        configuration.setProperty("hibernate.connection.username", postgres.getUsername());
                        configuration.setProperty("hibernate.connection.password", postgres.getPassword());
                        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
                        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
                        configuration.setProperty("hibernate.show_sql", "true");
                        configuration.setProperty("hibernate.format_sql", "true");

                        configuration.addAnnotatedClass(User.class);

                        sessionFactory = configuration.buildSessionFactory();
                        System.out.println("Test SessionFactory created successfully");
                    } catch (Exception e) {
                        throw new RuntimeException("Hibernate initialization failed: " + e.getMessage());
                    }
                }
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            System.out.println("Test SessionFactory closed");
        }
    }
}