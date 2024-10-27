package com.example.identityservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.identityservice.dto.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(RuntimeException.class)
	ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException exception) {
		log.error("Exception: {}", exception.getMessage());

		ApiResponse<Object> apiResponse = ApiResponse.<Object>builder()
				.code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
				.message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage()).build();

		return ResponseEntity.badRequest().body(apiResponse);

	}

	@ExceptionHandler(AppException.class)
	ResponseEntity<ApiResponse<Object>> handleAppException(AppException exception) {

		ApiResponse<Object> apiResponse = ApiResponse.<Object>builder().code(exception.getErrorCode().getCode())
				.message(exception.getErrorCode().getMessage()).build();

		return new ResponseEntity<>(apiResponse, exception.getErrorCode().getHttpStatus());

	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException exception) {

		ErrorCode errorCode = ErrorCode.INVALID_KEY;

		try {
			String enumKey = exception.getFieldError().getDefaultMessage();
			errorCode = ErrorCode.valueOf(enumKey);
		} catch (IllegalArgumentException e) {
			log.error(e.getMessage());
		}

		ApiResponse<Object> apiResponse = ApiResponse.<Object>builder().code(errorCode.getCode())
				.message(errorCode.getMessage()).build();

		return new ResponseEntity<>(apiResponse, errorCode.getHttpStatus());

	}

	@ExceptionHandler(AccessDeniedException.class)
	ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException exception) {
		log.error("Exception: {}", exception.getMessage());

		ApiResponse<Object> apiResponse = ApiResponse.<Object>builder().code(ErrorCode.ACCESS_DENINED.getCode())
				.message(ErrorCode.ACCESS_DENINED.getMessage()).build();

		return new ResponseEntity<>(apiResponse, ErrorCode.ACCESS_DENINED.getHttpStatus());

	}

}
