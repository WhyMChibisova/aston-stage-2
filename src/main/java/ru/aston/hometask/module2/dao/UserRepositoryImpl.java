package ru.aston.hometask.module2.dao;

import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.aston.hometask.module2.entity.User;
import ru.aston.hometask.module2.util.HibernateSessionFactoryUtil;

import java.util.List;
import java.util.Optional;

@Slf4j
public class UserRepositoryImpl implements UserRepository {

    @Override
    public User save(User user) {
        Transaction transaction = null;
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            log.info("User created successfully with id: {}", user.getId());
            return user;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Error while creating user: {}", e.getMessage());
            throw new RuntimeException("Error while creating user: " + e.getMessage());
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            log.info("User with id {}: {}", id, user != null ? "found" : "unfound");
            return Optional.ofNullable(user);
        } catch (Exception e) {
            log.error("Error while finding user by id {}: {}", id, e.getMessage());
            throw new RuntimeException("Error while finding user by id: " + e.getMessage());
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            List<User> users = session.createQuery("FROM User", User.class).list();
            log.info("Received {} users", users.size());
            return users;
        } catch (Exception e) {
            log.error("Error while receiving all users: {}", e.getMessage());
            throw new RuntimeException("Error while receiving users: " + e.getMessage());
        }
    }

    @Override
    public User update(User user) {
        Transaction transaction = null;
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User updatedUser = session.merge(user);
            transaction.commit();
            log.info("User updated successfully with id: {}", user.getId());
            return updatedUser;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Error while updating user: {}", e.getMessage());
            throw new RuntimeException("Error while updating user: " + e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User deletedUser = session.get(User.class, id);
            if (deletedUser != null) {
                session.remove(deletedUser);
                transaction.commit();
                log.info("User deleted successfully with id: {}", deletedUser.getId());
            } else {
                transaction.rollback();
                log.warn("Attempted to delete non-existent user with id: {}", id);
                throw new RuntimeException("User with id " + id + " not found");
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Error while deleting user: {}", e.getMessage());
            throw new RuntimeException("Error while deleting user: " + e.getMessage());
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            User user = session.createQuery("FROM User WHERE email =: email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            log.info("User with email {}: {}", email, user != null ? "found" : "unfound");
            return Optional.ofNullable(user);
        } catch (NoResultException e) {
            log.warn("No user found with email: {}", email);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error while finding user by email {}: {}", email, e.getMessage());
            throw new RuntimeException("Error while finding user by email: " + e.getMessage());
        }
    }
}