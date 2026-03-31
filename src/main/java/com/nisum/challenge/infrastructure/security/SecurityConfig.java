package com.nisum.challenge.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.nisum.challenge.domain.repository.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtUtil jwtUtil;
	private final UserRepositoryPort userRepository;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationEntryPoint authEntryPoint)
			throws Exception {
		http.csrf(csrf -> csrf.disable());

		http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

		http.authorizeHttpRequests(auth -> auth.requestMatchers("/api/users", "/api/users/login", "/h2-console/**",
				"/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/actuator/**").permitAll().anyRequest().authenticated());

		http.addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userRepository),
				UsernamePasswordAuthenticationFilter.class);

		http.exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPoint));

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
