package com.example.identityservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.identityservice.dto.request.CreateRoleRequest;
import com.example.identityservice.dto.response.RoleResponse;
import com.example.identityservice.entity.Role;
import com.example.identityservice.mapper.RoleMapper;
import com.example.identityservice.repository.RoleRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class RoleService {
	RoleRepository roleRepository;
	RoleMapper roleMapper;

	public RoleResponse create(CreateRoleRequest request) {
		Role role = roleMapper.toRole(request);
		var newRole = roleRepository.save(role);
		return roleMapper.toRoleResponse(newRole);
	}

	public List<RoleResponse> getAll() {
		List<Role> roles = roleRepository.findAll();

		return roles.stream().map(role -> roleMapper.toRoleResponse(role)).toList();

	}

	public boolean deleteById(String name) {
		try {
			roleRepository.deleteById(name);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return false;
	}

}
