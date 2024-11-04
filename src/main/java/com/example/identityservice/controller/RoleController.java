package com.example.identityservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<ApiResponse<RoleResponse>> createRole(@RequestBody @Valid CreateRoleRequest request) {
		RoleResponse newRole = roleService.create(request);
		ApiResponse<RoleResponse> response = ApiResponse.<RoleResponse>builder().code(1000).result(newRole).build();

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PreAuthorize("hasAuthority('READ_ROLE') || hasRole('ADMIN')")
	@GetMapping()
	public ResponseEntity<ApiResponse<List<RoleResponse>>> getRoles() {
		List<RoleResponse> roles = roleService.getAll();

		ApiResponse<List<RoleResponse>> response = ApiResponse.<List<RoleResponse>>builder().code(1000).result(roles)
				.build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
