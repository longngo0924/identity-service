package com.example.identityservice.controller;

import java.text.ParseException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.identityservice.dto.request.AuthenticationRequest;
import com.example.identityservice.dto.request.IntrospectRequest;
import com.example.identityservice.dto.request.LogoutRequest;
import com.example.identityservice.dto.request.RefreshRequest;
import com.example.identityservice.dto.response.ApiResponse;
import com.example.identityservice.dto.response.AuthenticationResponse;
import com.example.identityservice.dto.response.IntrospectResponse;
import com.example.identityservice.dto.response.LogoutResponse;
import com.example.identityservice.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

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

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<LogoutResponse>> logout(@RequestBody LogoutRequest request)
			throws JOSEException, ParseException {
		LogoutResponse logoutResponse = authenticationService.logout(request);
		ApiResponse<LogoutResponse> response = ApiResponse.<LogoutResponse>builder().code(1000).result(logoutResponse)
				.build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<AuthenticationResponse>> logout(@RequestBody RefreshRequest request)
			throws JOSEException, ParseException {
		AuthenticationResponse authenticationResponse = authenticationService.refresh(request);
		ApiResponse<AuthenticationResponse> response = ApiResponse.<AuthenticationResponse>builder().code(1000)
				.result(authenticationResponse).build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/introspect")
	public ResponseEntity<ApiResponse<IntrospectResponse>> introspectToken(@RequestBody IntrospectRequest request)
			throws JOSEException, ParseException {
		IntrospectResponse introspectResponse = authenticationService.introspectToken(request);
		ApiResponse<IntrospectResponse> response = ApiResponse.<IntrospectResponse>builder().code(1000)
				.result(introspectResponse).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
