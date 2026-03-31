package com.nisum.challenge.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Phone {
	private String number;
	private String cityCode;
	private String countryCode;
}
