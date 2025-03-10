package com.pooja.zest.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pooja.zest.entity.User;
import com.pooja.zest.mapper.UserMapper;
import com.pooja.zest.repository.UserRepository;
import com.pooja.zest.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setEmail("test@gmail.com");
        user.setPassword("password123");
    }

    @Test
    void testRegisterUser() {
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User registeredUser = userService.registerUser(user);

        assertNotNull(registeredUser);
        assertEquals(user.getEmail(), registeredUser.getEmail());
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(user);
    }

    @Test
    void testFindUserById() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findUserById(1);

        assertTrue(foundUser.isPresent());
        assertEquals(user.getEmail(), foundUser.get().getEmail());
        verify(userRepository).findById(1);
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> allUsers = userService.getAllUsers();

        assertEquals(1, allUsers.size());
        assertEquals(user.getEmail(), allUsers.get(0).getEmail());
        verify(userRepository).findAll();
    }

    @Test
    void testGetUserByEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        User foundUser = userService.getUserByEmail("test@example.com");

        assertNotNull(foundUser);
        assertEquals(user.getEmail(), foundUser.getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void testUserLoginSuccess() {
        UserMapper userMapper = new UserMapper();
        userMapper.setEmail("test@example.com");
        userMapper.setPassword("password123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);

        User loggedInUser = userService.userLogin(userMapper);

        assertNotNull(loggedInUser);
        assertEquals(user.getEmail(), loggedInUser.getEmail());
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", user.getPassword());
    }

    @Test
    void testUserLoginFailure() {
        UserMapper userMapper = new UserMapper();
        userMapper.setEmail("test@example.com");
        userMapper.setPassword("wrongPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", user.getPassword())).thenReturn(false);

        User loggedInUser = userService.userLogin(userMapper);

        assertNull(loggedInUser);
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("wrongPassword", user.getPassword());
    }
}
