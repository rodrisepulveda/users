package com.nisum.challenge.dto;

import java.util.UUID;

public record UserActiveStatusResponse(UUID id, String email, boolean active) {
}
