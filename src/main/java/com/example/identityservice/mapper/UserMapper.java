package com.example.identityservice.mapper;

import org.mapstruct.Mapper;

import com.example.identityservice.dto.request.CreateUserRequest;
import com.example.identityservice.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
	User toUser(CreateUserRequest request);
}
