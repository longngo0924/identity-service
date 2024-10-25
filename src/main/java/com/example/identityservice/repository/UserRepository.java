package com.example.identityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.identityservice.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	
	public boolean existsByUsername(String username);

	public boolean existsByEmail(String email);
}
