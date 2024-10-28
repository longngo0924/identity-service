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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.hamcrest.Matchers.*;

import com.example.identityservice.dto.request.CreateUserRequest;
import com.example.identityservice.dto.request.UpdateUserRequest;
import com.example.identityservice.dto.response.UserResponse;
import com.example.identityservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	private CreateUserRequest createUserRequest;
	private UserResponse userResponse;
	private UpdateUserRequest updateUserRequest;

	@MockBean
	private UserService userService;

	@BeforeEach
	void initData() {
		this.createUserRequest = CreateUserRequest.builder().email("abc@gmail.com").username("abcuser")
				.password("abcpassword").build();
		this.userResponse = UserResponse.builder().email("abc@gmail.com").username("abcuser").build();

		this.updateUserRequest = UpdateUserRequest.builder().password("abcpassword").build();
	}

	@Test
	void createUser_validRequest_success() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String content = objectMapper.writeValueAsString(createUserRequest);

		Mockito.when(userService.createUser(ArgumentMatchers.any())).thenReturn(userResponse);

		mockMvc.perform(MockMvcRequestBuilders.post("/users").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().is(201))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
				.andExpect(MockMvcResultMatchers.jsonPath("result.username").value("abcuser"));
	}

	@Test
	@WithMockUser("admin")
	void getUserInfo_validRequest_success() throws Exception {

		userResponse.setUsername("admin");

		Mockito.when(userService.getUserInfo()).thenReturn(userResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/users/user-info").contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
				.andExpect(MockMvcResultMatchers.jsonPath("result.username").value("admin"));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void getUsers_validRequest_success() throws Exception {

		Mockito.when(userService.getUsers()).thenReturn(Arrays.asList(userResponse));

		mockMvc.perform(MockMvcRequestBuilders.get("/users").contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
				.andExpect(MockMvcResultMatchers.jsonPath("result", not(empty())));
	}

	@Test
	@WithAnonymousUser
	void getUsers_invalidRequest_fail() throws Exception {
		Mockito.when(userService.getUsers()).thenReturn(Arrays.asList(userResponse));
		mockMvc.perform(MockMvcRequestBuilders.get("/users").contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void getUserById_validRequest_success() throws Exception {

		Mockito.when(userService.getUserById(ArgumentMatchers.anyString())).thenReturn(userResponse);

		mockMvc.perform(MockMvcRequestBuilders.get("/users/123").contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
				.andExpect(MockMvcResultMatchers.jsonPath("result.username").value("abcuser"));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void updateUser_invalidRequest_fail() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();
		String content = objectMapper.writeValueAsString(updateUserRequest);

		Mockito.when(userService.updateUser(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
				.thenReturn(userResponse);

		mockMvc.perform(
				MockMvcRequestBuilders.patch("/users/123").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(1000));
	}
}
