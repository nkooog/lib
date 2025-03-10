package bcs.aicrm.broadcns.auth.config.security.auth;


import bcs.aicrm.broadcns.auth.lgin.model.VO.LGIN000VO;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class UserPrincipalDetail implements UserDetails {

	private LGIN000VO user;

	public UserPrincipalDetail(LGIN000VO user) {
		this.user = user;
	}

	public LGIN000VO getUser() {
		return user;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UserPrincipalDetail) {
			return this.user.getUsrId().equals(((UserPrincipalDetail) obj).user.getUsrId());
		}
		return false;
	}

	// hashCode 메소드 구현
	@Override
	public int hashCode() {
		return user.getUsrId().hashCode();  // usrId를 기준으로 해시코드 생성
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(user.getUsrGrd()));
	}

	@Override
	public String getPassword() {
		return user.getScrtNo();
	}

	@Override
	public String getUsername() {
		return user.getUsrNm();
	}

	// 계정이 만료되지 않았는지를 담아두기 위해 (true: 만료안됨)
	@Override
	public boolean isAccountNonExpired() {
		return user.getAcStCd().equals("9") ? false : true;
	}

	// 계정이 잠겨있지 않았는지를 담아두기 위해 (true: 잠기지 않음)
	@Override
	public boolean isAccountNonLocked() {
		return user.getAcStCd().equals("2") ? false : true;
	}

	// 계정의 비밀번호가 만료되지 않았는지를 담아두기 위해 (true: 만료안됨)
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	// 계정이 활성화되어있는지를 담아두기 위해 (true: 활성화됨)
	@Override
	public boolean isEnabled() {
		return true;
	}
}
