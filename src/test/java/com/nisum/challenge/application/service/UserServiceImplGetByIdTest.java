package com.nisum.challenge.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nisum.challenge.domain.exception.NotFoundException;
import com.nisum.challenge.domain.model.User;
import com.nisum.challenge.domain.repository.UserRepositoryPort;
import com.nisum.challenge.infrastructure.security.JwtUtil;

class UserServiceImplGetByIdTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getById_usuarioExiste_retornaUsuario() {
        UUID userId = UUID.randomUUID();
        User expectedUser = User.builder().id(userId).name("Rodrigo").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User result = userService.getById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository).findById(userId);
    }

    @Test
    void getById_usuarioNoExiste_lanzaNotFoundException() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.getById(userId));

        assertEquals("User with ID " + userId + " not found", ex.getMessage());
        verify(userRepository).findById(userId);
    }
}
