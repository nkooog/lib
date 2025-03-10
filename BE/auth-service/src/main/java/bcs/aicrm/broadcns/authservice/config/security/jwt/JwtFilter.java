package bcs.aicrm.broadcns.authservice.config.security.jwt;

import bcs.aicrm.broadcns.authservice.comm.errors.AccessExpiredException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtTokenProvider provider;

	private JwtAuthencationEntryPoint jwtAuthencationEntryPoint;

	@Value("${jwt.authHeader}")
	private String authHeader;
	@Value("${jwt.refreshHeader}")
	private String refreshHeader;

	@Value("${jwt.type}")
	private String type;

	public JwtFilter(JwtTokenProvider provider, JwtAuthencationEntryPoint jwtAuthencationEntryPoint) {
		this.provider = provider;
		this.jwtAuthencationEntryPoint = jwtAuthencationEntryPoint;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		String token = this.resolveToken(request);
		JSONObject json = new JSONObject();
		try {
			if( token != null ) {

				Authentication authentication = this.provider.getAuthentication(token);

				if(authentication!=null)  {
					// 다음 filter로 가기전 authentication 정보는 SecurityContextHolder에 담는다.
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
		}catch (MalformedJwtException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			this.jwtAuthencationEntryPoint.commence(request, response, new BadCredentialsException(e.getMessage()));
			return;
		}catch (ExpiredJwtException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			this.jwtAuthencationEntryPoint.commence(request, response, new BadCredentialsException(e.getMessage()));
			return;
		}catch (JwtException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			this.jwtAuthencationEntryPoint.commence(request, response, new BadCredentialsException(e.getMessage()));
			return;
		}catch (BadCredentialsException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			this.jwtAuthencationEntryPoint.commence(request, response, e);
			return;
		}catch (AccessExpiredException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			this.jwtAuthencationEntryPoint.commence(request, response, e);
			return;
		}

		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(this.authHeader);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(this.type)) {
			return bearerToken.substring(7);
		}
		return null;
	}

}
