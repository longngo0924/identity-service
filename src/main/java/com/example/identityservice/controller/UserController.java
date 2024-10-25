package com.example.identityservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.identityservice.dto.request.CreateUserRequest;
import com.example.identityservice.dto.response.ApiResponse;
import com.example.identityservice.entity.User;
import com.example.identityservice.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<User>> createUser(@RequestBody CreateUserRequest request) {
		ApiResponse<User> newUser = userService.createUser(request);
		return new ResponseEntity<>(newUser, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable("id") String id) {
		ApiResponse<User> user = userService.getUserById(id);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
}
