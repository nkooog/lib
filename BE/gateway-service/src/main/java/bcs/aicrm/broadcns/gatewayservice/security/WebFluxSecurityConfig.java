package bcs.aicrm.broadcns.gatewayservice.security;

import bcs.aicrm.broadcns.gatewayservice.filters.AuthenticationJwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class WebFluxSecurityConfig {

	private AuthenticationJwtFilter jwtFilter;
	private AuthencationEntryPoint entryPoint;

	@Autowired
	public WebFluxSecurityConfig(AuthencationEntryPoint entryPoint, AuthenticationJwtFilter jwtFilter) {
		this.entryPoint = entryPoint;
		this.jwtFilter = jwtFilter;
	}

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		http
				.formLogin(formLoginSpec -> formLoginSpec.disable())
				.csrf(csrfSpec -> csrfSpec.disable())
				.cors(corsSpec -> corsSpec.disable())
				.httpBasic(httpBasicSpec -> httpBasicSpec.disable())
				.securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) //session STATELESS (SpringContextHolder 미사용)
				.authorizeExchange(
						auth -> auth
								.pathMatchers("/auth/login","/swagger-ui/**", "/v3/api-docs/**", "/swagger.html")
								.permitAll()
								.anyExchange().authenticated()
				)
				.anonymous(anonymousSpec -> anonymousSpec.disable())
				.exceptionHandling(exec -> {
					exec.authenticationEntryPoint(this.entryPoint);
				})
				.addFilterBefore(this.jwtFilter.authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
				;
		return http.build();
	}

}
