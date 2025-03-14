package bcs.aicrm.broadcns.gatewayservice.filters;

import bcs.aicrm.broadcns.gatewayservice.exec.UnAuthenticationException;
import bcs.aicrm.broadcns.gatewayservice.security.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Component
public class AuthenticationJwtFilter {

	private JwtProvider jwtProvider;

	@Autowired
	public AuthenticationJwtFilter(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	public AuthenticationWebFilter authenticationWebFilter() {

		ReactiveAuthenticationManager authenticationManager = Mono::just;

		AuthenticationWebFilter authenticationWebFilter
				= new AuthenticationWebFilter(authenticationManager);
		authenticationWebFilter.setServerAuthenticationConverter(serverAuthenticationConverter());
		return authenticationWebFilter;
	}

	private ServerAuthenticationConverter serverAuthenticationConverter(){
		return exchange -> {
			String token = this.jwtProvider.resolveToken(exchange.getRequest());
			try {
				if(!Objects.isNull(token) && this.jwtProvider.validateJwtToken(token)){
					return Mono.justOrEmpty(this.jwtProvider.getAuthentication(token));
				}
			} catch (UnAuthenticationException e) {
				log.error(e.getMessage(), e);
			}
			return Mono.empty();
		};
	}

}
