package bcs.aicrm.broadcns.authservice.config.security;

import bcs.aicrm.broadcns.authservice.config.security.jwt.JwtAuthencationEntryPoint;
import bcs.aicrm.broadcns.authservice.config.security.jwt.JwtFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtFilter filter;
	private final JwtAuthencationEntryPoint entryPoint;
	private final String[] AUTH = {"/h2-console/**","/favicon.ico","/auth/login", "/swagger-ui/**", "/v3/api-docs/**", "/swagger.html","/auth/health"};

	public SecurityConfig(JwtFilter filter, JwtAuthencationEntryPoint entryPoint) {
		this.filter = filter;
		this.entryPoint = entryPoint;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
				// form방식 로그인이 아니므로 비활성화
				.httpBasic( httpBasic -> httpBasic.disable())
				.csrf(csrfConfigurer -> csrfConfigurer.disable())
				// h2 console을 사용하기 위함. 기본적으로 security에서 방지
				.headers(headerConfig -> headerConfig.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable()))
				.authorizeHttpRequests(
						auth -> auth.requestMatchers(AUTH)
								.permitAll()
								.anyRequest().authenticated()
//						auth -> auth.requestMatchers("**")
//								.permitAll()
				)
				.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.exceptionHandling(exception -> {
					exception.authenticationEntryPoint(entryPoint);
				})
				.addFilterBefore(this.filter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

}
