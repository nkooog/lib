package bcs.aicrm.broadcns.authservice.comm.errors;
import org.springframework.security.core.AuthenticationException;

public class AccessExpiredException extends AuthenticationException {

	public AccessExpiredException(String message) {
		super(message);
	}
}
