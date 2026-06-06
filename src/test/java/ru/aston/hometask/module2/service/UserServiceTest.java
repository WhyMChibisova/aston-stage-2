package ru.aston.hometask.module2.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.hometask.module2.dao.UserRepository;
import ru.aston.hometask.module2.entity.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Masha")
                .email("test@test.com")
                .age(23)
                .build();
    }

    @Test
    void saveUser_whenDataIsValid() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User savedUser = userService.saveUser("Masha", "test@test.com", 23);

        assertNotNull(savedUser);
        assertEquals(testUser, savedUser);

        verify(userRepository, times(1)).findByEmail("test@test.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void saveUser_whenEmailAlreadyExists_thenThrowException() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.saveUser("Masha", "test@test.com", 23));
        assertEquals("User with such email already exists", exception.getMessage());

        verify(userRepository, times(1)).findByEmail("test@test.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "   ", "\t", "\n" })
    void saveUser_whenNameIsInvalid_thenThrowException(String invalidName) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.saveUser(invalidName, "test@test.com", 23));
        assertEquals("Name can't be empty", exception.getMessage());

        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "   ", "\t", "\n", "test" })
    void saveUser_whenEmailIsInvalid_thenThrowException(String invalidEmail) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.saveUser("Masha", invalidEmail, 23));
        assertEquals("Invalid email", exception.getMessage());

        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = { -1, 151 })
    void saveUser_whenAgeIsInvalid_thenThrowException(Integer invalidAge) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.saveUser("Masha", "test@test.com", invalidAge));
        assertEquals("Age must be between 0 and 150", exception.getMessage());

        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findUserById_whenUserExists_thenReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> foundUser = userService.findUserById(1L);

        assertTrue(foundUser.isPresent());
        assertEquals(foundUser.get(), testUser);

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void findUserById_whenUserNotExists_thenReturnEmpty() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.findUserById(1L);

        assertTrue(foundUser.isEmpty());

        verify(userRepository, times(1)).findById(1L);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = { -1, 0 })
    void findUserById_whenIdIsInvalid_thenThrowException(Long id) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.findUserById(id));
        assertEquals("Invalid user id", exception.getMessage());

        verify(userRepository, never()).findById(id);
    }

    @Test
    void findAllUsers_whenUsersExist() {
        List<User> users = List.of(
                testUser,
                User.builder().id(2L).name("Dima").email("test2@test.com").age(22).build()
        );
        when(userRepository.findAll()).thenReturn(users);

        List<User> actualUsers = userService.findAllUsers();

        assertEquals(2, actualUsers.size());
        assertEquals(users.get(0), actualUsers.get(0));
        assertEquals(users.get(1), actualUsers.get(1));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findAllUsers_whenUsersNotExist() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<User> actualUsers = userService.findAllUsers();

        assertEquals(0, actualUsers.size());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateUser_whenDataIsValid() {
        User updatedUser = User.builder()
                .id(1L)
                .name("Masha2")
                .email("test2@test.com")
                .age(23)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("test2@test.com")).thenReturn(Optional.empty());
        when(userRepository.update(any(User.class))).thenReturn(updatedUser);

        User actualUser = userService.updateUser(1L, "Masha2", "test2@test.com", 23);

        assertEquals(updatedUser, actualUser);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByEmail("test2@test.com");
        verify(userRepository, times(1)).update(any(User.class));
    }

    @Test
    void updateUser_whenSameEmails() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(userRepository.update(any(User.class))).thenReturn(testUser);

        User actualUser = userService.updateUser(1L, "Masha", "test@test.com", 25);

        assertEquals(25, actualUser.getAge());

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByEmail("test@test.com");
        verify(userRepository, times(1)).update(any(User.class));
    }

    @Test
    void updateUser_whenEmailBelongsToAnotherUser_thenThrowException() {
        User anotherUser = User.builder()
                .id(2L)
                .name("Masha2")
                .email("test2@test.com")
                .age(23)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("test2@test.com")).thenReturn(Optional.of(anotherUser));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(1L, "Masha", "test2@test.com", 25));
        assertEquals("Email is already used by another user", exception.getMessage());

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByEmail("test2@test.com");
        verify(userRepository, never()).update(any(User.class));
    }

    @Test
    void updateUser_whenUserNonExists_thenThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(1L, "Masha", "test2@test.com", 25));
        assertEquals("User with id 1 not found", exception.getMessage());

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).update(any(User.class));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = { -1, 0 })
    void updateUser_whenIdIsInvalid_thenThrowException(Long id) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(id, "Masha", "test2@test.com", 25));
        assertEquals("Invalid user id", exception.getMessage());

        verify(userRepository, never()).findById(1L);
        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).update(any(User.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "   ", "\t", "\n" })
    void updateUser_whenNameIsInvalid_thenThrowException(String invalidName) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(1L, invalidName, "test2@test.com", 25));
        assertEquals("Name can't be empty", exception.getMessage());

        verify(userRepository, never()).findById(1L);
        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).update(any(User.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "   ", "\t", "\n", "test" })
    void updateUser_whenEmailIsInvalid_thenThrowException(String invalidEmail) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(1L, "Masha", invalidEmail, 25));
        assertEquals("Invalid email", exception.getMessage());

        verify(userRepository, never()).findById(1L);
        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).update(any(User.class));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = { -1, 151 })
    void updateUser_whenAgeIsInvalid_thenThrowException(Integer invalidAge) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(1L, "Masha", "test2@test.com", invalidAge));
        assertEquals("Age must be between 0 and 150", exception.getMessage());

        verify(userRepository, never()).findById(1L);
        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).update(any(User.class));
    }

    @Test
    void deleteUser_whenUserExists() {
        doNothing().when(userRepository).delete(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(1L);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = { -1, 0 })
    void deleteUser_whenIdIsInvalid_thenThrowException(Long id) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.deleteUser(id));
        assertEquals("Invalid user id", exception.getMessage());

        verify(userRepository, never()).delete(any());
    }
}