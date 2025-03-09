package com.pooja.zest.ControllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.pooja.zest.controller.UserController;
import com.pooja.zest.entity.User;
import com.pooja.zest.mapper.UserMapper;
import com.pooja.zest.service.UserService;
import com.pooja.zest.utils.JwtUtil;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

	@Mock
	private UserService userService;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private UserDetailsService userDetailsService;

	@Mock
	private JwtUtil jwtUtil;

	@InjectMocks
	private UserController userController;

	private User user;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setId(1);
		user.setEmail("pooja@gmail.com");
		user.setPassword("p@1234");
	}

	@Test
	void tesRegisterUserSucess() {
		when(userService.registerUser(any(User.class))).thenReturn(user);

		ResponseEntity<Object> registerUser = userController.registerUser(user);

		assertNotNull(registerUser);
		assertEquals(HttpStatus.CREATED, registerUser.getStatusCode());
		assertEquals(user, registerUser.getBody());

		verify(userService).registerUser(user);
	}

	@Test
	void testRegisterUserFailure() {
		when(userService.registerUser(any(User.class))).thenReturn(null);

		ResponseEntity<Object> registerUser = userController.registerUser(user);

		assertEquals(HttpStatus.BAD_REQUEST, registerUser.getStatusCode());
		assertEquals("User not registered", registerUser.getBody());
	}

	@Test
	void testLoginUserSucess() {
		UserMapper userMapper = new UserMapper();
		userMapper.setEmail("pooja@gmail.com");
		userMapper.setPassword("p@1234");

		when(jwtUtil.generateToken(userMapper.getEmail())).thenReturn("jwt-token");

		ResponseEntity<Object> response = userController.loginUser(userMapper);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("jwt-token", response.getBody());

		verify(authenticationManager).authenticate(any());
		verify(userDetailsService).loadUserByUsername(userMapper.getEmail());
		verify(jwtUtil).generateToken(userMapper.getEmail());
	}

	@Test
	void testLoginUserFailure() {
		UserMapper userMapper = new UserMapper();
		userMapper.setEmail("pooja@gmail.com");
		userMapper.setPassword("12345"); // wrong password

		when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Bad credentials"));

		ResponseEntity<Object> loginUser = userController.loginUser(userMapper);

		assertEquals(HttpStatus.BAD_REQUEST, loginUser.getStatusCode());
		assertEquals("Incorrect Username or Password", loginUser.getBody());
	}

	@Test
	void testFindUserByIdSucess() {
		when(userService.findUserById(1)).thenReturn(Optional.of(user));

		ResponseEntity<Optional<User>> userById = userController.findUserById(user.getId());

		assertNotNull(userById.getBody());
		assertEquals(HttpStatus.FOUND, userById.getStatusCode()); // Fixed status code
		assertEquals(user.getEmail(), userById.getBody().get().getEmail());

		verify(userService).findUserById(1);
	}

	@Test
	void testGetAllUsers() {
		 User user = new User(); // Create a User object
		    List<User> userList = Arrays.asList(user);
		    when(userService.getAllUsers()).thenReturn(userList);

		    List<User> allUsers = userController.getAllUsers();

		    assertNotNull(allUsers);
		    assertEquals(1, allUsers.size());
		    
		    verify(userService).getAllUsers();		
	}
	
	@Test
	void testGetUserByEmailSucess() {
		when(userService.getUserByEmail(user.getEmail())).thenReturn(user);
		
		ResponseEntity<User> userByEmail = userController.getUserByEmail(user.getEmail());
		
		assertNotNull(userByEmail);
		assertEquals(HttpStatus.FOUND, userByEmail.getStatusCode());
		assertEquals(user.getEmail(), userByEmail.getBody().getEmail());
		
		verify(userService).getUserByEmail(user.getEmail());
	}
	
	@Test
	void testGetUserByEmailFailure() {
		when(userService.getUserByEmail(user.getEmail())).thenReturn(null);
		
		ResponseEntity<User> userByEmail = userController.getUserByEmail(user.getEmail());
		
		assertEquals(HttpStatus.NOT_FOUND, userByEmail.getStatusCode());
		assertNull(userByEmail.getBody());
		
		verify(userService).getUserByEmail(user.getEmail());
	}
}




























