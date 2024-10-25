package com.example.identityservice.service;

import org.springframework.stereotype.Service;

import com.example.identityservice.dto.request.CreateUserRequest;
import com.example.identityservice.dto.response.ApiResponse;
import com.example.identityservice.entity.User;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.exception.ErrorCode;
import com.example.identityservice.mapper.UserMapper;
import com.example.identityservice.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserService {

	UserRepository userRepository;
	UserMapper userMapper;

	public ApiResponse<User> createUser(CreateUserRequest request) {

		boolean isUsernameExisted = userRepository.existsByUsername(request.getUsername());
		boolean isEmailExisted = userRepository.existsByEmail(request.getEmail());
		
		if (isUsernameExisted || isEmailExisted) {
			throw new AppException(ErrorCode.USER_EXISTED);
		}

		User user = userMapper.toUser(request);
		User newUser = userRepository.save(user);
		ApiResponse<User> apiResponse = new ApiResponse<>();
		apiResponse.setCode(1000);
		apiResponse.setResult(newUser);
		return apiResponse;
	}

	public ApiResponse<User> getUserById(String id) {
		User user = userRepository.findById(id).get();
		ApiResponse<User> apiResponse = new ApiResponse<>();
		apiResponse.setCode(1000);
		apiResponse.setResult(user);
		return apiResponse;
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
