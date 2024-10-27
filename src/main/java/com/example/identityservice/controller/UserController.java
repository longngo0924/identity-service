package com.example.identityservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.identityservice.dto.request.CreateUserRequest;
import com.example.identityservice.dto.request.UpdateUserRequest;
import com.example.identityservice.dto.response.ApiResponse;
import com.example.identityservice.dto.response.UserResponse;
import com.example.identityservice.service.UserService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

	UserService userService;

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping()
	public ResponseEntity<ApiResponse<List<UserResponse>>> getUsers() {
		List<UserResponse> users = userService.getUsers();

		ApiResponse<List<UserResponse>> response = ApiResponse.<List<UserResponse>>builder().code(1000).result(users)
				.build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody @Valid CreateUserRequest request) {
		UserResponse newUser = userService.createUser(request);
		ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder().code(1000).result(newUser).build();

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable("id") String id) {
		UserResponse user = userService.getUserById(id);
		ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder().code(1000).result(user).build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable("id") String id,
			@RequestBody UpdateUserRequest request) {
		UserResponse user = userService.updateUser(id, request);
		ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder().code(1000).result(user).build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/user-info")
	public ResponseEntity<ApiResponse<UserResponse>> getUserInfo() {
		UserResponse user = userService.getUserInfo();
		ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder().code(1000).result(user).build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
