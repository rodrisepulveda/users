package com.nisum.challenge.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserCreatedResponse(UUID id, LocalDateTime created, LocalDateTime modified, LocalDateTime lastLogin,
		String token, boolean isActive) {
}
