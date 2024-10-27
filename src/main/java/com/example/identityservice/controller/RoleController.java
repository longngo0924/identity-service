package com.example.identityservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.identityservice.dto.request.CreateRoleRequest;
import com.example.identityservice.dto.response.ApiResponse;
import com.example.identityservice.dto.response.RoleResponse;
import com.example.identityservice.service.RoleService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
	RoleService roleService;

	@PostMapping
	public ResponseEntity<ApiResponse<RoleResponse>> createPermission(@RequestBody @Valid CreateRoleRequest request) {
		RoleResponse newRole = roleService.create(request);
		ApiResponse<RoleResponse> response = ApiResponse.<RoleResponse>builder().code(1000).result(newRole).build();

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}
