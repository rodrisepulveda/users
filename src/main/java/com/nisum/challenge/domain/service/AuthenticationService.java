package com.nisum.challenge.domain.service;

import com.nisum.challenge.domain.model.User;

public interface AuthenticationService {
	User authenticate(String email, String password);
}
