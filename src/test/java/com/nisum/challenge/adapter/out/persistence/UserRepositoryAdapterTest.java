package com.nisum.challenge.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.nisum.challenge.adapter.out.persistence.entity.UserEntity;
import com.nisum.challenge.adapter.out.persistence.jpa.JpaUserRepository;
import com.nisum.challenge.adapter.out.persistence.mapper.UserEntityMapper;
import com.nisum.challenge.domain.model.User;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private JpaUserRepository jpaRepository;

    @Mock
    private UserEntityMapper mapper;

    @InjectMocks
    private UserRepositoryAdapter adapter;

    private UUID userId;
    private UserEntity userEntity;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail("test@example.com");

        user = User.builder()
                .id(userId)
                .email("test@example.com")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void findByEmail_retornaUsuarioDominioSiExiste() {
        when(jpaRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));
        when(mapper.toDomain(userEntity)).thenReturn(user);

        Optional<User> result = adapter.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        verify(jpaRepository).findByEmail("test@example.com");
    }

    @Test
    void save_convierteADominioYRetornaGuardado() {
        when(mapper.toEntity(user)).thenReturn(userEntity);
        when(jpaRepository.save(userEntity)).thenReturn(userEntity);
        when(mapper.toDomain(userEntity)).thenReturn(user);

        User result = adapter.save(user);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(jpaRepository).save(userEntity);
    }

    @Test
    void findById_retornaUsuarioDominioSiExiste() {
        when(jpaRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(mapper.toDomain(userEntity)).thenReturn(user);

        Optional<User> result = adapter.findById(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        verify(jpaRepository).findById(userId);
    }

    @Test
    void findAll_retornaPaginaDeUsuariosDominio() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<UserEntity> page = new PageImpl<>(List.of(userEntity));

        when(jpaRepository.findAll(pageable)).thenReturn(page);
        when(mapper.toDomain(userEntity)).thenReturn(user);

        Page<User> result = adapter.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(userId, result.getContent().get(0).getId());
        verify(jpaRepository).findAll(pageable);
    }
}
