package bcs.aicrm.broadcns.auth.config.security.auth;
import bcs.aicrm.broadcns.auth.lgin.model.DTO.LGIN000DTO;
import bcs.aicrm.broadcns.auth.lgin.model.VO.LGIN000VO;
import bcs.aicrm.broadcns.auth.lgin.service.LGIN000Service;
import bcs.aicrm.broadcns.auth.util.AES256Crypt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Slf4j
@Service
public class UserPrincipalDetailService implements UserDetailsService {

	private LGIN000Service service;
	private MessageSource messageSource;

	@Autowired
	public UserPrincipalDetailService(LGIN000Service service, MessageSource messageSource) {
		this.service = service;
		this.messageSource = messageSource;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return null;
	}

	public UserDetails loadUserByUsername(LGIN000DTO lgin000DTO) throws Exception {

		log.debug(lgin000DTO.getScrtNo());

		LGIN000DTO dto = LGIN000DTO.builder()
				.tenantId(lgin000DTO.getTenantId())
				.scrtNo(AES256Crypt.encrypt(lgin000DTO.getScrtNo()))
				.usrId(lgin000DTO.getUsrId())
				.mlingCd(lgin000DTO.getMlingCd())
				.build();

		LGIN000VO lgin000VO = this.service.LGIN000SEL07(dto);

		if(lgin000VO == null) {
			throw new UsernameNotFoundException(
					this.messageSource.getMessage("fail.common.login", null, "로그인 정보가 올바르지 않습니다.", Locale.KOREA));
		}

		return new UserPrincipalDetail(lgin000VO);
	}
}
