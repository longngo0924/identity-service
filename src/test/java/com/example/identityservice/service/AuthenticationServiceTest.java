package com.example.identityservice.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import com.example.identityservice.dto.request.AuthenticationRequest;
import com.example.identityservice.dto.response.AuthenticationResponse;
import com.example.identityservice.entity.User;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.repository.InvalidTokenRepository;
import com.example.identityservice.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
class AuthenticationServiceTest {

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private InvalidTokenRepository invalidTokenRepository;

	@Value("${jwt.signing-key}")
	private String jwtSigningKey;

	@Value("${jwt.issuer}")
	private String jwtIssuer;

	@Value("${jwt.expire-time}")
	private String jwtExpireTime;

	@Value("${jwt.refreshable-time}")
	private String jwtRefreshableTime;

	@Autowired
	private AuthenticationService authenticationService;

	private User user;
	private AuthenticationRequest authenticationRequest;
	private AuthenticationResponse authenticationResponse;

	@BeforeEach
	void initData() {
		this.authenticationRequest = new AuthenticationRequest();
		authenticationRequest.setUsername("testUser");
		authenticationRequest.setPassword("testPassword");

		this.user = new User();
		user.setUsername("testUser");
		user.setPassword(new BCryptPasswordEncoder().encode("testPassword"));

		this.authenticationResponse = new AuthenticationResponse();
		authenticationResponse.setAuthenticated(true);
		authenticationResponse.setAccess("dummyAccessToken");
		authenticationResponse.setRefresh("dummyRefreshToken");
	}

	@Test
	void authenticateUser_validCredentials_success() {
		Mockito.when(userRepository.findByUsername(authenticationRequest.getUsername())).thenReturn(Optional.of(user));

		AuthenticationResponse response = authenticationService.authenticateUser(authenticationRequest);
		assertNotNull(response);
		assertTrue(response.isAuthenticated());
	}

	@Test
	void authenticateUser_invalidPassword_throwsException() {
		Mockito.when(userRepository.findByUsername(authenticationRequest.getUsername())).thenReturn(Optional.of(user));

		authenticationRequest.setPassword("wrongPassword");

		assertThrows(AppException.class, () -> authenticationService.authenticateUser(authenticationRequest));
	}

}
