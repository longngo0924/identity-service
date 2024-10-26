package com.example.identityservice.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.identityservice.dto.request.CreateUserRequest;
import com.example.identityservice.dto.response.UserResponse;
import com.example.identityservice.entity.User;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.exception.ErrorCode;
import com.example.identityservice.mapper.UserMapper;
import com.example.identityservice.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

	UserRepository userRepository;
	UserMapper userMapper;
	PasswordEncoder passwordEncoder;

	public UserResponse createUser(CreateUserRequest request) {

		boolean isUsernameExisted = userRepository.existsByUsername(request.getUsername());
		boolean isEmailExisted = userRepository.existsByEmail(request.getEmail());

		if (isUsernameExisted || isEmailExisted) {
			throw new AppException(ErrorCode.USER_EXISTED);
		}

		String encodedPassword = passwordEncoder.encode(request.getPassword());
		request.setPassword(encodedPassword);

		User user = userMapper.toUser(request);
		User newUser = userRepository.save(user);

		return userMapper.toUserResponse(newUser);
	}

	public UserResponse getUserById(String id) {
		User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
		return userMapper.toUserResponse(user);
	}

	public boolean deleteUser(String id) {
		try {
			userRepository.deleteById(id);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return false;

	}
}
