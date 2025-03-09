package com.pooja.zest.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pooja.zest.entity.User;
import com.pooja.zest.mapper.UserMapper;
import com.pooja.zest.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User registerUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	public Optional<User> findUserById(int id) {
		return userRepository.findById(id);
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public User userLogin(UserMapper userMapper) {
		User user = userRepository.findByEmail(userMapper.getEmail());

		if (Objects.nonNull(user) && passwordEncoder.matches(userMapper.getPassword(), user.getPassword())) {
			return user;
		}
		return null;
	}
}
