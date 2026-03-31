package com.nisum.challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PhoneRequest {

	@NotBlank(message = "El número es obligatorio")
	@Schema(example = "1234567")
	private String number;

	@NotBlank(message = "El código de ciudad es obligatorio")
	@Schema(example = "2")
	private String cityCode;

	@NotBlank(message = "El código de país es obligatorio")
	@Schema(example = "56")
	private String countryCode;
}
