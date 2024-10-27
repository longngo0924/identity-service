package com.example.identityservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.identityservice.dto.request.CreatePermissionRequest;
import com.example.identityservice.dto.response.ApiResponse;
import com.example.identityservice.dto.response.PermissionResponse;
import com.example.identityservice.service.PermissionService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
	PermissionService permissionService;

	@PostMapping
	public ResponseEntity<ApiResponse<PermissionResponse>> createPermission(
			@RequestBody @Valid CreatePermissionRequest request) {
		PermissionResponse newPermission = permissionService.create(request);
		ApiResponse<PermissionResponse> response = ApiResponse.<PermissionResponse>builder().code(1000)
				.result(newPermission).build();

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}
