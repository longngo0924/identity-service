package com.example.identityservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.identityservice.dto.request.AuthenticationRequest;
import com.example.identityservice.dto.response.ApiResponse;
import com.example.identityservice.dto.response.AuthenticationResponse;
import com.example.identityservice.service.AuthenticationService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
	AuthenticationService authenticationService;

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticateUser(
			@RequestBody AuthenticationRequest request) {
		AuthenticationResponse authenticationResponse = authenticationService.authenticateUser(request);
		ApiResponse<AuthenticationResponse> response = ApiResponse.<AuthenticationResponse>builder().code(1000)
				.result(authenticationResponse).build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
