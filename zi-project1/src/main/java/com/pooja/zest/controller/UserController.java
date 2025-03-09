package com.pooja.zest.controller;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pooja.zest.entity.User;
import com.pooja.zest.mapper.UserMapper;
import com.pooja.zest.service.UserService;
import com.pooja.zest.utils.JwtUtil;

@RestController
@RequestMapping(path = "/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	@PostMapping(path = "/register")
	public ResponseEntity<Object> registerUser(@RequestBody User user) {
		User newUser = userService.registerUser(user);
		if (Objects.nonNull(newUser))
			return new ResponseEntity<>(newUser, HttpStatus.CREATED);
		else
			return new ResponseEntity<>("User not registered", HttpStatus.BAD_REQUEST);
	}

	@PostMapping(path = "/login")
	public ResponseEntity<Object> loginUser(@RequestBody UserMapper userMapper) {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(userMapper.getEmail(), userMapper.getPassword()));
			userDetailsService.loadUserByUsername(userMapper.getEmail());
			String token = jwtUtil.generateToken(userMapper.getEmail());
			return new ResponseEntity<Object>(token, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>("Incorrect Username or Password", HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(path = "/{id}")
	public ResponseEntity<Optional<User>> findUserById(@PathVariable int id) {
		Optional<User> user = userService.findUserById(id);
		if (user.isPresent()) {
			return new ResponseEntity<Optional<User>>(user, HttpStatus.FOUND);
		} else
			return new ResponseEntity<Optional<User>>(HttpStatus.NOT_FOUND);
	}

	@GetMapping(path = "/admin/all")
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping(path = "/email")
	public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
		User user = userService.getUserByEmail(email);
		if (Objects.nonNull(user)) {
			return new ResponseEntity<User>(user, HttpStatus.FOUND);
		} else
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
	}

}
