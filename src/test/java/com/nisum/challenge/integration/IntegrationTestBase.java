// src/test/java/com/nisum/challenge/integration/IntegrationTestBase.java
package com.nisum.challenge.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = Replace.NONE)   // <— importante
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional  // auto‐rollback al final de cada test
public abstract class IntegrationTestBase {

    @Container
    static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("testdb")
        .withUsername("sa")
        .withPassword("sa");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto",       () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect",
                     () -> "org.hibernate.dialect.PostgreSQLDialect");
        // opcional: si tienes Actuator en test, exponer prometheus:
        registry.add("management.endpoints.web.exposure.include", () -> "health,info,prometheus");
    }

    // puedes inyectar MockMvc si usas @AutoConfigureMockMvc en clases hijas:
    @Autowired
    protected MockMvc mockMvc;
}
