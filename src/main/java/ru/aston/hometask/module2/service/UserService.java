package ru.aston.hometask.module2.service;

import ru.aston.hometask.module2.dao.UserRepository;
import ru.aston.hometask.module2.entity.User;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(String name, String email, Integer age) {
        validateUserData(name, email, age);

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with such email already exists");
        }

        User savedUser = User.builder()
                .name(name)
                .email(email)
                .age(age)
                .build();

        return userRepository.save(savedUser);
    }

    public Optional<User> findUserById(Long id) {
        validateId(id);
        return userRepository.findById(id);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, String name, String email, Integer age) {
        validateId(id);
        validateUserData(name, email, age);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));

        Optional<User> userWithEmail = userRepository.findByEmail(email);
        if (userWithEmail.isPresent() && !userWithEmail.get().getId().equals(id)) {
            throw new IllegalArgumentException("Email is already used by another user");
        }

        existingUser.setName(name);
        existingUser.setEmail(email);
        existingUser.setAge(age);

        return userRepository.update(existingUser);
    }

    public void deleteUser(Long id) {
        validateId(id);
        userRepository.delete(id);
    }

    private void validateUserData(String name, String email, Integer age) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name can't be empty");
        }
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (age == null || age < 0 || age > 150) {
            throw new IllegalArgumentException("Age must be between 0 and 150");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid user id");
        }
    }
}