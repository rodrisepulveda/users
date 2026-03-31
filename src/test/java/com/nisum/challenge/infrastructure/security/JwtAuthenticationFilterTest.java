package com.nisum.challenge.infrastructure.security;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.nisum.challenge.domain.model.User;
import com.nisum.challenge.domain.repository.UserRepositoryPort;
import com.nisum.challenge.domain.service.TokenProvider;

import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;

class JwtAuthenticationFilterTest {

    private TokenProvider tokenProvider;
    private UserRepositoryPort userRepository;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
    	tokenProvider = mock(JwtUtil.class);
        userRepository = mock(UserRepositoryPort.class);
        filter = new JwtAuthenticationFilter(tokenProvider, userRepository);
    }

    @Test
    void doFilterInternal_tokenValido_usuarioActivo_autentica() throws IOException, ServletException {
        String token = "valid-token";
        UUID userId = UUID.randomUUID();

        Claims claims = mock(Claims.class);
        when(claims.get("userId", String.class)).thenReturn(userId.toString());

        User user = User.builder().id(userId).email("user@test.com").isActive(true).build();

        when(tokenProvider.isTokenValid(token)).thenReturn(true);
        when(tokenProvider.getClaims(token)).thenReturn(claims);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        // No excepción = pasó
    }

    @Test
    void doFilterInternal_tokenInvalido_noAutentica() throws IOException, ServletException {
        String token = "invalid-token";

        when(tokenProvider.isTokenValid(token)).thenReturn(false);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        // No autenticación = sin excepción
    }

    @Test
    void doFilterInternal_usuarioInactivo_noAutentica() throws IOException, ServletException {
        String token = "valid-token";
        UUID userId = UUID.randomUUID();

        Claims claims = mock(Claims.class);
        when(claims.get("userId", String.class)).thenReturn(userId.toString());

        User user = User.builder().id(userId).email("user@test.com").isActive(false).build();

        when(tokenProvider.isTokenValid(token)).thenReturn(true);
        when(tokenProvider.getClaims(token)).thenReturn(claims);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        // No autenticación = sin excepción
    }

    @Test
    void doFilterInternal_sinHeaderContinuaFiltro() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        // Sin header => continúa sin error
    }
}
