package com.example.identityservice.configuration;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.identityservice.entity.User;
import com.example.identityservice.enums.Role;
import com.example.identityservice.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AppInitConfig {

	UserRepository userRepository;
	PasswordEncoder passwordEncoder;

	@NonFinal
	@Value("${admin.username}")
	String adminUsername;

	@NonFinal
	@Value("${admin.password}")
	String adminPassword;

	@Bean
	ApplicationRunner applicationRunner() {
		return args -> {
			if (userRepository.findByUsername(adminUsername).isEmpty()) {
				Set<String> roles = new HashSet<>();
				roles.add(Role.ADMIN.name());

				User user = User.builder().username(adminUsername).password(passwordEncoder.encode(adminPassword))
						.roles(roles).build();

				userRepository.save(user);

				log.warn("Admin was create with username {} and password {}", adminUsername, adminPassword);
			}

		};
	}
}
