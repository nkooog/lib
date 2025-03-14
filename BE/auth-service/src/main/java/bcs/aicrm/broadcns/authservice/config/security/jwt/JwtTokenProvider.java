package bcs.aicrm.broadcns.authservice.config.security.jwt;

import bcs.aicrm.broadcns.authservice.config.security.auth.UserPrincipalDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
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

		Date accessTokenExpiration = getTokenDate(10);
		Date refreshTokenExpiration = getTokenDate(60);

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
		return Jwts.parserBuilder()
							.setSigningKey(this.KEY)
							.build()
							.parseClaimsJws(token)
							.getBody();
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
		String refresh = null;

		if(authentication != null) {
			UserPrincipalDetail detail = (UserPrincipalDetail) authentication.getPrincipal();
			buffer.append(detail.getUser().getTenantId());
			buffer.append(SEPARATOR);
			buffer.append(detail.getUser().getUsrId());

			refresh = (String) this.redisTemplate.opsForValue().get(buffer.toString());
		}

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

}
