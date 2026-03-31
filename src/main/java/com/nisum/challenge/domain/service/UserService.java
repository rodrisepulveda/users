package com.nisum.challenge.domain.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nisum.challenge.domain.model.User;

public interface UserService {
	User registerUser(User usuario);

	User updateActiveStatus(UUID id, boolean isActive);

	User getById(UUID id);

	Page<User> getAllUsers(Pageable pageable);

}
