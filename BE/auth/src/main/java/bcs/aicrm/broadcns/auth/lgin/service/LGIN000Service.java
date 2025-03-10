package bcs.aicrm.broadcns.auth.lgin.service;


import bcs.aicrm.broadcns.auth.lgin.model.DTO.LGIN000DTO;
import bcs.aicrm.broadcns.auth.lgin.model.VO.LGIN000VO;

public interface LGIN000Service {

	LGIN000VO LGIN000SEL07(LGIN000DTO lgin000DTO);
	LGIN000VO LGIN000SEL08(LGIN000VO lgin000VO);
	void LGIN000UPT04(LGIN000VO lgin000VO);
	int LGIN000SEL05(LGIN000DTO lgin000DTO);
	int LGIN000UPT01(LGIN000VO lgin000VO);
	int LGIN000UPT02(LGIN000VO lgin000VO);
	void LGIN000INS01(LGIN000VO vo);
}
