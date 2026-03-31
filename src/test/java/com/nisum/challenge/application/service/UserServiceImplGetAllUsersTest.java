package com.nisum.challenge.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nisum.challenge.domain.model.User;
import com.nisum.challenge.domain.repository.UserRepositoryPort;
import com.nisum.challenge.infrastructure.security.JwtUtil;


class UserServiceImplGetAllUsersTest {

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
    void getAllUsers_retornaPaginaDeUsuarios() {
        Pageable pageable = PageRequest.of(0, 2);

        User user1 = User.builder().id(UUID.randomUUID()).name("Rodrigo").build();
        User user2 = User.builder().id(UUID.randomUUID()).name("María").build();
        List<User> users = List.of(user1, user2);

        Page<User> expectedPage = new PageImpl<>(users);

        when(userRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<User> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Rodrigo", result.getContent().get(0).getName());
        verify(userRepository).findAll(pageable);
    }
    
	@Test
	void getAllUsers_sinUsuariosRetornaPaginaVacia() {
		// Arrange
		Pageable pageable = PageRequest.of(0, 10);
		Page<User> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
		when(userRepository.findAll(pageable)).thenReturn(emptyPage);

		// Act
		Page<User> result = userService.getAllUsers(pageable);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getContent()).isEmpty();
		assertThat(result.getTotalElements()).isZero();
		assertThat(result.getNumber()).isEqualTo(0);
		assertThat(result.getSize()).isEqualTo(10);
	}
}
