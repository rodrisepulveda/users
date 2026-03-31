package com.nisum.challenge.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

class OpenApiConfigTest {

    @Test
    void customOpenAPI_devuelveOpenAPIConInfoEsperado() {
        // Arrange
        OpenApiConfig config = new OpenApiConfig();

        // Act
        OpenAPI openAPI = config.customOpenAPI();

        // Assert
        assertNotNull(openAPI);
        Info info = openAPI.getInfo();
        assertNotNull(info);
        assertEquals("User Registration API", info.getTitle());
        assertEquals("1.0", info.getVersion());
        assertEquals("API for managing users, registration and authentication", info.getDescription());
    }
}
