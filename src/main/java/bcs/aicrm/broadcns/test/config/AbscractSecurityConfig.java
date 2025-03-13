package bcs.aicrm.broadcns.test.config;

import bcs.aicrm.broadcns.test.config.jwt.JwtAuthenticationEntryPoint;
import bcs.aicrm.broadcns.test.config.jwt.JwtFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

abstract class AbscractSecurityConfig{

	protected JwtFilter jwtFilter;
	protected JwtAuthenticationEntryPoint entryPoint;

	public AbscractSecurityConfig(JwtFilter jwtFilter, JwtAuthenticationEntryPoint entryPoint) {
		this.jwtFilter = jwtFilter;
		this.entryPoint = entryPoint;
	}

	public String[] getNonAuthenticatedItems() throws Exception {
		return new String[] {"/auth/login", "/auth/health","/swagger-ui/**", "/v3/api-docs/**", "/swagger.html"};
	}

	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.httpBasic( httpBasic -> httpBasic.disable())
				.csrf(csrfConfigurer -> csrfConfigurer.disable())
				.headers(headerConfig -> headerConfig.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable()))
				.authorizeHttpRequests(
						auth -> {
							try {
								auth.requestMatchers(this.getNonAuthenticatedItems())
										.permitAll()
										.requestMatchers(this.getUserRoleAuthenticatedItems())
										.hasAnyAuthority("USER")
										.requestMatchers(this.getAdminRoleAuthenticatedItems())
										.hasAnyAuthority("ADMIN")
										.anyRequest().authenticated();
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
				)
				.sessionManagement(session ->
						session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.exceptionHandling(exception -> {
					exception.authenticationEntryPoint(this.entryPoint);
				})
				.addFilterBefore(this.jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	protected abstract String[] getUserRoleAuthenticatedItems() throws Exception;
	protected abstract String[] getAdminRoleAuthenticatedItems() throws Exception;
}
