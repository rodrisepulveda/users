package com.nisum.challenge.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginRequest {

	@Email(message = "El correo no tiene un formato válido")
	@NotBlank(message = "El correo es obligatorio")
	private String email;

	@NotBlank(message = "La contraseña es obligatoria")
	private String password;
}
