package bcs.aicrm.broadcns.gatewayservice.exec;

import org.springframework.security.core.AuthenticationException;

public class UnAuthenticationException extends AuthenticationException {

	public UnAuthenticationException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public UnAuthenticationException(String msg) {
		super(msg);
	}
}
