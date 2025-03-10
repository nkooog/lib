package bcs.aicrm.broadcns.auth.config.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

@Slf4j
@Getter
public class CustomAuthencationDetails extends WebAuthenticationDetails {

	private String tenantId;
	private String mlingCd;
	private String extNoUseYn;
	private String scrtNo;

	public CustomAuthencationDetails(HttpServletRequest request) {
		super(request);
		this.tenantId = request.getParameter("tenantId");
		this.mlingCd = request.getParameter("mlingCd");
		this.extNoUseYn = request.getParameter("extNoUseYn");
		this.scrtNo = request.getParameter("scrtNo");
	}
}
