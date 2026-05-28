package ru.aston.hometask.module2.dao;

import ru.aston.hometask.module2.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    List<User> findAll();
    User update(User user);
    void delete(Long id);
    Optional<User> findByEmail(String email);
}