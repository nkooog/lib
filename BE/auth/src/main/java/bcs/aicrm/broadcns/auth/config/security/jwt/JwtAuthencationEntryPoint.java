package bcs.aicrm.broadcns.auth.config.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthencationEntryPoint implements AuthenticationEntryPoint {

	private ObjectMapper objectMapper;

	@Autowired
	public JwtAuthencationEntryPoint(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

		JSONObject object = new JSONObject();

		object.put("message", authException.getMessage());
		object.put("status", response.getStatus());

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write(this.objectMapper.writeValueAsString(object));
	}

}
