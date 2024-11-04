package com.example.identityservice.controller;

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

import com.example.identityservice.dto.request.CreateRoleRequest;
import com.example.identityservice.dto.response.RoleResponse;
import com.example.identityservice.service.RoleService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
class RoleControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private CreateRoleRequest createRoleRequest;
	private RoleResponse roleResponse;

	@MockBean
	private RoleService roleService;

	@BeforeEach
	void initDate() {
		this.createRoleRequest = CreateRoleRequest.builder().name("STAFF").description("Staff Role").build();
		this.roleResponse = RoleResponse.builder().name("STAFF").description("Staff Role").build();
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void createRole_validRequest_success() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		String content = objectMapper.writeValueAsString(createRoleRequest);

		Mockito.when(roleService.create(ArgumentMatchers.any())).thenReturn(roleResponse);

		mockMvc.perform(MockMvcRequestBuilders.post("/roles").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpectAll(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("result.name").value("STAFF"));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void getRoles_validRequest_success() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/roles").contentType(MediaType.APPLICATION_JSON))
				.andExpectAll(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("result").exists());
	}
}
