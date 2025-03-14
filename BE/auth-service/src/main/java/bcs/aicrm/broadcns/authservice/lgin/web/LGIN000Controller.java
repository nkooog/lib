package bcs.aicrm.broadcns.authservice.lgin.web;

import bcs.aicrm.broadcns.authservice.config.security.auth.AuthenticationService;
import bcs.aicrm.broadcns.authservice.config.security.jwt.JwtToken;
import bcs.aicrm.broadcns.authservice.config.security.jwt.JwtTokenProvider;
import bcs.aicrm.broadcns.authservice.lgin.model.DTO.LGIN000DTO;
import bcs.aicrm.broadcns.authservice.lgin.service.api.LGIN000API;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;

@Slf4j
@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "LGIN000Controller", description = "로그인 관련 API")
public class LGIN000Controller implements LGIN000API {

	private final String SEPARATOR = ":";
	private AuthenticationService authService;
	private JwtTokenProvider provider;

	private RedisTemplate redisTemplate;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
		log.debug("test");
        return ResponseEntity.ok().body("test");
    }

	@Autowired
	public LGIN000Controller(AuthenticationService authService, JwtTokenProvider provider, RedisTemplate redisTemplate) {
		this.authService = authService;
		this.provider = provider;
		this.redisTemplate = redisTemplate;
	}

	@Override
	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity login(@RequestBody @Valid LGIN000DTO lgin000DTO, Errors errors) throws Exception {

		JSONObject json = new JSONObject();
		StringBuffer buffer = new StringBuffer();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		HttpStatus status = null;

		if(errors.hasErrors()) {
			return ResponseEntity.badRequest().body(errors);
		}

		Authentication authentication = this.authService.authenticate(lgin000DTO);
		status = this.provider.isValidToken(authentication);

		buffer.append(lgin000DTO.getTenantId());
		buffer.append(this.SEPARATOR);
		buffer.append(lgin000DTO.getUsrId());

		switch (status) {
			case OK -> {
				String refresh = (String) this.redisTemplate.opsForValue().get(buffer.toString());
				Claims claims = this.provider.parseClaims(refresh);
				json.put("result", JwtToken.builder()
						.refreshToken(refresh)
						.refreshTokenExpiration(dateFormat.format(claims.getExpiration()))
						.build());
			}
			case CREATED -> {
				JwtToken jwtToken = this.provider.generateToken(authentication);
				json.put("status" , HttpStatus.CREATED.value());
				json.put("message", HttpStatus.CREATED);
				json.put("result" , jwtToken);
			}
		}

		return ResponseEntity.status(status).body(json);

	}

}
