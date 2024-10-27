package com.example.identityservice.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.identityservice.dto.request.CreateUserRequest;
import com.example.identityservice.dto.request.UpdateUserRequest;
import com.example.identityservice.dto.response.UserResponse;
import com.example.identityservice.entity.User;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.exception.ErrorCode;
import com.example.identityservice.mapper.UserMapper;
import com.example.identityservice.repository.RoleRepository;
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
	RoleRepository roleRepository;

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

	public List<UserResponse> getUsers() {
		List<User> users = userRepository.findAll();
		return users.stream().map(user -> userMapper.toUserResponse(user)).toList();
	}

	public UserResponse getUserInfo() {
		var context = SecurityContextHolder.getContext();
		String username = context.getAuthentication().getName();
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		return userMapper.toUserResponse(user);
	}

	public UserResponse updateUser(String id, UpdateUserRequest request) {
		User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		if (StringUtils.isNotBlank(request.getPassword())) {
			String encodedPassword = passwordEncoder.encode(request.getPassword());
			user.setPassword(encodedPassword);
		}

		if (!CollectionUtils.isEmpty(request.getRoles())) {
			var roles = roleRepository.findAllById(request.getRoles());
			var currentRoles = user.getRoles();

			roles.forEach(role -> {
				if (!currentRoles.contains(role)) {
					user.getRoles().add(role);
				}
			});
		}

		User updatedUser = userRepository.save(user);
		return userMapper.toUserResponse(updatedUser);
	}
}
