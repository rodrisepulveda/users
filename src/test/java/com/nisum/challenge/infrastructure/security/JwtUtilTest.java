package com.nisum.challenge.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nisum.challenge.domain.service.TokenProvider;

import io.jsonwebtoken.Claims;

class JwtUtilTest {

    private  TokenProvider tokenProvider;
    private final String secret = "12345678901234567890123456789012"; // Debe tener al menos 32 bytes para HS256
    private final long expirationMillis = 3600000; // 1 hora

    @BeforeEach
    void setUp() {
    	tokenProvider = new JwtUtil(secret, expirationMillis);
    }

    @Test
    void generateToken_tokenEsValidoYContieneDatosCorrectos() {
        UUID userId = UUID.randomUUID();
        String email = "test@correo.com";

        String token = tokenProvider.generateToken(userId, email);

        assertNotNull(token);
        assertTrue(tokenProvider.isTokenValid(token));

        Claims claims = tokenProvider.getClaims(token);
        assertEquals(email, claims.getSubject());
        assertEquals(userId.toString(), claims.get("userId"));
    }

    @Test
    void isTokenValid_tokenInvalido_retornaFalse() {
        String tokenInvalido = "este-no-es-un-token";

        boolean esValido = tokenProvider.isTokenValid(tokenInvalido);

        assertFalse(esValido);
    }

    @Test
    void getClaims_tokenInvalido_lanzaExcepcion() {
        String tokenInvalido = "invalido";

        assertThrows(Exception.class, () -> tokenProvider.getClaims(tokenInvalido));
    }
}
