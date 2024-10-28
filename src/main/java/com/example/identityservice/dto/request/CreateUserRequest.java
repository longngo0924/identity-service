package com.example.identityservice.dto.request;

import com.example.identityservice.validator.EmailConstraint;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {

	@Size(min = 6, message = "INVALID_USERNAME")
	String username;

	@EmailConstraint(message = "INVALID_EMAIL")
	String email;

	@Size(min = 8, max = 20, message = "INVALID_PASSWORD")
	String password;
}
