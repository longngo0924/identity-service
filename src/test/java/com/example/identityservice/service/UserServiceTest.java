package com.example.identityservice.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;

import com.example.identityservice.dto.request.CreateUserRequest;
import com.example.identityservice.dto.request.UpdateUserRequest;
import com.example.identityservice.dto.response.UserResponse;
import com.example.identityservice.entity.Role;
import com.example.identityservice.entity.User;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.exception.ErrorCode;
import com.example.identityservice.repository.RoleRepository;
import com.example.identityservice.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
class UserServiceTest {

	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private RoleRepository roleRepository;

	@Autowired
	private UserService userService;

	private User user;
	
	private Role role;

	private CreateUserRequest createUserRequest;
	
	private UpdateUserRequest updateUserRequest;

	@BeforeEach
	void initData() {
		this.createUserRequest = CreateUserRequest.builder().email("abc@gmail.com").username("abcuser")
				.password("abcpassword").build();
		
		this.updateUserRequest = UpdateUserRequest.builder().password("123456")
				.roles(new HashSet<String>(Arrays.asList("ADMIN"))).build();
		
		this.user = User.builder().id("123").username("abc").password("abcxyz").email("abc@abc.com").build();
		
		this.role = Role.builder().name("ADMIN").description("Admin role").build();
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
	
	
	@Test
	void getUsers_validRequest_success() {
		Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(user));
		
		List<UserResponse> users = userService.getUsers();
		assertNotNull(users);
	}
	
	@Test
	void updateUser_validRequest_success() {
		user.setRoles(new HashSet<Role>(Arrays.asList(role)));
		
		Mockito.when(userRepository.save(ArgumentMatchers.any())).thenReturn(user);
		Mockito.when(userRepository.findById("123")).thenReturn(Optional.of(user));
		Mockito.when(roleRepository.findAllById(ArgumentMatchers.anyIterable())).thenReturn(Arrays.asList(role));
		
		UserResponse savedUser = userService.updateUser("123", updateUserRequest);
		assertNotNull(savedUser);
	}
	
	@Test
	void getUserById_validRequest_success() {
		Optional<User> optionalUser = Optional.of(user);
		Mockito.when(userRepository.findById(ArgumentMatchers.anyString())).thenReturn(optionalUser);
		UserResponse userRespone = userService.getUserById("123");

		assertNotNull(userRespone);
	}
	
	@Test
	void deleteUser_validRequest_success() {
		boolean deleteResult = userService.deleteUser(ArgumentMatchers.anyString());
		assertTrue(deleteResult);
	}
	
	@Test
	void getUserInfo_validRequest_success() {
		Authentication authentication = Mockito.mock(Authentication.class);
		SecurityContext context = Mockito.mock(SecurityContext.class);

		Mockito.when(context.getAuthentication()).thenReturn(authentication);
		Mockito.when(authentication.getName()).thenReturn("mockUser");

		SecurityContextHolder.setContext(context);

		Optional<User> optionalUser = Optional.of(user);
		Mockito.when(userRepository.findByUsername("mockUser")).thenReturn(optionalUser);

		UserResponse userResponse = userService.getUserInfo();
		assertNotNull(userResponse);
	}
	
	@Test
	void getUserInfo_userNotFound_throwsException() {
		Authentication authentication = Mockito.mock(Authentication.class);
		SecurityContext context = Mockito.mock(SecurityContext.class);

		Mockito.when(context.getAuthentication()).thenReturn(authentication);
		Mockito.when(authentication.getName()).thenReturn("unknownUser");

		SecurityContextHolder.setContext(context);

		Mockito.when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

		assertThrows(AppException.class, () -> userService.getUserInfo());
	}
}
