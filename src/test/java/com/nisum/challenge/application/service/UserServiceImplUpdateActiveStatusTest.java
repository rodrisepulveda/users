package com.nisum.challenge.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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

class UserServiceImplUpdateActiveStatusTest {

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
    void updateActiveStatus_usuarioExiste_actualizaEstadoYRetornaUsuario() {
        UUID userId = UUID.randomUUID();
        User existingUser = User.builder()
                .id(userId)
                .name("Rodrigo")
                .isActive(false)
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .name("Rodrigo")
                .isActive(true)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);

        User result = userService.updateActiveStatus(userId, true);

        assertTrue(result.isActive());
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateActiveStatus_usuarioNoExiste_lanzaNotFoundException() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () ->
                userService.updateActiveStatus(userId, true));

        assertEquals("User not found", ex.getMessage());
        verify(userRepository, never()).save(any());
    }
}
