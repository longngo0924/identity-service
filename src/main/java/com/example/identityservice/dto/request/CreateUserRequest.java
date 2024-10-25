package com.example.identityservice.dto.request;

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

	@Size(min = 8, message = "INVALID_USERNAME")
	String username;
	
	String email;
	
	@Size(min = 8, max = 20, message = "INVALID_PASSWORD")
	String password;
}
