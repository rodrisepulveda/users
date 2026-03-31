package com.nisum.challenge.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.nisum.challenge.adapter.out.persistence.entity.UserEntity;
import com.nisum.challenge.adapter.out.persistence.jpa.JpaUserRepository;
import com.nisum.challenge.adapter.out.persistence.mapper.UserEntityMapper;
import com.nisum.challenge.domain.model.User;
import com.nisum.challenge.domain.repository.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

	private final JpaUserRepository jpaRepository;
	private final UserEntityMapper mapper;

	@Override
	public Optional<User> findByEmail(String email) {
		return jpaRepository.findByEmail(email).map(mapper::toDomain);
	}

	@Override
	public User save(User usuario) {
		UserEntity entity = mapper.toEntity(usuario);
		UserEntity saved = jpaRepository.save(entity);
		return mapper.toDomain(saved);
	}

	@Override
	public Optional<User> findById(UUID id) {
		return jpaRepository.findById(id).map(mapper::toDomain);
	}

	@Override
	public Page<User> findAll(Pageable pageable) {
		Page<UserEntity> entities = jpaRepository.findAll(pageable);
		return entities.map(mapper::toDomain); // <-- Mapea cada entidad a un objeto de dominio
	}
}
