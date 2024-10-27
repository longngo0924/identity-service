package com.example.identityservice.exception;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {

	USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
	INVALID_USERNAME(1003, "Username must be at least 8 characters", HttpStatus.BAD_REQUEST),
	INVALID_PASSWORD(1004, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
	USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
	UNAUTHENTICATED(1006,
			"Unauthenticated", HttpStatus.UNAUTHORIZED),
	ACCESS_DENINED(1007, "Access denied", HttpStatus.FORBIDDEN),

	INVALID_KEY(9998, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
	UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),;

	int code;
	String message;
	HttpStatus httpStatus;

	ErrorCode(int code, String message, HttpStatus httpStatus) {
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
	}

}
