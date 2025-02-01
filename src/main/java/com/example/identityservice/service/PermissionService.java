package com.example.identityservice.service;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.example.identityservice.dto.request.CreatePermissionRequest;
import com.example.identityservice.dto.response.PermissionResponse;
import com.example.identityservice.entity.Permission;
import com.example.identityservice.mapper.PermissionMapper;
import com.example.identityservice.repository.PermissionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class PermissionService {
	PermissionRepository permissionRepository;
	PermissionMapper permissionMapper;

	public PermissionResponse create(CreatePermissionRequest request) {
		Permission permission = permissionMapper.toPermission(request);
		var newPermission = permissionRepository.save(permission);
		return permissionMapper.toPermissionResponse(newPermission);
	}

	public List<PermissionResponse> getAll() {
		List<Permission> permissions = permissionRepository.findAll();

		Stream<Permission> stream = permissions.stream();

		return stream.map(permission -> permissionMapper.toPermissionResponse(permission)).toList();

	}

	public boolean deleteById(String name) {
		try {
			permissionRepository.deleteById(name);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return false;
	}
}
