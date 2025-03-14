package bcs.aicrm.broadcns.gatewayservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Component
public class AuthencationEntryPoint implements ServerAuthenticationEntryPoint {

	private ObjectMapper objectMapper;

	@Autowired
	public AuthencationEntryPoint(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {

		return Mono.defer(() -> {

			String requestPath = exchange.getRequest().getPath().toString();

			log.debug("Unauthorized error: {}", requestPath);
			log.debug("RequestPath: {}", requestPath);
			log.debug("exception: {}", ex.getClass().getName());

			ServerHttpResponse serverHttpResponse = exchange.getResponse();
			serverHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
			serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);

			ErrorMessage errorMessage = new ErrorMessage(
					HttpStatus.UNAUTHORIZED
					,   LocalDateTime.now()
					,   ex.getMessage()
					,   requestPath
			);

			try {

				byte[] errorByte = this.objectMapper
						.registerModule(new JavaTimeModule())
						.writeValueAsBytes(errorMessage);

				DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(errorByte);
				serverHttpResponse.writeWith(Mono.just(dataBuffer));
				return serverHttpResponse.writeWith(Mono.just(dataBuffer));
			}catch (Exception e) {
				log.error(e.getMessage(), e);
				return serverHttpResponse.setComplete();
			}
		});
	}

	@Data
	@NoArgsConstructor
	class ErrorMessage{

		private HttpStatus status;
		private LocalDateTime localDateTime;
		private String message;
		private String path;

		public ErrorMessage(HttpStatus status, LocalDateTime localDateTime, String message, String path) {
			this.status = status;
			this.localDateTime = localDateTime;
			this.message = message;
			this.path = path;
		}
	}
}
