package ru.aston.hometask.module2.ui;

import lombok.extern.slf4j.Slf4j;
import ru.aston.hometask.module2.entity.User;
import ru.aston.hometask.module2.service.UserService;

import java.util.List;
import java.util.Optional;

@Slf4j
public class UserMenu extends Menu {
    private static final String CRUD_MENU_MSG = """
            --- MENU ---
            1. Create user
            2. Find user by id
            3. Get all users
            4. Update user
            5. Delete user
            6. Exit
            """;

    private final UserService userService;

    public UserMenu() {
        this.userService = new UserService();
    }

    @Override
    public void show() {
        while (true) {
            int choice = readInt(CRUD_MENU_MSG);
            try {
                switch (choice) {
                    case 1 -> createUser();
                    case 2 -> findUserById();
                    case 3 -> findAllUsers();
                    case 4 -> updateUser();
                    case 5 -> deleteUser();
                    case 6 -> {
                        log.info("Application completed");
                        return;
                    }
                    default -> System.out.println("Unknown action! Try again");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                log.error("Operation failed: {}", e.getMessage());
            }
        }
    }

    private void createUser() {
        String name = readString("Enter name:");
        String email = readString("Enter email:");
        Integer age = readInt("Enter age: ");

        User user = userService.saveUser(name, email, age);
        System.out.println("User created successfully with id: " + user.getId());
    }

    private void findUserById() {
        Long id = readLong("Enter user id: ");

        Optional<User> user = userService.findUserById(id);
        if (user.isPresent()) {
            System.out.println(user.get());
        } else {
            System.out.println("User not found");
        }
    }

    private void findAllUsers() {
        List<User> users = userService.findAllUsers();

        if (users.isEmpty()) {
            System.out.println("Users not found");
        } else {
            users.forEach(System.out::println);
        }
    }

    private void updateUser() {
        Long id = readLong("Enter user id: ");
        String name = readString("Enter name:");
        String email = readString("Enter email:");
        Integer age = readInt("Enter age: ");

        userService.updateUser(id, name, email, age);
        System.out.println("User updated successfully");
    }

    private void deleteUser() {
        Long id = readLong("Enter user id: ");
        userService.deleteUser(id);
        System.out.println("User deleted successfully");
    }
}