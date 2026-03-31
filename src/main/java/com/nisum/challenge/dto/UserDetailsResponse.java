package com.nisum.challenge.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserDetailsResponse(
    UUID id,
    String name,
    LocalDateTime created,
    LocalDateTime modified,
    LocalDateTime lastLogin,
    boolean active,
    List<PhoneResponse> phones
) {}
