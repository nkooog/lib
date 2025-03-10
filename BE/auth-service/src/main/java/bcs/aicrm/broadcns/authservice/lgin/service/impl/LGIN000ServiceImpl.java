package bcs.aicrm.broadcns.authservice.lgin.service.impl;

import bcs.aicrm.broadcns.authservice.lgin.model.DTO.LGIN000DTO;
import bcs.aicrm.broadcns.authservice.lgin.model.VO.LGIN000VO;
import bcs.aicrm.broadcns.authservice.lgin.service.LGIN000Service;
import bcs.aicrm.broadcns.authservice.lgin.service.dao.LGIN000DAO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service("LGIN000Service")
public class LGIN000ServiceImpl implements LGIN000Service {

	@Resource(name="LGIN000DAO")
	private LGIN000DAO lgin000DAO;

	@Override
	public LGIN000VO LGIN000SEL07(LGIN000DTO lgin000DTO) {
		return (LGIN000VO) this.lgin000DAO.selectByOne("LGIN000SEL07", lgin000DTO);
	}

	@Override
	public LGIN000VO LGIN000SEL08(LGIN000VO lgin000VO) {
		return (LGIN000VO) this.lgin000DAO.selectByOne("LGIN000SEL08", lgin000VO);
	}

	@Override
	public void LGIN000UPT04(LGIN000VO lgin000VO) {
		this.lgin000DAO.sqlUpdate("LGIN000UPT04", lgin000VO);
	}

	@Override
	public int LGIN000SEL05(LGIN000DTO lgin000DTO) {
		return (int) this.lgin000DAO.selectByOne("LGIN000SEL05", lgin000DTO);
	}

	@Override
	public int LGIN000UPT01(LGIN000VO lgin000VO) {
		return this.lgin000DAO.sqlUpdate("LGIN000UPT01", lgin000VO);
	}

	@Override
	public int LGIN000UPT02(LGIN000VO lgin000VO) {
		return this.lgin000DAO.sqlUpdate("LGIN000UPT02", lgin000VO);
	}

	@Override
	public void LGIN000INS01(LGIN000VO vo) {
		this.lgin000DAO.sqlInsert("LGIN000INS01", vo);
	}
}
