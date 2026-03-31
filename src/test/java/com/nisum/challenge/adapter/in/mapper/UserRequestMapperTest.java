package com.nisum.challenge.adapter.in.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.nisum.challenge.domain.model.Phone;
import com.nisum.challenge.domain.model.User;
import com.nisum.challenge.dto.CreateUserRequest;
import com.nisum.challenge.dto.PhoneRequest;

class UserRequestMapperTest {

    private final UserRequestMapper mapper = new UserRequestMapper();

    @Test
    void toDomain_conDatosValidos_retornaUserConCamposCorrectos() {
        CreateUserRequest request = new CreateUserRequest(
                "Rodrigo",
                "rodri@correo.com",
                "Password123!",
                List.of(new PhoneRequest("12345678", "9", "56"))
        );

        User user = mapper.toDomain(request);

        assertEquals("Rodrigo", user.getName());
        assertEquals("rodri@correo.com", user.getEmail());
        assertEquals("Password123!", user.getPassword());
        assertNotNull(user.getPhones());
        assertEquals(1, user.getPhones().size());

        Phone phone = user.getPhones().get(0);
        assertEquals("12345678", phone.getNumber());
        assertEquals("9", phone.getCityCode());
        assertEquals("56", phone.getCountryCode());
    }

    @Test
    void toDomain_conListaDePhonesNull_retornaUserConPhonesNull() {
        CreateUserRequest request = new CreateUserRequest(
                "Rodrigo",
                "rodri@correo.com",
                "Password123!",
                null
        );

        User user = mapper.toDomain(request);

        assertEquals("Rodrigo", user.getName());
        assertEquals("rodri@correo.com", user.getEmail());
        assertEquals("Password123!", user.getPassword());
        assertNull(user.getPhones());
    }
}
