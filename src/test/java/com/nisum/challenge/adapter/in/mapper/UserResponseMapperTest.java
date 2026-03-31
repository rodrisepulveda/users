package com.nisum.challenge.adapter.in.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.nisum.challenge.domain.model.Phone;
import com.nisum.challenge.domain.model.User;
import com.nisum.challenge.dto.UserCreatedResponse;
import com.nisum.challenge.dto.UserDetailsResponse;

class UserResponseMapperTest {

	private final UserResponseMapper mapper = new UserResponseMapper();

	@Test
	void toResponse_conUsuarioValido_retornaUserCreatedResponse() {
		UUID userId = UUID.randomUUID();
		LocalDateTime now = LocalDateTime.now();

		User user = User.builder()
				.id(userId)
				.created(now)
				.modified(now)
				.lastLogin(now)
				.token("token-123")
				.isActive(true)
				.build();

		UserCreatedResponse response = mapper.toResponse(user);

		assertEquals(userId, response.id());
		assertEquals(now, response.created());
		assertEquals(now, response.modified());
		assertEquals(now, response.lastLogin());
		assertEquals("token-123", response.token());
		assertTrue(response.isActive());
	}

	@Test
	void toUserDetailsResponse_conUsuarioYPhones_retornaUserDetailsResponseConPhones() {
		UUID userId = UUID.randomUUID();
		LocalDateTime now = LocalDateTime.now();

		User user = User.builder()
				.id(userId)
				.name("Rodrigo")
				.created(now)
				.modified(now)
				.lastLogin(now)
				.isActive(true)
				.phones(List.of(
						Phone.builder().number("12345678").cityCode("9").countryCode("56").build()
				))
				.build();

		UserDetailsResponse response = mapper.toUserDetailsResponse(user);

		assertEquals(userId, response.id());
		assertEquals("Rodrigo", response.name());
		assertEquals(now, response.created());
		assertEquals(now, response.modified());
		assertEquals(now, response.lastLogin());
		assertTrue(response.active());

		assertNotNull(response.phones());
		assertEquals(1, response.phones().size());
		assertEquals("12345678", response.phones().get(0).number());
		assertEquals("9", response.phones().get(0).cityCode());
		assertEquals("56", response.phones().get(0).countryCode());
	}
}
