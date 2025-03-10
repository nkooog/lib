package bcs.aicrm.broadcns.auth.lgin.model.VO;

import bcs.aicrm.broadcns.auth.comm.VO.COMM000VO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LGIN000VO extends COMM000VO {

	private String usrId;
	private String scrtNo;
	private String acStCd;
	private String acStRsnCd;
	private Timestamp scrtNoLstUpdDtm;
	private Integer pwErrTcnt;
	private String usrNm;
	private String usrAlnm;
	private String usrAlnmUseYn;
	private String potoImgFileNm;
	private String potoImgIdxFileNm;
	private String potoImgPsn;
	private String cnslGrpCd;
	private String orgCd;
	private String orgCdVrsn;
	private String cntyTelNo;
	private String mbphNo;
	private String emlAddrIsd;
	private String emlAddrIsdDmn;
	private String emlAddrIsdDmnCd;
	private String emlAddrExtn;
	private String emlAddrExtnDmn;
	private String emlAddrExtnDmnCd;
	private Date qualAcqsDd;
	private Date qualLossDd;
	private String usrGrd;
	private String unfyBlbdCreAthtYn;
	private String kldCtgrCreAtht;
	private String athtLvlOrgCd;
	private String athtLvlDtCd;
	private String chatChnlPmssCntCd;
	private String kldScwdSaveYn;
	private String autoPfcnUseYn;
	private String cmmtSetlmnYn;
	private String kldMgntSetlmnYn;
	private String srchKeyword1;
	private String srchKeyword2;
	private Timestamp lstLginDtm;
	private String lstLginIpAddr;
	private String lstLginExtNo;
	private Timestamp lstLgoutDtm;
	private String lstLgoutIpAddr;
	private String lstLgoutExtNo;
	private String abolmnId;
	private String abolmnOrgCd;
	private int bsVlMgntNo;
	private String bsVlNm;
	private String bsVl1;
	private String bsVl2;
	private String bsVl3;
	private String useYn;
	private String currentHour;
	private String currentMin;
	private String sysLogMsg;
	private String sysLogDvCd;

}