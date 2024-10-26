package com.example.identityservice.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.identityservice.dto.request.AuthenticationRequest;
import com.example.identityservice.dto.response.AuthenticationResponse;
import com.example.identityservice.entity.User;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.exception.ErrorCode;
import com.example.identityservice.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationService {

	PasswordEncoder passwordEncoder;

	UserRepository userRepository;

	public AuthenticationResponse authenticateUser(AuthenticationRequest request) {
		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
		AuthenticationResponse authenticationResponse = new AuthenticationResponse();
		authenticationResponse.setAuthenticated(passwordEncoder.matches(request.getPassword(), user.getPassword()));
		
		return authenticationResponse;
	}
}
