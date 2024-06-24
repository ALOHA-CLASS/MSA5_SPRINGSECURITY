package com.aloha.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// 폼 기반 로그인 비활성화
		http.formLogin(login ->login.disable());							

		// HTTP 기본 인증 비활성화
		http.httpBasic(basic ->basic.disable());

		// CSRF(Cross-Site Request Forgery) 공격 방어 기능 비활성화
		http.csrf(csrf ->csrf.disable());

		// 세션 관리 정책 설정: STATELESS로 설정하면 서버는 세션을 생성하지 않음
	 	// 🔐 세션을 사용하여 인증하지 않고,  JWT 를 사용하여 인증하기 때문에, 세션 불필요
		http.sessionManagement(management ->management
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


		// 구성이 완료된 SecurityFilterChain을 반환합니다.
		return http.build();
	}

  
}
