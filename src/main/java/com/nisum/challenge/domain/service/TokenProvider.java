package com.nisum.challenge.domain.service;

import java.util.UUID;

import io.jsonwebtoken.Claims;

public interface TokenProvider {
    String generateToken(UUID userId, String email);

	Claims getClaims(String token);

	boolean isTokenValid(String tokenInvalido);
}
