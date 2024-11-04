package com.example.identityservice.controller;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.identityservice.dto.request.CreatePermissionRequest;
import com.example.identityservice.dto.response.PermissionResponse;
import com.example.identityservice.service.PermissionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
class PermissionControllerTest {

	@MockBean
	private PermissionService permissionService;

	@Autowired
	private MockMvc mockMvc;

	private CreatePermissionRequest createPermissionRequest;

	private PermissionResponse permissionResponse;

	@BeforeEach
	void initData() {
		this.createPermissionRequest = CreatePermissionRequest.builder().name("CREATE_DATA")
				.description("Create Data Permission").build();

		this.permissionResponse = PermissionResponse.builder().name("CREATE_DATA").description("Create Data Permission")
				.build();
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void createPermission_validRequest_success() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		var content = objectMapper.writeValueAsString(createPermissionRequest);

		Mockito.when(permissionService.create(ArgumentMatchers.any())).thenReturn(permissionResponse);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/permissions").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("code").value("1000"));
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	void createPermission_invalidRequest_fail() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		var content = objectMapper.writeValueAsString(createPermissionRequest);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/permissions").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void getPermission_validRequest_success() throws Exception {

		Mockito.when(permissionService.getAll()).thenReturn(Arrays.asList(permissionResponse));

		mockMvc.perform(MockMvcRequestBuilders.get("/permissions")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("code").value("1000"))
				.andExpect(MockMvcResultMatchers.jsonPath("result").isNotEmpty());
	}
}
