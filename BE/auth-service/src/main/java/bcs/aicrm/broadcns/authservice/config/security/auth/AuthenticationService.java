package bcs.aicrm.broadcns.authservice.config.security.auth;
import bcs.aicrm.broadcns.authservice.lgin.model.DTO.LGIN000DTO;
import bcs.aicrm.broadcns.authservice.lgin.model.VO.LGIN000VO;
import bcs.aicrm.broadcns.authservice.lgin.service.LGIN000Service;
import bcs.aicrm.broadcns.authservice.util.AES256Crypt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Slf4j
@Service
public class AuthenticationService {

	private final UserPrincipalDetailService userDetailsService;
	private LGIN000Service service;
	private MessageSource messageSource;

	@Autowired
	public AuthenticationService(UserPrincipalDetailService userDetailsService, MessageSource messageSource, LGIN000Service service) {
		this.userDetailsService = userDetailsService;
		this.messageSource = messageSource;
		this.service = service;
	}

	public Authentication authenticate(LGIN000DTO lgin000DTO) throws Exception {

		String message= null;

		UserPrincipalDetail userPrincipalDetail = (UserPrincipalDetail) this.userDetailsService.loadUserByUsername(lgin000DTO);
		LGIN000VO userVO = userPrincipalDetail.getUser();

		// isAccountNonLocked 계정상태가 잠김되지 않았는지를 담아두기 위해 (true: 만료안됨)
		if(!userPrincipalDetail.isAccountNonLocked()) {
			message = this.messageSource.getMessage("fail.common.loginIncorrect", null, "해당 계정은 잠김상태 입니다. 관리자에게 문의하십시오.", Locale.KOREA);
		}

		// isAccountNonExpired 계정이 만료되지 않았는지를 담아두기 위해 (true: 만료안됨)
		if(!userPrincipalDetail.isAccountNonExpired()) {
			message = this.messageSource.getMessage("LGIN000M.error.expired", null, "사용기간이 만료되었습니다.관리자에게 문의하십시오.", Locale.KOREA);
		}

		// isCredentialsNonExpired 계정의 비밀번호가 만료되지 않았는지를 담아두기 위해 (true: 만료안됨)
		if(!userPrincipalDetail.isCredentialsNonExpired()) {
			message = this.messageSource.getMessage("LGIN000M.success.passw.change", null, "비밀번호 변경일이 만료된 사용자입니다.", Locale.KOREA);
		}


		if(!userPrincipalDetail.getPassword().equals(AES256Crypt.encrypt(lgin000DTO.getScrtNo()))){
			LGIN000VO lgin000VO = LGIN000VO.builder()
					.tenantId(lgin000DTO.getTenantId())
					.bsVlMgntNo(4)
					.build();

			LGIN000VO result = this.service.LGIN000SEL08(lgin000VO);

			int bscPwErrTcnt = Integer.parseInt(result.getBsVl1());
			//업데이트 전의 현재비번오류건수
			int voPwErrTcnt = userVO.getPwErrTcnt();

			//5회 미만인 경우 오류횟수 증가
			if(voPwErrTcnt < bscPwErrTcnt) {
				//비밀번호 오류허용횟수 +1 증가
				this.service.LGIN000UPT04(userVO);

				//증가된 건 조회
				int lginErrCnt = this.service.LGIN000SEL05(lgin000DTO);

				//증가 후 비번 오류횟수 초과	: 계정 잠금
				if(lginErrCnt == bscPwErrTcnt) {
					lgin000VO = LGIN000VO.builder()
							.acStCd("2")        //계정잠김
							.acStRsnCd("3")     //비밀번호초과
							.tenantId(userVO.getTenantId())
							.usrId(userVO.getUsrId())
							.orgCd(userVO.getOrgCd())
							.build();
					this.service.LGIN000UPT01(lgin000VO); //계정잠김처리
				}

			}
			message = this.messageSource.getMessage("LGIN000M.error.requiredIn", null, "입력하신 정보가 올바르지 않습니다.", Locale.KOREA);
		}else{
			//사용자정보변경, 로그인이력생성
			this.service.LGIN000UPT02(userVO);

			LGIN000VO user = LGIN000VO.builder()
					.tenantId(userVO.getTenantId())
					.usrId(userVO.getUsrId())
					.sysLogDvCd("1000")
					.sysLogMsg(this.messageSource.getMessage("LGIN000M.login", null, "LGIN000M.login", Locale.KOREA))
					.build();
			this.service.LGIN000INS01(user);
		}

		if(message != null) {
			throw new BadCredentialsException(message);
		}

		UsernamePasswordAuthenticationToken authentication =
				new UsernamePasswordAuthenticationToken(userPrincipalDetail, lgin000DTO.getScrtNo(), userPrincipalDetail.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);

		return authentication;
	}

}
