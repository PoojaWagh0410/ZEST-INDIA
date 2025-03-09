package com.pooja.zest.EmployeeServic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.pooja.zest.entity.User;
import com.pooja.zest.mapper.UserMapper;
import com.pooja.zest.repository.UserRepository;
import com.pooja.zest.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@InjectMocks
	private UserService userService;

	private User user;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setId(1);
		user.setEmail("test@example.com");
		user.setPassword("password");
	}

	@Test
	void testRegisterUser() {
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encryptedPassword");
		when(userRepository.save(any(User.class))).thenReturn(user);

		User savedUser = userService.registerUser(user);

		assertNotNull(savedUser);
		assertEquals(user.getEmail(), savedUser.getEmail());
		verify(userRepository).save(any(User.class));
	}

	@Test
	void testFindUserById() {
		when(userRepository.findById(1)).thenReturn(Optional.of(user));

		Optional<User> foundUser = userService.findUserById(1);

		assertTrue(foundUser.isPresent());
		assertEquals(user.getEmail(), foundUser.get().getEmail());
	}

	@Test
	void testGetAllUsers() {
		List<User> users = Arrays.asList(user);
		when(userRepository.findAll()).thenReturn(users);

		List<User> result = userService.getAllUsers();

		assertEquals(1, result.size());
		assertEquals(user.getEmail(), result.get(0).getEmail());
	}

	@Test
	void testGetUserByEmail() {
		when(userRepository.findByEmail("test@example.com")).thenReturn(user);

		User foundUser = userService.getUserByEmail("test@example.com");

		assertNotNull(foundUser);
		assertEquals(user.getEmail(), foundUser.getEmail());
	}

	@Test
	void testUserLogin_Success() {
		UserMapper userMapper = new UserMapper();
		userMapper.setEmail("test@example.com");
		userMapper.setPassword("password");

		when(userRepository.findByEmail("test@example.com")).thenReturn(user);
		when(bCryptPasswordEncoder.matches("password", user.getPassword())).thenReturn(true);

		User loggedInUser = userService.userLogin(userMapper);

		assertNotNull(loggedInUser);
		assertEquals(user.getEmail(), loggedInUser.getEmail());
	}

	@Test
	void testUserLogin_Failure() {
		UserMapper userMapper = new UserMapper();
		userMapper.setEmail("test@example.com");
		userMapper.setPassword("wrongpassword");

		when(userRepository.findByEmail("test@example.com")).thenReturn(user);
		when(bCryptPasswordEncoder.matches("wrongpassword", user.getPassword())).thenReturn(false);

		User loggedInUser = userService.userLogin(userMapper);

		assertNull(loggedInUser);
	}
}
