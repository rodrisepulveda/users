package com.nisum.challenge.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class User {
	private UUID id;
	private String name;
	private String email;
	private String password;
	private LocalDateTime created;
	private LocalDateTime modified;
	private LocalDateTime lastLogin;
	private String token;
	private boolean isActive;
	private List<Phone> phones;
}
