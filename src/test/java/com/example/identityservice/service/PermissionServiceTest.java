package com.example.identityservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import com.example.identityservice.dto.request.CreatePermissionRequest;
import com.example.identityservice.dto.response.PermissionResponse;
import com.example.identityservice.entity.Permission;
import com.example.identityservice.mapper.PermissionMapper;
import com.example.identityservice.repository.PermissionRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
class PermissionServiceTest {

	@MockBean
	private PermissionRepository permissionRepository;

	@MockBean
	private PermissionMapper permissionMapper;

	@Autowired
	private PermissionService permissionService;

	private Permission permission;
	private CreatePermissionRequest createPermissionRequest;
	private PermissionResponse permissionResponse;

	@BeforeEach
	void initData() {
		this.createPermissionRequest = new CreatePermissionRequest();
		createPermissionRequest.setName("READ_ACCESS");
		createPermissionRequest.setDescription("Read access permission");

		this.permission = new Permission();
		permission.setName("READ_ACCESS");
		permission.setDescription("Read access permission");

		this.permissionResponse = new PermissionResponse();
		permissionResponse.setName("READ_ACCESS");
		permissionResponse.setDescription("Read access permission");
	}

	@Test
	void createPermission_validRequest_success() {
		Mockito.when(permissionMapper.toPermission(createPermissionRequest)).thenReturn(permission);
		Mockito.when(permissionRepository.save(permission)).thenReturn(permission);
		Mockito.when(permissionMapper.toPermissionResponse(permission)).thenReturn(permissionResponse);

		PermissionResponse response = permissionService.create(createPermissionRequest);
		assertNotNull(response);
		assertEquals("READ_ACCESS", response.getName());
	}

	@Test
	void getAllPermissions_validRequest_success() {
		Mockito.when(permissionRepository.findAll()).thenReturn(List.of(permission));
		Mockito.when(permissionMapper.toPermissionResponse(permission)).thenReturn(permissionResponse);

		List<PermissionResponse> permissions = permissionService.getAll();
		assertFalse(permissions.isEmpty());
	}

	@Test
	void deletePermissionById_validRequest_success() {
		Mockito.doNothing().when(permissionRepository).deleteById("READ_ACCESS");
		boolean result = permissionService.deleteById("READ_ACCESS");
		assertTrue(result);
	}

	@Test
	void deletePermissionById_invalidRequest_fail() {
		Mockito.doThrow(new RuntimeException("Permission not found")).when(permissionRepository)
				.deleteById("INVALID_PERMISSION");

		boolean result = permissionService.deleteById("INVALID_PERMISSION");
		assertFalse(result);
	}
}
