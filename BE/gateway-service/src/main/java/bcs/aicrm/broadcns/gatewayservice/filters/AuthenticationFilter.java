package bcs.aicrm.broadcns.gatewayservice.filters;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    public AuthenticationFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.debug(" ##################################### ");
        // Global Pre Filter
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.debug("Global Filter baseMessage : {}", config.getBaseMessage());
            log.debug("requestUrl : {}", request.getURI());

            // Global Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {    // Mono : 비동기 방식의 서버에서 단일값을 전달할 때 사용
                log.debug("Custom POST filter : response code -> {}", response.getStatusCode());
            }));
        };
    }

    @Data
    public static class Config {
        private String baseMessage;
    }
}
