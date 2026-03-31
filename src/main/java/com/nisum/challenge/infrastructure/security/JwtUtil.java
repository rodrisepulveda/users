package com.nisum.challenge.infrastructure.security;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nisum.challenge.domain.service.TokenProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil implements TokenProvider {

	private final Key key;
	private final long expirationMillis;

	public JwtUtil(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration}") long expirationMillis) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
		this.expirationMillis = expirationMillis;
	}

	public String generateToken(UUID userId, String email) {
		Date now = new Date();
		Date exp = new Date(now.getTime() + expirationMillis);
		return Jwts.builder().setSubject(email).claim("userId", userId.toString()).setIssuedAt(now).setExpiration(exp)
				.signWith(key, SignatureAlgorithm.HS256).compact();
	}

	public boolean isTokenValid(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public Claims getClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}
}
