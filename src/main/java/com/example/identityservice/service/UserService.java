package com.example.identityservice.service;

import org.springframework.stereotype.Service;

import com.example.identityservice.entity.User;
import com.example.identityservice.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User createUser(User user) {
		return userRepository.save(user);
	}

	public User getUserById(String id) {
		return userRepository.findById(id).get();
	}

	public boolean deleteUser(String id) {
		try {
			userRepository.deleteById(id);
			return true;
		} catch (Exception e) {
			System.out.println(e);
		}
		return false;

	}
}
