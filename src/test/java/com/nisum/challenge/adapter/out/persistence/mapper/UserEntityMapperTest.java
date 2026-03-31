package com.nisum.challenge.adapter.out.persistence.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.nisum.challenge.adapter.out.persistence.entity.PhoneEntity;
import com.nisum.challenge.adapter.out.persistence.entity.UserEntity;
import com.nisum.challenge.domain.model.Phone;
import com.nisum.challenge.domain.model.User;

class UserEntityMapperTest {

	private final UserEntityMapper mapper = new UserEntityMapper();

	@Test
	void toDomain_conUserEntityValido_retornaUserDominio() {
		UUID userId = UUID.randomUUID();
		LocalDateTime now = LocalDateTime.now();

		PhoneEntity phoneEntity = new PhoneEntity();
		phoneEntity.setNumber("12345678");
		phoneEntity.setCitycode("9");
		phoneEntity.setContrycode("56");

		UserEntity entity = new UserEntity();
		entity.setId(userId);
		entity.setName("Rodrigo");
		entity.setEmail("rodri@correo.com");
		entity.setPassword("encodedPass");
		entity.setCreated(now);
		entity.setModified(now);
		entity.setLastLogin(now);
		entity.setToken("jwt-token");
		entity.setActive(true);
		entity.setPhones(List.of(phoneEntity));

		User user = mapper.toDomain(entity);

		assertEquals(userId, user.getId());
		assertEquals("Rodrigo", user.getName());
		assertEquals("rodri@correo.com", user.getEmail());
		assertEquals("encodedPass", user.getPassword());
		assertEquals(now, user.getCreated());
		assertEquals(now, user.getModified());
		assertEquals(now, user.getLastLogin());
		assertEquals("jwt-token", user.getToken());
		assertTrue(user.isActive());
		assertEquals(1, user.getPhones().size());
		assertEquals("12345678", user.getPhones().get(0).getNumber());
		assertEquals("9", user.getPhones().get(0).getCityCode());
		assertEquals("56", user.getPhones().get(0).getCountryCode());
	}

	@Test
	void toEntity_conUserDominioValido_retornaUserEntity() {
		UUID userId = UUID.randomUUID();
		LocalDateTime now = LocalDateTime.now();

		Phone phone = Phone.builder()
				.number("87654321")
				.cityCode("2")
				.countryCode("34")
				.build();

		User user = User.builder()
				.id(userId)
				.name("María")
				.email("maria@correo.com")
				.password("1234")
				.created(now)
				.modified(now)
				.lastLogin(now)
				.token("token-123")
				.isActive(false)
				.phones(List.of(phone))
				.build();

		UserEntity entity = mapper.toEntity(user);

		assertEquals(userId, entity.getId());
		assertEquals("María", entity.getName());
		assertEquals("maria@correo.com", entity.getEmail());
		assertEquals("1234", entity.getPassword());
		assertEquals(now, entity.getCreated());
		assertEquals(now, entity.getModified());
		assertEquals(now, entity.getLastLogin());
		assertEquals("token-123", entity.getToken());
		assertFalse(entity.isActive());

		assertNotNull(entity.getPhones());
		assertEquals(1, entity.getPhones().size());
		PhoneEntity phoneEntity = entity.getPhones().get(0);
		assertEquals("87654321", phoneEntity.getNumber());
		assertEquals("2", phoneEntity.getCitycode());
		assertEquals("34", phoneEntity.getContrycode());
		assertEquals(entity, phoneEntity.getUsers()); // Verifica asignación inversa
	}
	
    @Test
    void toDomain_conListaTelefonosNull_debeRetornarUsuarioConListaVacia() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();
        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setName("Rodrigo");
        userEntity.setEmail("rodri@correo.com");
        userEntity.setPassword("1234");
        userEntity.setCreated(ahora);
        userEntity.setModified(ahora);
        userEntity.setLastLogin(ahora);
        userEntity.setToken("token123");
        userEntity.setActive(true);
        userEntity.setPhones(null); // <- caso que se quiere cubrir

        // Act
        User user = mapper.toDomain(userEntity);

        // Assert
        assertNotNull(user);
        assertEquals(0, user.getPhones().size()); // se espera lista vacía, no null
        assertEquals(id, user.getId());
        assertEquals("Rodrigo", user.getName());
    }
    

    @Test
    void toEntity_conListaTelefonosNull_noAsignaPhones() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime ahora = LocalDateTime.now();
        User user = User.builder()
                .id(id)
                .name("Rodrigo")
                .email("rodri@correo.com")
                .password("1234")
                .created(ahora)
                .modified(ahora)
                .lastLogin(ahora)
                .token("token123")
                .isActive(true)
                .phones(null) // <- caso que se quiere cubrir
                .build();

        // Act
        UserEntity entity = mapper.toEntity(user);

        // Assert
        assertNotNull(entity);
        assertNull(entity.getPhones()); // no se asigna nada
        assertEquals("Rodrigo", entity.getName());
        assertEquals("rodri@correo.com", entity.getEmail());
        assertEquals("1234", entity.getPassword());
    }
}
