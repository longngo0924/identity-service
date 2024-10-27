package com.example.identityservice.configuration;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.identityservice.entity.Permission;
import com.example.identityservice.entity.Role;
import com.example.identityservice.entity.User;
import com.example.identityservice.enums.Roles;
import com.example.identityservice.repository.PermissionRepository;
import com.example.identityservice.repository.RoleRepository;
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
	PermissionRepository permissionRepository;
	RoleRepository roleRepository;
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

				Permission permission = Permission.builder().name("FULL_ACCESS").description("Full Access Permission")
						.build();
				
				var newPermission = permissionRepository.save(permission);
				
				Set<Permission> permissions = new HashSet<>();				
				permissions.add(newPermission);

				Role role = Role.builder().name(Roles.ADMIN.name()).description("Admin Role").permissions(permissions)
						.build();
				
				var newRole = roleRepository.save(role);

				Set<Role> roles = new HashSet<>();
				roles.add(newRole);

				User user = User.builder().username(adminUsername).password(passwordEncoder.encode(adminPassword))
						.roles(roles).build();

				userRepository.save(user);

				log.warn("Admin was create with username: {} and password: {}", adminUsername, adminPassword);
			}

		};
	}
}
