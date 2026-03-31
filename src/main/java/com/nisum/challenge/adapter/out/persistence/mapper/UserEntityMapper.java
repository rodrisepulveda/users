package com.nisum.challenge.adapter.out.persistence.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nisum.challenge.adapter.out.persistence.entity.PhoneEntity;
import com.nisum.challenge.adapter.out.persistence.entity.UserEntity;
import com.nisum.challenge.domain.model.Phone;
import com.nisum.challenge.domain.model.User;

@Component
public class UserEntityMapper {

	public User toDomain(UserEntity entity) {
		List<Phone> phones = entity.getPhones() != null
			? entity.getPhones().stream().map(this::mapTelefono).toList()
			: List.of();

		return User.builder()
			.id(entity.getId())
			.name(entity.getName())
			.email(entity.getEmail())
			.password(entity.getPassword())
			.created(entity.getCreated())
			.modified(entity.getModified())
			.lastLogin(entity.getLastLogin())
			.token(entity.getToken())
			.isActive(entity.isActive())
			.phones(phones)
			.build();
	}

	public UserEntity toEntity(User domain) {
		UserEntity entity = new UserEntity();
		entity.setId(domain.getId());
		entity.setName(domain.getName());
		entity.setEmail(domain.getEmail());
		entity.setPassword(domain.getPassword());
		entity.setCreated(domain.getCreated());
		entity.setModified(domain.getModified());
		entity.setLastLogin(domain.getLastLogin());
		entity.setToken(domain.getToken());
		entity.setActive(domain.isActive());

		if (domain.getPhones() != null) {
			List<PhoneEntity> telefonos = domain.getPhones().stream()
				.map(this::mapTelefonoEntity)
				.collect(Collectors.toList());

			telefonos.forEach(t -> t.setUsers(entity));
			entity.setPhones(telefonos);
		}

		return entity;
	}

	private Phone mapTelefono(PhoneEntity entity) {
		return Phone.builder()
			.number(entity.getNumber())
			.cityCode(entity.getCitycode())
			.countryCode(entity.getContrycode())
			.build();
	}

	private PhoneEntity mapTelefonoEntity(Phone domain) {
		PhoneEntity entity = new PhoneEntity();
		entity.setNumber(domain.getNumber());
		entity.setCitycode(domain.getCityCode());
		entity.setContrycode(domain.getCountryCode());
		return entity;
	}
}
