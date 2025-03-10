package bcs.aicrm.broadcns.gatewayservice.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Set;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private final Key signingKey;

    public AuthenticationFilter(@Value("${jwt.secret}") String secretKey) {
        super(Config.class);
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
// ✅ 인증이 필요 없는 경로 목록
    private static final Set<String> EXCLUDED_PATHS = Set.of(
            "/users/register",
            "/users/health",
            "/users/check-duplicate-email",
            "/users/check-duplicate-userId"
    );

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            String path = request.getPath().value(); // 요청 경로 가져오기

            // ✅ /users/register 경로는 인증 없이 통과
            if (isExcludedPath(path)) {
                logger.info("Skipping authentication for path");
                return chain.filter(exchange); // 필터링 건너뛰고 다음 단계로 진행
            }

            // ✅ Authorization 헤더 확인
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION).replace("Bearer ", "");

            // ✅ JWT 검증
            if (!validateToken(token)) {
                return onError(exchange, "Invalid JWT Token", HttpStatus.UNAUTHORIZED);
            }
            // ✅ 토큰이 유효하면 요청에 사용자 정보를 추가 (선택적)
            Claims claims = Jwts.parser()
                    .setSigningKey(signingKey)
                    .parseClaimsJws(token)
                    .getBody();
            // 예: 사용자 ID 또는 역할 정보를 요청 헤더에 추가
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", claims.getSubject()) // 사용자 ID 추가
                    .build();
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(modifiedRequest)
                    .build();

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        logger.error("JWT Authentication failed: {}", err);
        return response.setComplete();
    }

    private boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(signingKey)
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getExpiration().after(new Date()); // ✅ 토큰 만료 여부 확인
        } catch (Exception e) {
            logger.error("JWT Token validation error: {}", e.getMessage());
            return false;
        }
    }

    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.contains(path);
    }

    public static class Config {}
}
