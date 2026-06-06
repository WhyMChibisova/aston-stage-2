package ru.aston.hometask.module2;

import lombok.extern.slf4j.Slf4j;
import ru.aston.hometask.module2.ui.UserMenu;
import ru.aston.hometask.module2.util.HibernateSessionFactoryUtil;

@Slf4j
public class Main {
    public static void main(String[] args) {
        log.info("Application started");

        try {
            new UserMenu().show();
        } catch (Exception e) {
            log.error("Application error: {}", e.getMessage());
            System.out.println("Application error: " + e.getMessage());
        } finally {
            HibernateSessionFactoryUtil.shutdown();
        }
    }
}