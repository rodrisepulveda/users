package com.nisum.challenge.adapter.in.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nisum.challenge.domain.model.Phone;
import com.nisum.challenge.domain.model.User;
import com.nisum.challenge.dto.CreateUserRequest;
import com.nisum.challenge.dto.PhoneRequest;

@Component
public class UserRequestMapper {

	public User toDomain(CreateUserRequest request) {
		return User.builder().name(request.name()).email(request.email()).password(request.password())
				.phones(mapPhones(request.phones())).build();
	}

	private List<Phone> mapPhones(List<PhoneRequest> phoneRequests) {
		if (phoneRequests == null) {
			return null;
		}

		return phoneRequests.stream().map(this::mapPhone).collect(Collectors.toList());
	}

	private Phone mapPhone(PhoneRequest request) {
		return Phone.builder().number(request.getNumber()).cityCode(request.getCityCode())
				.countryCode(request.getCountryCode()).build();
	}
}
