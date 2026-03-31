package com.nisum.challenge.dto;

import jakarta.validation.constraints.NotNull;

public record UserActiveStatusRequest(
    @NotNull(message = "El campo 'active' es obligatorio") Boolean active
) {}
