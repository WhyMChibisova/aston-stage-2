package ru.aston.hometask.module2.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import ru.aston.hometask.module2.entity.User;
import ru.aston.hometask.module2.util.HibernateSessionFactoryUtilTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer(
            DockerImageName.parse("postgres:15-alpine")
    )
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static SessionFactory sessionFactory;
    private UserRepository userRepository;
    private User testUser;

    @BeforeAll
    static void setUpAll() {
        sessionFactory = HibernateSessionFactoryUtilTest.getSessionFactory(postgres);
    }

    @AfterAll
    static void tearDownAll() {
        HibernateSessionFactoryUtilTest.shutdown();
    }

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryImpl(sessionFactory);
        testUser = User.builder()
                .name("Masha")
                .email("test@test.com")
                .age(23)
                .build();
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    private void cleanDatabase() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.createNativeMutationQuery("TRUNCATE TABLE users RESTART IDENTITY CASCADE").executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error while cleaning db: " + e.getMessage());
        }
    }

    @Test
    void save_whenDataIsValid() {
        User savedUser = userRepository.save(testUser);

        assertNotNull(savedUser.getId());
        assertEquals(testUser.getName(), savedUser.getName());
        assertEquals(testUser.getEmail(), savedUser.getEmail());
        assertEquals(testUser.getAge(), savedUser.getAge());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    void save_whenSameEmailExists_thanThrowException() {
        userRepository.save(testUser);
        User sameEmailUser = User.builder()
                .name("Dima")
                .email("test@test.com")
                .age(22)
                .build();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userRepository.save(sameEmailUser));
    }

    @Test
    void save_whenUserIsNull_thanThrowException() {
        assertThrows(RuntimeException.class, () -> userRepository.save(null));
    }

    @Test
    void save_whenNameIsInvalid_thanThrowException() {
        String overSizeName = "t".repeat(101);

        testUser.setName(overSizeName);
        assertThrows(RuntimeException.class, () -> userRepository.save(testUser));

        testUser.setName(null);
        assertThrows(RuntimeException.class, () -> userRepository.save(testUser));
    }

    @Test
    void save_whenEmailIsInvalid_thanThrowException() {
        String overSizeEmail = "@".repeat(51);

        testUser.setEmail(overSizeEmail);
        assertThrows(RuntimeException.class, () -> userRepository.save(testUser));

        testUser.setEmail(null);
        assertThrows(RuntimeException.class, () -> userRepository.save(testUser));
    }

    @Test
    void save_whenAgeIsInvalid_thanThrowException() {
        testUser.setAge(null);
        assertThrows(RuntimeException.class, () -> userRepository.save(testUser));
    }

    @Test
    void findById_whenUserExists() {
        User savedUser = userRepository.save(testUser);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getName(), foundUser.get().getName());
        assertEquals(savedUser.getEmail(), foundUser.get().getEmail());
        assertEquals(savedUser.getAge(), foundUser.get().getAge());
    }

    @Test
    void findById_whenUserNonExists_thanReturnEmpty() {
        Optional<User> foundUser = userRepository.findById(1L);

        assertTrue(foundUser.isEmpty());
    }

    @Test
    void findAll_whenUsersExist() {
        User testUser2 = User.builder()
                .name("Dima")
                .email("test2@test.com")
                .age(22)
                .build();
        userRepository.save(testUser);
        userRepository.save(testUser2);

        List<User> foundUsers = userRepository.findAll();

        assertEquals(2, foundUsers.size());

        assertEquals(testUser.getName(), foundUsers.get(0).getName());
        assertEquals(testUser.getEmail(), foundUsers.get(0).getEmail());
        assertEquals(testUser.getAge(), foundUsers.get(0).getAge());

        assertEquals(testUser2.getName(), foundUsers.get(1).getName());
        assertEquals(testUser2.getEmail(), foundUsers.get(1).getEmail());
        assertEquals(testUser2.getAge(), foundUsers.get(1).getAge());
    }

    @Test
    void findAll_whenUserNonExist_thenReturnEmpty() {
        List<User> foundUsers = userRepository.findAll();

        assertEquals(0, foundUsers.size());
    }

    @Test
    void update_whenUserIsValid() {
        User savedUser = userRepository.save(testUser);
        savedUser.setName("Dima");

        User updatedUser = userRepository.update(savedUser);

        assertEquals(savedUser.getId(), updatedUser.getId());
        assertEquals("Dima", updatedUser.getName());
        assertEquals(savedUser.getEmail(), updatedUser.getEmail());
        assertEquals(savedUser.getAge(), updatedUser.getAge());
        assertEquals(savedUser.getCreatedAt(), updatedUser.getCreatedAt());
    }

    @Test
    void update_whenUserNonExists_thanThrowException() {
        User savedUser = userRepository.save(testUser);
        savedUser.setId(2L);

        assertThrows(RuntimeException.class, () -> userRepository.update(savedUser));
    }

    @Test
    void delete_whenUserExists() {
        User savedUser = userRepository.save(testUser);
        Optional<User> foundUser1 = userRepository.findById(savedUser.getId());
        assertTrue(foundUser1.isPresent());

        userRepository.delete(savedUser.getId());
        Optional<User> foundUser2 = userRepository.findById(savedUser.getId());
        assertTrue(foundUser2.isEmpty());
    }

    @Test
    void delete_whenUserNonExists_thanThrowException() {
        assertThrows(RuntimeException.class, () -> userRepository.delete(1L));
    }

    @Test
    void findByEmail_whenUserExists() {
        User savedUser = userRepository.save(testUser);

        Optional<User> foundUser = userRepository.findByEmail(savedUser.getEmail());

        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals(savedUser.getName(), foundUser.get().getName());
        assertEquals(savedUser.getEmail(), foundUser.get().getEmail());
        assertEquals(savedUser.getAge(), foundUser.get().getAge());
    }

    @Test
    void findByEmail_whenUserNonExists_thanReturnEmpty() {
        Optional<User> foundUser = userRepository.findByEmail("t@test.com");

        assertTrue(foundUser.isEmpty());
    }
}