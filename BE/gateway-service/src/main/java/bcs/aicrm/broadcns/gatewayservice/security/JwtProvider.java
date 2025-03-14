package bcs.aicrm.broadcns.gatewayservice.security;

import bcs.aicrm.broadcns.gatewayservice.exec.UnAuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;

@Slf4j
@Component
public class JwtProvider {

	private final SecretKey KEY;

	@Value("${spring.jwt.type}")
	private String type;

	public JwtProvider(@Value("${spring.jwt.secret}") String KEY) {
		byte[] keyBytes = Decoders.BASE64.decode(KEY);
		this.KEY = Keys.hmacShaKeyFor(keyBytes);
	}

	public String getToken(ServerHttpRequest request){
		return request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
	}

	public String resolveToken(ServerHttpRequest request){
		String bearerToken = getToken(request);
		if(!StringUtils.isBlank(bearerToken) && bearerToken.startsWith(this.type)){
			return bearerToken.substring(7);
		}
		return null;
	}

	public Authentication getAuthentication(String accessToken) {

		Claims claims = this.parseClaims(accessToken);

		// TODO: 권한정보 예외처리 추가 예정

		// 클레임에서 권한 정보 가져오기
		Collection<? extends GrantedAuthority> authentication = Arrays
				.stream(claims.get(HttpHeaders.AUTHORIZATION).toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.toList();

		String userInfo = claims.get("sub").toString();
		// UserDetails 객체를 만들어서 Authentication 리턴
		return new UsernamePasswordAuthenticationToken(userInfo, null, authentication);
	}

	public Claims parseClaims(String accessToken) {
		try {
			return Jwts.parserBuilder().setSigningKey(this.KEY).build().parseClaimsJws(accessToken).getBody();
		} catch(ExpiredJwtException e) {
			return e.getClaims();
		}
	}

	public boolean validateJwtToken(String authToken) throws UnAuthenticationException{
		try {
			Jwts.parserBuilder().setSigningKey(this.KEY).build().parseClaimsJws(authToken);
			return true;
		} catch (MalformedJwtException e) {
			log.error("Invalid JWT MalformedJwt: {}", e.getMessage());
			throw new UnAuthenticationException("Invalid JWT MalformedJwt: "+e.getMessage());
		} catch (ExpiredJwtException e) {
			log.error("JWT token is expired: {}", e.getMessage());
			throw new UnAuthenticationException("JWT token is expired: "+e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("JWT token is unsupported: {}", e.getMessage());
			throw new UnAuthenticationException("JWT token is unsupported: "+e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
			throw new UnAuthenticationException("JWT claims string is empty: "+e.getMessage());
		}catch (JwtException e){
			log.error("Invalid JWT token: {}", e.getMessage());
			throw new UnAuthenticationException("Invalid JWT token: "+e.getMessage());
		}
	}

}
