package com.nisum.challenge.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.nisum.challenge.domain.repository.UserRepositoryPort;

class SecurityConfigTest {

    @Test
    void passwordEncoder_devuelveBCryptPasswordEncoder() {
        SecurityConfig config = new SecurityConfig(mock(JwtUtil.class), mock(UserRepositoryPort.class));
        PasswordEncoder encoder = config.passwordEncoder();

        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);
    }

    @Test
    void securityFilterChain_noLanzaExcepcion() throws Exception {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        UserRepositoryPort userRepo = mock(UserRepositoryPort.class);
        JwtAuthenticationEntryPoint entryPoint = mock(JwtAuthenticationEntryPoint.class);
        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);

        SecurityConfig config = new SecurityConfig(jwtUtil, userRepo);

        // No podemos verificar comportamiento interno con mocks, pero sí que no falle
        SecurityFilterChain chain = config.securityFilterChain(http, entryPoint);
        assertNotNull(chain);
    }
}
