package com.example.identityservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import com.example.identityservice.dto.request.CreateRoleRequest;
import com.example.identityservice.dto.response.RoleResponse;
import com.example.identityservice.entity.Role;
import com.example.identityservice.mapper.RoleMapper;
import com.example.identityservice.repository.PermissionRepository;
import com.example.identityservice.repository.RoleRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class RoleServiceTest {
	@MockBean
	private RoleRepository roleRepository;

	@MockBean
	private PermissionRepository permissionRepository;

	@MockBean
	private RoleMapper roleMapper;

	@Autowired
	private RoleService roleService;

	private Role role;
	private CreateRoleRequest createRoleRequest;
	private RoleResponse roleResponse;

	@BeforeEach
	void initData() {
		this.createRoleRequest = new CreateRoleRequest();
		createRoleRequest.setName("ADMIN");
		createRoleRequest.setPermissions(Set.of("READ", "WRITE"));

		this.role = new Role();
		role.setName("ADMIN");
		role.setPermissions(new HashSet<>());

		this.roleResponse = new RoleResponse();
		roleResponse.setName("ADMIN");
	}

	@Test
	void createRole_validRequest_success() {
		Mockito.when(roleMapper.toRole(createRoleRequest)).thenReturn(role);
		Mockito.when(permissionRepository.findAllById(createRoleRequest.getPermissions()))
				.thenReturn(new ArrayList<>());
		Mockito.when(roleRepository.save(role)).thenReturn(role);
		Mockito.when(roleMapper.toRoleResponse(role)).thenReturn(roleResponse);

		RoleResponse response = roleService.create(createRoleRequest);
		assertNotNull(response);
		assertEquals("ADMIN", response.getName());
	}

	@Test
	void getAllRoles_validRequest_success() {
		Mockito.when(roleRepository.findAll()).thenReturn(List.of(role));
		Mockito.when(roleMapper.toRoleResponse(role)).thenReturn(roleResponse);

		List<RoleResponse> roles = roleService.getAll();
		assertNotNull(roles);
		assertFalse(roles.isEmpty());
	}

	@Test
	void deleteRoleById_validRequest_success() {
		Mockito.doNothing().when(roleRepository).deleteById("ADMIN");
		boolean result = roleService.deleteById("ADMIN");
		assertTrue(result);
	}

	@Test
	void deleteRoleById_invalidRequest_fail() {
		Mockito.doThrow(new RuntimeException("Role not found")).when(roleRepository).deleteById("INVALID_ROLE");

		boolean result = roleService.deleteById("INVALID_ROLE");
		assertFalse(result);
	}
}
