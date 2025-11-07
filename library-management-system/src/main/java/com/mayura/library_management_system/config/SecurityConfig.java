package com.mayura.library_management_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.mayura.library_management_system.Services.CustomUserDetails;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final CustomUserDetails customUserDetails;
	public SecurityConfig(CustomUserDetails customUserDetails) {
		this.customUserDetails=customUserDetails; 
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(request -> request.requestMatchers("/register","/login").permitAll()
				.anyRequest().authenticated())
		.formLogin(form -> form.loginProcessingUrl("/login").defaultSuccessUrl("/welcom",true).permitAll())
		.logout(logout -> logout.logoutSuccessUrl("/login").permitAll()).userDetailsService(customUserDetails);
		return http.build();
	}
}
