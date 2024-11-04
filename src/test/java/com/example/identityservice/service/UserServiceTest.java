package com.example.identityservice.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import com.example.identityservice.dto.request.CreateUserRequest;
import com.example.identityservice.dto.response.UserResponse;
import com.example.identityservice.entity.User;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.exception.ErrorCode;
import com.example.identityservice.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
class UserServiceTest {

	@MockBean
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	private User user;

	private CreateUserRequest createUserRequest;

	@BeforeEach
	void initDate() {
		this.createUserRequest = CreateUserRequest.builder().email("abc@gmail.com").username("abcuser")
				.password("abcpassword").build();
		this.user = User.builder().id("123").username("abc").password("abcxyz").email("abc@abc.com").build();
	}

	@Test
	void createUser_validRequest_success() {
		Mockito.when(userRepository.save(ArgumentMatchers.any())).thenReturn(user);

		UserResponse newUser = this.userService.createUser(createUserRequest);

		assertNotNull(newUser.getId());
	}

	@Test
	void getUserById_invalidRequest_fail() {
		Mockito.when(userRepository.findById(ArgumentMatchers.anyString()))
				.thenThrow(new AppException(ErrorCode.USER_NOT_EXISTED));

		assertThrows(AppException.class, () -> userService.getUserById(""));
	}
}
