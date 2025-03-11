package bcs.aicrm.broadcns.authservice.config.security.jwt;

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
		object.put("path", request.getRequestURI());

		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(this.objectMapper.writeValueAsString(object));
	}

}
