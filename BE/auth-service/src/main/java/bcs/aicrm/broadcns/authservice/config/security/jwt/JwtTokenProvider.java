package bcs.aicrm.broadcns.authservice.config.security.jwt;

import bcs.aicrm.broadcns.authservice.comm.errors.AccessExpiredException;
import bcs.aicrm.broadcns.authservice.config.security.auth.UserPrincipalDetail;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

	private final SecretKey KEY;
	private final String SEPARATOR = ":";

	private RedisTemplate redisTemplate;

	@Value("${jwt.authHeader}")
	private String header;

	@Value("${jwt.type}")
	private String type;

	@Autowired
	public JwtTokenProvider(RedisTemplate redisTemplate, @Value("${jwt.secret}") String KEY ) {
		byte[] keyBytes = Decoders.BASE64.decode(KEY);
		this.KEY = Keys.hmacShaKeyFor(keyBytes);
		this.redisTemplate = redisTemplate;
	}

	public JwtToken generateToken(Authentication authentication) {
		// 권한 가져오기
		String authorities = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));

		// 테넌트명:사용자ID
		UserPrincipalDetail detail = (UserPrincipalDetail) authentication.getPrincipal();
		StringBuffer buffer = new StringBuffer();

		buffer.append(detail.getUser().getTenantId());
		buffer.append(SEPARATOR);
		buffer.append(detail.getUser().getUsrId());

		Date accessTokenExpiration = getTokenDate(1);
		Date refreshTokenExpiration = getTokenDate(7);

		// Access Token 생성
		String accessToken = Jwts.builder()
				.signWith(this.KEY)
				.setSubject(buffer.toString())
				.claim(this.header, authorities)
				.setExpiration(accessTokenExpiration)
				.compact();

		// Refresh Token 생성
		String refreshToken = Jwts.builder()
				.signWith(this.KEY)
				.setExpiration(refreshTokenExpiration)
				.compact();

		String refreshKey = buffer.toString();

		ValueOperations<String, String> valueOperations = this.redisTemplate.opsForValue();
		valueOperations.set(refreshKey, refreshToken);

		return JwtToken.builder()
				.grantType(this.type.trim())
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.accessTokenExpiration(accessTokenExpiration.toString())
				.refreshTokenExpiration(refreshTokenExpiration.toString())
				.build();
	}

	public Claims parseClaims(String token) {
		Claims claims = null;
		try {

			claims = Jwts.parserBuilder()
							.setSigningKey(this.KEY)
							.build()
							.parseClaimsJws(token)
							.getBody();

		} catch (MalformedJwtException e) {
			// JWT 형식 오류 처리
			log.debug("잘못된 JWT 형식입니다.");
			throw new MalformedJwtException("잘못된 JWT 형식입니다.");
		} catch (ExpiredJwtException e) {
			// JWT 토큰 만료 오류 처리
			log.debug("JWT 토큰이 만료되었습니다.", e);
			log.debug( "만료 토큰 subject 정보 : " + e.getClaims().getSubject());

			if(e.getClaims().getSubject() != null) {
				if(validExpired(e.getClaims().getSubject())) {
					throw new AccessExpiredException("재발급 진행");
				}
			}
			throw new ExpiredJwtException(null, null, "만료된 토큰입니다.");

		} catch (JwtException e) {
			// 기타 JWT 예외 처리
			log.debug("JWT 처리 오류 발생", e);
			throw new JwtException("토큰 처리 중 오류가 발생했습니다.");
		}
		return claims;
	}

	public Authentication getAuthentication(String accessToken) {

		// 토큰 복호화
		Claims claims = parseClaims(accessToken);

		if (claims == null || claims.get(this.header) == null ) {
			return null;
		}

		// 클레임에서 권한 정보 가져오기
		Collection<? extends GrantedAuthority> authentication = (Collection<? extends GrantedAuthority>) this.getClaimsAuthtication(claims);

		String userInfo = claims.get("sub").toString();
		// UserDetails 객체를 만들어서 Authentication 리턴
		return new UsernamePasswordAuthenticationToken(userInfo, null, authentication);
	}

	public Collection<? extends GrantedAuthority> getClaimsAuthtication(Claims claims) {
		return Arrays.stream(
						claims.get(this.header).toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}


	public HttpStatus isValidToken(Authentication authentication) {

		StringBuffer buffer = new StringBuffer();
		JwtToken jwtToken = null;
		Claims claims = null;
		HttpStatus httpStatus = null;


		if(authentication != null) {
			UserPrincipalDetail detail = (UserPrincipalDetail) authentication.getPrincipal();
			buffer.append(detail.getUser().getTenantId());
			buffer.append(SEPARATOR);
			buffer.append(detail.getUser().getUsrId());
		}

		String refresh = (String) this.redisTemplate.opsForValue().get(buffer.toString());

		if(refresh != null) {
			claims = this.parseClaims(refresh);
			if(claims != null) {
				httpStatus = HttpStatus.OK;
			}
		}else{
			httpStatus = HttpStatus.CREATED;
		}

		return httpStatus;
	}

	// LocalDateTime To Date
	public Date getTokenDate(Integer minutes) {
		return java.sql.Timestamp.valueOf(LocalDateTime.now().plusMinutes(minutes).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	}

	public boolean validExpired(String sub) {
		try {
			String refresh = (String) this.redisTemplate.opsForValue().get(sub);
			Jwts.parserBuilder()
					.setSigningKey(this.KEY)
					.build()
					.parseClaimsJws(refresh);
			return true;
		}catch (Exception e) {
			return false;
		}
	}

}
