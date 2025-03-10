package bcs.aicrm.broadcns.auth.comm.errors;
import org.springframework.security.core.AuthenticationException;

public class AccessExpiredException extends AuthenticationException {

	public AccessExpiredException(String message) {
		super(message);
	}
}
